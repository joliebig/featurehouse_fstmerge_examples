
package net.sourceforge.squirrel_sql.client.update;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertFalse;

import java.util.Locale;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.IPluginManager;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ReleaseXmlBean;
import net.sourceforge.squirrel_sql.fw.util.IUpdateSettings;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

public class UpdateControllerImplTest extends BaseSQuirreLJUnit4TestCase {

   private static final String FILE_SYSTEM_UPDATE_PATH = "fileSystemUpdatePath";

	private static final String LOCAL_RELEASE_FILE = "release.xml";

	UpdateControllerImpl underTest = null;
   
   EasyMockHelper mockHelper = new EasyMockHelper();
   
   
   IApplication mockApplication = null;
   SquirrelPreferences prefs = null;
   IUpdateSettings mockUpdateSettings = null;
   UpdateUtil mockUpdateUtil = null;
   ChannelXmlBean mockInstalledChannelXmlBean = null;
   ChannelXmlBean mockAvailableChannelXmlBean = null;
   IPluginManager mockPluginManager = null;
   
   
   @Before
   public void setUp() throws Exception {
      
      mockApplication = mockHelper.createMock(IApplication.class);
      
      prefs = mockHelper.createMock(SquirrelPreferences.class);
      mockPluginManager = mockHelper.createMock(IPluginManager.class);
      mockInstalledChannelXmlBean = setupSnapshotChannelXmlBean();
      mockAvailableChannelXmlBean = mockHelper.createMock(ChannelXmlBean.class);
      
   }

   private IUpdateSettings setupUpdateSettings(boolean isRemoteUpdateSite)
	{
   	IUpdateSettings result = mockHelper.createMock(IUpdateSettings.class); 
   	expect(result.getUpdateServerPath()).andStubReturn("pathForUpdates");
   	expect(result.getFileSystemUpdatePath()).andStubReturn(FILE_SYSTEM_UPDATE_PATH);
   	expect(result.isRemoteUpdateSite()).andStubReturn(isRemoteUpdateSite);
		return result;
	}

	private void replayAll() {
   	mockHelper.replayAll();
   }
   
   private void verifyAll() {
   	mockHelper.verifyAll();
   }
   
   @After
   public void tearDown() throws Exception {
   	
   }

   @Test 
   public void testIsUpToDateLocalSiteUpdatesAvailable() throws Exception {
   	
   	
   	mockUpdateUtil =
			setupUpdateUtilLocalUpdateSite(mockInstalledChannelXmlBean, mockAvailableChannelXmlBean);
   	boolean isRemoteUpdateSite = false;
   	mockUpdateSettings = setupUpdateSettings(isRemoteUpdateSite);
   	
   	expect(mockApplication.getPluginManager()).andReturn(mockPluginManager).anyTimes();
   	expect(mockApplication.getSquirrelPreferences()).andReturn(prefs).anyTimes();
   	expect(prefs.getUpdateSettings()).andReturn(mockUpdateSettings).anyTimes();

   	setupSnapshotChannelXmlBean();
   	
   	replayAll();
   	
   	underTest = new UpdateControllerImpl(mockApplication);
   	underTest.setUpdateUtil(mockUpdateUtil);
   	boolean result = underTest.isUpToDate();
   	assertFalse(result);
   	verifyAll();
   	
   }
   
   @Test 
   public void testIsIpToDateRemoteSiteUpdatesAvailable() throws Exception {
   	
   	mockUpdateUtil =
			setupUpdateUtilRemoteUpdateSite(mockInstalledChannelXmlBean, mockAvailableChannelXmlBean);
   	boolean isRemoteUpdateSite = true;
   	mockUpdateSettings = setupUpdateSettings(isRemoteUpdateSite);

   	
   }
   
   private UpdateUtil setupUpdateUtilLocalUpdateSite(ChannelXmlBean installed, ChannelXmlBean available)
		throws Exception
	{
		UpdateUtil mockUpdateUtil = setupUpdateUtil();
		expect(mockUpdateUtil.getLocalReleaseFile()).andStubReturn(LOCAL_RELEASE_FILE);
		expect(mockUpdateUtil.getLocalReleaseInfo(LOCAL_RELEASE_FILE)).andStubReturn(installed);
		expect(mockUpdateUtil.loadUpdateFromFileSystem(FILE_SYSTEM_UPDATE_PATH)).andReturn(available);
		return mockUpdateUtil;
	}

   private UpdateUtil setupUpdateUtilRemoteUpdateSite(ChannelXmlBean installed, ChannelXmlBean available)
		throws Exception
	{
   	UpdateUtil mockUpdateUtil = setupUpdateUtil();
   	
   	return mockUpdateUtil;
	}
   
   private UpdateUtil setupUpdateUtil() {
		UpdateUtil mockUpdateUtil = mockHelper.createMock(UpdateUtil.class);
		mockUpdateUtil.setPluginManager(mockPluginManager);
		return mockUpdateUtil;
   }
   
   
   private ChannelXmlBean setupSnapshotChannelXmlBean() {
   	ChannelXmlBean mockChannelXmlBean = mockHelper.createMock(ChannelXmlBean.class);
   	ReleaseXmlBean mockReleaseXmlBean = mockHelper.createMock(ReleaseXmlBean.class);
   	expect(mockChannelXmlBean.getName()).andStubReturn("Snapshot");
   	expect(mockChannelXmlBean.getCurrentRelease()).andStubReturn(mockReleaseXmlBean);
   	return mockChannelXmlBean;
   }
}
