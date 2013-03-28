package net.sourceforge.squirrel_sql.fw.dialects;

public class SybaseDialectTest extends DialectTestCase {

    private SybaseDialectExt dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new SybaseDialectExt();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
