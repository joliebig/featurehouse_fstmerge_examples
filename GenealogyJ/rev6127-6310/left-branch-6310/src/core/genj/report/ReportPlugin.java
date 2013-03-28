
package genj.report;

import genj.app.Workbench;
import genj.app.WorkbenchListener;
import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.util.Resources;
import genj.util.Trackable;
import genj.util.swing.Action2;
import genj.util.swing.Action2.Group;
import genj.view.ActionProvider;
import genj.view.View;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;


public class ReportPlugin implements ActionProvider, WorkbenchListener {
  
  private final static Resources RESOURCES = Resources.get(ReportPlugin.class);
  private final static int MAX_HISTORY = 5;

  private boolean showReportPickerOnOpen = true;
  
  private Workbench workbench;
  private Action2.Group workbenchActions = new Action2.Group(Resources.get(this).getString("report.reports"));
  
  public ReportPlugin(Workbench workbench) {
    this.workbench = workbench;
    
    workbench.addWorkbenchListener(this);

  }
    
  public void commitRequested(Workbench workbench) {
  }
  
  public void selectionChanged(Workbench workbench, Context context, boolean isActionPerformed) {
  }
  
  public boolean workbenchClosing(Workbench workbench) {
    return true;
  }
  
  public void gedcomClosed(Workbench workbench, Gedcom gedcom) {
  }
  
  public void gedcomOpened(Workbench workbench, Gedcom gedcom) {
  }
  
  
  public void createActions(Context context, Purpose purpose, Group result) {
    
    
    if (context.getGedcom()==null)
      return;
    
    switch (purpose) {
      case TOOLBAR:
        break;
        
      case MENU:
        workbenchActions.clear();

        
        Map<String, Action2.Group> categories = new HashMap<String, Action2.Group>();
        for (Report report : ReportLoader.getInstance().getReports()) {
          try {
            ActionRun run = null;
            if (context.getEntity()!=null&&report.accepts(context.getEntity())!=null)
              run = new ActionRun(report.accepts(context.getEntity()), context.getEntity(), report);
            if (run==null&&report.accepts(context.getGedcom())!=null)
              run = new ActionRun(report.accepts(context.getGedcom()), context.getGedcom(), report);
            if (run!=null) {
              String cat = report.getCategory();
              Action2.Group catgroup = categories.get(cat);
              if (catgroup==null) {
                catgroup = new Action2.Group(cat, report.getIcon(), true);
                categories.put(cat, catgroup);
                workbenchActions.add(catgroup);
              }
              catgroup.add(run);
            }
          } catch (Throwable t) {
            ReportView.LOG.log(Level.WARNING, "Report "+report.getClass().getName()+" failed in accept()", t);
          }
        }
        
        result.add(workbenchActions);
        break;
        
      case CONTEXT:

        
        List<? extends Property> properties = context.getProperties();
        if (properties.size()>1) {
          Action2.Group group = new ActionProvider.PropertiesActionGroup(properties);
          getActions(properties, context.getGedcom(), group);
          result.add(group);
        } else if (properties.size()==1) {
          Action2.Group group = new ActionProvider.PropertyActionGroup(context.getProperty());
          getActions(context.getProperty(), context.getGedcom(), group);
          result.add(group);
        }
        
        
        List<? extends Entity> entities = context.getEntities();
        if (entities.size()>1) {
          Action2.Group group = new ActionProvider.EntitiesActionGroup(entities);
          getActions(entities, context.getGedcom(), group);
          result.add(group);
        } else if (entities.size()==1) {
          Action2.Group group = new ActionProvider.EntityActionGroup(context.getEntity());
          getActions(context.getEntity(), context.getGedcom(), group);
          result.add(group);
        }
        
        
        Action2.Group group = new ActionProvider.GedcomActionGroup(context.getGedcom());
        getActions(context.getGedcom(), context.getGedcom(), group);
        result.add(group);
        
        break;
    }

    
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
  
    
    Map<String, Action2.Group> categories = new HashMap<String, Action2.Group>();
    for (Report report : ReportLoader.getInstance().getReports()) {
      try {
        String accept = report.accepts(context); 
        if (accept!=null) {
          ActionRun run = new ActionRun(accept, context, report);
          String cat = report.getCategory();
          if (cat==null)
            group.add(run);
          else {
            Action2.Group catgroup = categories.get(cat);
            if (catgroup==null) {
              catgroup = new Action2.Group(RESOURCES.getString("title")+" ("+cat+")", report.getIcon());
              categories.put(cat, catgroup);
            }
            catgroup.add(run);
          }
        }
      } catch (Throwable t) {
        ReportView.LOG.log(Level.WARNING, "Report "+report.getClass().getName()+" failed in accept()", t);
      }
    }
    
    for (Action2.Group cat : categories.values()) {
      group.add(cat);
    }
    
    
  }
  
  
  private class ActionRun extends Action2 {
    
    private Object context;
    
    private Report report;
    
    private ActionRun(Report report) {
      setText(report.getName());
    }
    
    private ActionRun(String txt, Object context, Report report) {
      
      this.context = context;
      this.report = report;
      
      setText(txt);
    }
    
    
    public void actionPerformed(ActionEvent event) {
      showReportPickerOnOpen = false;
      try {
        ReportView view = (ReportView)workbench.openView(ReportViewFactory.class);
        view.startReport(report, context);
      } finally {
        showReportPickerOnOpen = true;
      }
    }
  } 

  public void viewClosed(Workbench workbench, View view) {
  }

  public void viewOpened(Workbench workbench, View view) {
    if (view instanceof ReportView) {
      ReportView reportView = (ReportView)view;
      reportView.setPlugin(this);
      if (showReportPickerOnOpen)
        reportView.startReport();
    }
  }
  
  public void viewRestored(Workbench workbench, View view) {
    if (view instanceof ReportView)
      ((ReportView)view).setPlugin(this);
  }
  
   void setEnabled(boolean set) {
    if (workbenchActions!=null)
      workbenchActions.setEnabled(set);
  }

  public void processStarted(Workbench workbench, Trackable process) {
  }

  public void processStopped(Workbench workbench, Trackable process) {
  }
}
