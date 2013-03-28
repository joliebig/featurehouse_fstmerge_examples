
package genj.edit.actions;

import genj.edit.EditView;
import genj.edit.EditViewFactory;
import genj.edit.Images;
import genj.util.swing.Action2;
import genj.view.ViewContext;
import genj.view.ViewHandle;
import genj.view.ViewManager;


public class OpenForEdit extends Action2 {
  
  private ViewContext context;
  
  private ViewManager manager;
  
  public OpenForEdit(ViewContext ctxt, ViewManager mgr) {
    manager = mgr;
    context = ctxt;
    setImage(Images.imgView);
    setText(AbstractChange.resources.getString("edit"));
  }
  
  protected void execute() {

    
    
    ViewHandle handle;
    while (true) {
	    handle = manager.openView(EditViewFactory.class, context.getGedcom());
	    if (!((EditView)handle.getView()).isSticky()) 
	      break;
    }
    
    
    ((EditView)handle.getView()).setContext(context);
  }
  
} 

