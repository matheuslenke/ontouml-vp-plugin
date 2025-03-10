package org.ontouml.vp.model.ontouml2vp;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.model.IProject;
import java.util.stream.Stream;
import org.ontouml.ontouml4j.model.view.*;

public class IClassDiagramLoader {

  static IProject vpProject = ApplicationManager.instance().getProjectManager().getProject();
  static DiagramManager diagramManager = ApplicationManager.instance().getDiagramManager();

  public static void load(Diagram fromDiagram, boolean shouldOverride, boolean shouldAutoLayout) {
    if (!shouldOverride && vpDiagramExists(fromDiagram))
      return;

    IClassDiagramUIModel toDiagram = createIDiagram(fromDiagram);
    transferDiagramProperties(fromDiagram, toDiagram);

    // TODO
    fromDiagram.getViews().stream()
        .filter(view -> view instanceof ClassView)
        .map(view -> (ClassView) view)
        .forEach(fromClassView -> IClassUIModelLoader.load(toDiagram, fromClassView));

    fromDiagram.getViews().stream()
        .filter(view -> view instanceof PackageView)
        .map(view -> (PackageView) view)
        .forEach(fromView -> IPackageUIModelLoader.load(toDiagram, fromView));

    fromDiagram.getViews().stream()
        .filter(view -> view instanceof NoteView)
        .map(view -> (NoteView) view)
        .forEach(fromView -> INoteUIModelLoader.load(toDiagram, fromView));

    fromDiagram.getViews().stream()
        .filter(view -> view instanceof BinaryRelationView)
        .map(view -> (BinaryRelationView) view)
        .forEach(fromRelationView -> IAssociationUIModelLoader.load(toDiagram, fromRelationView));

    fromDiagram.getViews().stream()
        .filter(view -> view instanceof BinaryRelationView)
        .map(view -> (BinaryRelationView) view)
        .forEach(fromRelationView -> IAssociationUIModelLoader.load(toDiagram, fromRelationView));

    fromDiagram.getViews().stream()
        .filter(view -> view instanceof BinaryRelationView)
        .map(view -> (BinaryRelationView) view)
        // .filter(view -> view.getModelElement() != null)
        // .filter(view -> view.holdsBetweenClassAndRelation())
        .forEach(
            fromRelationView -> IAssociationClassUIModelLoader.load(toDiagram, fromRelationView));

    fromDiagram.getViews().stream()
        .filter(view -> view instanceof BinaryRelationView)
        .map(view -> (BinaryRelationView) view)
        // .filter(view -> view.getModelElement() != null)
        // .filter(view -> !view.getModelElement().holdsBetweenClassAndRelation())
        .forEach(
            fromRelationView -> IAssociationClassUIModelLoader.load(toDiagram, fromRelationView));

    fromDiagram.getViews().stream()
        .filter(view -> view instanceof GeneralizationView)
        .map(view -> (GeneralizationView) view)
        .forEach(fromGenView -> IGeneralizationUIModelLoader.load(toDiagram, fromGenView));

    fromDiagram.getViews().stream()
        .filter(view -> view instanceof GeneralizationSetView)
        .map(view -> (GeneralizationSetView) view)
        .forEach(fromGsView -> IGeneralizationSetUIModelLoader.load(toDiagram, fromGsView));

    // For information about auto layout for VP diagrams see the JavaDoc at
    // https://www.visual-paradigm.com/support/documents/pluginjavadoc/index.html?com/vp/plugin/diagram/LayoutOption.html
    // For custom options, I could not make the guide below work so far
    // https://knowhow.visual-paradigm.com/openapi/layout-diagram/
    if (shouldAutoLayout) {
      Stream.of(toDiagram.toDiagramElementArray())
          .filter(IClassUIModel.class::isInstance)
          .map(IClassUIModel.class::cast)
          .forEach(ele -> ele.fitSize());
      diagramManager.layout(toDiagram, DiagramManager.LAYOUT_ORTHOGONAL);
    }
  }

  private static void transferDiagramProperties(Diagram fromDiagram, IDiagramUIModel toDiagram) {
    // String fromOwnerId = fromDiagram.getOwner().getId();
    // IModelElement toOwner = vpProject.getModelElementById(fromOwnerId);

    // if (toOwner != null) toOwner.addSubDiagram(toDiagram); 89w9q1

    String name = fromDiagram.getFirstName().orElse("Unnamed diagram");
    toDiagram.setName(name);
  }

  private static IClassDiagramUIModel createIDiagram(Diagram fromDiagram) {
    IDiagramUIModel vpDiagram = vpProject.getDiagramById(fromDiagram.getId());

    if (vpDiagram != null) {
      System.out.println("Diagram " + fromDiagram.getId() + " exists! Let's override it!");
      vpDiagram.delete();
    } else {
      System.out.println("Diagram " + fromDiagram.getId() + " not found! Let's create it");
    }

    IClassDiagramUIModel toDiagram = (IClassDiagramUIModel) diagramManager
        .createDiagram(DiagramManager.DIAGRAM_TYPE_CLASS_DIAGRAM);
    fromDiagram.setId(toDiagram.getId());

    return toDiagram;
  }

  private static boolean vpDiagramExists(Diagram fromDiagram) {
    return vpProject.getDiagramById(fromDiagram.getId()) != null;
  }
}
