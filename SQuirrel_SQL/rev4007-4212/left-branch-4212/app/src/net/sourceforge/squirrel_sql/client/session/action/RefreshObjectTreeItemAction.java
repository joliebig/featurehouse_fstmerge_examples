package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;

public class RefreshObjectTreeItemAction extends SquirrelAction
											implements IObjectTreeAction
{
	
	private IObjectTreeAPI _tree;

	
	public RefreshObjectTreeItemAction(IApplication app)
	{
		super(app);
	}

	public void setObjectTree(IObjectTreeAPI tree)
	{
		_tree = tree;
      setEnabled(null != _tree);
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (_tree != null)
		{
			CursorChanger cursorChg = new CursorChanger(getApplication().getMainFrame());
			cursorChg.show();
			try
			{
				new RefreshObjectTreeItemCommand(_tree).execute();
			}
			finally
			{
				cursorChg.restore();
			}
		}
	}
}

