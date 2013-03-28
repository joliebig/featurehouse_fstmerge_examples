
package genj.view;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomDirectory;
import genj.gedcom.Property;
import genj.gedcom.TagPath;
import genj.util.MnemonicAndText;
import genj.util.Origin;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.MenuHelper;
import genj.window.WindowManager;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.lang.reflect.Array;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.spi.ServiceRegistry;
import javax.swing.FocusManager;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;


public class ViewManager {
  
   final static Logger LOG = Logger.getLogger("genj.view");

  
   static Resources RESOURCES = Resources.get(ViewManager.class);
  
  
   Map keyStrokes2factories = new HashMap();
  
  
  private ContextHook contextHook = new ContextHook();
  
  
  private ViewFactory[] factories = null;
  
  
  private Map gedcom2factory2handles = new HashMap();
  private LinkedList allHandles = new LinkedList();
  
  
  private WindowManager windowManager = null;
  
  
  public ViewManager(WindowManager windowManager) {

    
    List factories = new ArrayList();
    Iterator it = ServiceRegistry.lookupProviders(ViewFactory.class);
    while (it.hasNext()) 
      factories.add(it.next());

    
    init(windowManager, factories);
    
    
    GedcomDirectory.getInstance().addListener(new GedcomDirectory.Listener() {
      public void gedcomRegistered(int num, Gedcom gedcom) {
      }
      public void gedcomUnregistered(int num, Gedcom gedcom) {
        closeViews(gedcom);
      }
    });
  }
  
  
  public ViewManager(WindowManager windowManager, String[] factoryTypes) {
    
    
    List factories = new ArrayList();
    for (int f=0;f<factoryTypes.length;f++) {    
      try {
        factories.add( (ViewFactory)Class.forName(factoryTypes[f]).newInstance() );
      } catch (Throwable t) {
        LOG.log(Level.SEVERE, "Factory of type "+factoryTypes[f]+" cannot be instantiated", t);
      }
    }
    
    
    init(windowManager, factories);
  }
  
  
  public ViewHandle[] getViews(Gedcom gedcom) {
    
    
    List result = new ArrayList();
    for (Iterator handles = allHandles.iterator(); handles.hasNext() ; ) {
      ViewHandle handle = (ViewHandle)handles.next();
      if (handle.getGedcom()==gedcom)  
        result.add(handle);
    }
    
    
    return (ViewHandle[])result.toArray(new ViewHandle[result.size()]);
  }
  
  
  private void init(WindowManager setWindowManager, List setFactories) {
    
    
    windowManager = setWindowManager;
    
    
    factories = (ViewFactory[])setFactories.toArray(new ViewFactory[setFactories.size()]);
    
    
    for (int f=0;f<factories.length;f++) {    
      ViewFactory factory = factories[f];
      
      String keystroke = "ctrl "+new MnemonicAndText(factory.getTitle(false)).getMnemonic();
      if (!keyStrokes2factories.containsKey(keystroke)) {
        keyStrokes2factories.put(keystroke, factory);
      }
    }
    
    
  }
  
  
   
  
  
