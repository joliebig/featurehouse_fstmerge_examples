


package net.sf.freecol.common.model.pathfinding;

import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;



public interface GoalDecider {



    
    public PathNode getGoal();
    
    
    public boolean hasSubGoals();
    
    
    public boolean check(Unit u, PathNode pathNode);
}
