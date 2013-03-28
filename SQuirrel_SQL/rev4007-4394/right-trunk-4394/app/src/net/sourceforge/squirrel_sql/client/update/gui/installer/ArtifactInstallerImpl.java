
package net.sourceforge.squirrel_sql.client.update.gui.installer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactAction;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallEventType;
import net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallStatusEvent;
import net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallStatusEventFactory;
import net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallStatusListener;
import net.sourceforge.squirrel_sql.client.update.gui.installer.util.InstallFileOperationInfo;
import net.sourceforge.squirrel_sql.client.update.gui.installer.util.InstallFileOperationInfoFactory;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class ArtifactInstallerImpl implements ArtifactInstaller
{

	
	private static ILogger s_log = LoggerController.createLogger(ArtifactInstallerImpl.class);


	
	private ChangeListXmlBean _changeListBean = null;

	
	private List<InstallStatusListener> _listeners = new ArrayList<InstallStatusListener>();

	
	private File updateDir = null;

	
	
	
	private File downloadsRootDir = null;
	
	
	private File coreDownloadsDir = null;

	
	private File pluginDownloadsDir = null;
	
	
	private File i18nDownloadsDir = null;	
	
	

	
	private File backupRootDir = null;

	
	private File coreBackupDir = null;

	
	private File pluginBackupDir = null;

	
	private File translationBackupDir = null;

	

	
	private File installRootDir = null;

	
	private File coreInstallDir = null;

	
	private File pluginInstallDir = null;

	
	private File i18nInstallDir = null;
	
	
	private File changeListFile = null;
	
	

	
	private InstallStatusEventFactory installStatusEventFactory = null;
	public void setInstallStatusEventFactory(InstallStatusEventFactory installStatusEventFactory)
	{
		this.installStatusEventFactory = installStatusEventFactory;
	}

	
	private InstallFileOperationInfoFactory installFileOperationInfoFactory = null;
	public void setInstallFileOperationInfoFactory(
		InstallFileOperationInfoFactory installFileOperationInfoFactory)
	{
		this.installFileOperationInfoFactory = installFileOperationInfoFactory;
	}

	
	private UpdateUtil _util = null;
	public void setUpdateUtil(UpdateUtil util)
	{
		this._util = util;
		updateDir = _util.getSquirrelUpdateDir();
		backupRootDir = _util.checkDir(updateDir, UpdateUtil.BACKUP_ROOT_DIR_NAME);

		coreBackupDir = _util.checkDir(backupRootDir, UpdateUtil.CORE_ARTIFACT_ID);
		pluginBackupDir = _util.checkDir(backupRootDir, UpdateUtil.PLUGIN_ARTIFACT_ID);
		translationBackupDir = _util.checkDir(backupRootDir, UpdateUtil.TRANSLATION_ARTIFACT_ID);

		installRootDir = _util.getSquirrelHomeDir();

		coreInstallDir = _util.getSquirrelLibraryDir();
		pluginInstallDir = _util.getSquirrelPluginsDir();
		i18nInstallDir = _util.getSquirrelLibraryDir();
		
		coreDownloadsDir = _util.getCoreDownloadsDir();
		pluginDownloadsDir = _util.getPluginDownloadsDir();
		i18nDownloadsDir = _util.getI18nDownloadsDir();
	}	
	
	
	
	public void setChangeList(ChangeListXmlBean changeList) throws FileNotFoundException
	{
		_changeListBean = changeList;
	}

	
	public File getChangeListFile()
	{
		return changeListFile;
	}

	
	public void setChangeListFile(File changeListFile)
	{
		this.changeListFile = changeListFile;
	}
	
	
	public void addListener(InstallStatusListener listener)
	{
		_listeners.add(listener);
	}

	
	public boolean backupFiles() throws FileNotFoundException, IOException
	{
		boolean result = true;
		sendBackupStarted();

		List<ArtifactStatus> stats = _changeListBean.getChanges();
		for (ArtifactStatus status : stats)
		{
			String artifactName = status.getName();
			String artifactType = status.getType();
			
			if (!status.isInstalled())
			{
				if (s_log.isInfoEnabled())
				{
					s_log.info("Skipping backup of artifact (" + status + ") which isn't installed.");
				}
				continue;
			}
			if (status.isCoreArtifact())
			{

				File installDir = coreInstallDir;
				if (artifactName.equals("squirrel-sql.jar"))
				{
					installDir = installRootDir;
				}
				File coreFile = _util.getFile(installDir, artifactName);
				File backupFile = _util.getFile(coreBackupDir, artifactName);
				_util.copyFile(coreFile, backupFile);
			}
			if (status.isPluginArtifact())
			{
				
				File pluginBackupFile = _util.getFile(pluginBackupDir, artifactName);
				String pluginDirectory = artifactName.replace(".zip", "");
				String pluginJarFilename = artifactName.replace(".zip", ".jar");
				File[] sourceFiles = new File[2];
				sourceFiles[0] = _util.getFile(pluginInstallDir, pluginDirectory);
				sourceFiles[1] = _util.getFile(pluginInstallDir, pluginJarFilename);
				_util.createZipFile(pluginBackupFile, sourceFiles);
			}
			if (status.isTranslationArtifact())
			{
				File translationFile = _util.getFile(i18nInstallDir, artifactName);
				File backupFile = _util.getFile(translationBackupDir, artifactName);
				if (_util.fileExists(translationFile))
				{
					_util.copyFile(translationFile, backupFile);
				}
			}
		}

		sendBackupComplete();
		return result;
	}

	
	public void installFiles() throws IOException
	{
		sendInstallStarted();

		List<File> filesToRemove = new ArrayList<File>();
		List<InstallFileOperationInfo> filesToInstall = new ArrayList<InstallFileOperationInfo>();

		for (ArtifactStatus status : _changeListBean.getChanges())
		{
			ArtifactAction action = status.getArtifactAction();
			File installDir = null;
			File fileToCopy = null;
			File fileToRemove = null;
			String artifactName = status.getName();
			boolean isPlugin = false;

			if (status.isCoreArtifact())
			{
				if (action == ArtifactAction.REMOVE)
				{
					s_log.error("Skipping core artifact (" + status.getName() + ") that was marked for removal");
					continue;
				}

				
				
				if ("squirrel-sql.jar".equals(status.getName()))
				{
					installDir = installRootDir;
				}
				else
				{
					installDir = coreInstallDir;
				}
				fileToCopy = _util.getFile(coreDownloadsDir, artifactName);
				fileToRemove = _util.getFile(installDir, artifactName);
				filesToRemove.add(fileToRemove);
			}
			if (status.isPluginArtifact())
			{
				isPlugin = true;
				installDir = pluginInstallDir;
				if (action != ArtifactAction.REMOVE) {
					fileToCopy = _util.getFile(pluginDownloadsDir, artifactName);
				}
				
				
				
				String jarFileToRemove = artifactName.replace(".zip", ".jar");
				String pluginDirectoryToRemove = artifactName.replace(".zip", "");

				filesToRemove.add(_util.getFile(installDir, jarFileToRemove));
				filesToRemove.add(_util.getFile(installDir, pluginDirectoryToRemove));
			}
			if (status.isTranslationArtifact())
			{
				installDir = i18nInstallDir;
				if (action != ArtifactAction.REMOVE) {
					fileToCopy = _util.getFile(i18nDownloadsDir, artifactName);
				}
				fileToRemove = _util.getFile(installDir, artifactName);
				filesToRemove.add(fileToRemove);
			}
			if (fileToCopy != null) {
				InstallFileOperationInfo info = installFileOperationInfoFactory.create(fileToCopy, installDir);
				info.setPlugin(isPlugin);
				filesToInstall.add(info);
			}
		}
		boolean success = removeOldFiles(filesToRemove);
		success = success && installFiles(filesToInstall);
		success = success && moveChangeListFile();
		
		if (!success) {
			restoreFilesFromBackup(filesToInstall);
		} 
		
		sendInstallComplete();
	}

	private void restoreFilesFromBackup(List<InstallFileOperationInfo> filesToInstall)
	{
		
		
	}


	private boolean moveChangeListFile()
	{
		boolean result = true;
		if (changeListFile != null) {
			try {
				_util.copyFile(changeListFile, backupRootDir);
				result = _util.deleteFile(changeListFile);
			} catch (IOException e) {
				result = false;
				s_log.error("Unexpected exception: "+e.getMessage(), e);
			}
		} else {
			if (s_log.isInfoEnabled()) {
				s_log.info("moveChangeListFile: Changelist file was null.  Skipping move");
			}
		}
		return result;
	}

	

	
	private boolean removeOldFiles(List<File> filesToRemove)
	{
		boolean result = true;
		for (File fileToRemove : filesToRemove) {
			result = removeOldFile(fileToRemove);
			if (!result) {
				break;
			}
		}
		return result;
	}

	
	private boolean removeOldFile(File fileToRemove)
	{
		boolean result = true;
		String absolutePath = fileToRemove.getAbsolutePath();
		if (s_log.isDebugEnabled()) {
			s_log.debug("Examining file to remove: "+absolutePath);
		}
		if (fileToRemove.exists()) {		
			try {
				if (s_log.isDebugEnabled()) {
					s_log.debug("Attempting to delete file: "+absolutePath);
				}			
				result = _util.deleteFile(fileToRemove);
				if (!result) {
					s_log.error("Delete operation failed for file/directory: "+absolutePath);
				}
			} catch (SecurityException e) {
				result = false;
				s_log.error("Unexpected security exception: "+e.getMessage());
			}
		} else {
			if (s_log.isInfoEnabled()) {
				s_log.info("Skipping delete of file doesn't appear to exist: "+absolutePath);
			}
		}
		return result;
	}

	private boolean installFiles(List<InstallFileOperationInfo> filesToInstall) throws IOException
	{
		boolean result = true;
		for (InstallFileOperationInfo info : filesToInstall) {
			File installDir = info.getInstallDir();
			File fileToCopy = info.getFileToInstall();
			try {
				installFile(installDir, fileToCopy);
			} catch (Exception e) {
				s_log.error("installFiles: unexpected exception: "+e.getMessage(), e);
				result = false;
				break;
			}
		}
		return result;
	}

	private void installFile(File installDir, File fileToCopy) throws IOException
	{
		if (fileToCopy.getAbsolutePath().endsWith(".zip")) {
			
			
			_util.extractZipFile(fileToCopy, installDir);
		} else {
			
			_util.copyFile(fileToCopy, installDir);
		}
				
	}

	private void sendBackupStarted()
	{
		if (s_log.isInfoEnabled())
		{
			s_log.info("Backup started");
		}

		InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.BACKUP_STARTED);
		sendEvent(evt);
	}

	private void sendBackupComplete()
	{
		if (s_log.isInfoEnabled())
		{
			s_log.info("Backup complete");
		}
		InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.BACKUP_COMPLETE);
		sendEvent(evt);
	}

	private void sendInstallStarted()
	{
		if (s_log.isInfoEnabled())
		{
			s_log.info("Install started");
		}
		InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.INSTALL_STARTED);
		sendEvent(evt);
	}

	private void sendInstallComplete()
	{
		if (s_log.isInfoEnabled())
		{
			s_log.info("Install completed");
		}
		InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.INSTALL_COMPLETE);
		sendEvent(evt);
	}

	private void sendEvent(InstallStatusEvent evt)
	{
		for (InstallStatusListener listener : _listeners)
		{
			listener.handleInstallStatusEvent(evt);
		}
	}


}
