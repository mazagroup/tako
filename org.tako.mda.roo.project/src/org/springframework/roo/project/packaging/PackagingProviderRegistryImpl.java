package org.springframework.roo.project.packaging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * The {@link PackagingProviderRegistry} implementation.
 * 
 * @author Andrew Swan
 * @since 1.2.0
 */
@Component(service = PackagingProviderRegistry.class )
public class PackagingProviderRegistryImpl implements PackagingProviderRegistry {

  private final Object mutex = new Object();
  // Using a map avoids each PackagingProvider having to implement equals()
  // properly (when removing)
  private final Map<String, PackagingProvider> packagingProviders =
      new HashMap<String, PackagingProvider>();

  @Reference(name = "packagingProvider",
		    policy = ReferencePolicy.DYNAMIC, service = PackagingProvider.class,
		    cardinality = ReferenceCardinality.MULTIPLE)  
  protected void bindPackagingProvider(final PackagingProvider packagingProvider) {
    synchronized (mutex) {
      final PackagingProvider previousPackagingProvider =
          packagingProviders.put(packagingProvider.getId(), packagingProvider);
      Validate.isTrue(previousPackagingProvider == null,
          "More than one PackagingProvider with ID = '%s'", packagingProvider.getId());
    }
  }
  
  protected void unbindPackagingProvider(final PackagingProvider packagingProvider) {
    synchronized (mutex) {
      packagingProviders.remove(packagingProvider.getId());
    }
  }  

  public Collection<PackagingProvider> getAllPackagingProviders() {
    return new ArrayList<PackagingProvider>(packagingProviders.values());
  }

  public PackagingProvider getDefaultPackagingProvider() {
    PackagingProvider defaultCoreProvider = null;
    for (final PackagingProvider packagingProvider : packagingProviders.values()) {
      if (packagingProvider.isDefault()) {
        if (packagingProvider instanceof CorePackagingProvider) {
          defaultCoreProvider = packagingProvider;
        } else {
          return packagingProvider;
        }
      }
    }
    Validate.validState(defaultCoreProvider != null,
        "Should have found a default core PackagingProvider");
    return defaultCoreProvider;
  }

  public PackagingProvider getPackagingProvider(final String id) {
    for (final PackagingProvider packagingProvider : packagingProviders.values()) {
      if (packagingProvider.getId().equalsIgnoreCase(id)) {
        return packagingProvider;
      }
    }
    return null;
  }


}
