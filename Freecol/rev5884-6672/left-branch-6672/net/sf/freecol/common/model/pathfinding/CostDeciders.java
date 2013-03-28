package net.sf.freecol.common.model.pathfinding;

import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;


public final class CostDeciders {

    private static final CostDecider BASE_COST_DECIDER = new BaseCostDecider();
    private static final CostDecider AVOID_BLOCKING_UNITS_COST_DECIDER = new AvoidBlockingUnitsCostDecider();
    
    
    private static final CostDecider tileCostDecider = new CostDecider() {
        public int getCost(Unit unit, Tile oldTile, Tile newTile, int movesLeft, int turns) {
            if (unit.isNaval() 
                    && newTile.isLand()
                    && (newTile.getSettlement() == null
                        || newTile.getSettlement().getOwner().equals(unit.getOwner()))) {
                return ILLEGAL_MOVE;
            } else if (!unit.isNaval() && !newTile.isLand()){
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

    
    public static CostDecider defaultFor(final Unit unit) {
        if (unit==null || !unit.getOwner().isAI()) {
            return avoidSettlementsAndBlockingUnits();
        }
        return unit.isOffensiveUnit() ? avoidSettlements() : avoidSettlementsAndBlockingUnits();
    }

    
    
    public static CostDecider avoidSettlements() {
        return BASE_COST_DECIDER;
    }
    
    
    public static CostDecider avoidSettlementsAndBlockingUnits() {
        return AVOID_BLOCKING_UNITS_COST_DECIDER;
    }
    
    
    public static CostDecider numberOfTiles() {
        return tileCostDecider;
    }
}
