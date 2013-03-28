package net.sourceforge.squirrel_sql.fw.gui.action;



import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;


public class TableCopyInsertStatementCommand extends TableCopySqlPartCommandBase implements ICommand
{
   
   private JTable _table;
   private String _statementSeparator;

   
   public TableCopyInsertStatementCommand(JTable table, String statementSeparator)
   {
      super();
      _statementSeparator = statementSeparator;
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

         StringBuffer colNames = new StringBuffer();
         StringBuffer vals = new StringBuffer();

         for (int rowIdx = 0; rowIdx < nbrSelRows; ++rowIdx)
         {

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

               if (firstCol)
               {
                  firstCol = false;
                  colNames.append("INTO (");
                  vals.append("(");
               }
               else
               {
                  colNames.append(",");
                  vals.append(",");
               }

               Object cellObj = _table.getValueAt(selRows[rowIdx], selCols[colIdx]);

               colNames.append(colDef.getColumnName());
               vals.append(getData(colDef, cellObj, StatType.IN));
            }

            colNames.append(")");
            vals.append(")");

            buf.append(colNames).append(" VALUES ").append(vals);

            if(1 < _statementSeparator.length())
            {
               buf.append(" ").append(_statementSeparator).append("\n");
            }
            else
            {
               buf.append(_statementSeparator).append("\n");
            }

            colNames.setLength(0);
            vals.setLength(0);

         }
         final StringSelection ss = new StringSelection(buf.toString());
         Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
      }
   }


}