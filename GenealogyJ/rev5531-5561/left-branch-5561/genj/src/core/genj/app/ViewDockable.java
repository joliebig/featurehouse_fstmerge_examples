
package genj.app;

import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.gedcom.TagPath;
import genj.util.Registry;
import genj.util.swing.Action2;
import genj.util.swing.MenuHelper;
import genj.view.ActionProvider;
import genj.view.ContextProvider;
import genj.view.SelectionListener;
import genj.view.ToolBar;
import genj.view.View;
import genj.view.ViewContext;
import genj.view.ViewFactory;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
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


class ViewDockable extends DefaultDockable implements SelectionListener, WorkbenchListener {
  
  private final static Logger LOG = Logger.getLogger("genj.app");
  private final static ContextHook HOOK = new ContextHook();

  private ViewFactory factory;
  private View view;
  private Workbench workbench;
  private boolean ignoreSelectionChanged = false;

  
  public ViewDockable(Workbench workbench, ViewFactory factory, Context context) {

    this.workbench = workbench;
    this.factory = factory;
    
    
    String title = factory.getTitle();

    
    Registry registry = new Registry(Registry.lookup(context.getGedcom().getOrigin().getFileName(), context.getGedcom().getOrigin()), factory.getClass().getName() + ".1");

    
    
    view = factory.createView(title, registry, context);
    
    
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

    
    view.addSelectionListener(this);

    
    workbench.addWorkbenchListener(this);

    
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

    
    view.removeSelectionListener(this);

    
    workbench.removeWorkbenchListener(this);

    
    super.undocked();
  }

  
  public void select(Context context, boolean isActionPerformed) {
    ignoreSelectionChanged = true;
    try {
      workbench.fireSelection(context, isActionPerformed);
    } finally {
      ignoreSelectionChanged = false;
    }
  }

  
  public void selectionChanged(Context context, boolean isActionPerformed) {
    if (!ignoreSelectionChanged)
      view.select(context, isActionPerformed);
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
            View.fireSelection(component, context, true);
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
      
      Property[] properties = context.getProperties();
      Entity[] entities = context.getEntities();
      Gedcom gedcom = context.getGedcom();

      
      MenuSelectionManager.defaultManager().clearSelectedPath();
      
      
      List<ActionProvider> providers = getWorkbench(target).getActionProviders();
      
      
      
      while (target.getParent()!=null) target = target.getParent();

      
      MenuHelper mh = new MenuHelper().setTarget(target);
      JPopupMenu popup = mh.createPopup();

      
      mh.createItems(context.getActions());
      mh.createSeparator(); 
      
      
      if (properties.length>1) {
        mh.createMenu("'"+Property.getPropertyNames(properties, 5)+"' ("+properties.length+")");
        for (ActionProvider provider : providers) try {
          mh.createSeparator();
          mh.createItems(provider.createActions(properties));
        } catch (Throwable t) {
          LOG.log(Level.WARNING, "Action Provider exception on createActions(Property[])", t);
        }
        mh.popMenu();
      }
      if (properties.length==1) {
        Property property = properties[0];
        while (property!=null&&!(property instanceof Entity)&&!property.isTransient()) {
          
          mh.createMenu(Property.LABEL+" '"+TagPath.get(property).getName() + '\'' , property.getImage(false));
          for (ActionProvider provider : providers) try {
            mh.createItems(provider.createActions(property));
          } catch (Throwable t) {
            LOG.log(Level.WARNING, "Action Provider exception on createActions(Property)", t);
          }
          mh.popMenu();
          
          property = property.getParent();
        }
      }
          
      
      if (entities.length>1) {
        mh.createMenu("'"+Property.getPropertyNames(entities,5)+"' ("+entities.length+")");
        for (ActionProvider provider : providers) try {
          mh.createSeparator();
          mh.createItems(provider.createActions(entities));
        } catch (Throwable t) {
          LOG.log(Level.WARNING, "Action Provider exception on createActions(Entity[])", t);
        }
        mh.popMenu();
      }
      if (entities.length==1) {
        Entity entity = entities[0];
        String title = Gedcom.getName(entity.getTag(),false)+" '"+entity.getId()+'\'';
        mh.createMenu(title, entity.getImage(false));
        for (ActionProvider provider : providers) try {
          mh.createItems(provider.createActions(entity));
        } catch (Throwable t) {
          LOG.log(Level.WARNING, "Action Provider exception on createActions(Entity)", t);
        }
        mh.popMenu();
      }
          
      
      String title = "Gedcom '"+gedcom.getName()+'\'';
      mh.createMenu(title, Gedcom.getImage());
      for (ActionProvider provider : providers) try {
        mh.createItems(provider.createActions(gedcom));
      } catch (Throwable t) {
        LOG.log(Level.WARNING, "Action Provider exception on createActions(Gedcom", t);
      }
      mh.popMenu();

      
      return popup;
    }
    
  } 

  public void gedcomClosed(Gedcom gedcom) {
  }

  public void gedcomOpened(Gedcom gedcom) {
  }
  
  
  private class ActionCloseView extends Action2 {

    
    protected ActionCloseView() {
      setImage(Images.imgClose);
    }

    
    public void actionPerformed(ActionEvent event) {
      workbench.closeView(factory);
    }
  } 

} 