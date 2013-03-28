package net.sourceforge.squirrel_sql.plugins.dataimport.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.Resources;


public class ImportTableDataAction extends SquirrelAction implements ISessionAction {
	private static final long serialVersionUID = -8807675482631144975L;
	private ISession session;
	
	
	public ImportTableDataAction(IApplication app, Resources resources) {
		super(app, resources);
	}

	
	public void setSession(ISession session) {
		this.session = session;
	}

	
	public void actionPerformed(ActionEvent ev) {
		if (session != null)
		{
			IObjectTreeAPI treeAPI = session.getSessionInternalFrame().getObjectTreeAPI();
			IDatabaseObjectInfo[] tables = treeAPI.getSelectedDatabaseObjects();
			if (tables.length == 1 && tables[0] instanceof ITableInfo) {
			try
			{
				new ImportTableDataCommand(session, (ITableInfo) tables[0]).execute();
			}
			catch (Throwable th)
			{
				session.showErrorMessage(th);
			}
			}
			else
			{
				session.showErrorMessage("Wrong object");
			}
		}
	}

}
