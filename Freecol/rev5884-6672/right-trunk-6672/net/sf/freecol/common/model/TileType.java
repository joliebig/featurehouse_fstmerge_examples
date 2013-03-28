

package net.sf.freecol.common.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.freecol.common.util.RandomChoice;

public final class TileType extends FreeColGameObjectType {

    private static int nextIndex = 0;

    private String artBasic;
    private String artOverlay;
    private String artForest;
    private String artCoast;
    private Color minimapColor;

    private boolean forest;
    private boolean water;
    private boolean canSettle;

    private int basicMoveCost;
    private int basicWorkTurns;
    
    public static enum RangeType { HUMIDITY, TEMPERATURE, ALTITUDE }
    
    private int[] humidity = new int[2];
    private int[] temperature = new int[2];
    private int[] altitude = new int[2];

    private List<RandomChoice<ResourceType>> resourceType;


    
    private boolean connected;

    
    private AbstractGoods primaryGoods = null;

    
    private AbstractGoods secondaryGoods = null;

    
    private List<AbstractGoods> production;

    private Map<String, AbstractGoods> primaryGoodsMap =
        new HashMap<String, AbstractGoods>();

    private Map<String, AbstractGoods> secondaryGoodsMap =
        new HashMap<String, AbstractGoods>();

    private Map<String, Map<GoodsType, AbstractGoods>> productionMap =
        new HashMap<String, Map<GoodsType, AbstractGoods>>();


    

    public TileType() {
        setIndex(nextIndex++);
    }

    

    public String getArtBasic() {
        return artBasic;
    }

    public String getArtOverlay() {
        return artOverlay;
    }

    public String getArtForest() {
        return artForest;
    }

    public String getArtCoast() {
        return artCoast;
    }

    public Color getMinimapColor() {
        return minimapColor;
    }

    public boolean isForested() {
        return forest;
    }

    public boolean isWater() {
        return water;
    }

    
    public boolean isConnected() {
        return connected;
    }

    public boolean canSettle() {
        return canSettle;
    }

    
    public boolean canHaveImprovement(TileImprovementType improvement) {
        return (improvement != null && improvement.isTileTypeAllowed(this));
    }

    public int getBasicMoveCost() {
        return basicMoveCost;
    }

    public int getBasicWorkTurns() {
        return basicWorkTurns;
    }

    public Set<Modifier> getDefenceBonus() {
        return getModifierSet("model.modifier.defence");
    }

    
    public int getProductionOf(GoodsType goodsType, UnitType unitType) {
        return (int) featureContainer.applyModifier(0f, goodsType.getId(), unitType);
        
    }

    
    public Set<Modifier> getProductionBonus(GoodsType goodsType) {
        return featureContainer.getModifierSet(goodsType.getId());
    }

    
    public AbstractGoods getPrimaryGoods() {
        return primaryGoods;
    }

    
    public AbstractGoods getPrimaryGoods(String difficulty) {
        AbstractGoods result = primaryGoodsMap.get(difficulty);
        if (result == null) {
            result = primaryGoodsMap.get(null);
        }
        return result;
    }

    
    public boolean isPrimaryGoodsType(GoodsType type) {
        return (primaryGoods != null && primaryGoods.getType() == type);
    }

    
    public void setPrimaryGoods(final AbstractGoods newPrimaryGoods) {
        this.primaryGoods = newPrimaryGoods;
    }

    
    public AbstractGoods getSecondaryGoods() {
        return secondaryGoods;
    }

    
    public AbstractGoods getSecondaryGoods(String difficulty) {
        AbstractGoods result = secondaryGoodsMap.get(difficulty);
        if (result == null) {
            result = secondaryGoodsMap.get(null);
        }
        return result;
    }

    
    public void setSecondaryGoods(final AbstractGoods newSecondaryGoods) {
        this.secondaryGoods = newSecondaryGoods;
    }

    
    public boolean isSecondaryGoodsType(GoodsType type) {
        return (secondaryGoods != null && secondaryGoods.getType() == type);
    }

    
    public List<AbstractGoods> getProduction() {
        return production;
    }

    
    public List<AbstractGoods> getProduction(String difficulty) {
        Map<GoodsType, AbstractGoods> result = new HashMap<GoodsType, AbstractGoods>();
        Map<GoodsType, AbstractGoods> defaultMap = productionMap.get(null);
        Map<GoodsType, AbstractGoods> difficultyMap = productionMap.get(difficulty);
        if (defaultMap != null) {
            result.putAll(defaultMap);
        }
        if (difficultyMap != null) {
            result.putAll(difficultyMap);
        }
        return new ArrayList<AbstractGoods>(result.values());
    }

    public List<RandomChoice<ResourceType>> getWeightedResources() {
        return resourceType;
    }

