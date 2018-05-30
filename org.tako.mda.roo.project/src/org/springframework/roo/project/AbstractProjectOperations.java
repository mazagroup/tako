package org.springframework.roo.project;

import static org.springframework.roo.project.DependencyScope.COMPILE;
import static org.springframework.roo.support.util.AnsiEscapeCode.FG_CYAN;
import static org.springframework.roo.support.util.AnsiEscapeCode.decorate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.model.JavaPackage;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.project.maven.Pom;
import org.springframework.roo.shell.Shell;
import org.springframework.roo.support.util.CollectionUtils;
import org.springframework.roo.support.util.DomUtils;
import org.springframework.roo.support.util.XmlElementBuilder;
import org.springframework.roo.support.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides common project operations. Should be subclassed by a
 * project-specific operations subclass.
 *
 * @author Ben Alex
 * @author Adrian Colyer
 * @author Stefan Schmidt
 * @author Alan Stewart
 * @author Juan Carlos García
 * @author Paula Navarro
 * @since 1.0
 */
//@SuppressWarnings("deprecation")
public abstract class AbstractProjectOperations implements ProjectOperations {

  private static final String DEFAULT_VALUE_TEXT = "VALUE_TEXT";
  static final String ADDED = "added";
  static final String CHANGED = "changed";
  static final String REMOVED = "removed";
  static final String SKIPPED = "skipped";
  static final String UPDATED = "updated";

  private final Map<String, Feature> features = new HashMap<String, Feature>();

  protected FileManager fileManager;
  protected MetadataService metadataService;
  protected PathResolver pathResolver;
  protected PomManagementService pomManagementService;
  protected Shell shell;

  /**
   * Generates a message about the addition of the given items to the POM
   *
   * @param action the past tense of the action that was performed
   * @param items the items that were acted upon (required, can be empty)
   * @param singular the singular of this type of item (required)
   * @param plural the plural of this type of item (required)
   * @return a non-<code>null</code> message
   * @since 1.2.0
   */
  static String getDescriptionOfChange(final String action, final Collection<String> items,
      final String singular, final String plural) {
    if (items.isEmpty()) {
      return "";
    }
    return highlight(action + " " + (items.size() == 1 ? singular : plural)) + " "
        + StringUtils.join(items, ", ");
  }

  /**
   * Highlights the given text
   *
   * @param text the text to highlight (can be blank)
   * @return the highlighted text
   */
  static String highlight(final String text) {
    return decorate(text, FG_CYAN);
  }

  @Override
  public void addBuildPlugin(final String moduleName, final Plugin plugin) {
    addBuildPlugin(moduleName, plugin, true);
  }

  @Override
  public void addBuildPlugin(final String moduleName, final Plugin plugin,
      boolean addToPluginManagement) {
    Validate.isTrue(isProjectAvailable(moduleName), "Plugin modification prohibited at this time");
    Validate.notNull(plugin, "Plugin required");
    addBuildPlugins(moduleName, Collections.singletonList(plugin), addToPluginManagement);
  }

  @Override
  public void addBuildPlugins(final String moduleName, final Collection<? extends Plugin> newPlugins) {
    addBuildPlugins(moduleName, newPlugins, true);
  }

  @Override
  public void addBuildPlugins(final String moduleName,
      final Collection<? extends Plugin> newPlugins, boolean addToPluginManagement) {
    Validate.isTrue(isProjectAvailable(moduleName), "Plugin modification prohibited at this time");
    Validate.notNull(newPlugins, "Plugins required");
    if (CollectionUtils.isEmpty(newPlugins)) {
      return;
    }
    final Pom parentPom = getPomFromModuleName("");
    boolean isSamePom = false;
    Pom pom = null;
    if ("".equals(moduleName)) {
      isSamePom = true;
      pom = parentPom;
    } else {
      pom = getPomFromModuleName(moduleName);
    }
    Validate.notNull(parentPom,
        "The parentPom is not available, so plugin addition cannot be performed");
    Validate.notNull(pom, "The pom is not available, so plugin addition cannot be performed");


    final Document parentDocument =
        XmlUtils.readXml(fileManager.getInputStream(parentPom.getPath()));
    Document document = null;
    if (isSamePom) {
      document = parentDocument;
    } else {
      document = XmlUtils.readXml(fileManager.getInputStream(pom.getPath()));
    }

    writePluginInPom(newPlugins, parentPom, pom, parentDocument, document,
        parentDocument.getDocumentElement(), document.getDocumentElement(), addToPluginManagement,
        isSamePom);

  }

  @Override
  public List<Dependency> addDependencies(final String moduleName,
      final Collection<? extends Dependency> newDependencies) {
    return addDependencies(moduleName, newDependencies, true, false);
  }

  @Override
  public List<Dependency> addDependencies(final String moduleName,
      final Collection<? extends Dependency> newDependencies, boolean addToDependencyManagement,
      boolean checkFullDependency) {
    Validate.isTrue(isProjectAvailable(moduleName),
        "Dependency modification prohibited; no such module '%s'", moduleName);
    final Pom parentPom = getPomFromModuleName("");
    boolean isSamePom = false;
    Pom pom = null;
    if ("".equals(moduleName)) {
      isSamePom = true;
      pom = parentPom;
    } else {
      pom = getPomFromModuleName(moduleName);
    }
    Validate.notNull(pom, "The pom is not available, so dependencies cannot be added");

    final Document parentDocument =
        XmlUtils.readXml(fileManager.getInputStream(parentPom.getPath()));
    Document document = null;
    if (isSamePom) {
      document = parentDocument;
    } else {
      document = XmlUtils.readXml(fileManager.getInputStream(pom.getPath()));
    }
    return writeDependencyInPom(newDependencies, parentPom, pom, parentDocument, document,
        parentDocument.getDocumentElement(), document.getDocumentElement(),
        addToDependencyManagement, isSamePom, checkFullDependency);
  }

  @Override
  public Dependency addDependency(final String moduleName, final Dependency dependency) {
    return addDependency(moduleName, dependency, true, false);
  }

  @Override
  public Dependency addDependency(final String moduleName, final Dependency dependency,
      boolean addToDependencyManagement) {
    return addDependency(moduleName, dependency, addToDependencyManagement, false);
  }

  @Override
  public Dependency addDependency(final String moduleName, final Dependency dependency,
      boolean addToDependencyManagement, boolean checkFullDependency) {
    Validate.isTrue(isProjectAvailable(moduleName),
        "Dependency modification prohibited at this time");
    Validate.notNull(dependency, "Dependency required");
    List<Dependency> result =
        addDependencies(moduleName, Collections.singletonList(dependency),
            addToDependencyManagement, checkFullDependency);

    if (result.isEmpty()) {
      return null;
    }
    return result.get(0);
  }

  @Override
  public final Dependency addDependency(final String moduleName, final String groupId,
      final String artifactId, final String version) {
    return addDependency(moduleName, groupId, artifactId, version, true);
  }

  @Override
  public final Dependency addDependency(final String moduleName, final String groupId,
      final String artifactId, final String version, boolean addToDependencyManagement) {
    return addDependency(moduleName, groupId, artifactId, version, COMPILE,
        addToDependencyManagement);
  }

  @Override
  public final Dependency addDependency(final String moduleName, final String groupId,
      final String artifactId, final String version, final DependencyScope scope) {
    return addDependency(moduleName, groupId, artifactId, version, scope, true);
  }

  @Override
  public final Dependency addDependency(final String moduleName, final String groupId,
      final String artifactId, final String version, final DependencyScope scope,
      boolean addToDependencyManagement) {
    return addDependency(moduleName, groupId, artifactId, version, scope, "",
        addToDependencyManagement);
  }

  @Override
  public final Dependency addDependency(final String moduleName, final String groupId,
      final String artifactId, final String version, DependencyScope scope, final String classifier) {
    return addDependency(moduleName, groupId, artifactId, version, scope, classifier, true);
  }

