
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.schemainfo.ObjFilterMatcher;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;


public interface ISequenceParentExtractor
{
	String getSequenceParentQuery();

	void bindParameters(PreparedStatement pstmt, IDatabaseObjectInfo parentDbinfo,
		ObjFilterMatcher filterMatcher) throws SQLException;
}
