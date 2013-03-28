package net.sourceforge.squirrel_sql.fw.dialects;

public class SQLServerDialectTest extends DialectTestCase {

    private SQLServerDialectExt dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new SQLServerDialectExt();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
