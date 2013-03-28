

package net.sf.freecol.common.model;

import java.awt.Color;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;



public class Nation extends FreeColGameObjectType {
	
	static public String UNKNOWN_NATION_ID = "model.nation.unknownEnemy";

    private static int nextIndex = 0;

    
    private Color color;

    
    private NationType type;

    
    private boolean selectable;

    
    private String anthem;

    
    private Nation refNation;

    
    public Nation() {
        setIndex(nextIndex++);
    }

    
    public final Color getColor() {
        return color;
    }

    
    public final void setColor(final Color newColor) {
        this.color = newColor;
    }

    
    public final String getAnthem() {
        return anthem;
    }

    
    public final void setAnthem(final String newAnthem) {
        this.anthem = newAnthem;
    }

    
    public final NationType getType() {
        return type;
    }

    
    public final void setType(final NationType newType) {
        this.type = newType;
    }

    
    public final String getRulerNameKey() {
        return getId() + ".ruler";
    }

    
    public final boolean isSelectable() {
        return selectable;
    }

    
    public final Nation getRefNation() {
        return refNation;
    }

    
    public final void setRefNation(final Nation newRefNation) {
        this.refNation = newRefNation;
    }

    
    public final void setSelectable(final boolean newSelectable) {
        this.selectable = newSelectable;
    }

    public void readAttributes(XMLStreamReader in, Specification specification)
        throws XMLStreamException {
        setColor(new Color(Integer.decode(in.getAttributeValue(null, "color"))));
        type = specification.getNationType(in.getAttributeValue(null, "nation-type"));
        selectable = getAttribute(in, "selectable", false);
        String refId = getAttribute(in, "ref", null);
        if (refId != null) {
            refNation = specification.getNation(refId);
        }
        anthem = in.getAttributeValue(null, "anthem");
   }


}
