package org.ontouml.vp.services.metaproperties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ontouml.ontouml4j.model.MultilingualText;
import org.ontouml.ontouml4j.model.Project;
import org.ontouml.vp.utils.OntoUMLStringUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import javax.swing.JSplitPane;
import javax.swing.JTree;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IModel;
import com.vp.plugin.model.factory.IModelElementFactory;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;

public class ProjectMetapropertiesPanelView extends JPanel implements TreeSelectionListener {

    private final Project project;
    private final String projectId;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TAG_ALTERNATIVE_NAMES = "ontouml.metaproperties.alternativeNames";
    // Add other metaproperty tag constants here as needed e.g.
    // private static final String TAG_DESCRIPTION = "ontouml.metaproperties.description";

    private JTextArea nameReadOnlyArea;
    private JTextArea alternativeNamesReadOnlyArea;
    private JTextArea descriptionReadOnlyArea;

    private JButton saveButton;
    private JButton cancelButton;

    private ProjectMetapropertiesDialogHandler dialogHandler;

    private ProjectMetapropertiesListener listener;

    private JSplitPane splitPane;
    private JTree sidebarTree;
    private JPanel contentPanel;
    private JPanel projectDetailsPanel;
    private JPanel classesPanelPlaceholder;
    private JPanel associationsPanelPlaceholder;
    private JPanel classDetailsPanel; // New panel for class metaproperties
    private JTextArea classAlternativeNamesReadOnlyArea; // For class alternative names

    private IClass currentSelectedClass; // To store the currently selected class

    public static class SidebarItem {
        final Object element;
        final String displayName;

        SidebarItem(Object element, String displayName) {
            this.element = element;
            this.displayName = displayName;
        }

        public Object getElement() {
            return element;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public ProjectMetapropertiesPanelView(Project project, String projectId, ProjectMetapropertiesDialogHandler dialogHandler) {
        super(new BorderLayout());
        if (projectId == null || projectId.isEmpty()) {
            throw new IllegalArgumentException("Project ID cannot be null or empty for the panel.");
        }
        this.project = project;
        this.projectId = projectId;
        this.dialogHandler = dialogHandler;
        this.listener = new ProjectMetapropertiesListener(this.dialogHandler, project, this);
        initializeView();
    }

    public ProjectMetapropertiesPanelView(Project project) {
        super(new BorderLayout());
        this.project = project;
        this.projectId = project != null ? project.getId() : null;
        if (this.projectId == null || this.projectId.isEmpty()){
            // Consider not throwing an error but logging, as project might be new and not yet saved
            System.err.println("Warning: ProjectMetapropertiesPanel created without a valid projectId. Metaproperties might not save correctly to OntoUML model.");
        }
        initializeView();
    }

    private void initializeView() {
        projectDetailsPanel = createProjectDetailsPanel();
        // Placeholders are useful for features not yet implemented
        classesPanelPlaceholder = createPlaceholderPanel("Select a class to view/edit its metaproperties.");
        associationsPanelPlaceholder = createPlaceholderPanel("Association Metaproperties (Not Implemented)");

        contentPanel = projectDetailsPanel; // Default view

        JScrollPane sidebarScrollPane = createSidebar();

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebarScrollPane, contentPanel);
        splitPane.setDividerLocation(250);

        add(splitPane, BorderLayout.CENTER);

        // Add listener and set initial selection AFTER splitPane is initialized
        if (sidebarTree != null) { // Ensure sidebarTree was initialized
            sidebarTree.addTreeSelectionListener(this);
            
            DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) sidebarTree.getModel().getRoot();
            DefaultMutableTreeNode nodeToSelect = null;

            if (rootNode != null && rootNode.getChildCount() > 0) {
                // Assuming "Project Metaproperties" is the first child of the root based on createSidebar logic.
                DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) rootNode.getChildAt(0);
                if (firstChild.getUserObject() instanceof SidebarItem) {
                    SidebarItem item = (SidebarItem) firstChild.getUserObject();
                    if ("Project".equals(item.getElement()) && "Project Metaproperties".equals(item.getElement())) {
                        nodeToSelect = firstChild;
                    }
                }
            }
            
