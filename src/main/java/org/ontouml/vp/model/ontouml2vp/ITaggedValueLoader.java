package org.ontouml.vp.model.ontouml2vp;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;
import org.ontouml.ontouml4j.model.ModelElement;

public class ITaggedValueLoader {

  static void loadTaggedValues(ModelElement fromElement, IModelElement toModelElement) {
    ITaggedValueContainer taggedValueContainer = toModelElement.getTaggedValues();

    fromElement
        .getCustomProperties()
        .forEach(
            (key, value) -> {
              ITaggedValue taggedValue = taggedValueContainer.createTaggedValue();
              taggedValue.setName(key);

              if (value instanceof Integer) taggedValue.setValue((Integer) value);
              else if (value instanceof Float) taggedValue.setValue((Float) value);
              else if (value instanceof Boolean) taggedValue.setValue((Boolean) value);
              else if (value instanceof String) taggedValue.setValue((String) value);
            });
  }
}
