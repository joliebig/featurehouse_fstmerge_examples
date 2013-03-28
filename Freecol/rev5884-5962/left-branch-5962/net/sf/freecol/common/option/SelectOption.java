

package net.sf.freecol.common.option;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.client.gui.i18n.Messages;


public class SelectOption extends AbstractOption {
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(SelectOption.class.getName());

    private int value;

    protected boolean localizedLabels = false;

    private Map<Integer, String> itemValues = new LinkedHashMap<Integer, String>();


    
    public SelectOption(XMLStreamReader in) throws XMLStreamException {
        super(NO_ID);
        readFromXML(in);
    }

    
    public int getValue() {
        return value;
    }

    
    public void setValue(int value) {
        final int oldValue = this.value;
        this.value = value;

        if (value != oldValue) {
            firePropertyChange("value", Integer.valueOf(oldValue), Integer.valueOf(value));
        }
        isDefined = true;
    }

    
    public Map<Integer, String> getItemValues() {
        return itemValues;
    }

    
    protected String getStringValue() {
        return Integer.toString(value);
    }

    
    protected void setValue(String value) {
        setValue(Integer.parseInt(value));
    }

    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("id", getId());
        out.writeAttribute("value", getStringValue());

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        final String id = in.getAttributeValue(null, "id");
        final String defaultValue = in.getAttributeValue(null, "defaultValue");
        final String localizedLabels = in.getAttributeValue(null, "localizedLabels");
        final String value = in.getAttributeValue(null, "value");

        if (localizedLabels != null) {
            this.localizedLabels = localizedLabels.equals("true");
        }

        if (id == null && getId().equals("NO_ID")) {
            throw new XMLStreamException("invalid <" + getXMLElementTagName() + "> tag : no id attribute found.");
        }
        if (defaultValue == null && value == null) {
            throw new XMLStreamException("invalid <" + getXMLElementTagName()
                    + "> tag : no value nor default value found.");
        }

        if (getId() == NO_ID) {
            setId(id);
        }
        if (value != null) {
            setValue(Integer.parseInt(value));
            in.nextTag();
        } else {
            setValue(Integer.parseInt(defaultValue));
            while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
                if (in.getLocalName() == getXMLItemElementTagName()) {
                    String label = in.getAttributeValue(null, "label");
                    final String itemValue = in.getAttributeValue(null, "value");
                    if (this.localizedLabels) {
                        label = Messages.message(label);
                    }
                    itemValues.put(Integer.parseInt(itemValue), label);
                } else {
                    throw new XMLStreamException("Unknown child \"" + in.getLocalName() + "\" in a \""
                            + getXMLElementTagName() + "\". ");
                }
                in.nextTag();
            }
        }
    }

    
    public static String getXMLElementTagName() {
        return "selectOption";
    }
    
    
    public String getXMLItemElementTagName() {
        return "selectValue";
    }

}
