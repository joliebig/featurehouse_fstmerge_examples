
import java.util.ArrayList;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactAction;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class ChangeListXmlBeanExternalTest extends BaseSQuirreLJUnit4TestCase {

    
      
   @Before
   public void setUp() throws Exception {
   }

   @After
   public void tearDown() throws Exception {
   }
   
   @Test
   public void testWriteChangeList() throws Exception  {
      ArtifactStatus status = new ArtifactStatus();
      status.setName("fw.jar");
      status.setType("core");
      status.setInstalled(true);
      status.setArtifactAction(ArtifactAction.INSTALL);
      
      ArrayList<ArtifactStatus> list = new ArrayList<ArtifactStatus>();
      list.add(status);
      
      ChangeListXmlBean bean = new ChangeListXmlBean();
      bean.setChanges(list);
      
      UpdateXmlSerializer serializer = new UpdateXmlSerializer();
      serializer.write(bean, "/tmp/changeList.xml");
   }

}
