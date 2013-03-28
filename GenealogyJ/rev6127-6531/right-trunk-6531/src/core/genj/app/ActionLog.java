
package genj.app;

import genj.gedcom.PropertyFile;
import genj.util.EnvironmentChecker;
import genj.util.swing.Action2;

import java.awt.Desktop;
import java.awt.event.ActionEvent;


 class ActionLog extends Action2 {
  
  
  protected ActionLog() {
    setText("Log");
    setImage(PropertyFile.DEFAULT_IMAGE);
  }

  
  public void actionPerformed(ActionEvent event) {
    try {
      Desktop.getDesktop().open(EnvironmentChecker.getLog());
    } catch (Throwable t) {
    }
  }
}