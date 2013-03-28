
package net.sourceforge.squirrel_sql.client.update.gui.installer;

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
		sendBackupStarted();

		FileWrapper localReleaseFile = _util.getLocalReleaseFile();
		_util.copyFile(localReleaseFile, _util.getBackupDir());
		
		for (ArtifactStatus status : _changeList)
		{
			String artifactName = status.getName();
			
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
				if (pluginDirectoryFile.exists()) {
					filesToZip.add(pluginDirectoryFile);
				}
				FileWrapper pluginJarFile = _util.getFile(pluginInstallDir, pluginJarFilename);
				if (pluginJarFile.exists()) {
					filesToZip.add(pluginJarFile);
				}
				if (filesToZip.size() > 0) {
					_util.createZipFile(pluginBackupFile, filesToZip.toArray(new FileWrapper[filesToZip.size()]));
				} else {
					s_log.error("Plugin ("+status.getName()+") was listed as already installed, but it's " +
							"files didn't exist and couldn't be backed up: pluginDirectoryFile="+
							pluginDirectoryFile.getAbsolutePath()+" pluginJarFile="+
							pluginJarFile.getAbsolutePath());
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
		}

		sendBackupComplete();
		return result;
	}

	
	public void installFiles() throws IOException
	{
		sendInstallStarted();

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
		success = success && backupAndDeleteChangeListFile();
		success = success && installNewReleaseXmlFile();
		
		if (!success) {
			restoreFilesFromBackup(filesToInstall);
		} 
		
		sendInstallComplete();
	}

	
	public boolean restoreBackupFiles() throws FileNotFoundException, IOException
	{
		for (ArtifactStatus status : _changeList) {
			String name = status.getName();
			FileWrapper backupDir = null;
			FileWrapper installDir = null;
			
			if (status.isCoreArtifact()) {
				backupDir = coreBackupDir;
				installDir = getCoreArtifactLocation(name, installRootDir, coreInstallDir);
			}
			if (status.isPluginArtifact()) {
				backupDir = pluginBackupDir;
				installDir = pluginInstallDir;
			}
			if (status.isTranslationArtifact()) {
				backupDir = translationBackupDir;
				installDir = coreInstallDir; 
			}
			FileWrapper backupJarPath = _util.getFile(backupDir, name);
			FileWrapper installJarPath = _util.getFile(installDir, name);
			
			if (!_util.deleteFile(installJarPath)) { 
				return false;
			} else {
				_util.copyFile(backupJarPath, installJarPath);
			}			
		}
		if (!_util.deleteFile(_util.getLocalReleaseFile())) {
			return false;
		} else {
			FileWrapper backupReleaseFile = _util.getFile(_util.getBackupDir(), UpdateUtil.RELEASE_XML_FILENAME);
			_util.copyFile(backupReleaseFile, updateDir);
		}
		
		return true;
	}
	
	
	
	
	private List<ArtifactStatus> initializeChangeList(ChangeListXmlBean changeListBean) {
		ArrayList<ArtifactStatus> result = new ArrayList<ArtifactStatus>();
		for (ArtifactStatus status : changeListBean.getChanges()) {
			
			
			
			
			
			
			if (status.isPluginArtifact()) {
				result.add(status);
				continue;
			}
			
			if (status.getArtifactAction() == ArtifactAction.INSTALL) {
				FileWrapper installedFileLocation = null;
				
				if (status.isCoreArtifact()) {
					installedFileLocation = 
						_util.getFile(getCoreArtifactLocation(status.getName(), installRootDir, coreInstallDir),
										  status.getName());
				}
				if (status.isTranslationArtifact()) {
					installedFileLocation = _util.getFile(coreInstallDir, status.getName());
				}
				
				long installedSize = installedFileLocation.length();
				if (installedSize == status.getSize()) {
					long installedCheckSum = _util.getCheckSum(installedFileLocation);
					if (installedCheckSum == status.getChecksum()) {
						if (s_log.isDebugEnabled()) {
							s_log.debug("initializeChangeList: found a core/translation artifact that is not " +
									"installed: installedSize= "+installedSize+"installedCheckSum="+
									installedCheckSum+" statusSize="+status.getSize()+" statusChecksum="+
									status.getChecksum());
						}
						continue;
					}
				}
			}
			
			
			result.add(status);
		}
		
		return result;
	}
	
	
	
	private FileWrapper getCoreArtifactLocation(String artifactName, FileWrapper rootDir, FileWrapper coreDir) {
		if (UpdateUtil.SQUIRREL_SQL_JAR_FILENAME.equals(artifactName)) {
			return rootDir;
		} else {
			return coreDir;
		}
	}
	
	private void restoreFilesFromBackup(List<InstallFileOperationInfo> filesToInstall)
	{
		
	}

	private boolean backupAndDeleteChangeListFile()
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

	
	private boolean installNewReleaseXmlFile() {
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
			s_log.error("installNewReleaseXmlFile: unexpected exception - "+e.getMessage(), e);
		}
		catch (IOException e)
		{
			result = false;
			s_log.error("installNewReleaseXmlFile: unexpected exception - "+e.getMessage(), e);
		}
		return result;
	}
	
	
	private boolean removeOldFiles(List<FileWrapper> filesToRemove)
	{
		boolean result = true;
		for (FileWrapper fileToRemove : filesToRemove) {
			result = removeOldFile(fileToRemove);
			if (!result) {
				break;
			}
		}
		return result;
	}

	
	private boolean removeOldFile(FileWrapper fileToRemove)
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
			FileWrapper installDir = info.getInstallDir();
			FileWrapper fileToCopy = info.getFileToInstall();
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

	private void installFile(FileWrapper installDir, FileWrapper fileToCopy) throws IOException
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
