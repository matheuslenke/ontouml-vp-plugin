package org.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.connector.IAssociationUIModel;
import org.ontouml.ontouml4j.model.Relation;
import org.ontouml.ontouml4j.model.view.BinaryRelationView;

public class IAssociationUIModelTransformer {
  public static BinaryRelationView transform(IDiagramElement sourceElement) {

    if (!(sourceElement instanceof IAssociationUIModel)) return null;

    IAssociationUIModel source = (IAssociationUIModel) sourceElement;
    BinaryRelationView target = new BinaryRelationView();

    IDiagramElementTransformer.transform(source, target, Relation.class);
    IConnectorTransformer.transform(source, target);

    // TODO: investigate if we can access the labels associated to the association (e.g.
    // stereotype+name, multiplicities)

    return target;
  }
}