  @Override
  public final Dependency addDependency(final String moduleName, final String groupId,
      final String artifactId, final String version, DependencyScope scope,
      final String classifier, boolean addToDependencyManagement) {
    Validate.isTrue(isProjectAvailable(moduleName),
        "Dependency modification prohibited at this time");
    Validate.notNull(groupId, "Group ID required");
    Validate.notNull(artifactId, "Artifact ID required");
    Validate.notBlank(version, "Version required");
    if (scope == null) {
      scope = COMPILE;
    }
    final Dependency dependency =
        new Dependency(groupId, artifactId, version, DependencyType.JAR, scope, classifier);
    return addDependency(moduleName, dependency, addToDependencyManagement, false);
  }

  public void addFilter(final String moduleName, final Filter filter) {
    Validate.isTrue(isProjectAvailable(moduleName), "Filter modification prohibited at this time");
    Validate.notNull(filter, "Filter required");
    final Pom pom = getPomFromModuleName(moduleName);
    Validate.notNull(pom, "The pom is not available, so filter addition cannot be performed");
    if (filter == null || pom.isFilterRegistered(filter)) {
      return;
    }

    final Document document = XmlUtils.readXml(fileManager.getInputStream(pom.getPath()));
    final Element root = document.getDocumentElement();
    final String descriptionOfChange;
    final Element buildElement = XmlUtils.findFirstElement("/project/build", root);
    final Element existingFilter =
        XmlUtils.findFirstElement("filters/filter['" + filter.getValue() + "']", buildElement);
    if (existingFilter == null) {
      // No such filter; add it
      final Element filtersElement =
          DomUtils.createChildIfNotExists("filters", buildElement, document);
      filtersElement.appendChild(XmlUtils.createTextElement(document, "filter", filter.getValue()));
      descriptionOfChange = highlight(ADDED + " filter") + " '" + filter.getValue() + "'";
    } else {
      existingFilter.setTextContent(filter.getValue());
      descriptionOfChange = highlight(UPDATED + " filter") + " '" + filter.getValue() + "'";
    }

    fileManager.createOrUpdateTextFileIfRequired(pom.getPath(), XmlUtils.nodeToString(document),
        descriptionOfChange, false);
  }

  @Override
  public void addPackageToPluginExecution(final String moduleName, final Plugin plugin,
      String executionId, final String packageName) {
    addPackageToPluginExecution(moduleName, plugin, executionId, packageName, true);
  }

  @Override
  public void addPackageToPluginExecution(final String moduleName, final Plugin plugin,
      String executionId, final String packageName, boolean addToPluginManagement) {
    // Delegates in generic addElementToPluginExecution
    addElementToPluginExecution(moduleName, plugin, executionId, "packages", "package",
        packageName, addToPluginManagement);
  }

  @Override
  public void addElementToPluginExecution(final String moduleName, final Plugin plugin,
      String executionId, String parentElementName, String elementName, final String elementValue) {
    addElementToPluginExecution(moduleName, plugin, executionId, parentElementName, elementName,
        elementValue, true);
  }

  @Override
  public void addElementToPluginExecution(final String moduleName, final Plugin plugin,
      String executionId, String parentElementName, String elementName, String elementValue,
      boolean addToPluginManagement) {
    Map<String, String> elementValues = new HashMap<String, String>();
    elementValues.put(DEFAULT_VALUE_TEXT, elementValue);
    addElementToPluginExecution(moduleName, plugin, executionId, parentElementName, elementName,
        elementValues, addToPluginManagement);
  }


  @Override
  public void addElementToPluginExecution(final String moduleName, final Plugin plugin,
      String executionId, String parentElementName, String elementName,
      Map<String, String> elementValues) {
    addElementToPluginExecution(moduleName, plugin, executionId, parentElementName, elementName,
        elementValues, true);
  }

  @Override
  public void addElementToPluginExecution(final String moduleName, final Plugin plugin,
      String executionId, String parentElementName, String elementName,
      Map<String, String> elementValues, boolean addToPluginManagement) {

    Validate.isTrue(isProjectAvailable(moduleName), "Package modification prohibited at this time");
    Validate.notNull(executionId, "Execution id required");
    Validate.notNull(plugin, "Plugin required");
    Validate.notNull(parentElementName, "parentElementName required");
    Validate.notNull(elementName, "elementName required");
    Validate.notNull(elementValues, "elementValues required");

    String descriptionOfChange;
    final Pom parentPom = getPomFromModuleName("");
    boolean isSamePom = false;
    Pom pom = null;
    if ("".equals(moduleName)) {
      isSamePom = true;
      pom = parentPom;
    } else {
      pom = getPomFromModuleName(moduleName);
    }
    Validate.notNull(parentPom,
        "The parentPom is not available, so plugin addition cannot be performed");
    Validate.notNull(pom, "The pom is not available, so plugin addition cannot be performed");


    final Document parentDocument =
        XmlUtils.readXml(fileManager.getInputStream(parentPom.getPath()));
    Document document = null;
    if (isSamePom) {
      document = parentDocument;
    } else {
      document = XmlUtils.readXml(fileManager.getInputStream(pom.getPath()));
    }

    final Element parentRoot = parentDocument.getDocumentElement();
    final Element root = document.getDocumentElement();


    // Find plugin
    Element pluginsElement = null;
    if (addToPluginManagement) {
      pluginsElement =
          DomUtils.createChildIfNotExists("build/pluginManagement/plugins", parentRoot,
              parentDocument);
    } else {
      pluginsElement = DomUtils.createChildIfNotExists("/project/build/plugins", root, document);
    }

    final List<Element> existingPluginElements = XmlUtils.findElements("plugin", pluginsElement);


    for (final Element existingPluginElement : existingPluginElements) {
      final Plugin existingPlugin = new Plugin(existingPluginElement);

      if (existingPlugin.hasSameCoordinates(plugin)) {

        for (final Execution execution : existingPlugin.getExecutions()) {
          if (executionId.equals(execution.getId()) && execution.getConfiguration() != null) {

            // Check if element is already added
            final Element elementsElement =
                DomUtils.createChildIfNotExists(parentElementName, execution.getConfiguration()
                    .getConfiguration(), document);

            final List<Element> existingElements =
                XmlUtils.findElements(elementName, elementsElement);

            for (Element existingElement : existingElements) {
              for (Entry<String, String> element : elementValues.entrySet()) {
                if (elementValues.size() == 1 && element.getKey().equals(DEFAULT_VALUE_TEXT)) {
                  final String elem = DomUtils.getTextContent(existingElement, "");
                  if (elem.equals(element.getValue())) {
                    return;
                  }
                } else {
                  Element childElement =
                      DomUtils.getChildElementByTagName(existingElement, element.getKey());
                  if (childElement != null
                      && DomUtils.getTextContent(childElement, "").equals(element.getValue())) {
                    return;
                  }
                }
              }
            }

            // No such package; add it
            Element newParentElement = null;
            for (Entry<String, String> element : elementValues.entrySet()) {
              descriptionOfChange =
                  highlight(ADDED + " " + elementName + "/" + element.getKey()) + " '"
                      + element.getValue() + "'";

              if (elementValues.size() == 1 && element.getKey().equals(DEFAULT_VALUE_TEXT)) {
                if (!isSamePom && addToPluginManagement) {
                  elementsElement.appendChild(XmlUtils.createTextElement(parentDocument,
                      elementName, element.getValue()));
                  fileManager.createOrUpdateTextFileIfRequired(parentPom.getPath(),
                      XmlUtils.nodeToString(parentDocument), descriptionOfChange, false);
                } else {
                  elementsElement.appendChild(XmlUtils.createTextElement(document, elementName,
                      element.getValue()));
                  fileManager.createOrUpdateTextFileIfRequired(pom.getPath(),
                      XmlUtils.nodeToString(document), descriptionOfChange, false);
                }
              } else {
                if (!isSamePom && addToPluginManagement) {
                  if (newParentElement == null) {
                    newParentElement =
                        DomUtils.createChildIfNotExists(elementName, execution.getConfiguration()
                            .getConfiguration(), parentDocument);
                    elementsElement.appendChild(newParentElement);
                  }
                  newParentElement.appendChild(XmlUtils.createTextElement(parentDocument,
                      element.getKey(), element.getValue()));
                  fileManager.createOrUpdateTextFileIfRequired(parentPom.getPath(),
                      XmlUtils.nodeToString(parentDocument), descriptionOfChange, false);
                } else {
                  if (newParentElement == null) {
                    newParentElement =
                        DomUtils.createChildIfNotExists(elementName, execution.getConfiguration()
                            .getConfiguration(), document);
                    elementsElement.appendChild(newParentElement);
                  }
                  newParentElement.appendChild(XmlUtils.createTextElement(document,
                      element.getKey(), element.getValue()));
                  fileManager.createOrUpdateTextFileIfRequired(pom.getPath(),
                      XmlUtils.nodeToString(document), descriptionOfChange, false);
                }
              }
            }
          }
        }
      }
    }
  }

