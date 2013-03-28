
package net.sourceforge.squirrel_sql.client.update;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static net.sourceforge.squirrel_sql.client.update.UpdateUtil.DOWNLOADS_DIR_NAME;
import static net.sourceforge.squirrel_sql.client.update.UpdateUtil.PLUGIN_ARTIFACT_ID;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

import junit.framework.Assert;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.plugin.IPluginManager;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.util.PathUtils;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer;
import net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory;
import net.sourceforge.squirrel_sql.fw.util.IOUtilities;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

public class UpdateUtilImplTest extends BaseSQuirreLJUnit4TestCase {

	private UpdateUtilImpl underTest = null;
	private EasyMockHelper mockHelper = new EasyMockHelper();
   
   
   private IPluginManager mockPluginManager = null;
   private FileWrapperFactory mockFileWrapperFactory = null;
   private ApplicationFileWrappers mockApplicationFileWrappers = null;
	private FileWrapper mockUpdateDirectory = null;
	private FileWrapper mockDownloadsDirectory = null;
	private FileWrapper mockDownloadsPluginDirectory = null;
   private IOUtilities mockIOUtilities = null;
   private UpdateXmlSerializer mockSerializer = null;
	private PathUtils mockPathUtils = null;
   
   @Before
   public void setUp() throws Exception {
      underTest = new UpdateUtilImpl();
      
      
      mockPluginManager = mockHelper.createMock(IPluginManager.class);
      mockFileWrapperFactory = mockHelper.createMock(FileWrapperFactory.class);
      mockApplicationFileWrappers = mockHelper.createMock(ApplicationFileWrappers.class);
      mockIOUtilities = mockHelper.createMock(IOUtilities.class);
      mockSerializer = mockHelper.createMock("UpdateXmlSerializer", UpdateXmlSerializer.class);
      mockPathUtils = mockHelper.createMock(PathUtils.class);
      
      
      underTest.setPluginManager(mockPluginManager);
      underTest.setFileWrapperFactory(mockFileWrapperFactory);
      underTest.setApplicationFileWrappers(mockApplicationFileWrappers);
      underTest.setIOUtilities(mockIOUtilities);
      underTest.setUpdateXmlSerializer(mockSerializer);
      underTest.setPathUtils(mockPathUtils);
      
      
      setupAppFileExpectations();
      setupDirectoryExpectations();
   }

   private void setupAppFileExpectations() {
   	mockUpdateDirectory = mockHelper.createMock("mockUpdateDirectory",FileWrapper.class);
   	expect(mockUpdateDirectory.exists()).andStubReturn(true);
   	expect(mockUpdateDirectory.isDirectory()).andStubReturn(true);
   	expect(mockApplicationFileWrappers.getUpdateDirectory()).andStubReturn(mockUpdateDirectory);
   }
   
   private void setupDirectoryExpectations() {
   	
   	mockDownloadsDirectory = mockHelper.createMock("mockDownloadsDirectory",FileWrapper.class);
   	expect(mockDownloadsDirectory.exists()).andStubReturn(true);
   	expect(mockDownloadsDirectory.isDirectory()).andStubReturn(true);
   	expect(mockFileWrapperFactory.create(mockUpdateDirectory, DOWNLOADS_DIR_NAME));
   	expectLastCall().andStubReturn(mockDownloadsDirectory);
   	
   	
   	mockDownloadsPluginDirectory = mockHelper.createMock("mockDownloadsPluginDirectory", FileWrapper.class);
   	expect(mockDownloadsPluginDirectory.exists()).andStubReturn(true);
   	expect(mockDownloadsPluginDirectory.isDirectory()).andStubReturn(true);
   	expect(mockFileWrapperFactory.create(mockDownloadsDirectory, PLUGIN_ARTIFACT_ID));
   	expectLastCall().andStubReturn(mockDownloadsPluginDirectory);
   }
   
   @After
   public void tearDown() throws Exception {
      underTest = null;
   }

