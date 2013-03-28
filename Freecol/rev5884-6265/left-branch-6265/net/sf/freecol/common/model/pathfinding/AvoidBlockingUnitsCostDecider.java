


package net.sf.freecol.common.model.pathfinding;

import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;


class AvoidBlockingUnitsCostDecider extends BaseCostDecider {
    
    
    @Override
    public int getCost(final Unit unit,
            final Tile oldTile,
            final Tile newTile,
            final int movesLeftBefore,
            final int turns) {
        final int normalCost = super.getCost(unit, oldTile, newTile, movesLeftBefore, turns);
        if (normalCost == ILLEGAL_MOVE
                || normalCost == Map.COST_INFINITY) {
            return normalCost;
        }
        
        int extraCost = 0;
        final Unit defender = newTile.getFirstUnit();
        if (defender != null && defender.getOwner() != unit.getOwner()) {
            if (turns == 0) {
                return ILLEGAL_MOVE;
            } else {
                extraCost += Math.max(0, 20 - turns * 4);
            }
        } else if (newTile.isLand()
                && newTile.getFirstUnit() != null
                && newTile.getFirstUnit().isNaval()
                && newTile.getFirstUnit().getOwner() != unit.getOwner()) {
            
            extraCost += Math.max(0, 20 - turns * 4); 
        }
        return normalCost + extraCost;
    }
}    