  public void addModuleDependency(final String moduleToDependUpon) {
    addModuleDependency(getFocusedModuleName(), moduleToDependUpon, false);
  }

  public void addModuleDependency(final String moduleName, final String moduleToDependUpon) {
    addModuleDependency(moduleName, moduleToDependUpon, false);
  }

  public void addModuleDependency(final String moduleName, final String moduleToDependUpon,
      boolean testDependency) {
    Validate.isTrue(isProjectAvailable(moduleName),
        "Dependency modification prohibited at this time");
    Validate.notNull(moduleToDependUpon, "Dependency required");

    if (StringUtils.isBlank(moduleToDependUpon)) {
      return; // No need to ever add a dependency upon the root POM
    }
    final Pom module = getPomFromModuleName(moduleName);
    if (module != null && StringUtils.isNotBlank(module.getModuleName())
        && !moduleToDependUpon.equals(module.getModuleName())) {
      final ProjectMetadata dependencyProject = getProjectMetadata(moduleToDependUpon);
      if (dependencyProject != null) {
        final Pom dependencyPom = dependencyProject.getPom();
        if (!dependencyPom.getPath().equals(module.getPath())) {
          if (testDependency) {

            // Add as test-type dependency
            final Dependency dependency =
                dependencyPom.asDependency(DependencyScope.TEST,
                    DependencyType.valueOfTypeCode("test-jar"));
            detectCircularDependency(module, dependencyPom);
            addDependency(module.getModuleName(), dependency);
          } else {

            // Add as "normal" dependency
            final Dependency dependency = dependencyPom.asDependency(COMPILE);
            if (!module.hasDependencyExcludingVersion(dependency)) {
              detectCircularDependency(module, dependencyPom);
              addDependency(module.getModuleName(), dependency);
            }
          }
        }
      }
    }
  }

  public void addPluginRepositories(final String moduleName,
      final Collection<? extends Repository> repositories) {
    Validate.isTrue(isProjectAvailable(moduleName),
        "Plugin repository modification prohibited at this time");
    Validate.notNull(repositories, "Plugin repositories required");
    addRepositories(moduleName, repositories, "pluginRepositories", "pluginRepository");
  }

  public void addPluginRepository(final String moduleName, final Repository repository) {
    Validate.isTrue(isProjectAvailable(moduleName),
        "Plugin repository modification prohibited at this time");
    Validate.notNull(repository, "Repository required");
    addRepository(moduleName, repository, "pluginRepositories", "pluginRepository");
  }

  public void addProperty(final String moduleName, final Property property) {
    Validate
        .isTrue(isProjectAvailable(moduleName), "Property modification prohibited at this time");
    Validate.notNull(property, "Property to add required");
    final Pom pom = getPomFromModuleName(moduleName);
    Validate.notNull(pom, "The pom is not available, so property addition cannot be performed");
    if (pom.isPropertyRegistered(property)) {
      return;
    }

    final Document document = XmlUtils.readXml(fileManager.getInputStream(pom.getPath()));
    final Element root = document.getDocumentElement();
    final String descriptionOfChange;
    final Element existing =
        XmlUtils.findFirstElement("/project/properties/" + property.getName(), root);
    if (existing == null) {
      // No existing property of this name; add it
      final Element properties =
          DomUtils.createChildIfNotExists("properties", document.getDocumentElement(), document);
      properties.appendChild(XmlUtils.createTextElement(document, property.getName(),
          property.getValue()));
      descriptionOfChange =
          highlight(ADDED + " property") + " '" + property.getName() + "' = '"
              + property.getValue() + "'";
    } else {
      // A property of this name exists; update it
      existing.setTextContent(property.getValue());
      descriptionOfChange =
          highlight(UPDATED + " property") + " '" + property.getName() + "' to '"
              + property.getValue() + "'";
    }

    fileManager.createOrUpdateTextFileIfRequired(pom.getPath(), XmlUtils.nodeToString(document),
        descriptionOfChange, false);
  }

  public void addRepositories(final String moduleName,
      final Collection<? extends Repository> repositories) {
    addRepositories(moduleName, repositories, "repositories", "repository");
  }

  private void addRepositories(final String moduleName,
      final Collection<? extends Repository> repositories, final String containingPath,
      final String path) {
    Validate.isTrue(isProjectAvailable(moduleName),
        "Repository modification prohibited at this time");
    Validate.notNull(repositories, "Repositories required");

    if (CollectionUtils.isEmpty(repositories)) {
      return;
    }
    final Pom pom = getPomFromModuleName(moduleName);
    Validate.notNull(pom, "The pom is not available, so repository addition cannot be performed");
    final Document document = XmlUtils.readXml(fileManager.getInputStream(pom.getPath()));
    final Element repositoriesElement =
        DomUtils.createChildIfNotExists(containingPath, document.getDocumentElement(), document);

    if ("pluginRepository".equals(path)) {
      if (pom.isAllPluginRepositoriesRegistered(repositories)) {
        return;
      }
    } else if (pom.isAllRepositoriesRegistered(repositories)) {
      return;
    }

    final List<Repository> existingRepositories = new ArrayList<Repository>();
    for (Element exisitingRepElement : XmlUtils.findElements(path, repositoriesElement)) {
      existingRepositories.add(new Repository(exisitingRepElement));
    }

    final List<String> addedRepositories = new ArrayList<String>();
    for (final Repository repository : repositories) {
      if ("pluginRepository".equals(path)) {
        if (pom.isPluginRepositoryRegistered(repository)) {
          continue;
        } else if (existingRepositories.contains(repository)) {
          continue;
        }
      } else {
        if (pom.isRepositoryRegistered(repository)) {
          continue;
        } else if (existingRepositories.contains(repository)) {
          continue;
        }
      }
      if (repository != null) {
        repositoriesElement.appendChild(repository.getElement(document, path));
        addedRepositories.add(repository.getUrl());
      }
    }
    final String message = getDescriptionOfChange(ADDED, addedRepositories, path, containingPath);

    fileManager.createOrUpdateTextFileIfRequired(pom.getPath(), XmlUtils.nodeToString(document),
        message, false);
  }

  public void addRepository(final String moduleName, final Repository repository) {
    addRepository(moduleName, repository, "repositories", "repository");
  }

  private void addRepository(final String moduleName, final Repository repository,
      final String containingPath, final String path) {
    Validate.isTrue(isProjectAvailable(moduleName),
        "Repository modification prohibited at this time");
    Validate.notNull(repository, "Repository required");
    addRepositories(moduleName, Collections.singletonList(repository), containingPath, path);
  }

