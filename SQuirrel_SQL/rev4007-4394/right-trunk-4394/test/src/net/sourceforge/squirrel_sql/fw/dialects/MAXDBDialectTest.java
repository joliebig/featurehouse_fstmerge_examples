package net.sourceforge.squirrel_sql.fw.dialects;

public class MAXDBDialectTest extends DialectTestCase {

    private MAXDBDialectExt dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new MAXDBDialectExt();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
