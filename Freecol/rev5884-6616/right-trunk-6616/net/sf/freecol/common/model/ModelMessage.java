


package net.sf.freecol.common.model;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;


public class ModelMessage extends StringTemplate {

    
    public static enum MessageType { 
        DEFAULT,
        WARNING,
        SONS_OF_LIBERTY,
        GOVERNMENT_EFFICIENCY,
        WAREHOUSE_CAPACITY,
        UNIT_IMPROVED,
        UNIT_DEMOTED,
        UNIT_LOST,
        UNIT_ADDED,
        BUILDING_COMPLETED,
        FOREIGN_DIPLOMACY,
        MARKET_PRICES,
        LOST_CITY_RUMOUR,
        GIFT_GOODS,
        MISSING_GOODS,
        TUTORIAL,
        COMBAT_RESULT,
        ACCEPTED_DEMANDS,
        REJECTED_DEMANDS
    }

    private String ownerId; 
    private String sourceId;
    private String displayId;
    private MessageType messageType;
    private boolean beenDisplayed = false;


    public ModelMessage() {
        
    }

    
    public ModelMessage(String id, FreeColGameObject source, FreeColObject display) {
        this(MessageType.DEFAULT, id, source, display);
    }

    
    public ModelMessage(MessageType messageType, String id, FreeColGameObject source) {
        this(messageType, id, source, getDefaultDisplay(messageType, source));
    }

    
    public ModelMessage(String id, FreeColGameObject source) {
        this(MessageType.DEFAULT, id, source, getDefaultDisplay(MessageType.DEFAULT, source));
    }

    
    public ModelMessage(MessageType messageType, String id, FreeColGameObject source, FreeColObject display) {
        super(id, TemplateType.TEMPLATE);
        this.messageType = messageType;
        this.sourceId = source.getId();
        this.displayId = (display != null) ? display.getId() : source.getId();
        this.ownerId = null;
    }

    
    @Override
    public final ModelMessage setDefaultId(final String newDefaultId) {
        super.setDefaultId(newDefaultId);
        return this;
    }

    
    static private FreeColObject getDefaultDisplay(MessageType messageType,
                                                   FreeColGameObject source) {
        FreeColObject newDisplay = null;
        switch (messageType) {
        case SONS_OF_LIBERTY:
        case GOVERNMENT_EFFICIENCY:
            newDisplay = FreeCol.getSpecification().getGoodsType("model.goods.bells");
            break;
        case LOST_CITY_RUMOUR:
        case UNIT_IMPROVED:
        case UNIT_DEMOTED:
        case UNIT_LOST:
        case UNIT_ADDED:
        case COMBAT_RESULT:
            newDisplay = source;
            break;
        case BUILDING_COMPLETED:
            newDisplay = FreeCol.getSpecification().getGoodsType("model.goods.hammers");
            break;
        case TUTORIAL:
        case DEFAULT:
        case WARNING:
        case WAREHOUSE_CAPACITY:
        case FOREIGN_DIPLOMACY:
        case MARKET_PRICES:
        case GIFT_GOODS:
        case MISSING_GOODS:
        default:
            if (source instanceof Player) {
                newDisplay = source;
            }
        }
        return newDisplay;
    }


    
    public boolean hasBeenDisplayed() {
        return beenDisplayed;
    }

    
    public void setBeenDisplayed(boolean beenDisplayed) {
        this.beenDisplayed = beenDisplayed;
    }

    
    public String getSourceId() {
        return sourceId;
    }

    
    public void setSourceId(String source) {
        this.sourceId = source;
    }
    
    
    public String getDisplayId() {
        return displayId;
    }

    
    public void setDisplayId(String displayId) {
        this.displayId = displayId;
    }

    
    public MessageType getMessageType() {
        return messageType;
    }

    
    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getMessageTypeName() {
        return "model.message." + messageType.toString();
    }

    
    public void divert(FreeColGameObject newSource) {
        if (displayId == sourceId) displayId = newSource.getId();
        sourceId = newSource.getId();
    }

    
    public String getOwnerId() {
        return ownerId;
    }

    
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    
    public ModelMessage add(String key, String value) {
        super.add(key, value);
        return this;
    }

    
    public ModelMessage add(String value) {
        super.add(value);
        return this;
    }

    
    public ModelMessage addName(String key, String value) {
        super.addName(key, value);
        return this;
    }

    
    public ModelMessage addName(String value) {
        super.addName(value);
        return this;
    }

    
    public ModelMessage addAmount(String key, int amount) {
        super.addAmount(key, amount);
        return this;
    }

    
    public ModelMessage addStringTemplate(String key, StringTemplate template) {
        super.addStringTemplate(key, template);
        return this;
    }

    
    public ModelMessage addStringTemplate(StringTemplate template) {
        super.addStringTemplate(template);
        return this;
    }


    
    @Override
    public boolean equals(Object o) {
        if (o instanceof ModelMessage) {
            ModelMessage m = (ModelMessage) o;
            if (sourceId.equals(m.sourceId)
                && getId().equals(m.getId())
                && messageType == m.messageType) {
                return super.equals(m);
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int value = 1;
        value = 37 * value + sourceId.hashCode();
        value = 37 * value + getId().hashCode();
        value = 37 * value + messageType.ordinal();
        value = 37 * value + super.hashCode();
        return value;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ModelMessage<" + hashCode() + ", ");
        sb.append(((sourceId == null) ? "null" : sourceId) + ", ");
        sb.append(((displayId == null) ? "null" : displayId) + ", ");
        sb.append(super.toString());
        sb.append(", " + messageType + " >");
        return sb.toString();
    }

    public void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
        writeAttributes(out);
        writeChildren(out);
        out.writeEndElement();
    }

    public void writeAttributes(XMLStreamWriter out) throws XMLStreamException {
        super.writeAttributes(out);
        if (ownerId != null) {
            out.writeAttribute("owner", ownerId);
        }
        out.writeAttribute("source", sourceId);
        if (displayId != null) {
            out.writeAttribute("display", displayId);
        }
        out.writeAttribute("messageType", messageType.toString());
        out.writeAttribute("hasBeenDisplayed", String.valueOf(beenDisplayed));
    }

    
    public void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        super.readAttributes(in);

        
        ownerId = in.getAttributeValue(null, "owner");
         
        messageType = Enum.valueOf(MessageType.class, getAttribute(in, "messageType", MessageType.DEFAULT.toString()));
        beenDisplayed = Boolean.parseBoolean(in.getAttributeValue(null, "hasBeenDisplayed"));
        sourceId = in.getAttributeValue(null, "source");
        displayId = in.getAttributeValue(null, "display");

        super.readChildren(in);
    }

    
    public static String getXMLElementTagName() {
        return "modelMessage";
    }
}
