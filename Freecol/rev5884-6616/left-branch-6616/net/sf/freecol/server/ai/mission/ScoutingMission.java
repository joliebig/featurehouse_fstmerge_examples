

package net.sf.freecol.server.ai.mission;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.AbstractGoods;
import net.sf.freecol.common.model.EquipmentType;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Map.Position;
import net.sf.freecol.common.model.Unit.MoveType;
import net.sf.freecol.common.model.pathfinding.GoalDecider;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.server.ai.AIColony;
import net.sf.freecol.server.ai.AIMain;
import net.sf.freecol.server.ai.AIObject;
import net.sf.freecol.server.ai.AIUnit;

import org.w3c.dom.Element;


public class ScoutingMission extends Mission {

    private static final Logger logger = Logger.getLogger(ScoutingMission.class.getName());

    private boolean valid = true;

    private EquipmentType scoutEquipment;

    private Tile transportDestination = null;

    
    private String debugAction = "";


    
    public ScoutingMission(AIMain aiMain, AIUnit aiUnit) {
        super(aiMain, aiUnit);
    }

    
    public ScoutingMission(AIMain aiMain, Element element) {
        super(aiMain);
        readFromXMLElement(element);
    }

    
    public ScoutingMission(AIMain aiMain, XMLStreamReader in) throws XMLStreamException {
        super(aiMain);
        readFromXML(in);
    }

    
    public void dispose() {
        super.dispose();
    }

    
    public void doMission(Connection connection) {
        Map map = getUnit().getGame().getMap();

        if (getUnit().getTile() == null) {
            return;
        }

        if (!isValid()) {
            return;
        }

        if (getUnit().getRole() != Unit.Role.SCOUT) {
            if (getUnit().getColony() != null) {
                AIColony colony = (AIColony) getAIMain().getAIObject(getUnit().getColony());
                for (EquipmentType equipment : FreeCol.getSpecification().getEquipmentTypeList()) {
                    if (equipment.getRole() == Unit.Role.SCOUT &&
                        getUnit().canBeEquippedWith(equipment) && 
                        colony.canBuildEquipment(equipment)) {
                        Element equipUnitElement = Message.createNewRootElement("equipUnit");
                        equipUnitElement.setAttribute("unit", getUnit().getId());
                        equipUnitElement.setAttribute("type", equipment.getId());
                        equipUnitElement.setAttribute("amount", "1");
                        try {
                            connection.ask(equipUnitElement);
                            scoutEquipment = equipment;
                        } catch (IOException e) {
                            logger.warning("Could not send \"equipUnit\"-message!");
                        }
                        return;
                    }
                }
                valid = false;
                return;
            }

        }

        if (!isTarget(getUnit().getTile(), getUnit(), scoutEquipment)) {
            GoalDecider destinationDecider = new GoalDecider() {
                private PathNode best = null;


                public PathNode getGoal() {
                    return best;
                }

                public boolean hasSubGoals() {
                    return false;
                }

                public boolean check(Unit u, PathNode pathNode) {
                    Tile t = pathNode.getTile();
                    boolean target = isTarget(t, getUnit(), scoutEquipment);
                    if (target) {
                        best = pathNode;
                        debugAction = "Target: " + t.getPosition();
                    }
                    return target;
                }
            };
            PathNode bestPath = map.search(getUnit(), destinationDecider, Integer.MAX_VALUE);

            if (bestPath != null) {
                transportDestination = null;
                Direction direction = moveTowards(connection, bestPath);
                if (direction != null) {
                    final MoveType mt = getUnit().getMoveType(direction);             
                    if (mt == MoveType.ENTER_INDIAN_VILLAGE_WITH_SCOUT) {
                        Element scoutMessage = Message.createNewRootElement("scoutIndianSettlement");
                        scoutMessage.setAttribute("unit", getUnit().getId());
                        scoutMessage.setAttribute("direction", direction.toString());
                        scoutMessage.setAttribute("action", "basic");
                        try {
                            connection.ask(scoutMessage);
                        } catch (IOException e) {
                            logger.warning("Could not send \"scoutIndianSettlement\"-message!");
                            return;
                        }
                        scoutMessage.setAttribute("action", "speak");
                        try {
                            connection.ask(scoutMessage);
                        } catch (IOException e) {
                            logger.warning("Could not send \"scoutIndianSettlement (speak)\"-message!");
                            return;
                        }
                        if (getUnit().isDisposed()) {
                            return;
                        }
                    } else if (mt.isProgress()) {
                        move(connection, direction);
                    }
                }
            } else {
                if (transportDestination != null && !isTarget(transportDestination, getUnit(), scoutEquipment)) {
                    transportDestination = null;
                }
                if (transportDestination == null) {
                    updateTransportDestination();
                }
            }
        }

        exploreLostCityRumour(connection);
        if (getUnit().isDisposed()) {
            return;
        }

        if (isTarget(getUnit().getTile(), getUnit(), scoutEquipment) &&
            getUnit().getColony() != null) {
            if (scoutEquipment != null) {
                Element equipUnitElement = Message.createNewRootElement("equipUnit");
                equipUnitElement.setAttribute("unit", getUnit().getId());
                equipUnitElement.setAttribute("type", scoutEquipment.getId());
                equipUnitElement.setAttribute("amount", "0");
                try {
                    connection.ask(equipUnitElement);
                    scoutEquipment = null;
                } catch (IOException e) {
                    logger.warning("Could not send \"equipUnit (0)\"-message!");
                    return;
                }
                debugAction = "Awaiting 52 horses";
            }
        }
    }
        
