package net.sourceforge.squirrel_sql.fw.dialects;

public class IngresDialectTest extends DialectTestCase {

    private IngresDialect dialect = null;
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new IngresDialect();
    }
    
}
