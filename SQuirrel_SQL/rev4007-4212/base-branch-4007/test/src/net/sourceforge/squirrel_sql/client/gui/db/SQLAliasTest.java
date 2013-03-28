package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifierFactory;

import com.gargoylesoftware.base.testing.EqualsTester;

public class SQLAliasTest extends BaseSQuirreLTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @SuppressWarnings("serial")
    public void testEqualsObject() {
        IIdentifierFactory factory = IdentifierFactory.getInstance();
        IIdentifier id1 = factory.createIdentifier();
        IIdentifier id2 = factory.createIdentifier();
        SQLAlias alias1 = new SQLAlias(id1);
        SQLAlias alias2 = new SQLAlias(id1);
        SQLAlias alias3 = new SQLAlias(id2);
        SQLAlias alias4 = new SQLAlias(id1) {
            
        };
        new EqualsTester(alias1, alias2, alias3, alias4);
    }

    public void testIsValid() {
        SQLAlias uninitializedAlias = new SQLAlias();
        assertEquals(false, uninitializedAlias.isValid());
    }

}
