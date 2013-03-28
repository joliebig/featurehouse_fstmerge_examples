
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
import genj.view.ContextProvider;
import genj.view.ToolBar;
import genj.view.View;
import genj.view.ViewContext;
import genj.window.WindowManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

import spin.Spin;


public class EditView extends View implements ContextProvider  {
  
   final static Logger LOG = Logger.getLogger("genj.edit");
  
  
  private Gedcom  gedcom;
  
  
  private Registry registry;
  
  
  private Stack<Context> backs = new Stack<Context>(), forwards = new Stack<Context>();
  
  
  private BeanFactory beanFactory;

  
  static final Resources resources = Resources.get(EditView.class);

  
  private Sticky   sticky = new Sticky();
  private Back     back = new Back();
  private Forward  forward = new Forward();
  private Mode     mode;
  private Callback callback = new Callback();
  private Undo undo;
  private Redo redo;
  
  
  private  boolean isSticky = false;

  
  private Editor editor;
  
  
  public EditView(String setTitle, Context context, Registry setRegistry) {
    
    super(new BorderLayout());
    
    
    gedcom   = context.getGedcom();
    registry = setRegistry;
    beanFactory = new BeanFactory(registry);

    
    mode = new Mode();
    undo = new Undo(gedcom);
    redo = new Redo(gedcom);
    
    
    setAdvanced(registry.get("advanced", false));
    
    
    InputMap imap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
    ActionMap amap = getActionMap();
    imap.put(KeyStroke.getKeyStroke("alt LEFT"), back);
    amap.put(back, back);
    imap.put(KeyStroke.getKeyStroke("alt RIGHT"), forward);
    amap.put(forward, forward);

    
    Entity entity = gedcom.getEntity(registry.get("entity", (String)null));
    if (registry.get("sticky", false) && entity!=null) {
      isSticky = true;
    } else {
      entity = context.getEntity();
    }
    if (entity!=null)
      setContext(new ViewContext(entity));

    
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
    
    
    callback.enable();
    
  }

  
  public void removeNotify() {
    
    
    registry.put("sticky", isSticky);
    Entity entity = editor.getContext().getEntity();
    if (entity!=null)
      registry.put("entity", entity.getId());

    
    registry.put("advanced", mode.advanced);

    
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
  
  
  public ViewContext getContext() {
    return editor.getContext();
  }
  
  @Override
  public void commit() {
    editor.commit();
  }
  
  public void select(Context context, boolean isActionPerformed) {
    
    
    if (context.getEntity()==null)
      return;
    
    
    if (isSticky) 
      return;
    
    if (isActionPerformed) {
      
      if (context.getProperty() instanceof PropertyXRef) {
        
        PropertyXRef xref = (PropertyXRef)context.getProperty();
        xref = xref.getTarget();
        if (xref!=null)
          context = new ViewContext(xref);
      }
      
      
    }
    
    
    setContext(context);
    
    
  }
  
  public void setAdvanced(boolean advanced) {
    setEditor(advanced ? (Editor)new AdvancedEditor() : new BasicEditor());
  }
  
  public void back() {
    
    if (backs.isEmpty())
      return;

    
    Context old = editor.getContext();
    if (old.getEntities().length>0) {
      forwards.push(editor.getContext());
      forward.setEnabled(true);
    }
    
    
    editor.setContext((Context)backs.pop());
    
    
    back.setEnabled(backs.size()>0);

  }
  
  public void forward() {
    
    if (forwards.isEmpty())
      return;
    
    
    Context old = editor.getContext();
    if (old.getEntities().length>0) {
      backs.push(editor.getContext());
      back.setEnabled(true);
    }
    
    
    ViewContext context = new ViewContext((Context)forwards.pop());
    
    
    
    
    editor.setContext(context);
    
    
    forward.setEnabled(forwards.size()>0);
  }

  
  public void setContext(Context context) {
    
    
    ViewContext current = editor.getContext();
    if (current.getEntity()!=context.getEntity()) {
      
      Context c = new Context(current.getEntity());
      for (Property p : current.getProperties()) 
        if (p.getEntity().equals(current.getEntity()))
          c.addProperty(p);
      
      backs.push(c);
      
      
      while (backs.size()>32)
        backs.remove(0);
      
      back.setEnabled(true);
      forwards.clear();
      forward.setEnabled(false);
    }

    
    editor.setContext(context);

    


    
  }
  
  
  public void populate(ToolBar toolbar) {

    
    toolbar.add(back);
    toolbar.add(forward);
    
    
    ButtonHelper bh = new ButtonHelper();
    toolbar.add(bh.create(sticky, Images.imgStickOn, isSticky));
    
    
    toolbar.add(undo);
    toolbar.add(redo);
    
    
    
    
    
    toolbar.addSeparator();
    toolbar.add(bh.create(mode, Images.imgAdvanced, mode.advanced));
    
    
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
    




















  
  
  private class Sticky extends Action2 {
    
    protected Sticky() {
      super.setImage(Images.imgStickOff);
      super.setTip(resources, "action.stick.tip");
    }
    
    public void actionPerformed(ActionEvent event) {
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
    public void actionPerformed(ActionEvent event) {
      advanced = !advanced;
      setAdvanced(advanced);
    }
  } 

    
  private class Forward extends Action2 {
    
    
    public Forward() {
      
      
      setImage(Images.imgForward);
      setTip(Resources.get(this).getString("action.forward.tip"));
      
    }
    
    
    public void actionPerformed(ActionEvent event) {
      
      if (!forwards.isEmpty())
        forward();
    }
    
  } 
  
    
  private class Back extends Action2 {
    
    
    public Back() {
      
      
      setImage(Images.imgBack);
      setTip(Resources.get(this).getString("action.return.tip"));
      setEnabled(false);
      
    }

    
    public void actionPerformed(ActionEvent event) {
      
      if (!backs.isEmpty()) 
        back();
    }
    
  } 

    
  private class Callback extends GedcomListenerAdapter {
    
    void enable() {
      gedcom.addGedcomListener((GedcomListener)Spin.over(this));
      backs.clear();
      forwards.clear();
      back.setEnabled(false);
      forward.setEnabled(false);
    }
    
    void disable() {
      gedcom.removeGedcomListener((GedcomListener)Spin.over(this));
      backs.clear();
      forwards.clear();
      back.setEnabled(false);
      forward.setEnabled(false);
    }
    
    public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
      remove(entity, backs);
      remove(entity, forwards);
    }

    public void gedcomPropertyDeleted(Gedcom gedcom, Property property, int pos, Property removed) {
      remove(removed, backs);
      remove(removed, forwards);
    }

    public void gedcomWriteLockReleased(Gedcom gedcom) {
      
      back.setEnabled(!backs.isEmpty());
      forward.setEnabled(!forwards.isEmpty());
      
      if (editor.getContext().getEntities().length==0 && !backs.isEmpty())
        back();
    }
    
    void remove(Entity entity,  Stack<Context> stack) {
      
      for (Iterator<Context> it = stack.listIterator(); it.hasNext(); ) {
        Context ctx = it.next();
        if (ctx.getEntity().equals(entity))
          it.remove();
      }
    }
    
    void remove(Property prop, Stack<Context> stack) {
      List<Property> list = Collections.singletonList(prop);
      
      for (Iterator<Context> it = stack.listIterator(); it.hasNext(); ) {
        Context ctx = it.next();
        ctx.removeProperties(list);
      }
      
    }

  } 
  
} 
