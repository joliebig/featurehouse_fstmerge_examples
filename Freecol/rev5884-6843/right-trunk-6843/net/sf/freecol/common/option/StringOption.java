


package net.sf.freecol.common.option;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;



public class StringOption extends AbstractOption {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(StringOption.class.getName());

    public static final String NONE = "none";

    public static enum Generate {
        UNITS, IMMIGRANTS, LAND_UNITS, NAVAL_UNITS, BUILDINGS, FOUNDING_FATHERS
    }

    private String value;

    
    private boolean addNone;

    
    private Generate generateChoices;

    
    private List<String> choices;

    
    public StringOption(XMLStreamReader in) throws XMLStreamException {
        super(NO_ID);
        readFromXMLImpl(in);
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

    
    public final boolean addNone() {
        return addNone;
    }

    
    public final void setAddNone(final boolean newAddNone) {
        this.addNone = newAddNone;
    }

    
    public final List<String> getChoices() {
        return choices;
    }

    
    public final void setChoices(final List<String> newChoices) {
        this.choices = newChoices;
    }

    
    public final Generate getGenerateChoices() {
        return generateChoices;
    }

    
    public final void setGenerateChoices(final Generate newGenerateChoices) {
        this.generateChoices = newGenerateChoices;
    }

    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("id", getId());
        out.writeAttribute("value", value);
        if (generateChoices != null) {
            out.writeAttribute("generate", generateChoices.toString());
        }
        if (addNone) {
            out.writeAttribute("addNone", Boolean.toString(addNone));
        }
        if (choices != null && !choices.isEmpty()) {
            for (String choice : choices) {
                out.writeStartElement("choice");
                out.writeAttribute("value", choice);
                out.writeEndElement();
            }
        }

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        final String id = in.getAttributeValue(null, "id");
        final String defaultValue = in.getAttributeValue(null, "defaultValue");
        final String value = in.getAttributeValue(null, "value");

        if (id == null && getId().equals("NO_ID")){
            throw new XMLStreamException("invalid <" + getXMLElementTagName()
                                         + "> tag : no id attribute found.");
        }
        if (defaultValue == null && value == null) {
            throw new XMLStreamException("invalid <" + getXMLElementTagName()
                                         + "> tag : no value nor default value found.");
        }

        if(getId() == NO_ID) {
            setId(id);
        }
        if(value != null) {
            setValue(value);
        } else {
            setValue(defaultValue);
        }

        addNone = getAttribute(in, "addNone", false);
        String generate = in.getAttributeValue(null, "generate");
        if (generate != null) {
            generateChoices = Enum.valueOf(StringOption.Generate.class, generate);
        }

        choices = new ArrayList<String>();
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if ("choice".equals(in.getLocalName())) {
                choices.add(in.getAttributeValue(null, "value"));
                in.nextTag();
            }
        }
    }


    
    public static String getXMLElementTagName() {
        return "stringOption";
    }

}
