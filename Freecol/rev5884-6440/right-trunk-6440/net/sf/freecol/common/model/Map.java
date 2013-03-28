

package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.PseudoRandom;
import net.sf.freecol.common.model.pathfinding.CostDecider;
import net.sf.freecol.common.model.pathfinding.CostDeciders;
import net.sf.freecol.common.model.pathfinding.GoalDecider;

import org.w3c.dom.Element;


public class Map extends FreeColGameObject {
    
    private static final Logger logger = Logger.getLogger(Map.class.getName());

    
    public static enum Direction {
        N  ( 0, -2,  0, -2), 
        NE ( 1, -1,  0, -1), 
        E  ( 1,  0,  1,  0),
        SE ( 1,  1,  0,  1),
        S  ( 0,  2,  0,  2),
        SW ( 0,  1, -1,  1),
        W  (-1,  0, -1,  0),
        NW ( 0, -1, -1, -1);

        private int oddDX, oddDY, evenDX, evenDY;

        Direction(int oddDX, int oddDY, int evenDX, int evenDY) {
            this.oddDX = oddDX;
            this.oddDY = oddDY;
            this.evenDX = evenDX;
            this.evenDY = evenDY;
        }

        public int getOddDX() {
            return oddDX;
        }

        public int getOddDY() {
            return oddDY;
        }

        public int getEvenDX() {
            return evenDX;
        }

        public int getEvenDY() {
            return evenDY;
        }

        public Direction getNextDirection() {
            return values()[(ordinal() + 1) % 8];
        }

        public Direction getPreviousDirection() {
            return values()[(ordinal() + 7) % 8];
        }

        
        public Direction getReverseDirection() {
            switch (this) {
            case N:
                return S;
            case NE:
                return SW;
            case E:
                return W;
            case SE:
                return NW;
            case S:
                return N;
            case SW:
                return NE;
            case W:
                return E;
            case NW:
                return SE;
            default:
                return null;
            }
        }

    }

    public static final int NUMBER_OF_DIRECTIONS = Direction.values().length;

    
    public static final int COST_INFINITY = Integer.MIN_VALUE;

    
    public static enum PathType { BOTH_LAND_AND_SEA, ONLY_LAND, ONLY_SEA }

    private Tile[][] tiles;
    
    private final java.util.Map<String, Region> regions = new HashMap<String, Region>();

    

    public Map(Game game, Tile[][] tiles) {
        super(game);
        this.tiles = tiles;
    }

    
    public Map(Game game, XMLStreamReader in) throws XMLStreamException {
        super(game, in);
        readFromXML(in);
    }

    
    public Map(Game game, String id) {
        super(game, id);
    }

