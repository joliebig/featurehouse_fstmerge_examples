package net.sourceforge.squirrel_sql.fw.dialects;

public class AxionDialectTest extends DialectTestCase {

    private AxionDialectExt dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new AxionDialectExt();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
