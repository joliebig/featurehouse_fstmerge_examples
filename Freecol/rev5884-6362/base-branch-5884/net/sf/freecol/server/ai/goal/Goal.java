

 

package net.sf.freecol.server.ai.goal;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.UnitType;

import net.sf.freecol.server.ai.AIMain;
import net.sf.freecol.server.ai.AIObject;
import net.sf.freecol.server.ai.AIPlayer;
import net.sf.freecol.server.ai.AIUnit;

import net.sf.freecol.server.ai.goal.GoalConstants;


public abstract class Goal extends AIObject implements GoalConstants {
    private static final Logger logger = Logger.getLogger(Goal.class.getName());

    private float relativeWeight;
    private int turnCreated;
    private int turnLastUnitAdded;
    protected boolean needsPlanning;
    protected boolean isFinished;
    protected List<AIUnit> availableUnitsList;
    
    protected AIPlayer player;
    private Goal parentGoal;
    
                               
    public Goal(AIPlayer p, Goal g, float w) {
        super(p.getAIMain());
        player = p;
        parentGoal = g;
        relativeWeight = w;
        turnCreated = getGame().getTurn().getNumber();
        needsPlanning = true; 
        isFinished = false; 
        availableUnitsList = new ArrayList<AIUnit>();
    }

     
    public Goal (AIPlayer p, Goal g, float w, AIUnit u) {
        this(p,g,w);
        addUnit(u);
    }

    
    public boolean isFinished() {
        return isFinished;
    }

    
    public List<AIUnit> cancelGoal() {
        logger.finest("Entering method cancelGoal() for "+getDebugDescription());
        List<AIUnit> cancelledUnitsList = new ArrayList<AIUnit>();
        
        
        Iterator<Goal> git = getSubGoalIterator();
        while (git!=null && git.hasNext()) {
            Goal g = git.next();
            List<AIUnit> ulist = g.cancelGoal();
            cancelledUnitsList.addAll(ulist);
        }
        
        
        Iterator<AIUnit> uit = getOwnedAIUnitsIterator();
        while (uit.hasNext()) {
            AIUnit u = uit.next();
            cancelledUnitsList.add(u);
        }
        logger.info("Got "+cancelledUnitsList.size()+" units from cancelled subgoals");
        return cancelledUnitsList;
    }

                  
    public void doPlanning() {
        logger.finest("Entering method doPlanning() for "+getDebugDescription());
        boolean subgoalsPlanned = false;
        
        normalizeSubGoalWeights();
        
        Iterator<Goal> git = getSubGoalIterator();
        while (git!=null && git.hasNext()) {
            Goal g = git.next();
            if (g.needsPlanning()) {
                g.doPlanning();
                subgoalsPlanned = true;
            }
        }
        
        
        if (needsPlanning || subgoalsPlanned) {
            plan();
            needsPlanning = false;
        }
    }

    
    public boolean needsPlanning() {
        logger.finest("Entering method needsPlanning() for "+getDebugDescription());
        if (needsPlanning) {
            return true;
        } else {
            Iterator<Goal> git = getSubGoalIterator();
            while (git!=null && git.hasNext()) {
                Goal g = git.next();
                if (g.needsPlanning()) {
                    return true;
                }
            }
        }
        return false;
    }

    
    public void setNeedsPlanningRecursive(boolean p) {
        logger.finest("Entering method setNeedsPlanningRecursive() for "+getDebugDescription());
        needsPlanning = p;
        
        Iterator<Goal> git = getSubGoalIterator();
        while (git!=null && git.hasNext()) {
            Goal g = git.next();
            g.setNeedsPlanningRecursive(p);
        }
    }

    
    public float getWeight() {
        return relativeWeight;
    }

             
    public float getParentWeight() {
        if (parentGoal == null) {
            
            return 1.0f;
        } else {
            return parentGoal.getAbsoluteWeight();
        }
    }

                  
    public float getAbsoluteWeight() {
        return getParentWeight() * relativeWeight;
    }

                   
    public void setWeight(float w) {
        relativeWeight = w;
    }

         
    public void normalizeSubGoalWeights() {
        float sumWeights = 0f;
        
        Iterator<Goal> git = getSubGoalIterator();
        while (git!=null && git.hasNext()) {
            Goal g = git.next();
            sumWeights += g.getWeight();
        }
        
        
        if (sumWeights>0f && (sumWeights<0.95f || sumWeights>1.05f)) {
            git = getSubGoalIterator();
            while (git!=null && git.hasNext()) {
                Goal g = git.next();
                g.setWeight(g.getWeight()/sumWeights);
            }
        }
    }



























    
    protected void requestWorker(GoodsType gt, int minProduction) {
        int turnsWithoutUnit = getGame().getTurn().getNumber() - turnLastUnitAdded;
        
        
        
    }

    
    public void addUnit(AIUnit u) {
        logger.finest("Entering method addUnit() for "+getDebugDescription()+" with unit: "+u.getId());
        turnLastUnitAdded = getGame().getTurn().getNumber();
        availableUnitsList.add(u);
        u.setGoal(this);
        needsPlanning = true; 
        isFinished = false; 
    }

                       
    protected void addUnitToParent(AIUnit u) {
        logger.finest("Entering method addUnitToParent() for "+getDebugDescription()+" with unit: "+u.getId());
        if (parentGoal != null) {
            parentGoal.addUnit(u);
        } else {
            
            
            u.setGoal(null);
        }
    }

    
    public boolean canYieldUnit(UnitType ut, AIObject o) {
        Iterator<AIUnit> uit = getOwnedAIUnitsIterator();
        while (uit.hasNext()) {
            AIUnit u = uit.next();
            
            if (u.getUnit().getType().equals(ut)) {
                return true;
            }
        }
        
        Iterator<Goal> git = getSubGoalIterator();
        while (git!=null && git.hasNext()) {
            Goal g = git.next();
            if (g.canYieldUnit(ut, o)) {
                return true;
            }
        }
        
        return false;
    }
    
    
    public float getYieldedUnitWeight(UnitType ut, AIObject o) {
        
        
        float unitWeight = 99f;
        
        Iterator<AIUnit> uit = getOwnedAIUnitsIterator();
        while (uit.hasNext()) {
            AIUnit u = uit.next();
            
            if (u.getUnit().getType().equals(ut)) {
                unitWeight = getAbsoluteWeight();
            }
        }
        
        Iterator<Goal> git = getSubGoalIterator();
        while (git!=null && git.hasNext()) {
            Goal g = git.next();
            float newWeight = g.getYieldedUnitWeight(ut, o);
            if (newWeight < unitWeight) {
                unitWeight = newWeight;
            }
        }
        return unitWeight;
    }
     
    
    public AIUnit yieldUnit(UnitType ut, AIObject o) {
        float unitWeight = 99f;
        AIUnit yieldedUnit = null;
        boolean isOwnUnit = false;

        
        Iterator<AIUnit> uit = getOwnedAIUnitsIterator();
        while (uit.hasNext()) {
            AIUnit u = uit.next();
            
            if (u.getUnit().getType().equals(ut)) {
                unitWeight = getAbsoluteWeight();
                yieldedUnit = u;
                isOwnUnit = true;
            }
        }
        
        Iterator<Goal> git = getSubGoalIterator();
        while (git!=null && git.hasNext()) {
            Goal g = git.next();
            float newWeight = g.getYieldedUnitWeight(ut, o);
            if (newWeight < unitWeight) {
                unitWeight = newWeight;
                yieldedUnit = g.yieldUnit(ut, o);
                isOwnUnit = false;
            }
        }
        if (isOwnUnit) {
            removeUnit(yieldedUnit);
            needsPlanning = true;
        }
        return yieldedUnit;    
    }

              
    protected void validateOwnedUnits() {
        Iterator<AIUnit> uit = getOwnedAIUnitsIterator();
        while (uit.hasNext()) {
            AIUnit u = uit.next();
            if (!(u.getGoal()==this)) {
                logger.warning("Goal "+getGoalDescription()+" owns unit with another goal: "+u.getGoal().getGoalDescription());
                removeUnit(u);
            }
            
            
            
            
        }    
    }

     
    public String getGoalDescription() {
        String goalName = getClass().toString();
        goalName = goalName.substring(goalName.lastIndexOf('.') + 1,goalName.length()-4);
        return goalName;
    }

        
    public String getDebugDescription() {
        String descr = "";
        
        
        
        if (parentGoal!=null) {
            descr = parentGoal.getGoalDescription() + ">>";
        }
        descr += getGoalDescription();
        return descr;
    }

    
    public static String getXMLElementTagName() {
        return "aiGoal";
    }




              
    protected abstract Iterator<AIUnit> getOwnedAIUnitsIterator();
    
    
    protected abstract Iterator<Goal> getSubGoalIterator();

         
    protected abstract void removeUnit(AIUnit u);

         
    protected abstract void plan();

    
    protected abstract void toXMLImpl(XMLStreamWriter out) throws XMLStreamException;

    
    protected abstract void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException;




}