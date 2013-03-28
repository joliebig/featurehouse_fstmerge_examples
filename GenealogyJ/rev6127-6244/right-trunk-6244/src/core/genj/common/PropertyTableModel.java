
package genj.common;

import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.gedcom.TagPath;


public interface PropertyTableModel {
  
  
  public Gedcom getGedcom();
  
  
  public int getNumRows();
  
  
  public int getNumCols();

  
  public Property getRowRoot(int row);
  
  
  public TagPath getColPath(int col);
  
  
  public String getColName(int col);
  
  
  public void addListener(PropertyTableModelListener listener);
  
  
  public void removeListener(PropertyTableModelListener listener);
  
}
