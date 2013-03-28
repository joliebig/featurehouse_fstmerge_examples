package net.sourceforge.squirrel_sql.fw.gui;

import javax.swing.ListModel;

import java.io.File;

public interface IFileListBoxModel extends ListModel
{
	
	void addFile(File file);

	File removeFile(int idx);

	void insertFileAt(File file, int idx);

	
	String[] getFileNames();

	
	File getFile(int idx);
}
