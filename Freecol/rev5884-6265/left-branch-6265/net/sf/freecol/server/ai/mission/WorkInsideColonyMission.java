

package net.sf.freecol.server.ai.mission;

import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.server.ai.AIColony;
import net.sf.freecol.server.ai.AIMain;
import net.sf.freecol.server.ai.AIObject;
import net.sf.freecol.server.ai.AIUnit;

import org.w3c.dom.Element;


public class WorkInsideColonyMission extends Mission{
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(WorkInsideColonyMission.class.getName());


    private AIColony aiColony;


    
    public WorkInsideColonyMission(AIMain aiMain, AIUnit aiUnit, AIColony aiColony) {
        super(aiMain, aiUnit);
        this.aiColony = aiColony;
        if (aiColony == null) {
            throw new NullPointerException("aiColony == null");
        }
    }


    
    public WorkInsideColonyMission(AIMain aiMain, Element element) {
        super(aiMain);
        readFromXMLElement(element);
    }

    
    public WorkInsideColonyMission(AIMain aiMain, XMLStreamReader in) throws XMLStreamException {
        super(aiMain);
        readFromXML(in);
    }
    

    
    public void dispose() {
        super.dispose();
    }


    
    public void doMission(Connection connection) {
        
    }



    
    public boolean isValid() {
        return !aiColony.getColony().isDisposed();
    }

    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
        
        out.writeAttribute("unit", getUnit().getId());
        out.writeAttribute("colony", aiColony.getId());

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setAIUnit((AIUnit) getAIMain().getAIObject(in.getAttributeValue(null, "unit")));
        aiColony = (AIColony) getAIMain().getAIObject(in.getAttributeValue(null, "colony"));
        if (aiColony == null) {
            aiColony = new AIColony(getAIMain(), in.getAttributeValue(null, "colony"));
        }
        in.nextTag();
    }


    
    public static String getXMLElementTagName() {
        return "workInsideColonyMission";
    }
}
