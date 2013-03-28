


package net.sf.freecol.common.model.pathfinding;

import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.MoveType;


class BaseCostDecider implements CostDecider {

    private int movesLeft;
    private boolean newTurn;
    protected MoveType moveType;
    
        
    public int getCost(final Unit unit,
            final Tile oldTile,
            final Tile newTile,
            int movesLeftBefore,
            final int turns) {
        newTurn = false;
              
        
        if (!newTile.isExplored()
            || !unit.getSimpleMoveType(oldTile, newTile, true).isLegal()) {
            return ILLEGAL_MOVE;
        }
        
        int moveCost = unit.getMoveCost(oldTile, newTile, movesLeftBefore);
        if (moveCost <= movesLeftBefore) {
            movesLeft = movesLeftBefore - moveCost;
        } else {
            
            movesLeftBefore = unit.getInitialMovesLeft();
            
            final int mc = unit.getMoveCost(oldTile, newTile, movesLeftBefore);
            moveCost = movesLeft + mc;
            movesLeft = movesLeftBefore - mc;
            newTurn = true;
        }
        
        moveType = unit.getMoveType(oldTile, newTile, movesLeftBefore, true);
        if (!moveType.isLegal()) {
            return ILLEGAL_MOVE;
        }
        
        return moveCost;
    }
    
    
    public int getMovesLeft() {
        return movesLeft;
    }
    
          
    public boolean isNewTurn() {
        return newTurn;
    }
}