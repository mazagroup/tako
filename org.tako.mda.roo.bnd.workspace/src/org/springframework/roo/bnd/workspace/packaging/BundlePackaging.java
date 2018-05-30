package org.springframework.roo.bnd.workspace.packaging;

import static org.springframework.roo.bnd.workspace.Path.SRC_MAIN_JAVA;
import static org.springframework.roo.bnd.workspace.Path.SRC_MAIN_RESOURCES;

import java.util.Arrays;
import java.util.Collection;

import org.osgi.service.component.annotations.Component;
import org.springframework.roo.bnd.workspace.GAV;
import org.springframework.roo.bnd.workspace.LogicalPath;
import org.springframework.roo.bnd.workspace.Path;
import org.springframework.roo.bnd.workspace.ProjectOperations;
import org.springframework.roo.model.JavaPackage;

/**
 * The {@link PackagingProvider} that creates an OSGi bundle.
 * 
 * @author Andrew Swan
 * @since 1.2.0
 */
@Component(service = CorePackagingProvider.class)
public class BundlePackaging implements CorePackagingProvider {

  public String createArtifacts(final JavaPackage topLevelPackage,
      final String nullableProjectName, final String javaVersion, final GAV parentPom,
      final String module, final ProjectOperations projectOperations) {
    // Already created by the creator addon
    return projectOperations.getPathResolver().getIdentifier(
        LogicalPath.getInstance(Path.ROOT, ""), "pom.xml");
  }

  public String getId() {
    return "bundle";
  }

  public Collection<Path> getPaths() {
    return Arrays.asList(SRC_MAIN_JAVA, SRC_MAIN_RESOURCES);
  }

  public boolean isDefault() {
    return false;
  }
}
