package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.WindowManager;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class SQLFilterCommand implements ICommand
{
	
	private final IObjectTreeAPI _objectTree;

	
	private final IDatabaseObjectInfo _objectInfo;

	
	public SQLFilterCommand(IObjectTreeAPI objectTree,
								IDatabaseObjectInfo objectInfo)
	{
		super();
		if (objectInfo == null)
		{
			throw new IllegalArgumentException("Null IDatabaseObjectInfo passed");
		}
		if (objectTree == null)
		{
			throw new IllegalArgumentException("Null IObjectTreeAPI passed");
		}
		_objectTree = objectTree;
		_objectInfo = objectInfo;
	}

	
	public void execute()
	{
		if (_objectTree != null)
		{
			final ISession session = _objectTree.getSession();
			final IApplication app = session.getApplication();
			final WindowManager winMgr = app.getWindowManager();
			winMgr.showSQLFilterDialog( _objectTree, _objectInfo);
		}
	}
}
