package net.sourceforge.squirrel_sql.fw.sql;


import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.sql.dbobj.BestRowIdentifier;

import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;


public class SQLDatabaseMetaDataTest extends BaseSQuirreLJUnit4TestCase {

	private static final String TEST_TABLE = "aTable";
	private static final String TEST_SCHEMA = "aSchema";
	private static final String TEST_CATALOG = "aCatalog";
	private SQLDatabaseMetaData classUnderTest = null;
	private EasyMockHelper mockHelper = new EasyMockHelper();

	
	private Connection mockConnection = mockHelper.createMock(Connection.class);
	private ISQLConnection mockSqlConnection = mockHelper.createMock(ISQLConnection.class);
	private DatabaseMetaData mockDatabaseMetaData = mockHelper.createMock(DatabaseMetaData.class);

	@Before
	public void setUp() throws Exception {

		expect(mockDatabaseMetaData.getDatabaseProductName()).andStubReturn("PostgreSQL");
		expect(mockDatabaseMetaData.getDatabaseProductVersion()).andStubReturn("8.1.8");
		expect(mockDatabaseMetaData.supportsSchemasInIndexDefinitions()).andStubReturn(true);
		expect(mockDatabaseMetaData.supportsSchemasInDataManipulation()).andStubReturn(true);
		expect(mockDatabaseMetaData.supportsCatalogsInDataManipulation()).andStubReturn(true);
		expect(mockDatabaseMetaData.supportsSchemasInTableDefinitions()).andStubReturn(true);
		expect(mockDatabaseMetaData.getCatalogSeparator()).andStubReturn(".");
		expect(mockDatabaseMetaData.getIdentifierQuoteString()).andStubReturn("\"");
		
		expect(mockConnection.getMetaData()).andStubReturn(mockDatabaseMetaData);
		expect(mockSqlConnection.getConnection()).andStubReturn(mockConnection);

	}

	@Test
	public void testGetSchemas() throws SQLException {

		
		ResultSet schemaResultSet1 = buildVarcharResultSet("schemaResultSet1", new String[] { TEST_SCHEMA });	
		
		ResultSet schemaResultSet2 = buildVarcharResultSet("schemaResultSet2", new String[] { TEST_SCHEMA, "aSchema2" });
		expect(mockDatabaseMetaData.getSchemas()).andReturn(schemaResultSet1);
		expect(mockDatabaseMetaData.getSchemas()).andReturn(schemaResultSet2);
		
		mockHelper.replayAll();
		classUnderTest = new SQLDatabaseMetaData(mockSqlConnection);

		
		String[] currentSchemas = classUnderTest.getSchemas();
		assertEquals(1, currentSchemas.length);

		
		currentSchemas = classUnderTest.getSchemas();
		assertEquals(2, currentSchemas.length);

		mockHelper.verifyAll();
	}

	@Test
	public void testGetCatalogs() throws SQLException {

		
		ResultSet catalogResultSet1 = buildVarcharResultSet(null, new String[] { TEST_CATALOG });
		
		ResultSet catalogResultSet2 = buildVarcharResultSet(null, new String[] { TEST_CATALOG, "aCatalog2" });
		expect(mockDatabaseMetaData.getCatalogs()).andReturn(catalogResultSet1);
		expect(mockDatabaseMetaData.getCatalogs()).andReturn(catalogResultSet2);

		mockHelper.replayAll();
		classUnderTest = new SQLDatabaseMetaData(mockSqlConnection);

		
		String[] currentCatalogs = classUnderTest.getCatalogs();
		assertEquals(1, currentCatalogs.length);

		
		currentCatalogs = classUnderTest.getCatalogs();
		assertEquals(2, currentCatalogs.length);

		mockHelper.verifyAll();
	}

	@Test
	public void testPGGetTableTypes() throws SQLException {
		
		ResultSet mockTableTypeResultSet = buildVarcharResultSet("mockTableTypeResultSet", new String[] {
		      "SYSTEM INDEX", "SYSTEM VIEW", "SYSTEM TABLE", "SYSTEM TOAST INDEX", "SYSTEM TOAST TABLE",
		      "SYSTEM VIEW", "TABLE", "TEMPORARY INDEX", "TEMPORARY TABLE", "VIEW" });
		expect(mockDatabaseMetaData.getTableTypes()).andStubReturn(mockTableTypeResultSet);

		mockHelper.replayAll();
		classUnderTest = new SQLDatabaseMetaData(mockSqlConnection);

		String[] tableTypes = classUnderTest.getTableTypes();
		for (int i = 0; i < tableTypes.length; i++) {
			String type = tableTypes[i];
			assertFalse(
			   "'SYSTEM INDEX' is a type returned from " + "SQLDatabaseMetaData.getTableTypes for PostgreSQL - "
			         + "it should not be.", "SYSTEM INDEX".equals(type));
		}

		mockHelper.verifyAll();
	}

	
	@Test
	public void testGetIdentifierQuoteStringMSSQL() throws SQLException {
		Connection mockCon = mockHelper.createMock(Connection.class);
		DatabaseMetaData md = mockHelper.createMock(DatabaseMetaData.class);
		expect(md.getIdentifierQuoteString()).andStubReturn("foo");
		expect(mockCon.getMetaData()).andStubReturn(md);
		ISQLConnection sqlcon = mockHelper.createMock(ISQLConnection.class);
	   expect(sqlcon.getConnection()).andStubReturn(mockCon);
		SQLDatabaseMetaData sqlmd = new SQLDatabaseMetaData(sqlcon);

		mockHelper.replayAll();

		String quoteString = sqlmd.getIdentifierQuoteString();
		assertEquals("foo", quoteString);
		quoteString = sqlmd.getIdentifierQuoteString();
		assertEquals("foo", quoteString);
		quoteString = sqlmd.getIdentifierQuoteString();
		assertEquals("foo", quoteString);
		quoteString = sqlmd.getIdentifierQuoteString();
		assertEquals("foo", quoteString);

		mockHelper.verifyAll();
	}

