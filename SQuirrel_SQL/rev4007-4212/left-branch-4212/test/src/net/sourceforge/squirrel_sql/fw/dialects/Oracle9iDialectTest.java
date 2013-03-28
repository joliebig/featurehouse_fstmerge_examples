package net.sourceforge.squirrel_sql.fw.dialects;

public class Oracle9iDialectTest extends DialectTestCase {

    private Oracle9iDialect dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new Oracle9iDialect();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
