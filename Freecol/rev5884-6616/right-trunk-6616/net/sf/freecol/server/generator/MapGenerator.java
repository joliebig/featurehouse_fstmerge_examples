

package net.sf.freecol.server.generator;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.FreeColException;
import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.io.FreeColSavegameFile;
import net.sf.freecol.common.model.AbstractUnit;
import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.BuildingType;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.ColonyTile;
import net.sf.freecol.common.model.EuropeanNationType;
import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.IndianNationType;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.LostCityRumour;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.NationType;
import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TileImprovement;
import net.sf.freecol.common.model.TileImprovementType;
import net.sf.freecol.common.model.TileItemContainer;
import net.sf.freecol.common.model.TileType;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.model.Map.Position;
import net.sf.freecol.common.model.Settlement.SettlementType;
import net.sf.freecol.common.model.Unit.UnitState;
import net.sf.freecol.common.util.RandomChoice;
import net.sf.freecol.common.util.XMLStream;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerGame;
import net.sf.freecol.server.model.ServerPlayer;
import net.sf.freecol.server.model.ServerRegion;



public class MapGenerator implements IMapGenerator {

    private static final Logger logger = Logger.getLogger(MapGenerator.class.getName());
    
    private final Random random;
    private final MapGeneratorOptions mapGeneratorOptions;
    
    private final LandGenerator landGenerator;
    private final TerrainGenerator terrainGenerator;
    
    
    
    private static final float MIN_DISTANCE_FROM_POLE = 0.30f;

    
    
    public MapGenerator() {
        this.mapGeneratorOptions = new MapGeneratorOptions();
        this.random = new Random();
                
        landGenerator = new LandGenerator(mapGeneratorOptions);
        terrainGenerator = new TerrainGenerator(mapGeneratorOptions);
    }


    
    
    public void createMap(Game game) throws FreeColException {        
        
        
        final File importFile = getMapGeneratorOptions().getFile(MapGeneratorOptions.IMPORT_FILE);
        final Game importGame;
        if (importFile != null) {
            importGame = loadSaveGame(importFile);
        } else {
            importGame = null;
        }
        
        
        boolean[][] landMap;
        if (importGame != null) {
            landMap = LandGenerator.importLandMap(importGame);
        } else {
            landMap = landGenerator.createLandMap();
        }
        
        
        terrainGenerator.createMap(game, importGame, landMap);

        Map map = game.getMap();        
        createIndianSettlements(map, game.getPlayers());
        createEuropeanUnits(map, game.getPlayers());
        createLostCityRumours(map, importGame);
        
    }
    
    
    private Game loadSaveGame(File importFile) throws FreeColException {        
        
        XMLStream xs = null;
        Game game = null;
        try {
            final FreeColSavegameFile fis = new FreeColSavegameFile(importFile);
            xs = FreeColServer.createXMLStreamReader(fis);
            final XMLStreamReader xsr = xs.getXMLStreamReader();
            xsr.nextTag();
            
            FreeColServer.checkSavegameVersion(xsr);
            
            ArrayList<Object> serverObjects = null;
            while (xsr.nextTag() != XMLStreamConstants.END_ELEMENT) {
                if (xsr.getLocalName().equals("serverObjects")) {
                    
                    serverObjects = new ArrayList<Object>();
                    while (xsr.nextTag() != XMLStreamConstants.END_ELEMENT) {
                        if (xsr.getLocalName().equals(ServerPlayer.getServerAdditionXMLElementTagName())) {
                            serverObjects.add(new ServerPlayer(xsr));
                        } else {
                            throw new XMLStreamException("Unknown tag: " + xsr.getLocalName());
                        }
                    }
                } else if (xsr.getLocalName().equals(Game.getXMLElementTagName())) {
                    
                    game = new ServerGame(null, null, xsr, serverObjects
                                    .toArray(new FreeColGameObject[serverObjects.size()]));
                    game.setCurrentPlayer(null);
                    game.checkIntegrity();
                }
            }
            xs.close();
        } catch (XMLStreamException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.warning(sw.toString());
            throw new FreeColException(e.toString());
        } catch (FreeColException fe) {
            StringWriter sw = new StringWriter();
            fe.printStackTrace(new PrintWriter(sw));
            logger.warning(sw.toString());
            throw new FreeColException(fe.toString());
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.warning(sw.toString());
            throw new FreeColException(e.toString());
        } finally {
            xs.close();
        }
        return game;
    }

