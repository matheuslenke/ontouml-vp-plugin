package org.ontouml.vp.model.ontouml.view;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.ontouml.vp.model.ontouml.OntoumlElement;
import org.ontouml.vp.model.ontouml.deserialization.GeneralizationViewDeserializer;
import org.ontouml.vp.model.ontouml.model.Generalization;
import org.ontouml.vp.model.ontouml.serialization.GeneralizationViewSerializer;

import java.util.ArrayList;
import java.util.List;

@JsonSerialize(using = GeneralizationViewSerializer.class)
@JsonDeserialize(using = GeneralizationViewDeserializer.class)
public class GeneralizationView extends ConnectorView<Generalization> {

  public GeneralizationView(String id, Generalization generalization) {
    super(id, generalization);
  }

  public GeneralizationView(Generalization generalization) {
    this(null, generalization);
  }

  public GeneralizationView() {
    this(null, null);
  }

  @Override
  public List<OntoumlElement> getContents() {
    return new ArrayList<>();
  }

  @Override
  public String getType() {
    return "GeneralizationView";
  }
}
