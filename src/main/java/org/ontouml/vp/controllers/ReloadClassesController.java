package org.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import org.ontouml.vp.OntoUMLPlugin;
import org.ontouml.vp.utils.ViewManagerUtils;

public class ReloadClassesController implements VPActionController {

  @Override
  public void performAction(VPAction action) {
    reloadPlugin();
  }

  @Override
  public void update(VPAction action) {}

  private void reloadPlugin() {
    System.out.println("----------------------------------------");
    System.out.println("Reloading OntoUML Plugin...");
    ApplicationManager app = ApplicationManager.instance();
    app.reloadPluginClasses(OntoUMLPlugin.PLUGIN_ID);
    System.out.println("Plugin reloaded!");
    ViewManagerUtils.simpleDialog("Plugin reloaded!");
  }
}
