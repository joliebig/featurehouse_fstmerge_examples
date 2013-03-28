
package net.sourceforge.squirrel_sql.plugins.postgres.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IndexParentInfoTest extends BaseSQuirreLJUnit4TestCase
{

	private IndexParentInfo classUnderTest = null;
	
	private ITableInfo mockTableInfo = mockHelper.createMock(ITableInfo.class);
	
	private SQLDatabaseMetaData mockSQLDatabaseMetaData = mockHelper.createMock(SQLDatabaseMetaData.class);
	
	@Before
	public void setUp() throws Exception
	{		
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public final void testGetTableInfo() throws SQLException
	{
		setupDboExpectations(mockTableInfo);
		setupSqlDatabaseMetaDataExpectations(mockSQLDatabaseMetaData);
		mockHelper.replayAll();
		classUnderTest = new IndexParentInfo(mockTableInfo, mockSQLDatabaseMetaData);
		assertNotNull(classUnderTest.getTableInfo());
		assertEquals(classUnderTest.getTableInfo(), mockTableInfo);
		mockHelper.verifyAll();
	}

}
