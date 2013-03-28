package net.sourceforge.squirrel_sql.fw.id;

import junit.framework.TestCase;

import com.gargoylesoftware.base.testing.EqualsTester;

public class IntegerIdentifierTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testEquals() {
        IntegerIdentifier uid1 = new IntegerIdentifier(1);

        IntegerIdentifier uid2 = new IntegerIdentifier(1);

        IntegerIdentifier uid3 = new IntegerIdentifier(2);

        IntegerIdentifier uid4 = new IntegerIdentifier(1) {
            private static final long serialVersionUID = 1L;
        };

        new EqualsTester(uid1, uid2, uid3, uid4);
    }

}
