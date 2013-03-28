package net.sourceforge.squirrel_sql.fw.gui;

import javax.swing.JList;

import java.io.File;

public class FileListBox extends JList
{
	
	public FileListBox()
	{
		this(new ClassPathListModel());
	}

	
	public FileListBox(IFileListBoxModel model)
	{
		super(model);
	}

	
	public File getSelectedFile()
	{
		return (File)getSelectedValue();
	}

	public IFileListBoxModel getTypedModel()
	{
		return (IFileListBoxModel)getModel();
	} 
}
