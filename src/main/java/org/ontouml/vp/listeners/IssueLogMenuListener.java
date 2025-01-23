package org.ontouml.vp.listeners;

import org.ontouml.vp.utils.ViewManagerUtils;
import org.ontouml.vp.views.IssueLogMenu;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;

public final class IssueLogMenuListener extends MouseAdapter {
  private final List<String> idModelElementList;
  private final JList<Object> messageList;

  public IssueLogMenuListener(List<String> list, JList<Object> messages) {
    super();
    idModelElementList = list;
    messageList = messages;
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    final Point p = e.getPoint();
    final int index = messageList.locationToIndex(p);

    messageList.setSelectedIndex(index);
  }

  @Override
  public void mouseExited(MouseEvent e) {
    messageList.clearSelection();
  }

  public void mouseReleased(MouseEvent e) {
    doPop(e);
  }

  private void doPop(MouseEvent e) {
    IssueLogMenu menu;
    String idModelElement = idModelElementList.get(messageList.locationToIndex(e.getPoint()));

    if (idModelElement == null) {
      menu = new IssueLogMenu();
    } else {
      menu = new IssueLogMenu(idModelElement);
      if (!ViewManagerUtils.isElementInAnyDiagram(idModelElement)) {
        menu.disableItem("Take me there!");
      }
    }

    menu.show(e.getComponent(), e.getX(), e.getY());
  }
}
