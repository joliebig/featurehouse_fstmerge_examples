package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.WindowManager;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;

public class EditWhereColsCommand implements ICommand
{
	
	final IApplication _app;

	
	private final IObjectTreeAPI _tree;
	
	
	private final IDatabaseObjectInfo _objectInfo;

	public EditWhereColsCommand(IApplication app, IObjectTreeAPI tree,
								IDatabaseObjectInfo objectInfo)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		if (tree == null)
		{
			throw new IllegalArgumentException("IObjectTreeAPI == null");
		}
		if (objectInfo == null)
		{
			throw new IllegalArgumentException("IDatabaseObjectInfo == null");
		}
		_app = app;
		_tree = tree;
		_objectInfo = objectInfo;
	}

	
	public void execute()
	{
		if (_tree != null)
		{
			final WindowManager winMgr = _app.getWindowManager();
			winMgr.showEditWhereColsDialog(_tree, _objectInfo);
		}
	}
}
