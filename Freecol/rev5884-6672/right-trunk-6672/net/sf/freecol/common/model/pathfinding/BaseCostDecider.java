


package net.sf.freecol.common.model.pathfinding;

import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.MoveType;


class BaseCostDecider implements CostDecider {

    private int movesLeft;
    private boolean newTurn;
    
        
    public int getCost(final Unit unit,
            final Tile oldTile,
            final Tile newTile,
            int movesLeftBefore,
            final int turns) {
        newTurn = false;
              
        
        if (!newTile.isExplored()) {
            return ILLEGAL_MOVE;
        }
        
        
        
        switch (unit.getSimpleMoveType(oldTile, newTile, true)) {
        case MOVE_HIGH_SEAS:
            break;
        case MOVE: case EXPLORE_LOST_CITY_RUMOUR:
            if (!(unit.getLocation() instanceof Unit)) break;
            
        case ATTACK:
        case EMBARK:
        case ENTER_INDIAN_SETTLEMENT_WITH_FREE_COLONIST:
        case ENTER_INDIAN_SETTLEMENT_WITH_SCOUT:
        case ENTER_INDIAN_SETTLEMENT_WITH_MISSIONARY:
        case ENTER_FOREIGN_COLONY_WITH_SCOUT:
        case ENTER_SETTLEMENT_WITH_CARRIER_AND_GOODS:
            movesLeft = 0;
            newTurn = false;
            return movesLeftBefore;
        default:
            return ILLEGAL_MOVE;
        }

        int moveCost = unit.getMoveCost(oldTile, newTile, movesLeftBefore);
        if (moveCost <= movesLeftBefore) {
            movesLeft = movesLeftBefore - moveCost;
        } else { 
            final int thisTurnMovesLeft = movesLeftBefore;
            int initialMoves = unit.getInitialMovesLeft();
            final int moveCostNextTurn = unit.getMoveCost(oldTile, newTile,
                                                          initialMoves);
            moveCost = thisTurnMovesLeft + moveCostNextTurn;
            movesLeft = initialMoves - moveCostNextTurn;
            newTurn = true;
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
