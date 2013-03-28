

package net.sf.freecol.common.model;

import net.sf.freecol.client.gui.i18n.Messages;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;


public class HistoryEvent extends FreeColObject {

    public static enum Type {
        DISCOVER_NEW_WORLD,
        DISCOVER_REGION,
        MEET_NATION,
        CITY_OF_GOLD,
        FOUND_COLONY,
        ABANDON_COLONY,
        CONQUER_COLONY,
        COLONY_DESTROYED,
        COLONY_CONQUERED,
        DESTROY_SETTLEMENT,
        
        DESTROY_NATION,
        NATION_DESTROYED,
        FOUNDING_FATHER,
        DECLARE_INDEPENDENCE,
        INDEPENDENCE,
        SPANISH_SUCCESSION
    }
            

    
    private int turn;

    
    private Type type;

    
    private String[] strings = new String[0];

    public HistoryEvent() {
        
        setId("");
    }

    public HistoryEvent(int turn, Type type, String... strings) {
        setId("");
        this.turn = turn;
        this.type = type;
        this.strings = strings;
    }        

    
    public final int getTurn() {
        return turn;
    }

    
    public final void setTurn(final int newInt) {
        this.turn = newInt;
    }

    
    public final Type getType() {
        return type;
    }

    
    public final void setType(final Type newType) {
        this.type = newType;
    }

    
    public final String[] getStrings() {
        return strings;
    }

    
    public final void setStrings(final String[] newStrings) {
        this.strings = newStrings;
    }

    public String toString() {
        return Messages.message("model.history." + type.toString(), strings);
    }

    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute(ID_ATTRIBUTE_TAG, getId());
        out.writeAttribute("turn", Integer.toString(turn));
        out.writeAttribute("type", type.toString());

        toArrayElement("strings", strings, out);

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setId(in.getAttributeValue(null, ID_ATTRIBUTE_TAG));
        turn = Integer.parseInt(in.getAttributeValue(null, "turn"));
        type = Enum.valueOf(Type.class, in.getAttributeValue(null, "type"));

        strings = readFromArrayElement("strings", in, new String[0]);

        in.nextTag();
    }

    
    public static String getXMLElementTagName() {
        return "historyEvent";
    }

}