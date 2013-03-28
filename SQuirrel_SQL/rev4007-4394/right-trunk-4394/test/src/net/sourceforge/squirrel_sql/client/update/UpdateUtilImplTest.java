
package net.sourceforge.squirrel_sql.client.update;

import static org.junit.Assert.*;

import java.io.File;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UpdateUtilImplTest extends BaseSQuirreLJUnit4TestCase {

   UpdateUtilImpl underTest = null;
   
   String codeCompletionDir = 
      "/opt/squirrel/eclipse_build/plugins/refactoring";
   String codeCompletionJar = 
      "/opt/squirrel/eclipse_build/plugins/refactoring.jar";
   
   String zipFile = 
      "/opt/squirrel/eclipse_build/update/backup/plugin/codecompletion.zip";
      
   
   @Before
   public void setUp() throws Exception {
      underTest = new UpdateUtilImpl();
   }

   @After
   public void tearDown() throws Exception {
      underTest = null;
   }

   @Test
   public void testCreateZipFile() throws Exception {
      File[] files = new File[2];
      files[0] = new File(codeCompletionDir);
      files[1] = new File(codeCompletionJar);
      File target = new File(zipFile);
      underTest.createZipFile(target, files);
   }

}
