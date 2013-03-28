

package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.freecol.common.Specification;
import net.sf.freecol.common.model.UnitTypeChange.ChangeType;

public final class UnitType extends BuildableType implements Comparable<UnitType> {

    public static int nextIndex = 0;

    public static final int DEFAULT_OFFENCE = 0;
    public static final int DEFAULT_DEFENCE = 1;
    public static final int FOOD_CONSUMPTION = 2;

    
    private int offence;

    
    private int defence;

    
    private int space;

    
    private int hitPoints;

    
    private int spaceTaken;

    
    private int skill;

    
    private int price;

    
    private int movement;
    
    
    private int lineOfSight;

    
    private int recruitProbability;

    
    private GoodsType expertProduction;

    
    private int scoreValue;

    
    private String pathImage;

    
    private int maximumAttrition;

    
    private String skillTaught;

    
    private EquipmentType defaultEquipment;

    
    private int foodConsumed = FOOD_CONSUMPTION;

    
    private List<UnitTypeChange> typeChanges = new ArrayList<UnitTypeChange>();
    

    
    public UnitType() {
        setIndex(nextIndex++);
    }

    
    public boolean canCarryUnits() {
        return hasAbility("model.ability.carryUnits");
    }

    
    public boolean canCarryGoods() {
        return hasAbility("model.ability.carryGoods");
    }

    
    public int getScoreValue() {
        return scoreValue;
    }

    
    public void setScoreValue(final int newScoreValue) {
        this.scoreValue = newScoreValue;
    }

    
    public int getOffence() {
        return offence;
    }

    
    public void setOffence(final int newOffence) {
        this.offence = newOffence;
    }

    
    public int getDefence() {
        return defence;
    }

    
    public void setDefence(final int newDefence) {
        this.defence = newDefence;
    }

    
    public int getLineOfSight() {
        return lineOfSight;
    }

    
    public void setLineOfSight(final int newLineOfSight) {
        this.lineOfSight = newLineOfSight;
    }

    
    public int getSpace() {
        return space;
    }

    
    public void setSpace(final int newSpace) {
        this.space = newSpace;
    }

    
    public int getHitPoints() {
        return hitPoints;
    }

    
    public void setHitPoints(final int newHitPoints) {
        this.hitPoints = newHitPoints;
    }

    
    public int getSpaceTaken() {
        return Math.max(spaceTaken, space + 1);
    }

    
    public void setSpaceTaken(final int newSpaceTaken) {
        this.spaceTaken = newSpaceTaken;
    }

    
    public boolean isRecruitable() {
        return recruitProbability > 0;
    }

    
    public int getRecruitProbability() {
        return recruitProbability;
    }

    
    public void setRecruitProbability(final int newRecruitProbability) {
        this.recruitProbability = newRecruitProbability;
    }

    
    public int getSkill() {
        return skill;
    }

    
    public void setSkill(final int newSkill) {
        this.skill = newSkill;
    }

    
    public int getPrice() {
        return price;
    }

    
    public void setPrice(final int newPrice) {
        this.price = newPrice;
    }

    
    public int getMovement() {
        return movement;
    }

    
    public void setMovement(final int newMovement) {
        this.movement = newMovement;
    }

    
    public int getMaximumAttrition() {
        return maximumAttrition;
    }

    
    public void setMaximumAttrition(final int newMaximumAttrition) {
        this.maximumAttrition = newMaximumAttrition;
    }

    
    public GoodsType getExpertProduction() {
        return expertProduction;
    }

    
    public void setExpertProduction(final GoodsType newExpertProduction) {
        this.expertProduction = newExpertProduction;
    }

    
    public EquipmentType getDefaultEquipmentType() {
        return defaultEquipment;
    }

    
    public void setDefaultEquipmentType(final EquipmentType newDefaultEquipment) {
        this.defaultEquipment = newDefaultEquipment;
    }

    public EquipmentType[] getDefaultEquipment() {
        if (hasAbility("model.ability.canBeEquipped") && defaultEquipment != null) {
            int count = defaultEquipment.getMaximumCount();
            EquipmentType[] result = new EquipmentType[count];
            for (int index = 0; index < count; index++) {
                result[index] = defaultEquipment;
            }
            return result;
        } else {
            return EquipmentType.NO_EQUIPMENT;
        }
    }

    public List<UnitTypeChange> getTypeChanges() {
        return typeChanges;
    }

    
    public String getPathImage() {
        return pathImage;
    }

    
    public void setPathImage(final String newPathImage) {
        this.pathImage = newPathImage;
    }

    
    public String getSkillTaught() {
        return skillTaught;
    }

    
    public void setSkillTaught(final String newSkillTaught) {
        this.skillTaught = newSkillTaught;
    }

    
    public int getFoodConsumed() {
        return foodConsumed;
    }

    
    public void setFoodConsumed(final int newFoodConsumed) {
        this.foodConsumed = newFoodConsumed;
    }


