
package net.sourceforge.squirrel_sql.fw.dialects;


public class MySQLDialectTest extends DialectTestCase {

    private MySQLDialectExt dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new MySQLDialectExt();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
