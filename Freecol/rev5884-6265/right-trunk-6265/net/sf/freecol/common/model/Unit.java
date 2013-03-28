

package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.Specification;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Map.PathType;
import net.sf.freecol.common.model.Map.Position;
import net.sf.freecol.common.model.Player.Stance;
import net.sf.freecol.common.model.TradeRoute.Stop;
import net.sf.freecol.common.model.UnitTypeChange.ChangeType;
import net.sf.freecol.common.util.EmptyIterator;
import net.sf.freecol.common.util.Utils;

import org.w3c.dom.Element;


public class Unit extends FreeColGameObject implements Locatable, Location, Ownable, Nameable {
    private static Comparator<Unit> skillLevelComp  = null;
    
    private static final Logger logger = Logger.getLogger(Unit.class.getName());

    
    private static final String EQUIPMENT_TAG = "equipment";

    private static final String UNITS_TAG_NAME = "units";

    
    public static final int TURNS_TO_SAIL = Specification.getSpecification().getIntegerOption(
            "model.option.turnsToSail").getValue();

    public static final String CARGO_CHANGE = "CARGO_CHANGE";

    
    public static enum UnitState { ACTIVE, FORTIFIED, SENTRY, IN_COLONY, IMPROVING,
            TO_EUROPE, TO_AMERICA, FORTIFYING, SKIPPED }

    
    public static enum Role {
        DEFAULT, PIONEER, MISSIONARY, SOLDIER, SCOUT, DRAGOON;
    
        public String getId() {
            return toString().toLowerCase();
        }
    }

    
    public static enum MoveType {
        MOVE(null, true),
        MOVE_HIGH_SEAS(null, true),
        EXPLORE_LOST_CITY_RUMOUR(null, true),
        ATTACK(null, false),
        EMBARK(null, false),
        ENTER_INDIAN_VILLAGE_WITH_FREE_COLONIST(null, false),
        ENTER_INDIAN_VILLAGE_WITH_SCOUT(null, false),
        ENTER_INDIAN_VILLAGE_WITH_MISSIONARY(null, false),
        ENTER_FOREIGN_COLONY_WITH_SCOUT(null, false),
        ENTER_SETTLEMENT_WITH_CARRIER_AND_GOODS(null, false),
        MOVE_ILLEGAL("Unspecified illegal move"),
        MOVE_NO_MOVES("Attempt to move without moves left"),
        MOVE_NO_ACCESS_LAND("Attempt to move a naval unit onto land"),
        MOVE_NO_ACCESS_BEACHED("Attempt to move onto foreign beached ship"),
        MOVE_NO_ACCESS_EMBARK("Attempt to embark onto absent or foreign carrier"),
        MOVE_NO_ACCESS_FULL("Attempt to embark onto full carrier"),
        MOVE_NO_ACCESS_SETTLEMENT("Attempt to move into foreign settlement"),
        MOVE_NO_ATTACK_MARINE("Attempt to attack non-settlement from on board ship"),
        MOVE_NO_ATTACK_CIVILIAN("Attempt to attack with civilian unit"),
        MOVE_NO_EUROPE("Attempt to move to Europe by incapable unit"),
        MOVE_NO_REPAIR("Attempt to move a unit that is under repair");

        
        private String reason;

        
        private boolean progress;

        MoveType(String reason) {
            this.reason = reason;
            this.progress = false;
        }

        MoveType(String reason, boolean progress) {
            this.reason = reason;
            this.progress = progress;
        }

        public boolean isLegal() {
            return this.reason == null;
        }

        public String whyIllegal() {
            return reason;
        }

        public boolean isProgress() {
            return progress;
        }
    }

    private UnitType unitType;

    private boolean naval;

    private int movesLeft;

    private UnitState state = UnitState.ACTIVE;

    private Role role = Role.DEFAULT;

    
    private int workLeft;

    private int hitpoints; 

    private Player owner;

    private List<Unit> units = Collections.emptyList();

    private GoodsContainer goodsContainer;

    private Location entryLocation;

    private Location location;

    private IndianSettlement indianSettlement = null; 

    private Location destination = null;

    private TradeRoute tradeRoute = null; 

    
    private int currentStop = -1;

    
    private int treasureAmount;

    
    private TileImprovement workImprovement;

    
    private GoodsType workType;

    private int experience = 0;

    private int turnsOfTraining = 0;

    
    private int attrition = 0;

    
    private String name = null;

    
    private int visibleGoodsCount;

    
    private boolean alreadyOnHighSea = false;

    
    private Unit student;

    
    private Unit teacher;

    
    private TypeCountMap<EquipmentType> equipment = new TypeCountMap<EquipmentType>();


    
    public Unit(Game game, Player owner, UnitType type) {
        this(game, null, owner, type, UnitState.ACTIVE);
    }

    
    public Unit(Game game, Location location, Player owner, UnitType type, UnitState state) {
        this(game, location, owner, type, state, type.getDefaultEquipment());
    }

    
    public Unit(Game game, Location location, Player owner, UnitType type, UnitState state, 
                EquipmentType... initialEquipment) {
        super(game);

        visibleGoodsCount = -1;

        if (type.canCarryGoods()) {
            goodsContainer = new GoodsContainer(game, this);
        }

        UnitType newType = type.getUnitTypeChange(ChangeType.CREATION, owner);
        if (newType == null) {
            unitType = type;
        } else {
            unitType = newType;
        }
        this.owner = owner;
        naval = unitType.hasAbility("model.ability.navalUnit");
        setLocation(location);

        workLeft = -1;
        workType = Goods.FOOD;

        this.movesLeft = getInitialMovesLeft();
        hitpoints = unitType.getHitPoints();

        for (EquipmentType equipmentType : initialEquipment) {
            if (EquipmentType.NO_EQUIPMENT.equals(equipmentType)) {
                equipment.clear();
                break;
            } else {
                equipment.incrementCount(equipmentType, 1);
            }
        }
        setRole();
        setStateUnchecked(state);

        getOwner().setUnit(this);
        getOwner().invalidateCanSeeTiles();
        getOwner().modifyScore(type.getScoreValue());
    }

    
    public Unit(Game game, XMLStreamReader in) throws XMLStreamException {
        super(game, in);
        readFromXML(in);
    }

    
    public Unit(Game game, Element e) {
        super(game, e);
        readFromXMLElement(e);
    }

    
    public Unit(Game game, String id) {
        super(game, id);
    }

    
    public boolean canCarryUnits() {
        return hasAbility("model.ability.carryUnits");
    }

    
    public boolean canCarryGoods() {
        return hasAbility("model.ability.carryGoods");
    }

    
    public String getLocationName() {
        return Messages.message("onBoard", "%unit%", getName());
    }

    
    public final UnitType getType() {
        return unitType;
    }

    
    public int getTreasureAmount() {
        if (canCarryTreasure()) {
            return treasureAmount;
        }
        throw new IllegalStateException("Unit can't carry treasure");
    }

    
    public void setTreasureAmount(int amt) {
        if (canCarryTreasure()) {
            this.treasureAmount = amt;
        } else {
            throw new IllegalStateException("Unit can't carry treasure");
        }
    }

    
    public final TypeCountMap<EquipmentType> getEquipment() {
        return equipment;
    }

    
    public final void setEquipment(final TypeCountMap<EquipmentType> newEquipment) {
        this.equipment = newEquipment;
    }

    
    public final TradeRoute getTradeRoute() {
        return tradeRoute;
    }

    
    public final void setTradeRoute(final TradeRoute newTradeRoute) {
        this.tradeRoute = newTradeRoute;
        if (newTradeRoute != null) {
            ArrayList<Stop> stops = newTradeRoute.getStops();
            if (stops.size() > 0) {
                setDestination(newTradeRoute.getStops().get(0).getLocation());
                currentStop = 0;
            }
        }
    }

    
    public Stop getStop() {
        return (validateCurrentStop() < 0) ? null
            : getTradeRoute().getStops().get(currentStop);
    }

    
    public int getCurrentStop() {
        return currentStop;
    }

    
    public void setCurrentStop(int currentStop) {
        this.currentStop = currentStop;
    }

    
    public int validateCurrentStop() {
        if (tradeRoute == null) {
            currentStop = -1;
        } else {
            ArrayList<Stop> stops = tradeRoute.getStops();
            if (stops == null || stops.size() == 0) {
                currentStop = -1;
            } else {
                if (currentStop < 0 || currentStop >= stops.size()) {
                    
                    
                    currentStop = 0;
                }
            }
        }
        return currentStop;
    }

    
    public boolean canCashInTreasureTrain() {
        return canCashInTreasureTrain(getLocation());
    }

    
    public boolean canCashInTreasureTrain(Location loc) {
        if (!canCarryTreasure()) {
            throw new IllegalStateException("Can't carry treasure");
        }
        if (getOwner().getEurope() == null) {
            
            
            return loc.getColony() != null;
        }
        if (loc.getColony() != null) {
            
            return loc.getColony().isConnected();
        }
        
        return loc instanceof Europe
            || (loc instanceof Unit && ((Unit) loc).getLocation() instanceof Europe);
    }

    
    public int getTransportFee() {
        if (canCashInTreasureTrain()) {
            if (!isInEurope() && getOwner().getEurope() != null) {
                return (int) getOwner().getFeatureContainer()
                    .applyModifier(getTreasureAmount() / 2f,
                                   "model.modifier.treasureTransportFee",
                                   unitType, getGame().getTurn());
            }
        }
        return 0;
    }

    
    public boolean isColonist() {
        return unitType.hasAbility("model.ability.foundColony")
            && owner.isEuropean();
    }

    
    public int getNeededTurnsOfTraining() {
        
        int result = 0;
        if (student != null) {
            result = getNeededTurnsOfTraining(unitType, student.unitType);
            if (getColony() != null) {
                result -= getColony().getProductionBonus();
            }
        }
        return result;
    }

    
    public static int getNeededTurnsOfTraining(UnitType typeTeacher, UnitType typeStudent) {
        UnitType teaching = getUnitTypeTeaching(typeTeacher, typeStudent);
        if (teaching != null) {
            return typeStudent.getEducationTurns(teaching);
        } else {
            throw new IllegalStateException("typeTeacher=" + typeTeacher + " typeStudent=" + typeStudent);
        }
    }

    
    public static UnitType getUnitTypeTeaching(UnitType typeTeacher, UnitType typeStudent) {
        UnitType skillTaught = Specification.getSpecification().getUnitType(typeTeacher.getSkillTaught());
        if (typeStudent.canBeUpgraded(skillTaught, ChangeType.EDUCATION)) {
            return skillTaught;
        } else {
            return typeStudent.getEducationUnit(0);
        }
    }

    
    public int getSkillLevel() {
        return getSkillLevel(unitType);
    }

    
    public static int getSkillLevel(UnitType unitType) {
        if (unitType.hasSkill()) {
            return unitType.getSkill();
        }

        return 0;
    }
    
