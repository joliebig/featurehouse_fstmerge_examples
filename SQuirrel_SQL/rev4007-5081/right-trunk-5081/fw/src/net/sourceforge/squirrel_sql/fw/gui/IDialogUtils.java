
package net.sourceforge.squirrel_sql.fw.gui;

import java.awt.Component;
import java.awt.Frame;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JComponent;

import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;


public interface IDialogUtils
{

	File selectFileForWriting(Frame parentFrame, FileExtensionFilter[] filters);

	File selectFileForWriting(Frame parentFrame, FileExtensionFilter[] filters, JComponent accessory);

	void showNotYetImplemented(Component owner);

	boolean showYesNo(Component owner, String msg);

	boolean showYesNo(Component owner, String msg, String title);

	void showOk(Component owner, String msg);

	
	public String showInputDialog(Component parentComponent, Object message, String title, int messageType,
		Icon icon, Object[] selectionValues, Object initialSelectionValue);
}