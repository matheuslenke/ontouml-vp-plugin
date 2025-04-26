package org.ontouml.vp.services.project;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vp.plugin.ApplicationManager;
import org.ontouml.vp.data.ProjectMetapropertiesData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class for loading and saving OntoUML project metaproperties
 * to a JSON file in the Visual Paradigm workspace.
 */
public class ProjectMetapropertiesService {

    private static final String PROPERTIES_FILENAME = "ontouml-project-properties.json";
    private final ObjectMapper objectMapper;
    private final Path propertiesFilePath;

    public ProjectMetapropertiesService() {
        this.objectMapper = new ObjectMapper();
        // Pretty print the JSON file
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        File workspaceDir = ApplicationManager.instance().getWorkspaceLocation();
        if (workspaceDir == null || !workspaceDir.isDirectory()) {
            // Handle case where workspace location is not available or not a directory
            System.err.println("Error: Visual Paradigm workspace location not found or invalid. Cannot initialize ProjectMetapropertiesService.");
            this.propertiesFilePath = null; // Indicate service is not usable
        } else {
            this.propertiesFilePath = Paths.get(workspaceDir.getAbsolutePath(), PROPERTIES_FILENAME);
        }
    }

    /**
     * Loads all stored project properties from the JSON file.
     *
     * @return A Map where the key is the project ID and the value is the ProjectMetapropertiesData.
     *         Returns an empty map if the file doesn't exist or an error occurs.
     */
    private Map<String, ProjectMetapropertiesData> loadAllProperties() {
        if (propertiesFilePath == null || !Files.exists(propertiesFilePath)) {
            return new HashMap<>(); // Return empty map if file doesn't exist or path is invalid
        }

        try {
            File file = propertiesFilePath.toFile();
            if (file.length() == 0) { // Handle empty file case
                return new HashMap<>();
            }
            // Define the complex type for deserialization
            TypeReference<HashMap<String, ProjectMetapropertiesData>> typeRef 
              = new TypeReference<HashMap<String, ProjectMetapropertiesData>>() {};
            return objectMapper.readValue(file, typeRef);
        } catch (IOException e) {
            System.err.println("Error reading project properties file: " + propertiesFilePath);
            e.printStackTrace();
            // Consider showing an error message to the user via VP's message dialog
            return new HashMap<>(); // Return empty map on error
        }
    }

    /**
     * Saves the entire map of project properties back to the JSON file.
     *
     * @param allProperties The map containing properties for all projects.
     * @return true if saving was successful, false otherwise.
     */
    private boolean saveAllProperties(Map<String, ProjectMetapropertiesData> allProperties) {
        if (propertiesFilePath == null) {
            System.err.println("Error: Cannot save properties, file path is not initialized.");
            return false;
        }

        try {
            objectMapper.writeValue(propertiesFilePath.toFile(), allProperties);
            return true;
        } catch (IOException e) {
            System.err.println("Error writing project properties file: " + propertiesFilePath);
            e.printStackTrace();
            // Consider showing an error message to the user
            return false;
        }
    }

    /**
     * Loads the properties for a specific project ID.
     *
     * @param projectId The ID of the project whose properties are to be loaded.
     * @return The ProjectMetapropertiesData for the given ID, or null if not found or an error occurs.
     */
    public ProjectMetapropertiesData loadProperties(String projectId) {
        if (projectId == null || projectId.isEmpty()) {
            System.err.println("Error: Project ID is null or empty, cannot load properties.");
            return null;
        }
        Map<String, ProjectMetapropertiesData> allProperties = loadAllProperties();
        return allProperties.get(projectId); // Returns null if projectId is not in the map
    }

    /**
     * Saves the properties for a specific project ID.
     *
     * @param projectId The ID of the project whose properties are to be saved.
     * @param data      The ProjectMetapropertiesData to save for the project.
     * @return true if saving was successful, false otherwise.
     */
    public boolean saveProperties(String projectId, ProjectMetapropertiesData data) {
        if (propertiesFilePath == null) return false; // Service not initialized
        if (projectId == null || projectId.isEmpty() || data == null) {
            System.err.println("Error: Project ID or data is null/empty, cannot save properties.");
            return false;
        }

        Map<String, ProjectMetapropertiesData> allProperties = loadAllProperties();
        allProperties.put(projectId, data);
        return saveAllProperties(allProperties);
    }

} 