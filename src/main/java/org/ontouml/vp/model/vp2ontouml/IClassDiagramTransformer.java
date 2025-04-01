package org.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.connector.IAnchorUIModel;
import com.vp.plugin.diagram.connector.IAssociationClassUIModel;
import com.vp.plugin.diagram.connector.IAssociationUIModel;
import com.vp.plugin.diagram.connector.IGeneralizationUIModel;
import com.vp.plugin.diagram.shape.*;
import com.vp.plugin.model.IModelElement;
import java.util.Arrays;
import org.ontouml.ontouml4j.model.ModelElement;
import org.ontouml.ontouml4j.model.Package;
import org.ontouml.ontouml4j.model.Project;
import org.ontouml.ontouml4j.model.view.AnchorView;
import org.ontouml.ontouml4j.model.view.BinaryConnectorView;
import org.ontouml.ontouml4j.model.view.Diagram;
import org.ontouml.ontouml4j.model.view.GeneralizationSetView;
import org.ontouml.ontouml4j.model.view.NaryRelationView;
import org.ontouml.ontouml4j.model.view.NoteView;
import org.ontouml.ontouml4j.model.view.View;
import org.ontouml.ontouml4j.shape.Diamond;
import org.ontouml.ontouml4j.shape.Path;
import org.ontouml.ontouml4j.shape.Text;

public class IClassDiagramTransformer {

  public static Diagram transform(IDiagramUIModel sourceElement, Project project) {
    if (!(sourceElement instanceof IClassDiagramUIModel))
      return null;

    IClassDiagramUIModel source = (IClassDiagramUIModel) sourceElement;

    Diagram target = project.createDiagram(source.getId(), null);

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

    resolveShapes(project, target);

    return target;
  }

  private static void resolveShapes(Project project, Diagram target) {
    /*
     * This loops through the created binary connector views in order to add the
     * path elements to the project
     */
    target.getViews().stream().filter(item -> item instanceof BinaryConnectorView)
        .forEach(
            item -> {
              BinaryConnectorView view = (BinaryConnectorView) item;
              Path path = view.getPath();
              project.addElement(path);
            });

    target.getViews().stream().filter(item -> item instanceof NoteView)
        .forEach(
            item -> {
              NoteView view = (NoteView) item;
              Text text = view.getText();
              project.addElement(text);
            });

    target.getViews().stream().filter(item -> item instanceof GeneralizationSetView)
        .forEach(
            item -> {
              GeneralizationSetView view = (GeneralizationSetView) item;
              Text text = view.getText();
              project.addElement(text);
            });

    target.getViews().stream().filter(item -> item instanceof NaryRelationView)
        .forEach(item -> {
          NaryRelationView view = (NaryRelationView) item;
          Diamond diamond = view.getDiamond();
          project.addElement(diamond);
        });
  }

  private static ModelElement getOwner(IClassDiagramUIModel source, Package root) {
    IModelElement owner = source.getParentModel();

    if (owner == null)
      return root;

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
    } else if (source instanceof INoteUIModel) {
      target = INoteUIModelTransformer.transform(source, diagram);
    } else if (source instanceof IAnchorUIModel) {
      target = IAnchorUIModelTransformer.transform(source, diagram);
    } else if (source instanceof INARYUIModel) {
      target = INaryUIModelTransformer.transform(source, diagram);
    }

    Trace.getInstance().put(source.getId(), source, target);

    return target;
  }
}
