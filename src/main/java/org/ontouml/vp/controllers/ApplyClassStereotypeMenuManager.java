package org.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.model.IClass;
import org.ontouml.vp.model.uml.Class;
import org.ontouml.vp.utils.OntoUMLConstraintsManager;
import org.ontouml.vp.utils.StereotypesManager;
import org.ontouml.vp.utils.VPContextUtils;

public class ApplyClassStereotypeMenuManager extends ApplyStereotypeMenuManager {

  private final ApplyClassStereotypeId classStereotypeId;

  ApplyClassStereotypeMenuManager(VPAction action, VPContext context) {
    this.action = action;
    this.context = context;
    this.classStereotypeId = ApplyClassStereotypeId.getFromActionId(action.getActionId());
  }

  @Override
  public void performAction() {
    VPContextUtils.getModelElements(context)
        .forEach(
            element -> {
              StereotypesManager.applyStereotype(element, classStereotypeId.getStereotype());
              if (classStereotypeId.hasKnownNature()) {
                Class.setDefaultRestrictedTo((IClass) element);
              }
            });
  }

  @Override
  public void update() {
    if (classStereotypeId.isFixed()) {
      action.setEnabled(true);
    } else {
      boolean shouldEnable = isStereotypeAllowedForAllSelectedElements(classStereotypeId);
      action.setEnabled(shouldEnable);
    }
  }

  private boolean isStereotypeAllowedForAllSelectedElements(
      ApplyClassStereotypeId classStereotypeId) {
    String stereotype = classStereotypeId.getStereotype();
    return VPContextUtils.getModelElements(context).stream()
        .allMatch(
            element -> OntoUMLConstraintsManager.isStereotypeAllowed((IClass) element, stereotype));
  }
}
