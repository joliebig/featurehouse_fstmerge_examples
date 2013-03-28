
package genj.edit.actions;

import java.awt.event.ActionEvent;

import genj.app.Workbench;
import genj.edit.EditViewFactory;
import genj.edit.Images;
import genj.gedcom.Context;
import genj.util.swing.Action2;


public class OpenForEdit extends Action2 {
  private Context context;
  private Workbench workbench;
  
  
  public OpenForEdit(Workbench workbench, Context context) {
    this.context = context;
    this.workbench = workbench;
    setImage(Images.imgView);
    setText(AbstractChange.resources.getString("edit"));
  }
  
  
  public void actionPerformed(ActionEvent event) {
    workbench.openView(EditViewFactory.class, context);
  }
  
} 

