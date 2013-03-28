package net.sourceforge.squirrel_sql.plugins.editextras;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;

class FormatSQLAction extends SquirrelAction
					implements ISQLPanelAction
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(FormatSQLAction.class);

	
	private static final ILogger s_log =
		LoggerController.createLogger(FormatSQLAction.class);

	
	private ISession _session;

	private EditExtrasPlugin _plugin;

	FormatSQLAction(IApplication app, EditExtrasPlugin plugin)
	{
		super(app, plugin.getResources());
		_plugin = plugin;
	}
   
   public void setSQLPanel(ISQLPanelAPI panel)
   {
      if(null != panel)
      {
         _session = panel.getSession();
      }
      else
      {
         _session = null;
      }
      setEnabled(null != _session);
   }


	public void actionPerformed(ActionEvent evt)
	{
		if (_session != null)
		{
			try
			{
				new FormatSQLCommand(_session, _plugin).execute();
			}
			catch (Throwable ex)
			{
				
				final String msg = s_stringMgr.getString("editextras.errorProcessingFormat", ex);
				_session.showErrorMessage(msg);
				s_log.error(msg, ex);
			}
		}
	}

}
