package net.sourceforge.squirrel_sql.fw.gui.action;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JPopupMenu;

public abstract class BaseAction extends AbstractAction
{
	
	public interface IBaseActionPropertyNames
	{
		String DISABLED_ICON = "squirrelDisabledIcon";
		String ROLLOVER_ICON = "squirrelRolloverIcon";
	}

	
	protected BaseAction()
	{
		super();
	}

	
	protected BaseAction(String title)
	{
		super(title);
	}

	
	protected BaseAction(String title, Icon icon)
	{
		super(title, icon);
	}

	
	protected Frame getParentFrame(ActionEvent evt)
	{
		Frame parent = null;
		if (evt != null)
		{
			final Object src = evt.getSource();
			if (src instanceof Component)
			{
				Component comp = (Component)src;
				while (comp != null && parent == null)
				{
					if (comp instanceof Frame)
					{
						parent = (Frame)comp;
					}
					else if (comp instanceof JPopupMenu)
					{
						comp = ((JPopupMenu)comp).getInvoker();
					}
					else
					{
						comp = comp.getParent();
					}
				}
			}
		}
		return parent;
	}
}
