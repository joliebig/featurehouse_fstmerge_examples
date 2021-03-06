
package net.sourceforge.squirrel_sql.client.update.gui.installer;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

public class ArtifactInstallerImplTest extends BaseSQuirreLJUnit4TestCase
{

	ArtifactInstallerImpl implUnderTest = null;

	EasyMockHelper helper = new EasyMockHelper();

	private ChangeListXmlBean mockChangeListBean = helper.createMock(ChangeListXmlBean.class);

	private UpdateUtil mockUpdateUtil = helper.createMock(UpdateUtil.class);

	private InstallStatusEventFactory mockInstallStatusEventFactory =
		helper.createMock(InstallStatusEventFactory.class);

	private InstallStatusEvent mockBackupStartedStatusEvent = helper.createMock(InstallStatusEvent.class);

	private InstallStatusEvent mockBackupCompletedStatusEvent = helper.createMock(InstallStatusEvent.class);

	private InstallStatusEvent mockInstallStartedStatusEvent = helper.createMock(InstallStatusEvent.class);

	private InstallStatusEvent mockInstallCompletedStatusEvent = helper.createMock(InstallStatusEvent.class);

	private InstallFileOperationInfoFactory mockInstallFileOperationInfoFactory =
		helper.createMock(InstallFileOperationInfoFactory.class);

	private InstallStatusListener mockInstallStatusListener = helper.createMock(InstallStatusListener.class);

	
	private static final String FW_JAR_FILENAME = "fw.jar";

	private static final String SQUIRREL_SQL_JAR_FILENAME = "squirrel-sql.jar";

	private static final String SPRING_JAR_FILENAME = "spring.jar";

	private static final String DBCOPY_ZIP_FILENAME = "dbcopy.zip";
	
	private static final String DBCOPY_JAR_FILENAME = "dbcopy.jar";
	
	private static final String DBCOPY_DIR_FILENAME = "dbcopy";

	private static final String SQUIRREL_SQL_ES_JAR_FILENAME = "squirrel-sql_es.jar";

	private File mockSquirreHomeDirFile = helper.createMock("mockSquirreHomeDirFile", File.class);

	private File mockSquirreLLibDirFile = helper.createMock("mockSquirreLLibDirFile", File.class);

	private File mockSquirrelPluginsDirFile = helper.createMock("mockSquirrelPluginsDirFile", File.class);

	private File mockUpdateRootDirFile = helper.createMock("mockUpdateRootDirFile", File.class);

	private File mockBackupRootDirFile = helper.createMock("mockBackupRootDirFile", File.class);

	private File mockBackupCoreDirFile = helper.createMock("mockBackupCoreDirFile", File.class);

	private File mockBackupPluginDirFile = helper.createMock("mockBackupPluginDirFile", File.class);

	private File mockBackupTranslationDirFile = helper.createMock("mockBackupTranslationDirFile", File.class);

	private File mockInstalledFrameworkJarFile = helper.createMock("mockInstalledFrameworkJarFile", File.class);

	private File mockBackupFrameworkJarFile = helper.createMock("mockBackupFrameworkJarFile", File.class);

	private File mockInstalledSquirrelSqlJarFile = helper.createMock("mockInstalledSquirrelSqlJarFile", File.class);

	private File mockBackupSquirrelSqlJarFile = helper.createMock("mockBackupSquirrelSqlJarFile", File.class);

	private File mockBackupDbCopyZipFile = helper.createMock("mockBackupDbCopyZipFile", File.class);

	private File mockInstalledDbCopyPluginDirFile = helper.createMock("mockInstalledDbCopyPluginDirFile", File.class);

	

	private File mockInstalledSquirrelSqlEsJarFile = helper.createMock("mockInstalledSquirrelSqlEsJarFile", File.class);

	private File mockInstalledDbCopyZipFile = helper.createMock("mockInstalledDbCopyZipFile", File.class);
	
	private File mockBackupSquirrelSqlEsJarFile = helper.createMock("mockBackupSquirrelSqlEsJarFile", File.class);

