package org.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.model.IModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IPackage;
import org.ontouml.ontouml4j.model.ModelElement;
import org.ontouml.ontouml4j.model.Package;
import org.ontouml.ontouml4j.model.Project;

public class IPackageTransformer {

  public static ModelElement transform(IModelElement sourceElement, Project project) {

    if (!(sourceElement instanceof IPackage) && !(sourceElement instanceof IModel)) return null;

    Package target = new Package();
    IModelElementTransformer.transform(sourceElement, target);
    ITaggedValueTransformer.transform(sourceElement, target);

    project.addPackage(target);

    return target;
  }
}
