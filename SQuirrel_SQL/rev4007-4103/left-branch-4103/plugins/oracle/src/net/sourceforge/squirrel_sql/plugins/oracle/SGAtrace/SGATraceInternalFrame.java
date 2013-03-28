package net.sourceforge.squirrel_sql.plugins.oracle.SGAtrace;


import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.oracle.OracleInternalFrame;
import net.sourceforge.squirrel_sql.plugins.oracle.OracleInternalFrameCallback;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SGATraceInternalFrame extends OracleInternalFrame
{
    private static final long serialVersionUID = 1L;


    private static final String PREF_PART_SGA_FRAME = "SGAFrame";


   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(SGATraceInternalFrame.class);

   private SGATracePanel _sgaTracePanel;
   
   private SGATraceToolBar _toolBar;

   transient private Resources _resources;

   public SGATraceInternalFrame(ISession session, Resources resources)
   {
      
      super(session, s_stringMgr.getString("oracle.sgaTitle", session.getTitle()));
      _resources = resources;
      createGUI();
   }

   public SGATracePanel getSGATracePanel()
   {
      return _sgaTracePanel;
   }

   private void createGUI()
   {
      addInternalFrameListener(new InternalFrameAdapter()
      {
         public void internalFrameClosing(InternalFrameEvent e)
         {
            SGATraceInternalFrame.super.internalFrameClosing(_toolBar.isStayOnTop(), _sgaTracePanel.getAutoRefreshPeriod());
            _sgaTracePanel.setAutoRefresh(false);
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
            _sgaTracePanel = new SGATracePanel(getSession(), autoRefeshPeriod);
            _toolBar = new SGATraceToolBar(getSession(), stayOnTop, autoRefeshPeriod);
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.add(_toolBar, BorderLayout.NORTH);
            contentPanel.add(_sgaTracePanel, BorderLayout.CENTER);
            setContentPane(contentPanel);
         }
      };

      initFromPrefs(PREF_PART_SGA_FRAME, cb);


   }

   
   private class SGATraceToolBar extends OracleToolBar
   {
    private static final long serialVersionUID = 1L;

    SGATraceToolBar(ISession session, boolean stayOnTop, int autoRefeshPeriod)
      {
         super();
         createGUI(session, stayOnTop, autoRefeshPeriod);
      }

      private void createGUI(ISession session, boolean stayOnTop, int autoRefeshPeriod)
      {
         IApplication app = session.getApplication();
         setUseRolloverButtons(true);
         setFloatable(false);
         add(new GetSGATraceAction(app, _resources, _sgaTracePanel));

         addStayOnTop(stayOnTop);


         
         
         final JCheckBox autoRefresh = new JCheckBox(s_stringMgr.getString("oracle.enableAutoRefresh"), false);
         autoRefresh.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               _sgaTracePanel.setAutoRefresh(autoRefresh.isSelected());
            }
         });
         add(autoRefresh);

         
         final SpinnerNumberModel model = new SpinnerNumberModel(autoRefeshPeriod, 1, 60, 5);
         final JSpinner refreshRate = new JSpinner(model);
         refreshRate.addChangeListener(new ChangeListener()
         {
            public void stateChanged(ChangeEvent e)
            {
               _sgaTracePanel.setAutoRefreshPeriod(model.getNumber().intValue());
            }
         });
         add(refreshRate);
         
         add(new JLabel(s_stringMgr.getString("oracle.refreshSecons")));
      }
   }
}
