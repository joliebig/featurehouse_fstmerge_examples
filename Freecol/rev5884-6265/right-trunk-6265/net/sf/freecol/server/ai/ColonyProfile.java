

package net.sf.freecol.server.ai;

import java.util.ArrayList;
import java.util.List;

import net.sf.freecol.common.Specification;
import net.sf.freecol.common.model.GoodsType;

public class ColonyProfile {

    
    private ProfileType type;

    
    private List<GoodsType> preferredProduction;

    public static enum ProfileType {
        OUTPOST, SMALL, MEDIUM, LARGE, CAPITAL
    };


    public ColonyProfile() {
        this(ProfileType.MEDIUM, null);
    }

    public ColonyProfile(ProfileType type, List<GoodsType> production) {
        this.type = type;
        if (production == null) {
            preferredProduction = new ArrayList<GoodsType>();
            for (GoodsType goodsType : Specification.getSpecification().getGoodsTypeList()) {
                if (goodsType.isFoodType() || goodsType.isLibertyType()) {
                    preferredProduction.add(goodsType);
                }
            }
        } else {
            preferredProduction = production;
        }
    }

    
    public final ProfileType getType() {
        return type;
    }

    
    public final void setType(final ProfileType newType) {
        this.type = newType;
    }

    
    public final List<GoodsType> getPreferredProduction() {
        return preferredProduction;
    }

    
    public final void setPreferredProduction(final List<GoodsType> newPreferredProduction) {
        this.preferredProduction = newPreferredProduction;
    }

}