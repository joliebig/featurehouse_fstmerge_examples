
package genj.edit.actions;

import genj.gedcom.Gedcom;
import genj.gedcom.PropertyFile;
import genj.io.FileAssociation;
import genj.util.swing.Action2;

import java.awt.event.ActionEvent;
import java.io.File;


public class RunExternal extends Action2 {
  
  
  private FileAssociation association;
  
  
  private File file;
  
  
  public RunExternal(PropertyFile f) {
    file = f.getFile();
    super.setImage(f.getImage(false));
    super.setText("Open...");
  }
  
  
  public RunExternal(PropertyFile f, FileAssociation fa) {
    association = fa;
    file = f.getFile();
    super.setImage(f.getImage(false));
    super.setText(association.getName()+" ("+association.getSuffixes()+")");
  }
  
  
  public RunExternal(Gedcom ged, String f, FileAssociation fa) {
    association = fa;
    file = ged.getOrigin().getFile(f);
    super.setText(association.getName()+" ("+association.getSuffixes()+")");
  }
  
  
  public void actionPerformed(ActionEvent event) {
    if (file==null)
      return;
    if (association==null)
      association = FileAssociation.get(file, "View", getTarget());
    if (association!=null)
      association.execute(file);
  }
  
} 
