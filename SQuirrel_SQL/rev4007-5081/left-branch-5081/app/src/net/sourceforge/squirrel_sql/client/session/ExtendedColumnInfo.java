

package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import java.io.Serializable;

public class ExtendedColumnInfo implements Serializable
{
   private static final long serialVersionUID = 1L;
   private String _columnName;
   private String _columnType;
   private int _columnSize;
   private int _decimalDigits;
   private boolean _nullable;
   private String _cat;
   private String _schem;
   private String _simpleTableName;
   private String _qualifiedName;

   public ExtendedColumnInfo(TableColumnInfo info, String simpleTableName)
   {
      _columnName = info.getColumnName();
      _columnType = info.getTypeName();
      _columnSize = info.getColumnSize();
      _decimalDigits = info.getDecimalDigits();
      if ("YES".equals(info.isNullable()))
      {
         _nullable = true;
      }
      else
      {
         _nullable = false;
      }
      _cat = info.getCatalogName();
      _schem = info.getSchemaName();
      _simpleTableName = simpleTableName;

      _qualifiedName = _cat + "." + _schem + "." + _simpleTableName + "." +_columnName;
   }

   public String getColumnName()
   {
      return _columnName;
   }

   public String getColumnType()
   {
      return _columnType;
   }

   public int getColumnSize()
   {
      return _columnSize;
   }

   public int getDecimalDigits()
   {
      return _decimalDigits;
   }

   public boolean isNullable()
   {
      return _nullable;
   }

   public String getCatalog()
   {
      return _cat;
   }

   public String getSchema()
   {
      return _schem;
   }

   public String getSimpleTableName()
   {
      return _simpleTableName;
   }

   
   @Override
   public int hashCode() {
       final int prime = 31;
       int result = 1;
       result = prime * result
       + ((_qualifiedName == null) ? 0 : _qualifiedName.hashCode());
       return result;
   }

   
   @Override
   public boolean equals(Object obj) {
       if (this == obj)
           return true;
       if (obj == null)
           return false;
       if (getClass() != obj.getClass())
           return false;
       final ExtendedColumnInfo other = (ExtendedColumnInfo) obj;
       if (_qualifiedName == null) {
           if (other._qualifiedName != null)
               return false;
       } else if (!_qualifiedName.equals(other._qualifiedName))
           return false;
       return true;
   }
}
