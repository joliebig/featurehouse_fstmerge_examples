
package net.sourceforge.squirrel_sql.client.session.mainpanel;

import junit.framework.TestCase;

import com.gargoylesoftware.base.testing.EqualsTester;

public class SQLHistoryItemTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testEqualsObject() {
        String sql1 = "select foo from foo";
        String sql2 = "select foo2 from foo2";
        String aliasName1 = "TestAlias";
        String aliasName2 = "TestAlias2";
        SQLHistoryItem item1 = new SQLHistoryItem(sql1, aliasName1);
        SQLHistoryItem item2 = new SQLHistoryItem(sql1, aliasName1);
        SQLHistoryItem item3 = new SQLHistoryItem(sql2, aliasName2);
        SQLHistoryItem item4 = new SQLHistoryItem(sql1, aliasName1) {
            private static final long serialVersionUID = 1L;
        } ;
        
        new EqualsTester(item1, item2, item3, item4);
    }

}
