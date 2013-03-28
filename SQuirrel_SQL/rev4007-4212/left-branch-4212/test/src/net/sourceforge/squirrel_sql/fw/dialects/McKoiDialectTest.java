package net.sourceforge.squirrel_sql.fw.dialects;

public class McKoiDialectTest extends DialectTestCase {

    private McKoiDialect dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new McKoiDialect();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
