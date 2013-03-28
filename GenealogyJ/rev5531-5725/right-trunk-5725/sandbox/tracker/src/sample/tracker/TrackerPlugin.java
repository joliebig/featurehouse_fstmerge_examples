
package sample.tracker;

import genj.app.ExtendGedcomClosed;
import genj.app.ExtendGedcomOpened;
import genj.app.ExtendMenubar;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomLifecycleEvent;
import genj.gedcom.GedcomLifecycleListener;
import genj.gedcom.GedcomListener;
import genj.gedcom.Property;
import genj.gedcom.TagPath;
import genj.plugin.ExtensionPoint;
import genj.plugin.Plugin;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ImageIcon;
import genj.view.ExtendContextMenu;
import genj.window.WindowBroadcastEvent;
import genj.window.WindowBroadcastListener;
import genj.window.WindowClosingEvent;
import genj.window.WindowManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;


public class TrackerPlugin implements Plugin {
  
  private final ImageIcon IMG = new ImageIcon(this, "/Tracker.gif");
  
  private final Resources RESOURCES = Resources.get(this);
  
  private Log log = new Log();
  private Map gedcom2tracker = new HashMap();
  private boolean active = true;
  
  
  private class Log extends JTextArea implements WindowBroadcastListener {
    private Log() {
      super(40,10);
      setEditable(false);
    }
    public boolean handleBroadcastEvent(WindowBroadcastEvent event) {
      
      if (event instanceof WindowClosingEvent)
        ((WindowClosingEvent)event).cancel();
      return true;
    }
  }
  
  
  
  public void extend(ExtensionPoint ep) {
    
    if (ep instanceof ExtendGedcomOpened) {
      
      Gedcom gedcom = ((ExtendGedcomOpened)ep).getGedcom();
      GedcomTracker tracker = new GedcomTracker();
      gedcom.addLifecycleListener(tracker);
      gedcom.addGedcomListener(tracker);
      gedcom2tracker.put(gedcom, tracker);
      log(RESOURCES.getString("log.attached", gedcom.getName()));
      
      return;
    }

    if (ep instanceof ExtendGedcomClosed) {
      
      Gedcom gedcom = ((ExtendGedcomClosed)ep).getGedcom();
      GedcomTracker tracker = (GedcomTracker)gedcom2tracker.get(gedcom);
      gedcom.removeLifecycleListener(tracker);
      gedcom.removeGedcomListener(tracker);
      log(RESOURCES.getString("log.detached", gedcom.getName()));
    }
    
    if (ep instanceof ExtendContextMenu) {
      
      ((ExtendContextMenu)ep).getContext().addAction("**Tracker**", 
          new Action2(RESOURCES.getString("action.remove"), false));
    }
    
    if (ep instanceof ExtendMenubar) {
      ExtendMenubar em = (ExtendMenubar)ep;
      
      if (!em.getWindowManager().show("tracker"))
        em.getWindowManager().openWindow("tracker", "Tracker", new ImageIcon(this, "/Tracker.gif"), new JScrollPane(log));
      
      em.addAction(ExtendMenubar.TOOLS_MENU, new EnableDisable());
      em.addAction(ExtendMenubar.HELP_MENU, new About());
    }
    
  }

   
  private void log(String msg) {
    
    try {
      Document doc = log.getDocument();
      doc.insertString(doc.getLength(), msg, null);
      doc.insertString(doc.getLength(), "\n", null);
    } catch (BadLocationException e) {
      
    }
  }
  
  
  private class EnableDisable extends Action2 {
    public EnableDisable() {
      setText();
    }
    protected void execute() {
      active = !active;
      setText();
      log("Writing TRAcs is "+(active?"enabled":"disabled"));
    }
    private void setText() {
      setText(RESOURCES.getString(active ? "action.disable" : "action.enable"));
    }
  }
  
  
  private class About extends Action2 {
    About() {
      setText(RESOURCES.getString("action.about"));
    }
    protected void execute() {
      String text = RESOURCES.getString("info.txt", RESOURCES.getString((active?"info.active":"info.inactive")));
      WindowManager.getInstance(getTarget()).openDialog("tracker.about", "Tracker", WindowManager.INFORMATION_MESSAGE, text, Action2.okOnly(), getTarget());
    }
  } 
    
  
  private class GedcomTracker implements GedcomListener, GedcomLifecycleListener { 

    private TagPath PATH = new TagPath(".:TRAC");
    private Set touchedEntities = new HashSet();

    public void handleLifecycleEvent(GedcomLifecycleEvent event) {
      
      
      
      
      
      
      
      
      
      
      if (active)
      if (event.getId()==GedcomLifecycleEvent.AFTER_UNIT_OF_WORK) {
        
        List list = new ArrayList(touchedEntities);
        for (Iterator it = list.iterator(); it.hasNext();) {
          Entity entity = (Entity) it.next();
          int value;
          try {
            value = Integer.parseInt(entity.getValue(PATH, "0"))+1;
          } catch (NumberFormatException e) {
            value = 1;
          }
          entity.setValue(PATH, Integer.toString(value));
        }
      }
      
      
      if (event.getId()==GedcomLifecycleEvent.WRITE_LOCK_RELEASED) {
        touchedEntities.clear();
      }
      
      
    }
  
    
    public void gedcomEntityAdded(Gedcom gedcom, Entity entity) {
      log("Entity "+entity+" added to "+gedcom.getName());
      touchedEntities.add(entity);
    }
  
    
    public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
      log("Entity "+entity+" deleted from "+gedcom.getName());
      touchedEntities.remove(entity);
    }
  
    
    public void gedcomPropertyAdded(Gedcom gedcom, Property property, int pos, Property added) {
      log("Property "+added.getTag()+" (value "+added.getDisplayValue()+") added to "+property.getEntity()+" in "+gedcom.getName());
      touchedEntities.add(property.getEntity());
    }
  
    
    public void gedcomPropertyChanged(Gedcom gedcom, Property property) {
      log("Property "+property.getTag()+" changed to "+property.getDisplayValue()+" in "+property.getEntity()+" in "+gedcom.getName());
      touchedEntities.add(property.getEntity());
    }
  
    
    public void gedcomPropertyDeleted(Gedcom gedcom, Property property, int pos, Property deleted) {
      log("Property "+deleted.getTag()+" deleted from "+property.getEntity()+" in "+gedcom.getName());
      touchedEntities.add(property.getEntity());
    }
    
    
    public void gedcomPropertyLinked(Gedcom gedcom, Property from, Property to) {
      log("Property "+from.getTag()+" in "+from.getEntity()+" is now linked with "+to.getTag()+" in "+to.getEntity()+" in "+gedcom.getName());
      touchedEntities.add(from.getEntity());
      touchedEntities.add(to.getEntity());
    }
    
    
    public void gedcomPropertyUnlinked(Gedcom gedcom, Property from, Property to) {
      log("Property "+from.getTag()+" in "+from.getEntity()+" is no longer linked with "+to.getTag()+" in "+to.getEntity()+" in "+gedcom.getName());
      touchedEntities.add(from.getEntity());
      touchedEntities.add(to.getEntity());
    }

  } 
  
} 
