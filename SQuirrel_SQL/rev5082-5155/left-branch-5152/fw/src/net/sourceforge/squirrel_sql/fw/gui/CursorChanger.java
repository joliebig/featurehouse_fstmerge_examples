package net.sourceforge.squirrel_sql.fw.gui;

import java.awt.Component;
import java.awt.Cursor;

public class CursorChanger
{
	private final Component _comp;
	private final Cursor _newCursor;

	public CursorChanger(Component comp)
	{
		this(comp, Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	public CursorChanger(Component comp, Cursor newCursor)
	{
		super();

		if (newCursor == null)
		{
			throw new IllegalArgumentException("null Cursor passed");
		}
		if (comp == null)
		{
			throw new IllegalArgumentException("null Component passed");
		}

		_comp = comp;
		_newCursor = newCursor;
	}

	public void show()
	{
		_comp.setCursor(_newCursor);
	}

	public void restore()
	{
		_comp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

}
