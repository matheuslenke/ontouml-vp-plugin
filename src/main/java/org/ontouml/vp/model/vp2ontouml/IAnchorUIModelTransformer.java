package org.ontouml.vp.model.vp2ontouml;

import org.ontouml.ontouml4j.model.Anchor;
import org.ontouml.ontouml4j.model.view.AnchorView;
import org.ontouml.ontouml4j.model.view.Diagram;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.connector.IAnchorUIModel;

public class IAnchorUIModelTransformer {
  public static AnchorView transform(IDiagramElement sourceElement, Diagram diagram) {
    if (!(sourceElement instanceof IAnchorUIModel))
      return null;

    IAnchorUIModel source = (IAnchorUIModel) sourceElement;
    AnchorView target = new AnchorView();

    IDiagramElementTransformer.transform(source, target, Anchor.class);
    IConnectorTransformer.transform(source, target);
    diagram.addElement(target);

    return target;
  }
}
