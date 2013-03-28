package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;


import java.awt.Component;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetScrollingPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.MapDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseObjectTab;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public abstract class BasePreparedStatementTab extends BaseObjectTab
{
	
	private final String _title;

	
	private final String _hint;

	private boolean _firstRowOnly;

	
	private DataSetScrollingPanel _comp;

	
	private final static ILogger s_log = LoggerController.createLogger(BasePreparedStatementTab.class);

	public BasePreparedStatementTab(String title, String hint) {
		this(title, hint, false);
	}

	public BasePreparedStatementTab(String title, String hint, boolean firstRowOnly) {
		super();
		if (title == null)
		{
			throw new IllegalArgumentException("Title == null");
		}
		_title = title;
		_hint = hint != null ? hint : title;
		_firstRowOnly = firstRowOnly;
	}

	
	public String getTitle()
	{
		return _title;
	}

	
	public String getHint()
	{
		return _hint;
	}

	public void clear()
	{
	}

	public Component getComponent()
	{
		if (_comp == null)
		{
			ISession session = getSession();
			SessionProperties props = session.getProperties();
			String destClassName = props.getMetaDataOutputClassName();
			try
			{
				_comp = new DataSetScrollingPanel(destClassName, null);
			} catch (Exception e)
			{
				s_log.error("Unexpected exception from call to getComponent: " + e.getMessage(), e);
			}

		}
		return _comp;
	}

	protected void refreshComponent() throws DataSetException
	{
		ISession session = getSession();
		if (session == null)
		{
			throw new IllegalStateException("Null ISession");
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = createStatement();
			rs = pstmt.executeQuery();
			final IDataSet ds = createDataSetFromResultSet(rs);
			_comp.load(ds);
		} catch (SQLException ex)
		{
			throw new DataSetException(ex);
		} finally
		{
			SQLUtilities.closeResultSet(rs, true);
		}
	}

	
	protected abstract PreparedStatement createStatement() throws SQLException;

	protected IDataSet createDataSetFromResultSet(ResultSet rs) throws DataSetException
	{
		final ResultSetDataSet rsds = new ResultSetDataSet();
		rsds.setResultSet(rs, getDialectType());
		if (!_firstRowOnly)
		{
			return rsds;
		}

		final int columnCount = rsds.getColumnCount();
		final ColumnDisplayDefinition[] colDefs = rsds.getDataSetDefinition().getColumnDefinitions();
		final Map<String, Object> data = new HashMap<String, Object>();
		if (rsds.next(null))
		{
			for (int i = 0; i < columnCount; ++i)
			{
				data.put(colDefs[i].getLabel(), rsds.get(i));
			}
		}
		return new MapDataSet(data);

	}
}
