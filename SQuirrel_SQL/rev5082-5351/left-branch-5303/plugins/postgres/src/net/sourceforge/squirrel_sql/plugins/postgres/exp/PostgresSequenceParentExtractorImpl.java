
package net.sourceforge.squirrel_sql.plugins.postgres.exp;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ISequenceParentExtractor;
import net.sourceforge.squirrel_sql.client.session.schemainfo.ObjFilterMatcher;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

public class PostgresSequenceParentExtractorImpl implements ISequenceParentExtractor
{
	private static final String SQL = 
		"SELECT relname " +
	   "FROM pg_class " +
	   "WHERE relkind = 'S' " +
	   "AND relnamespace IN ( " +
	   "   SELECT " +
	   "   oid " +
	   "   FROM pg_namespace " +
	   "   WHERE nspname = ? " +
	   ") " +
	   "AND relname like ? ";
	
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
