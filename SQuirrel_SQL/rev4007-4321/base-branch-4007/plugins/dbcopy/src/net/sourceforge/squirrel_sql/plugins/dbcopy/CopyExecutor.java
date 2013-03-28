
package net.sourceforge.squirrel_sql.plugins.dbcopy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectUtils;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.PrimaryKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.AnalysisEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.ErrorEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.RecordEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.StatementEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.TableEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.DBCopyPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.PreferencesManager;
import net.sourceforge.squirrel_sql.plugins.dbcopy.util.DBUtil;

import org.hibernate.MappingException;


public class CopyExecutor extends I18NBaseObject {

    
    SessionInfoProvider prov = null;
    
    
    ISession sourceSession = null;
    
    
    ISession destSession = null;
    
    
    private Thread execThread = null;
    
     
    private boolean originalAutoCommitValue = true;
    
    
    private boolean currentAutoCommitValue = true;    
    
    
    private static DBCopyPreferenceBean prefs = 
                                            PreferencesManager.getPreferences();    
    
    
    private final static ILogger log = 
                         LoggerController.createLogger(CopyExecutor.class);
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(CopyExecutor.class);
    
    
    private ArrayList<ITableInfo> selectedTableInfos = null;    
    
    
    private ArrayList<CopyTableListener> listeners = 
        new ArrayList<CopyTableListener>();
    
    
    private volatile boolean cancelled = false;    
    
    
    private UICallbacks pref = null;
    
    
    private long start = 0;
    
    
    private long end = 0;
    
    
    public CopyExecutor(SessionInfoProvider p) {
        prov = p;
        sourceSession = prov.getCopySourceSession();
        destSession = prov.getCopyDestSession();
    }
    
    
    public void execute() {
        Runnable runnable = new Runnable() {
            public void run() {
                _execute();
            }
        };
        execThread = new Thread(runnable);
        execThread.setName("DBCopy Executor Thread");
        execThread.start();
    }

    
    public void cancel() {
        cancelled = true;
        execThread.interrupt();        
    }
    
    
    private void _execute() {
        start = System.currentTimeMillis();
        boolean encounteredException = false;
        ISQLConnection destConn = destSession.getSQLConnection();
        if (!analyzeTables()) {
            return;
        }
        setupAutoCommit(destConn);
        IDatabaseObjectInfo[] sourceObjs = prov.getSourceSelectedDatabaseObjects();
        int[] counts = getTableCounts();
        sendCopyStarted(counts);
        String destSchema = prov.getDestSelectedDatabaseObject().getSimpleName();
        String destCatalog = prov.getDestSelectedDatabaseObject().getCatalogName();
        for (int i = 0; i < sourceObjs.length; i++) {
            if (false == sourceObjs[i] instanceof ITableInfo) {
                continue;
            }
            ITableInfo sourceTI = (ITableInfo)sourceObjs[i];
            sendTableCopyStarted(sourceTI, i+1);
            try {
                int destTableCount = DBUtil.getTableCount(destSession,
                                                          destCatalog,
                                                          destSchema, 
                                                          sourceTI.getSimpleName(),
                                                          DialectFactory.DEST_TYPE);
                if (destTableCount == -1) {
                    createTable(sourceTI);
                } 
                if (destTableCount > 0) {
                    try {
                        String t = sourceTI.getSimpleName();
                        if (pref.appendRecordsToExisting(t)) {
                            
                        } else if (pref.deleteTableData(sourceTI.getSimpleName())) {
                            
                            DBUtil.deleteDataInExistingTable(destSession,
                                                             destCatalog,
                                                             destSchema,
                                                             sourceTI.getSimpleName());
                        } else {
                            continue; 
                        }
                        
                    } catch (UserCancelledOperationException e) {
                        cancelled = true;
                        break;
                    }
                } 
                
                copyTable(sourceTI, counts[i]);
                
                if (i == sourceObjs.length - 1 && !cancelled) {
                    
                    
                    
                    
                    
                    
                    copyConstraints(sourceObjs);
                }
                if (!cancelled) {
                    sendTableCopyFinished(sourceTI, i+1);
                    sleep(prefs.getTableDelayMillis());
                }
            } catch (SQLException e) {
                encounteredException = true;
                sendErrorEvent(ErrorEvent.SQL_EXCEPTION_TYPE, e);
                break;
            } catch (MappingException e) {
                encounteredException = true;
                sendErrorEvent(ErrorEvent.MAPPING_EXCEPTION_TYPE, e);
                break;
            } catch (UserCancelledOperationException e) {
                cancelled = true;
                break;
            } catch (Exception e) {
                encounteredException = true;
                sendErrorEvent(ErrorEvent.GENERIC_EXCEPTION, e);
                break;
            }
        }        
        restoreAutoCommit(destConn);
        if (cancelled) {
            sendErrorEvent(ErrorEvent.USER_CANCELLED_EXCEPTION_TYPE);
            return;
        }
        if (encounteredException) {
            return;
        }         
        end = System.currentTimeMillis();
        
        ISession session = prov.getCopyDestSession();
        session.getSchemaInfo().reload(prov.getDestSelectedDatabaseObject());
        session.getSchemaInfo().fireSchemaInfoUpdate();

        notifyCopyFinished();
    }
    
    
    public void addListener(CopyTableListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }
        listeners.add(listener);
    }
    
    
    private void sleep(long sleepTime) {
        boolean shouldSleep = prefs.isDelayBetweenObjects();
        if (!shouldSleep || sleepTime <= 0) {
            return;
        }
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            
        }
    }
    
    
    private boolean analyzeTables() {        
        boolean result = true;
        if (!prefs.isTestColumnNames()) {
            return true;
        }
        if (DBUtil.sameDatabaseType(prov.getCopySourceSession(), 
                                    prov.getCopyDestSession()))
        {
            
            
            return true;
        }
        sendAnalysisStarted();
        try {
            IDatabaseObjectInfo[] dbObjs = prov.getSourceSelectedDatabaseObjects();        
            for (int tableIdx = 0; tableIdx < dbObjs.length; tableIdx++) {
                ITableInfo ti = (ITableInfo)dbObjs[tableIdx];
                sendAnalyzingTable(ti, tableIdx);
                DBUtil.validateColumnNames(ti, prov);
            }
        } catch (MappingException e) {
            sendErrorEvent(ErrorEvent.MAPPING_EXCEPTION_TYPE, e);
            result = false;
        } catch (UserCancelledOperationException e) {
            sendErrorEvent(ErrorEvent.USER_CANCELLED_EXCEPTION_TYPE, e);
            result = false;
        }
        return result;
    }        

    
    
    private void setupAutoCommit(ISQLConnection con) {
        boolean autoCommitPref = prefs.isAutoCommitEnabled();
        try {
            originalAutoCommitValue = con.getAutoCommit();
            currentAutoCommitValue = originalAutoCommitValue;
            if (autoCommitPref != originalAutoCommitValue) {
                con.setAutoCommit(autoCommitPref);
                currentAutoCommitValue = autoCommitPref;
            }
        } catch (SQLException e) {
            
            currentAutoCommitValue = true;
            sendErrorEvent(ErrorEvent.SETUP_AUTO_COMMIT_TYPE, e);
        }
    
    }
    
    
    private void restoreAutoCommit(ISQLConnection con) {
        if (originalAutoCommitValue == currentAutoCommitValue) {
            return;
        }
        try {
            con.setAutoCommit(originalAutoCommitValue);
        } catch (SQLException e) {
            sendErrorEvent(ErrorEvent.RESTORE_AUTO_COMMIT_TYPE, e);                      
        }
    }    
    
    private int[] getTableCounts() {
        int[] result = null;
        
        ISession sourceSession = prov.getCopySourceSession();
        IDatabaseObjectInfo[] dbObjs = prov.getSourceSelectedDatabaseObjects();
        if (dbObjs != null) {
            result = new int[dbObjs.length];
            selectedTableInfos = new ArrayList<ITableInfo>();
            for (int i = 0; i < dbObjs.length; i++) {
                if (false == dbObjs[i] instanceof ITableInfo) {
                    continue;
                }          
                try {
                    ITableInfo ti = (ITableInfo) dbObjs[i];
                    selectedTableInfos.add(ti);
                    
                    
                    result[i] = 
                        DBUtil.getTableCount(sourceSession,
                                             ti.getCatalogName(),
                                             ti.getSchemaName(),
                                             ti.getSimpleName(),
                                             DialectFactory.SOURCE_TYPE);
                } catch (Exception e) {
                    log.error("",e);
                    result[i] = 0;
                }
            }           
        }
        return result;
    }
    
    private void sendAnalysisStarted() {
        AnalysisEvent event = new AnalysisEvent(prov);
        Iterator<CopyTableListener> i = listeners.iterator();
        while (i.hasNext()) {
            CopyTableListener listener = i.next();
            listener.tableAnalysisStarted(event);
        }
    }
    
    private void sendAnalyzingTable(ITableInfo ti, int number) {
        TableEvent event = new TableEvent(prov);
        event.setTableCount(prov.getSourceSelectedDatabaseObjects().length);
        event.setTableNumber(number);
        Iterator<CopyTableListener> i = listeners.iterator();
        event.setTableName(ti.getSimpleName());
        while (i.hasNext()) {
            CopyTableListener listener = i.next();
            listener.analyzingTable(event);
        }                
    }
    
    private void sendCopyStarted(int[] tableCounts) {
        CopyEvent event = new CopyEvent(prov);
        event.setTableCounts(tableCounts);
        Iterator<CopyTableListener> i = listeners.iterator();
        while (i.hasNext()) {
            CopyTableListener listener = i.next();
            listener.copyStarted(event);
        }        
    }
    
    private void sendTableCopyStarted(ITableInfo ti, int number) {
        TableEvent event = new TableEvent(prov);
        event.setTableNumber(number);
        event.setTableCount(prov.getSourceSelectedDatabaseObjects().length);
        event.setTableName(ti.getSimpleName());
        Iterator<CopyTableListener> i = listeners.iterator();
        while (i.hasNext()) {
            CopyTableListener listener = i.next();
            listener.tableCopyStarted(event);
        }
    }

    private void sendTableCopyFinished(ITableInfo ti, int number) {
        TableEvent event = new TableEvent(prov);
        event.setTableNumber(number);
        event.setTableCount(prov.getSourceSelectedDatabaseObjects().length);
        event.setTableName(ti.getSimpleName());
        Iterator<CopyTableListener> i = listeners.iterator();
        while (i.hasNext()) {
            CopyTableListener listener = i.next();
            listener.tableCopyFinished(event);
        }
    }    
    
    
    private void sendErrorEvent(int type) {
        sendErrorEvent(type, null);
    }

        
    private void sendErrorEvent(int type, Exception e) {
        ErrorEvent event = new ErrorEvent(prov, type);
        event.setException(e);
        Iterator<CopyTableListener> i = listeners.iterator();
        while (i.hasNext()) {
            CopyTableListener listener = i.next();
            listener.handleError(event);
        }        
    }
    
    private void sendRecordEvent(int number, int count) {
        RecordEvent event = new RecordEvent(prov, number, count);
        Iterator<CopyTableListener> i = listeners.iterator();
        while (i.hasNext()) {
            CopyTableListener listener = i.next();
            listener.recordCopied(event);
        }
    }
    
    private void sendStatementEvent(String sql, String[] vals) {
        StatementEvent event = 
            new StatementEvent(sql, StatementEvent.INSERT_RECORD_TYPE);
        event.setBindValues(vals);
        Iterator<CopyTableListener> i = listeners.iterator();
        while (i.hasNext()) {
            CopyTableListener listener = i.next();
            listener.statementExecuted(event);
        }        
    }
    
    private void notifyCopyFinished() {
        int seconds = (int)getElapsedSeconds();
        Iterator<CopyTableListener> i = listeners.iterator();
        while (i.hasNext()) {
            CopyTableListener listener = i.next();
            listener.copyFinished(seconds);
        }
    }
    
    
    private long getElapsedSeconds() {
        long result = 1;
        double elapsed = end - start;
        if (elapsed > 1000) {
            result = Math.round(elapsed / 1000);
        }
        return result;
    }
    
    
    private void copyTable(ITableInfo sourceTableInfo, int sourceTableCount) 
        throws MappingException, SQLException, UserCancelledOperationException
    {
        PreparedStatement insertStmt = null;
        ResultSet rs = null;
        if (cancelled) {
            return;
        }
        if (!PreferencesManager.getPreferences().isCopyData()) {
            return;
        }
        ISQLConnection sourceConn = prov.getCopySourceSession().getSQLConnection();
        ISQLConnection destConn = prov.getCopyDestSession().getSQLConnection();
        SQLDatabaseMetaData sourceMetaData = sourceConn.getSQLMetaData();
        SQLDatabaseMetaData destMetaData = destConn.getSQLMetaData();
        try {
            String destSchema = 
                prov.getDestSelectedDatabaseObject().getSimpleName();            
            ITableInfo destTableInfo = 
                DBUtil.getTableInfo(prov.getCopyDestSession(),
                                    destSchema,
                                    sourceTableInfo.getSimpleName());
            
            TableColumnInfo[] sourceInfos = sourceMetaData.getColumnInfo(sourceTableInfo);
            TableColumnInfo[] destInfos = destMetaData.getColumnInfo(destTableInfo);
            
            destInfos = sort(sourceInfos, 
                             destInfos, 
                             sourceTableInfo.getQualifiedName(),
                             destTableInfo.getQualifiedName());
            
            String sourceColList = DBUtil.getColumnList(sourceInfos);
            String destColList = DBUtil.getColumnList(destInfos);
            
            String selectSQL = DBUtil.getSelectQuery(prov,
                                                     sourceColList, 
                                                     sourceTableInfo);
            String insertSQL = DBUtil.getInsertSQL(prov, destColList, 
                                                   sourceTableInfo, 
                                                   destInfos.length);
            insertStmt = destConn.prepareStatement(insertSQL);
            
            int count = 1;
            int commitCount = prefs.getCommitCount(); 
            int columnCount = destInfos.length;
            String[] bindVarVals = new String[columnCount];
                        
            boolean foundLOBType = false;
            
            DBUtil.setLastStatement(selectSQL);
            rs = DBUtil.executeQuery(prov.getCopySourceSession(), selectSQL);
            DBUtil.setLastStatement(insertSQL);
            boolean isMysql = DialectFactory.isMySQL(destSession.getMetaData());
            boolean isSourceOracle = 
                DialectFactory.isOracle(sourceSession.getMetaData());
            boolean isDestOracle = DialectFactory.isOracle(destSession.getMetaData());
            while (rs.next() && !cancelled) {
                
                
                
                if (isMysql && foundLOBType) 
                {
                    insertStmt.clearParameters();
                }
                StringBuilder lastStmtValuesBuffer = new StringBuilder();
                lastStmtValuesBuffer.append("\n(Bind variable values: ");
                for (int i = 0; i < columnCount; i++) {

                    int sourceColType = sourceInfos[i].getDataType();
                    
                    
                    sourceColType = DBUtil.replaceOtherDataType(sourceInfos[i]);
                    sourceColType = getDateReplacement(sourceColType, 
                                                       isSourceOracle);
                    
                    int destColType   = destInfos[i].getDataType();
                    
                    
                    destColType = DBUtil.replaceOtherDataType(destInfos[i]);
                    destColType = getDateReplacement(destColType, isDestOracle);
                    
                    
                    String bindVal = DBUtil.bindVariable(insertStmt,
                                                         sourceColType,
                                                         destColType,
                                                         i+1,
                                                         rs);
                    bindVarVals[i] = bindVal;
                    lastStmtValuesBuffer.append(bindVal);
                    if (i + 1 < columnCount) {
                        lastStmtValuesBuffer.append(", ");
                    }
                    if (isLOBType(destColType)) {
                    	foundLOBType = true;
                    }
                }                
                lastStmtValuesBuffer.append(")");
                DBUtil.setLastStatementValues(lastStmtValuesBuffer.toString());
                sendStatementEvent(insertSQL, bindVarVals);
                insertStmt.executeUpdate();
                sendRecordEvent(count, sourceTableCount);
                count++;
                if (!currentAutoCommitValue) {
                    if ((count % commitCount) == 0) {
                        commitConnection(destConn);
                    }
                }
                sleep(prefs.getRecordDelayMillis());
            }
        } finally {
            SQLUtilities.closeResultSet(rs);
            SQLUtilities.closeStatement(insertStmt);
            if (!currentAutoCommitValue) {
                commitConnection(destConn);
            }
        }
    }
    
    
    private int getDateReplacement(int type, boolean isOracle) 
    {
        int result = type;
        if (isOracle && type == java.sql.Types.DATE) {
            result = java.sql.Types.TIMESTAMP;
        }
        return result;
    }
    
    
    private boolean isLOBType(int columnType) {
        if (columnType == Types.BLOB 
        		|| columnType == Types.CLOB
                || columnType == Types.LONGVARBINARY
                || columnType == Types.BINARY) 
        {
            return true;
        }
        return false;
    }
    
    
    private TableColumnInfo[] sort(TableColumnInfo[] sourceInfos, 
                                   TableColumnInfo[] destInfos,
                                   String sourceTableName,
                                   String destTableName)
        throws MappingException 
    {
        if (sourceInfos.length != destInfos.length) {
            
            
            
            String msg = 
                s_stringMgr.getString("CopyExecutor.tablecolmismatch",
                                      new Object[] {
                                              sourceTableName,
                                              Integer.valueOf(sourceInfos.length),
                                              destTableName,
                                              Integer.valueOf(destInfos.length)});
            throw new MappingException(msg);
        }
        ArrayList<TableColumnInfo> result = new ArrayList<TableColumnInfo>();
        
        for (int sourceIdx = 0; sourceIdx < sourceInfos.length; sourceIdx++) {
            TableColumnInfo sourceInfo = sourceInfos[sourceIdx];
            
            String sourceColumnName = sourceInfo.getColumnName().trim();
            boolean found = false;
            int destIdx = 0;
            while (!found && destIdx < destInfos.length) {
                TableColumnInfo destInfo = destInfos[destIdx];
                
                String destColumnName = destInfo.getColumnName().trim();
                if (destColumnName.equalsIgnoreCase(sourceColumnName)) {
                    result.add(destInfo);
                    found = true;
                }
                destIdx++;
            }
            if (!found) {
                throw new MappingException("Destination table "+destTableName+
                                    " doesn't appear to have a column named "+
                                    sourceInfo.getColumnName());
            }
        }
        return result.toArray(new TableColumnInfo[destInfos.length]);
    }
    
    
    private void commitConnection(ISQLConnection connection) {
        try {
            connection.commit();
        } catch (SQLException e) {
            log.error("Failed to commit connection - "+connection, e);
        }
    }
    
    
    private void copyConstraints(IDatabaseObjectInfo[] dbObjs) 
        throws SQLException, UserCancelledOperationException 
    {
        if (!prefs.isCopyForeignKeys() 
        		|| DialectFactory.isAxion(prov.getCopySourceSession().getMetaData())) {
            return;
        }
        ISQLConnection destConn = prov.getCopyDestSession().getSQLConnection();
        for (int i = 0; i < dbObjs.length; i++) {
            ITableInfo ti = (ITableInfo) dbObjs[i];
            Set<String> fkStmts = 
                DBUtil.getForeignKeySQL(prov, ti, selectedTableInfos);
            Iterator<String> it = fkStmts.iterator();
            while (it.hasNext()) {
                String fkSQL = it.next();
                DBUtil.setLastStatementValues("");
                try {
                    DBUtil.executeUpdate(destConn, fkSQL, true);
                } catch (SQLException e) {
                    log.error("Unexpected exception while attempting to " +
                              "create FK constraint using sql = "+fkSQL, e);
                }
            }           
        }
    }    
    
    private void createTable(ITableInfo ti) 
        throws SQLException, UserCancelledOperationException, MappingException
    {
        if (cancelled) {
            return;
        }
        ISQLConnection destCon = prov.getCopyDestSession().getSQLConnection();
        String createTableSql = DBUtil.getCreateTableSql(prov, ti);
        DBUtil.executeUpdate(destCon, createTableSql, true);
        
        if (prefs.isCommitAfterTableDefs() && !currentAutoCommitValue) {
            commitConnection(destCon);
        }
        
        if (prefs.isCopyIndexDefs()) {
            Collection<String> indices = null;
            ISQLDatabaseMetaData sqlmd = sourceSession.getMetaData();
            if (prefs.isCopyPrimaryKeys()) {
                PrimaryKeyInfo[] pkList = sqlmd.getPrimaryKey(ti);
                List<PrimaryKeyInfo> pkList2 = Arrays.asList(pkList);
                indices = DialectUtils.createIndexes(ti, sqlmd, pkList2);
            } else {
                indices = DialectUtils.createIndexes(ti, sqlmd, null);
            }
            Iterator<String> i = indices.iterator();
            while (i.hasNext()) {
                String createIndicesSql = i.next();
                DBUtil.executeUpdate(destCon, createIndicesSql, true);
            }
        }
    }

    
    public void setPref(UICallbacks pref) {
        this.pref = pref;
    }

    
    public UICallbacks getPref() {
        return pref;
    }    
    
}
