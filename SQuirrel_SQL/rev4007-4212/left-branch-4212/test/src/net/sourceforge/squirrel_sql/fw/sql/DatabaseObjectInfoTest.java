
package net.sourceforge.squirrel_sql.fw.sql;

import static org.junit.Assert.*;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.test.TestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.base.testing.EqualsTester;

public class DatabaseObjectInfoTest extends BaseSQuirreLJUnit4TestCase {

    DatabaseObjectInfo dboInfoUnderTest = null;

    ISQLDatabaseMetaData oracleSQLDatabaseMetaData = null;
    
    ISQLDatabaseMetaData h2SQLDatabaseMetaData = null; 

    String testCatalog = "TestCatalog";

    String testSchema = "TestSchema";

    @Before
    public void setUp() throws Exception {
        oracleSQLDatabaseMetaData = 
            TestUtil.getEasyMockSQLMetaData("oracle", "jdbc:oracle:thin@",
                false, true);
        
        h2SQLDatabaseMetaData = TestUtil.getEasyMockH2SQLMetaData();
    }

    @After
    public void tearDown() throws Exception {
        h2SQLDatabaseMetaData = null;
        oracleSQLDatabaseMetaData = null;
    }

    @Test
    public final void testGetQualifiedNameH2() throws Exception {
        
        String tableName = "foo\"\"bar";  
        dboInfoUnderTest = new DatabaseObjectInfo(testCatalog, testSchema,
                tableName, DatabaseObjectType.TABLE, h2SQLDatabaseMetaData);
        
        String identifierQuoteString = 
            h2SQLDatabaseMetaData.getIdentifierQuoteString();
        String sep = h2SQLDatabaseMetaData.getCatalogSeparator();
        String expectedQualifiedName = 
            identifierQuoteString + testSchema  + identifierQuoteString + sep +
            identifierQuoteString + "foo\"\"\"\"bar" + identifierQuoteString;
        
        String qn = dboInfoUnderTest.getQualifiedName();
        assertEquals(expectedQualifiedName, qn);
    }

    @Test
    public final void testEqualsAndHashcode() {
        DatabaseObjectInfo a = new DatabaseObjectInfo(testCatalog, testSchema,
                "table1", DatabaseObjectType.TABLE, h2SQLDatabaseMetaData);
        DatabaseObjectInfo b = new DatabaseObjectInfo(testCatalog, testSchema,
                "table1", DatabaseObjectType.TABLE, h2SQLDatabaseMetaData);
        
        DatabaseObjectInfo c = new DatabaseObjectInfo(testCatalog, testSchema,
                "table2", DatabaseObjectType.TABLE, h2SQLDatabaseMetaData);
        
        DatabaseObjectInfo d = new MyDatabaseObjectInfo(testCatalog, testSchema,
                "table1", DatabaseObjectType.TABLE, h2SQLDatabaseMetaData);
        
        new EqualsTester(a,b,c,d);
    }

    @SuppressWarnings("serial")
    private static class MyDatabaseObjectInfo extends DatabaseObjectInfo {
        
        public MyDatabaseObjectInfo(String catalog, 
                                  String schema, 
                                  String simpleName,
                                  DatabaseObjectType dboType, 
                                  ISQLDatabaseMetaData md) 
        {
            super(catalog,schema,simpleName,dboType,md);
        }        
    }
}
