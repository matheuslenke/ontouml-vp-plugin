package org.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.shape.IGeneralizationSetUIModel;
import org.ontouml.ontouml4j.model.GeneralizationSet;
import org.ontouml.ontouml4j.model.view.Diagram;
import org.ontouml.ontouml4j.model.view.GeneralizationSetView;

public class IGeneralizationSetUIModelTransformer {

  public static GeneralizationSetView transform(IDiagramElement sourceElement, Diagram diagram) {
    if (!(sourceElement instanceof IGeneralizationSetUIModel)) return null;

    IGeneralizationSetUIModel source = (IGeneralizationSetUIModel) sourceElement;
    GeneralizationSetView target = new GeneralizationSetView();

    IDiagramElementTransformer.transform(source, target, GeneralizationSet.class);
    IShapeTransformer.transform(source, target);
    diagram.addElement(target);

    return target;
  }
}
