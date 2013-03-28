
package genj.report;

import genj.app.PluginFactory;
import genj.app.Workbench;
import genj.app.WorkbenchListener;
import genj.app.Workbench.ToolLocation;
import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.util.swing.Action2;
import genj.view.ActionProvider;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


public class ReportPluginFactory implements PluginFactory {
  
  
  public Object createPlugin(Workbench workbench) {
    return new ReportPlugin(workbench);
  }

  
  private class ReportPlugin implements ActionProvider, WorkbenchListener {
    
    private Workbench workbench;
    private Action2.Group actions;
    
    public ReportPlugin(Workbench workbench) {
      this.workbench = workbench;
      
      workbench.addWorkbenchListener(this);

    }
    
    private void uninstallActions() {
      if (actions!=null) {
        workbench.uninstallTool(actions);
        actions = null;
      }
    }
    
    private void installActions() {
      
      uninstallActions();
      
      Context context = workbench.getContext();
      if (context!=null) {
        Action2.Group newActions = new Action2.Group("Reports");
        getActions(context.getGedcom(), context.getGedcom(), newActions);
        if (newActions.size()>0) {
          actions = newActions;
          workbench.installTool(actions, ToolLocation.MAINMENU);
        }
      }
    }
    
    public void commitRequested() {
    }
    
    public void selectionChanged(Context context, boolean isActionPerformed) {
    }
    
    public boolean workbenchClosing() {
      return true;
    }
    
    public void gedcomClosed(Gedcom gedcom) {
      uninstallActions();
    }
    
    public void gedcomOpened(Gedcom gedcom) {
      installActions();
    }
    
    public int getPriority() {
      return NORMAL;
    }
    
    
    public List<Action2> createActions(Property[] properties) {
      return getActions(properties, properties[0].getGedcom());
    }

    
    public List<Action2> createActions(Entity entity) {
      return getActions(entity, entity.getGedcom());
    }

    
    public List<Action2> createActions(Gedcom gedcom) {
      return getActions(gedcom, gedcom);
    }

    
    public List<Action2> createActions(Property property) {
      return getActions(property, property.getGedcom());
    }

    
    private List<Action2> getActions(Object context, Gedcom gedcom) {

      Action2.Group action = new Action2.Group("Reports", ReportViewFactory.IMG);
      getActions(context, gedcom, action);
      List<Action2> result = new ArrayList<Action2>();
      if (action.size()>0)
        result.add(action);
      return result;
      
    }
    
    private void getActions(Object context, Gedcom gedcom, Action2.Group group) {
    
      
      for (Report report : ReportLoader.getInstance().getReports()) {
        try {
          String accept = report.accepts(context); 
          if (accept!=null) {
            ActionRun run = new ActionRun(accept, context, gedcom, report);
            if (group.size()>10) {
              Action2.Group next = new Action2.Group("...");
              group.add(next);
              group = next;
            }
            group.add(run);
          }
        } catch (Throwable t) {
          ReportView.LOG.log(Level.WARNING, "Report "+report.getClass().getName()+" failed in accept()", t);
        }
      }
      
      
    }
    
    
    private class ActionRun extends Action2 {
      
      private Object context;
      private Gedcom gedcom;
      
      private Report report;
      
      private ActionRun(Report report) {
        setImage(report.getImage());
        setText(report.getName());
      }
      
      private ActionRun(String txt, Object context, Gedcom gedcom, Report report) {
        
        this.context = context;
        this.gedcom = gedcom;
        this.report = report;
        
        setImage(report.getImage());
        setText(txt);
      }
      
      
      public void actionPerformed(ActionEvent event) {
        





















      }
    } 
 }
}
