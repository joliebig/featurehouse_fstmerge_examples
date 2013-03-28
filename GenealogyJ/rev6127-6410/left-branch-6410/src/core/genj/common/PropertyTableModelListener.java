
package genj.common;


public interface PropertyTableModelListener {

  public void handleRowsChanged(PropertyTableModel model, int rowStart, int rowEnd, int col);
  
  public void handleRowsAdded(PropertyTableModel model, int rowStart, int rowEnd);

  public void handleRowsDeleted(PropertyTableModel model, int rowStart, int rowEnd);
}
