


package net.sf.freecol.server.ai.mission;

import java.io.IOException;
import java.util.logging.Logger;

import net.sf.freecol.common.model.CombatModel;
import net.sf.freecol.common.model.pathfinding.CostDeciders;
import net.sf.freecol.common.model.pathfinding.GoalDecider;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Tension;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Unit.MoveType;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.BuyGoodsMessage;
import net.sf.freecol.common.networking.DisembarkMessage;
import net.sf.freecol.common.networking.SellGoodsMessage;
import net.sf.freecol.common.networking.UnloadCargoMessage;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.server.ai.AIMain;
import net.sf.freecol.server.ai.AIObject;
import net.sf.freecol.server.ai.AIUnit;

import org.w3c.dom.Element;



public abstract class Mission extends AIObject {

    private static final Logger logger = Logger.getLogger(Mission.class.getName());

    protected static final int MINIMUM_TRANSPORT_PRIORITY = 60,     
                               NORMAL_TRANSPORT_PRIORITY = 100;     

    protected static final int NO_PATH_TO_TARGET = -2,
                               NO_MORE_MOVES_LEFT = -1;

    private AIUnit aiUnit;


    
    public Mission(AIMain aiMain) {
        this(aiMain, null);
    }
    

    
    public Mission(AIMain aiMain, AIUnit aiUnit) {
        super(aiMain);
        this.aiUnit = aiUnit;   
    }

    
    
    protected Direction moveTowards(Connection connection, Tile tile) {
        PathNode pathNode = getUnit().findPath(tile);
        
        if (pathNode != null) {
            return moveTowards(connection, pathNode);
        } else {
            return null;
        }
    }


    
    protected Direction moveTowards(Connection connection, PathNode pathNode) {
        if (getUnit().getMovesLeft() <= 0) {            
            return null;
        }
        
        while (pathNode.next != null 
               && pathNode.getTurns() == 0
               && this.isValid() == true
               && getUnit().getMoveType(pathNode.getDirection()).isProgress()) {
            move(connection, pathNode.getDirection());         
            pathNode = pathNode.next;
        }
        if (pathNode.getTurns() == 0 && getUnit().getMoveType(pathNode.getDirection()).isLegal()) {
            return pathNode.getDirection();
        }
        return null;
    }

    protected void moveRandomly(Connection connection) {
        Tile thisTile = getUnit().getTile();
        Unit unit = getUnit();
        Direction[] randomDirections = unit.getGame().getMap().getRandomDirectionArray();
        while (unit.getMovesLeft() > 0) {
            Direction direction = Direction.N;
            int j;
            for (j = 0; j < randomDirections.length; j++) {
                direction = randomDirections[j];
                if (unit.getGame().getMap().getNeighbourOrNull(direction, thisTile) != null &&
                    unit.getMoveType(direction) == MoveType.MOVE) {
                    break;
                }
            }
            if (j == randomDirections.length){
                unit.setMovesLeft(0);
                break;
            }
            thisTile = unit.getGame().getMap().getNeighbourOrNull(direction, thisTile);

            move(connection, direction);
        }
    }

    
    protected void move(Connection connection, Direction direction) {
        Element moveElement = Message.createNewRootElement("move");
        moveElement.setAttribute("unit", getUnit().getId());
        moveElement.setAttribute("direction", direction.toString());

        try {
            connection.sendAndWait(moveElement);
        } catch (IOException e) {
            logger.warning("Could not send \"move\"-message!");
        }
    }
    
    protected void moveUnitToAmerica(Connection connection, Unit unit) {
        Element moveToAmericaElement = Message.createNewRootElement("moveToAmerica");
        moveToAmericaElement.setAttribute("unit", unit.getId());
        try {
            connection.sendAndWait(moveToAmericaElement);
        } catch (IOException e) {
            logger.warning("Could not send \"moveToAmericaElement\"-message!");
        }
    }
    
