package org.ontouml.vp.views;

import org.ontouml.ontouml4j.model.MultilingualText;
import org.ontouml.ontouml4j.model.Project;
import org.ontouml.ontouml4j.model.utils.ProjectMetaProperties;
import org.ontouml.vp.controllers.ProjectMetapropertiesDialogHandler;
import org.ontouml.vp.listeners.ProjectMetapropertiesListener;
import org.ontouml.vp.utils.OntoUMLStringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.net.URI;
import java.util.ArrayList;

public class ProjectMetapropertiesPanel extends JPanel {

    private final Project project;
    private final String projectId;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

    // Input fields - Updated
    private JTextArea nameReadOnlyArea; // Added: Read-only display for name
    private JButton editNameButton; // Added: Button to open name editor dialog
    private JTextArea alternativeNamesReadOnlyArea; // Read-only display
    private JButton editAlternativeNamesButton;
    private MultilingualInputComponent descriptionInputComponent;
    private JTextArea editorialNotesReadOnlyArea; // Read-only display
    private JButton editEditorialNotesButton;
    // Keep old text areas for now for non-multilingual fields that need editing
    private JTextArea creatorsTextArea;
    private JTextArea contributorsTextArea;

    // Buttons
    private JButton saveButton;
    private JButton cancelButton;

    private ProjectMetapropertiesDialogHandler dialogHandler;

    private ProjectMetapropertiesListener listener;

    public ProjectMetapropertiesPanel(Project project, String projectId, ProjectMetapropertiesDialogHandler dialogHandler) {
        super(new GridBagLayout());
        if (projectId == null || projectId.isEmpty()) {
            throw new IllegalArgumentException("Project ID cannot be null or empty for the panel.");
        }
        this.project = project;
        this.projectId = projectId;
        this.dialogHandler = dialogHandler;
        this.listener = new ProjectMetapropertiesListener(this.dialogHandler, project, this);
        initializeComponents();
    }

    public ProjectMetapropertiesPanel(Project project) {
        super(new GridBagLayout());
        this.project = project;
        this.projectId = project != null ? project.getId() : null;
        if (this.projectId == null || this.projectId.isEmpty()){
            System.err.println("Warning: ProjectMetapropertiesPanel created without a valid projectId.");
        }
        initializeComponents();
    }

    private void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // Add descriptive label at the top
        JLabel infoLabel = new JLabel("<html>Edit the metadata properties associated with this OntoUML project. Multilingual fields show one language at a time; use the dropdown or 'Edit...' button.</html>");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across columns
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(10, 5, 10, 5); // Add some padding
        add(infoLabel, gbc);

        // Reset constraints for subsequent components
        gbc.gridwidth = 1;
        gbc.gridy++; // Move to the next row
        gbc.insets = new Insets(3, 5, 3, 5); // Reset insets

        // Format Date objects correctly
        String createdDateStr = (project.getCreated() != null) ? DATE_FORMAT.format(project.getCreated()) : "N/A";
        // Correct handling assuming getModified() returns Date, not Optional<Date> - Assuming it returns Date
        String modifiedDateStr = (project.getModified() != null) ? DATE_FORMAT.format(project.getModified()) : "N/A";

        addField(gbc, "ID:", createReadOnlyTextField(project.getId()));

        // Name (Multilingual) - Replaced with read-only area and edit button
        Map<String, String> nameMap = project.getName() != null
                                             ? project.getName().getMap()
                                             : Collections.emptyMap();
        JScrollPane nameScrollPane = createReadOnlyTextArea(OntoUMLStringUtils.formatMultilingualTextMap(nameMap)); // Using a new utility method
        nameReadOnlyArea = (JTextArea) nameScrollPane.getViewport().getView();
        addListFieldWithEditButton(gbc, "Name:", nameScrollPane, this::editName); // Added

        // Alternative Names (List<MultilingualText>) - Read-only display + Edit button
        List<Map<String, String>> alternativeNamesMaps = Optional.ofNullable(project.getAlternativeNames()).orElse(Collections.emptyList())
                .stream().map(MultilingualText::getMap).collect(Collectors.toList());
        JScrollPane altNamesScrollPane = createReadOnlyTextArea(OntoUMLStringUtils.formatMultiLanguageString(alternativeNamesMaps));
        alternativeNamesReadOnlyArea = (JTextArea) altNamesScrollPane.getViewport().getView();
        addListFieldWithEditButton(gbc, "Alternative Names:", altNamesScrollPane, this::editAlternativeNames);

