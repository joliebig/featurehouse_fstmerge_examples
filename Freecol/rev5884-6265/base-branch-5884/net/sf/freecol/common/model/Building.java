

package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.i18n.Messages;

import org.w3c.dom.Element;


public final class Building extends FreeColGameObject implements WorkLocation, Ownable, Named {

    public static final String UNIT_CHANGE = "UNIT_CHANGE";

    
    private Colony colony;

    
    private List<Unit> units = Collections.emptyList();

    private BuildingType buildingType;


    
    public Building(Game game, Colony colony, BuildingType type) {
        super(game);

        this.colony = colony;
        this.buildingType = type;
    }

    
    public Building(Game game, XMLStreamReader in) throws XMLStreamException {
        super(game, in);

        readFromXML(in);
    }

    
    public Building(Game game, Element e) {
        super(game, e);

        readFromXMLElement(e);
    }

    
    public Building(Game game, String id) {
        super(game, id);
    }

    
    public Player getOwner() {
        return colony.getOwner();
    }

    
    public void setOwner(final Player p) {
        throw new UnsupportedOperationException();
    }

    
    public Tile getTile() {
        return colony.getTile();
    }

    
    public String getName() {
        return buildingType.getName();
    }

    
    public int getLevel() {
        return buildingType.getLevel();
    }

    
    public String getLocationName() {
        return Messages.message("inLocation", "%location%", getName());
    }

    
    public String getNextName() {
        final BuildingType next = buildingType.getUpgradesTo();
        return next == null ? null : next.getName();
    }

    
    public boolean canBuildNext() {
        return getColony().canBuild(buildingType.getUpgradesTo());
    }

    
    public Colony getColony() {
        return colony;
    }

    
    public BuildingType getType() {
        return buildingType;
    }
    
    
    public boolean canBeDamaged() {
        return buildingType.getGoodsRequired() != null;
    }
    
    
    public void damage() {
        if (canBeDamaged()) {
            setType(buildingType.getUpgradesFrom());
        }
    }
    
    
    public void upgrade() {
        if (!canBuildNext()) {
            throw new IllegalStateException("Cannot upgrade this building.");
        }
        setType(buildingType.getUpgradesTo());
    }
    
    private void setType(final BuildingType newBuildingType) {
        
        colony.getFeatureContainer().remove(buildingType.getFeatureContainer());

        if (newBuildingType != null) {
            buildingType = newBuildingType;
            
            
            colony.getFeatureContainer().add(buildingType.getFeatureContainer());
            
            
            for (Unit unit : units) {
                if (!canAdd(unit.getType())) {
                    unit.putOutsideColony();
                }
            }
        }
        
        
        while (units.size() > getMaxUnits()) {
            getLastUnit().putOutsideColony();
        }
    }
    
    
    public int getMaxUnits() {
        return buildingType.getWorkPlaces();
    }

    
    public int getUnitCount() {
        return units.size();
    }

    
    public boolean canAdd(final Locatable locatable) {
        if (locatable.getLocation() == this) {
            return true;
        }
        
        if (getUnitCount() >= getMaxUnits()) {
            return false;
        }

        if (!(locatable instanceof Unit)) {
            return false;
        }
        return canAdd(((Unit) locatable).getType());
    }

    
    public boolean canAdd(final UnitType unitType) {
        return buildingType.canAdd(unitType);
    }


    
    public void add(final Locatable locatable) {
        if (!canAdd(locatable)) {
            throw new IllegalStateException("Cannot add " + locatable + " to " + getName());
        } else if (!units.contains(locatable)) {
            if (units.equals(Collections.emptyList())) {
                units = new ArrayList<Unit>();
            } 
            final Unit unit = (Unit) locatable;

            unit.removeAllEquipment(false);
            unit.setState(Unit.UnitState.IN_COLONY);

            Unit student = unit.getStudent();
            if (buildingType.hasAbility("model.ability.teach")) {
                if (student == null) {
                    student = findStudent(unit);
                    if (student != null) {
                        unit.setStudent(student);
                        student.setTeacher(unit);
                    }
                }
            } else if (student != null) {
                student.setTeacher(null);
                unit.setStudent(null);
            }

            units.add(unit);
            
            GoodsType output = getGoodsOutputType();
            if (output != null) {
                firePropertyChange(output.getId(),
                                   new AbstractGoods(output, 0),
                                   new AbstractGoods(output, 1));
            }
        }
    }


    
    public UnitType getExpertUnitType() {
        return FreeCol.getSpecification().getExpertForProducing(getGoodsOutputType());
    }

    
    public void remove(final Locatable locatable) {
        if (locatable instanceof Unit) {
            if (units.remove(locatable)) {
                ((Unit) locatable).setMovesLeft(0);
                
                GoodsType output = getGoodsOutputType();
                if (output != null) {
                    firePropertyChange(output.getId(),
                                       new AbstractGoods(output, 1),
                                       new AbstractGoods(output, 0));
                }
            }
        } else {
            throw new IllegalStateException("Can only add units to building.");
        }
    }

    
    public boolean contains(final Locatable locatable) {
        return units.contains(locatable);
    }

    
    public Unit getFirstUnit() {
        if (units.isEmpty()) {
            return null;
        } else {
            return units.get(0);
        }
    }

    
    public Unit getLastUnit() {
        if (units.isEmpty()) {
            return null;
        } else {
            return units.get(units.size() - 1);
        }
    }

    
    public Iterator<Unit> getUnitIterator() {
        return units.iterator();
    }

