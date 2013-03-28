

package net.sf.freecol.server.ai;

import java.util.logging.Logger;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.model.Locatable;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.server.ai.goal.Goal;
import net.sf.freecol.server.ai.mission.BuildColonyMission;
import net.sf.freecol.server.ai.mission.CashInTreasureTrainMission;
import net.sf.freecol.server.ai.mission.DefendSettlementMission;
import net.sf.freecol.server.ai.mission.IdleAtColonyMission;
import net.sf.freecol.server.ai.mission.IndianBringGiftMission;
import net.sf.freecol.server.ai.mission.IndianDemandMission;
import net.sf.freecol.server.ai.mission.Mission;
import net.sf.freecol.server.ai.mission.PioneeringMission;
import net.sf.freecol.server.ai.mission.PrivateerMission;
import net.sf.freecol.server.ai.mission.ScoutingMission;
import net.sf.freecol.server.ai.mission.TransportMission;
import net.sf.freecol.server.ai.mission.UnitSeekAndDestroyMission;
import net.sf.freecol.server.ai.mission.UnitWanderHostileMission;
import net.sf.freecol.server.ai.mission.UnitWanderMission;
import net.sf.freecol.server.ai.mission.WishRealizationMission;
import net.sf.freecol.server.ai.mission.WorkInsideColonyMission;

import org.w3c.dom.Element;


public class AIUnit extends AIObject implements Transportable {
    private static final Logger logger = Logger.getLogger(AIUnit.class.getName());




    
    private Unit unit;

    
    private Mission mission;

    
    private Goal goal = null;              

    
    private int dynamicPriority;

    
    private AIUnit transport = null;


    
    public AIUnit(AIMain aiMain, Unit unit) {
        super(aiMain, unit.getId());

        this.unit = unit;

        mission = new UnitWanderHostileMission(aiMain, this);
    }

    
    public AIUnit(AIMain aiMain, Element element) {
        super(aiMain, element.getAttribute("ID"));
        readFromXMLElement(element);
    }

    
    public AIUnit(AIMain aiMain, XMLStreamReader in) throws XMLStreamException {
        super(aiMain, in.getAttributeValue(null, "ID"));
        readFromXML(in);
    }

    
    public AIUnit(AIMain aiMain, String id) {
        super(aiMain, id);
        unit = (Unit) getAIMain().getFreeColGameObject(id);
        if (unit == null) {
            logger.warning("Could not find unit: " + id);
        }
        uninitialized = true;
    }

    
    public Unit getUnit() {
        return unit;
    }

    
    public void abortWish(Wish w) {
        if (mission instanceof WishRealizationMission) {
            
            mission = null;
            dynamicPriority = 0;
        }
        if (w.getTransportable() == this) {
            w.dispose();
        }
    }

    
    public Locatable getTransportLocatable() {
        return unit;
    }

    
    public Location getTransportSource() {
        return getUnit().getLocation();
    }

    
    public Location getTransportDestination() {
        if (hasMission()) {
            return mission.getTransportDestination();
        } else {
            return null;
        }
    }

    
    public int getTransportPriority() {
        if (hasMission()) {
            return mission.getTransportPriority() + dynamicPriority;
        } else {
            return 0;
        }
    }

    
    public void increaseTransportPriority() {
        if (hasMission()) {
            ++dynamicPriority;
        }
    }

    
    public AIUnit getTransport() {
        return transport;
    }

    
    public void setTransport(AIUnit transport) {
        AIUnit oldTransport = this.transport;
        this.transport = transport;

        if (oldTransport != null) {
            
            if (oldTransport.getMission() != null && oldTransport.getMission() instanceof TransportMission) {
                TransportMission tm = (TransportMission) oldTransport.getMission();
                if (tm.isOnTransportList(this)) {
                    tm.removeFromTransportList(this);
                }
            }
        }

        if (transport != null && transport.getMission() instanceof TransportMission
                && !((TransportMission) transport.getMission()).isOnTransportList(this)) {
            
            ((TransportMission) transport.getMission()).addToTransportList(this);
        }
    }

    
    public Mission getMission() {
        return mission;
    }

    
    public boolean hasMission() {
        return (mission != null);
    }

    
    public void setMission(Mission mission) {
        final Mission oldMission = this.mission;
        if (oldMission != null) {
            oldMission.dispose();
        }
        this.mission = mission;
        this.dynamicPriority = 0;
    }

    
    public void doMission(Connection connection) {
        if (getMission() != null && getMission().isValid()) {
            getMission().doMission(connection);
        }
    }

    
    public void dispose() {
        setMission(null);
        setTransport(null);
        super.dispose();
    }

    
    public String getId() {
        if (unit != null) {
            return unit.getId();
        } else {
            logger.warning("unit == null");
            return null;
        }
    }

