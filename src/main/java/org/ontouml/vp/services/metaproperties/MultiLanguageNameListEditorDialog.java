package org.ontouml.vp.services.metaproperties;

import org.ontouml.ontouml4j.model.MultilingualText;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class MultiLanguageNameListEditorDialog extends JDialog {

    private final List<MultilingualText> dataList;
    private DefaultListModel<MultilingualText> listModel;
    private JList<MultilingualText> nameList;
    private boolean saved = false;

    public MultiLanguageNameListEditorDialog(Frame owner, String title, List<MultilingualText> initialData) {
        super(owner, title, true);
        this.dataList = (initialData != null) ? new ArrayList<>(initialData) : new ArrayList<>();
        initializeUI();
        populateListModel();
        setSize(500, 350);
        setLocationRelativeTo(owner);
    }

    private void initializeUI() {
        setLayout(new BorderLayout(5, 5));

        listModel = new DefaultListModel<>();
        nameList = new JList<>(listModel);
        nameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        nameList.setCellRenderer(new MultilingualTextListCellRenderer());

        JScrollPane scrollPane = new JScrollPane(nameList);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton removeButton = new JButton("Remove");
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        addButton.addActionListener(this::addEntry);
        editButton.addActionListener(this::editEntry);
        removeButton.addActionListener(this::removeEntry);
        okButton.addActionListener(this::saveAndClose);
        cancelButton.addActionListener(e -> dispose());

        nameList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editEntry(null);
                }
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void populateListModel() {
        listModel.clear();
        for (MultilingualText item : dataList) {
            listModel.addElement(item);
        }
    }

    private Frame getParentFrame() {
        return (Frame) SwingUtilities.getWindowAncestor(this);
    }

    private void addEntry(ActionEvent e) {
        MultilingualText newName = new MultilingualText();
        MultilingualText editedName = MultiLanguageNameEditorDialog.showDialog(
                getParentFrame(),
                "Add New Alternative Name",
                newName
        );

        if (editedName != null && !editedName.getText().isEmpty()) {
            dataList.add(editedName);
            populateListModel();
            nameList.setSelectedValue(editedName, true);
        }
    }

    private void editEntry(ActionEvent e) {
        int selectedIndex = nameList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a name to edit.", "Edit Name", JOptionPane.WARNING_MESSAGE);
            return;
        }

        MultilingualText selectedName = listModel.getElementAt(selectedIndex);
        MultilingualText nameToEdit = new MultilingualText();
         if (selectedName != null && selectedName.getMap() != null) {
             selectedName.getMap().forEach(nameToEdit::putText);
         }


        MultilingualText updatedName = MultiLanguageNameEditorDialog.showDialog(
                getParentFrame(),
                "Edit Alternative Name",
                nameToEdit
        );

        if (updatedName != null && !updatedName.getText().isEmpty()) {
            dataList.set(selectedIndex, updatedName);
            listModel.setElementAt(updatedName, selectedIndex);
        } else if (updatedName != null && updatedName.getText().isEmpty()) {
             int confirm = JOptionPane.showConfirmDialog(this,
                    "The edited name has no text entries. Do you want to remove it from the list?",
                    "Remove Empty Name?", JOptionPane.YES_NO_OPTION);
             if (confirm == JOptionPane.YES_OPTION) {
                 dataList.remove(selectedIndex);
                 populateListModel();
             }
        }
    }

    private void removeEntry(ActionEvent e) {
        int selectedIndex = nameList.getSelectedIndex();
        if (selectedIndex != -1) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to remove this name?",
                    "Remove Name", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dataList.remove(selectedIndex);
                populateListModel(); // Refresh list model
            }
        } else {
             JOptionPane.showMessageDialog(this, "Please select a name to remove.", "Remove Name", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void saveAndClose(ActionEvent e) {
        this.saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public List<MultilingualText> getUpdatedDataList() {
        return new ArrayList<>(dataList);
    }

    public static List<MultilingualText> showDialog(Component parent, String title, List<MultilingualText> initialData) {
        Frame owner = (parent instanceof Frame) ? (Frame) parent : (Frame) SwingUtilities.getWindowAncestor(parent);
        MultiLanguageNameListEditorDialog dialog = new MultiLanguageNameListEditorDialog(owner, title, initialData);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            return dialog.getUpdatedDataList();
        }
        return null;
    }


    private static class MultilingualTextListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof MultilingualText) {
                String displayText = ((MultilingualText)value).getText().orElse("");

                ((JLabel) renderer).setText(displayText);
            } else if (value != null) {
                 ((JLabel) renderer).setText(value.toString()); // Fallback
            }
            return renderer;
        }
    }
} 