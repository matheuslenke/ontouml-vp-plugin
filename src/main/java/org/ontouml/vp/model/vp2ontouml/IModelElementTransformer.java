package org.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.model.IModelElement;

import org.ontouml.ontouml4j.model.MultilingualText;
import org.ontouml.ontouml4j.model.NamedElement;

public class IModelElementTransformer {

  public static void transform(IModelElement source, NamedElement target) {
    String id = source.getId();
    target.setId(id);

    String name = source.getName();
    if (name != null) {
      target.addName(name);
    }

    MultilingualText description = new MultilingualText(source.getDescription());
    target.setDescription(description.isEmpty() ? null : description);
  }
}