    protected void moveUnitToEurope(Connection connection, Unit unit) {
        Element moveToAmericaElement = Message.createNewRootElement("moveToEurope");
        moveToAmericaElement.setAttribute("unit", unit.getId());
        try {
            connection.sendAndWait(moveToAmericaElement);
        } catch (IOException e) {
            logger.warning("Could not send \"moveToAmericaElement\"-message!");
        }
    }
    
    
    protected void moveButDontAttack(Connection connection, Direction direction) {
        if (direction != null) {
            if (getUnit().getMoveType(direction).isProgress()) {
                move(connection, direction);
            }
        }
    }
    
    
    protected void exploreLostCityRumour(Connection connection) {
        if (getUnit().getTile().hasLostCityRumour()) {           
            Element exploreElement = Message.createNewRootElement("explore");
            exploreElement.setAttribute("unit", getUnit().getId());       
            try {
                connection.ask(exploreElement);
            } catch (IOException e) {
                logger.warning("Could not send \"explore\"-message!");
            }
        }
    }
    
    
    protected PathNode findTarget(int maxTurns) {
        if (!getUnit().isOffensiveUnit()) {
            throw new IllegalStateException("A target can only be found for offensive units. You tried with: "
                                            + getUnit().toString());
        }
        
        GoalDecider gd = new GoalDecider() {
            private PathNode bestTarget = null;
            private int higherTension = 0;
            
            public PathNode getGoal() {
                return bestTarget;              
            }
            
            public boolean hasSubGoals() {
                return true;
            }
            
            public boolean check(Unit unit, PathNode pathNode) {
                CombatModel combatModel = getGame().getCombatModel();
                Tile newTile = pathNode.getTile();
                Unit defender = newTile.getDefendingUnit(unit);
                
                if( defender == null){
                    return false;
                }

                if( defender.getOwner() == unit.getOwner()){
                    return false;
                }

                if (newTile.isLand() && unit.isNaval() || !newTile.isLand() && !unit.isNaval()) {
                    return false;
                }

                int tension = 0;
                Tension alarm = unit.getOwner().getTension(defender.getOwner());
                if (alarm != null) {
                    tension = alarm.getValue();
                }

                if (unit.getIndianSettlement() != null &&
                        unit.getIndianSettlement().getAlarm(defender.getOwner()) != null) {
                    tension += unit.getIndianSettlement().getAlarm(defender.getOwner()).getValue();
                }
                if (defender.canCarryTreasure()) {
                    tension += Math.min(defender.getTreasureAmount() / 10, 600);
                }
                if (defender.getType().getDefence() > 0 &&
                        newTile.getSettlement() == null) {
                    tension += 100 - combatModel.getDefencePower(unit, defender) * 2;
                }
                if (defender.hasAbility("model.ability.expertSoldier") &&
                        !defender.isArmed()) {
                    tension += 50 - combatModel.getDefencePower(unit, defender) * 2;
                }
                if (unit.hasAbility("model.ability.piracy")){
                    tension += PrivateerMission.getModifierValueForTarget(combatModel, unit, defender);
                }
                
                if (unit.getOwner().isIndian() 
                        && defender.getOwner().isAI()) {
                    tension -= 200;
                }
                
                if (tension > Tension.Level.CONTENT.getLimit()) {
                    if (bestTarget == null) {
                        bestTarget = pathNode;
                        higherTension = tension;
                        return true;
                    } else if (bestTarget.getTurns() == pathNode.getTurns()
                            && tension > higherTension) {
                        bestTarget = pathNode;
                        higherTension = tension;
                        return true;
                    }
                }
                return false;
            }
        };
        return getGame().getMap().search(getUnit(), getUnit().getTile(), gd,
                CostDeciders.avoidIllegal(), maxTurns);
    }
    
   
    
    public Tile getTransportDestination() {
        if (getUnit().getTile() == null) {
            if (getUnit().isOnCarrier()) {
                return (Tile) ((Unit) getUnit().getLocation()).getEntryLocation();
            } else {
                return (Tile) getUnit().getOwner().getEntryLocation();
            }
        } else if (!getUnit().isOnCarrier()) {
            return null;
        }
        
        Unit carrier = (Unit) getUnit().getLocation();
        
        if (carrier.getTile().getSettlement() != null) {
            return carrier.getTile();
        }        
        
        GoalDecider gd = new GoalDecider() {
            private PathNode bestTarget = null;
            
            public PathNode getGoal() {
                return bestTarget;              
            }
            
            public boolean hasSubGoals() {
                return false;
            }
            
            public boolean check(Unit unit, PathNode pathNode) {
                Tile newTile = pathNode.getTile();
                boolean hasOurSettlement = (newTile.getSettlement() != null) 
                        && newTile.getSettlement().getOwner() == unit.getOwner();
                if (hasOurSettlement) {
                    bestTarget = pathNode;
                }
                return hasOurSettlement;
            }
        };
        PathNode path = getGame().getMap().search(carrier, carrier.getTile(),
                gd, CostDeciders.avoidSettlementsAndBlockingUnits(),
                Integer.MAX_VALUE);
        if (path != null) {
            return path.getLastNode().getTile();
        } else {
            return null;
        }
    }
    
    
    
