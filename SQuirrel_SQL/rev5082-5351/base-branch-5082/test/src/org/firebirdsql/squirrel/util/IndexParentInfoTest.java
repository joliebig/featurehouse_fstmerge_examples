
package org.firebirdsql.squirrel.util;


import static org.junit.Assert.assertEquals;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IndexParentInfoTest extends BaseSQuirreLJUnit4TestCase
{
	private IndexParentInfo classUnderTest = null;
	private SQLDatabaseMetaData mockSQLDatabaseMetaData = 
		mockHelper.createMock(SQLDatabaseMetaData.class);
	private IDatabaseObjectInfo relatedObjectInfo = mockHelper.createMock(IDatabaseObjectInfo.class);
	
	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testInit() throws Exception
	{
		setupDboExpectations(relatedObjectInfo);
		setupSqlDatabaseMetaDataExpectations(mockSQLDatabaseMetaData);
		mockHelper.replayAll();
		classUnderTest = new IndexParentInfo(relatedObjectInfo, mockSQLDatabaseMetaData);
		mockHelper.verifyAll();		
		
		assertEquals(relatedObjectInfo, classUnderTest.getRelatedObjectInfo());
	}
}
