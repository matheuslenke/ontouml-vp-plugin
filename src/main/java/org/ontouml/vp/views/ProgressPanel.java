package org.ontouml.vp.views;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class ProgressPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  private final JProgressBar progressBar;
  private final JButton btnCancel;

  public ProgressPanel() {
    super();
    setSize(200, 200);
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    JLabel label = new JLabel("Loading");
    label.setAlignmentX(Component.CENTER_ALIGNMENT);
    add(label);

    progressBar = new JProgressBar();
    progressBar.setStringPainted(true);
    progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
    add(progressBar);

    btnCancel = new JButton("Cancel");
    btnCancel.setAlignmentX(Component.CENTER_ALIGNMENT);
    add(btnCancel);
  }

  public void startUpdatingProgressBarString(Supplier<Boolean> shouldStop) {
    new SwingWorker<List<String>, String>() {
      protected void process(List<String> chunks) {
        String latest = chunks.get(chunks.size() - 1);
        progressBar.setString(latest);
      }

      protected List<String> doInBackground() throws Exception {
        List<String> circles =
            new ArrayList<>(Arrays.asList("\u25F4", "\u25F7", "\u25F6", "\u25F5"));
        int index = 0;

        while (!shouldStop.get() || isCancelled()) {
          publish(circles.get(index));
          index = (index + 1) % circles.size();
          Thread.sleep(200);
        }

        return circles;
      }
    }.execute();
  }

  public void setValue(int value) {
    progressBar.setValue(value);
    progressBar.setString("Completed " + value + "%");
  }

  public void setCancelAction(ActionListener cancelAction) {
    ActionListener[] listeners = btnCancel.getActionListeners();

    for (int i = 0; listeners != null && i < listeners.length; i++) {
      btnCancel.removeActionListener(listeners[i]);
    }

    btnCancel.addActionListener(cancelAction);
  }
}
