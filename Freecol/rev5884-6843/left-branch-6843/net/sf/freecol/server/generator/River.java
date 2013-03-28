

package net.sf.freecol.server.generator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TileImprovement;
import net.sf.freecol.common.model.TileImprovementType;
import net.sf.freecol.common.model.TileItemContainer;
import net.sf.freecol.common.model.TileType;
import net.sf.freecol.common.model.Map.Position;
import net.sf.freecol.server.model.ServerRegion;



public class River {

    private static final Logger logger = Logger.getLogger(MapGenerator.class.getName());

    private static final TileType greatRiver = FreeCol.getSpecification().getTileType("model.tile.greatRiver");

    private static final TileImprovementType riverType = 
        FreeCol.getSpecification().getTileImprovementType("model.improvement.river");

    
    private static enum DirectionChange {
        STRAIGHT_AHEAD,
        RIGHT_TURN,
        LEFT_TURN;

        public Direction getNewDirection(Direction oldDirection) {
            switch(this) {
            case STRAIGHT_AHEAD:
                return oldDirection;
            case RIGHT_TURN:
                switch(oldDirection) {
                case NE:
                    return Direction.SE;
                case SE:
                    return Direction.SW;
                case SW:
                    return Direction.NW;
                case NW:
                    return Direction.NE;
                default:
                    return oldDirection;
                }
            case LEFT_TURN:
                switch(oldDirection) {
                case NE:
                    return Direction.NW;
                case SE:
                    return Direction.NE;
                case SW:
                    return Direction.SE;
                case NW:
                    return Direction.SW;
                default:
                    return oldDirection;
                }
            }
            return oldDirection;
        }
    }

    
    private Direction direction;
    
    
    private Map map;
    
    
    private List<RiverSection> sections = new ArrayList<RiverSection>();

    
    private River nextRiver = null;

    
    private ServerRegion region;

    
    private java.util.Map<Position, River> riverMap;

    
    private boolean connected = false;


    
    public River(Map map, java.util.Map<Position, River> riverMap, ServerRegion region) {
        this.map = map;
        this.riverMap = riverMap;
        this.region = region;
        int index = map.getGame().getModelController().getPseudoRandom()
            .nextInt(Direction.longSides.length);
        direction = Direction.longSides[index];
        logger.fine("Starting new river flowing " + direction.toString());
    }

    public List<RiverSection> getSections() {
        return sections;
    }

    
    public int getLength() {
        return this.sections.size();
    }

