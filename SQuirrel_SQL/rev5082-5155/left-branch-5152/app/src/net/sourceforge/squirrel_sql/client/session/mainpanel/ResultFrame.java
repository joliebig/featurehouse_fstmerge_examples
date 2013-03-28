package net.sourceforge.squirrel_sql.client.session.mainpanel;


import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SessionDialogWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DesktopContainerFactory;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DesktopStyle;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ReturnResultTabAction;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ResultFrame extends SessionDialogWidget
{
	
	private static ILogger s_log = LoggerController.createLogger(ResultFrame.class);

       private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ResultFrame.class);

   
	private IResultTab _tab;
   private JCheckBox _chkOnTop;

   
   public ResultFrame(ISession session, IResultTab tab)
   {
      super(getFrameTitle(session, tab), true, true, true, true, session);
      _tab = tab;

      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

      final Container cont = getContentPane();
      cont.setLayout(new BorderLayout());
      final IApplication app = session.getApplication();


      JPanel pnlButtons = new JPanel(new GridBagLayout());
      GridBagConstraints gbc;

      JButton rtnBtn = new JButton(new ReturnResultTabAction(app, this));
      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,0,5), 0,0);
      pnlButtons.add(rtnBtn, gbc);

      
      _chkOnTop = new JCheckBox(s_stringMgr.getString("resultFrame.stayOnTop"));
      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,5), 0,0);
      pnlButtons.add(_chkOnTop, gbc);
      _chkOnTop.setSelected(true);

      _chkOnTop.setVisible(session.getApplication().getDesktopStyle().supportsLayers());

      _chkOnTop.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onStayOnTopChanged();
         }
      });

      gbc = new GridBagConstraints(2,0,1,1,1,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,5), 0,0);
      pnlButtons.add(new JPanel(), gbc);



      cont.add(pnlButtons, BorderLayout.NORTH);
      cont.add(tab.getOutputComponent(), BorderLayout.CENTER);
   }

   private void onStayOnTopChanged()
   {
      if(_chkOnTop.isSelected())
      {
         setLayer(JLayeredPane.PALETTE_LAYER.intValue());
      }
      else
      {
         setLayer(JLayeredPane.DEFAULT_LAYER.intValue());
      }

      
      
      toFront();
   }

   
	public void dispose()
	{
		if (_tab != null)
		{
			_tab.closeTab();
			_tab = null;
		}
		super.dispose();
	}

	public void returnToTabbedPane()
	{
		s_log.debug("ResultFrame.returnToTabbedPane()");
		getContentPane().remove(_tab.getOutputComponent());
		_tab.returnToTabbedPane();
		_tab = null;
		dispose();
	}

	private static String getFrameTitle(ISession session, IResultTab tab)
		throws IllegalArgumentException
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null ResultTab passed");
		}
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}

		return session.getTitle() + " - " + tab.getViewableSqlString();
	}
}
