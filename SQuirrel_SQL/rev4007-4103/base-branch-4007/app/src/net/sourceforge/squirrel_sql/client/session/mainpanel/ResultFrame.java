package net.sourceforge.squirrel_sql.client.session.mainpanel;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.*;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.BaseInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.BaseSessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ReturnResultTabAction;

public class ResultFrame extends BaseSessionInternalFrame
{
	
	private static ILogger s_log = LoggerController.createLogger(ResultFrame.class);

       private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ResultFrame.class);

   
	private IResultTab _tab;
   private JCheckBox _chkOnTop;

   
   public ResultFrame(ISession session, IResultTab tab)
   {
      super(session, getFrameTitle(session, tab), true, true, true, true);
      _tab = tab;

      setDefaultCloseOperation(DISPOSE_ON_CLOSE);

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
         getDesktopPane().setLayer(this, JLayeredPane.PALETTE_LAYER.intValue());
      }
      else
      {
         getDesktopPane().setLayer(this, JLayeredPane.DEFAULT_LAYER.intValue());
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
