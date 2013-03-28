
package net.sourceforge.squirrel_sql.plugins.codecompletion;


import java.sql.Types;


public class CodeCompletionColumnInfo extends CodeCompletionInfo
{
   private String _columnName;
   private String _columnType;
   private int _columnSize;
   private boolean _nullable;

   private String _toString;
   private int _decimalDigits;


   public CodeCompletionColumnInfo(String columnName, String columnType, int columnSize, int decimalDigits, boolean nullable)
   {
      _columnName = columnName;
      _columnType = columnType;
      _columnSize = columnSize;
      _decimalDigits = decimalDigits;
      _nullable = nullable;

      String decimalDigitsString = 0 == _decimalDigits ? "" : "," + _decimalDigits;
      _toString = _columnName + "  " + _columnType + "(" + _columnSize + decimalDigitsString + ") " + (_nullable? "NULL": "NOT NULL");
   }

   public String getCompareString()
   {
      return _columnName;
   }

   public String toString()
   {
      return _toString;
   }
}
