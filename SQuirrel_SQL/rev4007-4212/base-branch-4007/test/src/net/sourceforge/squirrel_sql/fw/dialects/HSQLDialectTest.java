package net.sourceforge.squirrel_sql.fw.dialects;

public class HSQLDialectTest extends DialectTestCase {

    private HSQLDialect dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new HSQLDialect();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }    
}
