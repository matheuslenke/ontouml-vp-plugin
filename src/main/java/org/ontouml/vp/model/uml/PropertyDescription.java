package org.ontouml.vp.model.uml;

import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IMultiplicity;
import com.vp.plugin.model.ITaggedValueContainer;

public class PropertyDescription {

  private final IAssociationEnd associationEnd;
  final boolean isDerived; // must invert
  final boolean isReadOnly; // must invert
  final String aggregationKind; // must invert
  final String multiplicity; // must invert
  final String name; // must invert
  final IMultiplicity multiplicityDetail; // must invert
  final ITaggedValueContainer taggedValues; // I don't know
  final IModelElement[] redefinedProperties; // must invert
  final IModelElement[] subsettedProperties; // must invert

  public PropertyDescription(IAssociationEnd associationEnd) {
    this.associationEnd = associationEnd;

    aggregationKind = associationEnd.getAggregationKind();
    multiplicityDetail = associationEnd.getMultiplicityDetail();
    multiplicity = associationEnd.getMultiplicity();
    name = associationEnd.getName();
    taggedValues = associationEnd.getTaggedValues();
    isDerived = associationEnd.isDerived();
    isReadOnly = associationEnd.isReadOnly();
    redefinedProperties = associationEnd.toRedefinedPropertyArray();
    subsettedProperties = associationEnd.toSubsettedPropertyArray();
  }

  public void copyTo(IAssociationEnd associationEnd) {
    associationEnd.setAggregationKind(aggregationKind);
    associationEnd.setName(name);
    associationEnd.setMultiplicity(multiplicity);

    ModelElement.setDerived(associationEnd, isDerived);

    if (multiplicityDetail != null) {
      ModelElement.setOrdered(associationEnd, multiplicityDetail.isOrdered());
    }

    if (taggedValues != null) {
      associationEnd.setTaggedValues((ITaggedValueContainer) taggedValues.duplicate());
    }

    Property.removeRedefinedProperties(associationEnd);
    Property.removeSubsettedProperties(associationEnd);
    Property.addRedefinedProperties(associationEnd, redefinedProperties);
    Property.addSubsettedProperties(associationEnd, subsettedProperties);
  }

  public void partialCopyTo(IAssociationEnd associationEnd) {
    associationEnd.setName(name);

    ModelElement.setDerived(associationEnd, isDerived);

    Property.removeRedefinedProperties(associationEnd);
    Property.removeSubsettedProperties(associationEnd);
    Property.addRedefinedProperties(associationEnd, redefinedProperties);
    Property.addSubsettedProperties(associationEnd, subsettedProperties);
  }
}
