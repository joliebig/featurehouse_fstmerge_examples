

package net.sf.freecol.common.model;

import java.util.HashMap;
import java.util.Map;

import net.sf.freecol.common.Specification;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public class NationOptions extends FreeColObject{

    
    public static final int DEFAULT_NO_OF_EUROPEANS = 4;

    
    public static enum Advantages { NONE, FIXED, SELECTABLE };

    
    public static enum NationState { AVAILABLE, AI_ONLY, NOT_AVAILABLE };

    
    private boolean selectColors;

    
    private Advantages nationalAdvantages;

    
    private Map<Nation, NationState> nations = new HashMap<Nation, NationState>();

    
    public final Map<Nation, NationState> getNations() {
        return nations;
    }

    
    public final void setNations(final Map<Nation, NationState> newNations) {
        this.nations = newNations;
    }

    
    public final Advantages getNationalAdvantages() {
        return nationalAdvantages;
    }

    
    public final void setNationalAdvantages(final Advantages newNationalAdvantages) {
        this.nationalAdvantages = newNationalAdvantages;
    }

    
    public final boolean canSelectColors() {
        return selectColors;
    }

    
    public final void setSelectColors(final boolean newSelectColors) {
        this.selectColors = newSelectColors;
    }

    
    public final NationState getNationState(Nation nation) {
        return nations.get(nation);
    }

    
    public final void setNationState(final Nation nation, final NationState state) {
        this.nations.put(nation, state);
    }

    
    public static final NationOptions getDefaults() {
        NationOptions result = new NationOptions();
        result.setSelectColors(true);
        result.setNationalAdvantages(Advantages.SELECTABLE);
        int counter = 0;
        Map<Nation, NationState> defaultNations = new HashMap<Nation, NationState>();
        for (Nation nation : Specification.getSpecification().getNations()) {
            if (nation.getType().isREF()) {
                continue;
            } else if (nation.getType().isEuropean() && nation.isSelectable()) {
                if (counter < DEFAULT_NO_OF_EUROPEANS) {
                    defaultNations.put(nation, NationState.AVAILABLE);
                    counter++;
                } else {
                    defaultNations.put(nation, NationState.NOT_AVAILABLE);
                }
            } else {
                defaultNations.put(nation, NationState.AI_ONLY);
            }
        }
        result.setNations(defaultNations);
        return result;
    }


    
    public final void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        

        selectColors = getAttribute(in, "selectColors", true);
        String advantages = getAttribute(in, "nationalAdvantages", "selectable").toUpperCase();
        nationalAdvantages = Enum.valueOf(Advantages.class, advantages);

        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if (in.getLocalName().equals("Nations")) {
                nations.clear();
                while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
                    if (in.getLocalName().equals("Nation")) {
                        String nationId = in.getAttributeValue(null, ID_ATTRIBUTE_TAG);
                        Nation nation = Specification.getSpecification().getNation(nationId);
                        NationState state = Enum.valueOf(NationState.class,
                                                         in.getAttributeValue(null, "state"));
                        nations.put(nation, state);
                    }
                    in.nextTag();
                }
            }
        }
    }
    
    
    public void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());
        
        out.writeAttribute("selectColors", Boolean.toString(selectColors));
        out.writeAttribute("nationalAdvantages", nationalAdvantages.toString());
        out.writeStartElement("Nations");
        for (Map.Entry<Nation, NationState> entry : nations.entrySet()) {
            out.writeStartElement("Nation");
            out.writeAttribute(ID_ATTRIBUTE_TAG, entry.getKey().getId());
            out.writeAttribute("state", entry.getValue().toString());
            out.writeEndElement();
        }
        out.writeEndElement();

        out.writeEndElement();
    }

    public static String getXMLElementTagName() {
        return "nationOptions";
    }

    
    public String toString() {
        StringBuilder result = new StringBuilder(); 
        result.append("selectColors: " + selectColors + "\n");
        result.append("nationalAdvantages: " + nationalAdvantages.toString() + "\n");
        result.append("Nations:\n");
        for (Map.Entry<Nation, NationState> entry : nations.entrySet()) {
            result.append("   " + entry.getKey().getId() + " " + entry.getValue().toString() + "\n");
        }
        return result.toString();
    }
}