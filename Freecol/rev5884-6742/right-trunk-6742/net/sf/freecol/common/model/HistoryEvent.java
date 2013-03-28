

package net.sf.freecol.common.model;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;


public class HistoryEvent extends StringTemplate {

    public static enum EventType {
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

    
    private EventType eventType;

    public HistoryEvent() {
        
    }

    public HistoryEvent(int turn, EventType eventType) {
        super("model.history." + eventType.toString(), TemplateType.TEMPLATE);
        this.turn = turn;
        this.eventType = eventType;
    }        

    
    public final int getTurn() {
        return turn;
    }

    
    public final void setTurn(final int newInt) {
        this.turn = newInt;
    }

    
    public final EventType getEventType() {
        return eventType;
    }

    
    public final void setEventType(final EventType newEventType) {
        this.eventType = newEventType;
    }

    
    public HistoryEvent add(String key, String value) {
        super.add(key, value);
        return this;
    }

    
    public HistoryEvent addName(String key, String value) {
        super.addName(key, value);
        return this;
    }

    
    public HistoryEvent addAmount(String key, int amount) {
        super.addAmount(key, amount);
        return this;
    }

    
    public HistoryEvent addStringTemplate(String key, StringTemplate template) {
        super.addStringTemplate(key, template);
	return this;
    }


    public String toString() {
        return eventType.toString() + " (" + Turn.getYear(turn) + ") ["
            + super.toString() + "]";
    }

    
    public void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
        writeAttributes(out);
        writeChildren(out);
        out.writeEndElement();
    }

    public void writeAttributes(XMLStreamWriter out) throws XMLStreamException {
        super.writeAttributes(out);
        out.writeAttribute("turn", Integer.toString(turn));
        out.writeAttribute("eventType", eventType.toString());
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        super.readAttributes(in);
        turn = Integer.parseInt(in.getAttributeValue(null, "turn"));
        String eventString = in.getAttributeValue(null, "eventType");
        
        if (eventString == null) {
            eventString = in.getAttributeValue(null, "type");
        }
        if ("".equals(getId())) {
            setId("model.history." + eventString);
        }
        
        eventType = Enum.valueOf(EventType.class, eventString);
        super.readChildren(in);
    }

    
    public static String getXMLElementTagName() {
        return "historyEvent";
    }

}