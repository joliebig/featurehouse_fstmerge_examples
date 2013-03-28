package net.sourceforge.squirrel_sql.fw.gui;

import javax.swing.DefaultListModel;

public class SortedListModel extends DefaultListModel
{
	
	public SortedListModel()
	{
		super();
	}

	
	public void add(int index, Object obj)
	{
		addElement(obj);
	}

	
	public void insertElementAt(int index, Object obj)
	{
		addElement(obj);
	}

	
	public void addElement(Object obj)
	{
		super.add(getIndexInList(obj), obj);
	}

	public Object remove(int index)
	{
		Object obj = get(index);
		removeElement(obj);
		return obj;
	}

	public void removeElementAt(int index)
	{
		removeElement(get(index));
	}

	public void removeRange(int fromIndex, int toIndex)
	{
		for (int i = fromIndex; i <= toIndex; ++i)
		{
			remove(i);
		}
	}

	
	protected int getIndexInList(Object obj)
	{
		final int limit = getSize();
		final String objStr = obj.toString();
		for (int i = 0; i < limit; ++i)
		{
			if (objStr.compareToIgnoreCase(get(i).toString()) <= 0)
			{
				return i;
			}
		}
		return limit;
	}
}
