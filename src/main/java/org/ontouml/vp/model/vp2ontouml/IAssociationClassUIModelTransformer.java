package org.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.connector.IAssociationClassUIModel;

import org.ontouml.ontouml4j.model.BinaryRelation;
import org.ontouml.ontouml4j.model.view.BinaryRelationView;

public class IAssociationClassUIModelTransformer {

  public static BinaryRelationView transform(IDiagramElement sourceElement) {
    if (!(sourceElement instanceof IAssociationClassUIModel)) return null;

    IAssociationClassUIModel source = (IAssociationClassUIModel) sourceElement;
    BinaryRelationView target = new BinaryRelationView();

    IDiagramElementTransformer.transform(source, target, BinaryRelation.class);
    IConnectorTransformer.transform(source, target);

    return target;
  }
}
