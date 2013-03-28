

package net.sf.freecol.server.generator;

import java.awt.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.logging.Logger;

import net.sf.freecol.common.Specification;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Map.Position;
import net.sf.freecol.common.model.Map.WholeMapIterator;
import net.sf.freecol.common.model.Region;
import net.sf.freecol.common.model.Region.RegionType;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TileImprovement;
import net.sf.freecol.common.model.TileImprovementType;
import net.sf.freecol.common.model.TileItemContainer;
import net.sf.freecol.common.model.TileType;
import net.sf.freecol.server.model.ServerRegion;
import net.sf.freecol.common.util.RandomChoice;



public class TerrainGenerator {

    private static final Logger logger = Logger.getLogger(TerrainGenerator.class.getName());
    
    public static final int LAND_REGIONS_SCORE_VALUE = 1000;
    public static final int LAND_REGION_MIN_SCORE = 5;
    public static final int PACIFIC_SCORE_VALUE = 100;

    public static final int LAND_REGION_MAX_SIZE = 75;

    private final MapGeneratorOptions mapGeneratorOptions;

    private final Random random = new Random();

    private TileType ocean = Specification.getSpecification().getTileType("model.tile.ocean");
    private TileType lake = Specification.getSpecification().getTileType("model.tile.lake");
    private TileImprovementType riverType =
        Specification.getSpecification().getTileImprovementType("model.improvement.river");
    private TileImprovementType fishBonusLandType =
        Specification.getSpecification().getTileImprovementType("model.improvement.fishBonusLand");
    private TileImprovementType fishBonusRiverType =
        Specification.getSpecification().getTileImprovementType("model.improvement.fishBonusRiver");

    private ArrayList<TileType> terrainTileTypes = null;

    
    public TerrainGenerator(MapGeneratorOptions mapGeneratorOptions) {
        this.mapGeneratorOptions = mapGeneratorOptions;
    }


    
    public void createMap(Game game, boolean[][] landMap) {
        createMap(game, null, landMap);
    }


    
    public void createMap(Game game, Game importGame, boolean[][] landMap) {
        final int width = landMap.length;
        final int height = landMap[0].length;

        final boolean importTerrain = (importGame != null)
            && getMapGeneratorOptions().getBoolean(MapGeneratorOptions.IMPORT_TERRAIN);
        final boolean importBonuses = (importGame != null)
            && getMapGeneratorOptions().getBoolean(MapGeneratorOptions.IMPORT_BONUSES);

        boolean mapHasLand = false;
        Tile[][] tiles = new Tile[width][height];
        for (int y = 0; y < height; y++) {
            int latitude = (Math.min(y, (height-1) - y) * 200) / height; 
            for (int x = 0; x < width; x++) {
                if (landMap[x][y]) {
                    mapHasLand = true;
                }
                Tile t;
                if (importTerrain && importGame.getMap().isValid(x, y)) {
                    Tile importTile = importGame.getMap().getTile(x, y);
                    if (importTile.isLand() == landMap[x][y]) {
                        t = new Tile(game, importTile.getType(), x, y);
                        
                        if (importTile.getTileItemContainer() != null) {
                            TileItemContainer container = new TileItemContainer(game, t);
                            container.copyFrom(importTile.getTileItemContainer(), importBonuses, true);
                            t.setTileItemContainer(container);
                        }
                    } else {
                        t = createTile(game, x, y, landMap, latitude);
                    }
                } else {
                    t = createTile(game, x, y, landMap, latitude);
                }
                tiles[x][y] = t;
            }
        }

        Map map = new Map(game, tiles);
        game.setMap(map);

        if (!importTerrain) {
            createOceanRegions(map);
            createHighSeas(map);
            if (mapHasLand) {
                createMountains(map);
                findLakes(map);
                createRivers(map);
                createLandRegions(map);
            }
        }

        
        
        
        WholeMapIterator iterator = map.getWholeMapIterator();
        while (iterator.hasNext()) {
            Tile tile = map.getTile(iterator.next());
            perhapsAddBonus(game, tile, !importBonuses);
            if (!tile.isLand()) {
                encodeStyle(tile);
            }
        }
    }

