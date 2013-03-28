

package tree;

import genj.gedcom.Fam;
import genj.gedcom.Indi;


public class IndiBox {

    
    public static enum Direction
    {
        NONE, SPOUSE, PARENT, CHILD, NEXTMARRIAGE
    };

    
    public IndiBox prev = null;

    
    public IndiBox spouse = null;

    
    public IndiBox parent = null;

    
    public IndiBox[] children = null;

    
    public IndiBox nextMarriage = null;

    
    public int x = 0;

    
    public int y = 0;

    
    public int width = 10;

    
    public int height = 10;

    
    public int wPlus = 0;

    public int wMinus = 0;

    public int hPlus = 0;

    public int hMinus = 0;

    
    public Indi individual;

    
    public FamBox family;

    
    public IndiBox(Indi individual) {
        this.individual = individual;
    }

    
    public IndiBox(Indi individual, IndiBox prev) {
        this.individual = individual;
        this.prev = prev;
    }

    
    public Direction getDir() {
        if (prev == null)
            return Direction.NONE;
        if (this == prev.spouse)
            return Direction.SPOUSE;
        if (this == prev.parent)
            return Direction.PARENT;
        if (this == prev.nextMarriage)
            return Direction.NEXTMARRIAGE;
        return Direction.CHILD;
    }

    
    public boolean hasChildren() {
        return (children != null && children.length > 0);
    }

    public Fam getFamily() {
        if (family == null)
            return null;
        return family.family;
    }
}