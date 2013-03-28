package net.sourceforge.squirrel_sql.fw.gui;

import java.awt.Component;
import java.awt.Frame;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class Dialogs
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(Dialogs.class);

	public static File selectFileForWriting(Frame parentFrame,
							FileExtensionFilter[] filters)
	{
		return selectFileForWriting(parentFrame, filters, null);
	}

	public static File selectFileForWriting(Frame parentFrame,
							FileExtensionFilter[] filters, JComponent accessory)
	{
		File outFile = null;
		final JFileChooser chooser = new JFileChooser();
		if (filters != null)
		{
			for (int i = 0; i < filters.length; ++i)
			{
				chooser.addChoosableFileFilter(filters[i]);
			}
		}

		if (accessory != null)
		{
			chooser.setAccessory(accessory);
		}

		for (;;)
		{
			outFile = null;
			if (chooser.showSaveDialog(parentFrame) == JFileChooser.APPROVE_OPTION)
			{
				outFile = chooser.getSelectedFile();
				if (canSaveToFile(parentFrame, outFile))
				{
					break;
				}
			}
			else
			{
				break;
			}
		}

		return outFile;
	}

	public static void showNotYetImplemented(Component owner)
	{
		JOptionPane.showMessageDialog(owner, s_stringMgr.getString("Dialogs.nyi"),
										"", JOptionPane.INFORMATION_MESSAGE);
	}

	public static boolean showYesNo(Component owner, String msg)
	{
		return showYesNo(owner, msg, "");
	}

	public static boolean showYesNo(Component owner, String msg, String title)
	{
		int rc = JOptionPane.showConfirmDialog(owner, msg, title,
												JOptionPane.YES_NO_OPTION);
		return rc == JOptionPane.YES_OPTION;
	}

	public static void showOk(Component owner, String msg)
	{
		JOptionPane.showMessageDialog(owner, msg, "", JOptionPane.INFORMATION_MESSAGE);
	}

	private static boolean canSaveToFile(Frame parentFrame, File outFile)
	{
		if (!outFile.exists())
		{
			return true;
		}
		String msg = s_stringMgr.getString("Dialogs.alreadyexists",
											outFile.getAbsolutePath());
		if (!Dialogs.showYesNo(parentFrame, msg))
		{
			return false;
		}
		if (!outFile.canWrite())
		{
			msg = s_stringMgr.getString("Dialogs.cannotwrite",
											outFile.getAbsolutePath());
			Dialogs.showOk(parentFrame, msg);
			return false;
		}
		outFile.delete();
		return true;
	}
}
