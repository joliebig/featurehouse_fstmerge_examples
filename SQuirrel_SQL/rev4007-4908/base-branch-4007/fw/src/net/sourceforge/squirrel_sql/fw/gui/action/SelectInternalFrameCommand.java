package net.sourceforge.squirrel_sql.fw.gui.action;

import java.beans.PropertyVetoException;

import javax.swing.*;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

public class SelectInternalFrameCommand implements ICommand
{
	private final JInternalFrame _child;

	public SelectInternalFrameCommand(JInternalFrame child)
	{
		super();
		if (child == null)
		{
			throw new IllegalArgumentException("JInternalFrame == null");
		}
		_child = child;
	}

	public void execute()
	{
		try
		{
			if (!_child.isSelected())
			{
				if (!_child.isVisible())
				{
					_child.setVisible(true);
				}
				if(_child.isIcon())
				{
					_child.setIcon(false);
				}
				_child.setSelected(true);
            _child.requestFocus();
			}
		}
		catch (PropertyVetoException ignore)
		{
			
		}
	}
}