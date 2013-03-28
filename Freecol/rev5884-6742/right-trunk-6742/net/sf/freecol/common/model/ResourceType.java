

package net.sf.freecol.common.model;

import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


public final class ResourceType extends FreeColGameObjectType {

    private static int nextIndex = 0;

    private int minValue;
    private int maxValue;

    

    public ResourceType() {
        setIndex(nextIndex++);
    }

    

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public Set<Modifier> getProductionModifier(GoodsType goodsType, UnitType unitType) {
        return featureContainer.getModifierSet(goodsType.getId(), unitType);
    }

    public GoodsType getBestGoodsType() {
        GoodsType bestType = null;
        float bestValue = 0f;
        for (Modifier modifier : featureContainer.getModifiers()) {
            GoodsType goodsType = Specification.getSpecification().getGoodsType(modifier.getId());
            float value = goodsType.getInitialSellPrice() * modifier.applyTo(100);
            if (bestType == null || value > bestValue) {
                bestType = goodsType;
                bestValue = value;
            }
        }
        return bestType;
    }

    

    public void readAttributes(XMLStreamReader in, Specification specification)
            throws XMLStreamException {
        if (hasAttribute(in, "maximum-value")) {
            maxValue = Integer.parseInt(in.getAttributeValue(null, "maximum-value"));
            minValue = getAttribute(in, "minimum-value", 0);
        } else {
            maxValue = -1;
            minValue = -1;
        }
    }

}
