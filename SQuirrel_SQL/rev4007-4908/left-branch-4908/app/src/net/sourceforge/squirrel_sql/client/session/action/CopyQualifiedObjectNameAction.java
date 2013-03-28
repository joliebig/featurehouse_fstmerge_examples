package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;

public class CopyQualifiedObjectNameAction
	extends SquirrelAction
	implements IObjectTreeAction, CopyObjectNameCommand.ICopyTypes
{
	
	private final static ILogger s_log =
		LoggerController.createLogger(CopyQualifiedObjectNameAction.class);

	
	private IObjectTreeAPI _tree;

	
	public CopyQualifiedObjectNameAction(IApplication app)
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
			try
			{
				new CopyObjectNameCommand(_tree, QUALIFIED_NAME).execute();
			}
			catch (Throwable ex)
			{
				final String msg = "Error occured copying object names";
				_tree.getSession().showErrorMessage(msg + ": " + ex);
				s_log.error(msg, ex);
			}
		}
	}
}
