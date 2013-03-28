
package genj.fo;

import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;


public class ScreenFormat extends Format {

  
  public ScreenFormat() {
    super("Screen", null, false);
  }

  
  protected void formatImpl(Document doc, OutputStream out) throws Throwable {

    
    org.xml.sax.ContentHandler handler = new org.apache.fop.fo.FOTreeBuilder("application/X-fop-awt-preview", new org.apache.fop.apps.FOUserAgent(), out);

    
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    
    
    transformer.transform(doc.getDOMSource(), new SAXResult(handler));

    
  }
  
}
