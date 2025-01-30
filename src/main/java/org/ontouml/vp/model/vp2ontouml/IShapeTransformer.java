package org.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IShapeUIModel;

import org.ontouml.ontouml4j.model.view.View;

public class IShapeTransformer {

  public static void transform(IShapeUIModel source, View target) {

    // TODO: Fix View. Maybe it should have X and Y?
    // target.getRectangle().getTopLeft().setX(source.getX());
    // target.getRectangle().getTopLeft().setY(source.getY());
    // target.setY(source.getY());

    // target.setWidth(source.getWidth());
    // target.setHeight(source.getHeight());

    // target.getShape().setId(source.getId() + "_shape");
  }
}