   @Test	
   public void testGetInstalledPlugins() {
   	PluginInfo[] pluginInfos = new PluginInfo[2];
   	PluginInfo mockPlugin1 = mockHelper.createMock(PluginInfo.class);
   	PluginInfo mockPlugin2 = mockHelper.createMock(PluginInfo.class);
   	EasyMock.expect(mockPlugin1.getInternalName()).andReturn("plugin1");
   	EasyMock.expect(mockPlugin2.getInternalName()).andReturn("plugin2");
   	pluginInfos[0] = mockPlugin1;
   	pluginInfos[1] = mockPlugin2;
   	expect(mockPluginManager.getPluginInformation()).andReturn(pluginInfos);
   	
   	mockHelper.replayAll();
   	Set<String> installedPlugins = underTest.getInstalledPlugins();
   	mockHelper.verifyAll();
   	
   	assertEquals(2, installedPlugins.size());
   }
   
   @Test
   public void testGetDownloadFileLocation_core() {
   	String coreJarFilename = "somecore.jar";
   	ArtifactStatus mockArtifactStatus = mockHelper.createMock(ArtifactStatus.class);
   	expect(mockArtifactStatus.getType()).andReturn(UpdateUtil.CORE_ARTIFACT_ID).atLeastOnce();
   	expect(mockArtifactStatus.getName()).andReturn(coreJarFilename);




   	FileWrapper mockDownloadsCoreDirectory = mockHelper.createMock("mockDownloadsCoreDirectory", FileWrapper.class);
   	expect(mockDownloadsCoreDirectory.isDirectory()).andReturn(true);
   	expect(mockFileWrapperFactory.create(mockDownloadsDirectory, UpdateUtil.CORE_ARTIFACT_ID));
   	expectLastCall().andReturn(mockDownloadsCoreDirectory);
   	FileWrapper mockSomeCoreJarFile = mockHelper.createMock("mockSomeCoreJarFile", FileWrapper.class);
   	expect(mockFileWrapperFactory.create(mockDownloadsCoreDirectory, coreJarFilename));
   	expectLastCall().andReturn(mockSomeCoreJarFile);
   	
   	mockHelper.replayAll();
   	FileWrapper result = underTest.getDownloadFileLocation(mockArtifactStatus);
   	mockHelper.verifyAll();
   	
   	assertEquals(mockSomeCoreJarFile, result);
   }
   
   @Test
   public void testGetCheckSum() throws IOException {
   	
   	FileWrapper mockFile = mockHelper.createMock("mockFile", FileWrapper.class);
   	expect(mockFile.getAbsolutePath()).andReturn("/path/To/Mock/File");
   	expect(mockIOUtilities.getCheckSum(mockFile)).andReturn(1000L);
   	mockHelper.replayAll();
   	long checksumResult = underTest.getCheckSum(mockFile);
   	mockHelper.verifyAll();
   	
   	assertEquals(1000L, checksumResult);
   }
   
   
   @Test
   public void testCopyFile_scenario1() throws IOException {

   	
   	FileWrapper fromFile = mockHelper.createMock("fromFile", FileWrapper.class);
   	expect(fromFile.exists()).andReturn(true);
   	expect(fromFile.getAbsolutePath()).andReturn("/path/to/from/file").atLeastOnce();
   	expect(mockIOUtilities.getCheckSum(fromFile)).andReturn(1000L);
   	
   	
   	FileWrapper toFile = mockHelper.createMock("toFile", FileWrapper.class);
   	expect(toFile.isDirectory()).andReturn(false);
   	expect(toFile.exists()).andReturn(true);
   	expect(toFile.getAbsolutePath()).andReturn("/path/to/to/file").atLeastOnce();
   	expect(mockIOUtilities.getCheckSum(toFile)).andReturn(1000L);
   	
   	mockHelper.replayAll();
		underTest.copyFile(fromFile, toFile);
   	mockHelper.verifyAll();
   	
   }
   
