package org.ontouml.vp.model.ontouml.deserialization;

import static org.ontouml.vp.model.ontouml.deserialization.DeserializerUtils.*;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.ontouml.vp.model.ontouml.model.AggregationKind;
import org.ontouml.vp.model.ontouml.model.Classifier;
import org.ontouml.vp.model.ontouml.model.Property;
import java.io.IOException;
import java.util.List;

public class PropertyDeserializer extends JsonDeserializer<Property> {

  @Override
  public Property deserialize(JsonParser parser, DeserializationContext context)
      throws IOException {
    ObjectCodec codec = parser.getCodec();
    JsonNode root = parser.readValueAsTree();

    Property property = new Property();
    ElementDeserializer.deserialize(property, root, codec);
    ModelElementDeserializer.deserialize(property, root, codec);
    DecoratableDeserializer.deserialize(property, root, codec);

    boolean isDerived = DeserializerUtils.deserializeBooleanField(root, "isDerived");
    property.setDerived(isDerived);

    boolean isReadOnly = DeserializerUtils.deserializeBooleanField(root, "isReadOnly");
    property.setReadOnly(isReadOnly);

    boolean isOrdered = DeserializerUtils.deserializeBooleanField(root, "isOrdered");
    property.setOrdered(isOrdered);

    String cardinality = DeserializerUtils.deserializeNullableStringField(root, "cardinality");
    property.setCardinality(cardinality);

    Classifier<?, ?> propertyType = DeserializerUtils.deserializeClassifierField(root, "propertyType", codec);
    property.setPropertyType(propertyType);

    List<Property> subsettedProperties =
        deserializeArrayField(root, "subsettedProperties", Property.class, codec);
    property.setSubsettedProperties(subsettedProperties);

    List<Property> redefinedProperties =
        deserializeArrayField(root, "redefinedProperties", Property.class, codec);
    property.setRedefinedProperties(redefinedProperties);

    String aggregationKind = DeserializerUtils.deserializeNullableStringField(root, "aggregationKind");
    property.setAggregationKind(AggregationKind.findByName(aggregationKind).orElse(null));

    return property;
  }
}
