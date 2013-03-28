
package net.sourceforge.squirrel_sql.fw.datasetviewer;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

public class DatabaseTypesDataSetTest extends BaseSQuirreLJUnit4TestCase
{

	private DatabaseTypesDataSet classUnderTest = null;
	
	private EasyMockHelper mockHelper = new EasyMockHelper();
	
	private ResultSet resultSet = mockHelper.createMock(ResultSet.class);
	private ResultSetMetaData resultSetMetaData = mockHelper.createMock(ResultSetMetaData.class);
	
	@Before
	public void setUp() throws Exception
	{
		expect(resultSet.getMetaData()).andStubReturn(resultSetMetaData);
	}

	@After
	public void tearDown() throws Exception
	{
		mockHelper.resetAll();
	}

	
	@Test
	public void testDatabaseTypesDataSetResultSet_Nullable() throws Exception
	{
		int[] columnIndices = new int[] { 7 };
		expect(resultSetMetaData.getColumnDisplaySize(7)).andStubReturn(7);
		expect(resultSetMetaData.getColumnLabel(7)).andStubReturn("NULLABLE");
		resultSet.close();

		expect(resultSet.next()).andReturn(true).times(3);
		expect(resultSet.next()).andReturn(false);
		expect(resultSet.getShort(7)).andReturn(DatabaseMetaData.attributeNoNulls);
		expect(resultSet.getShort(7)).andReturn(DatabaseMetaData.attributeNullable);
		expect(resultSet.getShort(7)).andReturn(DatabaseMetaData.attributeNullableUnknown);
		
		mockHelper.replayAll();
		classUnderTest = new DatabaseTypesDataSet(resultSet, columnIndices);
		classUnderTest.next(null);
		assertNotNull(classUnderTest.get(0));
		mockHelper.verifyAll();
	}
	
	
	@Test
	public void testDatabaseTypesDataSetResultSet_Searchable() throws Exception
	{
		int[] columnIndices = new int[] { 9 };
		
		expect(resultSetMetaData.getColumnDisplaySize(9)).andStubReturn(9);
		expect(resultSetMetaData.getColumnLabel(9)).andStubReturn("SEARCHABLE");
		resultSet.close();
		expect(resultSet.next()).andReturn(true).times(4);
		expect(resultSet.next()).andReturn(false);
		expect(resultSet.getShort(9)).andReturn((short)DatabaseMetaData.typePredChar);
		expect(resultSet.getShort(9)).andReturn((short)DatabaseMetaData.typePredBasic);
		expect(resultSet.getShort(9)).andReturn((short)DatabaseMetaData.typePredNone);
		expect(resultSet.getShort(9)).andReturn((short)DatabaseMetaData.typeSearchable);
		
		mockHelper.replayAll();
		classUnderTest = new DatabaseTypesDataSet(resultSet, columnIndices);
		assertEquals(1, classUnderTest.getColumnCount());
		assertNotNull(classUnderTest.getDataSetDefinition());
		
		assertTrue(classUnderTest.next(null));
		assertTrue(classUnderTest.next(null));
		assertTrue(classUnderTest.next(null));
		assertTrue(classUnderTest.next(null));
		assertFalse(classUnderTest.next(null));
		
		mockHelper.verifyAll();
	}

}
