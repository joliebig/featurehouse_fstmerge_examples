package net.sourceforge.squirrel_sql.plugins.oracle.dboutput;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.oracle.OracleInternalFrame;
import net.sourceforge.squirrel_sql.plugins.oracle.OracleInternalFrameCallback;

public class DBOutputInternalFrame extends OracleInternalFrame
{
   private static final String PREF_PART_DB_OUTPUT_FRAME = "DBOutputFrame";


   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(DBOutputInternalFrame.class);

   
   private final IApplication _app;

   
   private IIdentifier _sessionId;

   private DBOutputPanel _dbOutputPanel;
   
   private DBOutputToolBar _toolBar;

   private Resources _resources;

   public DBOutputInternalFrame(ISession session, Resources resources)
   {
      
      super(session, s_stringMgr.getString("oracle.dbOutputTitle", session.getTitle()));
      _app = session.getApplication();
      _resources = resources;
      _sessionId = session.getIdentifier();
      createGUI(session);
   }

   public DBOutputPanel getDBOutputPanel()
   {
      return _dbOutputPanel;
   }

   private void createGUI(ISession session)
   {
      addWidgetListener(new WidgetAdapter()
      {
         public void widgetClosing(WidgetEvent e)
         {
            onWidgetClosing();
         }
      });


      Icon icon = _resources.getIcon(getClass(), "frameIcon"); 
      if (icon != null)
      {
         setFrameIcon(icon);
      }


      OracleInternalFrameCallback cb = new OracleInternalFrameCallback()
      {

         public void createPanelAndToolBar(boolean stayOnTop, int autoRefeshPeriod)
         {
            _dbOutputPanel = new DBOutputPanel(getSession(), autoRefeshPeriod);
            _toolBar = new DBOutputToolBar(getSession(), stayOnTop, autoRefeshPeriod);
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.add(_toolBar, BorderLayout.NORTH);
            contentPanel.add(_dbOutputPanel, BorderLayout.CENTER);
            setContentPane(contentPanel);

            _dbOutputPanel.setAutoRefreshPeriod(autoRefeshPeriod);
         }
      };


      initFromPrefs(PREF_PART_DB_OUTPUT_FRAME, cb);
   }


   private void onWidgetClosing()
   {

      internalFrameClosing(_toolBar.isStayOnTop(), _dbOutputPanel.getAutoRefreshPeriod());

      
      _dbOutputPanel.setAutoRefresh(false);
   }

   
   private class DBOutputToolBar extends OracleToolBar
   {
      private JCheckBox _autoRefresh;

      DBOutputToolBar(ISession session, boolean stayOnTop, int autoRefeshPeriod)
      {
         super();
         createGUI(session, stayOnTop, autoRefeshPeriod);
      }

      private void createGUI(ISession session, boolean stayOnTop, int autoRefeshPeriod)
      {
         IApplication app = session.getApplication();
         setUseRolloverButtons(true);
         setFloatable(false);
         add(new GetDBOutputAction(app, _resources, _dbOutputPanel));
         add(new ClearDBOutputAction(app, _resources, _dbOutputPanel));

         addStayOnTop(stayOnTop);
         
         
         
         _autoRefresh = new JCheckBox(s_stringMgr.getString("oracle.dboutputEnableAutoRefer"), false);
         _autoRefresh.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               _dbOutputPanel.setAutoRefresh(_autoRefresh.isSelected());
            }
         });
         add(_autoRefresh);


         
         final SpinnerNumberModel model = new SpinnerNumberModel(autoRefeshPeriod, 1, 60, 5);
         JSpinner refreshRate = new JSpinner(model);
         refreshRate.addChangeListener(new ChangeListener()
         {
            public void stateChanged(ChangeEvent e)
            {
               _dbOutputPanel.setAutoRefreshPeriod(model.getNumber().intValue());
            }
         });
         add(refreshRate);
         
         add(new JLabel(s_stringMgr.getString("oracle.Seconds2")));
      }

   }
}
