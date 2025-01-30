package org.ontouml.vp.model.ontouml2vp;

import org.ontouml.ontouml4j.model.view.BinaryConnectorView;
import java.awt.*;

public class IConnectorUIModelLoader {

  public static Point[] loadPoints(BinaryConnectorView fromView) {
    return fromView.getPath().getPoints().stream()
        .map(p -> new Point(p.getX(), p.getY()))
        .toArray(Point[]::new);
  }
}
