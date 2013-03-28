


package net.sf.freecol.common.model;

import java.lang.reflect.Method;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;



public final class Scope extends FreeColObject implements Cloneable {


    
    private String type;

    
    private String abilityID;

    
    private boolean abilityValue = true;

    
    private String methodName;

    
    private String methodValue;

    
    private boolean matchesNull = true;

    
    private boolean matchNegated = false;


    
    public Scope() {}

    
    public Scope(XMLStreamReader in) throws XMLStreamException {
        readFromXMLImpl(in);
    }

    
    public boolean isMatchesNull() {
        return matchesNull;
    }

    
    public void setMatchesNull(final boolean newMatchesNull) {
        this.matchesNull = newMatchesNull;
    }

    
    public boolean isMatchNegated() {
        return matchNegated;
    }

    
    public void setMatchNegated(final boolean newMatchNegated) {
        this.matchNegated = newMatchNegated;
    }

    
    public String getType() {
        return type;
    }

    
    public void setType(final String newType) {
        this.type = newType;
    }

    
    public String getAbilityID() {
        return abilityID;
    }

    
    public void setAbilityID(final String newAbilityID) {
        this.abilityID = newAbilityID;
    }

    
    public boolean isAbilityValue() {
        return abilityValue;
    }

    
    public void setAbilityValue(final boolean newAbilityValue) {
        this.abilityValue = newAbilityValue;
    }

    
    public String getMethodName() {
        return methodName;
    }

    
    public void setMethodName(final String newMethodName) {
        this.methodName = newMethodName;
    }

    
    public String getMethodValue() {
        return methodValue;
    }

    
    public void setMethodValue(final String newMethodValue) {
        this.methodValue = newMethodValue;
    }


    
    public boolean appliesTo(FreeColObject object) {
        if (object == null) {
            return matchesNull;
        }
        if (type != null && !type.equals(object.getId())) {
            return matchNegated;
        }
        if (abilityID != null && object.hasAbility(abilityID) != abilityValue) {
            return matchNegated;
        }
        if (methodName != null) {
            try {
                Method method = object.getClass().getMethod(methodName);
                if (!method.invoke(object).toString().equals(methodValue)) {
                    return matchNegated;
                }
            } catch(Exception e) {
                return matchNegated;
            }
        }
        return !matchNegated;
    }

    public int hashCode() {
        int hash = 7;
        hash += 31 * hash + (type == null ? 0 : type.hashCode());
        hash += 31 * hash + (abilityID == null ? 0 : abilityID.hashCode());
        hash += 31 * hash + (abilityValue ? 1 : 0);
        hash += 31 * hash + (methodName == null ? 0 : methodName.hashCode());
        hash += 31 * hash + (methodValue == null ? 0 : methodValue.hashCode());
        hash += 31 * hash + (matchesNull ? 1 : 0);
        hash += 31 * hash + (matchNegated ? 1 : 0);
        return hash;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Scope) {
            Scope otherScope = (Scope) o;
            if (matchNegated != otherScope.matchNegated) {
                return false;
            }
            if (matchesNull != otherScope.matchesNull) {
                return false;
            }
            if (type == null) {
                if (otherScope.getType() != type) {
                    return false;
                }
            } else if (!type.equals(otherScope.getType())) {
                return false;
            }
            if (abilityID == null) {
                if (otherScope.getAbilityID() != abilityID) {
                    return false;
                }
            } else if (!abilityID.equals(otherScope.getAbilityID())) {
                return false;
            }
            if (abilityValue != otherScope.isAbilityValue()) {
                return false;
            }
            if (methodName == null) {
                if (otherScope.getMethodName() != methodName) {
                    return false;
                }
            } else if (!methodName.equals(otherScope.getMethodName())) {
                return false;
            }
            if (methodValue == null) {
                if (otherScope.getMethodValue() != methodValue) {
                    return false;
                }
            } else if (!methodValue.equals(otherScope.getMethodValue())) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }


    
    public void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        matchNegated = getAttribute(in, "matchNegated", false);
        matchesNull = getAttribute(in, "matchesNull", true);
        type = in.getAttributeValue(null, "type");
        abilityID = in.getAttributeValue(null, "ability-id");
        abilityValue = getAttribute(in, "ability-value", true);
        methodName = in.getAttributeValue(null, "method-name");
        methodValue = in.getAttributeValue(null, "method-value");
        in.nextTag();
    }
    
    
    public void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("matchNegated", Boolean.toString(matchNegated));
        out.writeAttribute("matchesNull", Boolean.toString(matchesNull));
        out.writeAttribute("type", type);
        out.writeAttribute("ability-id", abilityID);
        out.writeAttribute("ability-value", String.valueOf(abilityValue));
        out.writeAttribute("method-name", methodName);
        out.writeAttribute("method-value", methodValue);

        out.writeEndElement();
    }
    
    public static String getXMLElementTagName() {
        return "scope";
    }


}
