

package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


public final class GoodsType extends FreeColGameObjectType {

    public static final int NO_BREEDING = Integer.MAX_VALUE;
    public static final int NO_PRICE = Integer.MAX_VALUE;

    private static int nextIndex = 0;

    private boolean isFarmed;
    private boolean isFood;
    private boolean ignoreLimit;
    private boolean newWorldGoods;

    
    private boolean buildingMaterial;

    
    private boolean militaryGoods;

    
    private boolean tradeGoods;

    
    private boolean storable;

    private GoodsType madeFrom;
    private GoodsType makes;
    private GoodsType storedAs;
    
    private int initialAmount;
    private int initialPrice;
    private int priceDiff;

    
    private int breedingNumber = NO_BREEDING;

    
    private int price = NO_PRICE;


    

    public GoodsType() {
        setIndex(nextIndex++);
    }

    

    public StringTemplate getLabel(boolean sellable) {
        if (sellable) {
            return StringTemplate.key(getNameKey());
        } else {
            return StringTemplate.template("model.goods.goodsBoycotted")
                .add("%goods%", getNameKey());
        }
    }

    public boolean isRawMaterial() {
        return makes != null;
    }

    public boolean isRefined() {
        return madeFrom != null;
    }

    public GoodsType getRawMaterial() {
        return madeFrom;
    }

    public GoodsType getProducedMaterial() {
        return makes;
    }

    public boolean isFarmed() {
        return isFarmed;
    }

    public boolean limitIgnored() {
        return ignoreLimit;
    }

    public boolean isNewWorldGoodsType() {
        return newWorldGoods;
    }

    public boolean isNewWorldLuxuryType() {
        return (madeFrom != null && madeFrom.isNewWorldGoodsType());
    }

    
    public boolean isImmigrationType() {
        return !getModifierSet("model.modifier.immigration").isEmpty();
    }

    
    public boolean isLibertyType() {
        return !getModifierSet("model.modifier.liberty").isEmpty();
    }

    public boolean isStorable() {
        return storable;
    }

    public boolean isStoredAs() {
        return storedAs!=null;
    }

    public GoodsType getStoredAs() {
        if (storedAs==null) {
            return this;
        } else {
            return storedAs;
        }
    }

    public int getInitialAmount() {
        return initialAmount;
    }

    public int getInitialSellPrice() {
        return initialPrice;
    }

    public int getInitialBuyPrice() {
        return initialPrice + priceDiff;
    }

    public int getPriceDifference() {
        return priceDiff;
    }

    
    
    public boolean isFoodType() {
        return isFood;
    }
    
    public GoodsType outputType() {
        return makes;
    }

    public GoodsType inputType() {
        return madeFrom;
    }

    
    public boolean isBuildingMaterial() {
        return buildingMaterial;
    }

    
    public void setBuildingMaterial(final boolean newBuildingMaterial) {
        this.buildingMaterial = newBuildingMaterial;
    }

    
    public List<GoodsType> getProductionChain() {
        List<GoodsType> result = new ArrayList<GoodsType>();
        GoodsType currentGoods = this;
        while (currentGoods != null) {
            result.add(0, currentGoods);
            currentGoods = currentGoods.madeFrom;
        }
        return result;
    }

    
    public boolean isRawBuildingMaterial() {
        if (this.madeFrom!=null) {
            return false;
        }
        GoodsType refinedType = makes;
        while (refinedType != null) {
            if (refinedType.isBuildingMaterial()) {
                return true;
            } else {
                refinedType = refinedType.makes;
            }
        }
        return false;
    }

    
    public boolean isMilitaryGoods() {
        return militaryGoods;
    }

    
    public void setMilitaryGoods(final boolean newMilitaryGoods) {
        this.militaryGoods = newMilitaryGoods;
    }

    
    public boolean isTradeGoods() {
        return tradeGoods;
    }

    
    public void setTradeGoods(final boolean newTradeGoods) {
        this.tradeGoods = newTradeGoods;
    }


    
    public boolean isLibertyGoodsType() {
        return getFeatureContainer().containsModifierKey("model.modifier.liberty");
    }

    
    public boolean isImmigrationGoodsType() {
        return getFeatureContainer().containsModifierKey("model.modifier.liberty");
    }

    
    public int getBreedingNumber() {
        return breedingNumber;
    }

    
    public void setBreedingNumber(final int newBreedingNumber) {
        this.breedingNumber = newBreedingNumber;
    }

    
    public boolean isBreedable() {
        return breedingNumber != NO_BREEDING;
    }

    
    public int getPrice() {
        return price;
    }

    
    public void setPrice(final int newPrice) {
        this.price = newPrice;
    }

    

    public void readAttributes(XMLStreamReader in, Specification specification) 
        throws XMLStreamException {
        isFarmed = getAttribute(in, "is-farmed", false);
        isFood = getAttribute(in, "is-food", false);
        ignoreLimit = getAttribute(in, "ignore-limit", false);
        newWorldGoods = getAttribute(in, "new-world-goods", false);
        breedingNumber = getAttribute(in, "breeding-number", NO_BREEDING);
        price = getAttribute(in, "price", NO_PRICE);

        madeFrom = specification.getType(in, "made-from", GoodsType.class, null);
        if (madeFrom != null) {
            madeFrom.makes = this;
        }

        storable = getAttribute(in, "storable", true);
        storedAs = specification.getType(in, "stored-as", GoodsType.class, null);
    }

    public void readChildren(XMLStreamReader in, Specification specification) 
        throws XMLStreamException {

        
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if ("market".equals(in.getLocalName())) {
                initialAmount = Integer.parseInt(in.getAttributeValue(null, "initial-amount"));
                initialPrice = getAttribute(in, "initial-price", 1);
                priceDiff = getAttribute(in, "price-difference", 1);
                in.nextTag(); 
            } else {
                super.readChild(in, specification);
            }
        }
    }

}
