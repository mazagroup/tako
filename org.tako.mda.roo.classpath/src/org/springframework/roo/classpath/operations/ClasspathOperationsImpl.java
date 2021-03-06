package org.springframework.roo.classpath.operations;

import static org.springframework.roo.model.JavaType.OBJECT;
import static org.springframework.roo.model.RooJavaType.ROO_EQUALS;
import static org.springframework.roo.model.RooJavaType.ROO_JAVA_BEAN;
import static org.springframework.roo.model.RooJavaType.ROO_SERIALIZABLE;
import static org.springframework.roo.model.RooJavaType.ROO_TO_STRING;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.springframework.roo.classpath.PhysicalTypeCategory;
import org.springframework.roo.classpath.PhysicalTypeIdentifier;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.TypeLocationService;
import org.springframework.roo.classpath.TypeManagementService;
import org.springframework.roo.classpath.details.ClassOrInterfaceTypeDetails;
import org.springframework.roo.classpath.details.ClassOrInterfaceTypeDetailsBuilder;
import org.springframework.roo.classpath.details.ConstructorMetadata;
import org.springframework.roo.classpath.details.ConstructorMetadataBuilder;
import org.springframework.roo.classpath.details.FieldMetadata;
import org.springframework.roo.classpath.details.annotations.AnnotatedJavaType;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadataBuilder;
import org.springframework.roo.classpath.itd.InvocableMemberBodyBuilder;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.model.JavaSymbolName;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.model.JdkJavaType;
import org.springframework.roo.model.ReservedWords;
import org.springframework.roo.bnd.workspace.LogicalPath;
import org.springframework.roo.bnd.workspace.Path;
import org.springframework.roo.bnd.workspace.PathResolver;
import org.springframework.roo.bnd.workspace.ProjectOperations;
import org.springframework.roo.shell.converters.StaticFieldConverter;

/**
 * OSGi implementation of {@link ClasspathOperations}.
 *
 * @author Andrew Swan
 * @since 1.2.0
 */
@Component(service=ClasspathOperations.class)
public class ClasspathOperationsImpl implements ClasspathOperations {

  @Reference
  MetadataService metadataService;
  @Reference
  PathResolver pathResolver;
  @Reference
  ProjectOperations projectOperations;
  @Reference
  StaticFieldConverter staticFieldConverter;
  @Reference
  TypeLocationService typeLocationService;
  @Reference
  TypeManagementService typeManagementService;

  @Activate
  protected void activate(final ComponentContext context) {
    staticFieldConverter.add(InheritanceType.class);
  }

  @Deactivate
  protected void deactivate(final ComponentContext context) {
    staticFieldConverter.remove(InheritanceType.class);
  }

  @Override
  public void createClass(final JavaType name, final boolean rooAnnotations,
      final LogicalPath path, final JavaType superclass, final JavaType implementsType,
      final boolean createAbstract, final boolean permitReservedWords) {
    if (!permitReservedWords) {
      ReservedWords.verifyReservedWordsNotPresent(name);
    }

    Validate.isTrue(!JdkJavaType.isPartOfJavaLang(name.getSimpleTypeName()),
        "Class name '%s' is part of java.lang", name.getSimpleTypeName());

    int modifier = Modifier.PUBLIC;
    if (createAbstract) {
      modifier |= Modifier.ABSTRACT;
    }

    final String declaredByMetadataId = PhysicalTypeIdentifier.createIdentifier(name, path);
    final ClassOrInterfaceTypeDetailsBuilder cidBuilder =
        new ClassOrInterfaceTypeDetailsBuilder(declaredByMetadataId, modifier, name,
            PhysicalTypeCategory.CLASS);

    if (!superclass.equals(OBJECT)) {
      final ClassOrInterfaceTypeDetails superclassClassOrInterfaceTypeDetails =
          typeLocationService.getTypeDetails(superclass);
      if (superclassClassOrInterfaceTypeDetails != null) {
        cidBuilder.setSuperclass(new ClassOrInterfaceTypeDetailsBuilder(
            superclassClassOrInterfaceTypeDetails));
      }
    }

    final List<JavaType> extendsTypes = new ArrayList<JavaType>();
    extendsTypes.add(superclass);
    cidBuilder.setExtendsTypes(extendsTypes);

    if (implementsType != null) {
      final Set<JavaType> implementsTypes = new LinkedHashSet<JavaType>();
      final ClassOrInterfaceTypeDetails typeDetails =
          typeLocationService.getTypeDetails(declaredByMetadataId);
      if (typeDetails != null) {
        implementsTypes.addAll(typeDetails.getImplementsTypes());
      }
      implementsTypes.add(implementsType);
      cidBuilder.setImplementsTypes(implementsTypes);
    }

    if (rooAnnotations) {
      final List<AnnotationMetadataBuilder> annotations =
          new ArrayList<AnnotationMetadataBuilder>();
      annotations.add(new AnnotationMetadataBuilder(ROO_JAVA_BEAN));
      annotations.add(new AnnotationMetadataBuilder(ROO_TO_STRING));
      annotations.add(new AnnotationMetadataBuilder(ROO_EQUALS));
      annotations.add(new AnnotationMetadataBuilder(ROO_SERIALIZABLE));
      cidBuilder.setAnnotations(annotations);
    }
    typeManagementService.createOrUpdateTypeOnDisk(cidBuilder.build());
  }

