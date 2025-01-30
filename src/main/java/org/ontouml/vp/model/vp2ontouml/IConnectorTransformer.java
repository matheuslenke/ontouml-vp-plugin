package org.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramElement;

import org.ontouml.ontouml4j.model.view.BinaryConnectorView;
import org.ontouml.ontouml4j.model.view.View;
import org.ontouml.ontouml4j.shape.Path;

import java.util.Arrays;

public class IConnectorTransformer {

  public static void transform(IConnectorUIModel source, BinaryConnectorView target) {

    Path path = new Path();
    path.setId(source.getId() + "_path");
    // Arrays.stream(source.getPoints())
    //     .forEachOrdered(p -> path.moveTo((int) p.getX(), (int) p.getY()));
    target.setPath(path);

    IDiagramElement connectorSource = null;
    if (source.getFromShape() != null) connectorSource = source.getFromShape();
    else if (source.getFromConnector() != null) connectorSource = source.getFromConnector();

    View connectorSourceStub = ReferenceTransformer.transformStub(connectorSource);
    target.setSourceView(connectorSourceStub);

    IDiagramElement connectorTarget = null;
    if (source.getToShape() != null) connectorTarget = source.getToShape();
    else if (source.getToConnector() != null) connectorTarget = source.getToConnector();

    View connectorTargetStub = ReferenceTransformer.transformStub(connectorTarget);
    target.setTargetView(connectorTargetStub);
  }
}
