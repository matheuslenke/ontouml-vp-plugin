package org.ontouml.vp.model.vp2ontouml;

import java.util.*;

import com.vp.plugin.model.IRelationship;
import org.ontouml.ontouml4j.model.NaryRelation;
import org.ontouml.ontouml4j.model.view.Diagram;
import org.ontouml.ontouml4j.model.view.NaryRelationView;
import org.ontouml.ontouml4j.model.view.View;
import org.ontouml.ontouml4j.shape.Path;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.diagram.format.IShapeUIModelFillColor;
import com.vp.plugin.diagram.shape.INARYUIModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.INARY;

public class INaryUIModelTransformer {
  public static NaryRelationView transform(IDiagramElement sourceElement, Diagram diagram) {
    if (!(sourceElement instanceof INARYUIModel))
      return null;

    INARYUIModel source = (INARYUIModel) sourceElement;

    NaryRelationView target = new NaryRelationView();

    IDiagramElementTransformer.transform(source, target, NaryRelation.class);
    IShapeTransformer.transform(source, target);

    INARY inary = (INARY) sourceElement.getModelElement();

    // The first step is to get all IRelationshipEnd elements connected to the
    Iterator<?> fromEndIterable =  inary.fromRelationshipIterator();
    List<View> members = new ArrayList<>();
    fromEndIterable.forEachRemaining(item -> {
      IRelationship element = (IRelationship) item;
      Optional<?> diagramElement = Arrays.stream(element.getDiagramElements()).findFirst();
      if (diagramElement.isPresent()) {
        IDiagramElement diagramElementValue = (IDiagramElement) diagramElement.get();
        Optional<View> classView = diagram.getViewById(diagramElementValue.getId());
        classView.ifPresent(members::add);
      } else {
        System.out.println("No diagram element found for the relationship end.");
      }
      target.setMembers(members);
    });

    // Each end is connected to the INARY Element. In order to access the other
    // elements, we need then to get the opposite IRelationshipEnd.
    // for (IRelationshipEnd element : fromEndIterable) {
    // IRelationshipEnd oppositeEnd = element.getOppositeEnd();
    // Property endProperty = IPropertyTransformer.transform(oppositeEnd, project);
    // targetProperties.add(endProperty);
    // project.addProperty(endProperty);

    // IDiagramElement[] diagramElements = oppositeEnd.getDiagramElements();
    // System.out.println("Number of diagram elements: " + diagramElements.length);
    // for (IDiagramElement diagramElement : diagramElements) {
    // System.out.println("Diagram Element: " + diagramElement.getId() + " " +
    // diagramElement.getShapeType());
    // }
    // }

    target.setMembers(new ArrayList<View>());

    target.setPaths(new ArrayList<Path>());

    // if (parent instanceof IShapeUIModel) {
    // IShapeUIModel shape = (IShapeUIModel) parent;
    // shape.get
    // IModelElement[] members = shape.getSelectedShapeMembers();
    // }

    diagram.addElement(target);

//    exploreShapeUIGetters(source);

    return target;
  }

