package net.sourceforge.squirrel_sql.fw.dialects;

public class FirebirdDialectTest extends DialectTestCase {

    private FirebirdDialectExt dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new FirebirdDialectExt();
    }
        
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