        // Description (Multilingual)
        descriptionInputComponent = new MultilingualInputComponent(
                project.getDescription(), // Pass potentially null MultilingualText
                 true); // true = JTextArea
        addField(gbc, "Description:", descriptionInputComponent);

        // Editorial Notes (List<MultilingualText>) - Read-only display + Edit button
        List<Map<String, String>> editorialNotesMaps = Optional.ofNullable(project.getEditorialNotes()).orElse(Collections.emptyList())
                .stream().map(MultilingualText::getMap).collect(Collectors.toList());
        JScrollPane editorialNotesScrollPane = createReadOnlyTextArea(OntoUMLStringUtils.formatMultiLanguageString(editorialNotesMaps));
        editorialNotesReadOnlyArea = (JTextArea) editorialNotesScrollPane.getViewport().getView();
        addListFieldWithEditButton(gbc, "Editorial Notes:", editorialNotesScrollPane, this::editEditorialNotes);

        addField(gbc, "Created:", createReadOnlyTextField(createdDateStr));
        addField(gbc, "Modified:", createReadOnlyTextField(modifiedDateStr));

        // Creators/Contributors (Still editable text areas for now - assuming Resource format is simple)
        // If Resource parsing/formatting becomes complex, these might also need dedicated editors.
        JScrollPane creatorsScrollPane = createTextArea(OntoUMLStringUtils.formatResources(Optional.ofNullable(project.getCreators()).orElse(Collections.emptyList())));
        creatorsTextArea = (JTextArea) creatorsScrollPane.getViewport().getView();
        addField(gbc, "Creators:", creatorsScrollPane);

        JScrollPane contributorsScrollPane = createTextArea(OntoUMLStringUtils.formatResources(Optional.ofNullable(project.getContributors()).orElse(Collections.emptyList())));
        contributorsTextArea = (JTextArea) contributorsScrollPane.getViewport().getView();
        addField(gbc, "Contributors:", contributorsScrollPane);

        // Read-only fields (MetaProperties)
        ProjectMetaProperties metaProperties = project.getMetaProperties();
        // Add the previously commented-out or missing read-only fields
        addField(gbc, "Publisher:", createReadOnlyTextField(OntoUMLStringUtils.formatResource(Optional.ofNullable(metaProperties.getPublisher()))));
        addField(gbc, "Designed For Tasks:", createReadOnlyTextArea(OntoUMLStringUtils.formatResources(Optional.ofNullable(metaProperties.getDesignedForTasks()).orElse(Collections.emptyList()))));
        addField(gbc, "License:", createReadOnlyTextField(OntoUMLStringUtils.formatResource(Optional.ofNullable(metaProperties.getLicense()))));
        addField(gbc, "Access Rights:", createReadOnlyTextArea(OntoUMLStringUtils.formatResources(Optional.ofNullable(metaProperties.getAccessRights()).orElse(Collections.emptyList()))));
        addField(gbc, "Themes:", createReadOnlyTextArea(OntoUMLStringUtils.formatResources(Optional.ofNullable(metaProperties.getThemes()).orElse(Collections.emptyList()))));
        addField(gbc, "Contexts:", createReadOnlyTextArea(OntoUMLStringUtils.formatResources(Optional.ofNullable(metaProperties.getContexts()).orElse(Collections.emptyList()))));
        addField(gbc, "Ontology Types:", createReadOnlyTextArea(OntoUMLStringUtils.formatResources(Optional.ofNullable(metaProperties.getOntologyTypes()).orElse(Collections.emptyList()))));
        addField(gbc, "Representation Style:", createReadOnlyTextField(OntoUMLStringUtils.formatResource(Optional.ofNullable(metaProperties.getRepresentationStyle()))));
        addField(gbc, "Namespace:", createReadOnlyTextField(Optional.ofNullable(metaProperties.getNamespace()).map(URI::toString).orElse("N/A")));
        addField(gbc, "Landing Pages:", createReadOnlyTextArea(OntoUMLStringUtils.formatStringList(
                Optional.ofNullable(metaProperties.getLandingPages()).orElse(Collections.emptyList())
                        .stream().map(URI::toString).collect(Collectors.toList())
        )));
        addField(gbc, "Sources:", createReadOnlyTextArea(OntoUMLStringUtils.formatStringList(
                Optional.ofNullable(metaProperties.getSources()).orElse(Collections.emptyList())
                        .stream().map(URI::toString).collect(Collectors.toList())
        )));
        addField(gbc, "Bibliographic Citations:", createReadOnlyTextArea(OntoUMLStringUtils.formatMultiLanguageString(
                Optional.ofNullable(metaProperties.getBibliographicCitations()).orElse(Collections.emptyList())
                        .stream().map(MultilingualText::getMap).collect(Collectors.toList())
        )));
        addField(gbc, "Keywords:", createReadOnlyTextArea(OntoUMLStringUtils.formatMultiLanguageString(
                Optional.ofNullable(metaProperties.getKeywords()).orElse(Collections.emptyList())
                        .stream().map(MultilingualText::getMap).collect(Collectors.toList())
        )));
        addField(gbc, "Acronyms:", createReadOnlyTextArea(OntoUMLStringUtils.formatStringList(Optional.ofNullable(metaProperties.getAcronyms()).orElse(Collections.emptyList()))));
        addField(gbc, "Languages:", createReadOnlyTextArea(OntoUMLStringUtils.formatStringList(Optional.ofNullable(metaProperties.getLanguages()).orElse(Collections.emptyList()))));

