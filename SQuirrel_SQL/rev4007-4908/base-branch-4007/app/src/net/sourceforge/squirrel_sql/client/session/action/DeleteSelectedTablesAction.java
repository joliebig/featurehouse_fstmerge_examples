package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.event.ActionEvent;
import java.util.List;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class DeleteSelectedTablesAction extends SquirrelAction
										implements IObjectTreeAction
{
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DeleteSelectedTablesAction.class);   
    
	
	private static  String TITLE = 
        s_stringMgr.getString("DeleteSelectedTablesAction.title");

	
	private static String MSG = 
        s_stringMgr.getString("DeleteSelectedTablesAction.message");

    
	private IObjectTreeAPI _tree;

	
	public DeleteSelectedTablesAction(IApplication app)
	{
		super(app);
	}

	
	public void setObjectTree(IObjectTreeAPI tree)
	{
		_tree = tree;
      setEnabled(null != _tree);
	}

	
	public void actionPerformed(ActionEvent e)
	{
		if (_tree != null)
		{
			List<ITableInfo> tables = _tree.getSelectedTables();
			if (tables.size() > 0)
			{
				if (Dialogs.showYesNo(getApplication().getMainFrame(), MSG, TITLE))
				{
                    DeleteTablesCommand command = 
                        new DeleteTablesCommand(_tree, tables);
                    command.execute();
				}
			}
		}
	}
}