	private File mockDownloadsCoreDirFile = helper.createMock("mockDownloadsCoreDirFile", File.class);

	private File mockDownloadsPluginDirFile = helper.createMock("mockDownloadsPluginDirFile", File.class);

	private File mockDownloadsFrameworkJarFile = helper.createMock("mockDownloadsFrameworkJarFile", File.class);

	private File mockDownloadsSquirrelSqlJarFile = helper.createMock("mockDownloadsSquirrelSqlJarFile", File.class);

	private File mockDownloadsSpringJarFile = helper.createMock("mockDownloadsSpringJarFile", File.class);

	private File mockDownloadsDbCopyPluginZipFile = helper.createMock("mockDownloadsDbCopyPluginZipFile", File.class);
	
	private File mockDownloadsSquirrelSqlEsJarFile = helper.createMock("mockDownloadsSquirrelSqlEsJarFile", File.class);
	
	private File mockPathToInstalledDBCopyJarFile = helper.createMock("mockPathToInstalledDBCopyJarFile", File.class);
	
	private File mockPathToInstalledDBCopyDirectory = helper.createMock("mockPathToInstalledDBCopyDirectory", File.class);
	
	private File mockDownloadsI18nDirFile = helper.createMock("mockDownloadsI18nDirFile", File.class);

	private File mockInstalledSpringJarFile = helper.createMock("mockInstalledSpringJarFile", File.class);
	
	private InstallFileOperationInfo mockInstallSquirrelSqlJarOperationInfo =
		helper.createMock(InstallFileOperationInfo.class);

	private InstallFileOperationInfo mockInstallFrameworkJarOperationInfo =
		helper.createMock(InstallFileOperationInfo.class);

	private InstallFileOperationInfo mockInstallSpringJarOperationInfo =
		helper.createMock(InstallFileOperationInfo.class);

	private InstallFileOperationInfo mockInstallDbCopyZipOperationInfo = 
		helper.createMock(InstallFileOperationInfo.class);

	private InstallFileOperationInfo mockInstallSquirrelSqlEsOperationInfo = 
		helper.createMock(InstallFileOperationInfo.class);
	
	@Before
	public void setUp() throws Exception
	{
		helper.resetAll();
		setupUpdateUtil();
		setupFileAbsolutePathExpectations();
	}
	
	private void setupUpdateUtil()
	{
		expect(mockUpdateUtil.getSquirrelHomeDir()).andReturn(mockSquirreHomeDirFile).anyTimes();
		expect(mockUpdateUtil.getSquirrelUpdateDir()).andReturn(mockUpdateRootDirFile).anyTimes();
		expect(mockUpdateUtil.getSquirrelLibraryDir()).andReturn(mockSquirreLLibDirFile).anyTimes();
		expect(mockUpdateUtil.getSquirrelPluginsDir()).andReturn(mockSquirrelPluginsDirFile).anyTimes();
		
		implUnderTest = new ArtifactInstallerImpl();
		implUnderTest.setInstallStatusEventFactory(mockInstallStatusEventFactory);
		implUnderTest.setInstallFileOperationInfoFactory(mockInstallFileOperationInfoFactory);
		implUnderTest.addListener(mockInstallStatusListener);
	}

