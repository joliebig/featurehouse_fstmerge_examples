

package net.sf.freecol.server.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Map.Position;


public class LandGenerator {
    private final Random random = new Random();
    
    public final static int POLAR_HEIGHT = 2;

    private final MapGeneratorOptions mapGeneratorOptions;
     
    private boolean[][] map;
     
    private int width;
    private int height;

    private int preferredDistanceToEdge;
    private int numberOfLandTiles;
    private int minLandMass;
    private int minimumNumberOfTiles;

    private int genType;

    
    public LandGenerator(MapGeneratorOptions mapGeneratorOptions) {
        this.mapGeneratorOptions = mapGeneratorOptions;
    }



    
    public static boolean[][] importLandMap(Game game) {
        boolean[][] map = new boolean[game.getMap().getWidth()][game.getMap().getHeight()];
        for (int i=0; i<map.length; i++) {
            for (int j=0; j<map[0].length; j++) {
                map[i][j] = game.getMap().getTile(i, j).isLand();
            }
        }
        return map;
    }



    
    public boolean[][] createLandMap() {
        
        width = mapGeneratorOptions.getWidth();
        height = mapGeneratorOptions.getHeight();         
        preferredDistanceToEdge = mapGeneratorOptions.getPrefDistToEdge();
        minLandMass = mapGeneratorOptions.getLandMass();
        minimumNumberOfTiles = mapGeneratorOptions.getLand();
        genType = mapGeneratorOptions.getLandGeneratorType();

        
        map = new boolean[width][height];
        numberOfLandTiles = 0;

        
        
        
        switch (genType) {
            case MapGeneratorOptions.LAND_GEN_CLASSIC:
                createClassicLandMap();
                break;
            case MapGeneratorOptions.LAND_GEN_CONTINENT:
                addPolarRegions();
                
                int contsize = (minimumNumberOfTiles*75)/100;
                addLandmass(contsize,contsize, width/2, random.nextInt(height/2)+height/4);
                
                while (numberOfLandTiles < minimumNumberOfTiles) {
                    addLandmass(15,25);
                }
                cleanMap();
                break;
            case MapGeneratorOptions.LAND_GEN_ARCHIPELAGO:
                addPolarRegions();
                
                int archsize = (minimumNumberOfTiles*10)/100;
                for (int i=0;i<5;i++) {
                    addLandmass(archsize-10,archsize);
                }
                
            case MapGeneratorOptions.LAND_GEN_ISLANDS:
                addPolarRegions();
                
                while (numberOfLandTiles < minimumNumberOfTiles) {
                    int s=random.nextInt(50) + 25;
                    addLandmass(20,s);
                }
                cleanMap();
                break;
        }

        return map;
    }


    private void createClassicLandMap() {    
        int x;
        int y;

        while (numberOfLandTiles < minimumNumberOfTiles) {
            int failCounter=0;
            do {
                x=(random.nextInt(width-preferredDistanceToEdge*4)) + preferredDistanceToEdge*2;
                y=(random.nextInt(height-preferredDistanceToEdge*4)) + preferredDistanceToEdge*2;
                failCounter++;
                
                
                
                if (failCounter>100) {
                    failCounter=0;
                    minimumNumberOfTiles--;
                    break;
                }
            } while (map[x][y]);

            setLand(x,y);
        }

        addPolarRegions();
        cleanMap();
    }



    
    private void addLandmass(int minsize, int maxsize, int x, int y) {
        int size = 0;
        boolean[][] newland = new boolean[width][height];

        List<Position>l = new ArrayList<Position>();
        Position p;

        
        if (x<0 || y<0) {
            do {
                x=(random.nextInt(width-preferredDistanceToEdge*2)) + preferredDistanceToEdge;
                y=(random.nextInt(height-preferredDistanceToEdge*2)) + preferredDistanceToEdge;
            } while (map[x][y] || !isSingleTile(x,y));
        }

        newland[x][y] = true;
        size++;

        
        p = new Position(x, y);  
        for (Direction direction : Direction.longSides) {
            Position n = Map.getAdjacent(p, direction);
            if (Map.isValid(n, width, height) && isSingleTile(n.getX(),n.getY()) && n.getX()>preferredDistanceToEdge && n.getX()<width-preferredDistanceToEdge) {
                l.add(n);
            }
        }

        
        
        
        while (size < maxsize && l.size()>0) {
            int i=random.nextInt(l.size());
            p = l.remove(i);
            
            if (!newland[p.getX()][p.getY()]) {
                newland[p.getX()][p.getY()] = true;
                size++;
                
                
                for (Direction direction : Direction.longSides) {
                    Position n = Map.getAdjacent(p, direction);
                    if (Map.isValid(n, width, height) && isSingleTile(n.getX(),n.getY()) && n.getX()>preferredDistanceToEdge && n.getX()<width-preferredDistanceToEdge) {
                        l.add(n);
                    }
                }
            }
        }
        
        
        for (x=0; x<width; x++) {
            for (y=0; y<height; y++) {
                if (newland[x][y] == true) {
                    map[x][y] = true;
                    numberOfLandTiles++;
                }
            }
        }
    }

    private void addLandmass(int minsize, int maxsize) {
        addLandmass(minsize, maxsize, -1, -1);
    }

    
    private void addPolarRegions() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < POLAR_HEIGHT; y++) {
                if (map[x][y] == false) {
                    map[x][y] = true;
                    numberOfLandTiles++;
                }
            }
            int limit = height - 1 - POLAR_HEIGHT;
            for (int y = limit; y < height; y++) {
                if (map[x][y] == false) {
                    map[x][y] = true;
                    numberOfLandTiles++;
                }
            }
        }
    }



    
    private void cleanMap() {
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                if (isSingleTile(x, y)) {
                    map[x][y] = false;
                }
            }
        }
    }



    
    private boolean isSingleTile(int x, int y) {
        Position p = new Position(x, y);

        for (Direction direction : Direction.values()) {
            Position n = Map.getAdjacent(p, direction);
            if (Map.isValid(n, width, height) && map[n.getX()][n.getY()]) {
                return false;
            }
        }

        return true;
    }



                       
    private void setLand(int x, int y) {
        if (map[x][y]) {
            return;
        }

        map[x][y] = true;
        numberOfLandTiles++;

        Position p = new Position(x, y);

        for (Direction direction : Direction.longSides) {
            Position n = Map.getAdjacent(p, direction);
            if (Map.isValid(n, width, height)) {
                growLand(n.getX(), n.getY());
            }
        }
    }



                       
    private void growLand(int i, int j) {
        if (map[i][j]) {
            return;
        }

        
        
        
        
        
        
        int r = random.nextInt(8)
                + Math.max(-1,
                          (1+Math.max(preferredDistanceToEdge-Math.min(i,width-i),
                                    2*preferredDistanceToEdge-Math.min(j, height-j))));

        int sum = 0;
        Position p = new Position(i, j);

        for (Direction direction : Direction.values()) {
            Position n = Map.getAdjacent(p, direction);
            if (Map.isValid(n, width, height) && map[n.getX()][n.getY()]) {
                sum++;
            }
        }

        if (sum > r) {
            setLand(i,j);
        }
    }
    
    
    
}