        // Filler component to push buttons down
        JPanel filler = new JPanel();
        filler.setOpaque(false); // Make it invisible
        gbc.gridx = 0;
        gbc.gridy++; // Ensure it's below the last field
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0; // Takes up vertical space
        gbc.fill = GridBagConstraints.BOTH;
        add(filler, gbc);

        // Reset gridy for the button panel row
        // gbc.gridy++; // This was incremented for the filler, no need to increment again

        // Add Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        // Only add listeners if a listener object exists (set in constructor)
        if (this.listener != null) {
            cancelButton.setActionCommand(ProjectMetapropertiesListener.CMD_CANCEL); // Use command for clarity
            saveButton.setActionCommand(ProjectMetapropertiesListener.CMD_SAVE);     // Use command for clarity
            cancelButton.addActionListener(this.listener);
            saveButton.addActionListener(this.listener);
        } else {
             // Disable buttons if no listener is available (e.g., read-only view)
             saveButton.setEnabled(false);
             cancelButton.setEnabled(false); // Or maybe change Cancel to "Close"?
        }

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        // gbc.gridy remains the same (last field row + 1)
        gbc.gridwidth = 2; // Span across both columns
        gbc.weightx = 1.0;
        gbc.weighty = 0;   // Button panel itself doesn't expand vertically
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally
        gbc.anchor = GridBagConstraints.PAGE_END; // Anchor to the bottom edge
        add(buttonPanel, gbc);
    }

    // New method to handle adding a field with an associated "Edit..." button
    private void addListFieldWithEditButton(GridBagConstraints gbc, String labelText, JComponent displayComponent, Runnable editAction) {
        // Label
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        add(new JLabel(labelText), gbc);

        // Main component (read-only text area/scroll pane) + Edit Button
        JPanel fieldPanel = new JPanel(new BorderLayout(1, 0)); // Panel to hold display area and button
        fieldPanel.add(displayComponent, BorderLayout.CENTER);

        JButton editButton = new JButton("Edit...");
        editButton.setMargin(new Insets(1, 1, 1, 1)); // Make button smaller
        editButton.addActionListener(e -> editAction.run());
        fieldPanel.add(editButton, BorderLayout.EAST);
        // Disable button if no listener is present (panel is read-only)
        editButton.setEnabled(this.listener != null);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH; // Fill both horizontal and vertical
        gbc.weighty = 0; // Don't take extra vertical space
        add(fieldPanel, gbc);

        gbc.gridy++;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Reset fill for next row
    }

    // --- Helper: Get the top-level frame for dialog parenting ---
    private Frame getParentFrame() {
        Component parent = this.getParent();
        while (parent != null && !(parent instanceof Frame)) {
            parent = parent.getParent();
        }
        return (Frame) parent;
    }

    // Method to show the dialog for Editing Name
    private void editName() {
        MultilingualText currentName = project.getName() != null
                                        ? project.getName() : new MultilingualText(); // Use existing or create new

        // Replace placeholder with call to the actual dialog
        MultilingualText updatedName = MultiLanguageNameEditorDialog.showDialog(
                getParentFrame(), // Use helper to find parent Frame
                "Edit Project Name",
                currentName
        );

        // After the dialog is implemented and returns the updated MultilingualText:
        if (updatedName != null) { // Check if dialog was saved, not cancelled
             project.setName(updatedName);
             // Update the read-only display
             // TODO: Ensure OntoUMLStringUtils.formatMultilingualTextMap exists and works
             Map<String, String> updatedMap = updatedName.getMap();
             nameReadOnlyArea.setText(OntoUMLStringUtils.formatMultilingualTextMap(updatedMap));
             nameReadOnlyArea.setCaretPosition(0);
        }
    }

    // Method to show the dialog for Alternative Names
    private void editAlternativeNames() {
        List<MultilingualText> currentList = project.getAlternativeNames() != null
                                              ? project.getAlternativeNames() : new ArrayList<>();
        List<MultilingualText> updatedList = MultiLanguageListEditorDialog.showDialog(
                getParentFrame(), // Use helper to find parent Frame
                "Edit Alternative Names",
                currentList
        );

        if (updatedList != null) { // Dialog was not cancelled
            project.setAlternativeNames(updatedList);
            // Update the read-only display
            List<Map<String, String>> maps = updatedList.stream().map(MultilingualText::getMap).collect(Collectors.toList());
            alternativeNamesReadOnlyArea.setText(OntoUMLStringUtils.formatMultiLanguageString(maps));
            alternativeNamesReadOnlyArea.setCaretPosition(0);
        }
    }

    // Method to show the dialog for Editorial Notes
    private void editEditorialNotes() {
        List<MultilingualText> currentList = project.getEditorialNotes() != null
                                              ? project.getEditorialNotes() : new ArrayList<>();
        List<MultilingualText> updatedList = MultiLanguageListEditorDialog.showDialog(
                 getParentFrame(), // Use helper to find parent Frame
                "Edit Editorial Notes",
                currentList
        );

        if (updatedList != null) { // Dialog was not cancelled
            project.setEditorialNotes(updatedList);
            // Update the read-only display
            List<Map<String, String>> maps = updatedList.stream().map(MultilingualText::getMap).collect(Collectors.toList());
            editorialNotesReadOnlyArea.setText(OntoUMLStringUtils.formatMultiLanguageString(maps));
            editorialNotesReadOnlyArea.setCaretPosition(0);
        }
    }

    // Helper to add Label + Component pair (Unchanged from before, used for simple fields)
    private void addField(GridBagConstraints gbc, String labelText, JComponent component) {
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        // Adjust fill/weighty based on component type for better layout
        if (component instanceof JScrollPane || component instanceof MultilingualInputComponent) {
            // Let text areas and custom components fill available space
            gbc.fill = GridBagConstraints.BOTH;
            // Give text areas a slight vertical weight if needed, or keep 0 if filler handles it
            gbc.weighty = (component instanceof JScrollPane && ((JScrollPane) component).getViewport().getView() instanceof JTextArea) ? 0.1 : 0;
        } else {
            // Standard text fields just fill horizontally
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weighty = 0;
        }
        add(component, gbc);

        gbc.gridy++;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
    }

    private JTextField createReadOnlyTextField(String text) {
        JTextField textField = new JTextField(text != null ? text : "N/A");
        textField.setEditable(false);
        textField.setCaretPosition(0);
        textField.setBorder(BorderFactory.createEtchedBorder());
        return textField;
    }

    private JTextField createTextField(String text) {
        JTextField textField = new JTextField(text != null ? text : "");
        textField.setEditable(true);
        textField.setCaretPosition(0);
        textField.setBorder(BorderFactory.createEtchedBorder());
        return textField;
    }

    private JScrollPane createReadOnlyTextArea(String text) {
        JTextArea textArea = new JTextArea(text != null ? text : "");
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setCaretPosition(0);
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setRows(1);
        scrollPane.setPreferredSize(new Dimension(100, 60));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    private JScrollPane createTextArea(String text) {
        JTextArea textArea = new JTextArea(text != null ? text : "");
        textArea.setEditable(true);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setRows(1);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    // Methods to add action listeners (Keep for potential external use, though internal buttons set listeners)
    public void addSaveActionListener(java.awt.event.ActionListener listener) {
        saveButton.addActionListener(listener);
    }

    // Remove old input getters
    // public String getNameInput() { ... }
    // public String getAlternativeNamesInput() { ... }
    // public String getDescriptionInput() { ... }
    // public String getEditorialNotesInput() { ... }

    // Add getters for the new components if needed by the listener/handler
    public MultilingualInputComponent getDescriptionInputComponent() {
        return descriptionInputComponent;
    }

    public String getCreatorsInput() {
        return creatorsTextArea.getText();
    }

    public String getContributorsInput() {
        return contributorsTextArea.getText();
    }

    public String getProjectId() {
        return projectId;
    }

    // Method for the listener to retrieve the updated project object
    // Note: The project object is modified directly by the dialogs and components
    public Project getUpdatedProject() {
        // Ensure latest data from MultilingualInputComponents is saved to the project object
        if (descriptionInputComponent != null) {
            project.setDescription(descriptionInputComponent.getData());
        }
        // Creators/Contributors are still plain text, need parsing logic on save
        // TODO: Implement parsing for creators/contributors in the save listener
        return project;
    }
} 