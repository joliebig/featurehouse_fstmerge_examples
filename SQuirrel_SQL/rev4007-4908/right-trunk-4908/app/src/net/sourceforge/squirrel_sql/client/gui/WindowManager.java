package net.sourceforge.squirrel_sql.client.gui;


import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.db.*;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.*;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrameWindowState;
import net.sourceforge.squirrel_sql.client.gui.mainframe.WidgetUtils;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.util.ThreadCheckingRepaintManager;
import net.sourceforge.squirrel_sql.client.mainframe.action.*;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.client.session.properties.EditWhereColsSheet;
import net.sourceforge.squirrel_sql.client.session.properties.SessionPropertiesSheet;
import net.sourceforge.squirrel_sql.client.session.sqlfilter.SQLFilterSheet;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.WindowState;
import net.sourceforge.squirrel_sql.fw.gui.debug.DebugEventListener;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.beans.PropertyVetoException;

public class WindowManager
{
	
	private static final ILogger s_log =
		LoggerController.createLogger(WindowManager.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(WindowManager.class);

	
	private static final String MENU = WindowManager.class.getName() + ".menu";

	
	private final IApplication _app;

	
	private DriverWindowManager _driverWinMgr;

	
	private AliasWindowManager _aliasWinMgr;

	
	private MainFrame _mainFrame;

	
	private AliasesListInternalFrame _aliasesListWindow;

	
	private DriversListInternalFrame _driversListWindow;

	


	
	private final SessionWindowsHolder _sessionWindows = new SessionWindowsHolder();

	private final SessionWindowListener _windowListener = new SessionWindowListener();



	
	
	


	private final SessionListener _sessionListener = new SessionListener();

	private EventListenerList _listenerList = new EventListenerList();

	private boolean _sessionClosing = false;

	
	public WindowManager(IApplication app, boolean enableUserInterfaceDebug)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}

		if (s_log.isDebugEnabled())
		{
			RepaintManager.setCurrentManager(new ThreadCheckingRepaintManager());
		}

		_app = app;

