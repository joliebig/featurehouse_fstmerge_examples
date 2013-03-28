package net.sourceforge.squirrel_sql.fw.gui;

import javax.swing.DefaultListModel;

import java.io.File;


public class DefaultFileListBoxModel extends DefaultListModel
										implements IFileListBoxModel
{
	
	public DefaultFileListBoxModel()
	{
		super();
	}

	public void addFile(File file)
	{
		addElement(file);
	}

	
	public File getFile(int idx)
	{
		return (File)get(idx);
	}
	
	
	public String[] getFileNames()
	{
		String[] fileNames = new String[getSize()];
		for (int i = 0, limit = fileNames.length; i < limit; ++i)
		{
			fileNames[i] = getFile(i).getAbsolutePath();
		}
		return fileNames;
	}

	




	public void insertFileAt(File file, int idx)
	{
		insertElementAt(file, idx);
	}


	public File removeFile(int idx)
	{
		return (File)remove(idx);
	}

}
