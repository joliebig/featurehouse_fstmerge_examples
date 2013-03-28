

package genj.fo;

import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;


public class XSLFOFormat extends Format {
  
  
  public XSLFOFormat() {
    super("XSL-FO", "xml", true);
  }
  
  
  protected void formatImpl(Document doc, OutputStream out) throws Throwable {
    
    
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    
    
    transformer.transform(doc.getDOMSource(), new StreamResult(out));
    
    
  }
  
}