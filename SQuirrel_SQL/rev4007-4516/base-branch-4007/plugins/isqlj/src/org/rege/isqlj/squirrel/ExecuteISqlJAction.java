package org.rege.isqlj.squirrel;



import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;


public class ExecuteISqlJAction
		extends SquirrelAction 
		implements ISessionAction
{
    private ISession        session;
    private ISqlJPlugin     plugin;

    public ExecuteISqlJAction( IApplication app, Resources rsrc, ISqlJPlugin plugin)
            throws IllegalArgumentException 
	{
        super(app, rsrc);
        if (plugin ==  null) 
		{
            throw new IllegalArgumentException("null ISqlJPlugin passed");
        }
        this.plugin = plugin;
    }
    {
    }
	
    public void actionPerformed(ActionEvent evt) 
	{
        if (session != null) 
		{
			try
			{
            	new ExecuteISqlJCommand( getParentFrame(evt), session, plugin).execute();
			}
			catch (BaseException ex)
			{
				session.showErrorMessage(ex);
			}
        }
    }
    public void setSession(ISession session) 
	{
        this.session = session;
    }
	
}

