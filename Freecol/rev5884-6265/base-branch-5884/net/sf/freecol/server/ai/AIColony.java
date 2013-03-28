

package net.sf.freecol.server.ai;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.AbstractGoods;
import net.sf.freecol.common.model.BuildableType;
import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.ColonyTile;
import net.sf.freecol.common.model.EquipmentType;
import net.sf.freecol.common.model.ExportData;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TileImprovement;
import net.sf.freecol.common.model.TileImprovementType;
import net.sf.freecol.common.model.TypeCountMap;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.model.UnitTypeChange;
import net.sf.freecol.common.model.Unit.Role;
import net.sf.freecol.common.model.UnitTypeChange.ChangeType;
import net.sf.freecol.common.model.WorkLocation;
import net.sf.freecol.common.networking.ClaimLandMessage;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.server.ai.mission.PioneeringMission;
import net.sf.freecol.server.ai.mission.TransportMission;
import net.sf.freecol.server.ai.mission.WorkInsideColonyMission;

import org.w3c.dom.Element;


public class AIColony extends AIObject {

    private static final Logger logger = Logger.getLogger(AIColony.class.getName());

    private static final EquipmentType toolsType = FreeCol.getSpecification()
        .getEquipmentType("model.equipment.tools");

    private static enum ExperienceUpgrade { NONE, SOME, EXPERT }

    
    private Colony colony;

    private ColonyPlan colonyPlan;

    private ArrayList<AIGoods> aiGoods = new ArrayList<AIGoods>();

    private ArrayList<Wish> wishes = new ArrayList<Wish>();

    private ArrayList<TileImprovementPlan> tileImprovementPlans = new ArrayList<TileImprovementPlan>();


    
    public AIColony(AIMain aiMain, Colony colony) {
        super(aiMain, colony.getId());

        this.colony = colony;
        colonyPlan = new ColonyPlan(aiMain, colony);        
    }

    
    public AIColony(AIMain aiMain, Element element) {
        super(aiMain, element.getAttribute("ID"));
        readFromXMLElement(element);
    }

    
    public AIColony(AIMain aiMain, XMLStreamReader in) throws XMLStreamException {
        super(aiMain, in.getAttributeValue(null, "ID"));
        readFromXML(in);
    }

    
    public AIColony(AIMain aiMain, String id) {
        this(aiMain, (Colony) aiMain.getGame().getFreeColGameObject(id));
    }

    
    public Colony getColony() {
        return colony;
    }

    
    public void dispose() {
        List<AIObject> disposeList = new ArrayList<AIObject>();
        for (AIGoods ag : aiGoods) {
            if (ag.getGoods().getLocation() == colony) {
                disposeList.add(ag);
            }
        }
        for(Wish w : wishes) {
            disposeList.add(w);
        }
        for(TileImprovementPlan ti : tileImprovementPlans) {
            disposeList.add(ti);
        }
        for(AIObject o : disposeList) {
            o.dispose();
        }
        super.dispose();
    }
    
    
    public Iterator<AIGoods> getAIGoodsIterator() {
        Iterator<AIGoods> agi = aiGoods.iterator();
        
        while (agi.hasNext()) {
            AIGoods ag = agi.next();
            if (ag.getGoods().getLocation() != colony) {
                agi.remove();
            }
        }
        return aiGoods.iterator();
    }

    
    public Iterator<Wish> getWishIterator() {
        return wishes.iterator();
    }

    
    public void createTileImprovementPlans() {

        Map<Tile, TileImprovementPlan> plans =
            new HashMap<Tile, TileImprovementPlan>();
        for (TileImprovementPlan plan : tileImprovementPlans) {
            plans.put(plan.getTarget(), plan);
        }
        for (WorkLocationPlan wlp : colonyPlan.getWorkLocationPlans()) {
            if (wlp.getWorkLocation() instanceof ColonyTile) {
                ColonyTile colonyTile = (ColonyTile) wlp.getWorkLocation();
                Tile target = colonyTile.getWorkTile();
                TileImprovementPlan plan = plans.get(target);
                if (plan == null) {
                    plan = wlp.createTileImprovementPlan();
                    if (plan != null) {
                        if (colonyTile.getUnit() != null) {
                            plan.setValue(2 * plan.getValue());
                        }
                        tileImprovementPlans.add(plan);
                        plans.put(target, plan);
                    }
                } else if (wlp.updateTileImprovementPlan(plan) == null) {
                    tileImprovementPlans.remove(plan);
                    plan.dispose();
                } else if (colonyTile.getUnit() != null) {
                    plan.setValue(2 * plan.getValue());
                }
            }
        }

        Tile centerTile = colony.getTile();
        TileImprovementPlan centerPlan = plans.get(centerTile);
        TileImprovementType type = TileImprovement
            .findBestTileImprovementType(centerTile, FreeCol.getSpecification()
                                         .getGoodsType("model.goods.food"));
        if (type == null) {
            if (centerPlan != null) {
                tileImprovementPlans.remove(centerPlan);
            }
        } else {
            if (centerPlan == null) {
                centerPlan = new TileImprovementPlan(getAIMain(), colony.getTile(), type, 30);
                tileImprovementPlans.add(0, centerPlan);
            } else {
                centerPlan.setType(type);
            }
        }

        Collections.sort(tileImprovementPlans);
    }

    
    public Iterator<TileImprovementPlan> getTileImprovementPlanIterator() {
        return tileImprovementPlans.iterator();
    }
    
    
    public boolean removeTileImprovementPlan(TileImprovementPlan plan){
        return tileImprovementPlans.remove(plan);
    }
    
    
    private void createWishes() {
        wishes.clear();
        int expertValue = 100;
        int goodsWishValue = 50;

        
        for (Unit unit : colony.getUnitList()) {
            if (unit.getWorkType() != null
                && unit.getWorkType() != unit.getType().getExpertProduction()) {
                UnitType expert = FreeCol.getSpecification().getExpertForProducing(unit.getWorkType());
                wishes.add(new WorkerWish(getAIMain(), colony, expertValue, expert, true));
            }
        }

        
        if (wishes.isEmpty()) {
            int newPopulation = colony.getUnitCount() + 1;
            if (colony.governmentChange(newPopulation) >= 0) {
                
                boolean needFood = colony.getFoodProduction()
                    <= newPopulation * Colony.FOOD_CONSUMPTION;
                
                UnitType expert = getNextExpert(needFood);
                wishes.add(new WorkerWish(getAIMain(), colony, expertValue / 5, expert, false));
            }
        }

        

        
        boolean badlyDefended = isBadlyDefended();
        if (badlyDefended) {
            UnitType bestDefender = null;
            for (UnitType unitType : FreeCol.getSpecification().getUnitTypeList()) {
                if ((bestDefender == null
                     || bestDefender.getDefence() < unitType.getDefence())
                    && !unitType.hasAbility("model.ability.navalUnit")
                    && unitType.isAvailableTo(colony.getOwner())) {
                    bestDefender = unitType;
                }
            }
            if (bestDefender != null) {
                wishes.add(new WorkerWish(getAIMain(), colony, expertValue, bestDefender, true));
            }
        }

        
        
        TypeCountMap<GoodsType> requiredGoods = new TypeCountMap<GoodsType>();

        
        if (colony.getCurrentlyBuilding() != null) {
            for (AbstractGoods goods : colony.getCurrentlyBuilding().getGoodsRequired()) {
                if (colony.getProductionNetOf(goods.getType()) == 0) {
                    requiredGoods.incrementCount(goods.getType(), goods.getAmount());
                }
            }
        }

        
        for (TileImprovementPlan plan : tileImprovementPlans) {
            for (AbstractGoods goods : plan.getType().getExpendedEquipmentType()
                     .getGoodsRequired()) {
                requiredGoods.incrementCount(goods.getType(), goods.getAmount());
            }
        }

        
        for (WorkLocation workLocation : colony.getWorkLocations()) {
            if (workLocation instanceof Building) {
                Building building = (Building) workLocation;
                GoodsType inputType = building.getGoodsInputType();
                if (inputType != null
                    && colony.getProductionNetOf(inputType) < building.getMaximumGoodsInput()) {
                    requiredGoods.incrementCount(inputType, 100);
                }
            }
        }

        
        for (GoodsType goodsType : FreeCol.getSpecification().getGoodsTypeList()) {
            if (goodsType.isBreedable()) {
                requiredGoods.incrementCount(goodsType, goodsType.getBreedingNumber());
            }
        }

        
        if (badlyDefended) {
            for (EquipmentType type : FreeCol.getSpecification().getEquipmentTypeList()) {
                if (type.isMilitaryEquipment()) {
                    for (Unit unit : colony.getUnitList()) {
                        if (unit.canBeEquippedWith(type)) {
                            for (AbstractGoods goods : type.getGoodsRequired()) {
                                requiredGoods.incrementCount(goods.getType(), goods.getAmount());
                            }
                            break;
                        }
                    }
                }
            }
        }

        for (GoodsType type : requiredGoods.keySet()) {
            GoodsType requiredType = type;
            while (requiredType != null && !requiredType.isStorable()) {
                requiredType = requiredType.getRawMaterial();
            }
            if (requiredType != null) {
                int amount = Math.min((requiredGoods.getCount(requiredType)
                                       - colony.getGoodsCount(requiredType)),
                                      colony.getWarehouseCapacity());
                if (amount > 0) {
                    int value = colonyCouldProduce(requiredType) ?
                        goodsWishValue / 10 : goodsWishValue;
                    wishes.add(new GoodsWish(getAIMain(), colony, value, amount, requiredType));
                }
            }
        }
        Collections.sort(wishes);
    }

