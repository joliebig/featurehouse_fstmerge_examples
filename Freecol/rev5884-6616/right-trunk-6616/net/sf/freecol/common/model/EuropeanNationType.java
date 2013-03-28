


package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.freecol.common.model.Settlement.SettlementType;
import net.sf.freecol.common.model.Unit.Role;


public class EuropeanNationType extends NationType {


    
    private boolean ref;

    
    private List<AbstractUnit> startingUnits;

    
    private Map<String, Map<String, AbstractUnit>> startingUnitMap =
        new HashMap<String, Map<String, AbstractUnit>>();

    
    public EuropeanNationType() {
        setTypeOfSettlement(SettlementType.SMALL_COLONY);
    }

    
    public final boolean isREF() {
        return ref;
    }

    
    public final void setREF(final boolean newREF) {
        this.ref = newREF;
    }

    
    public boolean isEuropean() {
        return true;
    }

    
    public List<AbstractUnit> getStartingUnits() {
        return startingUnits;
    }

    
    public List<AbstractUnit> getStartingUnits(String difficulty) {
        Map<String, AbstractUnit> result = new HashMap<String, AbstractUnit>();
        Map<String, AbstractUnit> defaultMap = startingUnitMap.get(null);
        Map<String, AbstractUnit> difficultyMap = startingUnitMap.get(difficulty);
        if (defaultMap != null) {
            result.putAll(defaultMap);
        }
        if (difficultyMap != null) {
            result.putAll(difficultyMap);
        }
        return new ArrayList<AbstractUnit>(result.values());
    }

    
    public void applyDifficultyLevel(String difficulty) {
        startingUnits = getStartingUnits(difficulty);
    }


    public void readAttributes(XMLStreamReader in, Specification specification)
            throws XMLStreamException {
        ref = getAttribute(in, "ref", false);
    }

    public void readChildren(XMLStreamReader in, Specification specification)
            throws XMLStreamException {
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            String childName = in.getLocalName();
            if ("unit".equals(childName)) {
                String id = in.getAttributeValue(null, "id");
                String type = in.getAttributeValue(null, "type");
                Role role = Enum.valueOf(Role.class, getAttribute(in, "role", "default").toUpperCase());
                String difficulty = in.getAttributeValue(null, "difficulty");
                AbstractUnit unit = new AbstractUnit(type, role, 1);
                Map<String, AbstractUnit> units = startingUnitMap.get(difficulty);
                if (units == null) {
                    units = new HashMap<String, AbstractUnit>();
                    startingUnitMap.put(difficulty, units);
                }
                units.put(id, unit);
                in.nextTag();
            } else {
                super.readChild(in, specification);
            }
        }
    }

}
