


package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.Unit.Role;


public class AbstractUnit extends FreeColObject {

    
    private Role role = Role.DEFAULT;

    
    private int number = 1;

    public AbstractUnit() {
        
    }

    public AbstractUnit(String id, Role someRole, int someNumber) {
        setId(id);
        this.role = someRole;
        this.number = someNumber;
    }

    public AbstractUnit(UnitType unitType, Role someRole, int someNumber) {
        this(unitType.getId(), someRole, someNumber);
    }

    
    public AbstractUnit(XMLStreamReader in) throws XMLStreamException {
        readFromXMLImpl(in);
    }


    
    public final UnitType getUnitType() {
        return FreeCol.getSpecification().getUnitType(getId());
    }

    
    public final Role getRole() {
        return role;
    }

    
    public final void setRole(final Role newRole) {
        this.role = newRole;
    }

    
    public final int getNumber() {
        return number;
    }

    
    public final void setNumber(final int newNumber) {
        this.number = newNumber;
    }

    
    public EquipmentType[] getEquipment() {
        List<EquipmentType> equipment = new ArrayList<EquipmentType>();
        switch(role) {
        case PIONEER:
            EquipmentType tools = FreeCol.getSpecification().getEquipmentType("model.equipment.tools");
            for (int count = 0; count < tools.getMaximumCount(); count++) {
                equipment.add(tools);
            }
            break;
        case MISSIONARY:
            equipment.add(FreeCol.getSpecification().getEquipmentType("model.equipment.missionary"));
            break;
        case SOLDIER:
            equipment.add(FreeCol.getSpecification().getEquipmentType("model.equipment.muskets"));
            break;
        case SCOUT:
            equipment.add(FreeCol.getSpecification().getEquipmentType("model.equipment.horses"));
            break;
        case DRAGOON:
            equipment.add(FreeCol.getSpecification().getEquipmentType("model.equipment.muskets"));
            equipment.add(FreeCol.getSpecification().getEquipmentType("model.equipment.horses"));
            break;
        case DEFAULT:
        default:
        }
        return equipment.toArray(new EquipmentType[equipment.size()]);
    }


    public String toString() {
        return Integer.toString(number) + " " + getId() + " (" + role.toString() + ")";
    }
    
    
    public final void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setId(in.getAttributeValue(null, "id"));
        role = Enum.valueOf(Role.class, getAttribute(in, "role", "default").toUpperCase());
        number = getAttribute(in, "number", 1);
        in.nextTag(); 
    }
    
    
    public void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());
        out.writeAttribute("id", getId());
        out.writeAttribute("role", role.toString().toLowerCase());
        out.writeAttribute("number", String.valueOf(number));
        out.writeEndElement();
    }

    public static String getXMLElementTagName() {
        return "abstractUnit";
    }

}

