
package org.firebirdsql.squirrel.tab;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBaseDataSetTabTest;

import org.easymock.EasyMock;
import org.junit.Before;

public class IndexInfoTabTest extends AbstractBaseDataSetTabTest
{

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		classUnderTest = new IndexInfoTab();
		classUnderTest.setDatabaseObjectInfo(mockDatabaseObjectInfo);
		clazz = IndexInfoTab.class;
	}

	
	@Override
	public void testCreateDataSet() throws Exception
	{
		ResultSet mockResultSet = mockHelper.createMock(ResultSet.class);
		PreparedStatement mockPreparedStatement = mockHelper.createMock(PreparedStatement.class);

		expect(mockSQLConnection.prepareStatement(isA(String.class))).andStubReturn(mockPreparedStatement);
		mockPreparedStatement.setString(1, TEST_SIMPLE_NAME);
		expect(mockPreparedStatement.executeQuery()).andStubReturn(mockResultSet);

		expect(mockResultSet.next()).andReturn(true);
		expect(mockResultSet.getString(EasyMock.anyInt())).andReturn("testString").anyTimes();
		expect(mockResultSet.getInt(EasyMock.anyInt())).andReturn(0).anyTimes();
		expect(mockResultSet.getStatement()).andStubReturn(mockPreparedStatement);
		
		mockPreparedStatement.close();
		mockResultSet.close();
		super.testCreateDataSet();
	}

}