    public static Comparator<Unit> getSkillLevelComparator(){
        if(skillLevelComp != null){
            return skillLevelComp;
        }
        
        
        
        skillLevelComp = new Comparator<Unit>(){
            public int compare(Unit u1,Unit u2){
                if(u1.getSkillLevel() < u2.getSkillLevel()){
                    return -1;
                }
                if(u1.getSkillLevel() > u2.getSkillLevel()){
                    return 1;
                }
                return 0;
            }
        };
        
        return skillLevelComp;
    }

    
    public int getTurnsOfTraining() {
        return turnsOfTraining;
    }

    
    public void setTurnsOfTraining(int turnsOfTraining) {
        this.turnsOfTraining = turnsOfTraining;
    }

    
    public int getExperience() {
        return experience;
    }

    
    public void modifyExperience(int value) {
        experience += value;
    }

    
    public boolean hasAbility(String id) {
        Set<Ability> result = new HashSet<Ability>();
        
        result.addAll(unitType.getFeatureContainer().getAbilitySet(id));
        
        result.addAll(getOwner().getFeatureContainer()
                      .getAbilitySet(id, unitType, getGame().getTurn()));
        
        for (EquipmentType equipmentType : equipment.keySet()) {
            result.addAll(equipmentType.getFeatureContainer().getAbilitySet(id));
        }
        return FeatureContainer.hasAbility(result);
    }


    
    public Set<Modifier> getModifierSet(String id) {
        Set<Modifier> result = new HashSet<Modifier>();
        
        result.addAll(unitType.getFeatureContainer().getModifierSet(id));
        
        result.addAll(getOwner().getFeatureContainer()
                      .getModifierSet(id, unitType, getGame().getTurn()));
        
        for (EquipmentType equipmentType : equipment.keySet()) {
            result.addAll(equipmentType.getFeatureContainer().getModifierSet(id));
        }
        return result;
    }
    
    
    public void addFeature(Feature feature) {
        throw new UnsupportedOperationException("Can not add Feature to Unit directly!");
    }

    
    public boolean canBeStudent(Unit teacher) {
        return canBeStudent(unitType, teacher.unitType);
    }

    
    public static boolean canBeStudent(UnitType typeStudent, UnitType typeTeacher) {
        return getUnitTypeTeaching(typeTeacher, typeStudent) != null;
    }

    
    public final Unit getStudent() {
        return student;
    }

    
    public final void setStudent(final Unit newStudent) {
    	Unit oldStudent = this.student;
    	if(oldStudent == newStudent){
    		return;
    	}
    	
        if (newStudent == null) {
        	this.student = null;
        	if(oldStudent != null && oldStudent.getTeacher() == this){
        		oldStudent.setTeacher(null);
        	}
        } else if (newStudent.getColony() != null &&
                   newStudent.getColony() == getColony() &&
                   newStudent.canBeStudent(this)) {
        	if(oldStudent != null && oldStudent.getTeacher() == this){
        		oldStudent.setTeacher(null);
        	}
        	this.student = newStudent;
        	newStudent.setTeacher(this);
        } else {
            throw new IllegalStateException("unit can not be student: " + newStudent.getName());
        }
    }

    
    public final Unit getTeacher() {
        return teacher;
    }

    
    public final void setTeacher(final Unit newTeacher) {
    	Unit oldTeacher = this.teacher;
    	if(newTeacher == oldTeacher){
    		return;
    	}
    	
        if (newTeacher == null) {
        	this.teacher = null;
        	if(oldTeacher != null && oldTeacher.getStudent() == this){
        		oldTeacher.setStudent(null);
        	}
        } else {
            UnitType skillTaught = FreeCol.getSpecification().getUnitType(newTeacher.getType().getSkillTaught());
            if (newTeacher.getColony() != null &&
                newTeacher.getColony() == getColony() &&
                getColony().canTrain(skillTaught)) {
            	if(oldTeacher != null && oldTeacher.getStudent() == this){
            		oldTeacher.setStudent(null);
            	}
            	this.teacher = newTeacher;
            	this.teacher.setStudent(this);
            } else {
                throw new IllegalStateException("unit can not be teacher: " + newTeacher.getName());
            }
        }
    }

    
    public Building getWorkLocation() {
        if (getLocation() instanceof Building) {
            return ((Building) getLocation());
        }
        return null;
    }

    
    public ColonyTile getWorkTile() {
        if (getLocation() instanceof ColonyTile) {
            return ((ColonyTile) getLocation());
        }
        return null;
    }

    
    public GoodsType getWorkType() {
        if (getLocation() instanceof Building) {
            return ((Building) getLocation()).getGoodsOutputType();
        }
        return workType;
    }

    
    public void setWorkType(GoodsType type) {
        if (type == null) {
            throw new IllegalStateException("GoodsType must not be 'null'.");
        } else if (workType != type) {
            experience = 0;
            if (type.isFarmed()) {
                GoodsType oldWorkType = workType;
                workType = type;
                if (getLocation() instanceof ColonyTile) {
                    ColonyTile colonyTile = (ColonyTile) getLocation();
                    colonyTile.firePropertyChange(oldWorkType.getId(), 
                                                  colonyTile.getProductionOf(this, oldWorkType), null);
                    colonyTile.firePropertyChange(type.getId(), 
                                                  null, colonyTile.getProductionOf(this, type));
                }
            }
        }
    }

    
    public TileImprovement getWorkImprovement() {
        return workImprovement;
    }

    
    public void setWorkImprovement(TileImprovement imp) {
        workImprovement = imp;
    }

    
    public Location getDestination() {
        return destination;
    }

    
    public void setDestination(Location newDestination) {
        this.destination = newDestination;
    }

    
    public PathNode findPath(Tile end) {
        if (getTile() == null) {
            logger.warning("getTile() == null for " + getName() + " at location: " + getLocation());
        }
        return findPath(getTile(), end);
    }

    
    public PathNode findPath(Tile start, Tile end) {
        Location dest = getDestination();
        setDestination(end);
        PathNode path = getGame().getMap().findPath(this, start, end);
        setDestination(dest);
        return path;
    }

    
    public int getTurnsToReach(Tile end) {
        return getTurnsToReach(getTile(), end);
    }

    
    public int getTurnsToReach(Tile start, Tile end) {

        if (start == end) {
            return 0;
        }

        if (isOnCarrier()) {
            Location dest = getDestination();
            setDestination(end);
            PathNode p = getGame().getMap().findPath(this, start, end, (Unit) getLocation());
            setDestination(dest);
            if (p != null) {
                return p.getTotalTurns();
            }
        }
        PathNode p = findPath(start, end);
        if (p != null) {
            return p.getTotalTurns();
        }

        return Integer.MAX_VALUE;
    }
    
    
    public int getTurnsToReach(Location destination) {
        if (destination == null) {
            logger.log(Level.WARNING, "destination == null", new Throwable());
        }
        
        if (getTile() == null) {
            if (destination.getTile() == null) {
                return 0;
            }
            final PathNode p;
            if (isOnCarrier()) {
                final Unit carrier = (Unit) getLocation();
                p = getGame().getMap().findPath(this, (Tile) carrier.getEntryLocation(), destination.getTile(), carrier);
            } else {
                
                p = getGame().getMap().findPath((Tile) getOwner().getEntryLocation(), destination.getTile(), 
                                                Map.PathType.BOTH_LAND_AND_SEA);
            }
            if (p != null) {
                return p.getTotalTurns();
            } else {
                return Integer.MAX_VALUE;
            }
        }
        
        if (destination.getTile() == null) {
            
            return 10;
        }
        
        return getTurnsToReach(destination.getTile());
    }

    
    public int getMoveCost(Tile target) {
        return getMoveCost(getTile(), target, getMovesLeft());
    }

    
    public int getMoveCost(Tile from, Tile target, int ml) {
        
        

        int cost = target.getMoveCost(from);

        
        
        if (cost > ml) {
            if ((ml + 2 >= getInitialMovesLeft() || cost <= ml + 2 || target.getSettlement()!=null) && ml != 0) {
                return ml;
            }

            return cost;
        } else if (isNaval() && from.isLand() && from.getSettlement() == null) {
            
            return ml;
        } else {
            return cost;
        }
    }

    
    public boolean canTradeWith(Settlement settlement) {
        return canCarryGoods()
            && goodsContainer.getGoodsCount() > 0
            && getOwner().getStance(settlement.getOwner()) != Stance.WAR
            && (settlement instanceof IndianSettlement
                || hasAbility("model.ability.tradeWithForeignColonies"));
    }

    
    public MoveType getMoveType(Direction direction) {
        if (getTile() == null) {
            throw new IllegalStateException("getTile() == null");
        }

        Tile target = getGame().getMap().getNeighbourOrNull(direction, getTile());

        return getMoveType(target);
    }

    
    public MoveType getMoveType(Tile target) {
        return getMoveType(getTile(), target, getMovesLeft());
    }
    
    
    public MoveType getMoveType(Tile from, Tile target, int ml) {
        return getMoveType(from, target, ml, false);
    }

    
    public MoveType getMoveType(Tile from, Tile target, int ml, boolean ignoreEnemyUnits) {
        MoveType move = getSimpleMoveType(from, target, ignoreEnemyUnits);

        if (move.isLegal()) {
            if (ml <= 0
                || (from != null && getMoveCost(from, target, ml) > ml)) {
                move = MoveType.MOVE_NO_MOVES;
            }
        }
        return move;
    }

    
    public MoveType getSimpleMoveType(Tile from, Tile target, boolean ignoreEnemyUnits) {
        return (isNaval())
            ? getNavalMoveType(from, target, ignoreEnemyUnits)
            : getLandMoveType(from, target, ignoreEnemyUnits);
    }

    
    public MoveType getSimpleMoveType(Tile target) {
        return getSimpleMoveType(getTile(), target, false);
    }

    
    public MoveType getSimpleMoveType(Direction direction) {
        if (getTile() == null) {
            throw new IllegalStateException("getTile() == null");
        }

        Tile target = getGame().getMap().getNeighbourOrNull(direction, getTile());

        return getSimpleMoveType(target);
    }

    
    private MoveType getNavalMoveType(Tile from, Tile target, boolean ignoreEnemyUnits) {
        if (target == null) {
            return (getOwner().canMoveToEurope()) ? MoveType.MOVE_HIGH_SEAS
                : MoveType.MOVE_NO_EUROPE;
        } else if (isUnderRepair()) {
            return MoveType.MOVE_NO_REPAIR;
        }

        if (target.isLand()) {
            Settlement settlement = target.getSettlement();
            if (settlement == null) {
                return MoveType.MOVE_NO_ACCESS_LAND;
            } else if (settlement.getOwner() == getOwner()) {
                return MoveType.MOVE;
            } else if (canTradeWith(settlement)) {
                return MoveType.ENTER_SETTLEMENT_WITH_CARRIER_AND_GOODS;
            } else {
                return MoveType.MOVE_NO_ACCESS_SETTLEMENT;
            }
        } else { 
            Unit defender = target.getFirstUnit();
            if (defender != null && !ignoreEnemyUnits
                && defender.getOwner() != getOwner()) {
                return (isOffensiveUnit()) ? MoveType.ATTACK
                    : MoveType.MOVE_NO_ATTACK_CIVILIAN;
            } else if (target.canMoveToEurope() && getOwner().canMoveToEurope()) {
                return MoveType.MOVE_HIGH_SEAS;
            } else {
                return MoveType.MOVE;
            }
        }
    }

    
    private boolean perhapsLearnFromSettlement(IndianSettlement settlement) {
        
        if (settlement.getLearnableSkill() == null
            && settlement.hasBeenVisited(getOwner())) {
            return false;
        }
        
        
        
        
        return getType().canBeUpgraded(scoutSkill, ChangeType.NATIVES);
    }
    private static UnitType scoutSkill
        = FreeCol.getSpecification().getUnitType("model.unit.seasonedScout");

    
    private MoveType getLandMoveType(Tile from, Tile target, boolean ignoreEnemyUnits) {
        if (target == null) {
            return MoveType.MOVE_ILLEGAL;
        }

        Unit defender = target.getFirstUnit();
        Settlement settlement = target.getSettlement();

        if (target.isLand()) {
            if (settlement != null) {
                if (settlement.getOwner() == getOwner()) {
                    return MoveType.MOVE;
                } else if (canTradeWith(settlement)) {
                    return MoveType.ENTER_SETTLEMENT_WITH_CARRIER_AND_GOODS;
                } else {
                    if (isColonist()) {
                        if (settlement instanceof Colony) {
                            switch (getRole()) {
                            case DEFAULT: case PIONEER: case MISSIONARY:
                                break;
                            case SCOUT:
                                return MoveType.ENTER_FOREIGN_COLONY_WITH_SCOUT;
                            case SOLDIER: case DRAGOON:
                                break;
                            }
                        } else if (settlement instanceof IndianSettlement) {
                            switch (getRole()) {
                            case DEFAULT: case PIONEER:
                                if (perhapsLearnFromSettlement((IndianSettlement) settlement)) {
                                    return MoveType.ENTER_INDIAN_VILLAGE_WITH_FREE_COLONIST;
                                }
                                break;
                            case MISSIONARY:
                                if (((IndianSettlement) settlement).getAlarm(getOwner()) == null) {
                                    return MoveType.MOVE_ILLEGAL;
                                } else {
                                    return MoveType.ENTER_INDIAN_VILLAGE_WITH_MISSIONARY;
                                }
                            case SCOUT:
                                return MoveType.ENTER_INDIAN_VILLAGE_WITH_SCOUT;
                            case SOLDIER: case DRAGOON:
                                break;
                            }
                        }
                    }
                    return (isOffensiveUnit()) ? MoveType.ATTACK
                        : MoveType.MOVE_NO_ACCESS_SETTLEMENT;
                }
            } else if (defender != null
                    && defender.getOwner() != getOwner() 
                    && !ignoreEnemyUnits) {
                if (defender.isNaval()) {
                    return MoveType.MOVE_NO_ACCESS_BEACHED;
                } else if (from != null && !from.isLand()) {
                    return MoveType.MOVE_NO_ATTACK_MARINE;
                }
                return (isOffensiveUnit()) ? MoveType.ATTACK
                    : MoveType.MOVE_NO_ATTACK_CIVILIAN;
            } else if (target.hasLostCityRumour() && getOwner().isEuropean()) {
                
                
                return MoveType.EXPLORE_LOST_CITY_RUMOUR;
            } else {
                return MoveType.MOVE;
            }
        } else { 
            if (defender == null || defender.getOwner() != getOwner()) {
                return MoveType.MOVE_NO_ACCESS_EMBARK;
            }
            for (Unit unit : target.getUnitList()) {
                if (unit.getSpaceLeft() >= getSpaceTaken()) {
                    return MoveType.EMBARK;
                }
            }
            return MoveType.MOVE_NO_ACCESS_FULL;
        }
    }

    
    public int getMovesLeft() {
        return movesLeft;
    }

    
    public void setMovesLeft(int movesLeft) {
        if (movesLeft < 0) {
            movesLeft = 0;
        }

        this.movesLeft = movesLeft;
    }

    
    public int getSpaceTaken() {
        return unitType.getSpaceTaken();
    }

    
    public int getLineOfSight() {
        float line = unitType.getLineOfSight();
        Set<Modifier> modifierSet = getModifierSet("model.modifier.lineOfSightBonus");
        if (getTile() != null && getTile().getType() != null) {
            modifierSet.addAll(getTile().getType().getFeatureContainer()
                               .getModifierSet("model.modifier.lineOfSightBonus",
                                               unitType, getGame().getTurn()));
        }
        return (int) FeatureContainer.applyModifierSet(line, getGame().getTurn(), modifierSet);
    }

    
    public void move(Direction direction) {
        MoveType moveType = getMoveType(direction);

        
        if (!moveType.isProgress()) {
            throw new IllegalStateException("Illegal move requested: " + moveType
                                            + " while trying to move a " + getName()
                                            + " located at " + getTile().getPosition().toString()
                                            + ". Direction: " + direction
                                            + " Moves Left: " + getMovesLeft());
        }

        Tile newTile = getGame().getMap().getNeighbourOrNull(direction, getTile());
        if (newTile != null) {
            setState(UnitState.ACTIVE);
            setStateToAllChildren(UnitState.SENTRY);
            int moveCost = getMoveCost(newTile);
            setMovesLeft(getMovesLeft() - moveCost);
            setLocation(newTile);
            activeAdjacentSentryUnits(newTile);

            
            
            if (newTile.canMoveToEurope()) {
                setAlreadyOnHighSea(true);
            } else {
                setAlreadyOnHighSea(false);
            }

        } else {
            throw new IllegalStateException("Illegal move requested - no target tile!");
        }
    }

    
    public void activeAdjacentSentryUnits(Tile tile) {
        Map map = getGame().getMap();
        Iterator<Position> it = map.getAdjacentIterator(tile.getPosition());
        while (it.hasNext()) {
            Iterator<Unit> unitIt = map.getTile(it.next()).getUnitIterator();
            while (unitIt.hasNext()) {
                Unit unit = unitIt.next();
                if (unit.getState() == UnitState.SENTRY && unit.getOwner() != getOwner()) {
                    unit.setState(UnitState.ACTIVE);
                }
            }
        }
    }

    
    public boolean isOnCarrier(){
    	return(this.getLocation() instanceof Unit);
    }
    
    
    public void setStateToAllChildren(UnitState state) {
        if (canCarryUnits()) {
            for (Unit u : getUnitList())
                u.setState(state);
        }
    }

    
    private void spendAllMoves() {
        if (getColony() != null && getMovesLeft() < getInitialMovesLeft())
            setMovesLeft(0);
    }

    
    public void add(Locatable locatable) {
        if (locatable instanceof Unit && canCarryUnits()) {
            Unit unit = (Unit) locatable;
            if (getSpaceLeft() < unit.getSpaceTaken()) {
                throw new IllegalStateException("Not enough space for " + unit.getName()
                                                + " left on " + getName());
            }
            if (units.contains(locatable)) {
            	logger.warning("Tried to add a 'Locatable' already in the carrier.");
            	return;
            }
            
            if (units.equals(Collections.emptyList())) {
            	units = new ArrayList<Unit>();
            } 
            units.add(unit);
            unit.setState(UnitState.SENTRY);
            firePropertyChange(CARGO_CHANGE, null, locatable);
            spendAllMoves();
        } else if (locatable instanceof Goods && canCarryGoods()) {
            Goods goods = (Goods) locatable;
            if (getLoadableAmount(goods.getType()) < goods.getAmount()){
                throw new IllegalStateException("Not enough space for " + goods.toString()
                                                + " left on " + getName());
            }
            goodsContainer.addGoods(goods);
            firePropertyChange(CARGO_CHANGE, null, locatable);
            spendAllMoves();
        } else {
            throw new IllegalStateException("Tried to add a 'Locatable' to a non-carrier unit.");
        }
    }

    
    public void remove(Locatable locatable) {
        if (locatable == null) {
            throw new IllegalArgumentException("Locatable must not be 'null'.");
        } else if (locatable instanceof Unit && canCarryUnits()) {
            units.remove(locatable);
            firePropertyChange(CARGO_CHANGE, locatable, null);
            spendAllMoves();
        } else if (locatable instanceof Goods && canCarryGoods()) {
            goodsContainer.removeGoods((Goods) locatable);
            firePropertyChange(CARGO_CHANGE, locatable, null);
            spendAllMoves();
        } else {
            logger.warning("Tried to remove a 'Locatable' from a non-carrier unit.");
        }
    }

    
    public boolean contains(Locatable locatable) {
        if (locatable instanceof Unit && canCarryUnits()) {
            return units.contains(locatable);
        } else if (locatable instanceof Goods && canCarryGoods()) {
            return goodsContainer.contains((Goods) locatable);
        } else {
            return false;
        }
    }

    
    public boolean canAdd(Locatable locatable) {
        if (locatable == this) {
            return false;
        } else if (locatable instanceof Unit && canCarryUnits()) {
            return getSpaceLeft() >= locatable.getSpaceTaken();
        } else if (locatable instanceof Goods) {
            Goods g = (Goods) locatable;
            return (getLoadableAmount(g.getType()) >= g.getAmount());
        } else {
            return false;
        }
    }

    
    public int getLoadableAmount(GoodsType type) {
        if (canCarryGoods()) {
            int result = getSpaceLeft() * 100;
            int count = getGoodsContainer().getGoodsCount(type) % 100;
            if (count > 0 && count < 100) {
                result += (100 - count);
            }
            return result;
        } else {
            return 0;
        }
    }

    
    public int getUnitCount() {
        return units.size();
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

    
    public boolean isVisibleTo(Player player) {
    	if(player == getOwner()){
    		return true;
    	}
    	
    	Tile unitTile = getTile();
    	if(unitTile == null){
    		return false;
    	}
    	
    	if(!player.canSee(unitTile)){
    		return false;
    	}
    	
    	Settlement settlement = unitTile.getSettlement();
    	if(settlement != null && settlement.getOwner() != player){
    		return false;
    	}

    	if(isOnCarrier() && ((Unit) getLocation()).getOwner() != player){
    		return false;
    	}
    	
    	return true;
    }

    
    public Iterator<Unit> getUnitIterator() {
        return new ArrayList<Unit>(units).iterator();
    }

    public List<Unit> getUnitList() {
        return units;
    }

    
    public Iterator<Goods> getGoodsIterator() {
        if (canCarryGoods()) {
            return goodsContainer.getGoodsIterator();
        } else {
            return EmptyIterator.getInstance();
        }
    }
    
    
    public List<Goods> getGoodsList() {
        if (canCarryGoods()) {
            return goodsContainer.getGoods();
        } else {
            return Collections.emptyList();
        }
    }

    public GoodsContainer getGoodsContainer() {
        return goodsContainer;
    }

    
    public void work(WorkLocation workLocation) {
        
        
        if (workLocation.getColony() != this.getColony()) {
            throw new IllegalStateException("Can only set a 'Unit'  to a 'WorkLocation' that is in the same 'Colony'.");
        }
        if (workLocation.getTile().getOwner() != getOwner()) {
            throw new IllegalStateException("Can only set a 'Unit' to a 'WorkLocation' owned by the same player.");
        }
        setState(UnitState.IN_COLONY);
        setLocation(workLocation);
    }

    
    public void work(TileImprovement improvement) {
        
        if (!hasAbility("model.ability.improveTerrain")) {
            throw new IllegalStateException("Only 'Pioneers' can perform TileImprovement.");
        } else if (improvement == null){
            
            throw new IllegalArgumentException("Improvement must not be 'null'.");
        } else {
            TileImprovementType impType = improvement.getType();

            
            if (impType == null) {
                throw new IllegalArgumentException("ImprovementType must not be 'null'.");
            } else if (impType.isNatural()) {
                throw new IllegalArgumentException("ImprovementType must not be natural.");
            } else if (!impType.isTileTypeAllowed(getTile().getType())) {
                
                throw new IllegalArgumentException(impType.getName() + " not allowed on "
                                                   + getTile().getType().getName());
            } else {
                

                
                TileImprovement oldImprovement = getTile().findTileImprovementType(impType);
                if (oldImprovement == null) {
                    
                    if (!impType.isWorkerAllowed(this)) {
                        throw new IllegalArgumentException(getName() + " not allowed to perform "
                                                           + improvement.getName());
                    }
                } else {
                    
                    if (!oldImprovement.isWorkerAllowed(this)) {
                        throw new IllegalArgumentException(getName() + " not allowed to perform "
                                                           + improvement.getName());
                    }
                }
            }
        }
        
        setWorkImprovement(improvement);
        setState(UnitState.IMPROVING);
        
    }
    
    
    public void setLocationNoUpdate(Location newLocation) {
        location = newLocation;
    }

    
    public void setLocation(Location newLocation) {

        Colony oldColony = this.getColony();
        Location oldLocation = location;
        
        if (location != null) {
            location.remove(this);
        }
        location = newLocation;
        if (newLocation != null) {
            newLocation.add(this);
        }

        
        if (oldLocation instanceof WorkLocation) {
            if (!(newLocation instanceof WorkLocation)) {
                getOwner().modifyScore(-getType().getScoreValue());
                if (oldColony != null) {
                    
                    oldColony.updatePopulation(-1);
                    setState(UnitState.ACTIVE);
                }
            }
        } else if (newLocation instanceof WorkLocation) {
            
            UnitType newType = unitType.getUnitTypeChange(ChangeType.ENTER_COLONY, owner);
            if (newType == null) {
                getOwner().modifyScore(getType().getScoreValue());
            } else {
                getOwner().modifyScore(-getType().getScoreValue());
                setType(newType);
                getOwner().modifyScore(getType().getScoreValue() * 2);
            }
            newLocation.getColony().updatePopulation(1);
            if (getState() != UnitState.IN_COLONY) {
                logger.warning("Adding unit " + getId() + " with state==" + getState()
                               + " (should be IN_COLONY) to WorkLocation in "
                               + newLocation.getColony().getName() + ". Fixing: ");
                setState(UnitState.IN_COLONY);
            }
        }
                
        
        if (!Utils.equals(oldColony, getColony())){
            setTurnsOfTraining(0);
        }

        if (student != null &&
            !(newLocation instanceof Building &&
              ((Building) newLocation).getType().hasAbility("model.ability.teach"))) {
            
            student.setTeacher(null);
            student = null;
        }

        if (newLocation instanceof WorkLocation) {
            removeAllEquipment(false);
        } else if (teacher != null) {
            teacher.setStudent(null);
            teacher = null;
        }

        if (!getOwner().isIndian()) {
            getOwner().setExplored(this);
        }
    }

    
    public void setIndianSettlement(IndianSettlement indianSettlement) {
        if (this.indianSettlement != null) {
            this.indianSettlement.removeOwnedUnit(this);
        }

        this.indianSettlement = indianSettlement;

        if (indianSettlement != null) {
            indianSettlement.addOwnedUnit(this);
        }
    }

    
    public IndianSettlement getIndianSettlement() {
        return indianSettlement;
    }

    
    public Location getLocation() {
        return location;
    }

    
    public void putOutsideColony() {
        if (getTile().getSettlement() == null) {
            throw new IllegalStateException();
        }

        if (getState() == UnitState.IN_COLONY) {
            setState(UnitState.ACTIVE);
        }

        setLocation(getTile());
    }

    
    public boolean canBeEquippedWith(EquipmentType equipmentType) {
        for (Entry<String, Boolean> entry : equipmentType.getUnitAbilitiesRequired().entrySet()) {
            if (hasAbility(entry.getKey()) != entry.getValue()) {
                return false;
            }
        }
        if (!equipmentType.getLocationAbilitiesRequired().isEmpty()) {
            if (isInEurope()) {
                return true;
            } else {
                Colony colony = getColony();
                if (colony == null) {
                    return false;
                } else {
                    for (Entry<String, Boolean> entry : equipmentType.getLocationAbilitiesRequired().entrySet()) {
                        if (colony.getFeatureContainer().hasAbility(entry.getKey()) != entry.getValue()) {
                            return false;
                        }
                    }
                }
            }
        }
        if (equipment.getCount(equipmentType) >= equipmentType.getMaximumCount()) {
            return false;
        }
        return true;
    }

    
    public void equipWith(EquipmentType equipmentType) {
        equipWith(equipmentType, 1, false);
    }

    
    public void equipWith(EquipmentType equipmentType, int amount) {
        equipWith(equipmentType, amount, false);
    }

    
    public void equipWith(EquipmentType equipmentType, boolean asResultOfCombat) {
        equipWith(equipmentType, 1, asResultOfCombat);
    }

    
    public void equipWith(EquipmentType equipmentType, int amount, boolean asResultOfCombat) {
        if (equipmentType == null) {
            throw new IllegalArgumentException("EquipmentType is 'null'.");
        } else if (amount < 1) {
            throw new IllegalArgumentException("Amount must be a positive integer.");
        }
        if (!canBeEquippedWith(equipmentType)) {
            logger.fine("Unable to equip unit " + getId() + " with " + equipmentType.getName());
            return;
        }
        if (!(asResultOfCombat || 
              (getColony() != null && getColony().canBuildEquipment(equipmentType)) ||
              (isInEurope() && getOwner().getEurope().canBuildEquipment(equipmentType)) ||
              (getIndianSettlement() != null))) {
            logger.fine("Unable to build equipment " + equipmentType.getName());
            return;
        }
        if (!asResultOfCombat) {
            setMovesLeft(0);
            if (getColony() != null) {
                for (AbstractGoods goods : equipmentType.getGoodsRequired()) {
                    int requiredAmount = amount * goods.getAmount();
                    if(getColony().getGoodsCount(goods.getType()) < requiredAmount){
                        throw new IllegalStateException("Not enough goods to equip");
                    }
                    getColony().removeGoods(goods.getType(), requiredAmount);
                }
            } else if (isInEurope()) {
                for (AbstractGoods goods : equipmentType.getGoodsRequired()) {
                    int requiredAmount = amount * goods.getAmount();
                    getOwner().getMarket().buy(goods.getType(), requiredAmount, getOwner());
                }
            } else if(getIndianSettlement() != null) {
            	for (AbstractGoods goods : equipmentType.getGoodsRequired()) {            		
                    int requiredAmount = amount * goods.getAmount();
                    if(getIndianSettlement().getGoodsCount(goods.getType()) < requiredAmount){
                        throw new IllegalStateException("Not enough goods to equip");
                    }
                    getIndianSettlement().removeGoods(goods.getType(), requiredAmount);
                }
            }
        }
        equipment.incrementCount(equipmentType, amount);
        Set<EquipmentType> equipmentTypes = equipment.keySet();
        
        
        Set<EquipmentType> eqLst = new HashSet<EquipmentType>(equipmentTypes);
        for (EquipmentType oldEquipment : eqLst) {
            if (!oldEquipment.isCompatibleWith(equipmentType)) {
                dumpEquipment(oldEquipment, equipment.getCount(oldEquipment), asResultOfCombat);
                equipmentTypes.remove(oldEquipment);
            }
        }
        setRole();
    }
    
    public void removeEquipment(EquipmentType equipmentType) {
        int amount = getEquipmentCount(equipmentType);
        
        removeEquipment(equipmentType, amount, false);
    }

    
    public void removeEquipment(EquipmentType equipmentType, int amount) {
        removeEquipment(equipmentType, amount, false);
    }

    
    public void removeEquipment(EquipmentType equipmentType, int amount, boolean asResultOfCombat) {
        dumpEquipment(equipmentType, amount, asResultOfCombat);
        equipment.incrementCount(equipmentType, -amount);
        if (asResultOfCombat) {
            
            setMovesLeft(Math.min(movesLeft, getInitialMovesLeft()));
        } else {
            setMovesLeft(0);
        }
        setRole();
    }

    public void removeAllEquipment(boolean asResultOfCombat) {
        for (EquipmentType equipmentType : equipment.keySet()) {
            dumpEquipment(equipmentType, equipment.getCount(equipmentType), asResultOfCombat);
        }
        equipment.clear();
        setMovesLeft(0);
        setRole();
    }

    private void dumpEquipment(EquipmentType equipmentType, int amount, boolean asResultOfCombat) {
        if (!asResultOfCombat) {
            
            if (getColony() != null) {
                for (AbstractGoods goods : equipmentType.getGoodsRequired()) {
                    getColony().addGoods(goods.getType(), amount * goods.getAmount());
                }
            } else if (isInEurope()) {
                for (AbstractGoods goods : equipmentType.getGoodsRequired()) {
                    getOwner().getMarket().sell(goods.getType(), amount * goods.getAmount(), getOwner());
                }
            }
        }
        
    }

    
    public int getEquipmentCount(EquipmentType equipmentType) {
        return equipment.getCount(equipmentType);
    }

    
    public void switchEquipmentWith(Unit unit){
        if(!isColonist() || !unit.isColonist()){
            throw new IllegalArgumentException("Both units need to be colonists to switch equipment");
        }
        
        if(getTile() != unit.getTile()){
            throw new IllegalStateException("Units can only switch equipment in the same location");
        }
        
        if(getTile().getSettlement() == null){
            throw new IllegalStateException("Units can only switch equipment in a settlement");
        }
        
        List<EquipmentType> equipList = new ArrayList<EquipmentType>(getEquipment().keySet());
        List<EquipmentType> otherEquipList = new ArrayList<EquipmentType>(unit.getEquipment().keySet());
        removeAllEquipment(false);
        unit.removeAllEquipment(false);
        for(EquipmentType equip : otherEquipList){
            equipWith(equip);
        }
        for(EquipmentType equip : equipList){
            unit.equipWith(equip);
        }
    }
    
    
    public boolean isInEurope() {
        if (location instanceof Unit) {
            return ((Unit) location).isInEurope();
        } else {
            return getLocation() instanceof Europe && 
                getState() != UnitState.TO_EUROPE &&
                getState() != UnitState.TO_AMERICA;
        }
    }

    
    public boolean isCarrier() {
        return isCarrier(unitType);
    }

    
    public static boolean isCarrier(UnitType unitType) {
        return unitType.canCarryGoods() ||
            unitType.canCarryUnits();
    }

    
    public Player getOwner() {
        return owner;
    }

    
    public String getApparentOwnerName() {
        return (hasAbility("model.ability.piracy")) ? Player.UNKNOWN_ENEMY
            : owner.getNationAsString();
    }

    
    public void setOwner(Player owner) {
        Player oldOwner = this.owner;
        
        
        if(oldOwner == owner){
            return;
        }
        
        if(oldOwner == null){
            logger.warning("Unit " + getId() + " had no previous owner");
        }

        
        this.owner = owner;
        
        
        for (Unit unit : getUnitList()) {
            unit.setOwner(owner);
        }
                
        if(oldOwner != null){
            oldOwner.removeUnit(this);
            oldOwner.modifyScore(-getType().getScoreValue());
            
            if(!isOnCarrier()){
                oldOwner.invalidateCanSeeTiles();
            }
        }
        owner.setUnit(this);
        owner.modifyScore(getType().getScoreValue());

        
        if(!isOnCarrier()){
            getOwner().setExplored(this);
        }

        if (getGame().getFreeColGameObjectListener() != null) {
            getGame().getFreeColGameObjectListener().ownerChanged(this, oldOwner, owner);
        }
    }

    
    public void setType(UnitType newUnitType) {
        if (newUnitType.isAvailableTo(owner)) {
            if (unitType == null) {
                owner.modifyScore(newUnitType.getScoreValue());
            } else {
                owner.modifyScore(newUnitType.getScoreValue() - unitType.getScoreValue());
            }
            this.unitType = newUnitType;
            naval = unitType.hasAbility("model.ability.navalUnit");
            if (getMovesLeft() > getInitialMovesLeft()) {
                setMovesLeft(getInitialMovesLeft());
            }
            hitpoints = unitType.getHitPoints();
            if (getTeacher() != null && !canBeStudent(getTeacher())) {
                getTeacher().setStudent(null);
                setTeacher(null);
            }
        } else {
            
            logger.warning(newUnitType.getName() + " is not available to " + owner.getPlayerType() +
                           " player " + owner.getName());
        }

    }

    
    public boolean isArmed() {
    	if(getOwner().isIndian()){
            return equipment.containsKey(FreeCol.getSpecification().getEquipmentType("model.equipment.indian.muskets"));
    	}
        return equipment.containsKey(FreeCol.getSpecification().getEquipmentType("model.equipment.muskets"));
    }

    public boolean isMounted() {
    	if(getOwner().isIndian()){
            return equipment.containsKey(FreeCol.getSpecification().getEquipmentType("model.equipment.indian.horses"));
    	}
        return equipment.containsKey(FreeCol.getSpecification().getEquipmentType("model.equipment.horses"));
    }

    
    public String getName() {
        String completeName = "";
        String customName = "";
        if (name != null) {
            customName = " " + name + " ";
        }

        
        if (canCarryTreasure()) {
            completeName = Messages.message(getType().getId() + ".gold", "%gold%",
                                    String.valueOf(getTreasureAmount()));
        } else if ((equipment == null || equipment.isEmpty()) &&
                   getType().getDefaultEquipmentType() != null) {
            completeName = getName(getType(), getRole()) + " (" +
                Messages.message(getType().getDefaultEquipmentType().getId() + ".none") + ")";
        } else {
            completeName = getName(getType(), getRole());
        }

        
        int index = completeName.lastIndexOf(" (");
        if (index < 0) {
            completeName = completeName + customName;
        } else {
            completeName = completeName.substring(0, index) + customName + 
                completeName.substring(index);
        }

        return completeName;
    }

    
    public String getEquipmentLabel() {
        if (equipment != null && !equipment.isEmpty()) {
            List<String> equipmentStrings = new ArrayList<String>();
            for (java.util.Map.Entry<EquipmentType, Integer> entry : equipment.getValues().entrySet()) {
                EquipmentType type = entry.getKey();
                int amount = entry.getValue().intValue();
                if (type.getGoodsRequired().isEmpty()) {
                    equipmentStrings.add(Messages.message("model.goods.goodsAmount",
                                                          "%goods%", type.getName(),
                                                          "%amount%", Integer.toString(amount)));
                } else {
                    for (AbstractGoods goods : type.getGoodsRequired()) {
                        equipmentStrings.add(Messages.message("model.goods.goodsAmount",
                                                              "%goods%", goods.getType().getName(),
                                                              "%amount%", Integer.toString(amount * goods.getAmount())));
                    }
                }
            }
            return Utils.join("/", equipmentStrings.toArray(new String[equipmentStrings.size()]));
        } else {
            return null;
        }
    }


    
    public static String getName(UnitType someType, Role someRole) {
        String key = someRole.toString().toLowerCase();
        if (someRole == Role.DEFAULT) {
            key = "name";
        }
        String messageID = someType.getId() +  "." + key;
        if (Messages.containsKey(messageID)) {
            return Messages.message(messageID);
        } else {
            return Messages.message("model.unit." + key + ".name", "%unit%", someType.getName());
        }
    }

    
    public void setName(String newName) {
        this.name = newName;
    }

    
    public int getInitialMovesLeft() {
        return (int) FeatureContainer.applyModifierSet(unitType.getMovement(), getGame().getTurn(),
                                                       getModifierSet("model.modifier.movementBonus"));
    }

    
    public void setHitpoints(int hitpoints) {
        this.hitpoints = hitpoints;
        if (hitpoints >= unitType.getHitPoints()) {
            setState(UnitState.ACTIVE);
        }
    }

    
    public int getHitpoints() {
        return hitpoints;
    }

    
    public boolean isUnderRepair() {
        return (hitpoints < unitType.getHitPoints());
    }

    
    public void sendToRepairLocation(Location l) {
        setLocation(l);
        setState(UnitState.ACTIVE);
        setMovesLeft(0);
    }

    
    public String toString() {
        return getName() + " " + getMovesAsString();
    }

    public String getMovesAsString() {
        String moves = "";
        if (getMovesLeft() % 3 == 0 || getMovesLeft() / 3 > 0) {
            moves += Integer.toString(getMovesLeft() / 3);
        }

        if (getMovesLeft() % 3 != 0) {
            if (getMovesLeft() / 3 > 0) {
                moves += " ";
            }

            moves += "(" + Integer.toString(getMovesLeft() - (getMovesLeft() / 3) * 3) + "/3) ";
        }

        moves += "/" + Integer.toString(getInitialMovesLeft() / 3);
        return moves;
    }

    
    public boolean isNaval() {
        return naval;
    }

    
    public String getOccupationIndicator() {

        if (getDestination() != null) {
            if(getTradeRoute() != null)
                return Messages.message("model.unit.occupation.inTradeRoute");
            else
                return Messages.message("model.unit.occupation.goingSomewhere");
        } else if (state == UnitState.IMPROVING && workImprovement != null) {
            return workImprovement.getOccupationString();
        } else if (state == UnitState.ACTIVE && getMovesLeft() == 0) {
            if(isUnderRepair())
                return Messages.message("model.unit.occupation.underRepair");
            else
                return Messages.message("model.unit.occupation.activeNoMovesLeft");
        } else {
            return Messages.message("model.unit.occupation." + state.toString().toLowerCase());
        }
    }

    
    public String getDetailedOccupationIndicator() {
        TradeRoute tradeRoute = getTradeRoute();

        switch (state) {
        case ACTIVE:
            if (getMovesLeft() != 0) break;
            return (isUnderRepair())
                ? Messages.message("model.unit.occupation.underRepair")
                + ": " + Integer.toString(getTurnsForRepair())
                : (tradeRoute != null)
                ? Messages.message("model.unit.occupation.inTradeRoute")
                + ": " + tradeRoute.getName()
                : Messages.message("model.unit.occupation.activeNoMovesLeft");
        case IMPROVING:
            if (workImprovement == null) break;
            return workImprovement.getOccupationString()
                + ": " + Integer.toString(getWorkLeft());
        case FORTIFIED:
        case SENTRY:
        case IN_COLONY:
        case TO_EUROPE:
        case TO_AMERICA:
        case FORTIFYING:
        case SKIPPED:
            break;
        }
        return (tradeRoute != null)
            ? Messages.message("model.unit.occupation.inTradeRoute")
            + ": " + tradeRoute.getName()
            : (getDestination() != null)
            ? Messages.message("model.unit.occupation.goingSomewhere")
            : Messages.message("model.unit.occupation."
                               + state.toString().toLowerCase());
    }

    
    public UnitState getState() {
        return state;
    }

    
    public Role getRole() {
        return role;
    }

    
    private void setRole() {
        Role oldRole = role;
        role = Role.DEFAULT;
        for (EquipmentType type : equipment.keySet()) {
            switch (type.getRole()) {
            case SOLDIER:
                if (role == Role.SCOUT) {
                    role = Role.DRAGOON;
                } else {
                    role = Role.SOLDIER;
                }
                break;
            case SCOUT:
                if (role == Role.SOLDIER) {
                    role = Role.DRAGOON;
                } else {
                    role = Role.SCOUT;
                }
                break;
            default:
                role = type.getRole();
            }
        }
        if (getState() == UnitState.IMPROVING && role != Role.PIONEER) {
            setStateUnchecked(UnitState.ACTIVE);
            setMovesLeft(0);
        }
        
        
        
        boolean keepExperience = (role == oldRole) || 
                                 (role == Role.SOLDIER && oldRole == Role.DRAGOON) ||
                                 (role == Role.DRAGOON && oldRole == Role.SOLDIER);
        
        if(!keepExperience){
            experience = 0;
        }
    }

    
    public boolean checkSetState(UnitState s) {
        switch (s) {
        case ACTIVE:
        case SENTRY:
            return true;
        case IN_COLONY:
            return !isNaval();
        case FORTIFIED:
            return getState() == UnitState.FORTIFYING;
        case IMPROVING:
            if (location instanceof Tile
                && location.getTile().claimable(getOwner())) {
                return getMovesLeft() > 0;
            }
            return false;
        case FORTIFYING:
        case SKIPPED:
            return (getMovesLeft() > 0);
        case TO_EUROPE:
            return isNaval() &&
                ((location instanceof Europe) && (getState() == UnitState.TO_AMERICA)) ||
                (getEntryLocation() == getLocation());
        case TO_AMERICA:
            return (location instanceof Europe && isNaval() && !isUnderRepair());
        default:
            logger.warning("Invalid unit state: " + s);
            return false;
        }
    }

    
    public void setState(UnitState s) {
        if (state == s) {
            
            return;
        } else if (!checkSetState(s)) {
            throw new IllegalStateException("Illegal UnitState transition: " + state + " -> " + s);
        } else {
            setStateUnchecked(s);
        }
    }

    private void setStateUnchecked(UnitState s) {
        
        
        switch (state) {
        case IMPROVING: 
            if (workLeft > 0) {
                workImprovement.getTile().getTileItemContainer().removeTileItem(workImprovement);
                workImprovement = null;
            }
            break;
        default:
            
            break;
        }

        
        switch (s) {
        case ACTIVE:
            workLeft = -1;
            break;
        case SENTRY:
            workLeft = -1;
            break;
        case FORTIFIED:
            workLeft = -1;
            movesLeft = 0;
            break;
        case FORTIFYING:
            movesLeft = 0;
            workLeft = 1;
            break;
        case IMPROVING:
            movesLeft = 0;
            workLeft = -1;
            if (workImprovement != null) {
                workLeft = workImprovement.getTurnsToComplete();
            }
            state = s;
            doAssignedWork();
            return;
        case TO_EUROPE:
            workLeft = (state == UnitState.TO_AMERICA) 
                ? TURNS_TO_SAIL + 1 - workLeft
                : TURNS_TO_SAIL;
            workLeft = (int) getOwner().getFeatureContainer().applyModifier(workLeft,
                "model.modifier.sailHighSeas", unitType, getGame().getTurn());
            movesLeft = 0;
            break;
        case TO_AMERICA:
            workLeft = (state == UnitState.TO_EUROPE) 
                ? TURNS_TO_SAIL + 1 - workLeft
                : TURNS_TO_SAIL;
            workLeft = (int) getOwner().getFeatureContainer().applyModifier(workLeft,
                "model.modifier.sailHighSeas", unitType, getGame().getTurn());
            movesLeft = 0;
            break;
        case SKIPPED:
            
            break;
        default:
            workLeft = -1;
        }
        state = s;
    }

    
    public boolean canMoveToEurope() {
        if (getLocation() instanceof Europe) {
            return true;
        }
        if (!getOwner().canMoveToEurope()) {
            return false;
        }

        List<Tile> surroundingTiles = getGame().getMap().getSurroundingTiles(getTile(), 1);
        if (surroundingTiles.size() != 8) {
            
            
            
            return true;
        } else {
            for (int i = 0; i < surroundingTiles.size(); i++) {
                Tile tile = surroundingTiles.get(i);
                if (tile == null || tile.canMoveToEurope()) {
                    return true;
                }
            }
        }
        return false;
    }

    
    public void moveToEurope() {
        
        if (!canMoveToEurope()) {
            throw new IllegalStateException("It is not allowed to move units to europe from the tile where this unit is located.");
        } else if (getLocation() instanceof Tile) {
            
            setEntryLocation(getLocation());
        }

        setState(UnitState.TO_EUROPE);
        setLocation(getOwner().getEurope());

        logger.info("Unit " + this.getId() + " moving to Europe");
        
        
        alreadyOnHighSea = false;
    }

    
    public void moveToAmerica() {
        if (!(getLocation() instanceof Europe)) {
            throw new IllegalStateException("A unit can only be moved to america from europe.");
        }

        setState(UnitState.TO_AMERICA);

        logger.info("Unit " + getId() + " moving to America");
        
        
        alreadyOnHighSea = false;
    }

    
    public boolean canBuildColony() {
        return (unitType.hasAbility("model.ability.foundColony") &&
                getMovesLeft() > 0 && 
                getTile() != null && 
                getTile().isColonizeable());
    }

    
    public void buildColony(Colony colony) {
        if (!canBuildColony()) {
            throw new IllegalStateException("Unit " + getName() + " can not build colony on " + getTile().getName() + "!");
        }
        if (!getTile().getPosition().equals(colony.getTile().getPosition())) {
            throw new IllegalStateException("A Unit can only build a colony if on the same tile as the colony");
        }

        colony.placeSettlement();
        joinColony(colony);
    }
    
    
    public void buildIndianSettlement(IndianSettlement indianSettlement) {
        if (!canBuildColony()) {
            throw new IllegalStateException("Unit " + getName() + " can not build settlement on " + getTile().getName() + "!");
        }
        if (!getTile().getPosition().equals(indianSettlement.getTile().getPosition())) {
            throw new IllegalStateException("A Unit can only build a settlement if on the same tile as the settlement");
        }

        indianSettlement.placeSettlement();
        joinIndianSettlement(indianSettlement);
    }

    
    public void joinColony(Colony colony) {
        setState(UnitState.IN_COLONY);
        setLocation(colony);
        setMovesLeft(0);
    }
    
    
    public void joinIndianSettlement(IndianSettlement indianSettlement) {
        setState(UnitState.IN_COLONY);
        setLocation(indianSettlement);
        setMovesLeft(0);
    }

    
    public Tile getTile() {
        return (location != null) ? location.getTile() : null;
    }

    
    public int getSpaceLeft() {
        int space = unitType.getSpace() - getGoodsCount();

        Iterator<Unit> unitIterator = getUnitIterator();
        while (unitIterator.hasNext()) {
            Unit u = unitIterator.next();
            space -= u.getSpaceTaken();
        }

        return space;
    }

    
    public int getVisibleGoodsCount() {
        if (visibleGoodsCount >= 0) {
            return visibleGoodsCount;
        } else {
            return getGoodsCount();
        }
    }

    public int getGoodsCount() {
        return canCarryGoods() ? goodsContainer.getGoodsCount() : 0;
    }

    
    public void moveToFront(Unit u) {
        if (canCarryUnits() && units.remove(u)) {
            units.add(0, u);
        }
    }

    
    public int getWorkLeft() {
        if (state == UnitState.IMPROVING && unitType.hasAbility("model.ability.expertPioneer")){
            return workLeft / 2;
        }
        
        return workLeft;
    }

    
    public void doAssignedWork() {
        logger.finest("Entering method doAssignedWork.");
        if (workLeft > 0) {
            if (state == UnitState.IMPROVING) {
                
                
                if (getWorkImprovement().isComplete()) {
                    setState(UnitState.ACTIVE);
                    return;
                }

                
                int amountOfWork = unitType.hasAbility("model.ability.expertPioneer") ? 2 : 1;
                
                workLeft = getWorkImprovement().doWork(amountOfWork);
                
                
                
                if (0 < workLeft && workLeft < amountOfWork){
                    workLeft = getWorkImprovement().doWork(workLeft);
                }
            } else {
                workLeft--;
            }

            
            if (state == UnitState.TO_AMERICA && getOwner().isREF()) {
                workLeft = 0;
            }

            if (workLeft == 0) {
                workLeft = -1;

                UnitState state = getState();

                switch (state) {
                case TO_EUROPE:
                	logger.info("Unit " + getId() + " arrives in Europe");
                        
                        if(this.getTradeRoute() != null){
                                setMovesLeft(0);
                                setState(UnitState.ACTIVE);
                                return;
                        }
                        
                    addModelMessage(getOwner().getEurope(), ModelMessage.MessageType.DEFAULT, this,
                                    "model.unit.arriveInEurope",
                                    "%europe%", getOwner().getEurope().getName());
                    setState(UnitState.ACTIVE);
                    break;
                case TO_AMERICA:
                	logger.info("Unit " + getId() + " arrives in America");
                    getGame().getModelController().setToVacantEntryLocation(this);
                    setState(UnitState.ACTIVE);
                    break;
                case FORTIFYING:
                    setState(UnitState.FORTIFIED);
                    break;
                case IMPROVING:
                    expendEquipment(getWorkImprovement().getExpendedEquipmentType(), 
                                    getWorkImprovement().getExpendedAmount());
                    
                    GoodsType deliverType = getWorkImprovement().getDeliverGoodsType();
                    if (deliverType != null) {
                        int deliverAmount = getTile().potential(deliverType, getType())
                            * getWorkImprovement().getDeliverAmount();
                        if (unitType.hasAbility("model.ability.expertPioneer")) {
                            deliverAmount *= 2;
                        }
                        if (getColony() != null && getColony().getOwner().equals(getOwner())) {
                            getColony().addGoods(deliverType, deliverAmount);
                        } else {
                            List<Tile> surroundingTiles = getTile().getMap().getSurroundingTiles(getTile(), 1);
                            List<Settlement> adjacentColonies = new ArrayList<Settlement>();
                            for (int i = 0; i < surroundingTiles.size(); i++) {
                                Tile t = surroundingTiles.get(i);
                                if (t.getColony() != null && t.getColony().getOwner().equals(getOwner())) {
                                    adjacentColonies.add(t.getColony());
                                }
                            }
                            if (adjacentColonies.size() > 0) {
                                int deliverPerCity = (deliverAmount / adjacentColonies.size());
                                for (int i = 0; i < adjacentColonies.size(); i++) {
                                    Colony c = (Colony) adjacentColonies.get(i);
                                    
                                    
                                    if (i == 0) {
                                        c.addGoods(deliverType, deliverPerCity
                                                   + (deliverAmount % adjacentColonies.size()));
                                    } else {
                                        c.addGoods(deliverType, deliverPerCity);
                                    }
                                }
                            }
                        }
                    }
                    
                    TileImprovement improvement = getWorkImprovement();
                    setWorkImprovement(null);
                    setState(UnitState.ACTIVE);
                    setMovesLeft(0);
                    
                    
                    getGame().getModelController().tileImprovementFinished(this, improvement);
                    
                    break;
                default:
                    logger.warning("Unknown work completed. State: " + state);
                    setState(UnitState.ACTIVE);
                }
            }
        }
    }

    
    private void expendEquipment(EquipmentType type, int amount) {
        equipment.incrementCount(type, -amount);
        setRole();
        
        EquipmentType tools = FreeCol.getSpecification().getEquipmentType("model.equipment.tools");
        if (!equipment.containsKey(tools)) {
            addModelMessage(this, ModelMessage.MessageType.WARNING, this,
                            Messages.getKey(getId() + ".noMoreTools", 
                                            "model.unit.noMoreTools"),
                            "%unit%", getName(),
                            "%location%", getLocation().getLocationName());
        }
    }

    
    public void setEntryLocation(Location entryLocation) {
        this.entryLocation = entryLocation;
    }

    
    public Location getEntryLocation() {
        return (entryLocation != null) ? entryLocation : getOwner().getEntryLocation();
    }

    
    
    public Location getVacantEntryLocation() {
        Tile l = (Tile) getEntryLocation();

        if (l.getFirstUnit() != null && l.getFirstUnit().getOwner() != getOwner()) {
            int radius = 1;
            while (true) {
                Iterator<Position> i = getGame().getMap().getCircleIterator(l.getPosition(), false, radius);
                while (i.hasNext()) {
                    Tile l2 = getGame().getMap().getTile(i.next());
                    if (l2.getFirstUnit() == null || l2.getFirstUnit().getOwner() == getOwner()) {
                        return l2;
                    }
                }

                radius++;
            }
        }

        return l;
    }

    
    public boolean isOffensiveUnit() {
        return unitType.getOffence() > UnitType.DEFAULT_OFFENCE || isArmed() || isMounted();
    }

    
    public boolean isDefensiveUnit() {
        return (unitType.getDefence() > UnitType.DEFAULT_DEFENCE || isArmed() || isMounted()) && !isNaval();
    }

    
    public boolean isUndead() {
        return hasAbility("model.ability.undead");
    }

    
    public void train() {
        String oldName = getName();
        UnitType skillTaught = FreeCol.getSpecification().getUnitType(getTeacher().getType().getSkillTaught());
        UnitType learning = getUnitTypeTeaching(skillTaught, unitType);

        if (learning != null) {
            setType(learning);
        }

        String newName = getName();
        if (!newName.equals(oldName)) {
            Colony colony = getTile().getColony();
            addModelMessage(colony, ModelMessage.MessageType.UNIT_IMPROVED, this,
                            "model.unit.unitEducated",
                            "%oldName%", oldName,
                            "%unit%", newName,
                            "%colony%", colony.getName());
        }
        this.setTurnsOfTraining(0);
        
        setMovesLeft(0);
    }

    
    
    public void adjustTension(Unit enemyUnit) {
        Player myPlayer = getOwner();
        Player enemy = enemyUnit.getOwner();     
        if(myPlayer.isAI()){
            myPlayer.modifyTension(enemy, -Tension.TENSION_ADD_MINOR);
            if (getIndianSettlement() != null) {
                getIndianSettlement().modifyAlarm(enemy, -Tension.TENSION_ADD_UNIT_DESTROYED / 2);
            }
        }

        
        if (enemy.isAI()) {
            Settlement settlement = enemyUnit.getTile().getSettlement();
            if (settlement != null) {
                
                if (settlement instanceof IndianSettlement) {
                    IndianSettlement indianSettlement = (IndianSettlement) settlement;
                    if (indianSettlement.isCapital()){
                        indianSettlement.modifyAlarm(myPlayer, Tension.TENSION_ADD_CAPITAL_ATTACKED);
                    } else {
                        indianSettlement.modifyAlarm(myPlayer, Tension.TENSION_ADD_SETTLEMENT_ATTACKED);
                    }
                } else { 
                    enemy.modifyTension(myPlayer, Tension.TENSION_ADD_NORMAL);
                }
            } else {
                
                
                IndianSettlement homeTown = enemyUnit.getIndianSettlement();
                if (homeTown != null) {
                    homeTown.modifyAlarm(myPlayer, Tension.TENSION_ADD_UNIT_DESTROYED);
                } else {
                    enemy.modifyTension(myPlayer, Tension.TENSION_ADD_MINOR);
                }
            }
        }
    }

    
    public boolean canCarryTreasure() {
        return unitType.hasAbility("model.ability.carryTreasure");
    }

    
    public boolean canCaptureGoods() {
        return unitType.hasAbility("model.ability.captureGoods");
    }

    
    public void captureGoods(Unit enemyUnit) {
        if (!canCaptureGoods()) {
            return;
        }
        
        Iterator<Goods> iter = enemyUnit.getGoodsIterator();
        while (iter.hasNext() && getSpaceLeft() > 0) {
            
            
            Goods g = iter.next();

            
            
            
            getGoodsContainer().addGoods(g);
        }
    }

    
    public Colony getColony() {
        Location location = getLocation();
        return (location != null ? location.getColony() : null);
    }

    
    
    public int getProductionOf(GoodsType goodsType, int base) {
        if (base == 0) {
            return 0;
        } else {
            return Math.round(FeatureContainer.applyModifierSet(base, getGame().getTurn(),
                                                                getModifierSet(goodsType.getId())));
        }
    }


    
    public void disposeAllUnits() {
        
        
        for (Unit unit : new ArrayList<Unit>(units)) {
            unit.dispose();
        }
    }

    
    public void dispose() {
        disposeAllUnits();
        if (unitType.canCarryGoods()) {
            goodsContainer.dispose();
        }

        if (location != null) {
            location.remove(this);
        }

        if (teacher != null) {
            teacher.setStudent(null);
            teacher = null;
        }

        if (student != null) {
            student.setTeacher(null);
            student = null;
        }

        setIndianSettlement(null);

        getOwner().invalidateCanSeeTiles();
        getOwner().removeUnit(this);

        super.dispose();
    }

    
    private void checkExperiencePromotion() {
        GoodsType produce = getWorkType();
        
        if(produce == null){
            return;
        }
        
        UnitType learnType = FreeCol.getSpecification().getExpertForProducing(produce);
        if (learnType == null || 
            learnType == unitType ||
            !unitType.canBeUpgraded(learnType, ChangeType.EXPERIENCE)) {
                return;
        }
        
        int random = getGame().getModelController().getRandom(getId() + "experience", 5000);
        if (random >= Math.min(experience, 200)) {
            return;
        }

        logger.finest("About to change type of unit due to experience.");
        String oldName = getName();
        setType(learnType);
        addModelMessage(getColony(), ModelMessage.MessageType.UNIT_IMPROVED, this,
                "model.unit.experience",
                "%oldName%", oldName,
                "%unit%", getName(),
                "%colony%", getColony().getName());        
    }

    
    public void newTurn() {
        if (isUninitialized()) {
            logger.warning("Calling newTurn for an uninitialized object: " + getId());
            return;
        }
        if (location instanceof ColonyTile) {
            checkExperiencePromotion();
        } 
        if (location instanceof Tile && ((Tile) location).getSettlement() == null) {
            attrition++;
            if (attrition > getType().getMaximumAttrition()) {
                addModelMessage(this, ModelMessage.MessageType.UNIT_LOST, this,
                                "model.unit.attrition", "%unit%", getName());
                dispose();
            }
        } else {
            attrition = 0;
        }
        if (isUnderRepair()) {
            movesLeft = 0;
        } else {
            movesLeft = getInitialMovesLeft();
        }
        doAssignedWork();
        if (getState() == UnitState.SKIPPED) {
            setState(UnitState.ACTIVE);
        }

    }

    private Location newLocation(Game game, String locationString) {
        String XMLElementTag = locationString.substring(0, locationString.indexOf(':'));
        if (XMLElementTag.equals(Tile.getXMLElementTagName())) {
            return new Tile(game, locationString);
        } else if (XMLElementTag.equals(ColonyTile.getXMLElementTagName())) {
            return new ColonyTile(game, locationString);
        } else if (XMLElementTag.equals(Colony.getXMLElementTagName())) {
            return new Colony(game, locationString);
        } else if (XMLElementTag.equals(IndianSettlement.getXMLElementTagName())) {
            return new IndianSettlement(game, locationString);
        } else if (XMLElementTag.equals(Europe.getXMLElementTagName())) {
            return new Europe(game, locationString);
        } else if (XMLElementTag.equals(Building.getXMLElementTagName())) {
            return new Building(game, locationString);
        } else if (XMLElementTag.equals(Unit.getXMLElementTagName())) {
            return new Unit(game, locationString);
        } else {
            logger.warning("Unknown type of Location: " + locationString);
            return new Tile(game, locationString);
        }
    }

    
    public boolean isAlreadyOnHighSea() {
        return alreadyOnHighSea;
    }

    
    public void setAlreadyOnHighSea(boolean alreadyOnHighSea) {
        this.alreadyOnHighSea = alreadyOnHighSea;
    }

    
    public int getTurnsForRepair() {
        return unitType.getHitPoints() - getHitpoints();
    }

    
    public String getPathTypeImage() {
        if (isMounted()) {
            return "horse";
        } else {
            return unitType.getPathImage();
        }
    }

    
    public TypeCountMap<EquipmentType> getAutomaticEquipment(){
        
        
        
        
        if(isArmed()){
            return null;
        }

        if(!getOwner().hasAbility("model.ability.automaticEquipment")){
            return null;
        }

        Settlement settlement = null;
        if (getLocation() instanceof WorkLocation) {
            settlement = getColony();
        }
        if (getLocation() instanceof IndianSettlement) {
            settlement = (Settlement) getLocation();
        }
        if(settlement == null){
            return null;
        }

        TypeCountMap<EquipmentType> equipmentList = null;

        
        Set<Ability> autoDefence = getOwner().getFeatureContainer().getAbilitySet("model.ability.automaticEquipment");

        for (EquipmentType equipment : Specification.getSpecification().getEquipmentTypeList()) {
                for (Ability ability : autoDefence) {
                    if (!ability.appliesTo(equipment)){
                        continue;
                    }
                    if (!canBeEquippedWith(equipment)) {
                        continue;
                    }

                    boolean hasReqGoods = true;
                    for(AbstractGoods goods : equipment.getGoodsRequired()){
                        if(settlement.getGoodsCount(goods.getType()) < goods.getAmount()){
                            hasReqGoods = false;
                            break;
                        }
                    }
                    if(hasReqGoods){
                        
                        if(equipmentList == null){
                            equipmentList = new TypeCountMap<EquipmentType>();
                        }
                        equipmentList.incrementCount(equipment, 1);
                    }
                }
        }
        return equipmentList;
    }


    private void unitsToXML(XMLStreamWriter out, Player player, boolean showAll, boolean toSavedGame)
            throws XMLStreamException {
        if (!units.isEmpty()) {
            out.writeStartElement(UNITS_TAG_NAME);
            for (Unit unit : units) {
                unit.toXML(out, player, showAll, toSavedGame);
            }
            out.writeEndElement();
        }
    }

    
    protected void toXMLImpl(XMLStreamWriter out, Player player, boolean showAll, boolean toSavedGame)
        throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute(ID_ATTRIBUTE, getId());
        if (name != null) {
            out.writeAttribute("name", name);
        }
        out.writeAttribute("unitType", unitType.getId());
        out.writeAttribute("movesLeft", Integer.toString(movesLeft));
        out.writeAttribute("state", state.toString());
        out.writeAttribute("role", role.toString());
        String ownerID = null;
        if (getOwner().equals(player) || !hasAbility("model.ability.piracy") || showAll) {
            ownerID = owner.getId();
        } else {
            ownerID = Player.UNKNOWN_ENEMY;
        }
        out.writeAttribute("owner", ownerID);
        out.writeAttribute("turnsOfTraining", Integer.toString(turnsOfTraining));
        out.writeAttribute("workType", workType.getId());
        out.writeAttribute("experience", Integer.toString(experience));
        out.writeAttribute("treasureAmount", Integer.toString(treasureAmount));
        out.writeAttribute("hitpoints", Integer.toString(hitpoints));
        out.writeAttribute("attrition", Integer.toString(attrition));
        
        writeAttribute(out, "student", student);
        writeAttribute(out, "teacher", teacher);

        if (getGame().isClientTrusted() || showAll || player == getOwner()) {
            writeAttribute(out, "indianSettlement", indianSettlement);
            out.writeAttribute("workLeft", Integer.toString(workLeft));
        } else {
            out.writeAttribute("workLeft", Integer.toString(-1));
        }

        if (entryLocation != null) {
            out.writeAttribute("entryLocation", entryLocation.getId());
        }

        if (location != null) {
            if (getGame().isClientTrusted() || showAll || player == getOwner()
                || !(location instanceof Building || location instanceof ColonyTile)) {
                out.writeAttribute("location", location.getId());
            } else {
                out.writeAttribute("location", getColony().getId());
            }
        }

        if (destination != null) {
            out.writeAttribute("destination", destination.getId());
        }
        if (tradeRoute != null) {
            out.writeAttribute("tradeRoute", tradeRoute.getId());
            out.writeAttribute("currentStop", String.valueOf(currentStop));
        }
        
        writeFreeColGameObject(workImprovement, out, player, showAll, toSavedGame);

        
        if (getGame().isClientTrusted() || showAll || getOwner().equals(player)) {
            unitsToXML(out, player, showAll, toSavedGame);
            if (canCarryGoods()) {
                goodsContainer.toXML(out, player, showAll, toSavedGame);
            }
        } else {
            if (canCarryGoods()) {
                out.writeAttribute("visibleGoodsCount", Integer.toString(getGoodsCount()));
                GoodsContainer emptyGoodsContainer = new GoodsContainer(getGame(), this);
                emptyGoodsContainer.setFakeID(goodsContainer.getId());
                emptyGoodsContainer.toXML(out, player, showAll, toSavedGame);
            }
        }

        if (!equipment.isEmpty()) {
            out.writeStartElement(EQUIPMENT_TAG);
            int index = 0;
            for (EquipmentType type : equipment.keySet()) {
                for (int index2 = 0; index2 < equipment.getCount(type); index2++) {
                    out.writeAttribute("x" + Integer.toString(index), type.getId());
                    index++;
                }
            }
            out.writeAttribute(ARRAY_SIZE, Integer.toString(index));
            out.writeEndElement();
        }

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setId(in.getAttributeValue(null, ID_ATTRIBUTE));
        setName(in.getAttributeValue(null, "name"));
        UnitType oldUnitType = unitType;
        unitType = FreeCol.getSpecification().getUnitType(in.getAttributeValue(null, "unitType"));

        naval = unitType.hasAbility("model.ability.navalUnit");
        movesLeft = Integer.parseInt(in.getAttributeValue(null, "movesLeft"));
        state = Enum.valueOf(UnitState.class, in.getAttributeValue(null, "state"));
        role = Enum.valueOf(Role.class, in.getAttributeValue(null, "role"));
        workLeft = Integer.parseInt(in.getAttributeValue(null, "workLeft"));
        attrition = getAttribute(in, "attrition", 0);

        String ownerID = in.getAttributeValue(null, "owner");
        if (ownerID.equals(Player.UNKNOWN_ENEMY)) {
            owner = getGame().getUnknownEnemy();
        } else {
            owner = (Player) getGame().getFreeColGameObject(ownerID);
            if (owner == null) {
                owner = new Player(getGame(), in.getAttributeValue(null, "owner"));
            }
        }

        if (oldUnitType == null) {
            owner.modifyScore(unitType.getScoreValue());
        } else {
            owner.modifyScore(unitType.getScoreValue() - oldUnitType.getScoreValue());
        }

        turnsOfTraining = Integer.parseInt(in.getAttributeValue(null, "turnsOfTraining"));
        hitpoints = Integer.parseInt(in.getAttributeValue(null, "hitpoints"));

        teacher = getFreeColGameObject(in, "teacher", Unit.class);
        student = getFreeColGameObject(in, "student", Unit.class);

        final String indianSettlementStr = in.getAttributeValue(null, "indianSettlement");
        if (indianSettlementStr != null) {
            indianSettlement = (IndianSettlement) getGame().getFreeColGameObject(indianSettlementStr);
            if (indianSettlement == null) {
                indianSettlement = new IndianSettlement(getGame(), indianSettlementStr);
            }
        } else {
            setIndianSettlement(null);
        }

        treasureAmount = getAttribute(in, "treasureAmount", 0);

        final String destinationStr = in.getAttributeValue(null, "destination");
        if (destinationStr != null) {
            destination = (Location) getGame().getFreeColGameObject(destinationStr);
            if (destination == null) {
                destination = newLocation(getGame(), destinationStr);
            }
        } else {
            destination = null;
        }

        currentStop = -1;
        tradeRoute = null;
        final String tradeRouteStr = in.getAttributeValue(null, "tradeRoute");
        if (tradeRouteStr != null) {
            tradeRoute = (TradeRoute) getGame().getFreeColGameObject(tradeRouteStr);
            final String currentStopStr = in.getAttributeValue(null, "currentStop");
            if (currentStopStr != null) {
                currentStop = Integer.parseInt(currentStopStr);
            }
        }

        workType = FreeCol.getSpecification().getType(in, "workType", GoodsType.class, null);
        experience = getAttribute(in, "experience", 0);
        visibleGoodsCount = getAttribute(in, "visibleGoodsCount", -1);

        final String entryLocationStr = in.getAttributeValue(null, "entryLocation");
        if (entryLocationStr != null) {
            entryLocation = (Location) getGame().getFreeColGameObject(entryLocationStr);
            if (entryLocation == null) {
                entryLocation = newLocation(getGame(), entryLocationStr);
            }
        }

        final String locationStr = in.getAttributeValue(null, "location");
        if (locationStr != null) {
            location = (Location) getGame().getFreeColGameObject(locationStr);
            if (location == null) {
                location = newLocation(getGame(), locationStr);
            }
            
            
            
            
            
            
            if ((location instanceof WorkLocation) && state!=UnitState.IN_COLONY) {
                logger.warning("Found "+getId()+" with state=="+state+" on WorkLocation in "+location.getColony().getName()+". Fixing: ");
                state=UnitState.IN_COLONY;
            }
        }

        units.clear();
        if (goodsContainer != null) goodsContainer.removeAll();
        equipment.clear();
        workImprovement = null;
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if (in.getLocalName().equals(UNITS_TAG_NAME)) {
                units = new ArrayList<Unit>();
                while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
                    if (in.getLocalName().equals(Unit.getXMLElementTagName())) {
                        units.add(updateFreeColGameObject(in, Unit.class));
                    }
                }
            } else if (in.getLocalName().equals(GoodsContainer.getXMLElementTagName())) {
                goodsContainer = (GoodsContainer) getGame().getFreeColGameObject(in.getAttributeValue(null, ID_ATTRIBUTE));
                if (goodsContainer != null) {
                    goodsContainer.readFromXML(in);
                } else {
                    goodsContainer = new GoodsContainer(getGame(), this, in);
                }
            } else if (in.getLocalName().equals(EQUIPMENT_TAG)) {
                int length = Integer.parseInt(in.getAttributeValue(null, ARRAY_SIZE));
                for (int index = 0; index < length; index++) {
                    String equipmentId = in.getAttributeValue(null, "x" + String.valueOf(index));
                    equipment.incrementCount(FreeCol.getSpecification().getEquipmentType(equipmentId), 1);
                }
                in.nextTag();
            } else if (in.getLocalName().equals(TileImprovement.getXMLElementTagName())) {
                workImprovement = updateFreeColGameObject(in, TileImprovement.class);
            }
        }
        
        if (goodsContainer == null && canCarryGoods()) {
            logger.warning("Carrier with ID " + getId() + " did not have a \"goodsContainer\"-tag.");
            goodsContainer = new GoodsContainer(getGame(), this);
        }

        getOwner().setUnit(this);
        getOwner().invalidateCanSeeTiles();
    }

    
    @Override
    protected void toXMLPartialImpl(XMLStreamWriter out, String[] fields)
        throws XMLStreamException {
        toXMLPartialByClass(out, getClass(), fields);
    }

    
    @Override
    protected void readFromXMLPartialImpl(XMLStreamReader in)
        throws XMLStreamException {
        readFromXMLPartialByClass(in, getClass());
    }

    
    public static String getXMLElementTagName() {
        return "unit";
    }
}
