
package net.sourceforge.squirrel_sql.client.gui.session;

import javax.swing.Action;


public class ToolbarItem
{
	private Action action = null;

	
	public Action getAction()
	{
		return action;
	}

	
	public boolean isSeparator()
	{
		return isSeparator;
	}

	private boolean isSeparator = true;

	
	public ToolbarItem()
	{

	}

	
	public ToolbarItem(Action action)
	{
		this.action = action;
		this.isSeparator = false;
	}

}
