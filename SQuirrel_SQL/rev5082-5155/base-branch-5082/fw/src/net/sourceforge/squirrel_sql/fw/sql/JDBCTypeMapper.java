package net.sourceforge.squirrel_sql.fw.sql;


import java.lang.reflect.Field;
import java.sql.DatabaseMetaData;
import java.sql.Types;
import java.util.ArrayList;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class JDBCTypeMapper
{

	
	private static ILogger s_log = LoggerController.createLogger(JDBCTypeMapper.class);
	
	
	public static String[] getJdbcTypeList()
	{
		ArrayList<String> result = new ArrayList<String>();		
		Field[] fields = java.sql.Types.class.getDeclaredFields();
      for (int i = 0; i < fields.length; i++) {
          Field field = fields[i];
          result.add(field.getName());
      }
		return result.toArray(new String[result.size()]);
	}

	public static String getJdbcTypeName(int jdbcType)
	{
		String result = "UNKNOWN";
		try
		{
			Field[] fields = java.sql.Types.class.getDeclaredFields();
			for (int i = 0; i < fields.length; i++)
			{
				Field field = fields[i];
				if (field.getInt(null) == jdbcType)
				{
					result = field.getName();
					break;
				}
			}
		}
		catch (SecurityException e)
		{
			s_log.error("getJdbcTypeName: unexpected exception: "+e.getMessage(), e);
		}
		catch (IllegalArgumentException e)
		{
			s_log.error("getJdbcTypeName: unexpected exception: "+e.getMessage(), e);
		}
		catch (IllegalAccessException e)
		{
			s_log.error("getJdbcTypeName: unexpected exception: "+e.getMessage(), e);
		}
		return result;
	}
	
	
	public static int getJdbcType(String jdbcTypeName, int defaultVal) {
		
		if (jdbcTypeName == null) 
		{ 
			return Types.NULL; 
		} 
		int result = defaultVal;
		
		try
		{
			Field[] fields = java.sql.Types.class.getDeclaredFields();
			for (int i = 0; i < fields.length; i++)
			{
				Field field = fields[i];
				if (field.getName().equalsIgnoreCase(jdbcTypeName)) {
					result = field.getInt(null);
				}
			}
		}
		catch (IllegalArgumentException e)
		{
			s_log.error("getJdbcTypeName: unexpected exception: "+e.getMessage(), e);
		}
		catch (IllegalAccessException e)
		{
			s_log.error("getJdbcTypeName: unexpected exception: "+e.getMessage(), e);
		}
		return result;
	}
	
	
	
	public static int getJdbcType(String jdbcTypeName)
	{
		return getJdbcType(jdbcTypeName, Types.NULL);
	}

	public static boolean isNumberType(int jdbcType)
	{
		boolean result = false;
		switch (jdbcType)
		{
		case Types.BIGINT:
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.INTEGER:
		case Types.NUMERIC:
			result = true;
			break;
		default:
			result = false;
		}
		return result;
	}

	public static boolean isDateType(int jdbcType)
	{
		boolean result = false;
		switch (jdbcType)
		{
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			result = true;
			break;
		default:
			result = false;
		}
		return result;
	}

	public static boolean isLongType(int jdbcType)
	{
		boolean result = false;
		switch (jdbcType)
		{
		case Types.LONGVARBINARY:
		case Types.LONGVARCHAR:
		case Types.BLOB:
		case Types.CLOB:
			result = true;
			break;
		default:
			result = false;
		}
		return result;
	}

	public static IndexInfo.IndexType getIndexType(short indexType)
	{
		IndexInfo.IndexType result = null;
		switch (indexType)
		{
		case DatabaseMetaData.tableIndexStatistic:
			result = IndexInfo.IndexType.STATISTIC;
			break;
		case DatabaseMetaData.tableIndexClustered:
			result = IndexInfo.IndexType.CLUSTERED;
			break;
		case DatabaseMetaData.tableIndexHashed:
			result = IndexInfo.IndexType.HASHED;
			break;
		case DatabaseMetaData.tableIndexOther:
			result = IndexInfo.IndexType.OTHER;
			break;
		default:
			throw new IllegalArgumentException("Unknown index type: " + indexType);
		}
		return result;
	}

	public static IndexInfo.SortOrder getIndexSortOrder(String sortOrder)
	{
		if (sortOrder == null) { return IndexInfo.SortOrder.NONE; }
		if (sortOrder.equalsIgnoreCase("A")) { return IndexInfo.SortOrder.ASC; }
		if (sortOrder.equalsIgnoreCase("D")) { return IndexInfo.SortOrder.DESC; }

		throw new IllegalArgumentException("Unknown index sort order: " + sortOrder);
	}
}
