package net.sourceforge.squirrel_sql.plugins.oracle.SGAtrace;


import java.awt.BorderLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.oracle.OraclePlugin;
import net.sourceforge.squirrel_sql.plugins.oracle.common.AutoWidthResizeTable;

public class SGATracePanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(SGATracePanel.class);

	
   private static final String sgaTraceSQL =
      "  SELECT a.SQL_Text, " +
         "         a.First_Load_Time, " +
         "         b.username, " +
         "         a.Parse_Calls, " +
         "         a.Executions, " +
         "         a.Sorts, " +
         "         a.Disk_Reads, " +
         "         a.Buffer_Gets, " +
         "         a.Rows_Processed, " +
         "         DECODE ( a.Executions, " +
         "                  0, " +
         "                  'N/A', " +
         "                  ROUND ( a.Sorts / a.Executions, " +
         "                          3 ) ), " +
         "         DECODE ( a.Executions, " +
         "                  0, " +
         "                  'N/A', " +
         "                  ROUND ( a.Disk_Reads / a.Executions, " +
         "                          3 ) ), " +
         "         DECODE ( a.Executions, " +
         "                  0, " +
         "                  'N/A', " +
         "                  ROUND ( a.Buffer_Gets / a.Executions, " +
         "                          3 ) ), " +
         "         DECODE ( a.Executions, " +
         "                  0, " +
         "                  'N/A', " +
         "                  ROUND ( a.Rows_Processed / a.Executions, " +
         "                          3 ) ), " +
         "         DECODE ( a.Rows_Processed, " +
         "                  0, " +
         "                  'N/A', " +
         "                  ROUND ( a.Sorts / a.Rows_Processed, " +
         "                          3 ) ), " +
         "         DECODE ( a.Rows_Processed, " +
         "                  0, " +
         "                  'N/A', " +
         "                  ROUND ( a.Disk_Reads / a.Rows_Processed, " +
         "                          3 ) ) , " +
         "         DECODE ( a.Rows_Processed, " +
         "                  0, " +
         "                  'N/A', " +
         "                  ROUND ( a.Buffer_Gets / a.Rows_Processed, " +
         "                          3 ) ), " +
         "         a.Address || ':' || a.Hash_Value " +
         "    FROM v$sqlarea a, " +
         "         sys.all_users b " +
         "   WHERE a.parsing_user_id = b.user_id ";

   
   private ISession _session;

   private AutoWidthResizeTable _sgaTrace;
   private boolean hasResized = false;
   private Timer _refreshTimer = new Timer(true);

   private boolean _autoRefresh = false;
   private int _refreshPeriod = 10;

   public class RefreshTimerTask extends TimerTask
   {
      public void run()
      {
         populateSGATrace();
      }
   }

   
   public SGATracePanel(ISession session, int autoRefeshPeriod)
   {
      super();
      _session = session;
      _refreshPeriod = autoRefeshPeriod;
      createGUI();
   }

   
   public ISession getSession()
   {
      return _session;
   }

   private void resetTimer()
   {
      if (_refreshTimer != null)
      {
         _refreshTimer.cancel();
         
         _refreshTimer = null;
      }
      if (_autoRefresh && (_refreshPeriod > 0))
      {
         _refreshTimer = new Timer(true);
         _refreshTimer.scheduleAtFixedRate(new RefreshTimerTask(),
            _refreshPeriod * 1000,
            _refreshPeriod * 1000);
      }
   }

   public void setAutoRefresh(boolean enable)
   {
      if (enable != _autoRefresh)
      {
         _autoRefresh = enable;
         resetTimer();
      }
   }

   public boolean getAutoRefesh()
   {
      return _autoRefresh;
   }

   public void setAutoRefreshPeriod(int seconds)
   {
      if (_refreshPeriod != seconds)
      {
         _refreshPeriod = seconds;
         resetTimer();
      }
   }

   public int getAutoRefreshPeriod()
   {
      return _refreshPeriod;
   }

   protected DefaultTableModel createTableModel()
   {
      DefaultTableModel tm = new DefaultTableModel();
      
      tm.addColumn(s_stringMgr.getString("oracle.sqlText"));
      
      tm.addColumn(s_stringMgr.getString("oracle.firstLoadTime"));
      
      tm.addColumn(s_stringMgr.getString("oracle.parseSchema"));
      
      tm.addColumn(s_stringMgr.getString("oracle.parseCalla"));
      
      tm.addColumn(s_stringMgr.getString("oracle.execution"));
      
      tm.addColumn(s_stringMgr.getString("oracle.sorts"));
      
      tm.addColumn(s_stringMgr.getString("oracle.diskReads"));
      
      tm.addColumn(s_stringMgr.getString("oracle.bufferGets"));
      
      tm.addColumn(s_stringMgr.getString("oracle.rows"));
      
      tm.addColumn(s_stringMgr.getString("oracle.sortsPerExec"));
      
      tm.addColumn(s_stringMgr.getString("oracle.diskReadsPerExec"));
      
      tm.addColumn(s_stringMgr.getString("oracle.bufferPerExec"));
      
      tm.addColumn(s_stringMgr.getString("oracle.rowsPerExec"));
      
      tm.addColumn(s_stringMgr.getString("oracle.sortsPerExec"));
      
      tm.addColumn(s_stringMgr.getString("oracle.diskReadsPerRow"));
      
      tm.addColumn(s_stringMgr.getString("oracle.buffer.getsPerRow"));
      return tm;
   }

   public synchronized void populateSGATrace()
   {
      if (!OraclePlugin.checkObjectAccessible(_session, sgaTraceSQL))
      {
         return;
      }
      PreparedStatement s = null;
      ResultSet rs = null;
      try
      {
         s = _session.getSQLConnection().getConnection().prepareStatement(sgaTraceSQL);
         if (s.execute())
         {
            rs = s.getResultSet();
            DefaultTableModel tm = createTableModel();
            while (rs.next())
            {
               String sqlText = rs.getString(1);
               String flt = rs.getString(2);
               String schema = rs.getString(3);
               String calls = rs.getString(4);
               String executions = rs.getString(5);
               String sorts = rs.getString(6);
               String diskReads = rs.getString(7);
               String bufGets = rs.getString(8);
               String rows = rs.getString(9);
               String sortsExec = rs.getString(10);
               String diskReadsExec = rs.getString(11);
               String rowsExec = rs.getString(12);
               String sortsRows = rs.getString(13);
               String diskReadsRow = rs.getString(14);
               String bufGetsRow = rs.getString(15);

               
               tm.addRow(new Object[]{sqlText, flt, schema, calls, executions,
                  sorts, diskReads, bufGets, rows, sortsExec,
                  diskReadsExec, rowsExec, sortsRows, diskReadsRow,
                  bufGetsRow});
            }
            _sgaTrace.setModel(tm);
            if (!hasResized)
            {
               hasResized = true;
               _sgaTrace.resizeColumnWidth(300);
            }
         }
      }
      catch (SQLException ex)
      {
         _session.showErrorMessage(ex);
      } finally {
      	SQLUtilities.closeResultSet(rs);
      	SQLUtilities.closeStatement(s);
      }
      
   }

   private void createGUI()
   {
      setLayout(new BorderLayout());
      _sgaTrace = new AutoWidthResizeTable(new DefaultTableModel());
      _sgaTrace.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      add(new JScrollPane(_sgaTrace));

      populateSGATrace();
	}

}
