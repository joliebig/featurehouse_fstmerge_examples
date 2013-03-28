package net.sourceforge.squirrel_sql.fw.dialects;

public class H2DialectTest extends DialectTestCase {

    private H2DialectExt dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new H2DialectExt();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
