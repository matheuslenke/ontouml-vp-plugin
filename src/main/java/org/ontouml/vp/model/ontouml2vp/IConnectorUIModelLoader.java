package org.ontouml.vp.model.ontouml2vp;

import java.awt.Point;

import org.ontouml.ontouml4j.model.view.BinaryConnectorView;

public class IConnectorUIModelLoader {

  public static Point[] loadPoints(BinaryConnectorView fromView) {
    return fromView.getPath().getPoints().stream()
        .map(p -> new Point(p.getX(), p.getY()))
        .toArray(Point[]::new);
  }
}
