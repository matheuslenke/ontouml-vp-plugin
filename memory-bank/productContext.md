# Product Context: OntoUML Visual Paradigm Plugin

## Purpose

The primary purpose of this plugin is to bridge the gap between the Visual Paradigm (VP) modeling environment and the OntoUML modeling standard. It allows users familiar with VP to leverage its features while adhering to the specific constraints and constructs of OntoUML.

## Problem Solved

Visual Paradigm does not natively support the OntoUML standard. This plugin addresses this limitation by providing the necessary stereotypes, meta-properties, validation rules, and import/export capabilities for OntoUML directly within VP.

## Core Functionality

*   **OntoUML Stereotype & Property Application:** Allow users to apply OntoUML stereotypes (like `kind`, `mode`, `relator`) and meta-properties (like `isExtensional`, `isPowertype`) to VP model elements via context menus.
*   **Model Verification:** Validate VP models against OntoUML rules using the `ontouml4j` library.
*   **JSON Import/Export:** Enable importing models from OntoUML JSON format and exporting VP models to OntoUML JSON.
*   **gUFO Export:** Support exporting models to gUFO format.
*   **Diagram Generation:** Assist in generating diagrams based on model structure.

## User Experience Goals

The plugin should integrate seamlessly into the Visual Paradigm user interface, making OntoUML modeling feel like a native part of the tool. Actions should be accessible through standard VP mechanisms like toolbars and context menus. 