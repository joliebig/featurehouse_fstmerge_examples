package net.sourceforge.squirrel_sql.plugins.refactoring.commands;


import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.*;

import java.sql.SQLException;


public class PrimaryKeyCommandUtility {
    
    protected ISession _session;

    
    protected final IDatabaseObjectInfo[] _info;


    
    public PrimaryKeyCommandUtility(ISession session,
                                    IDatabaseObjectInfo[] info) {
        if (session == null) throw new IllegalArgumentException("ISession cannot be null");
        if (info == null) throw new IllegalArgumentException("IDatabaseObjectInfo[] cannot be null");

        _session = session;
        _info = info;
    }


    
    protected boolean tableHasPrimaryKey() throws SQLException {
        if (!(_info[0] instanceof ITableInfo)) {
            return false;
        }
        ITableInfo ti = (ITableInfo) _info[0];
        SQLDatabaseMetaData md = _session.getSQLConnection().getSQLMetaData();
        PrimaryKeyInfo[] pks = md.getPrimaryKey(ti);
        return (pks != null && pks.length > 0);
    }
}
