package net.sourceforge.squirrel_sql.plugins.mysql.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;

public class CheckTableAction extends SquirrelAction implements ISessionAction
{
    private static final long serialVersionUID = 1L;
	
	interface ICheckTypes
	{
		int QUICK = 0;
		int FAST = 1;
		int MEDIUM = 2;
		int EXTENDED = 3;
		int CHANGED = 4;
	}

	
	transient private ISession _session;

	
	transient private final MysqlPlugin _plugin;

	
	private int _checkType;

	
	private CheckTableAction(IApplication app, Resources rsrc,
							MysqlPlugin plugin, int checkType)
	{
		super(app, rsrc);
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		if (rsrc == null)
		{
			throw new IllegalArgumentException("Resources == null");
		}
		if (plugin == null)
		{
			throw new IllegalArgumentException("MysqlPlugin == null");
		}
		if (checkType < ICheckTypes.QUICK || checkType > ICheckTypes.CHANGED)
		{
			throw new IllegalArgumentException("Invalid checkType of " + checkType);
		}

		_plugin = plugin;
		_checkType = checkType;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (_session != null)
		{
			try
			{
				new CheckTableCommand(_session, _plugin, _checkType).execute();
			}
			catch (Throwable th)
			{
				_session.showErrorMessage(th);
			}
		}
	}

	
	public void setSession(ISession session)
	{
		_session = session;
	}

	public static final class ChangedCheckTableAction extends CheckTableAction
	{
        private static final long serialVersionUID = 1L;	    
		public ChangedCheckTableAction(IApplication app, Resources rsrc,
										MysqlPlugin plugin)
		{
			super(app, rsrc, plugin, ICheckTypes.CHANGED);
		}
	}

	public static final class ExtendedCheckTableAction extends CheckTableAction
	{
        private static final long serialVersionUID = 1L;	    
		public ExtendedCheckTableAction(IApplication app, Resources rsrc,
										MysqlPlugin plugin)
		{
			super(app, rsrc, plugin, ICheckTypes.EXTENDED);
		}
	}

	public static final class FastCheckTableAction extends CheckTableAction
	{
        private static final long serialVersionUID = 1L;

        public FastCheckTableAction(IApplication app, Resources rsrc,
										MysqlPlugin plugin)
		{
			super(app, rsrc, plugin, ICheckTypes.FAST);
		}
	}

	public static final class MediumCheckTableAction extends CheckTableAction
	{
        private static final long serialVersionUID = 1L;	    
		public MediumCheckTableAction(IApplication app, Resources rsrc,
										MysqlPlugin plugin)
		{
			super(app, rsrc, plugin, ICheckTypes.MEDIUM);
		}
	}

	public static final class QuickCheckTableAction extends CheckTableAction
	{
        private static final long serialVersionUID = 1L;	    
		public QuickCheckTableAction(IApplication app, Resources rsrc,
										MysqlPlugin plugin)
		{
			super(app, rsrc, plugin, ICheckTypes.QUICK);
		}
	}
}