    public static void encodeStyle(Tile tile) {
        int x = tile.getX();
        int y = tile.getY();
        int base = 1;
        int style = 0;
        for (Direction d : Direction.values()) {
            Tile otherTile = tile.getMap().getNeighbourOrNull(d, x, y);
            if (otherTile != null && otherTile.isLand()) {
                style += base;
            }
            base *= 2;
        }
        tile.setStyle(style);
    }


    private Tile createTile(Game game, int x, int y, boolean[][] landMap, int latitude) {
        Tile t;
        if (landMap[x][y]) {
            t = new Tile(game, getRandomLandTileType(latitude), x, y);
        } else {
            t = new Tile(game, ocean, x, y);
        }
        
        return t;
    }


    
    private void perhapsAddBonus(Game game, Tile t, boolean generateBonus) {
        if (t.isLand()) {
            if (generateBonus && random.nextInt(100) < getMapGeneratorOptions().getPercentageOfBonusTiles()) {
                
                t.setResource(RandomChoice.getWeightedRandom(random, t.getType().getWeightedResources()));
            }
        } else {
            int adjacentLand = 0;
            int riverBonus = 0;
            int fishBonus = 0;
            boolean adjacentRiver = false;
            for (Direction direction : Direction.values()) {
                Tile otherTile = t.getMap().getNeighbourOrNull(direction, t);
                if (otherTile != null && otherTile.isLand()) {
                    adjacentLand++;
                    if (otherTile.hasRiver()) {
                        adjacentRiver = true;
                    }
                }
            }

            
            
            if (adjacentLand > 2) {
                t.add(new TileImprovement(game, t, fishBonusLandType));
            }
                
            
            
            
            
            if (!t.hasRiver() && adjacentRiver) {
                t.add(new TileImprovement(game, t, fishBonusRiverType));
            }

            if (t.getType().isConnected()) {
                if (generateBonus && adjacentLand > 1 && random.nextInt(10 - adjacentLand) == 0) {
                    t.setResource(RandomChoice.getWeightedRandom(random, t.getType().getWeightedResources()));
                }
            } else {
                if (random.nextInt(100) < getMapGeneratorOptions().getPercentageOfBonusTiles()) {
                    
                    t.setResource(RandomChoice.getWeightedRandom(random, t.getType().getWeightedResources()));
                }
            }
        }
    }


    
    private MapGeneratorOptions getMapGeneratorOptions() {
        return mapGeneratorOptions;
    }


    
    private TileType getRandomLandTileType(int latitudePercent) {
        
        final int forestChance = getMapGeneratorOptions().getPercentageOfForests();
        final int temperaturePreference = getMapGeneratorOptions().getTemperature();
        
        
        if (terrainTileTypes==null) {
            terrainTileTypes = new ArrayList<TileType>();
            for (TileType tileType : Specification.getSpecification().getTileTypeList()) {
                if (tileType.getId().equals("model.tile.hills") ||
                    tileType.getId().equals("model.tile.mountains") ||
                    tileType.isWater()) {
                    
                    
                    continue;
                }
                terrainTileTypes.add(tileType);
            }
        }

        
        int poleTemperature = -20;
        int equatorTemperature= 40;
        if (temperaturePreference==MapGeneratorOptions.TEMPERATURE_COLD) {
            poleTemperature = -20;
            equatorTemperature = 25;
        } else if (temperaturePreference==MapGeneratorOptions.TEMPERATURE_CHILLY) {
            poleTemperature = -20;
            equatorTemperature = 30;
        } else if (temperaturePreference==MapGeneratorOptions.TEMPERATURE_TEMPERATE) {
            poleTemperature = -10;
            equatorTemperature = 35;
        } else if (temperaturePreference==MapGeneratorOptions.TEMPERATURE_WARM) {
            poleTemperature = -5;
            equatorTemperature = 40;
        } else if (temperaturePreference==MapGeneratorOptions.TEMPERATURE_HOT) {
            poleTemperature = 0;
            equatorTemperature = 40;
        }
        int temperatureRange = equatorTemperature-poleTemperature;
        int localeTemperature = poleTemperature + latitudePercent*temperatureRange/100;
        int temperatureDeviation = 7; 
        localeTemperature += random.nextInt(temperatureDeviation*2)-temperatureDeviation;
        if (localeTemperature>40)
            localeTemperature = 40;
        if (localeTemperature<-20)
            localeTemperature = -20;
        
        
        int localeHumidity = Specification.getSpecification().getRangeOption(MapGeneratorOptions.HUMIDITY).getValue();
        int humidityDeviation = 20; 
        localeHumidity += random.nextInt(humidityDeviation*2) - humidityDeviation;
        if (localeHumidity<0) 
            localeHumidity = 0;
        if (localeHumidity>100)
            localeHumidity = 100;
        
        
        ArrayList<TileType> candidateTileTypes = new ArrayList<TileType>();
        candidateTileTypes.addAll(terrainTileTypes);
        
        
        Iterator<TileType> it = candidateTileTypes.iterator();
        while (it.hasNext()) {
            TileType t = it.next();
            if (!t.withinRange(TileType.RangeType.TEMPERATURE, localeTemperature)) {
                it.remove();
            }
        }
        if (candidateTileTypes.size() == 1) {
            return candidateTileTypes.get(0);
        } else if (candidateTileTypes.size()==0) {
            throw new RuntimeException("No TileType for temperature==" + localeTemperature );
        }
        
        
        it = candidateTileTypes.iterator();
        while (it.hasNext()) {
            TileType t = it.next();
            if (!t.withinRange(TileType.RangeType.HUMIDITY, localeHumidity)) {
                it.remove();
            }
        }
        if (candidateTileTypes.size() == 1) {
            return candidateTileTypes.get(0);
        } else if (candidateTileTypes.size()==0) {
            throw new RuntimeException("No TileType for temperature==" + localeTemperature 
                    +" and humidity==" + localeHumidity);
        }
 
        
        boolean forested = random.nextInt(100) < forestChance;
        it = candidateTileTypes.iterator();
        while (it.hasNext()) {
            TileType t = it.next();
            if (t.isForested() != forested) {
                it.remove();
            }
        }
        if (candidateTileTypes.size() == 1) {
            return candidateTileTypes.get(0);
        } else if (candidateTileTypes.size()==0) {
            throw new RuntimeException("No TileType for temperature==" + localeTemperature 
                    +" and humidity==" + localeHumidity + " and forested=="+forested);
        }
        
        
        return candidateTileTypes.get(random.nextInt(candidateTileTypes.size()));
    }


    
    private void createOceanRegions(Map map) {
        Game game = map.getGame();

        ServerRegion pacific =
            new ServerRegion(game, "model.region.pacific", RegionType.OCEAN);
        ServerRegion northPacific =
            new ServerRegion(game, "model.region.northPacific", RegionType.OCEAN, pacific);
        ServerRegion southPacific =
            new ServerRegion(game, "model.region.southPacific", RegionType.OCEAN, pacific);
        ServerRegion atlantic =
            new ServerRegion(game, "model.region.atlantic", RegionType.OCEAN);
        ServerRegion northAtlantic =
            new ServerRegion(game, "model.region.northAtlantic", RegionType.OCEAN, atlantic);
        ServerRegion southAtlantic =
            new ServerRegion(game, "model.region.southAtlantic", RegionType.OCEAN, atlantic);

        for (ServerRegion region : new ServerRegion[] {
                northPacific, southPacific,
                atlantic, northAtlantic, southAtlantic }) {
            region.setPrediscovered(true);
            map.setRegion(region);
        }
        
        map.setRegion(pacific);
        pacific.setDiscoverable(true);
        pacific.setScoreValue(PACIFIC_SCORE_VALUE);

        
        
        
        
        
        
        
        
        
        
        
        
        
        
        int maxx = map.getWidth();
        int midx = maxx / 2;
        int maxy = map.getHeight();
        int midy = maxy / 2;
        Position pNP = new Position(0,      midy-1);
        Position pSP = new Position(0,      midy+1);
        Position pNA = new Position(maxx-1, midy-1);
        Position pSA = new Position(maxx-1, midy+1);

        Rectangle rNP = new Rectangle(0,0,       midx,midy);
        Rectangle rSP = new Rectangle(0,midy,    midx,maxy);
        Rectangle rNA = new Rectangle(midx,0,    maxx,midy);
        Rectangle rSA = new Rectangle(midx,midy, maxx,maxy);
        fillOcean(map, pNP, northPacific,  rNP);
        fillOcean(map, pSP, southPacific,  rSP);
        fillOcean(map, pNA, northAtlantic, rNA);
        fillOcean(map, pSA, southAtlantic, rSA);

        Rectangle rN = new Rectangle(0,0,    maxx,midy);
        Rectangle rS = new Rectangle(0,midy, maxx,maxy);
        fillOcean(map, pNP, northPacific,  rN);
        fillOcean(map, pSP, southPacific,  rS);
        fillOcean(map, pNA, northAtlantic, rN);
        fillOcean(map, pSA, southAtlantic, rS);

        Rectangle rAll = new Rectangle(0,0, maxx,maxy);
        fillOcean(map, pNP, northPacific,  rAll);
        fillOcean(map, pSP, southPacific,  rAll);
        fillOcean(map, pNA, northAtlantic, rAll);
        fillOcean(map, pSA, southAtlantic, rAll);
    }

    
    private void fillOcean(Map map, Position p, Region region,
                           Rectangle bounds) {
        Queue<Position> q = new LinkedList<Position>();
        boolean[][] visited = new boolean[map.getWidth()][map.getHeight()];
        visited[p.getX()][p.getY()] = true;
        q.add(p);

        while ((p = q.poll()) != null) {
            Tile tile = map.getTile(p);
            tile.setRegion(region);

            for (Direction direction : Direction.values()) {
                Position n = Map.getAdjacent(p, direction);
                if (map.isValid(n)
                    && !visited[n.getX()][n.getY()]
                    && bounds.contains(n.getX(), n.getY())) {
                    visited[n.getX()][n.getY()] = true;
                    Tile next = map.getTile(n);
                    if ((next.getRegion() == null || next.getRegion() == region)
                        && !next.isLand()) {
                        q.add(n);
                    }
                }
            }
        }
    }


    
    private void createLandRegions(Map map) {
        Game game = map.getGame();

        
        ServerRegion arctic =
            new ServerRegion(game, "model.region.arctic", RegionType.LAND);
        ServerRegion antarctic =
            new ServerRegion(game, "model.region.antarctic", RegionType.LAND);

        map.setRegion(arctic);
        arctic.setPrediscovered(true);
        map.setRegion(antarctic);
        antarctic.setPrediscovered(true);

        int arcticHeight = LandGenerator.POLAR_HEIGHT;
        int antarcticHeight = map.getHeight() - LandGenerator.POLAR_HEIGHT - 1;

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < arcticHeight; y++) {
                if (map.isValid(x, y)) {
                    Tile tile = map.getTile(x, y);
                    if (tile.isLand()) {
                        arctic.addTile(tile);
                    }
                }
            }
            for (int y = antarcticHeight; y < map.getHeight(); y++) {
                if (map.isValid(x, y)) {
                    Tile tile = map.getTile(x, y);
                    if (tile.isLand()) {
                        antarctic.addTile(tile);
                    }
                }
            }
        }

        
        
        
        
        int thirdWidth = map.getWidth()/3;
        int twoThirdWidth = 2 * thirdWidth;
        int thirdHeight = map.getHeight()/3;
        int twoThirdHeight = 2 * thirdHeight;

        ServerRegion northWest =
            new ServerRegion(game, "model.region.northWest", RegionType.LAND);
        northWest.setBounds(new Rectangle(0,0,thirdWidth,thirdHeight));
        ServerRegion north =
            new ServerRegion(game, "model.region.north", RegionType.LAND);
        north.setBounds(new Rectangle(thirdWidth,0,thirdWidth,thirdHeight));
        ServerRegion northEast =
            new ServerRegion(game, "model.region.northEast", RegionType.LAND);            
        northEast.setBounds(new Rectangle(twoThirdWidth,0,map.getWidth()-twoThirdWidth,thirdHeight));

        ServerRegion west =
            new ServerRegion(game, "model.region.west", RegionType.LAND);
        west.setBounds(new Rectangle(0,thirdHeight,thirdWidth,thirdHeight));
        ServerRegion center =
            new ServerRegion(game, "model.region.center", RegionType.LAND);
        center.setBounds(new Rectangle(thirdWidth,thirdHeight,thirdWidth,thirdHeight));
        ServerRegion east =
            new ServerRegion(game, "model.region.east", RegionType.LAND);
        east.setBounds(new Rectangle(twoThirdWidth,thirdHeight,map.getWidth()-twoThirdWidth,thirdHeight));

        ServerRegion southWest =
            new ServerRegion(game, "model.region.southWest", RegionType.LAND);
        southWest.setBounds(new Rectangle(0,twoThirdHeight,thirdWidth,map.getHeight()-twoThirdHeight));
        ServerRegion south =
            new ServerRegion(game, "model.region.south", RegionType.LAND);
        south.setBounds(new Rectangle(thirdWidth,twoThirdHeight,thirdWidth,map.getHeight()-twoThirdHeight));
        ServerRegion southEast =
            new ServerRegion(game, "model.region.southEast", RegionType.LAND);
        southEast.setBounds(new Rectangle(twoThirdWidth,twoThirdHeight,map.getWidth()-twoThirdWidth,map.getHeight()-twoThirdHeight));

        for (ServerRegion region : new ServerRegion[] { northWest, north, northEast,
                                                        west, center, east,
                                                        southWest, south, southEast } ) {
            region.setDiscoverable(false);
            map.setRegion(region);
        }

        
        int continents = 0;
        boolean[][] landmap = new boolean[map.getWidth()][map.getHeight()];
        int[][] continentmap = new int[map.getWidth()][map.getHeight()];

        
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                continentmap[x][y] = 0;
                if (map.isValid(x, y)) {
                    Tile tile = map.getTile(x, y);
                    boolean isMountainRange = false;
                    if (tile.getRegion() != null) {
                        isMountainRange = (tile.getRegion().getType() == RegionType.MOUNTAIN);
                    }
                    if (tile.isLand()) {
                        
                        
                        if ((y<arcticHeight) || (y>=antarcticHeight) || isMountainRange) {
                            landmap[x][y] = false;
                        } else {
                            landmap[x][y] = true;
                        }
                    } else {
                        landmap[x][y] = false;
                    }
                }
            }
        }

        
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                if (landmap[x][y]) { 
                    continents++;
                    boolean[][] continent = floodFill(landmap,new Position(x,y));
                    
                    for (int yy = 0; yy < map.getHeight(); yy++) {
                        for (int xx = 0; xx < map.getWidth(); xx++) {
                            if (continent[xx][yy]) {
                                continentmap[xx][yy] = continents;
                                landmap[xx][yy] = false;
                            }
                        }
                    }
                }
            }
        }
        logger.info("Number of individual landmasses is " + continents);

        
        int[] continentsize = new int[continents+1];
        int landsize = 0;
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                continentsize[continentmap[x][y]]++;
                if (continentmap[x][y]>0) {
                    landsize++;
                }
            }
        }

        
        int oldcontinents = continents;
        for (int c = 1; c <= oldcontinents; c++) { 
            if (continentsize[c]>LAND_REGION_MAX_SIZE) {
                boolean[][] splitcontinent = new boolean[map.getWidth()][map.getHeight()];
                Position splitposition = new Position(0,0);
                
                for (int x = 0; x < map.getWidth(); x++) {
                    for (int y = 0; y < map.getHeight(); y++) {
                        if (continentmap[x][y]==c) {
                            splitcontinent[x][y] = true;
                            splitposition = new Position(x,y);
                        } else {
                            splitcontinent[x][y] = false;
                        }
                    }
                }

                while (continentsize[c]>LAND_REGION_MAX_SIZE) {
                    int targetsize = LAND_REGION_MAX_SIZE;
                    if (continentsize[c] < 2*LAND_REGION_MAX_SIZE) {
                        targetsize = continentsize[c]/2;
                    }
                    continents++; 

                    boolean[][] newregion = floodFill(splitcontinent, splitposition, targetsize);
                    for (int x = 0; x < map.getWidth(); x++) {
                        for (int y = 0; y < map.getHeight(); y++) {
                            if (newregion[x][y]) {
                                continentmap[x][y] = continents;
                                splitcontinent[x][y] = false;
                                continentsize[c]--;
                            }
                            if (splitcontinent[x][y]) {
                                splitposition = new Position(x,y);
                            }
                        }
                    }
                }
            }
        }
        logger.info("Number of land regions being created: " + continents);
        
        
        ServerRegion[] landregions = new ServerRegion[continents+1];
        for (int c = 1; c <= continents; c++) { 
            landregions[c] =
                new ServerRegion(map.getGame(), "model.region.land" + c, Region.RegionType.LAND);
            landregions[c].setDiscoverable(true);
            map.setRegion(landregions[c]);
            
        } 
        
        
        continentsize = new int[continents+1];
        landsize = 0;
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                continentsize[continentmap[x][y]]++;
                if (continentmap[x][y]>0) {
                    landsize++;
                    Tile tile = map.getTile(x, y);
                    if (tile.isLand() && (tile.getRegion() == null)) {
                        landregions[continentmap[x][y]].addTile(tile);
                    }
                }
            }
        }
        
        
        for (int c = 1; c <= continents; c++) { 
            int score = Math.max((int)(((float)continentsize[c]/landsize)*LAND_REGIONS_SCORE_VALUE), LAND_REGION_MIN_SCORE);
            landregions[c].setScoreValue(score);
            logger.info("Created land region (size " + continentsize[c] + 
                        ", score value " + score + ").");
        }
    }


    
    private void createHighSeas(Map map) {
        createHighSeas(map,
            getMapGeneratorOptions().getDistLandHighSea(),
            getMapGeneratorOptions().getMaxDistToEdge()
        );
    }


    
    public static void determineHighSeas(Map map,
            int distToLandFromHighSeas,
            int maxDistanceToEdge) {
        
        
        TileType ocean = null, highSeas = null;
        for (TileType t : Specification.getSpecification().getTileTypeList()) {
            if (t.isWater()) {
                if (t.hasAbility("model.ability.moveToEurope")) {
                    if (highSeas == null) {
                        highSeas = t;
                        if (ocean != null) {
                            break;
                        }
                    }
                } else {
                    if (ocean == null) {
                        ocean = t;
                        if (highSeas != null) {
                            break;
                        }
                    }
                }
            }
        }
        if (highSeas == null || ocean == null) {
            throw new RuntimeException("Both Ocean and HighSeas TileTypes must be defined");
        }
        
        
        for (Tile t : map.getAllTiles()) {
            t.setRegion(null);
            if (t.getType() == highSeas) {
                t.setType(ocean);
            }
        }
        
        
        createHighSeas(map, distToLandFromHighSeas, maxDistanceToEdge);
    }


    
    private static void createHighSeas(Map map,
            int distToLandFromHighSeas,
            int maxDistanceToEdge) {
        
        if (distToLandFromHighSeas < 0
                || maxDistanceToEdge < 0) {
            throw new IllegalArgumentException("The integer arguments cannot be negative.");
        }

        TileType highSeas = null;
        for (TileType t : Specification.getSpecification().getTileTypeList()) {
            if (t.isWater()) {
                if (t.hasAbility("model.ability.moveToEurope")) {
                    highSeas = t;
                    break;
                }
            }
        }
        if (highSeas == null) {
            throw new RuntimeException("HighSeas TileType is defined by the 'sail-to-europe' attribute");
        }

        for (int y = 0; y < map.getHeight(); y++) {
            for (int x=0; x<maxDistanceToEdge && 
                          x<map.getWidth() && 
                          !map.isLandWithinDistance(x, y, distToLandFromHighSeas); x++) {
                if (map.isValid(x, y)) {
                    map.getTile(x, y).setType(highSeas);
                }
            }

            for (int x=1; x<=maxDistanceToEdge && 
                          x<=map.getWidth()-1 &&
                          !map.isLandWithinDistance(map.getWidth()-x, y, distToLandFromHighSeas); x++) {
                if (map.isValid(map.getWidth()-x, y)) {
                    map.getTile(map.getWidth()-x, y).setType(highSeas);
                }
            }
        }
    }


    
    private void createMountains(Map map) {
        float randomHillsRatio = 0.5f;
        
        
        int maximumLength = Math.max(getMapGeneratorOptions().getWidth(), getMapGeneratorOptions().getHeight()) / 10;
        int number = (int)(getMapGeneratorOptions().getNumberOfMountainTiles()*(1-randomHillsRatio));
        logger.info("Number of land tiles is " + getMapGeneratorOptions().getLand() +
                    ", number of mountain tiles is " + number);
        logger.fine("Maximum length of mountain ranges is " + maximumLength);
        
        
        TileType hills = Specification.getSpecification().getTileType("model.tile.hills");
        TileType mountains = Specification.getSpecification().getTileType("model.tile.mountains");
        if (hills == null || mountains == null) {
            throw new RuntimeException("Both Hills and Mountains TileTypes must be defined");
        }
        
        
        int counter = 0;
        nextTry: for (int tries = 0; tries < 100; tries++) {
            if (counter < number) {
                Position p = map.getRandomLandPosition();
                if (p == null) {
                    
                    return;
                }
                Tile startTile = map.getTile(p);
                if (startTile.getType() == hills || startTile.getType() == mountains) {
                    
                    continue;
                }

                
                Iterator<Position> it = map.getCircleIterator(p, true, 3);
                while (it.hasNext()) {
                    if (map.getTile(it.next()).getType() == mountains) {
                        continue nextTry;
                    }
                }

                
                
                it = map.getCircleIterator(p, true, 2);
                while (it.hasNext()) {
                    if (!map.getTile(it.next()).isLand()) {
                        continue nextTry;
                    }
                }

                ServerRegion mountainRegion = new ServerRegion(map.getGame(),
                                                               "model.region.mountain" + tries,
                                                               Region.RegionType.MOUNTAIN,
                                                               startTile.getRegion());
                mountainRegion.setDiscoverable(true);
                mountainRegion.setClaimable(true);
                map.setRegion(mountainRegion);
                Direction direction = map.getRandomDirection();
                int length = maximumLength - random.nextInt(maximumLength/2);
                for (int index = 0; index < length; index++) {
                    p = Map.getAdjacent(p, direction);
                    Tile nextTile = map.getTile(p);
                    if (nextTile == null || !nextTile.isLand()) 
                        continue;
                    nextTile.setType(mountains);
                    mountainRegion.addTile(nextTile);
                    counter++;
                    it = map.getCircleIterator(p, false, 1);
                    while (it.hasNext()) {
                        Tile neighborTile = map.getTile(it.next());
                        if (neighborTile==null || !neighborTile.isLand() || neighborTile.getType()==mountains)
                            continue;
                        int r = random.nextInt(8);
                        if (r == 0) {
                            neighborTile.setType(mountains);
                            mountainRegion.addTile(neighborTile);
                            counter++;
                        } else if (r > 2) {
                            neighborTile.setType(hills);
                            mountainRegion.addTile(neighborTile);
                        }
                    }
                }
                int scoreValue = 2 * mountainRegion.getSize();
                mountainRegion.setScoreValue(scoreValue);
                logger.info("Created mountain region (direction " + direction +
                            ", length " + length + ", size " + mountainRegion.getSize() +
                            ", score value " + scoreValue + ").");
            }
        }
        logger.info("Added " + counter + " mountain range tiles.");
        
        
        number = (int)(getMapGeneratorOptions().getNumberOfMountainTiles()*randomHillsRatio);
        counter = 0;
        nextTry: for (int tries = 0; tries < 1000; tries++) {
            if (counter < number) {
                Position p = map.getRandomLandPosition();
                Tile t = map.getTile(p);
                if (t.getType() == hills || t.getType() == mountains) {
                    
                    continue;
                }
                
                
                Iterator<Position> it = map.getCircleIterator(p, true, 3);
                while (it.hasNext()) {
                    if (map.getTile(it.next()).getType() == mountains) {
                        continue nextTry;
                    }
                }

                
                
                it = map.getCircleIterator(p, true, 1);
                while (it.hasNext()) {
                    if (!map.getTile(it.next()).isLand()) {
                        continue nextTry;
                    }
                }

                int k = random.nextInt(4);
                if (k == 0) {
                    
                    t.setType(mountains);
                } else {
                    
                    t.setType(hills);
                }
                counter++;
            }
        }
        logger.info("Added " + counter + " random hills tiles.");
    }


    
    private void createRivers(Map map) {
        int number = getMapGeneratorOptions().getNumberOfRivers();
        int counter = 0;
        HashMap<Position, River> riverMap = new HashMap<Position, River>();
        List<River> rivers = new ArrayList<River>();

        for (int i = 0; i < number; i++) {
            nextTry: for (int tries = 0; tries < 100; tries++) {
                Position position = map.getRandomLandPosition();
                if (!map.getTile(position).getType().canHaveImprovement(riverType)) {
                    continue;
                }
                
                Iterator<Position> it = map.getCircleIterator(position, true, 2);
                while (it.hasNext()) {
                    Tile neighborTile = map.getTile(it.next());
                    if (!neighborTile.isLand()) {
                        continue nextTry;
                    }
                }
                if (riverMap.get(position) == null) {
                    
                    ServerRegion riverRegion = new ServerRegion(map.getGame(),
                                                                "model.region.river" + i,
                                                                Region.RegionType.RIVER,
                                                                map.getTile(position).getRegion());
                    riverRegion.setDiscoverable(true);
                    riverRegion.setClaimable(true);
                    River river = new River(map, riverMap, riverRegion);
                    if (river.flowFromSource(position)) {
                        logger.fine("Created new river with length " + river.getLength());
                        map.setRegion(riverRegion);
                        rivers.add(river);
                        counter++;
                        break;
                    } else {
                        logger.fine("Failed to generate river.");
                    }
                }
            }
        }

        logger.info("Created " + counter + " rivers of maximum " + number + ".");

        for (River river : rivers) {
            ServerRegion region = river.getRegion();
            int scoreValue = 0;
            for (RiverSection section : river.getSections()) {
                scoreValue += section.getSize();
            }
            scoreValue *= 2;
            region.setScoreValue(scoreValue);
            logger.info("Created river region (length " + river.getLength() +
                        ", score value " + scoreValue + ").");
        }
    }


    private void findLakes(Map map) {
        Game game = map.getGame();

        
        
        ServerRegion inlandlakes =
            new ServerRegion(game, "model.region.inlandlakes", RegionType.LAKE);
        map.setRegion(inlandlakes);
        inlandlakes.setPrediscovered(true);

        Position p = null;

        
        
        
        for (int x : new int[] {0, map.getWidth() - 1}) {
            for (int y = 0; y < map.getHeight(); y++) {
                Tile tile = map.getTile(x, y);
                if (tile != null && tile.getType() != null && !tile.isLand()) {
                    p = new Position(x, y);
                    break;
                }
            }
        }

        if (p == null) {
            
            logger.warning("Find lakes: unable to find entry point.");
            return;        
        }

        boolean[][] watermap = new boolean[map.getWidth()][map.getHeight()];
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                watermap[x][y] = !map.getTile(x,y).isLand();
            }
        }
        
        boolean[][] visited = floodFill(watermap,p);

        
        for (int y=0; y < map.getHeight(); y++) {
            for (int x=0; x < map.getWidth(); x++) {
                if (watermap[x][y] && !visited[x][y]) {
                    Tile tile = map.getTile(x, y);
                    if (tile != null) {
                        tile.setType(lake);
                        inlandlakes.addTile(tile);
                    }
                }
            }
        }
    }
    
    private boolean isNorth(int height, int y) {
        return y<(height/2);
    }

    private boolean isWest(int width, int x) {
        return x<(width/2);
    }
    
    
    private boolean[][] floodFill(boolean[][] boolmap, Position p, int limit) {
        
        
        Queue<Position>q = new LinkedList<Position>();

        boolean[][] visited = new boolean[boolmap.length][boolmap[0].length];
        visited[p.getX()][p.getY()] = true;
        limit--;
        do {
            for (Direction direction : Direction.values()) {
                Position n = Map.getAdjacent(p, direction);
                if (Map.isValid(n,boolmap.length,boolmap[0].length) && boolmap[n.getX()][n.getY()] && !visited[n.getX()][n.getY()] && limit > 0) {
                    visited[n.getX()][n.getY()] = true;
                    limit--;
                    q.add(n);
                }
            }

            p = q.poll();
        } while ((p != null) && (limit > 0));
        return visited;
    }
    
    private boolean[][] floodFill(boolean[][] boolmap, Position p) {
        return floodFill (boolmap, p, java.lang.Integer.MAX_VALUE);
    }

}
