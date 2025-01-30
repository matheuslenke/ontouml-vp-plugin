package org.ontouml.vp.model.ontouml2vp;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;

import org.ontouml.ontouml4j.model.BinaryRelation;
import org.ontouml.ontouml4j.model.Classifier;
import org.ontouml.ontouml4j.model.Property;
import org.ontouml.ontouml4j.model.Relation;
import org.ontouml.vp.utils.StereotypesManager;

public class IAssociationLoader {

  static IProject vpProject = ApplicationManager.instance().getProjectManager().getProject();

  public static IAssociation importElement(BinaryRelation fromRelation) {
    LoaderUtils.logElementCreation(fromRelation);

    IAssociation toRelation = getOrCreateAssociation(fromRelation);
    fromRelation.setId(toRelation.getId());

    LoaderUtils.loadName(fromRelation, toRelation);

    loadSource(fromRelation, toRelation);
    loadTarget(fromRelation, toRelation);

    boolean isDerived = fromRelation.isDerived();
    toRelation.setDerived(isDerived);

    boolean isAbstract = fromRelation.isAbstract();
    toRelation.setAbstract(isAbstract);

    fromRelation
        .getStereotype()
        .ifPresent(stereotype -> StereotypesManager.applyStereotype(toRelation, stereotype));

    loadEndProperties(fromRelation.getSourceEnd(), (IAssociationEnd) toRelation.getFromEnd());
    loadEndProperties(fromRelation.getTargetEnd(), (IAssociationEnd) toRelation.getToEnd());

    ITaggedValueLoader.loadTaggedValues(fromRelation, toRelation);

    return toRelation;
  }

  private static void loadEndProperties(Property fromProperty, IAssociationEnd toProperty) {
    LoaderUtils.loadName(fromProperty, toProperty);

    IMultiplicity detail = toProperty.getMultiplicityDetail();
    if (detail == null) {
      detail = IModelElementFactory.instance().createMultiplicity();
      toProperty.setMultiplicityDetail(detail);
    }

    boolean isDerived = fromProperty.isDerived();
    toProperty.setDerived(isDerived);

    boolean isReadOnly = fromProperty.isReadOnly();
    toProperty.setReadOnly(isReadOnly);

    boolean isOrdered = fromProperty.isOrdered();
    toProperty.getMultiplicityDetail().setOrdered(isOrdered);

    fromProperty
        .getAggregationKind()
        .map(agg -> agg.getName().toLowerCase())
        .ifPresent(agg -> toProperty.setAggregationKind(agg));

    fromProperty.getCardinalityValue().ifPresent(value -> toProperty.setMultiplicity(value));
    ITaggedValueLoader.loadTaggedValues(fromProperty, toProperty);
  }

  private static void loadSource(BinaryRelation fromRelation, IAssociation toRelation) {
    Classifier<?, ?> fromSource = fromRelation.getSource();
    IModelElement toSource = vpProject.getModelElementById(fromSource.getId());

    if (toSource != null) toRelation.setFrom(toSource);
  }

  private static void loadTarget(BinaryRelation fromRelation, IAssociation toRelation) {
    Classifier<?, ?> fromTarget = fromRelation.getTarget();
    IModelElement toTarget = vpProject.getModelElementById(fromTarget.getId());

    if (toTarget != null) toRelation.setTo(toTarget);
  }

  private static IAssociation getOrCreateAssociation(Relation fromRelation) {
    IModelElement toRelation = vpProject.getModelElementById(fromRelation.getId());

    if (toRelation instanceof IAssociation) {
      System.out.println("Relation " + fromRelation.getId() + " exists! Let's update it!");
    } else {
      System.out.println("Relation " + fromRelation.getId() + " not found! Let's create it");
      toRelation = IModelElementFactory.instance().createAssociation();
    }

    return (IAssociation) toRelation;
  }
}
