

package net.sf.freecol.server.ai;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.logging.Logger;

import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.ColonyTile;
import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Ownable;
import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Player.Stance;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tension;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TileImprovement;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.UnitState;
import net.sf.freecol.common.model.pathfinding.GoalDecider;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.model.Map.Position;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.common.networking.NetworkConstants;
import net.sf.freecol.server.ai.mission.DefendSettlementMission;
import net.sf.freecol.server.ai.mission.Mission;
import net.sf.freecol.server.ai.mission.TransportMission;
import net.sf.freecol.server.ai.mission.UnitSeekAndDestroyMission;
import net.sf.freecol.server.ai.mission.UnitWanderHostileMission;
import net.sf.freecol.server.ai.mission.WorkInsideColonyMission;

import org.w3c.dom.Element;


public abstract class EuropeanAIPlayer extends NewAIPlayer {

    private static final Logger logger = Logger.getLogger(EuropeanAIPlayer.class.getName());

    
    public boolean hasManOfWar() {
        Iterator<Unit> it = getPlayer().getUnitIterator();
        while (it.hasNext()) {
            Unit unit = it.next();
            if ("model.unit.manOWar".equals(unit.getType().getId())) {
                return true;
            }
        }
        return false;
    }

    
    public AIUnit trainAIUnitInEurope(UnitType unitType) {

        if (unitType==null) {
            throw new IllegalArgumentException("Invalid UnitType.");
        }

        AIUnit unit = null;
        try {
            Element trainUnitInEuropeElement = Message.createNewRootElement("trainUnitInEurope");
            trainUnitInEuropeElement.setAttribute("unitType", unitType.getId());
            Element reply = this.getConnection().ask(trainUnitInEuropeElement);
            if (reply!=null && reply.getTagName().equals("trainUnitInEuropeConfirmed")) {
                Element unitElement = (Element) reply.getChildNodes().item(0);
                String unitID = unitElement.getAttribute("ID");
                unit = (AIUnit) getAIMain().getAIObject(unitID);
                if (unit==null) {
                    logger.warning("Could not train the specified AI unit "+unitType.getId()+" in europe.");
                }
            } else {
                logger.warning("Could not train the specified AI unit "+unitType.getId()+" in europe.");
            }
        } catch (IOException e) {
            logger.warning("Could not send \"trainUnitInEurope\"-message to the server.");
        }
        return unit;
    }


    
    public AIUnit recruitAIUnitInEurope(int slot) {

        AIUnit unit = null;
        Element recruitUnitInEuropeElement = Message.createNewRootElement("recruitUnitInEurope");
        recruitUnitInEuropeElement.setAttribute("slot", Integer.toString(slot));
        try {
            Element reply = this.getConnection().ask(recruitUnitInEuropeElement);
            if (reply!=null && reply.getTagName().equals("recruitUnitInEuropeConfirmed")) {
                Element unitElement = (Element) reply.getChildNodes().item(0);
                String unitID = unitElement.getAttribute("ID");
                unit = (AIUnit) getAIMain().getAIObject(unitID);
                if (unit==null) {
                    logger.warning("Could not recruit the specified AI unit in europe");
                }
                return unit;
            } else {
                logger.warning("Could not recruit the specified AI unit in europe.");
            }
        } catch (IOException e) {
            logger.warning("Could not send \"recruitUnitInEurope\"-message to the server.");
        }
        return unit;
    }

    
    protected void ensureCorrectMissions() {
        logger.finest("Entering method ensureCorrectMissions");
        Iterator<AIUnit> it = getAIUnitIterator();
        while (it.hasNext()) {
            AIUnit au = it.next();
            if (!au.hasMission()
                && (au.getUnit().getLocation() instanceof ColonyTile || au.getUnit().getLocation() instanceof Building)) {
                AIColony ac = (AIColony) getAIMain().getAIObject(au.getUnit().getColony());
                au.setMission(new WorkInsideColonyMission(getAIMain(), au, ac));
            }
        }
    }


    
    protected void giveNavalMissions() {
        logger.finest("Entering method giveNavalMissions");
        if (!getPlayer().isEuropean()) {
            return;
        }
        Iterator<AIUnit> aiUnitsIterator = getAIUnitIterator();
        while (aiUnitsIterator.hasNext()) {
            AIUnit aiUnit = aiUnitsIterator.next();
            if (aiUnit.getUnit().isNaval() && !aiUnit.hasMission()) {
                
                aiUnit.setMission(new TransportMission(getAIMain(), aiUnit));
            }
        }
    }

    
    protected void rearrangeWorkersInColonies() {
        logger.finest("Entering method rearrangeWorkersInColonies");
        if (!getPlayer().isEuropean()) {
            return;
        }
        Iterator<AIColony> ci = getAIColonyIterator();
        while (ci.hasNext()) {
            AIColony c = ci.next();
            ArrayList<Tile> oldWorkTiles = new ArrayList<Tile>();
            for (ColonyTile colonyTile : c.getColony().getColonyTiles()) {
                if (colonyTile.getUnit() != null) {
                    oldWorkTiles.add(colonyTile.getWorkTile());
                }
            }

            c.rearrangeWorkers(getConnection());

            ArrayList<Tile> tilesToUpdate = new ArrayList<Tile>();
            for (ColonyTile colonyTile : c.getColony().getColonyTiles()) {
                boolean isOccupied = colonyTile.getUnit() != null;
                boolean wasOccupied = oldWorkTiles.remove(colonyTile.getWorkTile());
                if (isOccupied != wasOccupied) {
                    tilesToUpdate.add(colonyTile.getWorkTile());
                }
            }
            sendUpdatedTilesToAll(tilesToUpdate);
        }
    }

    
    protected void secureSettlements() {
        logger.finest("Entering method secureSettlements");
        
        
        
    }


    
    protected void secureColony(Colony colony) {
        Map map = getPlayer().getGame().getMap();
        int olddefenders = 0;
        int defenders = 0;
        int threat = 0;
        int worstThreat = 0;
        Location bestTarget = null;
        Iterator<Unit> ui = colony.getTile().getUnitIterator();
        while (ui.hasNext()) {
            if ((ui.next()).isDefensiveUnit()) {
                defenders++;
            }
        }
        Iterator<Position> positionIterator = map.getCircleIterator(colony.getTile().getPosition(), true, 5);
        while (positionIterator.hasNext()) {
            Tile t = map.getTile(positionIterator.next());
            if (t.getFirstUnit() != null) {
                if (t.getFirstUnit().getOwner() == getPlayer()) {
                    Iterator<Unit> uit = t.getUnitIterator();
                    while (uit.hasNext()) {
                        if (uit.next().isOffensiveUnit()) {
                            defenders++;
                        }
                    }
                } else {
                    int thisThreat = 0;
                    if (getPlayer().getTension(t.getFirstUnit().getOwner()).getValue() >= Tension.TENSION_ADD_MAJOR) {
                        Iterator<Unit> uit = t.getUnitIterator();
                        while (uit.hasNext()) {
                            if (uit.next().isOffensiveUnit()) {
                                thisThreat += 2;
                            }
                        }
                    } else if (getPlayer().getTension(t.getFirstUnit().getOwner()).getValue() >= Tension.TENSION_ADD_MINOR) {
                        Iterator<Unit> uit = t.getUnitIterator();
                        while (uit.hasNext()) {
                            if (uit.next().isOffensiveUnit()) {
                                thisThreat++;
                            }
                        }
                    }
                    threat += thisThreat;
                    if (thisThreat > worstThreat) {
                        if (t.getSettlement() != null) {
                            bestTarget = t.getSettlement();
                        } else {
                            bestTarget = t.getFirstUnit();
                        }
                        worstThreat = thisThreat;
                    }
                }
            }
        }
        olddefenders = defenders;
        if (colony.hasStockade()) {
            defenders += (defenders * (colony.getStockade().getLevel()) / 2);
        }
        if (threat > defenders) {
            
            ArrayList<Unit> recruits = new ArrayList<Unit>();
            ArrayList<Unit> others = new ArrayList<Unit>();
            int inColonyCount = 0;
            
            
            ui = colony.getUnitIterator();
            while (ui.hasNext()) {
                Unit u = (ui.next());
                if (u.isOffensiveUnit()) {
                    continue; 
                    
                }
                if (u.getLocation() != colony.getTile()) {
                    
                    inColonyCount++;
                }
                if (u.hasAbility("model.ability.expertSoldier")) {
                    recruits.add(u);
                } else if (u.hasAbility("model.ability.canBeEquipped")) {
                    others.add(u);
                }
            }
            
            Collections.sort(others, Unit.getSkillLevelComparator());
            recruits.addAll(others);
            
            int recruitCount = threat - defenders;
            if (recruitCount > recruits.size() - 1) {
                recruitCount = recruits.size() - 1;
            }
            if (recruitCount > inColonyCount - 1) {
                recruitCount = inColonyCount - 1;
            }
            
            boolean needMuskets = false;
            boolean needHorses = false;
            ui = recruits.iterator();
            while (ui.hasNext() && recruitCount > 0) {
                Unit u = (ui.next());
                if (!u.isArmed() && u.canBeEquippedWith(muskets)) {
                    recruitCount--;
                    Element equipUnitElement = Message.createNewRootElement("equipUnit");
                    equipUnitElement.setAttribute("unit", u.getId());
                    equipUnitElement.setAttribute("type", muskets.getId());
                    equipUnitElement.setAttribute("amount", "1");
                    u.equipWith(muskets);
                    sendAndWaitSafely(equipUnitElement);
                    Element putOutsideColonyElement = Message.createNewRootElement("putOutsideColony");
                    putOutsideColonyElement.setAttribute("unit", u.getId());
                    u.putOutsideColony();
                    sendAndWaitSafely(putOutsideColonyElement);
                    
                    if (u.checkSetState(UnitState.FORTIFYING)) {
                        Element changeStateElement = Message.createNewRootElement("changeState");
                        changeStateElement.setAttribute("unit", u.getId());
                        changeStateElement.setAttribute("state", UnitState.FORTIFYING.toString());
                        sendAndWaitSafely(changeStateElement);
                    }
                    olddefenders++;
                    if (!u.isMounted() && u.canBeEquippedWith(horses)) {
                        equipUnitElement = Message.createNewRootElement("equipUnit");
                        equipUnitElement.setAttribute("unit", u.getId());
                        equipUnitElement.setAttribute("type", horses.getId());
                        equipUnitElement.setAttribute("amount", "1");
                        sendAndWaitSafely(equipUnitElement);
                    } else {
                        needHorses = true;
                    }
                } else {
                    needMuskets = true;
                    break;
                }
            }
            AIColony ac = null;
            if (needMuskets || needHorses) {
                Iterator<AIColony> aIterator = getAIColonyIterator();
                while (aIterator.hasNext()) {
                    AIColony temp = aIterator.next();
                    if (temp != null && temp.getColony() == colony) {
                        ac = temp;
                        break;
                    }
                }
            }
            if (needMuskets && ac != null) {
                
                
                Iterator<Wish> wishes = ac.getWishIterator();
                boolean made = false;
                while (wishes.hasNext()) {
                    Wish w = wishes.next();
                    if (!(w instanceof GoodsWish)) {
                        continue;
                    }
                    GoodsWish gw = (GoodsWish) w;
                    if (gw.getGoodsType() == Goods.MUSKETS) {
                        made = true;
                    }
                }
                if (made == false) {
                    
                    ac
                        .addGoodsWish(new GoodsWish(getAIMain(), colony, (threat - olddefenders) * 50,
                                                    Goods.MUSKETS));
                }
            }
            if (needHorses && ac != null) {
                
                
                Iterator<Wish> wishes = ac.getWishIterator();
                boolean made = false;
                while (wishes.hasNext()) {
                    Wish w = wishes.next();
                    if (!(w instanceof GoodsWish)) {
                        continue;
                    }
                    GoodsWish gw = (GoodsWish) w;
                    if (gw.getGoodsType() == Goods.HORSES) {
                        made = true;
                    }
                }
                if (made == false) {
                    
                    ac.addGoodsWish(new GoodsWish(getAIMain(), colony, (threat - defenders) * 50, Goods.HORSES));
                }
            }
            defenders = olddefenders;
            if (colony.hasStockade()) {
                defenders += (defenders * (colony.getStockade().getLevel()) / 2);
            }
        }
        if (defenders > (threat * 2)) {
            
            
            Unit u = null;
            Iterator<Unit> uit = colony.getUnitIterator();
            while (uit.hasNext()) {
                Unit candidate = uit.next();
                if (candidate.isOffensiveUnit() && candidate.getState() == UnitState.FORTIFIED) {
                    u = candidate;
                    break;
                }
            }
            if (u != null) {
                u.setState(UnitState.ACTIVE);
                u.setLocation(colony.getTile());
                AIUnit newDefenderAI = (AIUnit) getAIMain().getAIObject(u);
                if (bestTarget != null) {
                    newDefenderAI.setMission(new UnitSeekAndDestroyMission(getAIMain(), newDefenderAI, bestTarget));
                } else {
                    newDefenderAI.setMission(new UnitWanderHostileMission(getAIMain(), newDefenderAI));
                }
            }
        }
    }

    
    protected void createAIGoodsInColonies() {
        logger.finest("Entering method createAIGoodsInColonies");
        Iterator<AIColony> ci = getAIColonyIterator();
        while (ci.hasNext()) {
            AIColony c = ci.next();
            c.createAIGoods();
        }
    }

