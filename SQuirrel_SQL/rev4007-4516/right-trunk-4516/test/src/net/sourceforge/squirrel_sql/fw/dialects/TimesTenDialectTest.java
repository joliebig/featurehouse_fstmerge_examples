package net.sourceforge.squirrel_sql.fw.dialects;

public class TimesTenDialectTest extends DialectTestCase {

    private TimesTenDialectExt dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new TimesTenDialectExt();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
