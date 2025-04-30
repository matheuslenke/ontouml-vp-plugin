package org.ontouml.vp.services.metaproperties;

import org.ontouml.ontouml4j.model.MultilingualText;
import org.ontouml.ontouml4j.model.Project;
import org.ontouml.vp.utils.OntoUMLStringUtils;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class ProjectMetapropertiesPanelView extends JPanel {

    private final Project project;
    private final String projectId;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

    private JTextArea nameReadOnlyArea;
    private JTextArea alternativeNamesReadOnlyArea;
    private JTextArea descriptionReadOnlyArea;

    private JButton saveButton;
    private JButton cancelButton;

    private ProjectMetapropertiesDialogHandler dialogHandler;

    private ProjectMetapropertiesListener listener;

    public ProjectMetapropertiesPanelView(Project project, String projectId, ProjectMetapropertiesDialogHandler dialogHandler) {
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

    public ProjectMetapropertiesPanelView(Project project) {
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

        // Add descriptive label at the top
        JLabel infoLabel = new JLabel("Edit the metadata properties associated with this OntoUML project.");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(10, 5, 10, 5); // Add some padding
        add(infoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.insets = new Insets(3, 5, 3, 5); // Reset insets

        String createdDateStr = (project.getCreated() != null) ? DATE_FORMAT.format(project.getCreated()) : "";
        String modifiedDateStr = (project.getModified() != null) ? DATE_FORMAT.format(project.getModified()) : "";

        addField(gbc, "ID:", createReadOnlyTextField(project.getId()));

        addNameScrollPane(gbc);
        addAltNamesScrollPane(gbc);
        addDescriptionScrollPane(gbc);


        addFillerComponent(gbc);

        JPanel buttonPanel = addButtonPanel();

        gbc.gridx = 0;
        // gbc.gridy remains the same (last field row + 1)
        gbc.gridwidth = 2; // Span across both columns
        gbc.weightx = 1.0;
        gbc.weighty = 0;   // Button panel itself doesn't expand vertically
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally
        gbc.anchor = GridBagConstraints.PAGE_END; // Anchor to the bottom edge
        add(buttonPanel, gbc);
    }

    private void addFillerComponent(GridBagConstraints gbc) {
        JPanel filler = new JPanel();
        filler.setOpaque(false); // Make it invisible
        gbc.gridx = 0;
        gbc.gridy++; // Ensure it's below the last field
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0; // Takes up vertical space
        gbc.fill = GridBagConstraints.BOTH;
        add(filler, gbc);
    }

    private JPanel addButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Save");
        saveButton.setBackground(Color.BLUE);
        cancelButton = new JButton("Cancel");

        if (this.listener != null) {
            cancelButton.setActionCommand(ProjectMetapropertiesListener.CMD_CANCEL);
            saveButton.setActionCommand(ProjectMetapropertiesListener.CMD_SAVE);
            cancelButton.addActionListener(this.listener);
            saveButton.addActionListener(this.listener);
        } else {
             saveButton.setEnabled(false);
             cancelButton.setEnabled(false);
        }

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        return buttonPanel;
    }

    private void addAltNamesScrollPane(GridBagConstraints gbc) {
        List<Map<String, String>> alternativeNamesMaps = Optional.ofNullable(project.getAlternativeNames()).orElse(Collections.emptyList())
                .stream().map(MultilingualText::getMap).collect(Collectors.toList());
        JScrollPane altNamesScrollPane = createReadOnlyTextArea(OntoUMLStringUtils.formatMultiLanguageString(alternativeNamesMaps));
        alternativeNamesReadOnlyArea = (JTextArea) altNamesScrollPane.getViewport().getView();
        addListFieldWithEditButton(gbc, "Alternative Names:", altNamesScrollPane, this::editAlternativeNames);
    }

    private void addNameScrollPane(GridBagConstraints gbc) {
        Map<String, String> nameMap = project.getName() != null
                                             ? project.getName().getMap()
                                             : Collections.emptyMap();
        JScrollPane nameScrollPane = createReadOnlyTextArea(OntoUMLStringUtils.formatMultilingualTextMap(nameMap));
        nameReadOnlyArea = (JTextArea) nameScrollPane.getViewport().getView();
        addListFieldWithEditButton(gbc, "Name:", nameScrollPane, this::editName);
    }

    private void addDescriptionScrollPane(GridBagConstraints gbc) {
        Map<String, String> descriptionMap = project.getDescription() != null ? project.getDescription().getMap() : Collections.emptyMap();
        JScrollPane descriptionScrollPane = createReadOnlyTextArea(OntoUMLStringUtils.formatMultilingualTextMap(descriptionMap));
        descriptionReadOnlyArea = (JTextArea) descriptionScrollPane.getViewport().getView();
        addListFieldWithEditButton(gbc, "Description:", descriptionScrollPane, this::editName);
    }

    private void addListFieldWithEditButton(GridBagConstraints gbc, String labelText, JComponent displayComponent, Runnable editAction) {
        // Label
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        add(new JLabel(labelText), gbc);

        JPanel fieldPanel = new JPanel(new BorderLayout(1, 0));
        fieldPanel.add(displayComponent, BorderLayout.CENTER);

        JButton editButton = new JButton("Edit...");
        editButton.setMargin(new Insets(1, 1, 1, 1));
        editButton.addActionListener(e -> editAction.run());
        fieldPanel.add(editButton, BorderLayout.EAST);

        editButton.setEnabled(this.listener != null);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0;
        add(fieldPanel, gbc);

        gbc.gridy++;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
    }

    private Frame getParentFrame() {
        Component parent = this.getParent();
        while (parent != null && !(parent instanceof Frame)) {
            parent = parent.getParent();
        }
        return (Frame) parent;
    }

    private void editName() {
        MultilingualText currentName = project.getName() != null
                                        ? project.getName() : new MultilingualText();

        MultilingualText updatedName = MultiLanguageNameEditorDialog.showDialog(
                getParentFrame(),
                "Edit Project Name",
                currentName
        );

        if (updatedName != null) { 
             project.setName(updatedName);
             Map<String, String> updatedMap = updatedName.getMap();
             nameReadOnlyArea.setText(OntoUMLStringUtils.formatMultilingualTextMap(updatedMap));
             nameReadOnlyArea.setCaretPosition(0);
        }
    }

    private void editAlternativeNames() {
        List<MultilingualText> currentList = project.getAlternativeNames() != null
                                              ? new ArrayList<>(project.getAlternativeNames())
                                              : new ArrayList<>();
        
        List<MultilingualText> updatedList = MultiLanguageNameListEditorDialog.showDialog(
                getParentFrame(), 
                "Edit Alternative Names",
                currentList
        );

        if (updatedList != null) { 
            project.setAlternativeNames(updatedList);
            List<Map<String, String>> maps = updatedList.stream().map(MultilingualText::getMap).collect(Collectors.toList());
            alternativeNamesReadOnlyArea.setText(OntoUMLStringUtils.formatMultiLanguageString(maps));
            alternativeNamesReadOnlyArea.setCaretPosition(0);
        }
    }

    private void addField(GridBagConstraints gbc, String labelText, JComponent component) {
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        if (component instanceof JScrollPane) {
            gbc.fill = GridBagConstraints.BOTH;
        } else {
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weighty = 0;
        }
        add(component, gbc);

        gbc.gridy++;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
    }

    private JTextField createReadOnlyTextField(String text) {
        JTextField textField = new JTextField(text != null ? text : "");
        textField.setEditable(false);
        textField.setLayout(new FlowLayout(FlowLayout.CENTER));
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

    public void addSaveActionListener(java.awt.event.ActionListener listener) {
        saveButton.addActionListener(listener);
    }

    public String getProjectId() {
        return projectId;
    }

    public Project getUpdatedProject() {
        return project;
    }
} 