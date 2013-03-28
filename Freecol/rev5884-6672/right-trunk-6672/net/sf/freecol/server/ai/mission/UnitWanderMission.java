


package net.sf.freecol.server.ai.mission;

import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.server.ai.AIMain;
import net.sf.freecol.server.ai.AIObject;
import net.sf.freecol.server.ai.AIUnit;

import org.w3c.dom.Element;



public class UnitWanderMission extends Mission {
    private static final Logger logger = Logger.getLogger(UnitWanderMission.class.getName());



    
    public UnitWanderMission(AIMain aiMain, AIUnit aiUnit) {
        super(aiMain, aiUnit);
    }

    
    
    public UnitWanderMission(AIMain aiMain, Element element) {
        super(aiMain);
        readFromXMLElement(element);
    }

    
    public UnitWanderMission(AIMain aiMain, XMLStreamReader in) throws XMLStreamException {
        super(aiMain);
        readFromXML(in);
    }
    
    
    public void doMission(Connection connection) {
        Tile thisTile = getUnit().getTile();
        Unit unit = getUnit();
        
        while(unit.getMovesLeft() > 0) {
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
        return "unitWanderMission";
    }
}
