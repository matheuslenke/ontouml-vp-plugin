# Technical Context: OntoUML Visual Paradigm Plugin

## Technology Stack

*   **Primary Language:** Java 11 (as specified in `pom.xml`)
*   **Build System:** Apache Maven (using the Maven Wrapper `./mvnw`)
*   **Core Dependencies:**
    *   Visual Paradigm OpenAPI (`openapi.jar`, `vpplatform.jar`, etc.): Provided by the local Visual Paradigm installation.
    *   `ontouml4j`: A local library dependency providing OntoUML validation and manipulation capabilities.
    *   Jackson & Gson: Used for JSON processing (import/export).

## Build & Configuration

*   **Build Configuration:** `pom.xml` defines project dependencies, build plugins, and properties.
    *   Requires local system paths `visualparadigm.app.dir` and `visualparadigm.plugin.dir` to be set within `pom.xml` for linking against the VP installation during development builds.
*   **Plugin Definition:** `src/main/resources/plugin.xml` defines the plugin's ID (`it.unibz.inf.ontouml.vp`), UI elements (menus, toolbars, context actions), action controllers, and runtime dependencies (like including `ontouml4j` in the `lib/` folder of the packaged plugin).
*   **Packaging:** `mvn package` creates a distributable ZIP file.
*   **Development Installation:** `mvn install` copies the built plugin to the directory specified by `visualparadigm.plugin.dir`.
*   **Running:** `mvn exec:exec` launches Visual Paradigm with the development version of the plugin loaded (requires `mvn install` first).

## Development Environment

*   Requires a local installation of Visual Paradigm.
*   Requires the `ontouml4j` library to be available locally as specified in `pom.xml`.
*   Maven needs to be configured with the correct paths to the VP installation. 