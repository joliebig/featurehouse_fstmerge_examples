package net.sourceforge.squirrel_sql.fw.sql;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;



public class SQLDatabaseMetaDataTest extends BaseSQuirreLTestCase {

	SQLDatabaseMetaData iut = null;
	
	
	Connection mockConnection = null;
    ISQLConnection mockSqlConnection = null;
    DatabaseMetaData mockDatabaseMetaData = null;
    
	protected void setUp() throws Exception {
		super.setUp();
        
        mockDatabaseMetaData = createMock(DatabaseMetaData.class);
        expect(mockDatabaseMetaData.getDatabaseProductName()).andReturn("PostgreSQL");
        expect(mockDatabaseMetaData.getDatabaseProductVersion()).andReturn("8.1.8");

        
        ResultSet mockTableTypeResultSet = 
            buildVarcharResultSet(new String[] { "SYSTEM INDEX", 
                    "SYSTEM VIEW", "SYSTEM TABLE", "SYSTEM TOAST INDEX", 
                    "SYSTEM TOAST TABLE", "SYSTEM VIEW", "TABLE", 
                    "TEMPORARY INDEX", "TEMPORARY TABLE", "VIEW"});        
        expect(mockDatabaseMetaData.getTableTypes()).andReturn(mockTableTypeResultSet);
        
        
        ResultSet catalogResultSet1 = 
            buildVarcharResultSet(new String[] { "aCatalog" });
        expect(mockDatabaseMetaData.getCatalogs()).andReturn(catalogResultSet1);
        
        
        ResultSet catalogResultSet2 = 
            buildVarcharResultSet(new String[] { "aCatalog", "aCatalog2" });
        expect(mockDatabaseMetaData.getCatalogs()).andReturn(catalogResultSet2);
        
        
        ResultSet schemaResultSet1 = 
            buildVarcharResultSet(new String[] { "aSchema" });
        expect(mockDatabaseMetaData.getSchemas()).andReturn(schemaResultSet1);
        
        
        ResultSet schemaResultSet2 = 
            buildVarcharResultSet(new String[] { "aSchema", "aSchema2" });
        expect(mockDatabaseMetaData.getSchemas()).andReturn(schemaResultSet2);
        replay(mockDatabaseMetaData);

        mockConnection = createMock(Connection.class);
        expect(mockConnection.getMetaData()).andReturn(mockDatabaseMetaData).anyTimes();
        replay(mockConnection);        
        
        mockSqlConnection = createMock(ISQLConnection.class);
        expect(mockSqlConnection.getConnection()).andReturn(mockConnection).anyTimes();
        replay(mockSqlConnection);
        
        
        iut = new SQLDatabaseMetaData(mockSqlConnection);
	}

	public void testGetSchemas() {
		
		try {
			
			String[] currentSchemas = iut.getSchemas();
			assertEquals(1, currentSchemas.length);
						
			
			currentSchemas = iut.getSchemas();
			assertEquals(2, currentSchemas.length);
		} catch (SQLException e) {
			fail("Unexpected exception:  "+e.getMessage());
		}	
			
	}

	public void testGetCatalogs() {
		try {
            
			String[] currentCatalogs = iut.getCatalogs();
			assertEquals(1, currentCatalogs.length);
            			
			
			currentCatalogs = iut.getCatalogs();
			assertEquals(2, currentCatalogs.length);
		} catch (SQLException e) {
			fail("Unexpected exception:  "+e.getMessage());
		}
	}

    private ResultSet buildVarcharResultSet(String[] values) throws SQLException {
        ResultSetMetaData rsmd = createMock(ResultSetMetaData.class);
        expect(rsmd.getColumnCount()).andReturn(1);
        expect(rsmd.getColumnType(1)).andReturn(java.sql.Types.VARCHAR).anyTimes();
        expect(rsmd.getColumnTypeName(1)).andReturn("varchar").anyTimes();
        replay(rsmd);
        ResultSet rs = createMock(ResultSet.class);
        expect(rs.getMetaData()).andReturn(rsmd);
        for (String value : values) {
            expect(rs.next()).andReturn(true);
            expect(rs.getString(1)).andReturn(value);    
            expect(rs.wasNull()).andReturn(false);
        }
        expect(rs.next()).andReturn(false);
        rs.close();
        replay(rs);
        return rs;
    }
	
	
    public void testPGGetTableTypes() {
        try {
            String[] tableTypes = iut.getTableTypes();
            for (int i = 0; i < tableTypes.length; i++) {
                String type = tableTypes[i];
                assertFalse(
                    "'SYSTEM INDEX' is a type returned from " +
                    "SQLDatabaseMetaData.getTableTypes for PostgreSQL - " +
                    "it should not be.", 
                    "SYSTEM INDEX".equals(type));
            }
        } catch (SQLException e) {
            fail("Unexpected exception: "+e.getMessage());
        }
    }
    
    
    public void testGetIdentifierQuoteStringMSSQL() throws SQLException {
        Connection con = createNiceMock(Connection.class);
        DatabaseMetaData md = createNiceMock(DatabaseMetaData.class);
        expect(md.getIdentifierQuoteString()).andReturn("foo").anyTimes();
        expect(md.getDatabaseProductName())
                    .andReturn("microsoft")
                    .andReturn("sybase")
                    .andReturn("adaptive")
                    .andReturn("sql server");
        expect(con.getMetaData()).andReturn(md).anyTimes();
        replay(con);
        replay(md);
        SQLConnection sqlcon = new SQLConnection(con, null, null);
        SQLDatabaseMetaData sqlmd = new SQLDatabaseMetaData(sqlcon);
        try {
            String quoteString = sqlmd.getIdentifierQuoteString();
            assertEquals("foo", quoteString);
            quoteString = sqlmd.getIdentifierQuoteString();
            assertEquals("foo", quoteString);
            quoteString = sqlmd.getIdentifierQuoteString();
            assertEquals("foo", quoteString);
            quoteString = sqlmd.getIdentifierQuoteString();
            assertEquals("foo", quoteString);
            
        } catch (SQLException e) {
            fail("Unexpected exception: "+e.getMessage());
        }        
    }
    
    
}
