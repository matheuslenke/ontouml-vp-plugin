package org.ontouml.vp.model.ontouml.deserialization;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import org.ontouml.vp.model.ontouml.model.Decoratable;
import java.io.IOException;

public class DecoratableDeserializer {

  public static void deserialize(Decoratable<?> decoratable, JsonNode root, ObjectCodec codec)
      throws IOException {
    String stereotype = DeserializerUtils.deserializeNullableStringField(root, "stereotype");
    decoratable.setStereotype(stereotype);
  }
}
