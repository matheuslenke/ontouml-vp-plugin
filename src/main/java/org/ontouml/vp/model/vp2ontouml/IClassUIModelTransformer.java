package org.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.shape.IClassUIModel;
import org.ontouml.ontouml4j.model.Class;
import org.ontouml.ontouml4j.model.view.ClassView;
import org.ontouml.ontouml4j.model.view.Diagram;

public class IClassUIModelTransformer {
  public static ClassView transform(IDiagramElement sourceElement, Diagram diagram) {
    if (!(sourceElement instanceof IClassUIModel)) return null;

    IClassUIModel source = (IClassUIModel) sourceElement;
    ClassView target = new ClassView();

    IDiagramElementTransformer.transform(source, target, Class.class);
    IShapeTransformer.transform(source, target);

    diagram.addElement(target);
    return target;
  }
}
