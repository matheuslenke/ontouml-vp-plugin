package org.ontouml.vp.model.vp2ontouml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.*;
import com.vp.plugin.model.property.IReferencedBy;

import v.ccr.re;

import org.ontouml.ontouml4j.model.NaryRelation;
import org.ontouml.ontouml4j.model.Project;
import org.ontouml.ontouml4j.model.Property;

public class INaryTransformer {
  public static NaryRelation transform(IModelElement sourceElement, Project project) {
    if (!(sourceElement instanceof INARY)) {
      return null;
    }

    INARY naryElement = (INARY) sourceElement;
    NaryRelation target = new NaryRelation();

    IModelElementTransformer.transform(sourceElement, target);
    ITaggedValueTransformer.transform(sourceElement, target);
    IStereotypeTransformer.transform(sourceElement, target);

    List<Property> targetProperties = new ArrayList<>();

    // The first step is to get all IRelationshipEnd elements connected to the
    Iterable<IRelationshipEnd> fromEndIterable = () -> naryElement.fromRelationshipEndIterator();

    Iterator relationShipIterable = naryElement.fromRelationshipIterator();

    relationShipIterable.forEachRemaining(item -> {
      if (item instanceof IRelationship) {
        IRelationship relationship = (IRelationship) item;
        IModelElement from = relationship.getFrom();
        IModelElement to = relationship.getTo();
        System.out.println("From: " + from);
        System.out.println("To: " + to);
        System.out.println(relationship.getName());
      }
    });

    // Each end is connected to the INARY Element. In order to access the other
    // elements, we need then to get the opposite IRelationshipEnd.
    for (IRelationshipEnd element : fromEndIterable) {
      IRelationshipEnd oppositeEnd = element.getOppositeEnd();
      Property endProperty = IPropertyTransformer.transform(oppositeEnd);
      targetProperties.add(endProperty);
      project.addProperty(endProperty);

      IDiagramElement[] diagramElements = oppositeEnd.getDiagramElements();
      System.out.println("Number of diagram elements: " + diagramElements.length);
      for (IDiagramElement diagramElement : diagramElements) {
        System.out.println("Diagram Element: " + diagramElement.getId() + " " + diagramElement.getShapeType());
      }

      // callAllGettersAndPrint(elementEnd);

      // IModelElement to = relationship.getTo();
      // IModelElement from = relationship.getFrom();
      // IModelElementTransformer.transform(relationship, project);
    }

    target.setProperties(targetProperties);
    project.addElement(target);

    // exploreInaryMethods(naryElement);

    return target;
  }

  /**
   * Calls all identified 'get' methods on the provided IAssociationEnd object
   * and prints their return values to the console.
   *
   * @param associationEnd The IAssociationEnd object to call methods on.
   */
  public static void callAllGettersAndPrint(IAssociationEnd associationEnd) {
    if (associationEnd == null) {
      System.out.println("Provided IAssociationEnd object is null.");
      return;
    }

    System.out.println("--- Calling Getters for IAssociationEnd ---");

    try {
      System.out.println("getAggregationKind: " + associationEnd.getAggregationKind());
      System.out.println("getAnalysisItemDiagramIds: " + Arrays.toString(associationEnd.getAnalysisItemDiagramIds()));

      // --- Methods requiring an index (using 0 as an example) ---
      // Note: Check counts (e.g., analysisItemModelCount()) before calling these in
      // production
      // int index = 0;
      // System.out
      // .println("getAnalysisItemModelByIndex(" + index + "): " +
      // associationEnd.getAnalysisItemModelByIndex(index));
      // System.out
      // .println("getRedefinedPropertyByIndex(" + index + "): " +
      // associationEnd.getRedefinedPropertyByIndex(index));
      // System.out
      // .println("getSubsettedPropertyByIndex(" + index + "): " +
      // associationEnd.getSubsettedPropertyByIndex(index));
      // System.out
      // .println("getSyncMappingModelByIndex(" + index + "): " +
      // associationEnd.getSyncMappingModelByIndex(index));
      // // --- End of index-based methods ---

      System.out.println("getDefaultValue: " + associationEnd.getDefaultValue());
      System.out.println("getEjbCodeDetail: " + associationEnd.getEjbCodeDetail());
      System.out.println("getJavaCodeAttributeName: " + associationEnd.getJavaCodeAttributeName());
      System.out.println("getMultiplicity: " + associationEnd.getMultiplicity());
      System.out.println("getMultiplicityDetail: " + associationEnd.getMultiplicityDetail());
      System.out.println("getNavigable: " + associationEnd.getNavigable());
      System.out.println("getOrmDetail: " + associationEnd.getOrmDetail());
      System.out.println("getPropertyStrings: " + associationEnd.getPropertyStrings());
      System.out.println("getQualifier: " + associationEnd.getQualifier());
      System.out.println("getQualityReason: " + associationEnd.getQualityReason());
      System.out.println("getQualityScore: " + associationEnd.getQualityScore());
      System.out.println("getReferencedAttribute: " + associationEnd.getReferencedAttribute());
      System.out.println("getRepresentativeAttribute: " + associationEnd.getRepresentativeAttribute());
      System.out.println("getTaggedValues: " + associationEnd.getTaggedValues());
      System.out.println("getType: " + associationEnd.getType());
      System.out.println("getTypeAsElement: " + associationEnd.getTypeAsElement());
      System.out.println("getTypeAsModel: " + associationEnd.getTypeAsModel());
      System.out.println("getTypeAsString: " + associationEnd.getTypeAsString());
      System.out.println("getTypeAsText: " + associationEnd.getTypeAsText());
      System.out.println("getTypeModifier: " + associationEnd.getTypeModifier());
      System.out.println("getVisibility: " + associationEnd.getVisibility());

    } catch (Exception e) {
      System.err.println("An error occurred while calling getter methods: " + e.getMessage());
      e.printStackTrace(); // Print stack trace for debugging
    }

    System.out.println("--- Finished Calling Getters ---");
  }

