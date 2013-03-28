


package net.sf.freecol.common.option;

import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;



public class IntegerOption extends AbstractOption {
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(IntegerOption.class.getName());

    private int value;
    private int minimumValue;
    private int maximumValue;

    
    
    
    public IntegerOption(String id, OptionGroup optionGroup, int minimumValue, int maximumValue, int defaultValue) {
        super(id, optionGroup);

        this.value = defaultValue;
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    
     public IntegerOption(XMLStreamReader in) throws XMLStreamException {
         super(NO_ID);
         readFromXML(in);
     }

    
    
    public int getMinimumValue() {
        return minimumValue;
    }
    
    
    
    public int getMaximumValue() {
        return maximumValue;
    }


    
    public int getValue() {
        return value;
    }

    
    
    public void setValue(int value) {
        final int oldValue = this.value;
        this.value = value;
        
        if (value != oldValue && isDefined) {
            firePropertyChange("value", Integer.valueOf(oldValue), Integer.valueOf(value));
        }
        isDefined = true;
    }

    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("id", getId());
        out.writeAttribute("value", Integer.toString(value));

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
            minimumValue = Integer.parseInt(in.getAttributeValue(null, "minimumValue"));
            maximumValue = Integer.parseInt(in.getAttributeValue(null, "maximumValue"));
        }
        in.nextTag();
    }

    
    public static String getXMLElementTagName() {
        return "integerOption";
    }

}
