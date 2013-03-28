


package net.sf.freecol.server.ai.mission;

import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.MoveType;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.server.ai.AIMain;
import net.sf.freecol.server.ai.AIObject;
import net.sf.freecol.server.ai.AIUnit;

import org.w3c.dom.Element;



public class UnitWanderHostileMission extends Mission {
    private static final Logger logger = Logger.getLogger(UnitWanderHostileMission.class.getName());



    
    public UnitWanderHostileMission(AIMain aiMain, AIUnit aiUnit) {
        super(aiMain, aiUnit);
    }


    
    public UnitWanderHostileMission(AIMain aiMain, Element element) {
        super(aiMain);
        readFromXMLElement(element);
    }
    
    
    public UnitWanderHostileMission(AIMain aiMain, XMLStreamReader in) throws XMLStreamException {
        super(aiMain);
        readFromXML(in);
    }


    
    public void doMission(Connection connection) {
        Tile thisTile = getUnit().getTile();
        Unit unit = getUnit();
        Map map = getGame().getMap();

        if (!(unit.getLocation() instanceof Tile)) {
            return;
        }
        
        PathNode pathToTarget = null;
        if (unit.isOffensiveUnit()) {
            pathToTarget = findTarget(5);
        }
        
        if (pathToTarget != null) {
            Direction direction = moveTowards(connection, pathToTarget);
            if (direction != null &&
                unit.getMoveType(direction) == MoveType.ATTACK) {
                attack(connection, unit, direction);
            }
        } else {
            
            moveRandomly(connection);
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
        return "unitWanderHostileMission";
    }
}
