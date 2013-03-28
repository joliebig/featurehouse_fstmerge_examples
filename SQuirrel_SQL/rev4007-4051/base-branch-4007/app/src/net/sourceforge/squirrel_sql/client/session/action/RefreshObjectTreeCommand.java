package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

public class RefreshObjectTreeCommand implements ICommand
{
	
	private final IObjectTreeAPI _tree;

	
	public RefreshObjectTreeCommand(IObjectTreeAPI tree)
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
		_tree.refreshTree(true);
	}
}