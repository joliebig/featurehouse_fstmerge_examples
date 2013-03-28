package net.sourceforge.squirrel_sql.fw.dialects;

public class SQLServerDialectTest extends DialectTestCase {

    private SQLServerDialect dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new SQLServerDialect();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