  public void addResource(final String moduleName, final Resource resource) {
    Validate
        .isTrue(isProjectAvailable(moduleName), "Resource modification prohibited at this time");
    Validate.notNull(resource, "Resource to add required");
    final Pom pom = getPomFromModuleName(moduleName);
    Validate.notNull(pom, "The pom is not available, so resource addition cannot be performed");
    if (pom.isResourceRegistered(resource)) {
      return;
    }

    final Document document = XmlUtils.readXml(fileManager.getInputStream(pom.getPath()));
    final Element buildElement =
        XmlUtils.findFirstElement("/project/build", document.getDocumentElement());
    final Element resourcesElement =
        DomUtils.createChildIfNotExists("resources", buildElement, document);
    resourcesElement.appendChild(resource.getElement(document));
    final String descriptionOfChange =
        highlight(ADDED + " resource") + " " + resource.getSimpleDescription();

    fileManager.createOrUpdateTextFileIfRequired(pom.getPath(), XmlUtils.nodeToString(document),
        descriptionOfChange, false);
  }

  protected void bindFeature(final Feature feature) {
    if (feature != null) {
      features.put(feature.getName(), feature);
    }
  }
  
  protected void unbindFeature(final Feature feature) {
    if (feature != null) {
      features.remove(feature.getName());
    }
  }


  private void detectCircularDependency(final Pom module, final Pom dependencyModule) {
    if (hasModuleDependency(dependencyModule, module)) {
      throw new IllegalStateException("ERROR: Circular dependency detected, '"
          + dependencyModule.getModuleName() + "' already depends on '" + module.getModuleName()
          + "'.");
    }
  }

  private boolean hasModuleDependency(Pom module, Pom dependencyModule) {
    if (module.isDependencyRegistered(dependencyModule.asDependency(COMPILE), false)) {
      return true;
    }

    for (String moduleName : getModuleNames()) {
      Pom relatedModule = getPomFromModuleName(moduleName);

      if (module.isDependencyRegistered(relatedModule.asDependency(COMPILE), false)
          && hasModuleDependency(relatedModule, dependencyModule)) {
        return true;
      }
    }
    return false;
  }

  public Pom getFocusedModule() {
    final ProjectMetadata focusedProjectMetadata = getFocusedProjectMetadata();
    if (focusedProjectMetadata == null) {
      return null;
    }
    return focusedProjectMetadata.getPom();
  }

  public String getFocusedModuleName() {
    return pomManagementService.getFocusedModuleName();
  }

  public ProjectMetadata getFocusedProjectMetadata() {
    return getProjectMetadata(getFocusedModuleName());
  }

  public String getFocusedProjectName() {
    return getProjectName(getFocusedModuleName());
  }

  public JavaPackage getFocusedTopLevelPackage() {
    return getTopLevelPackage(getFocusedModuleName());
  }

  public Pom getModuleForFileIdentifier(final String fileIdentifier) {
	  return pomManagementService.getModuleForFileIdentifier(fileIdentifier);
  }

  public Collection<String> getModuleNames() {
    return pomManagementService.getModuleNames();
  }

  public PathResolver getPathResolver() {
    return pathResolver;
  }

  public final Pom getPomFromModuleName(final String moduleName) {
    final ProjectMetadata projectMetadata = getProjectMetadata(moduleName);
    return projectMetadata == null ? null : projectMetadata.getPom();
  }

  public Collection<Pom> getPoms() {
    return pomManagementService.getPoms();
  }

  private String getPomDependenciesUpdateMessage(final Collection<String> addedDependencies,
      final Collection<String> removedDependencies, final Collection<String> skippedDependencies) {
    final List<String> changes = new ArrayList<String>();
    changes.add(getDescriptionOfChange(ADDED, addedDependencies, "dependency", "dependencies"));
    changes.add(getDescriptionOfChange(REMOVED, removedDependencies, "dependency", "dependencies"));
    changes.add(getDescriptionOfChange(SKIPPED, skippedDependencies, "dependency", "dependencies"));
    for (final Iterator<String> iter = changes.iterator(); iter.hasNext();) {
      if (StringUtils.isBlank(iter.next())) {
        iter.remove();
      }
    }
    return StringUtils.join(changes, "; ");
  }

  private String getPomPluginsUpdateMessage(final Collection<String> addedPlugins,
      final Collection<String> removedPlugins) {
    final List<String> changes = new ArrayList<String>();
    changes.add(getDescriptionOfChange(ADDED, addedPlugins, "plugin", "plugins"));
    changes.add(getDescriptionOfChange(REMOVED, removedPlugins, "plugin", "plugins"));
    for (final Iterator<String> iter = changes.iterator(); iter.hasNext();) {
      if (StringUtils.isBlank(iter.next())) {
        iter.remove();
      }
    }
    return StringUtils.join(changes, "; ");
  }

  public final ProjectMetadata getProjectMetadata(final String moduleName) {
    return (ProjectMetadata) metadataService.get(ProjectMetadata.getProjectIdentifier(moduleName));
  }

  public String getProjectName(final String moduleName) {
    final Pom pom = getPomFromModuleName(moduleName);
    Validate.notNull(pom, "A pom with module name '%s' could not be found", moduleName);
    return pom.getDisplayName();
  }

  public JavaPackage getTopLevelPackage(final String moduleName) {
    final Pom pom = getPomFromModuleName(moduleName);
    if (pom != null) {
      if (StringUtils.isBlank(moduleName)) {
        return new JavaPackage(pom.getGroupId(), moduleName);
      } else {
        // Module package
        return new JavaPackage(pom.getGroupId().concat(".")
            .concat(pom.getArtifactId().replace("-", ".")), moduleName);
      }
    }
    return null;
  }

  public boolean isFeatureInstalled(final String featureName) {
    final Feature feature = features.get(featureName);
    if (feature == null) {
      return false;
    }
    for (final String moduleName : getModuleNames()) {
      if (feature.isInstalledInModule(moduleName)) {
        return true;
      }
    }
    return false;
  }

  public boolean isFeatureInstalled(final String... featureNames) {
    for (final String featureName : featureNames) {
      if (isFeatureInstalled(featureName)) {
        return true;
      }
    }
    return false;
  }

  public boolean isFeatureInstalled(final Pom module, final String featureName) {
    final Feature feature = features.get(featureName);
    if (feature == null) {
      return false;
    }
    if (feature.isInstalledInModule(module.getModuleName())) {
      return true;
    }
    return false;
  }

  public boolean isFocusedProjectAvailable() {
    return isProjectAvailable(getFocusedModuleName());
  }

  public boolean isModuleCreationAllowed() {
    return isProjectAvailable("") && isModuleFocusAllowed();
  }

  public boolean isModuleFocusAllowed() {
    return getModuleNames().size() > 1;
  }

  public boolean isMultimoduleProject() {
    return isModuleFocusAllowed();
  }

  public final boolean isProjectAvailable(final String moduleName) {
    return getProjectMetadata(moduleName) != null;
  }

  public void removeBuildPlugin(final String moduleName, final Plugin plugin) {
    Validate.isTrue(isProjectAvailable(moduleName), "Plugin modification prohibited at this time");
    Validate.notNull(plugin, "Plugin required");
    removeBuildPlugins(moduleName, Collections.singletonList(plugin));
  }

  public void removeBuildPluginImmediately(final String moduleName, final Plugin plugin) {
    Validate.isTrue(isProjectAvailable(moduleName), "Plugin modification prohibited at this time");
    Validate.notNull(plugin, "Plugin required");
    removeBuildPlugins(moduleName, Collections.singletonList(plugin), true);
  }

  public void removeBuildPlugins(final String moduleName, final Collection<? extends Plugin> plugins) {
    removeBuildPlugins(moduleName, plugins, false);
  }

