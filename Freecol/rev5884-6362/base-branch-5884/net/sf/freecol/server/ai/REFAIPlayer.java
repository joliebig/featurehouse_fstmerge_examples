

package net.sf.freecol.server.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;


import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Player.PlayerType;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.networking.GiveIndependenceMessage;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.server.ai.mission.BuildColonyMission;
import net.sf.freecol.server.ai.mission.CashInTreasureTrainMission;
import net.sf.freecol.server.ai.mission.Mission;
import net.sf.freecol.server.ai.mission.PioneeringMission;
import net.sf.freecol.server.ai.mission.ScoutingMission;
import net.sf.freecol.server.ai.mission.TransportMission;
import net.sf.freecol.server.ai.mission.UnitWanderHostileMission;
import net.sf.freecol.server.ai.mission.WishRealizationMission;

import org.w3c.dom.Element;


public class REFAIPlayer extends EuropeanAIPlayer {

    private static final Logger logger = Logger.getLogger(REFAIPlayer.class.getName());


    
    public void startWorking() {
        logger.fine("Entering AI code for: " + getPlayer().getNationAsString());
        sessionRegister.clear();
        clearAIUnits();
        checkForREFDefeat();
        if (!isWorkForREF()) {
            return;
        }
        determineStances();
        rearrangeWorkersInColonies();
        abortInvalidAndOneTimeMissions();
        ensureCorrectMissions();
        giveNavalMissions();
        secureSettlements();
        giveNormalMissions();
        createAIGoodsInColonies();
        createTransportLists();
        doMissions();
        rearrangeWorkersInColonies();
        abortInvalidMissions();
        
        giveNormalMissions();
        doMissions();
        rearrangeWorkersInColonies();
        abortInvalidMissions();
        ensureCorrectMissions();
        clearAIUnits();
    }

    
    private void giveNormalMissions() {
        logger.finest("Entering method giveNormalMissions");

        int numberOfUnits = FreeCol.getSpecification().numberOfUnitTypes();
        
        ArrayList<ArrayList<Wish>> workerWishes = new ArrayList<ArrayList<Wish>>(numberOfUnits);
        for (int i = 0; i < numberOfUnits; i++) {
            workerWishes.add(new ArrayList<Wish>());
        }
        if (getPlayer().isEuropean()) {
            Iterator<AIColony> aIterator = getAIColonyIterator();
            while (aIterator.hasNext()) {
                Iterator<Wish> wIterator = aIterator.next().getWishIterator();
                while (wIterator.hasNext()) {
                    Wish w = wIterator.next();
                    if (w instanceof WorkerWish && w.getTransportable() == null) {
                        workerWishes.get(((WorkerWish) w).getUnitType().getIndex()).add(w);
                    }
                }
            }
        }

        final boolean fewColonies = hasFewColonies();
        Iterator<AIUnit> aiUnitsIterator = getAIUnitIterator();
        while (aiUnitsIterator.hasNext()) {
            AIUnit aiUnit = aiUnitsIterator.next();

            if (aiUnit.hasMission()) {
                continue;
            }

            Unit unit = aiUnit.getUnit();

            if (unit.isUninitialized()) {
                logger.warning("Trying to assign a mission to an uninitialized object: " + unit.getId());
                continue;
            }

            if (unit.canCarryTreasure()) {
                aiUnit.setMission(new CashInTreasureTrainMission(getAIMain(), aiUnit));
            } else if (unit.hasAbility("model.ability.scoutIndianSettlement") &&
                       ScoutingMission.isValid(aiUnit)) {
                aiUnit.setMission(new ScoutingMission(getAIMain(), aiUnit));
            } else if ((unit.isOffensiveUnit() || unit.isDefensiveUnit())
                       && (!unit.isColonist() || unit.hasAbility("model.ability.expertSoldier") ||
                           getGame().getTurn().getNumber() > 5)) {
                giveMilitaryMission(aiUnit);
            } else if (unit.getEquipment().containsKey(toolsType)
                       && PioneeringMission.isValid(aiUnit)) {
                aiUnit.setMission(new PioneeringMission(getAIMain(), aiUnit));
            } else if (unit.isColonist()) {
                
                HashMap<Location, Integer> distances = new HashMap<Location, Integer>(121);
                for (ArrayList<Wish> al : workerWishes) {
                    for (Wish w : al) {
                        if (!distances.containsKey(w.getDestination())) {
                            distances.put(w.getDestination(), unit.getTurnsToReach(w.getDestination()));
                        }
                    }
                }

                
                
                ArrayList<Wish> wishList = workerWishes.get(unit.getType().getIndex());
                WorkerWish bestWish = null;
                int bestTurns = Integer.MAX_VALUE;
                for (int i = 0; i < wishList.size(); i++) {
                    WorkerWish ww = (WorkerWish) wishList.get(i);
                    if (ww.getTransportable() != null) {
                        wishList.remove(i);
                        i--;
                        continue;
                    }
                    int turns = distances.get(ww.getDestination());
                    if (turns == Integer.MAX_VALUE) {
                        if (ww.getDestination().getTile() == null) {
                            turns = 5;
                        } else {
                            turns = 10;
                        }
                    } else if (turns > 5) {
                        turns = 5;
                    }
                    if (bestWish == null
                        || ww.getValue() - (turns * 2) > bestWish.getValue() - (bestTurns * 2)) {
                        bestWish = ww;
                        bestTurns = turns;
                    }
                }
                if (bestWish != null) {
                    bestWish.setTransportable(aiUnit);
                    aiUnit.setMission(new WishRealizationMission(getAIMain(), aiUnit, bestWish));
                    continue;
                }
                
                Tile colonyTile = null;
                if (getPlayer().canBuildColonies()) {
                    colonyTile = BuildColonyMission.findColonyLocation(aiUnit.getUnit());
                }
                if (colonyTile != null) {
                    bestTurns = unit.getTurnsToReach(colonyTile);
                }

                
                if (!fewColonies || colonyTile == null || bestTurns > 10) {
                    for (int i = 0; i < workerWishes.size(); i++) {
                        wishList = workerWishes.get(i);
                        for (int j = 0; j < wishList.size(); j++) {
                            WorkerWish ww = (WorkerWish) wishList.get(j);
                            if (ww.getTransportable() != null) {
                                wishList.remove(j);
                                j--;
                                continue;
                            }
                            int turns = distances.get(ww.getDestination());
                            if (turns == Integer.MAX_VALUE) {
                                if (ww.getDestination().getTile() == null) {
                                    turns = 5;
                                } else {
                                    turns = 10;
                                }
                            } else if (turns > 5) {
                                turns = 5;
                            }
                            
                            
                            if (bestWish == null
                                || ww.getValue() - (turns * 2) > bestWish.getValue() - (bestTurns * 2)) {
                                bestWish = ww;
                                bestTurns = turns;
                            }
                        }
                    }
                }
                if (bestWish != null) {
                    bestWish.setTransportable(aiUnit);
                    aiUnit.setMission(new WishRealizationMission(getAIMain(), aiUnit, bestWish));
                    continue;
                }
                
                if (colonyTile != null) {
                    Mission mission = new BuildColonyMission(getAIMain(),
                                                             aiUnit,
                                                             colonyTile,
                                                             getPlayer().getColonyValue(colonyTile));
                    aiUnit.setMission(mission);

                    boolean isUnitOnCarrier = aiUnit.getUnit().isOnCarrier();
                    if (isUnitOnCarrier) {
                        AIUnit carrier = (AIUnit) getAIMain().getAIObject(
                                                                          (FreeColGameObject) aiUnit.getUnit().getLocation());

                        
                        Mission carrierMission = carrier.getMission();

                        boolean isCarrierMissionToTransport = carrierMission instanceof TransportMission;
                        if(!isCarrierMissionToTransport){
                            throw new IllegalStateException("Carrier carrying unit not on a transport mission");
                        }
                        
                        ((TransportMission) carrierMission).addToTransportList(aiUnit);
                    }
                    continue;
                }
            }
            if (!aiUnit.hasMission()) {
                aiUnit.setMission(new UnitWanderHostileMission(getAIMain(), aiUnit));
            }
        }
    }

    
    private List<Player> getDominionsAtWar() {
        List<Player> dominions = new LinkedList<Player>();
        Iterator<Player> it = getGame().getPlayerIterator();
        while (it.hasNext()) {
            Player p = it.next();
            if (p.getREFPlayer() == getPlayer()
                && p.getPlayerType() == PlayerType.REBEL
                && p.getMonarch() == null) {
                dominions.add(p);
            }
        }
        return dominions;
    }


    
    private int getNumberOfKingUnits() {
        int n = 0;
        for (Unit unit : getPlayer().getUnits()) {
            if (unit.hasAbility("model.ability.refUnit")) {
                n++;
            }
        }
        return n;
    }

    
    private void checkForREFDefeat() {
        logger.finest("Entering method checkForREFDefeat");

        List<Player> dominions = getDominionsAtWar();

        

        if (dominions.isEmpty()) {
            return;
        }

        if (!getPlayer().getSettlements().isEmpty()) {
            return;
        }

        if (hasManOfWar() && getNumberOfKingUnits() > 6) {
            return;
        }

        for (Player p : dominions) {
            sendAndWaitSafely(new GiveIndependenceMessage(p).toXMLElement());
        }
    }

    
    private boolean isWorkForREF() {
        logger.finest("Entering method isWorkForREF");
        Iterator<Unit> it = getPlayer().getUnitIterator();
        while (it.hasNext()) {
            if (it.next().getTile() != null) {
                return true;
            }
        }
        Iterator<Player> it2 = getGame().getPlayerIterator();
        while (it2.hasNext()) {
            Player p = it2.next();
            if (p.getREFPlayer() == getPlayer() &&
                p.getPlayerType() == PlayerType.REBEL) {
                return true;
            }
        }
        return false;
    }

    
    public boolean acceptIndianDemand(Unit unit, Colony colony, Goods goods, int gold) {
        
        return false;
    }


}