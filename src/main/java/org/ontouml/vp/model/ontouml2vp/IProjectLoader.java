package org.ontouml.vp.model.ontouml2vp;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import org.ontouml.ontouml4j.model.*;
import org.ontouml.ontouml4j.model.Class;
import org.ontouml.ontouml4j.model.Package;

public class IProjectLoader {

    static IProject vpProject = ApplicationManager.instance().getProjectManager().getProject();

    public static void load(
            Project project, boolean shouldOverrideDiagrams, boolean shouldAutoLayoutDiagrams) {
        System.out.println("Loading model of project " + project.getId() + "...");
        importModel(project);
        System.out.println("Model loaded!");
        System.out.println("Loading diagrams of project " + project.getId() + "...");
        project
                .getAllDiagrams()
                .forEach(
                        diagram -> IClassDiagramLoader.load(
                                diagram, shouldOverrideDiagrams, shouldAutoLayoutDiagrams));
        System.out.println("Diagrams loaded!");
    }

    public static void importModel(Project fromProject) {

        fromProject
                .getAllPackages().stream()
                .filter(pkg -> !pkg.isRoot())
                .forEach(IPackageLoader::importElement);

        fromProject.getAllClasses().stream()
                .filter(c -> !c.isPrimitiveDatatype())
                .forEach(IClassLoader::importElement);

        fromProject.getAllNotes().stream().forEach(INoteLoader::importElement);

        // transform attributes
        fromProject.getAllClasses().stream()
                .filter(Class::hasAttributes)
                .forEach(IAttributeLoader::importAttributes);

        fromProject.getAllPackages().forEach(IProjectLoader::transferContainerAndName);

        fromProject.getAllClasses().forEach(IProjectLoader::transferContainerAndName);

        fromProject.getAllRelations().stream()
                .filter(rel -> rel instanceof BinaryRelation)
                .map(rel -> (BinaryRelation) rel)
                .filter(BinaryRelation::holdsBetweenClasses)
                .forEach(System.out::println);

        // transform relations between classes
        fromProject.getAllRelations().stream()
                .filter(rel -> rel instanceof BinaryRelation)
                .map(rel -> (BinaryRelation) rel)
                .filter(BinaryRelation::holdsBetweenClasses)
                .forEach(IAssociationLoader::importElement);

        // transform relations between classes and relations
        fromProject.getAllRelations().stream()
                .filter(rel -> rel instanceof BinaryRelation)
                .map(rel -> (BinaryRelation) rel)
                .filter(BinaryRelation::holdsBetweenClassAndRelation)
                .forEach(IAssociationClassLoader::importElement);

        // transform generalization
        fromProject.getAllGeneralizations().forEach(IGeneralizationLoader::importElement);

        // transform generalization sets
        fromProject.getAllGeneralizationSets().forEach(IGeneralizationSetLoader::importElement);
    }

    private static void transferContainerAndName(ModelElement fromElement) {
        IModelElement toElement = vpProject.getModelElementById(fromElement.getId());

        if (toElement == null)
            return;

        System.out.println(
                "Transferring container and name of "
                        + fromElement.getType()
                        + " "
                        + fromElement.getFirstName().orElse(null)
                        + " ("
                        + fromElement.getId()
                        + ")");

        OntoumlElement container = fromElement.getContainer();
        if (container != null) {
            if ((container instanceof Package && !((Package) container).isRoot())
                    || container instanceof Class) {
                IModelElement toContainer = vpProject.getModelElementById(container.getId());
                toContainer.addChild(toElement);
            }
        }

        LoaderUtils.loadName(fromElement, toElement);
    }
}
