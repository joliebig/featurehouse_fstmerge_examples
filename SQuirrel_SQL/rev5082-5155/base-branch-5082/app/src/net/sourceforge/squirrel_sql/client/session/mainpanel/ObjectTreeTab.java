package net.sourceforge.squirrel_sql.client.session.mainpanel;

import java.awt.Component;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class ObjectTreeTab extends BaseMainPanelTab
{
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ObjectTreeTab.class);   
    
	
	public ObjectTreeTab()
	{
		super();
	}

	
	private ObjectTreePanel _comp;

	
	public String getTitle()
	{
        
		return s_stringMgr.getString("ObjectTreeTab.title");
	}

	
	public String getHint()
	{
        
		return s_stringMgr.getString("ObjectTreeTab.hint");
	}

	
	protected void refreshComponent()
	{
		
	}

	
	public synchronized Component getComponent()
	{
		if (_comp == null)
		{
			_comp = new ObjectTreePanel(getSession());
		}
		return _comp;
	}

}