  @Override
  public void createConstructor(final JavaType name, final Set<String> fields) {
    final ClassOrInterfaceTypeDetails javaTypeDetails = typeLocationService.getTypeDetails(name);
    Validate.notNull(javaTypeDetails, "The type specified, '%s', doesn't exist",
        name.getFullyQualifiedTypeName());

    final String declaredByMetadataId =
        PhysicalTypeIdentifier.createIdentifier(name,
            pathResolver.getFocusedPath(Path.SRC_MAIN_JAVA));
    final List<FieldMetadata> constructorFields = new ArrayList<FieldMetadata>();
    final List<? extends FieldMetadata> declaredFields = javaTypeDetails.getDeclaredFields();
    if (fields != null) {
      for (final String field : fields) {
        declared: for (final FieldMetadata declaredField : declaredFields) {
          if (field.equals(declaredField.getFieldName().getSymbolName())) {
            constructorFields.add(declaredField);
            break declared;
          }
        }
      }
      if (constructorFields.isEmpty()) {
        // User supplied a set of fields that do not exist in the
        // class, so return without creating any constructor
        throw new IllegalArgumentException(
            String.format(
                "The set of fields provided for the constructor does not exist in the class '%s'",
                name));
      }
    }

    // Search for an existing constructor
    final List<JavaType> parameterTypes = new ArrayList<JavaType>();
    for (final FieldMetadata fieldMetadata : constructorFields) {
      parameterTypes.add(fieldMetadata.getFieldType());
    }

    final ConstructorMetadata result = javaTypeDetails.getDeclaredConstructor(parameterTypes);
    if (result != null) {

      // Found an existing constructor on this class
      throw new IllegalArgumentException(String.format(
          "The class '%s' already has a constructor method with the same arguments and it cannot "
              + "be created. Use '--force' parameter to overrite it.", name));
    }

    final List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();

    final InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
    bodyBuilder.appendFormalLine("super();");
    for (final FieldMetadata field : constructorFields) {
      final String fieldName = field.getFieldName().getSymbolName();
      bodyBuilder.appendFormalLine("this." + fieldName + " = " + fieldName + ";");
      parameterNames.add(field.getFieldName());
    }

    // Create the constructor
    final ConstructorMetadataBuilder constructorBuilder =
        new ConstructorMetadataBuilder(declaredByMetadataId);
    constructorBuilder.setModifier(Modifier.PUBLIC);
    constructorBuilder.setParameterTypes(AnnotatedJavaType.convertFromJavaTypes(parameterTypes));
    constructorBuilder.setParameterNames(parameterNames);
    constructorBuilder.setBodyBuilder(bodyBuilder);

    final ClassOrInterfaceTypeDetailsBuilder cidBuilder =
        new ClassOrInterfaceTypeDetailsBuilder(javaTypeDetails);
    cidBuilder.addConstructor(constructorBuilder);
    typeManagementService.createOrUpdateTypeOnDisk(cidBuilder.build());
  }

  @Override
  public void createEnum(final JavaType name, final LogicalPath path,
      final boolean permitReservedWords) {
    if (!permitReservedWords) {
      ReservedWords.verifyReservedWordsNotPresent(name);
    }
    final String physicalTypeId = PhysicalTypeIdentifier.createIdentifier(name, path);
    final ClassOrInterfaceTypeDetailsBuilder cidBuilder =
        new ClassOrInterfaceTypeDetailsBuilder(physicalTypeId, Modifier.PUBLIC, name,
            PhysicalTypeCategory.ENUMERATION);
    typeManagementService.createOrUpdateTypeOnDisk(cidBuilder.build());
  }

  @Override
  public void createInterface(final JavaType name, final LogicalPath path,
      final boolean permitReservedWords) {
    if (!permitReservedWords) {
      ReservedWords.verifyReservedWordsNotPresent(name);
    }

    final String declaredByMetadataId = PhysicalTypeIdentifier.createIdentifier(name, path);
    final ClassOrInterfaceTypeDetailsBuilder cidBuilder =
        new ClassOrInterfaceTypeDetailsBuilder(declaredByMetadataId, Modifier.PUBLIC, name,
            PhysicalTypeCategory.INTERFACE);
    typeManagementService.createOrUpdateTypeOnDisk(cidBuilder.build());
  }

