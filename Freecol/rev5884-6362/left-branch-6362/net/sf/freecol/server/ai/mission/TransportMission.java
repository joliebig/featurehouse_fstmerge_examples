

package net.sf.freecol.server.ai.mission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.CombatModel;
import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Locatable;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Market;
import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Player.Stance;
import net.sf.freecol.common.model.Unit.MoveType;
import net.sf.freecol.common.model.Unit.Role;
import net.sf.freecol.common.model.Unit.UnitState;
import net.sf.freecol.common.model.pathfinding.CostDeciders;
import net.sf.freecol.common.model.pathfinding.GoalDecider;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.server.ai.AIColony;
import net.sf.freecol.server.ai.AIGoods;
import net.sf.freecol.server.ai.AIMain;
import net.sf.freecol.server.ai.AIObject;
import net.sf.freecol.server.ai.AIPlayer;
import net.sf.freecol.server.ai.AIUnit;
import net.sf.freecol.server.ai.GoodsWish;
import net.sf.freecol.server.ai.Transportable;
import net.sf.freecol.server.ai.Wish;
import net.sf.freecol.server.ai.WorkerWish;

import org.w3c.dom.Element;


public class TransportMission extends Mission {

    private static final Logger logger = Logger.getLogger(TransportMission.class.getName());

    private static final String ELEMENT_TRANSPORTABLE = "transportable";

    private static final int MINIMUM_GOLD_TO_STAY_IN_EUROPE = 600;

    private ArrayList<Transportable> transportList = new ArrayList<Transportable>();

    class Destination{
    	private boolean atDestination;
    	private boolean moveToEurope;
    	private PathNode path;
    	
    	
    	public Destination(){
    		this.atDestination = true;
    		this.moveToEurope = false;
    		this.path = null;
    	}
    	
    	
    	public Destination(boolean moveToEurope,PathNode path){
    		this.atDestination = false;
    		this.moveToEurope = moveToEurope;
    		this.path = path;
    	}
    	
    	public boolean moveToEurope(){
    		return moveToEurope;
    	}
    	
    	public PathNode getPath(){
    		return path;
    	}
    	
