package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;

public class SetDefaultCatalogAction extends SquirrelAction
										implements IObjectTreeAction
{
	
	private final static ILogger s_log =
		LoggerController.createLogger(SetDefaultCatalogAction.class);

	private IObjectTreeAPI _tree;

	
	public SetDefaultCatalogAction(IApplication app)
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
		IDatabaseObjectInfo[] catalogs = _tree.getSelectedDatabaseObjects();
		if (catalogs.length == 1)
		{
			String catalog = catalogs[0].getSimpleName();
			try
			{
				new SetDefaultCatalogCommand(_tree.getSession(), catalog).execute();
			}
			catch (Throwable th)
			{
				_tree.getSession().showErrorMessage(th);
				s_log.error("Error occured setting session catalog to " + catalog, th);
			}
		}
		else
		{
			_tree.getSession().getApplication().showErrorDialog("Must select a single catalog");
		}












	}
}
