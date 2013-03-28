

package net.sf.freecol.server.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import net.sf.freecol.common.Specification;
import net.sf.freecol.common.model.AbstractGoods;
import net.sf.freecol.common.model.BuildableType;
import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.BuildingType;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.ColonyTile;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Market;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.UnitType;

import net.sf.freecol.server.ai.ColonyProfile.ProfileType;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class ColonyPlan {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ColonyPlan.class.getName());

    
    public static final int DOCKS_PRIORITY = 10;
    public static final int ARTILLERY_PRIORITY = 10;
    public static final int CHURCH_PRIORITY = 15;
    public static final int WAGON_TRAIN_PRIORITY = 20;
    public static final int SCHOOL_PRIORITY = 30;
    public static final int UPGRADE_PRIORITY = 50;
    public static final int CUSTOMS_HOUSE_PRIORITY = 60;
    public static final int TOWN_HALL_PRIORITY = 75;
    public static final int WAREHOUSE_PRIORITY = 90;
    public static final int BUILDING_PRIORITY = 120;

    
    
    private static final int MAX_LEVEL = 3;
    private static final int MIN_RAW_GOODS_THRESHOLD = 20;
    
    private static final GoodsType hammersType = Specification.getSpecification().getGoodsType("model.goods.hammers");
    private static final GoodsType toolsType = Specification.getSpecification().getGoodsType("model.goods.tools");
    private static final GoodsType lumberType = Specification.getSpecification().getGoodsType("model.goods.lumber");
    private static final GoodsType oreType = Specification.getSpecification().getGoodsType("model.goods.ore");

    
    private Colony colony;

    private AIMain aiMain;

    private ArrayList<WorkLocationPlan> workLocationPlans = new ArrayList<WorkLocationPlan>();

    private GoodsType primaryRawMaterial = null;
    
    private GoodsType secondaryRawMaterial = null;

    
    private ColonyProfile profile = new ColonyProfile();

    
    
    
    public ColonyPlan(AIMain aiMain, Colony colony) {
        if (colony == null) {
            throw new IllegalArgumentException("Parameter 'colony' must not be 'null'.");
        }
        this.aiMain = aiMain;
        this.colony = colony;
        selectProfile();
    }

    
    public ColonyPlan(AIMain aiMain, Element element) {
        this.aiMain = aiMain;
        readFromXMLElement(element);
    }

    
    public List<WorkLocationPlan> getWorkLocationPlans() {
        return new ArrayList<WorkLocationPlan>(workLocationPlans);
    }

    
    public List<WorkLocationPlan> getSortedWorkLocationPlans() {
        List<WorkLocationPlan> workLocationPlans = getWorkLocationPlans();
        Collections.sort(workLocationPlans);

        return workLocationPlans;
    }


    
    public final ColonyProfile getProfile() {
        return profile;
    }

    
    public final void setProfile(final ColonyProfile newProfile) {
        this.profile = newProfile;
    }

    public class Buildable implements Comparable<Buildable> {
        BuildableType type;
        int priority;

        public Buildable(BuildableType type, int priority) {
            this.type = type;
            this.priority = priority;
        }

        public int compareTo(Buildable other) {
            return other.priority - priority;
        }
    }

    
    public Iterator<BuildableType> getBuildable() {

        
        if (profile.getType() == ProfileType.OUTPOST) {
            List<BuildableType> result = Collections.emptyList();
            return result.iterator();
        }

        List<Buildable> buildables = new ArrayList<Buildable>();
        
        List<BuildingType> docks = new ArrayList<BuildingType>();
        List<BuildingType> customs = new ArrayList<BuildingType>();
        List<BuildingType> builders = new ArrayList<BuildingType>();
        List<BuildingType> defence = new ArrayList<BuildingType>();
        List<BuildingType> military = new ArrayList<BuildingType>();
        List<BuildingType> schools = new ArrayList<BuildingType>();
        List<BuildingType> churches = new ArrayList<BuildingType>();
        List<BuildingType> townHalls = new ArrayList<BuildingType>();

        for (BuildingType type : Specification.getSpecification().getBuildingTypeList()) {
            if (type.hasAbility("model.ability.produceInWater")) {
                docks.add(type);
            } 
            if (type.hasAbility("model.ability.export")) {
                customs.add(type);
            } 
            if (type.hasAbility("model.ability.teach")) {
                schools.add(type);
            }
            if (!type.getModifierSet("model.modifier.defence").isEmpty()) {
                defence.add(type);
            }
            if (type.getProducedGoodsType() != null) {
                GoodsType output = type.getProducedGoodsType();
                if (output.isBuildingMaterial()) {
                    builders.add(type);
                }
                if (output.isMilitaryGoods()) {
                    military.add(type);
                }
                if (output.isLibertyType()) {
                    townHalls.add(type);
                }
                if (output.isImmigrationType()) {
                    churches.add(type);
                }
            }
        }

        List<UnitType> buildableDefenders = new ArrayList<UnitType>();
        UnitType bestWagon = null;
        for (UnitType unitType : Specification.getSpecification().getUnitTypeList()) {
            if (unitType.getDefence() > UnitType.DEFAULT_DEFENCE
                && !unitType.hasAbility("model.ability.navalUnit")
                && !unitType.getGoodsRequired().isEmpty()) {
                buildableDefenders.add(unitType);
            }
            if (unitType.hasAbility("model.ability.carryGoods")
                && !unitType.hasAbility("model.ability.navalUnit")
                && colony.canBuild(unitType)
                && (bestWagon == null || unitType.getSpace() > bestWagon.getSpace())) {
                bestWagon = unitType;
            }
        }

        int wagonTrains = 0;
        for (Unit unit: colony.getOwner().getUnits()) {
            if (unit.hasAbility("model.ability.carryGoods") && !unit.isNaval()) {
                wagonTrains++;
            }
        }

        if (colony.isLandLocked()) {
            
            int landLockedColonies = 0;
            for (Colony otherColony : colony.getOwner().getColonies()) {
                if (otherColony.isLandLocked()) {
                    landLockedColonies++;
                }
            }
            if (bestWagon != null && landLockedColonies > wagonTrains) {
                buildables.add(new Buildable(bestWagon, WAGON_TRAIN_PRIORITY
                                             * (landLockedColonies - wagonTrains)));
            }
        } else if (!colony.hasAbility("model.ability.produceInWater")) {
            
            int potential = 0;
            for (ColonyTile colonyTile : colony.getColonyTiles()) {
                Tile tile = colonyTile.getWorkTile();
                if (!tile.isLand()) {
                    for (AbstractGoods goods : tile.getSortedPotential()) {
                        if (goods.getType().isFoodType()) {
                            potential += goods.getAmount();
                            break;
                        }
                    }
                }
            }
            for (BuildingType buildingType : docks) {
                if (colony.canBuild(buildingType)) {
                    buildables.add(new Buildable(buildingType, potential * DOCKS_PRIORITY));
                    break;
                }
            }
        }


        
        Iterator<WorkLocationPlan> wlpIt = getSortedWorkLocationPlans().iterator();
        while (wlpIt.hasNext()) {
            WorkLocationPlan wlp = wlpIt.next();
            if (wlp.getWorkLocation() instanceof Building) {
                Building b = (Building) wlp.getWorkLocation();
                if (b.canBuildNext()) {
                    buildables.add(new Buildable(b.getType().getUpgradesTo(), UPGRADE_PRIORITY));
                }

                
                
                GoodsType outputType = b.getGoodsOutputType();
                if (outputType != null) {
                    for (BuildingType otherType : Specification.getSpecification()
                             .getBuildingTypeList()) {
                        if (!otherType.getModifierSet(outputType.getId()).isEmpty()
                            && colony.canBuild(otherType)) {
                            int priority = (colony.getBuilding(otherType) == null) ?
                                2 * UPGRADE_PRIORITY : UPGRADE_PRIORITY;
                            buildables.add(new Buildable(otherType, priority));
                        }
                    }
                }
            }
        }

        
        
        
        if (!colony.hasAbility("model.ability.export")) {
            for (BuildingType buildingType : customs) {
                if (colony.canBuild(buildingType)) {
                    buildables.add(new Buildable(buildingType, CUSTOMS_HOUSE_PRIORITY));
                    break;
                }
            }
        }

        
        for (BuildingType buildingType : builders) {
            if (colony.canBuild(buildingType)) {
                int priority = BUILDING_PRIORITY;
                
                if (buildingType.getProducedGoodsType() != null
                    && buildingType.getProducedGoodsType().isMilitaryGoods()) {
                    priority /= 2;
                }
                buildables.add(new Buildable(buildingType, priority));
            }
        }

        
        Building building = colony.getWarehouse();
        if (building.canBuildNext()) {
            int priority = colony.getGoodsContainer()
                .hasReachedCapacity(colony.getWarehouseCapacity()) ?
                2 * WAREHOUSE_PRIORITY : WAREHOUSE_PRIORITY;
            buildables.add(new Buildable(building.getType().getUpgradesTo(), priority));
        } else if (bestWagon != null && wagonTrains < 4 * colony.getOwner().getColonies().size()) {
            buildables.add(new Buildable(bestWagon, WAGON_TRAIN_PRIORITY));
        }

        
        for (BuildingType buildingType : defence) {
            if (colony.canBuild(buildingType)) {
                int priority = (colony.getBuilding(buildingType) == null
                                || profile.getType() == ProfileType.LARGE
                                || profile.getType() == ProfileType.CAPITAL) ?
                    2 * UPGRADE_PRIORITY : UPGRADE_PRIORITY;
                buildables.add(new Buildable(buildingType, priority));
            }
        }

        
        for (BuildingType buildingType : military) {
            if (colony.canBuild(buildingType)) {
                if (colony.getBuilding(buildingType) == null
                    && (buildingType.getConsumedGoodsType() == null 
                        || buildingType.getConsumedGoodsType().isFarmed())) {
                    buildables.add(new Buildable(buildingType, UPGRADE_PRIORITY));
                } else {
                    buildables.add(new Buildable(buildingType, UPGRADE_PRIORITY / 2));
                }
            }
        }

        
        
        if (((AIColony) aiMain.getAIObject(colony)).isBadlyDefended()) {
            for (UnitType unitType : buildableDefenders) {
                if (colony.canBuild(unitType)) {
                    int priority = (profile.getType() == ProfileType.LARGE
                                    || profile.getType() == ProfileType.CAPITAL) ?
                        2 * ARTILLERY_PRIORITY : ARTILLERY_PRIORITY;
                    buildables.add(new Buildable(unitType, priority));
                    break;
                }
            }
        }        

        
        if (profile.getType() != ProfileType.SMALL) {
            for (BuildingType buildingType : schools) {
                if (colony.canBuild(buildingType)) {
                    int priority = SCHOOL_PRIORITY;
                    if (colony.getBuilding(buildingType) != null) {
                        if (profile.getType() == ProfileType.MEDIUM) {
                            priority /= 2;
                        }
                        if (buildingType.getUpgradesTo() == null) {
                            if (profile.getType() != ProfileType.CAPITAL) {
                                continue;
                            }
                        }
                    }
                    buildables.add(new Buildable(buildingType, priority));
                }
            }
        }

        
        
        for (BuildingType buildingType : townHalls) {
            if (colony.canBuild(buildingType)) {
                int priority = (colony.getBuilding(buildingType) == null) ?
                    2 * TOWN_HALL_PRIORITY : TOWN_HALL_PRIORITY;
                buildables.add(new Buildable(buildingType, priority));
            }
        }

        
        for (BuildingType buildingType : churches) {
            if (colony.canBuild(buildingType)) {
                int priority = (colony.getBuilding(buildingType) == null) ?
                    2 * CHURCH_PRIORITY : CHURCH_PRIORITY;
                buildables.add(new Buildable(buildingType, priority));
            }
        }


        Collections.sort(buildables);
        List<BuildableType> result = new ArrayList<BuildableType>();
        Set<BuildableType> found = new HashSet<BuildableType>();
        for (Buildable buildable : buildables) {
            if (!found.contains(buildable.type)) {
                result.add(buildable.type);
                found.add(buildable.type);
            }
        }
        return result.iterator();
    }

    
    public AIMain getAIMain() {
        return aiMain;
    }

    
    public Game getGame() {
        return aiMain.getGame();
    }

    
    public void create() {
        
        workLocationPlans.clear();
        if (profile.getType() == ProfileType.OUTPOST) {
            GoodsType goodsType = profile.getPreferredProduction().get(0);
            workLocationPlans.add(new WorkLocationPlan(getAIMain(),
                                                       getBestTileToProduce(goodsType),
                                                       goodsType));
            return;
        }
                
        Building townHall = colony.getBuildingForProducing(Goods.BELLS);
        
        
        for (ColonyTile ct : colony.getColonyTiles()) {

            if (ct.getWorkTile().getOwningSettlement() != null &&
                ct.getWorkTile().getOwningSettlement() != colony || ct.isColonyCenterTile()) {
                continue;
            }

            GoodsType goodsType = getBestGoodsToProduce(ct.getWorkTile());
            WorkLocationPlan wlp = new WorkLocationPlan(getAIMain(), ct, goodsType);
            workLocationPlans.add(wlp);
        }
        
        
        GoodsType buildingReq = null;
        GoodsType buildingRawMat = null;
        Building buildingReqProducer = null;
        
        buildingReq = getBuildingReqGoods();
        
        if(buildingReq != null){
            if(buildingReq == hammersType){
                buildingRawMat = lumberType;
            }
            else{
                buildingRawMat = oreType;
            }
            buildingReqProducer = colony.getBuildingForProducing(buildingReq);
        }

        
        
        boolean buildingRawMatReq = buildingRawMat != null 
                                    && colony.getGoodsCount(buildingRawMat) < MIN_RAW_GOODS_THRESHOLD
                                    && getProductionOf(buildingRawMat) <= 0; 
        
        if(buildingRawMatReq) {
            WorkLocationPlan bestChoice = null;
            int highestPotential = 0;

            Iterator<WorkLocationPlan> wlpIterator = workLocationPlans.iterator();
            while (wlpIterator.hasNext()) {
                WorkLocationPlan wlp = wlpIterator.next();
                
                if (wlp.getWorkLocation() instanceof ColonyTile
                    && ((ColonyTile) wlp.getWorkLocation()).getWorkTile().potential(buildingRawMat, null) > highestPotential) {
                    highestPotential = ((ColonyTile) wlp.getWorkLocation()).getWorkTile().potential(buildingRawMat, null);
                    bestChoice = wlp;
                }
            }
            if (highestPotential > 0) {
                
                
                assert bestChoice != null;
                bestChoice.setGoodsType(buildingRawMat);
            }
        }

        
        primaryRawMaterial = null;
        secondaryRawMaterial = null;
        int primaryRawMaterialProduction = 0;
        int secondaryRawMaterialProduction = 0;
        List<GoodsType> goodsTypeList = Specification.getSpecification().getGoodsTypeList();
        for (GoodsType goodsType : goodsTypeList) {
            
            
            if (goodsType.getProducedMaterial() == null 
                    || goodsType.getProducedMaterial() == hammersType) {
                continue;
            }
            if (getProductionOf(goodsType) > primaryRawMaterialProduction) {
                secondaryRawMaterial = primaryRawMaterial;
                secondaryRawMaterialProduction = primaryRawMaterialProduction;
                primaryRawMaterial = goodsType;
                primaryRawMaterialProduction = getProductionOf(goodsType);
            } else if (getProductionOf(goodsType) > secondaryRawMaterialProduction) {
                secondaryRawMaterial = goodsType;
                secondaryRawMaterialProduction = getProductionOf(goodsType);
            }
        }

        
        
        
        Iterator<WorkLocationPlan> wlpIterator = workLocationPlans.iterator();
        while (wlpIterator.hasNext()) {
            WorkLocationPlan wlp = wlpIterator.next();
            if (!(wlp.getWorkLocation() instanceof ColonyTile)) {
                continue;
            }
            if (wlp.getGoodsType() == primaryRawMaterial || wlp.getGoodsType() == secondaryRawMaterial
                    || wlp.getGoodsType() == Goods.LUMBER || wlp.getGoodsType() == Goods.ORE
                    || wlp.getGoodsType() == Goods.SILVER) {
                continue;
            }
            
            if (((ColonyTile) wlp.getWorkLocation()).getWorkTile().potential(Goods.FOOD, null) <= 2) {
                if (wlp.getGoodsType() == null) {
                    
                    wlpIterator.remove();
                } else if (wlp.getProductionOf(wlp.getGoodsType()) <= 2) {
                    
                    wlpIterator.remove();
                }
                continue;
            }

            wlp.setGoodsType(Goods.FOOD);
        }

        
        
        
        if(buildingReq != null && 
            (getProductionOf(buildingRawMat) > 0 
              || colony.getGoodsCount(buildingRawMat) > 0)){
            WorkLocationPlan wlp = new WorkLocationPlan(getAIMain(),
                    colony.getBuildingForProducing(buildingReq), buildingReq);
            workLocationPlans.add(wlp);
        }

        
        WorkLocationPlan townHallWlp = new WorkLocationPlan(getAIMain(), townHall, Goods.BELLS);
        workLocationPlans.add(townHallWlp);

        
        if (primaryRawMaterial != null) {
            GoodsType producedGoods = primaryRawMaterial.getProducedMaterial();
            Building b = colony.getBuildingForProducing(producedGoods);
            if (b != null) {
                WorkLocationPlan wlp = new WorkLocationPlan(getAIMain(), b, producedGoods);
                workLocationPlans.add(wlp);
            }
        }

        
        if (secondaryRawMaterial != null 
                && getFoodProduction() < workLocationPlans.size() * Colony.FOOD_CONSUMPTION 
                && secondaryRawMaterial.isNewWorldGoodsType()) {
            Iterator<WorkLocationPlan> wlpIterator2 = workLocationPlans.iterator();
            while (wlpIterator2.hasNext()) {
                WorkLocationPlan wlp = wlpIterator2.next();
                if (wlp.getWorkLocation() instanceof ColonyTile && wlp.getGoodsType() == secondaryRawMaterial) {
                    Tile t = ((ColonyTile) wlp.getWorkLocation()).getWorkTile();
                    
                    if (t.getMaximumPotential(Goods.FOOD, null) > 2) {
                        wlp.setGoodsType(Goods.FOOD);
                    } else {
                        wlpIterator2.remove();
                    }
                }
            }
        }

        
        if (getFoodProduction() < workLocationPlans.size() * Colony.FOOD_CONSUMPTION) {
            Iterator<WorkLocationPlan> wlpIterator2 = workLocationPlans.iterator();
            while (wlpIterator2.hasNext() && getFoodProduction() < workLocationPlans.size() * Colony.FOOD_CONSUMPTION) {
                WorkLocationPlan wlp = wlpIterator2.next();
                if (wlp.getWorkLocation() instanceof ColonyTile && wlp.getGoodsType() == primaryRawMaterial) {
                    Tile t = ((ColonyTile) wlp.getWorkLocation()).getWorkTile();
                    
                    if (t.getMaximumPotential(Goods.FOOD, null) > 2) {
                        wlp.setGoodsType(Goods.FOOD);
                    } else {
                        wlpIterator2.remove();
                    }
                }
            }
        }

        
        if (getFoodProduction() < workLocationPlans.size() * Colony.FOOD_CONSUMPTION) {
            Iterator<WorkLocationPlan> wlpIterator2 = workLocationPlans.iterator();
            while (wlpIterator2.hasNext() && getFoodProduction() < workLocationPlans.size() * Colony.FOOD_CONSUMPTION) {
                WorkLocationPlan wlp = wlpIterator2.next();
                if (wlp.getWorkLocation() instanceof Building) {
                    Building b = (Building) wlp.getWorkLocation();
                    if ( b != buildingReqProducer && b != townHall) {
                        wlpIterator2.remove();
                    }
                }
            }
        }

        
        
        
        
        GoodsType buildMatToGo = buildingReq;
        if(colony.getGoodsCount(buildingRawMat) > 0){
            buildMatToGo = buildingRawMat;
        }
        if (getFoodProduction() < workLocationPlans.size() * Colony.FOOD_CONSUMPTION) {
            Iterator<WorkLocationPlan> wlpIterator2 = workLocationPlans.iterator();
            while (wlpIterator2.hasNext() && getFoodProduction() < workLocationPlans.size() * Colony.FOOD_CONSUMPTION) {
                WorkLocationPlan wlp = wlpIterator2.next();
                if (wlp.getWorkLocation() instanceof ColonyTile && wlp.getGoodsType() == buildMatToGo) {
                    wlpIterator2.remove();
                }
            }
            
            if (getFoodProduction() < workLocationPlans.size() * Colony.FOOD_CONSUMPTION) {
                buildMatToGo = (buildMatToGo == buildingRawMat)? buildingReq : buildingRawMat;
                
                wlpIterator2 = workLocationPlans.iterator();
                while (wlpIterator2.hasNext() && getFoodProduction() < workLocationPlans.size() * Colony.FOOD_CONSUMPTION) {
                    WorkLocationPlan wlp = wlpIterator2.next();
                    if (wlp.getWorkLocation() instanceof ColonyTile && wlp.getGoodsType() == buildMatToGo) {
                        wlpIterator2.remove();
                    }
                }
            }
        }        
        
        
        
        
        
        if(getFoodProduction() < workLocationPlans.size() * Colony.FOOD_CONSUMPTION + 2){
            return;
        }

        int primaryWorkers = 1;
        int secondaryWorkers = 0;
        int builders = 1;
        int gunsmiths = 0;
        boolean colonistAdded = true;
        
        while (colonistAdded) {
            boolean blacksmithAdded = false;

            
            if (getFoodProduction() >= workLocationPlans.size() * Colony.FOOD_CONSUMPTION + 2 &&
                secondaryRawMaterial != null &&
                12 * secondaryWorkers + 6 <= getProductionOf(secondaryRawMaterial) &&
                secondaryWorkers <= MAX_LEVEL) {
                GoodsType producedGoods = secondaryRawMaterial.getProducedMaterial();
                Building b = colony.getBuildingForProducing(producedGoods);
                if (b != null) {
                    WorkLocationPlan wlp = new WorkLocationPlan(getAIMain(), b, producedGoods);
                    workLocationPlans.add(wlp);
                    colonistAdded = true;
                    secondaryWorkers++;
                    if (secondaryRawMaterial == Goods.ORE) {
                        blacksmithAdded = true;
                    }
                }
            }

            
            if (getFoodProduction() >= workLocationPlans.size() * Colony.FOOD_CONSUMPTION + 2 && primaryRawMaterial != null
                    && 12 * primaryWorkers + 6 <= getProductionOf(primaryRawMaterial)
                    && primaryWorkers <= MAX_LEVEL) {
                GoodsType producedGoods = primaryRawMaterial.getProducedMaterial();
                Building b = colony.getBuildingForProducing(producedGoods);
                if (b != null) {
                    WorkLocationPlan wlp = new WorkLocationPlan(getAIMain(), b, producedGoods);
                    workLocationPlans.add(wlp);
                    colonistAdded = true;
                    primaryWorkers++;
                    if (primaryRawMaterial == Goods.ORE) {
                        blacksmithAdded = true;
                    }
                }
            }

            
            if (blacksmithAdded && getFoodProduction() >= workLocationPlans.size() * Colony.FOOD_CONSUMPTION + 2
                    && gunsmiths < MAX_LEVEL) {
                Building b = colony.getBuildingForProducing(Goods.MUSKETS);
                if (b != null) {
                    WorkLocationPlan wlp = new WorkLocationPlan(getAIMain(), b, Goods.MUSKETS);
                    workLocationPlans.add(wlp);
                    colonistAdded = true;
                    gunsmiths++;
                }
            }

            
            if (getFoodProduction() >= workLocationPlans.size() * Colony.FOOD_CONSUMPTION + 2
                    && buildingReqProducer != null 
                    && buildingReqProducer.getProduction() * builders <= getProductionOf(buildingRawMat) 
                    && buildingReqProducer.getMaxUnits() < builders) {
                WorkLocationPlan wlp = new WorkLocationPlan(getAIMain(), buildingReqProducer, buildingReq);
                workLocationPlans.add(wlp);
                colonistAdded = true;
                builders++;
            }

            

            colonistAdded = false;
        }

        
        
        
    }

    
    public int getProductionOf(GoodsType goodsType) {
        int amount = 0;

        Iterator<WorkLocationPlan> wlpIterator = workLocationPlans.iterator();
        while (wlpIterator.hasNext()) {
            WorkLocationPlan wlp = wlpIterator.next();
            amount += wlp.getProductionOf(goodsType);
        }

        
        if (goodsType == colony.getTile().primaryGoods() ||
            goodsType == colony.getTile().secondaryGoods()) {
            amount += colony.getTile().getMaximumPotential(goodsType, null);
        }

        return amount;
    }

    
    public int getFoodProduction() {
        int amount = 0;
        for (GoodsType foodType : Specification.getSpecification().getGoodsFood()) {
            amount += getProductionOf(foodType);
        }

        return amount;
    }

    
    private GoodsType getBestGoodsToProduce(Tile t) {
        if (t.hasResource()) {
            return t.getTileItemContainer().getResource().getBestGoodsType();
        } else {
            List<AbstractGoods> sortedPotentials = t.getSortedPotential();
            if (sortedPotentials.isEmpty()) {
                return null;
            } else {
                return sortedPotentials.get(0).getType();
            }
        }
    }

    private ColonyTile getBestTileToProduce(GoodsType goodsType) {
        int bestProduction = -1;
        ColonyTile bestTile = null;
        for (ColonyTile ct : colony.getColonyTiles()) {
            Tile tile = ct.getWorkTile();
            if ((tile.getOwningSettlement() == null
                 || tile.getOwningSettlement() == colony)
                && !ct.isColonyCenterTile()) {
                int production = tile.potential(goodsType, null);
                if (bestTile == null || bestProduction < production) {
                    bestTile = ct;
                    bestProduction = production;
                }
            }
        }
        if (bestProduction > 0) {
            return bestTile;
        } else {
            return null;
        }
    }

    public class Production {
        ColonyTile colonyTile;
        GoodsType goodsType;

        public Production(ColonyTile ct, GoodsType gt) {
            colonyTile = ct;
            goodsType = gt;
        }
    }

    public Production getBestProduction(UnitType unitType) {
        Market market = colony.getOwner().getMarket();
        Production bestProduction = null;
        int value = -1;
        for (ColonyTile ct : colony.getColonyTiles()) {
            Tile tile = ct.getWorkTile();
            if ((tile.getOwningSettlement() == null
                 || tile.getOwningSettlement() == colony)
                && !ct.isColonyCenterTile()) {
                for (GoodsType goodsType : Specification.getSpecification().getFarmedGoodsTypeList()) {
                    int production = market.getSalePrice(goodsType, tile.potential(goodsType, unitType));
                    if (bestProduction == null || value < production) {
                        value = production;
                        bestProduction = new Production(ct, goodsType);
                    }
                }
            }
        }
        return bestProduction;
    }

    
    public void adjustProductionAndManufacture(){
        List<GoodsType> rawMatList = new ArrayList<GoodsType>(); 
    
        if(getBuildingReqGoods() == hammersType){
            rawMatList.add(lumberType);
        }
        rawMatList.add(oreType);
        
        if (primaryRawMaterial != null
                && primaryRawMaterial != lumberType
                && primaryRawMaterial != oreType
                && !primaryRawMaterial.isFoodType()) {
            rawMatList.add(primaryRawMaterial);
        }
        
        if (secondaryRawMaterial != null
                && secondaryRawMaterial != lumberType
                && secondaryRawMaterial != oreType
                && !secondaryRawMaterial.isFoodType()) {
            rawMatList.add(secondaryRawMaterial);
        }
        
        for(GoodsType rawMat : rawMatList){
            GoodsType producedGoods = rawMat.getProducedMaterial();
            if(producedGoods == null){
                continue;
            }
            adjustProductionAndManufactureFor(rawMat,producedGoods);
        }
    }
    
    public void adjustProductionAndManufactureFor(GoodsType rawMat, GoodsType producedGoods){
        Building factory = colony.getBuildingForProducing(producedGoods);
        if(factory == null){
            return;
        }
        
        List<Unit> producers = new ArrayList<Unit>();
        int stockRawMat = colony.getGoodsCount(rawMat);
       
        for(ColonyTile t : colony.getColonyTiles()){
            if(t.isColonyCenterTile()){
                continue;
            }
            Unit u = t.getUnit();
            if(u == null){
                continue;
            }
            if(u.getWorkType() != rawMat){
                continue;
            }
            producers.add(u); 
        }
        
        if(producers.size() == 0){
            return;
        }

        
        Comparator<Unit> comp = new Comparator<Unit>(){
                public int compare(Unit u1, Unit u2){
                    GoodsType goodsType = u1.getWorkType();
                    int prodU1 = ((ColonyTile) u1.getLocation()).getProductionOf(u1, goodsType);
                    int prodU2 = ((ColonyTile) u2.getLocation()).getProductionOf(u2, goodsType);
                    
                    if(prodU1 > prodU2){
                        return 1;
                    }
                    if(prodU1 < prodU2){
                        return -1;
                    }
                    return 0;
                }
        };
        Collections.sort(producers, comp);
        
        
        Iterator<Unit> iter = new ArrayList<Unit>(producers).iterator();
        while(iter.hasNext()){
            
            if(stockRawMat < 50 && producers.size() < 2){
                return;
            }
            
            if(factory.getUnitCount() == factory.getMaxUnits()){
                return;
            }
            Unit u = iter.next();
            
            if(!factory.canAdd(u.getType())){
                continue;
            }

            
            int rawProd = colony.getProductionNextTurn(rawMat) - ((ColonyTile)u.getWorkTile()).getProductionOf(u, rawMat);
            int mfnProd = colony.getProductionNextTurn(producedGoods) + factory.getAdditionalProductionNextTurn(u);
            if(stockRawMat < 50 && rawProd < mfnProd){
                return;
            }
            
            u.work(factory);
            u.setWorkType(producedGoods);
            producers.remove(u);
        }
    }
    
    public GoodsType getBuildingReqGoods(){
        BuildableType currBuild = colony.getCurrentlyBuilding();
        if(currBuild == null){
            return null;
        }
        
        if(colony.getGoodsCount(hammersType) < currBuild.getAmountRequiredOf(hammersType)){
            return hammersType;
        }
        else{
            return toolsType;
        }
    }
    
    public GoodsType getPrimaryRawMaterial(){
        return primaryRawMaterial;
    }

    public GoodsType getSecondaryRawMaterial(){
        return secondaryRawMaterial;
    }
    
    
    public Colony getColony() {
        return colony;
    }

    private void selectProfile() {
        int size = colony.getUnitCount();
        if (size < 4) {
            profile.setType(ProfileType.SMALL);
        } else if (size > 8) {
            profile.setType(ProfileType.LARGE);
        } else if (size > 12) {
            profile.setType(ProfileType.CAPITAL);
        }
    }

    
    public Element toXMLElement(Document document) {
        Element element = document.createElement(getXMLElementTagName());

        element.setAttribute("ID", colony.getId());

        return element;
    }

    
    public void readFromXMLElement(Element element) {
        colony = (Colony) getAIMain().getFreeColGameObject(element.getAttribute("ID"));
        
        selectProfile();
    }

    
    public static String getXMLElementTagName() {
        return "colonyPlan";
    }
    
    
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ColonyPlan for " + colony.getName() + " " + colony.getTile().getPosition());
        sb.append("\n\nPROFILE:\n");
        sb.append(profile.getType().toString());
        sb.append("\n");
        for (GoodsType goodsType : profile.getPreferredProduction()) {
            sb.append(goodsType.getName());
            sb.append("\n");
        }
        sb.append("\n\nWORK LOCATIONS:\n");
        for (WorkLocationPlan p : getSortedWorkLocationPlans()) {
            sb.append(p.getGoodsType().getName() + " (" + p.getWorkLocation() + ")\n");
        }
        sb.append("\n\nBUILD QUEUE:\n");
        final Iterator<BuildableType> it = getBuildable();
        while (it.hasNext()) {
            final BuildableType b = it.next();
            sb.append(b.getName());
            sb.append('\n');
        }
        return sb.toString();
    }
}
