


package net.sf.freecol.common.model;


public abstract class NationType extends FreeColGameObjectType {

    
    private int settlementRadius = 1;

    
    private int capitalRadius = 2;

    
    public NationType(int index) {
        setIndex(index);
    }

    
    public abstract boolean isEuropean();

    
    public abstract boolean isREF();

    
    public final int getSettlementRadius() {
        return settlementRadius;
    }

    
    public final void setSettlementRadius(final int newSettlementRadius) {
        this.settlementRadius = newSettlementRadius;
    }

    
    public final int getCapitalRadius() {
        return capitalRadius;
    }

    
    public final void setCapitalRadius(final int newCapitalRadius) {
        this.capitalRadius = newCapitalRadius;
    }

    public String toString() {
        return getName();
    }

}
