package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultFrame;

public class ReturnResultTabCommand implements ICommand
{
	
	private ResultFrame _resultFrame;

	
	public ReturnResultTabCommand(ResultFrame resultFrame)
	{
		super();
		if (resultFrame == null)
		{
			throw new IllegalArgumentException("Null ResultFrame passed");
		}

		_resultFrame = resultFrame;
	}

	public void execute()
	{
		_resultFrame.returnToTabbedPane();
	}
}