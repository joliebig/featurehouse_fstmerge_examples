package net.sourceforge.squirrel_sql.fw.id;

import junit.framework.TestCase;

import com.gargoylesoftware.base.testing.EqualsTester;

public class UidIdentifierTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testEquals() {
        UidIdentifier uid1 = createIIdentifier();
        uid1.setString("1");
        UidIdentifier uid2 = createIIdentifier();
        uid2.setString("1");
        UidIdentifier uid3 = createIIdentifier();
        uid3.setString("2");
        UidIdentifier uid4 = new UidIdentifier() {
            private static final long serialVersionUID = 1L;
        };
        uid4.setString("1");
        new EqualsTester(uid1, uid2, uid3, uid4);
    }

    protected UidIdentifier createIIdentifier() {
        return new UidIdentifier();
    }
}
