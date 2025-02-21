package org.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IShapeUIModel;
import java.util.List;
import org.ontouml.ontouml4j.model.view.*;
import org.ontouml.ontouml4j.shape.Point;
import org.ontouml.ontouml4j.shape.Text;

public class IShapeTransformer {

  public static void transform(IShapeUIModel source, View target) {

    if (target instanceof ClassView) {
      ((ClassView) target).getRectangle().getTopLeft().setX(source.getX());
      ((ClassView) target).getRectangle().getTopLeft().setY(source.getY());
      ((ClassView) target).getRectangle().setWidth(source.getWidth());
      ((ClassView) target).getRectangle().setHeight(source.getHeight());
      ((ClassView) target).getRectangle().setId(source.getId() + "_shape");
    } else if (target instanceof PackageView) {
      ((PackageView) target).getRectangle().getTopLeft().setX(source.getX());
      ((PackageView) target).getRectangle().getTopLeft().setY(source.getY());
      ((PackageView) target).getRectangle().setWidth(source.getWidth());
      ((PackageView) target).getRectangle().setHeight(source.getHeight());
      ((PackageView) target).getRectangle().setId(source.getId() + "_shape");
    } else if (target instanceof BinaryConnectorView) {
      ((BinaryConnectorView) target).getPath().setId(source.getId() + "_shape");
      Point point = new Point(source.getX(), source.getY());
      ((BinaryConnectorView) target).getPath().setPoints(List.of(point));
    } else if (target instanceof GeneralizationSetView) {
      Text text = new Text(source.getId() + "_shape");
      text.getTopLeft().setX(source.getX());
      text.getTopLeft().setY(source.getY());
      text.setWidth(source.getWidth());
      text.setHeight(source.getHeight());
      ((GeneralizationSetView) target).setText(text);
    } else if (target instanceof NoteView) {
      Text text = new Text(source.getId() + "_shape");
      text.getTopLeft().setX(source.getX());
      text.getTopLeft().setY(source.getY());
      text.setWidth(source.getWidth());
      text.setHeight(source.getHeight());
      ((NoteView) target).setText(text);
    } else if (target instanceof NaryRelationView) {
      // TODO: How to get diamond?
    }
  }
}
