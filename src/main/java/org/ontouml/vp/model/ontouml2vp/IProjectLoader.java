package org.ontouml.vp.model.ontouml2vp;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import org.ontouml.ontouml4j.model.Project;
import org.ontouml.ontouml4j.model.Class;
import org.ontouml.ontouml4j.model.ModelElement;
import org.ontouml.ontouml4j.model.Package;

public class IProjectLoader {

  static IProject vpProject = ApplicationManager.instance().getProjectManager().getProject();

  public static void load(
      Project project, boolean shouldOverrideDiagrams, boolean shouldAutoLayoutDiagrams) {
    System.out.println("Loading model of project " + project.getId() + "...");
    importModel(project);
    System.out.println("Model loaded!");
    System.out.println("Loading diagrams of project " + project.getId() + "...");
//     project
//         .getDiagrams()
//         .values()
//         .forEach(
//             diagram ->
//                 IClassDiagramLoader.load(
//                     diagram, shouldOverrideDiagrams, shouldAutoLayoutDiagrams));
//     System.out.println("Diagrams loaded!");
  }

  public static void importModel(Project fromProject) {

//     fromProject.getPackages().values().stream()
//         .filter(pkg -> !pkg.isRoot())
//         .forEach(pkg -> IPackageLoader.importElement(pkg));

//     fromProject.getClasses().values().stream()
//         .filter(c -> !c.isPrimitiveDatatype())
//         .forEach(c -> IClassLoader.importElement(c));

    // fromProject.getAllPrimitiveDatatypes().forEach(d -> IDataTypeLoader.importElement(d));

    // TODO
    // // transform attributes
    // fromProject.getAllClasses().stream()
    //     .filter(c -> c.hasAttributes())
    //     .forEach(c -> IAttributeLoader.importAttributes(c));

    // // transform literals
    // fromProject.getAllEnumerations().forEach(c -> IEnumerationLoader.importLiterals(c));

    // fromProject.getAllPackages().stream()
    //     .filter(pkg -> !pkg.isRoot())
    //     .forEach(pkg -> transferContainerAndName(pkg));

    // fromProject.getAllClasses().forEach(pkg -> transferContainerAndName(pkg));

    // fromProject.getAllRelations().stream()
    //     .filter(rel -> rel.holdsBetweenClasses())
    //     .forEach(rel -> System.out.println(rel));

    // // transform relations between classes
    // fromProject.getAllRelations().stream()
    //     .filter(rel -> rel.holdsBetweenClasses())
    //     .forEach(rel -> IAssociationLoader.importElement(rel));

    // // transform relations between classes and relations
    // fromProject.getAllRelations().stream()
    //     .filter(rel -> rel.holdsBetweenClassAndRelation())
    //     .forEach(rel -> IAssociationClassLoader.importElement(rel));

    // // transform generalization
    // fromProject.getAllGeneralizations().forEach(gen -> IGeneralizationLoader.importElement(gen));

    // // transform generalization sets
    // fromProject
    //     .getAllGeneralizationSets()
    //     .forEach(gs -> IGeneralizationSetLoader.importElement(gs));
  }

  private static void transferContainerAndName(ModelElement fromElement) {
    // IModelElement toElement = vpProject.getModelElementById(fromElement.getId());

    // if (toElement == null) return;

    // System.out.println(
    //     "Transferring container and name of "
    //         + fromElement.getType()
    //         + " "
    //         + fromElement.getFirstName().orElse(null)
    //         + " ("
    //         + fromElement.getId()
    //         + ")");

    // fromElement
    //     .getContainer()
    //     .ifPresent(
    //         container -> {
    //           if ((container instanceof Package && !((Package) container).isRoot())
    //               || container instanceof Class) {
    //             IModelElement toContainer = vpProject.getModelElementById(container.getId());
    //             toContainer.addChild(toElement);
    //           }
    //         });

    // LoaderUtils.loadName(fromElement, toElement);
  }
}
