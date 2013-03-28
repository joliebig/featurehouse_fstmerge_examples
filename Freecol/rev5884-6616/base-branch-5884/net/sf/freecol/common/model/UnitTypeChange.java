

package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.Specification;

public class UnitTypeChange extends FreeColObject {

    
    private UnitType newUnitType;

    public static enum ChangeType { EDUCATION, NATIVES, EXPERIENCE,
            LOST_CITY, PROMOTION, CREATION, ENTER_COLONY, INDEPENDENCE,
            CLEAR_SKILL, DEMOTION, CAPTURE }

    protected int turnsToLearn = 0;

    protected Set<ChangeType> changeTypes = new HashSet<ChangeType>();

    
    private List<Scope> scopes = new ArrayList<Scope>();


    public UnitTypeChange() {
        
    }

    
    public UnitTypeChange(XMLStreamReader in) throws XMLStreamException {
        this(in, Specification.getSpecification());
    }

    public UnitTypeChange(XMLStreamReader in, Specification specification) throws XMLStreamException {
        setId(in.getAttributeValue(null, ID_ATTRIBUTE_TAG));
        readAttributes(in, specification);
        readChildren(in, specification);
    }

    public List<Scope> getScopes() {
        return scopes;
    }

    public Set<ChangeType> getChangeTypes() {
        return changeTypes;
    }

    
    public boolean asResultOf(ChangeType type) {
        return changeTypes.contains(type);
    }

    
    public boolean appliesTo(Player player) {
        if (scopes.isEmpty()) {
            return true;
        } else {
            for (Scope scope : scopes) {
                if (scope.appliesTo(player)) {
                    return true;
                }
            }
            return false;
        }
    }

    
    public final int getTurnsToLearn() {
        return turnsToLearn;
    }

    
    public final void setTurnsToLearn(final int newTurnsToLearn) {
        this.turnsToLearn = newTurnsToLearn;
    }

    public boolean canBeTaught() {
        return asResultOf(ChangeType.EDUCATION) && turnsToLearn > 0;
    }

    
    public final UnitType getNewUnitType() {
        return newUnitType;
    }

    
    public final void setNewUnitType(final UnitType newNewUnitType) {
        this.newUnitType = newNewUnitType;
    }

    protected void readAttributes(XMLStreamReader in, Specification specification) throws XMLStreamException {
        newUnitType = specification.getType(in.getAttributeValue(null, "unit"), UnitType.class);
        turnsToLearn = getAttribute(in, "turnsToLearn", UnitType.UNDEFINED);
        if (getAttribute(in, "learnInSchool", false) || turnsToLearn > 0) {
            changeTypes.add(ChangeType.EDUCATION);
        }
        if (getAttribute(in, "learnFromNatives", false)) {
            changeTypes.add(ChangeType.NATIVES);
        }
        if (getAttribute(in, "learnFromExperience", false)) {
            changeTypes.add(ChangeType.EXPERIENCE);
        }
        if (getAttribute(in, "learnInLostCity", false)) {
            changeTypes.add(ChangeType.LOST_CITY);
        }
        if (getAttribute(in, "promotion", false)) {
            changeTypes.add(ChangeType.PROMOTION);
        }
        if (getAttribute(in, "clearSkill", false)) {
            changeTypes.add(ChangeType.CLEAR_SKILL);
        }
        if (getAttribute(in, "demotion", false)) {
            changeTypes.add(ChangeType.DEMOTION);
        }
        if (getAttribute(in, "capture", false)) {
            changeTypes.add(ChangeType.CAPTURE);
        }
        if (getAttribute(in, "creation", false)) {
            changeTypes.add(ChangeType.CREATION);
        }
        if (getAttribute(in, "enterColony", false)) {
            changeTypes.add(ChangeType.ENTER_COLONY);
        }
        if (getAttribute(in, "independence", false)) {
            changeTypes.add(ChangeType.INDEPENDENCE);
        }
    }

    public void readChildren(XMLStreamReader in, Specification specification) throws XMLStreamException {
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            String nodeName = in.getLocalName();
            if ("scope".equals(nodeName)) {
                scopes.add(new Scope(in));
            }
        }
    }

    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        
    }


}