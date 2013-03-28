package net.sourceforge.squirrel_sql.fw.dialects;

public class McKoiDialectTest extends DialectTestCase {

    private McKoiDialectExt dialect = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dialect = new McKoiDialectExt();
    }
    
    
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
