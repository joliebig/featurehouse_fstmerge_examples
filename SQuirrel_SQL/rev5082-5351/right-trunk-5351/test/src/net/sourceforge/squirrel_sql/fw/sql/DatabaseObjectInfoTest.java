
package net.sourceforge.squirrel_sql.fw.sql;

import static org.junit.Assert.*;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.FwTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.base.testing.EqualsTester;

public class DatabaseObjectInfoTest extends BaseSQuirreLJUnit4TestCase {

    DatabaseObjectInfo dboInfoUnderTest = null;

    ISQLDatabaseMetaData oracleSQLDatabaseMetaData = null;
    
    ISQLDatabaseMetaData h2SQLDatabaseMetaData = null; 

    ISQLDatabaseMetaData sybase12SQLDatabaseMeta = null;
    
    ISQLDatabaseMetaData sybase15SQLDatabaseMeta = null;
    
    String testCatalog = "TestCatalog";

    String testSchema = "TestSchema";

    @Before
    public void setUp() throws Exception {
        oracleSQLDatabaseMetaData = 
            FwTestUtil.getEasyMockSQLMetaData("oracle", "jdbc:oracle:thin@",
                false, true);
        
        h2SQLDatabaseMetaData = FwTestUtil.getEasyMockH2SQLMetaData();
        sybase12SQLDatabaseMeta = FwTestUtil.getEasyMockSybase12SQLMetaData();
        sybase15SQLDatabaseMeta = FwTestUtil.getEasyMockSybase15SQLMetaData();
    }

    @After
    public void tearDown() throws Exception {
        h2SQLDatabaseMetaData = null;
        oracleSQLDatabaseMetaData = null;
        sybase12SQLDatabaseMeta = null;
        sybase15SQLDatabaseMeta = null;
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
    public final void testGetQualifiedNameSybase12() throws Exception {
       String tableName = "mytable";
       final ISQLDatabaseMetaData md = sybase12SQLDatabaseMeta;
       assertEquals("Adaptive Server Enterprise", md.getDatabaseProductName());
       dboInfoUnderTest = 
          new DatabaseObjectInfo(testCatalog, testSchema, tableName, 
                                 DatabaseObjectType.TABLE, md);
       String identifierQuoteString = md.getIdentifierQuoteString();
       
       
       
       assertEquals("\"", identifierQuoteString);
       
       String sep = md.getCatalogSeparator();
       
       String expected = testCatalog + sep + testSchema + sep + tableName;
       String actual = dboInfoUnderTest.getQualifiedName();
       assertEquals(expected, actual);
    }

    @Test 
    public final void testGetQualifiedNameSybase15() throws Exception {
       String tableName = "mytable";
       final ISQLDatabaseMetaData md = sybase15SQLDatabaseMeta;
       assertEquals("Adaptive Server Enterprise", md.getDatabaseProductName());
       dboInfoUnderTest = 
          new DatabaseObjectInfo(testCatalog, testSchema, tableName, 
                                 DatabaseObjectType.TABLE, md);
       String identifierQuoteString = md.getIdentifierQuoteString();
       
       
       
       assertEquals("\"", identifierQuoteString);
       
       String sep = md.getCatalogSeparator();
       
       
       
       String expected = identifierQuoteString + testCatalog + identifierQuoteString + sep + 
                         identifierQuoteString + testSchema + identifierQuoteString + sep + 
                         identifierQuoteString + tableName + identifierQuoteString;
       String actual = dboInfoUnderTest.getQualifiedName();
       assertEquals(expected, actual);
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
