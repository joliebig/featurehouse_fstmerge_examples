
package genj.edit;

import genj.edit.beans.PropertyBean;
import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.gedcom.UnitOfWork;
import genj.util.Registry;
import genj.view.ContextProvider;
import genj.view.SelectionSink;
import genj.view.ViewContext;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.FocusManager;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;


class BasicEditor extends Editor implements SelectionSink, ContextProvider {

  final static Registry REGISTRY = Registry.get(BasicEditor.class);
  
  
  private Gedcom gedcom = null;

  
  private Entity currentEntity = null;

  
  private EditView view;
  
  
  private BeanPanel beanPanel;
  
  private boolean isIgnoreSetContext = false;
  
  
  public BasicEditor(Gedcom gedcom, EditView edit) {

    
    this.gedcom = gedcom;
    this.view = edit;
    
    
    beanPanel = new BeanPanel();
    beanPanel.addChangeListener(changes);

    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, new JScrollPane(beanPanel));
    
    
  }
  
  
  public void fireSelection(Context context, boolean isActionPerformed) {
    if (isActionPerformed || (context.getEntities().size()==1 && context.getEntity()==currentEntity))
      SelectionSink.Dispatcher.fireSelection(this, context, isActionPerformed);
  }

  
  public ViewContext getContext() {
    
    PropertyBean bean = getFocus();
    if (bean!=null&&bean.getContext()!=null) 
      return bean.getContext();
    
    if (currentEntity!=null)
      return new ViewContext(currentEntity);
    
    return new ViewContext(gedcom);
  }

  
  @Override
  public void setContext(Context context) {
    
    if (isIgnoreSetContext)
      return;
    
    actions.clear();
    
    
    if (context.getGedcom()==null) {
      setEntity(null, null);
      return;
    }
    
    
    if (changes.hasChanged() || currentEntity != context.getEntity()) {
      
      
      setEntity(context.getEntity(), context.getProperty());
      
    } else {

      
      if (beanPanel!=null && view.isGrabFocus()) {
        if (context.getProperties().size()==1)
          beanPanel.select(context.getProperty());
        else if (context.getProperties().isEmpty()&&context.getEntities().size()==1)
          beanPanel.select(context.getEntity());
      }      
    }
    
    
    for (PropertyBean bean : beanPanel.getBeans())
      actions.addAll(bean.getActions());
    
    
  }
  
  @Override
  public void commit() {
    
    
    try {
      isIgnoreSetContext = true;
      gedcom.doMuteUnitOfWork(new UnitOfWork() {
        public void perform(Gedcom gedcom) {
          beanPanel.commit();
        }
      });
    } finally {
      isIgnoreSetContext = false;
    }
    
    
    PropertyBean focussedBean = getFocus();
    Property focus = focussedBean !=null ? focussedBean.getProperty() : null;
    
    
    if (view.isGrabFocus())
      beanPanel.select(focus);

  }
  
  
  public void setEntity(Entity set, Property focus) {
    
    
    currentEntity = set;
    
    
    if (focus==null) {
      
      PropertyBean bean = getFocus();
      if (bean!=null&&bean.getProperty()!=null&&bean.getProperty().getEntity()==currentEntity) focus  = bean.getProperty();
      
      if (focus==null) focus = currentEntity;
    }
    
    
    beanPanel.setRoot(currentEntity);

    
    if (focus!=null && view.isGrabFocus())
      beanPanel.select(focus);

    
    changes.setChanged(false);
    
    
  }
  
  
  private PropertyBean getFocus() {
    
    Component focus = FocusManager.getCurrentManager().getFocusOwner();
    while (focus!=null&&!(focus instanceof PropertyBean))
      focus = focus.getParent();
    
    if (focus==null)
      return null;
    
    return SwingUtilities.isDescendingFrom(focus, this) ? (PropertyBean)focus : null;

  }
  
} 
