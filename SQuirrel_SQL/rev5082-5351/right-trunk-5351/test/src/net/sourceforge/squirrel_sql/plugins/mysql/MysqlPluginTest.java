
package net.sourceforge.squirrel_sql.plugins.mysql;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import net.sourceforge.squirrel_sql.client.plugin.AbstractSessionPluginTest;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class MysqlPluginTest extends AbstractSessionPluginTest
{	
	
	
	private ISQLDatabaseMetaData mockMetaData = mockHelper.createMock(ISQLDatabaseMetaData.class);
	private ISession mockSession = mockHelper.createMock(ISession.class);

	
	@Before
	public void setUp() throws Exception
	{
		expect(mockSession.getMetaData()).andStubReturn(mockMetaData);
		classUnderTest = new MysqlPlugin();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testIsPluginSessionMySQL5() throws Exception
	{
		testIsPluginSession(MYSQL_PRODUCT_NAME, MYSQL_5_PRODUCT_VERSION, true);
	}

	@Test
	public void testIsPluginSessionMySQL4() throws Exception
	{
		testIsPluginSession(MYSQL_PRODUCT_NAME, MYSQL_4_PRODUCT_VERSION, true);
	}

	@Test
	public void testIsPluginSessionPostgreSQL() throws Exception {
		testIsPluginSession(POSTGRESQL_PRODUCT_NAME, POSTGRESQL_8_2_PRODUCT_VERSION, false);		
	}
		
	
	
	private void testIsPluginSession(String productName, String productVersion, boolean isPluginSession)
		throws Exception
	{
		expect(mockMetaData.getDatabaseProductName()).andReturn(productName).anyTimes();
		expect(mockMetaData.getDatabaseProductVersion()).andReturn(productVersion).anyTimes();

		mockHelper.replayAll();

		boolean result = ((MysqlPlugin)classUnderTest).isPluginSession(mockSession);

		assertEquals("isPluginSession() != expected value: ",
			isPluginSession, result);

		mockHelper.verifyAll();
	}

	@Override
	protected String getDatabaseProductName()
	{
		return "mysql";
	}

	@Override
	protected String getDatabaseProductVersion()
	{
		return "5";
	}
		

}
