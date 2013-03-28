
package genj.common;

import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomListener;
import genj.gedcom.Property;
import genj.view.ContextProvider;
import genj.view.SelectionSink;
import genj.view.ViewContext;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import spin.Spin;


public class ContextListWidget extends JList implements ContextProvider {

  private Gedcom gedcom;
  private Callback callback = new Callback();
  private List<? extends Context> contexts = new ArrayList<Context>();
  
  
  public ContextListWidget(List<? extends Context> list) {
    
    setModel(new Model(list));
    
    setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    setCellRenderer(callback);
    addListSelectionListener(callback);
    
  }

  public List<? extends Context> getContexts() {
    return contexts;
  }
    
  
  
  public ViewContext getContext() {
    
    Object[] selection = getSelectedValues();
    
    
    if (selection.length==1&&selection[0] instanceof ViewContext)
      return (ViewContext)selection[0];
    
    
    List<Property> props = new ArrayList<Property>(16);
    List<Entity> ents = new ArrayList<Entity>(16);
    
    for (int i = 0; i < selection.length; i++) {
      Context context = (Context)selection[i];
      props.addAll(context.getProperties());
      ents.addAll(context.getEntities());
    }
    
    
    return new ViewContext(new Context(gedcom, ents, props));
  }
  
  
  @Override
  public void addNotify() {
    
    super.addNotify();
    
    gedcom.addGedcomListener((GedcomListener)Spin.over(getModel()));
  }
  
  
  @Override
  public void removeNotify() {
    
    gedcom.removeGedcomListener((GedcomListener)Spin.over(getModel()));
    
    super.removeNotify();
  }
  
  
  private class Model extends AbstractListModel implements GedcomListener {
    
    private List<Context> list = new ArrayList<Context>();
    
    private Model(List<? extends Context> set) {
      for (Context context : set) {
        list.add(context);
        if (gedcom==null)
          gedcom = context.getGedcom();
        else if (gedcom!=context.getGedcom())
          throw new IllegalArgumentException(gedcom+"!="+context.getGedcom());
      }
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
      for (ListIterator<Context> it = list.listIterator(); it.hasNext(); ) {
        Context context = it.next();
        if (context.getEntities().contains(entity))
          it.set(new Context(context.getGedcom()));
      }
      fireContentsChanged(this, 0, list.size());
    }

    public void gedcomPropertyAdded(Gedcom gedcom, Property property, int pos, Property added) {
      
    }

    public void gedcomPropertyChanged(Gedcom gedcom, Property prop) {
      
      fireContentsChanged(this, 0, list.size());
    }

    public void gedcomPropertyDeleted(Gedcom gedcom, Property property, int pos, Property removed) {
      for (ListIterator<Context> it=list.listIterator(); it.hasNext(); ) {
        Context context = it.next();
        if (context.getProperties().contains(property)) {
          if (context instanceof ViewContext)
            it.set(new ViewContext( ((ViewContext)context).getText(), ((ViewContext)context).getImage(), new Context(context.getGedcom())));
          else
            it.set(new Context(context.getGedcom()));
        }
      }
      
      fireContentsChanged(this, 0, list.size());
    }
    
  } 
  
  
  private class Callback extends DefaultListCellRenderer implements ListSelectionListener {
    
    
    public void valueChanged(ListSelectionEvent e) {
      if (e.getValueIsAdjusting())
        return;
      Context context = getContext();
      if (context!=null)
    	  SelectionSink.Dispatcher.fireSelection(ContextListWidget.this,context, false);
    }
    
    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      
      super.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);
      
      if (value instanceof ViewContext) {
        ViewContext ctx = (ViewContext)value;
        setIcon(ctx.getImage());
        setText(ctx.getText());
      } else {
        setIcon(Gedcom.getImage());
        setText(value.toString());
      }
      
      return this;
    }
  } 
}
