package net.sourceforge.squirrel_sql.fw.dialects;

public class MAXDBDialectTest extends DialectTestCase {

    private MAXDBDialect dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new MAXDBDialect();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
