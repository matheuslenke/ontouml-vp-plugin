package org.ontouml.vp.views;

import org.ontouml.vp.utils.ViewManagerUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

@SuppressWarnings("serial")
public final class IssueLogMenu extends JPopupMenu {
  private JMenuItem takeMeThere;
  private JMenuItem openSpec;
  private ActionListener menuListener;

  public IssueLogMenu() {}

  public IssueLogMenu(String idModelElement) {

    menuListener =
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
              case "Take me there!":
                System.out.println("Firing 'Highlight'");
                ViewManagerUtils.highlightDiagramElement(idModelElement);
                break;
              case "Open Specification":
                System.out.println("Firing 'Open Specification'");
                ViewManagerUtils.openSpecDiagramElement(idModelElement);
                break;
              default:
                break;
            }
          }
        };

    takeMeThere =
        new JMenuItem(
            "Take me there!",
            new ImageIcon(ViewManagerUtils.getFilePath(ViewManagerUtils.NAVIGATION_LOGO)));
    takeMeThere.addActionListener(menuListener);
    openSpec =
        new JMenuItem(
            "Open Specification",
            new ImageIcon(ViewManagerUtils.getFilePath(ViewManagerUtils.MORE_HORIZ_LOGO)));
    openSpec.addActionListener(menuListener);
    add(takeMeThere);
    add(openSpec);
  }

  public void disableItem(String item) {
    switch (item) {
      case "Take me there!":
        takeMeThere.setEnabled(false);
        break;
      case "Open Specification":
        openSpec.setEnabled(false);
        break;
      default:
        break;
    }
  }
}
