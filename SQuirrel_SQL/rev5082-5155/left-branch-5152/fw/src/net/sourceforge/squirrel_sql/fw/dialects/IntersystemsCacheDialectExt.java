
package net.sourceforge.squirrel_sql.fw.dialects;

import org.hibernate.HibernateException;

import java.sql.Types;
import java.sql.SQLException;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;


public class IntersystemsCacheDialectExt extends CommonHibernateDialect
{
   private class CacheHelper extends org.hibernate.dialect.Cache71Dialect {
      public CacheHelper() {
         super();
         registerColumnType(Types.BIT, "BIT");
         registerColumnType(Types.TINYINT, "TINYINT");
         registerColumnType(Types.LONGVARBINARY, 32700, "LONGVARBINARY");
         registerColumnType(Types.VARBINARY, 254, "VARBINARY");
         registerColumnType(Types.LONGVARCHAR, 32700, "LONGVARCHAR");
         registerColumnType(Types.NUMERIC, "NUMERIC($p,$s)");
         registerColumnType(Types.INTEGER, "INTEGER");
         registerColumnType(Types.SMALLINT, "SMALLINT");
         registerColumnType(Types.DOUBLE, "DOUBLE");
         registerColumnType(Types.VARCHAR, 3924, "VARCHAR($l)");
         registerColumnType(Types.DATE, "date");
         registerColumnType(Types.TIME, "time");
         registerColumnType(Types.TIMESTAMP, "timestamp");
      }
   }

   
   private CacheHelper _dialect = new CacheHelper();


   
   public String getDropConstraintSQL(String tableName, String constraintName,
      DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
   {
      return DialectUtils.getDropConstraintSQL(tableName, constraintName, qualifier, prefs, this);
   }


   
   public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
   {
      return _dialect.getTypeName(code, length, precision, scale);
   }

   
   public List<String> getCreateTableSQL(List<ITableInfo> tables, ISQLDatabaseMetaData md,
      CreateScriptPreferences prefs, boolean isJdbcOdbc) throws SQLException
   {
      return DialectUtils.getCreateTableSQL(tables, md, this, prefs, isJdbcOdbc);
   }


   
   public String getDisplayName()
   {
      return "Cache";
   }

   
   public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
   {
      if (databaseProductName == null)
      {
         return false;
      }
      if (databaseProductName.trim().startsWith("Cache"))
      {
         
         return true;
      }
      return false;
   }

}