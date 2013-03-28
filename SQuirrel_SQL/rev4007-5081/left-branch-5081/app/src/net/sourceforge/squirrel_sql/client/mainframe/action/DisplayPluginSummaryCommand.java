package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.awt.Frame;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.PluginSummaryDialog;

public class DisplayPluginSummaryCommand
{
	
	private IApplication _app;

	
	private Frame _frame;

	
	public DisplayPluginSummaryCommand(IApplication app, Frame frame)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
		_frame = frame;
	}

	
	public void execute()
	{
		try
		{
			new PluginSummaryDialog(_app, _frame).setVisible(true);
		}
		catch (DataSetException ex)
		{
			_app.showErrorDialog(ex);
		}
	}

}
