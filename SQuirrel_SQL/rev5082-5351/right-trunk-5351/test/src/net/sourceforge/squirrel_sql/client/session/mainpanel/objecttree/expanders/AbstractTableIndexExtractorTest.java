
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertNotNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.easymock.EasyMock;
import org.junit.Test;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableIndexExtractor;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

public abstract class AbstractTableIndexExtractorTest extends BaseSQuirreLJUnit4TestCase
{

	protected ITableIndexExtractor classUnderTest = null;
	private PreparedStatement mockPreparedStatement = mockHelper.createMock(PreparedStatement.class);
	private IDatabaseObjectInfo mockDatabaseObjectInfo = mockHelper.createMock(IDatabaseObjectInfo.class);

	public AbstractTableIndexExtractorTest()
	{
		super();
	}

	@Test
	public void testBindParamters() throws SQLException
	{
		expect(mockDatabaseObjectInfo.getSchemaName()).andStubReturn(TEST_SCHEMA_NAME);
		expect(mockDatabaseObjectInfo.getCatalogName()).andStubReturn(TEST_CATALOG_NAME);
		expect(mockDatabaseObjectInfo.getSimpleName()).andStubReturn(TEST_SIMPLE_NAME);
		expect(mockDatabaseObjectInfo.getQualifiedName()).andStubReturn(TEST_QUALIFIED_NAME);
		
		mockPreparedStatement.setString(anyInt(), EasyMock.isA(String.class));
		
		expectLastCall().atLeastOnce();
		
		mockHelper.replayAll();
		classUnderTest.bindParamters(mockPreparedStatement, mockDatabaseObjectInfo);
		mockHelper.verifyAll();
	}

	@Test
	public void testGetTableTriggerQuery()
	{
		assertNotNull(classUnderTest.getTableIndexQuery());
	}

}