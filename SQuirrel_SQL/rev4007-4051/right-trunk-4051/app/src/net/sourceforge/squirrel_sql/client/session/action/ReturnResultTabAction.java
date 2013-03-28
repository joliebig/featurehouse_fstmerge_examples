package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultFrame;

public class ReturnResultTabAction extends SquirrelAction
{
	
	private ResultFrame _resultFrame;

	
	public ReturnResultTabAction(IApplication app, ResultFrame resultFrame)
		throws IllegalArgumentException
	{
		super(app);
		if (resultFrame == null)
		{
			throw new IllegalArgumentException("Null ResultFrame passed");
		}

		_resultFrame = resultFrame;
	}

	public void actionPerformed(ActionEvent evt)
	{
		new ReturnResultTabCommand(_resultFrame).execute();
	}

}