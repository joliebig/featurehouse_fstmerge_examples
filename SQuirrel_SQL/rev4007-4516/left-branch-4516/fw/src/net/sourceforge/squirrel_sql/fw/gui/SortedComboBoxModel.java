package net.sourceforge.squirrel_sql.fw.gui;

import javax.swing.DefaultComboBoxModel;

public class SortedComboBoxModel extends DefaultComboBoxModel
{
	
	public SortedComboBoxModel()
	{
		super();
	}

	
	public void insertElementAt(int index, Object obj)
	{
		addElement(obj);
	}

	
	public void addElement(Object obj)
	{
		super.insertElementAt(obj, getIndexInList(obj));
	}

	
	protected int getIndexInList(Object obj)
	{
		final int limit = getSize();
		final String objStr = obj.toString();
		for (int i = 0; i < limit; ++i)
		{
			if (objStr.compareToIgnoreCase(getElementAt(i).toString()) <= 0)
			{
				return i;
			}
		}
		return limit;
	}
}
