package net.sourceforge.squirrel_sql.client.gui;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.gui.ToolBar;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;

public class HtmlViewerPanelToolBar extends ToolBar
{
	
	private final IApplication _app;

	
	private final HtmlViewerPanel _pnl;

	
	public HtmlViewerPanelToolBar(IApplication app, HtmlViewerPanel pnl)
	{
		super();

		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		if (pnl == null)
		{
			throw new IllegalArgumentException("HtmlViewerPanel == null");
		}

		_app = app;
		_pnl = pnl;

		setUseRolloverButtons(true);
		setFloatable(false);
		add(new HomeAction(_app));
		add(new BackAction(_app));
		add(new ForwardAction(_app));
		add(new RefreshAction(_app));
	}

	private final class BackAction extends SquirrelAction
	{
		public BackAction(IApplication app)
		{
			super(app);
			if (app == null)
			{
				throw new IllegalArgumentException("Null IApplication passed");
			}
		}

		public void actionPerformed(ActionEvent evt)
		{
			_pnl.goBack();
		}
	}

	private final class ForwardAction extends SquirrelAction
	{
		public ForwardAction(IApplication app)
		{
			super(app);
			if (app == null)
			{
				throw new IllegalArgumentException("Null IApplication passed");
			}
		}

		public void actionPerformed(ActionEvent evt)
		{
			_pnl.goForward();
		}
	}

	private final class RefreshAction extends SquirrelAction
	{
		public RefreshAction(IApplication app)
		{
			super(app);
			if (app == null)
			{
				throw new IllegalArgumentException("Null IApplication passed");
			}
		}

		public void actionPerformed(ActionEvent evt)
		{
			_pnl.refreshPage();
		}
	}

	private final class HomeAction extends SquirrelAction
	{
		public HomeAction(IApplication app)
		{
			super(app);
			if (app == null)
			{
				throw new IllegalArgumentException("Null IApplication passed");
			}
		}

		public void actionPerformed(ActionEvent evt)
		{
			_pnl.goHome();
		}
	}
}
