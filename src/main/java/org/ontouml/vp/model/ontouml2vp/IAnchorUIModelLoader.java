package org.ontouml.vp.model.ontouml2vp;

import java.awt.Point;

import org.ontouml.ontouml4j.model.view.AnchorView;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.connector.IAnchorUIModel;
import com.vp.plugin.model.IModelElement;

public class IAnchorUIModelLoader {
    static DiagramManager diagramManager = ApplicationManager.instance().getDiagramManager();

    public static void load(IClassDiagramUIModel toDiagram, AnchorView fromView) {
        IModelElement toModelElement = LoaderUtils.getIModelElement(fromView);

        if (!(toModelElement instanceof IAnchorUIModel)) {
            System.out.println(
                    LoaderUtils.getIncompatibleMessage(fromView, toModelElement, IAnchorUIModel.class));
            return;
        }

        IDiagramElement toSource = LoaderUtils.getIDiagramElement(toDiagram, fromView.getSourceView());
        IDiagramElement toTarget = LoaderUtils.getIDiagramElement(toDiagram, fromView.getTargetView());

        Point[] toPoints = IConnectorUIModelLoader.loadPoints(fromView);

        IDiagramElement toView = diagramManager.createConnector(toDiagram, toModelElement, toSource, toTarget,
                toPoints);

        fromView.setId(toView.getId());
        toView.resetCaption();
    }
}
