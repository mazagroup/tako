package org.springframework.roo.bnd.workspace.packaging;

import java.util.Collection;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.springframework.roo.bnd.workspace.Path;
import org.springframework.roo.bnd.workspace.ProjectOperations;
import org.springframework.roo.model.JavaPackage;

/**
 * The Maven "pom" {@link PackagingProvider}
 *
 * @author Andrew Swan
 * @since 1.2.0
 */
@Component(service = {PackagingProvider.class,CorePackagingProvider.class})
public class EarPackaging extends AbstractCorePackagingProvider {

  /**
   * Constructor
   */
  public EarPackaging() {
    // ear-pom-template.xml doesn't exist because we won't allow ear packaging projects
    super("ear", "ear-pom-template.xml");
  }
  
  @Activate
  protected void activate(final ComponentContext cContext) {
    super.activate(cContext);
  }  

  public Collection<Path> getPaths() {
    return null;
  }
}
