package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;

public class CreateDataScriptOfCurrentSQLAction extends SquirrelAction implements ISQLPanelAction{

    private static final long serialVersionUID = 1L;

    
    private ISession _session;

	
	private final SQLScriptPlugin _plugin;

    public CreateDataScriptOfCurrentSQLAction(IApplication app, Resources rsrc,
    											SQLScriptPlugin plugin) {
        super(app, rsrc);
        _plugin = plugin;
    }

    public void actionPerformed(ActionEvent evt) {
        if (_session != null) {
            new CreateDataScriptOfCurrentSQLCommand(_session, _plugin).execute();
        }
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
}
