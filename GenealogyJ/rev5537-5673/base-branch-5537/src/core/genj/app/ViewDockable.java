
package genj.app;

import java.util.logging.Level;

import genj.gedcom.Gedcom;
import genj.print.PrintRegistry;
import genj.print.PrintTask;
import genj.print.Printer;
import genj.util.EnvironmentChecker;
import genj.util.Registry;
import genj.util.swing.Action2;
import genj.util.swing.ButtonHelper;
import genj.view.ToolBarSupport;
import genj.view.ViewFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JToolBar;

import swingx.docking.DefaultDockable;
import swingx.docking.Docked;



public class ViewDockable extends DefaultDockable {

  
  public ViewDockable(ViewFactory factory, Gedcom gedcom) {
    
    
    String title = factory.getTitle();
    
    
    Registry registry = new Registry(Registry.lookup(gedcom.getOrigin().getFileName(), gedcom.getOrigin()), factory.getClass().getName()+".1");
    
    
    JComponent view = factory.createView(title, gedcom, registry);

    
    setContent(view);
    setTitle(title);
  }
  
  @Override
  public void docked(Docked docked) {
    super.docked(docked);

    
    JComponent view = getContent();
    if (!(view instanceof ToolBarSupport)) 
      return;

    

    
    
    


    
    
    














    
  }
  
}
