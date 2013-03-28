package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;

import java.awt.BorderLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JTextArea;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.fw.codereformat.CommentSpec;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public abstract class FormattedSourceTab extends BaseSourceTab {

    
    private final static ILogger s_log = LoggerController
            .createLogger(FormattedSourceTab.class);

    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(FormattedSourceTab.class);
    
    
    private CodeReformator formatter = null;

    
    private boolean compressWhitespace = true;

    private CommentSpec[] commentSpecs =
        new CommentSpec[]
        {
            new CommentSpec("/*", "*/"),
            new CommentSpec("--", "\n")
        };
    
    
    protected String statementSeparator = null;
    
    
    protected boolean appendSeparator = true;
    
    static interface i18n {
        
        
        String NO_SOURCE_AVAILABLE = 
            s_stringMgr.getString("FormatterSourceTab.noSourceAvailable");
    }
    
    public FormattedSourceTab(String hint) {
        super(hint);
        super.setSourcePanel(new FormattedSourcePanel());
    }

    
    protected void setupFormatter(String stmtSep, CommentSpec[] commentSpecs) {
        if (commentSpecs != null) {
            this.commentSpecs = commentSpecs;
        }
        statementSeparator = stmtSep;
        formatter = new CodeReformator(stmtSep, this.commentSpecs);
    }

    
    protected void setCompressWhitespace(boolean compressWhitespace) {
        this.compressWhitespace = compressWhitespace;
    }

    
    private final class FormattedSourcePanel extends BaseSourcePanel {
        private static final long serialVersionUID = 1L;

        private JTextArea _ta;

        FormattedSourcePanel() {
            super(new BorderLayout());
            _ta = new JTextArea();
            _ta.setEditable(false);
            add(_ta, BorderLayout.CENTER);
        }

        public void load(ISession session, PreparedStatement stmt) {
            _ta.setText("");

            
            _ta.setWrapStyleWord(true);

            ResultSet rs = null;
            try {
                rs = stmt.executeQuery();
                StringBuffer buf = new StringBuffer(4096);
                while (rs.next()) {
                    String line = rs.getString(1);
                    if (line == null) {
                        s_log.debug("load: Null object source line; skipping...");
                        continue;
                    }
                    if (compressWhitespace) {
                        buf.append(line.trim() + " ");
                    } else {
                        buf.append(line);
                    }
                }
                if (appendSeparator) {
                    buf.append("\n");
                    buf.append(statementSeparator);
                }
                if (formatter != null && buf.length() != 0) {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Object source code before formatting: "
                                + buf.toString());
                    }
                    _ta.setText(format(buf.toString()));
                } else {
                    if (buf.length() == 0) {
                        buf.append(i18n.NO_SOURCE_AVAILABLE);
                    }
                    _ta.setText(buf.toString());
                }
                _ta.setCaretPosition(0);

            } catch (Exception ex) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Unexpected exception while formatting " +
                                "object source code", ex);
                }
                session.showErrorMessage(ex);
            } finally {
                SQLUtilities.closeResultSet(rs);
            }
        }
    }

    
    private String format(String toFormat) {
        String result = toFormat;
        try {
            result = formatter.reformat(toFormat);
        } catch (IllegalStateException e) {
            s_log.error("format: Formatting SQL failed: "+e.getMessage(), e);
        }
        return result;
    }
}
