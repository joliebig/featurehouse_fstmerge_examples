
package net.sourceforge.squirrel_sql.plugins.oracle.types;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.BaseDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class OracleXmlTypeDataTypeComponent extends BaseDataTypeComponent implements IDataTypeComponent
{

	
	private static final String XML_TYPE_CLASSNAME = "oracle.xdb.XMLType";

	
	private static Class<?> XML_TYPE_CLASS = null;

	
	private static ILogger s_log = LoggerController.createLogger(OracleXmlTypeDataTypeComponent.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(OracleXmlTypeDataTypeComponent.class);

	
	static interface i18n
	{
		
		String CELL_ERROR_MSG = s_stringMgr.getString("OracleXmlTypeDataTypeComponent.cellErrorMsg");
	}

	
	public boolean canDoFileIO()
	{
		return true;
	}

	
	@Override
	public String getClassName()
	{
		return "java.lang.String";
	}

	
	public Object getDefaultValue(String dbDefaultValue)
	{
		
		if (s_log.isInfoEnabled())
		{
			s_log.info("getDefaultValue: not yet implemented");
		}
		return dbDefaultValue;
	}

	
	@Override
	public String getWhereClauseValue(Object value, ISQLDatabaseMetaData md)
	{
		
		return super.getWhereClauseValue(value, md);
	}

	
	public boolean isEditableInCell(Object originalValue)
	{
		return !i18n.CELL_ERROR_MSG.equals(originalValue);
	}

	
	public boolean isEditableInPopup(Object originalValue)
	{
		return !i18n.CELL_ERROR_MSG.equals(originalValue);
	}

	
	public boolean needToReRead(Object originalValue)
	{
		return false;
	}

	
	public Object readResultSet(ResultSet rs, int idx, boolean limitDataRead) throws SQLException
	{
		Object result = null;
		try
		{
			final Object o = rs.getObject(idx);
			if (o == null)
			{
				return NULL_VALUE_PATTERN;
			}
			else if ("oracle.sql.OPAQUE".equals(o.getClass().getName()))
			{
				final Method createXmlMethod = getCreateXmlMethod(o.getClass());

				
				
				final Object xmlTypeObj = createXmlMethod.invoke(null, o);
				final Method getStringValMethod = XML_TYPE_CLASS.getMethod("getStringVal", (Class[]) null);

				
				
				final Object stringValueResult = getStringValMethod.invoke(xmlTypeObj, (Object[]) null);
				result = stringValueResult;

			}
			else if (XML_TYPE_CLASSNAME.equals(o.getClass().getName()))
			{
				XML_TYPE_CLASS = o.getClass();
				final Method getStringValMethod = XML_TYPE_CLASS.getMethod("getStringVal", (Class[]) null);

				
				
				final Object stringValueResult = getStringValMethod.invoke(o, (Object[]) null);
				result = stringValueResult;
			}
			else
			{
				result = o;
			}
		}
		catch (final ClassNotFoundException e)
		{
			s_log.error("Perhaps the XDK, which contains the class " + XML_TYPE_CLASSNAME
				+ " is not in the CLASSPATH?", e);
		}
		catch (final Exception e)
		{
			s_log.error("Unexpected exception while attempting to read " + "SYS.XMLType column", e);
		}
		if (result == null)
		{
			result = i18n.CELL_ERROR_MSG;
		}
		return result;
	}

	
	private Method getCreateXmlMethod(Class<?>... argClasses) throws ClassNotFoundException,
		NoSuchMethodException
	{
		if (XML_TYPE_CLASS == null)
		{
			XML_TYPE_CLASS = Class.forName(XML_TYPE_CLASSNAME);
		}

		Method createXmlMethod = null;

		try
		{
			createXmlMethod = XML_TYPE_CLASS.getMethod("createXML", argClasses);
		}
		catch (final SecurityException e)
		{
			if (s_log.isDebugEnabled())
			{
				s_log.debug("getCreateXmlMethod: unable to get method named createXML in class "
					+ "oracle.xdb.XMLType: " + e.getMessage(), e);
			}
		}
		catch (final NoSuchMethodException e)
		{
			if (s_log.isDebugEnabled())
			{
				s_log.debug("getCreateXmlMethod: unable to get method named createXML in class "
					+ "oracle.xdb.XMLType: " + e.getMessage(), e);
			}
		}

		if (createXmlMethod == null)
		{
			try
			{
				createXmlMethod = XML_TYPE_CLASS.getMethod("createXml", argClasses);
			}
			catch (final SecurityException e)
			{
				s_log.error("getCreateXmlMethod: Unable to get method named createXml or createXML in class "
					+ "oracle.xdb.XMLType: " + e.getMessage(), e);
				throw e;
			}
			catch (final NoSuchMethodException e)
			{
				s_log.error("getCreateXmlMethod: Unable to get method named createXml or createXML in class "
					+ "oracle.xdb.XMLType: " + e.getMessage(), e);
				throw e;
			}

		}
		return createXmlMethod;
	}

	
	public void setPreparedStatementValue(PreparedStatement pstmt, Object value, int position)
		throws SQLException
	{
		if (value == null)
		{
			
			
			

			
			
			
			
			
			

			
			pstmt.setObject(position, null);
		}
		else
		{
			try
			{
				final Class<?>[] args = new Class[] { Connection.class, String.class };

				final Method createXmlMethod = getCreateXmlMethod(args);

				final Object xmlTypeObj = createXmlMethod.invoke(null, pstmt.getConnection(), value.toString());

				
				pstmt.setObject(position, xmlTypeObj);
			}
			catch (final Exception e)
			{
				s_log.error("setPreparedStatementValue: Unexpected exception - " + e.getMessage(), e);
			}

		}
	}

	
	public boolean useBinaryEditingPanel()
	{
		return false;
	}

}
