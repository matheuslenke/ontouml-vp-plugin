package org.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.model.*;
import org.ontouml.ontouml4j.model.*;
import org.ontouml.ontouml4j.model.Class;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IAnchorTransformer {
  public static Anchor transform(IModelElement sourceElement, Project project) {
    if (!(sourceElement instanceof IAnchor)) {
      return null;
    }

    Anchor target = new Anchor();

    IModelElementTransformer.transform(sourceElement, target);
    ITaggedValueTransformer.transform(sourceElement, target);

    String name = sourceElement.getName();
    target.addName(name);

    String description = sourceElement.getDescription();
    target.setDescription(new MultilingualText(description));

    IModelElement from = ((IAnchor) sourceElement).getFrom();
    IModelElement to = ((IAnchor) sourceElement).getTo();

    IModelElement element = getIModelElementFromAnchor((IAnchor) sourceElement);
    ModelElement modelElement = IProjectTransformer.transformModelElement(element, project);

    INOTE inote = getNoteFromAnchor((IAnchor) sourceElement);
    Note note = INoteTransformer.transform(inote, project);

    target.setNote(note);
    target.setElement(modelElement);

    project.addAnchor(target);

    return target;
  }

  private static INOTE getNoteFromAnchor(IAnchor sourceElement) {
    if (sourceElement.getFrom() instanceof INOTE) {
      return (INOTE) sourceElement.getFrom();
    } else if (sourceElement.getTo() instanceof INOTE) {
      return (INOTE) sourceElement.getTo();
    }
    return null;
  }

    private static IModelElement getIModelElementFromAnchor(IAnchor sourceElement) {
    if (!(sourceElement.getFrom() instanceof INOTE)) {
      return (IModelElement) sourceElement.getFrom();
    } else if (!(sourceElement.getTo() instanceof INOTE)) {
      return (IModelElement) sourceElement.getTo();
    }
    return null;
  }
}
