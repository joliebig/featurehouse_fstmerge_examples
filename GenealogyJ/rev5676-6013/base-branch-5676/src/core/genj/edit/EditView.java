
package genj.edit;

import genj.edit.actions.Redo;
import genj.edit.actions.Undo;
import genj.edit.beans.BeanFactory;
import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomListener;
import genj.gedcom.GedcomListenerAdapter;
import genj.gedcom.Property;
import genj.gedcom.PropertyXRef;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ButtonHelper;
import genj.util.swing.PopupWidget;
import genj.view.CommitRequestedEvent;
import genj.view.ContextProvider;
import genj.view.ContextSelectionEvent;
import genj.view.ToolBarSupport;
import genj.view.ViewContext;
import genj.view.ViewManager;
import genj.window.WindowBroadcastListener;
import genj.window.WindowManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import spin.Spin;


public class EditView extends JPanel implements ToolBarSupport, WindowBroadcastListener, ContextProvider  {
  
   final static Logger LOG = Logger.getLogger("genj.edit");
  
  
  private static List instances = new LinkedList();

  
  private Gedcom  gedcom;
  
  
  private Registry registry;
  
  
  private BeanFactory beanFactory;

  
  private ViewManager manager;
  
  
  static final Resources resources = Resources.get(EditView.class);

  
  private Sticky   sticky = new Sticky();
  private Back     back = new Back();
  private Forward forward = new Forward();
  private Mode     mode;
  private ContextMenu contextMenu = new ContextMenu();
  private Callback callback = new Callback();
  private Undo undo;
  private Redo redo;
  
  
  private  boolean isSticky = false;

  
  private Editor editor;
  
  
  public EditView(String setTitle, Gedcom setGedcom, Registry setRegistry, ViewManager setManager) {
    
    super(new BorderLayout());
    
    
    gedcom   = setGedcom;
    registry = setRegistry;
    manager  = setManager;
    beanFactory = new BeanFactory(manager, registry);

    
    mode = new Mode();
    undo = new Undo(gedcom);
    redo = new Redo(gedcom);
    
    
    if (registry.get("advanced", false))
      mode.trigger();
    
    
    InputMap imap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
    ActionMap amap = getActionMap();
    imap.put(KeyStroke.getKeyStroke("alt LEFT"), back);
    amap.put(back, back);
    imap.put(KeyStroke.getKeyStroke("alt RIGHT"), forward);
    amap.put(forward, forward);

    
  }
  
  

  
  
