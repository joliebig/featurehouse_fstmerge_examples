
package net.sourceforge.squirrel_sql.client.plugin;

import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class PluginSessionCallbackAdaptor implements PluginSessionCallback
{

	
	private final static ILogger s_log = LoggerController.createLogger(PluginSessionCallbackAdaptor.class);

	
	IPlugin _plugin = null;

	
	public PluginSessionCallbackAdaptor(IPlugin plugin)
	{
		Utilities.checkNull("PluginSessionCallbackAdaptor.init", "plugin", plugin);
		_plugin = plugin;
	}

	
	public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
	{
		if (s_log.isDebugEnabled())
		{
			s_log.debug("objectTreeInternalFrameOpened: " + _plugin.getDescriptiveName() + " doesn't provide "
				+ "special handling for newly opened interal ObjectTree frames");
		}
	}

	
	public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
	{
		if (s_log.isDebugEnabled())
		{
			s_log.debug("objectTreeInternalFrameOpened: " + _plugin.getDescriptiveName() + " doesn't provide "
				+ "special handling for newly opened interal SQL frames");
		}
	}

}
