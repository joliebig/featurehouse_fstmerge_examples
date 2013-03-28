


package net.sf.freecol.common.option;


import java.beans.PropertyChangeListener;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;



public interface Option {


    public static final String NO_ID = "NO_ID";

    

    
    public String getShortDescription();


    
    public String toString() ;


    
    public String getId();


    
    public String getName();

    
    public boolean isPreviewEnabled();
    
    
    public void addPropertyChangeListener(PropertyChangeListener pcl);
    
    
    public void removePropertyChangeListener(PropertyChangeListener pcl);
    
    
    public Element toXMLElement(Document document);


    
    public void readFromXMLElement(Element element);
    
    
    public void readFromXML(XMLStreamReader in) throws XMLStreamException;
    
    
    public void toXML(XMLStreamWriter out) throws XMLStreamException;     
}
