


package net.sf.freecol.common.option;

import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;



public class StringOption extends AbstractOption {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(StringOption.class.getName());

    private String value;

    
    public StringOption(XMLStreamReader in) throws XMLStreamException {
        super(NO_ID);
        readFromXML(in);
    }

    
    public String getValue() {
        return value;
    }
    
    
    
    public void setValue(String value) {
        final String oldValue = this.value;
        this.value = value;
        
        if (value != oldValue && isDefined) {
            firePropertyChange("value", oldValue, value);
        }
        isDefined = true;
    }


    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("id", getId());
        out.writeAttribute("value", value);

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        final String id = in.getAttributeValue(null, "id");
        final String defaultValue = in.getAttributeValue(null, "defaultValue");
        final String value = in.getAttributeValue(null, "value");

        if (id == null && getId().equals("NO_ID")){
            throw new XMLStreamException("invalid <" + getXMLElementTagName() + "> tag : no id attribute found.");
        }
        if (defaultValue == null && value == null) {
            throw new XMLStreamException("invalid <" + getXMLElementTagName() + "> tag : no value nor default value found.");
        }

        if(getId() == NO_ID) {
            setId(id);
        }
        if(value != null) {
            setValue(value);
        } else {
            setValue(defaultValue);
        }
        in.nextTag();

    }


    
    public static String getXMLElementTagName() {
        return "stringOption";
    }

}
