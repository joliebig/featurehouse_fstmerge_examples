package net.sourceforge.squirrel_sql.fw.datasetviewer;

import java.util.Iterator;
import java.util.Map;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class MapDataSet implements IDataSet
{

   
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(MapDataSet.class);


   private interface i18n
   {
      
      String UNSUPPORTED = s_stringMgr.getString("hashtabledataset.unsupported");
      
      String NAME_COLUMN = s_stringMgr.getString("hashtabledataset.key");
      
      String VALUE_COLUMN = s_stringMgr.getString("mapdataset.value");
   }

   
   private final static int s_columnCount = 2;

   private final static String[] s_hdgs = new String[]
   {
      i18n.NAME_COLUMN, i18n.VALUE_COLUMN
   };

   
   private final Map<?,?> _src;

   private DataSetDefinition _dsDef;

   private final static int[] s_hdgLens = new int[] { 30, 100 };

   
   private Iterator<?> _rowKeys;

   
   private Object[] _curRow = new Object[2];

   public MapDataSet(Map<?,?> src) throws DataSetException
   {
      super();
      if (src == null)
      {
         throw new IllegalArgumentException("Map == null");
      }

      _src = src;
      _dsDef = new DataSetDefinition(createColumnDefinitions());
      _rowKeys = _src.keySet().iterator();
   }

   public final int getColumnCount()
   {
      return s_columnCount;
   }

   public DataSetDefinition getDataSetDefinition()
   {
      return _dsDef;
   }

   public synchronized boolean next(IMessageHandler msgHandler)
   {
      _curRow[0] = null;
      _curRow[1] = null;
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

   private ColumnDisplayDefinition[] createColumnDefinitions()
   {
      final int columnCount = getColumnCount();
      ColumnDisplayDefinition[] columnDefs = new ColumnDisplayDefinition[columnCount];
      for (int i = 0; i < columnCount; ++i)
      {
         columnDefs[i] = new ColumnDisplayDefinition(s_hdgLens[i], s_hdgs[i]);
      }
      return columnDefs;
   }
}