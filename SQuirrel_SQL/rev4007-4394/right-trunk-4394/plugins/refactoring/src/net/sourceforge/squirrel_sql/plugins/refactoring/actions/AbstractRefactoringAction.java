package net.sourceforge.squirrel_sql.plugins.refactoring.actions;



import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.IObjectTreeAction;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.Resources;

public abstract class AbstractRefactoringAction extends SquirrelAction implements IObjectTreeAction
{
	
	protected ISession _session;

	
	protected IObjectTreeAPI _tree;

	public AbstractRefactoringAction(IApplication app, Resources rsrc)
	{
		super(app, rsrc);
	}

	
	public void actionPerformed(ActionEvent evt)
	{
		if (_session != null)
		{
			IDatabaseObjectInfo[] infos = _tree.getSelectedDatabaseObjects();
			if (infos.length > 1 && !isMultipleObjectAction())
			{
				_session.showErrorMessage(getErrorMessage());
			} else
			{
				try
				{
					getCommand(infos).execute();
				} catch (Exception e)
				{
					_session.showErrorMessage(e);
				}
			}
		}
	}

	
	protected abstract ICommand getCommand(IDatabaseObjectInfo[] info);

	
	protected abstract boolean isMultipleObjectAction();

	
	protected abstract String getErrorMessage();

	
	public void setObjectTree(IObjectTreeAPI tree)
	{
		_tree = tree;
		if (null != _tree)
			_session = _tree.getSession();
		else
			_session = null;
		setEnabled(null != _tree);
	}
}