    public Collection<Region> getRegions() {
        return regions.values();
    }

    
    public Region getRegion(final String id) {
        return regions.get(id);
    }

    
    public Region getRegionByName(final String id) {
        for (Region region : regions.values()) {
            if (id.equals(region.getName())) {
                return region;
            }
        }
        return null;
    }

    
    public void setRegion(final Region region) {
        regions.put(region.getNameKey(), region);
    }

    
    public PathNode findPath(Tile start, Tile end, PathType type) {
        return findPath(null, start, end, type);
    }

    
    public PathNode findPath(Unit unit, Tile start, Tile end) {
        if (unit == null) {
            throw new IllegalArgumentException("Unit must not be 'null'.");
        }
        return findPath(unit, start, end, null, null, CostDeciders.defaultFor(unit));
    }
    
    
    public PathNode findPath(Unit unit, Tile start, Tile end, CostDecider costDecider) {
        if (unit == null) {
            throw new IllegalArgumentException("Unit must not be 'null'.");
        }
        return findPath(unit, start, end, null, null, costDecider);
    }
    
    
    public PathNode findPath(Unit unit, Tile start, Tile end, Unit carrier, CostDecider costDecider) {
        if (unit == null) {
            throw new IllegalArgumentException("Unit must not be 'null'.");
        }
        return findPath(unit, start, end, null, carrier, costDecider);
    }

    
    public PathNode findPath(Unit unit, Tile start, Tile end, Unit carrier) {
        if (unit == null) {
            throw new IllegalArgumentException("Unit must not be 'null'.");
        }
        return findPath(unit, start, end, null, carrier, CostDeciders.defaultFor(unit));
    }

    
    private PathNode findPath(Unit unit, Tile start, Tile end, PathType type) {
        return findPath(unit, start, end, type, null, CostDeciders.defaultFor(unit));
    }

    
    private PathNode findPath(final Unit unit, final Tile start, final Tile end,
            final PathType type, final Unit carrier, final CostDecider costDecider) {
        

        if (start == null) {
            throw new IllegalArgumentException("Argument 'start' must not be 'null'.");
        }
        if (end == null) {
            throw new IllegalArgumentException("Argument 'end' must not be 'null'.");
        }
        if (start.equals(end)) {
            throw new IllegalArgumentException("start == end");
        }
        if (carrier != null && unit == null) {
            throw new IllegalArgumentException("Argument 'unit' must not be 'null'.");
        }

        
        Unit currentUnit = (carrier != null) ? carrier : unit;

        final PathNode firstNode;
        if (currentUnit != null) {
            firstNode = new PathNode(start, 0,
                                     getDistance(start.getPosition(),
                                                 end.getPosition()),
                                     Direction.N, currentUnit.getMovesLeft(), 0);
            firstNode.setOnCarrier(carrier != null);
        } else {
            firstNode = new PathNode(start, 0, getDistance(start.getPosition(),
                    end.getPosition()), Direction.N, -1, -1);
        }

        final HashMap<String, PathNode> openList = new HashMap<String, PathNode>();
        final HashMap<String, PathNode> closedList = new HashMap<String, PathNode>();
        final PriorityQueue<PathNode> openListQueue
            = new PriorityQueue<PathNode>(1024,
                new Comparator<PathNode>() {
                    public int compare(PathNode o, PathNode p) {
                        int i = o.getF() - p.getF();
                        if (i != 0) {
                            return i;
                        } else {
                            i = o.getTile().getX() - p.getTile().getX();
                            if (i != 0) {
                                return i;
                            } else {
                                return o.getTile().getY() - p.getTile().getY();
                            }
                        }
                    }
                });

        openList.put(firstNode.getTile().getId(), firstNode);
        openListQueue.offer(firstNode);

        while (!openList.isEmpty()) {
            
            PathNode currentNode = openListQueue.poll();
            final Tile currentTile = currentNode.getTile();
            openList.remove(currentTile.getId());
            closedList.put(currentTile.getId(), currentNode);

            
            if (currentTile == end) {
                while (currentNode.previous != null) {
                    currentNode.previous.next = currentNode;
                    currentNode = currentNode.previous;
                }
                return currentNode.next;
            }

            
            currentUnit = (currentNode.isOnCarrier()) ? carrier : unit;

            
            
            
            if (currentUnit != null
                && currentNode.previous != null) {
                Tile previousTile = currentNode.previous.getTile();
                if (!currentUnit.getSimpleMoveType(previousTile, currentTile,
                                                   false).isProgress()) {
                    continue;
                }
            }
            
            
            for (Direction direction : Direction.values()) {
                final Tile newTile = getNeighbourOrNull(direction, currentTile);
                if (newTile == null) {
                    continue;
                }
                
                
                
                
                
                if (currentNode.previous != null
                    && currentNode.previous.getTile() == newTile) {
                    continue;
                }
                if (closedList.containsKey(newTile.getId())) {
                    continue;
                }

                
                int cost = currentNode.getCost();
                int movesLeft = currentNode.getMovesLeft();
                int turns = currentNode.getTurns();
                boolean onCarrier = currentNode.isOnCarrier();
                Unit moveUnit;

                
                
                if (carrier != null
                    && onCarrier
                    && newTile.isLand()
                    && (newTile.getSettlement() == null
                        || newTile.getSettlement().getOwner() == currentUnit.getOwner())) {
                    moveUnit = unit;
                    movesLeft = unit.getInitialMovesLeft();
                } else {
                    moveUnit = (onCarrier) ? carrier : unit;
                }

                
                if (moveUnit == null) {
                    if (((type == PathType.ONLY_SEA && newTile.isLand())
                         || (type == PathType.ONLY_LAND && !newTile.isLand()))
                        && newTile != end) {
                        continue;
                    }
                    cost += newTile.getMoveCost(currentTile);
                } else {
                    int extraCost = costDecider.getCost(moveUnit,
                            currentTile, newTile, movesLeft, turns);
                    if (extraCost == CostDecider.ILLEGAL_MOVE) {
                        
                        
                        
                        if (newTile == end
                            && moveUnit.getSimpleMoveType(currentTile, newTile,
                                                          false).isLegal()) {
                            cost += moveUnit.getInitialMovesLeft();
                            movesLeft = 0;
                        } else {
                            continue;
                        }
                    
                    
                    
                    } else {
                        cost += extraCost;
                    }
                    movesLeft = costDecider.getMovesLeft();
                    if (costDecider.isNewTurn()) {
                        turns++;
                    }
                }

                
                final int f = cost + getDistance(newTile.getPosition(), end.getPosition());
                PathNode successor = openList.get(newTile.getId());
                if (successor != null) {
                    if (successor.getF() <= f) {
                        continue;
                    }
                    openList.remove(successor.getTile().getId());
                    openListQueue.remove(successor);
                }

                
                successor = new PathNode(newTile, cost, f, direction,
                        movesLeft, turns);
                successor.previous = currentNode;
                successor.setOnCarrier(carrier != null && moveUnit == carrier);
                openList.put(newTile.getId(), successor);
                openListQueue.offer(successor);
            }
        }

        return null;
    }

    
    public PathNode search(Unit unit, GoalDecider gd, int maxTurns) {
        return search(unit, unit.getTile(), gd, CostDeciders.defaultFor(unit), maxTurns);
    }

    
    public PathNode search(Unit unit, Tile startTile, GoalDecider gd,
            int maxTurns) {
        return search(unit, startTile, gd, CostDeciders.defaultFor(unit), maxTurns);
    }
    
    
    public PathNode search(Unit unit, GoalDecider gd,
            int maxTurns, Unit carrier) {
        return search(unit, unit.getTile(), gd, CostDeciders.defaultFor(unit), maxTurns, carrier);
    }

    
    public PathNode search(Tile startTile, GoalDecider gd,
            CostDecider costDecider, int maxTurns) {
        return search(null, startTile, gd, costDecider, maxTurns);
    }

    
    public PathNode search(Unit unit, Tile startTile, GoalDecider gd,
            CostDecider costDecider, int maxTurns) {
        return search(unit, startTile, gd, costDecider, maxTurns, null);
    }
    
    
    public PathNode search(final Unit unit, final Tile startTile,
            final GoalDecider gd, final int maxTurns,
            final Unit carrier) {
        return search(unit, startTile, gd, CostDeciders.defaultFor(unit), maxTurns, carrier);
    }
    
    
    public PathNode search(final Unit unit, final Tile startTile,
            final GoalDecider gd, final CostDecider costDecider,
            final int maxTurns, final Unit carrier) {
        

        if (startTile == null) {
            throw new IllegalArgumentException("startTile must not be 'null'.");
        }

        
        Unit currentUnit = (carrier != null) ? carrier : unit;

        final HashMap<String, PathNode> openList
            = new HashMap<String, PathNode>();
        final HashMap<String, PathNode> closedList
            = new HashMap<String, PathNode>();
        final PriorityQueue<PathNode> openListQueue
            = new PriorityQueue<PathNode>(1024,
                new Comparator<PathNode>() {
                    public int compare(PathNode o, PathNode p) {
                        return o.getCost() - p.getCost();
                    }
                });
        final PathNode firstNode
            = new PathNode(startTile, 0, 0, Direction.N,
                           (currentUnit != null) ? currentUnit.getMovesLeft() : -1,
                           0);
        firstNode.setOnCarrier(carrier != null);
        openList.put(startTile.getId(), firstNode);
        openListQueue.offer(firstNode);

        while (!openList.isEmpty()) {
            
            final PathNode currentNode = openListQueue.poll();
            final Tile currentTile = currentNode.getTile();
            openList.remove(currentTile.getId());
            closedList.put(currentTile.getId(), currentNode);

            
            currentUnit = (currentNode.isOnCarrier()) ? carrier : unit;

            
            if (gd.check(currentUnit, currentNode) && !gd.hasSubGoals()) {
                break;
            }

            
            if (currentNode.getTurns() > maxTurns) {
                break;
            }

            
            
            
            if (currentUnit != null
                && currentNode.previous != null) {
                Tile previousTile = currentNode.previous.getTile();
                if (!currentUnit.getSimpleMoveType(previousTile, currentTile,
                                                   false).isProgress()) {
                    continue;
                }
            }

            
            for (Direction direction : Direction.values()) {
                final Tile newTile = getNeighbourOrNull(direction, currentTile);
                if (newTile == null) {
                    continue;
                }
                
                
                
                
                
                if (currentNode.previous != null
                    && currentNode.previous.getTile() == newTile) {
                    continue;
                }
                if (closedList.containsKey(newTile.getId())) {
                    continue;
                }

                
                int cost = currentNode.getCost();
                int movesLeft = currentNode.getMovesLeft();
                int turns = currentNode.getTurns();
                boolean onCarrier = currentNode.isOnCarrier();
                Unit moveUnit;

                
                
                if (carrier != null
                    && onCarrier
                    && newTile.isLand()
                    && (newTile.getSettlement() == null
                        || newTile.getSettlement().getOwner() == currentUnit.getOwner())) {
                    moveUnit = unit;
                    movesLeft = moveUnit.getInitialMovesLeft();
                } else {
                    moveUnit = (onCarrier) ? carrier : unit;
                }

                
                int extraCost = costDecider.getCost(moveUnit,
                        currentTile, newTile, movesLeft, turns);
                if (extraCost == CostDecider.ILLEGAL_MOVE) {
                    continue;
                
                
                
                } else {
                    cost += extraCost;
                }
                movesLeft = costDecider.getMovesLeft();
                if (costDecider.isNewTurn()) {
                    turns++;
                }

                
                PathNode successor = openList.get(newTile.getId());
                if (successor != null) {
                    if (successor.getCost() <= cost) {
                        continue;
                    }
                    openList.remove(successor.getTile().getId());
                    openListQueue.remove(successor);
                }

                
                successor = new PathNode(newTile, cost, cost, direction,
                                         movesLeft, turns);
                successor.previous = currentNode;
                successor.setOnCarrier(carrier != null && moveUnit == carrier);
                openList.put(newTile.getId(), successor);
                openListQueue.offer(successor);
            }
        }

        PathNode bestTarget = gd.getGoal();
        if (bestTarget != null) {
            while (bestTarget.previous != null) {
                bestTarget.previous.next = bestTarget;
                bestTarget = bestTarget.previous;
            }
            return bestTarget.next;
        }
        return null;
    }

    
    public boolean isAdjacentToMapEdge(Tile tile) {
        for (Direction direction : Direction.values()) {
            if (getNeighbourOrNull(direction, tile) == null) {
                return true;
            }
        }
        return false;
    }

    
    public boolean isAdjacentToVerticalMapEdge(Tile tile) {
        if ((getNeighbourOrNull(Direction.E, tile) == null)||(getNeighbourOrNull(Direction.W, tile) == null)) {
            return true;
        }
        return false;
    }
   
    
    public PathNode findPathToEurope(Unit unit, Tile start) {
        return findPathToEurope(unit, start, CostDeciders.defaultFor(unit));
    }

    
    public PathNode findPathToEurope(Unit unit, Tile start, CostDecider costDecider) {
        GoalDecider gd = new GoalDecider() {
            private PathNode goal = null;

            public PathNode getGoal() {
                return goal;
            }

            public boolean hasSubGoals() {
                return false;
            }

            public boolean check(Unit u, PathNode pathNode) {
                Map map = u.getGame().getMap();

                if (pathNode.getTile().canMoveToEurope()) {
                    goal = pathNode;
                    return true;
                }
                
                
                
                
                
                
                if (map.isAdjacentToVerticalMapEdge(pathNode.getTile())) {
                    goal = pathNode;
                    return true;
                }
                return false;
            }
        };
        return search(unit, start, gd, costDecider, Integer.MAX_VALUE);
    }
    
    
    public PathNode findPathToEurope(Tile start) {
        final GoalDecider gd = new GoalDecider() {
            private PathNode goal = null;

            public PathNode getGoal() {
                return goal;
            }

            public boolean hasSubGoals() {
                return false;
            }

            public boolean check(Unit u, PathNode pathNode) {
                Map map = getGame().getMap();
                Tile t = pathNode.getTile();
                if (t.canMoveToEurope()) {
                    goal = pathNode;
                    return true;
                }
                
                
                
                
                
                
                if (map.isAdjacentToVerticalMapEdge(t)) {
                    goal = pathNode;
                    return true;
                }
                return false;
            }
        };
        final CostDecider cd = new CostDecider() {
            public int getCost(Unit unit, Tile oldTile, Tile newTile, int movesLeft, int turns) {
                if (newTile.isLand()) {
                    return ILLEGAL_MOVE;
                } else {
                    return 1;
                }
            }
            public int getMovesLeft() {
                return 0;
            }
            public boolean isNewTurn() {
                return false;
            }
        };
        return search(start, gd, cd, Integer.MAX_VALUE);
    }

    
    public boolean isLandWithinDistance(int x, int y, int distance) {
        Iterator<Position> i = getCircleIterator(new Position(x, y), true,
                distance);
        while (i.hasNext()) {
            if (getTile(i.next()).isLand()) {
                return true;
            }
        }

        return false;
    }

    
    public Tile getTile(Position p) {
        return getTile(p.getX(), p.getY());
    }

    
    public Tile getTile(int x, int y) {
        if (isValid(x, y)) {
            return tiles[x][y];
        } else {
            return null;
        }
    }

    
    public void setTile(Tile tile, int x, int y) {
        tiles[x][y] = tile;
    }

    
    public int getWidth() {
        if (tiles == null) {
            return 0;
        } else {
            return tiles.length;
        }
    }

    
    public int getHeight() {
        if (tiles == null) {
            return 0;
        } else {
            return tiles[0].length;
        }
    }
    
    
    public Direction getDirection(Tile t1, Tile t2) {
        for (Direction d : Direction.values()) {
            if (getNeighbourOrNull(d, t1) == t2) {
                return d;
            }
        }
        return null;
    }

    
    public Tile getNeighbourOrNull(Direction direction, Tile t) {
        return getNeighbourOrNull(direction, t.getX(), t.getY());
    }

    
    public Tile getNeighbourOrNull(Direction direction, int x, int y) {
        if (isValid(x, y)) {
            Position pos = getAdjacent(new Position(x, y), direction);
            return getTile(pos.getX(), pos.getY());
        } else {
            return null;
        }
    }

    
    public List<Tile> getSurroundingTiles(Tile t, int range) {
        List<Tile> result = new ArrayList<Tile>();
        Position tilePosition = new Position(t.getX(), t.getY());
        Iterator<Position> i = (range == 1) ? getAdjacentIterator(tilePosition)
                : getCircleIterator(tilePosition, true, range);

        while (i.hasNext()) {
            Position p = i.next();
            if (!p.equals(tilePosition)) {
                result.add(getTile(p));
            }
        }

        return result;
    }

    
    public List<Tile> getSurroundingTiles(Tile t, int rangeMin, int rangeMax) {
        if (rangeMin > rangeMax || rangeMin < 0) {
            throw new IllegalArgumentException("0 <= rangeMin <= rangeMax");
        }
        List<Tile> result = new ArrayList<Tile>();
        Position tilePosition = new Position(t.getX(), t.getY());
        if (rangeMax == 0) {
            result.add(getTile(tilePosition));
            return result;
        }
        Iterator<Position> i = (rangeMax == 1) ? getAdjacentIterator(tilePosition)
            : getCircleIterator(tilePosition, true, rangeMax);
        while (i.hasNext()) { 
            result.add(getTile(i.next()));
        }
        if (rangeMin == 0) { 
            return result;
        } else if (rangeMin == 1) { 
            result.remove(getTile(tilePosition));
            return result;
        } else if (rangeMin == 2) {
            i = getAdjacentIterator(tilePosition);
        } else {
            i = getCircleIterator(tilePosition, true, rangeMin - 1);
        }
        while (i.hasNext()) { 
            result.remove(getTile(i.next()));
        }
        return result;
    }

    
    public Direction getRandomDirection() {
        int random = getGame().getModelController().getPseudoRandom().nextInt(NUMBER_OF_DIRECTIONS);
        return Direction.values()[random];
    }

    
    public Direction[] getRandomDirectionArray() {
        Direction[] directions = Direction.values();
        PseudoRandom random = getGame().getModelController().getPseudoRandom();
        for (int i = 0; i < directions.length; i++) {
            int i2 = random.nextInt(NUMBER_OF_DIRECTIONS);
            if (i2 != i) {
                Direction temp = directions[i2];
                directions[i2] = directions[i];
                directions[i] = temp;
            }
        }

        return directions;
    }

    
    public WholeMapIterator getWholeMapIterator() {
        return new WholeMapIterator();
    }
    
    
     public static Position getAdjacent(Position position, Direction direction) {
         int x = position.x + ((position.y & 1) != 0 ?
                               direction.getOddDX() : direction.getEvenDX());
         int y = position.y + ((position.y & 1) != 0 ?
                               direction.getOddDY() : direction.getEvenDY());
         return new Position(x, y);
     }
     
     
      public Tile getAdjacentTile(Position position, Direction direction) {
          int x = position.x + ((position.y & 1) != 0 ?
                                direction.getOddDX() : direction.getEvenDX());
          int y = position.y + ((position.y & 1) != 0 ?
                                direction.getOddDY() : direction.getEvenDY());
          return this.getTile(x, y);
      }

    
    public Iterator<Position> getAdjacentIterator(Position centerPosition) {
        return new AdjacentIterator(centerPosition);
    }

    
    public Iterator<Position> getBorderAdjacentIterator(Position centerPosition) {
        return new BorderAdjacentIterator(centerPosition);
    }

    
    public Iterator<Position> getFloodFillIterator(Position centerPosition) {
        return new CircleIterator(centerPosition, true, Integer.MAX_VALUE);
    }

    
    public CircleIterator getCircleIterator(Position center, boolean isFilled,
            int radius) {
        return new CircleIterator(center, isFilled, radius);
    }

    
    public boolean isValid(Position position) {
        return isValid(position.x, position.y, getWidth(), getHeight());
    }

    
    public boolean isValid(int x, int y) {
        return isValid(x, y, getWidth(), getHeight());
    }

    
    public static boolean isValid(Position position, int width, int height) {
        return isValid(position.x, position.y, width, height);
    }
    
    
    public static boolean isValid(int x, int y, int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    
    public Position getRandomLandPosition() {
        PseudoRandom random = getGame().getModelController().getPseudoRandom();
        int x = (getWidth() > 10) ? random.nextInt(getWidth() - 10) + 5 : random.nextInt(getWidth());
        int y = (getHeight() > 10) ? random.nextInt(getHeight() - 10) + 5 : random.nextInt(getHeight());
        Position centerPosition = new Position(x, y);
        Iterator<Position> it = getFloodFillIterator(centerPosition);
        while (it.hasNext()) {
            Position p = it.next();
            if (getTile(p).isLand()) {
                return p;
            }
        }
        return null;
    }
    
    
    public int getDistance(Position position1, Position position2) {
        return getDistance(position1.getX(), position1.getY(),
                position2.getX(), position2.getY());
    }

    
    public int getDistance(int ax, int ay, int bx, int by) {
        int r = bx - ax - (ay - by) / 2;

        if (by > ay && ay % 2 == 0 && by % 2 != 0) {
            r++;
        } else if (by < ay && ay % 2 != 0 && by % 2 == 0) {
            r--;
        }

        return Math.max(Math.abs(ay - by + r), Math.abs(r));
    }

    
    public static final class Position {
        public final int x, y;

        
        public Position(int posX, int posY) {
            x = posX;
            y = posY;
        }

        
        public int getX() {
            return x;
        }

        
        public int getY() {
            return y;
        }

        
        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            } else if (other == null) {
                return false;
            } else if (!(other instanceof Position)) {
                return false;
            } else {
                return x == ((Position) other).x && y == ((Position) other).y;
            }
        }

        
        @Override
        public int hashCode() {
            return x | (y << 16);
        }

        
        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    
    private abstract class MapIterator implements Iterator<Position> {

