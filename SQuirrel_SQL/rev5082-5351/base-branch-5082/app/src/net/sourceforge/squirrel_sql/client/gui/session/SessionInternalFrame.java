package net.sourceforge.squirrel_sql.client.gui.session;


import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SessionTabWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISQLInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class SessionInternalFrame extends SessionTabWidget
					implements ISQLInternalFrame, IObjectTreeInternalFrame
{
    static final long serialVersionUID = 6961615570741567740L;
    
    
	private final IApplication _app;

	private SessionPanel _sessionPanel;
    
	public SessionInternalFrame(ISession session)
	{
		super(session.getTitle(), true, true, true, true, session);
		_app = session.getApplication();
		setVisible(false);
		createGUI(session);
	}

	public SessionPanel getSessionPanel()
	{
		return _sessionPanel;
	}

	public ISQLPanelAPI getSQLPanelAPI()
	{
		return _sessionPanel.getSQLPaneAPI();
	}

	public IObjectTreeAPI getObjectTreeAPI()
	{
		return _sessionPanel.getObjectTreePanel();
	}

   
	void addToToolbar(Action action)
   {
      _sessionPanel.addToToolbar(action);
   }

   public void addSeparatorToToolbar()
   {
      _sessionPanel.addSeparatorToToolbar();
   }

   public void addToToolsPopUp(String selectionString, Action action)
   {
      _sessionPanel.addToToolsPopUp(selectionString, action);
   }



   private void createGUI(final ISession session)
	{
		setVisible(false);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		final IApplication app = session.getApplication();
		Icon icon = app.getResources().getIcon(getClass(), "frameIcon"); 
		if (icon != null)
		{
			setFrameIcon(icon);
		}

		addWidgetListener(new WidgetAdapter()
		{
			public void widgetClosing(WidgetEvent evt)
			{
            if (!session.isfinishedLoading())
            {
               return;
            }
            final ISession mySession = getSession();
            if (mySession != null)
				{
               _sessionPanel.sessionWindowClosing();
					_app.getSessionManager().closeSession(mySession);
				}
			}
		});

		_sessionPanel = new SessionPanel(session);
		_sessionPanel.setMainPanelFactory(new MainPanelFactory());
		_sessionPanel.initialize(session);
		setContentPane(_sessionPanel);
		validate();
	}

   public void requestFocus()
   {
      if (ISession.IMainPanelTabIndexes.SQL_TAB == getSession().getSelectedMainTabIndex())
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               _sessionPanel.getSQLEntryPanel().requestFocus();
            }
         });
      }
      else if (ISession.IMainPanelTabIndexes.OBJECT_TREE_TAB == getSession().getSelectedMainTabIndex())
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               _sessionPanel.getObjectTreePanel().requestFocus();
            }
         });
      }

   }

   public boolean hasSQLPanelAPI()
   {
      return true;
   }

}
