package net.sourceforge.squirrel_sql.client.session.mainpanel;


import javax.swing.JComponent;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;


public interface ISQLExecuter
{
	
	public String getTitle();

	public JComponent getComponent();

	public void execute(ISQLEntryPanel parent);
}