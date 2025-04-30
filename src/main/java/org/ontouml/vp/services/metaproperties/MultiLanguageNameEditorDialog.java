package org.ontouml.vp.services.metaproperties;

import org.ontouml.ontouml4j.model.MultilingualText;
import org.ontouml.vp.utils.LanguageUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class MultiLanguageNameEditorDialog extends JDialog {
    private JComboBox<String> languageComboBox;
    private JTextField nameTextField;
    private JButton addUpdateButton;
    private JTable nameTable;
    private DefaultTableModel tableModel;
    private JButton saveButton;
    private JButton cancelButton;

    private MultilingualText originalNameData;
    private MultilingualText updatedNameData;

    public MultiLanguageNameEditorDialog(Frame owner, String title, MultilingualText nameData) {
        super(owner, title, true);
        this.originalNameData = nameData != null ? new MultilingualText(nameData.getMap()) : new MultilingualText(); // Work on a copy
        this.updatedNameData = null;
        initComponents();
        populateTable();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // --- Input Panel (Top) ---
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Language Selector
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Language:"), gbc);

        languageComboBox = new JComboBox<>(LanguageUtils.getLanguagesCode());
        languageComboBox.setEditable(true);
        gbc.gridx = 1;
        gbc.weightx = 0.3;
        inputPanel.add(languageComboBox, gbc);

        // Name Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Name:"), gbc);

        nameTextField = new JTextField(25);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.7;
        inputPanel.add(nameTextField, gbc);

        // Add/Update Button
        addUpdateButton = new JButton("Add/Update");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(addUpdateButton, gbc);

        add(inputPanel, BorderLayout.NORTH);

        // --- Table Panel (Center) ---
        tableModel = new DefaultTableModel(new String[]{"Language", "Name"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        nameTable = new JTable(tableModel);
        nameTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        nameTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && nameTable.getSelectedRow() != -1) {
                int selectedRow = nameTable.getSelectedRow();
                String lang = (String) tableModel.getValueAt(selectedRow, 0);
                String name = (String) tableModel.getValueAt(selectedRow, 1);
                languageComboBox.setSelectedItem(lang.isEmpty() ? "" : lang);
                nameTextField.setText(name);
            }
        });

        JScrollPane scrollPane = new JScrollPane(nameTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Defined Names"));
        add(scrollPane, BorderLayout.CENTER);

        // --- Button Panel (Bottom) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        addUpdateButton.addActionListener(e -> addOrUpdateName());
        saveButton.addActionListener(e -> saveChanges());
        cancelButton.addActionListener(e -> dispose());

        getRootPane().setDefaultButton(addUpdateButton);
    }

    private void populateTable() {
        tableModel.setRowCount(0);
        for (Map.Entry<String, String> entry : originalNameData.getMap().entrySet()) {
            String lang = entry.getKey().isEmpty() ? "" : entry.getKey();
            tableModel.addRow(new Object[]{lang, entry.getValue()});
        }
    }

    private void addOrUpdateName() {
        String lang = ((String) languageComboBox.getSelectedItem()).trim();
        String name = nameTextField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean updated = false;
        if (originalNameData.getText(lang).isPresent()) {
            originalNameData.putText(lang, name);
            updated = true;
        } else {
             originalNameData.putText(lang, name);
        }

        populateTable();
        nameTextField.setText("");
        languageComboBox.requestFocusInWindow();

        if (updated) {
             System.out.println("Updated name for language: '" + lang + "'");
        } else {
             System.out.println("Added name for language: '" + lang + "'");
        }
    }

    private void saveChanges() {
        this.updatedNameData = this.originalNameData;
        dispose();
    }

    /**
     * Shows the modal dialog to edit the MultilingualText for the name.
     *
     * @param owner The parent Frame.
     * @param title The dialog title.
     * @param nameData The initial MultilingualText data (can be null or empty).
     * @return The updated MultilingualText if saved, or null if cancelled.
     */
    public static MultilingualText showDialog(Frame owner, String title, MultilingualText nameData) {
        MultiLanguageNameEditorDialog dialog = new MultiLanguageNameEditorDialog(owner, title, nameData);
        dialog.setVisible(true); // Blocks until dialog is closed
        return dialog.updatedNameData; // Return the result (null if cancelled)
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Parent");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 200);
            frame.setLocationRelativeTo(null);

            JButton openDialogButton = new JButton("Edit Name");
            MultilingualText testName = new MultilingualText();
            testName.putText("en", "Default Project");
            testName.putText("pt", "Projeto Padrão");


            openDialogButton.addActionListener(e -> {
                MultilingualText result = MultiLanguageNameEditorDialog.showDialog(frame, "Edit Project Name", testName);
                if (result != null) {
                    System.out.println("Dialog Saved. Updated Name Map:");
                    result.getMap().forEach((lang, name) -> System.out.println("  " + lang + ": " + name));
                    // Update the original object if needed
                     testName.getMap().clear();
                     testName.getMap().putAll(result.getMap());
                } else {
                    System.out.println("Dialog Cancelled.");
                }
            });

            frame.getContentPane().setLayout(new FlowLayout());
            frame.getContentPane().add(openDialogButton);
            frame.setVisible(true);
        });
    }
} 