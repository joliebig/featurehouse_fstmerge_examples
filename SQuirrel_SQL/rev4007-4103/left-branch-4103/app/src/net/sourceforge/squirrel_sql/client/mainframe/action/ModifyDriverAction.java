package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;

import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.db.DriversListInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.db.IDriversList;

public class ModifyDriverAction extends SquirrelAction
{
	
	private static ILogger s_log =
		LoggerController.createLogger(ModifyDriverAction.class);

	
	private IDriversList _drivers;

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ModifyDriverAction.class);        
    
	
	public ModifyDriverAction(IApplication app, IDriversList list)
		throws IllegalArgumentException
	{
		super(app);
		if (list == null)
		{
			throw new IllegalArgumentException("Null DriversList passed");
		}
		_drivers = list;
	}

	
	public void actionPerformed(ActionEvent evt)
	{
		IApplication app = getApplication();
		DriversListInternalFrame tw = app.getWindowManager().getDriversListInternalFrame();
		tw.moveToFront();
		try
		{
			tw.setSelected(true);
		}
		catch (PropertyVetoException ex)
		{
            
			s_log.error(s_stringMgr.getString("ModifyDriverAction.error.selectingwindow"), ex);
		}
		ISQLDriver driver = _drivers.getSelectedDriver();
		if (driver != null)
		{
			new ModifyDriverCommand(app, driver).execute();
		}
	}
}
