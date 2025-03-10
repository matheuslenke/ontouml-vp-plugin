package org.ontouml.vp.model.ontouml2vp;

import org.ontouml.ontouml4j.model.view.ClassView;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.model.*;

public class IClassUIModelLoader {

  static DiagramManager diagramManager = ApplicationManager.instance().getDiagramManager();

  public static void load(IClassDiagramUIModel toDiagram, ClassView fromView) {
    IModelElement toModelElement = LoaderUtils.getIModelElement(fromView);

    if (!(toModelElement instanceof IClass) && !(toModelElement instanceof IDataType)) {
      System.out.println(
          LoaderUtils.getIncompatibleMessage(fromView, toModelElement, IClass.class));
      return;
    }

    IClassUIModel toView =
        (IClassUIModel) diagramManager.createDiagramElement(toDiagram, toModelElement);
    fromView.setId(toView.getId());

    toView.resetCaption();

     toView.setX(fromView.getRectangle().getX());
     toView.setY(fromView.getRectangle().getY());
     toView.setWidth(fromView.getRectangle().getWidth());
     toView.setHeight(fromView.getRectangle().getHeight());
  }
}