  private void removeBuildPlugins(final String moduleName,
      final Collection<? extends Plugin> plugins, final boolean writeImmediately) {
    Validate.isTrue(isProjectAvailable(moduleName), "Plugin modification prohibited at this time");
    Validate.notNull(plugins, "Plugins required");
    if (CollectionUtils.isEmpty(plugins)) {
      return;
    }
    final Pom pom = getPomFromModuleName(moduleName);
    Validate.notNull(pom, "The pom is not available, so plugin removal cannot be performed");
    if (!pom.isAnyPluginsRegistered(plugins)) {
      return;
    }

    final Document document = XmlUtils.readXml(fileManager.getInputStream(pom.getPath()));
    final Element root = document.getDocumentElement();
    final Element pluginsElement = XmlUtils.findFirstElement("/project/build/plugins", root);
    if (pluginsElement == null) {
      return;
    }

    final List<String> removedPlugins = new ArrayList<String>();
    for (final Plugin plugin : plugins) {
      // Can't filter the XPath on groupId, as it's optional in the POM
      // for Apache-owned plugins
      for (final Element candidate : XmlUtils.findElements(
          "plugin[artifactId = '" + plugin.getArtifactId() + "' and version = '"
              + plugin.getVersion() + "']", pluginsElement)) {
        final Plugin candidatePlugin = new Plugin(candidate);
        if (candidatePlugin.getGroupId().equals(plugin.getGroupId())) {
          // This element has the same groupId, artifactId, and
          // version as the plugin to be removed; remove it
          pluginsElement.removeChild(candidate);
          removedPlugins.add(candidatePlugin.getSimpleDescription());
          // Keep looping in case this plugin is in the POM more than
          // once (unlikely)
        }
      }
    }
    if (removedPlugins.isEmpty()) {
      return;
    }
    DomUtils.removeTextNodes(pluginsElement);
    final String message = getDescriptionOfChange(REMOVED, removedPlugins, "plugin", "plugins");

    fileManager.createOrUpdateTextFileIfRequired(pom.getPath(), XmlUtils.nodeToString(document),
        message, writeImmediately);
  }

  public void removeDependencies(final String moduleName,
      final Collection<? extends Dependency> dependenciesToRemove) {
    Validate.isTrue(isProjectAvailable(moduleName),
        "Dependency modification prohibited at this time");
    Validate.notNull(dependenciesToRemove, "Dependencies required");
    if (CollectionUtils.isEmpty(dependenciesToRemove)) {
      return;
    }
    final Pom pom = getPomFromModuleName(moduleName);
    Validate.notNull(pom, "The pom is not available, so dependency removal cannot be performed");
    if (!pom.isAnyDependenciesRegistered(dependenciesToRemove)) {
      return;
    }

    final Document document = XmlUtils.readXml(fileManager.getInputStream(pom.getPath()));
    final Element root = document.getDocumentElement();
    final Element dependenciesElement = XmlUtils.findFirstElement("/project/dependencies", root);
    if (dependenciesElement == null) {
      return;
    }

    final List<Element> existingDependencyElements =
        XmlUtils.findElements("dependency", dependenciesElement);
    final List<String> removedDependencies = new ArrayList<String>();
    for (final Dependency dependencyToRemove : dependenciesToRemove) {
      if (pom.isDependencyRegistered(dependencyToRemove, false)) {
        for (final Iterator<Element> iter = existingDependencyElements.iterator(); iter.hasNext();) {
          final Element candidate = iter.next();
          final Dependency candidateDependency = new Dependency(candidate);
          if (candidateDependency.equals(dependencyToRemove)) {
            // It's the same dependency; remove it
            dependenciesElement.removeChild(candidate);
            // Ensure we don't try to remove it again for another
            // Dependency
            iter.remove();
            removedDependencies.add(candidateDependency.getSimpleDescription());
          }
          // Keep looping in case it's in the POM more than once
        }
      }
    }
    if (removedDependencies.isEmpty()) {
      return;
    }
    DomUtils.removeTextNodes(dependenciesElement);
    final String message =
        getDescriptionOfChange(REMOVED, removedDependencies, "dependency", "dependencies");

    fileManager.createOrUpdateTextFileIfRequired(pom.getPath(), XmlUtils.nodeToString(document),
        message, false);
  }

  public void removeDependency(final String moduleName, final Dependency dependency) {
    removeDependency(moduleName, dependency, "/project/dependencies",
        "/project/dependencies/dependency");
  }

  /**
   * Removes an element identified by the given dependency, whenever it occurs
   * at the given path
   *
   * @param moduleName the name of the module to remove the dependency from
   * @param dependency the dependency to remove
   * @param containingPath the path to the dependencies element
   * @param path the path to the individual dependency elements
   */
  private void removeDependency(final String moduleName, final Dependency dependency,
      final String containingPath, final String path) {
    Validate.isTrue(isProjectAvailable(moduleName),
        "Dependency modification prohibited at this time");
    Validate.notNull(dependency, "Dependency to remove required");
    final Pom pom = getPomFromModuleName(moduleName);
    Validate.notNull(pom, "The pom is not available, so dependency removal cannot be performed");
    if (!pom.isDependencyRegistered(dependency, false)) {
      return;
    }

    final Document document = XmlUtils.readXml(fileManager.getInputStream(pom.getPath()));
    final Element root = document.getDocumentElement();

    String descriptionOfChange = "";
    final Element dependenciesElement = XmlUtils.findFirstElement(containingPath, root);
    for (final Element candidate : XmlUtils.findElements(path, root)) {
      if (dependency.equals(new Dependency(candidate))) {
        dependenciesElement.removeChild(candidate);
        descriptionOfChange =
            highlight(REMOVED + " dependency") + " " + dependency.getSimpleDescription();
        // Stay in the loop, just in case it was in the POM more than
        // once
      }
    }

    DomUtils.removeTextNodes(dependenciesElement);

    fileManager.createOrUpdateTextFileIfRequired(pom.getPath(), XmlUtils.nodeToString(document),
        descriptionOfChange, false);
  }

  public final void removeDependency(final String moduleName, final String groupId,
      final String artifactId, final String version) {
    removeDependency(moduleName, groupId, artifactId, version, "");
  }

  public final void removeDependency(final String moduleName, final String groupId,
      final String artifactId, final String version, final String classifier) {
    Validate.isTrue(isProjectAvailable(moduleName),
        "Dependency modification prohibited at this time");
    Validate.notNull(groupId, "Group ID required");
    Validate.notNull(artifactId, "Artifact ID required");
    Validate.notBlank(version, "Version required");
    final Dependency dependency =
        new Dependency(groupId, artifactId, version, DependencyType.JAR, COMPILE, classifier);
    removeDependency(moduleName, dependency);
  }

  /**
   * Method that removes version from element if blank or "-"
   *
   * @param element
   * @return Element without version if blank
   */
  private Element removeVersionIfBlank(Element element) {
    NodeList elementAttributes = element.getChildNodes();
    for (int i = 0; i < elementAttributes.getLength(); i++) {
      Element elementAttribute = (Element) elementAttributes.item(i);
      if (elementAttribute != null
          && elementAttribute.getTagName().equals("version")
          && (elementAttribute.getTextContent() == null
              || "-".equals(elementAttribute.getTextContent()) || "".equals(elementAttribute
              .getTextContent()))) {
        element.removeChild(elementAttributes.item(i));
        break;
      }
    }
    return element;
  }

  /**
   * Method that removes version from element if blank or "-"
   * 
   * @param element
   * @return Element without version if blank
   */
  private Element removeAllItems(Element element) {
    element = removeManagementConfiguration(element);
    NodeList elementAttributes = element.getChildNodes();
    List<Node> elementsToRemove = new ArrayList<Node>();
    for (int i = 0; i < elementAttributes.getLength(); i++) {
      Element elementAttribute = (Element) elementAttributes.item(i);
      if (elementAttribute != null && elementAttribute.getTagName().equals("executions")) {
        elementsToRemove.add(elementAttributes.item(i));
      }

      if (elementAttribute != null && elementAttribute.getTagName().equals("configuration")) {
        elementsToRemove.add(elementAttributes.item(i));
      }

      if (elementAttribute != null && elementAttribute.getTagName().equals("dependencies")) {
        elementsToRemove.add(elementAttributes.item(i));
      }
    }

    for (Node child : elementsToRemove) {
      element.removeChild(child);
    }

    return element;
  }

