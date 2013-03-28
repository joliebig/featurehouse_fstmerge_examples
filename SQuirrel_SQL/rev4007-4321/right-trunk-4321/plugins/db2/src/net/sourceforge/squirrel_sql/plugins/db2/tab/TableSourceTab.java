package net.sourceforge.squirrel_sql.plugins.db2.tab;

import static java.util.Arrays.asList;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.FormattedSourceTab;
import net.sourceforge.squirrel_sql.fw.dialects.CreateScriptPreferences;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UnknownDialectException;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class TableSourceTab extends FormattedSourceTab
{
	
	private static final String MQT_SQL =
	    "SELECT text " +
	    "FROM SYSCAT.VIEWS " +
	    "WHERE viewschema = ? " +
	    "and viewname = ? ";
	
	
	private static final String OS400_MQT_SQL = 
	    "select mqt_definition " +
	    "from qsys2.systables " +
	    "where table_schema = ? " +
	    "and table_name = ? ";
		
	
	private final static ILogger s_log =
		LoggerController.createLogger(ViewSourceTab.class);

    
    private boolean isOS400 = false;    
    
        
	public TableSourceTab(String hint, String stmtSep, boolean isOS400)
	{
		super(hint);
        super.setCompressWhitespace(true);
        super.setupFormatter(stmtSep, null); 
        this.isOS400 = isOS400;
	}

    
    @Override	
	protected PreparedStatement createStatement() throws SQLException
	{
		final ISession session = getSession();
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();
		
		    
		ISQLConnection conn = session.getSQLConnection();
        String sql = MQT_SQL;
        if (isOS400) {
            sql = OS400_MQT_SQL;
        }		
        if (!isMQT()) {
            sql = getTableSelectSql((ITableInfo)doi);

            
            super.appendSeparator = false;
        } else {
            
            super.appendSeparator = true;
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug("Running SQL for View source tab: "+sql);
            s_log.debug("schema="+doi.getSchemaName());
            s_log.debug("view name="+doi.getSimpleName());            
        }
		PreparedStatement pstmt = conn.prepareStatement(sql);
		if (isMQT()) { 
		    pstmt.setString(1, doi.getSchemaName());
		    pstmt.setString(2, doi.getSimpleName());
		}
		return pstmt;
	}
    
    private boolean isMQT() {
        final IDatabaseObjectInfo doi = getDatabaseObjectInfo();
        
        boolean isMQT = false;
        
        if (doi.getDatabaseObjectType() == DatabaseObjectType.TABLE) {
            TableInfo info = (TableInfo)doi;
            if (info.getType().startsWith("MATERIALIZED")) {
                isMQT = true;
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Table " + doi.getSimpleName()
                            + " appears to be an MQT");
                }
            } else {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Table " + doi.getSimpleName()
                        + " appears to be a regular table");
                }
            }
        }
        
        return isMQT;
    }
    
    private String getTableSelectSql(ITableInfo ti) {
        String sql = getRegularTableSelectSql(ti);
        if (sql == null) {
            sql = MQT_SQL;
        }
        return sql;
    }
    
    
    private String getRegularTableSelectSql(ITableInfo ti) {
        StringBuilder tmp = new StringBuilder();
        tmp.append("select '");
        
        ISQLDatabaseMetaData md = getSession().getMetaData();
        try {
            HibernateDialect dialect = DialectFactory.getDialect(md);
            List<ITableInfo> tableList = asList(new ITableInfo[] { ti });
            CreateScriptPreferences prefs = new CreateScriptPreferences();
            List<String> sqls = 
                dialect.getCreateTableSQL(tableList, md, prefs, false);
            for (String sql : sqls) {
                tmp.append(sql);
                tmp.append(statementSeparator);
                tmp.append("\n");
                tmp.append("\n");
            }
        } catch (UnknownDialectException e) {
            s_log.error("createStatement: Unable to determine the dialect "
                    + "to use");
            return null;
        } catch (SQLException e) {
            s_log.error("createStatement: Unexpected exception while "
                    + "constructing SQL for table(" + ti.getSimpleName()
                    + "): " + e.getMessage(), e);
            return null;
        }
        tmp.append("' from sysibm.sysdummy1");
        return tmp.toString();
    }
}
