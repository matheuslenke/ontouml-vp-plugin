package org.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IModelElement;
import org.ontouml.ontouml4j.model.ModelElement;
import org.ontouml.ontouml4j.model.view.View;
import org.ontouml.ontouml4j.shape.Shape;

public class IDiagramElementTransformer {

  public static <T extends ModelElement, S extends Shape> void transform(
      IDiagramElement source, View target, Class<T> type) {

    String id = source.getId();
    target.setId(id);

    T modelElement = getModelElement(source, type);
    target.setIsViewOf(modelElement);
  }

  private static <T extends ModelElement> T getModelElement(IDiagramElement view, Class<T> type) {
    IModelElement modelElement = view.getModelElement();
    ModelElement stub = ReferenceTransformer.transformStub(modelElement);

    return (type.isInstance(stub) ? type.cast(stub) : null);
  }
}
