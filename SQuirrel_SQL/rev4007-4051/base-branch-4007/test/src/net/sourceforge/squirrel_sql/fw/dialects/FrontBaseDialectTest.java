package net.sourceforge.squirrel_sql.fw.dialects;

public class FrontBaseDialectTest extends DialectTestCase {

    private FrontBaseDialect dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new FrontBaseDialect();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(Oracle9iDialectTest.class);
    }

}
