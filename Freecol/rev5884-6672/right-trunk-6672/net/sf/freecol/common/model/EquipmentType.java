

package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.freecol.common.model.Unit.Role;

public class EquipmentType extends BuildableType {

    public static final EquipmentType[] NO_EQUIPMENT = new EquipmentType[0];

    private static int nextIndex = 0;

    
    private int maximumCount = 1;

    
    private int combatLossPriority;

    
    private Role role;

    
    private boolean militaryEquipment;
    
    
    private HashMap<String, Boolean> requiredLocationAbilities = new HashMap<String, Boolean>();
    
    
    private List<String> compatibleEquipment = new ArrayList<String>();


    public EquipmentType() {
        setIndex(nextIndex++);
    }

    
    public final int getMaximumCount() {
        return maximumCount;
    }

    
    public final void setMaximumCount(final int newMaximumCount) {
        this.maximumCount = newMaximumCount;
    }

    
    public final Role getRole() {
        return role;
    }

    
    public final void setRole(final Role newRole) {
        this.role = newRole;
    }

    
    public final int getCombatLossPriority() {
        return combatLossPriority;
    }

    
    public boolean canBeCaptured() {
        return (combatLossPriority > 0);
    }

    
    public final void setCombatLossPriority(final int newCombatLossPriority) {
        this.combatLossPriority = newCombatLossPriority;
    }

    
    public Map<String, Boolean> getUnitAbilitiesRequired() {
        return getAbilitiesRequired();
    }

    
    public Map<String, Boolean> getLocationAbilitiesRequired() {
        return requiredLocationAbilities;
    }

    
    public boolean isCompatibleWith(EquipmentType otherType) {
        if (this.getId().equals(otherType.getId())) {
            
            return true;
        }
        return compatibleEquipment.contains(otherType.getId()) &&
            otherType.compatibleEquipment.contains(getId());
    }

    
    public final boolean isMilitaryEquipment() {
        return militaryEquipment;
    }

    
    public final void setMilitaryEquipment(final boolean newMilitaryEquipment) {
        this.militaryEquipment = newMilitaryEquipment;
    }

    public void readAttributes(XMLStreamReader in, Specification specification)
            throws XMLStreamException {
        maximumCount = getAttribute(in, "maximum-count", 1);
        combatLossPriority = getAttribute(in, "combat-loss-priority", 0);
        String roleString = getAttribute(in, "role", "default");
        role = Enum.valueOf(Role.class, roleString.toUpperCase());
    }

    public void readChildren(XMLStreamReader in, Specification specification)
            throws XMLStreamException {
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            String nodeName = in.getLocalName();
            if ("required-location-ability".equals(nodeName)) {
                String abilityId = in.getAttributeValue(null, "id");
                boolean value = getAttribute(in, "value", true);
                getLocationAbilitiesRequired().put(abilityId, value);
                specification.addAbility(abilityId);
                in.nextTag(); 
            } else if ("compatible-equipment".equals(nodeName)) {
                String equipmentId = in.getAttributeValue(null, "id");
                compatibleEquipment.add(equipmentId);
                in.nextTag(); 
            } else {
                FreeColObject object = super.readChild(in, specification);
                if (object instanceof Modifier) {
                    Modifier modifier = (Modifier) object;
                    if (modifier.getId().equals(Modifier.OFFENCE) ||
                        modifier.getId().equals(Modifier.DEFENCE)) {
                        militaryEquipment = true;
                    }
                }
            }
        }

        if (militaryEquipment) {
            for (AbstractGoods goods : getGoodsRequired()) {
                goods.getType().setMilitaryGoods(true);
            }
        }

    }
}