    private boolean colonyCouldProduce(GoodsType goodsType) {
        if (goodsType.isBreedable()) {
            return colony.getGoodsCount(goodsType) >= goodsType.getBreedingNumber();
        } else if (goodsType.isFarmed()) {
            for (ColonyTile colonyTile : colony.getColonyTiles()) {
                if (colonyTile.getWorkTile().potential(goodsType, null) > 0) {
                    return true;
                }
            }
        } else {
            if (!colony.getBuildingsForProducing(goodsType).isEmpty()) {
                if (goodsType.getRawMaterial() == null) {
                    return true;
                } else {
                    return colonyCouldProduce(goodsType.getRawMaterial());
                }
            }
        }
        return false;
    }


    private UnitType getNextExpert(boolean onlyFood) {
        
        UnitType bestType = FreeCol.getSpecification().getUnitType("model.unit.freeColonist");
        for (WorkLocationPlan plan : colonyPlan.getSortedWorkLocationPlans()) {
            if (plan.getGoodsType().isFoodType() || !onlyFood) {
                WorkLocation location = plan.getWorkLocation();
                if (location instanceof ColonyTile) {
                    ColonyTile colonyTile = (ColonyTile) location;
                    if (colonyTile.getUnit() == null
                        && (colonyTile.getWorkTile().isLand()
                            || colony.hasAbility("model.ability.produceInWater"))) {
                        bestType = FreeCol.getSpecification()
                            .getExpertForProducing(plan.getGoodsType());
                        break;
                    }
                } else if (location instanceof Building) {
                    Building building = (Building) location;
                    if (building.getUnitCount() < building.getMaxUnits()) {
                        bestType = building.getExpertUnitType();
                        break;
                    }
                }
            }
        }
        return bestType;
    }

