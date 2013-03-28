

package net.sf.freecol.common.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.freecol.common.Specification;
import net.sf.freecol.common.util.RandomChoice;

public final class TileType extends FreeColGameObjectType {

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

    private GoodsType secondaryGoods = null;

    
    private boolean connected;

    
    private List<AbstractGoods> production;

    

    public TileType(int index) {
        setIndex(index);
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
        Set<Modifier> result = featureContainer.getModifierSet(goodsType.getId(), unitType);
        if (unitType != null && !result.isEmpty()) {
            result.addAll(unitType.getFeatureContainer().getModifierSet(goodsType.getId()));
        }
        return (int) featureContainer.applyModifierSet(0f, null, result);
    }

    
    public Set<Modifier> getProductionBonus(GoodsType goodsType) {
        return featureContainer.getModifierSet(goodsType.getId());
    }

    public GoodsType getSecondaryGoods() {
        return secondaryGoods;
    }

    
    public List<AbstractGoods> getProduction() {
        return production;
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

    

    public void readAttributes(XMLStreamReader in, Specification specification)
        throws XMLStreamException {
        basicMoveCost = Integer.parseInt(in.getAttributeValue(null, "basic-move-cost"));
        basicWorkTurns = Integer.parseInt(in.getAttributeValue(null, "basic-work-turns"));
        forest = getAttribute(in, "is-forest", false);
        water = getAttribute(in, "is-water", false);
        canSettle = getAttribute(in, "can-settle", !water);
        connected = getAttribute(in, "is-connected", false);

        if (!water && canSettle) {
            secondaryGoods = specification.getGoodsType(in.getAttributeValue(null, "secondary-goods"));
        }
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
                float[] defaultArray = new float[] {0.0f, 0.0f, 0.0f};
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
            } else if ("production".equals(childName)) {
                GoodsType type = specification.getGoodsType(in.getAttributeValue(null, "goods-type"));
                int amount = Integer.parseInt(in.getAttributeValue(null, "value"));
                production.add(new AbstractGoods(type, amount));
                addModifier(new Modifier(type.getId(), this, amount, Modifier.Type.ADDITIVE));
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
