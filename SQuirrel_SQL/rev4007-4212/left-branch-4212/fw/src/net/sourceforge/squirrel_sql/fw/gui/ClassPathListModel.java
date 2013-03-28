package net.sourceforge.squirrel_sql.fw.gui;

import java.io.File;
import java.util.StringTokenizer;

public class ClassPathListModel extends DefaultFileListBoxModel
{
	
	public ClassPathListModel()
	{
		super();
		load();
	}

	
	private void load()
	{
		removeAllElements();
		String cp = System.getProperty("java.class.path");
		StringTokenizer strtok = new StringTokenizer(cp, File.pathSeparator);
		while (strtok.hasMoreTokens())
		{
			addFile(new File(strtok.nextToken()));
		}
	}
}
