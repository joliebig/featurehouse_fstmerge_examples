

package net.sf.freecol.common.model;

import java.util.Set;
import java.util.Random;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.freecol.common.Specification;

public final class ResourceType extends FreeColGameObjectType
{

    private int minValue;
    private int maxValue;

    

    public ResourceType(int index) {
        setIndex(index);
    }

    

    
    public int getRandomValue() {
        if (minValue == maxValue)
            return maxValue;

        Random rand = new Random();
        return (minValue + rand.nextInt(maxValue-minValue+1));
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

    
    public String getOutputString() {
        
        return getName();
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
