package net.sourceforge.squirrel_sql.plugins.postgres.tab;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseDataSetTab;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class LockTab extends BaseDataSetTab {

    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(LockTab.class);
    
    static interface i18n {
        
        String HINT = s_stringMgr.getString("LockDetailsTab.hint");
        
        String TITLE = s_stringMgr.getString("LockDetailsTab.title");
    }

    
    private static final String QUERY = "SELECT "
        + "    pgl.relation::regclass AS \"Class\", "
        + "    pg_get_userbyid(pg_stat_get_backend_userid(svrid)) AS \"User\", "
        + "    pgl.transaction AS \"Transaction\", "
        + "    pg_stat_get_backend_pid(svrid) AS \"Pid\", "
        + "    pgl.mode AS \"Mode\", "
        + "    pgl.granted AS \"Granted\", "
        + "    translate(pg_stat_get_backend_activity(svrid),E'\n',' ') AS \"Query\", "
        + "    pg_stat_get_backend_activity_start(svrid) AS \"Running since\" "
        + "FROM "
        + "    pg_stat_get_backend_idset() svrid, pg_locks pgl, pg_database db "
        + "WHERE "
        + "    datname = current_database() AND "
        
        
        + "    db.oid = pgl.database "
        + "ORDER BY " + "    user,pid";;
    
    
	
    @Override
    protected IDataSet createDataSet() throws DataSetException {
        final ISession session = getSession();
        try
        {
            ISQLConnection con = session.getSQLConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(QUERY);
            ResultSetDataSet rsds = new ResultSetDataSet();
            rsds.setResultSet(rs, DialectType.POSTGRES);
            return rsds;
        }
        catch (SQLException ex)
        {
            throw new DataSetException(ex);
        }
    }

    
    public String getHint() {
        return i18n.HINT;
    }

    
    public String getTitle() {
        return i18n.TITLE;
    }

    
    


}
