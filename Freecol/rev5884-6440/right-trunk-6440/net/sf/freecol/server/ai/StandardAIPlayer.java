



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
import net.sf.freecol.common.Specification;
import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.ColonyTile;
import net.sf.freecol.common.model.ColonyTradeItem;
import net.sf.freecol.common.model.CombatModel;
import net.sf.freecol.common.model.DiplomaticTrade;
import net.sf.freecol.common.model.EquipmentType;
import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.FoundingFather;
import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.GoldTradeItem;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsTradeItem;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.IndianSettlement;
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
import net.sf.freecol.common.model.Map.Position;
import net.sf.freecol.common.model.Player.PlayerType;
import net.sf.freecol.common.model.Player.Stance;
import net.sf.freecol.common.model.Unit.UnitState;
import net.sf.freecol.common.model.pathfinding.CostDeciders;
import net.sf.freecol.common.model.pathfinding.GoalDecider;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.common.networking.ClearSpecialityMessage;
import net.sf.freecol.common.networking.NetworkConstants;
import net.sf.freecol.server.ai.mission.BuildColonyMission;
import net.sf.freecol.server.ai.mission.CashInTreasureTrainMission;
import net.sf.freecol.server.ai.mission.DefendSettlementMission;
import net.sf.freecol.server.ai.mission.IdleAtColonyMission;
import net.sf.freecol.server.ai.mission.IndianBringGiftMission;
import net.sf.freecol.server.ai.mission.IndianDemandMission;
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


public class StandardAIPlayer extends AIPlayer {
    private static final Logger logger = Logger.getLogger(StandardAIPlayer.class.getName());

    private static final int MAX_DISTANCE_TO_BRING_GIFT = 5;

    private static final int MAX_NUMBER_OF_GIFTS_BEING_DELIVERED = 1;

    private static final int MAX_DISTANCE_TO_MAKE_DEMANDS = 5;

    private static final int MAX_NUMBER_OF_DEMANDS = 1;

