
package genj.edit;

import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.GedcomListenerAdapter;
import genj.gedcom.UnitOfWork;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ButtonHelper;
import genj.util.swing.DialogHelper;
import genj.view.ContextProvider;
import genj.view.ToolBar;
import genj.view.View;
import genj.view.ViewContext;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class EditView extends View implements ContextProvider{
  
   final static Logger LOG = Logger.getLogger("genj.edit");
  private final static Registry REGISTRY = Registry.get(EditView.class);
  static final Resources RESOURCES = Resources.get(EditView.class);
  
  private Mode     mode = new Mode();
  private Sticky sticky = new Sticky();
  private Focus focus = new Focus();
  private OK ok = new OK();
  private Cancel cancel = new Cancel();
  private Callback callback = new Callback();
  
  private boolean isIgnoreSetContext = false;
  private boolean isChangeSource = false;
  
  private Editor editor;
  private JPanel buttons;
  private ToolBar toolbar;
  private Gedcom gedcom;
  
  
  public EditView() {
    
    super(new BorderLayout());
    
    buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    ButtonHelper bh = new ButtonHelper().setInsets(0).setContainer(buttons);
    bh.create(ok).setFocusable(false);    
    bh.create(cancel).setFocusable(false);
    
    setLayout(new BorderLayout());
    add(BorderLayout.SOUTH, buttons);
    
    
    mode.setSelected(REGISTRY.get("advanced", false));
    focus.setSelected(REGISTRY.get("focus", false));

    
  }
  
  
  private void setEditor(Editor set) {

    
    Context old = null;
    if (set!=null) {
      
      
      old = editor!=null ? editor.getContext() : null;
      
      
      commit();
  
    }
    
    
    if (editor!=null) {
      editor.removeChangeListener(ok);
      editor.removeChangeListener(cancel);
      editor.setContext(new Context());
      remove(editor);
      editor = null;
    }
      
    
    editor = set;
    if (editor!=null) {
      add(editor, BorderLayout.CENTER);
      if (old!=null)
        editor.setContext(old);
      editor.addChangeListener(ok);
      editor.addChangeListener(cancel);
    }
    
    
    revalidate();
    repaint();
  }
  
  
   boolean isGrabFocus() {
    return focus.isSelected();
  }
  
  
  public ViewContext getContext() {
    return editor!=null ? editor.getContext() : null;
  }
  
  @Override
  public void commit() {
    commit(true);
  }
  
  private void commit(boolean ask) {

    
    if (!ok.isEnabled())
      return;

    
    
    
    if (!getTopLevelAncestor().isVisible())
      return;

    
    if (ask&&!Options.getInstance().isAutoCommit) {
    
      JCheckBox auto = new JCheckBox(RESOURCES.getString("confirm.autocomit"));
      auto.setFocusable(false);
      
      int rc = DialogHelper.openDialog(RESOURCES.getString("confirm.keep.changes"), 
          DialogHelper.QUESTION_MESSAGE, new JComponent[] {
            new JLabel(RESOURCES.getString("confirm.keep.changes")),
            auto
          }, 
          Action2.yesNo(),
          this
      );
      
      if (rc!=0) {
        
        ok.setEnabled(false);
        cancel.setEnabled(false);
        buttons.setVisible(false);
        
        return;
      }
    
      Options.getInstance().isAutoCommit = auto.isSelected();
    
    }
    
    try {

      isChangeSource = true;
      isIgnoreSetContext = true;
      
      if (gedcom.isWriteLocked())
        editor.commit();
      else
        gedcom.doUnitOfWork(new UnitOfWork() {
          public void perform(Gedcom gedcom) throws GedcomException {
            editor.commit();
          }
        });
      
    } catch (Throwable t) {
      LOG.log(Level.WARNING, "error committing editor", t);
    } finally {
      isChangeSource = false;
      isIgnoreSetContext = false;
      ok.setEnabled(false);
      cancel.setEnabled(false);
      buttons.setVisible(false);
    }
  }
  
  public void setContext(Context context, boolean isActionPerformed) {
    
    
    if (isIgnoreSetContext)
      return;
    
    
    if (context.getGedcom()!=gedcom && gedcom!=null) {
      gedcom.removeGedcomListener(callback);
      gedcom=null;
    }
    
    
    if (context.getGedcom()==null) {
      sticky.setSelected(false);
      setEditor(null);
      populate(toolbar);
      ok.setEnabled(false);
      cancel.setEnabled(false);
      buttons.setVisible(false);
      return;
    }
    
    
    if (context.getGedcom()!=gedcom) {
      gedcom = context.getGedcom();
      gedcom.addGedcomListener(callback);
    }
    
    
    commit();
    
    
    if (context.getEntities().size()==1) {

      if (editor==null) {
        sticky.setSelected(false);
        if (mode.isSelected())
          setEditor(new AdvancedEditor(context.getGedcom(), this));
        else
          setEditor(new BasicEditor(context.getGedcom(), this));
      }
      
      if (!sticky.isSelected()||isActionPerformed)
        editor.setContext(context);
      
    } else {
      if (editor!=null)
        editor.setContext(new Context(context.getGedcom()));
    }
  
    
    ok.setEnabled(false);
    cancel.setEnabled(false);
    buttons.setVisible(false);
    
    
    populate(toolbar);
  }
  
  
  public void populate(ToolBar toolbar) {

    this.toolbar = toolbar;
    if (toolbar==null)
      return;
    
    toolbar.beginUpdate();
    
    
    if (editor!=null) {
      for (Action a : editor.getActions())
        toolbar.add(a);
    }
    toolbar.addSeparator();

    
    toolbar.add(new JToggleButton(sticky));
    toolbar.add(new JToggleButton(focus));
    toolbar.add(new JToggleButton(mode));
    
    
    toolbar.endUpdate();
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
      super.setTip(RESOURCES, "action.stick.tip");
      super.setSelected(false);
    }
    
    public void actionPerformed(ActionEvent event) {
      setSelected(isSelected());
    }
    @Override
    public boolean setSelected(boolean selected) {
      super.setImage(selected ? Images.imgStickOn : Images.imgStickOff);
      return super.setSelected(selected);
    }
  } 
  
  
  private class Focus extends Action2 {
    
    protected Focus() {
      super.setImage(Images.imgFocus);
      super.setTip(RESOURCES, "action.focus.tip");
      super.setSelected(false);
    }
    
    public void actionPerformed(ActionEvent event) {
      setSelected(isSelected());
      REGISTRY.put("focus", isSelected());
    }
  } 
  
  
  private class Mode extends Action2 {
    private Mode() {
      setImage(Images.imgView);
      setTip(RESOURCES, "action.mode");
      super.setSelected(false);
    }
    public void actionPerformed(ActionEvent event) {
      setSelected(isSelected());
    }
    @Override
    public boolean setSelected(boolean selected) {
      REGISTRY.put("advanced", selected);
      if (getContext()!=null)
        setEditor(selected ? new AdvancedEditor(getContext().getGedcom(), EditView.this) : new BasicEditor(getContext().getGedcom(), EditView.this));
      populate(toolbar);
      return super.setSelected(selected);
    }
  } 

  
  private class OK extends Action2 implements ChangeListener {

    
    private OK() {
      setText(Action2.TXT_OK);
      setEnabled(false);
    }

    
    public void actionPerformed(ActionEvent event) {
      commit(false);
    }
    
    public void stateChanged(ChangeEvent e) {
      setEnabled(true);
      buttons.setVisible(true);
      buttons.revalidate();
    }

  } 

  
  private class Cancel extends Action2 implements ChangeListener {

    
    private Cancel() {
      setText(Action2.TXT_CANCEL);
      setEnabled(false);
    }

    
    public void actionPerformed(ActionEvent event) {
      
      ok.setEnabled(false);
      cancel.setEnabled(false);
      buttons.setVisible(false);

      
      Context ctx = editor.getContext();
      editor.setContext(new Context());
      editor.setContext(ctx);
      populate(toolbar);
    }
    
    public void stateChanged(ChangeEvent e) {
      setEnabled(true);
      buttons.setVisible(true);
      buttons.revalidate();
    }

  } 
  
  private class Callback extends GedcomListenerAdapter {
    
    @Override
    public void gedcomWriteLockAcquired(Gedcom gedcom) {
      
      
      if (!isChangeSource)
        commit(false);
      
    }
    
    @Override
    public void gedcomWriteLockReleased(Gedcom gedcom) {
      
      
      if (editor!=null && !isChangeSource) {
        Context ctx = editor.getContext();
        editor.setContext(new Context());
        editor.setContext(ctx);
        populate(toolbar);
      }
    }
  }
  
} 
