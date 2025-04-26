# Active Context: OntoUML Visual Paradigm Plugin

## Current Focus

The current activity involves the initial population and setup of the Memory Bank documentation based on the project overview and existing structure.

## Recent Changes

*   Created the core Memory Bank files:
    *   `projectbrief.md`
    *   `productContext.md`
    *   `techContext.md`
    *   `systemPatterns.md`
    *   `activeContext.md` (this file)
    *   `progress.md`

## Key Files & Concepts

*   `plugin.xml`: Central configuration file defining UI elements, actions, and controller mappings.
*   `pom.xml`: Defines build process, dependencies, and required local environment paths.
*   Controller/Service Pattern: Understanding the separation of UI handling (controllers) and business logic (services) is crucial.
*   VP OpenAPI: The interface for interacting with the Visual Paradigm environment.
*   `ontouml4j`: The core library for OntoUML-specific logic.

## Next Steps

*   Review the generated Memory Bank files for accuracy and completeness against the actual codebase (requires code exploration).
*   Begin investigating specific functionalities or addressing particular development tasks, updating the Memory Bank accordingly.
*   Refine the `progress.md` with more specific details once the current state is better understood.

## Learnings & Insights

*   The project relies heavily on configuration (`plugin.xml`, `pom.xml`) to connect its components and integrate with VP.
*   Development requires a specific local setup involving a VP installation and correctly configured paths in `pom.xml`. 