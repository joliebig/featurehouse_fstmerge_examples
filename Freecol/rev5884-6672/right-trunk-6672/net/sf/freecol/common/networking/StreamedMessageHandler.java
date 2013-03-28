


package net.sf.freecol.common.networking;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;




public interface StreamedMessageHandler {
    
    
    public void handle(Connection connection, XMLStreamReader in, XMLStreamWriter out);
    
    
    public boolean accepts(String tagName);

}
