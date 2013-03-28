package net.sourceforge.squirrel_sql.plugins.oracle.sessioninfo;


import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.BaseSessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.oracle.OracleInternalFrame;
import net.sourceforge.squirrel_sql.plugins.oracle.OracleInternalFrameCallback;
import net.sourceforge.squirrel_sql.plugins.oracle.dboutput.DBOutputPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SessionInfoInternalFrame extends OracleInternalFrame
{

   private static final String PREF_PART_INFO_FRAME = "InfoFrame";


   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(SessionInfoInternalFrame.class);



   private SessionInfoPanel _sessionInfoPanel;
   
   private SessionInfoToolBar _toolBar;

   private Resources _resources;

   public SessionInfoInternalFrame(ISession session, Resources resources)
   {
      
      super(session, s_stringMgr.getString("oracle.infoTitle", session.getTitle()));
      _resources = resources;
      createGUI(session);
   }

   public SessionInfoPanel getDBOutputPanel()
   {
      return _sessionInfoPanel;
   }

   private void createGUI(ISession session)
   {

      addInternalFrameListener(new InternalFrameAdapter()
      {
         public void internalFrameClosing(InternalFrameEvent e)
         {
            SessionInfoInternalFrame.super.internalFrameClosing(_toolBar.isStayOnTop(), _sessionInfoPanel.getAutoRefreshPeriod());
            _sessionInfoPanel.setAutoRefresh(false);
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
            _sessionInfoPanel = new SessionInfoPanel(getSession(), autoRefeshPeriod);
            _toolBar = new SessionInfoToolBar(getSession(), stayOnTop, autoRefeshPeriod);
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.add(_toolBar, BorderLayout.NORTH);
            contentPanel.add(_sessionInfoPanel, BorderLayout.CENTER);
            setContentPane(contentPanel);

            _sessionInfoPanel.setAutoRefreshPeriod(autoRefeshPeriod);
         }
      };

      initFromPrefs(PREF_PART_INFO_FRAME, cb);
   }

   
   private class SessionInfoToolBar extends OracleToolBar
   {
      SessionInfoToolBar(ISession session, boolean stayOnTop, int autoRefeshPeriod)
      {
         super();
         createGUI(session, stayOnTop, autoRefeshPeriod);
      }

      private void createGUI(ISession session, boolean stayOnTop, int autoRefeshPeriod)
      {
         IApplication app = session.getApplication();
         setUseRolloverButtons(true);
         setFloatable(false);
         add(new GetSessionInfoAction(app, _resources, _sessionInfoPanel));

         addStayOnTop(stayOnTop);

         
         
         final JCheckBox autoRefresh = new JCheckBox(s_stringMgr.getString("oracle.auotRefresh2"), false);
         autoRefresh.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               _sessionInfoPanel.setAutoRefresh(autoRefresh.isSelected());
            }
         });
         add(autoRefresh);

         
         final SpinnerNumberModel model = new SpinnerNumberModel(autoRefeshPeriod, 1, 60, 5);
         final JSpinner refreshRate = new JSpinner(model);
         refreshRate.addChangeListener(new ChangeListener()
         {
            public void stateChanged(ChangeEvent e)
            {
               _sessionInfoPanel.setAutoRefreshPeriod(model.getNumber().intValue());
            }
         });
         add(refreshRate);
         
         add(new JLabel(s_stringMgr.getString("oracle.secons3")));
      }
   }
}
