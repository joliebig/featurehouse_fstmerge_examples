

package net.sf.freecol.common.model.pathfinding;

import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Unit;



public final class CostDeciders {

    
    private static final CostDecider tileCostDecider
        = new CostDecider() {
            public int getCost(Unit unit, Tile oldTile, Tile newTile,
                               int movesLeft, int turns) {
                if (unit.isNaval()) {
                    if (!newTile.isLand()) return 1;
                    Settlement settlement = newTile.getSettlement();
                    return (settlement != null
                            && settlement.getOwner().equals(unit.getOwner())) ? 1
                        : ILLEGAL_MOVE;
                } else {
                    return (newTile.isLand()) ? 1 : ILLEGAL_MOVE;
                }
            }
            public int getMovesLeft() {
                return 0;
            }
            public boolean isNewTurn() {
                return false;
            }
        };


    
    private static final CostDecider
        avoidIllegalCostDecider = new BaseCostDecider();


    
    private static class AvoidSettlementsCostDecider
        extends BaseCostDecider {
        @Override
        public int getCost(Unit unit, Tile oldTile, Tile newTile,
                           int movesLeft, int turns) {
            int cost = super.getCost(unit, oldTile, newTile,
                                     movesLeft, turns);
            if (cost != ILLEGAL_MOVE && cost != Map.COST_INFINITY) {
                Settlement settlement = newTile.getSettlement();
                if (settlement != null
                    && settlement.getOwner() != unit.getOwner()) {
                    return ILLEGAL_MOVE;
                }
            }
            return cost;
        }
    };
    private static final AvoidSettlementsCostDecider
        avoidSettlementsCostDecider = new AvoidSettlementsCostDecider();


    
    private static class AvoidSettlementsAndBlockingUnitsCostDecider
        extends BaseCostDecider {
        @Override
            public int getCost(Unit unit, Tile oldTile, Tile newTile,
                               int movesLeft, int turns) {
            int cost = super.getCost(unit, oldTile, newTile,
                                     movesLeft, turns);
            if (cost != ILLEGAL_MOVE && cost != Map.COST_INFINITY) {
                Settlement settlement = newTile.getSettlement();
                if (settlement != null
                    && settlement.getOwner() != unit.getOwner()) {
                    return ILLEGAL_MOVE;
                }
                final Unit defender = newTile.getFirstUnit();
                if (defender != null
                    && defender.getOwner() != unit.getOwner()) {
                    if (turns == 0) {
                        return ILLEGAL_MOVE;
                    }
                    cost += Math.max(0, 20 - turns * 4);
                } else if (newTile.isLand()
                           && newTile.getFirstUnit() != null
                           && newTile.getFirstUnit().isNaval()
                           && newTile.getFirstUnit().getOwner() != unit.getOwner()) {
                    
                    
                    cost += Math.max(0, 20 - turns * 4);
                }
            }
            return cost;
        }
    };
    private static final AvoidSettlementsAndBlockingUnitsCostDecider
        avoidSettlementsAndBlockingUnitsCostDecider
        = new AvoidSettlementsAndBlockingUnitsCostDecider();


    
    public static CostDecider defaultFor(final Unit unit) {
        return (unit == null
                || !unit.getOwner().isAI()
                || !unit.isOffensiveUnit())
            ? avoidSettlementsAndBlockingUnits()
            : avoidSettlements();
    }

    
    public static CostDecider numberOfTiles() {
        return tileCostDecider;
    }

    
    public static CostDecider avoidIllegal() {
        return avoidIllegalCostDecider;
    }

    
    public static CostDecider avoidSettlements() {
        return avoidSettlementsCostDecider;
    }
    
    
    public static CostDecider avoidSettlementsAndBlockingUnits() {
        return avoidSettlementsAndBlockingUnitsCostDecider;
    }

}
