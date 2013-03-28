
package genj.common;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomListener;
import genj.gedcom.Property;
import genj.gedcom.PropertyDate;
import genj.gedcom.PropertyNumericValue;
import genj.gedcom.PropertySex;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SwingConstants;

import spin.Spin;


public abstract class AbstractPropertyTableModel implements PropertyTableModel, GedcomListener {
  
  protected final static int 
    LEFT = SwingConstants.LEFT,
    CENTER = SwingConstants.CENTER,
    RIGHT = SwingConstants.RIGHT;
  
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
  
  
  public String getColName(int col) {
    return getColPath(col).getName();    
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

  public String getCellValue(Property property, int row, int col) {
    return getDefaultCellValue(property, row, col);
  }
  
   static String getDefaultCellValue(Property property, int ro, int col) {
    if (property==null)
      return "";
    if (property instanceof Entity) 
      return ((Entity)property).getId();
    if (property instanceof PropertySex) 
      return Character.toString(((PropertySex)property).getDisplayValue().charAt(0));
    return property.getDisplayValue();
  }
  
  public int getCellAlignment(Property property, int row, int col) {
    return getDefaultCellAlignment(property, row, col);
  }
  
   static int getDefaultCellAlignment(Property property, int row, int col) {
    if (property instanceof Entity) 
      return RIGHT;
    if (property instanceof PropertyDate) 
      return RIGHT;
    if (property instanceof PropertyNumericValue) 
      return RIGHT;
    if (property instanceof PropertySex) 
      return CENTER;
    return LEFT;
  }

  public int compare(Property valueA, Property valueB, int col) {
    return defaultCompare(valueA, valueB, col);
  }

   static int defaultCompare(Property valueA, Property valueB, int col) {
    return valueA.compareTo(valueB);
  }
}
