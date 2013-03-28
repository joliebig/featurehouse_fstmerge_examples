
package net.sourceforge.squirrel_sql.plugins.postgres.types;

import java.awt.event.KeyListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.BaseDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IRestorableTextComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.StringFieldKeyTextHandler;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class PostgreSqlXmlTypeDataTypeComponent extends BaseDataTypeComponent implements IDataTypeComponent
{

	
	private static ILogger s_log = LoggerController.createLogger(PostgreSqlXmlTypeDataTypeComponent.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(PostgreSqlXmlTypeDataTypeComponent.class);

	
	static interface i18n
	{
		
		String CELL_ERROR_MSG = s_stringMgr.getString("PostgreSqlXmlTypeDataTypeComponent.cellErrorMsg");
	}

	
	public boolean canDoFileIO()
	{
		return true;
	}

	
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
		return null;
	}

	
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
			result = rs.getString(idx);
			if (result == null || "".equals(result))
			{
				return NULL_VALUE_PATTERN;
			} 
		} catch (Exception e) {
			s_log.error("Unexpected exception while attempting to read PostgreSQL XML column", e);
		}
		if (result == null)
		{
			result = i18n.CELL_ERROR_MSG;
		}
		return result;
	}

	
	public void setPreparedStatementValue(PreparedStatement pstmt, Object value, int position)
		throws SQLException
	{
		if (value == null || "".equals(value))
		{
			pstmt.setNull(position, java.sql.Types.OTHER, "xml");
		} else
		{
			try
			{				
				pstmt.setString(position, value.toString());
			} catch (Exception e)
			{
				s_log.error("setPreparedStatementValue: Unexpected exception - " + e.getMessage(), e);
			}

		}
	}

	
	public boolean useBinaryEditingPanel()
	{
		return false;
	}

	
	public boolean areEqual(Object obj1, Object obj2)
	{
		return ((String) obj1).equals(obj2);
	}

	
	@Override
	public Object validateAndConvert(String value, Object originalValue, StringBuffer messageBuffer)
	{
		
		if (value.equals("<null>"))
			return null;

		
		return value;	
	}

	
	@Override
	protected KeyListener getKeyListener(IRestorableTextComponent component)
	{
		boolean isNullable = false;
		int columnSize = -1;
		if (super._colDef != null) {
			isNullable = _colDef.isNullable();
			columnSize = _colDef.getColumnSize();
		}
		return new StringFieldKeyTextHandler(component, columnSize, isNullable);
	}

	
}