    int getDefendColonyMissionValue(Unit u, Colony colony, int turns) {
        logger.finest("Entering method getDefendColonyMissionValue");

        
        if(colony == null)
            return Integer.MIN_VALUE;

        
        int value = 10025 - turns;
        int numberOfDefendingUnits = 0;
        
        Iterator<AIUnit> aui = getAIUnitIterator();
        while (aui.hasNext()) {
            Mission m = aui.next().getMission();
            if (m != null && m instanceof DefendSettlementMission) {
                if (((DefendSettlementMission) m).getSettlement() == colony) {
                    
                    value -= 6;
                    numberOfDefendingUnits++;
                }
            }
        }
        if (u.getOwner().isREF()) {
            value -= 19;
            if (numberOfDefendingUnits > 0) {
                return 0;
            }
        }
        
        
        if (colony.getStockade() != null &&
            numberOfDefendingUnits > colony.getStockade().getLevel() + 1) {
            return Math.max(0, value - 9000);
        }
        return value;
    }

    
    void giveMilitaryMission(AIUnit aiUnit) {
        logger.finest("Entering method giveMilitaryMission");
        
        if (getPlayer().isIndian()) {
            aiUnit.setMission(new UnitWanderHostileMission(getAIMain(), aiUnit));
            return;
        }
        Unit unit = aiUnit.getUnit();
        Unit carrier = (unit.isOnCarrier()) ? (Unit) unit.getLocation() : null;
        Map map = unit.getGame().getMap();
        
        Ownable bestTarget = null; 
        int bestValue = Integer.MIN_VALUE; 
        
        Tile startTile = unit.getTile();
        if (startTile == null) {
            if (unit.isOnCarrier()) {
                startTile = (Tile) ((Unit) unit.getLocation()).getEntryLocation();
            } else {
                startTile = (Tile) unit.getOwner().getEntryLocation();
            }
        }
        
        if (unit.getColony() != null) {
            bestTarget = unit.getColony();
            bestValue = getDefendColonyMissionValue(unit, (Colony) bestTarget, 0);
        }
        
        GoalDecider gd = new GoalDecider() {
                private PathNode best = null;

                private int bestValue = Integer.MIN_VALUE;


                public PathNode getGoal() {
                    return best;
                }

                public boolean hasSubGoals() {
                    return true;
                }

                public boolean check(Unit u, PathNode pathNode) {
                    Tile t = pathNode.getTile();
                    if (t.getColony() != null && t.getColony().getOwner() == u.getOwner()) {
                        int value = getDefendColonyMissionValue(u, t.getColony(), pathNode.getTurns());
                        if (value > 0 && value > bestValue) {
                            bestValue = value;
                            best = pathNode;
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            };
        final int MAXIMUM_DISTANCE_TO_SETTLEMENT = 10; 
        
        PathNode bestPath = map.search(unit, startTile, gd,
                                       MAXIMUM_DISTANCE_TO_SETTLEMENT, carrier);
        if (bestPath != null) {
            PathNode ln = bestPath.getLastNode();
            int value = getDefendColonyMissionValue(unit, ln.getTile().getColony(), ln.getTurns());
            if (value > bestValue) {
                bestTarget = ln.getTile().getColony();
                bestValue = value;
            }
        }
        
        
        Location bestExistingTarget = null;
        int smallestDifference = Integer.MAX_VALUE;
        Iterator<AIUnit> aui = getAIUnitIterator();
        while (aui.hasNext() && smallestDifference>0) {
            AIUnit coAIUnit = aui.next();
            Unit coUnit = coAIUnit.getUnit();
            if (coUnit.getTile() != null && coAIUnit.getMission() instanceof UnitSeekAndDestroyMission) {
                Location target = ((UnitSeekAndDestroyMission) coAIUnit.getMission()).getTarget();
                int ourDistance = unit.getTurnsToReach(startTile, target.getTile());
                int coUnitDistance = coUnit.getTurnsToReach(target.getTile());
                if (ourDistance != Integer.MAX_VALUE) {
                    int difference = Math.abs(ourDistance - coUnitDistance);
                    if (difference < smallestDifference) {
                        smallestDifference = difference;
                        bestExistingTarget = target;
                    }
                }
            }
        }
        if (bestExistingTarget != null) {
            int value = getUnitSeekAndDestroyMissionValue(unit, bestExistingTarget.getTile(), smallestDifference);
            if (value > bestValue) {
                bestValue = value;
                bestTarget = (Ownable) bestExistingTarget;
            }
        }
        
        GoalDecider targetDecider = new GoalDecider() {
                private PathNode bestTarget = null;

                private int bestNewTargetValue = Integer.MIN_VALUE;


                public PathNode getGoal() {
                    return bestTarget;
                }

                public boolean hasSubGoals() {
                    return true;
                }

                public boolean check(Unit unit, PathNode pathNode) {
                    Tile newTile = pathNode.getTile();
                    Unit defender = newTile.getDefendingUnit(unit);
                    if (isTargetValidForSeekAndDestroy(unit,defender)) {
                        int value = getUnitSeekAndDestroyMissionValue(unit, pathNode.getTile(), pathNode.getTurns());
                        if (value > bestNewTargetValue) {
                            bestTarget = pathNode;
                            bestNewTargetValue = value;
                            return true;
                        }
                    }
                    return false;
                }
            };
        PathNode newTarget = map.search(unit, startTile, targetDecider, Integer.MAX_VALUE,
                                        carrier);
        if (newTarget != null) {
            Tile targetTile = newTarget.getLastNode().getTile();
            int value = getUnitSeekAndDestroyMissionValue(unit, targetTile, newTarget.getTotalTurns());
            if (value > bestValue) {
                bestValue = value;
                if (targetTile.getSettlement() != null) {
                    bestTarget = targetTile.getSettlement();
                } else if (getBestTreasureTrain(targetTile) != null) {
                    bestTarget = getBestTreasureTrain(targetTile);
                } else {
                    bestTarget = targetTile.getDefendingUnit(unit);
                }
            }
        }
        
        if (bestTarget != null && bestValue > 0) {
            if (bestTarget.getOwner() == unit.getOwner()) {
                aiUnit.setMission(new DefendSettlementMission(getAIMain(), aiUnit, (Colony) bestTarget));
            } else {
                aiUnit.setMission(new UnitSeekAndDestroyMission(getAIMain(), aiUnit, (Location) bestTarget));
            }
        } else {
            
            aiUnit.setMission(new UnitWanderHostileMission(getAIMain(), aiUnit));
        }
    }

    
    public Iterator<TileImprovementPlan> getTileImprovementPlanIterator() {
        ArrayList<TileImprovementPlan> tileImprovements = new ArrayList<TileImprovementPlan>();
        Iterator<AIColony> acIterator = getAIColonyIterator();
        while (acIterator.hasNext()) {
            AIColony ac = acIterator.next();
            Iterator<TileImprovementPlan> it = ac.getTileImprovementPlanIterator();
            while (it.hasNext()) {
                tileImprovements.add(it.next());
            }
        }
        return tileImprovements.iterator();
    }

    
    public boolean hasFewColonies() {
        logger.finest("Entering method hasFewColonies");
        if (!getPlayer().canBuildColonies()) {
            return false;
        }
        int numberOfColonies = 0;
        int numberOfWorkers = 0;
        for (Colony colony : getPlayer().getColonies()) {
            numberOfColonies++;
            numberOfWorkers += colony.getUnitCount();
        }

        logger.finest("Leaving method hasFewColonies");
        return numberOfColonies <= 2 || numberOfColonies >= 3
            && numberOfWorkers / numberOfColonies > numberOfColonies - 2;
    }

    
    protected void createTransportLists() {
        logger.finest("Entering method createTransportLists");
        if (!getPlayer().isEuropean()) {
            return;
        }
        ArrayList<Transportable> transportables = new ArrayList<Transportable>();

        
        Iterator<AIUnit> aui = getAIUnitIterator();
        while (aui.hasNext()) {
            AIUnit au = aui.next();
            
            if(au.getUnit().isNaval()){
                continue;
            }
            if (au.getTransportDestination() != null && au.getTransport() == null) {
                transportables.add(au);
            }
        }

        
        Iterator<AIColony> aci = getAIColonyIterator();
        while (aci.hasNext()) {
            AIColony ac = aci.next();
            Iterator<AIGoods> agi = ac.getAIGoodsIterator();
            while (agi.hasNext()) {
                AIGoods ag = agi.next();
                if (ag.getTransportDestination() != null && ag.getTransport() == null) {
                    transportables.add(ag);
                }
            }
        }

        Collections.sort(transportables, new Comparator<Transportable>() {
                public int compare(Transportable o1, Transportable o2) {
                    if (o1 == o2) {
                        return 0;
                    }
                    int result = o2.getTransportPriority() - o1.getTransportPriority();
                    if (result == 0) {
                        result = o1.getId().compareTo(o2.getId());
                    }
                    return result;
                }
            });

        ArrayList<Mission> vacantTransports = new ArrayList<Mission>();
        Iterator<AIUnit> iter = getAIUnitIterator();
        while (iter.hasNext()) {
            AIUnit au = iter.next();
            if (au.hasMission() && au.getMission() instanceof TransportMission
                && !(au.getUnit().getLocation() instanceof Europe)) {
                vacantTransports.add(au.getMission());
            }
        }

        Iterator<Transportable> ti = transportables.iterator();
        while (ti.hasNext()) {
            Transportable t = ti.next();
            t.increaseTransportPriority();
            if (t.getTransportLocatable().getLocation() instanceof Unit) {
                Mission m = ((AIUnit) getAIMain().getAIObject(
                                                              (FreeColGameObject) t.getTransportLocatable().getLocation())).getMission();
                if (m instanceof TransportMission) {
                    ((TransportMission) m).addToTransportList(t);
                }
                ti.remove();
            }
        }

        while (transportables.size() > 0) {
            Transportable t = transportables.get(0);
            TransportMission bestTransport = null;
            int bestTransportSpace = 0;
            int bestTransportTurns = Integer.MAX_VALUE;
            for (int i = 0; i < vacantTransports.size(); i++) {
                TransportMission tm = (TransportMission) vacantTransports.get(i);
                if (t.getTransportSource().getTile() == tm.getUnit().getLocation().getTile()) {
                    int transportSpace = tm.getAvailableSpace(t);
                    if (transportSpace > 0) {
                        bestTransport = tm;
                        bestTransportSpace = transportSpace;
                        bestTransportTurns = 0;
                        break;
                    } else {
                        continue;
                    }
                }
                PathNode path = tm.getPath(t);
                if (path != null && path.getTotalTurns() <= bestTransportTurns) {
                    int transportSpace = tm.getAvailableSpace(t);
                    if (transportSpace > 0
                        && (path.getTotalTurns() < bestTransportTurns || transportSpace > bestTransportSpace)) {
                        bestTransport = tm;
                        bestTransportSpace = transportSpace;
                        bestTransportTurns = path.getTotalTurns();
                    }
                }
            }
            if (bestTransport == null) {
                
                break;
            }
            bestTransport.addToTransportList(t);
            transportables.remove(t);
            vacantTransports.remove(bestTransport);
            bestTransportSpace--;
            for (int i = 0; i < transportables.size() && bestTransportSpace > 0; i++) {
                Transportable t2 = transportables.get(0);
                if (t2.getTransportLocatable().getLocation() == t.getTransportLocatable().getLocation()) {
                    bestTransport.addToTransportList(t2);
                    transportables.remove(t2);
                    bestTransportSpace--;
                }
            }
        }
    }

    
    public int tradeProposition(Unit unit, Settlement settlement, Goods goods, int gold) {
        logger.finest("Entering method tradeProposition");
        Colony colony = (Colony) settlement;
        Player otherPlayer = unit.getOwner();
        
        if (getPlayer().getStance(otherPlayer) == Stance.WAR) {
            return NetworkConstants.NO_TRADE;
        }
        
        int amount = colony.getWarehouseCapacity() - colony.getGoodsCount(goods.getType());
        amount = Math.min(amount, goods.getAmount());
        
        Tension.Level tensionLevel = getPlayer().getTension(otherPlayer).getLevel();
        int percentage = (9 - tensionLevel.ordinal()) * 10;
        
        int netProfits = ((100 - getPlayer().getTax()) * getPlayer().getMarket().getSalePrice(goods.getType(), amount)) / 100;
        int price = (netProfits * percentage) / 100;
        return price;
    }

    
    public abstract boolean acceptIndianDemand(Unit unit, Colony colony, Goods goods, int gold);

    
    public Iterator<AIColony> getAIColonyIterator() {
        ArrayList<AIColony> ac = new ArrayList<AIColony>();
        for (Colony colony : getPlayer().getColonies()) {
            AIColony a = (AIColony) getAIMain().getAIObject(colony.getId());
            if (a != null) {
                ac.add(a);
            } else {
                logger.warning("Could not find the AIColony for: " + colony);
            }
        }
        return ac.iterator();
    }

    
    public Iterator<Wish> getWishIterator() {
        ArrayList<Wish> wishList = new ArrayList<Wish>();
        Iterator<AIColony> ai = getAIColonyIterator();
        while (ai.hasNext()) {
            AIColony ac = ai.next();
            Iterator<Wish> wishIterator = ac.getWishIterator();
            while (wishIterator.hasNext()) {
                Wish w = wishIterator.next();
                wishList.add(w);
            }
        }
        Collections.sort(wishList);
        return wishList.iterator();
    }




}