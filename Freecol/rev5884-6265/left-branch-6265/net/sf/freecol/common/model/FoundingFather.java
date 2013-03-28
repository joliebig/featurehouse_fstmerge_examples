


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

import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.Specification;


public class FoundingFather extends FreeColGameObjectType {
    
    
    private int[] weight = new int[4];

    
    private FoundingFatherType type;

    
    private Map<UnitType, UnitType> upgrades;

    public static enum FoundingFatherType { TRADE, EXPLORATION, MILITARY,
            POLITICAL, RELIGIOUS }

    
    private Map<String, String> events = new HashMap<String, String>();

    
    private Set<String> availableTo = new HashSet<String>();

    
    private List<AbstractUnit> units;

    
    public FoundingFather(int newIndex) {
        setIndex(newIndex);
    }

    
    public String getText() {
        return Messages.message(getId() + ".text");
    }

    
    public String getBirthAndDeath() {
        return Messages.message(getId() + ".birthAndDeath");
    }

    
    public FoundingFatherType getType() {
        return type;
    }
    
    
    public String getTypeAsString() {
        return getTypeAsString(type);
    }

    
    public static String getTypeAsString(FoundingFatherType type) {
        return Messages.message("model.foundingFather." + type.toString().toLowerCase());
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

    
    public Map<String, String> getEvents() {
        return events;
    }

    
    public void setEvents(Map<String, String> newEvents) {
        events = newEvents;
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
            if ("event".equals(childName)) {
                String eventId = in.getAttributeValue(null, ID_ATTRIBUTE_TAG);
                String value = in.getAttributeValue(null, "value");
                events.put(eventId, value);
                in.nextTag(); 
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