   @Test 
   public void testCheckDir_exists() {

   	FileWrapper mockUpdateCoreDirectory = mockHelper.createMock(FileWrapper.class);
   	expect(mockUpdateCoreDirectory.exists()).andReturn(true);
   	
   	expect(mockFileWrapperFactory.create(mockUpdateDirectory, UpdateUtil.CORE_ARTIFACT_ID));
   	expectLastCall().andReturn(mockUpdateCoreDirectory);
   	
   	mockHelper.replayAll();
		underTest.checkDir(mockUpdateDirectory, UpdateUtil.CORE_ARTIFACT_ID);
   	mockHelper.verifyAll();   	
   }
   
   @Test 
   public void testCheckDir_notexists() {

   	FileWrapper mockUpdateCoreDirectory = mockHelper.createMock(FileWrapper.class);
   	expect(mockUpdateCoreDirectory.exists()).andReturn(false);
   	expect(mockUpdateCoreDirectory.mkdir()).andReturn(true);
   	
   	expect(mockFileWrapperFactory.create(mockUpdateDirectory, UpdateUtil.CORE_ARTIFACT_ID));
   	expectLastCall().andReturn(mockUpdateCoreDirectory);
   	
   	
   	mockHelper.replayAll();
		underTest.checkDir(mockUpdateDirectory, UpdateUtil.CORE_ARTIFACT_ID);
   	mockHelper.verifyAll();   	
   }

   @Test 
   public void testCheckDir_notexists_failure() {

   	FileWrapper mockUpdateCoreDirectory = mockHelper.createMock(FileWrapper.class);
   	expect(mockUpdateCoreDirectory.exists()).andReturn(false);
   	expect(mockUpdateCoreDirectory.mkdir()).andReturn(false);
   	expect(mockUpdateCoreDirectory.getAbsolutePath()).andReturn("/path/to/directory/that/was/not/made");
   	
   	expect(mockFileWrapperFactory.create(mockUpdateDirectory, UpdateUtil.CORE_ARTIFACT_ID));
   	expectLastCall().andReturn(mockUpdateCoreDirectory);
   	
   	
   	mockHelper.replayAll();
		underTest.checkDir(mockUpdateDirectory, UpdateUtil.CORE_ARTIFACT_ID);
   	mockHelper.verifyAll();   	
   }

   @Test
   public void testLoadUpdateFromFileSystem() throws IOException {

   	String pathToDirectoryThatContainsReleaseXml = "/path/to/release.xml/file";
   	String releaseXmlFilePath = "/path/to/release.xml/file/release.xml";
   	
   	FileWrapper releaseXmlFileDir = getDirectoryMock("releaseXmlFileDir"); 
   	FileWrapper releaseXmlFile = getFileMock("releaseXmlFile", releaseXmlFilePath);

   	expect(mockFileWrapperFactory.create(pathToDirectoryThatContainsReleaseXml));
   	expectLastCall().andReturn(releaseXmlFileDir);
   	expect(mockFileWrapperFactory.create(releaseXmlFileDir, UpdateUtil.RELEASE_XML_FILENAME));
   	expectLastCall().andReturn(releaseXmlFile);
   	
   	ChannelXmlBean mockChannelXmlBean = mockHelper.createMock(ChannelXmlBean.class);
		expect(mockSerializer.readChannelBean(releaseXmlFile)).andReturn(mockChannelXmlBean);
   	
   	mockHelper.replayAll();
		underTest.loadUpdateFromFileSystem(pathToDirectoryThatContainsReleaseXml);
   	mockHelper.verifyAll();   	
   	
   }

