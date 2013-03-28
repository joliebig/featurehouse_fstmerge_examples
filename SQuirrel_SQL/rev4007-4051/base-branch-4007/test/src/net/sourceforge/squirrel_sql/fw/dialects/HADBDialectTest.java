package net.sourceforge.squirrel_sql.fw.dialects;

public class HADBDialectTest extends DialectTestCase {

    private HADBDialect dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new HADBDialect();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
