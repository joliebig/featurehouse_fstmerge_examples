


package net.sf.freecol.server.ai;

import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TileImprovementType;
import net.sf.freecol.server.ai.mission.PioneeringMission;

import org.w3c.dom.Element;



public class TileImprovementPlan extends ValuedAIObject {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(TileImprovementPlan.class.getName());

    
    private TileImprovementType type;
    
    
    private AIUnit pioneer = null;
    
    
    private Tile target;


    
    public TileImprovementPlan(AIMain aiMain, Tile target, TileImprovementType type, int value) {
        super(aiMain, getXMLElementTagName() + ":" + aiMain.getNextID());
        
        this.target = target;
        this.type = type;
        setValue(value);
    }
    
    
    public TileImprovementPlan(AIMain aiMain, Element element) {
        super(aiMain, element.getAttribute("ID"));
        readFromXMLElement(element);
    }
    
    
    public TileImprovementPlan(AIMain aiMain, XMLStreamReader in) throws XMLStreamException {
        super(aiMain, in.getAttributeValue(null, "ID"));
        readFromXML(in);
    }
    
    
    public TileImprovementPlan(AIMain aiMain, String id) throws XMLStreamException {
        super(aiMain, id);
    }

    
    
    public void dispose() {
        if (pioneer != null && pioneer.getMission() != null) {
            ((PioneeringMission) pioneer.getMission()).setTileImprovementPlan(null);
        }
        super.dispose();
    }    

    
    public AIUnit getPioneer() {
        return pioneer;
    }
    
        
    public void setPioneer(AIUnit pioneer) {
        this.pioneer = pioneer;    
    }
    
    
    public TileImprovementType getType() {
        return type;
    }
    
    
    public void setType(TileImprovementType type) {
        this.type = type;
    }
    
    
    public Tile getTarget() {
        return target;
    }

    public String toString() {
        return type + " on " + target + " (" + getValue() + ")";
    }

    
    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("ID", getId());        
        out.writeAttribute("type", type.getId());
        out.writeAttribute("value", Integer.toString(getValue()));
        if (pioneer != null) {
            out.writeAttribute("pioneer", pioneer.getId());
        }
        out.writeAttribute("target", target.getId());

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setId(in.getAttributeValue(null, "ID"));
        type = FreeCol.getSpecification().getTileImprovementType(in.getAttributeValue(null, "type"));
        setValue(Integer.parseInt(in.getAttributeValue(null, "value")));
        
        final String pioneerStr = in.getAttributeValue(null, "pioneer");
        if (pioneerStr != null) {
            pioneer = (AIUnit) getAIMain().getAIObject(pioneerStr);
            if (pioneer == null) {
                pioneer = new AIUnit(getAIMain(), pioneerStr);
            }
        } else {
            pioneer = null;
        }
        target = (Tile) getAIMain().getFreeColGameObject(in.getAttributeValue(null, "target"));
        in.nextTag();
    }

    
    public static String getXMLElementTagName() {
        return "tileimprovementplan";
    }    
}
