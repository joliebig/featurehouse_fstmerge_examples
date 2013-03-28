


   
package net.sf.freecol.server.ai;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.ColonyTile;
import net.sf.freecol.common.model.ColonyTradeItem;
import net.sf.freecol.common.model.CombatModel;
import net.sf.freecol.common.model.DiplomaticTrade;
import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.FoundingFather;
import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.GoldTradeItem;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsTradeItem;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Ownable;
import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.StanceTradeItem;
import net.sf.freecol.common.model.Tension;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TileImprovement;
import net.sf.freecol.common.model.TradeItem;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.UnitTradeItem;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.model.Player.PlayerType;
import net.sf.freecol.common.model.Player.Stance;
import net.sf.freecol.common.model.Unit.Role;
import net.sf.freecol.common.model.pathfinding.GoalDecider;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.common.networking.ClearSpecialityMessage;
import net.sf.freecol.common.networking.NetworkConstants;
import net.sf.freecol.server.ai.goal.ManageMissionariesGoal;
import net.sf.freecol.server.ai.mission.BuildColonyMission;
import net.sf.freecol.server.ai.mission.CashInTreasureTrainMission;
import net.sf.freecol.server.ai.mission.DefendSettlementMission;
import net.sf.freecol.server.ai.mission.IdleAtColonyMission;
import net.sf.freecol.server.ai.mission.Mission;
import net.sf.freecol.server.ai.mission.PioneeringMission;
import net.sf.freecol.server.ai.mission.PrivateerMission;
import net.sf.freecol.server.ai.mission.ScoutingMission;
import net.sf.freecol.server.ai.mission.TransportMission;
import net.sf.freecol.server.ai.mission.UnitSeekAndDestroyMission;
import net.sf.freecol.server.ai.mission.UnitWanderHostileMission;
import net.sf.freecol.server.ai.mission.UnitWanderMission;
import net.sf.freecol.server.ai.mission.WishRealizationMission;
import net.sf.freecol.server.ai.mission.WorkInsideColonyMission;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Element;


public class ColonialAIPlayer extends AIPlayer {
    private static final Logger logger = Logger.getLogger(ColonialAIPlayer.class.getName());