		_aliasWinMgr = new AliasWindowManager(_app);
		_driverWinMgr = new DriverWindowManager(_app);

		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				initialize();
			}
		}, true);
		new DebugEventListener().setEnabled(enableUserInterfaceDebug);
	}

	
	public MainFrame getMainFrame()
	{
		return _mainFrame;
	}

	public AliasesListInternalFrame getAliasesListInternalFrame()
	{
		return _aliasesListWindow;
	}

	public DriversListInternalFrame getDriversListInternalFrame()
	{
		return _driversListWindow;
	}

	public WindowState getAliasesWindowState()
	{
		return new WindowState(_aliasesListWindow.getInternalFrame());
	}

	public WindowState getDriversWindowState()
	{
		return new WindowState(_driversListWindow.getInternalFrame());
	}

   
   public void showModifyAliasInternalFrame(final ISQLAlias alias)
   {
      if (alias == null)
      {
         throw new IllegalArgumentException("ISQLAlias == null");
      }

      _aliasWinMgr.showModifyAliasInternalFrame(alias);
   }

	
	public void showNewAliasInternalFrame()
	{
		_aliasWinMgr.showNewAliasInternalFrame();
	}

	
	public void showCopyAliasInternalFrame(final SQLAlias alias)
	{
		if (alias == null)
		{
			throw new IllegalArgumentException("ISQLAlias == null");
		}

		_aliasWinMgr.showCopyAliasInternalFrame(alias);
	}

	
	public void showModifyDriverInternalFrame(final ISQLDriver driver)
	{
		if (driver == null)
		{
			throw new IllegalArgumentException("ISQLDriver == null");
		}

		_driverWinMgr.showModifyDriverInternalFrame(driver);
	}

	
	public void showNewDriverInternalFrame()
	{
		_driverWinMgr.showNewDriverInternalFrame();
	}

	
	public void showCopyDriverInternalFrame(final ISQLDriver driver)
	{
		if (driver == null)
		{
			throw new IllegalArgumentException("ISQLDriver == null");
		}

		_driverWinMgr.showCopyDriverInternalFrame(driver);
	}

	
	public synchronized void registerSessionSheet(ISessionWidget sheet)
	{
        
        String dbg = 
            s_stringMgr.getString("WindowManager.registerSessionSheet",
                                  sheet.getClass().getName());
		s_log.debug(dbg);
		final IIdentifier sessionIdentifier = sheet.getSession().getIdentifier();

		
		final int idx = _sessionWindows.addFrame(sessionIdentifier, sheet);

		
		
		
		if ( idx > 1)
		{
			sheet.setTitle(sheet.getTitle() + " (" + idx + ")");
		}

		sheet.addWidgetListener(_windowListener);
	}

	
	public void addSessionWidgetListener(WidgetAdapter listener)
	{
		if (listener == null)
		{
			throw new IllegalArgumentException("InternalFrameListener == null");
		}

		_listenerList.add(WidgetListener.class, listener);
	}

	
	public synchronized SessionInternalFrame createInternalFrame(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}

		final SessionInternalFrame sif = new SessionInternalFrame(session);

		session.setSessionInternalFrame(sif);
		_app.getPluginManager().sessionStarted(session);
		_app.getMainFrame().addWidget(sif);

		
		
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				sif.setVisible(true);
                sif.getObjectTreeAPI().selectRoot();
			}
		});

		return sif;
	}














    
	
	public synchronized SQLInternalFrame createSQLInternalFrame(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		final SQLInternalFrame sif = new SQLInternalFrame(session);
		getMainFrame().addWidget(sif);

		
		
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				sif.setVisible(true);
            sif.requestFocus();
			}
		});

		return sif;
	}

	
	public synchronized ObjectTreeInternalFrame createObjectTreeInternalFrame(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		final ObjectTreeInternalFrame oif = new ObjectTreeInternalFrame(session);
		getMainFrame().addWidget(oif);

		
		
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				oif.setVisible(true);
                oif.getObjectTreeAPI().selectRoot();
			}
		});

		return oif;
	}

	
	public synchronized void showSessionPropertiesDialog(ISession session, int tabIndexToSelect)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}

		SessionPropertiesSheet propsSheet = getSessionPropertiesDialog(session);
		if (propsSheet == null)
		{
			propsSheet = new SessionPropertiesSheet(session);
			_app.getMainFrame().addWidget(propsSheet);
			positionSheet(propsSheet);
		}
		else
		{
			propsSheet.moveToFront();
		}

      propsSheet.selectTabIndex(tabIndexToSelect);
   }

	
	public synchronized SQLFilterSheet showSQLFilterDialog(IObjectTreeAPI objectTree,
											IDatabaseObjectInfo objectInfo)
	{
		if (objectTree == null)
		{
			throw new IllegalArgumentException("IObjectTree == null");
		}
		if (objectInfo == null)
		{
			throw new IllegalArgumentException("IDatabaseObjectInfo == null");
		}

		SQLFilterSheet sqlFilterSheet = getSQLFilterSheet(objectTree, objectInfo);
		if (sqlFilterSheet == null)
		{
			sqlFilterSheet = new SQLFilterSheet(objectTree, objectInfo);
			_app.getMainFrame().addWidget(sqlFilterSheet);
			positionSheet(sqlFilterSheet);
		}
		else
		{
			sqlFilterSheet.moveToFront();
		}

		return sqlFilterSheet;
	}

	
	public synchronized EditWhereColsSheet showEditWhereColsDialog(IObjectTreeAPI tree,
											IDatabaseObjectInfo objectInfo)
	{
		if (tree == null)
		{
			throw new IllegalArgumentException("IObjectTreeAPI == null");
		}
		if (objectInfo == null)
		{
			throw new IllegalArgumentException("IDatabaseObjectInfo == null");
		}

		ISession session = tree.getSession();
		EditWhereColsSheet editWhereColsSheet = getEditWhereColsSheet(session, objectInfo);
		if (editWhereColsSheet == null)
		{
			editWhereColsSheet = new EditWhereColsSheet(session, objectInfo);
			_app.getMainFrame().addWidget(editWhereColsSheet);
			positionSheet(editWhereColsSheet);
		}
		else
		{
			editWhereColsSheet.moveToFront();
		}

		return editWhereColsSheet;
	}

	public void moveToFront(final Window win)
	{
		if (win != null)
		{
			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					win.toFront();
					win.setVisible(true);
				}
			});
		}
	}

	public void moveToFront(final JInternalFrame fr)
	{
		if (fr != null)
		{
			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					fr.moveToFront();
					fr.setVisible(true);
					try
					{
						fr.setSelected(true);
					}
					catch (PropertyVetoException ex)
					{
                        
						s_log.error(s_stringMgr.getString("WindowManager.error.bringtofront"), ex);
					}
				}
			});
		}
	}

	public void activateNextSessionWindow()
	{
		final SessionManager sessMgr = _app.getSessionManager();
		final ISession sess = sessMgr.getActiveSession();

		if (sess == null)
		{
         return;
		}

      ISessionWidget activeSessionWindow = sess.getActiveSessionWindow();

      if(null == activeSessionWindow)
      {
         throw new IllegalStateException("Active Session with no active window ???");
      }


      ISessionWidget nextSessionWindow = _sessionWindows.getNextSessionWindow(activeSessionWindow);

		if (false == activeSessionWindow.equals(nextSessionWindow))
		{
			new SelectWidgetCommand(nextSessionWindow).execute();
		}
	}

	public void activatePreviousSessionWindow()
	{
      final SessionManager sessMgr = _app.getSessionManager();
      final ISession sess = sessMgr.getActiveSession();

      if (sess == null)
      {
         return;
      }

      ISessionWidget activeSessionWindow = sess.getActiveSessionWindow();

      if(null == activeSessionWindow)
      {
         throw new IllegalStateException("Active Session with no active window ???");
      }

      ISessionWidget previousSessionWindow = _sessionWindows.getPreviousSessionWindow(activeSessionWindow);

      if (false == activeSessionWindow.equals(previousSessionWindow))
      {
         new SelectWidgetCommand(previousSessionWindow).execute();
      }
	}

	protected void refireSessionSheetOpened(WidgetEvent evt)
	{
		
		Object[] listeners = _listenerList.getListenerList();
		
		
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == WidgetListener.class)
			{
				((WidgetListener)listeners[i + 1]).widgetOpened(evt);
			}
		}
	}

	protected void refireSessionSheetClosing(WidgetEvent evt)
	{
		
		Object[] listeners = _listenerList.getListenerList();
		
		
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == WidgetListener.class)
         {
            ((WidgetListener)listeners[i + 1]).widgetClosing(evt);
         }
      }
	}

	protected void refireSessionSheetClosed(WidgetEvent evt)
	{
		
		Object[] listeners = _listenerList.getListenerList();
		
		
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == WidgetListener.class)
         {
            ((WidgetListener)listeners[i + 1]).widgetClosed(evt);
         }
      }
	}

	protected void refireSessionSheetIconified(WidgetEvent evt)
	{
		
		Object[] listeners = _listenerList.getListenerList();
		
		
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == WidgetListener.class)
			{
				((WidgetListener)listeners[i + 1]).widgetIconified(evt);
			}
		}
	}

	protected void refireSessionSheetDeiconified(WidgetEvent evt)
	{
		
		Object[] listeners = _listenerList.getListenerList();
		
		
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == WidgetListener.class)
			{
				((WidgetListener)listeners[i + 1]).widgetDeiconified(evt);
			}
		}
	}

	protected void refireSessionSheetActivated(WidgetEvent evt)
	{
		
		Object[] listeners = _listenerList.getListenerList();
		
		
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == WidgetListener.class)
			{
				((WidgetListener)listeners[i + 1]).widgetActivated(evt);
			}
		}
	}

	protected void refireSessionSheetDeactivated(WidgetEvent evt)
	{
		
		Object[] listeners = _listenerList.getListenerList();
		
		
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == WidgetListener.class)
			{
				((WidgetListener)listeners[i + 1]).widgetDeactivated(evt);
			}
		}
	}

	private SessionPropertiesSheet getSessionPropertiesDialog(ISession session)
	{

      ISessionWidget[] framesOfSession = _sessionWindows.getFramesOfSession(session.getIdentifier());

      for (int i = 0; i < framesOfSession.length; i++)
      {
         if (framesOfSession[i] instanceof SessionPropertiesSheet)
         {
            return (SessionPropertiesSheet)framesOfSession[i];
         }
      }
		return null;
	}

	private SQLFilterSheet getSQLFilterSheet(IObjectTreeAPI tree,
												IDatabaseObjectInfo objectInfo)
	{
		final ISession session = tree.getSession();

      ISessionWidget[] framesOfSession = _sessionWindows.getFramesOfSession(session.getIdentifier());

      for (int i = 0; i < framesOfSession.length; i++)
      {
         if (framesOfSession[i] instanceof SQLFilterSheet)
         {
            final SQLFilterSheet sfs = (SQLFilterSheet)framesOfSession[i];
            if (sfs.getObjectTree() == tree &&
                  objectInfo.equals(sfs.getDatabaseObjectInfo()))
            {
               return sfs;
            }
         }
      }

		return null;
	}

	private EditWhereColsSheet getEditWhereColsSheet(ISession session,
											IDatabaseObjectInfo objectInfo)
	{



      ISessionWidget[] framesOfSession = _sessionWindows.getFramesOfSession(session.getIdentifier());

      for (int i = 0; i < framesOfSession.length; i++)
      {
         if (framesOfSession[i] instanceof EditWhereColsSheet)
         {
            final EditWhereColsSheet sfs = (EditWhereColsSheet)framesOfSession[i];


            if (objectInfo.equals(sfs.getDatabaseObjectInfo()))
            {
               return sfs;
            }
         }
      }
		return null;
	}


	private void positionSheet(SessionDialogWidget sfs)
	{
		DialogWidget.centerWithinDesktop(sfs);
		sfs.moveToFront();
	}

	private void selectFrontWindow()
	{
		final IDesktopContainer desktop = _app.getMainFrame().getDesktopContainer();
		if (desktop != null)
		{
			final IWidget[] jifs = desktop.getAllWidgets();
			if (jifs != null && jifs.length > 0)
			{
				jifs[0].moveToFront();
			}
		}
	}

	private void initialize()
	{
		createAliasesListUI();
		createDriversListUI();
		preLoadActions();
		_app.getSessionManager().addSessionListener(_sessionListener);
		createMainFrame();
		setupFromPreferences();
	}

	private void createMainFrame()
	{
		_mainFrame = new MainFrame(_app);
      GUIUtils.setMainFrame(_mainFrame);
   }

	private void createAliasesListUI()
	{
		final IToogleableAliasesList al = new AliasesList(_app);

		final ActionCollection actions = _app.getActionCollection();
		actions.add(new ModifyAliasAction(_app, al));
		actions.add(new DeleteAliasAction(_app, al));
		actions.add(new CopyAliasAction(_app, al));
		actions.add(new ConnectToAliasAction(_app, al));
		actions.add(new CreateAliasAction(_app));
		actions.add(new SortAliasesAction(_app, al));
		actions.add(new AliasPropertiesAction(_app, al));
		actions.add(new ToggleTreeViewAction(_app, al));
		actions.add(new NewAliasFolderAction(_app, al));
      actions.add(new CopyToPasteAliasFolderAction(_app, al));
		actions.add(new CutAliasFolderAction(_app, al));
		actions.add(new PasteAliasFolderAction(_app, al));
		actions.add(new CollapseAllAliasFolderAction(_app, al));
		actions.add(new ExpandAllAliasFolderAction(_app, al));

      _aliasesListWindow = new AliasesListInternalFrame(_app, al);

   }

	private void createDriversListUI()
	{
		final DriversList dl = new DriversList(_app);

		final ActionCollection actions = _app.getActionCollection();
		actions.add(new ModifyDriverAction(_app, dl));
		actions.add(new DeleteDriverAction(_app, dl));
		actions.add(new CopyDriverAction(_app, dl));
		actions.add(new CreateDriverAction(_app));
        actions.add(new ShowDriverWebsiteAction(_app, dl));

		_driversListWindow = new DriversListInternalFrame(_app, dl);
	}

	private void preLoadActions()
	{
		final ActionCollection actions = _app.getActionCollection();
		if (actions == null)
		{
			throw new IllegalStateException("ActionCollection hasn't been created.");
		}

		actions.add(new ViewAliasesAction(_app, getAliasesListInternalFrame()));
		actions.add(new ViewDriversAction(_app, getDriversListInternalFrame()));


	}

	private void setupFromPreferences()
	{
		final SquirrelPreferences prefs = _app.getSquirrelPreferences();
		final MainFrameWindowState ws = prefs.getMainFrameWindowState();

      prepareAliasWindow(ws);
      prepareDriversWindow(ws);
		prefs.setMainFrameWindowState(new MainFrameWindowState(this));
	}

   private void prepareDriversWindow(MainFrameWindowState ws)
   {
      _mainFrame.addWidget(_driversListWindow);
      WindowState toolWs = ws.getDriversWindowState();
      _driversListWindow.setBounds(toolWs.getBounds().createRectangle());

      if (toolWs.isVisible() && _app.getDesktopStyle().isInternalFrameStyle())
      {
         _driversListWindow.setVisible(true);

         
         _mainFrame.setEnabledDriversMenu(true);
         

         try
         {
            _driversListWindow.setSelected(true);
         }
         catch (PropertyVetoException ex)
         {
            
            s_log.error(s_stringMgr.getString("WindowManager.errorselectingwindow"), ex);
         }
      }
      else
      {
         _driversListWindow.setVisible(false);

         
         _mainFrame.setEnabledDriversMenu(false);
         
      }
   }

   private void prepareAliasWindow(MainFrameWindowState ws)
   {
      WindowState toolWs;
      _mainFrame.addWidget(_aliasesListWindow);
      toolWs = ws.getAliasesWindowState();
      _aliasesListWindow.setBounds(toolWs.getBounds().createRectangle());
      if (
              (toolWs.isVisible() && _app.getDesktopStyle().isInternalFrameStyle())
           || (false == _app.getDesktopStyle().isInternalFrameStyle() && false == _aliasesListWindow.isEmpty())
         )
      {
         _aliasesListWindow.setVisible(true);

         
         
         _mainFrame.setEnabledAliasesMenu(true);

         try
         {
            _aliasesListWindow.setSelected(true);
         }
         catch (PropertyVetoException ex)
         {
            
            s_log.error(s_stringMgr.getString("WindowManager.errorselectingwindow"), ex);
         }
      }
      else if(false == _app.getDesktopStyle().isInternalFrameStyle())
      {
         _aliasesListWindow.setVisible(false);

         
         
         _mainFrame.setEnabledAliasesMenu(false);
      }
   }

   
	private IWidget getWidgetForSession(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}

		IWidget firstWindow = null;

      ISessionWidget[] framesOfSession = _sessionWindows.getFramesOfSession(session.getIdentifier());
      for (int i = 0; i < framesOfSession.length; i++)
      {
         if (framesOfSession[i] instanceof ISessionWidget)
         {
            firstWindow = framesOfSession[i];
         }
         if (framesOfSession[i] instanceof SessionInternalFrame)
         {
            final SessionInternalFrame sif = (SessionInternalFrame)framesOfSession[i];
            if (sif.getSession().equals(session))
            {
               return sif;
            }
         }
      }
		return firstWindow;
	}

   public ISessionWidget[] getAllFramesOfSession(IIdentifier sessionIdentifier)
   {
      return _sessionWindows.getFramesOfSession(sessionIdentifier);
   }


   private final class SessionWindowListener implements WidgetListener
	{
		public void widgetOpened(WidgetEvent evt)
		{
			final IWidget widget = evt.getWidget();

			
			
			

			
			
			final JMenu menu = getMainFrame().getWindowsMenu();

			final Action action = new SelectWidgetAction(widget);

         final JMenuItem menuItem = menu.add(action);
			widget.putClientProperty(MENU, menuItem);

			
			IWidget[] frames = WidgetUtils.getOpenNonToolWindows(getMainFrame().getDesktopContainer().getAllWidgets());
			_app.getActionCollection().internalFrameOpenedOrClosed(frames.length);

			refireSessionSheetOpened(evt);
		}

		public void widgetClosing(WidgetEvent evt)
		{
			refireSessionSheetClosing(evt);
		}

		public void widgetClosed(WidgetEvent evt)
		{
			final IWidget widget = evt.getWidget();

			
			if (!_sessionClosing)
			{
				
				
				if (widget instanceof ISessionWidget)
				{
					final ISessionWidget sessionWidget = (ISessionWidget)widget;
					final IIdentifier sessionID = sessionWidget.getSession().getIdentifier();
               ISessionWidget[] sessionSheets = _sessionWindows.getFramesOfSession(sessionID);

               for (int i = 0; i < sessionSheets.length; i++)
               {
                  if (sessionSheets[i] == sessionWidget)
                  {
                     _sessionWindows.removeWindow(sessionSheets[i]);
                     WindowManager.this.selectFrontWindow();
                     break;
                  }
               }
				}
			}

			
			
			final JMenuItem menuItem = (JMenuItem)widget.getClientProperty(MENU);
			if (menuItem != null)
			{
				final JMenu menu = getMainFrame().getWindowsMenu();
				if (menu != null)
				{
					menu.remove(menuItem);
				}
			}

			
			IWidget[] frames = WidgetUtils.getOpenNonToolWindows(getMainFrame().getDesktopContainer().getAllWidgets());

			_app.getActionCollection().internalFrameOpenedOrClosed(frames.length);

			refireSessionSheetClosed(evt);
		}

		public void widgetIconified(WidgetEvent e)
		{
			refireSessionSheetIconified(e);
		}

		public void widgetDeiconified(WidgetEvent e)
		{
			refireSessionSheetDeiconified(e);
		}

		public void widgetActivated(WidgetEvent e)
		{
			refireSessionSheetActivated(e);
		}

		public void widgetDeactivated(WidgetEvent e)
		{
			refireSessionSheetDeactivated(e);
		}
	}

	
	private final class SessionListener extends SessionAdapter
	{
		
		public void sessionConnected(SessionEvent evt)
		{
			
			evt.getSession().setMessageHandler(_app.getMessageHandler());
		}

		
		public void sessionActivated(SessionEvent evt)
		{
			final ISession newSession = evt.getSession();

			
			_app.getActionCollection().setCurrentSession(newSession);

			
			
			ISession currSession = null;
			IWidget sif = getMainFrame().getDesktopContainer().getSelectedWidget();
			if (sif instanceof ISessionWidget)
			{
				currSession = ((ISessionWidget)sif).getSession();
			}
			if (currSession != newSession)
			{
				sif = getWidgetForSession(newSession);
				if (sif != null)
				{
					sif.moveToFront();
				}
			}

			
			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					getMainFrame().getSessionMenu().setEnabled(true);
				}
			});
		}

		
		public void sessionClosing(SessionEvent evt)
		{
			getMainFrame().getSessionMenu().setEnabled(false);

			
			_app.getActionCollection().setCurrentSession(null);

			try
			{
				if(_sessionClosing)
				{
					return;
				}

				_sessionClosing = true;
				IIdentifier sessionId = evt.getSession().getIdentifier();

				ISessionWidget[] framesOfSession = _sessionWindows.getFramesOfSession(sessionId);
				for (int i = 0; i < framesOfSession.length; i++)
				{
					if(framesOfSession[i] instanceof SessionTabWidget)
					{
						
						
						
						framesOfSession[i].closeFrame(false);
					}
					else
					{
						framesOfSession[i].closeFrame(true);
					}
				}

				_sessionWindows.removeAllWindows(sessionId);

				selectFrontWindow();
			}
			finally
			{
				_sessionClosing = false;
			}
		}
	}
}
