

package net.sf.freecol.server.ai.goal;

import java.io.IOException;
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

import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Unit.Role;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;

import org.w3c.dom.Element;


public class CreateMissionAtSettlementGoal extends Goal {
    private static final Logger logger = Logger.getLogger(CreateMissionAtSettlementGoal.class.getName());

    
    private IndianSettlement target;
    
    
    private GotoAdjacentGoal gotoSubGoal;

    public CreateMissionAtSettlementGoal(AIPlayer p, Goal g, float w, AIUnit u, IndianSettlement i) {
        super(p,g,w,u);
        target = i;
        gotoSubGoal = null;
    }
    
    protected Iterator<AIUnit> getOwnedAIUnitsIterator() {
        
        
        return availableUnitsList.iterator();
    }
    
    protected Iterator<Goal> getSubGoalIterator() {
        
        
        List<Goal> subGoalList = new ArrayList<Goal>();
        if (gotoSubGoal != null) {
            subGoalList.add(gotoSubGoal);
        }
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
        
        
        
        
        if (gotoSubGoal != null) {
            
            
            
            validateOwnedUnits();
            
            Iterator<AIUnit> uit = availableUnitsList.iterator();
            while (uit.hasNext()) {
                AIUnit u = uit.next();
                uit.remove();
                addUnitToParent(u);
            }
            
            if (gotoSubGoal.isFinished()) {
                
                List<AIUnit> units = gotoSubGoal.cancelGoal();
                availableUnitsList.addAll(units);
                gotoSubGoal = null;
            }
        } 
        if (gotoSubGoal == null) {
            
            
            
            validateOwnedUnits();
            
            boolean hasFoundMissionary = false;
            Iterator<AIUnit> uit = availableUnitsList.iterator();
            while (uit.hasNext()) {
                AIUnit u = uit.next();
                uit.remove();
                if (u.getUnit().getRole() != Role.MISSIONARY) {
                    
                    
                } else {
                    if (!hasFoundMissionary) {
                        hasFoundMissionary = true;
                        if (u.getUnit().getTile().isAdjacent(target.getTile())) {
                            
                            if (((IndianSettlement)target).getMissionary()==null ||
                                ((IndianSettlement)target).getMissionary().getOwner()!=player.getPlayer()) {
                                PathNode pathNode = u.getUnit().findPath(target.getTile());
                                Direction d = pathNode.getDirection();
                                u.getUnit().setMovesLeft(0);
                                                        
                                Element establishMsg = Message.createNewRootElement("missionaryAtSettlement");
                                establishMsg.setAttribute("unit", u.getUnit().getId());
                                establishMsg.setAttribute("direction", d.toString());
                                establishMsg.setAttribute("action", "establish");

                                
                                

                                try {
                                    player.getConnection().sendAndWait(establishMsg);
                                } catch (IOException e) {
                                    logger.warning("Could not send \"move\"-message!");
                                }
                            } else {
                                
                                addUnitToParent(u);
                            }
                            isFinished = true;
                        } else {
                            
                            
                            logger.info("Creating subgoal GotoAdjacentGoal.");
                            gotoSubGoal = new GotoAdjacentGoal(player,this,1,u,target.getTile());
                        }
                    } else {
                        
                        
                        
                        
                        
                        addUnitToParent(u);
                    }
                }
            }
        }
    }

    public String getGoalDescription() {
        String descr = super.getGoalDescription();
        if (target!=null) {
            descr += ":"+target.getName();
        } else {
            descr += ":null";
        }
        return descr;
    }
    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        
    }
    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        
    }
}