package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;

public class CreateAliasAction extends AliasAction
{
   private static final long serialVersionUID = 1L;

	
	public CreateAliasAction(IApplication app)
	{
		super(app);
	}

	
	public void actionPerformed(ActionEvent evt)
	{
      moveToFrontAndSelectAliasFrame();
      new CreateAliasCommand(getApplication()).execute();
	}
}
