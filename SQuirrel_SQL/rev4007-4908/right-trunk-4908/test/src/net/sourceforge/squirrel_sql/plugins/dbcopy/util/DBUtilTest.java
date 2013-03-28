package net.sourceforge.squirrel_sql.plugins.dbcopy.util;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.sql.SQLException;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;


public class DBUtilTest extends BaseSQuirreLTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetForeignKeySQL() throws Exception {
        
        
        ITableInfo ti = createNiceMock(ITableInfo.class);
        DBUtil.getForeignKeySQL(null, ti, null);
    }

    
    
    
    
    public void testGetQualifiedObjectName() throws SQLException {
        ISQLDatabaseMetaData md = createNiceMock(ISQLDatabaseMetaData.class);
        expect(md.getCatalogSeparator()).andReturn(".");
        expect(md.supportsCatalogsInTableDefinitions()).andReturn(true);
        expect(md.supportsSchemasInTableDefinitions()).andReturn(true);
        replay(md);
        ISession session = createNiceMock(ISession.class);
        expect(session.getMetaData()).andReturn(md).anyTimes();
        replay(session);
        String catalog = "TestCatalog";
        String schema = "TestSchema";
        String table = "TestTable";
        
        
        String expQualifiedName = catalog + "." + schema + "." + table;
        String actQualifiedName = 
            DBUtil.getQualifiedObjectName(session, 
                                          catalog, 
                                          schema, 
                                          table, 
                                          DialectFactory.SOURCE_TYPE);
        assertEquals(expQualifiedName, actQualifiedName);
    }
}
