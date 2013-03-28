
package net.sourceforge.squirrel_sql.plugins.sqlreplace;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.action.SelectInternalFrameCommand;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class SQLReplaceExecutionListener implements ISQLExecutionListener {

	private final static ILogger log = LoggerController.createLogger(SQLReplacePlugin.class);
	private ISession session = null;
	private SQLReplacePlugin plugin = null;
	
	public SQLReplaceExecutionListener() {
		
	}

	
	public SQLReplaceExecutionListener(SQLReplacePlugin plugin, ISession session) {
		this.session = session;
		this.plugin = plugin;
	}

	
	public void statementExecuted(String sql) {
		

	}

	
	public String statementExecuting(String sql) {
		StringBuffer buffer = new StringBuffer(sql);

		
		ReplacementManager repMan = plugin.getReplacementManager();
		String replacedStmnt = repMan.replace(buffer);
		
		GUIUtils.processOnSwingEventThread(new Runnable() {
			public void run() {
				new SelectInternalFrameCommand(session.getActiveSessionWindow()).execute();
			}
		});
		
		return replacedStmnt;
	}

}
