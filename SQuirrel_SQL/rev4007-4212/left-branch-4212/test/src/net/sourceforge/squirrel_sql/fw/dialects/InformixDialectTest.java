package net.sourceforge.squirrel_sql.fw.dialects;

public class InformixDialectTest extends DialectTestCase {

    private InformixDialect dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new InformixDialect();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