    public void setGoal(Goal g) {
        goal = g;
    }

    public Goal getGoal() {
        return goal;
    }

    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("ID", getId());
        if (transport != null) {
            if (transport.getUnit() == null) {
                logger.warning("transport.getUnit() == null");
            } else if (getAIMain().getAIObject(transport.getId()) == null) {
                logger.warning("broken reference to transport");
            } else if (transport.getMission() != null && transport.getMission() instanceof TransportMission
                    && !((TransportMission) transport.getMission()).isOnTransportList(this)) {
                logger.warning("We should not be on the transport list.");
            } else {
                out.writeAttribute("transport", transport.getUnit().getId());
            }
        }
        if (mission != null) {
            mission.toXML(out);
        }

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        final String inID = in.getAttributeValue(null, "ID");
        unit = (Unit) getAIMain().getFreeColGameObject(inID);

        if (unit == null) {
            logger.warning("Could not find unit: " + inID);
        }

        final String transportStr = in.getAttributeValue(null, "transport");
        if (transportStr != null) {
            transport = (AIUnit) getAIMain().getAIObject(transportStr);
            if (transport == null) {
                transport = new AIUnit(getAIMain(), transportStr);
            }
        } else {
            transport = null;
        }

        if (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if (in.getLocalName().equals(UnitWanderHostileMission.getXMLElementTagName())) {
                mission = new UnitWanderHostileMission(getAIMain(), in);
            } else if (in.getLocalName().equals(UnitWanderMission.getXMLElementTagName())) {
                mission = new UnitWanderMission(getAIMain(), in);
            } else if (in.getLocalName().equals(IndianBringGiftMission.getXMLElementTagName())) {
                mission = new IndianBringGiftMission(getAIMain(), in);
            } else if (in.getLocalName().equals(BuildColonyMission.getXMLElementTagName())) {
                mission = new BuildColonyMission(getAIMain(), in);
            } else if (in.getLocalName().equals(IndianDemandMission.getXMLElementTagName())) {
                mission = new IndianDemandMission(getAIMain(), in);
            } else if (in.getLocalName().equals(TransportMission.getXMLElementTagName())) {
                mission = new TransportMission(getAIMain(), in);
            } else if (in.getLocalName().equals(WishRealizationMission.getXMLElementTagName())) {
                mission = new WishRealizationMission(getAIMain(), in);
            } else if (in.getLocalName().equals(UnitSeekAndDestroyMission.getXMLElementTagName())) {
                mission = new UnitSeekAndDestroyMission(getAIMain(), in);
            } else if (in.getLocalName().equals(PioneeringMission.getXMLElementTagName())) {
                mission = new PioneeringMission(getAIMain(), in);
            } else if (in.getLocalName().equals(DefendSettlementMission.getXMLElementTagName())) {
                mission = new DefendSettlementMission(getAIMain(), in);
            } else if (in.getLocalName().equals(WorkInsideColonyMission.getXMLElementTagName())) {
                mission = new WorkInsideColonyMission(getAIMain(), in);
            } else if (in.getLocalName().equals(ScoutingMission.getXMLElementTagName())) {
                mission = new ScoutingMission(getAIMain(), in);
            } else if (in.getLocalName().equals(CashInTreasureTrainMission.getXMLElementTagName())) {
                mission = new CashInTreasureTrainMission(getAIMain(), in);
            } else if (in.getLocalName().equals(IdleAtColonyMission.getXMLElementTagName())) {
                mission = new IdleAtColonyMission(getAIMain(), in);
            } else if (in.getLocalName().equals(PrivateerMission.getXMLElementTagName())) {
                mission = new PrivateerMission(getAIMain(), in);
            } else {
                logger.warning("Could not find mission-class for: " + in.getLocalName());
                mission = new UnitWanderHostileMission(getAIMain(), this);
                return;
            }

            in.nextTag();
        }
    }

    
    public static String getXMLElementTagName() {
        return "aiUnit";
    }
}
