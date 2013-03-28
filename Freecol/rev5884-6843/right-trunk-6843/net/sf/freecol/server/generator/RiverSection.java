

package net.sf.freecol.server.generator;



import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.TileImprovement;



public class RiverSection {



    
    private static int[] base = {1, 3, 9, 27};
    
    
    private int branch[] = {TileImprovement.NO_RIVER, 
                            TileImprovement.NO_RIVER, 
                            TileImprovement.NO_RIVER, 
                            TileImprovement.NO_RIVER};
    
    
    private int size = TileImprovement.SMALL_RIVER;

    
    public Direction direction;
    
    
    private Map.Position position;

    
    public RiverSection(int style) {
        decodeStyle(style);
    }
    
    
    public RiverSection(Map.Position position, Direction direction) {
        this.position = position;
        this.direction = direction;
        setBranch(direction, TileImprovement.SMALL_RIVER);
    }
    
    
    public Map.Position getPosition() {
        return position;
    }
    
    
    public int getSize() {
        return size;
    }
    
    
    public void decodeStyle(int style) {
        int tempStyle = style;
        for (int i = base.length - 1; i >= 0; i--) {
            if (base[i] == 0) {
                continue;                       
            }
            if (tempStyle>0) {
                branch[i] = tempStyle / base[i];    
                tempStyle -= branch[i] * base[i];   
            }
        }
    }

    
    public int encodeStyle() {
        int style = 0;
        for (int i = 0; i < base.length; i++) {
            style += base[i] * branch[i];
        }
        return style;
    }

    
    public void setBranch(Direction direction, int size) {
        if (size != TileImprovement.SMALL_RIVER) {
            size = TileImprovement.LARGE_RIVER;
        }
        for (int i=0; i<Direction.longSides.length; i++) {
            if (base[i] == 0) {
                continue;                       
            }
            if (Direction.longSides[i]==direction) {
                branch[i] = size;
            }
        }
    }
    
    
    public int getBranch(Direction direction) {
        for (int i=0; i<Direction.longSides.length; i++) {
            if (base[i] == 0) {
                continue;                       
            }
            if (Direction.longSides[i]==direction) {
                return branch[i];
            }
        }
        return TileImprovement.NO_RIVER;
    }

    
    public void removeBranch(Direction direction) {
        setBranch(direction, TileImprovement.NO_RIVER);
    }
    
    
    public void growBranch(Direction direction, int increment) {
        for (int i=0; i<Direction.longSides.length; i++) {
            if (base[i] == 0) {
                continue;                       
            }
            if (Direction.longSides[i]==direction) {
                branch[i]+=increment;
                if (branch[i]>TileImprovement.LARGE_RIVER)
                    branch[i] = TileImprovement.LARGE_RIVER;
                else if (branch[i]<TileImprovement.NO_RIVER)
                    branch[i] = TileImprovement.NO_RIVER;
            }
        }
    }
    
    
    public void grow() {
        this.size++;
        setBranch(direction, TileImprovement.LARGE_RIVER);
    }
}
