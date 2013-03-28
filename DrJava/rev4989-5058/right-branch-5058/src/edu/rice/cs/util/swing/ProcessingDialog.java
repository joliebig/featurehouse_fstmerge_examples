

package edu.rice.cs.util.swing;

import java.awt.*;
import javax.swing.*;


public class ProcessingDialog extends JDialog {
  private Component _parent;
  private JProgressBar _pb;
  
  public ProcessingDialog(Frame parent, String title, String label) {
    super(parent, title);
    setResizable(false);    
    _parent = parent;
    setSize(350, 150);
    Utilities.setPopupLoc(this, parent);
    JLabel waitLabel = new JLabel(label, SwingConstants.CENTER);
    getRootPane().setLayout(new BorderLayout());
    getRootPane().add(waitLabel, BorderLayout.CENTER);
    _pb = new JProgressBar(0, 100);
    _pb.setValue(0);
    _pb.setStringPainted(false);
    _pb.setIndeterminate(true);
    getRootPane().add(_pb, BorderLayout.SOUTH);
  }
  
  public JProgressBar getProgressBar() { return _pb; }
  
  public void setVisible(boolean vis) {
    Utilities.setPopupLoc(this, _parent);
    super.setVisible(vis);
  }
}
