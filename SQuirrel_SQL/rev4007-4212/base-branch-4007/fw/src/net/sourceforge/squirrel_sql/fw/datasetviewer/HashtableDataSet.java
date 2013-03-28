package net.sourceforge.squirrel_sql.fw.datasetviewer;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import net.sourceforge.squirrel_sql.fw.util.EnumerationIterator;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class HashtableDataSet implements IDataSet
{

   private static abstract class HashtableDataSetI18n
   {
      
      private static final StringManager s_stringMgr =
         StringManagerFactory.getStringManager(HashtableDataSet.class);

       
      public static final String UNSUPPORTED = 
          s_stringMgr.getString("hashtabledataset.unsupported");
      
      public static final String NAME_COLUMN = 
          s_stringMgr.getString("hashtabledataset.key");
      
      public static final String VALUE_COLUMN = 
          s_stringMgr.getString("hashtabledataset.value");
   }

   private Hashtable<String,String> _src;
   private DataSetDefinition _dsDef;
   private final static String[] s_hdgs =
      new String[] {
         HashtableDataSetI18n.NAME_COLUMN,
         HashtableDataSetI18n.VALUE_COLUMN };
   private final static int[] s_hdgLens = new int[] { 30, 100 };
   private String[] _curRow = new String[2];
   private Iterator<String> _rowKeys;

   public HashtableDataSet(final Hashtable<String,String> src) 
       throws DataSetException
   {
      _src = new Hashtable<String, String>();
      for ( Map.Entry<String, String> entry : src.entrySet()) {
          _src.put(entry.getKey(), entry.getValue());
      }
      init();
   }

   public HashtableDataSet(final Properties props) throws DataSetException {
       _src = new Hashtable<String, String>();
       for (Object obj : props.keySet()) {
           String key = (String)obj;
           String value = props.getProperty(key);
           _src.put(key, value);
       }
       init();
   }
      
   public final int getColumnCount()
   {
      return s_hdgs.length;
   }

   public DataSetDefinition getDataSetDefinition()
   {
      return _dsDef;
   }

   public synchronized boolean next(IMessageHandler msgHandler)
   {
      _curRow[0] = null;
      if (_rowKeys.hasNext())
      {
         _curRow[0] = _rowKeys.next();
      }
      if (_curRow[0] != null)
      {
         _curRow[1] = _src.get(_curRow[0]);
      }
      return _curRow[0] != null;
   }

   public Object get(int columnIndex)
   {
      return _curRow[columnIndex];
   }

   private void init() {
       _dsDef = new DataSetDefinition(createColumnDefinitions());
       Enumeration<String> keyEnumeration = _src.keys();
       _rowKeys = new EnumerationIterator<String>(keyEnumeration);       
   }   
   
   private ColumnDisplayDefinition[] createColumnDefinitions()
   {
      final int columnCount = getColumnCount();
      ColumnDisplayDefinition[] columnDefs =
         new ColumnDisplayDefinition[columnCount];
      for (int i = 0; i < columnCount; ++i)
      {
         columnDefs[i] =
            new ColumnDisplayDefinition(s_hdgLens[i], s_hdgs[i]);
      }
      return columnDefs;
   }
}