    public int compareTo(UnitType other) {
        return getIndex() - other.getIndex();
    }

    
    public boolean isAvailableTo(Player player) {
        java.util.Map<String, Boolean> requiredAbilities = getAbilitiesRequired();
        for (Entry<String, Boolean> entry : requiredAbilities.entrySet()) {
            if (player.hasAbility(entry.getKey()) != entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    
    public UnitType getUnitTypeChange(ChangeType changeType, Player player) {
        for (UnitTypeChange change : typeChanges) {
            if (change.asResultOf(changeType) && change.appliesTo(player)) {
                UnitType result = change.getNewUnitType();
                if (result.isAvailableTo(player)) {
                    return result;
                }
            }
        }
        return null;
    }

    

    
    public boolean canBeUpgraded(UnitType newType, ChangeType changeType) {
        for (UnitTypeChange change : typeChanges) {
            if (change.asResultOf(changeType)) {
                if (newType == null
                    || newType == change.getNewUnitType()) {
                    return true;
                }
            }
        }
        return false;
    }

    
    public List<UnitType> getUnitTypesLearntInLostCity() {
        List<UnitType> unitTypes = new ArrayList<UnitType>();
        for (UnitTypeChange change : typeChanges) {
            if (change.asResultOf(ChangeType.LOST_CITY)) {
                unitTypes.add(change.getNewUnitType());
            }
        }
        return unitTypes;
    }

    
    public UnitType getEducationUnit(int maximumSkill) {
        for (UnitTypeChange change : typeChanges) {
            if (change.canBeTaught()) {
                UnitType unitType = change.getNewUnitType();
                if (unitType.hasSkill() && unitType.getSkill() <= maximumSkill) {
                    return unitType;
                }
            }
        }
        return null;
    }

    
    public int getEducationTurns(UnitType unitType) {
        for (UnitTypeChange change : typeChanges) {
            if (change.asResultOf(UnitTypeChange.ChangeType.EDUCATION)) {
                if (unitType == change.getNewUnitType()) {
                    return change.getTurnsToLearn();
                }
            }
        }
        return UNDEFINED;
    }

    public void readAttributes(XMLStreamReader in, Specification specification)
            throws XMLStreamException {
        offence = getAttribute(in, "offence", DEFAULT_OFFENCE);
        defence = getAttribute(in, "defence", DEFAULT_DEFENCE);
        movement = Integer.parseInt(in.getAttributeValue(null, "movement"));
        lineOfSight = getAttribute(in, "lineOfSight", 1);
        scoreValue = getAttribute(in, "scoreValue", 0);
        space = getAttribute(in, "space", 0);
        hitPoints = getAttribute(in, "hitPoints", 0);
        spaceTaken = getAttribute(in, "spaceTaken", 1);
        maximumAttrition = getAttribute(in, "maximumAttrition", Integer.MAX_VALUE);
        skillTaught = getAttribute(in, "skillTaught", getId());

        pathImage = in.getAttributeValue(null, "pathImage");

        recruitProbability = getAttribute(in, "recruitProbability", 0);
        skill = getAttribute(in, "skill", UNDEFINED);

        setPopulationRequired(getAttribute(in, "population-required", 1));

        price = getAttribute(in, "price", UNDEFINED);

        expertProduction = specification.getType(in, "expert-production", GoodsType.class, null);

    }

    public void readChildren(XMLStreamReader in, Specification specification) throws XMLStreamException {
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            String nodeName = in.getLocalName();
            if ("upgrade".equals(nodeName)) {
                typeChanges.add(new UnitTypeChange(in, specification));
            } else if ("downgrade".equals(nodeName)) {
                UnitTypeChange change = new UnitTypeChange(in, specification);
                if (change.getChangeTypes().isEmpty()) {
                    
                    change.getChangeTypes().add(ChangeType.CLEAR_SKILL);
                }
                typeChanges.add(change);
            } else if ("default-equipment".equals(nodeName)) {
                String equipmentString = in.getAttributeValue(null, "id");
                if (equipmentString != null) {
                    defaultEquipment = specification.getEquipmentType(equipmentString);
                }
                in.nextTag(); 
            } else {
                super.readChild(in, specification);
            }
        }
    }


    
    public boolean hasSkill() {

        return skill != UNDEFINED;
    }


    
    public boolean canBeBuilt() {
        return getGoodsRequired().isEmpty() == false;
    }


    
    public boolean hasPrice() {
        return price != UNDEFINED;
    }

    public int getProductionFor(GoodsType goodsType, int base) {
        if (base == 0) {
            return 0;
        }
        
        base = (int) featureContainer.applyModifier(base, goodsType.getId());
        return Math.max(base, 1);
    }


    
}
