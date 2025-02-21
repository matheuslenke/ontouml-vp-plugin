package org.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.connector.IGeneralizationUIModel;
import org.ontouml.ontouml4j.model.Generalization;
import org.ontouml.ontouml4j.model.view.Diagram;
import org.ontouml.ontouml4j.model.view.GeneralizationView;


public class IGeneralizationUIModelTransformer {
  public static GeneralizationView transform(IDiagramElement sourceElement, Diagram diagram) {
    if (!(sourceElement instanceof IGeneralizationUIModel)) return null;

    IGeneralizationUIModel source = (IGeneralizationUIModel) sourceElement;
    GeneralizationView target = new GeneralizationView();

    IDiagramElementTransformer.transform(source, target, Generalization.class);
    IConnectorTransformer.transform(source, target);
    diagram.addElement(target);

    return target;
  }
}
