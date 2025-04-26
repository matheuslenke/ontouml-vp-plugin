package org.ontouml.vp.views;

import org.ontouml.ontouml4j.model.MultilingualText;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class MultilingualInputComponent extends JPanel {

    private JComboBox<String> languageComboBox;
    private JTextComponent textInputComponent; // Can be JTextField or JTextArea wrapped in JScrollPane
    private MultilingualText data;
    private final boolean isTextArea;

    private static final String NO_LANGUAGE_TAG = "(default)"; // Represents null or empty language tag

    public MultilingualInputComponent(MultilingualText initialData, boolean isTextArea) {
        super(new BorderLayout(5, 0)); // Add gap between combobox and text field
        this.data = (initialData != null) ? initialData : new MultilingualText();
        this.isTextArea = isTextArea;
        initializeComponents();
        loadLanguages();
        selectInitialLanguage();
        loadTextForSelectedLanguage();
    }

    private void initializeComponents() {
        languageComboBox = new JComboBox<>();
        languageComboBox.setToolTipText("Select language tag");
        // Make combobox smaller horizontally
        languageComboBox.setPreferredSize(new Dimension(100, languageComboBox.getPreferredSize().height));

        if (isTextArea) {
            JTextArea textArea = new JTextArea();
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setRows(3); // Same as original panel
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
             // Set preferred size to match original panel's text areas
            scrollPane.setPreferredSize(new Dimension(450, 60));
            textInputComponent = textArea;
            add(scrollPane, BorderLayout.CENTER);
        } else {
            JTextField textField = new JTextField();
            textInputComponent = textField;
            add(textField, BorderLayout.CENTER);
        }

        textInputComponent.setBorder(BorderFactory.createEtchedBorder());

        // Add listener to save text when language changes
        languageComboBox.addActionListener(e -> {
            // TODO: Potentially save text of *previous* language before switching
            loadTextForSelectedLanguage();
        });

        // Add listener to update data structure when text changes (consider performance/event triggering)
//        textInputComponent.getDocument().addDocumentListener(new DocumentListener() { ... });
        // For simplicity now, we'll only collect data on save.

        add(languageComboBox, BorderLayout.WEST);
    }

    private void loadLanguages() {
        List<String> languages = new ArrayList<>(data.getMap().keySet());
        Collections.sort(languages);

        Vector<String> comboBoxModel = new Vector<>();
        comboBoxModel.add(NO_LANGUAGE_TAG); // Add default option first
        for (String lang : languages) {
            if (lang != null && !lang.isEmpty()) {
                comboBoxModel.add(lang);
            }
        }
        languageComboBox.setModel(new DefaultComboBoxModel<>(comboBoxModel));
    }

    private void selectInitialLanguage() {
        // Try to select a non-default language if available, otherwise select default
        if (languageComboBox.getItemCount() > 1) {
            languageComboBox.setSelectedIndex(1); // Select the first actual language tag
        } else {
            languageComboBox.setSelectedIndex(0); // Select NO_LANGUAGE_TAG
        }
    }

    private void loadTextForSelectedLanguage() {
        String selectedLanguage = (String) languageComboBox.getSelectedItem();
        String text = "";
        if (NO_LANGUAGE_TAG.equals(selectedLanguage)) {
            data.getText().ifPresent(t -> textInputComponent.setText(t));
        } else if (selectedLanguage != null) {
            data.getText(selectedLanguage).ifPresent(t -> textInputComponent.setText(t));
        }
        textInputComponent.setText(text != null ? text : "");
        textInputComponent.setCaretPosition(0);
    }

    // Call this when saving the panel's data
    public MultilingualText getData() {
        // Update the text for the currently selected language before returning
        saveTextForSelectedLanguage();
        // We might need to add logic here to remove entries if text is empty?
        // For now, keep it simple.
        return data;
    }

    private void saveTextForSelectedLanguage() {
        String selectedLanguage = (String) languageComboBox.getSelectedItem();
        String text = textInputComponent.getText();

        if (NO_LANGUAGE_TAG.equals(selectedLanguage)) {
            data.putText(text); // Use empty string as key for default
        } else if (selectedLanguage != null) {
            data.putText(selectedLanguage, text);
        }
         // Optionally remove the entry if the text is empty and it's not the default
         if (text == null || text.isEmpty()) {
            if (!NO_LANGUAGE_TAG.equals(selectedLanguage) && selectedLanguage != null) {
                 // Check if default exists; if not, don't remove the last entry?
                 // Simpler: just allow empty strings for now.
                 // data.getMap().remove(selectedLanguage);
            } else if (NO_LANGUAGE_TAG.equals(selectedLanguage)) {
                 data.getMap().remove(""); // Remove the default entry
                 data.getMap().remove(null);
             }
         }
    }

     // TODO: Add methods to add/remove languages if needed via the ComboBox or buttons
     // public void addLanguage(String lang) { ... }
     // public void removeLanguage(String lang) { ... }
} 