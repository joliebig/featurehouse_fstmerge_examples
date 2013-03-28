


package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.Specification;
import net.sf.freecol.client.gui.i18n.Messages;


public abstract class Feature extends FreeColObject {

    
    private FreeColGameObjectType source;

    
    private Turn firstTurn;

    
    private Turn lastTurn;

    
    private List<Scope> scopes;

    
    public final boolean hasTimeLimit() {
        return (firstTurn != null || lastTurn != null);
    }

    
    public final boolean hasScope() {
        return !(scopes == null || scopes.isEmpty());
    }

    
    public String getName() {
        return Messages.message(getId() + ".name");
    }

    
    public final List<Scope> getScopes() {
        return scopes;
    }

    
    public final void setScopes(final List<Scope> newScopes) {
        this.scopes = newScopes;
    }

    
    public final Turn getFirstTurn() {
        return firstTurn;
    }

    
    public final void setFirstTurn(final Turn newFirstTurn) {
        this.firstTurn = newFirstTurn;
    }

    
    public final Turn getLastTurn() {
        return lastTurn;
    }

    
    public final void setLastTurn(final Turn newLastTurn) {
        this.lastTurn = newLastTurn;
    }

    
    public final FreeColGameObjectType getSource() {
        return source;
    }

    
    public final void setSource(final FreeColGameObjectType newSource) {
        this.source = newSource;
    }

    
    public boolean appliesTo(final FreeColGameObjectType objectType) {
        if (!hasScope()) {
            return true;
        } else {
            for (Scope scope : scopes) {
                if (scope.appliesTo(objectType)) {
                    return true;
                }
            }
            return false;
        }
    }

    
    public boolean appliesTo(final FreeColGameObjectType objectType, Turn turn) {
        if (turn != null &&
            (firstTurn != null && turn.getNumber() < firstTurn.getNumber() ||
             lastTurn != null && turn.getNumber() > lastTurn.getNumber())) {
            return false;
        } else {
            return appliesTo(objectType);
        }
    }

    
    public boolean isOutOfDate(Turn turn) {
        return (turn != null &&
                (lastTurn != null && turn.getNumber() > lastTurn.getNumber()));
    }


    public int hashCode() {
        int hash = 7;
        hash += 31 * hash + (getId() == null ? 0 : getId().hashCode());
        hash += 31 * hash + (source == null ? 0 : source.hashCode());
        hash += 31 * hash + (firstTurn == null ? 0 : firstTurn.getNumber());
        hash += 31 * hash + (lastTurn == null ? 0 : lastTurn.getNumber());
        if (scopes != null) {
            for (Scope scope : scopes) {
                
                
                hash += scope.hashCode();
            }
        }
        return hash;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Feature) {
            Feature feature = (Feature) o;
            if (getId() == null) {
                if (feature.getId() != null) {
                    return false;
                }
            } else if (feature.getId() == null) {
                return false;
            } else if (!getId().equals(feature.getId())) {
                return false;
            }
            if (source != feature.source) {
                return false;
            }
            if (firstTurn == null) {
                if (feature.firstTurn != null) {
                    return false;
                }
            } else if (feature.firstTurn == null) {
                return false;
            } else if (firstTurn.getNumber() != feature.firstTurn.getNumber()) {
                return false;
            }
            if (scopes == null) {
                if (feature.scopes != null) {
                    return false;
                }
            } else if (feature.scopes == null) {
                return false;
            } else {
                for (Scope scope : scopes) {
                    if (!feature.scopes.contains(scope)) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }


    protected void writeAttributes(XMLStreamWriter out) throws XMLStreamException {
        if (getSource() != null) {
            out.writeAttribute("source", getSource().getId());
        }
        if (getFirstTurn() != null) {
            out.writeAttribute("firstTurn", String.valueOf(getFirstTurn().getNumber()));
        }
        if (getLastTurn() != null) {
            out.writeAttribute("lastTurn", String.valueOf(getLastTurn().getNumber()));
        }
    }

    protected void writeChildren(XMLStreamWriter out) throws XMLStreamException {
        if (getScopes() != null) {
            for (Scope scope : getScopes()) {
                scope.toXMLImpl(out);
            }
        }
    }

    
    protected void readFromXMLImpl(XMLStreamReader in, Specification specification)
        throws XMLStreamException {
        setId(in.getAttributeValue(null, ID_ATTRIBUTE_TAG));
        
        if (getId() == null) {
            setId(in.getAttributeValue(null, ID_ATTRIBUTE));
        }
        readAttributes(in, specification);
        readChildren(in);
    }

    protected void readAttributes(XMLStreamReader in, Specification specification)
        throws XMLStreamException {
        String sourceId = in.getAttributeValue(null, "source");
        if (sourceId == null) {
            setSource(null);
        } else if (specification != null) {
            setSource(specification.getType(sourceId));
        }

        String firstTurn = in.getAttributeValue(null, "firstTurn");
        if (firstTurn != null) {
            setFirstTurn(new Turn(Integer.parseInt(firstTurn)));
        }

        String lastTurn = in.getAttributeValue(null, "lastTurn");
        if (lastTurn != null) {
            setLastTurn(new Turn(Integer.parseInt(lastTurn)));
        }
    }

    protected void readChildren(XMLStreamReader in) throws XMLStreamException {
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            String childName = in.getLocalName();
            if (Scope.getXMLElementTagName().equals(childName)) {
                Scope scope = new Scope(in);
                if (getScopes() == null) {
                    setScopes(new ArrayList<Scope>());
                }
                getScopes().add(scope);
            } else {
                logger.finest("Parsing of " + childName + " is not implemented yet");
                while (in.nextTag() != XMLStreamConstants.END_ELEMENT ||
                       !in.getLocalName().equals(childName)) {
                    in.nextTag();
                }
            }
        }        
    }

}