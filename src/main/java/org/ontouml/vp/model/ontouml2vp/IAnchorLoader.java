package org.ontouml.vp.model.ontouml2vp;

import org.ontouml.ontouml4j.model.Anchor;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IAnchor;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;

public class IAnchorLoader {
  static IProject vpProject = ApplicationManager.instance().getProjectManager().getProject();

  public static IAnchor importElement(Anchor fromAnchor) {
    LoaderUtils.logElementCreation(fromAnchor);

    IAnchor toAnchor = getOrCreateAssociation(fromAnchor);
    fromAnchor.setId(toAnchor.getId());

    LoaderUtils.loadName(fromAnchor, toAnchor);

    IModelElement toSource = vpProject.getModelElementById(fromAnchor.getNote().getId());
    IModelElement toTarget = vpProject.getModelElementById(fromAnchor.getElement().getId());

    toAnchor.setFrom(toSource);
    toAnchor.setTo(toTarget);

    return toAnchor;
  }

  private static IAnchor getOrCreateAssociation(Anchor fromAnchor) {
    IModelElement toRelation = vpProject.getModelElementById(fromAnchor.getId());

    if (toRelation instanceof IAnchor) {
      System.out.println("Relation " + fromAnchor.getId() + " exists! Let's update it!");
    } else {
      System.out.println("Relation " + fromAnchor.getId() + " not found! Let's create it");
      toRelation = IModelElementFactory.instance().createAssociation();
    }

    return (IAnchor) toRelation;
  }
}
