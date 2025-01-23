package org.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IModelElement;
import org.ontouml.vp.model.uml.Association;
import org.ontouml.vp.utils.OntoUMLConstraintsManager;
import org.ontouml.vp.utils.StereotypesManager;
import org.ontouml.vp.utils.VPContextUtils;
import org.ontouml.vp.utils.ViewManagerUtils;
import java.util.Set;
import java.util.stream.Collectors;

public class ApplyAssociationStereotypeMenuManager extends ApplyStereotypeMenuManager {

  private final ApplyAssociationStereotypeId associationStereotypeId;
  private final Set<IModelElement> elements;
  private final Set<IModelElement> elementsWhereStereotypeIsAllowed;
  private final Set<IModelElement> elementsWhereStereotypeIsAllowedOnlyIfInverted;
  private final boolean hasSomeElementThatRequiresInversion;
  private final boolean allElementsCanReceiveTheStereotype;

  ApplyAssociationStereotypeMenuManager(VPAction action, VPContext context) {
    this.action = action;
    this.context = context;
    this.associationStereotypeId =
        ApplyAssociationStereotypeId.getFromActionId(action.getActionId());

    this.elements = VPContextUtils.getModelElements(context);
    this.elementsWhereStereotypeIsAllowed = getElementsWhereStereotypeIsAllowed(elements);
    this.elementsWhereStereotypeIsAllowedOnlyIfInverted =
        getElementsWhereStereotypeIsAllowedIfInverted(elements);

    this.allElementsCanReceiveTheStereotype =
        elements.stream()
            .allMatch(
                element ->
                    elementsWhereStereotypeIsAllowed.contains(element)
                        || elementsWhereStereotypeIsAllowedOnlyIfInverted.contains(element));

    this.hasSomeElementThatRequiresInversion =
        elementsWhereStereotypeIsAllowedOnlyIfInverted.stream()
            .anyMatch(element -> !elementsWhereStereotypeIsAllowed.contains(element));
  }

  @Override
  public void performAction() {
    boolean shouldProceed = true;

    if (shouldWarnAboutInvertingAssociations())
      shouldProceed = ViewManagerUtils.associationInvertionWarningDialog();

    if (!shouldProceed) return;

    elements.stream()
        .map(e -> (IAssociation) e)
        .filter(Association::holdsBetweenClasses)
        .forEach(
            association -> {
              if (doesRequireInverting(association))
                Association.invertAssociation(association, true);

              StereotypesManager.applyStereotype(
                  association, associationStereotypeId.getStereotype());
            });
  }

  @Override
  public void update() {
    updateMenuLabel();
    updateMenuEnable();
  }

  private void updateMenuLabel() {
    String label =
        hasSomeElementThatRequiresInversion
            ? associationStereotypeId.getDefaultLabel() + " (inverted)"
            : associationStereotypeId.getDefaultLabel();

    if (associationStereotypeId.isFixed()) action.setLabel(label);
    else if (allElementsCanReceiveTheStereotype) action.setLabel(label);
    else action.setLabel(associationStereotypeId.getDefaultLabel());
  }

  private void updateMenuEnable() {
    if (associationStereotypeId.isFixed()) {
      boolean holdsBetweenClasses =
          Association.holdsBetweenClasses((IAssociation) context.getModelElement());
      action.setEnabled(holdsBetweenClasses);
    } else {
      action.setEnabled(allElementsCanReceiveTheStereotype);
    }
  }

  private Set<IModelElement> getElementsWhereStereotypeIsAllowed(Set<IModelElement> elements) {
    String stereotype = associationStereotypeId.getStereotype();

    return elements.stream()
        .map(e -> (IAssociation) e)
        .filter(Association::holdsBetweenClasses)
        .filter(a -> OntoUMLConstraintsManager.isStereotypeAllowed(a, stereotype))
        .collect(Collectors.toSet());
  }

  private Set<IModelElement> getElementsWhereStereotypeIsAllowedIfInverted(
      Set<IModelElement> elements) {
    String stereotype = associationStereotypeId.getStereotype();

    return elements.stream()
        .map(e -> (IAssociation) e)
        .filter(Association::holdsBetweenClasses)
        .filter(a -> OntoUMLConstraintsManager.isStereotypeAllowedIfInverted(a, stereotype))
        .collect(Collectors.toSet());
  }

  private boolean shouldWarnAboutInvertingAssociations() {
    return elements.stream().anyMatch(element -> doesRequireInverting((IAssociation) element));
  }

  private boolean doesRequireInverting(IAssociation association) {
    return elementsWhereStereotypeIsAllowedOnlyIfInverted.contains(association)
        && !elementsWhereStereotypeIsAllowed.contains(association);
  }
}
