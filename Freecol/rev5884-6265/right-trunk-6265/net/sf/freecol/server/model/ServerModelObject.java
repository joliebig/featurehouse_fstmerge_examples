

package net.sf.freecol.server.model;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;


public interface ServerModelObject  {

    public void toServerAdditionElement(XMLStreamWriter out) throws XMLStreamException;
    public void readFromServerAdditionElement(XMLStreamReader in) throws XMLStreamException;
} 
