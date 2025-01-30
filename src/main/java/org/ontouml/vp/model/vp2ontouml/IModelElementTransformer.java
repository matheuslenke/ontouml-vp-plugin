package org.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.model.IModelElement;

import org.ontouml.ontouml4j.model.NamedElement;

public class IModelElementTransformer {

  public static void transform(IModelElement source, NamedElement target) {
    String id = source.getId();
    target.setId(id);

    String name = source.getName();
    target.addName(name);

    String description = source.getDescription();
    target.addDescription(description != null && description.isEmpty() ? null : description);
  }
}
