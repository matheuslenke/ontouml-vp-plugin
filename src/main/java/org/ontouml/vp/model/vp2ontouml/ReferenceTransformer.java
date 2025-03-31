package org.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.connector.IAnchorUIModel;
import com.vp.plugin.diagram.connector.IAssociationClassUIModel;
import com.vp.plugin.diagram.connector.IAssociationUIModel;
import com.vp.plugin.diagram.connector.IGeneralizationUIModel;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.diagram.shape.IGeneralizationSetUIModel;
import com.vp.plugin.diagram.shape.IModelUIModel;
import com.vp.plugin.diagram.shape.INARYUIModel;
import com.vp.plugin.diagram.shape.INoteUIModel;
import com.vp.plugin.diagram.shape.IPackageUIModel;
import com.vp.plugin.model.*;
import org.ontouml.ontouml4j.model.*;
import org.ontouml.ontouml4j.model.view.*;
import org.ontouml.ontouml4j.model.Class;
import org.ontouml.ontouml4j.model.Package;

public class ReferenceTransformer {
    public static ModelElement transformStub(IModelElement source) {
        ModelElement stub = null;

        if (source instanceof IClass || source instanceof IDataType) {
            stub = new Class();
        } else if (source instanceof IAssociation || source instanceof IAssociationClass) {
            stub = new BinaryRelation();
        } else if (source instanceof IPackage || source instanceof IModel) {
            stub = new Package();
        } else if (source instanceof IAttribute || source instanceof IAssociationEnd) {
            stub = new Property();
        } else if (source instanceof IGeneralization) {
            stub = new Generalization();
        } else if (source instanceof IGeneralizationSet) {
            stub = new GeneralizationSet();
        } else if (source instanceof INOTE) {
            stub = new Note();
        } else if (source instanceof IAnchor) {
            stub = new Anchor();
        } else if (source instanceof INARY) {
            stub = new NaryRelation();
        }

        if (stub != null) {
            stub.setId(source.getId());
        }

        return stub;
    }

    public static View transformStub(IDiagramElement source) {
        View stub = null;

        if (source instanceof IClassUIModel) {
            stub = new ClassView();
        } else if (source instanceof IAssociationUIModel
                || source instanceof IAssociationClassUIModel) {
            stub = new BinaryRelationView();
        } else if (source instanceof IPackageUIModel || source instanceof IModelUIModel) {
            stub = new PackageView();
        } else if (source instanceof IGeneralizationUIModel) {
            stub = new GeneralizationView();
        } else if (source instanceof IGeneralizationSetUIModel) {
            stub = new GeneralizationSetView();
        } else if (source instanceof INoteUIModel) {
            stub = new NoteView(null, null);
        } else if (source instanceof IAnchorUIModel) {
            stub = new AnchorView();
        } else if (source instanceof INARYUIModel) {
            stub = new NaryRelationView();
        }

        if (stub != null) {
            stub.setId(source.getId());
        }

        return stub;
    }
}
