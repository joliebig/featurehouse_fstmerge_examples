
package net.sourceforge.squirrel_sql.fw.dialects;


public class PointbaseDialectTest extends DialectTestCase {

    private PointbaseDialect dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new PointbaseDialect();
    }

    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