    private void updateTransportDestination() {
        if (getUnit().getTile() == null) {
            transportDestination = (Tile) getUnit().getOwner().getEntryLocation();
        } else if (getUnit().isOnCarrier()) {
            GoalDecider destinationDecider = new GoalDecider() {
                private PathNode best = null;


                public PathNode getGoal() {
                    return best;
                }

                public boolean hasSubGoals() {
                    return false;
                }

                public boolean check(Unit u, PathNode pathNode) {
                    Tile t = pathNode.getTile();
                    boolean target = isTarget(t, getUnit(), scoutEquipment);
                    if (target) {
                        best = pathNode;
                        debugAction = "Target: " + t.getPosition();
                    }
                    return target;
                }
            };
            PathNode bestPath = getGame().getMap().search(getUnit(), destinationDecider, Integer.MAX_VALUE, (Unit) getUnit().getLocation());
            if (bestPath != null) {
                transportDestination = bestPath.getLastNode().getTile();
                debugAction = "Transport to: " + transportDestination.getPosition();                
            } else {
                transportDestination = null;
                valid = false;
            }
        } else {
            Iterator<Position> it = getGame().getMap().getFloodFillIterator(getUnit().getTile().getPosition());
            while (it.hasNext()) {
                Tile t = getGame().getMap().getTile(it.next());
                if (isTarget(t, getUnit(), scoutEquipment)) {
                    transportDestination = t;
                    debugAction = "Transport to: " + transportDestination.getPosition();
                    return;
                }
            }
            transportDestination = null;
            valid = false;
        }
    }

    private static boolean isTarget(Tile t, Unit u, EquipmentType scoutEquipment) {
        if (t.hasLostCityRumour()) {
            return true;
        } else if (scoutEquipment != null && t.getColony() != null &&
                   t.getColony().getOwner() == u.getOwner()) {
            for (AbstractGoods goods : scoutEquipment.getGoodsRequired()) {
                if (goods.getType().isBreedable() &&
                    !t.getColony().canBreed(goods.getType()) &&
                    
                    t.getColony().getProductionNetOf(goods.getType()) > 1) {
                    return true;
                }
            }
            return false;
        } else if (t.getSettlement() != null && t.getSettlement() instanceof IndianSettlement
                && !((IndianSettlement) t.getSettlement()).hasBeenVisited()) {
            return true;
        } else {
            return false;
        }
    }

    
    public Tile getTransportDestination() {
        if (getUnit().isOnCarrier()
                || getUnit().getTile() == null) {
            if (transportDestination == null
                    || !transportDestination.isLand()) {
                updateTransportDestination();
            }
            return transportDestination;
        } else if (getUnit().getTile() == transportDestination) {
            transportDestination = null;
            return null;
        } else {
            return null;
        }
    }

    
    public int getTransportPriority() {
        if (getTransportDestination() != null) {
            return NORMAL_TRANSPORT_PRIORITY;
        } else {
            return 0;
        }
    }

    
    public boolean isValid() {
        Unit unit = getUnit();
        
        
        if(!unit.isMounted() && unit.getTile().getColony() == null){
            return false;
        }
        return valid && super.isValid();
    }

    
    public static boolean isValid(AIUnit au) {
        if (au.getUnit().getTile() == null) {
            return true;
        }
        Iterator<Position> it = au.getGame().getMap().getFloodFillIterator(au.getUnit().getTile().getPosition());
        while (it.hasNext()) {
            Tile t = au.getGame().getMap().getTile(it.next());
            if (isTarget(t, au.getUnit(), null)) {
                return true;
            }
        }
        return false;
    }

    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("unit", getUnit().getId());

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setAIUnit((AIUnit) getAIMain().getAIObject(in.getAttributeValue(null, "unit")));
        in.nextTag();
    }

    
    public static String getXMLElementTagName() {
        return "scoutingMission";
    }

    
    public String getDebuggingInfo() {
        return debugAction;
    }

}
