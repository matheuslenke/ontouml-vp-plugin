package org.ontouml.vp.model.ontouml2vp;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IHasChildrenBaseModelElement;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;
import org.ontouml.ontouml4j.model.Package;

public class IPackageLoader {

  static IProject vpProject = ApplicationManager.instance().getProjectManager().getProject();

  public static IHasChildrenBaseModelElement importElement(Package fromPackage) {
    LoaderUtils.logElementCreation(fromPackage);

    IHasChildrenBaseModelElement toPackage = getOrCreatePackage(fromPackage);
    fromPackage.setId(toPackage.getId());

    ITaggedValueLoader.loadTaggedValues(fromPackage, toPackage);

    return toPackage;
  }

  private static IHasChildrenBaseModelElement getOrCreatePackage(Package fromPackage) {
    IModelElement toPackage = vpProject.getModelElementById(fromPackage.getId());

    if (toPackage instanceof IHasChildrenBaseModelElement) {
      System.out.println("Package " + fromPackage.getId() + " exists! Let's update it!");
    } else {
      System.out.println("Package " + fromPackage.getId() + " not found! Let's create it");
      toPackage = IModelElementFactory.instance().createPackage();
    }

    return (IHasChildrenBaseModelElement) toPackage;
  }
}
