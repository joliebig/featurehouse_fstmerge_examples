
package genj.fo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import junit.framework.TestCase;


public class FormatTest extends TestCase {

  public void testFormats() throws IOException, TransformerException {
    
    
    Document doc = new Document("Some Title");
    
    
    Format[] fs = Format.getFormats();
    
    for (int i=0;i<fs.length;i++) {
      Format f = fs[i];
      try {
        f.format(doc, new ByteArrayOutputStream(1024));
      } catch (Throwable t) {
        fail("Formatter "+f.getClass().getName()+" failed with "+t.getMessage());
      }
    }

    
  }
  
  
}