  /**
   * Method that removes version, scope and exclusions from provided element. 
   * 
   * @param element
   * @return Element without version, scope nor exclusions
   */
  private Element removeManagementConfiguration(Element element) {
    NodeList elementAttributes = element.getChildNodes();
    List<Node> elementsToRemove = new ArrayList<Node>();
    for (int i = 0; i < elementAttributes.getLength(); i++) {
      Element elementAttribute = (Element) elementAttributes.item(i);
      if (elementAttribute != null && elementAttribute.getTagName().equals("version")) {
        elementsToRemove.add(elementAttributes.item(i));
      }

      if (elementAttribute != null && elementAttribute.getTagName().equals("scope")) {
        elementsToRemove.add(elementAttributes.item(i));
      }

      if (elementAttribute != null && elementAttribute.getTagName().equals("exclusions")) {
        elementsToRemove.add(elementAttributes.item(i));
      }
    }

    for (Node child : elementsToRemove) {
      element.removeChild(child);
    }

    return element;
  }

  public void removeFilter(final String moduleName, final Filter filter) {
    Validate.isTrue(isProjectAvailable(moduleName), "Filter modification prohibited at this time");
    Validate.notNull(filter, "Filter required");
    final Pom pom = getPomFromModuleName(moduleName);
    Validate.notNull(pom, "The pom is not available, so filter removal cannot be performed");
    if (filter == null || !pom.isFilterRegistered(filter)) {
      return;
    }

    final Document document = XmlUtils.readXml(fileManager.getInputStream(pom.getPath()));
    final Element root = document.getDocumentElement();

    final Element filtersElement = XmlUtils.findFirstElement("/project/build/filters", root);
    if (filtersElement == null) {
      return;
    }

    String descriptionOfChange = "";
    for (final Element candidate : XmlUtils.findElements("filter", filtersElement)) {
      if (filter.equals(new Filter(candidate))) {
        filtersElement.removeChild(candidate);
        descriptionOfChange = highlight(REMOVED + " filter") + " '" + filter.getValue() + "'";
        // We will not break the loop (even though we could
        // theoretically), just in case it was in the POM more than once
      }
    }

    final List<Element> filterElements = XmlUtils.findElements("filter", filtersElement);
    if (filterElements.isEmpty()) {
      filtersElement.getParentNode().removeChild(filtersElement);
    }

    DomUtils.removeTextNodes(root);

    fileManager.createOrUpdateTextFileIfRequired(pom.getPath(), XmlUtils.nodeToString(document),
        descriptionOfChange, false);
  }

  public void removePluginRepository(final String moduleName, final Repository repository) {
    Validate.isTrue(isProjectAvailable(moduleName),
        "Plugin repository modification prohibited at this time");
    Validate.notNull(repository, "Repository required");
    removeRepository(moduleName, repository, "/project/pluginRepositories/pluginRepository");
  }

  public void removeProperty(final String moduleName, final Property property) {
    Validate
        .isTrue(isProjectAvailable(moduleName), "Property modification prohibited at this time");
    Validate.notNull(property, "Property to remove required");
    final Pom pom = getPomFromModuleName(moduleName);
    Validate.notNull(pom, "The pom is not available, so property removal cannot be performed");
    if (!pom.isPropertyRegistered(property)) {
      return;
    }

    final Document document = XmlUtils.readXml(fileManager.getInputStream(pom.getPath()));
    final Element root = document.getDocumentElement();
    final Element propertiesElement = XmlUtils.findFirstElement("/project/properties", root);
    String descriptionOfChange = "";
    for (final Element candidate : XmlUtils.findElements("/project/properties/*",
        document.getDocumentElement())) {
      if (property.equals(new Property(candidate))) {
        propertiesElement.removeChild(candidate);
        descriptionOfChange = highlight(REMOVED + " property") + " " + property.getName();
        // Stay in the loop just in case it was in the POM more than
        // once
      }
    }

    DomUtils.removeTextNodes(propertiesElement);

    fileManager.createOrUpdateTextFileIfRequired(pom.getPath(), XmlUtils.nodeToString(document),
        descriptionOfChange, false);
  }

  public void removeRepository(final String moduleName, final Repository repository) {
    removeRepository(moduleName, repository, "/project/repositories/repository");
  }

  private void removeRepository(final String moduleName, final Repository repository,
      final String path) {
    Validate.isTrue(isProjectAvailable(moduleName),
        "Repository modification prohibited at this time");
    Validate.notNull(repository, "Repository required");
    final Pom pom = getPomFromModuleName(moduleName);
    Validate.notNull(pom, "The pom is not available, so repository removal cannot be performed");
    if ("pluginRepository".equals(path)) {
      if (!pom.isPluginRepositoryRegistered(repository)) {
        return;
      }
    } else {
      if (!pom.isRepositoryRegistered(repository)) {
        return;
      }
    }

    final Document document = XmlUtils.readXml(fileManager.getInputStream(pom.getPath()));
    final Element root = document.getDocumentElement();

    String descriptionOfChange = "";
    for (final Element candidate : XmlUtils.findElements(path, root)) {
      if (repository.equals(new Repository(candidate))) {
        candidate.getParentNode().removeChild(candidate);
        descriptionOfChange = highlight(REMOVED + " repository") + " " + repository.getUrl();
        // We stay in the loop just in case it was in the POM more than
        // once
      }
    }

    fileManager.createOrUpdateTextFileIfRequired(pom.getPath(), XmlUtils.nodeToString(document),
        descriptionOfChange, false);
  }

  public void removeResource(final String moduleName, final Resource resource) {
    Validate
        .isTrue(isProjectAvailable(moduleName), "Resource modification prohibited at this time");
    Validate.notNull(resource, "Resource required");
    final Pom pom = getPomFromModuleName(moduleName);
    Validate.notNull(pom, "The pom is not available, so resource removal cannot be performed");
    if (!pom.isResourceRegistered(resource)) {
      return;
    }

    final Document document = XmlUtils.readXml(fileManager.getInputStream(pom.getPath()));
    final Element root = document.getDocumentElement();
    final Element resourcesElement = XmlUtils.findFirstElement("/project/build/resources", root);
    if (resourcesElement == null) {
      return;
    }
    String descriptionOfChange = "";
    for (final Element candidate : XmlUtils.findElements(
        "resource[directory = '" + resource.getDirectory() + "']", resourcesElement)) {
      if (resource.equals(new Resource(candidate))) {
        resourcesElement.removeChild(candidate);
        descriptionOfChange =
            highlight(REMOVED + " resource") + " " + resource.getSimpleDescription();
        // Stay in the loop just in case it was in the POM more than
        // once
      }
    }

    final List<Element> resourceElements = XmlUtils.findElements("resource", resourcesElement);
    if (resourceElements.isEmpty()) {
      resourcesElement.getParentNode().removeChild(resourcesElement);
    }

    DomUtils.removeTextNodes(root);

    fileManager.createOrUpdateTextFileIfRequired(pom.getPath(), XmlUtils.nodeToString(document),
        descriptionOfChange, false);
  }

  public void setModule(final Pom module) {
    // Update window title with project name
    shell.flash(Level.FINE, "Spring Roo: " + getTopLevelPackage(module.getModuleName()),
        Shell.WINDOW_TITLE_SLOT);
    pomManagementService.setFocusedModule(module);
  }