            if (nodeToSelect != null) {
                sidebarTree.expandPath(new TreePath(rootNode.getPath()));
                sidebarTree.setSelectionPath(new TreePath(nodeToSelect.getPath()));
            } else if (rootNode != null) { // Fallback to selecting the root itself
                 sidebarTree.setSelectionPath(new TreePath(rootNode.getPath()));
            }
        }
    }

    private String getPackageName(IModelElement element) {
        if (element == null) return "<Unknown>";
        IModelElement parent = element.getParent();
        while (parent != null) {
            if (parent instanceof IPackage) {
                return parent.getName();
            }
            if (parent instanceof IModel) { // IModel can be a root container
                return parent.getName(); // Or a more specific name like "<Project Root>"
            }
            parent = parent.getParent();
        }
        return "<No Package>";
    }

    private void buildPackageTreeNodes(IModelElement currentVpContainer, DefaultMutableTreeNode currentTreeNode, Map<String, DefaultMutableTreeNode> containerNodes) {
        // Process IPackage children
        IModelElement[] packageChildren = currentVpContainer.toChildArray(IModelElementFactory.MODEL_TYPE_PACKAGE);
        if (packageChildren != null) {
            for (IModelElement childPkg : packageChildren) {
                if (childPkg instanceof IPackage) {
                    DefaultMutableTreeNode pkgTreeNode = new DefaultMutableTreeNode(new SidebarItem(childPkg, childPkg.getName()));
                    currentTreeNode.add(pkgTreeNode);
                    containerNodes.put(childPkg.getId(), pkgTreeNode);
                    buildPackageTreeNodes(childPkg, pkgTreeNode, containerNodes); // Recurse
                }
            }
        }

        // Process IModel children (if they can be nested and act as packages/containers)
        IModelElement[] modelChildren = currentVpContainer.toChildArray(IModelElementFactory.MODEL_TYPE_MODEL);
        if (modelChildren != null) {
            for (IModelElement childModel : modelChildren) {
                DefaultMutableTreeNode modelTreeNode = new DefaultMutableTreeNode(new SidebarItem(childModel, childModel.getName() + " (Model)"));
                currentTreeNode.add(modelTreeNode);
                containerNodes.put(childModel.getId(), modelTreeNode);
                buildPackageTreeNodes(childModel, modelTreeNode, containerNodes); // Recurse
            }
        }
    }

    private JScrollPane createSidebar() {
        IProject vpProject = ApplicationManager.instance().getProjectManager().getProject();
        String rootNodeName = (vpProject != null && vpProject.getName() != null && !vpProject.getName().isEmpty()) ? vpProject.getName() : "Project";
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootNodeName);
        
        SidebarItem projectSidebarItem = new SidebarItem("Project", "Project Metaproperties");
        DefaultMutableTreeNode projectMetapropertiesNode = new DefaultMutableTreeNode(projectSidebarItem);
        rootNode.add(projectMetapropertiesNode);

        Map<String, DefaultMutableTreeNode> containerNodes = new HashMap<>();

        if (vpProject != null) {
            containerNodes.put(vpProject.getId(), rootNode); 

            IModelElement[] models = vpProject.toModelElementArray();
            if (models != null && models.length > 0) {
                IModelElement primaryModel = models[0]; 
                // If the primary model's name is different from the project name, we might want to adjust the rootNode or add a sub-node for the model.
                // For now, rootNode represents the project, and primaryModel's contents go under it.
                containerNodes.put(primaryModel.getId(), rootNode);
                buildPackageTreeNodes(primaryModel, rootNode, containerNodes);
            }

            IModelElement[] classes = vpProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_CLASS);
            if (classes != null) {
                for (IModelElement modelElement : classes) {
                    if (modelElement instanceof IClass) {
                        IClass cls = (IClass) modelElement;
                        String displayName = cls.getName() != null && !cls.getName().isEmpty() ? cls.getName() : "(Unnamed Class)";
                        DefaultMutableTreeNode classTreeNode = new DefaultMutableTreeNode(new SidebarItem(cls, displayName));
                        IModelElement parentContainer = cls.getParent();
                        if (parentContainer == null) {
                            System.err.println("Warning: Class '" + displayName + "' has no parent container. Adding to root.");
                            rootNode.add(classTreeNode);
                            continue;
                        }
                        DefaultMutableTreeNode parentTreeNode = containerNodes.get(parentContainer.getId());
                        if (parentTreeNode != null) {
                            parentTreeNode.add(classTreeNode);
                        } else {
                            System.err.println("Warning: Parent container for class '" + displayName + "' not found in container map. Adding to root.");
                            rootNode.add(classTreeNode); 
                        }
                    }
                }
            }

            IModelElement[] associations = vpProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_ASSOCIATION);
            if (associations != null) {
                for (IModelElement modelElement : associations) {
                    if (modelElement instanceof IAssociation) {
                        IAssociation assoc = (IAssociation) modelElement;
                        IModelElement fromEnd = assoc.getFrom();
                        IModelElement toEnd = assoc.getTo();
                        String fromName = (fromEnd instanceof IClass && ((IClass) fromEnd).getName() != null) ? ((IClass) fromEnd).getName() : "(Unknown)";
                        String toName = (toEnd instanceof IClass && ((IClass) toEnd).getName() != null) ? ((IClass) toEnd).getName() : "(Unknown)";
                        String displayName = String.format("(%s -> %s)", fromName, toName);
                        
                        String assocName = assoc.getName();
                        if (assocName != null && !assocName.trim().isEmpty()) {
                            displayName = assocName + " " + displayName;
                        }

                        DefaultMutableTreeNode assocTreeNode = new DefaultMutableTreeNode(new SidebarItem(assoc, displayName));
                        IModelElement parentContainer = assoc.getParent();
                         if (parentContainer == null) { 
                            System.err.println("Warning: Association '" + displayName + "' has no parent container. Adding to root.");
                            rootNode.add(assocTreeNode); 
                            continue;
                        }
                        DefaultMutableTreeNode parentTreeNode = containerNodes.get(parentContainer.getId());
                        if (parentTreeNode != null) {
                            parentTreeNode.add(assocTreeNode);
                        } else {
                            System.err.println("Warning: Parent container for association '" + displayName + "' not found in container map. Adding to root.");
                            rootNode.add(assocTreeNode); 
                        }
                    }
                }
            }
        }

        this.sidebarTree = new JTree(); // Assign to class field, JTree instance created here
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
        this.sidebarTree.setModel(treeModel);
        this.sidebarTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.sidebarTree.setCellRenderer(new SidebarTreeCellRenderer()); // Apply the custom renderer

        // Listener and initial selection are now handled in initializeView after splitPane is ready.

        return new JScrollPane(this.sidebarTree);
    }

    private JPanel createPlaceholderPanel(String text) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(new JLabel(text), gbc);
        return panel;
    }
    
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) sidebarTree.getLastSelectedPathComponent();

        if (selectedNode == null) {
            return;
        }

        Object userObject = selectedNode.getUserObject();

        if (userObject instanceof SidebarItem) {
            SidebarItem selectedItem = (SidebarItem) userObject;
            Object element = selectedItem.getElement();
            currentSelectedClass = null; // Reset selected class

            if (element instanceof String && "Project".equals(element)) {
                contentPanel = projectDetailsPanel;
            } else if (element instanceof IClass) {
                currentSelectedClass = (IClass) element;
                classDetailsPanel = createClassDetailsPanel(currentSelectedClass);
                contentPanel = classDetailsPanel;
                 // Update placeholder text dynamically for now, or switch to a real editor panel
                // String className = ((IClass) element).getName();
                // className = (className == null || className.isEmpty()) ? "(Unnamed Class)" : className;
                // ((JLabel) classesPanelPlaceholder.getComponent(0)).setText("Metaproperties for Class: " + className);
            } else if (element instanceof IAssociation) {
                contentPanel = associationsPanelPlaceholder; // Keep placeholder for now
                String assocName = ((IAssociation) element).getName();
                String displayName;
                if (selectedItem.displayName.startsWith("(")) { // Heuristic to check if it's already formatted
                    displayName = selectedItem.displayName;
                } else {
                    assocName = (assocName == null || assocName.isEmpty()) ? "(Unnamed Association)" : assocName;
                    displayName = assocName;
                }
                ((JLabel) associationsPanelPlaceholder.getComponent(0)).setText("Metaproperties for Association: " + displayName);
            } else if (element instanceof IPackage || element instanceof IModel) {
                 return; 
            } else {
                contentPanel = projectDetailsPanel; 
            }
            splitPane.setRightComponent(contentPanel);
            splitPane.revalidate();
            splitPane.repaint();
        }
    }

    private JPanel createProjectDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
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
        panel.add(infoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.insets = new Insets(3, 5, 3, 5); // Reset insets

        String createdDateStr = (project.getCreated() != null) ? DATE_FORMAT.format(project.getCreated()) : "";
        String modifiedDateStr = (project.getModified() != null) ? DATE_FORMAT.format(project.getModified()) : "";

        addField(panel, gbc, "ID:", createReadOnlyTextField(project.getId()));

        addNameScrollPane(panel, gbc);
        addAltNamesScrollPane(panel, gbc);
        addDescriptionScrollPane(panel, gbc);


        addFillerComponent(panel, gbc);

        JPanel buttonPanel = addButtonPanel();

        gbc.gridx = 0;
        // gbc.gridy remains the same (last field row + 1)
        gbc.gridwidth = 2; // Span across both columns
        gbc.weightx = 1.0;
        gbc.weighty = 0;   // Button panel itself doesn't expand vertically
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally
        gbc.anchor = GridBagConstraints.PAGE_END; // Anchor to the bottom edge
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private void addFillerComponent(JPanel panel, GridBagConstraints gbc) {
        JPanel filler = new JPanel();
        filler.setOpaque(false); // Make it invisible
        gbc.gridx = 0;
        gbc.gridy++; // Ensure it's below the last field
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0; // Takes up vertical space
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(filler, gbc);
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

    private void addAltNamesScrollPane(JPanel panel, GridBagConstraints gbc) {
        List<Map<String, String>> alternativeNamesMaps = Optional.ofNullable(project.getAlternativeNames()).orElse(Collections.emptyList())
                .stream().map(MultilingualText::getMap).collect(Collectors.toList());
        JScrollPane altNamesScrollPane = createReadOnlyTextArea(OntoUMLStringUtils.formatMultiLanguageString(alternativeNamesMaps));
        alternativeNamesReadOnlyArea = (JTextArea) altNamesScrollPane.getViewport().getView();
        addListFieldWithEditButton(panel, gbc, "Alternative Names:", altNamesScrollPane, this::editAlternativeNames);
    }

    private void addNameScrollPane(JPanel panel, GridBagConstraints gbc) {
        Map<String, String> nameMap = project.getName() != null
                                             ? project.getName().getMap()
                                             : Collections.emptyMap();
        JScrollPane nameScrollPane = createReadOnlyTextArea(OntoUMLStringUtils.formatMultilingualTextMap(nameMap));
        nameReadOnlyArea = (JTextArea) nameScrollPane.getViewport().getView();
        addListFieldWithEditButton(panel, gbc, "Name:", nameScrollPane, this::editName);
    }

    private void addDescriptionScrollPane(JPanel panel, GridBagConstraints gbc) {
        Map<String, String> descriptionMap = project.getDescription() != null ? project.getDescription().getMap() : Collections.emptyMap();
        JScrollPane descriptionScrollPane = createReadOnlyTextArea(OntoUMLStringUtils.formatMultilingualTextMap(descriptionMap));
        descriptionReadOnlyArea = (JTextArea) descriptionScrollPane.getViewport().getView();
        addListFieldWithEditButton(panel, gbc, "Description:", descriptionScrollPane, this::editDescription);
    }

    private void editDescription() {
        MultilingualText currentDescription = project.getDescription() != null
                ? project.getDescription() : new MultilingualText();

        MultilingualText updatedDescription = MultiLanguageNameEditorDialog.showDialog(
                getParentFrame(),
                "Edit Project Description",
                currentDescription
        );

        if (updatedDescription != null) {
            project.setDescription(updatedDescription);
            Map<String, String> updatedMap = updatedDescription.getMap();
            descriptionReadOnlyArea.setText(OntoUMLStringUtils.formatMultilingualTextMap(updatedMap));
            descriptionReadOnlyArea.setCaretPosition(0);
        }
    }

    private void addListFieldWithEditButton(JPanel panel, GridBagConstraints gbc, String labelText, JComponent displayComponent, Runnable editAction) {
        // Label
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(labelText), gbc);

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
        panel.add(fieldPanel, gbc);

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
        
        List<MultilingualText> updatedList = StructuredMultilingualTextListEditorDialog.showDialog(
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

    private void addField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent component) {
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        if (component instanceof JScrollPane) {
            gbc.fill = GridBagConstraints.BOTH;
        } else {
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weighty = 0;
        }
        panel.add(component, gbc);

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

    private JPanel createClassDetailsPanel(IClass selectedClass) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int yPos = 0;

        // Label for the selected class
        gbc.gridx = 0;
        gbc.gridy = yPos++;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        JLabel classLabel = new JLabel("Editing Metaproperties for Class: " + selectedClass.getName());
        classLabel.setFont(classLabel.getFont().deriveFont(Font.BOLD));
        panel.add(classLabel, gbc);
        gbc.gridwidth = 1; // Reset gridwidth
        gbc.weightx = 0; // Reset weightx for labels

        // Class ID (read-only)
        gbc.gridx = 0;
        gbc.gridy = yPos;
        gbc.weightx = 0; // Label column
        panel.add(new JLabel("ID:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0; // Field column
        JTextField idField = createReadOnlyTextField(selectedClass.getId());
        panel.add(idField, gbc);
        yPos++;

        // Alternative Names
        List<MultilingualText> altNames = getClassAlternativeNames(selectedClass);
        List<Map<String, String>> altNamesMaps = altNames.stream()
                .map(MultilingualText::getMap)
                .collect(Collectors.toList());
        JScrollPane altNamesScrollPane = createReadOnlyTextArea(OntoUMLStringUtils.formatMultiLanguageString(altNamesMaps));
        classAlternativeNamesReadOnlyArea = (JTextArea) altNamesScrollPane.getViewport().getView();
        
        gbc.gridy = yPos; // yPos will be incremented by addListFieldWithEditButton
        addListFieldWithEditButton(panel, gbc, "Alternative Names:", altNamesScrollPane, this::editClassAlternativeNames);
        // yPos will be incremented within addListFieldWithEditButton if it modifies gbc.gridy directly or returns new yPos
        // For now, assume addListFieldWithEditButton handles its own yPos increment or sets gbc.gridy correctly for next element.
        // Let's manually increment yPos assuming addListFieldWithEditButton uses one row.
        yPos++; // Increment yPos after adding the alternative names field.


        // Placeholder for other class metaproperties
        gbc.gridx = 0;
        gbc.gridy = yPos++;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0; // Fill remaining space
        gbc.fill = GridBagConstraints.BOTH;
        JPanel filler = new JPanel();
        filler.setOpaque(false);
        panel.add(filler, gbc);
        
        // Note: Save/Cancel buttons for class metaproperties might be different
        // from project-level ones or could be handled globally.
        // For now, this panel doesn't have its own save/cancel.

        return panel;
    }
    
    private void editClassAlternativeNames() {
        if (currentSelectedClass == null) return;

        List<MultilingualText> currentList = getClassAlternativeNames(currentSelectedClass);
        
        List<MultilingualText> updatedList = StructuredMultilingualTextListEditorDialog.showDialog(
                getParentFrame(), 
                "Edit Alternative Names for " + currentSelectedClass.getName(),
                new ArrayList<>(currentList) // Pass a copy
        );

        if (updatedList != null) { 
            setClassAlternativeNames(currentSelectedClass, updatedList);
            List<Map<String, String>> maps = updatedList.stream().map(MultilingualText::getMap).collect(Collectors.toList());
            classAlternativeNamesReadOnlyArea.setText(OntoUMLStringUtils.formatMultiLanguageString(maps));
            classAlternativeNamesReadOnlyArea.setCaretPosition(0);
            // Potentially mark as dirty or trigger an update
        }
    }

    private List<MultilingualText> getClassAlternativeNames(IClass cls) {
        if (cls == null) return Collections.emptyList();
        ITaggedValueContainer taggedValueContainer = cls.getTaggedValues();
        if (taggedValueContainer == null) return Collections.emptyList();

        ITaggedValue taggedValue = taggedValueContainer.getTaggedValueByName(TAG_ALTERNATIVE_NAMES);
        if (taggedValue != null && taggedValue.getValueAsString() != null && !taggedValue.getValueAsString().isEmpty()) {
            try {
                return objectMapper.readValue(taggedValue.getValueAsString(), new TypeReference<List<MultilingualText>>() {});
            } catch (IOException e) {
                System.err.println("Error deserializing alternative names for class " + cls.getName() + ": " + e.getMessage());
                // Potentially log to VP message pane as well
                ApplicationManager.instance().getViewManager().showMessage("Error reading alternative names for " + cls.getName() + ". See logs.", "OntoUML Plugin");
            }
        }
        return Collections.emptyList();
    }

    private void setClassAlternativeNames(IClass cls, List<MultilingualText> names) {
        if (cls == null || names == null) return;

        try {
            String jsonValue = objectMapper.writeValueAsString(names);
            ITaggedValueContainer taggedValueContainer = cls.getTaggedValues();
            if (taggedValueContainer == null) {
                 // This case should ideally not happen for standard elements like IClass.
                 // If it does, it might indicate an issue with the VP API or element state.
                 ApplicationManager.instance().getViewManager().showMessage("Cannot set alternative names: Tagged value container not found for class " + cls.getName(), "OntoUML Plugin");
                System.err.println("Cannot set alternative names: Tagged value container not found for class " + cls.getName());
                return;
            }

            ITaggedValue taggedValue = taggedValueContainer.getTaggedValueByName(TAG_ALTERNATIVE_NAMES);
            if (taggedValue == null) {
                taggedValue = IModelElementFactory.instance().createTaggedValue();
                taggedValue.setName(TAG_ALTERNATIVE_NAMES);
                // taggedValue.setType(ITaggedValue.TYPE_TEXT); // Set type if necessary, usually defaults fine for string.
                taggedValueContainer.addTaggedValue(taggedValue);
            }
            taggedValue.setValue(jsonValue);
             ApplicationManager.instance().getViewManager().showMessage("Alternative names for " + cls.getName() + " updated (pending project save).", "OntoUML Plugin");
        } catch (JsonProcessingException e) {
            System.err.println("Error serializing alternative names for class " + cls.getName() + ": " + e.getMessage());
            ApplicationManager.instance().getViewManager().showMessage("Error saving alternative names for " + cls.getName() + ". See logs.", "OntoUML Plugin");
        }
    }
} 