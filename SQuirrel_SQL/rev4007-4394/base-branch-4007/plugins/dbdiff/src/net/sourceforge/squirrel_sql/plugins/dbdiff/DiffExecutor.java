package net.sourceforge.squirrel_sql.plugins.dbdiff;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbdiff.gui.ColumnDiffDialog;
import net.sourceforge.squirrel_sql.plugins.dbdiff.util.DBUtil;


public class DiffExecutor extends I18NBaseObject {

    
    private final static ILogger s_log = 
                         LoggerController.createLogger(DiffExecutor.class);
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DiffExecutor.class);
    
    static interface i18n {
        
        String NO_DIFFS_MESSAGE = 
            s_stringMgr.getString("DiffExecutor.noDiffsMessage");
    }
    
    
    SessionInfoProvider prov = null;
    
    
    ISession sourceSession = null;
    
    
    ISession destSession = null;
    
    
    private Thread execThread = null;
                
    
    private volatile boolean cancelled = false;    
    
    
    private long start = 0;
    
    
    private long end = 0;
    
    private List<ColumnDifference> colDifferences = 
        new ArrayList<ColumnDifference>();
    
    
    public DiffExecutor(SessionInfoProvider p) {
        prov = p;
        sourceSession = prov.getDiffSourceSession();
        destSession = prov.getDiffDestSession();
    }
    
    
    public void execute() {
       Runnable runnable = new Runnable() {
            public void run() {
                try {
                    _execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        execThread = new Thread(runnable);
        execThread.setName("DBDiff Executor Thread");
        execThread.start();
    }

    
    public void cancel() {
        cancelled = true;
        execThread.interrupt();        
    }
    
    
    private void _execute() throws SQLException {
        start = System.currentTimeMillis();
        boolean encounteredException = false;
        IDatabaseObjectInfo[] sourceObjs = 
            prov.getSourceSelectedDatabaseObjects();
        IDatabaseObjectInfo[] destObjs = 
            prov.getDestSelectedDatabaseObjects();

        if (!sanityCheck(sourceObjs, destObjs)) {
            return;
        }
        
        ISQLDatabaseMetaData sourceMetaData = 
            prov.getDiffSourceSession().getMetaData();
        ISQLDatabaseMetaData destMetaData = 
            prov.getDiffDestSession().getMetaData();

        
        
        Map<String, ITableInfo> tableMap1 = getTableMap(sourceMetaData, sourceObjs);
        Map<String, ITableInfo> tableMap2 = getTableMap(destMetaData, destObjs);
         
        Set<String> tableNames = getAllTableNames(tableMap1);
        tableNames.addAll(getAllTableNames(tableMap2));
        
        try {
            TableDiffExecutor diff = new TableDiffExecutor(sourceMetaData,
                                                           destMetaData);
            for (String table : tableNames) {
                if (tableMap1.containsKey(table)) {
                    if (tableMap2.containsKey(table)) {
                        ITableInfo t1 = tableMap1.get(table);
                        ITableInfo t2 = tableMap2.get(table);
                        diff.setTableInfos(t1, t2);
                        diff.execute();
                        List<ColumnDifference> columnDiffs = 
                            diff.getColumnDifferences();
                        if (columnDiffs != null && columnDiffs.size() > 0) {
                            colDifferences.addAll(columnDiffs);
                            for (ColumnDifference colDiff : columnDiffs) {
                                System.out.println(colDiff.toString());
                            }
                        }
                    } else { 
                        
                    }
                } else {
                    
                }
                    
            }
            final MainFrame frame = sourceSession.getApplication().getMainFrame();
            if (colDifferences != null && colDifferences.size() > 0) {
                GUIUtils.processOnSwingEventThread(new Runnable() {
                    public void run() {
                        ColumnDiffDialog dialog = new ColumnDiffDialog(frame, false);                 
                        dialog.setColumnDifferences(colDifferences);
                        dialog.setSession1Label(sourceSession.getAlias().getName());
                        dialog.setSession2Label(destSession.getAlias().getName());
                        dialog.setVisible(true);                        
                    }
                });
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        JOptionPane.showMessageDialog(frame, 
                                                      i18n.NO_DIFFS_MESSAGE, 
                                                      "DBDiff", 
                                                      JOptionPane.INFORMATION_MESSAGE);
                    }
                });      
            }
        } catch (SQLException e) {
            s_log.error("Encountered unexpected exception while executing " +
                        "diff: "+e.getMessage(), e);
        }
        
        if (encounteredException) {
            return;
        }         
        end = System.currentTimeMillis();
    }
    
    private Set<String> getAllTableNames(Map<String, ITableInfo> tables) {
        HashSet<String> result = new HashSet<String>();
        result.addAll(tables.keySet());
        return result;
    }
    
    private Map<String, ITableInfo> getTableMap(ISQLDatabaseMetaData md, 
                                                IDatabaseObjectInfo[] objs) 
        throws SQLException 
    {
        HashMap<String, ITableInfo> result = new HashMap<String, ITableInfo>();
        if (objs[0].getDatabaseObjectType() == DatabaseObjectType.TABLE) {
            for (int i = 0; i < objs.length; i++) {
                IDatabaseObjectInfo info = objs[i];
                result.put(info.getSimpleName(), (ITableInfo)info);
            }
        } else {
            
            String catalog = objs[0].getCatalogName();
            String schema = objs[0].getSchemaName();
            md.getTables(catalog, schema, null, new String[] { "TABLE" }, null);
        }
        return result;
    }
    
    
    public List<ColumnDifference> getColumnDifferences() {
        return colDifferences;
    }
            
    
    private boolean sanityCheck(IDatabaseObjectInfo[] sourceObjs, 
                                IDatabaseObjectInfo[] destObjs) 
    {
        boolean result = true;
        if (sourceObjs.length != destObjs.length) {
            result = false;
        }
        if (sourceObjs[0].getDatabaseObjectType() 
                != destObjs[0].getDatabaseObjectType()) 
        {
            result = false;
        }
        return result;
    }
    
    private int[] getTableCounts() {
        int[] result = null;
        
        ISession sourceSession = prov.getDiffSourceSession();
        IDatabaseObjectInfo[] dbObjs = prov.getSourceSelectedDatabaseObjects();
        if (dbObjs != null) {
            result = new int[dbObjs.length];
            for (int i = 0; i < dbObjs.length; i++) {
                if (false == dbObjs[i] instanceof ITableInfo) {
                    continue;
                }          
                try {
                    ITableInfo ti = (ITableInfo) dbObjs[i];
                    result[i] = 
                        DBUtil.getTableCount(sourceSession,
                                             ti.getCatalogName(),
                                             ti.getSchemaName(),
                                             ti.getSimpleName(),
                                             DialectFactory.SOURCE_TYPE);
                } catch (Exception e) {
                    s_log.error(
                        "Unexpected exception while attempting to get table counts",e);
                    result[i] = 0;
                }
            }           
        }
        return result;
    }
    
    
    private long getElapsedSeconds() {
        long result = 1;
        double elapsed = end - start;
        if (elapsed > 1000) {
            result = Math.round(elapsed / 1000);
        }
        return result;
    }
        
        
}
