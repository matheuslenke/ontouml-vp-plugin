package org.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.INOTE;
import org.ontouml.ontouml4j.model.*;

public class INoteTransformer {
  public static Note transform(IModelElement sourceElement, Project project) {
    if (!(sourceElement instanceof INOTE)) {
      return null;
    }

    Note target = new Note(sourceElement.getId());

    IModelElementTransformer.transform(sourceElement, target);
    ITaggedValueTransformer.transform(sourceElement, target);

    String name = sourceElement.getName();
    target.setName(new MultilingualText(name));

    String description = sourceElement.getDescription();
    target.setText(new MultilingualText(description));

    project.addNote(target);
    return target;
  }
}
