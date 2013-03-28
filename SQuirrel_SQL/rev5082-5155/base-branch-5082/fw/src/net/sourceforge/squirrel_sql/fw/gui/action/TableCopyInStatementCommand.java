package net.sourceforge.squirrel_sql.fw.gui.action;



import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;


public class TableCopyInStatementCommand extends TableCopySqlPartCommandBase implements ICommand
{
   
   private JTable _table;

   
   public TableCopyInStatementCommand(JTable table)
   {
      super();
      if (table == null)
      {
         throw new IllegalArgumentException("JTable == null");
      }
      _table = table;
   }

   
   public void execute()
   {
      int nbrSelRows = _table.getSelectedRowCount();
      int nbrSelCols = _table.getSelectedColumnCount();
      int[] selRows = _table.getSelectedRows();
      int[] selCols = _table.getSelectedColumns();
      if (selRows.length != 0 && selCols.length != 0)
      {
         StringBuffer buf = new StringBuffer();
         for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
         {
            TableColumn col = _table.getColumnModel().getColumn(selCols[colIdx]);

            ColumnDisplayDefinition colDef = null;
            if(col instanceof ExtTableColumn)
            {
               colDef = ((ExtTableColumn) col).getColumnDisplayDefinition();
            }

            int lastLength = buf.length();
            buf.append("(");
            for (int rowIdx = 0; rowIdx < nbrSelRows; ++rowIdx)
            {
               if(0 < rowIdx)
               {
                  buf.append(",");
                  if(100 < buf.length() - lastLength)
                  {
                     lastLength = buf.length(); 
                     buf.append("\n");
                  }
               }

               final Object cellObj = _table.getValueAt(selRows[rowIdx], selCols[colIdx]);
               buf.append(getData(colDef, cellObj, StatType.IN));
            }
            buf.append(")\n");
         }
         final StringSelection ss = new StringSelection(buf.toString());
         Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
      }
   }

}
