


package net.sf.freecol.common.model;

import net.sf.freecol.common.model.Settlement.SettlementType;


public abstract class NationType extends FreeColGameObjectType {

    
    private SettlementType typeOfSettlement;

    
    public NationType(int index) {
        setIndex(index);
    }

    
    public final SettlementType getTypeOfSettlement() {
        return typeOfSettlement;
    }

    
    public final void setTypeOfSettlement(final SettlementType newTypeOfSettlement) {
        this.typeOfSettlement = newTypeOfSettlement;
    }

    
    public abstract boolean isEuropean();

    
    public abstract boolean isREF();

    public String toString() {
        return getName();
    }

}