    public List<Unit> getUnitList() {
        return new ArrayList<Unit>(units);
    }

    
    public GoodsContainer getGoodsContainer() {
        return null;
    }

    
    public void newTurn() {
        if (buildingType.hasAbility("model.ability.teach")) {
            trainStudents();
        }
        if (buildingType.hasAbility("model.ability.repairUnits")) {
            repairUnits();
        }
        if (getGoodsOutputType() != null) {
            produceGoods();
        }
    }

    
    private void repairUnits() {
        for (Unit unit : getTile().getUnitList()) {
            if (unit.isUnderRepair() &&
                buildingType.hasAbility("model.ability.repairUnits", unit.getType())) {
                unit.setHitpoints(unit.getHitpoints() + 1);
                if (!unit.isUnderRepair()) {
                    addModelMessage(this, ModelMessage.MessageType.DEFAULT, this,
                                    "model.unit.unitRepaired",
                                    "%unit%", unit.getName(),
                                    "%repairLocation%", getLocationName());
                }
            }
        }
    }

    private void produceGoods() {
        final int goodsInput = getGoodsInput();
        final int goodsOutput = getProduction();
        final GoodsType goodsInputType = getGoodsInputType();
        final GoodsType goodsOutputType = getGoodsOutputType();

        if (goodsInput == 0 && !canAutoProduce() && getMaximumGoodsInput() > 0) {
            addModelMessage(getColony(), ModelMessage.MessageType.MISSING_GOODS,
                            goodsInputType,
                            "model.building.notEnoughInput",
                            "%inputGoods%", goodsInputType.getName(),
                            "%building%", getName(),
                            "%colony%", colony.getName());
        }

        if (goodsOutput <= 0) {
            return;
        }
        
        
        
        
        
        if (goodsOutputType.isBuildingMaterial() 
                && !goodsOutputType.isStorable()
                && !getColony().canBuild()){
            return;
        }

        
        if (goodsInputType != null) {
            colony.removeGoods(goodsInputType, goodsInput);
        }
        colony.addGoods(goodsOutputType, goodsOutput);

        if (getUnitCount() > 0) {
            final int experience = goodsOutput / getUnitCount();
            for (Unit unit : getUnitList()) {
                unit.modifyExperience(experience);
            }
        }
    }

    public Unit findStudent(final Unit teacher) {
        Unit student = null;
        GoodsType expertProduction = teacher.getType().getExpertProduction();
        boolean leastSkilled = getGameOptions().getBoolean(GameOptions.EDUCATE_LEAST_SKILLED_UNIT_FIRST);
        int skill = leastSkilled ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        for (Unit potentialStudent : getColony().getUnitList()) {
            
            if (potentialStudent.getTeacher() == null &&
                potentialStudent.canBeStudent(teacher)) {                
                if ((student == null || potentialStudent.getSkillLevel() == skill) &&
                    potentialStudent.getWorkType() == expertProduction) {
                    student = potentialStudent;
                } else if (leastSkilled && potentialStudent.getSkillLevel() < skill ||
                           !leastSkilled && potentialStudent.getSkillLevel() > skill) {
                    student = potentialStudent;
                    skill = student.getSkillLevel();
                }
            }
        }
        return student;
    }

    private boolean assignStudent(Unit teacher) {
        final Unit student = findStudent(teacher);
        if (student == null) {
            addModelMessage(getColony(), ModelMessage.MessageType.WARNING, teacher,
                            "model.building.noStudent",
                            "%teacher%", teacher.getName(),
                            "%colony%", colony.getName());
            return false;
        } else {
            teacher.setStudent(student);
            student.setTeacher(teacher);
            return true;
        }                
    }

