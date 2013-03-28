package net.sourceforge.squirrel_sql.fw.dialects;

public class SybaseDialectTest extends DialectTestCase {

    private SybaseDialect dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new SybaseDialect();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