  public void updateBuildPlugin(final String moduleName, final Plugin plugin) {
    final Pom pom = getPomFromModuleName(moduleName);
    Validate.notNull(pom, "The pom is not available, so plugins cannot be modified at this time");
    Validate.notNull(plugin, "Plugin required");
    for (final Plugin existingPlugin : pom.getBuildPlugins()) {
      if (existingPlugin.equals(plugin)) {
        // Already exists, so just quit
        return;
      }
    }

    // Delete any existing plugin with a different version
    removeBuildPlugin(moduleName, plugin);

    // Add the plugin
    addBuildPlugin(moduleName, plugin);
  }

  public void updateDependencyScope(final String moduleName, final Dependency dependency,
      final DependencyScope dependencyScope) {
    Validate.isTrue(isProjectAvailable(moduleName),
        "Dependency modification prohibited at this time");
    Validate.notNull(dependency, "Dependency to update required");
    final Pom pom = getPomFromModuleName(moduleName);
    Validate.notNull(pom, "The pom is not available, so updating a dependency cannot be performed");
    if (!pom.isDependencyRegistered(dependency, false)) {
      return;
    }

    final Document document = XmlUtils.readXml(fileManager.getInputStream(pom.getPath()));
    final Element root = document.getDocumentElement();
    final Element dependencyElement =
        XmlUtils.findFirstElement(
            "/project/dependencies/dependency[groupId = '" + dependency.getGroupId()
                + "' and artifactId = '" + dependency.getArtifactId() + "' and version = '"
                + dependency.getVersion() + "']", root);
    if (dependencyElement == null) {
      return;
    }

    final Element scopeElement = XmlUtils.findFirstElement("scope", dependencyElement);
    final String descriptionOfChange;
    if (scopeElement == null) {
      if (dependencyScope != null) {
        dependencyElement.appendChild(new XmlElementBuilder("scope", document).setText(
            dependencyScope.name().toLowerCase()).build());
        descriptionOfChange =
            highlight(ADDED + " scope") + " " + dependencyScope.name().toLowerCase()
                + " to dependency " + dependency.getSimpleDescription();
      } else {
        descriptionOfChange = null;
      }
    } else {
      if (dependencyScope != null) {
        scopeElement.setTextContent(dependencyScope.name().toLowerCase());
        descriptionOfChange =
            highlight(CHANGED + " scope") + " to " + dependencyScope.name().toLowerCase()
                + " in dependency " + dependency.getSimpleDescription();
      } else {
        dependencyElement.removeChild(scopeElement);
        descriptionOfChange =
            highlight(REMOVED + " scope") + " from dependency " + dependency.getSimpleDescription();
      }
    }

    if (descriptionOfChange != null) {
      fileManager.createOrUpdateTextFileIfRequired(pom.getPath(), XmlUtils.nodeToString(document),
          descriptionOfChange, false);
    }
  }

  public void updateProjectType(final String moduleName, final ProjectType projectType) {
    Validate.notNull(projectType, "Project type required");
    final Pom pom = getPomFromModuleName(moduleName);
    Validate.notNull(pom, "The pom is not available, so the project type cannot be changed");

    final Document document = XmlUtils.readXml(fileManager.getInputStream(pom.getPath()));
    final Element packaging =
        DomUtils.createChildIfNotExists("packaging", document.getDocumentElement(), document);
    if (packaging.getTextContent().equals(projectType.getType())) {
      return;
    }

    packaging.setTextContent(projectType.getType());
    final String descriptionOfChange =
        highlight(UPDATED + " project type") + " to " + projectType.getType();

    fileManager.createOrUpdateTextFileIfRequired(pom.getPath(), XmlUtils.nodeToString(document),
        descriptionOfChange, false);
  }

  /**
   * Write the dependencies provided in specified pom, as children of the provided
   * Element (usually 'dependencies' or 'dependencyManagement').
   *
   * @param newDependencies the collection of Dependency to be added.
   * @param parentPom the parent module which the dependencies should be added.
   * @param pom the module which the dependencies should be added.
   * @param parentDocument the parent Document to act upon.
   * @param document the Document to act upon.
   * @param parentElement the element where append the collection of dependencies as 
   *            children.
   * @param element the element where append the collection of dependencies as 
   *            children.
   * @param addToDependencyManagement boolean that indicates if is necessary to include 
   * the dependency in dependencyManagement section 
   * @param isSamePom boolean that indicates if parentPom and pom is the same
   * @param checkFullDependency whether should check the existence with full 
   *            dependency element or only compare 'artifactId' and 'groupId'. 
   * @return the list of added dependencies.
   */
  private List<Dependency> writeDependencyInPom(
      final Collection<? extends Dependency> newDependencies, final Pom parentPom, final Pom pom,
      final Document parentDocument, final Document document, final Element parentElement,
      final Element element, final boolean addToDependencyManagement, final boolean isSamePom,
      final boolean checkFullDependency) {

    final List<Dependency> finalDependencies = new ArrayList<Dependency>();
    final List<String> addedDependencies = new ArrayList<String>();
    final List<String> removedDependencies = new ArrayList<String>();
    final List<String> skippedDependencies = new ArrayList<String>();

    if (addToDependencyManagement) {

      // Include dependency in dependencyManagement element if needed
      final Element dependencyManagementElement =
          DomUtils.createChildIfNotExists("dependencyManagement/dependencies", parentElement,
              parentDocument);
      final List<Element> existingDependencyManagementElements =
          XmlUtils.findElements("dependency", dependencyManagementElement);

      for (final Dependency newDependency : newDependencies) {

        // First, check if this dependency has version. If not, is not possible
        // to include it on dependencyManagement element
        if (StringUtils.isEmpty(newDependency.getVersion())) {
          continue;
        }

        // ROO-3465: Prevent version changes adding checkVersion to false
        // when check if is possible to add the new dependency
        if (!parentPom.canAddDependencyToDependencyManagement(newDependency, checkFullDependency)) {
          continue;
        }

        boolean inserted = false;
        // Look for any existing instances of this dependency
        for (final Element existingDependencyElement : existingDependencyManagementElements) {
          final Dependency existingDependency = new Dependency(existingDependencyElement);
          if (existingDependency.hasSameCoordinates(newDependency)) {
            inserted = true;
            break;
          }
        }

        if (!inserted) {
          // We didn't encounter any existing dependencies with the
          // same coordinates; add it now

          Element newDependencyElement =
              removeVersionIfBlank(newDependency.getElement(parentDocument));
          dependencyManagementElement.appendChild(newDependencyElement);
          finalDependencies.add(newDependency);
          addedDependencies.add(newDependency.getSimpleDescription());
        }
      }
    }

    // Include dependency to pom file
    final Element dependenciesElement =
        DomUtils.createChildIfNotExists("dependencies", element, document);
    final List<Element> existingDependencyElements =
        XmlUtils.findElements("dependency", dependenciesElement);

    for (final Dependency newDependency : newDependencies) {
      // ROO-3465: Prevent version changes adding checkVersion to false
      // when check if is possible to add the new dependency
      if (pom.canAddDependency(newDependency, checkFullDependency)) {
        // Look for any existing instances of this dependency
        boolean inserted = false;
        for (final Element existingDependencyElement : existingDependencyElements) {
          final Dependency existingDependency = new Dependency(existingDependencyElement);
          if (existingDependency.hasSameCoordinates(newDependency)) {
            // It's the same artifact, but might have a different
            // version, exclusions, etc.
            if (!inserted) {
              // We haven't added the new one yet; do so now
              // ROO-3685: Check if current dependency has version when is added again
              Element newDependencyElement = null;
              if (addToDependencyManagement && !StringUtils.isEmpty(newDependency.getVersion())) {
                // If this dependency has been added to dependencyManagement, is not necessary
                // to include version, scope or exclusions
                newDependencyElement =
                    removeManagementConfiguration(newDependency.getElement(document));
              } else {
                // If this dependency has not been added to dependencyManagement, is necessary
                // to include version, only if it's not blank or null.
                newDependencyElement = removeVersionIfBlank(newDependency.getElement(document));
              }
              dependenciesElement.insertBefore(newDependencyElement, existingDependencyElement);
              inserted = true;
              Dependency newDependencyWithoutVersion = new Dependency(newDependencyElement);
              if (!newDependencyWithoutVersion.getVersion().equals(existingDependency.getVersion())) {
                // It's a genuine version change => mention the
                // old and new versions in the message
                finalDependencies.add(newDependency);
                addedDependencies.add(newDependency.getSimpleDescription());
                removedDependencies.add(existingDependency.getSimpleDescription());
              }
            }
            // Either way, we remove the previous one in case it was
            // different in any way
            dependenciesElement.removeChild(existingDependencyElement);
          }
          // Keep looping in case it's present more than once
        }
        if (!inserted) {
          // We didn't encounter any existing dependencies with the
          // same coordinates; add it now

          // ROO-3660: Check if current dependency has version. If
          // not, remove version attribute
          Element newDependencyElement = null;
          if (addToDependencyManagement && !StringUtils.isEmpty(newDependency.getVersion())) {
            // If this dependency has been added to dependencyManagement, is not necessary
            // to include version, scope or exclusions
            newDependencyElement =
                removeManagementConfiguration(newDependency.getElement(document));
          } else {
            // If this dependency has not been added to dependencyManagement, is necessary
            // to include version, only if it's not blank or null.
            newDependencyElement = removeVersionIfBlank(newDependency.getElement(document));
          }
          dependenciesElement.appendChild(newDependencyElement);
          finalDependencies.add(newDependency);
          addedDependencies.add(newDependency.getSimpleDescription());
        }
      } else {
        skippedDependencies.add(newDependency.getSimpleDescription());
        finalDependencies.add(newDependency);
      }

    }
    if (!newDependencies.isEmpty() || !skippedDependencies.isEmpty()) {
      final String message =
          getPomDependenciesUpdateMessage(addedDependencies, removedDependencies,
              skippedDependencies);
      fileManager.createOrUpdateTextFileIfRequired(pom.getPath(), XmlUtils.nodeToString(document),
          message, false);
      // If parent pom is different, is necessary to add dependencies to dependencyManagement
      if (!isSamePom && addToDependencyManagement) {
        fileManager.createOrUpdateTextFileIfRequired(parentPom.getPath(),
            XmlUtils.nodeToString(parentDocument), message, false);
      }
    }

    return finalDependencies;
  }


