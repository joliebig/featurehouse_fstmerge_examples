package net.sourceforge.squirrel_sql.client.update;


import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.update.UpdateUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class UpdateUtilExternalTest extends BaseSQuirreLJUnit4TestCase {

    UpdateUtil utilUnderTest = null;
    
    @Before
    public void setUp() throws Exception {
        utilUnderTest = new UpdateUtilImpl();
    }

    @After
    public void tearDown() {
        utilUnderTest = null;
    }
    
    @Test
    public void testFileDownload() {
        String host = "squirrel-sql.sourceforge.net";
        String file = "firebird_object_tree.jpg";
        String path = "/downloads/";
        UpdateUtil util = new UpdateUtilImpl();
        util.downloadHttpFile(host, path, file, ".");
    }
}