    	public boolean isAtDestination(){
    		return atDestination;
    	}
    }

    
    public TransportMission(AIMain aiMain, AIUnit aiUnit) {
        super(aiMain, aiUnit);

        if (!getUnit().isCarrier()) {
            logger.warning("Only carriers can transport unit/goods.");
            throw new IllegalArgumentException("Only carriers can transport unit/goods.");
        }
    }

    
    public TransportMission(AIMain aiMain, Element element) {
        super(aiMain);
        readFromXMLElement(element);
    }

    
    public TransportMission(AIMain aiMain, XMLStreamReader in) throws XMLStreamException {
        super(aiMain);
        readFromXML(in);
    }

    
    private void updateTransportList() {
        Unit carrier = getUnit();

        Iterator<Unit> ui = carrier.getUnitIterator();
        while (ui.hasNext()) {
            Unit u = ui.next();
            AIUnit aiUnit = (AIUnit) getAIMain().getAIObject(u);
            if(aiUnit == null){
                logger.warning("Could not find ai unit");
                continue;
            }
            addToTransportList(aiUnit);
        }
        
        
        List<Transportable> ts = new LinkedList<Transportable>();
        for (Transportable t : new LinkedList<Transportable>(transportList)) {
            if (ts.contains(t) || isCarrying(t)) {
                if (t.getTransportDestination() == null) {
                    removeFromTransportList(t);
                }
            } else {
                if (t.getTransportSource() == null) {
                    removeFromTransportList(t);
                }
            }
            ts.add(t);
        }
    }

    
    private boolean isCarrying(Transportable t) {
        
        return t.getTransportLocatable().getLocation() == getUnit();
    }

    
    public void dispose() {
    	
    	
    	List<Transportable> cargoList = new ArrayList<Transportable>();
    	List<Transportable> scheduledCargoList = new ArrayList<Transportable>();
    	
    	Iterator<Transportable> ti = transportList.iterator();
        while (ti.hasNext()) {
            Transportable t = ti.next();
            
            if (isCarrying(t)) {
            	cargoList.add(t);
            } else {
            	
            	
            	scheduledCargoList.add(t);
            }
        }
        
        for (Transportable t : cargoList)
        	((AIObject) t).dispose();
        
        for (Transportable t : scheduledCargoList)
        	t.setTransport(null);
        
        super.dispose();
    }

    
    public boolean isOnTransportList(Transportable newTransportable) {
        for (int i = 0; i < transportList.size(); i++) {
            if (transportList.get(i) == newTransportable) {
                return true;
            }
        }
        return false;
    }

    
    public void removeFromTransportList(Transportable transportable) {
        Iterator<Transportable> ti = transportList.iterator();
        while (ti.hasNext()) {
            Transportable t = ti.next();
            if (t == transportable) {
                ti.remove();
                if (transportable.getTransport() == getAIUnit()) {
                    transportable.setTransport(null);
                }
            }
        }

    }

    
    public void addToTransportList(Transportable newTransportable) {
        Unit carrier = getUnit();
        if (newTransportable.getTransportLocatable() instanceof Unit
                && ((Unit) newTransportable.getTransportLocatable()).isCarrier()) {
            throw new IllegalArgumentException("You cannot add a carrier to the transport list.");
        }
        Location newSource = newTransportable.getTransportSource();
        Location newDestination = newTransportable.getTransportDestination();

        if (newDestination == null) {
            if (newTransportable instanceof AIGoods) {
                logger.warning("No destination for goods: " + newTransportable.getTransportLocatable().toString());
                return;
            } else {
                logger.warning("No destination for: " + newTransportable.getTransportLocatable().toString());
                return;
            }
        }

        if (newSource == null && !isCarrying(newTransportable)) {
            logger.warning("No source for: " + newTransportable.getTransportLocatable().toString());
            return;
        }

        if (isOnTransportList(newTransportable)) {
            return;
        }

        int bestSourceIndex = -1;
        if (!isCarrying(newTransportable)) {
            
            int distToSource;
            if (carrier.getLocation().getTile() == newSource.getTile()) {
                distToSource = 0;
            } else {
                distToSource = getDistanceTo(newTransportable, ((carrier.getTile() != null) ? carrier.getTile()
                        : carrier.getEntryLocation().getTile()), true);
                
                
                if(distToSource == Map.COST_INFINITY){
                    return;
                }
            }
            bestSourceIndex = 0;
            int bestSourceDistance = distToSource;
            for (int i = 1; i < transportList.size() && bestSourceDistance > 0; i++) {
                Transportable t1 = transportList.get(i - 1);
                if (t1.getTransportSource() != null && t1.getTransportSource().getTile() == newSource.getTile()
                        || t1.getTransportDestination() != null
                        && t1.getTransportDestination().getTile() == newSource.getTile()) {
                    bestSourceIndex = i;
                    bestSourceDistance = 0;
                }

            }
            
            for (int i = 1; i < transportList.size() && bestSourceDistance > 0; i++) {
                Transportable t1 = transportList.get(i - 1);
                 
                if (isCarrying(t1)){
                    int distToDestination = getDistanceTo(newTransportable, t1.getTransportDestination(), true);
                    if(distToDestination == Map.COST_INFINITY){
                        continue;
                    }
                    if(distToDestination <= bestSourceDistance) {
                         bestSourceIndex = i;
                         bestSourceDistance = distToDestination;
                    }
                } else{
                    distToSource = getDistanceTo(newTransportable, t1.getTransportSource(), true);
                    if(distToSource == Map.COST_INFINITY){
                        continue;
                    }   
                    if (distToSource <= bestSourceDistance) {
                        bestSourceIndex = i;
                        bestSourceDistance = distToSource;
                    }
                }
            }
            transportList.add(bestSourceIndex, newTransportable);
        }

        int bestDestinationIndex = bestSourceIndex + 1;
        int bestDestinationDistance = Integer.MAX_VALUE;
        if (bestSourceIndex == -1) {
            bestDestinationIndex = 0;
            if (carrier.getTile() == newSource.getTile()) {
                bestDestinationDistance = 0;
            } else {
                int distToCarrier = getDistanceTo(newTransportable, carrier.getTile(), false);
                if(distToCarrier != Map.COST_INFINITY){
                    bestDestinationDistance = distToCarrier;
                }
            }
        }
        for (int i = Math.max(bestSourceIndex, 1); i < transportList.size() && bestDestinationDistance > 0; i++) {
            Transportable t1 = transportList.get(i - 1);
            if (t1.getTransportSource().getTile() == newDestination.getTile()
                    || t1.getTransportDestination().getTile() == newDestination.getTile()) {
                bestDestinationIndex = i;
                bestDestinationDistance = 0;
            }
        }
        for (int i = Math.max(bestSourceIndex, 1); i < transportList.size() && bestDestinationDistance > 0; i++) {
            Transportable t1 = transportList.get(i - 1);
            if (isCarrying(t1)){
                int distToDestination = getDistanceTo(newTransportable, t1.getTransportDestination(), false);
                if(distToDestination == Map.COST_INFINITY){
                    continue;
                }
                if(distToDestination <= bestDestinationDistance) {
                    bestDestinationIndex = i;
                    bestDestinationDistance = distToDestination;
                }
            } else{
                int distToSource = getDistanceTo(newTransportable, t1.getTransportSource(), false); 
                if(distToSource == Map.COST_INFINITY){
                    continue;
                }
                if(distToSource <= bestDestinationDistance) {
                    bestDestinationIndex = i;
                    bestDestinationDistance =  distToSource;
                }
            }
        }
        transportList.add(bestDestinationIndex, newTransportable);

        if (newTransportable.getTransport() != getAIUnit()) {
            newTransportable.setTransport(getAIUnit());
        }
    }

    
    private int getDistanceTo(Transportable t, Location start, boolean source) {
        
        PathNode path = getPath(t, start, source);
        
        if(path == null){
            return Map.COST_INFINITY;
        }
        
        return path.getTotalTurns();
    }
    
