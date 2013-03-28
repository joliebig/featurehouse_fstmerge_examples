

package net.sf.freecol.common.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.Specification;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.PlayerExploredTile;
import net.sf.freecol.common.model.Region;
import net.sf.freecol.common.model.Tile;

import org.w3c.dom.Element;


public final class Colony extends Settlement implements Nameable, PropertyChangeListener {

    private static final Logger logger = Logger.getLogger(Colony.class.getName());

    public static final int LIBERTY_PER_REBEL = 200;
    public static final int FOOD_PER_COLONIST = 200;

    public static final Ability HAS_PORT = new Ability("model.ability.hasPort");

    public static final FreeColGameObjectType SOL_MODIFIER_SOURCE = 
        new FreeColGameObjectType("model.source.solModifier");

    public static enum ColonyChangeEvent {
        POPULATION_CHANGE,
        PRODUCTION_CHANGE,
        BONUS_CHANGE,
        WAREHOUSE_CHANGE,
        BUILD_QUEUE_CHANGE
    }

    public static enum NoBuildReason {
        NONE,
        NOT_BUILDABLE,
        POPULATION_TOO_SMALL,
        MISSING_BUILD_ABILITY,
        MISSING_ABILITY,
        WRONG_UPGRADE
    }


    
    private final List<ColonyTile> colonyTiles = new ArrayList<ColonyTile>();

    
    private final java.util.Map<String, Building> buildingMap = new HashMap<String, Building>();

    
    private final java.util.Map<String, ExportData> exportData = new HashMap<String, ExportData>();

    
    private int sonsOfLiberty;

    
    private int oldSonsOfLiberty;

    
    private int tories;

    
    private int oldTories;

    
    private int productionBonus;

    
    private int immigration;

    
    private int liberty;

    
    private boolean landLocked = true;

    
    private int unitCount = -1;

    
    private int lastVisited = -1;

    
    private List<BuildableType> buildQueue = new ArrayList<BuildableType>();
    

    
    public Colony(Game game, Player owner, String name, Tile tile) {
        super(game, owner, name, tile);
        goodsContainer = new GoodsContainer(game, this);
        goodsContainer.addPropertyChangeListener(this);
        sonsOfLiberty = 0;
        oldSonsOfLiberty = 0;
        final Map map = game.getMap();
        tile.setOwner(owner);
        if (!tile.hasRoad()) {
            TileImprovement road = new TileImprovement(game, tile, FreeCol.getSpecification()
                                                       .getTileImprovementType("model.improvement.Road"));
            road.setTurnsToComplete(0);
            road.setVirtual(true);
            tile.add(road);
        }

        ColonyTile colonyTile = new ColonyTile(game, this, tile);
        colonyTile.addPropertyChangeListener(this);
        colonyTiles.add(colonyTile);
        for (Direction direction : Direction.values()) {
            Tile t = map.getNeighbourOrNull(direction, tile);
            if (t == null) {
                continue;
            }
            if (t.getOwner() == null) {
                t.setOwner(owner);
            }
            colonyTile = new ColonyTile(game, this, t);
            colonyTile.addPropertyChangeListener(this);
            colonyTiles.add(colonyTile);
            if (t.getType().isWater()) {
                landLocked = false;
            }
        }
        if (landLocked) {
            buildQueue.add(FreeCol.getSpecification().getBuildingType("model.building.Warehouse"));
        } else {
            buildQueue.add(FreeCol.getSpecification().getBuildingType("model.building.Docks"));
            featureContainer.addAbility(HAS_PORT);
        }
        Building building;
        List<BuildingType> buildingTypes = FreeCol.getSpecification().getBuildingTypeList();
        for (BuildingType buildingType : buildingTypes) {
            if ((buildingType.getUpgradesFrom() == null
                 && buildingType.getGoodsRequired().isEmpty())
                || isFree(buildingType)) {
                building = new Building(getGame(), this, buildingType);
                building.addPropertyChangeListener(this);
                addBuilding(building);
            }
        }
    }

    
    public Colony(Game game, XMLStreamReader in) throws XMLStreamException {
        super(game, in);
        readFromXML(in);
    }

    
    public Colony(Game game, Element e) {
        super(game, e);
        readFromXMLElement(e);
    }

    
    public Colony(Game game, String id) {
        super(game, id);
    }

    
    private boolean isFree(BuildingType buildingType) {
        float value = owner.getFeatureContainer()
            .applyModifier(100f, "model.modifier.buildingPriceBonus",
                           buildingType, getGame().getTurn());
        return (value == 0f && canBuild(buildingType));
    }

    
    public void addBuilding(final Building building) {
        BuildingType buildingType = building.getType().getFirstLevel();
        buildingMap.put(buildingType.getId(), building);
        featureContainer.add(building.getType().getFeatureContainer());
    }


    
    public boolean canReducePopulation() {
        return getUnitCount() > 
            FeatureContainer.applyModifierSet(0f, getGame().getTurn(),
                                              getModifierSet("model.modifier.minimumColonySize"));
    }

    
    public void updatePopulation(int difference) {
        int population = getUnitCount();
        if (population > 0) {
            
            for (BuildingType buildingType : FreeCol.getSpecification().getBuildingTypeList()) {
                if (isFree(buildingType)) {
                    Building b = createFreeBuilding(buildingType);
                    if (b != null) {
                        addBuilding(b);
                    }
                }
            }
            getTile().updatePlayerExploredTiles();

            updateSoL();
            updateProductionBonus();
        }
        firePropertyChange(ColonyChangeEvent.POPULATION_CHANGE.toString(), 
                           population - difference, population);
    }

    
    public ExportData getExportData(final GoodsType goodsType) {
        ExportData result = exportData.get(goodsType.getId());
        if (result == null) {
            result = new ExportData(goodsType);
            setExportData(result);
        }
        return result;
    }

    
    public final void setExportData(final ExportData newExportData) {
        exportData.put(newExportData.getId(), newExportData);
    }

    
    public boolean isLandLocked() {
        return landLocked;
    }

    
    public boolean isConnected() {
        Map map = getGame().getMap();
        Tile tile = getTile();
        for (Direction direction : Direction.values()) {
            Tile t = map.getNeighbourOrNull(direction, tile);
            if (t != null && t.getType().isWater() && t.isConnected()) {
                return true;
            }
        }
        return false;
    }

    
    public boolean isUndead() {
        final Iterator<Unit> unitIterator = getUnitIterator();
        return unitIterator.hasNext() && unitIterator.next().isUndead();
    }

    
    @Override
    public void setOwner(Player owner) {
        
        super.setOwner(owner);
        for (Unit unit : getUnitList()) {
            unit.setOwner(owner);
            if (unit.getLocation() instanceof ColonyTile) {
                ((ColonyTile) unit.getLocation()).getWorkTile().setOwner(owner);
            }
        }
        for (Unit target : tile.getUnitList()) {
            target.setOwner(getOwner());
        }
        for (ExportData exportDatum : exportData.values()) {
            exportDatum.setExported(false);
        }
        
        updatePopulation(0);
    }

    
    public void setUnitCount(int unitCount) {
        this.unitCount = unitCount;
    }

    
    public List<Building> getBuildingsForProducing(GoodsType goodsType) {
        List<Building> buildings = new ArrayList<Building>();
        for (Building building : getBuildings()) {
            if (building.getGoodsOutputType() == goodsType) {
                buildings.add(building);
            }
        }
        return buildings;
    }

    
    public List<Building> getBuildingsForConsuming(GoodsType goodsType) {
        List<Building> buildings = new ArrayList<Building>();
        for (Building building : getBuildings()) {
            if (building.getGoodsInputType() == goodsType) {
                buildings.add(building);
            }
        }
        return buildings;
    }

    
    public Building getBuildingForProducing(GoodsType goodsType) {
        List<Building> buildings = getBuildingsForProducing(goodsType);
        return (buildings.isEmpty()) ? null : buildings.get(0);
    }

    
    public Building getBuildingForConsuming(GoodsType goodsType) {
        List<Building> buildings = getBuildingsForConsuming(goodsType);
        return (buildings.isEmpty()) ? null : buildings.get(0);
    }

    
    public List<WorkLocation> getWorkLocations() {
        List<WorkLocation> result = new ArrayList<WorkLocation>(colonyTiles);
        result.addAll(buildingMap.values());
        return result;
    }

    
    public List<Building> getBuildings() {
        return new ArrayList<Building>(buildingMap.values());
    }

    
    public List<ColonyTile> getColonyTiles() {
        return colonyTiles;
    }

    
    public Building getBuilding(BuildingType type) {
        return buildingMap.get(type.getFirstLevel().getId());
    }


    
    public Building getBuildingWithAbility(String ability) {
        for (Building building : buildingMap.values()) {
            if (building.getType().hasAbility(ability)) {
                return building;
            }
        }
        return null;
    }

    
    public ColonyTile getColonyTile(int x, int y) {
        Tile t = getTile(x, y);
        for (ColonyTile c : colonyTiles) {
            if (c.getWorkTile() == t) {
                return c;
            }
        }
        return null;
    }

    
    public ColonyTile getColonyTile(Tile t) {
        for (ColonyTile c : colonyTiles) {
            if (c.getWorkTile() == t) {
                return c;
            }
        }
        return null;
    }

