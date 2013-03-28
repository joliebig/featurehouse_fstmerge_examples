

package net.sf.freecol.common.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.Map.Position;
import net.sf.freecol.common.model.Tension.Level;
import net.sf.freecol.common.model.Unit.Role;

import org.w3c.dom.Element;



public class IndianSettlement extends Settlement {

    private static final Logger logger = Logger.getLogger(IndianSettlement.class.getName());

    public static final int MISSIONARY_TENSION = -10;
    public static final int MAX_CONVERT_DISTANCE = 10;
    public static final int ALARM_RADIUS = 2;
    public static final int ALARM_TILE_IN_USE = 2;
    public static final int ALARM_NEW_MISSIONARY = -100;
    public static final int MAX_HORSES_PER_TURN = 2;
    public static final int TALES_RADIUS = 6;

    public static final String UNITS_TAG_NAME = "units";
    public static final String OWNED_UNITS_TAG_NAME = "ownedUnits";
    public static final String IS_VISITED_TAG_NAME = "isVisited";
    public static final String ALARM_TAG_NAME = "alarm";
    public static final String MISSIONARY_TAG_NAME = "missionary";
    public static final String WANTED_GOODS_TAG_NAME = "wantedGoods";
    
    
    

    
    public static final int KEEP_RAW_MATERIAL = 50;

    
    private UnitType learnableSkill = null;

    private GoodsType[] wantedGoods = new GoodsType[] {null, null, null};

    
    private Set<Player> visitedBy = new HashSet<Player>();

    private List<Unit> units = Collections.emptyList();

    private ArrayList<Unit> ownedUnits = new ArrayList<Unit>();

    private Unit missionary = null;

    
    private int convertProgress = 0;

    
    int lastTribute = 0;

    
    private java.util.Map<Player, Tension> alarm = new HashMap<Player, Tension>();

    
    private final Comparator<GoodsType> wantedGoodsComparator = new Comparator<GoodsType>() {
        public int compare(GoodsType goodsType1, GoodsType goodsType2) {
            return getPrice(goodsType2, 100) - getPrice(goodsType1, 100);
        }
    };

    
    private final Comparator<Goods> exportGoodsComparator = new Comparator<Goods>() {
        public int compare(Goods goods1, Goods goods2) {
            if (goods2.getAmount() == goods1.getAmount()) {
                return getPrice(goods2) - getPrice(goods1);
            } else {
                return goods2.getAmount() - goods1.getAmount();
            }
        }
    };

    
    public IndianSettlement(Game game, Player player, Tile tile, String name,
                            boolean isCapital,
                            UnitType learnableSkill, Set<Player> isVisited, Unit missionary) {
        super(game, player, name, tile);

        if (tile == null) {
            throw new IllegalArgumentException("Parameter 'tile' must not be 'null'.");
        }

        goodsContainer = new GoodsContainer(game, this);

        this.learnableSkill = learnableSkill;
        setCapital(isCapital);
        this.visitedBy = isVisited;
        this.missionary = missionary;

        convertProgress = 0;
        updateWantedGoods();
    }


    
    public IndianSettlement(Game game, XMLStreamReader in) throws XMLStreamException {
        super(game, in);
        readFromXML(in);
    }

    
    public IndianSettlement(Game game, Element e) {
        super(game, e);
        readFromXMLElement(e);
    }

    
    public IndianSettlement(Game game, String id) {
        super(game, id);
    }

    
    public StringTemplate getLocationName() {
        return StringTemplate.name(getName());
    }

    
    public java.util.Map<Player, Tension> getAlarm() {
        return alarm;
    }

    
    public int getLastTribute() {
        return lastTribute;
    }

    
    public void setLastTribute(int lastTribute) {
        this.lastTribute = lastTribute;
    }

    
    public void modifyAlarm(Player player, int addToAlarm) {
        Tension tension = alarm.get(player);
        Level oldLevel = null;
        Level newLevel = null;
        if(tension != null) {
            oldLevel = tension.getLevel();
            tension.modify(addToAlarm);            
        }
        
        if (owner != null) {
            if (isCapital()) {
                
                owner.modifyTension(player, addToAlarm, this);
            } else {
                owner.modifyTension(player, addToAlarm/2, this);
            }
        }
        
        if(alarm.get(player) != null){
            newLevel = alarm.get(player).getLevel();
        }
        if(newLevel != null && !newLevel.equals(oldLevel)){
            String propertyEvtName = "alarmLevel"; 
            PropertyChangeEvent e = new PropertyChangeEvent(this,propertyEvtName,oldLevel,newLevel);
            for(PropertyChangeListener listener : this.getPropertyChangeListeners(propertyEvtName)){
                listener.propertyChange(e);
            }
        }
        logger.finest("Alarm at " + getName()
                      + " modified by " + Integer.toString(addToAlarm)
                      + " now = " + ((alarm.get(player) == null) ? "(none)"
                                     : Integer.toString(alarm.get(player).getValue())));
    }

    
    public void propagatedAlarm(Player player, int addToAlarm) {
        Tension tension = alarm.get(player);
        if (tension != null) {
            tension.modify(addToAlarm);            
        }
    }

    
    public void setAlarm(Player player, Tension newAlarm) {
        
        if (player != owner) {
            alarm.put(player, newAlarm);
        }
    }

        
    public Tension getAlarm(Player player) {
        return alarm.get(player);
    }

    
    public String getAlarmLevelMessage(Player player) {
        if (alarm.get(player) == null) {
            alarm.put(player, new Tension(0));
        }
        return "indianSettlement.alarm." + alarm.get(player).getLevel().toString().toLowerCase();
    }

    
    public boolean hasBeenVisited() {
        Iterator<Player> playerIterator = visitedBy.iterator();
        while (playerIterator.hasNext()) {
            if (playerIterator.next().isEuropean()) {
                return true;
            }
        }
        return false;
    }
    
    
    public boolean hasBeenVisited(Player player) {
        return visitedBy.contains(player);
    }

    
    public void setVisited(Player player) {
        visitedBy.add(player);
        if (alarm.get(player) == null) {
            alarm.put(player, new Tension(0));
        }
    }

    
    public boolean allowContact(Unit unit) {
        return hasBeenVisited(unit.getOwner())
            || !unit.isNaval()
            || unit.getGoodsCount() > 0;
    }

    
    public void addOwnedUnit(Unit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Parameter 'unit' must not be 'null'.");
        }

