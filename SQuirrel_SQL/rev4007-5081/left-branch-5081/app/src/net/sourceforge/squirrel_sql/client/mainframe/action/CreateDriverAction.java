package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.URL;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.db.DriversListInternalFrame;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;

public class CreateDriverAction extends SquirrelAction
{
	
	private static ILogger s_log =
		LoggerController.createLogger(CreateDriverAction.class);

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(CreateDriverAction.class);
    
	
	public CreateDriverAction(IApplication app)
	{
		super(app);
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
            
			s_log.error(s_stringMgr.getString("CreateDriverAction.error.selectingwindow"), ex);
		}
            
        try {
            final URL url = app.getResources().getDefaultDriversUrl();
            net.sourceforge.squirrel_sql.client.gui.db.DataCache cache = _app.getDataCache();
            ISQLDriver[] missingDrivers = cache.findMissingDefaultDrivers(url);
            if (missingDrivers != null) {
                String msg =
                    s_stringMgr.getString("CreateDriverAction.confirm");
                if (Dialogs.showYesNo(_app.getMainFrame(), msg)) {
                    for (int i = 0; i < missingDrivers.length; i++) {
                        try {
                            cache.addDriver(missingDrivers[i], null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                }
            }
        } catch (XMLException e) {
            
            String msg = 
                s_stringMgr.getString("CreateDriverAction.error.loadDefaultDrivers");
            s_log.error(msg, e);
        } catch (IOException e) {
            
            String msg = 
                s_stringMgr.getString("CreateDriverAction.error.loadDefaultDrivers");
            s_log.error(msg, e);
        }

		new CreateDriverCommand(app).execute();
	}
}
