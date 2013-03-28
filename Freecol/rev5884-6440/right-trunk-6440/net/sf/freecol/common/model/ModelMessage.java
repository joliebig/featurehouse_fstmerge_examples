


package net.sf.freecol.common.model;

import java.util.Arrays;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.i18n.Messages;


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

    private Player owner;
    private FreeColGameObject source;
    private Location sourceLocation;
    private FreeColObject display;
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
        this.source = source;

        this.sourceLocation = null;
        if (source instanceof Unit) {
            Unit u = (Unit) source;
            this.owner = u.getOwner();
            if (u.getTile() != null) {
                this.sourceLocation = u.getTile();
            } else if (u.getColony() != null) {
                this.sourceLocation = u.getColony().getTile();
            } else if (u.getIndianSettlement() != null) {
                this.sourceLocation = u.getIndianSettlement().getTile();
            } else if (u.isInEurope()) {
                this.sourceLocation = u.getOwner().getEurope();
            }
        } else if (source instanceof Settlement) {
            this.owner = ((Settlement) source).getOwner();
            this.sourceLocation = ((Settlement) source).getTile();
        } else if (source instanceof Europe) {
            this.owner = ((Europe) source).getOwner();
        } else if (source instanceof Player) {
            this.owner = (Player) source;
        } else if (source instanceof Ownable) {
            this.owner = ((Ownable) source).getOwner();
        }
    }

    
    static private FreeColObject getDefaultDisplay(MessageType messageType, FreeColGameObject source) {
        FreeColObject newDisplay = null;
        switch(messageType) {
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


    
    public FreeColGameObject getSource() {
        return source;
    }

    
    public void setSource(FreeColGameObject newSource) {
        source = newSource;
    }
    
    
    public MessageType getMessageType() {
        return messageType;

    }


    public String getMessageTypeName() {
        return "model.message." + messageType.toString();
    }


    
    public FreeColObject getDisplay() {
        return display;
    }

    
    public void setDisplay(FreeColGameObject newDisplay) {
        display = newDisplay;
    }

    
    public Player getOwner() {
        return owner;
    }

    
    public void setOwner(Player newOwner) {
        newOwner = owner;
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
        if ( ! (o instanceof ModelMessage) ) { return false; }
        ModelMessage m = (ModelMessage) o;
        
        
        return ( source.equals(m.source)
                && getId().equals(m.getId())
                && messageType == m.messageType );
    }
    
    @Override
    public int hashCode() {
        int value = 1;
        value = 37 * value + ((source == null) ? 0 : source.hashCode());
        value = 37 * value + getId().hashCode();
        
        value = 37 * value + messageType.ordinal();
        return value;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ModelMessage<");
        sb.append(hashCode() + ", " + ((source == null) ? "null" : source.getId()) + ", " + getId() + ", "
                  + ((display==null) ? "null" : display.getId()) + ", ");
        
        sb.append(", " + messageType + " >");
        return sb.toString();
    }

    public static String getXMLElementTagName() {
        return "modelMessage";
    }

    public void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
        writeAttributes(out);
        writeChildren(out);
        out.writeEndElement();
    }

    public void writeAttributes(XMLStreamWriter out) throws XMLStreamException {
        super.writeAttributes(out);
        out.writeAttribute("owner", owner.getId());
        if (source != null) {
            if ((source instanceof Unit && ((Unit)source).isDisposed())
              ||(source instanceof Settlement && ((Settlement)source).isDisposed())) {
                if (sourceLocation==null) {
                    logger.warning("sourceLocation==null for source "+source.getId());
                    out.writeAttribute("source", owner.getId());
                } else {
                    out.writeAttribute("source", sourceLocation.getId());
                }
            } else {
                out.writeAttribute("source", source.getId());
            }
        }
        if (display != null) {
            out.writeAttribute("display", display.getId());
        }
        out.writeAttribute("messageType", messageType.toString());
        out.writeAttribute("hasBeenDisplayed", String.valueOf(beenDisplayed));
    }

    
    public void readFromXML(XMLStreamReader in, Game game) throws XMLStreamException {
        super.readAttributes(in);
        
        String ownerPlayer = in.getAttributeValue(null, "owner");
        owner = (Player)game.getFreeColGameObject(ownerPlayer);
         
        messageType = Enum.valueOf(MessageType.class, getAttribute(in, "messageType", MessageType.DEFAULT.toString()));
        beenDisplayed = Boolean.parseBoolean(in.getAttributeValue(null, "hasBeenDisplayed"));

        String sourceString = in.getAttributeValue(null, "source");
        source = game.getFreeColGameObject(sourceString);
        if (source == null) {
            logger.warning("source null from string " + sourceString);
            source = owner;
        }
        String displayString = in.getAttributeValue(null, "display");
        if (displayString != null) {
            
            display = game.getFreeColGameObject(displayString);
            if (display==null) {
                
                
                try {
                    display = FreeCol.getSpecification().getType(displayString);
                } catch (IllegalArgumentException e) {
                    
                    display = owner;
                    logger.warning("display null from string " + displayString);
                }
            }
        }

        super.readChildren(in);
        owner.addModelMessage(this);
    }

}
