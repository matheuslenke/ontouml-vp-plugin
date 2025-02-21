package org.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.shape.IBasePackageUIModel;
import com.vp.plugin.diagram.shape.IModelUIModel;
import com.vp.plugin.diagram.shape.IPackageUIModel;
import org.ontouml.ontouml4j.model.Package;
import org.ontouml.ontouml4j.model.view.Diagram;
import org.ontouml.ontouml4j.model.view.PackageView;


public class IPackageUIModelTransformer {
  public static PackageView transform(IDiagramElement sourceElement, Diagram diagram) {
    if (!(sourceElement instanceof IPackageUIModel) && !(sourceElement instanceof IModelUIModel))
      return null;

    IBasePackageUIModel source = (IBasePackageUIModel) sourceElement;
    PackageView target = new PackageView();

    IDiagramElementTransformer.transform(source, target, Package.class);
    IShapeTransformer.transform(source, target);
    diagram.addElement(target);

    return target;
  }
}
