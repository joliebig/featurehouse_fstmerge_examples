
package genj.fo;

import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;


public class TXTFormat extends Format {

  
  public TXTFormat() {
    super("Text", "txt", false);
  }
  
  
  protected void formatImpl(Document doc, OutputStream out) throws Throwable {

    




    
    Transformer transformer = getTemplates("./contrib/xslt/fo2txt.xsl").newTransformer();
    
    
    transformer.transform(doc.getDOMSource(), new StreamResult(out));

    
  }
  
}
