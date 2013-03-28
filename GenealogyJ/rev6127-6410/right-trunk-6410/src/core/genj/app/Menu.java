
package genj.app;

import genj.gedcom.Context;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomListener;
import genj.gedcom.GedcomListenerAdapter;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.Trackable;
import genj.util.swing.Action2;
import genj.util.swing.MacAdapter;
import genj.util.swing.MenuHelper;
import genj.view.ActionProvider;
import genj.view.SelectionSink;
import genj.view.View;
import genj.view.ViewFactory;
import genj.view.ActionProvider.Purpose;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import spin.Spin;


 class Menu extends JMenuBar implements SelectionSink, WorkbenchListener {
  
  private final static Logger LOG = Logger.getLogger("genj.app");
  private final static Resources RES = Resources.get(Menu.class);
  private final static Registry REGISTRY = Registry.get(Menu.class);
  
  private Workbench workbench;
  private Gedcom gedcom;
  private List<Action> actions = new CopyOnWriteArrayList<Action>();
  private GedcomListener callback = new GedcomListenerAdapter() {
    @Override
    public void gedcomWriteLockReleased(Gedcom gedcom) {
      setup(gedcom);
    }
  };
  
   Menu(Workbench workbench) {
    this.workbench = workbench;
    workbench.addWorkbenchListener(this);
    setup(null);
  }
  
  
  
  
  public void fireSelection(Context context, boolean isActionPerformed) {
    workbench.fireSelection(context, isActionPerformed);
  }
  
  private void remove(Action action) {
    
    if (gedcom!=null&&action instanceof GedcomListener)
      gedcom.removeGedcomListener((GedcomListener)Spin.over(action));
    
    if (action instanceof WorkbenchListener)
      workbench.removeWorkbenchListener((WorkbenchListener)action);
    
    actions.remove(action);
  }
  
  private void add(Action action) {
    
    actions.add(action);
    
    if (gedcom!=null&&action instanceof GedcomListener)
      gedcom.addGedcomListener((GedcomListener)Spin.over(action));
    if (action instanceof WorkbenchListener)
      workbench.addWorkbenchListener((WorkbenchListener)action);
  }
  
  private void setup(Gedcom gedcom) {

    
    if (this.gedcom!=null)
      this.gedcom.removeGedcomListener(callback);
    for (Action action : actions)
      remove(action);
    this.gedcom = gedcom;
    removeAll();
    revalidate();
    repaint();

    
    if (this.gedcom!=null)
      this.gedcom.addGedcomListener(callback);
    Action2.Group groups = new Action2.Group("ignore");
    
    
    Action2.Group file = new ActionProvider.FileActionGroup();
    groups.add(file);
    file.add(workbench.new ActionNew());
    file.add(workbench.new ActionOpen());
    file.add(workbench.new ActionSave(false));
    file.add(workbench.new ActionSave(true));
    file.add(workbench.new ActionClose());
    file.add(new ActionProvider.SeparatorAction());
    int i=0; for (String recent : REGISTRY.get("history", new ArrayList<String>())) try {
      if (gedcom==null||!recent.equals(gedcom.getOrigin().toString()))
        file.add(workbench.new ActionOpen(i++, new URL(recent)));
    } catch (MalformedURLException e) { }
    file.add(new ActionProvider.SeparatorAction());
    if (!MacAdapter.isMac())   
      file.add(workbench.new ActionExit()); 
    
    
    groups.add(new ActionProvider.EditActionGroup());
    
    
    Action2.Group views = new ActionProvider.ViewActionGroup();
    groups.add(views);
    for (ViewFactory factory : workbench.getViewFactories()) 
      views.add(workbench.new ActionOpenView(factory));
    
    
    Action2.Group tools = new ActionProvider.ToolsActionGroup();
    groups.add(tools);
    
    
    Action2.Group provided = new Action2.Group("ignore");
    for (ActionProvider provider : workbench.getProviders(ActionProvider.class)) {
      provider.createActions(workbench.getContext(), Purpose.MENU, provided);
      for (Action2 action : provided) {
        if (action instanceof Action2.Group) {
          groups.add(action);
        } else {
          tools.add(action);
        }
      }
      provided.clear();
    }
    
    Action2.Group edit = new ActionProvider.EditActionGroup();
    edit.add(new ActionProvider.SeparatorAction());
    if (!MacAdapter.isMac())
      edit.add(new ActionOptions());
    groups.add(edit);

    Action2.Group help = new ActionProvider.HelpActionGroup();
    help.add(new ActionProvider.SeparatorAction());
    help.add(new ActionLog());
    if (!MacAdapter.isMac())
      help.add(new ActionAbout());
    groups.add(help);

    
    MenuHelper mh = new MenuHelper() {
      @Override
      protected void set(Action action, JMenuItem item) {
        add(action);
        super.set(new ActionProxy(action), item);
      }
    };
    
    mh.pushMenu(this);
    
    for (Action2 group : groups) {
      Action2.Group subgroup = (Action2.Group)group;
      if (subgroup.size()>0) {
        mh.createMenu(subgroup);
        mh.popMenu();
      }
    }
    
    
  }
  
  
  
  
  
  
  
  
  
  
  
  
  public void commitRequested(Workbench workbench) {
  }

  public void gedcomClosed(Workbench workbench, Gedcom gedcom) {
    setup(null);
  }

  public void gedcomOpened(Workbench workbench, Gedcom gedcom) {
    setup(gedcom);
  }

  public void processStarted(Workbench workbench, Trackable process) {
  }

  public void processStopped(Workbench workbench, Trackable process) {
  }

  public void selectionChanged(Workbench workbench, Context context, boolean isActionPerformed) {
    setup(context.getGedcom());
  }

  public void viewClosed(Workbench workbench, View view) {
  }
  
  public void viewRestored(Workbench workbench, View view) {
  }

  public void viewOpened(Workbench workbench, View view) {
  }
  
  public void workbenchClosing(Workbench workbench) {
  }
  
  private class ActionProxy implements Action {
    private Action delegate;
    public ActionProxy(Action delegate) {
      this.delegate = delegate;
    }
    public void addPropertyChangeListener(PropertyChangeListener listener) {
      delegate.addPropertyChangeListener(listener);
    }
    public Object getValue(String key) {
      return delegate.getValue(key);
    }
    public boolean isEnabled() {
      return delegate.isEnabled();
    }
    public void putValue(String key, Object value) {
      delegate.putValue(key, value);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
      delegate.removePropertyChangeListener(listener);
    }
    public void setEnabled(boolean b) {
      delegate.setEnabled(b);
    }
    public void actionPerformed(ActionEvent e) {
      delegate.actionPerformed(e);
      setup(gedcom);
    }
  };
  
  
} 