    public RiverSection getLastSection() {
        return this.sections.get(sections.size() - 1);
    }

    
    public final ServerRegion getRegion() {
        return region;
    }

    
    public final void setRegion(final ServerRegion newServerRegion) {
        this.region = newServerRegion;
    }

    
    public void add(Map.Position position, Direction direction) {
        this.sections.add(new RiverSection(position, direction));
    }

    
    public void grow(RiverSection lastSection, Map.Position position) {
        
        boolean found = false;
        
        for (RiverSection section : sections) {
            if (found) {
                section.grow();
            } else if (section.getPosition().equals(position)) {
                section.setBranch(lastSection.direction.getReverseDirection(),
                        lastSection.getSize());
                section.grow();
                found = true;
            }
        }
        drawToMap();
        if (nextRiver != null) {
            RiverSection section = sections.get(sections.size() - 1);
            Position neighbor = Map.getAdjacent(section.getPosition(), section.direction);
            nextRiver.grow(section, neighbor);
        }
    }

    
    public boolean isNextToSelf(Map.Position p) {
        for (Direction direction : Direction.longSides) {
            Map.Position px = Map.getAdjacent(p, direction);
            if (this.contains(px)) {
                return true;
            }
        }
        return false;
    }

    
    public boolean isNextToWater(Map.Position p) {
        for (Direction direction : Direction.longSides) {
            Map.Position px = Map.getAdjacent(p, direction);
            final Tile tile = map.getTile(px);
            if (tile == null) {
                continue;
            }
            if (!tile.isLand() || tile.hasRiver()) {
                return true;
            }
        }
        return false;
    }

    
    public boolean contains(Map.Position p) {
        Iterator<RiverSection> sectionIterator = sections.iterator();
        while (sectionIterator.hasNext()) {
            Map.Position q = sectionIterator.next().getPosition();
            if (p.equals(q)) {
                return true;
            }
        }
        return false;
    }
    
    
    public boolean flowFromSource(Map.Position position) {
        Tile tile = map.getTile(position);
        if (!tile.getType().canHaveImprovement(riverType)) {
            
            logger.fine("Tile (" + tile.getType() + ") at "
                        + position + " cannot have rivers.");
            return false;
        } else if (isNextToWater(position)) {
            logger.fine("Tile at " + position + " is next to water.");
            return false;
        } else {
            logger.fine("Tile at " + position + " is suitable source.");
            return flow(position);
        }
    }

    
    private boolean flow(Map.Position source) {
        
        if (sections.size() % 2 == 0) {
            
            int length = DirectionChange.values().length;
            int index = map.getGame().getModelController().getPseudoRandom().nextInt(length);
            DirectionChange change = DirectionChange.values()[index];
            this.direction = change.getNewDirection(this.direction);
            logger.fine("Direction is now " + direction);
        }
        
        for (DirectionChange change : DirectionChange.values()) {
            Direction dir = change.getNewDirection(direction);
            Map.Position newPosition = Map.getAdjacent(source, dir);
            Tile nextTile = map.getTile(newPosition);
            
            if (nextTile == null) {
                continue;
            }
            
            if (!nextTile.getType().canHaveImprovement(riverType)) {
                
                logger.fine("Tile (" + nextTile.getType() + ") at "
                            + newPosition + " cannot have rivers.");
                continue;
            } else if (this.contains(newPosition)) {
                logger.fine("Tile at " + newPosition + " is already in river.");
                continue;
            } else if (isNextToSelf(newPosition)) {
                logger.fine("Tile at " + newPosition + " is next to the river.");
                continue;
            } else {
                
                for (DirectionChange change2 : DirectionChange.values()) {
                    Direction lastDir = change2.getNewDirection(dir);
                    Map.Position px = Map.getAdjacent(newPosition, lastDir);
                    Tile tile = map.getTile(px);
                    if (tile != null && (!tile.isLand() || tile.hasRiver())) {
                        
                        sections.add(new RiverSection(source, dir));
                        RiverSection lastSection = new RiverSection(newPosition, lastDir);
                        sections.add(lastSection);
                        
                        if (tile.hasRiver() && tile.isLand()) {
                            logger.fine("Point " + newPosition + " is next to another river.");
                            
                            nextRiver = riverMap.get(px);
                            nextRiver.grow(lastSection, px);
                            
                            connected = nextRiver.connected;
                            drawToMap();
                        } else {
                            
                            logger.fine("Point " + newPosition + " is next to water.");
                            River someRiver = riverMap.get(px);
                            if (someRiver == null) {
                                sections.add(new RiverSection(px, lastDir.getReverseDirection()));
                            } else {
                                RiverSection waterSection = someRiver.getLastSection();
                                waterSection.setBranch(lastDir.getReverseDirection(),
                                                       TileImprovement.SMALL_RIVER);
                            }
                            connected = tile.isConnected();
                            drawToMap();
                        }
                        return true;
                    }
                }
                
                logger.fine("Tile at " + newPosition + " is suitable.");
                sections.add(new RiverSection(source, dir));
                return flow(newPosition);
            }
        }
        sections = new ArrayList<RiverSection>();
        return false;
    }
    
    
    private void drawToMap() {
        RiverSection oldSection = null;
        
        for (RiverSection section : sections) {
            riverMap.put(section.getPosition(), this);
            if (oldSection != null) {
                section.setBranch(oldSection.direction.getReverseDirection(),
                        oldSection.getSize());
            }
            Tile tile = map.getTile(section.getPosition());
            if (tile.isLand()) {
                if (section.getSize() == TileImprovement.SMALL_RIVER || 
                    section.getSize() == TileImprovement.LARGE_RIVER) {
                    TileItemContainer container = tile.getTileItemContainer();
                    if (container == null) {
                        container = new TileItemContainer(tile.getGame(), tile);
                        tile.setTileItemContainer(container);
                    }
                    container.addRiver(section.getSize(), section.encodeStyle());
                    logger.fine("Added river (magnitude: " + section.getSize() +
                                ") to tile at " + section.getPosition());
                } else if (section.getSize() >= TileImprovement.FJORD_RIVER) {
                    tile.setType(greatRiver);   
                    
                    if (connected) {
                        tile.setConnected(true);
                    }
                    logger.fine("Added fjord (magnitude: " + section.getSize() +
                                ") to tile at " + section.getPosition());
                }
                region.addTile(tile);
                oldSection = section;
            }
        }
    }
}
