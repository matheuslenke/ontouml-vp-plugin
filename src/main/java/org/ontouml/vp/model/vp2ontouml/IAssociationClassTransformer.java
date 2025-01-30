package org.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.model.IAssociationClass;
import com.vp.plugin.model.IModelElement;
import org.ontouml.ontouml4j.model.OntoumlElement;
import org.ontouml.ontouml4j.model.BinaryRelation;
import org.ontouml.ontouml4j.model.Classifier;
import org.ontouml.ontouml4j.model.ModelElement;

public class IAssociationClassTransformer {
  public static ModelElement transform(IModelElement sourceElement) {
    if (!(sourceElement instanceof IAssociationClass)) return null;

    IAssociationClass source = (IAssociationClass) sourceElement;

    Classifier<?, ?> fromClassifier = createClassifierStub(source.getFrom());
    Classifier<?, ?> toClassifier = createClassifierStub(source.getTo());

    BinaryRelation target = BinaryRelation.createDerivation(null, null, fromClassifier, toClassifier);

    IModelElementTransformer.transform(source, target);
    ITaggedValueTransformer.transform(source, target);
    IStereotypeTransformer.transform(source, target);

    target.setDerived(true);
    target.setAbstract(false);

    return target;
  }

  private static Classifier<?, ?> createClassifierStub(IModelElement classifier) {
    OntoumlElement targetType = ReferenceTransformer.transformStub(classifier);
    return targetType instanceof Classifier<?, ?> ? (Classifier<?, ?>) targetType : null;
  }
}
