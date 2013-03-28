package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.db.AliasesListInternalFrame;

public class CreateAliasAction extends SquirrelAction
{
	
	private static ILogger s_log =
		LoggerController.createLogger(CreateAliasAction.class);

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(CreateAliasAction.class);    
    
	
	public CreateAliasAction(IApplication app)
	{
		super(app);
	}

	
	public void actionPerformed(ActionEvent evt)
	{
		IApplication app = getApplication();
		AliasesListInternalFrame tw = app.getWindowManager().getAliasesListInternalFrame();
		tw.moveToFront();
		try
		{
			tw.setSelected(true);
		}
		catch (PropertyVetoException ex)
		{
            
			s_log.error(s_stringMgr.getString("CreateAliasAction.error.selectingwindow"), ex);
		}
		new CreateAliasCommand(getApplication()).execute();
	}
}