    private boolean canAttackEnemyShips() {
        final Unit carrier = getUnit();
        return (carrier.getTile() != null)
                && carrier.isNaval()
                && carrier.isOffensiveUnit();
        
    }
    
    private boolean hasCargo() {
        final Unit carrier = getUnit();
        return (carrier.getGoodsCount() + carrier.getUnitCount()) > 0;
    }
    
    private void attackIfEnemyShipIsBlocking(Connection connection, Direction direction) {
        final Unit carrier = getUnit();
        final Map map = carrier.getGame().getMap();
        if (canAttackEnemyShips()
                && carrier.getMoveType(direction) == MoveType.ATTACK) {
            final Tile newTile = map.getNeighbourOrNull(direction, carrier.getTile());
            final Unit defender = newTile.getDefendingUnit(carrier);
            if (!canAttackPlayer(defender.getOwner())) {
                return;
            }
            attack(connection, carrier, direction);
        }
    }
    
    private void attackEnemyShips(Connection connection) {
        if (!canAttackEnemyShips()) {
            return;
        }
        final Unit carrier = getUnit();
        if (hasCargo() && !carrier.getOwner().isREF()) {
            
            return;
        }
        final PathNode pathToTarget = findNavalTarget(0);
        if (pathToTarget != null) {
            final Direction direction = moveTowards(connection, pathToTarget);
            if (direction != null &&
                    carrier.getMoveType(direction) == MoveType.ATTACK) {
                attack(connection, carrier, direction);
            }
        }
    }
    
    private boolean canAttackPlayer(Player target) {
        return (getUnit().getOwner().getStance(target) == Stance.WAR
                || getUnit().hasAbility("model.ability.piracy"));
    }
    
    
    protected PathNode findNavalTarget(final int maxTurns) {
        if (!getUnit().isOffensiveUnit()) {
            throw new IllegalStateException("A target can only be found for offensive units. You tried with: " + getUnit().getName());
        }
        if (!getUnit().isNaval()) {
            throw new IllegalStateException("A target can only be found for naval units. You tried with: " + getUnit().getName());
        }
        
        final GoalDecider gd = new GoalDecider() {
            private PathNode bestTarget = null;
            private int bestValue = 0;
            
            public PathNode getGoal() {
                return bestTarget;              
            }
            
            public boolean hasSubGoals() {
                return true;
            }
            
            public boolean check(final Unit unit, final PathNode pathNode) {
                final CombatModel combatModel = getGame().getCombatModel();
                final Tile newTile = pathNode.getTile();
                final Unit defender = newTile.getDefendingUnit(unit);
                if (newTile.isLand()
                        || defender == null
                        || defender.getOwner() == unit.getOwner()) {
                    return false;
                }
                if (!canAttackPlayer(defender.getOwner())) {
                    return false;
                }
                final int value = 1 + defender.getUnitCount() + defender.getGoodsCount();
                if (value > bestValue) {
                    bestTarget = pathNode;
                    bestValue = value;
                }
                return true;
            }
        };
        return getGame().getMap().search(getUnit(),
                getUnit().getTile(),
                gd,
                CostDeciders.avoidSettlements(),
                maxTurns);
    }
    
    
    public void doMission(Connection connection) {
    	logger.finest("Doing transport mission for unit " + getUnit() + "(" + getUnit().getId() + ")");
        if (transportList == null || transportList.size() <= 0) {
            updateTransportList();
        }

        Unit carrier = getUnit();
        if(carrier.getMovesLeft() == 0){
        	return;
        }
        if (carrier.getLocation() instanceof Europe) {
            
            if (carrier.getState() == UnitState.TO_EUROPE || carrier.getState() == UnitState.TO_AMERICA) {
                return;
            }
            
            inEurope(connection);
            return;
            
        }

        attackEnemyShips(connection);
        restockCargoAtDestination(connection);
        attackEnemyShips(connection);

        boolean transportListChanged = false;
        boolean moreWork = true;
        for (int i = 0; i < transportList.size() && moreWork || i == 0; i++) {
            if(carrier.getMovesLeft() == 0){
            	return;
            }
        	
            moreWork = false;

            if (transportListChanged) {
                i = 0;
                transportListChanged = false;
            }

            Destination destination = getNextStop();
            
            if(destination.isAtDestination()){
                transportListChanged = restockCargoAtDestination(connection);
                continue;
            }
            
            
            
            boolean canMoveToEurope = destination.moveToEurope() && carrier.canMoveToEurope();

            if(destination == null || (destination.getPath() == null && !canMoveToEurope)){
            	logger.warning("Could not get a next move for unit " + carrier);
            	carrier.setMovesLeft(0);
            	return;
            }

            
            if(canMoveToEurope){
            	moveUnitToEurope(connection, carrier);
            	return;
            }

            
            PathNode path = destination.getPath();
            boolean moveToEurope = destination.moveToEurope();
            Direction r = moveTowards(connection, path);
            if (r != null && carrier.getMoveType(r).isProgress()) {
            	
            	
            	if (carrier.getMoveType(r) == MoveType.MOVE_HIGH_SEAS && moveToEurope) {
            		moveUnitToEurope(connection, carrier);
            	} else {
            		move(connection, r);
            	}

            	if (!(carrier.getLocation() instanceof Europe)) {
            		moreWork = true;
            	}
            }
            if (r != null) {
            	attackIfEnemyShipIsBlocking(connection, r);
            }
            transportListChanged = restockCargoAtDestination(connection);
            attackEnemyShips(connection);
        }
    }

