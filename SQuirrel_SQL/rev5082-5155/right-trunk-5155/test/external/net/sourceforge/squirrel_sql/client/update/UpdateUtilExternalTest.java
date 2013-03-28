package net.sourceforge.squirrel_sql.client.update;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializerImpl;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactoryImpl;
import net.sourceforge.squirrel_sql.fw.util.IProxySettings;
import net.sourceforge.squirrel_sql.fw.util.ProxySettings;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class UpdateUtilExternalTest extends BaseSQuirreLJUnit4TestCase {

    UpdateUtil utilUnderTest = new UpdateUtilImpl();
    UpdateXmlSerializer serializer = null;
    FileWrapperFactory fileWrapperFactory = new FileWrapperFactoryImpl(); 
    IProxySettings proxySettings = new ProxySettings();
    
    @Before
    public void setUp() throws Exception {
        
        serializer = new UpdateXmlSerializerImpl();
    }

    @After
    public void tearDown() {
        
    }
    
    @Test
    public void testFileDownload() throws Exception {
        String host = "squirrel-sql.sourceforge.net";
        String file = "firebird_object_tree.jpg";
        
        int port = 80;
        
        utilUnderTest.downloadHttpUpdateFile(host, port, file, "/tmp", -1, -1, proxySettings);
        verifyFileExistsAndDeleteIt("firebird_object_tree.jpg", false);
    }
    
    @Test
    public void downloadHttpFile() throws Exception {
        String host = "squirrel-sql.sourceforge.net";
        String file = UpdateUtil.RELEASE_XML_FILENAME;
        
        int port = 80;
        utilUnderTest.downloadHttpUpdateFile(host, port, file, "/tmp", -1, -1, proxySettings);
        verifyFileExistsAndDeleteIt(file, false);
    }

    @Test
    public void testExtractZipFile() throws Exception {
   	 FileWrapper graphZip = fileWrapperFactory.create("/tmp/graph.zip");
   	 FileWrapper extractDir = fileWrapperFactory.create("/tmp/extract");
   	 utilUnderTest.extractZipFile(graphZip, extractDir);
    }
    
    
    @Test
    public void testFileDownloadCurrentRelease() throws Exception {
        String host = "http://squirrel-sql.sourceforge.net";
        String path = "/release/snapshot/";
        String file = UpdateUtil.RELEASE_XML_FILENAME;
        
        ChannelXmlBean bean = utilUnderTest.downloadCurrentRelease(host, 80, path, file, proxySettings);
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
