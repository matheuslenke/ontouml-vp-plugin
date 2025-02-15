package org.ontouml.vp.model.vp2ontouml;

import static org.ontouml.ontouml4j.deserialization.ReferenceResolver.resolveReferences;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vp.plugin.diagram.shape.INoteUIModel;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.ontouml.ontouml4j.model.*;
import org.ontouml.ontouml4j.model.Package;

public class IProjectTransformer {

  public static Project transform(IProject sourceProject) {
    Project targetProject = new Project();
    Trace.getInstance().put(sourceProject.getId(), sourceProject, targetProject);

    String name = sourceProject.getName();
    targetProject.addName(name);

    String id = sourceProject.getId();
    targetProject.setId(id);

    // Missing: description, alternativeNames, creators

    // TODO
    //    Package root = targetProject.createModel(id + "_root", name);
    //
    List<ModelElement> targetElements =
        getElementStream(sourceProject)
            .map((IModelElement source) -> transformModelElement(source, targetProject))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    // Setting the root package to the first package in the list.
    Optional<Package> root = targetProject.getPackages().values().stream().findFirst();
    root.ifPresent(targetProject::setRoot);

    // This part of the code is responsible for getting the referenced datatypes in the project
    List<ModelElement> targetDatatypes =
        getUsedDatatypes(sourceProject).stream()
            .map((IModelElement source) -> transformModelElement(source, targetProject))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    targetElements.addAll(targetDatatypes);

//    targetElements.forEach(element -> resolveContainer(element, root));
    resolveReferences(targetProject);
    //
    //    List<Diagram> diagrams = transformDiagrams(sourceProject, root);
    //    targetProject.setDiagrams(diagrams);
    try {
      System.out.println(targetProject.serializeAsString());
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return targetProject;
  }

  //  private static List<Diagram> transformDiagrams(IProject source, Package root) {
  //    return Stream.of(source.toDiagramArray())
  //        .filter(IClassDiagramUIModel.class::isInstance)
  //        .map(diag -> IClassDiagramTransformer.transform(diag, root))
  //        .filter(Objects::nonNull)
  //        .collect(Collectors.toList());
  //  }

  private static Stream<IModelElement> getElementStream(IProject project) {
    final String[] elementTypes = {
      IModelElementFactory.MODEL_TYPE_PACKAGE,
      IModelElementFactory.MODEL_TYPE_MODEL,
      IModelElementFactory.MODEL_TYPE_CLASS,
      IModelElementFactory.MODEL_TYPE_GENERALIZATION,
      IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET,
      IModelElementFactory.MODEL_TYPE_ASSOCIATION,
      IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS,
      IModelElementFactory.MODEL_TYPE_NOTE,
      IModelElementFactory.MODEL_TYPE_ANCHOR,
    };

    IModelElement[] sourceContents = project.toAllLevelModelElementArray(elementTypes);

    // Relationships may connect other types of model elements and these need to be filtered out
    // the code also filters out relationships connected to null
    return Stream.of(sourceContents)
        .filter(
            element -> {
              if (!(element instanceof IRelationship)) {
                return true;
              }

              var source = ((IRelationship) element).getFrom();
              var target = ((IRelationship) element).getTo();
              var sourceType = source != null ? source.getModelType() : null;
              var targetType = target != null ? target.getModelType() : null;
              var desiredTypes =
                  Arrays.asList(
                      IModelElementFactory.MODEL_TYPE_ASSOCIATION,
                      IModelElementFactory.MODEL_TYPE_CLASS);

              return desiredTypes.contains(sourceType) && desiredTypes.contains(targetType);
            });
  }

  private static void resolveContainer(PackageableElement targetElement, Package root) {
    Package targetContainer = getContainer(targetElement, root);
    targetContainer.addContent(targetElement);
  }

  private static Package getContainer(ModelElement targetElement, Package root) {
    Object sourceElement = Trace.getInstance().getSource(targetElement.getId());
    IModelElement sourceParent = getSourceParent(sourceElement);

    if (sourceParent == null) {
      return root;
    }

    OntoumlElement targetParent = Trace.getInstance().getTarget(sourceParent);
    return (targetParent instanceof Package) ? (Package) targetParent : root;
  }

  private static IModelElement getSourceParent(Object element) {
    // VP puts an association, associationclass and generalization inside the package of its source
    if (element instanceof IRelationship) {
      IModelElement from = ((IRelationship) element).getFrom();
      return from != null ? from.getParent() : null;
    }

    if (element instanceof IModelElement) {
      return ((IModelElement) element).getParent();
    }

    return null;
  }

  public static ModelElement transformModelElement(IModelElement source, Project project) {
    ModelElement target = null;

    if (source instanceof IClass || source instanceof IDataType) {
      target = IClassTransformer.transform(source, project);
    } else if (source instanceof IAssociation) {
      target = IAssociationTransformer.transform(source, project);
    } else if (source instanceof IAssociationClass) {
      target = IAssociationClassTransformer.transform(source, project);
    } else if (source instanceof IPackage || source instanceof IModel) {
      target = IPackageTransformer.transform(source, project);
    } else if (source instanceof IAttribute || source instanceof IAssociationEnd) {
      target = IPropertyTransformer.transform(source, project);
    } else if (source instanceof IGeneralization) {
      target = IGeneralizationTransformer.transform(source, project);
    } else if (source instanceof IGeneralizationSet) {
      target = IGeneralizationSetTransformer.transform(source, project);
    } else if (source instanceof INOTE) {
      target = INoteTransformer.transform(source, project);
    }

    if (target != null) {
      target.setProjectContainer(project);
    }
    Trace.getInstance().put(source.getId(), source, target);

    return target;
  }

  public static List<IAttribute> getAllAttributes(IProject project) {
    IModelElement[] classes =
        project.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_CLASS);

    return Stream.of(classes)
        .map(IClass.class::cast)
        .flatMap(clazz -> Stream.of(clazz.toAttributeArray()))
        .collect(Collectors.toList());
  }

  public static Set<IDataType> getUsedDatatypes(IProject project) {
    return getAllAttributes(project).stream()
        .map(attr -> attr.getType())
        .filter(IDataType.class::isInstance)
        .map(IDataType.class::cast)
        .collect(Collectors.toSet());
  }
}
