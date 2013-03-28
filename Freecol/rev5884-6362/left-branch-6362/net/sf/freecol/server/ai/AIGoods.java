


package net.sf.freecol.server.ai;

import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Locatable;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.server.ai.mission.TransportMission;

import org.w3c.dom.Element;



public class AIGoods extends AIObject implements Transportable {
    private static final Logger logger = Logger.getLogger(AIGoods.class.getName());


    public static final int IMPORTANT_DELIVERY = 110;
    public static final int FULL_DELIVERY = 100;
    
    
    public static final int TOOLS_FOR_COLONY_PRIORITY = 10;
    
    
    public static final int TOOLS_FOR_IMPROVEMENT = 10;

    
    public static final int TOOLS_FOR_PIONEER = 90;

    
    public static final int TOOLS_FOR_BUILDING = 100;
    
    private Goods goods;
    private Location destination;
    private int transportPriority;
    private AIUnit transport = null;


    
    public AIGoods(AIMain aiMain, Location location, GoodsType type, int amount, Location destination) {
        super(aiMain, getXMLElementTagName() + ":" + aiMain.getNextID());

        goods = new Goods(aiMain.getGame(), location, type, amount);
        this.destination = destination;
    }


        
    public AIGoods(AIMain aiMain, Element element) {
        super(aiMain, element.getAttribute("ID"));
        readFromXMLElement(element);
    }
    
        
    public AIGoods(AIMain aiMain, XMLStreamReader in) throws XMLStreamException {
        super(aiMain, in.getAttributeValue(null, "ID"));
        readFromXML(in);
    }
    
        
    public AIGoods(AIMain aiMain, String id) {
        super(aiMain, id);
        uninitialized = true;
    }

    
    public void abortWish(Wish w) {
        if (destination == w.getDestination()) {
            destination = null;
        }
        if (w.getTransportable() == this) {
            w.dispose();
        }
    }

    
    public Location getTransportSource() {
        return goods.getLocation();
    }


    
    public Location getTransportDestination() {
        return destination;
    }
    

    
    public Locatable getTransportLocatable() {
        return getGoods();
    }


    
    public int getTransportPriority() {
        if (goods.getAmount() <= 100) {
            return goods.getAmount();
        } else {
            return transportPriority;
        }
    }

    
        
    public void increaseTransportPriority() {
        transportPriority++;
    }

    
    
    public AIUnit getTransport() {
        return transport;
    }

    
    public void dispose() {
        setTransport(null);
        if (destination != null) {
            if (destination instanceof Colony) {
                ((AIColony) getAIMain().getAIObject((Colony) destination)).removeAIGoods(this);
            } else if (destination instanceof Europe) {
                
            } else {
                logger.warning("Unknown type of destination: " + destination);
            }
        }
        super.dispose();
    }
    
    
    public void setTransport(AIUnit transport) {
        AIUnit oldTransport = this.transport;
        this.transport = transport;
        
        if (oldTransport != null) {
            
            if (oldTransport.getMission() != null
                    && oldTransport.getMission() instanceof TransportMission) {
                TransportMission tm = (TransportMission) oldTransport.getMission();
                if (tm.isOnTransportList(this)) {
                    tm.removeFromTransportList(this);
                }
            }
        }
            
        if (transport != null
                && transport.getMission() instanceof TransportMission
                && !((TransportMission) transport.getMission()).isOnTransportList(this)) {
            
            ((TransportMission) transport.getMission()).addToTransportList(this);
        }
    }
    

    
    public void setTransportPriority(int transportPriority) {
        this.transportPriority = transportPriority;
    }

    
    
    public Goods getGoods() {
        return goods;
    }


        
    public void setGoods(Goods goods) {        
        if (goods == null) {
            throw new NullPointerException();
        }
        this.goods = goods;
    }
    
    
    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("ID", getId());
        if (destination != null) {
            out.writeAttribute("destination", destination.getId());
        }
        out.writeAttribute("transportPriority", Integer.toString(transportPriority));
        if (transport != null) {
            if (getAIMain().getAIObject(transport.getId()) == null) {
                logger.warning("broken reference to transport");
            } else if (transport.getMission() != null
                    && transport.getMission() instanceof TransportMission
                    && !((TransportMission) transport.getMission()).isOnTransportList(this)) {
                logger.warning("We should not be on the transport list.");
            } else {
                out.writeAttribute("transport", transport.getId());
            }
        }
        goods.toXML(out, null);

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setId(in.getAttributeValue(null, "ID"));
        final String destinationStr = in.getAttributeValue(null, "destination");
        if (destinationStr != null) {
            destination = (Location) getAIMain().getFreeColGameObject(destinationStr);
            if (destination == null) {
                logger.warning("Could not find destination: " + destinationStr);
            }
        } else {
            destination = null;
        }
        transportPriority = Integer.parseInt(in.getAttributeValue(null, "transportPriority"));

        final String transportStr = in.getAttributeValue(null, "transport");
        if (transportStr != null) {
            transport = (AIUnit) getAIMain().getAIObject(transportStr);
            if (transport == null) {
                transport = new AIUnit(getAIMain(), transportStr);
            }
        } else {
            transport = null;
        }
        
        in.nextTag();

        if (goods != null) {
            goods.readFromXML(in);
        } else {
            goods = new Goods(getAIMain().getGame(), in);
        }
        in.nextTag();
    }

    
    
    public String toString() {
        return "AIGoods@" + hashCode() + ": " + goods + " (" + transportPriority + ")";
    }
    

    
    public static String getXMLElementTagName() {
        return "aiGoods";
    }
}
