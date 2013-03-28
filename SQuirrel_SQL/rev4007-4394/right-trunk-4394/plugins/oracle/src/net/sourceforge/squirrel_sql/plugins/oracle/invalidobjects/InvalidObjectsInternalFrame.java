package net.sourceforge.squirrel_sql.plugins.oracle.invalidobjects;


import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.oracle.OracleInternalFrame;
import net.sourceforge.squirrel_sql.plugins.oracle.OracleInternalFrameCallback;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;

public class InvalidObjectsInternalFrame extends OracleInternalFrame
{

   private static final String PREF_PART_INVALID_FRAME = "InvalidFrame";


   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(InvalidObjectsInternalFrame.class);

   private InvalidObjectsPanel _invalidObjectsPanel;
   
   private InvalidObjectsToolBar _toolBar;

   private Resources _resources;

   public InvalidObjectsInternalFrame(ISession session, Resources resources)
   {

      
      super(session, s_stringMgr.getString("oracle.invalidTitle", session.getTitle()));
      _resources = resources;
      createGUI(session);
   }

   public InvalidObjectsPanel getDBOutputPanel()
   {
      return _invalidObjectsPanel;
   }

   private void createGUI(ISession session)
   {

      addInternalFrameListener(new InternalFrameAdapter()
      {
         public void internalFrameClosing(InternalFrameEvent e)
         {
            InvalidObjectsInternalFrame.super.internalFrameClosing(_toolBar.isStayOnTop(), 0);
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

            _invalidObjectsPanel = new InvalidObjectsPanel(getSession());
            _toolBar = new InvalidObjectsToolBar(getSession(), stayOnTop);
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.add(_toolBar, BorderLayout.NORTH);
            contentPanel.add(_invalidObjectsPanel, BorderLayout.CENTER);
            setContentPane(contentPanel);

         }
      };


      initFromPrefs(PREF_PART_INVALID_FRAME, cb);
   }


   

   
   private class InvalidObjectsToolBar extends OracleToolBar
   {
      InvalidObjectsToolBar(ISession session, boolean stayOnTop)
      {
         super();
         createGUI(session, stayOnTop);
      }

      private void createGUI(ISession session, boolean stayOnTop)
      {
         IApplication app = session.getApplication();
         setUseRolloverButtons(true);
         setFloatable(false);
         add(new GetInvalidObjectsAction(app, _resources, _invalidObjectsPanel));

         addStayOnTop(stayOnTop);
      }
   }
}