  // Example usage (requires an instance of IAssociationEnd)
  public static void main(String[] args) {
    // You would need to obtain an actual instance of IAssociationEnd here
    // For example, from your Visual Paradigm plugin or model context.
    IAssociationEnd exampleAssociationEnd = null; // Replace null with an actual object

    if (exampleAssociationEnd != null) {
      callAllGettersAndPrint(exampleAssociationEnd);
    } else {
      System.out.println("Cannot run example: exampleAssociationEnd is null.");
      System.out.println("Please provide a valid IAssociationEnd instance.");
    }
  }

  /**
   * Calls all documented methods on an INARY instance and prints results or
   * status.
   * Note: Many methods require specific parameters or state which cannot be
   * simulated accurately without a live Visual Paradigm environment and model.
   * Calls requiring complex objects, specific indexes, or names might fail
   * or produce non-representative results when called with null/default values.
   *
   * @param inaryInstance An object implementing the INARY interface.
   */
  public static void exploreInaryMethods(INARY inaryInstance) {
    if (inaryInstance == null) {
      System.out.println("Error: Provided INARY instance is null.");
      return;
    }

    System.out.println("--- Exploring INARY instance: " + inaryInstance.toString() + " ---"); // Basic info

    // --- Add/Create Methods (Attempting with null/dummy, likely to fail without
    // real objects) ---
    System.out.println("\n--- Add/Create Methods ---");
    try {
      System.out.print("Calling addAnalysisItemModel(null): ");
      inaryInstance.addAnalysisItemModel(null); // Requires IModelElement
      System.out.println("Call succeeded (may not have done anything meaningful).");
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling addAttribute(null): ");
      inaryInstance.addAttribute(null); // Requires IAttribute
      System.out.println("Call succeeded (may not have done anything meaningful).");
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling addOperation(null): ");
      inaryInstance.addOperation(null); // Requires IOperation
      System.out.println("Call succeeded (may not have done anything meaningful).");
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling addStereotype((IStereotype)null): ");
      inaryInstance.addStereotype((IStereotype) null); // Requires IStereotype
      System.out.println("Call succeeded (may not have done anything meaningful).");
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      String stereotypeName = "TestStereotype";
      System.out.print("Calling addStereotype(\"" + stereotypeName + "\"): ");
      inaryInstance.addStereotype(stereotypeName);
      System.out.println("Call succeeded.");
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      String deprecatedStereotypeName = "DeprecatedStereotype";
      System.out.print("Calling addStereotypes(\"" + deprecatedStereotypeName + "\") [DEPRECATED]: ");
      inaryInstance.addStereotypes(deprecatedStereotypeName); // Deprecated
      System.out.println("Call succeeded.");
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling addSyncMappingModel(null): ");
      inaryInstance.addSyncMappingModel(null); // Requires IModelElement
      System.out.println("Call succeeded (may not have done anything meaningful).");
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling createAttribute(): ");
      IAttribute createdAttribute = inaryInstance.createAttribute();
      System.out.println("Result: " + createdAttribute);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling createOperation(): ");
      IOperation createdOperation = inaryInstance.createOperation();
      System.out.println("Result: " + createdOperation);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }

    // --- Count Methods ---
    System.out.println("\n--- Count Methods ---");
    try {
      System.out.print("Calling analysisItemModelCount(): ");
      int analysisCount = inaryInstance.analysisItemModelCount();
      System.out.println("Result: " + analysisCount);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling attributeCount(): ");
      int attrCount = inaryInstance.attributeCount();
      System.out.println("Result: " + attrCount);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling fromRelationshipCount(): ");
      int fromRelCount = inaryInstance.fromRelationshipCount();
      System.out.println("Result: " + fromRelCount);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling fromRelationshipEndCount(): ");
      int fromRelEndCount = inaryInstance.fromRelationshipEndCount();
      System.out.println("Result: " + fromRelEndCount);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling operationCount(): ");
      int opCount = inaryInstance.operationCount();
      System.out.println("Result: " + opCount);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling stereotypeCount(): ");
      int stereoCount = inaryInstance.stereotypeCount();
      System.out.println("Result: " + stereoCount);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling stereotypesCount() [DEPRECATED]: ");
      int deprecatedStereoCount = inaryInstance.stereotypesCount(); // Deprecated
      System.out.println("Result: " + deprecatedStereoCount);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling syncMappingModelCount(): ");
      int syncCount = inaryInstance.syncMappingModelCount();
      System.out.println("Result: " + syncCount);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling toRelationshipCount(): ");
      int toRelCount = inaryInstance.toRelationshipCount();
      System.out.println("Result: " + toRelCount);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling toRelationshipEndCount(): ");
      int toRelEndCount = inaryInstance.toRelationshipEndCount();
      System.out.println("Result: " + toRelEndCount);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }

    // --- Iterator Methods ---
    System.out.println("\n--- Iterator Methods ---");
    printIteratorContents("analysisItemModelIterator()", inaryInstance.analysisItemModelIterator());
    printIteratorContents("attributeIterator()", inaryInstance.attributeIterator());
    printIteratorContents("fromRelationshipEndIterator()", inaryInstance.fromRelationshipEndIterator());
    printIteratorContents("fromRelationshipIterator()", inaryInstance.fromRelationshipIterator());
    printIteratorContents("operationIterator()", inaryInstance.operationIterator());
    printIteratorContents("stereotypeIterator()", inaryInstance.stereotypeIterator()); // Iterator over String names
    printIteratorContents("stereotypeModelIterator()", inaryInstance.stereotypeModelIterator()); // Iterator over
                                                                                                 // IStereotype
    printIteratorContents("stereotypesIterator() [DEPRECATED]", inaryInstance.stereotypesIterator()); // Deprecated
    printIteratorContents("stereotypesModelIterator() [DEPRECATED]", inaryInstance.stereotypesModelIterator()); // Deprecated
    printIteratorContents("syncMappingModelIterator()", inaryInstance.syncMappingModelIterator());
    printIteratorContents("toRelationshipEndIterator()", inaryInstance.toRelationshipEndIterator());
    printIteratorContents("toRelationshipIterator()", inaryInstance.toRelationshipIterator());

    // --- Getter Methods ---
    System.out.println("\n--- Getter Methods ---");
    try {
      System.out.print("Calling getAnalysisItemDiagramIds(): ");
      String[] analysisDiagramIds = inaryInstance.getAnalysisItemDiagramIds();
      System.out.println("Result: " + Arrays.toString(analysisDiagramIds));
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      int index = 0;
      System.out.print("Calling getAnalysisItemModelByIndex(" + index + "): ");
      IModelElement analysisModel = inaryInstance.getAnalysisItemModelByIndex(index);
      System.out.println("Result: " + analysisModel);
    } catch (Exception e) {
      System.out.println("Caught Exception (Index " + 0 + " might be invalid): " + e.getMessage());
    }
    try {
      int index = 0;
      System.out.print("Calling getAttributeByIndex(" + index + "): ");
      IAttribute attr = inaryInstance.getAttributeByIndex(index);
      System.out.println("Result: " + attr);
    } catch (Exception e) {
      System.out.println("Caught Exception (Index " + 0 + " might be invalid): " + e.getMessage());
    }
    try {
      String name = "testAttributeName";
      System.out.print("Calling getAttributeByName(\"" + name + "\"): ");
      IAttribute attrByName = inaryInstance.getAttributeByName(name);
      System.out.println("Result: " + attrByName);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      int index = 0;
      System.out.print("Calling getOperationByIndex(" + index + "): ");
      IOperation op = inaryInstance.getOperationByIndex(index);
      System.out.println("Result: " + op);
    } catch (Exception e) {
      System.out.println("Caught Exception (Index " + 0 + " might be invalid): " + e.getMessage());
    }
    try {
      String name = "testOperationName";
      System.out.print("Calling getOperationByName(\"" + name + "\"): ");
      IOperation opByName = inaryInstance.getOperationByName(name);
      System.out.println("Result: " + opByName);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling getQualityReason(): ");
      String qualityReason = inaryInstance.getQualityReason();
      System.out.println("Result: " + qualityReason);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling getQualityScore(): ");
      int qualityScore = inaryInstance.getQualityScore();
      System.out.println("Result: " + qualityScore);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      int index = 0;
      System.out.print("Calling getSyncMappingModelByIndex(" + index + "): ");
      IModelElement syncModel = inaryInstance.getSyncMappingModelByIndex(index);
      System.out.println("Result: " + syncModel);
    } catch (Exception e) {
      System.out.println("Caught Exception (Index " + 0 + " might be invalid): " + e.getMessage());
    }
    try {
      System.out.print("Calling getTaggedValues(): ");
      ITaggedValueContainer taggedValues = inaryInstance.getTaggedValues();
      System.out.println("Result: " + taggedValues); // Further exploration would require ITaggedValueContainer methods
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling getVisibility(): ");
      String visibility = inaryInstance.getVisibility();
      System.out.println("Result: " + visibility);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }

    // --- Boolean Check Methods ---
    System.out.println("\n--- Boolean Check Methods ---");
    try {
      String name = "TestStereotype"; // Use the one we tried to add
      System.out.print("Calling hasStereotype(\"" + name + "\"): ");
      boolean hasStereo = inaryInstance.hasStereotype(name);
      System.out.println("Result: " + hasStereo);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      String name = "DeprecatedStereotype"; // Use the one we tried to add
      System.out.print("Calling hasStereotypes(\"" + name + "\") [DEPRECATED]: ");
      boolean hasDeprecatedStereo = inaryInstance.hasStereotypes(name); // Deprecated
      System.out.println("Result: " + hasDeprecatedStereo);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling isAbstract(): ");
      boolean isAbstract = inaryInstance.isAbstract();
      System.out.println("Result: " + isAbstract);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling isActive(): ");
      boolean isActive = inaryInstance.isActive();
      System.out.println("Result: " + isActive);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling isLeaf(): ");
      boolean isLeaf = inaryInstance.isLeaf();
      System.out.println("Result: " + isLeaf);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling isRoot(): ");
      boolean isRoot = inaryInstance.isRoot();
      System.out.println("Result: " + isRoot);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }

    // --- Remove Methods (Attempting with null/dummy/index 0, likely to fail) ---
    System.out.println("\n--- Remove Methods ---");
    try {
      System.out.print("Calling removeAnalysisItemModel(null): ");
      inaryInstance.removeAnalysisItemModel(null); // Requires IModelElement
      System.out.println("Call succeeded (may not have done anything meaningful).");
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      int index = 0;
      System.out.print("Calling removeAnalysisItemModelByIndex(" + index + "): ");
      inaryInstance.removeAnalysisItemModelByIndex(index);
      System.out.println("Call succeeded (may not have done anything meaningful).");
    } catch (Exception e) {
      System.out.println("Caught Exception (Index " + 0 + " might be invalid or item non-existent): " + e.getMessage());
    }
    try {
      System.out.print("Calling removeAttribute(null): ");
      inaryInstance.removeAttribute(null); // Requires IAttribute
      System.out.println("Call succeeded (may not have done anything meaningful).");
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      int index = 0;
      System.out.print("Calling removeAttributeByIndex(" + index + "): ");
      inaryInstance.removeAttributeByIndex(index);
      System.out.println("Call succeeded (may not have done anything meaningful).");
    } catch (Exception e) {
      System.out.println("Caught Exception (Index " + 0 + " might be invalid or item non-existent): " + e.getMessage());
    }
    try {
      System.out.print("Calling removeOperation(null): ");
      inaryInstance.removeOperation(null); // Requires IOperation
      System.out.println("Call succeeded (may not have done anything meaningful).");
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      int index = 0;
      System.out.print("Calling removeOperationByIndex(" + index + "): ");
      inaryInstance.removeOperationByIndex(index);
      System.out.println("Call succeeded (may not have done anything meaningful).");
    } catch (Exception e) {
      System.out.println("Caught Exception (Index " + 0 + " might be invalid or item non-existent): " + e.getMessage());
    }
    try {
      System.out.print("Calling removeStereotype((IStereotype)null): ");
      inaryInstance.removeStereotype((IStereotype) null); // Requires IStereotype
      System.out.println("Call succeeded (may not have done anything meaningful).");
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      String name = "TestStereotype"; // Use the one we tried to add
      System.out.print("Calling removeStereotype(\"" + name + "\"): ");
      inaryInstance.removeStereotype(name);
      System.out.println("Call succeeded.");
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      String name = "DeprecatedStereotype"; // Use the one we tried to add
      System.out.print("Calling removeStereotypes(\"" + name + "\") [DEPRECATED]: ");
      inaryInstance.removeStereotypes(name); // Deprecated
      System.out.println("Call succeeded.");
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling removeSyncMappingModel(null): ");
      inaryInstance.removeSyncMappingModel(null); // Requires IModelElement
      System.out.println("Call succeeded (may not have done anything meaningful).");
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      int index = 0;
      System.out.print("Calling removeSyncMappingModelByIndex(" + index + "): ");
      inaryInstance.removeSyncMappingModelByIndex(index);
      System.out.println("Call succeeded (may not have done anything meaningful).");
    } catch (Exception e) {
      System.out.println("Caught Exception (Index " + 0 + " might be invalid or item non-existent): " + e.getMessage());
    }

    // --- Reorder Methods (Attempting with empty arrays, likely to fail/do nothing)
    // ---
    System.out.println("\n--- Reorder Methods ---");
    try {
      System.out.print("Calling reorderAttribute(new IAttribute[0]): ");
      inaryInstance.reorderAttribute(new IAttribute[0]); // Requires array of IAttribute
      System.out.println("Call succeeded (may not have done anything meaningful).");
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling reorderOperation(new IOperation[0]): ");
      inaryInstance.reorderOperation(new IOperation[0]); // Requires array of IOperation
      System.out.println("Call succeeded (may not have done anything meaningful).");
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }

    // --- Setter Methods ---
    System.out.println("\n--- Setter Methods ---");
    try {
      boolean value = true;
      System.out.print("Calling setAbstract(" + value + "): ");
      inaryInstance.setAbstract(value);
      System.out.println("Call succeeded. Current value: " + inaryInstance.isAbstract());
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      boolean value = true;
      System.out.print("Calling setActive(" + value + "): ");
      inaryInstance.setActive(value);
      System.out.println("Call succeeded. Current value: " + inaryInstance.isActive());
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      String[] ids = { "DiagramID1", "DiagramID2" };
      System.out.print("Calling setAnalysisItemDiagramIds(" + Arrays.toString(ids) + "): ");
      inaryInstance.setAnalysisItemDiagramIds(ids);
      System.out
          .println("Call succeeded. Current value: " + Arrays.toString(inaryInstance.getAnalysisItemDiagramIds()));
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      boolean value = true;
      System.out.print("Calling setLeaf(" + value + "): ");
      inaryInstance.setLeaf(value);
      System.out.println("Call succeeded. Current value: " + inaryInstance.isLeaf());
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      String reason = "TestQualityReason123";
      System.out.print("Calling setQualityReason(\"" + reason + "\"): ");
      inaryInstance.setQualityReason(reason);
      System.out.println("Call succeeded. Current value: " + inaryInstance.getQualityReason());
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      int score = 75;
      System.out.print("Calling setQualityScore(" + score + "): ");
      inaryInstance.setQualityScore(score);
      System.out.println("Call succeeded. Current value: " + inaryInstance.getQualityScore());
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      boolean value = true;
      System.out.print("Calling setRoot(" + value + "): ");
      inaryInstance.setRoot(value);
      System.out.println("Call succeeded. Current value: " + inaryInstance.isRoot());
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling setTaggedValues(null): ");
      inaryInstance.setTaggedValues(null); // Requires ITaggedValueContainer
      System.out
          .println("Call succeeded (likely set to null/empty). Current value: " + inaryInstance.getTaggedValues());
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      String visibility = "public"; // Common values: public, private, protected, package
      System.out.print("Calling setVisibility(\"" + visibility + "\"): ");
      inaryInstance.setVisibility(visibility);
      System.out.println("Call succeeded. Current value: " + inaryInstance.getVisibility());
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }

    // --- To Array Methods ---
    System.out.println("\n--- To Array Methods ---");
    try {
      System.out.print("Calling toAnalysisItemModelArray(): ");
      IModelElement[] analysisArray = inaryInstance.toAnalysisItemModelArray();
      System.out.println("Result: " + Arrays.toString(analysisArray));
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling toAttributeArray(): ");
      IAttribute[] attrArray = inaryInstance.toAttributeArray();
      System.out.println("Result: " + Arrays.toString(attrArray));
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling toFromRelationshipArray(): ");
      ISimpleRelationship[] fromRelArray = inaryInstance.toFromRelationshipArray();
      System.out.println("Result: " + Arrays.toString(fromRelArray));
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling toFromRelationshipEndArray(): ");
      IRelationshipEnd[] fromRelEndArray = inaryInstance.toFromRelationshipEndArray();
      System.out.println("Result: " + Arrays.toString(fromRelEndArray));
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling toOperationArray(): ");
      IOperation[] opArray = inaryInstance.toOperationArray();
      System.out.println("Result: " + Arrays.toString(opArray));
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling toStereotypeArray(): ");
      String[] stereoArray = inaryInstance.toStereotypeArray();
      System.out.println("Result: " + Arrays.toString(stereoArray));
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling toStereotypeModelArray(): ");
      IStereotype[] stereoModelArray = inaryInstance.toStereotypeModelArray();
      System.out.println("Result: " + Arrays.toString(stereoModelArray));
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling toStereotypesArray() [DEPRECATED]: ");
      String[] deprecatedStereoArray = inaryInstance.toStereotypesArray(); // Deprecated
      System.out.println("Result: " + Arrays.toString(deprecatedStereoArray));
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling toStereotypesModelArray() [DEPRECATED]: ");
      IStereotype[] deprecatedStereoModelArray = inaryInstance.toStereotypesModelArray(); // Deprecated
      System.out.println("Result: " + Arrays.toString(deprecatedStereoModelArray));
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling toSyncMappingModelArray(): ");
      IModelElement[] syncArray = inaryInstance.toSyncMappingModelArray();
      System.out.println("Result: " + Arrays.toString(syncArray));
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling toToRelationshipArray(): ");
      ISimpleRelationship[] toRelArray = inaryInstance.toToRelationshipArray();
      System.out.println("Result: " + Arrays.toString(toRelArray));
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling toToRelationshipEndArray(): ");
      IRelationshipEnd[] toRelEndArray = inaryInstance.toToRelationshipEndArray();
      System.out.println("Result: " + Arrays.toString(toRelEndArray));
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }

    System.out.println("\n--- INARY Exploration Complete ---");
  }