	Destination getNextStop() {
		Unit unit = getUnit();
		if(transportList.size() == 0){
			logger.finest(unit + "(" + unit.getId() + ") has nothing to transport, moving to default destination");
			return getDefaultDestination();
		}

		
		List<Location> unavailLoc = new ArrayList<Location>();
		for(Transportable transportable : transportList){
			Location destLoc = null;
			PathNode path = null;
			if(isCarrying(transportable)){
				destLoc = transportable.getTransportDestination();
			}
			else{
				destLoc = transportable.getTransportLocatable().getLocation();
			}
			
			
			if(destLoc == null || unavailLoc.contains(destLoc)){
				continue;
			}
			
			
			if(destLoc == unit.getLocation()){
				return new Destination();
			}
			
			
			if(destLoc instanceof Europe){
				path = findPathToEurope(unit.getTile());
			}
			else{
				path = getPath(transportable);
			}
			
			if(path == null){
				unavailLoc.add(destLoc);
				continue;
			}
			logger.finest("Transporting " + transportable + " to " + destLoc);
			boolean moveToEurope = destLoc instanceof Europe;
			return new Destination(moveToEurope,path);
		}
	
		
		logger.warning("None of the destinations is available moving to default destination");
		return getDefaultDestination();
	}

	
	private Destination getDefaultDestination() {
		Unit unit = getUnit();
		PathNode path = null;
		
		if(unit.isNaval() && unit.getOwner().getEurope() != null){
			
			if(unit.getLocation() instanceof Europe){
				return new Destination();
			}
			logger.finest("Trying to find move to Europe");
			boolean canMoveToEurope = unit.canMoveToEurope();
			if(!canMoveToEurope){
				path = findPathToEurope(unit.getTile());
				if(path != null){
					canMoveToEurope = true;
				}
			}
			if(canMoveToEurope){
				return new Destination(true,path);
			}
		}
		
		
		if(unit.getTile().getSettlement() != null){
			return new Destination();
		}
		
		path = findNearestColony(unit);
		if(path != null){
			return new Destination(false,path);
		}
		logger.warning("Could not get default destination for " + unit);
		return null;
	}

	
    private void buyCargo(Connection connection) {
        AIPlayer aiPlayer = (AIPlayer) getAIMain().getAIObject(getUnit().getOwner().getId());

        if (!(getUnit().getLocation() instanceof Europe)) {
            throw new IllegalStateException("Carrier not in Europe");
        }

        
        if (aiPlayer.hasFewColonies()) {
            
            Unit carrier = getUnit();
            Tile colonyTile = BuildColonyMission.findColonyLocation(carrier);
            int space = getAvailableSpace();
            while (colonyTile!=null && space > 0) {
                AIUnit newUnit = getCheapestUnitInEurope(connection);
                if (newUnit != null) {
                    if (newUnit.getUnit().isColonist() && !newUnit.getUnit().isArmed()
                        && !newUnit.getUnit().isMounted() && newUnit.getUnit().getRole() != Role.PIONEER) {
                        
                        int colonyValue = aiPlayer.getPlayer().getColonyValue(colonyTile);
                        newUnit.setMission(new BuildColonyMission(getAIMain(), newUnit, colonyTile, colonyValue));
                    }
                    addToTransportList(newUnit);
                    space--;
                } else {
                    return;
                }
            }
        }

        
        ArrayList<AIColony> aiColonies = new ArrayList<AIColony>();
        for (int i = 0; i < transportList.size(); i++) {
            Transportable t = transportList.get(i);
            if (t.getTransportDestination() != null && t.getTransportDestination().getTile() != null
                    && t.getTransportDestination().getTile().getColony() != null
                    && t.getTransportDestination().getTile().getColony().getOwner() == getUnit().getOwner()) {
                AIColony ac = (AIColony) getAIMain().getAIObject(
                        t.getTransportDestination().getTile().getColony().getId());
                aiColonies.add(ac);
            }
        }

        
        Iterator<Wish> highValueWishIterator = ((AIPlayer) getAIMain().getAIObject(getUnit().getOwner().getId()))
                .getWishIterator();
        while (highValueWishIterator.hasNext()) {
            Wish w = highValueWishIterator.next();
            if (w.getTransportable() != null) {
                continue;
            }
            if (w instanceof WorkerWish && w.getDestination() instanceof Colony) {
                WorkerWish ww = (WorkerWish) w;
                Colony c = (Colony) ww.getDestination();
                AIColony ac = (AIColony) getAIMain().getAIObject(c);
                if (!aiColonies.contains(ac)) {
                    aiColonies.add(ac);
                }
            } else if (w instanceof GoodsWish && w.getDestination() instanceof Colony) {
                GoodsWish gw = (GoodsWish) w;
                Colony c = (Colony) gw.getDestination();
                AIColony ac = (AIColony) getAIMain().getAIObject(c);
                if (!aiColonies.contains(ac)) {
                    aiColonies.add(ac);
                }
            } else {
                logger.warning("Unknown type of wish: " + w);
            }
        }
        for (int i = 0; i < aiColonies.size(); i++) {
            AIColony ac = aiColonies.get(i);
            
            
            int space = getAvailableSpace(getUnit().getType(), getUnit().getOwner().getEurope(), ac.getColony());
            Iterator<Wish> wishIterator = ac.getWishIterator();
            while (space > 0 && wishIterator.hasNext()) {
                Wish w = wishIterator.next();
                if (w.getTransportable() != null) {
                    continue;
                }
                if (w instanceof WorkerWish) {
                    WorkerWish ww = (WorkerWish) w;
                    AIUnit newUnit = getUnitInEurope(connection, ww.getUnitType());
                    if (newUnit != null) {
                        newUnit.setMission(new WishRealizationMission(getAIMain(), newUnit, ww));
                        ww.setTransportable(newUnit);
                        addToTransportList(newUnit);
                        space--;
                    }
                } else if (w instanceof GoodsWish) {
                    GoodsWish gw = (GoodsWish) w;
                    AIGoods ag = buyGoodsInEurope(connection, gw.getGoodsType(), 100, gw.getDestination());
                    if (ag != null) {
                        gw.setTransportable(ag);
                        addToTransportList(ag);
                        space--;
                    }
                } else {
                    logger.warning("Unknown type of wish: " + w);
                }
            }
        }

        
        int space = getAvailableSpace();
        while (space > 0) {
            AIUnit newUnit = getCheapestUnitInEurope(connection);
            if (newUnit != null) {
                addToTransportList(newUnit);
                space--;
            } else {
                break;
            }
        }
    }

    
    public AIGoods buyGoodsInEurope(Connection connection, GoodsType type, int amount, Location destination) {
        AIPlayer aiPlayer = (AIPlayer) getAIMain().getAIObject(getUnit().getOwner().getId());
        Player player = aiPlayer.getPlayer();
        Market market = player.getMarket();

        if (player.getGold() >= market.getBidPrice(type, amount)) {
            boolean success = buyGoods(connection, getUnit(), type, amount);
            if(!success){
                return null;
            }
            AIGoods ag = new AIGoods(getAIMain(), getUnit(), type, amount, destination);
            return ag;
        } else {
            return null;
        }
    }

    
    private AIUnit getUnitInEurope(Connection connection, UnitType unitType) {
        AIPlayer aiPlayer = (AIPlayer) getAIMain().getAIObject(getUnit().getOwner().getId());
        Player player = aiPlayer.getPlayer();
        Europe europe = player.getEurope();

        if (!(getUnit().getLocation() instanceof Europe)) {
            throw new IllegalStateException("Carrier not in Europe");
        }

        
        Iterator<Unit> ui = europe.getUnitIterator();
        while (ui.hasNext()) {
            Unit u = ui.next();
            if (unitType == null || unitType == u.getType()) {
                return (AIUnit) getAIMain().getAIObject(u.getId());
            }
        }

        int price = -1;
        if (unitType.hasPrice() && europe.getUnitPrice(unitType) >= 0) {
            price = europe.getUnitPrice(unitType);
        }
        
        
        if (player.getGold() >= player.getRecruitPrice()
            && price > player.getRecruitPrice()) {
            for (int i = 0; i < 3; i++) {
                
                if (europe.getRecruitable(i) == unitType) {
                    return aiPlayer.recruitAIUnitInEurope(i);
                }
            }
        }

        
        if (price > 0 && player.getGold() >= price) {
            return aiPlayer.trainAIUnitInEurope(unitType);
        }

        return null;
    }

    
    private AIUnit getCheapestUnitInEurope(Connection connection) {
        AIPlayer aiPlayer = (AIPlayer) getAIMain().getAIObject(getUnit().getOwner().getId());
        Player player = aiPlayer.getPlayer();
        Europe europe = player.getEurope();

        if (!(getUnit().getLocation() instanceof Europe)) {
            throw new IllegalStateException("Carrier not in Europe");
        }
        if (!player.canRecruitUnits()) {
            return null;
        }

        
        Iterator<Unit> ui = europe.getUnitIterator();
        while (ui.hasNext()) {
            Unit u = ui.next();
            if (!u.isCarrier() && ((AIUnit) getAIMain().getAIObject(u)).getTransport() == null) {
                return (AIUnit) getAIMain().getAIObject(u.getId());
            }
        }

        int priceTrained = 0;
        UnitType cheapestTrained = null;
        List<UnitType> unitTypes = FreeCol.getSpecification().getUnitTypesTrainedInEurope();
        for (UnitType unitType : unitTypes) {
            int price = europe.getUnitPrice(unitType);
            if (cheapestTrained == null || price < priceTrained) {
            	cheapestTrained = unitType;
            	priceTrained = price;
            }
        }
        
        if (player.getGold() >= player.getRecruitPrice() && cheapestTrained != null
                && player.getRecruitPrice() < priceTrained) {
            
            return aiPlayer.recruitAIUnitInEurope(1);
        }

        
        if (cheapestTrained != null && player.getGold() >= priceTrained) {
            return aiPlayer.trainAIUnitInEurope(cheapestTrained);
        }

        return null;
    }

    
    public PathNode getPath(Transportable transportable) {
        return getPath(transportable, getUnit().getTile(), !isCarrying(transportable));
    }

    
    private PathNode getPath(Transportable transportable, Location start, boolean source) {
        Unit carrier = getUnit();

        if (isCarrying(transportable) && source) {
            throw new IllegalStateException(
                    "Cannot find the path to the source while the transportable is on the carrier.");
        }

        PathNode path;
        Locatable locatable = transportable.getTransportLocatable();

        if (start == null || start.getTile() == null) {
            start = getUnit().getEntryLocation();
        }

        Location destination;
        if (source) {
            destination = locatable.getLocation();
        } else {
            destination = transportable.getTransportDestination();
        }

        if (destination == null) {
            return null;
        }

        if (destination instanceof Europe) {
            path = findPathToEurope(start.getTile());
        } else if (locatable instanceof Unit && isCarrying(transportable)) {
            path = getGame().getMap().findPath((Unit) locatable, start.getTile(), destination.getTile(), carrier);
            if (path == null || path.getTransportDropNode().previous == null) {
                path = null;
            } else {
                path.getTransportDropNode().previous.next = null;
            }
        } else {
            path = getGame().getMap().findPath(carrier, start.getTile(), destination.getTile());
        }

        return path;
    }

    
    public int getAvailableSpace(Transportable t) {
        if (t.getTransportLocatable() instanceof Unit) {
            Unit u = (Unit) t.getTransportLocatable();
            return getAvailableSpace(u.getType(), t.getTransportSource(), t.getTransportDestination());
        } else {
            return getAvailableSpace(null, t.getTransportSource(), t.getTransportDestination());
        }
    }

    
    public int getAvailableSpace(UnitType unitType, Location source, Location destination) {
        
        return Math.max(0, getUnit().getSpaceLeft() - transportList.size());
    }

    
    public int getAvailableSpace() {
        
        return Math.max(0, getUnit().getSpaceLeft() - transportList.size());
    }

    
    private boolean restockCargoAtDestination(Connection connection) {
        return unloadCargoAtDestination(connection) | loadCargoAtDestination(connection);
    }

    
    private boolean unloadCargoAtDestination(Connection connection) {
        Unit carrier = getUnit();

        boolean transportListChanged = false;

        
        if(carrier.getLocation() instanceof Europe &&
        		(carrier.getState() == UnitState.TO_EUROPE 
        				|| carrier.getState() != UnitState.TO_AMERICA)){
        	return false;
        }
        
        
        for (Transportable t : new ArrayList<Transportable>(transportList)) {
            
        	if (!isCarrying(t)) {
                continue;
            }
            if (t instanceof AIUnit) {
                AIUnit au = (AIUnit) t;
                Unit u = au.getUnit();
                Mission mission = au.getMission();
                
                
                
                
                
                
                if(mission == null || au.getTransportDestination() == null){
                	if(carrier.getLocation() instanceof Europe ||
                	    carrier.getTile().getSettlement() != null){
                		logger.warning("Unloading unit without mission or destination");
                		unitLeavesShip(connection, u);
                		continue;
                	}
                }
                
                if (mission != null && mission.isValid()) {
                    if (au.getTransportDestination() != null
                            && au.getTransportDestination().getTile() == carrier.getTile()) {
                        if (carrier.getLocation() instanceof Europe || u.getColony() != null) {
                            unitLeavesShip(connection, u);
                        }
                        mission.doMission(connection);
                        if (u.getLocation() != getUnit()) {
                            removeFromTransportList(au);
                            transportListChanged = true;
                        }
                    } else if (!(carrier.getLocation() instanceof Europe) && au.getTransportDestination() != null
                            && au.getTransportDestination().getTile() != null) {
                        PathNode p = getGame().getMap().findPath(u, carrier.getTile(),
                                au.getTransportDestination().getTile(), carrier);
                        if (p != null) {
                            final PathNode dropNode = p.getTransportDropNode();
                            int distToCarrier = dropNode.getTile().getDistanceTo(carrier.getTile());
                            if (dropNode != null &&
                                    distToCarrier != Map.COST_INFINITY &&
                                    distToCarrier <= 1) {
                                mission.doMission(connection);
                                if (u.getLocation() != getUnit()) {
                                    removeFromTransportList(au);
                                    transportListChanged = true;
                                }    
                            }
                        }
                        
                        
                    }
                }
            } else if (t instanceof AIGoods) {
                AIGoods ag = (AIGoods) t;
                if (ag.getTransportDestination() == null ||
                		(ag.getTransportDestination() != null
                				&& ag.getTransportDestination().getTile() == carrier.getLocation().getTile())) {
                	logger.finest("Unloading goods at " + carrier.getLocationName());
                    if (carrier.getLocation() instanceof Europe) {
                        boolean success = sellCargoInEurope(connection, carrier, ag.getGoods());
                        if(success){
                            removeFromTransportList(ag);
                            ag.dispose();
                            transportListChanged = true;
                        }
                    } else {
                        boolean success = unloadCargoInColony(connection, carrier, ag.getGoods());
                        if(success){
                            removeFromTransportList(ag);
                            ag.dispose();
                            transportListChanged = true;
                        }
                    }
                }
            } else {
                logger.warning("Unknown Transportable.");
            }
        }

        return transportListChanged;
    }

    
    private boolean loadCargoAtDestination(Connection connection) {
        Unit carrier = getUnit();

        

        boolean transportListChanged = false;

        Iterator<Transportable> tli = transportList.iterator();
        while (tli.hasNext()) {
            Transportable t = tli.next();
            if (isCarrying(t)) {
                continue;
            }
            if (t instanceof AIUnit) {
                AIUnit au = (AIUnit) t;
                Unit u = au.getUnit();
                if (u.getTile() == carrier.getTile() && carrier.getState() != UnitState.TO_EUROPE
                        && carrier.getState() != UnitState.TO_AMERICA) {
                    Element boardShipElement = Message.createNewRootElement("boardShip");
                    boardShipElement.setAttribute("unit", u.getId());
                    boardShipElement.setAttribute("carrier", carrier.getId());
                    try {
                        connection.sendAndWait(boardShipElement);
                        tli.remove();
                        transportListChanged = true;
                    } catch (IOException e) {
                        logger.warning("Could not send \"boardShipElement\"-message!");
                    }
                }
            } else if (t instanceof AIGoods) {
                AIGoods ag = (AIGoods) t;
                if (ag.getGoods().getTile() == carrier.getTile() && carrier.getState() != UnitState.TO_EUROPE
                        && carrier.getState() != UnitState.TO_AMERICA) {
                    if (carrier.getLocation() instanceof Europe) {
                        GoodsType goodsType = ag.getGoods().getType();
                        int goodsAmount = ag.getGoods().getAmount();
                        boolean success = buyGoods(connection, carrier, goodsType, goodsAmount);
                        if(success){
                            tli.remove();
                            transportListChanged = true;
                            ag.setGoods(new Goods(getGame(), carrier, goodsType, goodsAmount));
                        }
                    } else {
                        Element loadCargoElement = Message.createNewRootElement("loadCargo");
                        loadCargoElement.setAttribute("carrier", carrier.getId());
                        loadCargoElement.appendChild(ag.getGoods().toXMLElement(carrier.getOwner(),
                                loadCargoElement.getOwnerDocument()));

                        try {
                            connection.sendAndWait(loadCargoElement);
                            tli.remove();
                            transportListChanged = true;
                        } catch (IOException e) {
                            logger.warning("Could not send \"loadCargoElement\"-message!");
                        }
                        ag.setGoods(new Goods(getGame(), carrier, ag.getGoods().getType(), ag.getGoods().getAmount()));
                    }
                }
            } else {
                logger.warning("Unknown Transportable.");
            }
        }

        return transportListChanged;
    }

        
    public static boolean isValid(AIUnit aiUnit) {
        Unit unit = aiUnit.getUnit();
        
        if(!unit.isNaval()){
        	return true;
        }
        
        if(unit.isUnderRepair()){
        	return false;
        }

        boolean hasCargo = unit.getGoodsCount() > 0 || unit.getUnitCount() > 0;
        if(hasCargo){
        	return true;
        }
    
        
        if(unit.hasAbility("model.ability.piracy")){
            AIPlayer aiPlayer = (AIPlayer) aiUnit.getAIMain().getAIObject(unit.getOwner().getId());
        	
        	int transportMissions = getPlayerNavalTransportMissionCount(aiPlayer,unit);
        	if(transportMissions > 0){
        		
        		
        		logger.finest("Privateer (" + unit.getId() + ") at " + unit.getTile() + " does no longer have TransportMission");
        		return false;
        	}
        }
        
        return true;
    }
    
    
    public boolean isValid() {
        if(!super.isValid()){
            return false;
        }
        
        AIUnit aiUnit = getAIUnit(); 
        if(!isValid(aiUnit)){
            return false;
        }
        return true;
    }

    
    public Tile getTransportDestination() {
        return null;
    }

    
    public int getTransportPriority() {
        return 0;
    }

    
    private void inEurope(Connection connection){
        restockCargoAtDestination(connection);
        buyCargo(connection);
        restockCargoAtDestination(connection);

        
        Unit carrier = getUnit();
        if (carrier.getOwner().getGold() < MINIMUM_GOLD_TO_STAY_IN_EUROPE || transportList.size() > 0) {
            moveUnitToAmerica(connection, carrier);
        }        
    }
    
    
    protected PathNode findPathToEurope(Tile start) {
        return getGame().getMap().findPathToEurope(getUnit(), start);
    }
    
    
    public static int getPlayerNavalTransportMissionCount(AIPlayer aiPlayer, Unit unitExcluded){
        Player player = aiPlayer.getPlayer();
        int units = 0;
        
        for(Unit unit : player.getUnits()){
        	if(unit == unitExcluded){
        		continue;
        	}
            if(!unit.isNaval()){
                continue;
            }
            AIUnit aiUnit = (AIUnit) aiPlayer.getAIMain().getAIObject(unit);
            if(aiUnit.getMission() instanceof TransportMission){
                units++;
            }
        }
        return units;
    }

    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("unit", getUnit().getId());

