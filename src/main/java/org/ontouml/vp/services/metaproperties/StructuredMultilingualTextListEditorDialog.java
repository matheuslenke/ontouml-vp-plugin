package org.ontouml.vp.services.metaproperties;

import org.ontouml.ontouml4j.model.MultilingualText;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StructuredMultilingualTextListEditorDialog extends JDialog {

    private final List<MultilingualText> dataListWorkingCopy;
    private DefaultListModel<MultilingualText> listModel;
    private JList<MultilingualText> multilingualTextJList;

    private JPanel detailEditorPanelContainer; // Holds the actual editor panel
    private MultilingualTextDetailEditorPanel activeDetailEditorPanel; // The actual editor

    private boolean saved = false;

    private JButton addToListButton;
    private JButton removeFromListButton;
    private JButton okButton;
    private JButton cancelButton;

    // For the detail editor panel (right side)
    private static final String[] DETAIL_COLUMN_NAMES = {"Language Tag", "Text"};
    private static final int LANGUAGE_COLUMN_INDEX = 0;
    private static final int TEXT_COLUMN_INDEX = 1;


    public StructuredMultilingualTextListEditorDialog(Frame owner, String title, List<MultilingualText> initialData) {
        super(owner, title, true);
        this.dataListWorkingCopy = (initialData != null) ?
                new ArrayList<>(initialData.size()) : new ArrayList<>();
        if (initialData != null) {
            for (MultilingualText mt : initialData) {
                // Create deep copies for editing
                this.dataListWorkingCopy.add(new MultilingualText(mt));
            }
        }

        initializeUI();
        populateJList();
        setMinimumSize(new Dimension(700, 450));
        setSize(800, 500);
        setLocationRelativeTo(owner);
    }

    private void initializeUI() {
        setLayout(new BorderLayout(5, 5));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

        // Left Panel: List of MultilingualText items
        listModel = new DefaultListModel<>();
        multilingualTextJList = new JList<>(listModel);
        multilingualTextJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        multilingualTextJList.setCellRenderer(new MultilingualTextListCellRenderer());
        multilingualTextJList.addListSelectionListener(this::onListItemSelected);
        JScrollPane listScrollPane = new JScrollPane(multilingualTextJList);
        listScrollPane.setMinimumSize(new Dimension(200, 150)); // Min size for the list

        // Right Panel: Editor for selected MultilingualText (Container)
        detailEditorPanelContainer = new JPanel(new BorderLayout());
        detailEditorPanelContainer.add(createPlaceholderPanel("Select an item from the list to edit its translations."), BorderLayout.CENTER);
        detailEditorPanelContainer.setMinimumSize(new Dimension(300,150));


        // Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, detailEditorPanelContainer);
        splitPane.setDividerLocation(250);
        splitPane.setResizeWeight(0.3); // Give some weight to the list panel

        add(splitPane, BorderLayout.CENTER);

        // Bottom Button Panel (for the list)
        JPanel listControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addToListButton = new JButton("Add Item");
        removeFromListButton = new JButton("Remove Item");
        addToListButton.addActionListener(this::addMultilingualTextItem);
        removeFromListButton.addActionListener(this::removeMultilingualTextItem);
        listControlPanel.add(addToListButton);
        listControlPanel.add(removeFromListButton);
        
        JPanel bottomControls = new JPanel(new BorderLayout());
        bottomControls.add(listControlPanel, BorderLayout.WEST);

        JPanel dialogButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
        okButton.addActionListener(this::saveAndClose);
        cancelButton.addActionListener(e -> dispose());
        dialogButtonPanel.add(okButton);
        dialogButtonPanel.add(cancelButton);
        bottomControls.add(dialogButtonPanel, BorderLayout.EAST);

        add(bottomControls, BorderLayout.SOUTH);
    }
    
    private JPanel createPlaceholderPanel(String text) {
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(label, gbc);
        panel.setBorder(BorderFactory.createEtchedBorder());
        return panel;
    }

    private void populateJList() {
        listModel.clear();
        for (MultilingualText item : dataListWorkingCopy) {
            listModel.addElement(item);
        }
        if (!listModel.isEmpty()) {
            multilingualTextJList.setSelectedIndex(0);
        } else {
            // If list is empty, ensure right pane shows placeholder
            updateDetailPanel(null);
        }
    }

    private void onListItemSelected(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            MultilingualText selected = multilingualTextJList.getSelectedValue();
            updateDetailPanel(selected);
        }
    }

    private void updateDetailPanel(MultilingualText selected) {
        detailEditorPanelContainer.removeAll();
        if (selected != null) {
            activeDetailEditorPanel = new MultilingualTextDetailEditorPanel(selected);
            detailEditorPanelContainer.add(activeDetailEditorPanel, BorderLayout.CENTER);
        } else {
            activeDetailEditorPanel = null;
            detailEditorPanelContainer.add(createPlaceholderPanel("Select an item to edit or add a new item."), BorderLayout.CENTER);
        }
        detailEditorPanelContainer.revalidate();
        detailEditorPanelContainer.repaint();
    }

    private Frame getParentFrame() {
        return (Frame) SwingUtilities.getWindowAncestor(this);
    }

    private void addMultilingualTextItem(ActionEvent e) {
        // Use MultiLanguageNameEditorDialog to define the new MultilingualText item
        MultilingualText newName = new MultilingualText(); // Start with an empty one
        MultilingualText editedName = MultiLanguageNameEditorDialog.showDialog(
                getParentFrame(),
                "Add New Text Item",
                newName
        );

        if (editedName != null && !editedName.getText().isEmpty()) {
            dataListWorkingCopy.add(editedName);
            listModel.addElement(editedName);
            multilingualTextJList.setSelectedValue(editedName, true);
        }
    }

    private void removeMultilingualTextItem(ActionEvent e) {
        MultilingualText selected = multilingualTextJList.getSelectedValue();
        if (selected != null) {
            int selectedIndex = multilingualTextJList.getSelectedIndex();
            dataListWorkingCopy.remove(selected);
            listModel.removeElement(selected);

            if (listModel.isEmpty()) {
                updateDetailPanel(null);
            } else {
                if (selectedIndex >= listModel.getSize()) {
                    selectedIndex = listModel.getSize() - 1;
                }
                multilingualTextJList.setSelectedIndex(selectedIndex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to remove.", "Remove Item", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void saveAndClose(ActionEvent e) {
        // Ensure any pending edits in the detail panel are committed if necessary
        // (though direct modification of MultilingualText objects by the detail panel is preferred)
        this.saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public List<MultilingualText> getUpdatedDataList() {
        // Return a new list with copies if further modification outside is a concern,
        // or the working copy if it's understood that these are the final versions.
        // For now, returning the working copy which contains the modified objects.
        return saved ? dataListWorkingCopy : null;
    }

    public static List<MultilingualText> showDialog(Component parent, String title, List<MultilingualText> initialData) {
        Frame owner = (parent instanceof Frame) ? (Frame) parent : (Frame) SwingUtilities.getWindowAncestor(parent);
        StructuredMultilingualTextListEditorDialog dialog = new StructuredMultilingualTextListEditorDialog(owner, title, initialData);
        dialog.setVisible(true);

        return dialog.getUpdatedDataList();
    }

    // Inner class for the detail editor panel (right side)
    private static class MultilingualTextDetailEditorPanel extends JPanel {
        private final MultilingualText currentMultilingualText;
        private JTable langTextTable;
        private DefaultTableModel tableModel;

        public MultilingualTextDetailEditorPanel(MultilingualText mt) {
            super(new BorderLayout(5, 5));
            this.currentMultilingualText = mt; // This is a reference to the object in dataListWorkingCopy
            setBorder(new EmptyBorder(5,5,5,5));
            initializeDetailUI();
            populateTable();
        }

        private void initializeDetailUI() {
            // Table setup
            tableModel = new DefaultTableModel(DETAIL_COLUMN_NAMES, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Editing via dialog for consistency
                }
            };
            langTextTable = new JTable(tableModel);
            langTextTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            langTextTable.setRowHeight(25);
            TableColumn textColumn = langTextTable.getColumnModel().getColumn(TEXT_COLUMN_INDEX);
            textColumn.setPreferredWidth(350);
            JScrollPane scrollPane = new JScrollPane(langTextTable);
            add(scrollPane, BorderLayout.CENTER);

            // Button panel for table
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton addPairButton = new JButton("Add Pair");
            JButton editPairButton = new JButton("Edit Pair");
            JButton removePairButton = new JButton("Remove Pair");

            addPairButton.addActionListener(this::addLangTextPair);
            editPairButton.addActionListener(this::editLangTextPair);
            removePairButton.addActionListener(this::removeLangTextPair);
            
            langTextTable.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (evt.getClickCount() == 2) {
                        editLangTextPair(null); // Pass null for ActionEvent
                    }
                }
            });

            buttonPanel.add(addPairButton);
            buttonPanel.add(editPairButton);
            buttonPanel.add(removePairButton);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private void populateTable() {
            tableModel.setRowCount(0); // Clear existing rows
            if (currentMultilingualText != null && currentMultilingualText.getMap() != null) {
                for (Map.Entry<String, String> entry : currentMultilingualText.getMap().entrySet()) {
                    String lang = entry.getKey() == null || entry.getKey().isEmpty() ? "(default)" : entry.getKey();
                    tableModel.addRow(new Object[]{lang, entry.getValue()});
                }
            }
        }

        private void addLangTextPair(ActionEvent e) {
            if (currentMultilingualText == null) return;

            JTextField langField = new JTextField();
            JTextArea textField = new JTextArea(5, 30);
            textField.setLineWrap(true);
            textField.setWrapStyleWord(true);
            JScrollPane textScrollPane = new JScrollPane(textField);

            JPanel panel = new JPanel(new BorderLayout(5,5));
            panel.add(new JLabel("Language Tag (e.g., en, pt-BR):", JLabel.LEFT), BorderLayout.NORTH);
            panel.add(langField, BorderLayout.CENTER);
            panel.add(new JLabel("Text:", JLabel.LEFT), BorderLayout.SOUTH);

            JPanel mainPanel = new JPanel(new BorderLayout(5,5));
            mainPanel.add(panel, BorderLayout.NORTH);
            mainPanel.add(textScrollPane, BorderLayout.CENTER);

            int result = JOptionPane.showConfirmDialog(this, mainPanel,
                    "Add New Language Entry", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String lang = langField.getText().trim();
                String text = textField.getText();
                // lang can be empty for default, text should not be empty if we want to add it
                if (!text.isEmpty()) { 
                    currentMultilingualText.putText(lang, text);
                    populateTable(); // Refresh table
                    // Mark main dialog as dirty or refresh JList rendering if it shows aggregate
                    // ((StructuredMultilingualTextListEditorDialog) SwingUtilities.getWindowAncestor(this)).multilingualTextJList.repaint();
                    // Repainting the JList is important if the cell renderer depends on the content of MultilingualText
                    JDialog parentDialog = (JDialog) SwingUtilities.getWindowAncestor(this);
                    if (parentDialog instanceof StructuredMultilingualTextListEditorDialog) {
                        ((StructuredMultilingualTextListEditorDialog)parentDialog).multilingualTextJList.repaint();
                    }
                }
            }
        }

        private void editLangTextPair(ActionEvent e) {
            if (currentMultilingualText == null) return;
            int selectedRow = langTextTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a language entry to edit.", "Edit Entry", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String currentLangTagDisplay = (String) tableModel.getValueAt(selectedRow, LANGUAGE_COLUMN_INDEX);
            String currentText = (String) tableModel.getValueAt(selectedRow, TEXT_COLUMN_INDEX);
            String originalLangKey = "(default)".equals(currentLangTagDisplay) ? "" : currentLangTagDisplay;

            JTextField langField = new JTextField("(default)".equals(currentLangTagDisplay) ? "" : currentLangTagDisplay);
            JTextArea textField = new JTextArea(currentText, 5, 30);
            textField.setLineWrap(true);
            textField.setWrapStyleWord(true);
            JScrollPane textScrollPane = new JScrollPane(textField);

            JPanel panel = new JPanel(new BorderLayout(5,5));
            panel.add(new JLabel("Language Tag:"), BorderLayout.NORTH);
            panel.add(langField, BorderLayout.CENTER);
            panel.add(new JLabel("Text:"), BorderLayout.SOUTH);

            JPanel mainPanel = new JPanel(new BorderLayout(5,5));
            mainPanel.add(panel, BorderLayout.NORTH);
            mainPanel.add(textScrollPane, BorderLayout.CENTER);

            int result = JOptionPane.showConfirmDialog(this, mainPanel,
                    "Edit Language Entry", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String newLang = langField.getText().trim();
                String newText = textField.getText();

                // If language tag changed, remove old one first
                if (!originalLangKey.equals(newLang)) {
                    currentMultilingualText.getMap().remove(originalLangKey);
                }
                
                if (!newText.isEmpty()) { // Only update/add if text is not empty
                    currentMultilingualText.putText(newLang, newText);
                } else { // If new text is empty, effectively remove this language entry
                     currentMultilingualText.getMap().remove(originalLangKey);
                     if (!originalLangKey.equals(newLang)) { // if lang tag also changed and new text is empty
                         currentMultilingualText.getMap().remove(newLang);
                     }
                }
                populateTable();
                 JDialog parentDialog = (JDialog) SwingUtilities.getWindowAncestor(this);
                 if (parentDialog instanceof StructuredMultilingualTextListEditorDialog) {
                    ((StructuredMultilingualTextListEditorDialog)parentDialog).multilingualTextJList.repaint();
                 }
            }
        }

        private void removeLangTextPair(ActionEvent e) {
            if (currentMultilingualText == null) return;
            int selectedRow = langTextTable.getSelectedRow();
            if (selectedRow != -1) {
                String langTagDisplay = (String) tableModel.getValueAt(selectedRow, LANGUAGE_COLUMN_INDEX);
                String langKey = "(default)".equals(langTagDisplay) ? "" : langTagDisplay;

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to remove the entry for language '" + langTagDisplay + "'?",
                        "Remove Language Entry", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    currentMultilingualText.getMap().remove(langKey);
                    populateTable();
                    JDialog parentDialog = (JDialog) SwingUtilities.getWindowAncestor(this);
                    if (parentDialog instanceof StructuredMultilingualTextListEditorDialog) {
                        ((StructuredMultilingualTextListEditorDialog)parentDialog).multilingualTextJList.repaint();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a language entry to remove.", "Remove Entry", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // We need the renderer from MultiLanguageNameListEditorDialog.
    // For now, define it here or ensure it's accessible.
    // Assuming it's accessible or copied if in a different file.
    private static class MultilingualTextListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof MultilingualText) {
                MultilingualText mt = (MultilingualText) value;
                Optional<String> text = mt.getText(); // Gets text in default language or any available
                // Fallback to a more descriptive representation if text is empty
                String displayText = text.orElseGet(() -> {
                    if (mt.getMap() == null || mt.getMap().isEmpty()) return "(Empty)";
                    return "(Translations: " + mt.getMap().size() + ")";
                });
                // Ensure no excessively long text in list
                if (displayText.length() > 100) {
                    displayText = displayText.substring(0, 97) + "...";
                }
                ((JLabel) renderer).setText(displayText);
            } else if (value != null) {
                 ((JLabel) renderer).setText(value.toString()); // Fallback
            } else {
                 ((JLabel) renderer).setText("(null)");
            }
            return renderer;
        }
    }
} 