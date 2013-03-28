
package net.sourceforge.squirrel_sql.plugins.informix.exception;

import java.lang.reflect.Method;
import java.sql.Connection;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.ISessionListener;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.DefaultExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class InformixExceptionFormatter extends SessionAdapter 
    implements ISessionListener, ExceptionFormatter {
    
    
    private ISQLEntryPanel sqlEntryPanel = null;
    
    
    private ISession _session = null;
    
    
    private static final DefaultExceptionFormatter defaultFormatter = 
        new DefaultExceptionFormatter();
    
    
    private static final ILogger s_log =
       LoggerController.createLogger(InformixExceptionFormatter.class); 
    
    
    private static final StringManager s_stringMgr =
       StringManagerFactory.getStringManager(InformixExceptionFormatter.class);
    
    static interface i18n {
        
        String NOT_AVAILABLE_MSG = 
            s_stringMgr.getString("InformixExceptionFormatter.notAvailableMsg");
        
        
        String positionLabel = 
            s_stringMgr.getString("InformixExceptionFormatter.positionLabel");
    }
    
    
    public InformixExceptionFormatter(ISession session) {
        this.sqlEntryPanel = 
            session.getSQLPanelAPIOfActiveSessionWindow().getSQLEntryPanel();
        this._session = session;
        session.getApplication().getSessionManager().addSessionListener(this);
    }
    
    
    public String format(Throwable t) throws Exception {
        StringBuilder msg = new StringBuilder();
        msg.append(defaultFormatter.format(t));
        ISQLConnection sqlcon = _session.getSQLConnection();
        if (sqlcon != null && sqlcon.getConnection() != null) {
            String offset = getSqlErrorOffset();
            msg.append("\n");
            msg.append(i18n.positionLabel);
            msg.append(offset);
            if (!i18n.NOT_AVAILABLE_MSG.equals(offset)) {
                int offsetNum = getNumber(offset, -1);
                if (offsetNum != -1) {
                    int[] bounds = sqlEntryPanel.getBoundsOfSQLToBeExecuted();
                    int start = bounds[0];
                    int newPosition = start + offsetNum - 1;
                    sqlEntryPanel.setCaretPosition(newPosition);
                }
            }   
        } else {
            msg.append(i18n.NOT_AVAILABLE_MSG);
        }
        return msg.toString();
    }
    
    
    public boolean formatsException(Throwable t) {
        return true;
    }

    
    private String getSqlErrorOffset() {
        String result = i18n.NOT_AVAILABLE_MSG;
        try {
            ISQLConnection sqlcon = _session.getSQLConnection();
            Class<?> conClass = sqlcon.getConnection().getClass();
            Connection ifmxcon = sqlcon.getConnection();

            Method getSQLStatementOffsetMethod = 
                conClass.getMethod("getSQLStatementOffset", (Class[])null);
            Object offset = 
                getSQLStatementOffsetMethod.invoke(ifmxcon, (Object[])null);
            result = offset.toString();
        } catch (Exception e) {
            s_log.error("getSqlErrorOffset: Unexpected exception - "
                    + e.getMessage(), e);
        }
        return result;
    }
    
    
    private int getNumber(String numberStr, int defaultNum) {
        int result = defaultNum;
        try {
            result = Integer.parseInt(numberStr);
        } catch (NumberFormatException e) {
            s_log.error("getNumber: Unexpected exception - "
                + e.getMessage(), e);
        }
        return result;
    }

    
    
    
    
    
    public void allSessionsClosed() {
        _session.getApplication().getSessionManager().removeSessionListener(this);
        _session = null;
    }

    
    public void sessionClosed(SessionEvent evt) {
        if (evt.getSession() == _session) {
            _session.getApplication().getSessionManager().removeSessionListener(this);
            _session = null;
        }
    }

    
    public void sessionClosing(SessionEvent evt) {
        if (evt.getSession() == _session) {
            _session.getApplication().getSessionManager().removeSessionListener(this);
            _session = null;
        }        
    }

}
