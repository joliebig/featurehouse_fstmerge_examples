

package org.jmol.export.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JDialog;
import javax.swing.JFileChooser;


public class FileChooser extends JFileChooser {

  private Point dialogLocation = null;
  private Dimension dialogSize = null;
  private JDialog dialog = null;

  
  protected JDialog createDialog(Component parent) {
    dialog = super.createDialog(parent);
    if (dialog != null) {
      if (dialogLocation != null) {
        dialog.setLocation(dialogLocation);
      }
      if (dialogSize != null) {
        dialog.setSize(dialogSize);
      }
    }
    return dialog;
  }

  
  public void setDialogLocation(Point p) {
  	dialogLocation = p;
  }
  
  
  public void setDialogSize(Dimension d) {
    dialogSize = d;
  }
  
  
  public JDialog getDialog() {
    return dialog;
  }
}
