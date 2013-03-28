
package genj.fo;

import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;


public class HTMLFormat extends Format {
  
  
  public HTMLFormat() {
    super("HTML", "html", true);
  }

  
  protected void formatImpl(Document doc, OutputStream out) throws Throwable {
    
    
    Transformer transformer = getTemplates("./contrib/xslt/fo2html.xsl").newTransformer();
    
    
    transformer.transform(doc.getDOMSource(), new StreamResult(out));

    
  }
  
}