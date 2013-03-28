

package net.sf.freecol.common.option;

import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;


public class PercentageOption extends IntegerOption {
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(PercentageOption.class.getName());

    
    public PercentageOption(XMLStreamReader in) throws XMLStreamException {
        super(in);
    }

    
    public PercentageOption(String id, OptionGroup optionGroup, int defaultOption) {
        super(id, optionGroup, 0, 100, defaultOption);
    }


    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("id", getId());
        out.writeAttribute("value", Integer.toString(getValue()));

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
            setValue(Integer.parseInt(value));
        } else {
            setValue(Integer.parseInt(defaultValue));
        }
        in.nextTag();
    }

    
    public static String getXMLElementTagName() {
        return "percentageOption";
    }
}