	private void setupFileAbsolutePathExpectations() {
		
		expect(mockSquirreHomeDirFile.getAbsolutePath()).andReturn("mockSquirreHomeDirFile").anyTimes();

		expect(mockSquirreLLibDirFile.getAbsolutePath()).andReturn("mockSquirreLLibDirFile").anyTimes();

		expect(mockSquirrelPluginsDirFile.getAbsolutePath()).andReturn("mockSquirrelPluginsDirFile").anyTimes();

		expect(mockUpdateRootDirFile.getAbsolutePath()).andReturn("mockUpdateRootDirFile").anyTimes();

		expect(mockBackupRootDirFile.getAbsolutePath()).andReturn("mockBackupRootDirFile").anyTimes();

		expect(mockBackupCoreDirFile.getAbsolutePath()).andReturn("mockBackupCoreDirFile").anyTimes();

		expect(mockBackupPluginDirFile.getAbsolutePath()).andReturn("mockBackupPluginDirFile").anyTimes();

		expect(mockBackupTranslationDirFile.getAbsolutePath()).andReturn("mockBackupTranslationDirFile")
			.anyTimes();

		expect(mockInstalledFrameworkJarFile.getAbsolutePath()).andReturn("mockInstalledFrameworkJarFile")
			.anyTimes();

		expect(mockBackupFrameworkJarFile.getAbsolutePath()).andReturn("mockBackupFrameworkJarFile").anyTimes();

		expect(mockInstalledSquirrelSqlJarFile.getAbsolutePath()).andReturn("mockInstalledSquirrelSqlJarFile")
			.anyTimes();

		expect(mockBackupSquirrelSqlJarFile.getAbsolutePath()).andReturn("mockBackupSquirrelSqlJarFile")
			.anyTimes();

		expect(mockBackupDbCopyZipFile.getAbsolutePath()).andReturn("mockBackupDbCopyZipFile").anyTimes();

		expect(mockInstalledDbCopyPluginDirFile.getAbsolutePath()).andReturn("mockInstalledDbCopyPluginDirFile")
			.anyTimes();




		expect(mockInstalledSquirrelSqlEsJarFile.getAbsolutePath()).andReturn(
			"mockInstalledSquirrelSqlEsJarFile").anyTimes();

		expect(mockInstalledDbCopyZipFile.getAbsolutePath()).andReturn("mockInstalledDbCopyZipFile").anyTimes();

		expect(mockBackupSquirrelSqlEsJarFile.getAbsolutePath()).andReturn("mockBackupSquirrelSqlEsJarFile")
			.anyTimes();

		expect(mockDownloadsCoreDirFile.getAbsolutePath()).andReturn("mockDownloadsCoreDirFile").anyTimes();

		expect(mockDownloadsPluginDirFile.getAbsolutePath()).andReturn("mockDownloadsPluginDirFile").anyTimes();

		expect(mockDownloadsFrameworkJarFile.getAbsolutePath()).andReturn("mockDownloadsFrameworkJarFile")
			.anyTimes();

		expect(mockDownloadsSquirrelSqlJarFile.getAbsolutePath()).andReturn("mockDownloadsSquirrelSqlJarFile")
			.anyTimes();

		expect(mockDownloadsSpringJarFile.getAbsolutePath()).andReturn("mockDownloadsSpringJarFile").anyTimes();

		expect(mockDownloadsDbCopyPluginZipFile.getAbsolutePath()).andReturn("mockDownloadsDbCopyPluginZipFile")
			.anyTimes();

		expect(mockDownloadsSquirrelSqlEsJarFile.getAbsolutePath()).andReturn(
			"mockDownloadsSquirrelSqlEsJarFile").anyTimes();

		expect(mockPathToInstalledDBCopyJarFile.getAbsolutePath()).andReturn("mockPathToInstalledDBCopyJarFile")
			.anyTimes();

		expect(mockPathToInstalledDBCopyDirectory.getAbsolutePath()).andReturn(
			"mockPathToInstalledDBCopyDirectory").anyTimes();
		
		expect(mockInstalledSpringJarFile.getAbsolutePath()).andReturn("mockInstalledSpringJarFile").anyTimes();
	}
	
