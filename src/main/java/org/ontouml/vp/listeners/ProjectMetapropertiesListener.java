package org.ontouml.vp.listeners;

import org.ontouml.vp.controllers.ProjectMetapropertiesDialogHandler;
import org.ontouml.vp.data.ProjectMetapropertiesData;
import org.ontouml.vp.services.project.ProjectMetapropertiesService;
import org.ontouml.vp.views.ProjectMetapropertiesPanel;

import com.vp.plugin.ApplicationManager;
import org.ontouml.vp.utils.OntoUMLStringUtils;
import org.ontouml.ontouml4j.model.MultilingualText;
import org.ontouml.ontouml4j.model.Project;
import org.ontouml.ontouml4j.model.Resource;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Component;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Listener implementation for handling actions from the ProjectMetapropertiesPanel.
 */
public class ProjectMetapropertiesListener implements ActionListener {

    // Define constants for action commands
    public static final String CMD_SAVE = "Save";
    public static final String CMD_CANCEL = "Cancel";

    private final ProjectMetapropertiesDialogHandler dialogHandler;
    private final Project project; // The project instance being edited
    private final ProjectMetapropertiesPanel panel; // Reference to the panel instance

    // Constructor updated to take the panel directly
     public ProjectMetapropertiesListener(ProjectMetapropertiesDialogHandler dialogHandler, Project project, ProjectMetapropertiesPanel panel) {
         super();
         if (project == null) {
             throw new IllegalArgumentException("Project cannot be null for the listener.");
         }
         if (project.getId() == null || project.getId().isEmpty()) {
             throw new IllegalArgumentException("Project ID cannot be null or empty for the listener.");
         }
         if (panel == null) {
             throw new IllegalArgumentException("Panel cannot be null for the listener.");
         }
         this.dialogHandler = dialogHandler;
         this.project = project;
         this.panel = panel; 
     }


    /**
     * Invoked when an action occurs (e.g., button click).
     *
     * @param e The event object.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (CMD_SAVE.equals(command)) {
            System.out.println("Save command received for project: " + project.getId());
             onSave(); // Call save method without passing panel
         } else if (CMD_CANCEL.equals(command)) {
            System.out.println("Cancel command received for project: " + project.getId());
            onCancel();
        }
    }

    /**
     * Handles the save action. Retrieves data from the panel,
     * updates the Project object, and saves it using the service.
     */
    private void onSave() {
        if (panel == null) {
            System.err.println("Error: Panel instance is null in listener.");
            showErrorDialog("Internal error: Panel reference missing.");
            return;
        }
        if (project == null) {
            System.err.println("Error: Project instance is null in listener.");
            showErrorDialog("Internal error: Project data missing.");
            return;
        }
        String projectId = project.getId();
        if (projectId == null || projectId.isEmpty()) {
            System.err.println("Error: Project ID is null or empty in listener.");
            showErrorDialog("Internal error: Project ID missing.");
            return;
        }

        try {
            // 1. Update the Project object with data from the panel's components
            // This call ensures MultilingualInputComponents save their current state
            Project updatedProject = panel.getUpdatedProject(); // This modifies the project object held by the listener

            // 2. Parse Creators and Contributors from their text areas
            // TODO: This parsing is basic. Needs robust implementation in OntoUMLStringUtils or a dedicated parser.
            //  try {
            //      List<Resource> creators = OntoUMLStringUtils.parseResources(panel.getCreatorsInput());
            //      project.setCreators(creators);
            //  } catch (IllegalArgumentException ex) {
            //      showErrorDialog("Invalid format in Creators field: " + ex.getMessage());
            //      return; // Stop saving if parsing fails
            //  }
            //  try {
            //      List<Resource> contributors = OntoUMLStringUtils.parseResources(panel.getContributorsInput());
            //      project.setContributors(contributors);
            //  } catch (IllegalArgumentException ex) {
            //      showErrorDialog("Invalid format in Contributors field: " + ex.getMessage());
            //      return; // Stop saving if parsing fails
            //  }

            // 3. Save the updated Project object's relevant properties
            //    The service needs to know *what* to save. We should probably pass
            //    the specific fields rather than the whole Project object if the service
            //    is designed to save only metaproperties.
            //    Let's assume the service needs the relevant data fields.

            // Create ProjectMetapropertiesData from the updated project object
             ProjectMetapropertiesData dataToSave = new ProjectMetapropertiesData(
                     updatedProject.getName(),
                     updatedProject.getAlternativeNames(),
                     updatedProject.getDescription(),
                     updatedProject.getEditorialNotes(),
                     updatedProject.getCreators(),
                     updatedProject.getContributors()
             );

            // 4. Save the data using the service
            ProjectMetapropertiesService propertiesService = new ProjectMetapropertiesService();
            boolean success = propertiesService.saveProperties(projectId, dataToSave);

            if (success) {
                System.out.println("Project metaproperties saved successfully via service for project: " + projectId);
                if (dialogHandler != null) dialogHandler.closeDialog();
                showSuccessDialog("Properties saved successfully.");
            } else {
                System.err.println("Failed to save project metaproperties via service for project: " + projectId);
                showErrorDialog("Failed to save properties. Check console/logs for details.");
                // Keep the dialog open if saving failed
            }

        } catch (Exception ex) {
            System.err.println("Error saving project metaproperties: " + ex.getMessage());
            ex.printStackTrace();
            showErrorDialog("Error saving properties: " + ex.getMessage());
        }
    }

    /**
     * Handles the cancel action. Closes the dialog without saving.
     */
    private void onCancel() {
        if (dialogHandler != null) {
             dialogHandler.closeDialog();
         } else {
             System.err.println("Warning: Dialog handler is null in onCancel.");
             // Optionally try to find the window from the panel if handler is missing
             Window window = SwingUtilities.getWindowAncestor(panel);
             if (window != null) {
                 window.dispose();
             }
         }
    }

    // --- Helper methods for showing dialogs ---

    private void showSuccessDialog(String message) {
         ApplicationManager.instance().getViewManager().showMessageDialog(
                 getParentComponent(), // Parent component
                 message, // Message
                 "Success", // Title
                 JOptionPane.INFORMATION_MESSAGE // Message type icon
         );
     }
 
     private void showErrorDialog(String message) {
         ApplicationManager.instance().getViewManager().showMessageDialog(
                 getParentComponent(), // Parent component
                 message, // Message
                 "Save Error", // Title
                 JOptionPane.ERROR_MESSAGE // Message type icon
         );
     }
 
     // Helper to get the parent component for dialogs
     private Component getParentComponent() {
         // Use the panel reference stored in the listener
         return panel;
     }

    // Removed placeholder parsing methods as they are now in ProjectMetapropertiesData
    // private MultilingualText parseMultilingualText(String input) { ... }
    // private List<MultilingualText> parseMultilingualTextList(String input) { ... }
    // private List<Resource> parseResourceList(String input) { ... }

}