        Iterator<Transportable> tli = transportList.iterator();
        while (tli.hasNext()) {
            Transportable t = tli.next();
            out.writeStartElement(ELEMENT_TRANSPORTABLE);
            out.writeAttribute("ID", ((AIObject) t).getId());
            out.writeEndElement();
        }
        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setAIUnit((AIUnit) getAIMain().getAIObject(in.getAttributeValue(null, "unit")));

        transportList.clear();

        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if (in.getLocalName().equals(ELEMENT_TRANSPORTABLE)) {
                String tid = in.getAttributeValue(null, "ID");
                AIObject ao = getAIMain().getAIObject(tid);
                if (ao == null) {
                    if (tid.startsWith(Unit.getXMLElementTagName())) {
                        ao = new AIUnit(getAIMain(), tid);
                    } else {
                        ao = new AIGoods(getAIMain(), tid);
                    }
                }
                if (!(ao instanceof Transportable)) {
                    logger.warning("AIObject not Transportable, ID: " + in.getAttributeValue(null, "ID"));
                } else {
                    transportList.add((Transportable) ao);
                }
                in.nextTag();
            } else {
                logger.warning("Unknown tag.");
            }
        }
    }

    
    public static String getXMLElementTagName() {
        return "transportMission";
    }
    
    
    public String getDebuggingInfo() {
        Unit carrier = getUnit();
        return this.toString();
    }

    
    public String toString() {
        StringBuilder sb = new StringBuilder("Transport list:\n");
        List<Transportable> ts = new LinkedList<Transportable>();
        for(Transportable t : transportList) {
            Locatable l = t.getTransportLocatable();
            sb.append(l.toString());
            sb.append(" (");
            Location target; 
            if (ts.contains(t) || isCarrying(t)) {
                sb.append("to ");
                target = t.getTransportDestination();
            } else {
                sb.append("from ");
                target = t.getTransportSource();
            }
            if (target instanceof Europe) {
                sb.append("Europe");
            } else if (target == null) {
                sb.append("null");
            } else {
                sb.append(target.getTile().getPosition());
            }
            sb.append(")");
            sb.append("\n");
            ts.add(t);
        }
        return sb.toString();
    }
}
