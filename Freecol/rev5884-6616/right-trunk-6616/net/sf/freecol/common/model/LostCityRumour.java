

package net.sf.freecol.common.model;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Element;


public class LostCityRumour extends TileItem {

    
    private RumourType type = null;

    
    private String name = null;

    
    public static enum RumourType {
        NO_SUCH_RUMOUR,
        BURIAL_GROUND,
        EXPEDITION_VANISHES,
        NOTHING,
        LEARN,
        TRIBAL_CHIEF,
        COLONIST,
        RUINS,
        CIBOLA,
        FOUNTAIN_OF_YOUTH
    }

    
    public LostCityRumour(Game game, Tile tile) {
        super(game, tile);
    }

    
    public LostCityRumour(Game game, Tile tile, RumourType type, String name) {
        super(game, tile);
        this.type = type;
        this.name = name;
    }

    
    public LostCityRumour(Game game, XMLStreamReader in) throws XMLStreamException {
        super(game, in);
        readFromXML(in);
    }

    
    public LostCityRumour(Game game, Element e) {
        super(game, e);
        readFromXMLElement(e);
    }

    
    public final RumourType getType() {
        return type;
    }

    
    public final void setType(final RumourType newType) {
        this.type = newType;
    }

    
    public final String getName() {
        return name;
    }

    
    public final void setName(final String newName) {
        this.name = newName;
    }

    
    public final int getZIndex() {
        return RUMOUR_ZINDEX;
    }

    
    @Override
    protected void toXMLImpl(XMLStreamWriter out, Player player, boolean showAll, boolean toSavedGame)
        throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        
        out.writeAttribute("ID", getId());
        out.writeAttribute("tile", getTile().getId());
        if (type != null && (showAll || toSavedGame)) {
            out.writeAttribute("type", getType().toString());
        }
        if (name != null && (showAll || toSavedGame)) {
            out.writeAttribute("name", name);
        }

        
        out.writeEndElement();
    }

    
    @Override
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setId(in.getAttributeValue(null, "ID"));

        tile = getFreeColGameObject(in, "tile", Tile.class);
        String typeString = getAttribute(in, "type", null);
        if (typeString != null) {
            type = Enum.valueOf(RumourType.class, typeString);
        }
        name = getAttribute(in, "name", null);

        in.nextTag();
    }

    public static String getXMLElementTagName() {
        return "lostCityRumour";
    }

}
