package org.ontouml.vp.model.vp2ontouml;

import org.ontouml.ontouml4j.model.NaryRelation;
import org.ontouml.ontouml4j.model.view.Diagram;
import org.ontouml.ontouml4j.model.view.NaryRelationView;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.shape.INARYUIModel;

public class INaryUIModelTransformer {
  public static NaryRelationView transform(IDiagramElement sourceElement, Diagram diagram) {
    if (!(sourceElement instanceof INARYUIModel))
      return null;

    INARYUIModel source = (INARYUIModel) sourceElement;
    NaryRelationView target = new NaryRelationView();

    IDiagramElementTransformer.transform(source, target, NaryRelation.class);
    IShapeTransformer.transform(source, target);

    diagram.addElement(target);

    return target;
  }
}
