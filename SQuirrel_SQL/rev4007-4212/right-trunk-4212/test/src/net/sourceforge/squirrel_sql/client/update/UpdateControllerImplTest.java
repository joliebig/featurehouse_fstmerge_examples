
package net.sourceforge.squirrel_sql.client.update;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.io.FileNotFoundException;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.fw.util.UpdateSettings;
import net.sourceforge.squirrel_sql.test.TestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UpdateControllerImplTest extends BaseSQuirreLJUnit4TestCase {

   UpdateControllerImpl underTest = null;
   
   
   IApplication mockApplication = null;
   SquirrelPreferences prefs = null;
   UpdateSettings updateSettings = null;
   UpdateUtil util = null;
   PluginManager pluginMgr = null;
   
   @Before
   public void setUp() throws Exception {
      util = new UpdateUtilImpl();
      mockApplication = createMock(IApplication.class);
      updateSettings = new UpdateSettings();
      prefs = TestUtil.createClassMock(SquirrelPreferences.class);
      expect(prefs.getUpdateSettings()).andReturn(updateSettings).anyTimes();
      expect(mockApplication.getSquirrelPreferences()).andReturn(prefs).anyTimes();
      TestUtil.replayClassMock(prefs);
      pluginMgr = new PluginManager(mockApplication);
      expect(mockApplication.getPluginManager()).andReturn(pluginMgr);
      replay(mockApplication);
      underTest = new UpdateControllerImpl(mockApplication);
      underTest.setUpdateUtil(util);
   }

   @After
   public void tearDown() throws Exception {
   }

   @Test (expected = FileNotFoundException.class)
   public void testIsUpToDateLocalNoReleaseFile() throws Exception {
      updateSettings.setRemoteUpdateSite(false);
      updateSettings.setFileSystemUpdatePath(".");
      underTest.isUpToDate();
   }

}
