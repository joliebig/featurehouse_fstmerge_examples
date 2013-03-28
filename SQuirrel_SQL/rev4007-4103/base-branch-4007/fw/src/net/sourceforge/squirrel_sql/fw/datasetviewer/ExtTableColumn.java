

package net.sourceforge.squirrel_sql.fw.datasetviewer;

import javax.swing.table.TableColumn;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;

public class ExtTableColumn extends TableColumn
{
   private ColumnDisplayDefinition _colDef;

   public ExtTableColumn(int modelIndex, int width, TableCellRenderer cellRenderer, TableCellEditor cellEditor)
   {
      super(modelIndex, width, cellRenderer, cellEditor);
   }

   public void setColumnDisplayDefinition(ColumnDisplayDefinition colDef)
   {
      _colDef = colDef;
   }

   public ColumnDisplayDefinition getColumnDisplayDefinition()
   {
      return _colDef;
   }
}
