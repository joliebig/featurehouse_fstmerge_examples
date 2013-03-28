package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.gui.action.TileInternalFramesAction;

import net.sourceforge.squirrel_sql.client.IApplication;

public class TileVerticalAction extends TileInternalFramesAction
{
	
	private IApplication _app;

	
	public TileVerticalAction(IApplication app)
	{
		super();
		_app = app;
		app.getResources().setupAction(
			this,
			_app.getSquirrelPreferences().getShowColoriconsInToolbar());
	}

	public void actionPerformed(ActionEvent evt)
	{
		CursorChanger cursorChg = new CursorChanger(_app.getMainFrame());
		cursorChg.show();
		try
		{
			super.actionPerformed(evt);
		}
		finally
		{
			cursorChg.restore();
		}
	}

	
	protected RowColumnCount getRowColumnCount(int internalFrameCount)
	{
		int rows = 0;
		int cols = 0;
		if (internalFrameCount > 0)
		{
			rows = 1;
			cols = internalFrameCount;
		}
		return new RowColumnCount(rows, cols);
	}
}
