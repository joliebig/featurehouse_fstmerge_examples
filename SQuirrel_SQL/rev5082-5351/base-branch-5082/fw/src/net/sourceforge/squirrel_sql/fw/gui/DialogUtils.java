
package net.sourceforge.squirrel_sql.fw.gui;

import java.awt.Component;
import java.awt.Frame;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;


public class DialogUtils implements IDialogUtils
{

	
	public File selectFileForWriting(Frame parentFrame, FileExtensionFilter[] filters)
	{
		return Dialogs.selectFileForWriting(parentFrame, filters);
	}

	
	public File selectFileForWriting(Frame parentFrame, FileExtensionFilter[] filters, JComponent accessory)
	{
		return Dialogs.selectFileForWriting(parentFrame, filters, accessory);
	}

	
	public void showNotYetImplemented(Component owner)
	{
		Dialogs.showNotYetImplemented(owner);
	}

	
	public void showOk(Component owner, String msg)
	{
		Dialogs.showOk(owner, msg);
	}

	
	public boolean showYesNo(Component owner, String msg)
	{
		return Dialogs.showYesNo(owner, msg);
	}

	
	public boolean showYesNo(Component owner, String msg, String title)
	{
		return Dialogs.showYesNo(owner, msg, title);
	}

	
	public String showInputDialog(Component parentComponent, Object message, String title, int messageType,
		Icon icon, Object[] selectionValues, Object initialSelectionValue)
	{
		String dbName =
			(String) JOptionPane.showInputDialog(parentComponent, message, title, messageType, icon,
				selectionValues, initialSelectionValue);
		return dbName;
	}
}
