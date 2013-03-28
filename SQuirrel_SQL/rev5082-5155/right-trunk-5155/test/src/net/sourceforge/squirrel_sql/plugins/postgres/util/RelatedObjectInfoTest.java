
package net.sourceforge.squirrel_sql.plugins.postgres.util;

import static net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType.TABLE_TYPE_DBO;
import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RelatedObjectInfoTest extends BaseSQuirreLJUnit4TestCase
{

	private RelatedObjectInfo classUnderTest = null;

	private IDatabaseObjectInfo mockDatabaseObjectInfo = mockHelper.createMock(IDatabaseObjectInfo.class);

	private SQLDatabaseMetaData mockMetaData = mockHelper.createMock(SQLDatabaseMetaData.class);

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testContstructor() throws SQLException 
	{
		super.setupDboExpectations(mockDatabaseObjectInfo);
		super.setupSqlDatabaseMetaDataExpectations(mockMetaData);
		mockHelper.replayAll();
		classUnderTest = new RelatedObjectInfo(
		   mockDatabaseObjectInfo, TEST_SIMPLE_NAME, TABLE_TYPE_DBO, mockMetaData);
		mockHelper.verifyAll();
		
		assertEquals(mockDatabaseObjectInfo, classUnderTest.getRelatedObjectInfo());
	}
}