    private void trainStudents() {
        final Iterator<Unit> teachers = getUnitIterator();
        while (teachers.hasNext()) {
            final Unit teacher = teachers.next();
            if (teacher.getStudent() == null && !assignStudent(teacher)) {
                continue;
            }
            final int training = teacher.getTurnsOfTraining() + 1;
            if (training < teacher.getNeededTurnsOfTraining()) {
                teacher.setTurnsOfTraining(training);
            } else {
                teacher.setTurnsOfTraining(0);
                teacher.getStudent().train();
                if (teacher.getStudent() == null) {
                    assignStudent(teacher);
                }
            }
        }
    }


    
    public GoodsType getGoodsOutputType() {
        return getType().getProducedGoodsType();
    }

    
    public GoodsType getGoodsInputType() {
        return getType().getConsumedGoodsType();
    }

    
    public int getMaximumGoodsInput() {
        if (getGoodsInputType() == null) {
            return 0;
        } else if (canAutoProduce()) {
            return getMaximumAutoProduction();
        } else {
            return getProductivity();
        }
    }

    private int getStoredInput() {
        return colony.getGoodsCount(getGoodsInputType());
    }

    
    public int getGoodsInput() {
        if (getGoodsInputType() == null) {
            return 0;
        } else if (canAutoProduce()) {
            return getGoodsInputAuto(colony.getProductionOf(getGoodsInputType()));
        } else {
            return calculateGoodsInput(getMaximumGoodsInput(), 0);
        }
    }
    
    
    public int getGoodsInputNextTurn() {
        if (getGoodsInputType() == null) {
            return 0;
        } else if (canAutoProduce()) {
            return getGoodsInputAuto(colony.getProductionNextTurn(getGoodsInputType()));
        } else {
            return calculateGoodsInput(getMaximumGoodsInput(),
                                       colony.getProductionNextTurn(getGoodsInputType()));
        }
    }

    private int getGoodsInputAuto(int available) {
        if (getGoodsInputType() == null) {
            return 0;
        } else {
            int outputGoods = colony.getGoodsCount(getGoodsOutputType());
            if (outputGoods < getGoodsOutputType().getBreedingNumber() ||
                outputGoods >= colony.getWarehouseCapacity()) {
                return 0;
            } else {
                int surplus = available;
                
                if (getGoodsInputType().isFoodType()) {
                    surplus -= colony.getFoodConsumptionByType(getGoodsInputType());
                    if (surplus <= 0) {
                        return 0;
                    }
                    
                    surplus = (int) Math.ceil(surplus / 2.0);  
                }
               return surplus;
            }
        }
    }

