package net.sourceforge.squirrel_sql.client.session.mainpanel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class SQLHistoryItemWrapper
{
   
   private static final StringManager s_stringMgr =
       StringManagerFactory.getStringManager(SQLHistoryItemWrapper.class);


   private static final String[] COLUMNS = new String[]
      {
         
         s_stringMgr.getString("SQLHistoryItemWrapper.index"),

         
         s_stringMgr.getString("SQLHistoryItemWrapper.lastUsed"),

         
         s_stringMgr.getString("SQLHistoryItemWrapper.sql"),
      };
   private static final SimpleDateFormat LAST_USAGE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

   public static ArrayList<SQLHistoryItemWrapper> wrap(ArrayList<SQLHistoryItem> items)
   {
      ArrayList<SQLHistoryItemWrapper> ret = new ArrayList<SQLHistoryItemWrapper>(items.size());

      int i=0;
      for (SQLHistoryItem item : items)
      {
         ret.add(new SQLHistoryItemWrapper(item, ++i));
      }

      Collections.reverse(ret);

      return ret;
   }


   public static String[] getColumns()
   {
      return COLUMNS;
   }

   public static int getSQLColIx()
   {
      return 2;
   }


   private SQLHistoryItem _item;
   private int _index;
   private String _upperCaseSQL;
   private String _lastUsageTimeString;
   

   public SQLHistoryItemWrapper(SQLHistoryItem item, int index)
   {
      _item = item;
      _index = index;
      _upperCaseSQL = item.getSQL().toUpperCase();

      if(null != _item.getLastUsageTime())
      {
         _lastUsageTimeString = LAST_USAGE_DATE_FORMAT.format(_item.getLastUsageTime());
      }
   }

   public Object getColum(int column)
   {
      
      switch(column)
      {
         case 0: return _index;
         case 1: return _lastUsageTimeString;
         case 2: return _item.getSQL();
         default: throw new IllegalArgumentException("Unknown colum index " + column);

      }
   }


   public String getUpperCaseSQL()
   {
      return _upperCaseSQL;
   }
}
