package org.ontouml.vp.model.ontouml2vp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.ontouml.vp.model.ontouml.Project;
import java.io.IOException;

public class Ontouml2UmlLoader {
  public static void deserializeAndLoad(
      String json, boolean shouldOverrideDiagrams, boolean shouldAutoLayoutDiagrams)
      throws IOException {
    System.out.println("Deserializing project...");
    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    Project project = mapper.readValue(json, Project.class);
    System.out.println("Project " + project.getId() + " deserialized!");
    System.out.println("Loading project " + project.getId() + " into Visual Paradigm...");
    IProjectLoader.load(project, shouldOverrideDiagrams, shouldAutoLayoutDiagrams);
    System.out.println("Project " + project.getId() + " loaded!");
  }
}