  private void setEditor(Editor set) {

    
    ViewContext old = null;
    if (editor!=null) {
      old = editor.getContext();
      editor.setContext(new ViewContext(gedcom));
    }
    
    
    removeAll();
      
    
    editor = set;
    editor.init(gedcom, this, registry);

    
    add(editor, BorderLayout.CENTER);

    
    if (old!=null)
      editor.setContext(old);
      
    
    revalidate();
    repaint();
  }

  
  public void addNotify() {
    
    
    super.addNotify();    
    
    
    instances.add(this);
    
    
    Entity entity = gedcom.getEntity(registry.get("entity", (String)null));
    if (registry.get("sticky", false) && entity!=null) {
      isSticky = true;
    } else {
      
      ViewContext context = ContextSelectionEvent.getLastBroadcastedSelection();
      if (context!=null&&context.getGedcom()==gedcom&&gedcom.contains(context.getEntity()))
        entity = context.getEntity();
      
      if (entity==null)
        entity = gedcom.getFirstEntity(Gedcom.INDI);
    }
    
    if (entity!=null)
      setContext(new ViewContext(entity));

    
    callback.enable();
    gedcom.addGedcomListener((GedcomListener)Spin.over(undo));
    gedcom.addGedcomListener((GedcomListener)Spin.over(redo));
    
  }

  
  public void removeNotify() {
    
    
    registry.put("sticky", isSticky);
    Entity entity = editor.getContext().getEntity();
    if (entity!=null)
      registry.put("entity", entity.getId());

    
    registry.put("advanced", mode.advanced);

    
    instances.remove(this);
    
    
    callback.disable();
    gedcom.removeGedcomListener((GedcomListener)Spin.over(undo));
    gedcom.removeGedcomListener((GedcomListener)Spin.over(redo));
    
    
    super.removeNotify();

    
  }
  
  
   BeanFactory getBeanFactory() {
    return beanFactory;
  }
  
  
   boolean isCommitChanges() {
    
    
    
    
    if (!getTopLevelAncestor().isVisible())
      return false;
      
    
    if (Options.getInstance().isAutoCommit)
      return true;
    
    JCheckBox auto = new JCheckBox(resources.getString("confirm.autocomit"));
    auto.setFocusable(false);
    
    int rc = WindowManager.getInstance(this).openDialog(null, 
        resources.getString("confirm.keep.changes"), WindowManager.QUESTION_MESSAGE, 
        new JComponent[] {
          new JLabel(resources.getString("confirm.keep.changes")),
          auto
        },
        Action2.yesNo(), 
        this
    );
    
    if (rc!=0)
      return false;
    
    Options.getInstance().isAutoCommit = auto.isSelected();
    
    return true;
    
  }
  
  
   static EditView[] getInstances(Gedcom gedcom) {
    List result = new ArrayList();
    Iterator it = instances.iterator();
    while (it.hasNext()) {
      EditView edit = (EditView)it.next();
      if (edit.gedcom==gedcom)
        result.add(edit);
    }
    return (EditView[])result.toArray(new EditView[result.size()]);
  }
  
  
  public ViewContext getContext() {
    return editor.getContext();
  }

  
  public boolean handleBroadcastEvent(genj.window.WindowBroadcastEvent event) {
    
    
    if (event instanceof CommitRequestedEvent && ((CommitRequestedEvent)event).getGedcom()==gedcom) {
      editor.commit();
      return true;
    }
    
    
    
    ContextSelectionEvent cse = ContextSelectionEvent.narrow(event, gedcom);
    if (cse==null) 
      return true;
    
    ViewContext context = cse.getContext();
    
    
    if (context.getEntity()==null)
      return true;
    
    
    if (cse.isInbound()) {
      
      if (!isSticky) setContext(context); 
      
      return false;
    }
      
    
    if (cse.isActionPerformed()) {
      
      if (context.getProperty() instanceof PropertyXRef) {
        
        PropertyXRef xref = (PropertyXRef)context.getProperty();
        xref = xref.getTarget();
        if (xref!=null)
          context = new ViewContext(xref);
      }
      
      
      setContext(context);
      
    }
      
    
    return true;
  }
  
  public void setContext(ViewContext context) {
    
    
    ViewContext current = editor.getContext();
    if (current.getEntity()!=context.getEntity())
      back.push(current);

    
    setContextImpl(context);
    
    
  }
  
  private void setContextImpl(ViewContext context) {
    
    editor.setContext(context);

    
    context = editor.getContext();
    manager.setTitle(this, context!=null&&context.getEntity()!=null?context.getEntity().toString():"");
    
  }
  
  
  public void populate(JToolBar bar) {

    
    ButtonHelper bh = new ButtonHelper()
      .setInsets(0)
      .setContainer(bar);

    
    bh.create(back);
    bh.create(forward);
    
    
    bh.create(sticky, Images.imgStickOn, isSticky);
    
    
    bh.create(undo);
    bh.create(redo);
    
    
    bar.add(contextMenu);
    
    
    bar.addSeparator();
    bh.create(mode, Images.imgAdvanced, mode.advanced).setFocusable(false);
    
    
  }
  
  
  public Dimension getPreferredSize() {
    return new Dimension(256,480);
  }
  
  
  public boolean isSticky() {
    return isSticky;
  }
  
  
  public Entity getEntity() {
    return editor.getContext().getEntity();
  }
  
  
  private class ContextMenu extends PopupWidget {
    
    
    private ContextMenu() {
      setIcon(Gedcom.getImage());
      setToolTipText(resources.getString( "action.context.tip" ));
    }
    
    
    protected JPopupMenu createPopup() {
      
      editor.setContext(editor.getContext());
      
      return manager.getContextMenu(editor.getContext(), this);
    }
     
  } 
  
  
  private class Sticky extends Action2 {
    
