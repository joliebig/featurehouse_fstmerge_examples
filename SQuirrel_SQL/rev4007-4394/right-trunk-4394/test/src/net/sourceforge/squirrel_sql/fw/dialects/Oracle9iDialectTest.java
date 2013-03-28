package net.sourceforge.squirrel_sql.fw.dialects;

public class Oracle9iDialectTest extends DialectTestCase {

    private OracleDialectExt dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new OracleDialectExt();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
