package net.sourceforge.squirrel_sql.fw.dialects;

public class DB2DialectTest extends DialectTestCase {

    private DB2Dialect dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new DB2Dialect();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