    public void incrementLiberty(int amount) {
        liberty += amount;
    }

    public void incrementImmigration(int amount) {
        immigration += amount;
    }

    
    @Override
    public void add(Locatable locatable) {
        if (locatable instanceof Unit) {
            Unit newUnit = (Unit) locatable;
            if (newUnit.isColonist()) {
                WorkLocation w = getVacantWorkLocationFor(newUnit);
                if (w == null) {
                    logger.warning("Could not find a 'WorkLocation' for " + newUnit.getName()
                                   + " in " + getName());
                    newUnit.putOutsideColony();
                } else {
                    int oldPopulation = getUnitCount();
                    newUnit.work(w);
                    firePropertyChange(ColonyChangeEvent.POPULATION_CHANGE.toString(),
                                       oldPopulation, oldPopulation + 1);
                    updatePopulation(1);
                }
            } else {
                newUnit.putOutsideColony();
            }
        } else if (locatable instanceof Goods) {
            addGoods((Goods) locatable);
        } else {
            logger.warning("Tried to add an unrecognized 'Locatable' to a 'Colony'.");
        }
    }
    
    public void removeGoods(GoodsType type, int amount) {
        Goods removed = goodsContainer.removeGoods(type, amount);
        modifySpecialGoods(type, -removed.getAmount());
    }

    
    public void removeGoods(AbstractGoods goods) {
        removeGoods(goods.getType(), goods.getAmount());
    }

    
    public void removeGoods(GoodsType type) {
        Goods removed = goodsContainer.removeGoods(type);
        modifySpecialGoods(type, -removed.getAmount());
    }

    
    public void addGoods(GoodsType type, int amount) {
        goodsContainer.addGoods(type.getStoredAs(), amount);
        modifySpecialGoods(type, amount);
    }

    private void modifySpecialGoods(GoodsType goodsType, int amount) {
        FeatureContainer container = goodsType.getFeatureContainer();
        Set<Modifier> libertyModifiers = container.getModifierSet("model.modifier.liberty");
        if (!libertyModifiers.isEmpty()) {
            int newLiberty = (int) FeatureContainer.applyModifierSet(amount,
                                                                     getGame().getTurn(),
                                                                     libertyModifiers);
            incrementLiberty(newLiberty);
            getOwner().incrementLiberty(newLiberty);
        }

        Set<Modifier> immigrationModifiers = container.getModifierSet("model.modifier.immigration");
        if (!immigrationModifiers.isEmpty()) {
            int newImmigration = (int) FeatureContainer.applyModifierSet(amount,
                                                                         getGame().getTurn(),
                                                                         immigrationModifiers);
            incrementImmigration(newImmigration);
            getOwner().incrementImmigration(newImmigration);
        }

    }

    public void addGoods(AbstractGoods goods) {
        addGoods(goods.getType(), goods.getAmount());
    }




    
    @Override
    public void remove(Locatable locatable) {
        if (locatable instanceof Unit) {
            for (WorkLocation w : getWorkLocations()) {
                if (w.contains(locatable)) {
                    int oldPopulation = getUnitCount();
                    w.remove(locatable);
                    firePropertyChange(ColonyChangeEvent.POPULATION_CHANGE.toString(),
                                       oldPopulation, oldPopulation - 1);
                    updatePopulation(-1);
                    return;
                }
            }
        } else if (locatable instanceof Goods) {
            removeGoods((Goods) locatable);
        } else {
            logger.warning("Tried to remove an unrecognized 'Locatable' from a 'Colony'.");
        }
    }

    
    @Override
    public int getUnitCount() {
        int count = 0;
        if (unitCount != -1) {
            return unitCount;
        }
        for (WorkLocation w : getWorkLocations()) {
            count += w.getUnitCount();
        }
        return count;
    }

    public List<Unit> getUnitList() {
        ArrayList<Unit> units = new ArrayList<Unit>();
        for (WorkLocation wl : getWorkLocations()) {
            for (Unit unit : wl.getUnitList()) {
                units.add(unit);
            }
        }
        return units;
    }
    
    public Iterator<Unit> getUnitIterator() {
        return getUnitList().iterator();
    }

