
package genj.common;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomListener;
import genj.gedcom.Property;

import java.util.ArrayList;
import java.util.List;

import spin.Spin;


public abstract class AbstractPropertyTableModel implements PropertyTableModel, GedcomListener {
  
  private List listeners = new ArrayList(3);
  private Gedcom gedcom = null;
  private GedcomListener callback;
  
  
  public void addListener(PropertyTableModelListener listener) {
    listeners.add(listener);
    if (listeners.size()==1) {
      
      if (gedcom==null) gedcom=getGedcom();
      
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
    for (int i=0;i<listeners.size();i++)
      ((PropertyTableModelListener)listeners.get(i)).handleRowsChanged(this, rowStart, rowEnd, col);
  }
  
  
  protected void fireRowsAdded(int rowStart, int rowEnd) {
    for (int i=0;i<listeners.size();i++)
      ((PropertyTableModelListener)listeners.get(i)).handleRowsAdded(this, rowStart, rowEnd);
  }

  
  protected void fireRowsDeleted(int rowStart, int rowEnd) {
    for (int i=0;i<listeners.size();i++)
      ((PropertyTableModelListener)listeners.get(i)).handleRowsDeleted(this, rowStart, rowEnd);
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
