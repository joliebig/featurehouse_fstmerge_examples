

package edu.rice.cs.util.swing;

import javax.swing.table.*;
import java.util.Vector;


public class UneditableTableModel extends DefaultTableModel {
  public UneditableTableModel() { super(); }
  public UneditableTableModel(int rowCount, int columnCount) { super(rowCount,columnCount); }
  public UneditableTableModel(Object[][] data, Object[] columnNames) { super(data, columnNames); }
  public UneditableTableModel(Vector columnNames, int rowCount) { super(columnNames, rowCount); }
  public UneditableTableModel(Vector data, Vector columnNames) { super(data,columnNames); }
  public boolean isCellEditable(int row, int col) { return false; }
}
