
package net.sourceforge.squirrel_sql.client.plugin;

import static org.easymock.EasyMock.expect;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.plugins.DatabaseProductVersionData;

import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractSessionPluginTest extends AbstractPluginTest implements DatabaseProductVersionData
{

	
	@Test
	public void testIsPluginSession() throws Exception {
		ISession mockSession = mockHelper.createMock(ISession.class);
		ISQLDatabaseMetaData mockSQLDatabaseMetaData = mockHelper.createMock(ISQLDatabaseMetaData.class);
		expect(mockSession.getMetaData()).andStubReturn(mockSQLDatabaseMetaData);
		expect(mockSQLDatabaseMetaData.getDatabaseProductName()).andStubReturn(getDatabaseProductName());
		expect(mockSQLDatabaseMetaData.getDatabaseProductVersion()).andStubReturn(getDatabaseProductVersion());
		
		mockHelper.replayAll();
		if (classUnderTest instanceof DefaultSessionPlugin) {
			DefaultSessionPlugin plugin = (DefaultSessionPlugin)classUnderTest;
			Assert.assertTrue(plugin.isPluginSession(mockSession));
		}
		mockHelper.verifyAll();
	}
	
	
	protected abstract String getDatabaseProductName();

		
	protected abstract String getDatabaseProductVersion();
	
}