  /**
   * Helper method to print the contents of an iterator.
   */
  private static void printIteratorContents(String methodName, Iterator<?> iterator) {
    try {
      System.out.print("Calling " + methodName + ": ");
      if (iterator == null) {
        System.out.println("Result: Iterator is null.");
        return;
      }
      if (!iterator.hasNext()) {
        System.out.println("Result: Iterator is empty.");
        return;
      }
      StringBuilder sb = new StringBuilder("[");
      while (iterator.hasNext()) {
        sb.append(iterator.next());
        if (iterator.hasNext()) {
          sb.append(", ");
        }
      }
      sb.append("]");
      System.out.println("Result: " + sb.toString());
    } catch (Exception e) {
      System.out.println("Caught Exception while iterating: " + e.getMessage());
    }
  }

  // --- Example Usage (Requires a Mock or Real INARY object) ---
  /*
   * public static void main(String[] args) {
   * // In a real scenario, you would get this from the VP API
   * // e.g., IModelElement element = V PApi.getSelectedElement();
   * // if (element instanceof INARY) {
   * // INARY inaryInstance = (INARY) element;
   * // exploreInaryMethods(inaryInstance);
   * // }
   * 
   * // For demonstration, you might need a mock object (using a framework like
   * Mockito
   * // or by creating a simple dummy class that implements INARY),
   * // but creating a meaningful mock is complex without knowing the expected
   * behaviors.
   * 
   * System.out.
   * println("Please provide a valid INARY instance to run the exploration.");
   * // Example with a hypothetical MockInary (won't compile without definition)
   * // INARY mockInstance = new MockInary();
   * // exploreInaryMethods(mockInstance);
   * }
   */

}
