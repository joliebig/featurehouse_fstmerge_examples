


package net.sf.freecol.common.model;

import  net.sf.freecol.common.model.Map.Direction;


public class PathNode implements Comparable<PathNode> {


    private Tile tile;
    private int cost;
    
    
    private int f;

    private Direction direction;
    private int movesLeft;
    private int turns;
    private boolean onCarrier = false;

    
    public PathNode next = null;

    
    public PathNode previous = null;


    
    
    public PathNode(Tile tile, int cost, int f, Direction direction, int movesLeft, int turns) {
        this.tile = tile;
        this.cost = cost;
        this.f = f;
        this.direction = direction;
        this.movesLeft = movesLeft;
        this.turns = turns;
    }


    
    public int getCost() {
        return cost;
    }


    
    public Tile getTile() {
        return tile;
    }

    
    
    public boolean isOnCarrier() {
        return onCarrier;
    }
    

    
    public void setOnCarrier(boolean onCarrier) {
        this.onCarrier = onCarrier;
    }


    
    public int getTransportDropTurns() {
        PathNode temp = this;
        while (temp.next != null && temp.isOnCarrier()) {
            temp = temp.next;
        }
        return temp.getTurns();
    }
    

    
    public PathNode getTransportDropNode() {
        PathNode temp = this;
        while (temp.next != null && temp.isOnCarrier()) {
            temp = temp.next;
        }
        return temp;
    }
    
    
    
    public PathNode getLastNode() {
        PathNode temp = this;
        while (temp.next != null) {
            temp = temp.next;
        }
        return temp;
    }


    
    public int getF() {
        return f;
    }


    
    public Direction getDirection() {
        return direction;
    }


    
    public int getTurns() {
        return turns;
    }


    
    public int getTotalTurns() {
        PathNode temp = this;
        while (temp.next != null) {
            temp = temp.next;
        }
        return temp.getTurns();
    }


    
    public int getMovesLeft() {
        return movesLeft;
    }

    
    
    public void setMovesLeft(int movesLeft) {
        this.movesLeft = movesLeft;
    }


    
    public int compareTo(PathNode o) {
        return o.getF()-f;
    }
    
    
    public boolean equals(Object o) {
        if (!(o instanceof PathNode)) {
            return false;
        } else {
            return tile.getId().equals(((PathNode) o).getTile().getId()); 
        }
    }
    
    
    public int hashCode() {
        return tile.getX() * 10000 + tile.getY();
    }

    
    public String toString() {
        return "PathNode"
            + " tile=\"" + tile.getId() + "(" + Integer.toString(tile.getX())
            + "," + Integer.toString(tile.getY()) + ")\""
            + " cost=\"" + Integer.toString(cost) + "\""
            + " f=\"" + Integer.toString(f) + "\""
            + " direction=\"" + String.valueOf(direction) + "\""
            + " movesLeft=\"" + Integer.toString(movesLeft) + "\""
            + " turns=\"" + Integer.toString(turns) + "\""
            + " onCarrier=\"" + Boolean.toString(onCarrier) + "\""
            ;
    }
}
