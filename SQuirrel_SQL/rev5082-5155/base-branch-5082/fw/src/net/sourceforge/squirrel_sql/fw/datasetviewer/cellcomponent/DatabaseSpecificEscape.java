package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;



public class DatabaseSpecificEscape
{
   private static final IEscape[] _escapes =
      new IEscape[]
      {
         new PostgreSQLEscape(),
         new MckoiSQLEscape()
      };

   private static interface IEscape
   {
      public boolean productMatches(ISQLDatabaseMetaData md);
      public String escapeSQL(String sql);
   }


   public static String escapeSQL(String sql, ISQLDatabaseMetaData md)
   {
      for (int i = 0; i < _escapes.length; i++)
      {
         if(_escapes[i].productMatches(md))
         {
            return _escapes[i].escapeSQL(sql);
         }
      }

      return sql;
   }

   private static class PostgreSQLEscape implements IEscape
   {
      public boolean productMatches(ISQLDatabaseMetaData md)
      {
         return DialectFactory.isPostgreSQL(md);
      }

      public String escapeSQL(String sql)
      {
         return sql.replaceAll("\\\\","\\\\\\\\");
      }
   }

   private static class MckoiSQLEscape implements IEscape
   {
      public boolean productMatches(ISQLDatabaseMetaData md)
      {
         return DialectFactory.isMcKoi(md);
      }

      public String escapeSQL(String sql)
      {
         return sql.replaceAll("\\\\","\\\\\\\\");
      }
   }



}
