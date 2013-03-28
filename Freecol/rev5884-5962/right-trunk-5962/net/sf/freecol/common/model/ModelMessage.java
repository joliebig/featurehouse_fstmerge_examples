


package net.sf.freecol.common.model;

import java.util.Arrays;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.i18n.Messages;


public class ModelMessage extends FreeColObject {

    
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
    private MessageType type;
    private String[] data;
    private boolean beenDisplayed = false;


    public ModelMessage() {
    }


    private static String[] convertData(String[][] data) {
        if (data == null) {
            return null;
        }
        String[] strings = new String[data.length * 2];
        for (int index = 0; index < data.length; index++) {
            strings[index * 2] = data[index][0];
            strings[index * 2 + 1] = data[index][1];
        }
        return strings;
    }

    
    @Deprecated
    public ModelMessage(FreeColGameObject source, String id, String[][] data, MessageType type, FreeColObject display) {
        this(source, type, display, id, convertData(data));
    }

    
    public ModelMessage(FreeColGameObject source, MessageType type, FreeColObject display,
                        String id, String... data) {
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

        setId(id);
        this.data = data;
        this.type = type;
        if (display == null) {
            this.display = getDefaultDisplay(type, source);
        } else {
            this.display = display;
        }
        verifyFields();
    }

    
    private void verifyFields() {
        if (getId() == null) {
            throw new IllegalArgumentException("ModelMessage should not have a null id.");
        }
        if (source == null) {
            throw new IllegalArgumentException("ModelMessage with ID " + this.toString() + " should not have a null source.");
        }
        if (owner == null) {
            throw new IllegalArgumentException("ModelMessage with ID " + this.getId() + " should not have a null owner.");
        }
        if (!(display == null ||
              display instanceof FreeColGameObject ||
              display instanceof FreeColGameObjectType)) {
            throw new IllegalArgumentException("The display must be a FreeColGameObject or FreeColGameObjectType!");
        }

        if (data != null && data.length % 2 != 0) {
            throw new IllegalArgumentException("Data length must be multiple of 2.");
        }
    }
    
    
     public ModelMessage(FreeColGameObject source, String id, String[][] data, MessageType type) {
         this(source, type, getDefaultDisplay(type, source), id, convertData(data));

     }

     
     public ModelMessage(FreeColGameObject source, String id, String[][] data) {
         this(source, MessageType.DEFAULT, getDefaultDisplay(MessageType.DEFAULT, source), id, convertData(data));
     }
     
    
    static private FreeColObject getDefaultDisplay(MessageType type, FreeColGameObject source) {
        FreeColObject newDisplay = null;
        switch(type) {
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
    
    
    public String[] getData() {
        return data;
    }

    
    public MessageType getType() {
        return type;

    }


    public String getTypeName() {
        return Messages.message("model.message." + type.toString());
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


    
    @Override
    public boolean equals(Object o) {
        if ( ! (o instanceof ModelMessage) ) { return false; }
        ModelMessage m = (ModelMessage) o;
        
        if (!Arrays.equals(data, m.data)) {
            return false;
        }
        return ( source.equals(m.source)
                && getId().equals(m.getId())
                && type == m.type );
    }
    
    @Override
    public int hashCode() {
        int value = 1;
        value = 37 * value + ((source == null) ? 0 : source.hashCode());
        value = 37 * value + getId().hashCode();
        if (data != null) {
            for (String s : data) {
                value = 37 * value + s.hashCode();
            }
        }
        value = 37 * value + type.ordinal();
        return value;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ModelMessage<");
        sb.append(hashCode() + ", " + ((source == null) ? "null" : source.getId()) + ", " + getId() + ", " + ((display==null) ? "null" : display.getId()) + ", ");
        if (data != null) {
            for (String s : data) {
                sb.append(s + "/");
            }
        }
        sb.append(", " + type + " >");
        return sb.toString();
    }

    
    public static String getXMLElementTagName() {
        return "modelMessage";
    }

    public void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
        out.writeAttribute("owner", owner.getId());
        if (source!=null) {
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
        out.writeAttribute("type", type.toString());
        out.writeAttribute("ID", getId());
        out.writeAttribute("hasBeenDisplayed", String.valueOf(beenDisplayed));
        if (data != null) {
            toArrayElement("data", data, out);
        }
        out.writeEndElement();
    }

    
    public void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setId(in.getAttributeValue(null, "ID"));
        type = Enum.valueOf(MessageType.class, getAttribute(in, "type", MessageType.DEFAULT.toString()));
        beenDisplayed = Boolean.parseBoolean(in.getAttributeValue(null, "hasBeenDisplayed"));

        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if (in.getLocalName().equals("data")) {
                data = readFromArrayElement("data", in, new String[0]);
            }
        }
    }

    
    public void readFromXML(XMLStreamReader in, Game game) throws XMLStreamException {
        setId(in.getAttributeValue(null, "ID"));
        
        String ownerPlayer = in.getAttributeValue(null, "owner");
        owner = (Player)game.getFreeColGameObject(ownerPlayer);
         
        type = Enum.valueOf(MessageType.class, getAttribute(in, "type", MessageType.DEFAULT.toString()));
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

        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if (in.getLocalName().equals("data")) {
                data =  readFromArrayElement("data", in, new String[0]);
            }
        }

        verifyFields();
        owner.addModelMessage(this);
    }

}
