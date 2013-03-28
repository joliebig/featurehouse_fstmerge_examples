package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class SQLFilterAction extends SquirrelAction implements IObjectTreeAction
{
    private static final long serialVersionUID = 1L;

    transient private IObjectTreeAPI _tree;

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(SQLFilterAction.class);
    
	
	public SQLFilterAction(IApplication app)
	{
		super(app);
	}

	
	public void setObjectTree(IObjectTreeAPI tree)
	{
		_tree = tree;
		GUIUtils.processOnSwingEventThread(new Runnable() {
		    public void run() {
		        setEnabled(null != _tree);
		    }
		});
      
	}

	
	public void actionPerformed(ActionEvent evt)
	{
		if (_tree != null)
		{
			
			final IDatabaseObjectInfo selObjs[] =	_tree.getSelectedDatabaseObjects();
			final int objectTotal = selObjs.length;

			if ( (objectTotal == 1) &&
			        (
			                (selObjs[0].getDatabaseObjectType() == DatabaseObjectType.TABLE) ||
			                (selObjs[0].getDatabaseObjectType() == DatabaseObjectType.VIEW)
			        )
			)
			{
			    final IApplication app = getApplication();

			    final CursorChanger cursorChg = new CursorChanger(app.getMainFrame());
			    cursorChg.show();
			    try
			    {
			        new SQLFilterCommand(_tree, selObjs[0]).execute();
			    }
			    finally
			    {
			        cursorChg.restore();
			    }
			}
			else
			{
                
                
                String msg = 
                    s_stringMgr.getString("SQLFilterAction.singleObjectMessage");
			    _tree.getSession().showMessage(msg);
			}
		}
	}
}
