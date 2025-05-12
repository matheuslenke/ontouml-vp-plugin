package org.ontouml.vp.services.metaproperties;

import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModel;
import com.vp.plugin.model.IPackage;
import org.ontouml.vp.utils.ViewManagerUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class SidebarTreeCellRenderer extends DefaultTreeCellRenderer {

    // Define paths to icons if not available in ViewManagerUtils or for custom ones
    // For example, assuming a general "project settings" icon
    private ImageIcon projectIcon;
    private ImageIcon classIcon;
    private ImageIcon associationIcon;
    private ImageIcon packageIcon;
    private ImageIcon modelIcon; // Could be same as package or a generic model icon
    private ImageIcon defaultIcon; // Fallback

    public SidebarTreeCellRenderer() {
        // Initialize icons - it's better to load them once
        // Using ViewManagerUtils where possible
        // Placeholder for project icon, assuming one might exist or needs to be created
        // If ViewManagerUtils.PROJECT_LOGO existed, we'd use it.
        // For now, let's use a known existing icon as a placeholder if a specific project one isn't defined.
        // Or, load directly: new ImageIcon(getClass().getResource("/icons/project_icon.png"));
        // For this example, I'll use DIAGRAM_LOGO for "Project" as it's often the root view.
        projectIcon = new ImageIcon(ViewManagerUtils.getFilePath(ViewManagerUtils.DIAGRAM_LOGO)); 
        classIcon = new ImageIcon(ViewManagerUtils.getFilePath(ViewManagerUtils.CLASS_LOGO));
        associationIcon = new ImageIcon(ViewManagerUtils.getFilePath(ViewManagerUtils.ASSOCIATION_LOGO));
        packageIcon = new ImageIcon(ViewManagerUtils.getFilePath(ViewManagerUtils.PACKAGE_LOGO));
        modelIcon = new ImageIcon(ViewManagerUtils.getFilePath(ViewManagerUtils.PACKAGE_LOGO)); // Using package icon for IModel for now
        defaultIcon = null; // Or a generic icon
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                 boolean sel, boolean expanded,
                                                 boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        if (value instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();

            if (userObject instanceof ProjectMetapropertiesPanelView.SidebarItem) {
                ProjectMetapropertiesPanelView.SidebarItem item = (ProjectMetapropertiesPanelView.SidebarItem) userObject;
                Object element = item.getElement();
                setText(item.toString()); // Use the custom display name from SidebarItem

                if (element instanceof String && "Project".equals(element)) {
                    setIcon(projectIcon);
                } else if (element instanceof IClass) {
                    setIcon(classIcon);
                } else if (element instanceof IAssociation) {
                    setIcon(associationIcon);
                } else if (element instanceof IPackage) {
                    setIcon(packageIcon);
                } else if (element instanceof IModel) {
                    setIcon(modelIcon);
                } else {
                    setIcon(defaultIcon); // No specific icon or for the root project name node if it's not a SidebarItem
                }
            } else if (userObject instanceof String) { // For the root node if it's just a String
                 setText((String) userObject);
                 // Potentially set a root icon here if desired, e.g., projectIcon or a folder icon
                 // For now, let's assume the root might be the project name String
                 setIcon(projectIcon); 
            } else {
                setIcon(defaultIcon);
            }
        }
        return this;
    }
} 