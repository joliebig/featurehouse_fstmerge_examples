


package net.sf.freecol.server.ai.mission;

import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Ownable;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.server.ai.AIColony;
import net.sf.freecol.server.ai.AIMain;
import net.sf.freecol.server.ai.AIObject;
import net.sf.freecol.server.ai.AIUnit;
import net.sf.freecol.server.ai.GoodsWish;
import net.sf.freecol.server.ai.Wish;
import net.sf.freecol.server.ai.WorkerWish;

import org.w3c.dom.Element;



public class WishRealizationMission extends Mission {
    private static final Logger logger = Logger.getLogger(WishRealizationMission.class.getName());


    private Wish wish;


    
    public WishRealizationMission(AIMain aiMain, AIUnit aiUnit, Wish wish) {
        super(aiMain, aiUnit);
        this.wish = wish;
        
        if (wish == null) {
            throw new NullPointerException("wish == null");
        }
    }


    
    public WishRealizationMission(AIMain aiMain, Element element) {
        super(aiMain);
        readFromXMLElement(element);
    }

    
    public WishRealizationMission(AIMain aiMain, XMLStreamReader in) throws XMLStreamException {
        super(aiMain);
        readFromXML(in);
    }

    
    public void dispose() {
        if (wish != null) {
            wish.setTransportable(null);
            wish = null;
        }
        super.dispose();
    }


    
    public void doMission(Connection connection) {
        Unit unit = getUnit();

        if (!isValid()) {
            return;
        }

        
        if (getUnit().getTile() != null) {
            if (wish.getDestination().getTile() != getUnit().getTile()) {
                Direction r = moveTowards(connection, wish.getDestination().getTile());
                moveButDontAttack(connection, r);
            }
            if (wish.getDestination().getTile() == getUnit().getTile()) {
                if (wish.getDestination() instanceof Colony) {
                    Colony colony = (Colony) wish.getDestination();

                    Element workElement = Message.createNewRootElement("work");
                    workElement.setAttribute("unit", unit.getId());
                    workElement.setAttribute("workLocation", colony.getVacantWorkLocationFor(getUnit()).getId());
                    try {
                        connection.sendAndWait(workElement);
                    } catch (IOException e) {
                        logger.warning("Could not send \"work\"-message.");
                    }
                    
                    getAIUnit().setMission(new WorkInsideColonyMission(getAIMain(), getAIUnit(), (AIColony) getAIMain().getAIObject(colony)));
                } else {
                    logger.warning("Unknown type of destination for: " + wish);
                }
            }
        }
    }



        
    public Tile getTransportDestination() {
        if (getUnit().isOnCarrier()) {
            return wish.getDestination().getTile();
        } else if (getUnit().getTile() == wish.getDestination().getTile()) {
            return null;
        } else if (getUnit().getTile() == null || getUnit().findPath(wish.getDestination().getTile()) == null) {
            return wish.getDestination().getTile();
        } else {
            return null;
        }
    }


    
    public int getTransportPriority() {
        if (getUnit().isOnCarrier()) {
            return NORMAL_TRANSPORT_PRIORITY;
        } else if (getUnit().getLocation().getTile() == wish.getDestination().getTile()) {
            return 0;
        } else if (getUnit().getTile() == null || getUnit().findPath(wish.getDestination().getTile()) == null) {
            return NORMAL_TRANSPORT_PRIORITY;
        } else {
            return 0;
        }
    }


    
    public boolean isValid() {
        Location l = wish.getDestination();
        if (l == null) {
            return false;
        }
        if (((FreeColGameObject) l).isDisposed()) {
            return false;
        }
        if (l instanceof Ownable && ((Ownable) l).getOwner() != getUnit().getOwner()) {
            return false;
        }
        if (l instanceof Colony) {
            Colony colony = (Colony) l;
            if (colony.getVacantWorkLocationFor(getUnit()) == null) {
                return false;
            }
        }
        return true;
    }

    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        if (wish.shouldBeStored()) {
            out.writeStartElement(getXMLElementTagName());

            out.writeAttribute("unit", getUnit().getId());
            out.writeAttribute("wish", wish.getId());

            out.writeEndElement();
        }
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setAIUnit((AIUnit) getAIMain().getAIObject(in.getAttributeValue(null, "unit")));
        wish = (Wish) getAIMain().getAIObject(in.getAttributeValue(null, "wish"));
        if (wish == null) {
            final String wid = in.getAttributeValue(null, "wish");
            if (wid.startsWith(GoodsWish.getXMLElementTagName())) {
                wish = new GoodsWish(getAIMain(), wid);
            } else if (wid.startsWith(WorkerWish.getXMLElementTagName())) {
                wish = new WorkerWish(getAIMain(), wid);
            } else {
                logger.warning("Unknown type of Wish.");
            }
        }
        in.nextTag();
    }


    
    public static String getXMLElementTagName() {
        return "wishRealizationMission";
    }
    
    
    public String getDebuggingInfo() {
        if (wish == null) {
            return "No wish";
        } else {
            return wish.getDestination().getTile().getPosition() + " " + wish.getValue();
        }
    }

}
