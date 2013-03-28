


package net.sf.freecol.common.model.pathfinding;

import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;



public interface CostDecider {

    public static final int ILLEGAL_MOVE = -1;
    
    
    public int getCost(Unit unit, Tile oldTile, Tile newTile, int movesLeftBefore, int turns);   
    
    
    public int getMovesLeft();

        
    public boolean isNewTurn();    
}
