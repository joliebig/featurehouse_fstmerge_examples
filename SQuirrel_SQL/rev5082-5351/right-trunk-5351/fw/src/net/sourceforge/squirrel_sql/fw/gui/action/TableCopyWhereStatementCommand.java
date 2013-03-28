package net.sourceforge.squirrel_sql.fw.gui.action;



import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;


public class TableCopyWhereStatementCommand extends TableCopySqlPartCommandBase implements ICommand
{
   
   private JTable _table;

   
   public TableCopyWhereStatementCommand(JTable table)
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
         StringBuffer buf = new StringBuffer("WHERE ");
         for (int rowIdx = 0; rowIdx < nbrSelRows; ++rowIdx)
         {

            if(1 < nbrSelCols)
            {
               if(0 < rowIdx)
               {
                  buf.append("OR (");
               }
               else
               {
                  buf.append("(");
               }
            }
            else
            {
               if(0 < rowIdx)
               {
                  buf.append("OR ");
               }
            }

            boolean firstCol = true;
            for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
            {

               TableColumn col = _table.getColumnModel().getColumn(selCols[colIdx]);

               ColumnDisplayDefinition colDef = null;
               if(col instanceof ExtTableColumn)
               {
                  colDef = ((ExtTableColumn) col).getColumnDisplayDefinition();
               }
               else
               {
                  continue;
               }

               if(firstCol)
               {
                  firstCol = false;
               }
               else
               {
                  buf.append(" AND ");
               }

               final Object cellObj = _table.getValueAt(selRows[rowIdx], selCols[colIdx]);
               buf.append(colDef.getColumnName()).append(getData(colDef, cellObj, StatType.WHERE));
            }

            if(1 < nbrSelCols)
            {
               buf.append(")\n");
            }
            else
            {
               if(100 < buf.length() - buf.toString().lastIndexOf("\n"))
               {
                  buf.append("\n");
               }
               else
               {
                  buf.append(" ");
               }
            }
         }
         final StringSelection ss = new StringSelection(buf.toString());
         Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
      }
   }


}