
package net.sourceforge.squirrel_sql.client.update.gui.installer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

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
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class ArtifactInstallerImpl implements ArtifactInstaller
{

	
	private static ILogger s_log = LoggerController.createLogger(ArtifactInstallerImpl.class);

	
	private List<ArtifactStatus> _changeList = null;

	
	private List<InstallStatusListener> _listeners = new ArrayList<InstallStatusListener>();

	
	private FileWrapper updateDir = null;

	

	
	private FileWrapper downloadsRootDir = null;

	
	private FileWrapper coreDownloadsDir = null;

	
	private FileWrapper pluginDownloadsDir = null;

	
	private FileWrapper i18nDownloadsDir = null;

	

	
	private FileWrapper backupRootDir = null;

	
	private FileWrapper coreBackupDir = null;

	
	private FileWrapper pluginBackupDir = null;

	
	private FileWrapper translationBackupDir = null;

	

	
	private FileWrapper installRootDir = null;

	
	private FileWrapper coreInstallDir = null;

	
	private FileWrapper pluginInstallDir = null;

	
	private FileWrapper i18nInstallDir = null;

	
	private FileWrapper changeListFile = null;

	

	
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
		downloadsRootDir = _util.checkDir(updateDir, UpdateUtil.DOWNLOADS_DIR_NAME);

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
		_changeList = initializeChangeList(changeList);
	}

	
	public FileWrapper getChangeListFile()
	{
		return changeListFile;
	}

	
	public void setChangeListFile(FileWrapper changeListFile)
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
		sendBackupStarted(_changeList.size());

		FileWrapper localReleaseFile = _util.getLocalReleaseFile();
		_util.copyFile(localReleaseFile, _util.getBackupDir());

		for (ArtifactStatus status : _changeList)
		{
			String artifactName = status.getName();
			sendFileBackupStarted(artifactName);
			
			if (!status.isInstalled())
			{
				if (s_log.isInfoEnabled())
				{
					s_log.info("Skipping backup of artifact (" + status + ") which isn't installed.");
				}
				sendFileBackupComplete(artifactName);
				continue;
			}
			if (status.isCoreArtifact())
			{
				FileWrapper installDir = getCoreArtifactLocation(artifactName, installRootDir, coreInstallDir);
				FileWrapper coreFile = _util.getFile(installDir, artifactName);
				FileWrapper backupFile = _util.getFile(coreBackupDir, artifactName);
				_util.copyFile(coreFile, backupFile);
			}
			if (status.isPluginArtifact())
			{
				
				FileWrapper pluginBackupFile = _util.getFile(pluginBackupDir, artifactName);
				String pluginDirectory = artifactName.replace(".zip", "");
				String pluginJarFilename = artifactName.replace(".zip", ".jar");

				ArrayList<FileWrapper> filesToZip = new ArrayList<FileWrapper>();
				FileWrapper pluginDirectoryFile = _util.getFile(pluginInstallDir, pluginDirectory);
				if (pluginDirectoryFile.exists())
				{
					filesToZip.add(pluginDirectoryFile);
				}
				FileWrapper pluginJarFile = _util.getFile(pluginInstallDir, pluginJarFilename);
				if (pluginJarFile.exists())
				{
					filesToZip.add(pluginJarFile);
				}
				if (filesToZip.size() > 0)
				{
					_util.createZipFile(pluginBackupFile, filesToZip.toArray(new FileWrapper[filesToZip.size()]));
				}
				else
				{
					s_log.error("Plugin (" + status.getName() + ") was listed as already installed, but it's "
						+ "files didn't exist and couldn't be backed up: pluginDirectoryFile="
						+ pluginDirectoryFile.getAbsolutePath() + " pluginJarFile="
						+ pluginJarFile.getAbsolutePath());
				}
			}
			if (status.isTranslationArtifact())
			{
				FileWrapper translationFile = _util.getFile(i18nInstallDir, artifactName);
				FileWrapper backupFile = _util.getFile(translationBackupDir, artifactName);
				if (translationFile.exists())
				{
					_util.copyFile(translationFile, backupFile);
				}
			}
			breathing();
			sendFileBackupComplete(artifactName);
		}

		sendBackupComplete();
		return result;
	}

	
	public void installFiles() throws IOException
	{
		sendInstallStarted(_changeList.size());

		List<FileWrapper> filesToRemove = new ArrayList<FileWrapper>();
		List<InstallFileOperationInfo> filesToInstall = new ArrayList<InstallFileOperationInfo>();

		for (ArtifactStatus status : _changeList)
		{
			ArtifactAction action = status.getArtifactAction();
			FileWrapper installDir = null;
			FileWrapper fileToCopy = null;
			FileWrapper fileToRemove = null;
			String artifactName = status.getName();
			boolean isPlugin = false;

			if (status.isCoreArtifact())
			{
				if (action == ArtifactAction.REMOVE)
				{
					s_log.error("Skipping core artifact (" + status.getName() + ") that was marked for removal");
					continue;
				}
				installDir = getCoreArtifactLocation(status.getName(), installRootDir, coreInstallDir);
				fileToCopy = _util.getFile(coreDownloadsDir, artifactName);
				if (UpdateUtil.DOCS_ARCHIVE_FILENAME.equals(status.getName()))
				{
					fileToRemove = _util.getFile(installDir, artifactName.replace(".zip", ""));
				}
				else
				{
					fileToRemove = _util.getFile(installDir, artifactName);
				}

				filesToRemove.add(fileToRemove);
			}
			if (status.isPluginArtifact())
			{
				isPlugin = true;
				installDir = pluginInstallDir;
				if (action != ArtifactAction.REMOVE)
				{
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
				if (action != ArtifactAction.REMOVE)
				{
					fileToCopy = _util.getFile(i18nDownloadsDir, artifactName);
				}
				fileToRemove = _util.getFile(installDir, artifactName);
				filesToRemove.add(fileToRemove);
			}
			if (fileToCopy != null)
			{
				InstallFileOperationInfo info = installFileOperationInfoFactory.create(fileToCopy, installDir);
				info.setPlugin(isPlugin);
				info.setArtifactName(artifactName);
				filesToInstall.add(info);
			}

		}
		boolean success = removeOldFiles(filesToRemove);
		success = success && installFiles(filesToInstall);
		success = success && backupAndDeleteChangeListFile();
		success = success && installNewReleaseXmlFile();

		if (!success)
		{
			restoreFilesFromBackup(filesToInstall);
		}

		sendInstallComplete();
	}

	
	public boolean restoreBackupFiles() throws FileNotFoundException, IOException
	{
		for (ArtifactStatus status : _changeList)
		{
			String name = status.getName();
			FileWrapper backupDir = null;
			FileWrapper installDir = null;

			if (status.isCoreArtifact())
			{
				backupDir = coreBackupDir;
				installDir = getCoreArtifactLocation(name, installRootDir, coreInstallDir);
			}
			if (status.isPluginArtifact())
			{
				backupDir = pluginBackupDir;
				installDir = pluginInstallDir;
			}
			if (status.isTranslationArtifact())
			{
				backupDir = translationBackupDir;
				installDir = coreInstallDir; 
			}
			FileWrapper backupJarPath = _util.getFile(backupDir, name);
			FileWrapper installJarPath = _util.getFile(installDir, name);

			if (!_util.deleteFile(installJarPath))
			{
				return false;
			}
			else
			{
				_util.copyFile(backupJarPath, installJarPath);
			}
		}
		if (!_util.deleteFile(_util.getLocalReleaseFile()))
		{
			return false;
		}
		else
		{
			FileWrapper backupReleaseFile = _util.getFile(_util.getBackupDir(), UpdateUtil.RELEASE_XML_FILENAME);
			_util.copyFile(backupReleaseFile, updateDir);
		}

		return true;
	}

	

	private void breathing()
	{
		
		if (SwingUtilities.isEventDispatchThread())
		{
			if (s_log.isDebugEnabled())
			{
				s_log.debug("breathing: ignoring request to sleep the event dispatch thread");
			}
			return;
		}
		synchronized (this)
		{
			try
			{
				wait(50);
			}
			catch (InterruptedException e)
			{
				if (s_log.isInfoEnabled())
				{
					s_log.info("breathing: Interrupted", e);
				}
			}
		}
	}

	
	private List<ArtifactStatus> initializeChangeList(ChangeListXmlBean changeListBean)
	{
		sendInitChangelistStarted(changeListBean.getChanges().size());
		ArrayList<ArtifactStatus> result = new ArrayList<ArtifactStatus>();
		for (ArtifactStatus status : changeListBean.getChanges())
		{
			String artifactName = status.getName();
			sendFileInitChangelistStarted(artifactName);
			
			
			
			
			
			
			
			if (status.isPluginArtifact())
			{
				result.add(status);
				sendFileInitChangelistComplete(artifactName);
				continue;
			}

			if (status.getArtifactAction() == ArtifactAction.INSTALL)
			{
				FileWrapper installedFileLocation = null;
				
				if (status.isCoreArtifact())
				{
					installedFileLocation =
						_util.getFile(getCoreArtifactLocation(status.getName(), installRootDir, coreInstallDir),
							status.getName());
				}
				if (status.isTranslationArtifact())
				{
					installedFileLocation = _util.getFile(coreInstallDir, status.getName());
				}

				long installedSize = installedFileLocation.length();
				if (installedSize == status.getSize())
				{
					long installedCheckSum = _util.getCheckSum(installedFileLocation);
					if (installedCheckSum == status.getChecksum())
					{
						if (s_log.isDebugEnabled())
						{
							s_log.debug("initializeChangeList: found a core/translation artifact that is not "
								+ "installed: installedSize= " + installedSize + " installedCheckSum="
								+ installedCheckSum + " statusSize=" + status.getSize() + " statusChecksum="
								+ status.getChecksum());
						}
						sendFileInitChangelistComplete(artifactName);
						continue;
					}
				}
			}

			
			result.add(status);
			sendFileInitChangelistComplete(artifactName);
		}
		sendInitChangelistComplete();
		return result;
	}

	
	private FileWrapper getCoreArtifactLocation(String artifactName, FileWrapper rootDir, FileWrapper coreDir)
	{
		if (UpdateUtil.SQUIRREL_SQL_JAR_FILENAME.equals(artifactName)
			|| UpdateUtil.DOCS_ARCHIVE_FILENAME.equals(artifactName))
		{
			return rootDir;
		}
		else
		{
			return coreDir;
		}
	}

	private void restoreFilesFromBackup(List<InstallFileOperationInfo> filesToInstall)
	{
		
	}

	private boolean backupAndDeleteChangeListFile()
	{
		boolean result = true;
		if (changeListFile != null)
		{
			try
			{
				_util.copyFile(changeListFile, backupRootDir);
				result = _util.deleteFile(changeListFile);
			}
			catch (IOException e)
			{
				result = false;
				s_log.error("Unexpected exception: " + e.getMessage(), e);
			}
		}
		else
		{
			if (s_log.isInfoEnabled())
			{
				s_log.info("moveChangeListFile: Changelist file was null.  Skipping move");
			}
		}
		return result;
	}

	
	private boolean installNewReleaseXmlFile()
	{
		boolean result = true;

		try
		{
			_util.deleteFile(_util.getLocalReleaseFile());
		}
		catch (FileNotFoundException e)
		{
			
			if (s_log.isInfoEnabled())
			{
				s_log.info("installNewReleaseXmlFile: release file to be replaced was missing.");
			}
		}
		FileWrapper downloadReleaseFile = _util.getFile(downloadsRootDir, UpdateUtil.RELEASE_XML_FILENAME);
		try
		{
			_util.copyFile(downloadReleaseFile, updateDir);
		}
		catch (FileNotFoundException e)
		{
			result = false;
			s_log.error("installNewReleaseXmlFile: unexpected exception - " + e.getMessage(), e);
		}
		catch (IOException e)
		{
			result = false;
			s_log.error("installNewReleaseXmlFile: unexpected exception - " + e.getMessage(), e);
		}
		return result;
	}

	
	private boolean removeOldFiles(List<FileWrapper> filesToRemove)
	{
		boolean result = true;
		sendRemoveStarted(filesToRemove.size());
		for (FileWrapper fileToRemove : filesToRemove)
		{
			sendFileRemoveStarted(fileToRemove.getName());
			result = removeOldFile(fileToRemove);
			if (!result)
			{
				break;
			}
			breathing();
			sendFileRemoveComplete(fileToRemove.getName());
		}
		sendRemoveComplete();
		return result;
	}

	
	private boolean removeOldFile(FileWrapper fileToRemove)
	{
		boolean result = true;
		String absolutePath = fileToRemove.getAbsolutePath();
		if (s_log.isDebugEnabled())
		{
			s_log.debug("Examining file to remove: " + absolutePath);
		}
		if (fileToRemove.exists())
		{
			try
			{
				if (s_log.isDebugEnabled())
				{
					s_log.debug("Attempting to delete file: " + absolutePath);
				}
				result = _util.deleteFile(fileToRemove);
				if (!result)
				{
					s_log.error("Delete operation failed for file/directory: " + absolutePath);
				}
			}
			catch (SecurityException e)
			{
				result = false;
				s_log.error("Unexpected security exception: " + e.getMessage());
			}
		}
		else
		{
			if (s_log.isInfoEnabled())
			{
				s_log.info("Skipping delete of file doesn't appear to exist: " + absolutePath);
			}
		}
		return result;
	}

	private boolean installFiles(List<InstallFileOperationInfo> filesToInstall) throws IOException
	{
		boolean result = true;
		for (InstallFileOperationInfo info : filesToInstall)
		{
			try
			{
				sendFileInstallStarted(info.getArtifactName());
				installFile(info);
				sendFileInstallComplete(info.getArtifactName());
			}
			catch (Exception e)
			{
				s_log.error("installFiles: unexpected exception: " + e.getMessage(), e);
				result = false;
				break;
			}
			breathing();
		}
		return result;
	}

	private void installFile(InstallFileOperationInfo info) throws IOException
	{
		FileWrapper installDir = info.getInstallDir();
		FileWrapper fileToCopy = info.getFileToInstall();

		if (fileToCopy.getAbsolutePath().endsWith(".zip"))
		{
			
			
			_util.extractZipFile(fileToCopy, installDir);
		}
		else
		{
			_util.copyFile(fileToCopy, installDir);
		}

	}

	private void sendInitChangelistStarted(int numFilesToBackup)
	{
		logInfo("Changelist initialization started");
		InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.INIT_CHANGELIST_STARTED);
		evt.setNumFilesToUpdate(numFilesToBackup);
		sendEvent(evt);
	}

	private void sendFileInitChangelistStarted(String artifactName)
	{
		logInfo("Changelist init started for file: " + artifactName);
		InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.FILE_INIT_CHANGELIST_STARTED);
		evt.setArtifactName(artifactName);
		sendEvent(evt);
	}

	private void sendFileInitChangelistComplete(String artifactName)
	{
		logInfo("Changelist init complete for file: " + artifactName);
		InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.FILE_INIT_CHANGELIST_COMPLETE);
		evt.setArtifactName(artifactName);
		sendEvent(evt);
	}

	private void sendInitChangelistComplete()
	{
		logInfo("Changelist initialization complete");
		InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.INIT_CHANGELIST_COMPLETE);
		sendEvent(evt);
	}	
	
	private void sendBackupStarted(int numFilesToBackup)
	{
		logInfo("Backup started");
		InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.BACKUP_STARTED);
		evt.setNumFilesToUpdate(numFilesToBackup);
		sendEvent(evt);
	}

	private void sendFileBackupStarted(String artifactName)
	{
		logInfo("Backup started for file: " + artifactName);
		InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.FILE_BACKUP_STARTED);
		evt.setArtifactName(artifactName);
		sendEvent(evt);
	}

	private void sendFileBackupComplete(String artifactName)
	{
		logInfo("Backup complete for file: " + artifactName);
		InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.FILE_BACKUP_COMPLETE);
		evt.setArtifactName(artifactName);
		sendEvent(evt);
	}

	private void sendBackupComplete()
	{
		logInfo("Backup complete");
		InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.BACKUP_COMPLETE);
		sendEvent(evt);
	}

	
	private void sendRemoveStarted(int numFilesToRemove)
	{
		logInfo("Remove started");
		InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.REMOVE_STARTED);
		evt.setNumFilesToUpdate(numFilesToRemove);
		sendEvent(evt);
	}

	private void sendFileRemoveStarted(String artifactName)
	{
		logInfo("Remove started for file: " + artifactName);
		InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.FILE_REMOVE_STARTED);
		evt.setArtifactName(artifactName);
		sendEvent(evt);
	}

	private void sendFileRemoveComplete(String artifactName)
	{
		logInfo("Remove complete for file: " + artifactName);
		InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.FILE_REMOVE_COMPLETE);
		evt.setArtifactName(artifactName);
		sendEvent(evt);
	}

	private void sendRemoveComplete()
	{
		logInfo("Remove complete");
		InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.REMOVE_COMPLETE);
		sendEvent(evt);
	}
	
	private void sendInstallStarted(int numFilesToUpdate)
	{
		logInfo("Install started");
		InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.INSTALL_STARTED);
		evt.setNumFilesToUpdate(numFilesToUpdate);
		sendEvent(evt);
	}

	private void sendFileInstallStarted(String artifactName)
	{
		logInfo("Install started for file: " + artifactName);
		InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.FILE_INSTALL_STARTED);
		evt.setArtifactName(artifactName);
		sendEvent(evt);
	}

	private void sendFileInstallComplete(String artifactName)
	{
		logInfo("Install complete for file: " + artifactName);
		InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.FILE_INSTALL_COMPLETE);
		evt.setArtifactName(artifactName);
		sendEvent(evt);
	}

	private void sendInstallComplete()
	{
		logInfo("Install completed");
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

	private void logInfo(String message)
	{
		if (s_log.isInfoEnabled())
		{
			s_log.info(message);
		}
	}

}
