package net.sourceforge.squirrel_sql.fw.dialects;

public class TimesTenDialectTest extends DialectTestCase {

    private TimesTenDialect dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new TimesTenDialect();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