  /**
   * Calls only the 'get' and 'is' methods from the provided list
   * on an IShapeUI instance and prints their results.
   *
   * @param shapeUIInstance An object implementing the IShapeUI interface (or the
   *                        actual interface).
   */
  public static void exploreShapeUIGetters(IShapeUIModel shapeUIInstance) {
    if (shapeUIInstance == null) {
      System.out.println("Error: Provided IShapeUI instance is null.");
      return;
    }

    System.out.println("--- Exploring Getters for IShapeUI instance: " + shapeUIInstance.toString() + " ---");

    // --- Call Boolean Getters (is...) ---
    System.out.println("\n--- Boolean Getters (is...) ---");
    try {
      System.out.print("Calling isAutoFitsize(): ");
      boolean isAutoFit = shapeUIInstance.isAutoFitsize();
      System.out.println("Result: " + isAutoFit);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling isDisplayStereotypeIcon(): ");
      boolean isDisplayStereoIcon = shapeUIInstance.isDisplayStereotypeIcon();
      System.out.println("Result: " + isDisplayStereoIcon);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling isSupportDisplayImage(): ");
      boolean supportsImage = shapeUIInstance.isSupportDisplayImage();
      System.out.println("Result: " + supportsImage);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling isOverrideAppearanceWithStereotypeIcon(): ");
      boolean isOverride = shapeUIInstance.isOverrideAppearanceWithStereotypeIcon();
      System.out.println("Result: " + isOverride);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling isConnectToPoint(): ");
      boolean connectsToPoint = shapeUIInstance.isConnectToPoint();
      System.out.println("Result: " + connectsToPoint);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling isCoverConnector(): ");
      boolean coversConnector = shapeUIInstance.isCoverConnector();
      System.out.println("Result: " + coversConnector);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling isRequestDefaultSize(): ");
      boolean requestsDefault = shapeUIInstance.isRequestDefaultSize();
      System.out.println("Result: " + requestsDefault);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling isRequestFitSize(): ");
      boolean requestsFit = shapeUIInstance.isRequestFitSize();
      System.out.println("Result: " + requestsFit);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling isRequestFitSizeFromCenter(): ");
      boolean requestsFitCenter = shapeUIInstance.isRequestFitSizeFromCenter();
      System.out.println("Result: " + requestsFitCenter);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }

    // --- Call Object/String/Primitive Getters (get...) ---
    System.out.println("\n--- Object/String/Primitive Getters (get...) ---");
    try {
      System.out.print("Calling getFillColor(): ");
      IShapeUIModelFillColor fillColor = shapeUIInstance.getFillColor();
      System.out.println("Result: " + (fillColor != null ? fillColor.toString() : "null")); // Print object
                                                                                            // representation
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling getSelectedShapeMembers(): ");
      IModelElement[] members = shapeUIInstance.getSelectedShapeMembers();
      System.out.println("Result: " + Arrays.toString(members)); // Print array contents
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling getPresentationOption(): ");
      int presOption = shapeUIInstance.getPresentationOption();
      System.out.println("Result: " + presOption);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling getPrimitiveShapeType(): ");
      int shapeType = shapeUIInstance.getPrimitiveShapeType();
      System.out.println("Result: " + shapeType);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling getCustomText(): ");
      String customText = shapeUIInstance.getCustomText();
      System.out.println("Result: \"" + customText + "\"");
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling getShowStereotypeIconName(): ");
      int showStereoIconName = shapeUIInstance.getShowStereotypeIconName();
      System.out.println("Result: " + showStereoIconName);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling getShowAllocatedFrom(): ");
      int showAllocFrom = shapeUIInstance.getShowAllocatedFrom();
      System.out.println("Result: " + showAllocFrom);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling getShowAllocatedTo(): ");
      int showAllocTo = shapeUIInstance.getShowAllocatedTo();
      System.out.println("Result: " + showAllocTo);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling getDisplayImagePath(): ");
      String imgPath = shapeUIInstance.getDisplayImagePath();
      System.out.println("Result: \"" + imgPath + "\"");
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling getDisplayStereotypeIconStereotypeId(): ");
      String stereoIconId = shapeUIInstance.getDisplayStereotypeIconStereotypeId();
      System.out.println("Result: \"" + stereoIconId + "\"");
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling getHiddenStereotypeIds(): ");
      String[] hiddenStereoIds = shapeUIInstance.getHiddenStereotypeIds();
      System.out.println("Result: " + Arrays.toString(hiddenStereoIds)); // Print array contents
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling getShowTaggedValues(): ");
      int showTags = shapeUIInstance.getShowTaggedValues();
      System.out.println("Result: " + showTags);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling getHiddenTaggedValueIds(): ");
      String[] hiddenTagIds = shapeUIInstance.getHiddenTaggedValueIds();
      System.out.println("Result: " + Arrays.toString(hiddenTagIds)); // Print array contents
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling getRequestSetSizeOption(): ");
      int setSizeOption = shapeUIInstance.getRequestSetSizeOption();
      System.out.println("Result: " + setSizeOption);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling getParentConnectorHeaderLength(): ");
      int headerLength = shapeUIInstance.getParentConnectorHeaderLength();
      System.out.println("Result: " + headerLength);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling getParentConnectorLineLength(): ");
      int lineLength = shapeUIInstance.getParentConnectorLineLength();
      System.out.println("Result: " + lineLength);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling getParentConnectorDTheta(): ");
      double dTheta = shapeUIInstance.getParentConnectorDTheta();
      System.out.println("Result: " + dTheta);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }
    try {
      System.out.print("Calling getConnectionPointType(): ");
      int connPointType = shapeUIInstance.getConnectionPointType();
      System.out.println("Result: " + connPointType);
    } catch (Exception e) {
      System.out.println("Caught Exception: " + e.getMessage());
    }

    System.out.println("\n--- IShapeUI Getter Exploration Complete ---");
  }
}
