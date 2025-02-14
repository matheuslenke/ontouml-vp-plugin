package org.ontouml.vp.model.vp2ontouml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.*;
import org.ontouml.ontouml4j.model.Project;

import java.io.IOException;

public class Uml2OntoumlTransformer {

  public static String transformAndSerialize() throws IOException {
    final IProject source = ApplicationManager.instance().getProjectManager().getProject();
    Project target = IProjectTransformer.transform(source);
    return target.serializeAsString();
  }
}
