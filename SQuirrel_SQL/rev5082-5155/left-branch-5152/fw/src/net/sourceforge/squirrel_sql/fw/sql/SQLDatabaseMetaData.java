package net.sourceforge.squirrel_sql.fw.sql;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DatabaseTypesDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.sql.dbobj.BestRowIdentifier;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SQLDatabaseMetaData implements ISQLDatabaseMetaData
{

	
	private final static ILogger s_log =
		LoggerController.createLogger(SQLDatabaseMetaData.class);

	
	private interface IDriverNames
	{
        String AS400 = "AS/400 Toolbox for Java JDBC Driver";
        
        String FREE_TDS = "InternetCDS Type 4 JDBC driver for MS SQLServer";        
        String OPTA2000 = "i-net OPTA 2000";
	}

   public static class DriverMatch
   {
      private static final String COM_HTTX_DRIVER_PREFIX = "com.hxtt.sql.";

      public static boolean isComHttxDriver(ISQLConnection con)
      {
         if(null == con)
         {
            return false;
         }
         return con.getSQLDriver().getDriverClassName().startsWith(COM_HTTX_DRIVER_PREFIX);
      }
   }

	
	private ISQLConnection _conn;

	
	private Map<String, Object> _cache = 
        Collections.synchronizedMap(new HashMap<String, Object>());

    
    private boolean supportsSuperTables = true;
    
	
	SQLDatabaseMetaData(ISQLConnection conn)
	{
		super();
		if (conn == null)
		{
			throw new IllegalArgumentException("SQLDatabaseMetaData == null");
		}
		_conn = conn;
	}

    
    public synchronized String getUserName() throws SQLException
	{
		final String key = "getUserName";
		String value = (String)_cache.get(key);
		if (value == null)
		{
			value = privateGetJDBCMetaData().getUserName();
			_cache.put(key, value);
		}
		return value;
	}

    
    public synchronized String getDatabaseProductName()
		throws SQLException
	{
		final String key = "getDatabaseProductName";
		String value = (String)_cache.get(key);
		if (value == null)
		{
			value = privateGetJDBCMetaData().getDatabaseProductName();
            _cache.put(key, value);
		}
		return value;
	}

    
	public synchronized String getDatabaseProductVersion()
		throws SQLException
	{
		final String key = "getDatabaseProductVersion";
		String value = (String)_cache.get(key);
		if (value == null)
		{
			value = privateGetJDBCMetaData().getDatabaseProductVersion();
			_cache.put(key, value);
		}
		return value;
	}

    public synchronized int getDatabaseMajorVersion() 
        throws SQLException
    {
        final String key = "getDatabaseMajorVersion";
        Integer value = (Integer)_cache.get(key);
        if (value == null)
        {
            value = privateGetJDBCMetaData().getDatabaseMajorVersion();
            _cache.put(key, value);
        }
        return value;
    }
    
    
	public synchronized String getDriverName() throws SQLException
	{
		final String key = "getDriverName";
		String value = (String)_cache.get(key);
		if (value == null)
		{
			value = privateGetJDBCMetaData().getDriverName();
			_cache.put(key, value);
		}
		return value;
	}

    
    public int getJDBCVersion() throws SQLException
	{
		final String key = "getJDBCVersion";
		Integer value = (Integer)_cache.get(key);
		if (value == null)
		{
			DatabaseMetaData md = privateGetJDBCMetaData();
            int major = md.getJDBCMajorVersion();
            int minor = md.getJDBCMinorVersion();
            int vers = (major * 100) + minor;
            value = Integer.valueOf(vers);
            _cache.put(key, value);
		}
		return value.intValue();
	}

    
	public synchronized String getIdentifierQuoteString() throws SQLException
	{
		final String key = "getIdentifierQuoteString";
		String value = (String)_cache.get(key);
		if (value == null)
		{
			value = privateGetJDBCMetaData().getIdentifierQuoteString();
			if (value == null) {
			    value = "";
            }
			_cache.put(key, value);
		}
		return value;
	}

    
    public synchronized String getCascadeClause() throws SQLException {
        final String key = "getCascadeClause";
        String value = (String)_cache.get(key);
        if (value == null)
        {
            if (DialectFactory.isDB2(this) || DialectFactory.isOracle(this))
            {
                value = "CASCADE";
            }
            else
            {
                value = "";
            }
            _cache.put(key, value);
        }
        return value;
    }
    

   
   public synchronized String[] getSchemas() throws SQLException {

      boolean hasGuest = false;
      boolean hasSysFun = false;

      final boolean isMSSQLorSYBASE = DialectFactory.isSyBase(this)
            || DialectFactory.isMSSQLServer(this);

      final boolean isDB2 = DialectFactory.isDB2(this);

      final ArrayList<String> list = new ArrayList<String>();
      ResultSet rs = privateGetJDBCMetaData().getSchemas();
      try {
         if (rs != null) {
            DialectType dialectType = DialectFactory.getDialectType(this);
            final ResultSetReader rdr = new ResultSetReader(rs, dialectType);
            Object[] row = null;
            while ((row = rdr.readRow()) != null) {
               if (isMSSQLorSYBASE && row[0].equals("guest")) {
                  hasGuest = true;
               }
               if (isDB2 && row[0].equals("SYSFUN")) {
                  hasSysFun = true;
               }
               list.add((String) row[0]);
            }
         }
      } finally {
      	SQLUtilities.closeResultSet(rs);
      }

      
      
      if (isMSSQLorSYBASE && !hasGuest) {
         list.add("guest");
      }

      
      
      if (isDB2 && !hasSysFun) {
         list.add("SYSFUN");
      }

      return list.toArray(new String[list.size()]);
   }

    
    public boolean supportsSchemas() throws SQLException
	{
		return supportsSchemasInDataManipulation()
				|| supportsSchemasInTableDefinitions();
	}

    
    public synchronized boolean supportsSchemasInDataManipulation()
		throws SQLException
	{
		final String key = "supportsSchemasInDataManipulation";
		Boolean value = (Boolean)_cache.get(key);
		if (value != null)
		{
			return value.booleanValue();
		}

		try
		{
			value = Boolean.valueOf(privateGetJDBCMetaData().supportsSchemasInDataManipulation());
		}
		catch (SQLException ex)
		{
            boolean isSQLServer = 
                DialectFactory.isSyBase(this) || DialectFactory.isMSSQLServer(this);
            
			if (isSQLServer)
			{
				value = Boolean.TRUE;
                _cache.put(key, value);
			}
			throw ex;
		}

		_cache.put(key, value);

		return value.booleanValue();
	}

    
	public synchronized boolean supportsSchemasInTableDefinitions()
		throws SQLException
	{
		final String key = "supportsSchemasInTableDefinitions";
		Boolean value = (Boolean)_cache.get(key);
		if (value != null)
		{
			return value.booleanValue();
		}

		try
		{
			value = Boolean.valueOf(privateGetJDBCMetaData().supportsSchemasInTableDefinitions());
		}
		catch (SQLException ex)
		{
            boolean isSQLServer = 
                DialectFactory.isSyBase(this) || DialectFactory.isMSSQLServer(this);            
			if (isSQLServer)
			{
				value = Boolean.TRUE;
                _cache.put(key, value);
			}
			throw ex;
		}

		_cache.put(key, value);

		return value.booleanValue();
	}

    
    public synchronized boolean supportsStoredProcedures() throws SQLException
	{
		final String key = "supportsStoredProcedures";
		Boolean value = (Boolean)_cache.get(key);
		if (value != null)
		{
			return value.booleanValue();
		}

		
		
		if (DialectFactory.isPostgreSQL(this))
		{
			value = Boolean.TRUE;
		}
		else
		{
			value = Boolean.valueOf(privateGetJDBCMetaData().supportsStoredProcedures());
		}
		_cache.put(key, value);

		return value.booleanValue();
	}

    
    public synchronized boolean supportsSavepoints() throws SQLException {
        
        final String key = "supportsSavepoints";
        Boolean value = (Boolean)_cache.get(key);
        if (value != null)
        {
            return value.booleanValue();
        }
        value = Boolean.valueOf(privateGetJDBCMetaData().supportsSavepoints());

        _cache.put(key, value);

        return value.booleanValue();        
    }
    
    
    public synchronized boolean supportsResultSetType(int type) 
        throws SQLException
    {
        final String key = "supportsResultSetType";
        Boolean value = (Boolean)_cache.get(key);
        if (value != null)
        {
            return value.booleanValue();
        }
        value = Boolean.valueOf(privateGetJDBCMetaData().supportsResultSetType(type));

        _cache.put(key, value);

        return value.booleanValue();                
    }
        
    
    
    public synchronized String[] getCatalogs() throws SQLException
	{
		final ArrayList<String> list = new ArrayList<String>();
		ResultSet rs = privateGetJDBCMetaData().getCatalogs();
		try
		{
            if (rs != null) {
               DialectType dialectType = DialectFactory.getDialectType(this);
    			final ResultSetReader rdr = new ResultSetReader(rs, dialectType);
    			Object[] row = null;
    			while ((row = rdr.readRow()) != null)
    			{
                    if (row != null && row[0] != null) { 
                        list.add(row[0].toString());
                    }
    			}
            }
		}
		finally
		{
			SQLUtilities.closeResultSet(rs);
		}

		return list.toArray(new String[list.size()]);
	}

    
    public synchronized String getURL() throws SQLException {
        final String key = "getURL";
        String value = (String)_cache.get(key);
        if (value != null) {
            return value;
        }
        
        value = privateGetJDBCMetaData().getURL();
        _cache.put(key, value);
        
        return value;
    }    
    
    
    public synchronized String getCatalogTerm() throws SQLException {
        final String key = "getCatalogTerm";
        String value = (String)_cache.get(key);
        if (value != null) {
            return value;
        }
        
        value = privateGetJDBCMetaData().getCatalogTerm();
        _cache.put(key, value);
        
        return value;
    }
    
    
    public synchronized String getSchemaTerm() throws SQLException {
        final String key = "getSchemaTerm";
        String value = (String)_cache.get(key);
        if (value != null) {
            return value;
        }
        
        value = privateGetJDBCMetaData().getSchemaTerm();
        _cache.put(key, value);
        
        return value;        
    }

    
    public synchronized String getProcedureTerm() throws SQLException {
        final String key = "getProcedureTerm";
        String value = (String)_cache.get(key);
        if (value != null) {
            return value;
        }
        
        value = privateGetJDBCMetaData().getProcedureTerm();
        _cache.put(key, value);
        
        return value;        
    }
    
    
    
    public synchronized String getCatalogSeparator() throws SQLException
	{
		final String key = "getCatalogSeparator";
		String value = (String)_cache.get(key);
		if (value != null)
		{
			return value;
		}

		value = privateGetJDBCMetaData().getCatalogSeparator();
		_cache.put(key, value);

		return value;
	}

    
    public boolean supportsCatalogs() throws SQLException
	{
		return supportsCatalogsInTableDefinitions()
			|| supportsCatalogsInDataManipulation()
			|| supportsCatalogsInProcedureCalls();
	}

    
    public synchronized boolean supportsCatalogsInTableDefinitions() throws SQLException
	{
		final String key = "supportsCatalogsInTableDefinitions";
		Boolean value = (Boolean)_cache.get(key);
		if (value != null)
		{
			return value.booleanValue();
		}

		try
		{
			value = Boolean.valueOf(privateGetJDBCMetaData().supportsCatalogsInTableDefinitions());
		}
		catch (SQLException ex)
		{
            boolean isSQLServer = 
                DialectFactory.isSyBase(this) || DialectFactory.isMSSQLServer(this);
            
			if (isSQLServer)
			{
				value = Boolean.TRUE;
                _cache.put(key, value);
			}
			throw ex;
		}

		_cache.put(key, value);

		return value.booleanValue();
	}

    
    public synchronized boolean supportsCatalogsInDataManipulation() throws SQLException
	{
		final String key = "supportsCatalogsInDataManipulation";
		Boolean value = (Boolean)_cache.get(key);
		if (value != null)
		{
			return value.booleanValue();
		}

		try
		{
			value = Boolean.valueOf(privateGetJDBCMetaData().supportsCatalogsInDataManipulation());
		}
		catch (SQLException ex)
		{
		    boolean isSQLServer = 
                DialectFactory.isSyBase(this) || DialectFactory.isMSSQLServer(this);

			if (isSQLServer)
			{
				value = Boolean.TRUE;
                _cache.put(key, value);
			}
			throw ex;
		}
		_cache.put(key, value);

		return value.booleanValue();
	}

    
	public synchronized boolean supportsCatalogsInProcedureCalls() throws SQLException
	{
		final String key = "supportsCatalogsInProcedureCalls";
		Boolean value = (Boolean)_cache.get(key);
		if (value != null)
		{
			return value.booleanValue();
		}

		try
		{
			value = Boolean.valueOf(privateGetJDBCMetaData().supportsCatalogsInProcedureCalls());
		}
		catch (SQLException ex)
		{
            boolean isSQLServer = 
                DialectFactory.isSyBase(this) || DialectFactory.isMSSQLServer(this);
            
			if (isSQLServer)
			{
				value = Boolean.TRUE;
                _cache.put(key, value);
			}
			throw ex;
		}
		_cache.put(key, value);

		return value.booleanValue();
	}

    
	public synchronized DatabaseMetaData getJDBCMetaData() throws SQLException
	{
		return privateGetJDBCMetaData();
	}

	
    public synchronized IDataSet getMetaDataSet() throws SQLException {
        return new MetaDataDataSet(privateGetJDBCMetaData());
    }
    
    
    public ResultSet getTypeInfo() throws SQLException
	{
		return privateGetJDBCMetaData().getTypeInfo();
	}

    
    public synchronized IDataSet getTypesDataSet() throws DataSetException {
        ResultSet rs = null;
        try {
            rs = privateGetJDBCMetaData().getTypeInfo();
            return (new DatabaseTypesDataSet(rs));
        } catch (SQLException e) {
            throw new DataSetException(e);
        } finally {
            SQLUtilities.closeResultSet(rs);
        }
    }
    
    
    public synchronized DataTypeInfo[] getDataTypes()
		throws SQLException
	{
		final DatabaseMetaData md = privateGetJDBCMetaData();
		final ArrayList<DataTypeInfo> list = new ArrayList<DataTypeInfo>();
		final ResultSet rs = md.getTypeInfo();
		try
		{
			ResultSetColumnReader rdr = new ResultSetColumnReader(rs);
			while (rdr.next())
			{
				final String typeName = rdr.getString(1);
				final int dataType = rdr.getLong(2).intValue();
				final int precis = rdr.getLong(3).intValue();
				final String literalPrefix = rdr.getString(4);
				final String literalSuffix = rdr.getString(5);
				final String createParams = rdr.getString(6);
				final int nullable = rdr.getLong(7).intValue();
				final boolean caseSens = rdr.getBoolean(8).booleanValue();
				final int searchable = rdr.getLong(9).intValue();
				final boolean unsigned = rdr.getBoolean(10).booleanValue();
				final boolean canBeMoney = rdr.getBoolean(11).booleanValue();
				final boolean canBeAutoInc = rdr.getBoolean(12).booleanValue();
				final String localTypeName = rdr.getString(13);
				final int min = rdr.getLong(14).intValue();
				final int max = rdr.getLong(15).intValue();
				final int radix = rdr.getLong(18).intValue();
				list.add(new DataTypeInfo(typeName, dataType, precis,
										literalPrefix, literalSuffix,
										createParams, nullable, caseSens,
										searchable, unsigned, canBeMoney,
										canBeAutoInc, localTypeName,
										min, max, radix,
										this));
			}
		}
		finally
		{
         SQLUtilities.closeResultSet(rs);
      }
		return list.toArray(new DataTypeInfo[list.size()]);
	}

   
   public synchronized IProcedureInfo[] getProcedures(String catalog,
                                                      String schemaPattern,
                                                      String procedureNamePattern,
                                                      ProgressCallBack progressCallBack)
     throws SQLException
  {
     DatabaseMetaData md = privateGetJDBCMetaData();
     ArrayList<ProcedureInfo> list = new ArrayList<ProcedureInfo>();
     ResultSet rs = md.getProcedures(catalog, schemaPattern, procedureNamePattern);
     if (rs != null) {
	     int count = 0;
	     try
	     {
	        final int[] cols = new int[]{1, 2, 3, 7, 8};
	        DialectType dialectType = DialectFactory.getDialectType(this);
	        final ResultSetReader rdr = new ResultSetReader(rs, cols, dialectType);
	        Object[] row = null;
	        while ((row = rdr.readRow()) != null)
	        {
	           final int type = ((Number) row[4]).intValue();
	           ProcedureInfo pi = new ProcedureInfo(getAsString(row[0]), getAsString(row[1]),
	              getAsString(row[2]), getAsString(row[3]), type, this);
	
	           list.add(pi);
	
	           if (null != progressCallBack)
	           {
	              if(0 == count++ % 200 )
	              {
	                 progressCallBack.currentlyLoading(pi.getSimpleName());
	              }
	           }
	        }
	     }
	     finally
	     {
	   	  SQLUtilities.closeResultSet(rs);
	     }
     }
     return list.toArray(new IProcedureInfo[list.size()]);
  }

    
    public synchronized String[] getTableTypes() throws SQLException
	{
		final String key = "getTableTypes";
		String[] value = (String[])_cache.get(key);
		if (value != null)
		{
			return value;
		}

		final DatabaseMetaData md = privateGetJDBCMetaData();
        
		
		
		final Set<String> tableTypes = new TreeSet<String>();
		final ResultSet rs = md.getTableTypes();
		if (rs != null)
		{
			try
			{
				while (rs.next())
				{
					tableTypes.add(rs.getString(1).trim());
				}
			}
			finally
			{
				SQLUtilities.closeResultSet(rs);
         }
		}

		final String dbProductName = getDatabaseProductName();
		final int nbrTableTypes = tableTypes.size();

		
		
		
		
		if (nbrTableTypes == 1 && dbProductName.equals("InstantDB"))
		{
			tableTypes.clear();
			tableTypes.add("TABLE");
			tableTypes.add("SYSTEM TABLE");
		}

		
		
		
		
		else if (dbProductName.equals("PostgreSQL"))
		{
			if (nbrTableTypes == 0 || nbrTableTypes == 1)
			{
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Detected PostgreSQL and "+nbrTableTypes+
                                " table types - overriding to 4 table types");
                }
				tableTypes.clear();
				tableTypes.add("TABLE");
				tableTypes.add("SYSTEM TABLE");
				tableTypes.add("VIEW");
                tableTypes.add("SYSTEM VIEW");
			}
            
            
            if (tableTypes.contains("INDEX")) {
                tableTypes.remove("INDEX");
            }
            
            
            if (tableTypes.contains("SEQUENCE")) {
                tableTypes.remove("SEQUENCE");
            }
            
            
            if (tableTypes.contains("SYSTEM INDEX")) {
                tableTypes.remove("SYSTEM INDEX");
            }
		}

		
		
		else if (DialectFactory.getDialectType(this) == DialectType.INFORMIX) {
			if (nbrTableTypes == 0) {
				if (s_log.isDebugEnabled()) {
					s_log.debug("Detected Informix with no table types returned.  Defaulting to "
						+ "TABLE | SYSTEM TABLE | VIEW");
				}
				tableTypes.add("TABLE");
				tableTypes.add("SYSTEM TABLE");
				tableTypes.add("VIEW");				
			}
		}
		
		value = tableTypes.toArray(new String[tableTypes.size()]);
		_cache.put(key, value);
		return value;
	}


   
   public synchronized ITableInfo[] getTables(String catalog,
                                              String schemaPattern,
                                              String tableNamePattern,
                                              String[] types,
                                              ProgressCallBack progressCallBack)
      throws SQLException
   {


      final DatabaseMetaData md = privateGetJDBCMetaData();
      final String dbDriverName = getDriverName();
      Set<ITableInfo> list = new TreeSet<ITableInfo>();

      
      if (dbDriverName.equals(IDriverNames.FREE_TDS) && schemaPattern == null)      
      {
         schemaPattern = "dbo";
      }
      if (dbDriverName.equals(IDriverNames.AS400) && schemaPattern == null)
      {
          schemaPattern = "*ALLUSR";
      }

      
      if (catalog == null && DriverMatch.isComHttxDriver(_conn))
      {
          String[] catalogs=getCatalogs();
          if (catalogs != null) {
            for (int i = 0; i < catalogs.length; i++) {
              ITableInfo[] tables = getTables(catalogs[i],
                                              schemaPattern,
                                              tableNamePattern,
                                              types,
                                              progressCallBack);
              for(int j=0;j<tables.length;j++){
                list.add(tables[j]);
              }
            }
            return list.toArray(new ITableInfo[list.size()]);
          }
      }
      


      Map<String, ITableInfo> nameMap = null;
      ResultSet superTabResult = null;
      ResultSet tabResult = null;
      try
      {
         if (supportsSuperTables) {
             try
             {
                superTabResult = md.getSuperTables(catalog, 
                                                   schemaPattern,
                								   tableNamePattern);
                
                
                if (superTabResult != null && superTabResult.next())
                {
                   nameMap = new HashMap<String, ITableInfo>();
                }
             }
             catch (Throwable th)
             {
                s_log.debug("DBMS/Driver doesn't support getSupertables(): "+
                            th.getMessage());
                supportsSuperTables = false;
             }
         }
         
         tabResult = md.getTables(catalog, schemaPattern, tableNamePattern, types);
         int count = 0;
         while (tabResult != null && tabResult.next())
         {
            ITableInfo tabInfo = new TableInfo(tabResult.getString(1),
               tabResult.getString(2), tabResult.getString(3),
               tabResult.getString(4), tabResult.getString(5),
               this);
            if (nameMap != null)
            {
               nameMap.put(tabInfo.getSimpleName(), tabInfo);
            }
            list.add(tabInfo);

            if (null != progressCallBack)
            {
               if (0 == count++ % 100)
               {
                  progressCallBack.currentlyLoading(tabInfo.getSimpleName());
               }
            }
         }

         
         if (nameMap != null)
         {
            do
            {
               String tabName = superTabResult.getString(3);
               TableInfo tabInfo = (TableInfo) nameMap.get(tabName);
               if (tabInfo == null)
                  continue;
               String superTabName = superTabResult.getString(4);
               if (superTabName == null)
                  continue;
               TableInfo superInfo = (TableInfo) nameMap.get(superTabName);
               if (superInfo == null)
                  continue;
               superInfo.addChild(tabInfo);
               list.remove(tabInfo); 

               if (null != progressCallBack)
               {
                  if (0 == count++ % 20)
                  {
                     progressCallBack.currentlyLoading(tabInfo.getSimpleName());
                  }
               }
            }
            while (superTabResult.next());
         }
      }
      finally
      {
         SQLUtilities.closeResultSet(tabResult);
         SQLUtilities.closeResultSet(superTabResult);
      }

      return list.toArray(new ITableInfo[list.size()]);
   }
   
   
    public synchronized IUDTInfo[] getUDTs(String catalog, String schemaPattern,
								           String typeNamePattern, int[] types)
		throws SQLException
	{
		DatabaseMetaData md = privateGetJDBCMetaData();
		ArrayList<UDTInfo> list = new ArrayList<UDTInfo>();
		checkForInformix(catalog);
		ResultSet rs = md.getUDTs(catalog, schemaPattern, typeNamePattern, types);
		try
		{
			final int[] cols = new int[] {1, 2, 3, 4, 5, 6};
			DialectType dialectType = DialectFactory.getDialectType(this);
			final ResultSetReader rdr = new ResultSetReader(rs, cols, dialectType);
			Object[] row = null;
			while ((row = rdr.readRow()) != null)
			{
				list.add(new UDTInfo(getAsString(row[0]), getAsString(row[1]), getAsString(row[2]),
									getAsString(row[3]), getAsString(row[4]), getAsString(row[5]),
									this));
			}
		}
		finally
		{
			SQLUtilities.closeResultSet(rs);
      }

		return list.toArray(new IUDTInfo[list.size()]);
	}

   
   private void checkForInformix(String catalogName)
	{
   	if (DialectFactory.getDialectType(this) != DialectType.INFORMIX) {
   		return;
   	}
   	
		Statement stmt = null;
		try
		{
			stmt = _conn.createStatement();
			stmt.execute("Drop procedure mode_decode");
		} catch (SQLException e)
		{
			
			
			s_log.info("setInformixCatalog: unable to drop procedure mode_decode: " + e.getMessage(), e);
		} finally
		{
			SQLUtilities.closeStatement(stmt);
		}
	}
    
   private String getAsString(Object val)
   {
      if(null == val)
      {
         return null;
      }
      else
      {
         if (val instanceof String) {
             return (String)val;
         } else {
             return "" + val;
         }
      }

   }

   
   public synchronized String[] getNumericFunctions() throws SQLException
	{
		final String key = "getNumericFunctions";
		String[] value = (String[])_cache.get(key);
		if (value != null)
		{
			return value;
		}

		value = makeArray(privateGetJDBCMetaData().getNumericFunctions());
		_cache.put(key, value);
		return value;
	}

    
	public synchronized String[] getStringFunctions() throws SQLException
	{
		final String key = "getStringFunctions";
		String[] value = (String[])_cache.get(key);
		if (value != null)
		{
			return value;
		}

		value = makeArray(privateGetJDBCMetaData().getStringFunctions());
		_cache.put(key, value);
		return value;
	}

    
	public synchronized String[] getSystemFunctions() throws SQLException
	{
		final String key = "getSystemFunctions";
		String[] value = (String[])_cache.get(key);
		if (value != null)
		{
			return value;
		}

		value = makeArray(privateGetJDBCMetaData().getSystemFunctions());
		_cache.put(key, value);
		return value;
	}

    
	public synchronized String[] getTimeDateFunctions() throws SQLException
	{
		final String key = "getTimeDateFunctions";
		String[] value = (String[])_cache.get(key);
		if (value != null)
		{
			return value;
		}

		value = makeArray(privateGetJDBCMetaData().getTimeDateFunctions());
		_cache.put(key, value);
		return value;
	}

    
	public synchronized String[] getSQLKeywords() throws SQLException
	{
		final String key = "getSQLKeywords";
		String[] value = (String[])_cache.get(key);
		if (value != null)
		{
			return value;
		}

		value = makeArray(privateGetJDBCMetaData().getSQLKeywords());
		_cache.put(key, value);
		return value;
	}

	
	public synchronized BestRowIdentifier[] getBestRowIdentifier(ITableInfo ti) throws SQLException {
		final List<BestRowIdentifier> results = new ArrayList<BestRowIdentifier>();

		ResultSet rs = null;
		try {
			boolean columnsCanBeNullable = true;
			rs = privateGetJDBCMetaData().getBestRowIdentifier(
			   ti.getCatalogName(), ti.getSchemaName(), ti.getSimpleName(), DatabaseMetaData.bestRowTransaction,
			   columnsCanBeNullable);

			final String catalog = ti.getCatalogName();
			final String schema = ti.getSchemaName();
			final String table = ti.getSimpleName();

			final ResultSetColumnReader rdr = new ResultSetColumnReader(rs);
			while (rdr.next()) {
				final BestRowIdentifier rid = new BestRowIdentifier(
				   catalog, schema, table, rdr.getLong(1).intValue(), rdr.getString(2),
				   rdr.getLong(3).shortValue(), rdr.getString(4), rdr.getLong(5).intValue(),
				   rdr.getLong(7).shortValue(), rdr.getLong(8).shortValue(), this);
				results.add(rid);
			}
		} finally {
			SQLUtilities.closeResultSet(rs);
		}

		final BestRowIdentifier[] ar = new BestRowIdentifier[results.size()];
		return results.toArray(ar);
	}

	
   public ResultSet getColumnPrivileges(ITableInfo ti) throws SQLException {
      
      final String columns = DialectFactory.isMySQL(this) ? "%" : null;
      return privateGetJDBCMetaData().getColumnPrivileges(ti.getCatalogName(),
                                                          ti.getSchemaName(),
                                                          ti.getSimpleName(),
                                                          columns);
   }

   
   public synchronized IDataSet getColumnPrivilegesDataSet(ITableInfo ti,
         int[] columnIndices, boolean computeWidths) throws DataSetException {
      ResultSet rs = null;
      try {
         DatabaseMetaData md = privateGetJDBCMetaData();
         final String columns = DialectFactory.isMySQL(this) ? "%" : null;

         rs = md.getColumnPrivileges(ti.getCatalogName(),
                                     ti.getSchemaName(),
                                     ti.getSimpleName(),
                                     columns);
         ResultSetDataSet rsds = new ResultSetDataSet();
         rsds.setResultSet(rs,
                           columnIndices,
                           computeWidths,
                           DialectFactory.getDialectType(this));
         return rsds;
      } catch (SQLException e) {
         throw new DataSetException(e);
      } finally {
         SQLUtilities.closeResultSet(rs);
      }
   }
    
   
   public ResultSet getExportedKeys(ITableInfo ti) throws SQLException {
      return privateGetJDBCMetaData().getExportedKeys(ti.getCatalogName(),
                                                      ti.getSchemaName(),
                                                      ti.getSimpleName());
   }

   
   public synchronized IDataSet getExportedKeysDataSet(ITableInfo ti)
         throws DataSetException 
   {
      ResultSet rs = null;
      try {
         rs = privateGetJDBCMetaData().getExportedKeys(ti.getCatalogName(),
                                                       ti.getSchemaName(),
                                                       ti.getSimpleName());
         ResultSetDataSet rsds = new ResultSetDataSet();
         rsds.setResultSet(rs, null, true, DialectFactory.getDialectType(this));
         return rsds;
      } catch (SQLException e) {
         throw new DataSetException(e);
      } finally {
         SQLUtilities.closeResultSet(rs);
      }
   }
    
   
	public ResultSet getImportedKeys(ITableInfo ti)
		throws SQLException
	{
		return privateGetJDBCMetaData().getImportedKeys(
			ti.getCatalogName(), ti.getSchemaName(),
			ti.getSimpleName());
	}

	
	public synchronized ForeignKeyInfo[] getImportedKeysInfo(String catalog, 
                                                             String schema, 
                                                             String tableName) 
        throws SQLException 
    {
        ResultSet rs = 
            privateGetJDBCMetaData().getImportedKeys(catalog, schema, tableName);
        return getForeignKeyInfo(rs);
    }
    
    
    public synchronized ForeignKeyInfo[] getImportedKeysInfo(ITableInfo ti)
		throws SQLException
	{
		return getForeignKeyInfo(privateGetJDBCMetaData().getImportedKeys(ti.getCatalogName(),
								ti.getSchemaName(), ti.getSimpleName()));
	}

   
   public synchronized IDataSet getImportedKeysDataSet(ITableInfo ti)
         throws DataSetException {
      ResultSet rs = null;
      try {
         rs = privateGetJDBCMetaData().getImportedKeys(ti.getCatalogName(),
                                                       ti.getSchemaName(),
                                                       ti.getSimpleName());
         ResultSetDataSet rsds = new ResultSetDataSet();
         rsds.setResultSet(rs, null, true, DialectFactory.getDialectType(this));
         return rsds;
      } catch (SQLException e) {
         throw new DataSetException(e);
      } finally {
         SQLUtilities.closeResultSet(rs);
      }
   }    
    


   
   public synchronized ForeignKeyInfo[] getExportedKeysInfo(String catalog,
         String schema, String tableName) throws SQLException {
      ResultSet rs = privateGetJDBCMetaData().getExportedKeys(catalog,
                                                              schema,
                                                              tableName);
      return getForeignKeyInfo(rs);
   } 
    
	
	public synchronized ForeignKeyInfo[] getExportedKeysInfo(ITableInfo ti)
		throws SQLException
	{
		return getForeignKeyInfo(privateGetJDBCMetaData().getExportedKeys(ti.getCatalogName(),
								ti.getSchemaName(), ti.getSimpleName()));
	}
        
	private ForeignKeyInfo[] getForeignKeyInfo(ResultSet rs)
		throws SQLException
	{
		final Map<String, ForeignKeyInfo> keys = 
            new HashMap<String, ForeignKeyInfo>();
		final Map<String, ArrayList<ForeignKeyColumnInfo>> columns = 
            new HashMap<String, ArrayList<ForeignKeyColumnInfo>>();

		try
		{
			final ResultSetColumnReader rdr = new ResultSetColumnReader(rs);
			while (rdr.next())
			{
				final ForeignKeyInfo fki = new ForeignKeyInfo(rdr.getString(1),
							rdr.getString(2), rdr.getString(3), rdr.getString(4),
                            rdr.getString(5),rdr.getString(6), rdr.getString(7),
                            rdr.getString(8),
							rdr.getLong(10).intValue(), rdr.getLong(11).intValue(),
							rdr.getString(12), rdr.getString(13),
							rdr.getLong(14).intValue(), null, this);
				final String key = createForeignKeyInfoKey(fki);
				if (!keys.containsKey(key))
				{
					keys.put(key, fki);
					columns.put(key, new ArrayList<ForeignKeyColumnInfo>());
				}

				ForeignKeyColumnInfo fkiCol = new ForeignKeyColumnInfo(rdr.getString(8),
														rdr.getString(8),
														rdr.getLong(9).intValue());
				columns.get(key).add(fkiCol);
			}
		}
		finally
		{
			SQLUtilities.closeResultSet(rs);
      }

		final ForeignKeyInfo[] results = new ForeignKeyInfo[keys.size()];
		Iterator<ForeignKeyInfo> it = keys.values().iterator();
		int idx = 0;
		while (it.hasNext())
		{
			final ForeignKeyInfo fki = it.next();
			final String key = createForeignKeyInfoKey(fki);
			final List<ForeignKeyColumnInfo> colsList = columns.get(key);
			final ForeignKeyColumnInfo[] fkiCol = 
                colsList.toArray(new ForeignKeyColumnInfo[colsList.size()]);
			fki.setForeignKeyColumnInfo(fkiCol);
			results[idx++] = fki;
		}

		return results;
	}
        
    
   public synchronized ResultSetDataSet getIndexInfo(ITableInfo ti,
         int[] columnIndices, boolean computeWidths) throws DataSetException {
      ResultSet rs = null;
      try {
         rs = privateGetJDBCMetaData().getIndexInfo(ti.getCatalogName(),
                                                    ti.getSchemaName(),
                                                    ti.getSimpleName(),
                                                    false,
                                                    true);
         ResultSetDataSet rsds = new ResultSetDataSet();
         rsds.setResultSet(rs,
                           columnIndices,
                           computeWidths,
                           DialectFactory.getDialectType(this));
         return rsds;
      } catch (SQLException e) {
         throw new DataSetException(e);
      } finally {
         SQLUtilities.closeResultSet(rs);
      }
   }
    
    
    public List<IndexInfo> getIndexInfo(ITableInfo ti) throws SQLException {
        List<IndexInfo> result = new ArrayList<IndexInfo>();
        ResultSet rs = null;
        try {
            rs = privateGetJDBCMetaData().getIndexInfo(
                    ti.getCatalogName(), ti.getSchemaName(),
                    ti.getSimpleName(), false, true);
            while (rs.next()) {
                String catalog = rs.getString(1);
                String schema = rs.getString(2);
                String table = rs.getString(3); 
                boolean nonunique = rs.getBoolean(4);
                String indexQualifier = rs.getString(5);
                String indexName = rs.getString(6); 
                IndexInfo.IndexType indexType = 
                    JDBCTypeMapper.getIndexType(rs.getShort(7));
                short ordinalPosition = rs.getShort(8);
                String column = rs.getString(9); 
                IndexInfo.SortOrder sortOrder = 
                    JDBCTypeMapper.getIndexSortOrder(rs.getString(10));
                int cardinality = rs.getInt(11);
                int pages = rs.getInt(12);
                String filterCondition = rs.getString(13);

                IndexInfo indexInfo = new IndexInfo(catalog, 
                                                schema, 
                                                indexName,
                                                table,
                                                column,
                                                nonunique,
                                                indexQualifier,
                                                indexType,
                                                ordinalPosition,
                                                sortOrder,
                                                cardinality,
                                                pages,
                                                filterCondition,
                                                this);        
                result.add(indexInfo);
            }
        } catch (SQLException e) {
        } finally {
            SQLUtilities.closeResultSet(rs);
        }        
        return result;
    }
    
    
	public ResultSet getPrimaryKeys(ITableInfo ti)
		throws SQLException
	{
		return privateGetJDBCMetaData().getPrimaryKeys(
			ti.getCatalogName(), ti.getSchemaName(),
			ti.getSimpleName());
	}
    
	
   public synchronized IDataSet getPrimaryKey(ITableInfo ti,
         int[] columnIndices, boolean computeWidths) throws DataSetException {
      ResultSet rs = null;
      try {
         rs = privateGetJDBCMetaData().getPrimaryKeys(ti.getCatalogName(),
                                                      ti.getSchemaName(),
                                                      ti.getSimpleName());
         ResultSetDataSet rsds = new ResultSetDataSet();
         rsds.setResultSet(rs,
                           columnIndices,
                           computeWidths,
                           DialectFactory.getDialectType(this));
         return rsds;
      } catch (SQLException e) {
         throw new DataSetException(e);
      } finally {
         SQLUtilities.closeResultSet(rs);
      }
   }

    
    public synchronized PrimaryKeyInfo[] getPrimaryKey(ITableInfo ti) 
        throws SQLException
    {
        return getPrimaryKey(ti.getCatalogName(), 
                             ti.getSchemaName(), 
                             ti.getSimpleName());
    }
    
    
    public synchronized PrimaryKeyInfo[] getPrimaryKey(String catalog, 
                                                       String schema, 
                                                       String table) 
        throws SQLException
    {
        final List<PrimaryKeyInfo> results = new ArrayList<PrimaryKeyInfo>();
        ResultSet rs = null;
        try {
            rs = privateGetJDBCMetaData().getPrimaryKeys(catalog, schema,table);
            while (rs.next()) {
                PrimaryKeyInfo pkInfo = 
                    new PrimaryKeyInfo(rs.getString(1),  
                                       rs.getString(2),  
                                       rs.getString(3),  
                                       rs.getString(4),  
                                       rs.getShort(5),   
                                       rs.getString(6),  
                                       this);
                results.add(pkInfo);
            }
        }finally {
            SQLUtilities.closeResultSet(rs);
        }
        
        final PrimaryKeyInfo[] ar = new PrimaryKeyInfo[results.size()];
        return results.toArray(ar);
    }
    
    
    
    public ResultSet getProcedureColumns(IProcedureInfo ti)
		throws SQLException
	{
		return privateGetJDBCMetaData().getProcedureColumns(ti.getCatalogName(),
													        ti.getSchemaName(),
													        ti.getSimpleName(),
													        "%");
	}

   
   public synchronized IDataSet getProcedureColumnsDataSet(IProcedureInfo ti)
         throws DataSetException {
      ResultSet rs = null;
      try {
         DatabaseMetaData md = privateGetJDBCMetaData();
         rs = md.getProcedureColumns(ti.getCatalogName(),
                                     ti.getSchemaName(),
                                     ti.getSimpleName(),
                                     "%");
         ResultSetDataSet rsds = new ResultSetDataSet();
         rsds.setResultSet(rs, DialectFactory.getDialectType(this));
         return rsds;
      } catch (SQLException e) {
         throw new DataSetException(e);
      } finally {
         SQLUtilities.closeResultSet(rs);
      }
   }
    
     
	public ResultSet getTablePrivileges(ITableInfo ti)
		throws SQLException
	{
		return privateGetJDBCMetaData().getTablePrivileges(ti.getCatalogName(),
													ti.getSchemaName(),
													ti.getSimpleName());
	}

   
   public synchronized IDataSet getTablePrivilegesDataSet(ITableInfo ti,
         int[] columnIndices, boolean computeWidths) throws DataSetException {
      ResultSet rs = null;
      try {
         DatabaseMetaData md = privateGetJDBCMetaData();
         rs = md.getTablePrivileges(ti.getCatalogName(),
                                    ti.getSchemaName(),
                                    ti.getSimpleName());
         ResultSetDataSet rsds = new ResultSetDataSet();
         rsds.setResultSet(rs,
                           columnIndices,
                           computeWidths,
                           DialectFactory.getDialectType(this));
         return rsds;
      } catch (SQLException e) {
         throw new DataSetException(e);
      } finally {
         SQLUtilities.closeResultSet(rs);
      }
   }
    
    
	
	public ResultSet getVersionColumns(ITableInfo ti)
		throws SQLException
	{
		return privateGetJDBCMetaData().getVersionColumns(ti.getCatalogName(),
												          ti.getSchemaName(),
												          ti.getSimpleName());
	}

   
    public synchronized IDataSet getVersionColumnsDataSet(ITableInfo ti)
        throws DataSetException
    {
        ResultSet rs = null;
        try {
            DatabaseMetaData md = privateGetJDBCMetaData();
            rs = md.getVersionColumns(ti.getCatalogName(),
                                      ti.getSchemaName(),
                                      ti.getSimpleName());
            ResultSetDataSet rsds = new ResultSetDataSet();
            rsds.setResultSet(rs, DialectFactory.getDialectType(this));
            return rsds;
        } catch (SQLException e) {
            throw new DataSetException(e);
        } finally {
            SQLUtilities.closeResultSet(rs);
        }
    }
    
    
	private ResultSet getColumns(ITableInfo ti)
		throws SQLException
	{
		return privateGetJDBCMetaData().getColumns(ti.getCatalogName(),
											ti.getSchemaName(),
											ti.getSimpleName(), "%");
	}
    
	
   public synchronized IDataSet getColumns(ITableInfo ti, int[] columnIndices,
         boolean computeWidths) throws DataSetException {
      IDataSet result = null;
      ResultSet rs = null;
      try {
         rs = getColumns(ti);
         ResultSetDataSet rsds = new ResultSetDataSet();
         rsds.setResultSet(rs,
                           columnIndices,
                           computeWidths,
                           DialectFactory.getDialectType(this));
         result = rsds;
      } catch (SQLException e) {
         throw new DataSetException(e);
      } finally {
         SQLUtilities.closeResultSet(rs);
      }
      return result;
   }
    
    
    public synchronized TableColumnInfo[] getColumnInfo(String catalog, 
                                                        String schema, 
                                                        String table) 
        throws SQLException 
    {
       ResultSet rs = null;
       try
       {
          final Map<Integer, TableColumnInfo> columns = 
              new TreeMap<Integer, TableColumnInfo>();
          DatabaseMetaData md = privateGetJDBCMetaData();
          rs = md.getColumns(catalog, schema, table, "%");
          final ResultSetColumnReader rdr = new ResultSetColumnReader(rs);

          int isNullAllowed = DatabaseMetaData.typeNullableUnknown;
                    
          int index = 0;
          while (rdr.next())
          {
             
             if (DialectFactory.isPointbase(this)) {
            	 if (rdr.getBoolean(11)) {
            		 isNullAllowed = DatabaseMetaData.typeNullable;
            	 } else {
            		 isNullAllowed = DatabaseMetaData.typeNoNulls;
            	 } 
             } else {
            	 isNullAllowed = rdr.getLong(11).intValue();
             }
         	 
             final TableColumnInfo tci = 
                 new TableColumnInfo(rdr.getString(1),           
                                     rdr.getString(2),           
                                     rdr.getString(3),           
                                     rdr.getString(4),           
                                     rdr.getLong(5).intValue(),  
                                     rdr.getString(6),           
                                     rdr.getLong(7).intValue(),  
                                     rdr.getLong(9).intValue(),  
                                     rdr.getLong(10).intValue(), 
                                     isNullAllowed, 				  
                                     rdr.getString(12),          
                                     rdr.getString(13),          
                                     rdr.getLong(16).intValue(), 
                                     rdr.getLong(17).intValue(), 
                                     rdr.getString(18),          
                                     this);
             
             
             
             
             ++index;
             
             
             columns.put(Integer.valueOf(10000 * tci.getOrdinalPosition()  + index), tci);
          }

          return columns.values().toArray(new TableColumnInfo[columns.size()]);

       }
       finally
       {
           SQLUtilities.closeResultSet(rs);
       }
    }
    
    
    public synchronized TableColumnInfo[] getColumnInfo(ITableInfo ti)
		throws SQLException
	{
	    return getColumnInfo(ti.getCatalogName(), ti.getSchemaName(), ti.getSimpleName());
    }

   
    public boolean correctlySupportsSetMaxRows() throws SQLException
	{
		return !IDriverNames.OPTA2000.equals(getDriverName());
	}

   
	public synchronized boolean supportsMultipleResultSets()
			throws SQLException
	{
		final String key = "supportsMultipleResultSets";
		Boolean value = (Boolean)_cache.get(key);
		if (value != null)
		{
			return value.booleanValue();
		}

		value = Boolean.valueOf(privateGetJDBCMetaData().supportsMultipleResultSets());
		_cache.put(key, value);

		return value.booleanValue();
	}

   
	public synchronized boolean storesUpperCaseIdentifiers()
		throws SQLException
	{
		final String key = "storesUpperCaseIdentifiers";
		Boolean value = (Boolean)_cache.get(key);
		if (value != null)
		{
			return value.booleanValue();
		}

		value = Boolean.valueOf(privateGetJDBCMetaData().storesUpperCaseIdentifiers());
		_cache.put(key, value);

		return value.booleanValue();
	}


   
	public void clearCache()
	{
		_cache.clear();
	}

	
	private static String[] makeArray(String data)
	{
		if (data == null)
		{
			data = "";
		}

		final List<String> list = new ArrayList<String>();
		final StringTokenizer st = new StringTokenizer(data, ",");
		while (st.hasMoreTokens())
		{
			list.add(st.nextToken());
		}
		Collections.sort(list);

		return list.toArray(new String[list.size()]);
	}

	
	private DatabaseMetaData privateGetJDBCMetaData() throws SQLException
	{
        checkThread();
		return _conn.getConnection().getMetaData();
	}

    
	private String createForeignKeyInfoKey(ForeignKeyInfo fki)
	{
		final StringBuffer buf = new StringBuffer();
		buf.append(fki.getForeignKeyCatalogName())
			.append(fki.getForeignKeySchemaName())
			.append(fki.getForeignKeyTableName())
			.append(fki.getForeignKeyName())
			.append(fki.getPrimaryKeyCatalogName())
			.append(fki.getPrimaryKeySchemaName())
			.append(fki.getPrimaryKeyTableName())
			.append(fki.getPrimaryKeyName());
		return buf.toString();
	}
    
    
    private void checkThread() {
        
    }
}

