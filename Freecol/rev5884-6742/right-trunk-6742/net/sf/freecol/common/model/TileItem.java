

package net.sf.freecol.common.model;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.w3c.dom.Element;


public abstract class TileItem extends FreeColGameObject implements Locatable {

    public static final int RESOURCE_ZINDEX = 400;
    public static final int RUMOUR_ZINDEX = 500;

    protected Tile tile;
    
    
    public TileItem(Game game, Tile tile) {
        super(game);
        if (tile == null) {
            throw new IllegalArgumentException("Parameter 'tile' must not be 'null'.");
        }
        this.tile = tile;
    }

    
    public TileItem(Game game, XMLStreamReader in) throws XMLStreamException {
        super(game, in);
    }

    
    public TileItem(Game game, Element e) {
        super(game, e);
    }

    
    public TileItem(Game game, String id) {
        super(game, id);
    }

    
    public void setLocation(Location newLocation) {
        if (newLocation instanceof Tile) {
            tile = ((Tile) newLocation);
        } else {
            throw new IllegalArgumentException("newLocation is not a Tile");
        }
    }

    
    public Location getLocation() {
        return tile;
    }

    
    public Tile getTile() {
        return tile;
    }

    
    public int getSpaceTaken() {
        return 0;
    }

    
    public abstract int getZIndex();

    
    public void dispose() {
        super.dispose();
    }
}
