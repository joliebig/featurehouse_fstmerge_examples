package net.sourceforge.squirrel_sql.fw.dialects;

public class ProgressDialectTest extends DialectTestCase {

    private ProgressDialectExt dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new ProgressDialectExt();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
