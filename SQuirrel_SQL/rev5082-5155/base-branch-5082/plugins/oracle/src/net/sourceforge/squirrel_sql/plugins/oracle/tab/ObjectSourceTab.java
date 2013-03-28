package net.sourceforge.squirrel_sql.plugins.oracle.tab;


import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;


public class ObjectSourceTab extends BaseSourceTab
{
	
	private static String SQL =
		"select text from sys.all_source where type = ?" + " and owner = ? and name = ? order by line";

	private final String _columnData;

	public ObjectSourceTab(String columnData, String hint)
	{
		this(columnData, null, hint);
	}

	public ObjectSourceTab(String columnData, String title, String hint)
	{
		super(title, hint);
		if (columnData == null) { throw new IllegalArgumentException("Column Data is null"); }
		_columnData = columnData;
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		final ISession session = getSession();
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();

		ISQLConnection conn = session.getSQLConnection();
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		pstmt.setString(1, ObjectSourceTab.this._columnData);
		pstmt.setString(2, doi.getSchemaName());
		pstmt.setString(3, doi.getSimpleName());
		return pstmt;
	}
}
