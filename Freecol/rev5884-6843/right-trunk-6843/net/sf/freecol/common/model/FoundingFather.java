


package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;



public class FoundingFather extends FreeColGameObjectType {

    private static int nextIndex = 0;
    
    
    private int[] weight = new int[4];

    
    private FoundingFatherType type;

    
    private List<Event> events = new ArrayList<Event>();

    
    private Map<UnitType, UnitType> upgrades;

    public static enum FoundingFatherType { TRADE, EXPLORATION, MILITARY,
            POLITICAL, RELIGIOUS }

    
    private Set<String> availableTo = new HashSet<String>();

    
    private List<AbstractUnit> units;

    
    public FoundingFather() {
        setIndex(nextIndex++);
        setModifierIndex(Modifier.FATHER_PRODUCTION_INDEX);
    }

    
    public FoundingFatherType getType() {
        return type;
    }
    
    
    public String getTypeKey() {
        return getTypeKey(type);
    }

    
    public static String getTypeKey(FoundingFatherType type) {
        return "model.foundingFather." + type.toString().toLowerCase();
    }

    
    public int getWeight(int age) {
        switch(age) {
        case 1:
            return weight[1];
        case 2:
            return weight[2];
        case 3:
        default:
            return weight[3];
        }
    }

    
    public final List<AbstractUnit> getUnits() {
        return units;
    }

    
    public final void setUnits(final List<AbstractUnit> newUnits) {
        this.units = newUnits;
    }

    
    public final List<Event> getEvents() {
        return events;
    }

    
    public final void setEvents(final List<Event> newEvents) {
        this.events = newEvents;
    }

    
    public boolean isAvailableTo(Player player) {
        return (availableTo.isEmpty() || availableTo.contains(player.getNationID()) ||
                availableTo.contains(player.getNationType().getId()));
    }

    
    public final Map<UnitType, UnitType> getUpgrades() {
        return upgrades;
    }

    
    public final void setUpgrades(final Map<UnitType, UnitType> newUpgrades) {
        this.upgrades = newUpgrades;
    }

    public void readAttributes(XMLStreamReader in, Specification specification)
        throws XMLStreamException {
        String typeString = in.getAttributeValue(null, "type").toUpperCase();
        type = Enum.valueOf(FoundingFatherType.class, typeString);

        weight[1] = Integer.parseInt(in.getAttributeValue(null, "weight1"));
        weight[2] = Integer.parseInt(in.getAttributeValue(null, "weight2"));
        weight[3] = Integer.parseInt(in.getAttributeValue(null, "weight3"));

    }

    public void readChildren(XMLStreamReader in, Specification specification)
        throws XMLStreamException {
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            String childName = in.getLocalName();
            if (Event.getXMLElementTagName().equals(childName)) {
                Event event = new Event();
                event.readFromXML(in, specification);
                events.add(event);
            } else if ("nation".equals(childName) ||
                       "nation-type".equals(childName)) {
                availableTo.add(in.getAttributeValue(null, ID_ATTRIBUTE_TAG));
                in.nextTag();
            } else if ("unit".equals(childName)) {
                AbstractUnit unit = new AbstractUnit(in); 
                if (units == null) {
                    units = new ArrayList<AbstractUnit>();
                }
                units.add(unit);
            } else if ("upgrade".equals(childName)) {
                UnitType fromType = specification.getUnitType(in.getAttributeValue(null, "from-id"));
                UnitType toType = specification.getUnitType(in.getAttributeValue(null, "to-id"));
                if (fromType != null && toType != null) {
                    if (upgrades == null) {
                        upgrades = new HashMap<UnitType, UnitType>();
                    }
                    upgrades.put(fromType, toType);
                }
                in.nextTag();
            } else {
                super.readChild(in, specification);
            }
        }

    }

}
