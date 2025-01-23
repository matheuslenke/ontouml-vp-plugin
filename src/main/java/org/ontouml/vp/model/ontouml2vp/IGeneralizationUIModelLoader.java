package org.ontouml.vp.model.ontouml2vp;

import static org.ontouml.vp.model.ontouml2vp.LoaderUtils.getIDiagramElement;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.connector.IGeneralizationUIModel;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IModelElement;
import org.ontouml.vp.model.ontouml.view.GeneralizationView;
import java.awt.*;

public class IGeneralizationUIModelLoader {

  static DiagramManager diagramManager = ApplicationManager.instance().getDiagramManager();

  public static void load(IClassDiagramUIModel toDiagram, GeneralizationView fromView) {
    IModelElement toModelElement = LoaderUtils.getIModelElement(fromView);

    if (!(toModelElement instanceof IGeneralization)) {
      System.out.println(
          LoaderUtils.getIncompatibleMessage(fromView, toModelElement, IGeneralization.class));
      return;
    }

    IClassUIModel vpSource =
        LoaderUtils.getIDiagramElement(toDiagram, fromView.getSource(), IClassUIModel.class);
    IClassUIModel vpTarget =
        LoaderUtils.getIDiagramElement(toDiagram, fromView.getTarget(), IClassUIModel.class);

    Point[] points = IConnectorUIModelLoader.loadPoints(fromView);

    IGeneralizationUIModel toView =
        (IGeneralizationUIModel)
            diagramManager.createConnector(toDiagram, toModelElement, vpSource, vpTarget, points);

    fromView.setId(toView.getId());

    toView.resetCaption();
  }
}
