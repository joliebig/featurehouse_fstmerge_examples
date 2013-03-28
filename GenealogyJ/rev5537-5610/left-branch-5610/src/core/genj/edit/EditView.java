
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
import genj.view.ContextProvider;
import genj.view.ToolBar;
import genj.view.View;
import genj.view.ViewContext;
import genj.window.WindowManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;
import java.util.logging.Logger;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;

import spin.Spin;


public class EditView extends View implements ContextProvider  {
  
   final static Logger LOG = Logger.getLogger("genj.edit");
  private final static Registry REGISTRY = Registry.get(EditView.class);
  
  
  
  private Stack<Context> backs = new Stack<Context>(), forwards = new Stack<Context>();
  
  
  private BeanFactory beanFactory;

  
  static final Resources resources = Resources.get(EditView.class);

  
  private Back     back = new Back();
  private Forward  forward = new Forward();
  private Mode     mode = new Mode();
  private Callback callback = new Callback();
  private Undo undo = new Undo();
  private Redo redo = new Redo();
  private Sticky sticky = new Sticky();
  
  
  private Editor editor;
  
  
  public EditView() {
    
    super(new BorderLayout());
    
    
    beanFactory = new BeanFactory(REGISTRY);

    
    mode.setSelected(REGISTRY.get("advanced", false));

    
    InputMap imap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
    ActionMap amap = getActionMap();
    imap.put(KeyStroke.getKeyStroke("alt LEFT"), back);
    amap.put(back, back);
    imap.put(KeyStroke.getKeyStroke("alt RIGHT"), forward);
    amap.put(forward, forward);

    
  }
  
  

  
  
  private void setEditor(Editor set) {

    
    Context old = null;
    if (set!=null) {
      
      
      commit();
  
      
      old = editor!=null ? editor.getContext() : null;
    }
    
    
    if (editor!=null) {
      editor.setContext(new Context());
      editor = null;
      removeAll();
    }
      
    
    editor = set;
    if (editor!=null) {
      add(editor, BorderLayout.CENTER);
      if (old!=null)
        editor.setContext(old);
    }
    
    
    revalidate();
    repaint();
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
    
    int rc = WindowManager.getInstance().openDialog(null, 
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
    return editor!=null ? editor.getContext() : null;
  }
  
  @Override
  public void commit() {
    if (editor!=null)
      editor.commit();
  }
  
  public void setContext(Context context, boolean isActionPerformed) {
    
    
    if (editor==null) {
      
      
      if (context.getGedcom()==null)
        return;
      
      
      setEditor(mode.isSelected() ? new AdvancedEditor(context.getGedcom(), this, REGISTRY) : new BasicEditor(context.getGedcom(), this, REGISTRY));

      sticky.setSelected(false);
    }
      
    
    Context old = editor.getContext();
    if (old!=null && (context==null||context.getGedcom()!=old.getGedcom())) {
      callback.follow(null);
      undo.follow(null);
      redo.follow(null);

    } else {
      
      if (sticky.isSelected())
        return;
    }
    
    
    if (context.getGedcom()==null) {
      setEditor(null);
      return;
    }
    
    
    callback.follow(context.getGedcom());
    undo.follow(context.getGedcom());
    redo.follow(context.getGedcom());

    
    if (isActionPerformed) {
      if (context.getProperty() instanceof PropertyXRef) {
        PropertyXRef xref = (PropertyXRef)context.getProperty();
        xref = xref.getTarget();
        if (xref!=null)
          context = new ViewContext(xref);
      }

    }
    
    
    if (old.getEntity()!=null && old.getEntity()!=context.getEntity()) {

      backs.push(new Context(old));
      
      
      while (backs.size()>32)
        backs.remove(0);
      
      back.setEnabled(true);
      forwards.clear();
      forward.setEnabled(false);
    }

    
    editor.setContext(context);

  }
  
  public void back() {
    
    if (backs.isEmpty())
      return;

    
    Context old = editor.getContext();
    if (old.getEntities().size()>0) {
      forwards.push(editor.getContext());
      forward.setEnabled(true);
    }
    
    
    editor.setContext(backs.pop());
    
    
    back.setEnabled(backs.size()>0);

  }
  
  public void forward() {
    
    if (forwards.isEmpty())
      return;
    
    
    Context old = editor.getContext();
    if (old.getEntities().size()>0) {
      backs.push(editor.getContext());
      back.setEnabled(true);
    }
    
    
    ViewContext context = new ViewContext((Context)forwards.pop());
    
    editor.setContext(context);
    
    
    forward.setEnabled(forwards.size()>0);
  }

  
  
  public void populate(ToolBar toolbar) {

    
    toolbar.add(back);
    toolbar.add(forward);
    
    
    toolbar.add(new JToggleButton(sticky));
    toolbar.add(undo);
    toolbar.add(redo);
    
    
    toolbar.addSeparator();
    toolbar.add(new JToggleButton(mode));
    
    
  }
  
  
  public Dimension getPreferredSize() {
    return new Dimension(256,480);
  }
  
  
  public Entity getEntity() {
    return editor.getContext().getEntity();
  }
    




















  
  
  private class Sticky extends Action2 {
    
    protected Sticky() {
      super.setImage(Images.imgStickOff);
      super.setTip(resources, "action.stick.tip");
      super.setSelected(false);
    }
    
    public void actionPerformed(ActionEvent event) {
      setSelected(isSelected());
    }
    @Override
    public void setSelected(boolean selected) {
      super.setSelected(selected);
      super.setImage(isSelected() ? Images.imgStickOn : Images.imgStickOff);
    }
  } 
  
  
  private class Mode extends Action2 {
    private Mode() {
      setImage(Images.imgView);
      setTip(resources, "action.mode");
      super.setSelected(false);
    }
    public void actionPerformed(ActionEvent event) {
      setSelected(isSelected());
    }
    @Override
    public void setSelected(boolean selected) {
      super.setSelected(selected);
      REGISTRY.put("advanced", selected);
      if (getContext()!=null)
        setEditor(selected ? new AdvancedEditor(getContext().getGedcom(), EditView.this, REGISTRY) : new BasicEditor(getContext().getGedcom(), EditView.this, REGISTRY));
    }
  } 

    
  private class Forward extends Action2 {
    
    
    public Forward() {
      
      
      setImage(Images.imgForward);
      setTip(Resources.get(this).getString("action.forward.tip"));
      setEnabled(false);
      
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
    
    private Gedcom gedcom;
    
    void follow(Gedcom newGedcom) {
      
      if (gedcom==newGedcom)
        return;
      
      if (gedcom!=null) {
        gedcom.removeGedcomListener((GedcomListener)Spin.over(this));
        backs.clear();
        forwards.clear();
        back.setEnabled(false);
        forward.setEnabled(false);
      }
      
      gedcom = newGedcom;
        
      if (gedcom!=null) {
        gedcom.addGedcomListener((GedcomListener)Spin.over(this));
      }
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
      
      if (editor.getContext().getEntities().size()==0 && !backs.isEmpty())
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
      
      for (ListIterator<Context> it = stack.listIterator(); it.hasNext(); ) {
        Context ctx = it.next();
        if (ctx.getProperties().contains(prop)) {
          List<Property> props = new ArrayList<Property>(ctx.getProperties());
          props.remove(prop);
          it.set(new Context(ctx.getGedcom(), ctx.getEntities(), props));
        }
      }
      
    }

  } 
  
} 
