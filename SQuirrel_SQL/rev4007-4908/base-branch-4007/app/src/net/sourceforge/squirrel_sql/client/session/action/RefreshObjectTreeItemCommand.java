package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;

public class RefreshObjectTreeItemCommand implements ICommand
{
	
	private final IObjectTreeAPI _tree;

	
	public RefreshObjectTreeItemCommand(IObjectTreeAPI tree)
	{
		super();
		if (tree == null)
		{
			throw new IllegalArgumentException("IObjectTreeAPI == null");
		}
		_tree = tree;
	}

	
	public void execute()
	{
		_tree.refreshSelectedNodes();
	}
}