    public int getTransportPriority() {
        if (getTransportDestination() != null) {
            return NORMAL_TRANSPORT_PRIORITY;
        } else {
            return 0;
        }
    }
    
    protected boolean unloadCargoInColony(Connection connection, Unit carrier, Goods goods) {
        UnloadCargoMessage message = new UnloadCargoMessage(goods);
        try {
            connection.sendAndWait(message.toXMLElement());
        } catch (IOException e) {
            logger.warning("Could not send \"" + message.getXMLElementTagName()
                           + "\"-message!");
            return false;
        }
        return true;
    }

    protected boolean sellCargoInEurope(Connection connection, Unit carrier, Goods goods) {
        
        Player p = carrier.getOwner();
        if (p.isAI() && getAIMain().getFreeColServer().isSingleplayer()) {
            
            p.modifyGold(p.getMarket().getSalePrice(goods));
        }
        
        SellGoodsMessage message = new SellGoodsMessage(goods, carrier);
        try {
            connection.sendAndWait(message.toXMLElement());
        } catch (IOException e) {
            logger.warning("Could not send \"" + message.getXMLElementTagName()
                           + "\"-message!");
            return false;
        }
        return true;
    }
    
    
    public void dispose() {
        
    }
    

    
    public abstract void doMission(Connection connection);


    
    public boolean isValid() {
        if (getUnit() != null && getUnit().isDisposed()) {
            
            
            return false;
        }
        return true;
    }


    
    public Unit getUnit() {
        return aiUnit.getUnit();
    }


    
    public AIUnit getAIUnit() {
        return aiUnit;
    }
    
    
        
    protected void setAIUnit(AIUnit aiUnit) {
        this.aiUnit = aiUnit;
    }
    
    
    public String getDebuggingInfo() {
        return "";
    }


    public void attack(Connection connection, Unit unit, Direction direction) {
        assert direction != null;
        Element element = Message.createNewRootElement("attack");
        element.setAttribute("unit", unit.getId());
        element.setAttribute("direction", direction.toString());
    
        try {
            connection.ask(element);
        } catch (IOException e) {
            logger.warning("Could not send message!");
        }
    }
    
    
    protected boolean unitLeavesShip(Connection connection, Unit unit) {
        DisembarkMessage message = new DisembarkMessage(unit);
        try {
            connection.sendAndWait(message.toXMLElement());
        } catch (IOException e) {
            logger.warning("Could not send \"" + message.getXMLElementTagName()
                           + "\"-message to the server!");
            return false;
        }
        return true;
    }
    
    public boolean buyGoods(Connection connection, Unit carrier,
                            GoodsType goodsType, int amount) {
        BuyGoodsMessage message = new BuyGoodsMessage(carrier, goodsType,
                                                      amount);
        try {
            connection.sendAndWait(message.toXMLElement());
        } catch (IOException e) {
            logger.warning("Could not send \"" + message.getXMLElementTagName()
                           + "\"-message to the server.");
            return false;
        }
        return true;
    }
    
    public PathNode findNearestColony(Unit unit){
        Player player = unit.getOwner();
        PathNode nearestColony = null;
        int distToColony = Integer.MAX_VALUE;
        
        
        for(Colony colony : player.getColonies()){
            PathNode path = unit.findPath(colony.getTile());
            if(path == null){
                continue;
            }
            int dist = path.getTotalTurns();
            
            if(dist <= 1){
                nearestColony = path;
                break;
            }
            
            if(dist < distToColony){
                nearestColony = path;
                distToColony = dist; 
            }
        }       
        
        return nearestColony;
    }
}
