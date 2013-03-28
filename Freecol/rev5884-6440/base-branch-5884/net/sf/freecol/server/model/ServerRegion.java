


package net.sf.freecol.server.model;

import java.awt.Rectangle;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Map.Position;
import net.sf.freecol.common.model.Region;
import net.sf.freecol.common.model.Tile;


public class ServerRegion extends Region {

    
    private int size;

    
    private Rectangle bounds = new Rectangle();


    public ServerRegion(Game game, String nameKey, RegionType type) {
        this(game, nameKey, type, null);
    }

    public ServerRegion(Game game, String nameKey, RegionType type, Region parent) {
        super(game);
        setNameKey(nameKey);
        setType(type);
        setParent(parent);
    }

    
    public final int getSize() {
        return size;
    }

    
    public final void setSize(final int newSize) {
        this.size = newSize;
    }

    
    public final Rectangle getBounds() {
        return bounds;
    }

    
    public final void setBounds(final Rectangle newBounds) {
        this.bounds = newBounds;
    }

    
    public void addTile(Tile tile) {
        tile.setRegion(this);
        size++;
        if (bounds.x == 0 && bounds.width == 0 ||
            bounds.y == 0 && bounds.height == 0) {
            bounds.setBounds(tile.getX(), tile.getY(), 0, 0);
        } else {
            bounds.add(tile.getX(), tile.getY());
        } 
    }

    
    public Position getCenter() {
        return new Position(bounds.x + bounds.width/2, bounds.y + bounds.height/2);
    }


}