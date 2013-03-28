
package genj.gedcom;

import genj.util.Base64;
import junit.framework.TestCase;


public class PropertyBlobTest extends TestCase {
  
  private PropertyBlob blob = new PropertyBlob();

  
  public void testBlob() {     

    String data = Base64.encode("this is a blob".getBytes());;
    blob.setValue(data);
    
    
    assertEquals( "blob data got lost", data, Base64.encode(blob.getBlobData()));
    
    
    assertTrue( "blob data leaking", !data.equals(blob.getValue()));

    
    MultiLineProperty.Iterator lines = blob.getLineIterator();
    lines.setValue("this is an attempt to overwrite the blob data in a PropertyBlob.iterator");

    
    assertEquals( "blob data iterator got overwritten", data, lines.getValue());

    
  }
  
  
} 
