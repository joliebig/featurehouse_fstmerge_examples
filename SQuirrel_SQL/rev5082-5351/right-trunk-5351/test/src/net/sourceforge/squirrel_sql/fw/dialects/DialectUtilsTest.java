
package net.sourceforge.squirrel_sql.fw.dialects;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.FwTestUtil;
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyColumnInfo;
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.IndexInfo;
import net.sourceforge.squirrel_sql.fw.sql.PrimaryKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DialectUtilsTest extends BaseSQuirreLJUnit4TestCase {

    private final static String catalog = "testCatalog";

    private final static String schema = "testSchema";

    private final static String table = "testTable";

    private final static String pkCol = "id";

    private final static String pkName = "PKtestTable";

    private final static List<IndexInfo> noIndexList = new ArrayList<IndexInfo>();
    
    private final static ForeignKeyInfo[] noFKArray = new ForeignKeyInfo[0];
    
    private final static PrimaryKeyInfo[] noPrimaryKey = new PrimaryKeyInfo[0];
    
    CreateScriptPreferences prefs = null;
    
    ISQLDatabaseMetaData mockMetaData;

    ITableInfo childTableInfo;
    
    ITableInfo parentTableInfo;
    
    List<ITableInfo> oneTableList = new ArrayList<ITableInfo>();

    List<ITableInfo> twoTableList = new ArrayList<ITableInfo>();
    


    PrimaryKeyInfo mockPrimaryKeyInfo;
    
    PrimaryKeyInfo[] pkInfos;
    
    ForeignKeyInfo[] fkinfos;
    
    IndexInfo mockIndexInfo;
    
    List<IndexInfo> mockIndexInfos;
    
    ForeignKeyColumnInfo mockForeignKeyColumnInfo = 
   	 mockHelper.createMock("mockForeignKeyColumnInfo", ForeignKeyColumnInfo.class);
    
    @Before
    public void setUp() throws Exception {
        
        prefs = new CreateScriptPreferences();
        prefs.setIncludeExternalReferences(true);
        
        mockMetaData = FwTestUtil.getEasyMockSQLMetaData("oracle", 
                                                       "jdbc:oracle:thin",
                                                        false,
                                                        false);
        
        
        
        mockPrimaryKeyInfo = FwTestUtil.getEasyMockPrimaryKeyInfo(catalog, schema, table, pkCol, (short)1, pkName, true);
        
        pkInfos = new PrimaryKeyInfo[] { mockPrimaryKeyInfo };
        
        List<String> columnNames = 
            Arrays.asList(new String[] { pkCol, "fkcol", "data" });
        List<Integer> dataTypes = 
            Arrays.asList(new Integer[] { Types.INTEGER, Types.VARCHAR, Types.VARCHAR });
        
        List<String> parentColumnNames =
            Arrays.asList(new String[] { pkCol,  "startTime" });
        List<Integer> parentDataTypes = 
            Arrays.asList(new Integer[] { Types.INTEGER, Types.DATE });
        
        TableColumnInfo[] childColInfos = 
            FwTestUtil.getEasyMockTableColumns(catalog, schema, table, columnNames, dataTypes);

        TableColumnInfo[] parentColInfos = 
            FwTestUtil.getEasyMockTableColumns(catalog, schema, table, parentColumnNames, parentDataTypes);
        
        ISQLDatabaseMetaData tableMockMetaData = 
            FwTestUtil.getEasyMockSQLMetaData("oracle", 
                                            "jdbc:oracle:thin",
                                            false,
                                            true);
        
        childTableInfo = new TableInfo("testCatalog",
                                  "testSchema",
                                  "childTable",
                                  "TABLE",
                                  "a comment",
                                  tableMockMetaData);  
        
        parentTableInfo = new TableInfo("testCatalog",
                                        "testSchema",
                                        "parentTable",
                                        "TABLE",
                                        "a comment",
                                        tableMockMetaData);
        
        oneTableList.add(childTableInfo);
        
        twoTableList.add(childTableInfo);
        twoTableList.add(parentTableInfo);
        
        mockIndexInfos = FwTestUtil.getEasyMockIndexInfos("testTable", "data1");
        
        fkinfos = FwTestUtil.getEasyMockForeignKeyInfos("ChildTable_FK", 
                                                      "childTable",
                                                      "fkcol",
                                                      "parentTable",
                                                      "id");
        
        
        expect(mockMetaData.getPrimaryKey(childTableInfo)).andReturn(pkInfos).anyTimes();
        expect(mockMetaData.getPrimaryKey(parentTableInfo)).andReturn(noPrimaryKey).anyTimes();
        
        expect(mockMetaData.getColumnInfo(childTableInfo)).andReturn(childColInfos).anyTimes();
        expect(mockMetaData.getColumnInfo(parentTableInfo)).andReturn(parentColInfos).anyTimes();
        
        expect(mockMetaData.getImportedKeysInfo(childTableInfo)).andReturn(fkinfos).anyTimes();
        expect(mockMetaData.getImportedKeysInfo(parentTableInfo)).andReturn(noFKArray).anyTimes();
        
        expect(mockMetaData.getIndexInfo(childTableInfo)).andReturn(mockIndexInfos).anyTimes();
        expect(mockMetaData.getIndexInfo(parentTableInfo)).andReturn(noIndexList).anyTimes();
        
        replayMocks();
                                          
    }    
    
    @After
    public void tearDown() throws Exception {
        mockMetaData = null;
        childTableInfo = null;
    }

    private void replayMocks() {
        replay(mockMetaData);
    }

    

    @Test
    public void testGetTableSource() throws SQLException {
        Object[] dbNames = DialectFactory.getDbNames();
        for (Object dbName : dbNames) {
            HibernateDialect dialect = DialectFactory.getDialect(dbName.toString());
            checkGetTableSource(dialect, 
                                oneTableList,   
                                mockMetaData, 
                                prefs, 
                                false,
                                3);
        }
    }
    
    @Test
    public void testConstraintsAfterTable() throws SQLException {
        prefs.setConstraintsAtEnd(false);
        checkGetTableSource(new HSQLDialectExt(), 
                            twoTableList,
                            mockMetaData,
                            prefs,
                            false,
                            4);        
    }

    @Test
    public void testGetTableSourceDeleteAction() throws SQLException {
        prefs.setDeleteRefAction(true);
        prefs.setDeleteAction(DatabaseMetaData.importedKeyCascade);
        List<String> sqls = checkGetTableSource(new HSQLDialectExt(), 
                                                 twoTableList,
                                                 mockMetaData,
                                                 prefs,
                                                 false,
                                                 4);
        checkAction(sqls, "ALTER TABLE", " ON DELETE CASCADE");
        










        








        
        prefs.setDeleteAction(DatabaseMetaData.importedKeySetNull);
        sqls = checkGetTableSource(new HSQLDialectExt(), 
                                   twoTableList,
                                   mockMetaData,
                                   prefs,
                                   false,
                                   4);        
        checkAction(sqls, "ALTER TABLE", " ON DELETE SET NULL");
        
        prefs.setDeleteAction(DatabaseMetaData.importedKeySetDefault);
        sqls = checkGetTableSource(new HSQLDialectExt(), 
                                   twoTableList,
                                   mockMetaData,
                                   prefs,
                                   false,
                                   4);  
        checkAction(sqls, "ALTER TABLE", " ON DELETE SET DEFAULT");
        

    }    
    
    @Test
    public void testIsJdbcOdbc() throws SQLException {
        checkGetTableSource(new HSQLDialectExt(), 
                            twoTableList,
                            mockMetaData,
                            prefs,
                            true,
                            2);        
    }

    
    
    private List<String> checkGetTableSource(HibernateDialect d, 
                                     List<ITableInfo> tableList,
                                     ISQLDatabaseMetaData md,
                                     CreateScriptPreferences scriptPrefs,
                                     boolean isJdbcOdbc,
                                     int sqlCount) 
        throws SQLException 
    {
        
        List<String> createSQLs = 
            DialectUtils.getCreateTableSQL(tableList, md, d, scriptPrefs, isJdbcOdbc);
        assertEquals("SQL Statement Count", sqlCount, createSQLs.size());
        for (String sql : createSQLs) {
            
            assertEquals("sql.length() <= 0", true, sql.length() > 0);
        }        
        return createSQLs;
    }
    
    private void checkAction(List<String> sqls, 
                             String prefix, 
                             String actionClause) 
    {
        for (String sql : sqls) {
            if (sql.startsWith(prefix)) {
                int idx = sql.indexOf(actionClause);
                assertTrue(
                    "idx == -1: actionClause("+actionClause+") not found in sql: "+
                    sql, 
                    idx != -1);
            }
        }
    }
}