	@Test
	public void testGetBestRowIdentifier() throws SQLException {
		
		ITableInfo mockTableInfo = mockHelper.createMock(ITableInfo.class);
		ResultSet mockBestRowIdResultSet = mockHelper.createMock(ResultSet.class);
		ResultSetMetaData mockResultSetMetaData = mockHelper.createMock(ResultSetMetaData.class);
		
		expect(mockTableInfo.getCatalogName()).andStubReturn(TEST_CATALOG);
		expect(mockTableInfo.getSchemaName()).andStubReturn(TEST_SCHEMA);
		expect(mockTableInfo.getSimpleName()).andStubReturn(TEST_TABLE);
		expect(mockDatabaseMetaData.getBestRowIdentifier(TEST_CATALOG, TEST_SCHEMA, TEST_TABLE, 
			DatabaseMetaData.bestRowTransaction, true));
		expectLastCall().andReturn(mockBestRowIdResultSet);
		expect(mockBestRowIdResultSet.getMetaData()).andReturn(mockResultSetMetaData);
		expect(mockBestRowIdResultSet.getObject(1)).andReturn(1); 
		expect(mockResultSetMetaData.getColumnType(1)).andReturn(Types.BIGINT);
		expect(mockBestRowIdResultSet.getString(2)).andReturn("aColumn"); 
		expect(mockBestRowIdResultSet.getObject(3)).andReturn(3); 
		expect(mockResultSetMetaData.getColumnType(3)).andReturn(Types.SMALLINT);
		expect(mockBestRowIdResultSet.getString(4)).andReturn("SMALLINT"); 
		
		expect(mockBestRowIdResultSet.getObject(5)).andReturn(5); 
		expect(mockResultSetMetaData.getColumnType(5)).andReturn(Types.INTEGER);
		
		expect(mockBestRowIdResultSet.getObject(7)).andReturn(7); 
		expect(mockResultSetMetaData.getColumnType(7)).andReturn(Types.TINYINT);

		expect(mockBestRowIdResultSet.getObject(8)).andReturn(8); 
		expect(mockResultSetMetaData.getColumnType(8)).andReturn(Types.TINYINT);
		
		expect(mockBestRowIdResultSet.next()).andReturn(true);
		expect(mockBestRowIdResultSet.next()).andReturn(false);
		mockBestRowIdResultSet.close();
		
		
		
		mockHelper.replayAll();
		classUnderTest = new SQLDatabaseMetaData(mockSqlConnection);
		
		BestRowIdentifier[] result = classUnderTest.getBestRowIdentifier(mockTableInfo);
		
		assertEquals(1, result.length);
		BestRowIdentifier rid = result[0];
		assertEquals(1, rid.getScope());
		assertEquals("aColumn", rid.getColumnName());
		assertEquals(3, rid.getSQLDataType());
		assertEquals("SMALLINT", rid.getTypeName());
		assertEquals(5, rid.getPrecision());
		assertEquals(7, rid.getScale());
		assertEquals(8, rid.getPseudoColumn());
		
		mockHelper.verifyAll();
	}
	
	
	
	private ResultSet buildVarcharResultSet(String mockName, String[] values) throws SQLException {
		ResultSetMetaData rsmd = mockHelper.createMock(mockName, ResultSetMetaData.class);
		expect(rsmd.getColumnCount()).andStubReturn(1);
		expect(rsmd.getColumnType(1)).andStubReturn(java.sql.Types.VARCHAR);
		expect(rsmd.getColumnTypeName(1)).andStubReturn("varchar");
		ResultSet rs = mockHelper.createMock(ResultSet.class);
		expect(rs.getMetaData()).andStubReturn(rsmd);
		for (String value : values) {
			expect(rs.next()).andReturn(true);
			expect(rs.getString(1)).andReturn(value);
			expect(rs.wasNull()).andStubReturn(false);
		}
		expect(rs.next()).andReturn(false);
		rs.close();
		return rs;
	}
	
}
