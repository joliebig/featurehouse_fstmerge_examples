
package genj.fo;

import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;


public class CSVFormat extends Format {
  
  
  public CSVFormat() {
    super("CSV", "csv", true);
  }

  
  public boolean supports(Document doc) {
    return doc.containsCSV();
  }
  
  
  protected void formatImpl(Document doc, OutputStream out) throws Throwable {
    
    
    Transformer transformer = getTemplates("./contrib/xslt/fo2csv.xsl").newTransformer();
    
    
    transformer.transform(doc.getDOMSource(), new StreamResult(out));

    
  }
  
}