	@After
	public void tearDown() throws Exception
	{
		implUnderTest = null;
	}

	
	private void setupFileCopyExpectations(String filename, File installedDir, File installedFile,
		File backupDir, File backupFile) throws FileNotFoundException, IOException
	{
		expect(mockUpdateUtil.getFile(installedDir, filename)).andReturn(
			installedFile);
		
		expect(mockUpdateUtil.getFile(backupDir, filename)).andReturn(
			backupFile);
		
		mockUpdateUtil.copyFile(installedFile, backupFile);
	}
	
	
	@Test
	public final void testBackupFiles() throws Exception
	{

		
		makeCommonUpdateUtilAssertions();

		setupFileCopyExpectations(FW_JAR_FILENAME, mockSquirreLLibDirFile, mockInstalledFrameworkJarFile,
			mockBackupCoreDirFile, mockInstalledFrameworkJarFile);

		setupFileCopyExpectations(SQUIRREL_SQL_JAR_FILENAME, mockSquirreHomeDirFile, 
			mockInstalledSquirrelSqlJarFile, mockBackupCoreDirFile, mockBackupSquirrelSqlJarFile);
		
		expect(mockUpdateUtil.getFile(mockBackupPluginDirFile, "dbcopy.zip")).andReturn(mockBackupDbCopyZipFile);
		
		expect(mockUpdateUtil.getFile(mockSquirrelPluginsDirFile, "dbcopy")).andReturn(
			mockInstalledDbCopyPluginDirFile);
		expect(mockUpdateUtil.getFile(mockSquirrelPluginsDirFile, "dbcopy.jar")).andReturn(
			mockPathToInstalledDBCopyJarFile);
		
		mockUpdateUtil.createZipFile(isA(File.class), isA(File[].class));

		expect(mockUpdateUtil.getFile(mockSquirreLLibDirFile, SQUIRREL_SQL_ES_JAR_FILENAME)).andReturn(
			mockInstalledSquirrelSqlEsJarFile);
		expect(mockUpdateUtil.getFile(mockBackupTranslationDirFile, SQUIRREL_SQL_ES_JAR_FILENAME)).andReturn(
			mockBackupSquirrelSqlEsJarFile);
		expect(mockUpdateUtil.fileExists(mockInstalledSquirrelSqlEsJarFile)).andReturn(true);
		mockUpdateUtil.copyFile(mockInstalledSquirrelSqlEsJarFile, mockBackupSquirrelSqlEsJarFile);

		expect(mockInstallStatusEventFactory.create(InstallEventType.BACKUP_STARTED)).andReturn(
			mockBackupStartedStatusEvent);
		expect(mockInstallStatusEventFactory.create(InstallEventType.BACKUP_COMPLETE)).andReturn(
			mockBackupCompletedStatusEvent);

		expect(mockChangeListBean.getChanges()).andReturn(buildChangeList());

		mockInstallStatusListener.handleInstallStatusEvent(mockBackupStartedStatusEvent);
		mockInstallStatusListener.handleInstallStatusEvent(mockBackupCompletedStatusEvent);

		helper.replayAll();
		implUnderTest.setChangeList(mockChangeListBean);
		implUnderTest.setUpdateUtil(mockUpdateUtil);
		implUnderTest.backupFiles();
		helper.verifyAll();
	}
	
