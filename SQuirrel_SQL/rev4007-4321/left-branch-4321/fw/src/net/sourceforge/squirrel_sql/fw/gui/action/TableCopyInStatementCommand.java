package net.sourceforge.squirrel_sql.fw.gui.action;



import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.sql.Types;
import java.util.Calendar;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;


public class TableCopyInStatementCommand implements ICommand
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
               buf.append(getData(colDef, cellObj));
            }
            buf.append(")\n");
         }
         final StringSelection ss = new StringSelection(buf.toString());
         Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
      }
   }

   private String getData(ColumnDisplayDefinition colDef, Object cellObj)
   {
      if (cellObj == null)
      {
         return "null";
      }
      else
      {
         if(null == colDef)
         {
            return "'" + cellObj.toString().replaceAll("'", "''") + "'";
         }
         else
         {
            if(colDef.getSqlType() == Types.SMALLINT ||
               colDef.getSqlType() == Types.INTEGER ||
               colDef.getSqlType() == Types.DECIMAL ||
               colDef.getSqlType() == Types.DOUBLE ||
               colDef.getSqlType() == Types.BIGINT ||
               colDef.getSqlType() == Types.NUMERIC ||
               colDef.getSqlType() == Types.TINYINT ||
               colDef.getSqlType() == Types.BIT ||
               colDef.getSqlType() == Types.REAL
               )
            {
               return cellObj.toString();
            }
            else if(colDef.getSqlType() == Types.TIME && cellObj instanceof java.util.Date)
            {
               java.util.Date date = (java.util.Date) cellObj;
               Calendar cal = Calendar.getInstance();
               cal.setTime(date);
               return "{t '" + prefixNulls(cal.get(Calendar.HOUR_OF_DAY), 2) + ":" +
                               prefixNulls(cal.get(Calendar.MINUTE), 2) + ":" +
                               prefixNulls(cal.get(Calendar.SECOND), 2) + "'}";
            }
            else if(colDef.getSqlType() == Types.DATE && cellObj instanceof java.util.Date)
            {
               java.util.Date date = (java.util.Date) cellObj;
               Calendar cal = Calendar.getInstance();
               cal.setTime(date);
               return "{d '" + prefixNulls(cal.get(Calendar.YEAR), 4) + "-" +
                               prefixNulls(cal.get(Calendar.MONTH) + 1, 2) + "-" +
                               prefixNulls(cal.get(Calendar.DAY_OF_MONTH) ,2) + "'}";
            }
            else if(colDef.getSqlType() == Types.TIMESTAMP && cellObj instanceof java.util.Date)
            {
               java.util.Date date = (java.util.Date) cellObj;
               Calendar cal = Calendar.getInstance();
               cal.setTime(date);
               return "{ts '" + prefixNulls(cal.get(Calendar.YEAR), 4) + "-" +
                               prefixNulls(cal.get(Calendar.MONTH) + 1, 2) + "-" +
                               prefixNulls(cal.get(Calendar.DAY_OF_MONTH) ,2) + " " +
                               prefixNulls(cal.get(Calendar.HOUR_OF_DAY), 2) + ":" +
                               prefixNulls(cal.get(Calendar.MINUTE), 2) + ":" +
                               prefixNulls(cal.get(Calendar.SECOND), 2) + "'}";
            }
            else
            {
               return "'" + cellObj.toString().replaceAll("'", "''") + "'";
            }
         }
      }
   }

   private String prefixNulls(int toPrefix, int digitCount)
   {
      String ret = "" + toPrefix;

      while(ret.length() < digitCount)
      {
         ret = 0 + ret;
      }

      return ret;
   }


}
