package net.sourceforge.squirrel_sql.fw.dialects;


public class PostgreSQLDialectTest extends DialectTestCase {

    private PostgreSQLDialectExt dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new PostgreSQLDialectExt();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
