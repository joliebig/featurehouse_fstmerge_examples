package net.sourceforge.squirrel_sql.client.session.mainpanel;

import java.awt.Component;

import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
public class SQLTab extends BaseMainPanelTab
{

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(SQLTab.class);  

	
	private SQLPanel _comp;

	public SQLTab(ISession session)
	{
		super();
		setSession(session);
	}

	
	public String getTitle()
	{
        
		return s_stringMgr.getString("SQLTab.title");
	}

	
	public String getHint()
	{
        
		return s_stringMgr.getString("SQLTab.hint");
	}

	
	public synchronized Component getComponent()
	{
		if (_comp == null)
		{
			_comp = new SQLPanel(getSession(), true);
		}
		return _comp;
	}

	
	public void setSession(ISession session)
	{
		super.setSession(session);
		getSQLPanel().setSession(session);
	}

	
	public synchronized void refreshComponent()
	{
		

	}

	
	public void sessionClosing(ISession session)
	{
		if (_comp != null)
		{
			_comp.sessionClosing();
		}
	}

	
	public synchronized void select()
	{
		super.select();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				_comp.getSQLEntryPanel().requestFocus();
			}
		});
	}

	public SQLPanel getSQLPanel()
	{
		return (SQLPanel)getComponent();
	}
}
