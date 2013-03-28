


package net.sf.freecol.server.ai.mission;

import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.pathfinding.CostDeciders;
import net.sf.freecol.common.model.pathfinding.GoalDecider;
import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.networking.CashInTreasureTrainMessage;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.server.ai.AIMain;
import net.sf.freecol.server.ai.AIObject;
import net.sf.freecol.server.ai.AIUnit;

import org.w3c.dom.Element;



public class CashInTreasureTrainMission extends Mission { 
    
    
    
    
    private static final Logger logger = Logger.getLogger(CashInTreasureTrainMission.class.getName());

    

    
    public CashInTreasureTrainMission(AIMain aiMain, AIUnit aiUnit) {
        super(aiMain, aiUnit);
    }

    
    public CashInTreasureTrainMission(AIMain aiMain, Element element) {
        super(aiMain);
        readFromXMLElement(element);
    }
    
    
     public CashInTreasureTrainMission(AIMain aiMain, XMLStreamReader in) throws XMLStreamException {
         super(aiMain);
         readFromXML(in);
     }
    
    
    public void dispose() {
        super.dispose();
    }
    
    
    public void doMission(Connection connection) {     
        Unit unit = getUnit();
    	Map map = unit.getGame().getMap();
                
        if (!isValid()) {
            return;
        }
        
        if (!unit.canCashInTreasureTrain()) {
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
                    if (u.canCashInTreasureTrain(t)) {
                        best = pathNode;
                        return true;
                    }
                    return false;
                }
            };
            PathNode bestPath = map.search(unit, unit.getTile(),
                    destinationDecider,
                    CostDeciders.avoidSettlementsAndBlockingUnits(),
                    Integer.MAX_VALUE);
            if (bestPath != null) {
                Direction direction = moveTowards(connection, bestPath);
                moveButDontAttack(connection, direction);
            }
        }
        
        if (unit.canCashInTreasureTrain()) {
        	Message message = new CashInTreasureTrainMessage(unit);
            try {
                connection.sendAndWait(message.toXMLElement());
            } catch (IOException e) {
                logger.warning("Could not send message: \"cashInTreasureTrain\".");
            }
        }
    }

        
    public Tile getTransportDestination() {
        return null;
    }

    
    public int getTransportPriority() {
        if (getTransportDestination() != null) {
            return NORMAL_TRANSPORT_PRIORITY;
        } else {
            return 0;
        }
    }

    
    public boolean isValid() {  
        return !getUnit().isDisposed();
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
        return "cashInTreasureTrainMission";
    }
}