    private int getToolsRequired(BuildableType buildableType) {
        int toolsRequiredForBuilding = 0;
        if (buildableType != null) {
            for (AbstractGoods goodsRequired : buildableType.getGoodsRequired()) {
                if (goodsRequired.getType() == Goods.TOOLS) {
                    toolsRequiredForBuilding = goodsRequired.getAmount();
                    break;
                }
            }
        }
        return toolsRequiredForBuilding;
    }


    private int getHammersRequired(BuildableType buildableType) {
        int hammersRequiredForBuilding = 0;
        if (buildableType != null) {
            for (AbstractGoods goodsRequired : buildableType.getGoodsRequired()) {
                if (goodsRequired.getType() == Goods.HAMMERS) {
                    hammersRequiredForBuilding = goodsRequired.getAmount();
                    break;
                }
            }
        }
        return hammersRequiredForBuilding;
    }

    public boolean isBadlyDefended() {
        int defence = 0;
        for (Unit unit : colony.getTile().getUnitList()) {
            
            
            defence += unit.getType().getDefence();
            if (unit.isArmed()) {
                defence += 1;
            }
            if (unit.isMounted()) {
                defence += 1;
            }
        }

        
        return defence < 3 * colony.getUnitCount();
    }


    public void removeWish(Wish w) {
        wishes.remove(w);
    }

    
    public void addGoodsWish(GoodsWish gw) {
        wishes.add(gw);
    }

    
    public void removeAIGoods(AIGoods ag) {
        while (aiGoods.remove(ag)) { 
        }
    }

    
    public void createAIGoods() {
        int capacity = colony.getWarehouseCapacity();
        if (colony.hasAbility("model.ability.export")) {
            for (GoodsType goodsType : FreeCol.getSpecification().getGoodsTypeList()) {
                if (goodsType.isTradeGoods()) {
                    
                    colony.setExportData(new ExportData(goodsType, false, 0));
                } else if (!goodsType.isStorable()) {
                    
                    colony.setExportData(new ExportData(goodsType, false, 0));
                } else if (goodsType.isBreedable()) {
                    colony.setExportData(new ExportData(goodsType, true, capacity - 20));
                } else if (goodsType.isMilitaryGoods()) {
                    colony.setExportData(new ExportData(goodsType, true, capacity - 50));
                } else if (goodsType.isBuildingMaterial()) {
                    colony.setExportData(new ExportData(goodsType, true, Math.min(capacity, 250)));
                } else if (goodsType.isFoodType()) {
                    colony.setExportData(new ExportData(goodsType, false, 0));
                } else if (goodsType.isNewWorldGoodsType() || goodsType.isRefined()) {
                    colony.setExportData(new ExportData(goodsType, true, 0));
                } else {
                    colony.setExportData(new ExportData(goodsType, false, 0));
                }
            }
            aiGoods.clear();

        } else {

            ArrayList<AIGoods> newAIGoods = new ArrayList<AIGoods>();

            List<GoodsType> goodsList = FreeCol.getSpecification().getGoodsTypeList();
            loop: for (GoodsType goodsType : goodsList) {
                
                if (goodsType.isFoodType() || goodsType == Goods.LUMBER) {
                    continue;
                }
                
                if (!goodsType.isStorable()) {
                    continue;
                }
                
                if (goodsType.isMilitaryGoods()
                    && (colony.getProductionOf(goodsType) == 0
                        || (colony.getGoodsCount(goodsType)
                            < capacity - colony.getProductionOf(goodsType)))) {
                    continue;
                }

                
                for (Wish wish : wishes) {
                    if (wish instanceof GoodsWish
                        && ((GoodsWish) wish).getGoodsType() == goodsType) {
                        continue loop;
                    }
                }
                Building consumer = colony.getBuildingForConsuming(goodsType);
                if (consumer != null
                    && colony.getProductionOf(goodsType) < consumer.getGoodsInput()) {
                    continue;
                }

                
                
                if (goodsType == Goods.TOOLS && colony.getGoodsCount(Goods.TOOLS) > 0) {
                    if (colony.getProductionNetOf(Goods.TOOLS) > 0) {
                        final BuildableType currentlyBuilding = colony.getCurrentlyBuilding();
                        int requiredTools = getToolsRequired(currentlyBuilding);
                        int requiredHammers = getHammersRequired(currentlyBuilding);
                        int buildTurns = (requiredHammers - colony.getGoodsCount(Goods.HAMMERS)) /
                            (colony.getProductionOf(Goods.HAMMERS) + 1);
                        if (requiredTools > 0) {
                            if (colony.getWarehouseCapacity() > 100) {
                                requiredTools += 100;
                            }
                            int toolsProductionTurns = requiredTools / colony.getProductionNetOf(Goods.TOOLS);
                            if (buildTurns <= toolsProductionTurns + 1) {
                                continue;
                            }
                        } else if (colony.getWarehouseCapacity() > 100
                                   && colony.getGoodsCount(Goods.TOOLS) <= 100) {
                            continue;
                        }
                    } else {
                        continue;
                    }
                }

                if (colony.getGoodsCount(goodsType) > 0) {
                    List<AIGoods> alreadyAdded = new ArrayList<AIGoods>();
                    for (int j = 0; j < aiGoods.size(); j++) {
                        AIGoods ag = aiGoods.get(j);
                        if (ag == null) {
                            logger.warning("aiGoods == null");
                        } else if (ag.getGoods() == null) {
                            logger.warning("aiGoods.getGoods() == null");
                            if (ag.isUninitialized()) {
                                logger.warning("AIGoods uninitialized: " + ag.getId());
                            }
                        }
                        if (ag != null && ag.getGoods() != null && ag.getGoods().getType() == goodsType
                            && ag.getGoods().getLocation() == colony) {
                            alreadyAdded.add(ag);
                        }
                    }

                    int amountRemaining = colony.getGoodsCount(goodsType);
                    for (int i = 0; i < alreadyAdded.size(); i++) {
                        AIGoods oldGoods = alreadyAdded.get(i);
                        if (oldGoods.getGoods().getLocation() != colony) {
                            continue;
                        }
                        if (oldGoods.getGoods().getAmount() < 100 && oldGoods.getGoods().getAmount() < amountRemaining) {
                            int goodsAmount = Math.min(100, amountRemaining);
                            oldGoods.getGoods().setAmount(goodsAmount);
                            if (amountRemaining >= colony.getWarehouseCapacity()
                                && oldGoods.getTransportPriority() < AIGoods.IMPORTANT_DELIVERY) {
                                oldGoods.setTransportPriority(AIGoods.IMPORTANT_DELIVERY);
                            } else if (goodsAmount == 100 && oldGoods.getTransportPriority() < AIGoods.FULL_DELIVERY) {
                                oldGoods.setTransportPriority(AIGoods.FULL_DELIVERY);
                            }
                            amountRemaining -= goodsAmount;
                            newAIGoods.add(oldGoods);
                        } else if (oldGoods.getGoods().getAmount() > amountRemaining) {
                            if (amountRemaining == 0) {
                                if (oldGoods.getTransport() != null
                                    && oldGoods.getTransport().getMission() instanceof TransportMission) {
                                    ((TransportMission) oldGoods.getTransport().getMission())
                                    .removeFromTransportList(oldGoods);
                                }
                                oldGoods.dispose();
                            } else {
                                oldGoods.getGoods().setAmount(amountRemaining);
                                newAIGoods.add(oldGoods);
                                amountRemaining = 0;
                            }
                        } else {
                            newAIGoods.add(oldGoods);
                            amountRemaining -= oldGoods.getGoods().getAmount();
                        }
                    }
                    while (amountRemaining > 0) {
                        if (amountRemaining >= 100) {
                            AIGoods newGoods = new AIGoods(getAIMain(), colony, goodsType, 100, getColony().getOwner()
                                                           .getEurope());
                            if (amountRemaining >= colony.getWarehouseCapacity()) {
                                newGoods.setTransportPriority(AIGoods.IMPORTANT_DELIVERY);
                            } else {
                                newGoods.setTransportPriority(AIGoods.FULL_DELIVERY);
                            }
                            newAIGoods.add(newGoods);
                            amountRemaining -= 100;
                        } else {
                            AIGoods newGoods = new AIGoods(getAIMain(), colony, goodsType, amountRemaining, getColony()
                                                           .getOwner().getEurope());
                            newAIGoods.add(newGoods);
                            amountRemaining = 0;
                        }
                    }
                }
            }

            aiGoods.clear();
            Iterator<AIGoods> nai = newAIGoods.iterator();
            while (nai.hasNext()) {
                AIGoods ag = nai.next();
                int i;
                for (i = 0; i < aiGoods.size() && aiGoods.get(i).getTransportPriority() > ag.getTransportPriority(); i++)
                    ;
                aiGoods.add(i, ag);
            }
        }
    }


    
    public int getAvailableGoods(GoodsType goodsType) {
        int materialsRequiredForBuilding = 0;
        if (colony.getCurrentlyBuilding() != null) {
            for (AbstractGoods materials : colony.getCurrentlyBuilding().getGoodsRequired()) {
                if (materials.getType() == goodsType) {
                    materialsRequiredForBuilding = materials.getAmount();
                    break;
                }
            }
        }

        return Math.max(0, colony.getGoodsCount(goodsType) - materialsRequiredForBuilding);
    }


    
    public boolean canBuildEquipment(EquipmentType equipmentType) {
        if (getColony().canBuildEquipment(equipmentType)) {
            for (AbstractGoods goods : equipmentType.getGoodsRequired()) {
                int breedingNumber = goods.getType().getBreedingNumber();
                if (breedingNumber != GoodsType.NO_BREEDING &&
                    getColony().getGoodsCount(goods.getType()) < goods.getAmount() + breedingNumber) {
                    return false;
                }
                if (getAvailableGoods(goods.getType()) < goods.getAmount()) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }


    
    private ColonyTile getBestVacantTile(Connection connection,
                                         Unit unit, GoodsType goodsType) {
        ColonyTile colonyTile = colony.getVacantColonyTileFor(unit, goodsType, true);
        if (colonyTile == null) return null;

        
        Tile tile = colonyTile.getWorkTile();
        if (tile.getOwningSettlement() != colony) {
            ClaimLandMessage message = new ClaimLandMessage(tile, colony, 0);
            try {
                connection.sendAndWait(message.toXMLElement());
            } catch (IOException e) {
                logger.warning("Could not send \""
                               + message.getXMLElementTagName()
                               + "\"-message:" + e.getMessage());
            }
            if (tile.getOwningSettlement() != colony) {
                return null; 
            }
        }

        return colonyTile;
    }

    
    public void rearrangeWorkers(Connection connection) {
        colonyPlan.create();
        
        

        checkForUnequippedExpertPioneer();
        
        checkForUnarmedExpertSoldier();

        List<Unit> units = new ArrayList<Unit>();
        List<WorkLocationPlan> workLocationPlans = colonyPlan.getWorkLocationPlans();
        Collections.sort(workLocationPlans);

        
        Iterator<Unit> ui = colony.getUnitIterator();
        while (ui.hasNext()) {
            Unit unit = ui.next();
            units.add(unit);
            
            
            unit.putOutsideColony();
        }

        
        placeExpertsInWorkPlaces(units, workLocationPlans);

        boolean workerAdded = true;
        while (workerAdded) {
            workerAdded = false;
            
            int food = colony.getFoodProduction() - colony.getFoodConsumption();
            for (int i = 0; i < workLocationPlans.size() && food < 2; i++) {
                WorkLocationPlan wlp = workLocationPlans.get(i);
                WorkLocation wl = wlp.getWorkLocation();
                if (wlp.getGoodsType() == Goods.FOOD
                    && (((ColonyTile) wl).getWorkTile().isLand()
                        || colony.hasAbility("model.ability.produceInWater"))) {
                    Unit bestUnit = null;
                    int bestProduction = 0;
                    Iterator<Unit> unitIterator = units.iterator();
                    while (unitIterator.hasNext()) {
                        Unit unit = unitIterator.next();
                        int production = ((ColonyTile) wlp.getWorkLocation()).getProductionOf(unit,
                                                                                              Goods.FOOD);
                        if (production > 1
                            && (bestUnit == null || production > bestProduction || production == bestProduction
                                && unit.getSkillLevel() < bestUnit.getSkillLevel())) {
                            bestUnit = unit;
                            bestProduction = production;
                        }
                    }
                    if (bestUnit != null && wlp.getWorkLocation().canAdd(bestUnit)) {
                        
                        
                        bestUnit.work(wlp.getWorkLocation());
                        bestUnit.setWorkType(wlp.getGoodsType());
                        units.remove(bestUnit);
                        workLocationPlans.remove(wlp);
                        workerAdded = true;
                        food = colony.getFoodProduction() - colony.getFoodConsumption();
                    }
                }
            }
            
            if (food >= 2) {
                for (int i = 0; i < workLocationPlans.size(); i++) {
                    WorkLocationPlan wlp = workLocationPlans.get(i);
                    if (wlp.getGoodsType() != Goods.FOOD) {
                        Unit bestUnit = null;
                        int bestProduction = 0;
                        Iterator<Unit> unitIterator = units.iterator();
                        while (unitIterator.hasNext()) {
                            Unit unit = unitIterator.next();
                            int production = 0;
                            WorkLocation location = wlp.getWorkLocation();
                            if (location instanceof ColonyTile) {
                                production = ((ColonyTile) wlp.getWorkLocation()).getProductionOf(unit,
                                                                                                  wlp.getGoodsType());
                            } else if (location instanceof Building) {
                                production = ((Building) location).getUnitProductivity(unit);
                            }
                            if (bestUnit == null || production > bestProduction || production == bestProduction
                                && unit.getSkillLevel() < bestUnit.getSkillLevel()) {
                                bestUnit = unit;
                                bestProduction = production;
                            }
                        }
                        if (bestUnit != null && wlp.getWorkLocation().canAdd(bestUnit)) {
                            
                            
                            bestUnit.work(wlp.getWorkLocation());
                            bestUnit.setWorkType(wlp.getGoodsType());
                            units.remove(bestUnit);
                            workLocationPlans.remove(wlp);
                            workerAdded = true;
                            food = colony.getFoodProduction() - colony.getFoodConsumption();
                        }
                    }
                }
            }
        }

        
        int food = colony.getFoodProduction() - colony.getFoodConsumption();
        while (food < 0 && colony.getGoodsCount(Goods.FOOD) + food * 3 < 0) {
            WorkLocation bestPick = null;
            for (WorkLocation wl : colony.getWorkLocations()) {
                if (wl.getUnitCount() > 0) {
                    if (wl instanceof ColonyTile) {
                        ColonyTile ct = (ColonyTile) wl;
                        Unit u = ct.getUnit();
                        if (ct.getUnit().getWorkType() != Goods.FOOD) {
                            int uProduction = ct.getProductionOf(u, Goods.FOOD);
                            if (uProduction > 1) {
                                if (bestPick == null || bestPick instanceof Building) {
                                    bestPick = wl;
                                } else {
                                    ColonyTile bpct = (ColonyTile) bestPick;
                                    int bestPickProduction = bpct.getProductionOf(bpct.getUnit(), Goods.FOOD);
                                    if (uProduction > bestPickProduction
                                        || (uProduction == bestPickProduction && u.getSkillLevel() < bpct.getUnit()
                                            .getSkillLevel())) {
                                        bestPick = wl;
                                    }
                                }
                            } else {
                                if (bestPick == null) {
                                    bestPick = wl;
                                } 
                                
                            }
                        }
                    } else { 
                        if (bestPick == null
                            || (bestPick instanceof Building && ((Building) wl).getProduction() < ((Building) bestPick)
                                .getProduction())) {
                            bestPick = wl;
                        }
                    }
                }
            }
            if (bestPick == null) {
                break;
            }
            if (bestPick instanceof ColonyTile) {
                ColonyTile ct = (ColonyTile) bestPick;
                Unit u = ct.getUnit();
                if (ct.getProductionOf(u, Goods.FOOD) > 1) {
                    u.setWorkType(Goods.FOOD);
                } else {
                    u.putOutsideColony();
                    AIUnit au = (AIUnit) getAIMain().getAIObject(u);
                    if (au.getMission() instanceof WorkInsideColonyMission) {
                        au.setMission(null);
                    }
                }
            } else { 
                Building b = (Building) bestPick;
                Iterator<Unit> unitIterator = b.getUnitIterator();
                Unit bestUnit = unitIterator.next();
                while (unitIterator.hasNext()) {
                    Unit u = unitIterator.next();
                    if (u.getType().getExpertProduction() != u.getWorkType()) {
                        bestUnit = u;
                        break;
                    }
                }
                bestUnit.putOutsideColony();
                AIUnit au = (AIUnit) getAIMain().getAIObject(bestUnit);
                if (au.getMission() instanceof WorkInsideColonyMission) {
                    au.setMission(null);
                }
            }

            food = colony.getFoodProduction() - colony.getFoodConsumption();
        }

        
        for (WorkLocation wl : colony.getWorkLocations()) {
            while (wl.getUnitCount() > 0 && wl instanceof Building && ((Building) wl).getProductionNextTurn() <= 0) {
                Iterator<Unit> unitIterator = wl.getUnitIterator();
                Unit bestPick = unitIterator.next();
                while (unitIterator.hasNext()) {
                    Unit u = unitIterator.next();
                    if (u.getType().getExpertProduction() != u.getWorkType()) {
                        bestPick = u;
                        break;
                    }
                }
                GoodsType rawMaterial = bestPick.getWorkType().getRawMaterial();
                ColonyTile ct = (rawMaterial == null) ? null
                    : getBestVacantTile(connection, bestPick, rawMaterial);
                if (ct != null) {
                    bestPick.work(ct);
                    bestPick.setWorkType(rawMaterial);
                } else {
                    Building th = colony.getBuildingForProducing(Goods.BELLS);
                    if (th.canAdd(bestPick)) {
                        bestPick.work(th);
                    } else {
                        ct = getBestVacantTile(connection, bestPick, Goods.FOOD);
                        if (ct != null) {
                            bestPick.work(ct);
                            bestPick.setWorkType(Goods.FOOD);
                        } else {
                            bestPick.putOutsideColony();
                            if (bestPick.getLocation() == wl) {
                                break;
                            }
                        }
                    }
                }
            }
        }

        

        
        
        List<GoodsType> goodsList = FreeCol.getSpecification().getGoodsTypeList();
        for (GoodsType goodsType : goodsList) {
            int production = colony.getProductionNetOf(goodsType);
            int in_stock = colony.getGoodsCount(goodsType);
            if (Goods.FOOD != goodsType
                && goodsType.isStorable()
                && production + in_stock > colony.getWarehouseCapacity()) {
                Iterator<Unit> unitIterator = colony.getUnitIterator();
                int waste = production + in_stock - colony.getWarehouseCapacity();
                while (unitIterator.hasNext() && waste > 0){
                    Unit unit = unitIterator.next();
                    if (unit.getWorkType() == goodsType) {
                        final Location oldLocation = unit.getLocation();
                        unit.putOutsideColony();
                        boolean working = false;
                        waste = colony.getGoodsCount(goodsType) + colony.getProductionNetOf(goodsType)
                            - colony.getWarehouseCapacity();
                        int best = 0;
                        for (GoodsType goodsType2 : goodsList) {
                            if (!goodsType2.isFarmed())
                                continue;
                            ColonyTile bestTile = getBestVacantTile(connection, unit, goodsType2);
                            int production2 = (bestTile == null ? 0 :
                                               bestTile.getProductionOf(unit, goodsType2));
                            if (production2 > best && production2 + colony.getGoodsCount(goodsType2)
                                + colony.getProductionNetOf(goodsType2) < colony.getWarehouseCapacity()){
                                if (working){
                                    unit.putOutsideColony();
                                }
                                unit.work(bestTile);
                                unit.setWorkType(goodsType2);
                                best = production2;
                                working = true;
                            }
                        }
                        if (!working){
                            
                            
                            
                            
                            
                            
                            unit.setLocation(oldLocation);
                        }
                    }
                }
            }
        }


        
        for (int i = 0; i < workLocationPlans.size(); i++) {
            WorkLocationPlan wlp = workLocationPlans.get(i);
            WorkLocation wl = wlp.getWorkLocation();
            if (wlp.getGoodsType() == Goods.FOOD
                && (((ColonyTile) wl).getWorkTile().isLand()
                    || colony.hasAbility("model.ability.produceInWater"))) {
                Unit bestUnit = null;
                int bestProduction = 0;
                Iterator<Unit> unitIterator = units.iterator();
                while (unitIterator.hasNext()) {
                    Unit unit = unitIterator.next();
                    int production = ((ColonyTile) wlp.getWorkLocation()).getProductionOf(unit,
                                                                                          Goods.FOOD);
                    if (production > 1
                        && (bestUnit == null || production > bestProduction || production == bestProduction
                            && unit.getSkillLevel() < bestUnit.getSkillLevel())) {
                        bestUnit = unit;
                        bestProduction = production;
                    }
                }
                if (bestUnit != null && wlp.getWorkLocation().canAdd(bestUnit)) {
                    
                    bestUnit.work(wlp.getWorkLocation());
                    bestUnit.setWorkType(wlp.getGoodsType());
                    units.remove(bestUnit);
                    workLocationPlans.remove(wlp);
                }
            }
        }

        
        Iterator<Unit> ui6 = units.iterator();
        while (ui6.hasNext()) {
            Unit u = ui6.next();
            u.putOutsideColony();
            AIUnit au = (AIUnit) getAIMain().getAIObject(u);
            if (au.getMission() instanceof WorkInsideColonyMission) {
                au.setMission(null);
            }
        }

        
        
        
        decideBuildable(connection);
        createTileImprovementPlans();
        createWishes();
        colonyPlan.adjustProductionAndManufacture();
        checkConditionsForHorseBreed();
        
        if (this.colony.getUnitCount()<=0) {
            
            throw new IllegalStateException("Colony " + colony.getName() + " contains no units!");
        }
    }

    private void checkForUnequippedExpertPioneer() {
        if (colony.getUnitCount() < 2) {
            return;
        }
        
        for(Unit unit : colony.getUnitList()){
            if(!unit.hasAbility("model.ability.expertPioneer")){
                continue;
            }
            AIUnit aiu = (AIUnit) (AIUnit) this.getAIMain().getAIObject(unit);
            if( aiu == null){
                continue;
            }
            
            if(!PioneeringMission.isValid(aiu)){
                return;
            }
            unit.putOutsideColony();
            aiu.setMission(new PioneeringMission(getAIMain(), aiu));
            return;
        }
    }


    public static Unit bestUnitForWorkLocation(Collection<Unit> units, WorkLocation workLocation,
                                               GoodsType goodsType) {

        if (units == null || units.isEmpty() || workLocation == null
            || (workLocation instanceof ColonyTile && goodsType == null)
            || (workLocation instanceof Building
                && ((Building) workLocation).getUnitCount()
                >= ((Building) workLocation).getMaxUnits())) {
            return null;
        } else {
            Tile tile = null;
            Building building = null;
            UnitType expert = null;
            if (workLocation instanceof ColonyTile) {
                tile = ((ColonyTile) workLocation).getWorkTile();
                expert = FreeCol.getSpecification().getExpertForProducing(goodsType);
            } else if (workLocation instanceof Building) {
                building = (Building) workLocation;
                expert = building.getExpertUnitType();
            } else {
                return null;
            }

            Unit bestUnit = null;
            int production = 0;
            int bestProduction = 0;
            int experience = 0;
            int wastedExperience = 0;
            ExperienceUpgrade canBeUpgraded = ExperienceUpgrade.NONE;
            for (Unit unit : units) {
                if (unit.getType() == expert) {
                    
                    return unit;
                } else {
                    if (tile != null) {
                        production = unit.getProductionOf(goodsType,
                                                          tile.potential(goodsType, unit.getType()));
                    } else if (building != null) {
                        production = building.getUnitProductivity(unit);
                    }
                    if (production > bestProduction) {
                        
                        bestUnit = unit;
                        bestProduction = production;
                        canBeUpgraded = getExperienceUpgrade(unit, expert);
                        if (canBeUpgraded == ExperienceUpgrade.NONE) {
                            experience = 0;
                            wastedExperience = 0;
                        } else {
                            if (unit.getWorkType() == goodsType) {
                                experience = unit.getExperience();
                                wastedExperience = 0;
                            } else {
                                experience = 0;
                                wastedExperience = unit.getExperience();
                            }
                        }
                    } else if (production == bestProduction) {
                        ExperienceUpgrade upgradeable = getExperienceUpgrade(unit, expert);
                        if ((upgradeable == ExperienceUpgrade.EXPERT
                             && (canBeUpgraded != ExperienceUpgrade.EXPERT
                                 || (unit.getWorkType() == goodsType
                                     && unit.getExperience() > experience)
                                 || (unit.getWorkType() != goodsType
                                     && unit.getExperience() < wastedExperience)))
                            || (upgradeable == ExperienceUpgrade.NONE
                                && canBeUpgraded == ExperienceUpgrade.SOME)) {
                            
                            
                            bestUnit = unit;
                            canBeUpgraded = upgradeable;
                            if (unit.getWorkType() == goodsType) {
                                experience = unit.getExperience();
                                wastedExperience = 0;
                            } else {
                                experience = 0;
                                wastedExperience = unit.getExperience();
                            }
                        }
                    }
                }
            }
            if (bestProduction == 0) {
                return null;
            } else {
                return bestUnit;
            }
        }
    }

    private static ExperienceUpgrade getExperienceUpgrade(Unit unit, UnitType expert) {
        ExperienceUpgrade result = ExperienceUpgrade.NONE;
        for (UnitTypeChange change : unit.getType().getTypeChanges()) {
            if (change.asResultOf(ChangeType.EXPERIENCE)) {
                if (expert == change.getNewUnitType()) {
                    return ExperienceUpgrade.EXPERT;
                } else {
                    result = ExperienceUpgrade.SOME;
                }
            }
        }
        return result;
    }



    
    private void checkForUnarmedExpertSoldier() {
        EquipmentType musketsEqType = FreeCol.getSpecification().getEquipmentType("model.equipment.muskets");
        
        for(Unit unit : colony.getUnitList()){
            if(colony.getUnitCount() == 1){
                return;
            }
            
            if(!unit.hasAbility("model.ability.expertSoldier")){
                continue;
            }
            
            
            if(colony.canBuildEquipment(musketsEqType)){
                unit.putOutsideColony();
                continue;
            }
            
            
            for(Unit outsideUnit : colony.getTile().getUnitList()){
                if(outsideUnit.isArmed() 
                        && !outsideUnit.hasAbility("model.ability.expertSoldier")){
                    unit.putOutsideColony();
                    break;
                }
            }
        }
    }

    
    void checkConditionsForHorseBreed() {
        GoodsType horsesType = FreeCol.getSpecification().getGoodsType("model.goods.horses");
        EquipmentType horsesEqType = FreeCol.getSpecification().getEquipmentType("model.equipment.horses");
        GoodsType reqGoodsType = horsesType.getRawMaterial();
        
        
        if(colony.getGoodsCount(horsesType) >= horsesType.getBreedingNumber()){
            return;
        }
        
        int foodProdAvail = colony.getProductionOf(reqGoodsType) - colony.getFoodConsumptionByType(reqGoodsType);
        
        if(foodProdAvail <= 0){
            return;
        }
        
        
        for(Unit u : colony.getTile().getUnitList()){
            if(!u.isMounted()){
                continue;
            }
            u.removeEquipment(horsesEqType);
            return;
        }
    }

    private void placeExpertsInWorkPlaces(List<Unit> units, List<WorkLocationPlan> workLocationPlans) {
        boolean canProduceInWater = colony.hasAbility("model.ability.produceInWater");
        
        
        Iterator<Unit> uit = new ArrayList<Unit>(units).iterator();
        while (uit.hasNext()) {
            Unit unit = uit.next();
            
            GoodsType expertProd = unit.getType().getExpertProduction();
            
            
            if(expertProd == null){
                continue;
            }
            
            WorkLocationPlan bestWorkPlan = null;
            int bestProduction = 0;
                        
            Iterator<WorkLocationPlan> wlpIterator = workLocationPlans.iterator();
            while (wlpIterator.hasNext()) {
                WorkLocationPlan wlp = wlpIterator.next();
                WorkLocation wl = wlp.getWorkLocation();
                
                GoodsType locGoods = wlp.getGoodsType();
                
                boolean isColonyTile = wl instanceof ColonyTile;
                boolean isLand = true;
                if(isColonyTile){
                    isLand = ((ColonyTile) wl).getWorkTile().isLand();
                }
                
                
                if(isColonyTile && !isLand && !canProduceInWater){
                    continue;
                }
                
                
                if(expertProd != locGoods){
                    continue;
                }
                
                
                if(!isColonyTile){
                    bestWorkPlan = wlp;
                    break;
                }
                
                int planProd = wlp.getProductionOf(expertProd);
                if(bestWorkPlan == null || bestProduction < planProd){
                    bestWorkPlan = wlp;
                    bestProduction = planProd;
                    
                }
            }

            if(bestWorkPlan != null){
                
                
                unit.work(bestWorkPlan.getWorkLocation());
                unit.setWorkType(bestWorkPlan.getGoodsType());
                workLocationPlans.remove(bestWorkPlan);
                units.remove(unit);
            }
        }
    }

    
    private void decideBuildable(Connection connection) {
        Iterator<BuildableType> bi = colonyPlan.getBuildable();
        BuildableType buildable = (bi.hasNext()) ? bi.next() : null;
        if (buildable != null && colony.canBuild(buildable)
            && buildable != colony.getCurrentlyBuilding()) {
            Element element = Message.createNewRootElement("setBuildQueue");
            element.setAttribute("colony", colony.getId());
            element.setAttribute("size", "1");
            element.setAttribute("x0", buildable.getId());
            try {
                connection.sendAndWait(element);
            } catch (IOException e) {
                logger.warning("Could not send \"setBuildQueue\"-message.");
            }
            logger.fine("Colony " + colony.getId()
                        + " will build " + buildable.getId());
        }
    }

    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("ID", getId());

        Iterator<AIGoods> aiGoodsIterator = aiGoods.iterator();
        while (aiGoodsIterator.hasNext()) {
            AIGoods ag = aiGoodsIterator.next();
            if (ag == null) {
                logger.warning("ag == null");
                continue;
            }
            if (ag.getId() == null) {
                logger.warning("ag.getId() == null");
                continue;
            }
            out.writeStartElement(AIGoods.getXMLElementTagName() + "ListElement");
            out.writeAttribute("ID", ag.getId());
            out.writeEndElement();
        }

        Iterator<Wish> wishesIterator = wishes.iterator();
        while (wishesIterator.hasNext()) {
            Wish w = wishesIterator.next();
            if (!w.shouldBeStored()) {
                continue;
            }
            if (w instanceof WorkerWish) {
                out.writeStartElement(WorkerWish.getXMLElementTagName() + "WishListElement");
            } else if (w instanceof GoodsWish) {
                out.writeStartElement(GoodsWish.getXMLElementTagName() + "WishListElement");
            } else {
                logger.warning("Unknown type of wish.");
                continue;
            }
            out.writeAttribute("ID", w.getId());
            out.writeEndElement();
        }

        Iterator<TileImprovementPlan> TileImprovementPlanIterator = tileImprovementPlans.iterator();
        while (TileImprovementPlanIterator.hasNext()) {
            TileImprovementPlan ti = TileImprovementPlanIterator.next();
            out.writeStartElement(TileImprovementPlan.getXMLElementTagName() + "ListElement");
            out.writeAttribute("ID", ti.getId());
            out.writeEndElement();
        }

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        colony = (Colony) getAIMain().getFreeColGameObject(in.getAttributeValue(null, "ID"));
        if (colony == null) {
            throw new NullPointerException("Could not find Colony with ID: " + in.getAttributeValue(null, "ID"));
        }

        aiGoods.clear();
        wishes.clear();

        colonyPlan = new ColonyPlan(getAIMain(), colony);
        colonyPlan.create();

        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if (in.getLocalName().equals(AIGoods.getXMLElementTagName() + "ListElement")) {
                AIGoods ag = (AIGoods) getAIMain().getAIObject(in.getAttributeValue(null, "ID"));
                if (ag == null) {
                    ag = new AIGoods(getAIMain(), in.getAttributeValue(null, "ID"));
                }
                aiGoods.add(ag);
                in.nextTag();
            } else if (in.getLocalName().equals(WorkerWish.getXMLElementTagName() + "WishListElement")) {
                Wish w = (Wish) getAIMain().getAIObject(in.getAttributeValue(null, "ID"));
                if (w == null) {
                    w = new WorkerWish(getAIMain(), in.getAttributeValue(null, "ID"));
                }
                wishes.add(w);
                in.nextTag();
            } else if (in.getLocalName().equals(GoodsWish.getXMLElementTagName() + "WishListElement")) {
                Wish w = (Wish) getAIMain().getAIObject(in.getAttributeValue(null, "ID"));
                if (w == null) {
                    w = new GoodsWish(getAIMain(), in.getAttributeValue(null, "ID"));
                }
                wishes.add(w);
                in.nextTag();
            } else if (in.getLocalName().equals(TileImprovementPlan.getXMLElementTagName() + "ListElement")) {
                TileImprovementPlan ti = (TileImprovementPlan) getAIMain().getAIObject(in.getAttributeValue(null, "ID"));
                if (ti == null) {
                    ti = new TileImprovementPlan(getAIMain(), in.getAttributeValue(null, "ID"));
                }
                tileImprovementPlans.add(ti);
                in.nextTag();
            } else {
                logger.warning("Unknown tag name: " + in.getLocalName());
            }
        }

        if (!in.getLocalName().equals(getXMLElementTagName())) {
            logger.warning("Expected end tag, received: " + in.getLocalName());
        }
    }
    
    public ColonyPlan getColonyPlan() {
        return colonyPlan;
    }

    
    public static String getXMLElementTagName() {
        return "aiColony";
    }
}