  public ViewFactory[] getFactories() {
    return factories;
  }
  
  
   void openSettings(ViewHandle handle) {
    
    
    SettingsWidget settings = (SettingsWidget)windowManager.getContent("settings");
    if (settings==null) {
      settings = new SettingsWidget(this);
      settings.setView(handle);
      windowManager.openWindow(
        "settings", 
        RESOURCES.getString("view.edit.title"),
        Images.imgSettings,
        settings,
        null, null
      );
    } else {
      settings.setView(handle);
    }
    
  }
  
  
  public static Registry getRegistry(Gedcom gedcom) {
    Origin origin = gedcom.getOrigin();
    String name = origin.getFileName();
    return Registry.lookup(name, origin);
  }
  
  
   String getPackage(ViewFactory factory) {
    
    Matcher m = Pattern.compile(".*\\.(.*)\\..*").matcher(factory.getClass().getName());
    if (!m.find())
      throw new IllegalArgumentException("can't resolve package for "+factory);
    return m.group(1);
    
  }

  
  private int getNextInSequence(Gedcom gedcom, ViewFactory factory) {
    
    
    Map factories2handles = (Map)gedcom2factory2handles.get(gedcom);
    if (factories2handles==null)
      return 1;
    List handles = (List)factories2handles.get(factory.getClass());
    if (handles==null)
      return 1;
    
    
    int result = 1;
    for (Iterator it = handles.iterator(); it.hasNext(); ) {
      ViewHandle handle = (ViewHandle)it.next();
      if (handle==null) break;
      result++;
    }
    
    return result;
  }
  
  
  protected void closeView(ViewHandle handle) {
    
    windowManager.close("settings");
    
    windowManager.close(handle.getKey());
    
    MenuSelectionManager.defaultManager().clearSelectedPath();
    
    Map factory2handles = (Map)gedcom2factory2handles.get(handle.getGedcom());
    List handles = (List)factory2handles.get(handle.getFactory().getClass());
    handles.set(handle.getSequence()-1, null);
    allHandles.remove(handle);
    
  }
  
  
  public ViewHandle openView(Class factory, Gedcom gedcom) {
    for (int f=0; f<factories.length; f++) {
      if (factories[f].getClass().equals(factory)) 
        return openView(gedcom, factories[f]);   	
    }
    throw new IllegalArgumentException("Unknown factory "+factory.getName());
  }
  
  
  public ViewHandle openView(Gedcom gedcom, ViewFactory factory) {
    return openView(gedcom, factory, -1);
  }
  
  
  protected ViewHandle openView(final Gedcom gedcom, ViewFactory factory, int sequence) {
    
    
    if (sequence<0)
      sequence = getNextInSequence(gedcom, factory);
    Map factory2handles = (Map)gedcom2factory2handles.get(gedcom);
    if (factory2handles==null) {
      factory2handles = new HashMap();
      gedcom2factory2handles.put(gedcom, factory2handles);
    }
    Vector handles = (Vector)factory2handles.get(factory.getClass());
    if (handles==null) {
      handles = new Vector(10);
      factory2handles.put(factory.getClass(), handles);
    }
    handles.setSize(Math.max(handles.size(), sequence));
    
    
    if (handles.get(sequence-1)!=null) {
      ViewHandle old = (ViewHandle)handles.get(sequence-1);
      windowManager.show(old.getKey());
      return old;
    }
    
    
    Registry registry = new Registry( getRegistry(gedcom), getPackage(factory)+"."+sequence) ;

    
    String title = gedcom.getName()+" - "+factory.getTitle(false)+" ("+registry.getViewSuffix()+")";

    
    JComponent view = factory.createView(title, gedcom, registry, this);
    
    
    final ViewHandle handle = new ViewHandle(this, gedcom, title, registry, factory, view, sequence);
    
    
    ViewContainer container = new ViewContainer(handle);

    
    InputMap inputs = view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    inputs.put(KeyStroke.getKeyStroke("shift F10"), contextHook);
    inputs.put(KeyStroke.getKeyStroke("CONTEXT_MENU"), contextHook); 
    view.getActionMap().put(contextHook, contextHook);

    
    handles.set(handle.getSequence()-1, handle);
    allHandles.add(handle);

    
    Action2 close = new Action2() {
      protected void execute() {
        
        closeView(handle);
      }
    };
    
    
    windowManager.openWindow(handle.getKey(), title, factory.getImage(), container, null,  close);
        
    
    return handle;
  }
  
  
  public void closeViews(Gedcom gedcom) {
    
    
    ViewHandle[] handles = (ViewHandle[])allHandles.toArray(new ViewHandle[allHandles.size()]);
    for (int i=0;i<handles.length;i++) {
      if (handles[i].getGedcom()==gedcom) 
        closeView(handles[i]);
    }
    
    
  }
  
  
  public void showView(JComponent view) {

    
    for (Iterator handles = allHandles.iterator(); handles.hasNext(); ) {
      ViewHandle handle = (ViewHandle)handles.next();
      if (handle.getView()==view) {
        windowManager.show(handle.getKey());
        break;
      }
    }
    
    
  }
  
  
  public void setTitle(JComponent view, String title) {
    
    
    for (Iterator handles = allHandles.iterator(); handles.hasNext(); ) {
      ViewHandle handle = (ViewHandle)handles.next();
      if (handle.getView()==view) {
        windowManager.setTitle(handle.getKey(), handle.getTitle() + (title.length()>0 ? " - " + title : ""));
        break;
      }
    }
    
    
  }

  
  public Object[] getViews(Class of, Gedcom gedcom) {
    
    List result = new ArrayList(16);
    
    
    for (int f=0; f<factories.length; f++) {
      if (of.isAssignableFrom(factories[f].getClass())) 
        result.add(factories[f]);
    }
    
    for (Iterator handles = allHandles.iterator(); handles.hasNext(); ) {
      ViewHandle handle = (ViewHandle)handles.next();
      if (handle.getGedcom()==gedcom && of.isAssignableFrom(handle.getView().getClass()))
        result.add(handle.getView());
    }
    
    
    return result.toArray((Object[])Array.newInstance(of, result.size()));
  }
  
  
  public JPopupMenu getContextMenu(ViewContext context, Component target) {
    
    
    if (context==null)
      return null;
    
    Property[] properties = context.getProperties();
    Entity[] entities = context.getEntities();
    Gedcom gedcom = context.getGedcom();

    
    MenuSelectionManager.defaultManager().clearSelectedPath();
    
    
    
    while (target.getParent()!=null) target = target.getParent();

    
    MenuHelper mh = new MenuHelper().setTarget(target);
    JPopupMenu popup = mh.createPopup();

    
    mh.createItems(context.getActions());
    mh.createSeparator(); 
  
    
    ActionProvider[] as = (ActionProvider[])getViews(ActionProvider.class, context.getGedcom());
    
    
    if (properties.length>1) {
      mh.createMenu("'"+Property.getPropertyNames(properties, 5)+"' ("+properties.length+")");
      for (int i = 0; i < as.length; i++) try {
        mh.createSeparator();
        mh.createItems(as[i].createActions(properties, this));
      } catch (Throwable t) {
        LOG.log(Level.WARNING, "Action Provider threw "+t.getClass()+" on createActions(Property[])", t);
      }
      mh.popMenu();
    }
    if (properties.length==1) {
      Property property = properties[0];
      while (property!=null&&!(property instanceof Entity)&&!property.isTransient()) {
        
        mh.createMenu(Property.LABEL+" '"+TagPath.get(property).getName() + '\'' , property.getImage(false));
        for (int i = 0; i < as.length; i++) try {
          mh.createItems(as[i].createActions(property, this));
        } catch (Throwable t) {
          LOG.log(Level.WARNING, "Action Provider "+as[i].getClass().getName()+" threw "+t.getClass()+" on createActions(Property)", t);
        }
        mh.popMenu();
        
        property = property.getParent();
      }
    }
        
    
    if (entities.length>1) {
      mh.createMenu("'"+Property.getPropertyNames(entities,5)+"' ("+entities.length+")");
      for (int i = 0; i < as.length; i++) try {
        mh.createSeparator();
        mh.createItems(as[i].createActions(entities, this));
      } catch (Throwable t) {
        LOG.log(Level.WARNING, "Action Provider threw "+t.getClass()+" on createActions(Entity[])", t);
      }
      mh.popMenu();
    }
    if (entities.length==1) {
      Entity entity = entities[0];
      String title = Gedcom.getName(entity.getTag(),false)+" '"+entity.getId()+'\'';
      mh.createMenu(title, entity.getImage(false));
      for (int i = 0; i < as.length; i++) try {
        mh.createItems(as[i].createActions(entity, this));
      } catch (Throwable t) {
        LOG.log(Level.WARNING, "Action Provider "+as[i].getClass().getName()+" threw "+t.getClass()+" on createActions(Entity)", t);
      }
      mh.popMenu();
    }
        
    
    String title = "Gedcom '"+gedcom.getName()+'\'';
    mh.createMenu(title, Gedcom.getImage());
    for (int i = 0; i < as.length; i++) try {
      mh.createItems(as[i].createActions(gedcom, this));
    } catch (Throwable t) {
      LOG.log(Level.WARNING, "Action Provider "+as[i].getClass().getName()+" threw "+t.getClass()+" on createActions(Gedcom", t);
    }
    mh.popMenu();

    
    return popup;
  }
  
  
  private class ContextHook extends Action2 implements AWTEventListener {
    
    
    private ContextHook() {
      try {
        AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            Toolkit.getDefaultToolkit().addAWTEventListener(ContextHook.this, AWTEvent.MOUSE_EVENT_MASK);
            return null;
          }
        });
      } catch (Throwable t) {
        LOG.log(Level.WARNING, "Cannot install ContextHook ("+t.getMessage()+")");
      }
    }
    
    
    private ViewContext getContext(Component component) {
      ViewContext context;
      
      while (component!=null) {
        
        if (component instanceof ContextProvider) {
          ContextProvider provider = (ContextProvider)component;
          context = provider.getContext();
          if (context!=null)
            return context;
        }
        
        component = component.getParent();
      }
      
      return null;
    }
    
    
    protected void execute() {
      
      Component focus = FocusManager.getCurrentManager().getFocusOwner();
      if (!(focus instanceof JComponent))
        return;
      
      ViewContext context = getContext(focus);
      if (context!=null) {
        JPopupMenu popup = getContextMenu(context, focus);
        if (popup!=null)
          popup.show(focus, 0, 0);
      }
      
    }
    
    
    public void eventDispatched(AWTEvent event) {
      
      
      if (!(event instanceof MouseEvent)) 
        return;
      final MouseEvent me = (MouseEvent) event;
      if (!(me.isPopupTrigger()||me.getID()==MouseEvent.MOUSE_CLICKED))
        return;

      
      
      
      
      
      
      
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          
          
          Component component  = SwingUtilities.getDeepestComponentAt(me.getComponent(), me.getX(), me.getY());
          if (!(component instanceof JComponent))
            return;
          Point point = SwingUtilities.convertPoint(me.getComponent(), me.getX(), me.getY(), component );
          
          
          ViewContext context = getContext(component);
          if (context==null) 
            return;
    
          
          if (me.getButton()==MouseEvent.BUTTON1&&me.getID()==MouseEvent.MOUSE_CLICKED&&me.getClickCount()==2) {
            WindowManager.broadcast(new ContextSelectionEvent(context, component, true));
            return;
          }
  
          
          if(me.isPopupTrigger())  {
            
            
            MenuSelectionManager.defaultManager().clearSelectedPath();
            
            
            JPopupMenu popup = getContextMenu(context, (JComponent)component);
            if (popup!=null)
              popup.show((JComponent)component, point.x, point.y);
            
          }
        }
      });
        
      
    }
    
  } 
  

} 
