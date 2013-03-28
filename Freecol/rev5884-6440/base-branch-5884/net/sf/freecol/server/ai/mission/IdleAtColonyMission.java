


package net.sf.freecol.server.ai.mission;

import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.server.ai.AIMain;
import net.sf.freecol.server.ai.AIObject;
import net.sf.freecol.server.ai.AIUnit;

import org.w3c.dom.Element;



public class IdleAtColonyMission extends Mission {
    private static final Logger logger = Logger.getLogger(IdleAtColonyMission.class.getName());



    
    public IdleAtColonyMission(AIMain aiMain, AIUnit aiUnit) {
        super(aiMain, aiUnit);
    }

    
    
    public IdleAtColonyMission(AIMain aiMain, Element element) {
        super(aiMain);
        readFromXMLElement(element);
    }

    
    public IdleAtColonyMission(AIMain aiMain, XMLStreamReader in) throws XMLStreamException {
        super(aiMain);
        readFromXML(in);
    }
    
    
    public void doMission(Connection connection) {
        Tile thisTile = getUnit().getTile();
        Unit unit = getUnit();

        
        if (thisTile != null) {

            
            if (thisTile.getSettlement()!=null) {
                logger.info("Unit "+unit.getId()+" idle at settlement: "+thisTile.getSettlement().getId());
                return;
            }

            
            PathNode pathToTarget = findNearestColony(unit);
        
            if (pathToTarget != null) {
                Direction dir = moveTowards(connection, pathToTarget);
                if (dir != null) {
                    moveButDontAttack(connection, dir);
                }            
            } else {
                
                moveRandomly(connection);
            }        
        }
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
        return "idleAtColonyMission";
    }
}
