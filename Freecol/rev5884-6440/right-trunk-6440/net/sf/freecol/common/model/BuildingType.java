

package net.sf.freecol.common.model;

import java.awt.Image;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.freecol.common.Specification;
import net.sf.freecol.common.resources.ResourceManager;


public final class BuildingType extends BuildableType implements Comparable<BuildingType> {
    
    private static int nextIndex = 0;

    private int level;
  
    private int workPlaces, basicProduction, minSkill, maxSkill;
    private GoodsType consumes, produces;

    private Modifier productionModifier = null;
    private BuildingType upgradesFrom;
    private BuildingType upgradesTo;
    private int sequence;
    
    public BuildingType() {
        setIndex(nextIndex++);
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

    public void readAttributes(XMLStreamReader in, Specification specification) throws XMLStreamException {
        if (hasAttribute(in, "upgradesFrom")) {
            upgradesFrom = specification.getBuildingType(in.getAttributeValue(null, "upgradesFrom"));
            upgradesFrom.upgradesTo = this;
            level = upgradesFrom.level + 1;
        } else {
            level = 1;
        }
        setPopulationRequired(getAttribute(in, "required-population", 1));

        workPlaces = getAttribute(in, "workplaces", 0);
        basicProduction = getAttribute(in, "basicProduction", 0);

        consumes = specification.getType(in, "consumes", GoodsType.class, null);
        produces = specification.getType(in, "produces", GoodsType.class, null);

        if (produces != null && basicProduction > 0) {
            productionModifier = new Modifier(produces.getId(), this, basicProduction,
                                              Modifier.Type.ADDITIVE);
        }

        minSkill = getAttribute(in, "minSkill", Integer.MIN_VALUE);
        maxSkill = getAttribute(in, "maxSkill", Integer.MAX_VALUE);
        
        sequence = getAttribute(in, "sequence", 0);

    }

    
    public static String getXMLElementTagName() {
        return "building-type";
    }

    public boolean canAdd(UnitType unitType) {
        return unitType.hasSkill() && unitType.getSkill() >= minSkill && unitType.getSkill() <= maxSkill;
    }
  
}
