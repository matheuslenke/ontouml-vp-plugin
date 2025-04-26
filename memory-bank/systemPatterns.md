# System Patterns: OntoUML Visual Paradigm Plugin

## Architecture Overview

The plugin follows a standard Visual Paradigm (VP) plugin structure, driven by configuration and event handling.

*   **Plugin Entry Point:** The `org.ontouml.vp.OntoUMLPlugin` class serves as the main entry point, referenced in `plugin.xml`. However, much of the functionality is initiated through actions defined in `plugin.xml`.
*   **Configuration-Driven UI:** The plugin's user interface elements (menus, toolbars, context menus) and their associated actions are declared in `src/main/resources/plugin.xml`. This file maps UI interactions to specific Java controller classes.
*   **Controller Pattern:** Classes within the `org.ontouml.vp.controllers` package handle user actions triggered from the UI (e.g., button clicks, menu selections). They typically orchestrate the workflow for a specific feature.
*   **Service Pattern:** Classes within the `org.ontouml.vp.services` package encapsulate the core business logic for features like model verification (`verification.*`), JSON import/export (`json.*`), gUFO export (`gufo.*`), etc. Controllers delegate tasks to these services.
*   **VP OpenAPI Integration:** The plugin interacts with the VP environment (accessing models, diagrams, UI components) through the Visual Paradigm OpenAPI (`openapi.jar`, `vpplatform.jar`).
*   **External Library Integration:** The `ontouml4j` library is used for OntoUML-specific logic (validation). JSON libraries (Jackson, Gson) handle serialization/deserialization.

## Key Implementation Paths

*   **Applying Stereotypes/Properties:** User right-clicks element -> VP shows context menu defined in `plugin.xml` -> User selects action -> `plugin.xml` invokes the corresponding `ApplyStereotypeController` or `ApplyPropertiesController` -> Controller uses VP OpenAPI to modify the selected model element.
*   **Model Verification:** User clicks toolbar button -> `plugin.xml` invokes `ModelVerificationController` -> Controller uses VP OpenAPI to access the current project model -> Controller uses `ontouml4j` via a service (`ModelVerificationService`) to perform validation -> Results are displayed to the user (likely via VP's message pane).
*   **JSON Export:** User selects menu item -> `plugin.xml` invokes `JsonImportAndExportController` -> Controller interacts with user for file selection -> Controller uses VP OpenAPI to read the model -> Controller uses a service (`JsonExportService`) to transform the VP model into OntoUML JSON format (using Jackson/Gson) -> Service writes the JSON to the selected file.

## Dependency Management & Packaging

*   Maven (`pom.xml`) manages project dependencies, including the VP OpenAPI jars (from the local VP installation) and other libraries.
*   The `plugin.xml` specifies which libraries (like `ontouml4j`) need to be included in the `lib/` directory of the final plugin package.
*   The `maven-assembly-plugin` (configured in `pom.xml`) likely handles packaging the compiled classes, resources (`plugin.xml`, icons), and necessary libraries into the final distributable ZIP archive. 