    public List<ResourceType> getResourceTypeList() {
        List<ResourceType> result = new ArrayList<ResourceType>();
        for (RandomChoice<ResourceType> resource : resourceType) {
            result.add(resource.getObject());
        }
        return result;
    }

    
    public boolean canHaveResourceType(ResourceType resourceType) {
        return getResourceTypeList().contains(resourceType);
    }

    public boolean withinRange(RangeType rangeType, int value) {
        switch (rangeType) {
        case HUMIDITY:
            return (humidity[0] <= value && value <= humidity[1]);
        case TEMPERATURE:
            return (temperature[0] <= value && value <= temperature[1]);
        case ALTITUDE:
            return (altitude[0] <= value && value <= altitude[1]);
        default:
            return false;
        }
    }

    
    public void applyDifficultyLevel(String difficulty) {
        primaryGoods = getPrimaryGoods(difficulty);
        secondaryGoods = getSecondaryGoods(difficulty);
        
        if (production != null) {
            for (AbstractGoods goods : production) {
                Modifier oldModifier = new Modifier(goods.getType().getId(), this, goods.getAmount(),
                                                    Modifier.Type.ADDITIVE);
                getFeatureContainer().removeModifier(oldModifier);
            }
        }
        production = getProduction(difficulty);
        
        for (AbstractGoods goods : production) {
            addModifier(new Modifier(goods.getType().getId(), this, goods.getAmount(),
                                     Modifier.Type.ADDITIVE));
        }
    }


    

    public void readAttributes(XMLStreamReader in, Specification specification)
        throws XMLStreamException {
        basicMoveCost = Integer.parseInt(in.getAttributeValue(null, "basic-move-cost"));
        basicWorkTurns = Integer.parseInt(in.getAttributeValue(null, "basic-work-turns"));
        forest = getAttribute(in, "is-forest", false);
        water = getAttribute(in, "is-water", false);
        canSettle = getAttribute(in, "can-settle", !water);
        connected = getAttribute(in, "is-connected", false);
    }
        
    public void readChildren(XMLStreamReader in, Specification specification)
        throws XMLStreamException {

        artBasic = null;
        production = new ArrayList<AbstractGoods>();
        resourceType = new ArrayList<RandomChoice<ResourceType>>();

        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            String childName = in.getLocalName();
            if ("art".equals(childName)) {
                artBasic = in.getAttributeValue(null, "basic");
                artOverlay = in.getAttributeValue(null, "overlay");
                artForest = in.getAttributeValue(null, "forest");
                artCoast = getAttribute(in, "coast", (water ? null : "terrain/beach/"));
                minimapColor = new Color(Integer.decode(in.getAttributeValue(null, "minimap-color")));
                in.nextTag(); 
            } else if ("gen".equals(childName)) {
                humidity[0] = getAttribute(in, "humidityMin", 0);
                humidity[1] = getAttribute(in, "humidityMax", 100);
                temperature[0] = getAttribute(in, "temperatureMin", -20);
                temperature[1] = getAttribute(in, "temperatureMax", 40);
                altitude[0] = getAttribute(in, "altitudeMin", 0);
                altitude[1] = getAttribute(in, "altitudeMax", 0);
                in.nextTag(); 
            } else if ("production".equals(childName)
                       || "primary-production".equals(childName)
                       || "secondary-production".equals(childName)) {
                GoodsType type = specification.getGoodsType(in.getAttributeValue(null, "goods-type"));
                int amount = Integer.parseInt(in.getAttributeValue(null, "value"));
                AbstractGoods goods = new AbstractGoods(type, amount);
                String difficulty = in.getAttributeValue(null, "difficulty");
                if ("primary-production".equals(childName)) {
                    primaryGoodsMap.put(difficulty, goods);
                } else if ("secondary-production".equals(childName)) {
                    secondaryGoodsMap.put(difficulty, goods);
                } else {
                    Map<GoodsType, AbstractGoods> oldValue = productionMap.get(difficulty);
                    if (oldValue == null) {
                        oldValue = new HashMap<GoodsType, AbstractGoods>();
                        productionMap.put(difficulty, oldValue);
                    }
                    oldValue.put(type, goods);
                }
                in.nextTag(); 
            } else if ("resource".equals(childName)) {
                ResourceType type = specification.getResourceType(in.getAttributeValue(null, "type"));
                int probability = getAttribute(in, "probability", 100);
                resourceType.add(new RandomChoice<ResourceType>(type, probability));
                in.nextTag(); 
            } else {
                super.readChild(in, specification);
            }
        }
        
        if (artBasic == null) {
            throw new RuntimeException("TileType " + getId() + " has no art defined!");
        }
    }

}
