
package net.sourceforge.squirrel_sql.fw.dialects;


public class MySQLDialectTest extends DialectTestCase {

    private MySQLDialect dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new MySQLDialect();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