    private static enum AIStrategy { NONE, TRADE, IMMIGRATION, COOPERATION, CONQUEST }

    
    private AIStrategy strategy = AIStrategy.NONE;

    
    private ArrayList<AIUnit> myAIUnits = new ArrayList<AIUnit>();

             
    private ManageMissionariesGoal mGoal = new ManageMissionariesGoal((AIPlayer)this, null, 1.0f);

    
    public ColonialAIPlayer(AIMain aiMain, ServerPlayer player) {
        super(aiMain, player.getId());
        setPlayer(player);
    }

    
    public ColonialAIPlayer(AIMain aiMain, Element element) {
        super(aiMain, element.getAttribute("ID"));
        readFromXMLElement(element);
        
    }

    
    public ColonialAIPlayer(AIMain aiMain, XMLStreamReader in) throws XMLStreamException {
        super(aiMain, in.getAttributeValue(null, "ID"));
        readFromXML(in);
        
    }





    
    public void startWorking() {
        logger.fine("Entering AI code for: " + getPlayer().toString());

        
        if (getPlayer().isREF() ||
            getPlayer().isIndian() ||
            !getPlayer().isEuropean()) {
            throw new IllegalStateException("Player type not supported by this AI!");
        }
        
        
        this.strategy = AIStrategy.TRADE;

        
        clearAIUnits();

        
        manageMissionaries();
        mGoal.setNeedsPlanningRecursive(true);

        boolean furtherPlanning = true;
        while (furtherPlanning) {
            mGoal.doPlanning();
            furtherPlanning = mGoal.needsPlanning();            
        }
        
        
        cheat();
        determineStances();
        rearrangeWorkersInColonies();
        abortInvalidAndOneTimeMissions();
        ensureCorrectMissions();
        giveNavalMissions();
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
    
    
    public void removeTileImprovementPlan(TileImprovementPlan plan){
    	Iterator<AIColony> colonyIter = getAIColonyIterator();
        while (colonyIter.hasNext()) {
            AIColony colony = colonyIter.next();
            if(colony.removeTileImprovementPlan(plan)){
            	return;
            }
        }
        logger.warning("Not found given TileImprovementPlan to remove");
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

    
    public FoundingFather selectFoundingFather(List<FoundingFather> foundingFathers) {
        
        int age = getGame().getTurn().getAge();
        FoundingFather bestFather = null;
        int bestWeight = -1;
        for (FoundingFather father : foundingFathers) {
            if (father == null) continue;
            int weight = father.getWeight(age);
            if (weight > bestWeight) {
                bestWeight = weight;
                bestFather = father;
            }
        }
        return bestFather;
    }

    
    public boolean acceptTax(int tax) {
        Goods toBeDestroyed = getPlayer().getMostValuableGoods();
        if (toBeDestroyed == null) {
            return false;
        }
        
        GoodsType goodsType = toBeDestroyed.getType();
        if (goodsType.isFoodType() || goodsType.isBreedable()) {
            
            return false;
        } else if (goodsType.isMilitaryGoods() || 
                   goodsType.isTradeGoods() ||
                   goodsType.isBuildingMaterial()) {
            if (getGame().getTurn().getAge() == 3) {
                
                
                return false;
            } else {
                return true;
            }
        } else {
            int averageIncome = 0;
            int numberOfGoods = 0;
            List<GoodsType> goodsTypes = FreeCol.getSpecification().getGoodsTypeList();
            for (GoodsType type : goodsTypes) {
                if (type.isStorable()) {
                    averageIncome += getPlayer().getIncomeAfterTaxes(type);
                    numberOfGoods++;
                }
            }
            averageIncome = averageIncome / numberOfGoods;
            if (getPlayer().getIncomeAfterTaxes(toBeDestroyed.getType()) > averageIncome) {
                
                return false;
            } else {
                return true;
            }
        }
    }

    
    public boolean acceptIndianDemand(Unit unit, Colony colony, Goods goods, int gold) {
        
        if (strategy == AIStrategy.CONQUEST) {
            return false;
        } else {
            return true;
        }
    }

    
    public boolean acceptMercenaryOffer() {
        
        if (strategy == AIStrategy.CONQUEST || getPlayer().isAtWar()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean acceptDiplomaticTrade(DiplomaticTrade agreement) {
        Stance stance = null;
        int value = 0;
        Iterator<TradeItem> itemIterator = agreement.iterator();
        while (itemIterator.hasNext()) {
            TradeItem item = itemIterator.next();
            if (item instanceof GoldTradeItem) {
                int gold = ((GoldTradeItem) item).getGold();
                if (item.getSource() == getPlayer()) {
                    value -= gold;
                } else {
                    value += gold;
                }
            } else if (item instanceof StanceTradeItem) {
                
                stance = ((StanceTradeItem) item).getStance();
            } else if (item instanceof ColonyTradeItem) {
                
                if (item.getSource() == getPlayer()) {
                    value = Integer.MIN_VALUE;
                    break;
                } else {
                    value += 1000;
                }
            } else if (item instanceof UnitTradeItem) {
                
                if (item.getSource() == getPlayer()) {
                    value = Integer.MIN_VALUE;
                    break;
                } else {
                    value += 100;
                }
            } else if (item instanceof GoodsTradeItem) {
                Goods goods = ((GoodsTradeItem) item).getGoods();
                if (item.getSource() == getPlayer()) {
                    value -= getPlayer().getMarket().getBidPrice(goods.getType(), goods.getAmount());
                } else {
                    value += getPlayer().getMarket().getSalePrice(goods.getType(), goods.getAmount());
                }
            }
        }

        boolean accept = false;
        switch (stance) {
        case UNCONTACTED:
            accept = false;
            break;
        case WAR: 
            accept = value >= 0;
            break;
        case CEASE_FIRE:
            accept = value >= 500;
            break;
        case PEACE:
            if (agreement.getSender().hasAbility("model.ability.alwaysOfferedPeace")
                && value >= 0) {
                
                
                accept = true;
            } else if (value >= 1000) {
                accept = true;
            }
            break;
        case ALLIANCE:
            accept = value >= 2000;
            break;
        }

        logger.info("Trade value is " + value + ", accept is " + accept);
        return accept;
    }

    
    
    public void registerSellGoods(Goods goods) {
        
        logger.warning("registerSellGoods() not implemented for ColonialAIPlayer!");
    }

    
    public int buyProposition(Unit unit, Goods goods, int gold) {
        
        logger.warning("buyProposition() not implemented for ColonialAIPlayer!");
        return NetworkConstants.NO_TRADE;
    }

    
    public int tradeProposition(Unit unit, Settlement settlement, Goods goods, int gold) {
        logger.finest("Entering method tradeProposition");
        if (settlement instanceof Colony) {
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
        } else {
            logger.warning("ColonialAIPlayer shouldn't have indian settlement!");
            return NetworkConstants.NO_TRADE;
        }
    }

    
    @Override
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
        out.writeAttribute("ID", getId());
        out.writeEndElement();
    }

    
    @Override
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setPlayer((ServerPlayer) getAIMain().getFreeColGameObject(in.getAttributeValue(null, "ID")));
        in.nextTag();
    }

    
    public static String getXMLElementTagName() {
        return "colonialAIPlayer";
    }





    
    private void cheat() {
        logger.finest("Entering method cheat");
        
        for (GoodsType goodsType : FreeCol.getSpecification().getGoodsTypeList()) {
            getPlayer().resetArrears(goodsType);
        }
        
        
        if (getAIMain().getFreeColServer().isSingleplayer() && getPlayer().isAI()
                && getPlayer().getPlayerType() == PlayerType.COLONIAL) {
            Europe europe = getPlayer().getEurope();
            List<UnitType> unitTypes = FreeCol.getSpecification().getUnitTypeList();
            
            if (getRandom().nextInt(10) == 1) {
                int price = 0;
                UnitType unitToTrain = null;
                for (UnitType unitType : unitTypes) {
                    if (unitType.hasPrice()) {
                        int unitPrice = europe.getUnitPrice(unitType);
                        if (unitToTrain == null || unitPrice < price) {
                            unitToTrain = unitType;
                            price = unitPrice;
                        }
                    }
                }
                Unit unit = null;
                if (unitToTrain != null) {
                    getPlayer().modifyGold(price);
                    unit = this.trainAIUnitInEurope(unitToTrain).getUnit();
                }
                if (unit != null && unit.isColonist()) {
                    
                    GoodsType muskets = Specification.getSpecification().getGoodsType("model.goods.muskets");
                    GoodsType horses = Specification.getSpecification().getGoodsType("model.goods.horses");
                    getPlayer().modifyGold(getPlayer().getMarket().getBidPrice(muskets, 50));
                    getPlayer().modifyGold(getPlayer().getMarket().getBidPrice(horses, 50));
                    
                    sendAndWaitSafely(new ClearSpecialityMessage(unit).toXMLElement());
                    Element equipMusketsElement = Message.createNewRootElement("equipUnit");
                    equipMusketsElement.setAttribute("unit", unit.getId());
                    equipMusketsElement.setAttribute("type", "model.equipment.muskets");
                    equipMusketsElement.setAttribute("amount", Integer.toString(50));
                    sendAndWaitSafely(equipMusketsElement);
                    Element equipHorsesElement = Message.createNewRootElement("equipUnit");
                    equipHorsesElement.setAttribute("unit", unit.getId());
                    equipHorsesElement.setAttribute("type", "model.equipment.horses");
                    equipHorsesElement.setAttribute("amount", Integer.toString(50));
                    sendAndWaitSafely(equipHorsesElement);
                }
            }
            if (getRandom().nextInt(40) == 21) {
                int total = 0;
                ArrayList<UnitType> navalUnits = new ArrayList<UnitType>();
                for (UnitType unitType : unitTypes) {
                    if (unitType.hasAbility("model.ability.navalUnit") && unitType.hasPrice()) {
                        navalUnits.add(unitType);
                        total += europe.getUnitPrice(unitType);
                    }
                }
                
                UnitType unitToPurchase = null;
                int random = getRandom().nextInt(total);
                total = 0;
                for (UnitType unitType : navalUnits) {
                    total += unitType.getPrice();
                    if (random < total) {
                        unitToPurchase = unitType;
                        break;
                    }
                }
                getPlayer().modifyGold(europe.getUnitPrice(unitToPurchase));
                this.trainAIUnitInEurope(unitToPurchase);
            }
        }
    }

    
    private void ensureCorrectMissions() {
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

    
    private void determineStances() {
        logger.finest("Entering method determineStances");
        Player player = getPlayer();
        for (Player p : getGame().getPlayers()) {
            if(p == player){
                continue;
            }
            
            Stance stance = player.getStance(p);
            Tension tension = player.getTension(p);
            
            if (stance == Stance.UNCONTACTED || tension == null) {
                continue;
            }

            
            if (p.getREFPlayer() == player && p.getPlayerType() == PlayerType.REBEL) {
                tension.modify(1000);
                if(stance != Stance.WAR){
                    player.changeRelationWithPlayer(p, Stance.WAR);
                }
                continue;
            }
            
            
            if (stance != Stance.WAR 
                    && tension.getLevel() == Tension.Level.HATEFUL) {
                player.changeRelationWithPlayer(p, Stance.WAR);
                continue;
            }
            
            
            if (stance == Stance.WAR
                    && tension.getLevel().compareTo(Tension.Level.CONTENT) <= 0) {
                player.changeRelationWithPlayer(p, Stance.CEASE_FIRE);
                continue;
            }
            
            
            if (stance == Stance.CEASE_FIRE
                    && tension.getLevel().compareTo(Tension.Level.HAPPY) <= 0) {
                player.changeRelationWithPlayer(p, Stance.PEACE);
                continue;
            }
        }
    }

    
    private void abortInvalidMissions() {
        logger.finest("Entering method abortInvalidMissions");
        Iterator<AIUnit> aiUnitsIterator = getAIUnitIterator();
        while (aiUnitsIterator.hasNext()) {
            AIUnit aiUnit = aiUnitsIterator.next();
            if (aiUnit.getMission() == null) {
                continue;
            }
            if (!aiUnit.getMission().isValid()) {
                aiUnit.setMission(null);
            }
        }
    }

    
    private void abortInvalidAndOneTimeMissions() {
        logger.finest("Entering method abortInvalidAndOneTimeMissions");
        Iterator<AIUnit> aiUnitsIterator = getAIUnitIterator();
        while (aiUnitsIterator.hasNext()) {
            AIUnit aiUnit = aiUnitsIterator.next();
            if (aiUnit.getMission() == null) {
                continue;
            }
            if (!aiUnit.getMission().isValid() || aiUnit.getMission() instanceof UnitWanderHostileMission
                    || aiUnit.getMission() instanceof UnitWanderMission
                    || aiUnit.getMission() instanceof IdleAtColonyMission
            
            
            ) {
                aiUnit.setMission(null);
            }
        }
    }

    
    private void giveNavalMissions() {
        logger.finest("Entering method giveNavalMissions");
        Iterator<AIUnit> aiUnitsIterator = getAIUnitIterator();
        while (aiUnitsIterator.hasNext()) {
            AIUnit aiUnit = aiUnitsIterator.next();
            Unit unit = aiUnit.getUnit();
            if (aiUnit.hasMission() || !unit.isNaval()){
                continue;
            }

            if (PrivateerMission.isValid(aiUnit)) {
                aiUnit.setMission(new PrivateerMission(getAIMain(), aiUnit));
            }
            else{
                aiUnit.setMission(new TransportMission(getAIMain(), aiUnit));
            }
        }
    }

    
    private void rearrangeWorkersInColonies() {
        logger.finest("Entering method rearrangeWorkersInColonies");
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

    
    private void giveNormalMissions() {
        logger.finest("Entering method giveNormalMissions");
        
        int numberOfUnits = FreeCol.getSpecification().numberOfUnitTypes();
        
        Vector<ArrayList<Wish>> workerWishes = new Vector<ArrayList<Wish>>(numberOfUnits);
        for (int i = 0; i < numberOfUnits; i++) {
            workerWishes.add(new ArrayList<Wish>());
        }

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

        
        final boolean fewColonies = hasFewColonies();
        boolean isPioneerReq = PioneeringMission.getPlayerPioneers(this).size() == 0;
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
            
            
            
            
            
            boolean isPioneer = unit.hasAbility("model.ability.improveTerrain")
                                || unit.hasAbility("model.ability.expertPioneer");
            boolean isExpert = unit.getSkillLevel() > 0;
            if ((isPioneer || (isPioneerReq && !isExpert)) && PioneeringMission.isValid(aiUnit)) {
                aiUnit.setMission(new PioneeringMission(getAIMain(), aiUnit));
                isPioneerReq = false;
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
                if (aiUnit.getUnit().isOffensiveUnit()) {
                    aiUnit.setMission(new UnitWanderHostileMission(getAIMain(), aiUnit));
                } else {
                    
                    
                    aiUnit.setMission(new IdleAtColonyMission(getAIMain(), aiUnit));
                }
            }
        }
    }

    
    private void createAIGoodsInColonies() {
        logger.finest("Entering method createAIGoodsInColonies");

        Iterator<AIColony> ci = getAIColonyIterator();
        while (ci.hasNext()) {
            AIColony c = ci.next();
            c.createAIGoods();
        }
    }

    
    private void doMissions() {
        logger.finest("Entering method doMissions");
        Iterator<AIUnit> aiUnitsIterator = getAIUnitIterator();
        while (aiUnitsIterator.hasNext()) {
            AIUnit aiUnit = aiUnitsIterator.next();
            if (aiUnit.hasMission() && aiUnit.getMission().isValid()
                    && !(aiUnit.getUnit().isOnCarrier())) {
                try {
                    aiUnit.doMission(getConnection());
                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    logger.warning(sw.toString());
                }
            }
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

    int getUnitSeekAndDestroyMissionValue(Unit unit, Tile newTile, int turns) {
        logger.finest("Entering method getUnitSeekAndDestroyMissionValue");
        
        Unit defender = newTile.getDefendingUnit(unit);
        
        if(!isTargetValidForSeekAndDestroy(unit, defender)){
        	return Integer.MIN_VALUE;
        }
        
        int value = 10020;
        CombatModel combatModel = unit.getGame().getCombatModel();
        
        if (getBestTreasureTrain(newTile) != null) {
        	value += Math.min(getBestTreasureTrain(newTile).getTreasureAmount() / 10, 50);
        }
        if (defender.getType().getOffence() > 0 &&
        		newTile.getSettlement() == null) {
        	value += 200 - combatModel.getDefencePower(unit, defender) * 2 - turns * 50;
        }
            
        value += combatModel.getOffencePower(defender, unit) -
              combatModel.getDefencePower(defender, unit);
        value -= turns * 10;
 
        if (!defender.isNaval()) {
        	if (defender.hasAbility("model.ability.expertSoldier")
                    && !defender.isArmed()) {
                value += 10 - combatModel.getDefencePower(unit, defender) * 2 - turns * 25;
            }
            if (newTile.getSettlement() != null) {
                value += 300;
                Iterator<Unit> dp = newTile.getUnitIterator();
                while (dp.hasNext()) {
                    Unit u = dp.next();
                    if (u.isDefensiveUnit()) {
                        if (combatModel.getDefencePower(unit, u) > combatModel.getOffencePower(unit, u)) {
                            value -= 100 * (combatModel.getDefencePower(unit, u) - combatModel.getOffencePower(unit, u));
                        } else {
                            value -= combatModel.getDefencePower(unit, u);
                        }
                    }
                }
            }
        }   
        return Math.max(0, value);
    }
    
         
    boolean isTargetValidForSeekAndDestroy(Unit attacker, Unit defender) {
        if (attacker.getOwner()!=getPlayer()) {
            logger.warning("isTargetValidForSeekAndDestroy() called for other players unit!");
            return false;
        }
    
        if (defender == null) { 
            return false;
        }
    	
        
        if (attacker.isNaval() != defender.isNaval()) {
            return false;
        }

        if (attacker.isNaval()) { 
            if (attacker.getTile() == null || attacker.getTile().isLand()
                || defender.getTile() == null || defender.getTile().isLand()) {
                return false;
            }
        }

        Player attackerPlayer = attacker.getOwner();
        Player defenderPlayer = defender.getOwner();
        if (attackerPlayer == defenderPlayer) { 
            return false;
        }

        boolean atWar = attackerPlayer.getStance(defenderPlayer) == Stance.WAR;

        
        if (!atWar) {
            return false;
        }
        return true;
    }
        
    
    void giveMilitaryMission(AIUnit aiUnit) {
        logger.finest("Entering method giveMilitaryMission");
        
        final Unit unit = aiUnit.getUnit();
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
                    int value = getDefendColonyMissionValue(unit, t.getColony(), pathNode.getTurns());
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

            public boolean check(Unit u, PathNode pathNode) {
                Tile newTile = pathNode.getTile();
                Unit defender = newTile.getDefendingUnit(unit);
                if (isTargetValidForSeekAndDestroy(unit, defender)) {
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

    
    private void createTransportLists() {
        logger.finest("Entering method createTransportLists");

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

        
        if(transportables.isEmpty()){
            return;
        }
        
        
        for (Transportable t : transportables){
            t.increaseTransportPriority();
        }

        
        ArrayList<Mission> vacantTransports = new ArrayList<Mission>();
        Iterator<AIUnit> iter = getAIUnitIterator();
        while (iter.hasNext()) {
            AIUnit au = iter.next();
            if (au.hasMission() && au.getMission() instanceof TransportMission
                    && !(au.getUnit().getLocation() instanceof Europe)) {
                vacantTransports.add(au.getMission());
            }
        }
        
        
        
        if(vacantTransports.isEmpty()){
            return;
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

        
        
        
        
        
        ArrayList<Transportable> iteratingList = new ArrayList<Transportable>(transportables);
        for(Transportable t : iteratingList){
            Location transportableLoc = t.getTransportLocatable().getLocation();
            boolean isTransportableAlreadyOnCarrier = transportableLoc instanceof Unit;
            if (isTransportableAlreadyOnCarrier) {
                AIUnit carrierAI = (AIUnit) getAIMain().getAIObject((Unit) transportableLoc);
                Mission m = carrierAI.getMission();
                if (m instanceof TransportMission) {
                    ((TransportMission) m).addToTransportList(t);
                }
                transportables.remove(t);
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

    
    private Unit getBestTreasureTrain(Tile tile) {
        Unit bestTreasureTrain = null;
        for (Unit unit : tile.getUnitList()) {
            if (unit.canCarryTreasure() &&
                (bestTreasureTrain == null || 
                 bestTreasureTrain.getTreasureAmount() < unit.getTreasureAmount())) {
                bestTreasureTrain = unit;
            }
        }

        return bestTreasureTrain;
    }

    private void manageMissionaries() {
        Iterator<AIUnit> it = getAIUnitIterator();
        while (it.hasNext()) {
            AIUnit au = it.next();
            Unit u = au.getUnit();
            
            if (u.getRole()==Role.MISSIONARY) {
                logger.info("Found missionary unit:"+u.getId());
                mGoal.addUnit(au);
                
                it.remove(); 
            }
        }
    }

    
    protected Iterator<AIUnit> getAIUnitIterator() {
        logger.info("Override: getAIUnitIterator()!");
        if (myAIUnits.size() == 0) {
            ArrayList<AIUnit> au = new ArrayList<AIUnit>();
            Iterator<Unit> unitsIterator = getPlayer().getUnitIterator();
            while (unitsIterator.hasNext()) {
                Unit theUnit = unitsIterator.next();
                AIUnit a = (AIUnit) getAIMain().getAIObject(theUnit.getId());
                if (a != null && a.getGoal()==null) {
                    
                    au.add(a);
                } else {
                    logger.warning("Could not find the AIUnit for: " + theUnit + " (" + theUnit.getId() + ") - "
                            + (getGame().getFreeColGameObject(theUnit.getId()) != null));
                }
            }
            myAIUnits = au;
        }
        return myAIUnits.iterator();
    }

             
    protected void clearAIUnits() {
        logger.info("Override: clearAIUnits()!");
        myAIUnits.clear();    
    }

}
