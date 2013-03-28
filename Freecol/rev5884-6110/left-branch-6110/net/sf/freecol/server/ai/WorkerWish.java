


package net.sf.freecol.server.ai;

import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.UnitType;

import org.w3c.dom.Element;



public class WorkerWish extends Wish {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(WorkerWish.class.getName());

    private UnitType unitType;
    private boolean expertNeeded;


    
    public WorkerWish(AIMain aiMain, Location destination, int value, UnitType unitType, boolean expertNeeded) {
        super(aiMain, getXMLElementTagName() + ":" + aiMain.getNextID());

        if (destination == null) {
            throw new NullPointerException("destination == null");
        }
        
        this.destination = destination;
        setValue(value);
        this.unitType = unitType;
        this.expertNeeded = expertNeeded;
    }


    
    public WorkerWish(AIMain aiMain, Element element) {
        super(aiMain, element.getAttribute("ID"));
        readFromXMLElement(element);
    }
    
    
    public WorkerWish(AIMain aiMain, String id) {
        super(aiMain, id);
    }
    
    
    public WorkerWish(AIMain aiMain, XMLStreamReader in) throws XMLStreamException {
        super(aiMain, in.getAttributeValue(null, "ID"));
        readFromXML(in);
    }

    
    public void update(int value, UnitType unitType, boolean expertNeeded) {
        setValue(value);
        this.unitType = unitType;
        this.expertNeeded = expertNeeded;
    }

    
    public UnitType getUnitType() {
        return unitType;
    }

    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("ID", getId());
        out.writeAttribute("destination", destination.getId());
        if (transportable != null) {
            out.writeAttribute("transportable", transportable.getId());
        }
        out.writeAttribute("value", Integer.toString(getValue()));

        out.writeAttribute("unitType", unitType.getId());
        out.writeAttribute("expertNeeded", Boolean.toString(expertNeeded));

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {        
        setId(in.getAttributeValue(null, "ID"));
        destination = (Location) getAIMain().getFreeColGameObject(in.getAttributeValue(null, "destination"));
        
        final String transportableStr = in.getAttributeValue(null, "transportable"); 
        if (transportableStr != null) {
            transportable = (Transportable) getAIMain().getAIObject(transportableStr);
            if (transportable == null) {
                transportable = new AIUnit(getAIMain(), transportableStr);
            }
        } else {
            transportable = null;
        }
        setValue(Integer.parseInt(in.getAttributeValue(null, "value")));

        unitType = FreeCol.getSpecification().getUnitType(in.getAttributeValue(null, "unitType"));
        expertNeeded = Boolean.valueOf(in.getAttributeValue(null, "expertNeeded")).booleanValue();
        in.nextTag();
    }


    
    public static String getXMLElementTagName() {
        return "workerWish";
    }
    
    public String toString() {
        return "WorkerWish: " + unitType.getName()
            + " (" + getValue() + (expertNeeded ? ", expert)" : ")");
    }
}
