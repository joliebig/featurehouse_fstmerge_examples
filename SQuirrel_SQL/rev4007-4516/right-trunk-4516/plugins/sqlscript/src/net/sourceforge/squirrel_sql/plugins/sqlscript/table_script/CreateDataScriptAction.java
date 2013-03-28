package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.action.IObjectTreeAction;

public class CreateDataScriptAction extends SquirrelAction implements IObjectTreeAction {

    
    private ISession _session;

	
	private final SQLScriptPlugin _plugin;

    public CreateDataScriptAction(IApplication app, Resources rsrc, SQLScriptPlugin plugin) {
        super(app, rsrc);
        _plugin = plugin;
    }

    public void actionPerformed(ActionEvent evt) {
        if (_session != null) {
            new CreateDataScriptCommand(_session, _plugin, false).execute();
        }
    }

   public void setObjectTree(IObjectTreeAPI tree)
   {
      if(null != tree)
      {
         _session = tree.getSession();
      }
      else
      {
         _session = null;
      }
      setEnabled(null != _session);
   }
}
