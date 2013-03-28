


package net.sf.freecol.common.model;


public abstract class NationType extends FreeColGameObjectType {

    
    public NationType(int index) {
        setIndex(index);
    }

    
    public abstract boolean isEuropean();

    
    public abstract boolean isREF();

    public String toString() {
        return getName();
    }

}
