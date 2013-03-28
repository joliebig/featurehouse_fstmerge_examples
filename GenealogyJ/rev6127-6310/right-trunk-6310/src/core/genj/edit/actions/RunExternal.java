
package genj.edit.actions;

import genj.gedcom.PropertyFile;
import genj.util.swing.Action2;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RunExternal extends Action2 {
  
  
  private File file;
  
  
  public RunExternal(File file) {
    this.file = file;
    super.setImage(PropertyFile.DEFAULT_IMAGE);
    super.setText("Open");
    setEnabled(file.exists());
  }
  
  
  public void actionPerformed(ActionEvent event) {
    if (file==null)
      return;
    try {
      Desktop.getDesktop().open(file);
    } catch (Throwable t) {
      Logger.getLogger("genj.edit.actions").log(Level.INFO, "can't open "+file, t);
    }
  }
  
} 
