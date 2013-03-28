

package net.sf.freecol.common.model;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.Specification;

import org.w3c.dom.Element;



public final class Ability extends Feature {

    public static final String ADD_TAX_TO_BELLS = "model.ability.addTaxToBells";

    private boolean value = true;

    
    public Ability(String id) {
        this(id, null, true);
    }

    
    public Ability(String id, boolean value) {
        this(id, null, value);
    }

    
    public Ability(String id, FreeColGameObjectType source, boolean value) {
        setId(id);
        setSource(source);
        this.value = value;
    }
    
    
    public Ability(Element element) {
        readFromXMLElement(element);
    }
    
    
    public Ability(XMLStreamReader in, Specification specification)
        throws XMLStreamException {
        readFromXMLImpl(in, specification);
    }
    
    
    public boolean getValue() {
        return value;
    }

    
    public void setValue(final boolean newValue) {
        this.value = newValue;
    }


    public int hashCode() {
        int hash = super.hashCode();
        hash += (value ? 1 : 0);
        return hash;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof Ability) {
            return super.equals(o) && (value == ((Ability) o).value);
        } else {
            return false;
        }
    }

    



        
    public void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
        writeAttributes(out);
        out.writeEndElement();
    }

    public void readAttributes(XMLStreamReader in, Specification specification)
        throws XMLStreamException {
        super.readAttributes(in, specification);
        value = getAttribute(in, "value", true);
    }
    
    public void writeAttributes(XMLStreamWriter out) throws XMLStreamException {
        super.writeAttributes(out);
        out.writeAttribute("value", String.valueOf(value));
    }

    
    public static String getXMLElementTagName() {
        return "ability";
    }

    public String toString() {
        return getId() + (getSource() == null ? " " : " (" + getSource().getId() + ") ") +
            " " + value;
    }

}
