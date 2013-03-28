
package genj.edit;

import genj.edit.beans.PropertyBean;
import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomListener;
import genj.gedcom.GedcomListenerAdapter;
import genj.gedcom.Property;
import genj.gedcom.UnitOfWork;
import genj.util.Registry;
import genj.util.swing.Action2;
import genj.util.swing.ButtonHelper;
import genj.view.ContextProvider;
import genj.view.ViewContext;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.FocusManager;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import spin.Spin;


class BasicEditor extends Editor implements ContextProvider {

  final static Registry REGISTRY = Registry.get(BasicEditor.class);
  
  
  private Gedcom gedcom = null;

  
  private Entity currentEntity = null;

  
  private EditView view;
  
  
  private OK ok = new OK();
  private Cancel cancel = new Cancel();
  
  
  private BeanPanel beanPanel;
  private JPanel buttonPanel;
  
  private GedcomListener callback = new Callback();

  
  public BasicEditor(Gedcom gedcom, EditView edit) {

    
    this.gedcom = gedcom;
    this.view = edit;
    
    
    beanPanel = new BeanPanel();
    beanPanel.addChangeListener(ok);
    beanPanel.addChangeListener(cancel);

    
    buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    ButtonHelper bh = new ButtonHelper().setInsets(0).setContainer(buttonPanel);
    bh.create(ok).setFocusable(false);    
    bh.create(cancel).setFocusable(false);
    
    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, new JScrollPane(beanPanel));
    add(BorderLayout.SOUTH, buttonPanel);
    
    
  }
  
  
  @Override
  public void addNotify() {
    
    super.addNotify();
    
    gedcom.addGedcomListener((GedcomListener)Spin.over(callback));
    
  }

  
  @Override
  public void removeNotify() {
    
    gedcom.removeGedcomListener((GedcomListener)Spin.over(callback));
    
    super.removeNotify();
  }

  
  public ViewContext getContext() {
    
    PropertyBean bean = getFocus();
    if (bean!=null&&bean.getContext()!=null) 
      return bean.getContext();
    
    if (currentEntity!=null)
      return new ViewContext(currentEntity);
    
    return new ViewContext(gedcom);
  }

  
  public void setContext(Context context) {
    
    
    if (context.getGedcom()==null) {
      setEntity(null, null);
      return;
    }
    
    
    if (currentEntity != context.getEntity()) {
      
      
      setEntity(context.getEntity(), context.getProperty());
      
    } else {

      
      if (beanPanel!=null && view.isGrabFocus())
        beanPanel.select(context.getProperty());
      
    }

    
  }
  
  @Override
  public void commit() {
    
    
    if (!ok.isEnabled())
      return;
    
    
    try {
      gedcom.removeGedcomListener((GedcomListener)Spin.over(callback));
      gedcom.doMuteUnitOfWork(new UnitOfWork() {
        public void perform(Gedcom gedcom) {try {
          beanPanel.commit();
        } finally {
          ok.setEnabled(false);
        }}
      });
    } finally {
      gedcom.addGedcomListener((GedcomListener)Spin.over(callback));
    }

    
    PropertyBean focussedBean = getFocus();
    Property focus = focussedBean !=null ? focussedBean.getProperty() : null;
    
    
    if (view.isGrabFocus())
      beanPanel.select(focus);

    
    ok.setEnabled(false);
    cancel.setEnabled(false);
  }
  
  
  public void setEntity(Entity set, Property focus) {
    
    
    if (ok.isEnabled()&&!gedcom.isWriteLocked()&&currentEntity!=null&&view.isCommitChanges()) 
      commit();

    
    currentEntity = set;
    
    
    if (focus==null) {
      
      PropertyBean bean = getFocus();
      if (bean!=null&&bean.getProperty()!=null&&bean.getProperty().getEntity()==currentEntity) focus  = bean.getProperty();
      
      if (focus==null) focus = currentEntity;
    }
    
    
    beanPanel.setRoot(currentEntity);

    
    ok.setEnabled(false);
    cancel.setEnabled(false);

    
    if (focus!=null && view.isGrabFocus())
      beanPanel.select(focus);

    
  }
  
  
  private PropertyBean getFocus() {
    
    Component focus = FocusManager.getCurrentManager().getFocusOwner();
    while (focus!=null&&!(focus instanceof PropertyBean))
      focus = focus.getParent();
    
    if (!(focus instanceof PropertyBean))
      return null;
    
    return SwingUtilities.isDescendingFrom(focus, this) ? (PropertyBean)focus : null;

  }

  
  private class OK extends Action2 implements ChangeListener {

    
    private OK() {
      setText(Action2.TXT_OK);
    }

    
    public void actionPerformed(ActionEvent event) {
      commit();
    }
    
    public void stateChanged(ChangeEvent e) {
      setEnabled(true);
    }

  } 

  
  private class Cancel extends Action2 implements ChangeListener {

    
    private Cancel() {
      setText(Action2.TXT_CANCEL);
    }

    
    public void actionPerformed(ActionEvent event) {
      
      ok.setEnabled(false);
      cancel.setEnabled(false);

      
      setEntity(currentEntity, null);
    }
    
    public void stateChanged(ChangeEvent e) {
      setEnabled(true);
    }

  } 
  
  
  private class Callback extends GedcomListenerAdapter {
    
    private Property setFocus;
    
    public void gedcomWriteLockAcquired(Gedcom gedcom) {
      setFocus = null;
    }
    
    public void gedcomWriteLockReleased(Gedcom gedcom) {
      setEntity(currentEntity, setFocus);
    }
    
    public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
      if (currentEntity==entity)
        currentEntity = null;
    }
    public void gedcomPropertyAdded(Gedcom gedcom, Property property, int pos, Property added) {
      if (setFocus==null && property.getEntity()==currentEntity) {
        setFocus = added;
      }
    }
    public void gedcomPropertyChanged(Gedcom gedcom, Property property) {
      if (setFocus==null && property.getEntity()==currentEntity)
        setFocus = property;
    }
  };
  
} 
