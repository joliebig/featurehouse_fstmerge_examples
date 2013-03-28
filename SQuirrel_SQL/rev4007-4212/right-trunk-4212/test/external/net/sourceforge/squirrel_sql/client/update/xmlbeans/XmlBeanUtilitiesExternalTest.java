package net.sourceforge.squirrel_sql.client.update.xmlbeans;



import java.io.File;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.XmlBeanUtilities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class XmlBeanUtilitiesExternalTest extends BaseSQuirreLJUnit4TestCase {

    XmlBeanUtilities utilUnderTest = null;
    
    @Before
    public void setUp() throws Exception {
        utilUnderTest = new XmlBeanUtilities();
    }

    @After
    public void tearDown() {
        utilUnderTest = null;
    }
    
    @Test
    public void testChecksum() {
        File f = new File("/tmp/update_feature.zip");
        System.out.println("Checksum="+utilUnderTest.getCheckSum(f));
    }
    
    @Test
    public void testBuildRelease() throws Exception {
        String dir = "/home/manningr/weeklybuild/squirrel-sql-dist/release";
        ChannelXmlBean release = utilUnderTest.buildChannelRelease("Snapshot",
                                                                 "Snapshot",
                                                                 "Snapshot-20071001_0938",
                                                                 dir);
        UpdateXmlSerializer serializer = new UpdateXmlSerializer();
        serializer.write(release, dir + "/release.xml");
    }
}
