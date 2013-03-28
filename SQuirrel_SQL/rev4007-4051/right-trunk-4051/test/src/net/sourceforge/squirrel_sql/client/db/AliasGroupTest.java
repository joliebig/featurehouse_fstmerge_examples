
package net.sourceforge.squirrel_sql.client.db;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifier;

import com.gargoylesoftware.base.testing.EqualsTester;

public class AliasGroupTest extends BaseSQuirreLTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testEqualsObject() throws Exception {
        IIdentifier id1 = new IntegerIdentifier(1);
        IIdentifier id2 = new IntegerIdentifier(2);
        String name1 = "NameTest";
        String name2 = "NameTest2";
        
        AliasGroup ag1 = new AliasGroup();
        ag1.setIdentifier(id1);
        ag1.setName(name1);
        
        AliasGroup ag2 = new AliasGroup();
        ag2.setIdentifier(id1);
        ag2.setName(name1);
        
        AliasGroup ag3 = new AliasGroup();
        ag3.setIdentifier(id2);
        ag3.setName(name2);
        
        AliasGroup ag4 = new AliasGroup() {
            private static final long serialVersionUID = 1L;
        };
        ag4.setIdentifier(id1);
        ag4.setName(name1);
        
        new EqualsTester(ag1, ag2, ag3, ag4);
        
    }

}
