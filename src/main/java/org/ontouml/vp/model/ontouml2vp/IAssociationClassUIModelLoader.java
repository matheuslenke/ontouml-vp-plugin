package org.ontouml.vp.model.ontouml2vp;

import static org.ontouml.vp.model.ontouml2vp.LoaderUtils.getIDiagramElement;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IAssociationClass;
import com.vp.plugin.model.IModelElement;
import org.ontouml.vp.model.ontouml.view.RelationView;
import java.awt.Point;

public class IAssociationClassUIModelLoader {

  static DiagramManager diagramManager = ApplicationManager.instance().getDiagramManager();

  public static void load(IClassDiagramUIModel toDiagram, RelationView fromView) {
    IModelElement toModelElement = LoaderUtils.getIModelElement(fromView);

    if (!(toModelElement instanceof IAssociationClass)) {
      System.out.println(
          LoaderUtils.getIncompatibleMessage(fromView, toModelElement, IAssociationClass.class));
      return;
    }

    IDiagramElement toSource = LoaderUtils.getIDiagramElement(toDiagram, fromView.getSource());
    IDiagramElement toTarget = LoaderUtils.getIDiagramElement(toDiagram, fromView.getTarget());

    Point[] toPoints = IConnectorUIModelLoader.loadPoints(fromView);

    IDiagramElement toView =
        diagramManager.createConnector(toDiagram, toModelElement, toSource, toTarget, toPoints);

    fromView.setId(toView.getId());
    toView.resetCaption();
  }
}
