package net.sourceforge.squirrel_sql.client.action;

import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.IApplication;

public abstract class ClientAction extends SquirrelAction
{
	private static final long serialVersionUID = -9168131047077835490L;

	protected ClientAction(IApplication app)
	{
		super(app);
	}

	protected ClientAction(IApplication app, Resources rsrc)
	{
		super(app, rsrc);
	}

}
