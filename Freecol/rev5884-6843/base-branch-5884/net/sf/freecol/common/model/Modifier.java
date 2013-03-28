

package net.sf.freecol.common.model;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.Specification;

import org.w3c.dom.Element;



public final class Modifier extends Feature {

    public static final String OFFENCE = "model.modifier.offence";
    public static final String DEFENCE = "model.modifier.defence";
    public static final String CAPITAL_DEFENCE = "model.modifier.capitalDefence";
    public static final String SETTLEMENT_DEFENCE = "model.modifier.settlementDefence";

    public static final float UNKNOWN = Float.MIN_VALUE;

    public static enum Type { ADDITIVE, MULTIPLICATIVE, PERCENTAGE }

    private float value;

    
    private float increment;
    
    
    private Type type;

    
    private Type incrementType;

    

    private Modifier() {
        
    }

    
    public Modifier(String id, float value, Type type) {
        setId(id);
        setType(type);
        setValue(value);
    }

    
    public Modifier(String id, FreeColGameObjectType source, float value, Type type) {
        setId(id);
        setSource(source);
        setType(type);
        setValue(value);
    }

    
    public Modifier(Modifier template) {
        setId(template.getId());
        setSource(template.getSource());
        setType(template.getType());
        setValue(template.getValue());
        if (template.hasIncrement()) {
            setIncrement(template.getIncrement(), template.getIncrementType(),
                         template.getFirstTurn(), template.getLastTurn());
        }
    }

    
    public Modifier(Element element) {
        readFromXMLElement(element);
    }
    
    
    public Modifier(XMLStreamReader in, Specification specification) throws XMLStreamException {
        readFromXMLImpl(in, specification);
    }
    
    
    public Type getType() {
        return type;
    }

    
    public void setType(final Type newType) {
        this.type = newType;
    }

    
    public Type getIncrementType() {
        return incrementType;
    }

    
    public void setIncrementType(final Type newIncrementType) {
        this.incrementType = newIncrementType;
    }

    
    public float getValue() {
        return value;
    }

    
    public void setValue(final float newValue) {
        value = newValue;
    }

    
    public float getIncrement() {
        return increment;
    }

    
    public void setIncrement(final float newIncrement, Type type, Turn firstTurn, Turn lastTurn) {
        if (firstTurn == null) {
            throw new IllegalArgumentException("Parameter firstTurn must not be 'null'.");
        } else {
            increment = newIncrement;
            incrementType = type;
            setFirstTurn(firstTurn);
            setLastTurn(lastTurn);
        }
    }

    
    public boolean hasIncrement() {
        return incrementType != null;
    }

    
    public float applyTo(float number) {
        switch(type) {
        case ADDITIVE:
            return number + value;
        case MULTIPLICATIVE:
            return number * value;
        case PERCENTAGE:
            return number + (number * value) / 100;
        default:
            return number;
        }
    }

    

    public static Modifier createTeaPartyModifier(Turn turn) {
        Modifier bellsBonus = new Modifier("model.goods.bells", Specification.COLONY_GOODS_PARTY,
                                           50, Type.PERCENTAGE);
        bellsBonus.setIncrement(-2, Type.ADDITIVE, turn, new Turn(turn.getNumber() + 25));
        return bellsBonus;
    }


    


        
    public void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
        writeAttributes(out);
        out.writeEndElement();
    }

    
    public static String getXMLElementTagName() {
        return "modifier";
    }

    public void readAttributes(XMLStreamReader in, Specification specification)
        throws XMLStreamException {
        super.readAttributes(in, specification);
        setType(Enum.valueOf(Type.class, in.getAttributeValue(null, "type").toUpperCase()));
        value = Float.parseFloat(in.getAttributeValue(null, "value"));
        String incrementString = in.getAttributeValue(null, "incrementType");
        if (incrementString != null) {
            setType(Enum.valueOf(Type.class, incrementString.toUpperCase()));
            increment = Float.parseFloat(in.getAttributeValue(null, "increment"));
        }
    }
    
    public void writeAttributes(XMLStreamWriter out) throws XMLStreamException {
	super.writeAttributes(out);
	out.writeAttribute("value", String.valueOf(value));
	out.writeAttribute("type", type.toString());
        if (incrementType != null) {
            out.writeAttribute("incrementType", incrementType.toString());
            out.writeAttribute("increment", String.valueOf(increment));
        }
    }
    
    public String toString() {
        return getId() + (getSource() == null ? " " : " (" + getSource().getId() + ") ") +
            type + " " + value;
    }
    
}
