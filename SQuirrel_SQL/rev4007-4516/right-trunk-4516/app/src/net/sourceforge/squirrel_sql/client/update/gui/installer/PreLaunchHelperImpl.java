
package net.sourceforge.squirrel_sql.client.update.gui.installer;

import java.io.IOException;

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallStatusListenerImpl;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class PreLaunchHelperImpl implements PreLaunchHelper
{

	
	private static String INSTALL_UPDATES_MESSAGE;

	
	private static String INSTALL_UPDATES_TITLE;

	private static String RESTORE_FROM_BACKUP_TITLE;
	
	private static String RESTORE_FROM_BACKUP_MESSAGE;
	
	private static String RESTORE_FAILED_MESSAGE;
	
	private static String BACKUP_FAILED_MESSAGE;
	
	private static String INSTALL_FAILED_MESSAGE;
	
	
	private StringManager s_stringMgr;

	
	private ILogger s_log;
	
	
	
	
	private UpdateUtil updateUtil = null;
	public void setUpdateUtil(UpdateUtil util) { this.updateUtil = util; }
	
		
	private ArtifactInstallerFactory artifactInstallerFactory = null;
	public void setArtifactInstallerFactory(ArtifactInstallerFactory artifactInstallerFactory)
	{
		this.artifactInstallerFactory = artifactInstallerFactory;
	}

	
	
	public PreLaunchHelperImpl() throws IOException {
		
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
		try
		{
			FileWrapper changeListFile = updateUtil.getChangeListFile();
			if (changeListFile.exists())
			{
				logInfo("Pre-launch update app detected a changeListFile to be processed");
				if (prompt)
				{
					if (showConfirmDialog(INSTALL_UPDATES_MESSAGE, INSTALL_UPDATES_TITLE))
					{
						installUpdates(changeListFile);
					} else
					{
						logInfo("User cancelled update installation");
					}
				} else
				{
					installUpdates(changeListFile);
				}
			} else {
				logInfo("installUpdates: changeList file ("+changeListFile+") doesn't exist.");
			}
		} catch (Throwable e)
		{
			String message = INSTALL_FAILED_MESSAGE + ": " + e.getMessage();
			s_log.error(message, e);
			showErrorDialog(message);
		}
		shutdown("Pre-launch update app finished");
	}

	
	public void restoreFromBackup()
	{
		if (showConfirmDialog(RESTORE_FROM_BACKUP_MESSAGE, RESTORE_FROM_BACKUP_TITLE)) {
			
			try {
				FileWrapper backupDir = updateUtil.getBackupDir();
				FileWrapper changeListFile = updateUtil.getFile(backupDir, UpdateUtil.CHANGE_LIST_FILENAME);
				ChangeListXmlBean changeList = updateUtil.getChangeList(changeListFile);
			
				ArtifactInstaller installer = artifactInstallerFactory.create(changeList);
				if (!installer.restoreBackupFiles()) {
					showErrorDialog(RESTORE_FAILED_MESSAGE);
					s_log.error("restoreFromBackup: "+RESTORE_FAILED_MESSAGE);
				}
				
			} catch (Throwable e) {
				s_log.error("Unexpected error while attempting restore from backup: " + e.getMessage(), e);
				showErrorDialog(RESTORE_FAILED_MESSAGE);
			}
			
		}
		shutdown("Pre-launch update app finished");
	}
	
	
		
		
	
	private void shutdown(String message) {
		if (s_log.isInfoEnabled())
		{
			s_log.info(message);
		}
		LoggerController.shutdown();
		System.exit(0);		
	}
	
	
	private void installUpdates(FileWrapper changeList) throws Exception
	{
		ArtifactInstaller installer = artifactInstallerFactory.create(changeList);
		installer.addListener(new InstallStatusListenerImpl());
		if (installer.backupFiles()) {
			installer.installFiles();
		} else {
			showErrorDialog(BACKUP_FAILED_MESSAGE);
		}
	}
	
	
	
	private boolean showConfirmDialog(String message, String title)
	{
		int choice =
			JOptionPane.showConfirmDialog(null,
				message,
				title,
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		return choice == JOptionPane.YES_OPTION;
	}

	
	private void showErrorDialog(String message) {
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	private void logInfo(String message) {
		if (s_log.isInfoEnabled()) {
			s_log.info(message);
		}
	}
}