   @Test
   public void testIsPresentInDownloadDirectory() throws IOException {
   	
   	String pluginFilename = "aPluginName.zip";
   	String pluginFileAbsPath = "path/to/plugin/file/"+pluginFilename;
   	long pluginFileCheckSum = 10L;
   	long pluginFileByteSize = 20L;
   	
   	ArtifactStatus status = mockHelper.createMock(ArtifactStatus.class);
   	expect(status.getType()).andStubReturn(UpdateUtil.PLUGIN_ARTIFACT_ID);
   	expect(status.getName()).andReturn(pluginFilename);
		expect(status.getChecksum()).andReturn(pluginFileCheckSum);
		expect(status.getSize()).andReturn(pluginFileByteSize);
		
   	FileWrapper fileInDownlodDir = mockHelper.createMock("fileInDownlodDir", FileWrapper.class);   	
		expect(fileInDownlodDir.getAbsolutePath()).andReturn(pluginFileAbsPath);
   	expect(fileInDownlodDir.exists()).andReturn(true);
   	expect(fileInDownlodDir.length()).andReturn(pluginFileByteSize);
   	expect(mockFileWrapperFactory.create(mockDownloadsPluginDirectory, pluginFilename));
   	expectLastCall().andReturn(fileInDownlodDir);
   	expect(mockIOUtilities.getCheckSum(fileInDownlodDir)).andReturn(pluginFileCheckSum);
   	
   	mockHelper.replayAll();
		boolean result = underTest.isPresentInDownloadsDirectory(status);
   	mockHelper.verifyAll();
   	
   	assertTrue(result);
   }   
   
   @Test
   public void testDownloadHttpUpdateFile_verifysuccess() throws Exception {
   	testDownloadHttpUpdateFile(true);
   }
   
   @Test
   public void testDownloadHttpUpdateFile_verifyfailure() throws Exception {
   	testDownloadHttpUpdateFile(false);
   }   
   
   
   
   private void testDownloadHttpUpdateFile(boolean simulateSuccess) throws Exception {
		String host = "somehost.com";
		int port = 80;
		String fileToGet = "/updates/snapshot/release.xml";
		String fileFromPath = "release.xml";
		String destDir = "/some/dest/directory";
		int fileSize = 10;
		long checksum = 10;
		String absPath = destDir + "/" + fileFromPath;
		
		
		URL url = new URL("http", host, fileToGet);
		
		expect(mockIOUtilities.constructHttpUrl(host, port, fileToGet)).andReturn(url);
		
   	
   	expect(mockPathUtils.getFileFromPath(fileToGet)).andReturn(fileFromPath);
   	FileWrapper destFile = mockHelper.createMock(FileWrapper.class);
   	expect(destFile.getAbsolutePath()).andReturn(absPath);
   	expect(mockFileWrapperFactory.create(destDir, fileFromPath)).andReturn(destFile);
   	if (simulateSuccess) {
   		expect(mockIOUtilities.downloadHttpFile(url, destFile)).andReturn(fileSize);
   	} else {
   		expect(mockIOUtilities.downloadHttpFile(url, destFile)).andReturn(fileSize-1);
   	}
		
   	String downloadedPath = null;
   	mockHelper.replayAll();
   	try {
   		downloadedPath =  
   			underTest.downloadHttpUpdateFile(host, port, fileToGet, destDir, fileSize, checksum);
			if (!simulateSuccess) {
				fail("Expected an exception to be thrown for failed filesize verification");
			}
   	} catch (Exception e) {
   		if (simulateSuccess) {
   			fail("Unexpected exception : "+e.getMessage());
   		}
   	}
   	mockHelper.verifyAll();   
   	if (simulateSuccess) {
   		Assert.assertEquals(absPath, downloadedPath);
   	}
   	
   }
   
   private FileWrapper getDirectoryMock(String name) {
   	FileWrapper result = mockHelper.createMock(name, FileWrapper.class);
   	expect(result.isDirectory()).andStubReturn(true);
   	return result;
   }
   
   private FileWrapper getFileMock(String name, String path) {
   	FileWrapper result = mockHelper.createMock(name, FileWrapper.class);
   	expect(result.isDirectory()).andStubReturn(false);
   	expect(result.getAbsolutePath()).andStubReturn(path);
   	return result;   	
   }
}
