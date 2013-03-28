package genj.app;

import genj.gedcom.Context;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomListener;
import genj.gedcom.GedcomListenerAdapter;
import genj.util.Trackable;
import genj.util.swing.Action2;
import genj.util.swing.ToolbarWidget;
import genj.view.ActionProvider;
import genj.view.View;
import genj.view.ActionProvider.Purpose;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JButton;

import spin.Spin;


 class Toolbar extends ToolbarWidget implements WorkbenchListener {
  
  private final static Logger LOG = Logger.getLogger("genj.app");

  private Workbench workbench;
  private HistoryWidget history;
  
  private List<Action> actions = new CopyOnWriteArrayList<Action>();
  
  private GedcomListener callback = new GedcomListenerAdapter() {
    @Override
    public void gedcomWriteLockReleased(Gedcom gedcom) {
      setup(gedcom);
    }
  };
  private Gedcom gedcom;
  
  
   Toolbar(Workbench workbench) {
    this.workbench = workbench;
    workbench.addWorkbenchListener(this);
    history = new HistoryWidget(workbench);
    setFloatable(false);
    setup(null);
  }
  
  private void remove(Action action) {
    
    if (gedcom!=null&&action instanceof GedcomListener)
      gedcom.removeGedcomListener((GedcomListener)Spin.over(action));
    
    if (action instanceof WorkbenchListener)
      workbench.removeWorkbenchListener((WorkbenchListener)action);
    
    actions.remove(action);
  }
  
  private void setup(Gedcom gedcom) {
   
    
    if (this.gedcom!=null)
      this.gedcom.removeGedcomListener(callback);
    
    for (Action action : actions) 
      remove(action);
    removeAll();
    
    
    this.gedcom = gedcom;
      
    
    add(workbench.new ActionNew());
    add(workbench.new ActionOpen());
    add(workbench.new ActionSave(false));
    
    
    if (gedcom!=null) {
      Action2.Group actions = new Action2.Group("ignore");
      addSeparator();
      for (ActionProvider provider : workbench.getProviders(ActionProvider.class)) {
        actions.clear();
        provider.createActions(workbench.getContext(), Purpose.TOOLBAR, actions);
        for (Action2 action : actions) {
          if (action instanceof Action2.Group)
            LOG.warning("ActionProvider "+provider+" returned a group for toolbar");
          else {
            if (action instanceof ActionProvider.SeparatorAction)
              addSeparator();
            else {
              add(action);
            }
          }
        }
      }
    }
    
    if (this.gedcom!=null)
      this.gedcom.addGedcomListener(callback);
    
    
    add(history);
    
    
  }
  
  @Override
  public JButton add(Action action) {
    
    actions.add(action);
    
    action.putValue(Action.MNEMONIC_KEY, null);
    action.putValue(Action.NAME, null);
    
    if (gedcom!=null&&action instanceof GedcomListener)
      gedcom.addGedcomListener((GedcomListener)Spin.over(action));
    if (action instanceof WorkbenchListener)
      workbench.addWorkbenchListener((WorkbenchListener)action);
    
    
    return super.add(action);
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
}
