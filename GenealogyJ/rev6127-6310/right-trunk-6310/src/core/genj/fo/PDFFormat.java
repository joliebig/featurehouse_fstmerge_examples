
package genj.fo;

import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;


public class PDFFormat extends Format {

  
  public PDFFormat() {
    super("PDF", "pdf", false);
  }
  
  
  protected void formatImpl(Document doc, OutputStream out) throws Throwable {

    
    org.xml.sax.ContentHandler handler = new org.apache.fop.fo.FOTreeBuilder("application/pdf", new org.apache.fop.apps.FOUserAgent(), out);

    
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    
    
    transformer.transform(doc.getDOMSource(), new SAXResult(handler));

    
  }
  
}