        protected Direction[] directions = Direction.values();

        
        public abstract Position nextPosition() throws NoSuchElementException;

        
        public Position next() {
            return nextPosition();
        }

        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public final class WholeMapIterator extends MapIterator {
        private int x;
        private int y;

        
        public WholeMapIterator() {
            x = 0;
            y = 0;
        }

        
        public boolean hasNext() {
            return y < getHeight();
        }

        
        @Override
        public Position nextPosition() throws NoSuchElementException {
            if (y < getHeight()) {
                Position newPosition = new Position(x, y);
                x++;
                if (x == getWidth()) {
                    x = 0;
                    y++;
                }
                return newPosition;
            }
            throw new NoSuchElementException("Iterator exhausted");
        }
    }

    private final class AdjacentIterator extends MapIterator {
        
        private Position basePosition;
        
        private int x = 0;

        
        public AdjacentIterator(Position basePosition) {
            this.basePosition = basePosition;
        }

        
        public boolean hasNext() {
            for (int i = x; i < 8; i++) {
                Position newPosition = getAdjacent(basePosition, directions[i]);
                if (isValid(newPosition))
                    return true;
            }
            return false;
        }

        
        @Override
        public Position nextPosition() throws NoSuchElementException {
            for (int i = x; i < 8; i++) {
                Position newPosition = getAdjacent(basePosition, directions[i]);
                if (isValid(newPosition)) {
                    x = i + 1;
                    return newPosition;
                }
            }
            throw new NoSuchElementException("Iterator exhausted");
        }
    }

    
    public final class CircleIterator extends MapIterator {
        private int radius;
        private int currentRadius;
        private Position nextPosition = null;
        
