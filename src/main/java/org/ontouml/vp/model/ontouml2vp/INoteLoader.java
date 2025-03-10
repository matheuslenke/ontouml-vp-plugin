package org.ontouml.vp.model.ontouml2vp;

import org.ontouml.ontouml4j.model.Note;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.INOTE;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;

public class INoteLoader {

  static IProject vpProject = ApplicationManager.instance().getProjectManager().getProject();

  public static INOTE importElement(Note fromNote) {
    LoaderUtils.logElementCreation(fromNote);

    INOTE toNote = getOrCreateNote(fromNote);
    fromNote.setId(toNote.getId());

    String name = fromNote.getFirstName().orElse("Unnamed Note");
    toNote.setName(name);

    String text = fromNote.getFirstText();
    toNote.setDescription(text);

    return toNote;
  }

  private static INOTE getOrCreateNote(Note fromClass) {
    IModelElement toNote = vpProject.getModelElementById(fromClass.getId());

    if (toNote instanceof INOTE) {
      System.out.println("Class " + fromClass.getId() + " exists! Let's update it!");
    } else {
      System.out.println("Class " + fromClass.getId() + " not found! Let's create it");
      toNote = IModelElementFactory.instance().createNOTE();
    }

    return (INOTE) toNote;
  }
}
