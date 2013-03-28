package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;

public class UpdateAction extends SquirrelAction
{
    private static final long serialVersionUID = 4082643943728263683L;

    
	public UpdateAction(IApplication app)
	{
		super(app);
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
	}

	
	public void actionPerformed(ActionEvent evt)
	{
		new UpdateCommand(getApplication()).execute();
	}
}
