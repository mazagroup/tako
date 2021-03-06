package org.springframework.roo.bnd.workspace;

import static org.springframework.roo.support.util.FileUtils.CURRENT_DIRECTORY;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.roo.bnd.workspace.maven.Pom;
import org.springframework.roo.file.monitor.event.FileDetails;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.support.osgi.OSGiUtils;
import org.springframework.roo.support.util.FileUtils;

@Component(service = PathResolvingStrategy.class)
public class MavenPathResolvingStrategy extends AbstractPathResolvingStrategy {

  @Reference
  protected PomManagementService pomManagementService;
  
  @Activate
  protected void activate(final ComponentContext context) {
    super.activate(context);
  }  

  /**
   * Locates the first {@link PhysicalPath} which can be construed as a parent
   * of the presented identifier.
   *
   * @param identifier to locate the parent of (required)
   * @return the first matching parent, or null if not found
   */
  @Override
  protected PhysicalPath getApplicablePhysicalPath(final String identifier) {
    Validate.notNull(identifier, "Identifier required");
    PhysicalPath physicalPath = null;
    int longest = 0;
    for (final Pom pom : pomManagementService.getPoms()) {
      if (removeTrailingSeparator(identifier).startsWith(removeTrailingSeparator(pom.getRoot()))
          && removeTrailingSeparator(pom.getRoot()).length() > longest) {
        longest = removeTrailingSeparator(pom.getRoot()).length();
        int nextLongest = 0;
        for (final PhysicalPath thisPhysicalPath : pom.getPhysicalPaths()) {
          String possibleParent =
              new FileDetails(thisPhysicalPath.getLocation(), null).getCanonicalPath();
          if (!possibleParent.endsWith(File.separator)) {
            possibleParent = possibleParent.concat(File.separator);
          }
          if (removeTrailingSeparator(identifier).startsWith(possibleParent)
              && possibleParent.length() > nextLongest) {
            nextLongest = possibleParent.length();
            physicalPath = thisPhysicalPath;
          }
        }
      }
    }
    return physicalPath;
  }

  public String getCanonicalPath(final LogicalPath path, final JavaType javaType) {
    return getIdentifier(path, javaType.getRelativeFileName());
  }

  public String getCanonicalPath(final String moduleName, final Path path, final JavaType javaType) {
    Validate.notNull(moduleName, "Module name is null");
    return getCanonicalPath(path.getModulePathId(moduleName), javaType);
  }

  public String getFocusedCanonicalPath(final Path path, final JavaType javaType) {
    return getCanonicalPath(pomManagementService.getFocusedModuleName(), path, javaType);
  }

  public String getFocusedIdentifier(final Path path, final String relativePath) {
    return getIdentifier(
        LogicalPath.getInstance(path, pomManagementService.getFocusedModuleName()), relativePath);
  }

  public LogicalPath getFocusedPath(final Path path) {
    final PhysicalPath physicalPath = pomManagementService.getFocusedModule().getPhysicalPath(path);
    Validate.notNull(physicalPath, "Physical path for '%s' not found", path.name());
    return physicalPath.getLogicalPath();
  }

  public String getFocusedRoot(final Path path) {
    return pomManagementService.getFocusedModule().getPathLocation(path);
  }

  public String getIdentifier(final LogicalPath logicalPath, final String relativePath) {
    Validate.notNull(logicalPath, "Path required");
    Validate.notNull(relativePath, "Relative path cannot be null, although it can be empty");

    String initialPath = FileUtils.getCanonicalPath(getPath(logicalPath));
    initialPath = FileUtils.ensureTrailingSeparator(initialPath);
    return initialPath + StringUtils.strip(relativePath, File.separator);
  }

  public String getIdentifier(final String moduleName, final Path path, final String relativePath) {
    return getIdentifier(LogicalPath.getInstance(path, moduleName), relativePath);
  }

  private File getModuleRoot(final String module, final Pom pom) {
    if (pom == null) {
      // No POM exists for this module; we must be creating it
      return new File(pomManagementService.getFocusedModule().getRoot(), module);
    }
    // This is a known module; use its known root path
    return new File(pom.getRoot());
  }

  public LogicalPath getPath(final String moduleName, final Path path) {
    Validate.notNull(moduleName, "ModuleName required");

    final PhysicalPath physicalPath =
        pomManagementService.getPomFromModuleName(moduleName).getPhysicalPath(path);
    Validate.notNull(physicalPath, "Physical path for '%s' not found", path.name());
    return physicalPath.getLogicalPath();
  }


  private File getPath(final LogicalPath logicalPath) {
    final Pom pom = pomManagementService.getPomFromModuleName(logicalPath.getModule());
    final File moduleRoot = getModuleRoot(logicalPath.getModule(), pom);
    final String pathRelativeToPom = logicalPath.getPathRelativeToPom(pom);
    return new File(moduleRoot, pathRelativeToPom);
  }

  @Override
  protected Collection<LogicalPath> getPaths(final boolean sourceOnly) {
    final Collection<LogicalPath> pathIds = new ArrayList<LogicalPath>();
    for (final Pom pom : pomManagementService.getPoms()) {
      for (final PhysicalPath modulePath : pom.getPhysicalPaths()) {
        if (!sourceOnly || modulePath.isSource()) {
          pathIds.add(modulePath.getLogicalPath());
        }
      }
    }
    return pathIds;
  }

  public String getRoot(final LogicalPath modulePathId) {
    final Pom pom = pomManagementService.getPomFromModuleName(modulePathId.getModule());
    return pom.getPhysicalPath(modulePathId.getPath()).getLocationPath();
  }

  public boolean isActive() {
    return pomManagementService.getRootPom() != null;
  }

  private String removeTrailingSeparator(final String pomPath) {
    if (pomPath.endsWith(File.separator)) {
      return pomPath.substring(0, pomPath.length() - 1);
    }
    return pomPath + File.separator;
  }
}
