

package net.sf.freecol.common.model;

import java.awt.Image;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.freecol.common.resources.ResourceManager;


public final class BuildingType extends BuildableType implements Comparable<BuildingType> {
    
    private static int nextIndex = 0;

    private int level = 1;
    private int workPlaces = 3;
    private int basicProduction = 3;
    private int minSkill = Integer.MIN_VALUE;
    private int maxSkill = Integer.MAX_VALUE;
    private int sequence = 0;

    private GoodsType consumes, produces;
    private Modifier productionModifier = null;
    private BuildingType upgradesFrom;
    private BuildingType upgradesTo;
    
    public BuildingType() {
        setIndex(nextIndex++);
        setModifierIndex(Modifier.BUILDING_PRODUCTION_INDEX);
    }
    
    public BuildingType getUpgradesFrom() {
        return upgradesFrom;
    }
    
    public BuildingType getUpgradesTo() {
        return upgradesTo;
    }
    
    public Image getImage() {
        return ResourceManager.getImage(getId() + ".image");
    }
    
    public BuildingType getFirstLevel() {
        BuildingType buildingType = this;
        while (buildingType.getUpgradesFrom() != null) {
            buildingType = buildingType.getUpgradesFrom();
        }
        return buildingType;
    }
    
    public int getWorkPlaces() {
        return workPlaces;
    }
    
    public int getBasicProduction() {
        return basicProduction;
    }

    public GoodsType getConsumedGoodsType() {
        return consumes;
    }

    public GoodsType getProducedGoodsType() {
        return produces;
    }

    public int getLevel() {
        return level;
    }

    public int getSequence() {
        return sequence;
    }

    public FreeColGameObjectType getType() {
        return this;
    }

    public Modifier getProductionModifier() {
        return productionModifier;
    }

    public int compareTo(BuildingType other) {
        return getIndex() - other.getIndex();
    }

    
    @Override
    public final int getModifierIndex(Modifier modifier) {
        if (produces != null && produces.getId().equals(modifier.getId())) {
            return Modifier.AUTO_PRODUCTION_INDEX;
        } else {
            return getModifierIndex();
        }
    }


    public void readAttributes(XMLStreamReader in, Specification specification) throws XMLStreamException {
        String extendString = in.getAttributeValue(null, "extends");
        BuildingType parent = (extendString == null) ? this :
            specification.getBuildingType(extendString);
        String upgradeString = in.getAttributeValue(null, "upgradesFrom");
        if (upgradeString == null) {
            level = 1;
        } else {
            upgradesFrom = specification.getBuildingType(upgradeString);
            upgradesFrom.upgradesTo = this;
            level = upgradesFrom.level + 1;
        }
        setPopulationRequired(getAttribute(in, "required-population", parent.getPopulationRequired()));

        workPlaces = getAttribute(in, "workplaces", parent.workPlaces);
        basicProduction = getAttribute(in, "basicProduction", parent.basicProduction);

        consumes = specification.getType(in, "consumes", GoodsType.class, parent.consumes);
        produces = specification.getType(in, "produces", GoodsType.class, parent.produces);

        if (produces != null && basicProduction > 0) {
            productionModifier = new Modifier(produces.getId(), this, basicProduction,
                                              Modifier.Type.ADDITIVE);
        }

        minSkill = getAttribute(in, "minSkill", parent.minSkill);
        maxSkill = getAttribute(in, "maxSkill", parent.maxSkill);
        
        sequence = getAttribute(in, "sequence", parent.sequence);

        if (parent != this) {
            getFeatureContainer().add(parent.getFeatureContainer());
        }
    }

    
    public static String getXMLElementTagName() {
        return "building-type";
    }

    public boolean canAdd(UnitType unitType) {
        return unitType.hasSkill() && unitType.getSkill() >= minSkill && unitType.getSkill() <= maxSkill;
    }
  
}
