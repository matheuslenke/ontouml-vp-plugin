package org.ontouml.vp.model.uml;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IBaseDiagramElement;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import org.ontouml.vp.utils.StereotypesManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Victor Viola
 * @author Claudenir Fonseca
 */
public interface ModelElement {

  String TYPE_PACKAGE = "Package";
  String TYPE_CLASS = "Class";
  String TYPE_RELATION = "Relation";
  String TYPE_ASSOCIATION_CLASS = "Relation";
  String TYPE_GENERALIZATION = "Generalization";
  String TYPE_GENERALIZATION_SET = "GeneralizationSet";
  String TYPE_PROPERTY = "Property";
  String TYPE_LITERAL = "Literal";

  static String getUniqueStereotypeName(IModelElement element) {
    return element.stereotypeCount() == 1 ? element.toStereotypeModelArray()[0].getName() : null;
  }

  static IStereotype getUniqueStereotype(IModelElement element) {
    return element.stereotypeCount() == 1 ? element.toStereotypeModelArray()[0] : null;
  }

  static boolean hasUniqueStereotype(IModelElement element) {
    return getUniqueStereotype(element) != null;
  }

  static Set<IDiagramElement> getViews(IModelElement element) {
    return Optional.ofNullable(element.getDiagramElements())
        .map(Set::of)
        .orElse(Collections.emptySet());
  }

