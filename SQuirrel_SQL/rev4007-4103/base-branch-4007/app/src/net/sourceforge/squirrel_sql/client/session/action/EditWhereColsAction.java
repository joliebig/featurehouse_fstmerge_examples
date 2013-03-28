package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class EditWhereColsAction extends SquirrelAction
								implements IObjectTreeAction
{
	
	private IObjectTreeAPI _tree;

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(EditWhereColsAction.class);

    
	
	public EditWhereColsAction(IApplication app)
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
		final IApplication app = getApplication();
		if (_tree != null)
		{
			
			
			IDatabaseObjectInfo selectedObjects[] =	_tree.getSelectedDatabaseObjects();
			int objectTotal = selectedObjects.length;

			if ((objectTotal == 1)
				&& (selectedObjects[0].getDatabaseObjectType()
					== DatabaseObjectType.TABLE))
			{
				CursorChanger cursorChg = new CursorChanger(getApplication().getMainFrame());
				cursorChg.show();
				try
				{
					new EditWhereColsCommand(app, _tree, selectedObjects[0]).execute();
				}
				finally
				{
					cursorChg.restore();
				}
			}
			else
			{
                
                
                
                String msg = 
                    s_stringMgr.getString("EditWhereColsAction.singleObjectMessage");
				_tree.getSession().showMessage(msg);
			}
		}
	}
}
