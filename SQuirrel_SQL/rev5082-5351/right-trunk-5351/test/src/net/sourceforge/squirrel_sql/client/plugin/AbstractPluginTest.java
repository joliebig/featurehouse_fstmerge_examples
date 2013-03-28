
package net.sourceforge.squirrel_sql.client.plugin;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertNotNull;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import utils.EasyMockHelper;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;


public abstract class AbstractPluginTest extends BaseSQuirreLJUnit4TestCase
{
	protected IPlugin classUnderTest = null;
	protected EasyMockHelper mockHelper = new EasyMockHelper();
	

	@Test
	public void testGetInternalName() {
		assertNotNull(classUnderTest.getInternalName());
	}
	
	@Test
	public void testGetDescriptiveName() {
		assertNotNull(classUnderTest.getDescriptiveName());
	}

	@Test 
	public void testGetVersion() {
		assertNotNull(classUnderTest.getVersion());
	}

	@Test 
	public void testGetAuthor() {
		assertNotNull(classUnderTest.getAuthor());
	}

	@Test 
	public void testGetChangeLogFilename() {
		assertNotNull(classUnderTest.getChangeLogFileName());
	}

	@Test 
	public void testGetHelpFilename() {
		assertNotNull(classUnderTest.getHelpFileName());
	}
	
	@Test
	public void testGetLicenseFilename() {
		assertNotNull(classUnderTest.getLicenceFileName());
	}
	
	@Test
	public void testGetWebsite() {
		assertNotNull(classUnderTest.getWebSite());
	}

	@Test
	public void testGetContributors() {
		assertNotNull(classUnderTest.getContributors());
	}

}
