package org.springframework.roo.project.packaging;

import static org.springframework.roo.project.Path.SPRING_CONFIG_ROOT;
import static org.springframework.roo.project.Path.SRC_MAIN_JAVA;
import static org.springframework.roo.project.Path.SRC_MAIN_WEBAPP;
import static org.springframework.roo.project.Path.SRC_TEST_JAVA;
import static org.springframework.roo.project.Path.SRC_TEST_RESOURCES;

import java.util.Arrays;
import java.util.Collection;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.springframework.roo.project.Path;

/**
 * The core {@link PackagingProvider} for web modules.
 * 
 * @author Andrew Swan
 * @author Paula Navarro
 * @since 1.2.0
 */
@Component(service = {PackagingProvider.class,CorePackagingProvider.class})
public class WarPackaging extends AbstractCorePackagingProvider {

  public WarPackaging() {
    super("war", "war-pom-template.xml", "child-war-pom-template.xml");
  }
  
  @Activate
  protected void activate(final ComponentContext cContext) {
    super.activate(cContext);
  }    

  public Collection<Path> getPaths() {
    return Arrays.asList(SRC_MAIN_JAVA, SRC_TEST_JAVA, SRC_TEST_RESOURCES, SPRING_CONFIG_ROOT,
        SRC_MAIN_WEBAPP);
  }
}
