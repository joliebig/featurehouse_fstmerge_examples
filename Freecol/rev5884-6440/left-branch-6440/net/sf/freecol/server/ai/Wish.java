


package net.sf.freecol.server.ai;

import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.server.ai.mission.WishRealizationMission;

import org.w3c.dom.Element;



public abstract class Wish extends ValuedAIObject {

    private static final Logger logger = Logger.getLogger(Wish.class.getName());

    protected Location destination = null;
    
    
    protected Transportable transportable = null;



    
    public Wish(AIMain aiMain, String id) {
        super(aiMain, id);
    }


    
    public Wish(AIMain aiMain, Element element) {
        super(aiMain, element.getAttribute("ID"));
        readFromXMLElement(element);
    }
    
    
    public Wish(AIMain aiMain, XMLStreamReader in) throws XMLStreamException {
        super(aiMain, in.getAttributeValue(null, "ID"));
        readFromXML(in);
    }
    

    
    public boolean shouldBeStored() {
        return (transportable != null);
    }
    
    
    public void setTransportable(Transportable transportable) {
        this.transportable = transportable;
    }


    
    public Transportable getTransportable() {
        return transportable;
    }

    
    public void dispose() {
        if (destination instanceof Colony) {
            AIColony ac = (AIColony) getAIMain().getAIObject((FreeColGameObject) destination);
            ac.removeWish(this);
        } else {
            logger.warning("Unknown destination: " + destination);
        }
        if (transportable != null) {
            Transportable temp = transportable;
            transportable = null;
            temp.abortWish(this);
        }
        super.dispose();
    }

    
    public Location getDestination() {
        return destination;
    }
}
