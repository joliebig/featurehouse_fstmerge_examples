

package net.sf.freecol.server.ai.goal;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.server.ai.AIMain;
import net.sf.freecol.server.ai.AIPlayer;
import net.sf.freecol.server.ai.AIUnit;

import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Map.Position;
import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit.Role;

       
public class ManageMissionariesGoal extends Goal {
    private static final Logger logger = Logger.getLogger(ManageMissionariesGoal.class.getName());

    
    private List<Goal> subGoalList;

    public ManageMissionariesGoal(AIPlayer p, Goal g, float w) {
        super(p,g,w);
        subGoalList = new ArrayList<Goal>();
    }

    protected Iterator<AIUnit> getOwnedAIUnitsIterator() {
        
        
        return availableUnitsList.iterator();
    }

    protected Iterator<Goal> getSubGoalIterator() {
        
        return subGoalList.iterator();
    }
    
    protected void removeUnit(AIUnit u) {
        Iterator<AIUnit> uit = availableUnitsList.iterator();
        while (uit.hasNext()) {
            AIUnit unit = uit.next();
            if (unit.equals(u)) {
                uit.remove();
            }
        }
    }
    
             
    protected void plan() {
        isFinished = false;
        
        
        
        Iterator<Goal> git = subGoalList.iterator();
        while (git.hasNext()) {
            Goal g = git.next();
            if (g.isFinished()) {
                List<AIUnit> units = g.cancelGoal();
                availableUnitsList.addAll(units);
                git.remove();
            }
        }
        
        
        
        validateOwnedUnits();
        
        
        
        Iterator<AIUnit> uit = availableUnitsList.iterator();
        while (uit.hasNext()) {
            AIUnit u = uit.next();
            uit.remove();
            
            if (u.getUnit().getRole() == Role.MISSIONARY) {
                IndianSettlement i = findSettlement(u.getUnit().getTile());
                if (i != null) {
                    PathNode pathNode = u.getUnit().findPath(i.getTile());
                    if (pathNode != null) {
                        logger.info("Creating subgoal CreateMissionAtSettlementGoal.");
                        CreateMissionAtSettlementGoal g = new CreateMissionAtSettlementGoal(player,this,1,u,i);
                        subGoalList.add(g);
                    }
                }
            } else {
                
                
                u.setGoal(null);
            }
        }

        if (availableUnitsList.size()==0 && subGoalList.size()==0) {
            
            
            isFinished = true;
        } else {
            
            float newWeight = 1f/subGoalList.size();
            git = subGoalList.iterator();
            while (git.hasNext()) {
                Goal g = git.next();
                g.setWeight(newWeight);
            }
        }
    }
    
    public String getGoalDescription() {
        String descr = super.getGoalDescription();
        descr += ":"+availableUnitsList.size();
        return descr;
    }
    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        
    }
    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        
    }





    private IndianSettlement findSettlement(Tile t) {
        if (t==null) {
            
            return null;
        } else {
            
            
            
            Iterator<Position> i = player.getGame().getMap().getCircleIterator(t.getPosition(), true, MAX_SEARCH_RADIUS);
            while (i.hasNext()) {
                Position pos = i.next();
                Settlement s = player.getGame().getMap().getTile(pos).getSettlement();
                if (s instanceof IndianSettlement &&
                    (((IndianSettlement)s).getMissionary()==null ||
                    ((IndianSettlement)s).getMissionary().getOwner()!=player.getPlayer())) {
                        
                        return (IndianSettlement)s;
                }
            }
        }
        
        return null;
    }
}