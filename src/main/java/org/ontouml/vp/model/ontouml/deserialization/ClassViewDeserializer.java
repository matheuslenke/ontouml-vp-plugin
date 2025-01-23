package org.ontouml.vp.model.ontouml.deserialization;

import static org.ontouml.vp.model.ontouml.deserialization.DeserializerUtils.deserializeObjectField;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.ontouml.vp.model.ontouml.model.Class;
import org.ontouml.vp.model.ontouml.view.ClassView;
import org.ontouml.vp.model.ontouml.view.Rectangle;
import java.io.IOException;

public class ClassViewDeserializer extends JsonDeserializer<ClassView> {

  @Override
  public ClassView deserialize(JsonParser parser, DeserializationContext context)
      throws IOException {
    ObjectCodec codec = parser.getCodec();
    JsonNode root = parser.readValueAsTree();

    ClassView view = new ClassView();

    String id = root.get("id").asText();
    view.setId(id);

    Class element = deserializeObjectField(root, "modelElement", Class.class, codec);
    view.setModelElement(element);

    Rectangle shape = deserializeObjectField(root, "shape", Rectangle.class, codec);
    view.setShape(shape);

    return view;
  }
}
