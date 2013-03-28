

package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.Specification;
import net.sf.freecol.client.gui.i18n.Messages;

public final class TileImprovementType extends FreeColGameObjectType
{

    private boolean natural;
    private String typeId;
    private int magnitude;
    private int addWorkTurns;

    private String artOverlay;

    private List<TileType> allowedTileTypes;
    private TileImprovementType requiredImprovementType;

    private Set<String> allowedWorkers;
    private EquipmentType expendedEquipmentType;
    private int expendedAmount;
    private GoodsType deliverGoodsType;
    private int deliverAmount;

    private Map<TileType, TileType> tileTypeChange = new HashMap<TileType, TileType>();

    private int movementCost;
    private float movementCostFactor;

    
    private int zIndex;

    

    public TileImprovementType(int index) {
        setIndex(index);
    }

    

    public boolean isNatural() {
        return natural;
    }

    public int getMagnitude() {
        return magnitude;
    }

    public int getAddWorkTurns() {
        return addWorkTurns;
    }

    public String getOccupationString() {
        return Messages.message(getId() + ".occupationString");
    }

    public String getArtOverlay() {
        return artOverlay;
    }

    
    public int getZIndex() {
        return zIndex;
    }

    
    public void setZIndex(final int newZIndex) {
        this.zIndex = newZIndex;
    }

    public TileImprovementType getRequiredImprovementType() {
        return requiredImprovementType;
    }

    public EquipmentType getExpendedEquipmentType() {
        return expendedEquipmentType;
    }

    public int getExpendedAmount() {
        return expendedAmount;
    }

    public GoodsType getDeliverGoodsType() {
        return deliverGoodsType;
    }

    public int getDeliverAmount() {
        return deliverAmount;
    }

    public boolean isWorkerTypeAllowed(UnitType unitType) {
        return allowedWorkers.isEmpty() || allowedWorkers.contains(unitType.getId());
    }

    
    public boolean isWorkerAllowed(Unit unit) {
        if (!isWorkerTypeAllowed(unit.getType())) {
            return false;
        }
        return (unit.getEquipment().getCount(expendedEquipmentType) >= expendedAmount);
    }

    
    public boolean isTileTypeAllowed(TileType tileType) {
        return (allowedTileTypes.indexOf(tileType) >= 0);
    }

    
    public boolean isTileAllowed(Tile tile) {
        if (!isTileTypeAllowed(tile.getType())) {
            return false;
        }
        if (requiredImprovementType != null && tile.findTileImprovementType(requiredImprovementType) == null) {
            return false;
        }
        TileImprovement ti = tile.findTileImprovementType(this);
        return ti == null || !ti.isComplete();
    }

    public int getBonus(GoodsType goodsType) {
        Modifier result = getProductionModifier(goodsType);
        if (result == null) {
            return 0;
        } else {
            return (int) result.getValue();
        }
    }

    public Modifier getProductionModifier(GoodsType goodsType) {
        Set<Modifier> modifierSet = featureContainer.getModifierSet(goodsType.getId());
        if (modifierSet == null || modifierSet.isEmpty()) {
            return null;
        } else {
            if (modifierSet.size() > 1) {
                logger.warning("Only one Modifier for " + goodsType.getId() + " expected!");
            }
            return modifierSet.iterator().next();
        }
    }

    public TileType getChange(TileType tileType) {
        return tileTypeChange.get(tileType);
    }

    public boolean changeContainsTarget(TileType tileType) {
        return tileTypeChange.containsValue(tileType);
    }

    
    public int getValue(TileType tileType, GoodsType goodsType) {
        int value = 0;
        if (goodsType.isFarmed()) {
            TileType newTileType = getChange(tileType);
            
            if (newTileType != null) {
                int change = newTileType.getProductionOf(goodsType, null)
                    - tileType.getProductionOf(goodsType, null);
                if (change > 0) {
                    value += change * 3;
                }
            } else if (tileType.getProductionOf(goodsType, null) > 0) {
                
                for (Modifier modifier : featureContainer.getModifiers()) {
                    float change = modifier.applyTo(1);
                    if (modifier.getId().equals(goodsType.getId())) {
                        if (change > 1) {
                            value += change * 3;
                        }
                    }
                }
            }
        }
        return value;
    }

    
    public int getMovementCost(int moveCost) {
        int cost = moveCost;
        if (movementCostFactor >= 0) {
            float cost2 = cost * movementCostFactor;
            cost = (int)cost2;
            if (cost < cost2) {
                cost++;
            }
        }
        if (movementCost >= 0) {
            if (movementCost < cost) {
                return movementCost;
            } else {
                return cost;
            }
        }
        return cost;
    }

    

    public void readAttributes(XMLStreamReader in, Specification specification)
        throws XMLStreamException {
        natural = getAttribute(in, "natural", false);
        addWorkTurns = getAttribute(in, "add-work-turns", 0);
        movementCost = getAttribute(in, "movement-cost", -1);
        movementCostFactor = -1;
        magnitude = getAttribute(in, "magnitude", 1);

        requiredImprovementType = specification.getType(in, "required-improvement", 
                                                        TileImprovementType.class, null);

        artOverlay = getAttribute(in, "overlay", null);
        zIndex = getAttribute(in, "zIndex", 0);

        expendedEquipmentType = specification.getType(in, "expended-equipment-type", EquipmentType.class, null);
        expendedAmount = getAttribute(in, "expended-amount", 0);
        deliverGoodsType = specification.getType(in, "deliver-goods-type", GoodsType.class, null);
        deliverAmount = getAttribute(in, "deliver-amount", 0);
    }


    public void readChildren(XMLStreamReader in, Specification specification)
        throws XMLStreamException {

        allowedWorkers = new HashSet<String>();
        allowedTileTypes = new ArrayList<TileType>();
        tileTypeChange = new HashMap<TileType, TileType>();

        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            String childName = in.getLocalName();
            if ("tiles".equals(childName)) {
                boolean allLand = getAttribute(in, "all-land-tiles", false);
                boolean allForestUndefined = in.getAttributeValue(null, "all-forest-tiles") == null;
                boolean allForest = getAttribute(in, "all-forest-tiles", false);
                boolean allWater = getAttribute(in, "all-water-tiles", false);

                for (TileType t : specification.getTileTypeList()) {
                    if (t.isWater()){
                        if (allWater)
                            allowedTileTypes.add(t);
                    } else {
                        if (t.isForested()){
                            if ((allLand && allForestUndefined) || allForest){
                                allowedTileTypes.add(t);
                            }
                        } else {
                            if (allLand){
                                allowedTileTypes.add(t);
                            }
                        }
                                
                    }
                }
                in.nextTag(); 
            } else if ("tile".equals(childName)) {
                String tileId = in.getAttributeValue(null, "id");
                if (getAttribute(in, "value", true)) {
                    allowedTileTypes.add(specification.getTileType(tileId));
                } else {
                    allowedTileTypes.remove(specification.getTileType(tileId));
                }
                in.nextTag(); 
            } else if ("worker".equals(childName)) {
                allowedWorkers.add(in.getAttributeValue(null, "id"));
                in.nextTag(); 
            } else if ("change".equals(childName)) {
                tileTypeChange.put(specification.getTileType(in.getAttributeValue(null, "from")),
                                   specification.getTileType(in.getAttributeValue(null, "to")));
                in.nextTag(); 
            } else {
                super.readChild(in, specification);
            }
        }
    }
}
