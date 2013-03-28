package net.sourceforge.squirrel_sql.fw.dialects;

public class HADBDialectTest extends DialectTestCase {

    private HADBDialectExt dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new HADBDialectExt();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
