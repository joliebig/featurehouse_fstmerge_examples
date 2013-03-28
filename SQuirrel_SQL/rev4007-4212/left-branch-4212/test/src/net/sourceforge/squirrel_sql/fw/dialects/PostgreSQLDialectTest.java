package net.sourceforge.squirrel_sql.fw.dialects;


public class PostgreSQLDialectTest extends DialectTestCase {

    private PostgreSQLDialect dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new PostgreSQLDialect();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
