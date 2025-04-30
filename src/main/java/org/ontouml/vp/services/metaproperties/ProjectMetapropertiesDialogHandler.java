package org.ontouml.vp.services.metaproperties;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;
import org.ontouml.ontouml4j.model.Project;

import javax.swing.*;
import java.awt.*;

public class ProjectMetapropertiesDialogHandler implements IDialogHandler {

    private final Project project;
    private final String projectId;
    private IDialog dialog;
    private final ViewManager viewManager;
    private boolean wasShown = false;
    private boolean wasClosed = false;
    private final Dimension dialogSize = new Dimension(600, 500);
    ProjectMetapropertiesPanelView panel;
    JScrollPane scrollPane;

    public ProjectMetapropertiesDialogHandler(Project project, String projectId) {
        if (project == null) {
            throw new IllegalArgumentException("OntoUML Project data cannot be null for the dialog.");
        }
        if (projectId == null || projectId.isEmpty()) {
            throw new IllegalArgumentException("Project ID cannot be null or empty for the dialog.");
        }
        this.project = project;
        this.projectId = projectId;
        this.viewManager = ApplicationManager.instance().getViewManager();
        getComponent();
    }

    public Project getProject() {
        return project;
    }

    public String getProjectId() {
        return projectId;
    }

    @Override
    public Component getComponent() {
        if (panel == null) {
            this.panel = new ProjectMetapropertiesPanelView(project, projectId, this);
            this.scrollPane = new JScrollPane(panel);
            // scrollPane.setSize(dialogSize);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        }
        return scrollPane;
    }

    @Override
    public void prepare(IDialog iDialog) {
        this.dialog = iDialog;
        dialog.setTitle("OntoUML Project Metaproperties (ID: " + projectId + ")");
        dialog.setModal(true);
        dialog.setResizable(true);
        dialog.setSize(dialogSize);
    }

    @Override
    public void shown() {
        panel.updateUI();
        System.out.println("Project Metaproperties dialog shown via handler for project: " + projectId);
    }

    @Override
    public boolean canClosed() {
        wasClosed = true;
        ProjectMetapropertiesController.setDialogClosed();
        return true;
    }

    public void showDialog() {
        if (!wasClosed && !wasShown) { 
            wasShown = true;
            viewManager.showDialog(this);
        } else if (wasClosed) {
             System.out.println("Dialog handler instance was already closed.");
        } else {
             System.out.println("Dialog handler instance was already shown.");
        }
    }

   public void closeDialog() {
       if (!wasClosed && wasShown) {
           wasClosed = true;
           ProjectMetapropertiesController.setDialogClosed(); // Also notify controller
           if (dialog != null) {
               dialog.close();
           }
       }
   }
}