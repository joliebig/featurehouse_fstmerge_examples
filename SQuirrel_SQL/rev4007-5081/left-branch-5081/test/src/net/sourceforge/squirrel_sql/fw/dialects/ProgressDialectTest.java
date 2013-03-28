package net.sourceforge.squirrel_sql.fw.dialects;

public class ProgressDialectTest extends DialectTestCase {

    private ProgressDialect dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new ProgressDialect();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
