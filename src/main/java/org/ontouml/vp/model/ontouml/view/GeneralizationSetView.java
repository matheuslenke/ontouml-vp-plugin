package org.ontouml.vp.model.ontouml.view;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.ontouml.vp.model.ontouml.deserialization.GeneralizationSetViewDeserializer;
import org.ontouml.vp.model.ontouml.model.GeneralizationSet;
import org.ontouml.vp.model.ontouml.serialization.GeneralizationSetViewSerializer;

@JsonSerialize(using = GeneralizationSetViewSerializer.class)
@JsonDeserialize(using = GeneralizationSetViewDeserializer.class)
public class GeneralizationSetView extends NodeView<GeneralizationSet, Text> {

  public GeneralizationSetView(String id, GeneralizationSet genSet) {
    super(id, genSet);
  }

  public GeneralizationSetView(GeneralizationSet genSet) {
    this(null, genSet);
  }

  public GeneralizationSetView() {
    this(null, null);
  }

  @Override
  public String getType() {
    return "GeneralizationSetView";
  }

  @Override
  Text createShape() {
    return new Text("");
  }
}