  /**
   * Write the provided plugins in specified pom, as children of the provided 
   * Element (usually 'dependencies' or 'dependencyManagement').
   * 
   * @param newPlugins the collection of Plugin to be added.
   * @param parentPom the parent pom which the plugins should be added to pluginManagement.
   * @param pom the pom which the plugins should be added.
   * @param parentDocument the parent Document to act upon.
   * @param document the Document to act upon.
   * @param parentElement the element where append the collection of plugins on parentPom.
   * @param element the element where append the collection of plugins as  children.
   * @param addToPluginManagement boolean that indicates if plugin should be added to pluginManagement
   * section. 
   * @param isSamePom boolean that indicates if parentPom and pom is the same 
   */
  private void writePluginInPom(final Collection<? extends Plugin> newPlugins, final Pom parentPom,
      final Pom pom, final Document parentDocument, final Document document,
      final Element parentElement, final Element element, boolean addToPluginManagement,
      boolean isSamePom) {

    final List<String> addedPlugins = new ArrayList<String>();
    final List<String> removedPlugins = new ArrayList<String>();

    if (addToPluginManagement) {

      // Include dependency in pluginManagement element if needed
      final Element pluginManagementElement =
          DomUtils.createChildIfNotExists("build/pluginManagement/plugins", parentElement,
              parentDocument);
      final List<Element> existingPluginManagementElements =
          XmlUtils.findElements("plugin", pluginManagementElement);

      for (final Plugin newPlugin : newPlugins) {

        // First, check if this plugin has version. If not, is not possible
        // to include it on pluginManagement element
        if (StringUtils.isEmpty(newPlugin.getVersion())) {
          continue;
        }

        // ROO-3465: Prevent version changes adding checkVersion to false
        // when check if is possible to add the new plugin
        if (!parentPom.canAddPluginToPluginManagement(newPlugin, false)) {
          continue;
        }

        boolean inserted = false;
        // Look for any existing instances of this plugin
        for (final Element existingPluginElement : existingPluginManagementElements) {
          final Plugin existingPlugin = new Plugin(existingPluginElement);
          if (existingPlugin.hasSameCoordinates(newPlugin)) {
            inserted = true;
            break;
          }
        }

        if (!inserted) {
          // We didn't encounter any existing plugins with the
          // same coordinates; add it now

          Element newPluginElement = removeVersionIfBlank(newPlugin.getElement(parentDocument));
          pluginManagementElement.appendChild(newPluginElement);
          addedPlugins.add(newPlugin.getSimpleDescription());
        }
      }
    }

    // Include plugin to pom file
    final Element root = document.getDocumentElement();
    final Element pluginsElement = DomUtils.createChildIfNotExists("build/plugins", root, document);
    final List<Element> existingPluginElements = XmlUtils.findElements("plugin", pluginsElement);
    for (final Plugin newPlugin : newPlugins) {
      if (newPlugin != null) {

        // Look for any existing instances of this plugin
        boolean inserted = false;
        for (final Element existingPluginElement : existingPluginElements) {
          final Plugin existingPlugin = new Plugin(existingPluginElement);
          if (existingPlugin.hasSameCoordinates(newPlugin)) {
            // It's the same artifact, but might have a different
            // version, exclusions, etc.
            if (!inserted) {
              Element newPluginElement = null;
              if (addToPluginManagement && !StringUtils.isEmpty(newPlugin.getVersion())) {
                // If this dependency has been added to pluginManagement, is not necessary
                // to include version
                newPluginElement = removeAllItems(newPlugin.getElement(document));
              } else {
                // If this dependency has not been added to pluginManagement, is necessary
                // to include version, only if it's not blank or null.
                newPluginElement = removeVersionIfBlank(newPlugin.getElement(document));
              }
              // We haven't added the new one yet; do so now
              pluginsElement.insertBefore(newPluginElement, existingPluginElement);
              inserted = true;
              if (!newPlugin.getVersion().equals(existingPlugin.getVersion())) {
                // It's a genuine version change => mention the
                // old and new versions in the message
                addedPlugins.add(newPlugin.getSimpleDescription());
                removedPlugins.add(existingPlugin.getSimpleDescription());
              }
            }
            // Either way, we remove the previous one in case it was
            // different in any way
            pluginsElement.removeChild(existingPluginElement);
          }
          // Keep looping in case it's present more than once
        }
        if (!inserted) {
          // We didn't encounter any existing plugins with the
          // same coordinates; add it now
          Element newPluginElement = null;
          if (addToPluginManagement && !StringUtils.isEmpty(newPlugin.getVersion())) {
            // If this dependency has been added to pluginManagement, is not necessary
            // to include version
            newPluginElement = removeAllItems(newPlugin.getElement(document));
          } else {
            // If this dependency has not been added to pluginManagement, is necessary
            // to include version, only if it's not blank or null.
            newPluginElement = removeVersionIfBlank(newPlugin.getElement(document));
          }
          pluginsElement.appendChild(newPluginElement);
          addedPlugins.add(newPlugin.getSimpleDescription());
        }
      }
    }

    if (!newPlugins.isEmpty()) {
      final String message = getPomPluginsUpdateMessage(addedPlugins, removedPlugins);
      fileManager.createOrUpdateTextFileIfRequired(pom.getPath(), XmlUtils.nodeToString(document),
          message, false);
      // If parent pom is different, is necessary to add plugins to pluginManagement
      if (!isSamePom && addToPluginManagement) {
        fileManager.createOrUpdateTextFileIfRequired(parentPom.getPath(),
            XmlUtils.nodeToString(parentDocument), message, false);
      }
    }
  }
}