  @Override
  public void enumConstant(final JavaType name, final JavaSymbolName fieldName,
      final boolean permitReservedWords) {
    if (!permitReservedWords) {
      // No need to check the "name" as if the class exists it is assumed
      // it is a legal name
      ReservedWords.verifyReservedWordsNotPresent(fieldName);
    }

    final String declaredByMetadataId =
        PhysicalTypeIdentifier.createIdentifier(name,
            pathResolver.getFocusedPath(Path.SRC_MAIN_JAVA));
    typeManagementService.addEnumConstant(declaredByMetadataId, fieldName);
  }

  @Override
  public void focus(final JavaType type) {
    Validate.notNull(type, "Specify the type to focus on");
    final String physicalTypeIdentifier = typeLocationService.getPhysicalTypeIdentifier(type);
    Validate.notNull(physicalTypeIdentifier, "Cannot locate the type %s",
        type.getFullyQualifiedTypeName());
    final PhysicalTypeMetadata ptm =
        (PhysicalTypeMetadata) metadataService.get(physicalTypeIdentifier);
    Validate.notNull(ptm, "Class %s does not exist",
        PhysicalTypeIdentifier.getFriendlyName(physicalTypeIdentifier));
  }

  @Override
  public boolean isProjectAvailable() {
    return projectOperations.isFocusedProjectAvailable();
  }

  /**
   * Replaces a JavaType fullyQualifiedName for a shorter name using '~' for
   * TopLevelPackage
   *
   * @param cid
   *            ClassOrInterfaceTypeDetails of a JavaType
   * @param currentText
   *            String current text for option value
   * @return the String representing a JavaType with its name shortened
   */
  public String replaceTopLevelPackageString(ClassOrInterfaceTypeDetails cid, String currentText) {
    String javaTypeFullyQualilfiedName = cid.getType().getFullyQualifiedTypeName();
    String javaTypeString = "";
    String topLevelPackageString = "";

    // Add module value to topLevelPackage when necessary
    if (StringUtils.isNotBlank(cid.getType().getModule())
        && !cid.getType().getModule().equals(projectOperations.getFocusedModuleName())) {

      // Target module is not focused
      javaTypeString = cid.getType().getModule().concat(LogicalPath.MODULE_PATH_SEPARATOR);
      topLevelPackageString =
          projectOperations.getTopLevelPackage(cid.getType().getModule())
              .getFullyQualifiedPackageName();
    } else if (StringUtils.isNotBlank(cid.getType().getModule())
        && cid.getType().getModule().equals(projectOperations.getFocusedModuleName())
        && (currentText.startsWith(cid.getType().getModule()) || cid.getType().getModule()
            .startsWith(currentText)) && StringUtils.isNotBlank(currentText)) {

      // Target module is focused but user wrote it
      javaTypeString = cid.getType().getModule().concat(LogicalPath.MODULE_PATH_SEPARATOR);
      topLevelPackageString =
          projectOperations.getTopLevelPackage(cid.getType().getModule())
              .getFullyQualifiedPackageName();
    } else {

      // Not multimodule project
      topLevelPackageString =
          projectOperations.getFocusedTopLevelPackage().getFullyQualifiedPackageName();
    }

    // Autocomplete with abbreviate or full qualified mode
    String auxString =
        javaTypeString.concat(StringUtils.replace(javaTypeFullyQualilfiedName,
            topLevelPackageString, "~"));
    if ((StringUtils.isBlank(currentText) || auxString.startsWith(currentText))
        && StringUtils.contains(javaTypeFullyQualilfiedName, topLevelPackageString)) {

      // Value is for autocomplete only or user wrote abbreviate value
      javaTypeString = auxString;
    } else {

      // Value could be for autocomplete or for validation
      javaTypeString = String.format("%s%s", javaTypeString, javaTypeFullyQualilfiedName);
    }

    return javaTypeString;
  }


  /**
   * Replaces a JavaType fullyQualifiedName for a shorter name using '~' for
   * TopLevelPackage
   *
   * @param cid
   *            ClassOrInterfaceTypeDetails of a JavaType
   *
   * @return the String representing a JavaType with its name shortened
   */
  @Override
  public String replaceTopLevelPackage(ClassOrInterfaceTypeDetails cid) {
    String javaTypeFullyQualilfiedName = cid.getType().getFullyQualifiedTypeName();
    String javaTypeString = "";
    String topLevelPackageString = "";

    // Add module value to topLevelPackage when necessary
    if (StringUtils.isNotBlank(cid.getType().getModule())) {

      // Target module is not focused
      javaTypeString = cid.getType().getModule().concat(LogicalPath.MODULE_PATH_SEPARATOR);
      topLevelPackageString =
          projectOperations.getTopLevelPackage(cid.getType().getModule())
              .getFullyQualifiedPackageName();
    } else {

      // Not multimodule project
      topLevelPackageString =
          projectOperations.getFocusedTopLevelPackage().getFullyQualifiedPackageName();
    }

    return javaTypeString.concat(StringUtils.replace(javaTypeFullyQualilfiedName,
        topLevelPackageString, "~"));
  }
}
