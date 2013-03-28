package net.sourceforge.squirrel_sql.plugins.oracle.tab;

import java.awt.BorderLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JTextArea;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourcePanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab;
import net.sourceforge.squirrel_sql.fw.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.fw.codereformat.CommentSpec;
import net.sourceforge.squirrel_sql.fw.dialects.CreateScriptPreferences;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public abstract class OracleSourceTab extends BaseSourceTab {

    public static final int VIEW_TYPE = 0;
    public static final int STORED_PROC_TYPE = 1;
    public static final int TRIGGER_TYPE = 2;
    public static final int TABLE_TYPE = 3;
    
    protected int sourceType = VIEW_TYPE;

    
    private final static ILogger s_log =
        LoggerController.createLogger(OracleSourceTab.class);

    private static CommentSpec[] commentSpecs =
          new CommentSpec[]
          {
              new CommentSpec("/*", "*/"),
              new CommentSpec("--", "\n")
          };
    
    private static CodeReformator formatter = 
        new CodeReformator(";", commentSpecs);
    
    public OracleSourceTab(String hint)
    {
        super(hint);
        super.setSourcePanel(new OracleSourcePanel());
    }

    private final class OracleSourcePanel extends BaseSourcePanel
    {
        private static final long serialVersionUID = 7855991042669454322L;

        private JTextArea _ta;

        OracleSourcePanel()
        {
            super(new BorderLayout());
            createUserInterface();
        }

        public void load(ISession session, PreparedStatement stmt)
        {
            _ta.setText("");
            _ta.setWrapStyleWord(true);
            ResultSet rs = null;
            try
            {
                rs = stmt.executeQuery();
                StringBuffer buf = new StringBuffer(4096);
                while (rs.next())
                {
                    String line1 = rs.getString(1);
                    String line2 = rs.getString(2);
                    buf.append(line1.trim() + " ");
                    buf.append(line2.trim() + " ");
                }
                String source = "";
                if (buf.length() == 0 && sourceType == TABLE_TYPE) {
                    ISQLDatabaseMetaData md = session.getMetaData();
                    
                    
                    
                    
                    HibernateDialect dialect = DialectFactory.getDialect("Oracle");
                    
                    
                    CreateScriptPreferences prefs = new CreateScriptPreferences();
                    
                    ITableInfo[] tabs = new ITableInfo[] { (ITableInfo)getDatabaseObjectInfo() };
                    List<ITableInfo> tables = Arrays.asList(tabs);
                    
                    List<String> sqls = dialect.getCreateTableSQL(tables, md, prefs, false);
                    String sep = session.getQueryTokenizer().getSQLStatementSeparator();
                    for (String sql : sqls) {
                        buf.append(sql);
                        buf.append(sep);
                        buf.append("\n");
                    }
                    source = buf.toString();
                } else {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("View source before formatting: "+
                                    buf.toString());
                    }
                    source = formatter.reformat(buf.toString());
                }
                _ta.setText(source);
                _ta.setCaretPosition(0);
            }
            catch (SQLException ex)
            {
                s_log.error("Unexpected exception: "+ex.getMessage(), ex);
                session.showErrorMessage(ex);
            } finally {
            	SQLUtilities.closeResultSet(rs);
            }

        }

        private void createUserInterface()
        {
            _ta = new JTextArea();
            _ta.setEditable(false);
            add(_ta, BorderLayout.CENTER);
        }
    }

}
