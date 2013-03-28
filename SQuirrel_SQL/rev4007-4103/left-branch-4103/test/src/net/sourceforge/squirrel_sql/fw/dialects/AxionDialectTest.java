package net.sourceforge.squirrel_sql.fw.dialects;

public class AxionDialectTest extends DialectTestCase {

    private AxionDialect dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new AxionDialect();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
