
package net.sourceforge.squirrel_sql.client.update.gui.installer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallStatusListener;
import net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallStatusListenerImpl;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class PreLaunchHelperImpl implements PreLaunchHelper
{

	
	private String INSTALL_UPDATES_MESSAGE;

	
	private String INSTALL_UPDATES_TITLE;

	private String RESTORE_FROM_BACKUP_TITLE;

	private String RESTORE_FROM_BACKUP_MESSAGE;

	private String RESTORE_FAILED_MESSAGE;

	private String BACKUP_FAILED_MESSAGE;

	private String INSTALL_FAILED_MESSAGE;

	
	private StringManager s_stringMgr;

	
	private ILogger s_log;

	

	
	private UpdateUtil updateUtil = null;

	public void setUpdateUtil(UpdateUtil util)
	{
		this.updateUtil = util;
	}

	
	private ArtifactInstallerFactory artifactInstallerFactory = null;

	public void setArtifactInstallerFactory(ArtifactInstallerFactory artifactInstallerFactory)
	{
		this.artifactInstallerFactory = artifactInstallerFactory;
	}

	

	public PreLaunchHelperImpl() throws IOException
	{

		s_log = LoggerController.createLogger(PreLaunchHelperImpl.class);
		s_stringMgr = StringManagerFactory.getStringManager(PreLaunchHelperImpl.class);

		
		INSTALL_UPDATES_TITLE = s_stringMgr.getString("PreLaunchHelperImpl.installUpdatesTitle");

		
		INSTALL_UPDATES_MESSAGE = s_stringMgr.getString("PreLaunchHelperImpl.installUpdatesMessage");

		
		RESTORE_FROM_BACKUP_TITLE = s_stringMgr.getString("PreLaunchHelperImpl.restoreFromBackupTitle");

		
		
		RESTORE_FROM_BACKUP_MESSAGE = s_stringMgr.getString("PreLaunchHelperImpl.restoreFromBackupMessage");

		
		
		BACKUP_FAILED_MESSAGE = s_stringMgr.getString("PreLaunchHelperImpl.backupFailedMessage");

		
		INSTALL_FAILED_MESSAGE = s_stringMgr.getString("PreLaunchHelperImpl.installFailedMessage");

		
		
		RESTORE_FAILED_MESSAGE = s_stringMgr.getString("PreLaunchHelperImpl.restoreFailedMessage");

	}

	
	public void installUpdates(boolean prompt)
	{
		FileWrapper changeListFile = updateUtil.getChangeListFile();
		if (hasChangesToBeApplied(changeListFile))
		{
			logInfo("Pre-launch update app detected a changeListFile to be processed");
			if (prompt)
			{
				if (showConfirmDialog(INSTALL_UPDATES_MESSAGE, INSTALL_UPDATES_TITLE))
				{
					installUpdates(changeListFile);
				}
				else
				{
					logInfo("User cancelled update installation");
				}
			}
			else
			{
				installUpdates(changeListFile);
			}
		}
	}

	
	private boolean hasChangesToBeApplied(final FileWrapper changeListFile)
	{
		boolean result = false;
		try
		{
			if (changeListFile.exists())
			{
				final ChangeListXmlBean changeListBean = updateUtil.getChangeList(changeListFile);
				final List<ArtifactStatus> changeList = changeListBean.getChanges();
				final int changeListSize = changeList.size();
				logInfo("hasChangesToBeApplied: changeListFile (" + changeListSize + ") has " + changeListSize
					+ " changes to be applied");

				if (changeList != null && changeListSize > 0)
				{
					result = true;
				}
				else
				{
					logInfo("Aborting update: changeList was found with no updates");
				}
			}
			else
			{
				logInfo("installUpdates: changeList file (" + changeListFile + ") doesn't exist");
			}
		}
		catch (FileNotFoundException e)
		{
			s_log.error("hasChangesToBeApplied: unable to get change list from file (" + changeListFile + "): "
				+ e.getMessage());
		}

		return result;
	}

	
	public void restoreFromBackup()
	{
		if (showConfirmDialog(RESTORE_FROM_BACKUP_MESSAGE, RESTORE_FROM_BACKUP_TITLE))
		{

			try
			{
				FileWrapper backupDir = updateUtil.getBackupDir();
				FileWrapper changeListFile = updateUtil.getFile(backupDir, UpdateUtil.CHANGE_LIST_FILENAME);
				ChangeListXmlBean changeList = updateUtil.getChangeList(changeListFile);

				ArtifactInstaller installer = artifactInstallerFactory.create(changeList, null);
				if (!installer.restoreBackupFiles())
				{
					showErrorDialog(RESTORE_FAILED_MESSAGE);
					s_log.error("restoreFromBackup: " + RESTORE_FAILED_MESSAGE);
				}

			}
			catch (Throwable e)
			{
				s_log.error("Unexpected error while attempting restore from backup: " + e.getMessage(), e);
				showErrorDialog(RESTORE_FAILED_MESSAGE);
			}

		}
		shutdown("Pre-launch update app finished");
	}

	

	
	private void shutdown(String message)
	{
		logInfo(message);
		LoggerController.shutdown();
		System.exit(0);
	}

	
	private void installUpdates(final FileWrapper changeList)
	{
		Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					ProgressDialogController controller = new ProgressDialogControllerImpl();
					InstallStatusListener listener = new InstallStatusListenerImpl(controller);
					ArtifactInstaller installer = artifactInstallerFactory.create(changeList, listener);
					if (installer.backupFiles())
					{
						installer.installFiles();
					}
					else
					{
						showErrorDialog(BACKUP_FAILED_MESSAGE);
					}
					controller.hideProgressDialog();
					shutdown("Pre-launch update app finished");
				}
				catch (Throwable e)
				{
					String message = INSTALL_FAILED_MESSAGE + ": " + e.getMessage();
					s_log.error(message, e);
					showErrorDialog(message);
				}

			}

		});
		t.setName("Update Installer Thread");
		t.start();
	}

	
	private boolean showConfirmDialog(String message, String title)
	{
		int choice =
			JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		return choice == JOptionPane.YES_OPTION;
	}

	
	private void showErrorDialog(String message)
	{
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	private void logInfo(String message)
	{
		if (s_log.isInfoEnabled())
		{
			s_log.info(message);
		}
	}

}
