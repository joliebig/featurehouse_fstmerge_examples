


package net.sf.freecol.server.ai.mission;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.CombatModel;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Player.Stance;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.MoveType;
import net.sf.freecol.common.model.Unit.UnitState;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.server.ai.AIMain;
import net.sf.freecol.server.ai.AIObject;
import net.sf.freecol.server.ai.AIUnit;

import org.w3c.dom.Element;



public class DefendSettlementMission extends Mission {
    
    private static final Logger logger = Logger.getLogger(DefendSettlementMission.class.getName());


    
    private Settlement settlement;
    
    


    
    public DefendSettlementMission(AIMain aiMain, AIUnit aiUnit, Settlement settlement) {
        super(aiMain, aiUnit);

        this.settlement = settlement;
        
        if (settlement == null) {
            logger.warning("settlement == null");
            throw new NullPointerException("settlement == null");
        }        
    }

    
    public DefendSettlementMission(AIMain aiMain, Element element) {
        super(aiMain);
        readFromXMLElement(element);
    }

    
     public DefendSettlementMission(AIMain aiMain, XMLStreamReader in) throws XMLStreamException {
         super(aiMain);
         readFromXML(in);
     }
    
    
    public void doMission(Connection connection) {
        Unit unit = getUnit();
        Map map = unit.getGame().getMap();
        
        if (!isValid()) {
            return;
        }
        
        if (unit.getTile() == null) {
            return;
        }
        
        if (unit.isOffensiveUnit()) {
            CombatModel combatModel = unit.getGame().getCombatModel();
            Unit bestTarget = null;
            float bestDifference = Float.MIN_VALUE;
            Direction bestDirection = null;
            
            Direction[] directions = map.getRandomDirectionArray();
            for (Direction direction : directions) {
                Tile t = map.getNeighbourOrNull(direction, unit.getTile());
                if (t==null)
                    continue;
                Unit defender = t.getFirstUnit();
                if (defender != null
                    && defender.getOwner().atWarWith(unit.getOwner())
                    && unit.getMoveType(direction) == MoveType.ATTACK) {
                    Unit enemyUnit = t.getDefendingUnit(unit);
                    float enemyAttack = combatModel.getOffencePower(enemyUnit, unit);
                    float weAttack = combatModel.getOffencePower(unit, enemyUnit);
                    float enemyDefend = combatModel.getDefencePower(unit, enemyUnit);
                    float weDefend = combatModel.getDefencePower(enemyUnit, unit);

                    float difference = weAttack / (weAttack + enemyDefend) - enemyAttack / (enemyAttack + weDefend);
                    if (difference > bestDifference) {
                        if (difference > 0 || weAttack > enemyDefend) {
                            bestDifference = difference;
                            bestTarget = enemyUnit;
                            bestDirection = direction;
                        }
                    }
                }
            }
            
            if (bestTarget != null) {
                
                
                attack(connection, unit, bestDirection);               
                return;
            }
        }
            
        if (unit.getTile() != settlement.getTile()) {
            
            Direction r = moveTowards(connection, settlement.getTile());
            moveButDontAttack(connection, r);
        } else {
            if (unit.getState() != UnitState.FORTIFIED
                    && unit.getState() != UnitState.FORTIFYING
                    && unit.checkSetState(UnitState.FORTIFYING)) {
                Element changeStateElement = Message.createNewRootElement("changeState");
                changeStateElement.setAttribute("unit", unit.getId());
                changeStateElement.setAttribute("state", UnitState.FORTIFYING.toString());
                try {
                    logger.log(Level.FINEST, "Sending fortity request...");
                    connection.sendAndWait(changeStateElement);
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Couldn't fortify unit!", e);
                }
            }
        }
    }

        
     public Tile getTransportDestination() {
         if (getUnit().isOnCarrier()) {
             return settlement.getTile();
         } else if (getUnit().getLocation().getTile() == settlement.getTile()) {
             return null;
         } else if (getUnit().getTile() == null || getUnit().findPath(settlement.getTile()) == null) {
             return settlement.getTile();
         } else {
             return null;
         }
     }


     
     public int getTransportPriority() {
        if (getTransportDestination() != null) {
            return NORMAL_TRANSPORT_PRIORITY + 5;
        } else {
            return 0;
        }
     }
     
     
     public Settlement getSettlement() {
         return settlement;
     }
     
    
    public boolean isValid() {
        return !settlement.isDisposed()
                && settlement.getOwner() == getUnit().getOwner()
                && getUnit().isDefensiveUnit();
    }

    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
        
        out.writeAttribute("unit", getUnit().getId());
        out.writeAttribute("settlement", settlement.getId());

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setAIUnit((AIUnit) getAIMain().getAIObject(in.getAttributeValue(null, "unit")));        
        
        settlement = (Settlement) getGame().getFreeColGameObject(in.getAttributeValue(null, "settlement"));
        if (settlement == null) {
            logger.warning("settlement == null");
            throw new NullPointerException("settlement == null");
        }
        
        in.nextTag();
    }

    
    public static String getXMLElementTagName() {
        return "defendSettlementMission";
    }
    
    
    public String getDebuggingInfo() {
        String name = (settlement instanceof Colony) ? ((Colony) settlement).getName() : "";
        return settlement.getTile().getPosition().toString() + " " + name;
    }
}
