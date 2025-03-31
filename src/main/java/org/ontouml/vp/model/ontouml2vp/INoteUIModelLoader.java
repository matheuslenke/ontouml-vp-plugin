package org.ontouml.vp.model.ontouml2vp;

import org.ontouml.ontouml4j.model.view.NoteView;
import org.ontouml.ontouml4j.shape.Text;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.shape.INoteUIModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.INOTE;

public class INoteUIModelLoader {
  static DiagramManager diagramManager = ApplicationManager.instance().getDiagramManager();

  public static void load(IClassDiagramUIModel toDiagram, NoteView fromView) {
    IModelElement toModelElement = LoaderUtils.getIModelElement(fromView);

    if (!(toModelElement instanceof INOTE)) {
      System.out.println(
          LoaderUtils.getIncompatibleMessage(fromView, toModelElement, INOTE.class));
      return;
    }

    INoteUIModel toView = (INoteUIModel) diagramManager.createDiagramElement(toDiagram, toModelElement);
    fromView.setId(toView.getId());

    toView.resetCaption();
    Text text = fromView.getText();
    if (text != null) {
      toView.setX(text.getTopLeft().getX());
      toView.setY(text.getTopLeft().getY());
      toView.setWidth(text.getWidth());
      toView.setHeight(text.getHeight());
    }
  }
}