    protected Sticky() {
      super.setImage(Images.imgStickOff);
      super.setTip(resources, "action.stick.tip");
    }
    
    protected void execute() {
      isSticky = !isSticky;
    }
  } 
  
  
  private class Mode extends Action2 {
    private boolean advanced = false;
    private Mode() {
      setImage(Images.imgView);
      setEditor(new BasicEditor());
      setTip(resources, "action.mode");
    }
    protected void execute() {
      advanced = !advanced;
      setEditor(advanced ? (Editor)new AdvancedEditor() : new BasicEditor());
    }
  } 

    
  private class Forward extends Back {
    
    
    public Forward() {
      
      
      setImage(Images.imgForward);
      setTip(Resources.get(this).getString("action.forward.tip"));
      
    }
    
    
    protected void execute() {
      
      if (stack.size()==0)
        return;
      
      
      Context old = editor.getContext();
      if (old.getEntities().length>0) {
        back.stack.push(editor.getContext());
        back.setEnabled(true);
      }
      
      
      ViewContext context = new ViewContext((Context)stack.pop());
      
      
      WindowManager.broadcast(new ContextSelectionEvent(context, EditView.this));
      setContextImpl(context);
      
      
      setEnabled(stack.size()>0);
    }
    
  } 
  
    
  private class Back extends Action2 {
    
    
    protected Stack stack = new Stack();
    
    
    public Back() {
      
      
      setImage(Images.imgBack);
      setTip(Resources.get(this).getString("action.return.tip"));
      setEnabled(false);
      
    }

    
    protected void execute() {
      if (stack.size()==0)
        return;
      
      
      Context old = editor.getContext();
      if (old.getEntities().length>0) {
        forward.stack.push(editor.getContext());
        forward.setEnabled(true);
      }
      
      
      ViewContext context = new ViewContext((Context)stack.pop());
      
      
      WindowManager.broadcast(new ContextSelectionEvent(context, EditView.this));
      setContextImpl(context);
      
      
      setEnabled(stack.size()>0);
    }
    
    
    public void push(Context context) {
      
      forward.clear();
      
      stack.push(new Context(context));
      
      while (stack.size()>32)
        stack.remove(0);
      
      setEnabled(true);
    }
    
    void clear() {
      stack.clear();
      setEnabled(false);
    }
    
    void remove(Entity entity) {
      
      for (Iterator it = stack.listIterator(); it.hasNext(); ) {
        Context ctx = (Context)it.next();
        Entity[] ents = ctx.getEntities();
        for (int i = 0; i < ents.length; i++) {
          if (ents[i]==entity) {
            it.remove();
            break;
          }
        }
      }
      
      setEnabled(!stack.isEmpty());
    }
    
    void remove(Property prop) {
      List list = Collections.singletonList(prop);
      
      for (Iterator it = stack.listIterator(); it.hasNext(); ) {
        Context ctx = (Context)it.next();
        ctx.removeProperties(list);
      }
      
    }
  } 

    
  private class Callback extends GedcomListenerAdapter {
    
    void enable() {
      gedcom.addGedcomListener((GedcomListener)Spin.over(this));
      back.clear();
      forward.clear();
    }
    
    void disable() {
      gedcom.removeGedcomListener((GedcomListener)Spin.over(this));
      back.clear();
      forward.clear();
    }
    
    public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
      back.remove(entity);
      forward.remove(entity);
    }

    public void gedcomPropertyDeleted(Gedcom gedcom, Property property, int pos, Property removed) {
      back.remove(removed);
      forward.remove(removed);
    }

    public void gedcomWriteLockReleased(Gedcom gedcom) {
      
      if (editor.getContext().getEntities().length==0) {
        if (back.isEnabled()) back.execute();
      }
    }
  } 
  
} 
