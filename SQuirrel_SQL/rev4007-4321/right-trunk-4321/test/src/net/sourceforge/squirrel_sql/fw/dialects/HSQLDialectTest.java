package net.sourceforge.squirrel_sql.fw.dialects;

public class HSQLDialectTest extends DialectTestCase {

    private HSQLDialectExt dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new HSQLDialectExt();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }    
}
