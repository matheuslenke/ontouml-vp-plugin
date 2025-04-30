package org.ontouml.vp.services.metaproperties;

import org.ontouml.ontouml4j.model.MultilingualText;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MultilingualTextEditorDialog extends JDialog {

    private JTable table;
    private DefaultTableModel tableModel;
    private List<MultilingualText> dataList;
    private boolean saved = false;

    private static final String[] COLUMN_NAMES = {"Language Tag", "Text"};
    private static final int LANGUAGE_COLUMN_INDEX = 0;
    private static final int TEXT_COLUMN_INDEX = 1;

    public MultilingualTextEditorDialog(Frame owner, String title, List<MultilingualText> initialData) {
        super(owner, title, true); // Modal dialog
        this.dataList = (initialData != null) ? new ArrayList<>(initialData) : new ArrayList<>();
        initializeUI();
        populateTable();
        setSize(500, 350);
        setLocationRelativeTo(owner);
    }

    private void initializeUI() {
        setLayout(new BorderLayout(5, 5));

        // Table setup
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25); // Adjust row height for better readability

        // Make text column wider
        TableColumn textColumn = table.getColumnModel().getColumn(TEXT_COLUMN_INDEX);
        textColumn.setPreferredWidth(350);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
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

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(new JSeparator(SwingConstants.VERTICAL)); // Visual separator
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void populateTable() {
        tableModel.setRowCount(0); // Clear existing rows
        for (MultilingualText item : dataList) {
            for (Map.Entry<String, String> entry : item.getMap().entrySet()) {
                String lang = entry.getKey() == null || entry.getKey().isEmpty() ? "(default)" : entry.getKey();
                tableModel.addRow(new Object[]{lang, entry.getValue()});
            }
        }
    }

    private void addEntry(ActionEvent e) {
        // Use a simple dialog to get lang and text
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
                "Add New Entry", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String lang = langField.getText().trim();
            String text = textField.getText();
            if (!text.isEmpty()) { // Only add if text is not empty
                String langKey = lang.isEmpty() ? "" : lang; // Store empty string as key for default
                MultilingualText newEntry = new MultilingualText();
                newEntry.putText(langKey, text);
                dataList.add(newEntry); // Add as a new MultilingualText object
                populateTable(); // Refresh table
            }
        }
    }

     private void editEntry(ActionEvent e) {
         int selectedRow = table.getSelectedRow();
         if (selectedRow == -1) {
             JOptionPane.showMessageDialog(this, "Please select an entry to edit.", "Edit Entry", JOptionPane.WARNING_MESSAGE);
             return;
         }

         // Find the corresponding MultilingualText object and key
         // This is tricky because the table flattens the structure.
         // We need a more robust way to map rows back to the original data.
         // Let's simplify: Edit directly in the table for now, or rebuild based on table.

         // Rebuild approach (simpler to implement now):
         // For editing, we can pop up a similar dialog as 'add'
         String currentLang = (String) tableModel.getValueAt(selectedRow, LANGUAGE_COLUMN_INDEX);
         String currentText = (String) tableModel.getValueAt(selectedRow, TEXT_COLUMN_INDEX);
         String langKey = "(default)".equals(currentLang) ? "" : currentLang;

         JTextField langField = new JTextField(currentLang.equals("(default)") ? "" : currentLang);
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
                 "Edit Entry", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

         if (result == JOptionPane.OK_OPTION) {
             String newLang = langField.getText().trim();
             String newText = textField.getText();
             String newLangKey = newLang.isEmpty() ? "" : newLang;

             // Update the table directly for now (this doesn't perfectly update dataList)
             // A better approach would involve mapping rows to dataList indices.
             tableModel.setValueAt(newLang.isEmpty() ? "(default)" : newLang, selectedRow, LANGUAGE_COLUMN_INDEX);
             tableModel.setValueAt(newText, selectedRow, TEXT_COLUMN_INDEX);

             // Mark for rebuild on save
             // saved = false; // Not needed, rebuild happens in saveAndClose
         }
     }

    private void removeEntry(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to remove this entry?",
                    "Remove Entry", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                tableModel.removeRow(selectedRow);
                // Mark for rebuild on save
            }
        }
    }

    // Rebuilds the dataList from the table content before closing
    private void rebuildDataListFromTable() {
        dataList.clear();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String lang = (String) tableModel.getValueAt(i, LANGUAGE_COLUMN_INDEX);
            String text = (String) tableModel.getValueAt(i, TEXT_COLUMN_INDEX);
            String langKey = "(default)".equals(lang) ? "" : lang;

            if (text != null && !text.isEmpty()) {
                MultilingualText entry = new MultilingualText();
                entry.putText(langKey, text);
                dataList.add(entry);
            }
        }
    }

    private void saveAndClose(ActionEvent e) {
        rebuildDataListFromTable(); // Rebuild list based on table state
        this.saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public List<MultilingualText> getUpdatedDataList() {
        return dataList;
    }

    // Static method to easily show the dialog
    public static List<MultilingualText> showDialog(Component parent, String title, List<MultilingualText> initialData) {
        Frame owner = (parent instanceof Frame) ? (Frame) parent : (Frame) SwingUtilities.getWindowAncestor(parent);
        MultilingualTextEditorDialog dialog = new MultilingualTextEditorDialog(owner, title, initialData);
        dialog.setVisible(true); // Blocks until dialog is closed

        if (dialog.isSaved()) {
            return dialog.getUpdatedDataList();
        }
        return null; // Return null if cancelled
    }
} 