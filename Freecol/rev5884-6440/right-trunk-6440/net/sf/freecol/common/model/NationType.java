


package net.sf.freecol.common.model;

import net.sf.freecol.common.model.Settlement.SettlementType;


public abstract class NationType extends FreeColGameObjectType {

    private static int nextIndex = 0;

    
    private SettlementType typeOfSettlement;

    
    private int settlementRadius = 1;

    
    private int capitalRadius = 2;

    
    public NationType() {
        setIndex(nextIndex++);
    }

    
    public final SettlementType getTypeOfSettlement() {
        return typeOfSettlement;
    }

    
    public final void setTypeOfSettlement(final SettlementType newTypeOfSettlement) {
        this.typeOfSettlement = newTypeOfSettlement;
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
