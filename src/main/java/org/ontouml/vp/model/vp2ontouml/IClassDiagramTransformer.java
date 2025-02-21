package org.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.connector.IAssociationClassUIModel;
import com.vp.plugin.diagram.connector.IAssociationUIModel;
import com.vp.plugin.diagram.connector.IGeneralizationUIModel;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.diagram.shape.IGeneralizationSetUIModel;
import com.vp.plugin.diagram.shape.IModelUIModel;
import com.vp.plugin.diagram.shape.IPackageUIModel;
import com.vp.plugin.model.IModelElement;
import java.util.Arrays;
import org.ontouml.ontouml4j.model.ModelElement;
import org.ontouml.ontouml4j.model.Package;
import org.ontouml.ontouml4j.model.Project;
import org.ontouml.ontouml4j.model.view.Diagram;
import org.ontouml.ontouml4j.model.view.View;

public class IClassDiagramTransformer {

  public static Diagram transform(IDiagramUIModel sourceElement, Project project) {
    if (!(sourceElement instanceof IClassDiagramUIModel)) return null;

    IClassDiagramUIModel source = (IClassDiagramUIModel) sourceElement;

    Diagram target = new Diagram();
    target.setProjectContainer(project);

    String id = source.getId();
    target.setId(id);

    // TODO: Diagram should have name?
    String name = source.getName();
    target.addName(name);

    String description = source.getDocumentation();
    if (description != null) {
      target.addDescription(description);
    }

    ModelElement owner = getOwner(source, project.getRoot());
    target.setOwner(owner);

    Arrays.stream(source.toDiagramElementArray())
        .map(item -> transfromIDiagramElement(item, target))
        .forEach(target::addElement);

    return target;
  }

  private static ModelElement getOwner(IClassDiagramUIModel source, Package root) {
    IModelElement owner = source.getParentModel();

    if (owner == null) return root;

    return ReferenceTransformer.transformStub(owner);
  }

  public static View transfromIDiagramElement(IDiagramElement source, Diagram diagram) {
    View target = null;

    if (source instanceof IClassUIModel) {
      target = IClassUIModelTransformer.transform(source, diagram);
    } else if (source instanceof IAssociationUIModel) {
      target = IAssociationUIModelTransformer.transform(source, diagram);
    } else if (source instanceof IAssociationClassUIModel) {
      target = IAssociationClassUIModelTransformer.transform(source, diagram);
    } else if (source instanceof IGeneralizationUIModel) {
      target = IGeneralizationUIModelTransformer.transform(source, diagram);
    } else if (source instanceof IGeneralizationSetUIModel) {
      target = IGeneralizationSetUIModelTransformer.transform(source, diagram);
    } else if (source instanceof IPackageUIModel || source instanceof IModelUIModel) {
      target = IPackageUIModelTransformer.transform(source, diagram);
    }

    Trace.getInstance().put(source.getId(), source, target);

    return target;
  }
}
