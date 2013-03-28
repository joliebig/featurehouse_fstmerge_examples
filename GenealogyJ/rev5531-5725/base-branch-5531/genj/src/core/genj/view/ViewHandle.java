
package genj.view;

import genj.gedcom.Gedcom;
import genj.util.Registry;

import javax.swing.JComponent;


public class ViewHandle {
  
  
  private ViewManager manager;

  
  private Gedcom gedcom;
  
  
  private JComponent view;
  
  
  private String title;
  
  
  private Registry registry;
  
  
  private ViewFactory factory;
  
  
  private int sequence;
  
  
   ViewHandle(ViewManager manager, Gedcom gedcom, String title, Registry registry, ViewFactory factory, JComponent view, int sequence) {
    this.manager = manager;
    this.gedcom = gedcom;
    this.title = title;
    this.registry = registry;
    this.view = view;
    this.factory = factory;
    this.sequence = sequence;
  }
  
  
  public Gedcom getGedcom() {
    return gedcom;
  }
  
  
  public String getTitle() {
    return title;
  }
  
  
  public JComponent getView() {
    return view;
  }
  
  
  public ViewFactory getFactory() {
    return factory;
  }
  
  
  public int getSequence() {
    return sequence;
  }
  
  
  public Registry getRegistry() {
    return registry;
  }
  
  
  public ViewManager getManager() {
    return manager;
  }
  
  
   String getKey() {
    return gedcom.getName() + "." + manager.getPackage(factory) + "." + sequence;
  }
  
  
  public String persist() {
    return factory.getClass().getName() + "#" + sequence;
  }
  
  
  public static ViewHandle restore(ViewManager manager, Gedcom gedcom, String persisted) {

    try {
      int  hash = persisted.indexOf('#');
      ViewFactory factory = (ViewFactory)Class.forName(persisted.substring(0,hash).trim()).newInstance();
      int sequence = Integer.parseInt(persisted.substring(hash+1).trim());
      return manager.openView(gedcom, factory, sequence); 
    } catch (Throwable t) {
      return null;
    }
 
    
  }
  
}
