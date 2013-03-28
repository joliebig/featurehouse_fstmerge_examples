package net.sourceforge.squirrel_sql.plugins.oracle.invalidobjects;


import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.oracle.common.AutoWidthResizeTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InvalidObjectsPanel extends JPanel
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(InvalidObjectsPanel.class);


   
   private static final ILogger s_log = LoggerController.createLogger(InvalidObjectsPanel.class);

   
   private ISession _session;

   private AutoWidthResizeTable _invalidObjects;
   private boolean hasResized = false;


   private static final String invalidObjectSQL = "SELECT owner, " +
      "object_name, " +
      "object_type " +
      "FROM sys.all_objects " +
      "WHERE status = 'INVALID'";

   
   public InvalidObjectsPanel(ISession session)
   {
      super();
      _session = session;
      createGUI();
   }

   
   public ISession getSession()
   {
      return _session;
   }

   protected DefaultTableModel createTableModel()
   {
      DefaultTableModel tm = new DefaultTableModel()
      {
         public boolean isCellEditable(int row, int column)
         {
            return false;
         }
      };

      
      tm.addColumn(s_stringMgr.getString("oracle.owner"));
      
      tm.addColumn(s_stringMgr.getString("oracle.objectName"));
      
      tm.addColumn(s_stringMgr.getString("oracle.objectType"));
      return tm;
   }

   public synchronized void repopulateInvalidObjects()
   {
      try
      {
         PreparedStatement s = _session.getSQLConnection().getConnection().prepareStatement(invalidObjectSQL);
         if (s.execute())
         {
            ResultSet rs = s.getResultSet();
            DefaultTableModel tm = createTableModel();
            while (rs.next())
            {
               String owner = rs.getString(1);
               String object_name = rs.getString(2);
               String object_type = rs.getString(3);
               
               tm.addRow(new Object[]{owner, object_name, object_type});
            }
            _invalidObjects.setModel(tm);
            if (!hasResized)
            {
               
               hasResized = true;
               _invalidObjects.resizeColumnWidth(300);
            }
         }
      }
      catch (SQLException ex)
      {
         _session.showErrorMessage(ex);
      }
   }

   private void createGUI()
   {
      setLayout(new BorderLayout());
      _invalidObjects = new AutoWidthResizeTable();
      _invalidObjects.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      add(new JScrollPane(_invalidObjects));

      repopulateInvalidObjects();
	}

}
