

package edu.rice.cs.util.swing;

import java.awt.*;
import javax.swing.*;


public class ProcessingDialog extends JDialog {
  private Component _parent;
  private JProgressBar _pb;
  
  public ProcessingDialog(Frame parent, String title, String label) {
    this(parent, title, label, false);
  }

  public ProcessingDialog(Frame parent, String title, String label,
                          boolean modal) {
    super(parent, title, modal);
    setResizable(false);    
    _parent = parent;
    setSize(350, 150);
    if (_parent!=null) { Utilities.setPopupLoc(this, _parent); }
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
    if (_parent!=null) { Utilities.setPopupLoc(this, _parent); }
    super.setVisible(vis);
  }
}
