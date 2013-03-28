

package net.sf.freecol.common.model;

import java.util.Set;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.Specification;
import net.sf.freecol.client.gui.i18n.Messages;


public class FreeColGameObjectType extends FreeColObject {

    private int index = -1;

    
    protected FeatureContainer featureContainer = new FeatureContainer();

    public FreeColGameObjectType() {
        
    }

    public FreeColGameObjectType(String id) {
        setId(id);
    }

    
    public final FeatureContainer getFeatureContainer() {
        return featureContainer;
    }

    
    public final void setFeatureContainer(final FeatureContainer newFeatureContainer) {
        this.featureContainer = newFeatureContainer;
    }

    
    protected final void setIndex(final int index) {
        this.index = index;
    }

    
    public final int getIndex() {
        return index;
    }

    public final String getNameKey() {
        return getId() + ".name";
    }

    public final String getName() {
        return Messages.message(getNameKey());
    }

    public final String getDescription() {
        return Messages.message(getId() + ".description");
    }

    public boolean hasAbility(String id) {
        return featureContainer.hasAbility(id);
    }

    public boolean hasAbility(String id, FreeColGameObjectType type) {
        return featureContainer.hasAbility(id, type);
    }

    public void addAbility(Ability ability) {
        featureContainer.addAbility(ability);
    }

    public void addModifier(Modifier modifier) {
        featureContainer.addModifier(modifier);
    }

    public Set<Modifier> getModifierSet(String id) {
        return featureContainer.getModifierSet(id);
    }

    
    public void applyDifficultyLevel(String difficulty) {
        
    }

    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        
    }

    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        throw new UnsupportedOperationException("Call 'readFromXML' instead.");
    }

    public void readFromXML(XMLStreamReader in, Specification specification) throws XMLStreamException {
        setId(in.getAttributeValue(null, ID_ATTRIBUTE_TAG));
        readAttributes(in, specification);
        readChildren(in, specification);
    }

    protected void readAttributes(XMLStreamReader in, Specification specification)
        throws XMLStreamException {
        
    }

    public void readChildren(XMLStreamReader in, Specification specification) throws XMLStreamException {
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            readChild(in, specification);
        }
    }
    
    
    protected FreeColObject readChild(XMLStreamReader in, Specification specification)
        throws XMLStreamException {
        String childName = in.getLocalName();
        if (Ability.getXMLElementTagName().equals(childName)) {
            Ability ability = new Ability(in, specification);
            if (ability.getSource() == null) {
                ability.setSource(this);
            }
            addAbility(ability); 
            specification.addAbility(ability);
            return ability;
        } else if (Modifier.getXMLElementTagName().equals(childName)) {
            Modifier modifier = new Modifier(in, specification);
            if (modifier.getSource() == null) {
                modifier.setSource(this);
            }
            addModifier(modifier); 
            specification.addModifier(modifier);
            return modifier;
        } else {
            logger.warning("Parsing of " + childName + " is not implemented yet");
            while (in.nextTag() != XMLStreamConstants.END_ELEMENT ||
                   !in.getLocalName().equals(childName)) {
                in.nextTag();
            }
            return null;
        }
    }
    
    
    public String toString() {
        return getId();
    }
}
