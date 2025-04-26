package org.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.IProject;
import org.ontouml.ontouml4j.model.Project;
import org.ontouml.vp.data.ProjectMetapropertiesData;
import org.ontouml.vp.model.vp2ontouml.IProjectTransformer;
import org.ontouml.vp.services.project.ProjectMetapropertiesService;

import javax.swing.*;

public class ProjectMetapropertiesController implements VPActionController {

    private static volatile boolean isDialogOpen = false;

    @Override
    public void performAction(VPAction vpAction) {
        if (isDialogOpen) {
            System.out.println("Project Metaproperties dialog is already open.");
            return;
        }

        IProject vpProject = null;
        try {
            isDialogOpen = true;

            vpProject = ApplicationManager.instance().getProjectManager().getProject();
            if (vpProject == null) {
                showErrorDialog("No project is currently open.");
                isDialogOpen = false;
                return;
            }
            String projectId = vpProject.getId();
            if (projectId == null || projectId.isEmpty()) {
                showErrorDialog("Could not retrieve project ID.");
                isDialogOpen = false;
                return;
            }

            // 1. Transform the base VP project
            Project ontoumlProject = IProjectTransformer.transform(vpProject);
            if (ontoumlProject == null) {
                showErrorDialog("Could not transform project to OntoUML model.");
                isDialogOpen = false;
                return;
            }

            // 2. Try to load persisted properties from JSON
            ProjectMetapropertiesService propertiesService = new ProjectMetapropertiesService();
            ProjectMetapropertiesData persistedData = propertiesService.loadProperties(projectId);

            // 3. If loaded, update the transformed project with persisted data
            if (persistedData != null) {
                System.out.println("Loaded persisted metaproperties for project: " + projectId);
                persistedData.updateOntoUMLProject(ontoumlProject); 
                // Note: This uses the placeholder parsing logic within ProjectMetapropertiesData.
                // Consider moving parsing logic to a more robust location.
            } else {
                System.out.println("No persisted metaproperties found for project: " + projectId + ". Using defaults from VP project.");
                // If loading failed or no data exists, we just proceed with the transformed data.
            }

            // 4. Create and show the dialog handler, passing the (potentially updated) project and ID
            ProjectMetapropertiesDialogHandler handler = new ProjectMetapropertiesDialogHandler(ontoumlProject, projectId);
            handler.showDialog();
            
        } catch (Exception e) {
            showErrorDialog("Error loading project meta-properties: " + e.getMessage());
            e.printStackTrace();
            isDialogOpen = false; // Ensure flag is reset on error
        }
        // Note: isDialogOpen is reset by the DialogHandler's canClosed/closeDialog method
    }

    @Override
    public void update(VPAction vpAction) {
        // Enable the button only if a project is open
        IProject vpProject = ApplicationManager.instance().getProjectManager().getProject();
        // Also consider disabling if the dialog is already open? Optional.
        // vpAction.setEnabled(vpProject != null && !isDialogOpen);
        vpAction.setEnabled(vpProject != null);
    }

    // Public static method for the handler to call when closing
    public static synchronized void setDialogClosed() {
        isDialogOpen = false;
        System.out.println("Project Metaproperties dialog closed flag set to false.");
    }

    // Basic error dialog utility method (kept static)
    private static void showErrorDialog(String message) {
        ApplicationManager.instance().getViewManager().showMessageDialog(
                ApplicationManager.instance().getViewManager().getRootFrame(), // Parent component
                message, // Message
                "Metaproperties Error", // Title
                JOptionPane.ERROR_MESSAGE // Message type icon
        );
    }
}