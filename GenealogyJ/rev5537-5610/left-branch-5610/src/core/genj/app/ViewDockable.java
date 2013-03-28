
package genj.app;

import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.MenuHelper;
import genj.view.ActionProvider;
import genj.view.ContextProvider;
import genj.view.SelectionSink;
import genj.view.ToolBar;
import genj.view.View;
import genj.view.ViewContext;
import genj.view.ViewFactory;
import genj.view.ActionProvider.Purpose;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.FocusManager;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;

import swingx.docking.DefaultDockable;
import swingx.docking.Docked;


class ViewDockable extends DefaultDockable implements WorkbenchListener {
  
  private final static Logger LOG = Logger.getLogger("genj.app");
  private final static ContextHook HOOK = new ContextHook();

  private ViewFactory factory;
  private View view;
  private Workbench workbench;
  private boolean ignoreSelectionChanged = false;

  
  public ViewDockable(Workbench workbench, ViewFactory factory, Registry registry) {

    this.workbench = workbench;
    this.factory = factory;
    
    
    String title = factory.getTitle();

    
    view = factory.createView();
    
    
    view.putClientProperty(ViewDockable.class, this);

    
    setContent(view);
    setTitle(title);
    setIcon(factory.getImage());
  }
  
  public View getView() {
    return (View)getContent();
  }

  
  @Override
  public void docked(final Docked docked) {
    super.docked(docked);

    
    workbench.addWorkbenchListener(this);
    
    
    view.setContext(workbench.getContext(), true);
    
    
    final AtomicBoolean toolbar = new AtomicBoolean(false);
    
    view.populate(new ToolBar() {
      public void add(Action action) {
        docked.addTool(action);
        toolbar.set(true);
      }

      public void add(JComponent component) {
        docked.addTool(component);
        toolbar.set(true);
      }

      public void addSeparator() {
        docked.addToolSeparator();
        toolbar.set(true);
      }
    });

    
    
    
    

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    if (toolbar.get()) {
      docked.addToolSeparator();
      docked.addTool(new ActionCloseView());
    }

    
  }

  @Override
  public void undocked() {

    
    workbench.removeWorkbenchListener(this);

    
    view.setContext(new Context(), true);

    
    super.undocked();
  }

  
  public void selectionChanged(Context context, boolean isActionPerformed) {
    if (!ignoreSelectionChanged || isActionPerformed)
      view.setContext(context, isActionPerformed);
  }
  
  
  public void commitRequested() {
    view.commit();
  }
  
  
  public boolean workbenchClosing() {
    return view.closing();
  }
  
  
  private static class ContextHook extends Action2 implements AWTEventListener {

    
    private ContextHook() {
      try {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            Toolkit.getDefaultToolkit().addAWTEventListener(ContextHook.this, AWTEvent.MOUSE_EVENT_MASK);
            return null;
          }
        });
      } catch (Throwable t) {
        LOG.log(Level.WARNING, "Cannot install ContextHook", t);
      }
    }
    
    
    private static Workbench getWorkbench(Component component) {
      do {
        if (component instanceof View) {
          ViewDockable dockable = (ViewDockable) ((View)component).getClientProperty(ViewDockable.class);
          return dockable!=null ? dockable.workbench : null;
        }
        component = component.getParent();
      }
      while (component!=null);
      return null;
    }
    
    
    private ViewContext getContext(Component component) {
      ViewContext context;
      
      while (component != null) {
        
        if (component instanceof ContextProvider) {
          ContextProvider provider = (ContextProvider) component;
          context = provider.getContext();
          if (context != null)
            return context;
        }
        
        component = component.getParent();
      }
      
      return null;
    }

    
    public void actionPerformed(ActionEvent event) {
      
      Component focus = FocusManager.getCurrentManager().getFocusOwner();
      if (!(focus instanceof JComponent))
        return;
      
      ViewContext context = getContext(focus);
      if (context != null) {
        JPopupMenu popup = getContextMenu(context, focus);
        if (popup != null)
          popup.show(focus, 0, 0);
      }
      
    }

    
    public void eventDispatched(AWTEvent event) {

      
      if (!(event instanceof MouseEvent))
        return;
      final MouseEvent me = (MouseEvent) event;
      if (!(me.isPopupTrigger() || me.getID() == MouseEvent.MOUSE_CLICKED))
        return;

      
      
      
      
      
      
      
      
      
      

      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          
          
          Component component = SwingUtilities.getDeepestComponentAt(me.getComponent(), me.getX(), me.getY());
          if (!(component instanceof JComponent))
            return;
          Point point = SwingUtilities.convertPoint(me.getComponent(), me.getX(), me.getY(), component);

          
          ViewContext context = getContext(component);
          if (context == null)
            return;

          
          if (me.getButton() == MouseEvent.BUTTON1 && me.getID() == MouseEvent.MOUSE_CLICKED && me.getClickCount() == 2) {
        	  SelectionSink.Dispatcher.fireSelection(component,context, true);
            return;
          }

          
          if (me.isPopupTrigger()) {

            
            MenuSelectionManager.defaultManager().clearSelectedPath();

            
            JPopupMenu popup = getContextMenu(context, (JComponent) component);
            if (popup != null)
              popup.show((JComponent) component, point.x, point.y);

          }
        }
      });

      
    }

    
    private JPopupMenu getContextMenu(ViewContext context, Component target) {
      
      
      if (context==null)
        return null;
      
      List<? extends Property> properties = context.getProperties();
      List<? extends Entity> entities = context.getEntities();
      Gedcom gedcom = context.getGedcom();

      
      MenuSelectionManager.defaultManager().clearSelectedPath();
      
      
      MenuHelper mh = new MenuHelper();
      JPopupMenu popup = mh.createPopup();

      
      mh.createItems(context.getActions());
      
      
      List<Action2> groups = new ArrayList<Action2>(8);
      List<Action2> singles = new ArrayList<Action2>(8);
      Map<Action2.Group,Action2.Group> lookup = new HashMap<Action2.Group,Action2.Group>();
      for (Action2 action : getProvidedActions(getWorkbench(target).getActionProviders(), context)) {
        if (action instanceof Action2.Group) {
          Action2.Group group = lookup.get(action);
          if (group!=null) {
            group.add(new ActionProvider.SeparatorAction());
            group.addAll((Action2.Group)action);
          } else {
            lookup.put((Action2.Group)action, (Action2.Group)action);
            groups.add((Action2.Group)action);
          }
        } else {
          singles.add(action);
        }
      }
      
      
      mh.createItems(groups);
      mh.createItems(singles);
      
      
      return popup;
    }
    
    private List<Action2> getProvidedActions(List<ActionProvider> providers, Context context) {
      
      List<Action2> actions = new ArrayList<Action2>(8);
      for (ActionProvider provider : providers) 
        actions.addAll(provider.createActions(context, Purpose.CONTEXT));
      
      return actions;
    }

    
  } 

  public void gedcomClosed(Gedcom gedcom) {
  }

  public void gedcomOpened(Gedcom gedcom) {
  }
  
  
  private class ActionCloseView extends Action2 {

    
    protected ActionCloseView() {
      setImage(Images.imgClose);
      setTip(Resources.get(this).getString("cc.tip.close_view", factory.getTitle()));
    }

    
    public void actionPerformed(ActionEvent event) {
      workbench.closeView(factory.getClass());
    }
  } 

  public void viewClosed(View view) {
  }

  public void viewOpened(View view) {
  }

} 