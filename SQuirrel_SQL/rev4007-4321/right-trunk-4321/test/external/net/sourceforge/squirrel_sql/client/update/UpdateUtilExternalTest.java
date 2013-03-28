package net.sourceforge.squirrel_sql.client.update;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class UpdateUtilExternalTest extends BaseSQuirreLJUnit4TestCase {

    UpdateUtil utilUnderTest = new UpdateUtilImpl();
    UpdateXmlSerializer serializer = null;
    
    @Before
    public void setUp() throws Exception {
        
        serializer = new UpdateXmlSerializer();
    }

    @After
    public void tearDown() {
        
    }
    
    @Test
    public void testFileDownload() throws Exception {
        String host = "squirrel-sql.sourceforge.net";
        String file = "firebird_object_tree.jpg";
        String path = "/downloads/";
        int port = 80;
        
        utilUnderTest.downloadHttpFile(host, port, file, "/tmp");
        verifyFileExistsAndDeleteIt("firebird_object_tree.jpg", false);
    }
    
    @Test
    public void downloadHttpFile() throws Exception {
        String host = "squirrel-sql.sourceforge.net";
        String file = "release.xml";
        String path = "/release/snapshot/";
        int port = 80;
        utilUnderTest.downloadHttpFile(host, port, file, "/tmp");
        verifyFileExistsAndDeleteIt("release.xml", false);
    }

    
    @Test
    public void testFileDownloadCurrentRelease() throws Exception {
        String host = "http://squirrel-sql.sourceforge.net";
        String path = "/release/snapshot/";
        String file = "release.xml";
        
        ChannelXmlBean bean = utilUnderTest.downloadCurrentRelease(host, 80, path, file);
        assertNotNull(bean);
        serializer.write(bean, "/tmp/test.xml");
        verifyFileExistsAndDeleteIt("test.xml", false);
    }
    
    private void verifyFileExistsAndDeleteIt(String filename, boolean delete) throws Exception {
       File downloadFile = new File("/tmp", filename);
       assertTrue(downloadFile.exists());
       if (delete) {
      	 downloadFile.delete();
       }
    }
    
}
