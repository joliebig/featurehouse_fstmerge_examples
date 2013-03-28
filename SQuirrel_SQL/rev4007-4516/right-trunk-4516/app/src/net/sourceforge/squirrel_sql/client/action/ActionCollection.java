package net.sourceforge.squirrel_sql.client.action;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JInternalFrame;
import javax.swing.KeyStroke;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.BaseSessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.mainframe.action.AboutAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CascadeAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CloseAllSessionsAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.DisplayPluginSummaryAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.DumpApplicationAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ExitAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.GlobalPreferencesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.InstallDefaultDriversAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.MaximizeAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.NewSessionPropertiesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.SavePreferencesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ShowLoadedDriversOnlyAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.TileAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.TileHorizontalAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.TileVerticalAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.UpdateAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ViewHelpAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ViewLogsAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.*;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class ActionCollection
{
	
	private static ILogger s_log;

	
	private IApplication _app;

	
	private final Map<String, Action> _actionColl = new HashMap<String, Action>();

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ActionCollection.class);
    
	
	public ActionCollection(IApplication app)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		if (s_log == null)
		{
			s_log = LoggerController.createLogger(getClass());
		}
		_app = app;
		preloadActions();
		enableInternalFrameOptions(false);
	}

	
	public void add(Action action)
	{
		if (action == null)
		{
			throw new IllegalArgumentException("Action == null");
		}
		_actionColl.put(action.getClass().getName(), action);
	}

	
	public synchronized Action get(Class<? extends Action> actionClass)
	{
		if (actionClass == null)
		{
			throw new IllegalArgumentException("null Action Class passed.");
		}

		return get(actionClass.getName());
	}

	
	public synchronized Action get(String actionClassName)
	{
		if (actionClassName == null)
		{
			throw new IllegalArgumentException("null Action Class Name passed.");
		}

		Action action = _actionColl.get(actionClassName);
		if (action == null)
		{
            
            String errMsg = 
                s_stringMgr.getString("ActionCollection.actionNotFound", 
                                      actionClassName);
            s_log.error(errMsg);
			action = createAction(actionClassName);
		}
		return action;
	}

	
    @SuppressWarnings("unchecked")
	public void enableAction(Class actionClass, boolean enable)
		throws IllegalArgumentException
	{
		if (actionClass == null)
		{
			throw new IllegalArgumentException("null Action Class passed.");
		}

		final Action action = get(actionClass);
		if (action != null)
		{
			action.setEnabled(enable);
		}
	}

	
	public void internalFrameOpenedOrClosed(int nbrInternalFramesOpen)
	{
		enableInternalFrameOptions(nbrInternalFramesOpen > 0);
	}

	
	public void deactivationChanged(JInternalFrame frame)
	{
		final boolean isSQLFrame = (frame instanceof SQLInternalFrame);
		final boolean isTreeFrame = (frame instanceof ObjectTreeInternalFrame);
		final boolean isSessionInternalFrame = (frame instanceof SessionInternalFrame);

		for (Iterator<Action> it = actions(); it.hasNext();)
		{
			final Action act = it.next();

			if (act instanceof ISessionAction)
			{
				((ISessionAction)act).setSession(null);
			}

			if (isSQLFrame && (act instanceof ISQLPanelAction))
			{
				((ISQLPanelAction)act).setSQLPanel(null);
			}
			if (isTreeFrame && (act instanceof IObjectTreeAction))
			{
				((IObjectTreeAction)act).setObjectTree(null);
			}
			if ((isSessionInternalFrame) && (act instanceof ISQLPanelAction))
			{
				((ISQLPanelAction)act).setSQLPanel(null);
			}
			if ((isSessionInternalFrame) && (act instanceof IObjectTreeAction))
			{
				((IObjectTreeAction)act).setObjectTree(null);
			}
		}
	}

	
	public synchronized void activationChanged(JInternalFrame frame)
	{
		final boolean isSQLFrame = (frame instanceof SQLInternalFrame);
		final boolean isTreeFrame = (frame instanceof ObjectTreeInternalFrame);
		final boolean isSessionInternalFrame = (frame instanceof SessionInternalFrame);

		ISession session = null;
		if (frame instanceof BaseSessionInternalFrame)
		{
			session = ((BaseSessionInternalFrame)frame).getSession();
		}

		for (Iterator<Action> it = actions(); it.hasNext();)
		{
			final Action act = it.next();
			if (act instanceof ISessionAction)
			{
				((ISessionAction)act).setSession(session);
			}
			if (isSQLFrame && (act instanceof ISQLPanelAction))
			{
				((ISQLPanelAction)act).setSQLPanel(((SQLInternalFrame)frame).getSQLPanel().getSQLPanelAPI());
			}
			if (isTreeFrame && (act instanceof IObjectTreeAction))
			{
				((IObjectTreeAction)act).setObjectTree(((ObjectTreeInternalFrame)frame).getObjectTreePanel());
			}

			if ((isSessionInternalFrame) && (act instanceof ISQLPanelAction))
			{
            SessionInternalFrame sif = (SessionInternalFrame) frame;
            if(sif.getSessionPanel().isSQLTabSelected())
            {
   				((ISQLPanelAction)act).setSQLPanel(sif.getSessionPanel().getSQLPaneAPI());
            }
            else
            {
               ((ISQLPanelAction)act).setSQLPanel(null);
            }
			}
			if ((isSessionInternalFrame) && (act instanceof IObjectTreeAction))
			{
            SessionInternalFrame sif = (SessionInternalFrame) frame;
            if(sif.getSessionPanel().isObjectTreeTabSelected())
            {
               ((IObjectTreeAction)act).setObjectTree(((SessionInternalFrame)frame).getSessionPanel().getObjectTreePanel());
            }
            else
            {
               ((IObjectTreeAction)act).setObjectTree(null);
            }
			}
		}
	}

	
	public synchronized void loadActionKeys(ActionKeys[] actionKeys)
	{
		if (actionKeys == null)
		{
			throw new IllegalArgumentException("null ActionKeys[] passed");
		}

		for (int i = 0; i < actionKeys.length; ++i)
		{
			final ActionKeys ak = actionKeys[i];
			final Action action = get(ak.getActionClassName());
			if (action != null)
			{
				final String accel = ak.getAccelerator();
				if (accel != null && accel.length() > 0)
				{
					action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(accel));
				}

				final int mnemonic = ak.getMnemonic();
				if (mnemonic != KeyEvent.VK_UNDEFINED)
				{
					action.putValue(Action.MNEMONIC_KEY, Integer.valueOf(mnemonic));
				}
			}
		}
	}

	
	public Iterator<Action> actions()
	{
		return _actionColl.values().iterator();
	}

	
	public synchronized void setCurrentSession(ISession session)
	{
		for (Iterator<Action> it = actions(); it.hasNext();)
		{
			final Action act = it.next();
			if (act instanceof ISessionAction)
			{
				((ISessionAction)act).setSession(session);
			}
		}
	}

	
	private Action createAction(String actionClassName)
	{
		Action action = null;
		try
		{
            
            String msg = 
                s_stringMgr.getString("ActionCollection.createActionInfo", 
                                      actionClassName);
		    s_log.info(msg);
            action = (Action)Class.forName(actionClassName).newInstance();
			_actionColl.put(actionClassName, action);
		}
		catch (Exception ex)
		{
            
            String msg = 
                s_stringMgr.getString("ActionCollection.createActionError",
                                      actionClassName);
			s_log.error(msg, ex);
		}
		return action;
	}

	
	private void enableInternalFrameOptions(boolean enable)
	{
		enableAction(CascadeAction.class, enable);
		enableAction(MaximizeAction.class, enable);
		enableAction(TileAction.class, enable);
		enableAction(TileHorizontalAction.class, enable);
		enableAction(TileVerticalAction.class, enable);
		enableAction(CloseAllSessionsAction.class, enable);
	}

	
	private void preloadActions()
	{
		add(new AboutAction(_app));
		add(new CascadeAction(_app));
		add(new ToolsPopupAction(_app));
		add(new CloseAllSessionsAction(_app));
		add(new CloseAllSQLResultTabsAction(_app));
		add(new CloseAllSQLResultTabsButCurrentAction(_app));
		add(new CloseCurrentSQLResultTabAction(_app));
		add(new ToggleCurrentSQLResultTabStickyAction(_app));
		add(new CloseAllSQLResultWindowsAction(_app));
		add(new ViewObjectAtCursorInObjectTreeAction(_app));
		add(new CloseSessionAction(_app));
		add(new CommitAction(_app));
		add(new CopyQualifiedObjectNameAction(_app));
		add(new CopySimpleObjectNameAction(_app));
		add(new DisplayPluginSummaryAction(_app));
		
		add(new DeleteSelectedTablesAction(_app));
		add(new DumpApplicationAction(_app));
        add(new SavePreferencesAction(_app));
		add(new DumpSessionAction(_app));
		add(new ExecuteSqlAction(_app));
		add(new ExitAction(_app));
		add(new FileNewAction(_app));
		add(new FileOpenAction(_app));
		add(new FileAppendAction(_app));
		add(new FileSaveAction(_app));
		add(new FileSaveAsAction(_app));
        add(new FilePrintAction(_app));
		add(new FileCloseAction(_app));
		add(new GlobalPreferencesAction(_app));
		add(new GotoNextResultsTabAction(_app));
		add(new GotoPreviousResultsTabAction(_app));
		add(new InstallDefaultDriversAction(_app));
		add(new MaximizeAction(_app));
		add(new NewObjectTreeAction(_app));
		add(new NewSQLWorksheetAction(_app));
		add(new NewSessionPropertiesAction(_app));
		add(new NextSessionAction(_app));
		add(new PreviousSessionAction(_app));
		add(new ReconnectAction(_app));
		add(new RefreshSchemaInfoAction(_app));
		add(new RefreshObjectTreeItemAction(_app));
		add(new RollbackAction(_app));
		add(new SessionPropertiesAction(_app));
		add(new FilterObjectsAction(_app));
		add(new SetDefaultCatalogAction(_app));
		add(new ShowLoadedDriversOnlyAction(_app));
		add(new ShowNativeSQLAction(_app));
		add(new SQLFilterAction(_app));
		add(new EditWhereColsAction(_app));
		add(new TileAction(_app));
		add(new TileHorizontalAction(_app));
		add(new TileVerticalAction(_app));
		add(new ToggleAutoCommitAction(_app));
		add(new UpdateAction(_app));
		add(new ViewHelpAction(_app));
		add(new ViewLogsAction(_app));
		add(new PreviousSqlAction(_app));
		add(new NextSqlAction(_app));
		add(new SelectSqlAction(_app));
		add(new OpenSqlHistoryAction(_app));
	}

}