	@Test
	public final void testInstallFiles() throws IOException
	{
		
		makeCommonUpdateUtilAssertions();

		expect(mockInstallStatusEventFactory.create(InstallEventType.INSTALL_STARTED)).andReturn(
			mockInstallStartedStatusEvent);
		expect(mockInstallStatusEventFactory.create(InstallEventType.INSTALL_COMPLETE)).andReturn(
			mockInstallCompletedStatusEvent);

		mockInstallStatusListener.handleInstallStatusEvent(mockInstallStartedStatusEvent);
		mockInstallStatusListener.handleInstallStatusEvent(mockInstallCompletedStatusEvent);

		expect(mockChangeListBean.getChanges()).andReturn(buildChangeList());
		
		
		expect(mockUpdateUtil.getFile(mockSquirreHomeDirFile, SQUIRREL_SQL_JAR_FILENAME)).andReturn(
			mockInstalledSquirrelSqlJarFile);
		expect(mockInstalledSquirrelSqlJarFile.exists()).andReturn(true).anyTimes();
		expect(mockUpdateUtil.deleteFile(mockInstalledSquirrelSqlJarFile)).andReturn(true);
		
		expect(mockUpdateUtil.getFile(mockSquirreLLibDirFile, FW_JAR_FILENAME)).andReturn(
			mockInstalledFrameworkJarFile);
		expect(mockInstalledFrameworkJarFile.exists()).andReturn(true).anyTimes();
		expect(mockUpdateUtil.deleteFile(mockInstalledFrameworkJarFile)).andReturn(true);
		
		expect(mockUpdateUtil.getFile(mockSquirreLLibDirFile, SPRING_JAR_FILENAME)).andReturn(
			mockInstalledSpringJarFile);
		expect(mockInstalledSpringJarFile.exists()).andReturn(true).anyTimes();
		expect(mockUpdateUtil.deleteFile(mockInstalledSpringJarFile)).andReturn(true);
				
		expect(mockUpdateUtil.getFile(mockSquirreLLibDirFile, SQUIRREL_SQL_ES_JAR_FILENAME
			)).andReturn(
			mockInstalledSquirrelSqlEsJarFile);
		expect(mockInstalledSquirrelSqlEsJarFile.exists()).andReturn(false);
		
		
		expect(mockUpdateUtil.getFile(mockDownloadsCoreDirFile, SQUIRREL_SQL_JAR_FILENAME)).andReturn(
			mockDownloadsSquirrelSqlJarFile);
		
		expect(mockUpdateUtil.getFile(mockDownloadsCoreDirFile, FW_JAR_FILENAME)).andReturn(
			mockDownloadsFrameworkJarFile);
		
		expect(mockUpdateUtil.getFile(mockDownloadsCoreDirFile, SPRING_JAR_FILENAME)).andReturn(
			mockDownloadsSpringJarFile);
		
		expect(mockUpdateUtil.getFile(mockDownloadsPluginDirFile, DBCOPY_ZIP_FILENAME)).andReturn(
			mockDownloadsDbCopyPluginZipFile).anyTimes();
		
		expect(mockUpdateUtil.getFile(mockSquirrelPluginsDirFile, DBCOPY_JAR_FILENAME)).andReturn(
			mockPathToInstalledDBCopyJarFile);
		expect(mockPathToInstalledDBCopyJarFile.exists()).andReturn(true);
		expect(mockUpdateUtil.deleteFile(mockPathToInstalledDBCopyJarFile)).andReturn(true);
		
		expect(mockUpdateUtil.getFile(mockSquirrelPluginsDirFile, DBCOPY_DIR_FILENAME)).andReturn(
			mockPathToInstalledDBCopyDirectory);		
		expect(mockPathToInstalledDBCopyDirectory.exists()).andReturn(true);
		
		expect(mockUpdateUtil.deleteFile(mockPathToInstalledDBCopyDirectory)).andReturn(true);
		
		expect(mockUpdateUtil.getFile(mockDownloadsI18nDirFile, SQUIRREL_SQL_ES_JAR_FILENAME)).andReturn(
			mockDownloadsSquirrelSqlEsJarFile);

		boolean isPlugin = true;
		boolean isNotPlugin = false;
		
		
		setupFileCopyOperationInfo(mockDownloadsSquirrelSqlJarFile, mockSquirreHomeDirFile,
			mockInstallSquirrelSqlJarOperationInfo, isNotPlugin);
		setupFileCopyOperationInfo(mockDownloadsFrameworkJarFile, mockSquirreLLibDirFile,
			mockInstallFrameworkJarOperationInfo, isNotPlugin);
		setupFileCopyOperationInfo(mockDownloadsSpringJarFile, mockSquirreLLibDirFile,
			mockInstallSpringJarOperationInfo, isNotPlugin);
		setupFileCopyOperationInfo(mockDownloadsDbCopyPluginZipFile, mockSquirrelPluginsDirFile,
			mockInstallDbCopyZipOperationInfo, isPlugin);
		setupFileCopyOperationInfo(mockDownloadsSquirrelSqlEsJarFile, mockSquirreLLibDirFile,
			mockInstallSquirrelSqlEsOperationInfo, isNotPlugin);
									
		File mockChangeListFile = helper.createMock("mockChangeListFile", File.class);
		mockUpdateUtil.copyFile(mockChangeListFile, mockBackupRootDirFile);
		expect(mockUpdateUtil.deleteFile(mockChangeListFile)).andReturn(true);
		
		helper.replayAll();
		implUnderTest.setChangeList(mockChangeListBean);
		implUnderTest.setChangeListFile(mockChangeListFile);
		implUnderTest.setUpdateUtil(mockUpdateUtil);
		implUnderTest.installFiles();
		helper.verifyAll();
	}
	
