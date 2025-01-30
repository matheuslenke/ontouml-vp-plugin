package org.ontouml.vp.model.ontouml2vp;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import org.ontouml.ontouml4j.model.utils.AggregationKind;
import org.ontouml.ontouml4j.model.Class;
import org.ontouml.ontouml4j.model.Classifier;
import org.ontouml.ontouml4j.model.Property;
import java.util.Optional;

public class IAttributeLoader {

  static IProject vpProject = ApplicationManager.instance().getProjectManager().getProject();

  public static void importAttributes(Class fromClass) {
    IClass toClass = LoaderUtils.getToClass(fromClass);

    if (toClass == null) return;

    fromClass.getAttributes().forEach(a -> importAttribute(toClass, a));
  }

  private static void importAttribute(IClass toClass, Property fromAttribute) {
    LoaderUtils.logElementCreation(fromAttribute);

    IAttribute toAttribute = getOrCreateAttribute(toClass, fromAttribute);
    fromAttribute.setId(toAttribute.getId());

    IMultiplicity detail = toAttribute.getMultiplicityDetail();
    if (detail == null) {
      detail = IModelElementFactory.instance().createMultiplicity();
      toAttribute.setMultiplicityDetail(detail);
    }

    LoaderUtils.loadName(fromAttribute, toAttribute);

    boolean isDerived = fromAttribute.isDerived();
    toAttribute.setDerived(isDerived);

    boolean isOrdered = fromAttribute.isOrdered();
    toAttribute.getMultiplicityDetail().setOrdered(isOrdered);

    boolean isReadOnly = fromAttribute.isReadOnly();
    toAttribute.setReadOnly(isReadOnly);

    fromAttribute
        .getAggregationKind()
        .ifPresent(
            agg -> {
              int aggregationKind = getAggregationKind(agg);
              toAttribute.setAggregation(aggregationKind);
            });

    fromAttribute.getCardinalityValue().ifPresent(value -> toAttribute.setMultiplicity(value));
    getPropertyType(fromAttribute).ifPresent(pType -> toAttribute.setType(pType));

    ITaggedValueLoader.loadTaggedValues(fromAttribute, toAttribute);
  }

  private static Optional<? extends IModelElement> getPropertyType(Property fromAttribute) {
    Optional<Classifier<?, ?>> fromPropertyTypeOp = fromAttribute.getPropertyType();

    if (fromPropertyTypeOp.isPresent() && fromPropertyTypeOp.get() instanceof Class) {
      Class fromPropertyType = (Class) fromPropertyTypeOp.get();
      String id = fromPropertyType.getId();
      return Optional.ofNullable(vpProject.getModelElementById(id));
    }

    return Optional.empty();
  }

  private static int getAggregationKind(AggregationKind agg) {
    switch (agg.getName()) {
      case "SHARED":
        return 1;
      case "COMPOSITE":
        return 2;
      case "NONE":
      default:
        return 0;
    }
  }

  private static IAttribute getOrCreateAttribute(IClass toClass, Property fromAttribute) {
    IAttribute toAttribute = toClass.getAttributeByName(fromAttribute.getFirstName().orElse(""));

    if (toAttribute != null) {
      System.out.println("Attribute " + fromAttribute.getId() + " exists! Let's update it!");
    } else {
      System.out.println("Attribute " + fromAttribute.getId() + " not found! Let's create it");
      toAttribute = toClass.createAttribute();
    }

    return toAttribute;
  }
}
