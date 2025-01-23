package org.ontouml.vp.model.ontouml.view;

import org.ontouml.vp.model.ontouml.MultilingualText;
import org.ontouml.vp.model.ontouml.OntoumlElement;

/** Element defined in the concrete syntax of the language */
public abstract class DiagramElement extends OntoumlElement {

  public DiagramElement(String id, MultilingualText name) {
    super(id, name);
  }

  public DiagramElement(String id) {
    super(id, null);
  }
}