        private int n;

        
        public CircleIterator(Position center, boolean isFilled, int radius) {
            this.radius = radius;

            if (center == null) {
                throw new IllegalArgumentException("center must not be 'null'.");
            }

            n = 0;

            if (isFilled || radius == 1) {
                nextPosition = getAdjacent(center, Direction.NE);
                currentRadius = 1;
            } else {
                currentRadius = radius;
                nextPosition = center;
                for (int i = 1; i < radius; i++) {
                    nextPosition = getAdjacent(nextPosition, Direction.N);
                }
                nextPosition = getAdjacent(nextPosition, Direction.NE);
            }
            if (!isValid(nextPosition)) {
                determineNextPosition();
            }
        }

        
        public int getCurrentRadius() {
            return currentRadius;
        }

        
        private void determineNextPosition() {
            boolean positionReturned = (n != 0);
            do {
                n++;
                final int width = currentRadius * 2;
                if (n >= width * 4) {
                    currentRadius++;
                    if (currentRadius > radius) {
                        nextPosition = null;
                    } else if (!positionReturned) {
                        nextPosition = null;
                    } else {
                        n = 0;
                        positionReturned = false;
                        nextPosition = getAdjacent(nextPosition, Direction.NE);
                    }
                } else {
                    int i = n / width;
                    Direction direction;
                    switch (i) {
                    case 0:
                        direction = Direction.SE;
                        break;
                    case 1:
                        direction = Direction.SW;
                        break;
                    case 2:
                        direction = Direction.NW;
                        break;
                    case 3:
                        direction = Direction.NE;
                        break;
                    default:
                        throw new IllegalStateException("i=" + i + ", n=" + n
                                + ", width=" + width);
                    }
                    nextPosition = getAdjacent(nextPosition, direction);
                }
            } while (nextPosition != null && !isValid(nextPosition));
        }

        
        public boolean hasNext() {
            return nextPosition != null;
        }

        
        @Override
        public Position nextPosition() {
            if (nextPosition != null) {
                final Position p = nextPosition;
                determineNextPosition();
                return p;
            } else {
                return null;
            }
        }
    }

