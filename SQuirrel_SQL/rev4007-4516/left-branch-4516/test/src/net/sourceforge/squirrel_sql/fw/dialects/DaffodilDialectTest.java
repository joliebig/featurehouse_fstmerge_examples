package net.sourceforge.squirrel_sql.fw.dialects;

public class DaffodilDialectTest extends DialectTestCase {

    private DaffodilDialect dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new DaffodilDialect();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
