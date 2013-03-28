
package genj.common;

import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomListener;
import genj.gedcom.Property;
import genj.view.ContextProvider;
import genj.view.SelectionSink;
import genj.view.ViewContext;
import genj.view.ViewContext.ContextList;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import spin.Spin;


public class ContextListWidget extends JPanel implements ContextProvider {

  private Gedcom gedcom;
  private Callback callback = new Callback();
  private JList list;
  private JLabel title = new JLabel();
  private List<ViewContext> contexts = new ArrayList<ViewContext>();
  
  
  public ContextListWidget(ContextList list) {
    this(list.getGedcom(), list);
    
    title.setText(list.getTitle());
    title.setOpaque(true);
    title.setBackground(this.list.getBackground());
    add(title, BorderLayout.NORTH);
  }

  
  public ContextListWidget(Gedcom gedcom, List<ViewContext> contextList) {
    
    super(new BorderLayout());
    
    this.gedcom = gedcom;
    this.contexts.addAll(contextList);
    
    list = new JList(new Model(contextList));
    list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    list.setCellRenderer(callback);
    list.addListSelectionListener(callback);
    
    add(new JScrollPane(list), BorderLayout.CENTER);
  }

  public String getTitle() {
    return title.getText();
  }
  
  public List<ViewContext> getContexts() {
    return contexts;
  }
    
  
  
  public ViewContext getContext() {
    
    Object[] selection = list.getSelectedValues();
    
    
    if (selection.length==1&&selection[0] instanceof ViewContext)
      return (ViewContext)selection[0];
    
    
    List<Property> props = new ArrayList<Property>(16);
    List<Entity> ents = new ArrayList<Entity>(16);
    
    for (int i = 0; i < selection.length; i++) {
      ViewContext context = (ViewContext)selection[i];
      props.addAll(context.getProperties());
      ents.addAll(context.getEntities());
    }
    
    ViewContext result = new ViewContext(gedcom, ents, props);
    
    
    return result;
  }
  
  
  @Override
  public void addNotify() {
    
    super.addNotify();
    
    gedcom.addGedcomListener((GedcomListener)Spin.over(list.getModel()));
  }
  
  
  @Override
  public void removeNotify() {
    
    gedcom.removeGedcomListener((GedcomListener)Spin.over(list.getModel()));
    
    super.removeNotify();
  }
  
  
  private static class Model extends AbstractListModel implements GedcomListener {
    
    private List<ViewContext> list = new ArrayList<ViewContext>();
    
    private Model(List<ViewContext> set) {
      
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
      for (ListIterator<ViewContext> it=list.listIterator(); it.hasNext(); ) {
        ViewContext context = (ViewContext)it.next();
        if (context.getEntities().contains(entity))
          it.set(new ViewContext(context.getGedcom()));
      }
      
      fireContentsChanged(this, 0, list.size());
    }

    public void gedcomPropertyAdded(Gedcom gedcom, Property property, int pos, Property added) {
      
    }

    public void gedcomPropertyChanged(Gedcom gedcom, Property prop) {
      
      fireContentsChanged(this, 0, list.size());
    }

    public void gedcomPropertyDeleted(Gedcom gedcom, Property property, int pos, Property removed) {
      for (ListIterator<ViewContext> it=list.listIterator(); it.hasNext(); ) {
        ViewContext context = (ViewContext)it.next();
        if (context.getProperties().contains(property))
          it.set(new ViewContext(context.getText(), context.getImage(), new Context(context.getGedcom())));
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
    	  SelectionSink.Dispatcher.fireSelection(ContextListWidget.this,context, false);
    }
    
    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      
      super.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);
      
      ViewContext ctx = (ViewContext)value;
      setIcon(ctx.getImage());
      setText(ctx.getText());
      
      return this;
    }
  } 
}
