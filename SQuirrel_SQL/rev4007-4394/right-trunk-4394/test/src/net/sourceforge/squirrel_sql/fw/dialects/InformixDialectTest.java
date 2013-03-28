package net.sourceforge.squirrel_sql.fw.dialects;

public class InformixDialectTest extends DialectTestCase {

    private InformixDialectExt dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new InformixDialectExt();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
