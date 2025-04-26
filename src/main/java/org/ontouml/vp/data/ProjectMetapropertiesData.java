package org.ontouml.vp.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.ontouml.ontouml4j.model.MultilingualText;
import org.ontouml.ontouml4j.model.Project;
import org.ontouml.ontouml4j.model.Resource;
import org.ontouml.vp.utils.OntoUMLStringUtils; // Assuming parsing utils are here

import java.util.List;
import java.util.stream.Collectors;

/**
 * Plain Old Java Object (POJO) representing the metadata of an OntoUML project
 * intended for JSON serialization and persistence.
 */
public class ProjectMetapropertiesData {

    private MultilingualText name; // Format: text@lang or just text
    private List<MultilingualText> alternativeNames; // Format: "text1@lang1\ntext2@lang2" or "text1\ntext2"
    private MultilingualText description; // Format: text@lang or just text
    private List<MultilingualText> editorialNotes; // Format: "text1@lang1\ntext2@lang2" or "text1\ntext2"
    private List<Resource> creators; // Format: "Name1 <URI1>\nName2 <URI2>" (requires parsing)
    private List<Resource> contributors; // Format: "Name1 <URI1>\nName2 <URI2>" (requires parsing)

    // Default constructor for Jackson deserialization
    public ProjectMetapropertiesData() {}

    // Constructor for creating from panel inputs or existing data
    @JsonCreator // Important for Jackson if multiple constructors exist or names don't match getters
    public ProjectMetapropertiesData(
            @JsonProperty("name") MultilingualText name,
            @JsonProperty("alternativeNames") List<MultilingualText> alternativeNames,
            @JsonProperty("description") MultilingualText description,
            @JsonProperty("editorialNotes") List<MultilingualText> editorialNotes,
            @JsonProperty("creators") List<Resource> creators,
            @JsonProperty("contributors") List<Resource> contributors) {
        this.name = name;
        this.alternativeNames = alternativeNames;
        this.description = description;
        this.editorialNotes = editorialNotes;
        this.creators = creators;
        this.contributors = contributors;
    }

    /**
     * Creates a ProjectMetapropertiesData instance from an OntoUML Project object.
     * Uses OntoUMLStringUtils to format the complex types back into the String
     * representation expected by the panel/JSON.
     *
     * @param project The OntoUML Project object.
     * @return A new ProjectMetapropertiesData instance.
     */
    public static ProjectMetapropertiesData fromOntoUMLProject(Project project) {
        if (project == null) return new ProjectMetapropertiesData(); // Return empty data if project is null

        // Pass the complex types directly to the constructor
        return new ProjectMetapropertiesData(
                project.getName(),
                project.getAlternativeNames(),
                project.getDescription(),
                project.getEditorialNotes(),
                project.getCreators(),
                project.getContributors()
        );
    }

    /**
     * Updates an OntoUML Project object based on the data in this instance.
     * Uses the placeholder parsing methods defined in ProjectMetapropertiesListener
     * (or ideally moves them to OntoUMLStringUtils).
     *
     * @param project The OntoUML Project object to update.
     */
    public void updateOntoUMLProject(Project project) {
        if (project == null) return;

        // Directly set the fields from the data object
        project.setName(this.name);
        project.setAlternativeNames(this.alternativeNames);
        project.setDescription(this.description);
        project.setEditorialNotes(this.editorialNotes);
        project.setCreators(this.creators);
        project.setContributors(this.contributors);
    }

    public MultilingualText getName() {
        return name;
    }

    public void setName(MultilingualText name) {
        this.name = name;
    }

    public List<MultilingualText> getAlternativeNames() {
        return alternativeNames;
    }

    public void setAlternativeNames(List<MultilingualText> alternativeNames) {
        this.alternativeNames = alternativeNames;
    }

    public MultilingualText getDescription() {
        return description;
    }

    public void setDescription(MultilingualText description) {
        this.description = description;
    }

    public List<MultilingualText> getEditorialNotes() {
        return editorialNotes;
    }

    public void setEditorialNotes(List<MultilingualText> editorialNotes) {
        this.editorialNotes = editorialNotes;
    }

    public List<Resource> getCreators() {
        return creators;
    }

    public void setCreators(List<Resource> creators) {
        this.creators = creators;
    }

    public List<Resource> getContributors() {
        return contributors;
    }

    public void setContributors(List<Resource> contributors) {
        this.contributors = contributors;
    }

} 