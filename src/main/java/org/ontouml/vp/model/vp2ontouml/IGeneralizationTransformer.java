package org.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IModelElement;
import org.ontouml.ontouml4j.model.OntoumlElement;
import org.ontouml.ontouml4j.model.Classifier;
import org.ontouml.ontouml4j.model.Generalization;
import org.ontouml.ontouml4j.model.Project;

public class IGeneralizationTransformer {

  public static Generalization transform(IModelElement sourceElement, Project project) {
    if (!(sourceElement instanceof IGeneralization)) return null;

    IGeneralization source = (IGeneralization) sourceElement;
    Generalization target = new Generalization();

    IModelElementTransformer.transform(source, target);
    ITaggedValueTransformer.transform(source, target);

    Classifier<?, ?> general = transformGeneral(source);
    target.setGeneral(general);

    Classifier<?, ?> specific = transformSpecific(source);
    target.setSpecific(specific);

    project.addGeneralization(target);

    return target;
  }

  public static Classifier<?, ?> transformGeneral(IGeneralization generalization) {
    IModelElement general = generalization.getFrom();
    return (general != null) ? createClassifierStub(general) : null;
  }

  public static Classifier<?, ?> transformSpecific(IGeneralization generalization) {
    IModelElement specific = generalization.getTo();
    return (specific != null) ? createClassifierStub(specific) : null;
  }

  private static Classifier<?, ?> createClassifierStub(IModelElement classifier) {
    OntoumlElement targetType = ReferenceTransformer.transformStub(classifier);
    return targetType instanceof Classifier<?, ?> ? (Classifier<?, ?>) targetType : null;
  }
}
