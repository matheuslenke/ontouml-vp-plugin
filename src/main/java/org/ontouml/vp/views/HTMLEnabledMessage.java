package org.ontouml.vp.views;

import org.ontouml.vp.utils.ViewManagerUtils;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class HTMLEnabledMessage extends JEditorPane {
  private static final long serialVersionUID = 1L;

  public HTMLEnabledMessage(String htmlBody) {
    super("text/html", "<html><body style=\"" + getStyle() + "\">" + htmlBody + "</body></html>");
    addHyperlinkListener(
        new HyperlinkListener() {
          @Override
          public void hyperlinkUpdate(HyperlinkEvent e) {
            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
              try {
                final Desktop desktop = Desktop.getDesktop();
                desktop.browse(e.getURL().toURI());
              } catch (UnsupportedOperationException unsupportedException) {
                ViewManagerUtils.reportBugErrorDialog(true);
                unsupportedException.printStackTrace();
              } catch (SecurityException securityException) {
                ViewManagerUtils.reportBugErrorDialog(true);
                securityException.printStackTrace();
              } catch (Exception exception) {
                ViewManagerUtils.reportBugErrorDialog(false);
                exception.printStackTrace();
              }
            }
          }
        });
    setEditable(false);
    setBorder(null);
  }

  static StringBuffer getStyle() {
    // for copying style
    JLabel label = new JLabel();
    Font font = label.getFont();
    Color color = label.getBackground();

    // create some css from the label's font
    StringBuffer style = new StringBuffer("font-family:" + font.getFamily() + ";");
    style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
    style.append("font-size:" + font.getSize() + "pt;");
    style.append(
        "background-color: rgb("
            + color.getRed()
            + ","
            + color.getGreen()
            + ","
            + color.getBlue()
            + ");");
    return style;
  }
}