  static Set<IDiagramUIModel> getDiagrams(IModelElement element) {
    return getViews(element).stream()
        .map(IBaseDiagramElement::getDiagramUIModel)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  static boolean isPresentInOntoumlDiagram(IModelElement element) {
    return ModelElement.getDiagrams(element).stream().anyMatch(Diagram::isOntoUMLDiagram);
  }

  /** @return <code>IModelElement</code> on which the object is based. */
  IModelElement getSourceModelElement();

  /** @return object's type in OntoUML Schema. */
  String getOntoUMLType();

  /** @return object's ID (based on a <code>IModelElement</code>). */
  String getId();

  /**
   * Returns Visual Paradigm's link to the related model element. This method removes the project's
   * name which originally start the link returned.
   *
   * @param modelElement
   * @return a link identify a <code>IModelElement</code> in Visual Paradigm following the pattern
   *     <code>"vpp://modelelement/Cd.WKPaAUB22rwx4"</code>.
   */
  static String getModelElementURI(IModelElement modelElement) {
    if (modelElement == null) return null;

    final String link =
        ApplicationManager.instance().getProjectManager().getLink(modelElement, false);

    return link.substring(link.indexOf(".vpp:") + 1);
  }

  /**
   * Returns serialized JSON string of the whole project in OntoUML Schema.
   *
   * @param pretty - <code>true</code> if return string should be indented.
   * @return serialized version JSON of whole project in OntoUML Schema.
   */
  static String generateModel(boolean pretty) {
    final Model model = new Model();

    if (pretty) {
      return new GsonBuilder()
          .excludeFieldsWithoutExposeAnnotation()
          .serializeNulls()
          .setPrettyPrinting()
          .create()
          .toJson(model);
    } else {
      return new GsonBuilder()
          .excludeFieldsWithoutExposeAnnotation()
          .serializeNulls()
          .create()
          .toJson(model);
    }
  }

  static String generateModel(HashSet<String> elements, boolean pretty) {

    final Model model = new Model(elements);
    if (pretty) {
      return new GsonBuilder()
          .excludeFieldsWithoutExposeAnnotation()
          .serializeNulls()
          .setPrettyPrinting()
          .create()
          .toJson(model);
    } else {
      return new GsonBuilder()
          .excludeFieldsWithoutExposeAnnotation()
          .serializeNulls()
          .create()
          .toJson(model);
    }
  }

  static String safeGetString(String s) {
    if (s != null && s.length() != 0) return s;
    return null;
  }

  static String toOntoUMLSchemaType(IModelElement element) {
    switch (element.getModelType()) {
      case IModelElementFactory.MODEL_TYPE_MODEL:
      case IModelElementFactory.MODEL_TYPE_PACKAGE:
        return TYPE_PACKAGE;
      case IModelElementFactory.MODEL_TYPE_CLASS:
      case IModelElementFactory.MODEL_TYPE_DATA_TYPE:
        return TYPE_CLASS;
      case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
      case IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS:
        return TYPE_RELATION;
      case IModelElementFactory.MODEL_TYPE_GENERALIZATION:
        return TYPE_GENERALIZATION;
      case IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET:
        return TYPE_GENERALIZATION_SET;
      case IModelElementFactory.MODEL_TYPE_ATTRIBUTE:
      case IModelElementFactory.MODEL_TYPE_ASSOCIATION_END:
        return TYPE_PROPERTY;
      case IModelElementFactory.MODEL_TYPE_ENUMERATION_LITERAL:
        return TYPE_LITERAL;
    }

    return null;
  }

  static JsonObject transformPropertyAssignments(IModelElement sourceElement) {
    ITaggedValueContainer lContainer = sourceElement.getTaggedValues();
    if (lContainer == null) return null;

    JsonObject obj = new JsonObject();
    ITaggedValue[] lTaggedValues = lContainer.toTaggedValueArray();
    List<String> ignoredClassValues =
        Arrays.asList(
            StereotypesManager.PROPERTY_RESTRICTED_TO, StereotypesManager.PROPERTY_IS_EXTENSIONAL,
            StereotypesManager.PROPERTY_IS_POWERTYPE, StereotypesManager.PROPERTY_ORDER);

    for (int i = 0; lTaggedValues != null && i < lTaggedValues.length; i++) {
      if (ignoredClassValues.contains(lTaggedValues[i].getName())) {
        continue;
      }

      switch (lTaggedValues[i].getType()) {
        case 1:
          JsonObject referenceTag = new JsonObject();

          if (lTaggedValues[i].getValueAsElement() != null) {
            referenceTag.addProperty(
                "type", ModelElement.toOntoUMLSchemaType(lTaggedValues[i].getValueAsElement()));
            referenceTag.addProperty("id", lTaggedValues[i].getValueAsElement().getId());
          } else {
            referenceTag = null;
          }
          obj.add(lTaggedValues[i].getName(), referenceTag);
          break;
        case 5:
          obj.addProperty(
              lTaggedValues[i].getName(), Integer.parseInt((String) lTaggedValues[i].getValue()));
          break;
        case 6:
          obj.addProperty(
              lTaggedValues[i].getName(), Float.parseFloat((String) lTaggedValues[i].getValue()));
          break;
        case 7:
          obj.addProperty(
              lTaggedValues[i].getName(),
              Boolean.parseBoolean((String) lTaggedValues[i].getValue()));
          break;
        default:
          obj.addProperty(lTaggedValues[i].getName(), lTaggedValues[i].getValueAsString());
      }
    }

    if (obj.size() == 0) return null;

    return obj;
  }

  static boolean isAbstract(IModelElement element) {
    final String elementType = element.getModelType();

    switch (elementType) {
      case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
        return ((IAssociation) element).isAbstract();

      case IModelElementFactory.MODEL_TYPE_CLASS:
        return ((IClass) element).isAbstract();

      default:
        throw new UnsupportedOperationException(
            "This operation is not supported for elements of type " + elementType);
    }
  }

  static void setAbstract(IModelElement element, boolean isAbstract) {
    final String elementType = element.getModelType();

    switch (elementType) {
      case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
        ((IAssociation) element).setAbstract(isAbstract);
        break;

      case IModelElementFactory.MODEL_TYPE_CLASS:
        ((IClass) element).setAbstract(isAbstract);
        break;

      default:
        throw new UnsupportedOperationException(
            "This operation is not supported for elements of type " + elementType);
    }
  }

  static boolean isDerived(IModelElement element) {
    final String elementType = element.getModelType();

    switch (elementType) {
      case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
        return ((IAssociation) element).isDerived();

      case IModelElementFactory.MODEL_TYPE_ASSOCIATION_END:
        return ((IAssociationEnd) element).isDerived();

      case IModelElementFactory.MODEL_TYPE_ATTRIBUTE:
        return ((IAttribute) element).isDerived();

      case IModelElementFactory.MODEL_TYPE_CLASS:
        return element.getName().trim().startsWith("/");

      default:
        throw new UnsupportedOperationException(
            "This operation is not supported for elements of type " + elementType);
    }
  }

  static void setDerived(IModelElement element, boolean isDerived) {
    final String elementType = element.getModelType();

    switch (elementType) {
      case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
        ((IAssociation) element).setDerived(isDerived);
        break;

      case IModelElementFactory.MODEL_TYPE_ASSOCIATION_END:
        ((IAssociationEnd) element).setDerived(isDerived);
        break;

      case IModelElementFactory.MODEL_TYPE_ATTRIBUTE:
        ((IAttribute) element).setDerived(isDerived);
        break;

      case IModelElementFactory.MODEL_TYPE_CLASS:
        {
          final String currentName = element.getName() != null ? element.getName().trim() : "";

          if (isDerived(element)) {
            element.setName(currentName.substring(1));
          } else {
            element.setName("/" + currentName);
          }
          break;
        }

      default:
        throw new UnsupportedOperationException(
            "This operation is not supported for elements of type " + elementType);
    }
  }

  static boolean isOrdered(IModelElement element) {
    final String elementType = element.getModelType();

    switch (elementType) {
      case IModelElementFactory.MODEL_TYPE_ASSOCIATION_END:
        {
          final IAssociationEnd associationEnd = (IAssociationEnd) element;
          final IMultiplicity multiplicity = associationEnd.getMultiplicityDetail();
          return multiplicity != null && multiplicity.isOrdered();
        }

      case IModelElementFactory.MODEL_TYPE_ATTRIBUTE:
        {
          final IAttribute attribute = (IAttribute) element;
          final IMultiplicity multiplicity = attribute.getMultiplicityDetail();
          return multiplicity != null && multiplicity.isOrdered();
        }

      default:
        throw new UnsupportedOperationException(
            "This operation is not supported for elements of type " + elementType);
    }
  }

  static void setOrdered(IModelElement element, boolean isOrdered) {
    final String elementType = element.getModelType();
    IMultiplicity multiplicity = null;

    switch (elementType) {
      case IModelElementFactory.MODEL_TYPE_ASSOCIATION_END:
        {
          final IAssociationEnd associationEnd = (IAssociationEnd) element;
          multiplicity = associationEnd.getMultiplicityDetail();

          if (multiplicity == null) {
            multiplicity = IModelElementFactory.instance().createMultiplicity();
            associationEnd.setMultiplicityDetail(multiplicity);
          }
          break;
        }

      case IModelElementFactory.MODEL_TYPE_ATTRIBUTE:
        {
          final IAttribute attribute = (IAttribute) element;
          multiplicity = attribute.getMultiplicityDetail();

          if (multiplicity == null) {
            multiplicity = IModelElementFactory.instance().createMultiplicity();
            attribute.setMultiplicityDetail(multiplicity);
          }
          break;
        }

      default:
        throw new UnsupportedOperationException(
            "This operation is not supported for elements of type " + elementType);
    }

    multiplicity.setOrdered(isOrdered);
  }

  static boolean isReadOnly(IModelElement element) {
    final String elementType = element.getModelType();

    switch (elementType) {
      case IModelElementFactory.MODEL_TYPE_ASSOCIATION_END:
        {
          return ((IAssociationEnd) element).isReadOnly();
        }

      case IModelElementFactory.MODEL_TYPE_ATTRIBUTE:
        {
          return ((IAttribute) element).isReadOnly();
        }

      default:
        throw new UnsupportedOperationException(
            "This operation is not supported for elements of type " + elementType);
    }
  }

  static void setReadOnly(IModelElement element, boolean isReadOnly) {
    final String elementType = element.getModelType();

    switch (elementType) {
      case IModelElementFactory.MODEL_TYPE_ASSOCIATION_END:
        {
          ((IAssociationEnd) element).setReadOnly(isReadOnly);
          break;
        }

      case IModelElementFactory.MODEL_TYPE_ATTRIBUTE:
        {
          ((IAttribute) element).setReadOnly(isReadOnly);
          break;
        }

      default:
        throw new UnsupportedOperationException(
            "This operation is not supported for elements of type " + elementType);
    }
  }

  // TODO: CHECKME!
  static <T extends IModelElement> void forEachSelectedElement(
      T modelElement, Consumer<T> consumer) {
    if (modelElement == null) {
      return;
    }

    final String selectedElementType = modelElement.getModelType();
    final DiagramManager dm = ApplicationManager.instance().getDiagramManager();
    final IDiagramUIModel diagram = dm.getActiveDiagram();
    final IDiagramElement[] selectedDiagramElements =
        diagram != null ? diagram.getSelectedDiagramElement(selectedElementType) : null;

    if (diagram == null || selectedDiagramElements == null) {
      consumer.accept(modelElement);
      return;
    }

    Arrays.stream(selectedDiagramElements)
        .map(IDiagramElement::getModelElement)
        .forEach((Consumer<IModelElement>) consumer);
  }

  static boolean isOntoumlElement(Object object) {
    if (!(object instanceof IModelElement)) return false;

    final IModelElement element = (IModelElement) object;

    switch (element.getModelType()) {
      case IModelElementFactory.MODEL_TYPE_CLASS:
        return Class.isOntoumlClass((IClass) element);
      case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
        return Association.isOntoumlAssociation((IAssociation) element);
      case IModelElementFactory.MODEL_TYPE_ATTRIBUTE:
        return Property.isOntoumlProperty(element);
      default:
        return false;
    }
  }
}
