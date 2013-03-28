package org.firebirdsql.squirrel.act;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.ISession;

class AlterIndexCommand extends AbstractMultipleSQLCommand
{
    private final boolean _activate;
    
    public AlterIndexCommand(ISession session, IPlugin plugin, boolean activate)
    {
        super(session, plugin);
        _activate = activate;
    }

    
    protected String getSQL(IDatabaseObjectInfo dbObj)
    {
        return "ALTER INDEX " + dbObj.getQualifiedName() + (_activate ? " ACTIVE" : " INACTIVE");
    }
}
