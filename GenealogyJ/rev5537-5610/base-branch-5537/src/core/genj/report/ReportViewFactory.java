
package genj.report;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.gedcom.UnitOfWork;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ImageIcon;
import genj.view.ActionProvider;
import genj.view.ViewFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JComponent;


public class ReportViewFactory implements ViewFactory, ActionProvider {

   final static ImageIcon IMG = new ImageIcon(ReportViewFactory.class, "View");
  
  
  public JComponent createView(String title, Gedcom gedcom, Registry registry) {
    return new ReportView(title,gedcom,registry);
  }
  
  
  public ImageIcon getImage() {
    return IMG;
  }
  
  
  public String getTitle() {
    return Resources.get(this).getString("title");
  }
  
  
  public List createActions(Property[] properties) {
    return getActions(properties, properties[0].getGedcom());
  }

  
  public List createActions(Entity entity) {
    return getActions(entity, entity.getGedcom());
  }

  
  public List createActions(Gedcom gedcom) {
    return getActions(gedcom, gedcom);
  }

  
  public List createActions(Property property) {
    return getActions(property, property.getGedcom());
  }

  
  private List getActions(Object context, Gedcom gedcom) {
    List result = new ArrayList(10);
    
    Report[] reports = ReportLoader.getInstance().getReports();
    for (int r=0;r<reports.length;r++) {
      Report report = reports[r];
      try {
        String accept = report.accepts(context); 
        if (accept!=null)
          result.add(new ActionRun(accept, context, gedcom, report));
      } catch (Throwable t) {
        ReportView.LOG.log(Level.WARNING, "Report "+report.getClass().getName()+" failed in accept()", t);
      }
    }
    
    result = nestAsRequired(result,10);
      
    
    return result;
  }
  
  private List nestAsRequired(List actions,int max) {
    if (actions.size()<=max)
      return actions;
    List result = new ArrayList();
    result.addAll(actions.subList(0, max));
    
    Action2.Group group = new Action2.Group("...");
    group.addAll(nestAsRequired(actions.subList(max, actions.size()), max)); 
    result.add(group);
    
    return result;
  }

  
  private class ActionRun extends Action2 {
    
    private Object context;
    
    private Gedcom gedcom;
    
    private Report report;
    
    private ActionRun(String txt, Object context, Gedcom gedcom, Report report) {
      
      this.context = context;
      this.gedcom = gedcom;
      this.report = report;
      
      setImage(report.getImage());
      setText(txt);
      
      setAsync(Action2.ASYNC_SAME_INSTANCE);
    }
    
    protected boolean preExecute() {
      
      if (report.usesStandardOut()) {










        
        return false;
      }
      
      return true;
    }
    
    protected void execute() {
      
      final Report instance = report.getInstance(getTarget(), null);
      
      try{
        
        if (instance.isReadOnly())
          instance.start(context);
        else
          gedcom.doUnitOfWork(new UnitOfWork() {
            public void perform(Gedcom gedcom) {
              try {
                instance.start(context);
              } catch (Throwable t) {
                throw new RuntimeException(t);
              }
            }
          });
      
      } catch (Throwable t) {
        Throwable cause = t.getCause();
        if (cause instanceof InterruptedException)
          instance.println("***cancelled");
        else
          ReportView.LOG.log(Level.WARNING, "encountered throwable in "+instance.getClass().getName()+".start()", cause!=null?cause:t);
      }
    }
    
  } 

} 
