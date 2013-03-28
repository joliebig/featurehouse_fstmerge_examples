package net.sourceforge.squirrel_sql.fw.gui;

import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JComboBox;

public class DirectoryListComboBox extends JComboBox
{
	
	public DirectoryListComboBox()
	{
		super();
	}

	
	public void load(File dir)
	{
		load(dir, null);
	}

	
	public void load(File dir, FilenameFilter filter)
	{
		removeAllItems();
		if (dir != null && dir.isDirectory() && dir.canRead())
		{
			String[] files = null;
			if (filter == null)
			{
				files = dir.list();
			}
			else
			{
				files = dir.list(filter);
			}
			for (int i = 0; i < files.length; ++i)
			{
				addItem(files[i]);
			}
		}
	}
}
