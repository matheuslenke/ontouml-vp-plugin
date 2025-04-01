package org.ontouml.vp.model.vp2ontouml;

import java.util.ArrayList;
import java.util.List;

import org.ontouml.ontouml4j.model.NaryRelation;
import org.ontouml.ontouml4j.model.Project;
import org.ontouml.ontouml4j.model.Property;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.INARY;
import com.vp.plugin.model.IRelationshipEnd;

public class INaryTransformer {
  public static NaryRelation transform(IModelElement sourceElement, Project project) {
    if (!(sourceElement instanceof INARY)) {
      return null;
    }

    INARY naryElement = (INARY) sourceElement;
    NaryRelation target = new NaryRelation();

    IModelElementTransformer.transform(sourceElement, target);
    ITaggedValueTransformer.transform(sourceElement, target);
    IStereotypeTransformer.transform(sourceElement, target);

    List<Property> targetProperties = new ArrayList<>();

    // 1. The first step is to get all IRelationshipEnd elements connected to the
    // INARY element
    Iterable<IRelationshipEnd> fromEndIterable = () -> naryElement.fromRelationshipEndIterator();

    // Each end is connected to the INARY Element. In order to access the other
    // elements, we need then to get the opposite IRelationshipEnd.
    for (IRelationshipEnd element : fromEndIterable) {
      IRelationshipEnd oppositeEnd = element.getOppositeEnd();
      Property endProperty = IPropertyTransformer.transform(oppositeEnd, project);
      targetProperties.add(endProperty);
      project.addProperty(endProperty);
    }

    target.setProperties(targetProperties);
    project.addElement(target);

    return target;
  }
}
