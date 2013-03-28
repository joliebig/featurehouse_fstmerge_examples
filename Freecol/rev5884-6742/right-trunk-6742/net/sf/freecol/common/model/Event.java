

package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;



public class Event extends FreeColGameObjectType {

    
    private String value;

    
    private int scoreValue = 0;

    
    private List<Limit> limits;

    
    public final String getValue() {
        return value;
    }

    
    public final void setValue(final String newValue) {
        this.value = newValue;
    }

    
    public final List<Limit> getLimits() {
        return limits;
    }

    
    public final void setLimits(final List<Limit> newLimits) {
        this.limits = newLimits;
    }

    
    public final int getScoreValue() {
        return scoreValue;
    }

    
    public final void setScoreValue(final int newScoreValue) {
        this.scoreValue = newScoreValue;
    }

    public void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
        writeAttributes(out);
        writeChildren(out);
        out.writeEndElement();
    }

    public static String getXMLElementTagName() {
        return "event";
    }

    public void writeAttributes(XMLStreamWriter out) throws XMLStreamException {
        out.writeAttribute(ID_ATTRIBUTE_TAG, getId());
        if (value != null) {
            out.writeAttribute("value", value);
        }
        if (scoreValue != 0) {
            out.writeAttribute("scoreValue", Integer.toString(scoreValue));
        }
    }


    public void writeChildren(XMLStreamWriter out) throws XMLStreamException {
        if (limits != null) {
            for (Limit limit : limits) {
                limit.toXMLImpl(out);
            }
        }
    }

    public void readAttributes(XMLStreamReader in, Specification specification)
        throws XMLStreamException {
        setId(in.getAttributeValue(null, ID_ATTRIBUTE_TAG));
        value = in.getAttributeValue(null, "value");
        scoreValue = getAttribute(in, "scoreValue", 0);
    }

    @Override
    public FreeColObject readChild(XMLStreamReader in, Specification specification)
        throws XMLStreamException {
        if (Limit.getXMLElementTagName().equals(in.getLocalName())) {
            if (limits == null) {
                limits = new ArrayList<Limit>();
            }
            Limit limit = new Limit();
            limit.readFromXML(in, specification);
            limits.add(limit);
            return limit;
        } else {
            return super.readChild(in, specification);
        }
    }



}