    private int calculateGoodsInput(final int maximumGoodsInput, final int addToWarehouse) {
        final int availableInput = getStoredInput() + addToWarehouse;
        if (availableInput < maximumGoodsInput) {
            
            return availableInput;
        }
        return maximumGoodsInput;
    }
    
    
    private int getProductionAdding(int availableGoodsInput, Unit... additionalUnits) {
        if (getGoodsOutputType() == null) {
            return 0;
        } else {
            int maximumGoodsInput = getProductivity(additionalUnits);
            if (getGoodsInputType() != null) {
                
                if (availableGoodsInput < maximumGoodsInput) {
                    maximumGoodsInput = availableGoodsInput;
                }
                if (buildingType.hasAbility("model.ability.expertsUseConnections") &&
                    getGameOptions().getBoolean(GameOptions.EXPERTS_HAVE_CONNECTIONS)) {
                    int minimumGoodsInput = 0;
                    for (Unit unit: units) {
                        if (unit.getType() == getExpertUnitType()) {
                            minimumGoodsInput += 4;
                        }
                    }
                    for (Unit unit : additionalUnits) {
                        if (canAdd(unit) && unit.getType() == getExpertUnitType()) {
                            minimumGoodsInput += 4;
                        }
                    }
                    if (maximumGoodsInput < minimumGoodsInput) {
                        maximumGoodsInput = minimumGoodsInput;
                    }
                }
            }
            
            return applyModifiers(maximumGoodsInput);
        }
    }


    
    public int getProduction() {
        if (canAutoProduce()) {
            return getAutoProduction(getGoodsInput());
        } else if (getGoodsInputType() == null) {
            return getProductionAdding(0);
        } else {
            return getProductionAdding(getStoredInput());
        }
    }
    
    
    public int getProductionNextTurn() {
        if (canAutoProduce()) {
            return getAutoProduction(getGoodsInputNextTurn());
        } else if (getGoodsInputType() == null) {
            return getProductionAdding(0);
        } else {
            return getProductionAdding(getStoredInput() + 
                                       colony.getProductionNextTurn(getGoodsInputType()));
        }
    }

    
    public boolean canAutoProduce() {
        return buildingType.hasAbility("model.ability.autoProduction");
    }

    
    private int getAutoProduction(int availableInput) {
        if (getGoodsOutputType() == null ||
            colony.getGoodsCount(getGoodsOutputType()) >= colony.getWarehouseCapacity()) {
            return 0;
        }

        int goodsOutput = getMaximumAutoProduction();

        
        if (getGoodsInputType() != null && availableInput < goodsOutput) {
            goodsOutput = availableInput;
        }
        
        
        int availSpace = colony.getWarehouseCapacity() - colony.getGoodsCount(getGoodsOutputType());
        if (goodsOutput > availSpace) {
            goodsOutput = availSpace;
        }
        
        return applyModifiers(goodsOutput);
    }

    
    public int getAdditionalProductionNextTurn(Unit addUnit) {
        return getProductionAdding(getStoredInput() + 
                                   colony.getProductionNextTurn(getGoodsInputType()), addUnit) - 
            getProductionNextTurn();
    }

    
    public int getProductionOf(GoodsType goodsType) {
        if (goodsType == getGoodsOutputType()) {
            return getProduction();
        }

        return 0;
    }

    
    private int getProductivity(Unit... additionalUnits) {
        if (getGoodsOutputType() == null) {
            return 0;
        }

        int productivity = 0;
        for (Unit unit : units) {
            productivity += getUnitProductivity(unit);
        }
        for (Unit unit : additionalUnits) {
            if (canAdd(unit)) {
                productivity += getUnitProductivity(unit);
            }
        }
        return productivity;
    }

    
    public int getUnitProductivity(Unit prodUnit) {
        if (getGoodsOutputType() == null || prodUnit == null) {
            return 0;
        }

        int base = buildingType.getBasicProduction();
        int productivity = prodUnit.getProductionOf(getGoodsOutputType(), base);
        if (productivity > 0) {
            productivity += colony.getProductionBonus();
            if (productivity < 1)
                productivity = 1;
        }
        return productivity;
    }

    
    public Set<Modifier> getProductivityModifiers(Unit prodUnit) {
        if (getGoodsOutputType() == null) {
            return Collections.emptySet();
        } else {
            String outputId = getGoodsOutputType().getId();
            Set<Modifier> result = new LinkedHashSet<Modifier>();
            if (buildingType.getProductionModifier() != null) {
                result.add(buildingType.getProductionModifier());
            }
            result.addAll(prodUnit.getModifierSet(outputId));
            return result;
        }
    }

    
    public int getMaximumProduction() {
        if (canAutoProduce()) {
            return getMaximumAutoProduction();
        } else {
            return applyModifiers(getProductivity());
        }
    }
    
    
    private int getMaximumAutoProduction() {
        int available = colony.getGoodsCount(getGoodsOutputType());
        if (available < getGoodsOutputType().getBreedingNumber()) {
            
            return 0;
        }
        return Math.max(1, available / 10);
    }

    
    public int getAdditionalProduction(Unit addUnit) {
        return getProductionAdding(getStoredInput(), addUnit) - getProduction();
    }

    
    
    public int applyModifiers(int productivity) {
        GoodsType goodsOutputType = getGoodsOutputType();
        if (goodsOutputType == null) {
            return 0;
        }
        return Math.round(colony.getFeatureContainer().applyModifier(productivity,
                                                                     goodsOutputType.getId(),
                                                                     buildingType, getGame().getTurn()));
    }
    
    private static Comparator<Building> buildingComparator = new Comparator<Building>() {
        public int compare(Building b1, Building b2) {
            return b1.getType().getSequence() - b2.getType().getSequence();
        }
    };
    
    public static Comparator<Building> getBuildingComparator() {
        return buildingComparator;
    }

    
    @Override
    public void dispose() {
        for (Unit unit : new ArrayList<Unit>(units)) {
            unit.dispose();
        }
        super.dispose();
    }

    
    @Override
    protected void toXMLImpl(XMLStreamWriter out, Player player, boolean showAll, boolean toSavedGame)
            throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        
        out.writeAttribute("ID", getId());
        out.writeAttribute("colony", colony.getId());
        out.writeAttribute("buildingType", buildingType.getId());

        
        Iterator<Unit> unitIterator = getUnitIterator();
        while (unitIterator.hasNext()) {
            unitIterator.next().toXML(out, player, showAll, toSavedGame);
        }

        
        out.writeEndElement();
    }

    
    @Override
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setId(in.getAttributeValue(null, "ID"));

        colony = getFreeColGameObject(in, "colony", Colony.class);
        buildingType = FreeCol.getSpecification().getBuildingType(in.getAttributeValue(null, "buildingType"));

        units.clear();

        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            Unit unit = updateFreeColGameObject(in, Unit.class);
            if (!units.contains(unit)) {
                if (units.equals(Collections.emptyList())) {
                    units = new ArrayList<Unit>();
                }
                units.add(unit);
            }
        }
    }

    
    public static String getXMLElementTagName() {
        return "building";
    }

    public String toString() {
        return getName();
    }

}
