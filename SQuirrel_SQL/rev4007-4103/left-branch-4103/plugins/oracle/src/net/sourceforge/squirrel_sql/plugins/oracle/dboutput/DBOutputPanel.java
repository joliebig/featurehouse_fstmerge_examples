package net.sourceforge.squirrel_sql.plugins.oracle.dboutput;


import java.awt.BorderLayout;
import java.sql.SQLException;
import java.sql.CallableStatement;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class DBOutputPanel extends JPanel
{
   
   private static final ILogger s_log = LoggerController.createLogger(DBOutputPanel.class);

   
   private ISession _session;

   private JTextArea _textArea;
   private Timer _refreshTimer = new Timer(true);

   private boolean _autoRefresh = false;
   private int _refreshPeriod = 10;

   public class RefreshTimerTask extends TimerTask
   {
      public void run()
      {
         populateDBOutput();
      }
   }

   
   public DBOutputPanel(ISession session, int autoRefeshPeriod)
   {
      super();
      _session = session;
      _refreshPeriod = autoRefeshPeriod;
      createGUI();
      initDBOutput();
   }

   protected void initDBOutput()
   {
      try
      {
         CallableStatement c = _session.getSQLConnection().getConnection().prepareCall("{call dbms_output.enable()}");
         c.execute();
      }
      catch (SQLException ex)
      {
         _session.showErrorMessage(ex);
      }
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

   public void clearOutput()
   {
      Document doc = _textArea.getDocument();
      int length = doc.getLength();
      try
      {
         doc.remove(0, length);
      }
      catch (BadLocationException ex)
      {
         
      }
   }

   public synchronized void populateDBOutput()
   {
      try
      {
         final StringBuffer buf = new StringBuffer();
         
         
         
         
         CallableStatement c = _session.getSQLConnection().getConnection().prepareCall("{call dbms_output.get_line(?, ?)}");
         c.registerOutParameter(1, java.sql.Types.VARCHAR);
         c.registerOutParameter(2, java.sql.Types.INTEGER);
         
         int status = 0;
         while (status == 0)
         {
            c.execute();
            status = c.getInt(2);
            if (status == 0)
            {
               String str = c.getString(1);
               if (str != null)
                  buf.append(str);
               buf.append("\n");
            }
         }
         c.close();
         if (buf.length() > 0)
         {
            final JTextArea store = _textArea;
            SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  store.append(buf.toString());
               }
            });
         }
      }
      catch (SQLException ex)
      {
         _session.showErrorMessage(ex);
      }
   }

   private void createGUI()
   {
      final IApplication app = _session.getApplication();
      setLayout(new BorderLayout());
      _textArea = new JTextArea();
      _textArea.setEditable(false);
      add(new JScrollPane(_textArea,
         JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
	}

}