	@Test
	public void testDisallowCoreTypeFileRemoval() throws Exception {
		
		makeCommonUpdateUtilAssertions();
		setupInstallEventsAndListener();		
		
		expect(mockChangeListBean.getChanges()).andReturn(buildRemoveCoreFileChangeList());
		
		helper.replayAll();
		implUnderTest.setChangeList(mockChangeListBean);
		implUnderTest.setUpdateUtil(mockUpdateUtil);
		implUnderTest.installFiles();
		helper.verifyAll();		
	}
	
	
	
	@Test
	public void testInstallFiles_FailedToRemoveExistingFiles() throws Exception {

		makeCommonUpdateUtilAssertions();
		setupInstallEventsAndListener();
		
		File fileToRemove = helper.createMock("fileToRemove", File.class);
		expect(fileToRemove.getAbsolutePath()).andReturn("");
		expect(fileToRemove.exists()).andReturn(true);
		
		File fileToCopy = helper.createMock("fileToCopy", File.class);
		expect(mockUpdateUtil.deleteFile(fileToRemove)).andReturn(false);
		List<ArtifactStatus> mockChangeList = getSquirrelSqlEsJarChangeList();
		expect(mockChangeListBean.getChanges()).andReturn(mockChangeList);				
		expect(mockUpdateUtil.getFile(mockDownloadsI18nDirFile, SQUIRREL_SQL_ES_JAR_FILENAME)).andReturn(
			fileToCopy);
		expect(mockUpdateUtil.getFile(mockSquirreLLibDirFile, SQUIRREL_SQL_ES_JAR_FILENAME)).andReturn(
			fileToRemove);
		
		expect(mockInstallFileOperationInfoFactory.create(fileToCopy, mockSquirreLLibDirFile)).andReturn(
			mockInstallSquirrelSqlEsOperationInfo);
		mockInstallSquirrelSqlEsOperationInfo.setPlugin(false);
		
		helper.replayAll();
		implUnderTest.setChangeList(mockChangeListBean);
		implUnderTest.setUpdateUtil(mockUpdateUtil);
		implUnderTest.installFiles();
		helper.verifyAll();		
		
		
	}
	
	
	
	
	private void setupInstallEventsAndListener() { 
		expect(mockInstallStatusEventFactory.create(InstallEventType.INSTALL_STARTED)).andReturn(
			mockInstallStartedStatusEvent);
		
		mockInstallStatusListener.handleInstallStatusEvent(mockInstallStartedStatusEvent);
		
		expect(mockInstallStatusEventFactory.create(InstallEventType.INSTALL_COMPLETE)).andReturn(
			mockInstallCompletedStatusEvent);				
		
		mockInstallStatusListener.handleInstallStatusEvent(mockInstallCompletedStatusEvent);
	}
	
	private void setupFileCopyOperationInfo(File downloadsFile, File installDir,
		InstallFileOperationInfo info, boolean isPlugin) throws IOException
	{
		expect(mockInstallFileOperationInfoFactory.create(downloadsFile, installDir)).andReturn(info);
		expect(info.getInstallDir()).andReturn(installDir);
		expect(info.getFileToInstall()).andReturn(downloadsFile);
		info.setPlugin(isPlugin);
		mockUpdateUtil.copyFile(downloadsFile, installDir);
	}
	
