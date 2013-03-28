package net.sourceforge.squirrel_sql.fw.dialects;

public class DerbyDialectTest extends DialectTestCase {

    private DerbyDialectExt dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new DerbyDialectExt();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(Oracle9iDialectTest.class);
    }

}