        if (!ownedUnits.contains(unit)) {
            ownedUnits.add(unit);
        }
    }


    
    public Iterator<Unit> getOwnedUnitsIterator() {
        return ownedUnits.iterator();
    }


    
    public boolean removeOwnedUnit(Unit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Parameter 'unit' must not be 'null'.");
        }
        return ownedUnits.remove(unit);
    }


    
    public UnitType getLearnableSkill() {
        return learnableSkill;
    }

    
    public Unit getMissionary() {
        return missionary;
    }


    
    public void setMissionary(Unit missionary) {
        if (missionary != null) {
            if (missionary.getRole() != Role.MISSIONARY) {
                throw new IllegalArgumentException("Specified unit is not a missionary.");
            }
            missionary.setLocation(null);
            Tension currentAlarm = alarm.get(missionary.getOwner());
            if (currentAlarm == null) {
                alarm.put(missionary.getOwner(), new Tension(0));
            } else {
                currentAlarm.modify(ALARM_NEW_MISSIONARY);
            }
        }
        if (missionary != this.missionary) {
            convertProgress = 0;
        }
        if (this.missionary != null) {
            this.missionary.dispose();
        }
        this.missionary = missionary;
        getTile().updatePlayerExploredTiles();
    }

    public GoodsType[] getWantedGoods() {
        return wantedGoods;
    }

    public void setWantedGoods(int index, GoodsType type) {
        if (0 <= index && index <= 2) {
            wantedGoods[index] = type;
        }
    }

    
    public void setLearnableSkill(UnitType skill) {
        learnableSkill = skill;
    }


    
    public SettlementType getTypeOfSettlement() {
        return ((IndianNationType) owner.getNationType()).getTypeOfSettlement();
    }

    
    @Override
    public void add(Locatable locatable) {
        if (locatable instanceof Unit) {
            if (!units.contains(locatable)) {
                Unit indian = (Unit)locatable;
                if (units.equals(Collections.emptyList())) {
                    units = new ArrayList<Unit>();
                }
                units.add(indian);
                if (indian.getIndianSettlement() == null) {
                    
                    indian.setIndianSettlement(this);
                }
            }
        } else if (locatable instanceof Goods) {
            addGoods((Goods)locatable);
        } else {
            logger.warning("Tried to add an unrecognized 'Locatable' to a IndianSettlement.");
        }
    }


    
    @Override
    public void remove(Locatable locatable) {
        if (locatable instanceof Unit) {
            if (!units.remove(locatable)) {
                logger.warning("Failed to remove unit " + ((Unit)locatable).getId() + " from IndianSettlement");
            }
        } else if (locatable instanceof Goods) {
            removeGoods((Goods)locatable);
        } else {
            logger.warning("Tried to remove an unrecognized 'Locatable' from a IndianSettlement.");
        }
    }


    
    @Override
    public int getUnitCount() {
        return units.size();
    }

    public List<Unit> getUnitList() {
        return units;
    }
    
    public Iterator<Unit> getUnitIterator() {
        return units.iterator();
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

    
    @Override
    public Unit getDefendingUnit(Unit attacker) {
        Unit defender = null;
        float defencePower = -1.0f;
        for (Unit nextUnit : units) {
            float tmpPower = attacker.getGame().getCombatModel().getDefencePower(attacker, nextUnit);
            if (tmpPower > defencePower) {
                defender = nextUnit;
                defencePower = tmpPower;
            }
        }
        return defender;
    }


    
    public int getPrice(Goods goods) {
        return getPrice(goods.getType(), goods.getAmount());
    }


    
    public int getPrice(GoodsType type, int amount) {
        int returnPrice = 0;

    	GoodsType armsType = FreeCol.getSpecification().getGoodsType("model.goods.muskets");
    	GoodsType horsesType = FreeCol.getSpecification().getGoodsType("model.goods.horses");
    	EquipmentType armsEqType = FreeCol.getSpecification().getEquipmentType("model.equipment.indian.muskets");
    	EquipmentType horsesEqType = FreeCol.getSpecification().getEquipmentType("model.equipment.indian.horses");
    	
    	int musketsToArmIndian = armsEqType.getAmountRequiredOf(armsType);
    	int horsesToMountIndian = horsesEqType.getAmountRequiredOf(horsesType);
        int musketsCurrAvail = getGoodsCount(armsType);
        int horsesCurrAvail = getGoodsCount(horsesType);
        
        if (amount > 100) {
            throw new IllegalArgumentException();
        }

        if (type == armsType) {
            int need = 0;
            int supply = musketsCurrAvail;
            for (int i=0; i<ownedUnits.size(); i++) {
                need += musketsToArmIndian;
                if (ownedUnits.get(i).isArmed()) {
                    supply += musketsToArmIndian;
                }
            }

            int sets = ((musketsCurrAvail + amount) / musketsToArmIndian)
                - (musketsCurrAvail / musketsToArmIndian);
            int startPrice = (19+getPriceAddition()) - (supply / musketsToArmIndian);
            for (int i=0; i<sets; i++) {
                if ((startPrice-i) < 8 && (need > supply || musketsCurrAvail < musketsToArmIndian)) {
                    startPrice = 8+i;
                }
                returnPrice += musketsToArmIndian * (startPrice-i);
            }
        } else if (type == horsesType) {
            int need = 0;
            int supply = horsesCurrAvail;
            for (int i=0; i<ownedUnits.size(); i++) {
                need += horsesToMountIndian;
                if (ownedUnits.get(i).isMounted()) {
                    supply += horsesToMountIndian;
                }
            }

            int sets = (horsesCurrAvail + amount) / horsesToMountIndian
                - (horsesCurrAvail / horsesToMountIndian);
            int startPrice = (24+getPriceAddition()) - (supply/horsesToMountIndian);

            for (int i=0; i<sets; i++) {
                if ((startPrice-(i*4)) < 4 &&
                    (need > supply ||
                    		horsesCurrAvail < horsesToMountIndian * 2)) {
                    startPrice = 4+(i*4);
                }
                returnPrice += horsesToMountIndian * (startPrice-(i*4));
            }
        } else if (type.isFarmed()) {
            returnPrice = 0;
        } else {
            int currentGoods = getGoodsCount(type);

            
            GoodsType rawType = type.getRawMaterial();
            if (rawType != null) {
                int rawProduction = getMaximumProduction(rawType);
                if (currentGoods < 100) {
                    if (rawProduction < 5) {
                        currentGoods += rawProduction * 10;
                    } else if (rawProduction < 10) {
                        currentGoods += 50 + Math.max((rawProduction-5) * 5, 0);
                    } else if (rawProduction < 20) {
                        currentGoods += 75 + Math.max((rawProduction-10) * 2, 0);
                    } else {
                        currentGoods += 100;
                    }
                }
            }
            if (type.isTradeGoods()) {
                currentGoods += 20;
            }

            int valueGoods = Math.min(currentGoods + amount, 200) - currentGoods;
            if (valueGoods < 0) {
                valueGoods = 0;
            }

            returnPrice = (int) (((20.0+getPriceAddition())-(0.05*(currentGoods+valueGoods)))*(currentGoods+valueGoods)
                                 - ((20.0+getPriceAddition())-(0.05*(currentGoods)))*(currentGoods));
        }

        
        if (type == wantedGoods[0]) {
            returnPrice = (returnPrice*12)/10;
        } else if (type == wantedGoods[1]) {
            returnPrice = (returnPrice*11)/10;
        } else if (type == wantedGoods[2]) {
            returnPrice = (returnPrice*105)/100;
        }

        return returnPrice;
    }

    
    public int getMaximumProduction(GoodsType goodsType) {
        int amount = 0;
        Iterator<Position> it = getGame().getMap().getCircleIterator(getTile().getPosition(), true, getRadius());
        while (it.hasNext()) {
            Tile workTile = getGame().getMap().getTile(it.next());
            if (workTile.getOwningSettlement() == null || workTile.getOwningSettlement() == this) {
                
                amount += workTile.potential(goodsType, null);
            }
        }

        return amount;
    }


    
    public void updateWantedGoods() {
        
        List<GoodsType> goodsTypes = new ArrayList<GoodsType>(FreeCol.getSpecification().getGoodsTypeList());
        Collections.sort(goodsTypes, wantedGoodsComparator);
        int wantedIndex = 0;
        for (GoodsType goodsType : goodsTypes) {
            
            if (goodsType.isMilitaryGoods()) 
                continue;
            
            if (!goodsType.isStorable())
                continue;
            if (wantedIndex < wantedGoods.length) {
                wantedGoods[wantedIndex] = goodsType;
                wantedIndex++;
            } else {
                break;
            }
        }
    }


    
    private int getPriceAddition() {
        return getBonusMultiplier() - 1;
    }


    
    public int getBonusMultiplier() {
        int multiplier = 0;
        switch (getTypeOfSettlement()) {
        case INDIAN_CAMP:
            multiplier = 1;
            break;
        case INDIAN_VILLAGE:
            multiplier = 2;
            break;
        case AZTEC_CITY:
        case INCA_CITY:
            multiplier = 3;
            break;
        default:
            
        }
        if (isCapital()) {
            multiplier++;
        }
        return multiplier;
    }


    
    public int getGeneratedUnitCount() {
        int n;
        switch (getTypeOfSettlement()) {
        case INDIAN_CAMP:
            n = 0;
            break;
        case INDIAN_VILLAGE:
            n = 1;
            break;
        case AZTEC_CITY: case INCA_CITY:
            n = 2;
            break;
        default:
            throw new IllegalArgumentException("getTypeOfSettlement() out of range (" + getTypeOfSettlement() + ") in IndianSettlement.getGeneratedUnitCount()");
        }
        return 2 * n + 4;
    }


    @Override
    public boolean contains(Locatable locatable) {
        if (locatable instanceof Unit) {
            return units.contains(locatable);
        } else {
            return false;
        }
    }


    @Override
    public boolean canAdd(Locatable locatable) {
        return true;
    }

    public int getProductionOf(GoodsType type) {
        int potential = 0;
        Iterator<Position> it = getGame().getMap().getCircleIterator(getTile().getPosition(), true, getRadius());
        while (it.hasNext()) {
            Tile workTile = getGame().getMap().getTile(it.next());
            if ((workTile.getOwningSettlement() == null ||
                 workTile.getOwningSettlement() == this) && !workTile.isOccupied()) {
                
                potential += workTile.potential(type, null);
            }
        }

        
        
        
        
        if (type.isFoodType()) {
            potential = Math.min(potential, ownedUnits.size()*3);
        }
        return potential;
    }

    @Override
    public void newTurn() {
        if (isUninitialized()) {
            logger.warning("Uninitialized when calling newTurn");
            return;
        }

        List<GoodsType> goodsList = FreeCol.getSpecification().getGoodsTypeList();
        int workers = ownedUnits.size();
        for (GoodsType g : goodsList) {
            
            addGoods(g, getProductionOf(g));
        }

        
        
        GoodsType tools = FreeCol.getSpecification().getGoodsType("model.goods.tools");
        if (getGoodsCount(tools) > 0) {
            GoodsType typeWithSmallestAmount = null;
            for (GoodsType g : goodsList) {
                if (g.isFoodType() || g.isBuildingMaterial() || g.isRawBuildingMaterial()) {
                    continue;
                }
                if (g.isRawMaterial() && getGoodsCount(g) > KEEP_RAW_MATERIAL) {
                    if (typeWithSmallestAmount == null ||
                        getGoodsCount(g.getProducedMaterial()) < getGoodsCount(typeWithSmallestAmount)) {
                        typeWithSmallestAmount = g.getProducedMaterial();
                    }
                }
            }
            if (typeWithSmallestAmount != null) {
                int production = Math.min(getGoodsCount(typeWithSmallestAmount.getRawMaterial()),
                                          Math.min(10, getGoodsCount(tools)));
                removeGoods(tools, production);
                removeGoods(typeWithSmallestAmount.getRawMaterial(), production);
                addGoods(typeWithSmallestAmount, production * 5);
            }
        }

        
        consumeGoods(FreeCol.getSpecification().getGoodsType("model.goods.food"),
                     getFoodConsumption());
        consumeGoods(FreeCol.getSpecification().getGoodsType("model.goods.rum"),
                     2 * workers);
        consumeGoods(FreeCol.getSpecification().getGoodsType("model.goods.tradeGoods"),
                     2 * workers);
        
        consumeGoods(FreeCol.getSpecification().getGoodsType("model.goods.ore"), workers);
        consumeGoods(FreeCol.getSpecification().getGoodsType("model.goods.silver"), workers);
        consumeGoods(FreeCol.getSpecification().getGoodsType("model.goods.cigars"), workers);
        consumeGoods(FreeCol.getSpecification().getGoodsType("model.goods.coats"), workers);
        consumeGoods(FreeCol.getSpecification().getGoodsType("model.goods.cloth"), workers);
        goodsContainer.removeAbove(500);

        checkForNewIndian();

        
        if (getUnitCount() > 0) {
            increaseAlarm();
        }
        
        breedHorses();
         
        updateWantedGoods();
    }

    public boolean checkForNewMissionaryConvert() {
        
        
        if (missionary != null && getGame().getViewOwner() == null) {
            int increment = 8;
    
            
            if (missionary.hasAbility("model.ability.expertMissionary")) {
                increment = 13;
            }
    
            
            increment += 2 * alarm.get(missionary.getOwner()).getValue() / 100;
            convertProgress += increment;
    
            if (convertProgress >= 100 && getUnitCount() > 2) {
                convertProgress = 0;
                return true;
            }
        }
        return false;
    }

    
    private void checkForNewIndian() {
        
        if (getFoodCount() + 4*getGoodsCount(FreeCol.getSpecification().getGoodsType("model.goods.rum"))
                                             > 200+KEEP_RAW_MATERIAL ) {
            
            if (ownedUnits.size() <= getGeneratedUnitCount()) {
                
                List<UnitType> unitTypes = FreeCol.getSpecification().getUnitTypesWithAbility("model.ability.bornInIndianSettlement");
                if (unitTypes.size() > 0) {
                    int random = getGame().getModelController().getRandom(getId() + "bornInIndianSettlement", unitTypes.size());
                    Unit u = getGame().getModelController().createUnit(getId() + "newTurn200food",
                                                                       getTile(), getOwner(), unitTypes.get(random));
                    addOwnedUnit(u);    
                    u.setIndianSettlement(this);
                    logger.info("New indian native created in " + getTile() + " with ID=" + u.getId());
                }
            }
            
            consumeGoods(FreeCol.getSpecification().getGoodsType("model.goods.food"), 200);
            
            consumeGoods(FreeCol.getSpecification().getGoodsType("model.goods.rum"), 200/4);
            
        }
    }

    private void increaseAlarm() {

        java.util.Map<Player, Integer> extraAlarm = new HashMap<Player, Integer>();
        for (Player enemy : getGame().getEuropeanPlayers()) {
            extraAlarm.put(enemy, new Integer(0));
        }
        int alarmRadius = getRadius() + ALARM_RADIUS; 
        Iterator<Position> ci = getGame().getMap().getCircleIterator(getTile().getPosition(), true, alarmRadius);
        while (ci.hasNext()) {
            Tile tile = getGame().getMap().getTile(ci.next());
            Colony colony = tile.getColony();
                
            if (colony == null) {
                
                if (tile.getFirstUnit() != null) {
                    Player enemy =  tile.getFirstUnit().getOwner();
                    if (enemy.isEuropean()) {
                        int alarm = extraAlarm.get(enemy);
                        for (Unit unit : tile.getUnitList()) {
                            if (unit.isOffensiveUnit() && !unit.isNaval()) {
                                alarm += unit.getType().getOffence();
                            }
                        }
                        extraAlarm.put(enemy, alarm);
                    }
                }
                
                
                if (tile.getOwningSettlement() != null) {
                    Player enemy = tile.getOwningSettlement().getOwner();
                    if (enemy!=null && enemy.isEuropean()) {
                        extraAlarm.put(enemy, extraAlarm.get(enemy).intValue() + ALARM_TILE_IN_USE);
                    }
                }
            } else {
                
                Player enemy = colony.getOwner();
                extraAlarm.put(enemy, extraAlarm.get(enemy).intValue() + ALARM_TILE_IN_USE + colony.getUnitCount());
            }
        }

        
        
        if (missionary != null) {
            Player enemy = missionary.getOwner();
            int missionaryAlarm = MISSIONARY_TENSION;
            if (missionary.hasAbility("model.ability.expertMissionary")) {
                missionaryAlarm *= 2;
            }
            extraAlarm.put(enemy, extraAlarm.get(enemy).intValue() + missionaryAlarm);
        }

        for (Entry<Player, Integer> entry : extraAlarm.entrySet()) {
            Integer newAlarm = entry.getValue();
            if (alarm != null) {
                Player player = entry.getKey();
                int modifiedAlarm = (int) player.getFeatureContainer()
                    .applyModifier(newAlarm.intValue(), "model.modifier.nativeAlarmModifier",
                                   null, getGame().getTurn());
                Tension oldAlarm = alarm.get(player);
                if (oldAlarm != null) {
                    modifiedAlarm -= 4 + oldAlarm.getValue()/100;
                }
                modifyAlarm(player, modifiedAlarm);
            }
        }
    }


    private void consumeGoods(GoodsType type, int amount) {
        if (getGoodsCount(type) > 0) {
            amount = Math.min(amount, getGoodsCount(type));
            getOwner().modifyGold(amount);
            removeGoods(type, amount);
        }
    }
    
    public void equipBraves() {
    	GoodsType armsType = FreeCol.getSpecification().getGoodsType("model.goods.muskets");
    	GoodsType horsesType = FreeCol.getSpecification().getGoodsType("model.goods.horses");
    	EquipmentType armsEqType = FreeCol.getSpecification().getEquipmentType("model.equipment.indian.muskets");
    	EquipmentType horsesEqType = FreeCol.getSpecification().getEquipmentType("model.equipment.indian.horses");
    	
    	int musketsToArmIndian = armsEqType.getAmountRequiredOf(armsType);
    	int horsesToMountIndian = horsesEqType.getAmountRequiredOf(horsesType);
    	
    	int armsAvail = getGoodsCount(armsType);
    	int horsesAvail = getGoodsCount(horsesType);
    	
    	for(Unit brave : getUnitList()){
    		logger.finest("Muskets available=" + getGoodsCount(armsType));
    		if(armsAvail < musketsToArmIndian){
    			break;
    		}
    		if(brave.isArmed()){
    			continue;
    		}
    		logger.info("Equiping brave with muskets");
    		brave.equipWith(armsEqType, 1);
    		if(!brave.isArmed()){
    			logger.warning("Brave has NOT been armed");
    		}	
    		armsAvail = getGoodsCount(armsType);
    	}

    	for(Unit brave : getUnitList()){	
    		if(horsesAvail < horsesToMountIndian){
    			break;
    		}
    		if(brave.isMounted()){
    			continue;
    		}
    		logger.info("Equiping brave with horses");
    		brave.equipWith(horsesEqType, 1);
    		horsesAvail = getGoodsCount(horsesType);
    	}
    }
    
    
    @Override
    public List<FreeColGameObject> disposeList() {
        
        while (ownedUnits.size() > 0) {
            ownedUnits.remove(0).setIndianSettlement(null);
        }

        List<FreeColGameObject> objects = new ArrayList<FreeColGameObject>();
        while (units.size() > 0) {
            objects.addAll(units.remove(0).disposeList());
        }
        objects.addAll(super.disposeList());
        return objects;
    }

    
    @Override
    public void dispose() {
        disposeList();
    }

    
    public void createGoodsContainer() {
        goodsContainer = new GoodsContainer(getGame(), this);
    }
    
    
    private void breedHorses() {
    	GoodsType horsesType = FreeCol.getSpecification().getGoodsType("model.goods.horses");
    	GoodsType reqGoodsType = horsesType.getRawMaterial();
  
    	
    	if(getGoodsCount(horsesType) < horsesType.getBreedingNumber()){
    		return;
    	}
    	
    	int foodProdAvail = getProductionOf(reqGoodsType) - getFoodConsumptionByType(reqGoodsType);

    	
    	if(foodProdAvail <= 0){
    		return;
    	}
    	
    	int horsesThisTurn = Math.min(IndianSettlement.MAX_HORSES_PER_TURN, foodProdAvail);
    	
    	addGoods(horsesType, horsesThisTurn);
    }
    
    

    
    @Override
    protected void toXMLImpl(XMLStreamWriter out, Player player, boolean showAll, boolean toSavedGame)
        throws XMLStreamException {
        boolean full = getGame().isClientTrusted() || showAll || player == getOwner();
        PlayerExploredTile pet = (player == null) ? null
            : getTile().getPlayerExploredTile(player);

        if (toSavedGame && !showAll) {
            logger.warning("toSavedGame is true, but showAll is false");
        }

        
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute(ID_ATTRIBUTE, getId());
        out.writeAttribute("tile", tile.getId());
        out.writeAttribute("name", getName());
        out.writeAttribute("owner", owner.getId());
        out.writeAttribute("isCapital", Boolean.toString(isCapital()));

        if (full) {
            out.writeAttribute("lastTribute", Integer.toString(lastTribute));
            out.writeAttribute("convertProgress", Integer.toString(convertProgress));
            writeAttribute(out, "learnableSkill", learnableSkill);
            for (int i = 0; i < wantedGoods.length; i++) {
                String tag = "wantedGoods" + Integer.toString(i);
                out.writeAttribute(tag, wantedGoods[i].getId());
            }
        } else if (pet != null) {
            writeAttribute(out, "learnableSkill", pet.getSkill());
            GoodsType[] wanted = pet.getWantedGoods();
            int i, j = 0;
            for (i = 0; i < wanted.length; i++) {
                if (wanted[i] != null) {
                    String tag = "wantedGoods" + Integer.toString(j);
                    out.writeAttribute(tag, wanted[i].getId());
                    j++;
                }
            }
        }

        
        
        if (full) {
            Iterator<Player> playerIterator = visitedBy.iterator();
            while (playerIterator.hasNext()) {
                out.writeStartElement(IS_VISITED_TAG_NAME);
                out.writeAttribute("player", playerIterator.next().getId());
                out.writeEndElement();
            }
            for (Entry<Player, Tension> entry : alarm.entrySet()) {
                out.writeStartElement(ALARM_TAG_NAME);
                out.writeAttribute("player", entry.getKey().getId());
                out.writeAttribute("value", String.valueOf(entry.getValue().getValue()));
                out.writeEndElement();
            }
            if (missionary != null) {
                out.writeStartElement(MISSIONARY_TAG_NAME);
                missionary.toXML(out, player, showAll, toSavedGame);
                out.writeEndElement();
            }
            if (!units.isEmpty()) {
                out.writeStartElement(UNITS_TAG_NAME);
                for (Unit unit : units) {
                    unit.toXML(out, player, showAll, toSavedGame);
                }
                out.writeEndElement();
            }
            goodsContainer.toXML(out, player, showAll, toSavedGame);
            for (Unit unit : ownedUnits) {
                out.writeStartElement(OWNED_UNITS_TAG_NAME);
                out.writeAttribute(ID_ATTRIBUTE, unit.getId());
                out.writeEndElement();
            }
        } else if (pet != null) {
            if (hasBeenVisited(player)) {
                out.writeStartElement(IS_VISITED_TAG_NAME);
                out.writeAttribute("player", player.getId());
                out.writeEndElement();
            }
            if (getAlarm(player) != null) {
                out.writeStartElement(ALARM_TAG_NAME);
                out.writeAttribute("player", player.getId());
                out.writeAttribute("value", String.valueOf(getAlarm(player).getValue()));
                out.writeEndElement();
            }
            if (pet.getMissionary() != null) {
                out.writeStartElement(MISSIONARY_TAG_NAME);
                pet.getMissionary().toXML(out, player, showAll, toSavedGame);
                out.writeEndElement();
            }
        }

        if (!full) {
            GoodsContainer emptyGoodsContainer = new GoodsContainer(getGame(), this);
            emptyGoodsContainer.setFakeID(goodsContainer.getId());
            emptyGoodsContainer.toXML(out, player, showAll, toSavedGame);
        }

        out.writeEndElement();
    }

    
    @Override
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setId(in.getAttributeValue(null, ID_ATTRIBUTE));

        tile = (Tile) getGame().getFreeColGameObject(in.getAttributeValue(null, "tile"));
        if (tile == null) {
            tile = new Tile(getGame(), in.getAttributeValue(null, "tile"));
        }
        owner = (Player) getGame().getFreeColGameObject(in.getAttributeValue(null, "owner"));
        if (owner == null) {
            owner = new Player(getGame(), in.getAttributeValue(null, "owner"));
        }
        setCapital(getAttribute(in, "isCapital", false));
        setName(in.getAttributeValue(null, "name"));

        owner.addSettlement(this);

        ownedUnits.clear();
        
        for (int i = 0; i < wantedGoods.length; i++) {
            String tag = WANTED_GOODS_TAG_NAME + Integer.toString(i);
            String wantedGoodsId = getAttribute(in, tag, null);
            if (wantedGoodsId != null) {
                wantedGoods[i] = FreeCol.getSpecification().getGoodsType(wantedGoodsId);
            }
        }

        convertProgress = getAttribute(in, "convertProgress", 0);
        lastTribute = getAttribute(in, "lastTribute", 0);
        learnableSkill = FreeCol.getSpecification().getType(in, "learnableSkill", UnitType.class, null);

        visitedBy.clear();
        alarm = new HashMap<Player, Tension>();
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if (IS_VISITED_TAG_NAME.equals(in.getLocalName())) {
                Player player = (Player)getGame().getFreeColGameObject(in.getAttributeValue(null, "player"));
                visitedBy.add(player);
                in.nextTag(); 
            } else if (ALARM_TAG_NAME.equals(in.getLocalName())) {
                Player player = (Player) getGame().getFreeColGameObject(in.getAttributeValue(null, "player"));
                alarm.put(player, new Tension(getAttribute(in, "value", 0)));
                in.nextTag(); 
            } else if (WANTED_GOODS_TAG_NAME.equals(in.getLocalName())) {
                String[] wantedGoodsID = readFromArrayElement(WANTED_GOODS_TAG_NAME, in, new String[0]);
                for (int i = 0; i < wantedGoodsID.length; i++) {
                    if (i == 3)
                        break;
                    wantedGoods[i] = FreeCol.getSpecification().getGoodsType(wantedGoodsID[i]);
                }
            } else if (MISSIONARY_TAG_NAME.equals(in.getLocalName())) {
                in.nextTag();
                missionary = updateFreeColGameObject(in, Unit.class);
                in.nextTag();                
            } else if (UNITS_TAG_NAME.equals(in.getLocalName())) {
                units = new ArrayList<Unit>();
                while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
                    if (in.getLocalName().equals(Unit.getXMLElementTagName())) {
                        Unit unit = updateFreeColGameObject(in, Unit.class);
                        if (unit.getLocation() != this) {
                            logger.warning("fixing unit location");
                            unit.setLocation(this);
                        }
                        units.add(unit);
                    }
                }
            } else if (OWNED_UNITS_TAG_NAME.equals(in.getLocalName())) {
                Unit unit = getFreeColGameObject(in, ID_ATTRIBUTE, Unit.class);
                if(unit.getOwner() != null && unit.getOwner() != owner){
                	logger.warning("Error in savegame: unit " + unit.getId() + " does not belong to settlement " + getId());
                }
                else{
                	ownedUnits.add(unit);
                	owner.setUnit(unit);
                }
                in.nextTag();
            } else if (in.getLocalName().equals(GoodsContainer.getXMLElementTagName())) {
                goodsContainer = (GoodsContainer) getGame().getFreeColGameObject(in.getAttributeValue(null, ID_ATTRIBUTE));
                if (goodsContainer != null) {
                    goodsContainer.readFromXML(in);
                } else {
                    goodsContainer = new GoodsContainer(getGame(), this, in);
                }                
            }
        }
    }


    
    public Colony getColony() {
        return null;
    }
    
    
    public List<Goods> getSellGoods() {
        List<Goods> settlementGoods = getCompactGoods();
        for(Goods goods : settlementGoods) {
            if (goods.getAmount() > 100) {
                goods.setAmount(100);
            }
        }
        Collections.sort(settlementGoods, exportGoodsComparator);

        List<Goods> result = new ArrayList<Goods>();
        int count = 0;
        for (Goods goods : settlementGoods) {
            if (goods.getType().isNewWorldGoodsType() && goods.getAmount() > 0) {
                result.add(goods);
                count++;
                if (count > 2) {
                    return result;
                }
            }
        }
        
        return result;
    }

    
    public int getPlunder() {
        return owner.getGold() / 10;
    }

    
    public int getPriceToSell(Goods goods) {
        return getPriceToSell(goods.getType(), goods.getAmount());
    }

    
    public int getPriceToSell(GoodsType type, int amount) {
        if (amount > 100) {
            throw new IllegalArgumentException();
        }

        int price = 10 - getProductionOf(type);
        if (price < 1) price = 1;
        return amount * price;
    }
    
    public String toString() {
        StringBuilder s = new StringBuilder(getName());
        s.append(" at (").append(tile.getX()).append(",").append(tile.getY()).append(")"); 
        return s.toString();
    }

    
    public void tradeGoodsWithSetlement(IndianSettlement settlement) {
        GoodsType armsType = FreeCol.getSpecification().getGoodsType("model.goods.muskets");
        GoodsType horsesType = FreeCol.getSpecification().getGoodsType("model.goods.horses");
        
        List<GoodsType> goodsToTrade = new ArrayList<GoodsType>();
        goodsToTrade.add(armsType);
        goodsToTrade.add(horsesType);
        
        for(GoodsType goods : goodsToTrade){
            int goodsInStock = getGoodsCount(goods);
            if(goodsInStock <= 50){
                continue;
            }
            int goodsTraded = goodsInStock / 2;  
            settlement.addGoods(goods, goodsTraded);
            removeGoods(goods, goodsTraded);
        }
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
        return "indianSettlement";
    }
}