	private void makeCommonUpdateUtilAssertions()
	{
		expect(mockUpdateUtil.checkDir(mockUpdateRootDirFile, UpdateUtil.BACKUP_ROOT_DIR_NAME)).andReturn(
			mockBackupRootDirFile);
		expect(mockUpdateUtil.checkDir(mockBackupRootDirFile, UpdateUtil.CORE_ARTIFACT_ID)).andReturn(
			mockBackupCoreDirFile);
		expect(mockUpdateUtil.checkDir(mockBackupRootDirFile, UpdateUtil.PLUGIN_ARTIFACT_ID)).andReturn(
			mockBackupPluginDirFile);
		expect(mockUpdateUtil.checkDir(mockBackupRootDirFile, UpdateUtil.TRANSLATION_ARTIFACT_ID)).andReturn(
			mockBackupTranslationDirFile);

		expect(mockUpdateUtil.getCoreDownloadsDir()).andReturn(mockDownloadsCoreDirFile).atLeastOnce();
		expect(mockUpdateUtil.getPluginDownloadsDir()).andReturn(mockDownloadsPluginDirFile).atLeastOnce();
		expect(mockUpdateUtil.getI18nDownloadsDir()).andReturn(mockDownloadsI18nDirFile).atLeastOnce();
	}

	private List<ArtifactStatus> buildRemoveCoreFileChangeList() {
		ArrayList<ArtifactStatus> result = new ArrayList<ArtifactStatus>();
		final String coreType = UpdateUtil.CORE_ARTIFACT_ID;
		final boolean installed = true;
		ArtifactStatus squirrelSqlJarToRemove =
			getArtifactToRemove(SQUIRREL_SQL_JAR_FILENAME, installed, coreType);
		result.add(squirrelSqlJarToRemove);
		return result;
	}
	
	private List<ArtifactStatus> buildChangeList()
	{
		ArrayList<ArtifactStatus> result = new ArrayList<ArtifactStatus>();

		final boolean installed = true;
		final boolean notInstalled = false;
		final String coreType = UpdateUtil.CORE_ARTIFACT_ID;
		final String pluginType = UpdateUtil.PLUGIN_ARTIFACT_ID;
		final String i18nType = UpdateUtil.TRANSLATION_ARTIFACT_ID;

		ArtifactStatus newSquirrelSqlJar = getArtifactToInstall(SQUIRREL_SQL_JAR_FILENAME, true, coreType);
		ArtifactStatus newFrameworkJar = getArtifactToInstall(FW_JAR_FILENAME, installed, coreType);
		ArtifactStatus newSpringJar = getArtifactToInstall(SPRING_JAR_FILENAME, notInstalled, coreType);
		ArtifactStatus newDbcopyZip = getArtifactToInstall(DBCOPY_ZIP_FILENAME, installed, pluginType);
		ArtifactStatus newSquirrelSqlEsJar =
			getArtifactToInstall(SQUIRREL_SQL_ES_JAR_FILENAME, installed, i18nType);

		result.add(newSquirrelSqlJar);
		result.add(newFrameworkJar);
		result.add(newSpringJar);
		result.add(newDbcopyZip);
		result.add(newSquirrelSqlEsJar);
		return result;
	}

	private List<ArtifactStatus> getSquirrelSqlEsJarChangeList() {
		ArrayList<ArtifactStatus> result = new ArrayList<ArtifactStatus>();
		final boolean installed = true;
		final String i18nType = UpdateUtil.TRANSLATION_ARTIFACT_ID;
		result.add(getArtifactToInstall(SQUIRREL_SQL_ES_JAR_FILENAME, installed, i18nType));
		return result;
	}
	
	private ArtifactStatus getArtifactToInstall(String name, boolean installed, String type)
	{
		ArtifactStatus result = new ArtifactStatus();
		result.setArtifactAction(ArtifactAction.INSTALL);
		result.setName(name);
		result.setInstalled(installed);
		result.setType(type);
		return result;
	}

	private ArtifactStatus getArtifactToRemove(String name, boolean installed, String type)
	{
		ArtifactStatus result = new ArtifactStatus();
		result.setArtifactAction(ArtifactAction.REMOVE);
		result.setName(name);
		result.setInstalled(installed);
		result.setType(type);
		return result;
	}
	
}
