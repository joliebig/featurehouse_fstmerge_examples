
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.client.AppTestUtil;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.FwTestUtil;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

public class ObjectTreeTest extends BaseSQuirreLTestCase {

    ObjectTree tree = null;
    ISession session = null;

    public static void main(String[] args) {
        
        junit.textui.TestRunner.run(ObjectTreeTest.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        session = AppTestUtil.getEasyMockSession("Oracle");
        tree = new ObjectTree(session);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    
    public void testMatchKeyPrefixDeletedRows() {
        Map<String, Object> map = new HashMap<String, Object>();
        String tableKey = "table(100)";
        map.put(tableKey, null);
        
        IDatabaseObjectInfo dbInfo = 
            FwTestUtil.getEasyMockDatabaseObjectInfo("catalog", 
                                                   "schema", 
                                                   "table", 
                                                   "schema.table",
                                                   DatabaseObjectType.TABLE);
        ObjectTreeNode node = new ObjectTreeNode(session, dbInfo);

        
        
        assertEquals(true, tree.matchKeyPrefix(map, node, "table(0)"));

        
        
        
        assertEquals(true, tree.matchKeyPrefix(map, node, "table"));
        
    }

}
