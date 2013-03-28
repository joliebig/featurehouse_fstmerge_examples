
package net.sourceforge.squirrel_sql.fw.dialects;


public class PointbaseDialectTest extends DialectTestCase {

    private PointbaseDialectExt dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new PointbaseDialectExt();
    }

    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
