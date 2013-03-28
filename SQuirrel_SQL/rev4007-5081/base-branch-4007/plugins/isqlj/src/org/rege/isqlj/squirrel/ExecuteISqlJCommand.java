package org.rege.isqlj.squirrel;



import java.awt.Frame;
import java.io.*;

import javax.swing.JFileChooser;

import net.sourceforge.squirrel_sql.fw.gui.*;
import net.sourceforge.squirrel_sql.fw.util.*;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;

import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;

import org.rege.isqlj.JavaSql;
import org.rege.isqlj.ISqlJConnection;

public class ExecuteISqlJCommand
		implements ICommand
{
    private final ISession session;
    private ISqlJPlugin plugin;
    private final Frame frame;

    public ExecuteISqlJCommand( Frame frame, ISession session, ISqlJPlugin plugin)
        throws IllegalArgumentException 
	{
        super();
        if (session == null) 
		{
            throw new IllegalArgumentException("Null ISession passed");
        }
        if (plugin == null) 
		{
            throw new IllegalArgumentException("Null IPlugin passed");
        }
        this.frame = frame;
        this.session = session;
        this.plugin = plugin;
    }

    public void execute() throws BaseException 
	{
        if( session != null) 
		{
			String str = session.getSessionInternalFrame().getSQLPanelAPI().getSQLScriptToBeExecuted();
			try
			{
				JavaSql sqlj = new JavaSql();
				sqlj.getInterpreter().set( "session", new SqscConnection( session, plugin));
				sqlj.getInterpreter().set( "jdbc", 
						new ISqlJConnection( session.getSQLConnection().getConnection()));
				sqlj.exec( new StringReader( str));
			} catch( Exception ex)
			{
				throw new BaseException(ex);
			}
		}
    }

}


