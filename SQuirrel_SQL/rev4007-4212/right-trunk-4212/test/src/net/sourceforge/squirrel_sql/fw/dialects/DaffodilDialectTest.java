package net.sourceforge.squirrel_sql.fw.dialects;

public class DaffodilDialectTest extends DialectTestCase {

    private DaffodilDialectExt dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new DaffodilDialectExt();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
