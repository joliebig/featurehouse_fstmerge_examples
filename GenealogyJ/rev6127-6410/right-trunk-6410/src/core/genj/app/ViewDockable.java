
package genj.app;

import genj.gedcom.Context;
import genj.gedcom.Gedcom;
import genj.util.Resources;
import genj.util.Trackable;
import genj.util.swing.Action2;
import genj.util.swing.DialogHelper;
import genj.util.swing.MenuHelper;
import genj.util.swing.ToolbarWidget;
import genj.util.swing.DialogHelper.ComponentVisitor;
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
  
  private Tools toolbar = new Tools();
  
  
  
  public ViewDockable(Workbench workbench, ViewFactory factory) {

    this.workbench = workbench;
    this.factory = factory;
    
    
    String title = factory.getTitle();

    
    view = factory.createView();
    
    
    view.putClientProperty(ViewDockable.class, this);

    
    setContent(view);
    setTitle(title);
    setIcon(factory.getImage());
  }
  
  public static ViewDockable getDockable(View view) {
    return (ViewDockable)view.getClientProperty(ViewDockable.class);
  }
  
  public void close() {
    workbench.closeView(factory.getClass());
  }
  
  public View getView() {
    return (View)getContent();
  }

  public ViewFactory getViewFactory() {
    return factory;
  }
  
  @Override
  public void docked(final Docked docked) {
    super.docked(docked);
    
    
    workbench.addWorkbenchListener(this);
    
    
    view.setContext(workbench.getContext(), true);
    
    
    toolbar.beginUpdate();
    view.populate(toolbar);
    toolbar.endUpdate();

    
  }

  @Override
  public void undocked() {

    
    workbench.removeWorkbenchListener(this);

    
    view.setContext(new Context(), true);

    
    super.undocked();
  }

  
  public void selectionChanged(Workbench workbench, Context context, boolean isActionPerformed) {
    if (!ignoreSelectionChanged || isActionPerformed)
      view.setContext(context, isActionPerformed);
  }
  
  
  public void commitRequested(Workbench workbench) {
    view.commit();
  }
  
  
  public void workbenchClosing(Workbench workbench) {
    view.closing();
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
      
      Component result = DialogHelper.visitOwners(component, new ComponentVisitor() {
        public Component visit(Component parent, Component child) {
          if (parent instanceof Workbench)
            return (Workbench)parent;
          if (parent instanceof View) {
            ViewDockable dockable = (ViewDockable) ((View)parent).getClientProperty(ViewDockable.class);
            return dockable.workbench;
          }
          
          return null;
        }
      });
      
      return result instanceof Workbench ? (Workbench)result : null;
    }

    
    public void actionPerformed(ActionEvent event) {
      
      Component focus = FocusManager.getCurrentManager().getFocusOwner();
      if (!(focus instanceof JComponent))
        return;
      
      ViewContext context = new ContextProvider.Lookup(focus).getContext();
      if (context != null) {
        JPopupMenu popup = getContextMenu(context, getWorkbench((Component)event.getSource()));
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

      
      
      
      
      
      
      
      
      
      
      
      
      final Workbench workbench = getWorkbench((Component)me.getSource());
      if (workbench==null)
        return;
      
      
      final Component source = SwingUtilities.getDeepestComponentAt(me.getComponent(), me.getX(), me.getY());
      final ContextProvider.Lookup lookup = new ContextProvider.Lookup(source);
      if (lookup.getContext()==null)
        return;

      final Point point = SwingUtilities.convertPoint(me.getComponent(), me.getX(), me.getY(), workbench);
      
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          
          
          if (lookup.getProvider() == source
              && me.getButton() == MouseEvent.BUTTON1 
              && me.getID() == MouseEvent.MOUSE_CLICKED 
              && me.getClickCount() == 2) {
            SelectionSink.Dispatcher.fireSelection(me.getComponent(), lookup.getContext(), true);
            return;
          }

          
          if (me.isPopupTrigger()) {

            
            MenuSelectionManager.defaultManager().clearSelectedPath();

            
            JPopupMenu popup = getContextMenu(lookup.getContext(), workbench);
            if (popup != null)
              popup.show(workbench, point.x, point.y);

          }
        }
      });

      
    }

    
    private JPopupMenu getContextMenu(ViewContext context, Workbench workbench) {
      
      
      if (context==null||workbench==null)
        return null;
      
      
      MenuSelectionManager.defaultManager().clearSelectedPath();
      
      
      MenuHelper mh = new MenuHelper();
      JPopupMenu popup = mh.createPopup();

      
      mh.createItems(context.getActions());
      
      
      List<Action2> groups = new ArrayList<Action2>(8);
      List<Action2> singles = new ArrayList<Action2>(8);
      Map<Action2.Group,Action2.Group> lookup = new HashMap<Action2.Group,Action2.Group>();
      for (Action2 action : getProvidedActions(workbench.getProviders(ActionProvider.class), context)) {
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
    
    private Action2.Group getProvidedActions(List<ActionProvider> providers, Context context) {
      Action2.Group group = new Action2.Group("");
      
      for (ActionProvider provider : providers) 
        provider.createActions(context, Purpose.CONTEXT, group);
      
      return group;
    }

    
  } 

  public void gedcomClosed(Workbench workbench, Gedcom gedcom) {
  }

  public void gedcomOpened(Workbench workbench, Gedcom gedcom) {
  }
  
  
  private class ActionCloseView extends Action2 {

    
    protected ActionCloseView() {
      setImage(genj.view.Images.imgClose);
      setTip(Resources.get(this).getString("cc.tip.close_view", factory.getTitle()));
    }

    
    public void actionPerformed(ActionEvent event) {
      workbench.closeView(factory.getClass());
    }
  } 

  public void viewClosed(Workbench workbench, View view) {
  }
  
  public void viewRestored(Workbench workbench, View view) {
  }

  public void viewOpened(Workbench workbench, View view) {
  }

  public void processStarted(Workbench workbench, Trackable process) {
  }

  public void processStopped(Workbench workbench, Trackable process) {
  }

  
  private class Tools implements ToolBar {
    
    boolean isEmpty = true;
    boolean hasDefaults = false;
    
    public void add(Action action) {
      ToolbarWidget.patch(getDocked().addTool(action));
      isEmpty = false;
    }
    public void add(JComponent component) {
      getDocked().addTool(component);
      component.setFocusable(false);
      isEmpty = false;
    }
    public void addSeparator() {
      if (!isEmpty)
        getDocked().addToolSeparator();
    }
    public void beginUpdate() {
      hasDefaults = false;
      isEmpty = true;
      getDocked().clearTools();
    }
    public void endUpdate() {
      if (hasDefaults)
        return;
      hasDefaults = true;
      
      
      try {
        if (isEmpty && Toolkit.getDefaultToolkit().getScreenSize().height<1024)
          return;
      } catch (Throwable t) {
        
      }
      addSeparator();
      add(new ActionCloseView());
    }

  };

} 