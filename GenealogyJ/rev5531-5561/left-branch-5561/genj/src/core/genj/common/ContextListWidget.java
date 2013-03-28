
package genj.common;

import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomListener;
import genj.gedcom.Property;
import genj.view.ContextProvider;
import genj.view.View;
import genj.view.ViewContext;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import spin.Spin;


public class ContextListWidget extends JList implements ContextProvider {

  private Gedcom ged;
  
  private Callback callback = new Callback();
  
  
  public ContextListWidget(Gedcom gedcom) {
    super(new Model());
    ged = gedcom;
    setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    setCellRenderer(callback);
    addListSelectionListener(callback);
  }

  
  public ContextListWidget(Gedcom gedcom, List contextList) {
    this(gedcom);
    setContextList(contextList);
  }
  
  
  public ViewContext getContext() {
    
    Object[] selection = getSelectedValues();
    
    
    if (selection.length==1&&selection[0] instanceof ViewContext)
      return (ViewContext)selection[0];
    
    
    ViewContext result = new ViewContext(ged);
    for (int i = 0; i < selection.length; i++) {
      Context context = (Context)selection[i];
      result.addContext(context);
    }
    
    
    return result;
  }
  
  
  public void addNotify() {
    
    super.addNotify();
    
    ged.addGedcomListener((GedcomListener)Spin.over(getModel()));
  }
  
  
  public void removeNotify() {
    
    ged.removeGedcomListener((GedcomListener)Spin.over(getModel()));
    
    super.removeNotify();
  }
  
  
  public void setContextList(List contextList) {
    ((Model)getModel()).setContextList(contextList);
  }
  
  
  public void setModel(ListModel model) {
    if (!(model instanceof Model))
      throw new IllegalArgumentException("setModel() n/a");
    super.setModel(model);
  }

  
  public void setListData(Object[] listData) {
    throw new IllegalArgumentException("setListData() n/a");
  }
  
  
  public void setListData(Vector listData) {
    throw new IllegalArgumentException("setListData() n/a");
  }
  
  
  private static class Model extends AbstractListModel implements GedcomListener {
    
    private List list = new ArrayList();
    
    private void setContextList(List set) {
      
      int n = list.size();
      list.clear();
      if (n>0)
        fireIntervalRemoved(this, 0, n-1);
      
      list.addAll(set);
      n = list.size();
      if (n>0)
        fireIntervalAdded(this, 0, n-1);
      
    }

    public int getSize() {
      return list.size();
    }

    public Object getElementAt(int index) {
      return list.get(index);
    }
    
    public void gedcomEntityAdded(Gedcom gedcom, Entity entity) {
      
    }

    public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
      for (Iterator it=list.iterator(); it.hasNext(); ) {
        Context context = (Context)it.next();
        context.removeEntities(Collections.singletonList(entity));
      }
      
      fireContentsChanged(this, 0, list.size());
    }

    public void gedcomPropertyAdded(Gedcom gedcom, Property property, int pos, Property added) {
      
    }

    public void gedcomPropertyChanged(Gedcom gedcom, Property prop) {
      
      fireContentsChanged(this, 0, list.size());
    }

    public void gedcomPropertyDeleted(Gedcom gedcom, Property property, int pos, Property removed) {
      for (Iterator it=list.iterator(); it.hasNext(); ) {
        Context context = (Context)it.next();
        context.removeProperties(Collections.singletonList(removed));
      }
      
      fireContentsChanged(this, 0, list.size());
    }
    
  } 
  
  
  private class Callback extends DefaultListCellRenderer implements ListSelectionListener {
    
    
    public void valueChanged(ListSelectionEvent e) {
      if (e.getValueIsAdjusting())
        return;
      ViewContext context = getContext();
      if (context!=null)
        View.getView(ContextListWidget.this).fireSelection(context, false);
    }
    
    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      
      super.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);
      
      Context ctx = (Context)value;
      setIcon(ctx.getImage());
      setText(ctx.getText());
      
      return this;
    }
  } 
}
