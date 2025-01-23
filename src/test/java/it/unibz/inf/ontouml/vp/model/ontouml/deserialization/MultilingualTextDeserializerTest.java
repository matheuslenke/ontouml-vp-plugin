package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ontouml.vp.model.ontouml.MultilingualText;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class MultilingualTextDeserializerTest {

  @Test
  void deserializeObject() throws IOException {
    ObjectMapper mapper = new ObjectMapper();

    String json = "{\"en\":\"My Project\", \"pt\": \"Meu Projeto\" }";
    MultilingualText element = mapper.readValue(json, MultilingualText.class);

    assertThat(element.getLanguages()).containsExactly("en", "pt");
    assertThat(element.getText("en")).hasValue("My Project");
    assertThat(element.getText("pt")).hasValue("Meu Projeto");
  }

  @Test
  void deserializeString() throws IOException {
    ObjectMapper mapper = new ObjectMapper();

    String json = "\"My Project\"";
    MultilingualText element = mapper.readValue(json, MultilingualText.class);

    assertThat(element.getLanguages()).containsExactly("en");
    assertThat(element.getText("en")).hasValue("My Project");
  }

  @Test
  void deserializeNull() throws IOException {
    ObjectMapper mapper = new ObjectMapper();

    String json = "null";
    MultilingualText element = mapper.readValue(json, MultilingualText.class);

    assertThat(element.getLanguages()).isEmpty();
    assertThat(element.getText("en")).isEmpty();
  }
}
