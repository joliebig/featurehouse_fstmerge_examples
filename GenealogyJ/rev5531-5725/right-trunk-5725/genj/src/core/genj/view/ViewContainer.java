
package genj.view;

import genj.edit.actions.Redo;
import genj.edit.actions.Undo;
import genj.print.PrintRegistry;
import genj.print.PrintTask;
import genj.print.Printer;
import genj.util.EnvironmentChecker;
import genj.util.swing.Action2;
import genj.util.swing.ButtonHelper;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Iterator;
import java.util.logging.Level;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;


 class ViewContainer extends JPanel {
  
  private final static String
    ACC_CLOSE = "ctrl W",
    ACC_UNDO = "ctrl Z",
    ACC_REDO = "ctrl Y";

  
  private JToolBar bar;
  
  
  private ViewHandle viewHandle;
  
  
   ViewContainer(ViewHandle handle) {
    
    ViewManager mgr = handle.getManager();
    
    
    viewHandle = handle;
    JComponent view = viewHandle.getView();
    
    
    setLayout(new BorderLayout());
    add(view, BorderLayout.CENTER);
    
    
    for (Iterator it = mgr.keyStrokes2factories.keySet().iterator(); it.hasNext();) {
      String keystroke = it.next().toString();
      ViewFactory factory = (ViewFactory)mgr.keyStrokes2factories.get(keystroke);
      ActionOpen open = new ActionOpen(factory);
      open.setAccelerator(keystroke);
      open.install(view, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    
    new ActionClose().setAccelerator(ACC_CLOSE).install(view, JComponent.WHEN_IN_FOCUSED_WINDOW);
    new Undo(viewHandle.getGedcom(), true).setAccelerator(ACC_UNDO).install(view, JComponent.WHEN_IN_FOCUSED_WINDOW);
    new Redo(viewHandle.getGedcom(), true).setAccelerator(ACC_REDO).install(view, JComponent.WHEN_IN_FOCUSED_WINDOW);

    
  }
  
  
  public void addNotify() {
    
    
    super.addNotify();
    
    
    JComponent view = viewHandle.getView();
    if (!(view instanceof ToolBarSupport)||bar!=null) 
      return;

    
    bar = new JToolBar();
    
    
    ((ToolBarSupport)view).populate(bar);
    if (EnvironmentChecker.getProperty(this, "genj.view.toolbarproblem", null, "checking for switch to not use glue in toolbar")==null)
      bar.add(Box.createGlue());

    
    ButtonHelper bh = new ButtonHelper().setContainer(bar);
    bh.setInsets(0);

    
    if (SettingsWidget.hasSettings(view))
      bh.create(new ActionOpenSettings());
    
    
    try {
      Printer printer = (Printer)Class.forName(view.getClass().getName()+"Printer").newInstance();
      try {
        printer.setView(view);
        PrintTask print = new PrintTask(printer, viewHandle.getTitle(), view,  new PrintRegistry(viewHandle.getRegistry(), "print"));
        print.setTip(ViewManager.RESOURCES, "view.print.tip");
        bh.create(print);
      } catch (Throwable t) {
        ViewManager.LOG.log(Level.WARNING, "can't setup printing for printer "+printer.getClass().getName());
        ViewManager.LOG.log(Level.FINE, "throwable while setting up "+printer.getClass().getName(), t);
      }
    } catch (Throwable t) {
    }

    
    bh.create(new ActionClose());

    
    add(bar, viewHandle.getRegistry().get("toolbar", BorderLayout.WEST));
    
    
  }
  
  
  protected void addImpl(Component comp, Object constraints, int index) {
    
    if (comp==bar) {
      
      viewHandle.getRegistry().put("toolbar", constraints.toString());
      
      int orientation = SwingConstants.HORIZONTAL;
      if (BorderLayout.WEST.equals(constraints)||BorderLayout.EAST.equals(constraints))
        orientation = SwingConstants.VERTICAL;
      
      bar.setOrientation(orientation);
      
    }
    
    super.addImpl(comp, constraints, index);
    
  }

  
  private class ActionClose extends Action2 {
    
    protected ActionClose() {
      setImage(Images.imgClose);
      setTip(ViewManager.RESOURCES, "view.close.tip");
    }
    
    protected void execute() {
      viewHandle.getManager().closeView(viewHandle);
    }
  } 
  
  
  private class ActionOpenSettings extends Action2 {
    
    protected ActionOpenSettings() {
      super.setImage(Images.imgSettings).setTip(ViewManager.RESOURCES, "view.settings.tip");
    }
    
    protected void execute() {
      viewHandle.getManager().openSettings(viewHandle);
    }
  } 
  
  
  private class ActionOpen extends Action2 {
    private ViewFactory factory;
    
    private ActionOpen(ViewFactory factory) {
      this.factory = factory;
    }
    
    protected void execute() {
      viewHandle.getManager().openView(viewHandle.getGedcom(), factory, 1);
    }
  }
    
} 
