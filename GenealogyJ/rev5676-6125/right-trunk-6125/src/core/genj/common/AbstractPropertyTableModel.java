
package genj.common;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomListener;
import genj.gedcom.Property;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import spin.Spin;


public abstract class AbstractPropertyTableModel implements PropertyTableModel, GedcomListener {
  
  private List<PropertyTableModelListener> listeners = new CopyOnWriteArrayList<PropertyTableModelListener>();
  private Gedcom gedcom = null;
  private GedcomListener callback;
  
  protected AbstractPropertyTableModel(Gedcom gedcom) {
    this.gedcom = gedcom;
  }
  
  final public Gedcom getGedcom() {
    return gedcom;
  }
  
  
  public void addListener(PropertyTableModelListener listener) {
    listeners.add(listener);
    if (listeners.size()==1) {
      
      gedcom.addGedcomListener((GedcomListener)Spin.over((GedcomListener)this));
    }
  }
  
  
  public void removeListener(PropertyTableModelListener listener) {
    listeners.remove(listener);
    
    if (listeners.isEmpty())
      gedcom.removeGedcomListener((GedcomListener)Spin.over(this));
  }
  
  
  public String getName(int col) {
    return getPath(col).getName();    
  }
  
  
  protected void fireRowsChanged(int rowStart, int rowEnd, int col) {
    for (PropertyTableModelListener listener : listeners)
      listener.handleRowsChanged(this, rowStart, rowEnd, col);
  }
  
  
  protected void fireRowsAdded(int rowStart, int rowEnd) {
    for (PropertyTableModelListener listener : listeners)
      listener.handleRowsAdded(this, rowStart, rowEnd);
  }

  
  protected void fireRowsDeleted(int rowStart, int rowEnd) {
    for (PropertyTableModelListener listener : listeners)
      listener.handleRowsDeleted(this, rowStart, rowEnd);
  }

  
  public void gedcomEntityAdded(Gedcom gedcom, Entity entity) {
    
  }

  
  public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
    
  }

  
  public void gedcomPropertyAdded(Gedcom gedcom, Property property, int pos, Property added) {
    
  }

  
  public void gedcomPropertyChanged(Gedcom gedcom, Property property) {
    
  }

  
  public void gedcomPropertyDeleted(Gedcom gedcom, Property property, int pos, Property deleted) {
    
  }

}
