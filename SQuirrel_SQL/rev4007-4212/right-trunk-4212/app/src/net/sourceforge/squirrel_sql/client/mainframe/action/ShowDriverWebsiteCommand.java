package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.awt.Frame;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class ShowDriverWebsiteCommand implements ICommand
{
	
	private final IApplication _app;

	
	private final ISQLDriver _sqlDriver;

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ShowDriverWebsiteCommand.class);
    
    
    private Frame _frame;

	
	public ShowDriverWebsiteCommand(IApplication app, ISQLDriver sqlDriver)
		throws IllegalArgumentException
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		if (sqlDriver == null)
		{
			throw new IllegalArgumentException("ISQLDriver == null");
		}

		_app = app;
		_sqlDriver = sqlDriver;
	}

	public void execute()
	{
        String url = _sqlDriver.getWebSiteUrl();
        if (url == null || "".equals(url)) {
            
            final Object[] args = {_sqlDriver.getName()};
            
            
            String msg = 
                s_stringMgr.getString("ShowDriverWebsiteCommand.comfirm", args);
            if (Dialogs.showYesNo(_frame, msg)) {
                new ModifyDriverCommand(_app, _sqlDriver).execute();
                url = _sqlDriver.getUrl();
            }
        } 
        if (url != null && !"".equals(url)) {
            _app.openURL(url);
        }
	}
}