    public LandGenerator getLandGenerator() {
        return landGenerator;
    }

    public TerrainGenerator getTerrainGenerator() {
        return terrainGenerator;
    }

    
    public MapGeneratorOptions getMapGeneratorOptions() {
        return mapGeneratorOptions;
    }

    
    private void createLostCityRumours(Map map) {
        createLostCityRumours(map, null);
    }
    
    
    private void createLostCityRumours(Map map, Game importGame) {
        final boolean importRumours = getMapGeneratorOptions().getBoolean(MapGeneratorOptions.IMPORT_RUMOURS);
        
        if (importGame != null && importRumours) {
            for (Tile importTile : importGame.getMap().getAllTiles()) {
            	LostCityRumour rumor = importTile.getLostCityRumour();
            	
            	if(rumor == null){
            		continue;
            	}
                final Position p = importTile.getPosition();
                if (map.isValid(p)) {
                    final Tile t = map.getTile(p);
                    t.add(rumor);
                }
            }
        } else {
            int number = getMapGeneratorOptions().getNumberOfRumours();
            int counter = 0;

            
            if (importGame != null) {
                number = map.getWidth() * map.getHeight() * 25 / (100 * 35);
            }
            

            for (int i = 0; i < number; i++) {
                for (int tries=0; tries<100; tries++) {
                    Position p = new Position(random.nextInt(map.getWidth()), 
                            random.nextInt(map.getHeight()));
                    if (p.y <= LandGenerator.POLAR_HEIGHT ||
                        p.y >= map.getHeight() - LandGenerator.POLAR_HEIGHT - 1) {
                        
                        
                        continue;
                    }
                    Tile t = map.getTile(p);
                    if (map.getTile(p).isLand()
                            && !t.hasLostCityRumour()
                            && t.getSettlement() == null
                            && t.getUnitCount() == 0) { 
                        counter++;
                        t.add(new LostCityRumour(t.getGame(), t));
                        break;
                    }
                }
            }

            logger.info("Created " + counter + " lost city rumours of maximum " + number + ".");
        }
    }

    
    private void createIndianSettlements(final Map map, List<Player> players) {

        float shares = 0f;

        List<Player> indians = new ArrayList<Player>();
        HashMap<String, Territory> territoryMap = new HashMap<String, Territory>();
        for (Player player : players) {
            if (!player.isIndian()) {
                continue;
            }
            switch (((IndianNationType) player.getNationType()).getNumberOfSettlements()) {
            case HIGH:
                shares += 4;
                break;
            case AVERAGE:
                shares += 3;
                break;
            case LOW:
                shares += 2;
                break;
            }
            indians.add(player);
            List<String> regionNames = ((IndianNationType) player.getNationType()).getRegionNames();
            Territory territory = null;
            if (regionNames == null || regionNames.isEmpty()) {
                territory = new Territory(player, map.getRandomLandPosition());
                territoryMap.put(player.getId(), territory);
            } else {
                for (String name : regionNames) {
                    if (territoryMap.get(name) == null) {
                        ServerRegion region = (ServerRegion) map.getRegion(name);
                        if (region == null) {
                            territory = new Territory(player, map.getRandomLandPosition());
                        } else {
                            territory = new Territory(player, region);
                        }
                        territoryMap.put(name, territory);
                        logger.fine("Allocated region " + name + " for " +
                                    player + ". Center is " +
                                    territory.getCenter() + ".");
                        break;
                    }
                }
                if (territory == null) {
                    logger.warning("Failed to allocate preferred region " + regionNames.get(0) +
                                   " for " + player.getNation());
                    outer: for (String name : regionNames) {
                        Territory otherTerritory = territoryMap.get(name);
                        for (String otherName : ((IndianNationType) otherTerritory.player.getNationType())
                                 .getRegionNames()) {
                            if (territoryMap.get(otherName) == null) {
                                ServerRegion foundRegion = otherTerritory.region;
                                otherTerritory.region = (ServerRegion) map.getRegion(otherName);
                                territoryMap.put(otherName, otherTerritory);
                                territory = new Territory(player, foundRegion);
                                territoryMap.put(name, territory);
                                break outer;
                            }
                        }
                    }
                    if (territory == null) {
                        logger.warning("Unable to find free region for " + player.getName());
                        territory = new Territory(player, map.getRandomLandPosition());
                        territoryMap.put(player.getId(), territory);
                    }
                }
            }
        }

        if (indians.isEmpty()) {
            return;
        }

        List<Territory> territories = new ArrayList<Territory>(territoryMap.values());
        List<Tile> settlementTiles = new ArrayList<Tile>();

        final int minSettlementDistance = 3;
        
        
        int nativeSettlementDensity = 50;
        boolean isNativeSettlementDensitySet = Specification.getSpecification().hasOption("model.option.nativeSettlementDensity");
        
        if(isNativeSettlementDensitySet){
        	nativeSettlementDensity = Specification.getSpecification().getIntegerOption("model.option.nativeSettlementDensity").getValue();
        }
        
        int number = mapGeneratorOptions.getNumberOfSettlements() * nativeSettlementDensity / 100;

        for (int i = 0; i < number; i++) {
            nextTry: for (int tries = 0; tries < 100; tries++) {
                Position position = map.getRandomLandPosition();
                if (position.getY() <= LandGenerator.POLAR_HEIGHT ||
                    position.getY() >= map.getHeight() - LandGenerator.POLAR_HEIGHT - 1) {
                    continue;
                }
                Tile candidate = map.getTile(position);
                if (candidate.isSettleable()) {
                    for (Tile tile : settlementTiles) {
                        if (map.getDistance(position, tile.getPosition()) < minSettlementDistance) {
                            continue nextTry;
                        }
                    }                            
                    settlementTiles.add(candidate);
                    break;
                }
            }
        }
        int potential = settlementTiles.size();

        int capitals = indians.size();
        if (potential < capitals) {
            logger.warning("Number of potential settlements is smaller than number of tribes.");
            capitals = potential;
        }

        
        float share = settlementTiles.size() / shares;

        
        int counter = 0;
        for (Territory territory : territories) {
            switch (((IndianNationType) territory.player.getNationType()).getNumberOfSettlements()) {
            case HIGH:
                territory.numberOfSettlements = Math.round(4 * share);
                break;
            case AVERAGE:
                territory.numberOfSettlements = Math.round(3 * share);
                break;
            case LOW:
                territory.numberOfSettlements = Math.round(2 * share);
                break;
            }
            Tile tile = getClosestTile(map, territory.getCenter(), settlementTiles);
            if (tile == null) {
                
                break;
            } else {
                String name = "default region";
                if (territory.region != null) {
                    name = territory.region.getNameKey();
                }
                logger.fine("Placing the " + territory.player + 
                        " capital in region: " + name +
                        " at Tile: "+ tile.getPosition());
                placeIndianSettlement(territory.player, true, tile.getPosition(), map);
                territory.numberOfSettlements--;
                territory.position = tile.getPosition();
                settlementTiles.remove(tile);
                counter++;
            }
        }

        
        Collections.sort(settlementTiles, new Comparator<Tile>() {
            public int compare(Tile tile1, Tile tile2) {
                int distance1 = Math.min(Math.min(tile1.getX(), map.getWidth() - tile1.getX()),
                                         Math.min(tile1.getY(), map.getHeight() - tile1.getY()));
                int distance2 = Math.min(Math.min(tile2.getX(), map.getWidth() - tile2.getX()),
                                         Math.min(tile2.getY(), map.getHeight() - tile2.getY()));
                return (distance1 - distance2);
            }
        });

        
        for (Tile tile : settlementTiles) {
            Territory territory = getClosestTerritory(map, tile, territories);
            if (territory == null) {
                
                break;
            } else {
                String name = "default region";
                if (territory.region != null) {
                    name = territory.region.getNameKey();
                }
                logger.fine("Placing a " + territory.player + 
                        " camp in region: " + name +
                        " at Tile: "+ tile.getPosition());
                placeIndianSettlement(territory.player, false, tile.getPosition(), map);
                counter++;
                if (territory.numberOfSettlements < 2) {
                    territories.remove(territory);
                } else {
                    territory.numberOfSettlements--;
                }
            }
        }

        logger.info("Created " + counter + " Indian settlements of maximum " + potential);
    }