    private static enum AIStrategy { NONE, TRADE, IMMIGRATION, COOPERATION, CONQUEST }

    
    private AIStrategy strategy = AIStrategy.NONE;

    
    private HashMap<String, Integer> sessionRegister = new HashMap<String, Integer>();

    
    public StandardAIPlayer(AIMain aiMain, ServerPlayer player) {
        super(aiMain, player.getId());
        setPlayer(player);
    }

    
    public StandardAIPlayer(AIMain aiMain, Element element) {
        super(aiMain, element.getAttribute("ID"));
        readFromXMLElement(element);
    }

    
    public StandardAIPlayer(AIMain aiMain, XMLStreamReader in) throws XMLStreamException {
        super(aiMain, in.getAttributeValue(null, "ID"));
        readFromXML(in);
    }





    
    public void startWorking() {
        logger.fine("Entering AI code for: " + getPlayer() + ", year " + getGame().getTurn().getYear());

        
        this.strategy = AIStrategy.TRADE;
        sessionRegister.clear();
        clearAIUnits();
        if (getPlayer().isREF()) {
            if(checkForREFDefeat()){
                return;
            }
            if (!isWorkForREF()) {
                return;
            }
        }
        cheat();
        determineStances();
        rearrangeWorkersInColonies();
        abortInvalidAndOneTimeMissions();
        ensureCorrectMissions();
        giveNavalMissions();
        secureSettlements();
        giveNormalMissions();
        bringGifts();
        demandTribute();
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
        for (Settlement settlment : getPlayer().getSettlements()) {
            numberOfColonies++;
            numberOfWorkers += settlment.getUnitCount();
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
                    
            
            
            
            if (father.hasAbility("model.ability.buildCustomHouse")) {
                return father;
            }

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
        boolean validOffer = true;
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
                switch (stance) {
                    case UNCONTACTED:
                        validOffer = false; 
                        break;
                    case WAR: 
                        break;
                    case CEASE_FIRE:
                        value -= 500;
                        break;
                    case PEACE:
                        if (!agreement.getSender().hasAbility("model.ability.alwaysOfferedPeace")) {
                            
                            
                            value -= 1000;
                        }
                        break;
                    case ALLIANCE:
                        value -= 2000;
                        break;
                    }

            } else if (item instanceof ColonyTradeItem) {
                
                if (item.getSource() == getPlayer()) {
                    validOffer = false;
                    break;
                } else {
                    value += 1000;
                }
            } else if (item instanceof UnitTradeItem) {
                
                if (item.getSource() == getPlayer()) {
                    validOffer = false;
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
        if (validOffer) {
            logger.info("Trade value is " + value + ", accept if >=0");
        } else {
            logger.info("Trade offer is considered invalid!");
        }
        return (value>=0)&&validOffer;
    }


    
    public void registerSellGoods(Goods goods) {
        String goldKey = "tradeGold#" + goods.getType().getIndex() + "#" + goods.getAmount()
            + "#" + goods.getLocation().getId();
        sessionRegister.put(goldKey, null);
    }

    
    public int buyProposition(Unit unit, Goods goods, int gold) {
        logger.finest("Entering method tradeProposition");
        IndianSettlement settlement = (IndianSettlement) goods.getLocation();
        String goldKey = "tradeGold#" + goods.getType().getIndex() + "#" + goods.getAmount()
            + "#" + settlement.getId();
        String hagglingKey = "tradeHaggling#" + unit.getId();

        Integer registered = sessionRegister.get(goldKey);
        if (registered == null) {
            int price = settlement.getPriceToSell(goods)
                + getPlayer().getTension(unit.getOwner()).getValue();
            sessionRegister.put(goldKey, new Integer(price));
            return price;
        } else {
            int price = registered.intValue();
            if (price < 0 || price == gold) {
                return price;
            } else if (gold < (price * 9) / 10) {
                logger.warning("Cheating attempt: sending a offer too low");
                sessionRegister.put(goldKey, new Integer(-1));
                return NetworkConstants.NO_TRADE;
            } else {
                int haggling = 1;
                if (sessionRegister.containsKey(hagglingKey)) {
                    haggling = sessionRegister.get(hagglingKey).intValue();
                }
                if (getRandom().nextInt(3 + haggling) <= 3) {
                    sessionRegister.put(goldKey, new Integer(gold));
                    sessionRegister.put(hagglingKey, new Integer(haggling + 1));
                    return gold;
                } else {
                    sessionRegister.put(goldKey, new Integer(-1));
                    return NetworkConstants.NO_TRADE;
                }
            }
        }
    }

    
    public int tradeProposition(Unit unit, Settlement settlement, Goods goods, int gold) {
        logger.finest("Entering method tradeProposition");
        if (settlement instanceof IndianSettlement) {
            String goldKey = "tradeGold#" + goods.getType().getIndex() + "#" + goods.getAmount() + "#" + unit.getId();
            String hagglingKey = "tradeHaggling#" + unit.getId();
            int price;
            if (sessionRegister.containsKey(goldKey)) {
                price = sessionRegister.get(goldKey).intValue();
                if (price <= 0) {
                    return price;
                }
            } else {
                price = ((IndianSettlement) settlement).getPrice(goods) - getPlayer().getTension(unit.getOwner()).getValue();
                price = Math.min(price, getPlayer().getGold() / 2);
                if (price <= 0) {
                    return 0;
                }
                sessionRegister.put(goldKey, new Integer(price));
            }
            if (gold < 0 || price == gold) {
                return price;
            } else if (gold > (getPlayer().getGold() * 3) / 4) {
                sessionRegister.put(goldKey, new Integer(-1));
                return NetworkConstants.NO_TRADE;
            } else if (gold > (price * 11) / 10) {
                logger.warning("Cheating attempt: haggling with a request too high");
                sessionRegister.put(goldKey, new Integer(-1));
                return NetworkConstants.NO_TRADE;
            } else {
                int haggling = 1;
                if (sessionRegister.containsKey(hagglingKey)) {
                    haggling = sessionRegister.get(hagglingKey).intValue();
                }
                if (getRandom().nextInt(3 + haggling) <= 3) {
                    sessionRegister.put(goldKey, new Integer(gold));
                    sessionRegister.put(hagglingKey, new Integer(haggling + 1));
                    return gold;
                } else {
                    sessionRegister.put(goldKey, new Integer(-1));
                    return NetworkConstants.NO_TRADE;
                }
            }
        } else if (settlement instanceof Colony) {
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
            throw new IllegalArgumentException("Unknown type of settlement.");
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
        return "aiPlayer";
    }





    
    private void cheat() {
        logger.finest("Entering method cheat");
        
        for (GoodsType goodsType : FreeCol.getSpecification().getGoodsTypeList()) {
            getPlayer().resetArrears(goodsType);
        }

        
        if (getAIMain().getFreeColServer().isSingleplayer() && getPlayer().isEuropean() && !getPlayer().isREF() && getPlayer().isAI()
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
                    AIUnit aiUnit = this.trainAIUnitInEurope(unitToTrain);
                    if (aiUnit != null) unit = aiUnit.getUnit();
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
                    equipMusketsElement.setAttribute("amount", Integer.toString(1));
                    sendAndWaitSafely(equipMusketsElement);
                    Element equipHorsesElement = Message.createNewRootElement("equipUnit");
                    equipHorsesElement.setAttribute("unit", unit.getId());
                    equipHorsesElement.setAttribute("type", "model.equipment.horses");
                    equipHorsesElement.setAttribute("amount", Integer.toString(1));
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
        if (getPlayer().isIndian()) {
            return;
        }
        Iterator<AIUnit> it = getAIUnitIterator();
        while (it.hasNext()) {
            AIUnit au = it.next();
            
            if(au.hasMission()){
                continue;
            }
            
            
            Unit u = au.getUnit();
            if (u.getLocation() instanceof ColonyTile || u.getLocation() instanceof Building) {
                AIColony ac = (AIColony) getAIMain().getAIObject(u.getColony());
                if(ac == null){
                    logger.warning("Could not get AIColony object for unit=" + u.getId() + " at=" + u.getLocation());
                    continue;
                }
                au.setMission(new WorkInsideColonyMission(getAIMain(), au, ac));
            }
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
        if (!getPlayer().isEuropean()) {
            return;
        }
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

    
    private void secureSettlements() {
        logger.finest("Entering method secureSettlements");
        if (getPlayer().isEuropean()) {

            
            
            
            for (Colony colony : getPlayer().getColonies()) {
                equipSoldiersOutsideColony(colony);
                reOrganizeSoldiersOfColony(colony);
                
                
                
            }
            
        } else {
            List<IndianSettlement> settlements = getPlayer().getIndianSettlements();
            for (IndianSettlement is : settlements) {
                
                
                int n = getRandom().nextInt(settlements.size());
                IndianSettlement settlement = settlements.get(n);
                if(settlement != is){
                    is.tradeGoodsWithSetlement(settlement);
                }
                is.equipBraves();
                secureIndianSettlement(is);
            }
        }
    }

    void equipSoldiersOutsideColony(Colony colony) {
        boolean colonyHasArmedUnits = false;
        boolean canArmUnit = false;
        
        EquipmentType musketsEqType = FreeCol.getSpecification().getEquipmentType("model.equipment.muskets");
        EquipmentType horsesEqType = FreeCol.getSpecification().getEquipmentType("model.equipment.horses");
        
        
        
        Comparator<Unit> comp = Unit.getSkillLevelComparator();
        
        List<Unit>unarmedExpertSoldiers = new ArrayList<Unit>();
        List<Unit>unarmedColonists = new ArrayList<Unit>();
        List<Unit>unmountedSoldiers = new ArrayList<Unit>();
        
        
        for(Unit unit : colony.getTile().getUnitList()){
            boolean isArmed = unit.isArmed();
            boolean isMounted = unit.isMounted();
            boolean isExpertSoldier = unit.hasAbility("model.ability.expertSoldier");
            if(isExpertSoldier){
                if(isArmed){
                    colonyHasArmedUnits = true;
                    if(!isMounted){
                        unmountedSoldiers.add(unit);
                    }
                }
                else{
                    unarmedExpertSoldiers.add(unit);
                }
                continue;
            }
            if(unit.isArmed()){
                colonyHasArmedUnits = true;
                if(!isMounted){
                    unmountedSoldiers.add(unit);
                }
            }
            else{
                unarmedColonists.add(unit);
            }
        }
        
        
        for(Unit unit : unarmedExpertSoldiers){
            canArmUnit = colony.canBuildEquipment(musketsEqType);
            boolean isMounted = unit.isMounted();
            
            if(canArmUnit){
                unit.equipWith(musketsEqType);
                colonyHasArmedUnits = true;
                if(!isMounted){
                    unmountedSoldiers.add(unit);
                }
                continue;
            }
        }
        
        
        
        if(!colonyHasArmedUnits){
            canArmUnit = colony.canBuildEquipment(musketsEqType);
            
            if(!canArmUnit || unarmedColonists.size() == 0){
                return;
            }
        
            Collections.sort(unarmedColonists, comp);
            Unit unit = unarmedColonists.get(0);
            unit.equipWith(musketsEqType);
            if(!unit.isMounted()){
                unmountedSoldiers.add(unit);
            }
        }
        
        comp = new Comparator<Unit>(){
            public int compare(Unit unit1, Unit unit2){
                boolean isUnit1Expert = unit1.hasAbility("model.ability.expertSoldier");
                boolean isUnit2Expert = unit2.hasAbility("model.ability.expertSoldier");

                if(isUnit1Expert && !isUnit2Expert){
                    return -1;
                }
                if(!isUnit1Expert && isUnit2Expert){
                    return 1;
                }
                return 0;
            }
        };
        Collections.sort(unmountedSoldiers,comp);
        
        while(unmountedSoldiers.size() > 0 
                && colony.canBuildEquipment(horsesEqType)){
            Unit soldier = unmountedSoldiers.remove(0);
            soldier.equipWith(horsesEqType);
        }
    }
    
    void reOrganizeSoldiersOfColony(Colony colony){
        List<Unit>unarmedExpertSoldiers = new ArrayList<Unit>();
        List<Unit>armedNonExpertSoldiers = new ArrayList<Unit>();
        List<Unit>colonistsInside = new ArrayList<Unit>();
        
        
        
        
        Comparator<Unit> comp = Unit.getSkillLevelComparator();
        
        Comparator<Unit> reverseComp = Collections.reverseOrder(comp);
        
        
        for(Unit unit : colony.getTile().getUnitList()){
            boolean isArmed = unit.isArmed();
            boolean isExpertSoldier = unit.hasAbility("model.ability.expertSoldier"); 
            if(isExpertSoldier){
                if(!isArmed){
                    unarmedExpertSoldiers.add(unit);
                }
                
                continue;
            }
            if(isArmed){
                armedNonExpertSoldiers.add(unit);
                continue;
            }
        }

        
        Collections.sort(armedNonExpertSoldiers, reverseComp);
        
        
        for(Unit unit : unarmedExpertSoldiers){
            
            if(armedNonExpertSoldiers.size() == 0){
                return;
            }
        
            
            Unit otherSoldier = armedNonExpertSoldiers.remove(0);
            unit.switchEquipmentWith(otherSoldier);
            unit.setState(UnitState.ACTIVE);
            otherSoldier.setState(UnitState.ACTIVE);
            ((AIUnit)getAIMain().getAIObject(unit)).setMission(null);
            ((AIUnit)getAIMain().getAIObject(otherSoldier)).setMission(null);   
        }
        
        if(armedNonExpertSoldiers.size() == 0){
            return;
        }
        
        
        colonistsInside.addAll(colony.getUnitList());
        Collections.sort(colonistsInside, comp);
        
        for(Unit unit : armedNonExpertSoldiers){
            
            if(colonistsInside.size() == 0){
                return;
            }
            
            Unit unitInside = colonistsInside.remove(0);
            
            
            if(unit.getSkillLevel() <= unitInside.getSkillLevel()){
                return;
            }
        
            Location loc = unitInside.getLocation();
            
            unitInside.putOutsideColony();
            unit.switchEquipmentWith(unitInside);
            unit.setLocation(loc);
            unit.setState(UnitState.IN_COLONY);
            unitInside.setState(UnitState.ACTIVE);
            ((AIUnit)getAIMain().getAIObject(unit)).setMission(null);
            ((AIUnit)getAIMain().getAIObject(unitInside)).setMission(null);   
        }
    }
    

    
    void secureIndianSettlement(IndianSettlement is) {
        
        if (!is.getOwner().isAtWar()) {
            return;
        }

        Map map = getPlayer().getGame().getMap();

        int defenders = is.getTile().getUnitCount();
        int threat = 0;
        int worstThreat = 0;
        Location bestTarget = null;
        Iterator<Position> positionIterator = map.getCircleIterator(is.getTile().getPosition(), true, 2);
        while (positionIterator.hasNext()) {
            Tile t = map.getTile(positionIterator.next());
            
            
            if(!t.isLand()){
                continue;
            }

            
            if (t.getUnitCount() == 0) {
                continue;
            }

            Player enemy = t.getFirstUnit().getOwner();

            
            if (enemy == getPlayer()) {
                defenders++;
                continue;
            }

            Tension tension = getPlayer().getTension(enemy);

            
            if (tension == null) {
                logger.warning(getPlayer() + " tension towards " + enemy + " is NULL");
                continue;
            }

            int value = tension.getValue();
            int threatModifier = 0;
            int unitThreat = 0;
            if (value >= Tension.TENSION_ADD_MAJOR) {
                threatModifier = 2;
                unitThreat = t.getUnitCount() * 2;
            } else if (value >= Tension.TENSION_ADD_MINOR) {
                threatModifier = 1;
                unitThreat = t.getUnitCount();
            }

            threat += threatModifier;
            if (unitThreat > worstThreat) {
                if (t.getSettlement() != null) {
                    bestTarget = t.getSettlement();
                } else {
                    bestTarget = t.getFirstUnit();
                }
                worstThreat = unitThreat;
            }
        }
        
        if (threat > defenders && bestTarget != null) {
            AIUnit newDefenderAI = getBraveForSeekAndDestroy(is);
            if (newDefenderAI != null) {
                Tile targetTile = bestTarget.getTile();
                boolean targetIsSettlement = (targetTile.getSettlement() != null);
                boolean validUnitTarget = (!targetIsSettlement && isTargetValidForSeekAndDestroy(newDefenderAI.getUnit(), targetTile.getFirstUnit()));
                if (targetIsSettlement || validUnitTarget) {
                    newDefenderAI.setMission(new UnitSeekAndDestroyMission(getAIMain(), newDefenderAI, bestTarget));
                }
            }
        }
    }

    private AIUnit getBraveForSeekAndDestroy(final IndianSettlement indianSettlement) {
        final Iterator<Unit> it = indianSettlement.getOwnedUnitsIterator();
        while (it.hasNext()) {
            final AIUnit chosenOne = (AIUnit) getAIMain().getAIObject(it.next());
            if (chosenOne.getUnit().getLocation() instanceof Tile
                && (chosenOne.getMission() == null
                    || chosenOne.getMission() instanceof UnitWanderHostileMission)) {
                return chosenOne;
            }
        }
        return null;
    }

    
    private void secureColony(Colony colony) {
        GoodsType musketType = Specification.getSpecification().getGoodsType("model.goods.muskets");
        GoodsType horsesType = Specification.getSpecification().getGoodsType("model.goods.horses");
        final EquipmentType muskets = FreeCol.getSpecification().getEquipmentType("model.equipment.muskets");
        final EquipmentType horses = FreeCol.getSpecification().getEquipmentType("model.equipment.horses");

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
                    if (gw.getGoodsType() == musketType) {
                        made = true;
                    }
                }
                if (made == false) {
                    
                    ac
                            .addGoodsWish(new GoodsWish(getAIMain(), colony, (threat - olddefenders) * 50,
                                    musketType));
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
                    if (gw.getGoodsType() == horsesType) {
                        made = true;
                    }
                }
                if (made == false) {
                    
                    ac.addGoodsWish(new GoodsWish(getAIMain(), colony, (threat - defenders) * 50, horsesType));
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

    
    private void giveNormalMissions() {
        logger.finest("Entering method giveNormalMissions");

        int numberOfUnits = FreeCol.getSpecification().numberOfUnitTypes();
        
        Vector<ArrayList<Wish>> workerWishes = new Vector<ArrayList<Wish>>(numberOfUnits);
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
            
            
            if(unit.isNaval()){
            	continue;
            }

            if (unit.canCarryTreasure()) {
                aiUnit.setMission(new CashInTreasureTrainMission(getAIMain(), aiUnit));
                continue;
            }

            if (unit.hasAbility("model.ability.scoutIndianSettlement") &&
                       ScoutingMission.isValid(aiUnit)) {
                aiUnit.setMission(new ScoutingMission(getAIMain(), aiUnit));
                continue;
            }

            if (unit.isOffensiveUnit() || unit.isDefensiveUnit()){
            	Player owner = unit.getOwner();
            	boolean isPastStart = getGame().getTurn().getNumber() > 5
            			&& !owner.getSettlements().isEmpty();
            
            	if(!unit.isColonist() 
            			|| isPastStart
            			|| owner.isIndian()
            			|| owner.isREF()){
            		giveMilitaryMission(aiUnit);
            		continue;
            	}
            }

            
            
            
            
            boolean isPioneer = unit.hasAbility("model.ability.improveTerrain")
                                || unit.hasAbility("model.ability.expertPioneer");
            boolean isExpert = unit.getSkillLevel() > 0;
            if ((isPioneer || (isPioneerReq && !isExpert)) && PioneeringMission.isValid(aiUnit)) {
                aiUnit.setMission(new PioneeringMission(getAIMain(), aiUnit));
                isPioneerReq = false;
                continue;
            }

            if (unit.isColonist() & unit.getOwner().isEuropean()) {
                
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
                    if (colonyTile != null) {
                        bestTurns = unit.getTurnsToReach(colonyTile);
                    }
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

    
    private void bringGifts() {
        logger.finest("Entering method bringGifts");
        if (!getPlayer().isIndian()) {
            return;
        }
        for (IndianSettlement indianSettlement : getPlayer().getIndianSettlements()) {
            
            if (getRandom().nextInt(10) != 1) {
                continue;
            }
            int alreadyAssignedUnits = 0;
            Iterator<Unit> ownedUnits = indianSettlement.getOwnedUnitsIterator();
            while (ownedUnits.hasNext()) {
                if (((AIUnit) getAIMain().getAIObject(ownedUnits.next())).getMission() instanceof IndianBringGiftMission) {
                    alreadyAssignedUnits++;
                }
            }
            if (alreadyAssignedUnits > MAX_NUMBER_OF_GIFTS_BEING_DELIVERED) {
                continue;
            }
            
            ArrayList<Colony> nearbyColonies = new ArrayList<Colony>();
            Iterator<Position> it = getGame().getMap().getCircleIterator(indianSettlement.getTile().getPosition(), true,
                    MAX_DISTANCE_TO_BRING_GIFT);
            while (it.hasNext()) {
                Tile t = getGame().getMap().getTile(it.next());
                if (t.getColony() != null
                        && IndianBringGiftMission.isValidMission(getPlayer(), t.getColony().getOwner())) {
                    nearbyColonies.add(t.getColony());
                }
            }
            if (nearbyColonies.size() > 0) {
                Colony target = nearbyColonies.get(getRandom().nextInt(nearbyColonies.size()));
                Iterator<Unit> it2 = indianSettlement.getOwnedUnitsIterator();
                AIUnit chosenOne = null;
                while (it2.hasNext()) {
                    chosenOne = (AIUnit) getAIMain().getAIObject(it2.next());
                    if (chosenOne.getUnit().getLocation() instanceof Tile
                        && (chosenOne.getMission() == null
                            || chosenOne.getMission() instanceof UnitWanderHostileMission)) {
                        
                        PathNode pn = chosenOne.getUnit().findPath(indianSettlement.getTile(),
                                                                   target.getTile());
                        if (pn != null && pn.getTotalTurns() <= MAX_DISTANCE_TO_BRING_GIFT) {
                            chosenOne.setMission(new IndianBringGiftMission(getAIMain(), chosenOne, target));
                            break;
                        }
                    }
                }
            }
        }
    }

    
    private void demandTribute() {
        logger.finest("Entering method demandTribute");
        if (!getPlayer().isIndian()) {
            return;
        }
        for (IndianSettlement indianSettlement : getPlayer().getIndianSettlements()) {
            
            if (getRandom().nextInt(10) != 1) {
                continue;
            }
            int alreadyAssignedUnits = 0;
            Iterator<Unit> ownedUnits = indianSettlement.getOwnedUnitsIterator();
            while (ownedUnits.hasNext()) {
                if (((AIUnit) getAIMain().getAIObject(ownedUnits.next())).getMission() instanceof IndianDemandMission) {
                    alreadyAssignedUnits++;
                }
            }
            if (alreadyAssignedUnits > MAX_NUMBER_OF_DEMANDS) {
                continue;
            }
            
            ArrayList<Colony> nearbyColonies = new ArrayList<Colony>();
            Iterator<Position> it = getGame().getMap().getCircleIterator(indianSettlement.getTile().getPosition(), true,
                    MAX_DISTANCE_TO_MAKE_DEMANDS);
            while (it.hasNext()) {
                Tile t = getGame().getMap().getTile(it.next());
                if (t.getColony() != null) {
                    nearbyColonies.add(t. getColony());
                }
            }
            if (nearbyColonies.size() > 0) {
                int targetTension = Integer.MIN_VALUE;
                Colony target = null;
                for (int i = 0; i < nearbyColonies.size(); i++) {
                    Colony t = nearbyColonies.get(i);
                    Player to = t.getOwner();
                    if (getPlayer().getTension(to) == null ||
                        indianSettlement.getAlarm(to) == null) {
                        continue;
                    }
                    int tension = 1 + getPlayer().getTension(to).getValue()
                        + indianSettlement.getAlarm(to).getValue();
                    tension = getRandom().nextInt(tension);
                    if (tension > targetTension) {
                        targetTension = tension;
                        target = t;
                    }
                }
                if (target != null) {
                    Iterator<Unit> it2 = indianSettlement.getOwnedUnitsIterator();
                    AIUnit chosenOne = null;
                    while (it2.hasNext()) {
                        chosenOne = (AIUnit) getAIMain().getAIObject(it2.next());
                        if (chosenOne.getUnit().getLocation() instanceof Tile
                            && (chosenOne.getMission() == null
                                || chosenOne.getMission() instanceof UnitWanderHostileMission)) {
                            
                            PathNode pn = chosenOne.getUnit().findPath(indianSettlement.getTile(),
                                                                       target.getTile());
                            if (pn != null && pn.getTotalTurns() <= MAX_DISTANCE_TO_MAKE_DEMANDS) {
                                
                                
                                Player tp = target.getOwner();
                                int tension = 1 + getPlayer().getTension(tp).getValue()
                                    + indianSettlement.getAlarm(tp).getValue();
                                if (getRandom().nextInt(tension) > Tension.Level.HAPPY.getLimit()) {
                                    chosenOne.setMission(new IndianDemandMission(getAIMain(), chosenOne,
                                                                                 target));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    
    private void createAIGoodsInColonies() {
        logger.finest("Entering method createAIGoodsInColonies");
        if (!getPlayer().isEuropean()) {
            return;
        }
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
        
        
        value -= turns * 100;

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
        if (attackerPlayer.isEuropean()) {
            
            if (!atWar) {
                return false;
            }
        } else if (attackerPlayer.isIndian()) {
            
            
            if (!atWar && attackerPlayer.getTension(defenderPlayer)
                .getLevel().compareTo(Tension.Level.CONTENT) <= 0) {
                return false;
            }
        }
        return true;
    }

    
    void giveMilitaryMission(AIUnit aiUnit) {
        logger.finest("Entering method giveMilitaryMission");
        
        if (getPlayer().isIndian()) {
            aiUnit.setMission(new UnitWanderHostileMission(getAIMain(), aiUnit));
            return;
        }
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
                CostDeciders.avoidIllegal(),
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
        PathNode newTarget = map.search(unit, startTile, targetDecider,
                CostDeciders.avoidIllegal(), Integer.MAX_VALUE, carrier);
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

}
