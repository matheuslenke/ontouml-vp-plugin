package org.ontouml.vp.model.ontouml2vp;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;
import org.ontouml.ontouml4j.model.Generalization;

public class IGeneralizationLoader {

  static IProject vpProject = ApplicationManager.instance().getProjectManager().getProject();

  public static IGeneralization importElement(Generalization fromGeneralization) {

    LoaderUtils.logElementCreation(fromGeneralization);

    IGeneralization toGeneralization = getOrCreateGeneralization(fromGeneralization);
    fromGeneralization.setId(toGeneralization.getId());

    LoaderUtils.loadName(fromGeneralization, toGeneralization);

    loadGeneral(fromGeneralization, toGeneralization);
    loadSpecific(fromGeneralization, toGeneralization);

    ITaggedValueLoader.loadTaggedValues(fromGeneralization, toGeneralization);

    return toGeneralization;
  }

  private static void loadGeneral(Generalization fromGen, IGeneralization toGen) {
    fromGen
        .getGeneral()
        .map(general -> general.getId())
        .map(id -> vpProject.getModelElementById(id))
        .ifPresent(general -> toGen.setFrom(general));
  }

  private static void loadSpecific(Generalization fromGen, IGeneralization toGen) {
    fromGen
        .getSpecific()
        .map(specific -> specific.getId())
        .map(id -> vpProject.getModelElementById(id))
        .ifPresent(general -> toGen.setTo(general));
  }

  private static IGeneralization getOrCreateGeneralization(Generalization fromGeneralization) {
    IModelElement toGeneralization = vpProject.getModelElementById(fromGeneralization.getId());

    if (toGeneralization instanceof IGeneralization) {
      System.out.println(
          "Generalization " + fromGeneralization.getId() + " exists! Let's update it!");
    } else {
      System.out.println(
          "Generalization " + fromGeneralization.getId() + " not found! Let's create it");
      toGeneralization = IModelElementFactory.instance().createGeneralization();
    }

    return (IGeneralization) toGeneralization;
  }
}
