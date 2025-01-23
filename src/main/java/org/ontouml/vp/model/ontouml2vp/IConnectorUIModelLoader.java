package org.ontouml.vp.model.ontouml2vp;

import org.ontouml.vp.model.ontouml.view.ConnectorView;
import java.awt.*;

public class IConnectorUIModelLoader {

  public static Point[] loadPoints(ConnectorView fromView) {
    return fromView.getPath().getPoints().stream()
        .map(p -> new Point(p.getX(), p.getY()))
        .toArray(Point[]::new);
  }
}
