package net.sourceforge.squirrel_sql.client.action;

import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.IApplication;

import javax.swing.*;

public abstract class SquirrelAction extends BaseAction
{
	protected IApplication _app;
   private Resources _rsrc;

   protected SquirrelAction(IApplication app)
	{
		this(app, app.getResources());
	}

	protected SquirrelAction(IApplication app, Resources rsrc)
	{
		super();
      _rsrc = rsrc;
      if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (rsrc == null)
		{
			throw new IllegalArgumentException("No Resources object in IApplication");
		}

		_app = app;
		rsrc.setupAction(this, _app.getSquirrelPreferences().getShowColoriconsInToolbar());
	}

	protected IApplication getApplication()
	{
		return _app;
	}

   public KeyStroke getKeyStroke()
   {
      return _rsrc.getKeyStroke(this);
   }
}
