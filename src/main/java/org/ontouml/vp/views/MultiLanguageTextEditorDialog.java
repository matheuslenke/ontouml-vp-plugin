package org.ontouml.vp.views;

import org.ontouml.ontouml4j.model.MultilingualText;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MultiLanguageTextEditorDialog extends JDialog {

    private static final String[] LANGUAGE_CODES = getLanguagesCode(); // Reuse language codes

    private JComboBox<String> languageComboBox;
    private JTextArea textArea;
    private JButton saveButton;
    private JButton cancelButton;

    private Map<String, String> textMap; // Internal storage for edits
    private boolean saved = false;
    private String currentLanguage = LANGUAGE_CODES[0]; // Start with default

    private MultiLanguageTextEditorDialog(Frame owner, String title, MultilingualText initialData) {
        super(owner, title, true); // Modal dialog
        this.textMap = initialData != null && initialData.getMap() != null
                       ? new HashMap<>(initialData.getMap()) // Create a mutable copy
                       : new HashMap<>();
        initComponents();
        pack();
        setLocationRelativeTo(owner);
        loadTextForCurrentLanguage(); // Load initial text
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Top Panel: Language Selector
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Language:"));
        languageComboBox = new JComboBox<>(LANGUAGE_CODES);
        languageComboBox.setSelectedItem(currentLanguage);
        languageComboBox.addItemListener(this::languageChanged);
        topPanel.add(languageComboBox);

        // Center Panel: Text Area
        textArea = new JTextArea(10, 40);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Bottom Panel: Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        saveButton.addActionListener(this::saveChanges);
        cancelButton.addActionListener(e -> dispose()); // Just close the dialog

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add panels to dialog
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add padding
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    private void languageChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            // Save current text before switching
            saveCurrentText();

            // Update current language and load new text
            currentLanguage = (String) languageComboBox.getSelectedItem();
            loadTextForCurrentLanguage();
        }
    }

    private void saveCurrentText() {
        String text = textArea.getText();
        if (text != null && !text.trim().isEmpty()) {
            textMap.put(currentLanguage, text.trim());
        } else {
            textMap.remove(currentLanguage); // Remove entry if text is empty
        }
    }

    private void loadTextForCurrentLanguage() {
        textArea.setText(textMap.getOrDefault(currentLanguage, ""));
        textArea.setCaretPosition(0);
    }

    private void saveChanges(ActionEvent e) {
        saveCurrentText(); // Save text for the currently selected language
        this.saved = true;
        dispose(); // Close the dialog
    }

    public MultilingualText getUpdatedData() {
        if (!saved) {
            return null; // Return null if cancelled
        }
        // Clean up empty entries before returning
        textMap.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().trim().isEmpty());

        if (textMap.isEmpty()){
            return null; // Return null if map ended up empty
        } else {
            return new MultilingualText(textMap);
        }
    }

    // Static method to show the dialog
    public static MultilingualText showDialog(Frame owner, String title, MultilingualText initialData) {
        MultiLanguageTextEditorDialog dialog = new MultiLanguageTextEditorDialog(owner, title, initialData);
        dialog.setVisible(true); // Blocks until dialog is closed
        return dialog.getUpdatedData();
    }

    // Reusing the exact list from GufoExportView for consistency
    private static String[] getLanguagesCode() {
        return new String[] {
          "default", "aa", "ab", "ae", "af", "ak", "am", "an", "ar", "as", "av", "ay", "az", "ba", "be",
          "bg", "bh", "bm", "bi", "bn", "bo", "br", "bs", "ca", "ce", "ch", "co", "cr", "cs", "cu",
          "cv", "cy", "da", "de", "dv", "dz", "ee", "el", "en", "eo", "es", "et", "eu", "fa", "ff",
          "fi", "fj", "fo", "fr", "fy", "ga", "gd", "gl", "gn", "gu", "gv", "ha", "he", "hi", "ho",
          "hr", "ht", "hu", "hy", "hz", "ia", "id", "ie", "ig", "ii", "ik", "io", "is", "it", "iu",
          "ja", "jv", "ka", "kg", "ki", "kj", "kk", "kl", "km", "kn", "ko", "kr", "ks", "ku", "kv",
          "kw", "ky", "la", "lb", "lg", "li", "ln", "lo", "lt", "lu", "lv", "mg", "mh", "mi", "mk",
          "ml", "mn", "mr", "ms", "mt", "my", "na", "nb", "nd", "ne", "ng", "nl", "nn", "no", "nr",
          "nv", "ny", "oc", "oj", "om", "or", "os", "pa", "pi", "pl", "ps", "pt", "qu", "rm", "rn",
          "ro", "ru", "rw", "sa", "sc", "sd", "se", "sg", "si", "sk", "sl", "sm", "sn", "so", "sq",
          "sr", "ss", "st", "su", "sv", "sw", "ta", "te", "tg", "th", "ti", "tk", "tl", "tn", "to",
          "tr", "ts", "tt", "tw", "ty", "ug", "uk", "ur", "uz", "ve", "vi", "vo", "wa", "wo", "xh",
          "yi", "yo", "za", "zh", "zu"
        };
    }
} 