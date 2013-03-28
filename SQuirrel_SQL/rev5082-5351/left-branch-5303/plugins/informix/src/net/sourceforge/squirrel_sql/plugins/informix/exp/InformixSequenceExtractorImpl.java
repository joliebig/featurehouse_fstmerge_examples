
package net.sourceforge.squirrel_sql.plugins.informix.exp;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ISequenceParentExtractor;
import net.sourceforge.squirrel_sql.client.session.schemainfo.ObjFilterMatcher;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

public class InformixSequenceExtractorImpl implements ISequenceParentExtractor
{
	
	private static final String SQL =
        "SELECT  T2.tabname AS sequence_name " +
        "FROM   informix.syssequences AS T1, " +
        "       informix.systables    AS T2 " +
        "WHERE   T2.tabid = T1.tabid " +
        "and T2.owner = ? " +
        "and T2.tabname like ? ";
	
	
	@Override
	public String getSequenceParentQuery()
	{
		return SQL;
	}

	@Override
	public void bindParameters(PreparedStatement pstmt, IDatabaseObjectInfo parentDbinfo,
		ObjFilterMatcher filterMatcher) throws SQLException
	{
		pstmt.setString(1, parentDbinfo.getSchemaName());
		pstmt.setString(2, filterMatcher.getSqlLikeMatchString());
	}

}
