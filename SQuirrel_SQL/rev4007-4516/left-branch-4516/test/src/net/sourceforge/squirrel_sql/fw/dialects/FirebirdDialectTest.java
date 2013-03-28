package net.sourceforge.squirrel_sql.fw.dialects;

public class FirebirdDialectTest extends DialectTestCase {

    private FirebirdDialect dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new FirebirdDialect();
    }
        
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
