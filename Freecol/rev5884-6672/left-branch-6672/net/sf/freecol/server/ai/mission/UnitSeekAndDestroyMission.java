


package net.sf.freecol.server.ai.mission;

import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Ownable;
import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tension;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Player.Stance;
import net.sf.freecol.common.model.Unit.MoveType;
import net.sf.freecol.common.model.pathfinding.GoalDecider;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.server.ai.AIMain;
import net.sf.freecol.server.ai.AIObject;
import net.sf.freecol.server.ai.AIUnit;

import org.w3c.dom.Element;



public class UnitSeekAndDestroyMission extends Mission {
    private static final Logger logger = Logger.getLogger(UnitSeekAndDestroyMission.class.getName());
    
    
    private Location target;

    
    public UnitSeekAndDestroyMission(AIMain aiMain, AIUnit aiUnit, Location target) {
        super(aiMain, aiUnit);
        this.target = target; 
        
        if (!(target instanceof Ownable)) {
            logger.warning("!(target instanceof Ownable)");
            throw new IllegalArgumentException("!(target instanceof Ownable)");
        }        
        if (!(target instanceof Unit || target instanceof Settlement)) {
            logger.warning("!(target instanceof Unit || target instanceof Settlement)");
            throw new IllegalArgumentException("!(target instanceof Unit || target instanceof Settlement)");
            
        }
    }


    
    public UnitSeekAndDestroyMission(AIMain aiMain, Element element) {
        super(aiMain);
        readFromXMLElement(element);
    }
    
    
    public UnitSeekAndDestroyMission(AIMain aiMain, XMLStreamReader in) throws XMLStreamException {
        super(aiMain);
        readFromXML(in);
    }


    
    @Override
	public void doMission(Connection connection) {
        Unit unit = getUnit();

        if (!isValid()) {
            return;
        }
        
        PathNode pathToTarget = null;
        if (unit.isOnCarrier()) {
            if (unit.getTile() != null) {
                pathToTarget = getDisembarkPath(unit, unit.getTile(), target.getTile(), (Unit) unit.getLocation());
                if (pathToTarget.getTransportDropNode() != pathToTarget) {
                    pathToTarget = null;
                }
            }
        } else {
            if (unit.getTile() != null) {
                pathToTarget = getUnit().findPath(target.getTile());
            }
        }
        
        if (pathToTarget != null) {
            Direction direction = moveTowards(connection, pathToTarget);
            if (direction != null 
                && unit.getMoveType(direction) == MoveType.ATTACK) {
                Tile newTile = getGame().getMap().getNeighbourOrNull(direction, unit.getTile());
                Unit defender = newTile.getDefendingUnit(unit);
                if (defender == null) {
                    logger.warning("MoveType is ATTACK, but no defender is present!");
                } else {
                    Player enemy = defender.getOwner();
                    if (unit.getOwner().getStance(enemy) == Stance.WAR
                        || ((Ownable) target).getOwner() == enemy) {
                        attack(connection, unit, direction);
                    }
                }
            }
        }
    }

    
    private PathNode getDisembarkPath(Unit unit, Tile start, final Tile end, Unit carrier) {
        GoalDecider gd = new GoalDecider() {
            private PathNode goal = null;
            
            public PathNode getGoal() {
                return goal;
            }
            
            public boolean hasSubGoals() {
                return false;
            }
            
            public boolean check(Unit u, PathNode pathNode) {
                goal = pathNode;
                if (pathNode.getTile().getSettlement() == null) {
                    for (Direction direction : Direction.values()) {
                        Tile attackTile = u.getGame().getMap().getNeighbourOrNull(direction, pathNode.getTile());
                        if (end == attackTile 
                                && attackTile.getSettlement() != null 
                                && pathNode.getTile().isLand()) {
                            int cost = pathNode.getCost();
                            int movesLeft = pathNode.getMovesLeft();
                            int turns = pathNode.getTurns();
                            goal = new PathNode(attackTile, cost, cost, direction, movesLeft, turns);
                            goal.previous = pathNode;
                            return true;
                        }                        
                    }           
                }
                return pathNode.getTile() == end;
            }
        };
        return getGame().getMap().search(unit, start, gd, 
                Integer.MAX_VALUE, carrier);    
    }
    
    
    @Override
	public boolean isValid() {
        Player owner = getUnit().getOwner();
        Player targetPlayer;
        if (target == null) {
            return false;
        }     
        if (((FreeColGameObject) target).isDisposed()) {
            return false;
        }
        if (target.getTile() == null) {
            return false;
        }
        if (!getUnit().isOffensiveUnit()) {
            return false;
        }
        
        
        if (target instanceof Unit && 
            target.getTile().getSettlement() != null){
        		return false;
        }

        targetPlayer = ((Ownable) target).getOwner();
        Stance stance = owner.getStance(targetPlayer);

        return targetPlayer != owner &&
            (stance == Stance.WAR 
             || owner.isIndian() 
             && owner.getTension(targetPlayer).getLevel().compareTo(Tension.Level.CONTENT) >= 0);
    }

    
        
    @Override
	public Tile getTransportDestination() {
        if (target == null) {
            return null;
        }
        
        Tile dropTarget = target.getTile();
        if (getUnit().getTile() == null) {
            return dropTarget;
        } else if (getUnit().isOnCarrier()) {
            PathNode p = getDisembarkPath(getUnit(), 
                    getUnit().getTile(), 
                    target.getTile(), 
                    (Unit) getUnit().getLocation());
            if (p != null) {
                dropTarget = p.getTransportDropNode().getTile();
            }
        }
        
        if (getUnit().isOnCarrier()) {
            return dropTarget;
        } else if (getUnit().getLocation().getTile() == target) {
            return null;
        } else if (getUnit().findPath(target.getTile()) == null) {
            return dropTarget;
        } else {
            return null;
        }
    }
    
    
    
    @Override
	public int getTransportPriority() {
        if (getTransportDestination() != null) {
            return NORMAL_TRANSPORT_PRIORITY;
        } else {
            return 0;
        }
    }    

    
        
    public Location getTarget() {
        return target;
    }    

    
    
    public void setTarget(Location target) {
        this.target = target;
    }

    
    @Override
	protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
        
        out.writeAttribute("unit", getUnit().getId());
        out.writeAttribute("target", getTarget().getId());

        out.writeEndElement();
    }

    
    @Override
	protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setAIUnit((AIUnit) getAIMain().getAIObject(in.getAttributeValue(null, "unit")));        
        setTarget((Location) getGame().getFreeColGameObject(in.getAttributeValue(null, "target")));
        in.nextTag();
    }


    
    public static String getXMLElementTagName() {
        return "unitSeekAndDestroyMission";
    }
    
    
    @Override
	public String getDebuggingInfo() {
        if (target == null) {
            return "No target";
        } else {
            final String name;
            if (target instanceof Unit) {
                name = ((Unit) target).toString();
            } else if (target instanceof Colony) {
                name = ((Colony) target).getName();
            } else {
                name = "";
            }
            return target.getTile().getPosition() + " " + name;
        }
    }
}
