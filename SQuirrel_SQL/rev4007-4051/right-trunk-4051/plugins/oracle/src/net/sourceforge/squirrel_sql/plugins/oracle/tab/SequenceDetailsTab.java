package net.sourceforge.squirrel_sql.plugins.oracle.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class SequenceDetailsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SequenceDetailsTab.class);


	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("oracle.sequenceDetails");
		
		String HINT = s_stringMgr.getString("oracle.displaysSequenceDetails");
	}

	
	private static final String SQL =
		"select sequence_name, min_value, max_value, increment_by, cycle_flag,"
			+ " order_flag, cache_size, last_number"
			+ " from all_sequences"
			+ " where sequence_owner = ?"
			+ " and sequence_name = ?";

	public SequenceDetailsTab()
	{
		super(i18n.TITLE, i18n.HINT, true);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(SQL);
		IDatabaseObjectInfo doi = getDatabaseObjectInfo();
		pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
