package net.sourceforge.squirrel_sql.client.gui.db;

import javax.swing.JInternalFrame;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;

public class AliasWindowManager
{
	
	private static final ILogger s_log =
		LoggerController.createLogger(AliasWindowManager.class);

	
	private final IApplication _app;

	
	private final AliasWindowFactory _aliasWinFactory;

	
	public AliasWindowManager(IApplication app)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}

		_app = app;
		_aliasWinFactory = new AliasWindowFactory(_app);
	}

	
	public void showModifyAliasInternalFrame(final ISQLAlias alias)
	{
		if (alias == null)
		{
			throw new IllegalArgumentException("ISQLAlias == null");
		}

		moveToFront(_aliasWinFactory.getModifySheet(alias));
	}

	
	public void showNewAliasInternalFrame()
	{
		moveToFront(_aliasWinFactory.getCreateSheet());
	}

	
	public void showCopyAliasInternalFrame(final SQLAlias alias)
	{
		if (alias == null)
		{
			throw new IllegalArgumentException("ISQLAlias == null");
		}

		moveToFront(_aliasWinFactory.getCopySheet(alias));
	}

	public void moveToFront(final JInternalFrame fr)
	{
		if (fr != null)
		{
			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					GUIUtils.moveToFront(fr);
				}
			});
		}
		else
		{
			s_log.debug("JInternalFrame == null");
		}
	}

}