    private final class BorderAdjacentIterator extends MapIterator {
        
        private Position basePosition;
        
        private int index;

        
        public BorderAdjacentIterator(Position basePosition) {
            this.basePosition = basePosition;
            index = 1;
        }

        
        public boolean hasNext() {
            for (int i = index; i < 8; i += 2) {
                Position newPosition = getAdjacent(basePosition, directions[i]);
                if (isValid(newPosition))
                    return true;
            }
            return false;
        }

        
        @Override
        public Position nextPosition() throws NoSuchElementException {
            for (int i = index; i < 8; i += 2) {
                Position newPosition = getAdjacent(basePosition, directions[i]);
                if (isValid(newPosition)) {
                    index = i + 2;
                    return newPosition;
                }
            }
            throw new NoSuchElementException("Iterator exhausted");
        }
    }

    
    @Override
    protected void toXMLImpl(XMLStreamWriter out, Player player,
            boolean showAll, boolean toSavedGame) throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("ID", getId());
        out.writeAttribute("width", Integer.toString(getWidth()));
        out.writeAttribute("height", Integer.toString(getHeight()));

        for (Region region : regions.values()) {
            region.toXML(out);
        }

        Iterator<Position> tileIterator = getWholeMapIterator();
        while (tileIterator.hasNext()) {
            Tile tile = getTile(tileIterator.next());

            if (showAll || player.hasExplored(tile)) {
                tile.toXML(out, player, showAll, toSavedGame);
            } else {
                Tile hiddenTile = new Tile(getGame(), null, tile.getX(), tile.getY());
                hiddenTile.setFakeID(tile.getId());
                hiddenTile.toXML(out, player, showAll, toSavedGame);
            }
        }

