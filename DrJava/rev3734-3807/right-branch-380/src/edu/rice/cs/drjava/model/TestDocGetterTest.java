

package edu.rice.cs.drjava.model;

import edu.rice.cs.drjava.DrJavaTestCase;

import java.io.File;
import java.io.IOException;



public class TestDocGetterTest extends DrJavaTestCase {


    
    public void testGetDocumentForFile() throws IOException {
        TestDocGetter testDocGetter = new TestDocGetter(new File[0], new String[0]);
 try {
     testDocGetter.getDocumentForFile(new File(""));
 }
 catch (IllegalStateException e) {
     assertTrue(true);
     return;
 }
 fail("should throw IllegalStateException");
 
    }

}