    private Tile getClosestTile(Map map, Position center, List<Tile> tiles) {
        Tile result = null;
        int minimumDistance = Integer.MAX_VALUE;
        for (Tile tile : tiles) {
            int distance = map.getDistance(tile.getPosition(), center);
            if (distance < minimumDistance) {
                minimumDistance = distance;
                result = tile;
            }
        }
        return result;
    }

    private Territory getClosestTerritory(Map map, Tile tile, List<Territory> territories) {
        Territory result = null;
        int minimumDistance = Integer.MAX_VALUE;
        for (Territory territory : territories) {
            int distance = map.getDistance(tile.getPosition(), territory.getCenter());
            if (distance < minimumDistance) {
                minimumDistance = distance;
                result = territory;
            }
        }
        return result;
    }


    
    private IndianSettlement placeIndianSettlement(Player player, boolean capital,
                                       Position position, Map map) {
        final Tile tile = map.getTile(position);
        IndianSettlement settlement = 
            new IndianSettlement(map.getGame(), player, tile,
                                 Messages.getDefaultSettlementName(player, capital), capital,
                                 generateSkillForLocation(map, tile, player.getNationType()),
                                 new HashSet<Player>(), null);
        SettlementType kind = settlement.getTypeOfSettlement();
        logger.fine("Generated skill: " + settlement.getLearnableSkill());

        int unitCount = settlement.getGeneratedUnitCount();
        for (int i = 0; i < unitCount; i++) {
            UnitType unitType = FreeCol.getSpecification().getUnitType("model.unit.brave");
            Unit unit = new Unit(map.getGame(), settlement, player, unitType, UnitState.ACTIVE,
                                 unitType.getDefaultEquipment());
            unit.setIndianSettlement(settlement);

            if (i == 0) {
                unit.setLocation(tile);
            } else {
                unit.setLocation(settlement);
            }
        }
        settlement.placeSettlement();
        Map.CircleIterator iterator = map.getCircleIterator(position, false, settlement.getRadius() + 1);
        while (iterator.hasNext()) {
            Position p = iterator.next();
            if (map.getTile(p).isLand() && random.nextInt(2) == 0) {
                settlement.claimTile(map.getTile(p));
            }
        }

        iterator = map.getCircleIterator(position, false, settlement.getRadius() + 2);
        while (iterator.hasNext()) {
            Position p = iterator.next();
            if (map.getTile(p).isLand() && random.nextInt(4) == 0) {
                settlement.claimTile(map.getTile(p));
            }
        }

        
        if (FreeCol.isInDebugMode()) {
            for (GoodsType goodsType : FreeCol.getSpecification().getGoodsTypeList()) {
                if (goodsType.isNewWorldGoodsType())
                    settlement.addGoods(goodsType, 150);
            }
        }
        
        
        return settlement;
    }
    
    
    private UnitType generateSkillForLocation(Map map, Tile tile, NationType nationType) {
        List<RandomChoice<UnitType>> skills = ((IndianNationType) nationType).getSkills();
        java.util.Map<GoodsType, Integer> scale = new HashMap<GoodsType, Integer>();
        for (RandomChoice<UnitType> skill : skills) {
            scale.put(skill.getObject().getExpertProduction(), 1);
        }

        Iterator<Position> iter = map.getAdjacentIterator(tile.getPosition());
        while (iter.hasNext()) {
            Map.Position p = iter.next();
            Tile t = map.getTile(p);
            for (GoodsType goodsType : scale.keySet()) {
                scale.put(goodsType, scale.get(goodsType).intValue() + t.potential(goodsType, null));
            }
        }

        List<RandomChoice<UnitType>> scaledSkills = new ArrayList<RandomChoice<UnitType>>();
        for (RandomChoice<UnitType> skill : skills) {
            UnitType unitType = skill.getObject();
            int scaleValue = scale.get(unitType.getExpertProduction()).intValue();
            scaledSkills.add(new RandomChoice<UnitType>(unitType, skill.getProbability() * scaleValue));
        }
        UnitType skill = RandomChoice.getWeightedRandom(random, scaledSkills);
        if (skill == null) {
            
            List<UnitType> unitList = FreeCol.getSpecification().getUnitTypesWithAbility("model.ability.expertScout");
            return unitList.get(random.nextInt(unitList.size()));
        } else {
            return skill;
        }
    }

    
    private void createEuropeanUnits(Map map, List<Player> players) {
        final int width = map.getWidth();
        final int height = map.getHeight();
        final int poleDistance = (int)(MIN_DISTANCE_FROM_POLE*height/2);
        Game game = map.getGame();

        List<Player> europeanPlayers = new ArrayList<Player>();
        for (Player player : players) {
            if (player.isREF()) {
                
                int x = width - 2;
                
                int y = random.nextInt(height - 2*poleDistance) + poleDistance;
                player.setEntryLocation(map.getTile(x, y));
                continue;
            }
            if (player.isEuropean()) {
                europeanPlayers.add(player);
                logger.finest("found European player " + player);
            }
        }
        int startingPositions = europeanPlayers.size();
        List<Integer> startingYPositions = new ArrayList<Integer>();

        for (Player player : europeanPlayers) {
            logger.fine("generating units for player " + player);

            List<Unit> carriers = new ArrayList<Unit>();
            List<Unit> passengers = new ArrayList<Unit>();
            List<AbstractUnit> unitList = ((EuropeanNationType) player.getNationType())
                .getStartingUnits();
            for (AbstractUnit startingUnit : unitList) {
                Unit newUnit = new Unit(game, null, player, startingUnit.getUnitType(),
                                        UnitState.SENTRY, startingUnit.getEquipment());
                if (newUnit.canCarryUnits() && newUnit.isNaval()) {
                    newUnit.setState(UnitState.ACTIVE);
                    carriers.add(newUnit);
                } else {
                    passengers.add(newUnit);
                }
                
            }

            boolean startAtSea = true;
            if (carriers.isEmpty()) {
                logger.warning("No carriers defined for player " + player);
                startAtSea = false;
            }

            
            int x = width - 1;  
            int y;
            do {
                 y = random.nextInt(height - poleDistance*2) + poleDistance;
            } while (map.getTile(x, y).isLand() == startAtSea ||
                     isStartingPositionTooClose(map, y, startingPositions, startingYPositions));
            startingYPositions.add(new Integer(y));
            
            if (startAtSea) {
                
                while (map.getTile(x - 1, y).canMoveToEurope()) {
                    x--;
                }
            }

            Tile startTile = map.getTile(x,y);
            startTile.setExploredBy(player, true);
            player.setEntryLocation(startTile);

            if (startAtSea) {
                for (Unit carrier : carriers) {
                    carrier.setLocation(startTile);
                }
                passengers: for (Unit unit : passengers) {
                    for (Unit carrier : carriers) {
                        if (carrier.getSpaceLeft() >= unit.getSpaceTaken()) {
                            unit.setLocation(carrier);
                            continue passengers;
                        }
                    }
                    
                    unit.setLocation(player.getEurope());
                }
            } else {
                for (Unit unit : passengers) {
                    unit.setLocation(startTile);
                }
            }
            
            
            if (FreeCol.isInDebugMode()) {
                
                UnitType unitType = FreeCol.getSpecification().getUnitType("model.unit.galleon");
                Unit unit4 = new Unit(game, startTile, player, unitType, UnitState.ACTIVE);
                
                unitType = FreeCol.getSpecification().getUnitType("model.unit.privateer");
                @SuppressWarnings("unused") Unit privateer = new Unit(game, startTile, player, unitType, UnitState.ACTIVE);
                
                unitType = FreeCol.getSpecification().getUnitType("model.unit.freeColonist");
                @SuppressWarnings("unused") Unit unit5 = new Unit(game, unit4, player, unitType, UnitState.SENTRY);
                unitType = FreeCol.getSpecification().getUnitType("model.unit.veteranSoldier");
                @SuppressWarnings("unused") Unit unit6 = new Unit(game, unit4, player, unitType, UnitState.SENTRY);
                unitType = FreeCol.getSpecification().getUnitType("model.unit.jesuitMissionary");
                @SuppressWarnings("unused") Unit unit7 = new Unit(game, unit4, player, unitType, UnitState.SENTRY);

                Tile colonyTile = null;
                Iterator<Position> cti = map.getFloodFillIterator(new Position(x, y));
                while(cti.hasNext()) {
                    Tile tempTile = map.getTile(cti.next());
                    if (tempTile.getY() <= LandGenerator.POLAR_HEIGHT ||
                        tempTile.getY() >= map.getHeight() - LandGenerator.POLAR_HEIGHT - 1) {
                        
                        continue;
                    }
                    if (tempTile.isColonizeable()) {
                        colonyTile = tempTile;
                        break;
                    }
                }

                if (colonyTile != null) {
                    for (TileType t : FreeCol.getSpecification().getTileTypeList()) {
                        if (!t.isWater()) {
                            colonyTile.setType(t);
                            break;
                        }
                    }
                    unitType = FreeCol.getSpecification().getUnitType("model.unit.expertFarmer");
                    Unit buildColonyUnit = new Unit(game, colonyTile, player, unitType, UnitState.ACTIVE);
                    String colonyName = Messages.message(player.getNationName()) + " Colony";
                    Colony colony = new Colony(game, player, colonyName, colonyTile);
                    buildColonyUnit.buildColony(colony);
                    if (buildColonyUnit.getLocation() instanceof ColonyTile) {
                        Tile ct = ((ColonyTile) buildColonyUnit.getLocation()).getWorkTile();
                        for (TileType t : FreeCol.getSpecification().getTileTypeList()) {
                            if (!t.isWater()) {
                                ct.setType(t);
                                TileImprovementType plowType = FreeCol.getSpecification()
                                    .getTileImprovementType("model.improvement.plow");
                                TileImprovementType roadType = FreeCol.getSpecification()
                                    .getTileImprovementType("model.improvement.road");
                                TileImprovement road = new TileImprovement(game, ct, roadType);
                                road.setTurnsToComplete(0);
                                TileImprovement plow = new TileImprovement(game, ct, plowType);
                                plow.setTurnsToComplete(0);
                                ct.setTileItemContainer(new TileItemContainer(game, ct));
                                ct.getTileItemContainer().addTileItem(road);
                                ct.getTileItemContainer().addTileItem(plow);
                                break;
                            }
                        }
                    }
                    BuildingType schoolType = FreeCol.getSpecification().getBuildingType("model.building.schoolhouse");
                    Building schoolhouse = new Building(game, colony, schoolType);
                    colony.addBuilding(schoolhouse);
                    unitType = FreeCol.getSpecification().getUnitType("model.unit.masterCarpenter");
                    while (!schoolhouse.canAdd(unitType)) {
                        schoolhouse.upgrade();
                    }
                    Unit carpenter = new Unit(game, colonyTile, player, unitType, UnitState.ACTIVE);
                    carpenter.setLocation(colony.getBuildingForProducing(unitType.getExpertProduction()));

                    unitType = FreeCol.getSpecification().getUnitType("model.unit.elderStatesman");
                    Unit statesman = new Unit(game, colonyTile, player, unitType, UnitState.ACTIVE);
                    statesman.setLocation(colony.getBuildingForProducing(unitType.getExpertProduction()));

                    unitType = FreeCol.getSpecification().getUnitType("model.unit.expertLumberJack");
                    Unit lumberjack = new Unit(game, colony, player, unitType, UnitState.ACTIVE);
                    if (lumberjack.getLocation() instanceof ColonyTile) {
                        Tile lt = ((ColonyTile) lumberjack.getLocation()).getWorkTile();
                        for (TileType t : FreeCol.getSpecification().getTileTypeList()) {
                            if (t.isForested()) {
                                lt.setType(t);
                                break;
                            }
                        }
                        lumberjack.setWorkType(lumberjack.getType().getExpertProduction());
                    }

                    unitType = FreeCol.getSpecification().getUnitType("model.unit.seasonedScout");
                    @SuppressWarnings("unused")
                    Unit scout = new Unit(game, colonyTile, player, 
                                          unitType, UnitState.ACTIVE);

                    unitType = FreeCol.getSpecification().getUnitType("model.unit.veteranSoldier");
                    @SuppressWarnings("unused")
                    Unit unit8 = new Unit(game, colonyTile, player, 
                                          unitType, UnitState.ACTIVE);

                    @SuppressWarnings("unused")
                    Unit unit9 = new Unit(game, colonyTile, player, 
                                          unitType, UnitState.ACTIVE);

                    unitType = FreeCol.getSpecification().getUnitType("model.unit.artillery");
                    @SuppressWarnings("unused")
                    Unit unit10 = new Unit(game, colonyTile, player,
                                           unitType, UnitState.ACTIVE);

                    @SuppressWarnings("unused")
                    Unit unit11 = new Unit(game, colonyTile, player,
                                           unitType, UnitState.ACTIVE);

                    @SuppressWarnings("unused")
                    Unit unit12 = new Unit(game, colonyTile, player,
                                           unitType, UnitState.ACTIVE);

                    unitType = FreeCol.getSpecification().getUnitType("model.unit.treasureTrain");
                    Unit unit13 = new Unit(game, colonyTile, player, unitType, UnitState.ACTIVE);
                    unit13.setTreasureAmount(10000);
                    
                    unitType = FreeCol.getSpecification().getUnitType("model.unit.wagonTrain");
                    Unit unit14 = new Unit(game, colonyTile, player, unitType, UnitState.ACTIVE);
                    GoodsType cigarsType = FreeCol.getSpecification().getGoodsType("model.goods.cigars");
                    Goods cigards = new Goods(game, unit14, cigarsType, 5);
                    unit14.add(cigards);

                    unitType = FreeCol.getSpecification().getUnitType("model.unit.jesuitMissionary");
                    @SuppressWarnings("unused") Unit unit15 = new Unit(game, colonyTile, player,
                                                                       unitType, UnitState.ACTIVE);
                    @SuppressWarnings("unused") Unit unit16 = new Unit(game, colonyTile, player,
                                                                       unitType, UnitState.ACTIVE);

                }
            }
            
        }
    }

    
    private boolean isStartingPositionTooClose(Map map, int proposedY, int startingPositions,
                                                 List<Integer> usedYPositions) {
        final int poleDistance = (int)(MIN_DISTANCE_FROM_POLE*map.getHeight()/2);
        final int spawnableRange = map.getHeight() - poleDistance*2;
        final int minimumDistance = spawnableRange / (startingPositions * 2);
        for (Integer yPosition : usedYPositions) {
            if (Math.abs(yPosition.intValue() - proposedY) < minimumDistance) {
                return true;
            }
        }
        return false;
    }

    private class Territory {
        public ServerRegion region;
        public Position position;
        public Player player;
        public int numberOfSettlements;

        public Territory(Player player, Position position) {
            this.player = player;
            this.position = position;
        }

        public Territory(Player player, ServerRegion region) {
            this.player = player;
            this.region = region;
        }

        public Position getCenter() {
            if (position == null) {
                return region.getCenter();
            } else {
                return position;
            }
        }
        
        public String toString() {
            return player + " territory at " + region.toString();
        }
    }


}
