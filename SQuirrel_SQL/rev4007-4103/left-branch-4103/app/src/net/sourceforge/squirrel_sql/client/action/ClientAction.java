package net.sourceforge.squirrel_sql.client.action;

import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.IApplication;

public abstract class ClientAction extends SquirrelAction
{
	protected ClientAction(IApplication app)
	{
		super(app);
	}

	protected ClientAction(IApplication app, Resources rsrc)
	{
		super(app, rsrc);
	}

}
