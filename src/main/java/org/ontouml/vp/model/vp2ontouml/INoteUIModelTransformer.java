package org.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.shape.INoteUIModel;
import org.ontouml.ontouml4j.model.Note;
import org.ontouml.ontouml4j.model.view.Diagram;
import org.ontouml.ontouml4j.model.view.NoteView;

public class INoteUIModelTransformer {
  public static NoteView transform(IDiagramElement sourceElement, Diagram diagram) {
    if (!(sourceElement instanceof INoteUIModel))
      return null;

    INoteUIModel source = (INoteUIModel) sourceElement;
    NoteView target = new NoteView();

    IDiagramElementTransformer.transform(source, target, Note.class);
    IShapeTransformer.transform(source, target);

    diagram.addElement(target);
    return target;
  }
}
