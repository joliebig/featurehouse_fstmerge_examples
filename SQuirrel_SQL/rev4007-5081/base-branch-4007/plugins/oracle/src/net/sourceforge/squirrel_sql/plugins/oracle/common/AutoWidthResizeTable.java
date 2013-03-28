package net.sourceforge.squirrel_sql.plugins.oracle.common;

import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;


public class AutoWidthResizeTable extends JTable
{
  
  public AutoWidthResizeTable() {
    super();
  }
  
  public AutoWidthResizeTable(TableModel model) {
    super(model);
  }

  
  public void resizeColumnWidth() {
    resizeColumnWidth(-1);
  }

  public void resizeColumnWidth(int maxCellWidth) {
    TableModel tm = getModel();
    TableColumnModel colModel = getColumnModel();

    for (int col = tm.getColumnCount()-1; col >=0; col--) {
      TableColumn column = colModel.getColumn(col);
      TableCellRenderer renderer = column.getHeaderRenderer();
      if (renderer == null) {
        renderer = getTableHeader().getDefaultRenderer();
      }
      
      int cellWidth = renderer.getTableCellRendererComponent(this,
          column.getHeaderValue(), false, false, -1, col).getPreferredSize().
          width;

      renderer = getDefaultRenderer(tm.getColumnClass(col));

      for (int row = tm.getRowCount()-1; row>=0; row--) {
        int tmpWidth = renderer.getTableCellRendererComponent(this,
            tm.getValueAt(row, col), false, false, row, col).getPreferredSize().
            width;
        if (tmpWidth > cellWidth)
          cellWidth = tmpWidth;
      }
      
      
      cellWidth+=2;
      if ((maxCellWidth != -1) && (cellWidth >maxCellWidth))
        cellWidth = maxCellWidth;
      column.setPreferredWidth(cellWidth);
    }
  }

}