        out.writeEndElement();
    }

    
    @Override
    protected void readFromXMLImpl(XMLStreamReader in)
            throws XMLStreamException {
        setId(in.getAttributeValue(null, "ID"));

        if (tiles == null) {
            int width = Integer.parseInt(in.getAttributeValue(null, "width"));
            int height = Integer.parseInt(in.getAttributeValue(null, "height"));

            tiles = new Tile[width][height];
        }

        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if (in.getLocalName().equals(Tile.getXMLElementTagName())) {
                Tile t = updateFreeColGameObject(in, Tile.class);
                setTile(t, t.getX(), t.getY());
            } else if (in.getLocalName().equals(Region.getXMLElementTagName())) {
                setRegion(updateFreeColGameObject(in, Region.class));
            } else {
                logger.warning("Unknown tag: " + in.getLocalName() + " loading map");
                in.nextTag();
            }
        }
    }

    
    public static String getXMLElementTagName() {
        return "map";
    }

    
    public Iterable<Tile> getAllTiles() {
        return new Iterable<Tile>(){
            public Iterator<Tile> iterator(){
                final WholeMapIterator m = getWholeMapIterator();
                
                return new Iterator<Tile>(){
                    public boolean hasNext() {
                        return m.hasNext();
                    }

                    public Tile next() {
                        return getTile(m.next());
                    }

                    public void remove() {
                        m.remove();
                    }
                };
            }
        };
    }
}
