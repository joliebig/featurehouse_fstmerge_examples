package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

public interface ITableTriggerExtractor {

    
    String getTableTriggerQuery();
    
    
    
    void bindParamters(PreparedStatement pstmt, IDatabaseObjectInfo dbo)
        throws SQLException;
}
