package org.springframework.roo.bnd.workspace.packaging;

import static org.springframework.roo.bnd.workspace.Path.SPRING_CONFIG_ROOT;
import static org.springframework.roo.bnd.workspace.Path.SRC_MAIN_JAVA;
import static org.springframework.roo.bnd.workspace.Path.SRC_MAIN_RESOURCES;
import static org.springframework.roo.bnd.workspace.Path.SRC_TEST_JAVA;
import static org.springframework.roo.bnd.workspace.Path.SRC_TEST_RESOURCES;

import java.util.Arrays;
import java.util.Collection;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.springframework.roo.bnd.workspace.GAV;
import org.springframework.roo.bnd.workspace.Path;
import org.springframework.roo.bnd.workspace.ProjectOperations;
import org.springframework.roo.model.JavaPackage;

/**
 * The {@link PackagingProvider} that creates a JAR file.
 * 
 * @author Andrew Swan
 * @author Paula Navarro
 * @since 1.2.0
 */
@Component(service = {PackagingProvider.class,CorePackagingProvider.class})
public class JarPackaging extends AbstractCorePackagingProvider {

  public static final String NAME = "jar";

  /**
   * Constructor invoked by the OSGi container
   */
  public JarPackaging() {
    super(NAME, "jar-pom-template.xml", "child-jar-pom-template.xml");
  }
  
  @Activate
  protected void activate(final ComponentContext cContext) {
    super.activate(cContext);
  }


  @Override
  protected String createPom(final JavaPackage topLevelPackage, final String nullableProjectName,
      final String javaVersion, final GAV parentPom, final String moduleName,
      final ProjectOperations projectOperations) {

    final String pomPath =
        super.createPom(topLevelPackage, nullableProjectName, javaVersion, parentPom, moduleName,
            projectOperations);
    return pomPath;
  }

  public Collection<Path> getPaths() {
    return Arrays.asList(SRC_MAIN_JAVA, SRC_MAIN_RESOURCES, SRC_TEST_JAVA, SRC_TEST_RESOURCES,
        SPRING_CONFIG_ROOT);
  }

  @Override
  public boolean isDefault() {
    return true;
  }
}
