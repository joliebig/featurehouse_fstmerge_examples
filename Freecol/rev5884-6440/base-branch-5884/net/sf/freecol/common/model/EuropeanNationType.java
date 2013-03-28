


package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.Specification;


public class EuropeanNationType extends NationType {


    
    private boolean ref;

    
    private List<AbstractUnit> startingUnits = new ArrayList<AbstractUnit>();

    
    public EuropeanNationType(int index) {
        super(index);
    }

    
    public String getEuropeName() {
        return Messages.message(getId() + ".europe");
    }

    
    public String getREFName() {
        return Messages.message(getId() + ".ref");
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

    public void readAttributes(XMLStreamReader in, Specification specification)
            throws XMLStreamException {
        ref = getAttribute(in, "ref", false);
    }

    public void readChildren(XMLStreamReader in, Specification specification)
            throws XMLStreamException {
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            String childName = in.getLocalName();
            if ("unit".equals(childName)) {
                AbstractUnit unit = new AbstractUnit(in); 
                startingUnits.add(unit);
            } else {
                super.readChild(in, specification);
            }
        }
    }

}