    @Override
    public boolean contains(Locatable locatable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canAdd(Locatable locatable) {
        if (locatable instanceof Unit && ((Unit) locatable).getOwner() == getOwner()) {
            return true;
        } else if (locatable instanceof Goods) {
            return true;
        } else {
            return false;
        }
    }

    
    public boolean canTrain(Unit unit) {
        return canTrain(unit.getType());
    }

    
    public boolean canTrain(UnitType unitType) {
        if (!hasAbility("model.ability.teach")) {
            return false;
        }
        
        for (Building building : buildingMap.values()) {
            if (building.getType().hasAbility("model.ability.teach") &&
                building.canAdd(unitType)) {
                return true;
            }
        }
        return false;
    }
    
    public List<Unit> getTeachers() {
        List<Unit> teachers = new ArrayList<Unit>();
        for (Building building : buildingMap.values()) {
            if (building.getType().hasAbility("model.ability.teach")) {
                teachers.addAll(building.getUnitList());
            }
        }
        return teachers;
    }

    
    @Override
    public Unit getDefendingUnit(Unit attacker) {
        List<Unit> unitList = getUnitList();
        
        if (unitCount != -1 && unitList.isEmpty()) {
            
            return null;
        }
        
        Unit defender = null;
        float defencePower = -1.0f;
        for (Unit nextUnit : unitList) {
            float tmpPower = getGame().getCombatModel().getDefencePower(attacker, nextUnit);
            if (tmpPower > defencePower || defender == null) {
                defender = nextUnit;
                defencePower = tmpPower;
            }
        }
        if (defender == null) {
            throw new IllegalStateException("Colony " + getName() + " contains no units!");
        } else {
            return defender;
        }
    }

    
    public List<UnitType> getBuildableUnits() {
        ArrayList<UnitType> buildableUnits = new ArrayList<UnitType>();
        List<UnitType> unitTypes = FreeCol.getSpecification().getUnitTypeList();
        for (UnitType unitType : unitTypes) {
            if (unitType.getGoodsRequired().isEmpty() == false && canBuild(unitType)) {
                buildableUnits.add(unitType);
            }
        }
        return buildableUnits;
    }

    
    public BuildableType getCurrentlyBuilding() {
        if (buildQueue.isEmpty()) {
            return null;
        } else {
            return buildQueue.get(0);
        }
    }

    
    public void setCurrentlyBuilding(BuildableType buildable) {
        List<BuildableType> oldBuildQueue = new ArrayList<BuildableType>(buildQueue);
        
        if (buildable instanceof BuildingType) {
            if (buildQueue.contains(buildable)){
                buildQueue.remove(buildable);
            }
        }
        buildQueue.add(0, buildable);
        firePropertyChange(ColonyChangeEvent.BUILD_QUEUE_CHANGE.toString(),
                           oldBuildQueue, buildQueue);
    }

    
    public int getTurnsToComplete(BuildableType buildable) {
        int result = 0;
        boolean goodsMissing = false;
        boolean goodsBeingProduced = false;
        boolean productionMissing = false;
        
        for (AbstractGoods requiredGoods : buildable.getGoodsRequired()) {
            int amountNeeded = requiredGoods.getAmount();
            int amountAvailable = getGoodsCount(requiredGoods.getType());
            if (amountAvailable >= amountNeeded) {
                continue;
            }
            goodsMissing = true;
            int amountProduced = getProductionNextTurn(requiredGoods.getType());
            if (amountProduced <= 0) {
                productionMissing = true;
                continue;
            }
            goodsBeingProduced = true;
            
            int amountRemaining = amountNeeded - amountAvailable;
            int eta = amountRemaining / amountProduced;
            if (amountRemaining % amountProduced != 0) {
                eta++;
            }
            result = Math.max(result, eta);
        }
        if(!goodsMissing){
            return 0;
        }
        if(goodsMissing && !goodsBeingProduced){
            return Integer.MIN_VALUE;
        }
        if(productionMissing){
            result = result * -1;
        }
        
        return result;
    }


    
    public List<BuildableType> getBuildQueue() {
        return buildQueue;
    }

    
    public void setBuildQueue(final List<BuildableType> newBuildQueue) {
        List<BuildableType> oldBuildQueue = buildQueue;
        buildQueue = newBuildQueue;
        firePropertyChange(ColonyChangeEvent.BUILD_QUEUE_CHANGE.toString(),
                           oldBuildQueue, newBuildQueue);
    }


    
    public int getLiberty() {
        return liberty;
    }

    
    public void addLiberty(int amount) {
        if (FreeCol.isInDebugMode()) {
            getOwner().incrementLiberty(amount);
            List<GoodsType> libertyTypeList = FreeCol.getSpecification().getLibertyGoodsTypeList();
            if (getMembers() <= getUnitCount() + 1
                && amount > 0
                && !libertyTypeList.isEmpty()) {
                addGoods(libertyTypeList.get(0), amount);
            }
            updateSoL();
        }
    }

    
    public int getImmigration() {
        return immigration;
    }

    
    
    public int getConsumption(GoodsType goodsType) {
        
        if (Goods.BELLS.equals(goodsType)) {
            return Math.max(0, getUnitCount() - 2);
        } else if (Goods.FOOD.equals(goodsType)) {
            return getFoodConsumption();
        } else {
            return 0;
        }
    }

    
    public int getSoL() {
        return sonsOfLiberty;
    }

    
    public void updateSoL() {
        int units = getUnitCount();
        oldSonsOfLiberty = sonsOfLiberty;
        oldTories = tories;
        sonsOfLiberty = calculateMembership(units);
        tories = units - getMembers();
    }

    
    public int calculateMembership(int units) {
        if (units <= 0) {
            return 0;
        }
        
        int membership = (liberty * 100) / (LIBERTY_PER_REBEL * units);
        if (membership < 0) {
            membership = 0;
        } else if (membership > 100) {
            membership = 100;
        }
        return membership;
    }

    
    public int getMembers() {
	float result = (sonsOfLiberty * getUnitCount()) / 100f;
        return Math.round(result);
    }

    
    public int getTory() {
        return 100 - getSoL();
    }

    
    public int getProductionBonus() {
        return productionBonus;
    }

    public Modifier getProductionModifier(GoodsType goodsType) {
        return new Modifier(goodsType.getId(), SOL_MODIFIER_SOURCE,
                            productionBonus, Modifier.Type.ADDITIVE);
    }

    
    @Override
    public String toString() {
        return getName();
    }

    
    public String getLocationName() {
        return getName();
    }
    
    
    public int getFoodProduction() {
        int result = 0;
        for (GoodsType foodType : FreeCol.getSpecification().getGoodsFood()) {
            result += getProductionOf(foodType);
        }
        return result;
    }

    
    public int getProductionOf(GoodsType goodsType) {
        int amount = 0;
        for (WorkLocation workLocation : getWorkLocations()) {
            amount += workLocation.getProductionOf(goodsType);
        }
        return amount;
    }

    
    public WorkLocation getVacantWorkLocationFor(Unit unit) {
        for (GoodsType foodType : FreeCol.getSpecification().getGoodsFood()) {
            WorkLocation colonyTile = getVacantColonyTileFor(unit, foodType, false);
            if (colonyTile != null) {
                return colonyTile;
            }
        }
        for (Building building : buildingMap.values()) {
            if (building.canAdd(unit)) {
                return building;
            }
        }
        return null;
    }

    
    public ColonyTile getVacantColonyTileFor(Unit unit, GoodsType goodsType,
                                             boolean allowClaim) {
        ColonyTile bestPick = null;
        int highestProduction = 0;
        for (ColonyTile colonyTile : colonyTiles) {
            if (colonyTile.canAdd(unit)) {
                Tile workTile = colonyTile.getWorkTile();
                if (workTile.getOwningSettlement() != this && !allowClaim) {
                    continue;
                }
                
                if (owner.getLandPrice(workTile) == 0) {
                    int potential = colonyTile.getProductionOf(unit, goodsType);
                    if (potential > highestProduction) {
                        highestProduction = potential;
                        bestPick = colonyTile;
                    }
                }
            }
        }
        return bestPick;
    }

    
    public int getProductionNextTurn(GoodsType goodsType) {
        int count = 0;
        Building building = getBuildingForProducing(goodsType);
        if (building == null) {
            count = getProductionOf(goodsType);
        } else {
            count = building.getProductionNextTurn();
        }
        return count;
    }

    
    public int getProductionNetOf(GoodsType goodsType) {
        int count = getProductionNextTurn(goodsType);
        int used = getConsumption(goodsType);

        Building bldg = getBuildingForConsuming(goodsType);
        if (bldg != null) {
            used += bldg.getGoodsInputNextTurn();
        }

        if (goodsType.isStorable()) {
            BuildableType currentBuildable = getCurrentlyBuilding();
            if (currentBuildable != null &&
                currentBuildable.getGoodsRequired().isEmpty() == false) {
                boolean willBeFinished = true;
                int possiblyUsed = 0;
                for (AbstractGoods goodsRequired : currentBuildable.getGoodsRequired()) {
                    GoodsType requiredType = goodsRequired.getType();
                    int requiredAmount = goodsRequired.getAmount();
                    int presentAmount = getGoodsCount(requiredType);
                    if (requiredType.equals(goodsType)) {
                        if (presentAmount + (count - used) < requiredAmount) {
                            willBeFinished = false;
                            break;
                        } else if (presentAmount < requiredAmount) {
                            possiblyUsed = requiredAmount - presentAmount;
                        }
                    } else if (getGoodsCount(requiredType) + getProductionNextTurn(requiredType) <
                               goodsRequired.getAmount()) {
                        willBeFinished = false;
                        break;
                    }
                }
                if (willBeFinished && possiblyUsed > 0) {
                    used += possiblyUsed;
                }
            }
        }
        return count - used;
    }

    
    public boolean canBreed(GoodsType goodsType) {
        int breedingNumber = goodsType.getBreedingNumber();
        return (breedingNumber != GoodsType.NO_BREEDING &&
                breedingNumber <= getGoodsCount(goodsType));
    }


    
    public boolean canBuild() {
        return canBuild(getCurrentlyBuilding());
    }

    
    public boolean canBuild(BuildableType buildableType) {
        return (getNoBuildReason(buildableType) == NoBuildReason.NONE);
    }

    
    public NoBuildReason getNoBuildReason(BuildableType buildableType) {
        if (buildableType == null) {
            return null;
        } else if (buildableType.getGoodsRequired().isEmpty()) {
            return NoBuildReason.NOT_BUILDABLE;
        } else if (buildableType.getPopulationRequired() > getUnitCount()) {
            return NoBuildReason.POPULATION_TOO_SMALL;
        } else if (!(buildableType instanceof BuildingType ||
                     featureContainer.hasAbility("model.ability.build", buildableType, getGame().getTurn()))) {
            return NoBuildReason.MISSING_BUILD_ABILITY;
        } else {
            java.util.Map<String, Boolean> requiredAbilities = buildableType.getAbilitiesRequired();
            for (Entry<String, Boolean> entry : requiredAbilities.entrySet()) {
                if (hasAbility(entry.getKey()) != entry.getValue()) {
                    return NoBuildReason.MISSING_ABILITY;
                }
            }
        }
        if (buildableType instanceof BuildingType) {
            BuildingType newBuildingType = (BuildingType) buildableType;
            Building colonyBuilding = this.getBuilding(newBuildingType);
            if (colonyBuilding == null) {
                
                if (newBuildingType.getUpgradesFrom() != null) {
                    
                    return NoBuildReason.WRONG_UPGRADE;
                }
            } else {
                
                if (colonyBuilding.getType().getUpgradesTo() != newBuildingType) {
                    
                    return NoBuildReason.WRONG_UPGRADE;
                }
            }
        }
        return NoBuildReason.NONE;
    }

    
    public Building createBuilding(BuildingType buildingType) {
        return getGame().getModelController().createBuilding(getId() + "buildBuilding", this, buildingType);
    }

    
    public Building createFreeBuilding(BuildingType buildingType) {
        return getGame().getModelController().createBuilding(getId() + "buildFreeBuilding", this, buildingType);
    }

    void checkBuildableComplete() {
        
        if (lastVisited == getGame().getTurn().getNumber()) {
            return;
        }
        lastVisited = getGame().getTurn().getNumber();
        
        
        if (!canBuild()) {
            return;
        }
        
        
        BuildableType buildable = getCurrentlyBuilding();
        ArrayList<ModelMessage> messages = new ArrayList<ModelMessage>();
        for (AbstractGoods goodsRequired : buildable.getGoodsRequired()) {
            GoodsType requiredGoodsType = goodsRequired.getType();
            int available = getGoodsCount(requiredGoodsType);
            int required = goodsRequired.getAmount();
            if (available < required) {
                if (!requiredGoodsType.isStorable()) {
                    
                    
                    
                    return;
                }
                messages.add(new ModelMessage(this, ModelMessage.MessageType.MISSING_GOODS,
                                              requiredGoodsType,
                                              "model.colony.buildableNeedsGoods",
                                              "%colony%", getName(),
                                              "%buildable%", buildable.getName(),
                                              "%amount%", String.valueOf(required - available),
                                              "%goodsType%", requiredGoodsType.getName()));
            }
        }
       
        if (!messages.isEmpty()) {
            
            
            
            for (ModelMessage message : messages) {
                owner.addModelMessage(message);
            }
            return;
        }
      
        
        
        
        
        for (AbstractGoods goodsRequired : buildable.getGoodsRequired()) {
            if (getGameOptions().getBoolean(GameOptions.SAVE_PRODUCTION_OVERFLOW) ||
                goodsRequired.getType().isStorable()) {
                removeGoods(goodsRequired);
            } else {
                
                removeGoods(goodsRequired.getType());
            }
        }
        
        if (buildable instanceof UnitType) {
            
            Unit unit = getGame().getModelController()
                .createUnit(getId() + "buildUnit", getTile(), getOwner(),
                            (UnitType) buildable);
            addModelMessage(this, ModelMessage.MessageType.UNIT_ADDED, unit,
                            "model.colony.unitReady",
                            "%colony%", getName(),
                            "%unit%", unit.getName());
            if (buildQueue.size() > 1) {
                
                buildQueue.remove(0);
            }
        } else if (buildable instanceof BuildingType) {
            
            BuildingType upgradesFrom = ((BuildingType) buildable).getUpgradesFrom();
            if (upgradesFrom == null) {
                addBuilding(createBuilding((BuildingType) buildable));
            } else {
                getBuilding(upgradesFrom).upgrade();
            }
            addModelMessage(this, ModelMessage.MessageType.BUILDING_COMPLETED, this,
                            "model.colony.buildingReady", 
                            "%colony%", getName(),
                            "%building%", buildable.getName());
            buildQueue.remove(0);
        }
            
        
        if (buildQueue.isEmpty()) {
            addModelMessage(this, ModelMessage.MessageType.WARNING, this, 
                            "model.colony.cannotBuild", 
                            "%colony%", getName());
        }
    }

    
    public int getPriceForBuilding() {
        return getPriceForBuilding(getCurrentlyBuilding());
    }

    
    public int getPriceForBuilding(BuildableType buildableType) {
        
        
        int price = 0;
        for (AbstractGoods goodsRequired : buildableType.getGoodsRequired()) {
            GoodsType requiredGoodsType = goodsRequired.getType();
            int remaining = goodsRequired.getAmount() - getGoodsCount(requiredGoodsType);
            if (remaining > 0) {
                if (requiredGoodsType.isStorable()) {
                    price += (getOwner().getMarket().getBidPrice(requiredGoodsType, remaining) * 110) / 100;
                } else {
                    price += requiredGoodsType.getPrice() * remaining;
                }
            }
        }
        return price;
    }

    
    public void payForBuilding() {
        
        
        if (!canPayToFinishBuilding()) {
            throw new IllegalStateException("Not enough gold.");
        }
        for (AbstractGoods goodsRequired : getCurrentlyBuilding().getGoodsRequired()) {
            GoodsType requiredGoodsType = goodsRequired.getType();
            int remaining = goodsRequired.getAmount() - getGoodsCount(requiredGoodsType);
            if (remaining > 0) {
                if (requiredGoodsType.isStorable()) {
                    getOwner().getMarket().buy(requiredGoodsType, remaining, getOwner());
                } else {
                    getOwner().modifyGold(-remaining * requiredGoodsType.getPrice());
                }
                addGoods(requiredGoodsType, remaining);
            }
        }
    }
    
    
    public boolean canPayToFinishBuilding() {
        return canPayToFinishBuilding(getCurrentlyBuilding());
    }

    
    public boolean canPayToFinishBuilding(BuildableType buildableType) {
        if (buildableType == null){
            return false;
        }
        
        if (getPriceForBuilding(buildableType) > getOwner().getGold()) {
            return false;
        }
        return true;
    }

    
    public Collection<String> getWarnings(GoodsType goodsType, int amount, int production) {
        List<String> result = new LinkedList<String>();

        if (goodsType.isFoodType() && goodsType.isStorable()) {
            if (amount + production < 0) {
                result.add(Messages.message("model.colony.famineFeared",
                                            "%colony%", getName(),
                                            "%number%", "0"));
            }
        } else {
            
            int waste = (amount + production - getWarehouseCapacity());
            if (waste > 0 && !getExportData(goodsType).isExported() && !goodsType.limitIgnored()) {
                result.add(Messages.message("model.building.warehouseSoonFull",
                                            "%goods%", goodsType.getName(),
                                            "%colony%", getName(),
                                            "%amount%", String.valueOf(waste)));

            }
        }

        BuildableType currentlyBuilding = getCurrentlyBuilding();
        if (currentlyBuilding != null) {
            for (AbstractGoods goods : currentlyBuilding.getGoodsRequired()) {
                if (goods.getType().equals(goodsType) && amount < goods.getAmount()) {
                    result.add(Messages.message("model.colony.buildableNeedsGoods",
                                                "%colony%", getName(),
                                                "%buildable%", currentlyBuilding.getName(),
                                                "%amount%", String.valueOf(goods.getAmount() - amount),
                                                "%goodsType%", goodsType.getName()));
                }
            }
        }

        addInsufficientProductionMessage(result, getBuildingForProducing(goodsType));

        Building buildingForConsuming = getBuildingForConsuming(goodsType);
        if (buildingForConsuming != null && !buildingForConsuming.getGoodsOutputType().isStorable()) {
            
            addInsufficientProductionMessage(result, buildingForConsuming);
        }

        return result;
    }

    
    private void addInsufficientProductionMessage(List<String> warnings, Building building) {
        if (building != null) {
            int delta = building.getMaximumProduction() - building.getProductionNextTurn();
            if (delta > 0) {
                warnings.add(createInsufficientProductionMessage(building.getGoodsOutputType(),
                                                                 delta,
                                                                 building.getGoodsInputType(),
                                                                 building.getMaximumGoodsInput()
                                                                 - building.getGoodsInputNextTurn()));
            }
        }
    }

    
    private String createInsufficientProductionMessage(GoodsType outputType, int missingOutput,
                                                       GoodsType inputType, int missingInput) {
        return Messages.message("model.colony.insufficientProduction",
                                "%outputAmount%", String.valueOf(missingOutput),
                                "%outputType%", outputType.getName(),
                                "%colony%", getName(),
                                "%inputAmount%", String.valueOf(missingInput),
                                "%inputType%", inputType.getName());
    }

    
    public Unit getRandomUnit() {
        return getFirstUnit();
        
    }

    private Unit getFirstUnit() {
        for (WorkLocation wl : getWorkLocations()) {
            Iterator<Unit> unitIterator = wl.getUnitIterator();
            while (unitIterator.hasNext()) {
                Unit o = unitIterator.next();
                if (o != null) {
                    return o;
                }
            }
        }
        return null;
    }

    
    private void saveWarehouseState() {
        logger.finest("Saving state of warehouse in " + getName());
        getGoodsContainer().saveState();
    }


    
    private void addColonyTileProduction() {
        for (ColonyTile colonyTile : colonyTiles) {
            logger.finest("Calling newTurn for colony tile " + colonyTile.toString());
            colonyTile.newTurn();
        }
    }

    
    void updateFood() {
        int required = getFoodConsumption();
        int available = getFoodCount();
        int production = getFoodProduction();

        if (required > available) {
            
            getRandomUnit().dispose();
            removeFood(available);
            addModelMessage(this, ModelMessage.MessageType.UNIT_LOST,
                            "model.colony.colonistStarved", "%colony%", getName());
        } else {
            removeFood(required);
            if (required > production){
            	int turnsToLive = (available - required) / (required - production);
            	if(turnsToLive <= 3) {
                    addModelMessage(this, ModelMessage.MessageType.WARNING,
                                    "model.colony.famineFeared", "%colony%", getName(),
                                    "%number%", String.valueOf(turnsToLive));
            	}
            }
        }
    }

    
    private void checkForNewColonist() {
        if (getFoodCount() >= FOOD_PER_COLONIST) {
            List<UnitType> unitTypes = FreeCol.getSpecification()
                .getUnitTypesWithAbility("model.ability.bornInColony");
            if (!unitTypes.isEmpty()) {
                int random = getGame().getModelController()
                    .getRandom(getId() + "bornInColony", unitTypes.size());
                Unit u = getGame().getModelController()
                    .createUnit(getId() + "newTurn200food",
                                getTile(), getOwner(), unitTypes.get(random));
                removeFood(FOOD_PER_COLONIST);
                addModelMessage(this, ModelMessage.MessageType.UNIT_ADDED, u,
                                "model.colony.newColonist", "%colony%", getName());
                logger.info("New colonist created in " + getName() + " with ID=" + u.getId());
            }
        }
    }

    
    private void exportGoods() {
        if (hasAbility("model.ability.export")) {
            List<Goods> exportGoods = getCompactGoods();
            for (Goods goods : exportGoods) {
                GoodsType type = goods.getType();
                ExportData data = getExportData(type);
                if (data.isExported() && (owner.canTrade(goods, Market.CUSTOM_HOUSE))) {
                    int amount = goods.getAmount() - data.getExportLevel();
                    if (amount > 0) {
                        removeGoods(type, amount);
                        getOwner().getMarket().sell(type, amount, owner, Market.CUSTOM_HOUSE);
                    }
                }
            }
        }
    }


    
    private void createWarehouseCapacityWarning() {
        List<Goods> storedGoods = getGoodsContainer().getFullGoods();
        for (Goods goods : storedGoods) {
            GoodsType goodsType = goods.getType();
            if (!goodsType.isStorable() || goodsType.limitIgnored()) {
                
                continue;
            } else if (getExportData(goodsType).isExported() &&
                       owner.canTrade(goods, Market.CUSTOM_HOUSE)) {
                
                continue;
            } else if (goods.getAmount() < getWarehouseCapacity()) {
                int waste = (goods.getAmount() + getProductionNetOf(goodsType) -
                             getWarehouseCapacity());
                if (waste > 0) {
                    addModelMessage(this, ModelMessage.MessageType.WAREHOUSE_CAPACITY, goodsType,
                                    "model.building.warehouseSoonFull",
                                    "%goods%", goods.getName(),
                                    "%colony%", getName(),
                                    "%amount%", String.valueOf(waste));
                }
            }
        }
    }


    private void createSoLMessages() {
        if (sonsOfLiberty / 10 != oldSonsOfLiberty / 10) {
            if (sonsOfLiberty > oldSonsOfLiberty) {
                addModelMessage(this, ModelMessage.MessageType.SONS_OF_LIBERTY,
                                FreeCol.getSpecification().getGoodsType("model.goods.bells"),
                                "model.colony.SoLIncrease", 
                                "%oldSoL%", String.valueOf(oldSonsOfLiberty),
                                "%newSoL%", String.valueOf(sonsOfLiberty),
                                "%colony%", getName());
            } else {
                addModelMessage(this, ModelMessage.MessageType.SONS_OF_LIBERTY,
                                FreeCol.getSpecification().getGoodsType("model.goods.bells"),
                                "model.colony.SoLDecrease", 
                                "%oldSoL%", String.valueOf(oldSonsOfLiberty),
                                "%newSoL%", String.valueOf(sonsOfLiberty),
                                "%colony%", getName());

            }
        }


        ModelMessage govMgtMessage = checkForGovMgtChangeMessage();
        if (govMgtMessage != null){
            addModelMessage(govMgtMessage);
        }

    }
    
    
    public int governmentChange(int units) {
        final int veryBadGovernment = Specification.getSpecification()
            .getIntegerOption("model.option.veryBadGovernmentLimit").getValue();
        final int badGovernment = Specification.getSpecification()
            .getIntegerOption("model.option.badGovernmentLimit").getValue();

        int rebels = calculateMembership(units);
        int loyalists = Math.round((rebels * units) / 100f);
        int result = 0;

        if (rebels == 100) {
            
            if (sonsOfLiberty < 100) {
                result = 1;
            }
        } else if (rebels >= 50) {
            if (sonsOfLiberty == 100) {
                result = -1;
            } else if (sonsOfLiberty < 50) {
                result = 1;
            }
        } else {
            if (sonsOfLiberty >= 50) {
                result = -1;
            }
        	
            
            if (loyalists > veryBadGovernment) {
                if (tories <= veryBadGovernment) {
                    result = -1;
                }
            } else if (loyalists > badGovernment) {
                if (tories <= badGovernment) {
                    result = -1;
                } else if (tories > veryBadGovernment) {
                    result = 1;
                }
            } else if (tories > badGovernment) {
                result = 1;
            }
        }
        return result;
    }

    public ModelMessage checkForGovMgtChangeMessage() {
        final int veryBadGovernment = Specification.getSpecification()
            .getIntegerOption("model.option.veryBadGovernmentLimit").getValue();
        final int badGovernment = Specification.getSpecification()
            .getIntegerOption("model.option.badGovernmentLimit").getValue();
        
        String msgId = null;
        ModelMessage.MessageType msgType = ModelMessage.MessageType.GOVERNMENT_EFFICIENCY;
        
        if (sonsOfLiberty == 100) {
            
            if (oldSonsOfLiberty < 100) {
            	msgId = "model.colony.SoL100";
                msgType = ModelMessage.MessageType.SONS_OF_LIBERTY;
            }
        } else if (sonsOfLiberty >= 50) {
            if (oldSonsOfLiberty == 100) {
                msgId = "model.colony.lostSoL100";
                msgType = ModelMessage.MessageType.SONS_OF_LIBERTY;
            } else if (oldSonsOfLiberty < 50) {
                msgId = "model.colony.SoL50";
                msgType = ModelMessage.MessageType.SONS_OF_LIBERTY;
            }
        } else {
            if (oldSonsOfLiberty >= 50) {
                msgId = "model.colony.lostSoL50";
                msgType = ModelMessage.MessageType.SONS_OF_LIBERTY;
            }
        	
            
            if (tories > veryBadGovernment) {
                if (oldTories <= veryBadGovernment) {
                    
                    msgId = "model.colony.veryBadGovernment";
                }
            } else if (tories > badGovernment) {
                if (oldTories <= badGovernment) {
                    
                    msgId = "model.colony.badGovernment";
                } else if (oldTories > veryBadGovernment) {
                    
                    msgId = "model.colony.governmentImproved1";
                }
            } else if (oldTories > badGovernment) {
                
                msgId = "model.colony.governmentImproved2";
            }
        }
        
        
        if (msgId == null){
            return null;
        }
        
        ModelMessage msg = 
            new ModelMessage(this, msgType,
                             FreeCol.getSpecification().getGoodsType("model.goods.bells"),
                             msgId, "%colony%", getName());
     
        return msg;
    }


    private void updateProductionBonus() {
        final int veryBadGovernment = Specification.getSpecification()
            .getIntegerOption("model.option.veryBadGovernmentLimit").getValue();
        final int badGovernment = Specification.getSpecification()
            .getIntegerOption("model.option.badGovernmentLimit").getValue();
        int newBonus = 0;
        if (sonsOfLiberty == 100) {
            
            newBonus = 2;
        } else if (sonsOfLiberty >= 50) {
            newBonus = 1;
        } else if (tories > veryBadGovernment) {
            newBonus = -2;
        } else if (tories > badGovernment) {
            newBonus = -1;
        }
        if (getOwner().isAI()) {
            
            newBonus = Math.max(0, newBonus);
        }

        int oldBonus = productionBonus;
        productionBonus = newBonus;
        firePropertyChange(ColonyChangeEvent.BONUS_CHANGE.toString(), oldBonus, newBonus);
    }


    
    @Override
    public void newTurn() {
        
        if (unitCount != -1) {
            return;
        }

        if (getTile() == null) {
            
            logger.warning("Colony " + getName() + " lacks a tile!");
            return;
        }

        
        saveWarehouseState();

        addColonyTileProduction();
        List<GoodsType> goodsForBuilding = new ArrayList<GoodsType>();
        if (canBuild()) {
            for (AbstractGoods goodsRequired : getCurrentlyBuilding().getGoodsRequired()) {
                goodsForBuilding.add(goodsRequired.getType());
            }
        } else {
            
            BuildableType currentlyBuilding = getCurrentlyBuilding();
            if ((currentlyBuilding == null)) {
                for (GoodsType goodsType : Specification.getSpecification().getGoodsTypeList()) {
                    if (goodsType.isBuildingMaterial() &&
                        !goodsType.isStorable() &&
                        getProductionOf(goodsType) > 0) {
                        
                        addModelMessage(this, ModelMessage.MessageType.WARNING, this, 
                                        "model.colony.cannotBuild", 
                                        "%colony%", getName());
                    }
                }
            } else if (currentlyBuilding.getPopulationRequired() > getUnitCount()) {
                
                addModelMessage(this, ModelMessage.MessageType.WARNING, this, 
                                "model.colony.buildNeedPop", 
                                "%colony%", getName(), 
                                "%building%", currentlyBuilding.getName());
            }
        }
        
        List<Building> buildingsProducingBuildingMaterials = new ArrayList<Building>();
        List<Building> buildingsProducingFood = new ArrayList<Building>();
        List<Building> otherBuildings = new ArrayList<Building>();
        for (Building building : getBuildings()) {
            if (building.getType().hasAbility("model.ability.autoProduction")) {
                
                logger.finest("Calling newTurn for building " + building.getName());
                building.newTurn();
            } else if (building.getGoodsOutputType() != null &&
                       building.getGoodsOutputType().isFoodType()) {
                buildingsProducingFood.add(building);
            } else if (goodsForBuilding.contains(building.getGoodsOutputType())) {
                buildingsProducingBuildingMaterials.add(building);
            } else {
                int index = -1;
                GoodsType outputType = building.getGoodsOutputType();
                if (outputType != null) {
                    for (int i = 0; i < otherBuildings.size(); i++) {
                        if (outputType.equals(otherBuildings.get(i).getGoodsInputType())) {
                            index = i;
                        }
                    }
                }
                if (index == -1) {
                    otherBuildings.add(building);
                } else {
                    
                    otherBuildings.add(index, building);
                }
            }
        }

        
        for (Building building : buildingsProducingFood) {
            logger.finest("Calling newTurn for building " + building.getName());
            building.newTurn();
        }

        
        updateFood();
        if (getUnitCount() == 0) {
            dispose();
            return;
        }

        
        for (Building building : buildingsProducingBuildingMaterials) {
            logger.finest("Calling newTurn for building " + building.getName());
            building.newTurn();
        }

        
        
        checkBuildableComplete();

        
        
        for (Building building : otherBuildings) {
            logger.finest("Calling newTurn for building " + building.getName());
            building.newTurn();
        }

        
        
        checkForNewColonist();
        exportGoods();
        
        goodsContainer.cleanAndReport();
        
        createWarehouseCapacityWarning();

        
        
        
        for (GoodsType goodsType : Specification.getSpecification().getGoodsTypeList()) {
            if (!goodsType.isFoodType()) {
                removeGoods(goodsType, getConsumption(goodsType));
            }
        }

        
        updateSoL();
        createSoLMessages();
        updateProductionBonus();
    }

    
    public int getWarehouseCapacity() {
        
        return (int) featureContainer.applyModifier(0, "model.modifier.warehouseStorage",
                                                    null, getGame().getTurn());
    }
    
    public Building getWarehouse() {
        
        for (Building building : buildingMap.values()) {
            if (!building.getType().getModifierSet("model.modifier.warehouseStorage").isEmpty()) {
                return building;
            }
        }
        return null;
    }

    public void propertyChange(PropertyChangeEvent event) {
        firePropertyChange(event.getPropertyName(),
                           event.getOldValue(), event.getNewValue());
    }


    
    @Override
    public void dispose() {
        for (WorkLocation workLocation : getWorkLocations()) {
            ((FreeColGameObject) workLocation).dispose();
        }
        TileItemContainer container = getTile().getTileItemContainer();
        TileImprovement road = container.getRoad();
        if (road != null && road.isVirtual()) {
            container.removeTileItem(road);
        }
        super.dispose();
    }

    
    @Override
    protected void toXMLImpl(XMLStreamWriter out, Player player, boolean showAll, boolean toSavedGame)
        throws XMLStreamException {
        boolean full = getGame().isClientTrusted() || showAll || player == getOwner();
        PlayerExploredTile pet;

        
        out.writeStartElement(getXMLElementTagName());
        
        out.writeAttribute("ID", getId());
        out.writeAttribute("name", getName());
        out.writeAttribute("tile", tile.getId());
        if (full) {
            out.writeAttribute("owner", owner.getId());
            out.writeAttribute("sonsOfLiberty", Integer.toString(sonsOfLiberty));
            out.writeAttribute("oldSonsOfLiberty", Integer.toString(oldSonsOfLiberty));
            out.writeAttribute("tories", Integer.toString(tories));
            out.writeAttribute("oldTories", Integer.toString(oldTories));
            out.writeAttribute("liberty", Integer.toString(liberty));
            out.writeAttribute("immigration", Integer.toString(immigration));
            out.writeAttribute("productionBonus", Integer.toString(productionBonus));
            out.writeAttribute("landLocked", Boolean.toString(landLocked));
            for (ExportData data : exportData.values()) {
                data.toXML(out);
            }
            
            
            for (Modifier modifier : featureContainer.getModifierSet("model.goods.bells",
                                                                     null, getGame().getTurn())) {
                if (Specification.COLONY_GOODS_PARTY.equals(modifier.getSource())) {
                    modifier.toXML(out);
                }
            }

            for (WorkLocation workLocation : getWorkLocations()) {
                ((FreeColGameObject) workLocation).toXML(out, player, showAll, toSavedGame);
            }
            toListElement("buildQueue", buildQueue, out);
            goodsContainer.toXML(out, player, showAll, toSavedGame);
        } else if (player.canSee(getTile())) {
            out.writeAttribute("owner", owner.getId());
            out.writeAttribute("unitCount", Integer.toString(getUnitCount()));
            if (getStockade() != null) {
                getStockade().toXML(out, player, showAll, toSavedGame);
            }
            GoodsContainer emptyGoodsContainer = new GoodsContainer(getGame(), getColony());
            emptyGoodsContainer.setFakeID(getColony().getGoodsContainer().getId());
            emptyGoodsContainer.toXML(out, player, showAll, toSavedGame);
        } else if ((pet = getTile().getPlayerExploredTile(player)) != null) {
            out.writeAttribute("owner", pet.getOwner().getId());
            out.writeAttribute("unitCount", Integer.toString(pet.getColonyUnitCount()));
            
            if (getStockade() != null) {
                getStockade().toXML(out, player, showAll, toSavedGame);
            }
            GoodsContainer emptyGoodsContainer = new GoodsContainer(getGame(), getColony());
            emptyGoodsContainer.setFakeID(getColony().getGoodsContainer().getId());
            emptyGoodsContainer.toXML(out, player, showAll, toSavedGame);
        }
        
        out.writeEndElement();
    }

    
    @Override
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setId(in.getAttributeValue(null, "ID"));
        setName(in.getAttributeValue(null, "name"));
        owner = getFreeColGameObject(in, "owner", Player.class);
        tile = getFreeColGameObject(in, "tile", Tile.class);
        owner.addSettlement(this);
        sonsOfLiberty = getAttribute(in, "sonsOfLiberty", 0);
        oldSonsOfLiberty = getAttribute(in, "oldSonsOfLiberty", 0);
        tories = getAttribute(in, "tories", 0);
        oldTories = getAttribute(in, "oldTories", 0);
        liberty = getAttribute(in, "liberty", 0);
        immigration = getAttribute(in, "immigration", 0);
        productionBonus = getAttribute(in, "productionBonus", 0);
        landLocked = getAttribute(in, "landLocked", true);
        if (!landLocked) {
            featureContainer.addAbility(HAS_PORT);
        }
        unitCount = getAttribute(in, "unitCount", -1);
        
        loop: while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if (in.getLocalName().equals(ColonyTile.getXMLElementTagName())) {
                ColonyTile ct = (ColonyTile) getGame().getFreeColGameObject(in.getAttributeValue(null, "ID"));
                if (ct == null) {
                    ct = new ColonyTile(getGame(), in);
                    ct.addPropertyChangeListener(this);
                    colonyTiles.add(ct);
                } else {
                    ct.readFromXML(in);
                }
            } else if (in.getLocalName().equals(Building.getXMLElementTagName())) {
                Building building = (Building) getGame().getFreeColGameObject(in.getAttributeValue(null, "ID"));
                if (building == null) {
                    building = new Building(getGame(), in);
                    building.addPropertyChangeListener(this);
                    addBuilding(building);
                } else {
                    building.readFromXML(in);
                }
            } else if (in.getLocalName().equals(GoodsContainer.getXMLElementTagName())) {
                GoodsContainer gc = (GoodsContainer) getGame().getFreeColGameObject(in.getAttributeValue(null, "ID"));
                if (gc == null) {
                    if (goodsContainer != null) {
                        goodsContainer.removePropertyChangeListener(this);
                    }
                    goodsContainer = new GoodsContainer(getGame(), this, in);
                    goodsContainer.addPropertyChangeListener(this);
                } else {
                    goodsContainer.readFromXML(in);
                }
            } else if (in.getLocalName().equals(ExportData.getXMLElementTagName())) {
                ExportData data = new ExportData();
                data.readFromXML(in);
                exportData.put(data.getId(), data);
            } else if (Modifier.getXMLElementTagName().equals(in.getLocalName())) {
                Modifier modifier = new Modifier(in, Specification.getSpecification());
                if (Specification.COLONY_GOODS_PARTY.equals(modifier.getSource())) {
                    Set<Modifier> bellsBonus = featureContainer.getModifierSet("model.goods.bells");
                    for (Modifier existingModifier : bellsBonus) {
                        if (Specification.COLONY_GOODS_PARTY.equals(existingModifier.getSource()) &&
                            modifier.getType() == existingModifier.getType() ) {
                            
                            
                            continue loop;
                        }
                    }
                    
                    featureContainer.addModifier(modifier);
                }
            } else if ("buildQueue".equals(in.getLocalName())) {
                buildQueue.clear();
                int size = getAttribute(in, ARRAY_SIZE, 0);
                if (size > 0) {
                    for (int x = 0; x < size; x++) {
                        String typeId = in.getAttributeValue(null, "x" + Integer.toString(x));
                        buildQueue.add(Specification.getSpecification().getType(typeId, BuildableType.class));
                    }
                }
                in.nextTag();
            } else {
                logger.warning("Unknown tag: " + in.getLocalName() + " loading colony " + getName());
                in.nextTag();
            }
        }
    }

    
    public static String getXMLElementTagName() {
        return "colony";
    }

    
    public Colony getColony() {
        return this;
    }
    
    
    public boolean hasStockade() {
        return (getStockade() != null);
    }
    
     
    public Building getStockade() {
        
        for (Building building : buildingMap.values()) {
            if (!building.getType().getModifierSet("model.modifier.defence").isEmpty()) {
                return building;
            }
        }
        return null;
    }

    
    public final Set<Modifier> getModifierSet(String id) {
        Set<Modifier> result = featureContainer.getModifierSet(id, null, getGame().getTurn());
        result.addAll(owner.getFeatureContainer().getModifierSet(id, null, getGame().getTurn()));
        return result;
    }

    
    public boolean hasAbility(String id) {
        return hasAbility(id, null);
    }

    
    public boolean hasAbility(String id, FreeColGameObjectType type) {
        HashSet<Ability> colonyAbilities = 
            new HashSet<Ability>(featureContainer.getAbilitySet(id, type, getGame().getTurn()));
        Set<Ability> playerAbilities = owner.getFeatureContainer().getAbilitySet(id, type, getGame().getTurn());
        colonyAbilities.addAll(playerAbilities);
        return FeatureContainer.hasAbility(colonyAbilities);
    }

    public FeatureContainer getFeatureContainer() {
        return featureContainer;
    }
    
    
    public boolean canBombardEnemyShip(){
    	
    	if(isLandLocked()){
            return false;
    	}
    	
    	if(!hasAbility("model.ability.bombardShips")){
            return false;
    	}
    	return true;
    }
}
