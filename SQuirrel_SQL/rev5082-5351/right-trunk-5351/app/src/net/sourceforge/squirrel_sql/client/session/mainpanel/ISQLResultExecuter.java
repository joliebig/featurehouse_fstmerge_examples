package net.sourceforge.squirrel_sql.client.session.mainpanel;

import javax.swing.JComponent;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;

public interface ISQLResultExecuter
{
	
	public static interface ISQLResultExecuterFactory
	{
		public ISQLResultExecuter createSQLResultExecuter(ISession session,
															ISQLPanelAPI sqlpanel);
	}

	
	public String getTitle();

	public JComponent getComponent();

	public void execute(ISQLEntryPanel parent);
    
    
    public IResultTab getSelectedResultTab();
}