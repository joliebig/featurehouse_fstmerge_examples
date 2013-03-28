

package net.sf.freecol.common.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.PseudoRandom;
import net.sf.freecol.common.Specification;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Map.Position;
import net.sf.freecol.common.model.NationOptions.NationState;
import net.sf.freecol.common.model.Region.RegionType;
import net.sf.freecol.common.model.Settlement.SettlementType;
import net.sf.freecol.common.model.Unit.UnitState;
import net.sf.freecol.common.util.RandomChoice;

import org.w3c.dom.Element;


public class Player extends FreeColGameObject implements Nameable {

    private static final Logger logger = Logger.getLogger(Player.class.getName());

    public static final int SCORE_SETTLEMENT_DESTROYED = -40;
    public static final int SCORE_INDEPENDENCE_DECLARED = 100;
    public static final int SCORE_INDEPENDENCE_GRANTED = 1000;

    
    private static final String FOUNDING_FATHER_TAG = "foundingFathers";

    
    private static final String STANCE_TAG = "stance";

    
    private static final String TENSION_TAG = "tension";

    
    private int index;


    
    public static enum Stance {
        UNCONTACTED, WAR, CEASE_FIRE, PEACE, ALLIANCE
    }


    
    
    protected java.util.Map<Player, Tension> tension = new HashMap<Player, Tension>();

    
    private java.util.Map<String, Stance> stance = new HashMap<String, Stance>();

    private static final Color noNationColor = Color.BLACK;

    

    
    private String name;

    public static final String UNKNOWN_ENEMY = "unknown enemy";

    
    private NationType nationType;

    
    private String nationID;

    
    private String newLandName = null;

    
    private Color color = Color.BLACK;

    private boolean admin;

    
    private int score;

    
    private int gold;

    
    private int immigration;

    
    private int liberty;

    
    private int numberOfSettlements;

    
    private Market market;

    
    protected Europe europe;

    
    protected Monarch monarch;

    private boolean ready;

    
    private boolean ai;

    
    private boolean attackedByPrivateers = false;

    private int oldSoL;

    private boolean dead = false;

    
    final private Set<FoundingFather> allFathers = new HashSet<FoundingFather>();

    private FoundingFather currentFather;

    
    private int tax = 0;

    private PlayerType playerType;

    public static enum PlayerType {
        NATIVE, COLONIAL, REBEL, INDEPENDENT, ROYAL, UNDEAD
    }

    private int immigrationRequired = 12;

    
    private int settlementNameIndex = 0;
    private EnumMap<RegionType, Integer> regionNameIndex = new EnumMap<RegionType, Integer>(RegionType.class);

    private Location entryLocation;

    
    private final java.util.Map<String, Unit> units = new HashMap<String, Unit>();

    private final Iterator<Unit> nextActiveUnitIterator = new UnitIterator(this, new ActivePredicate());

    private final Iterator<Unit> nextGoingToUnitIterator = new UnitIterator(this, new GoingToPredicate());

    
    final private List<Settlement> settlements = new ArrayList<Settlement>();

    
    private List<TradeRoute> tradeRoutes = new ArrayList<TradeRoute>();

    
    private final List<ModelMessage> modelMessages = new ArrayList<ModelMessage>();

    
    protected boolean[][] canSeeTiles = null;

    
    private FeatureContainer featureContainer = new FeatureContainer();

    
    private String independentNationName;

    
    protected List<HistoryEvent> history = new ArrayList<HistoryEvent>();

    public static final Comparator<Player> playerComparator = new Comparator<Player>() {
        public int compare(Player player1, Player player2) {
            int counter1 = 0;
            int counter2 = 0;
            if (player1.isAdmin()) {
                counter1 += 8;
            }
            if (!player1.isAI()) {
                counter1 += 4;
            }
            if (player1.isEuropean()) {
                counter1 += 2;
            }
            if (player2.isAdmin()) {
                counter2 += 8;
            }
            if (!player2.isAI()) {
                counter2 += 4;
            }
            if (player2.isEuropean()) {
                counter2 += 2;
            }
            
            return counter2 - counter1;
        }
    };


    
    protected Player() {
        
    }

    
    public Player(Game game, String name, boolean admin, boolean ai, Nation nation) {
        this(game, name, admin, nation);
        this.ai = ai;
    }

    
    public Player(Game game, String name, boolean admin) {
        this(game, name, admin, game.getVacantNation());
    }

    
    public Player(Game game, String name, boolean admin, Nation newNation) {
        super(game);

        this.name = name;
        this.admin = admin;
        if (newNation != null && newNation.getType() != null) {
            this.nationType = newNation.getType();
            this.color = newNation.getColor();
            this.nationID = newNation.getId();
            try {
                featureContainer.add(nationType.getFeatureContainer());
            } catch (Throwable error) {
                error.printStackTrace();
            }
            if (nationType.isEuropean()) {
                
                gold = 0;
                europe = new Europe(game, this);
                if (!nationType.isREF()) {
                    
                    monarch = new Monarch(game, this, newNation.getRulerName());
                    playerType = PlayerType.COLONIAL;
                } else {
                    
                    playerType = PlayerType.ROYAL;
                }
            } else {
                
                gold = 1500;
                playerType = PlayerType.NATIVE;
            }
        } else {
            
            
            this.nationID = "model.nation.unknownEnemy";
            this.color = noNationColor;
            this.playerType = PlayerType.COLONIAL;
        }
        market = new Market(getGame(), this);
        immigration = 0;
        liberty = 0;
        currentFather = null;

        
        
        
        
        final String curId = getId();
        game.removeFreeColGameObject(curId);
        game.setFreeColGameObject(curId, this);
    }

    
    public Player(Game game, XMLStreamReader in) throws XMLStreamException {
        super(game, in);
        readFromXML(in);
    }

    
    public Player(Game game, Element e) {
        super(game, e);
        readFromXMLElement(e);
    }

    
    public Player(Game game, String id) {
        super(game, id);
    }

    
    public int getIndex() {
        return index;
    }
	
    
    public final FeatureContainer getFeatureContainer() {
        return featureContainer;
    }

    
    public final void setFeatureContainer(final FeatureContainer newFeatureContainer) {
        this.featureContainer = newFeatureContainer;
    }

    public boolean hasAbility(String id) {
        return featureContainer.hasAbility(id);
    }

    
    public void addModelMessage(ModelMessage modelMessage) {
        modelMessages.add(modelMessage);
    }

    
    public List<ModelMessage> getModelMessages() {
        return modelMessages;
    }

    
    public List<ModelMessage> getNewModelMessages() {

        ArrayList<ModelMessage> out = new ArrayList<ModelMessage>();

        for (ModelMessage message : modelMessages) {
            if (message.hasBeenDisplayed()) {
                continue;
            } else {
                out.add(message); 
            }
        }

        return out;
    }

    
    public void removeModelMessages() {
        Iterator<ModelMessage> messageIterator = modelMessages.iterator();
        while (messageIterator.hasNext()) {
            ModelMessage message = messageIterator.next();
            if (message.hasBeenDisplayed()) {
                messageIterator.remove();
            }
        }
    }

    
    public void clearModelMessages() {
        modelMessages.clear();
    }

    
    public void divertModelMessages(FreeColGameObject source, FreeColGameObject newSource) {
        
        
        List<ModelMessage> modelMessagesList = new ArrayList<ModelMessage>();
        modelMessagesList.addAll(modelMessages);
        
        for (ModelMessage modelMessage : modelMessagesList) {
            if (modelMessage.getSource() == source) {
                if (newSource == null) {
                    modelMessages.remove(modelMessage);
                } else {
                    modelMessage.setSource(newSource);
                    if (modelMessage.getDisplay() == source) {
                        modelMessage.setDisplay(newSource);
                    }
                }
            }
        }
    }

    
    public int getScore() {
        return score;
    }

    
    public void setScore(int newScore) {
        score = newScore;
    }

    
    public void modifyScore(int value) {
        score += value;
    }

    
    public Market getMarket() {
        return market;
    }

    
    public void reinitialiseMarket() {
        market = new Market(getGame(), this);
    }

    
    public String getMarketName() {
        Europe europe = getEurope();

        return (europe == null) ? Messages.message("model.market.independent")
            : europe.getName();
    }

    
    public boolean hasSettlement(Settlement s) {
        return settlements.contains(s);
    }

    
    public void addSettlement(Settlement s) {
        if (!settlements.contains(s)) {
            settlements.add(s);
            if (s.getOwner() != this) {
                s.setOwner(this);
            }
        }
    }

    
    public void removeSettlement(Settlement s) {
        if (settlements.contains(s)) {
            if (s.getOwner() == this) {
                throw new IllegalStateException("Cannot remove the ownership of the given settlement before it has been given to another player.");
            }
            settlements.remove(s);
        }
    }

                        
    public int getNumberOfSettlements() {
        return numberOfSettlements;
    }

    public void setNumberOfSettlements(int number) {
        numberOfSettlements = number;
    }

    
    public List<Settlement> getSettlements() {
        return settlements;
    }

    
    public List<Colony> getColonies() {
        ArrayList<Colony> colonies = new ArrayList<Colony>();
        for (Settlement s : settlements) {
            if (s instanceof Colony) {
                colonies.add((Colony) s);
            } else {
                throw new RuntimeException("getColonies can only be called for players whose settlements are colonies.");
            }
        }
        return colonies;
    }

    
    public int getColoniesPopulation() {
        int i = 0;
        for (Colony c : getColonies()) {
            i += c.getUnitCount();
        }
        return i;
    }

    
    public Colony getColony(String name) {
        for (Colony colony : getColonies()) {
            if (colony.getName().equals(name)) {
                return colony;
            }
        }
        return null;
    }

    
    public List<IndianSettlement> getIndianSettlements() {
        ArrayList<IndianSettlement> indianSettlements = new ArrayList<IndianSettlement>();
        for (Settlement s : settlements) {
            if (s instanceof IndianSettlement) {
                indianSettlements.add((IndianSettlement) s);
            } else {
                throw new RuntimeException("getIndianSettlements can only be called for players whose settlements are IndianSettlements.");
            }
        }
        return indianSettlements;
    }

    
    public IndianSettlement getIndianSettlement(String name) {
        for (IndianSettlement settlement : getIndianSettlements()) {
            if (settlement.getName().equals(name)) {
                return settlement;
            }
        }
        return null;
    }

    
    public Settlement getSettlement(String name) {
        return (isIndian()) ? getIndianSettlement(name) : getColony(name);
    }

    
    public PlayerType getPlayerType() {
        return playerType;
    }

    
    public void setPlayerType(PlayerType type) {
        playerType = type;
    }

    
    public boolean isEuropean() {
        return nationType != null && nationType.isEuropean();
    }

    
    public boolean isIndian() {
        return playerType == PlayerType.NATIVE;
    }

    
    public boolean isREF() {
        return nationType != null && nationType.isREF();
    }

    
    public boolean isAI() {
        return ai;
    }

    
    public void setAI(boolean ai) {
        this.ai = ai;
    }

    
    public boolean isAdmin() {
        return admin;
    }
    
    
    
    public static boolean checkForDeath(Player player) {
        
        
        if (player.isREF()) {
            
            Iterator<Player> players = player.getGame().getPlayerIterator();
            while (players.hasNext()) {
                Player enemy = players.next();
                if (enemy.getREFPlayer() == player
                    && enemy.getPlayerType() == PlayerType.REBEL) {
                    return false;
                }
            }

            
            Iterator<Unit> units = player.getUnitIterator();
            while (units.hasNext()) {
                if (!units.next().isInEurope()) {
                    return false;
                }
            }

            
            return true;
        }

        
        if (!player.getSettlements().isEmpty()) {
            return false;
        }
        
        
        boolean hasCarrier = false;
        List<Unit> unitList = player.getUnits();
        for(Unit unit : unitList){
            boolean isValidUnit = false;
            
            if(unit.isCarrier()){
                hasCarrier = true;
                logger.info("Still has carrier");
                continue;
            }
            
            
            if(unit.isColonist()){
                isValidUnit = true;
            }

            
            if(unit.isOffensiveUnit()){
                isValidUnit = true;
            }
             
            if(!isValidUnit){
                continue;
            }

            
            Location unitLocation = unit.getLocation();
            
            if(unitLocation instanceof Tile){
                logger.info("Found colonist in new world");
                return false;
            }
            
            if(unit.isOnCarrier()){
                Unit carrier = (Unit) unitLocation;
                
                if(carrier.getLocation() instanceof Tile){
                    logger.info("Found colonist aboard carrier in new world");
                    return false;
                }   
            }
        }

        
        
        
        
        if (!player.isEuropean() || player.getEurope() == null) {
            return true;
        }
        
        
        if (player.getGame().getTurn().getYear() >= 1600) {
            logger.info("No presence in new world after 1600");
            return true;
        }
        
        int goldNeeded = 0;
        
        if(!hasCarrier){
            
                
            Iterator<UnitType> navalUnits = FreeCol.getSpecification().getUnitTypesWithAbility("model.ability.navalUnit").iterator();
                
            int lowerPrice = Integer.MAX_VALUE;
                
            while(navalUnits.hasNext()){
                UnitType unit = navalUnits.next();
                
                int unitPrice = player.getEurope().getUnitPrice(unit);
                
                
                if(unitPrice == UnitType.UNDEFINED){
                    continue;
                }
                
                if(unitPrice < lowerPrice){
                    lowerPrice = unitPrice;
                }
            }
            
            
            if(lowerPrice == Integer.MAX_VALUE){
                logger.warning("Couldnt find naval unit to buy");
                return true;
            }
            
            goldNeeded += lowerPrice;
            
            
            if(goldNeeded > player.getGold()){
                logger.info("Does not have enough money to buy carrier");
                return true;
            }
            logger.info("Has enough money to buy carrier, has=" + player.getGold() + ", needs=" + lowerPrice);
        }

        
        Iterator<Unit> unitIterator = player.getEurope().getUnitIterator();
        while (unitIterator.hasNext()) {
            Unit unit = unitIterator.next();
            if (unit.isCarrier()) {
                
                for(Unit u : unit.getUnitList()){
                    if(u.isColonist()){
                        return false;
                    }
                }
                
                
                if(unit.getGoodsCount() > 0){
                    logger.info("Has goods to sell");
                    return false;
                }
                continue;
            }
            if (unit.isColonist()){
                logger.info("Has colonist unit waiting in port");
                return false;
            }
        }
        
        
        int goldToRecruit =  player.getEurope().getRecruitPrice();

        

        Iterator<UnitType> trainedUnits = FreeCol.getSpecification().getUnitTypesTrainedInEurope().iterator();

        int goldToTrain = Integer.MAX_VALUE;

        while(trainedUnits.hasNext()){
            UnitType unit = trainedUnits.next();

            if(!unit.hasAbility("model.ability.foundColony")){
                continue;
            }

            int unitPrice = player.getEurope().getUnitPrice(unit);
            
            
            if(unitPrice == UnitType.UNDEFINED){
                continue;
            }
            
            if(unitPrice < goldToTrain){
                goldToTrain = unitPrice;
            }
        }

        goldNeeded += Math.min(goldToTrain, goldToRecruit);

        
        if(goldNeeded > player.getGold()){
            logger.info("Does not have enough money for recruiting or training");
            return true;
        }
        return false;
    }
    

    
    public boolean isDead() {
        return dead;
    }

    
    public boolean getDead() {
        return dead;
    }

    
    public void setDead(boolean dead) {
        this.dead = dead;
    }

    
    public boolean isAtWar() {
        for (Player player : getGame().getPlayers()) {
            if (getStance(player) == Stance.WAR) {
                return true;
            }
        }
        return false;
    }

    
    public List<Player> getDominionsAtWar() {
        List<Player> dominions = new LinkedList<Player>();        
        Iterator<Player> it = getGame().getPlayerIterator();
        while (it.hasNext()) {
            Player p = it.next();
            if (p.getREFPlayer() == this
                    && p.getPlayerType() == PlayerType.REBEL
                    && p.getMonarch() == null) {
                dominions.add(p);
            }
        }
        return dominions;
    }

    
    public final Unit getUnit(String id) {
        return units.get(id);
    }

    
    public final void setUnit(final Unit newUnit) {
    	if (newUnit == null) {
    		logger.warning("Unit to add is null");
    		return;
    	}
    	
    	
    	if(newUnit.getOwner() != null && newUnit.getOwner() != this){
    		throw new IllegalStateException(this + " adding another players unit=" + newUnit);
    	}

    	units.put(newUnit.getId(), newUnit);
    }

    
    public void removeUnit(final Unit oldUnit) {
        if (oldUnit != null) {
            units.remove(oldUnit.getId());
        }
    }

    
    public int getSoL() {
        int sum = 0;
        int number = 0;
        for (Colony c : getColonies()) {
            sum += c.getSoL();
            number++;
        }
        if (number > 0) {
            return sum / number;
        } else {
            return 0;
        }
    }

    
    public final String getIndependentNationName() {
        return independentNationName;
    }

    
    public final void setIndependentNationName(final String newIndependentNationName) {
        this.independentNationName = newIndependentNationName;
    }

    
    public Player getREFPlayer() {
        Nation ref = getNation().getRefNation();
        return (ref == null) ? null : getGame().getPlayer(ref.getId());
    }

    
    public String getDefaultNewLandName() {
        return Messages.message(nationID + ".newLandName");
    }

    
    public String getNewLandName() {
        return newLandName;
    }

    
    public String getSafeNewLandName() {
        return (newLandName != null) ? newLandName
            : getDefaultNewLandName();
    }


    
    public boolean isNewLandNamed() {
        return newLandName != null;
    }

    
    public String getDefaultSettlementName(boolean capital) {
        String prefix = nationID + ".settlementName.";
        String name;

        if (capital) return Messages.message(prefix + "0");

        if (isIndian()) {
            
            
            
            PseudoRandom random = getGame().getModelController().getPseudoRandom();
            int upper = 100;
            int lower = 1;
            int i, n = 0;

            for (i = 0; i < 5; i++) { 
                n = random.nextInt(upper - lower) + lower;
                if (!Messages.containsKey(prefix + Integer.toString(n))) {
                    if (n == lower) break;
                    upper = n;
                    continue;
                }
                name = Messages.message(prefix + Integer.toString(n));
                if (getSettlement(name) == null) return name;
            }
            for (i = n+1; i < upper; i++) { 
                if (!Messages.containsKey(prefix + Integer.toString(i))) break;
                name = Messages.message(prefix + Integer.toString(i));
                if (getSettlement(name) == null) return name;
            }
            for (i = n-1; i > 0; i--) { 
                if (!Messages.containsKey(prefix + Integer.toString(i))) continue;
                name = Messages.message(prefix + Integer.toString(i));
                if (getSettlement(name) == null) return name;
            }
        } else {
            while (Messages.containsKey(prefix + Integer.toString(settlementNameIndex))) {
                name = Messages.message(prefix + Integer.toString(settlementNameIndex));
                settlementNameIndex++;
                if (getGame().getSettlement(name) == null) return name;
            }
        }

        
        String fallback = (isIndian()) ? "Settlement" : "Colony";
        do {
            name = Messages.message(fallback) + settlementNameIndex;
            settlementNameIndex++;
        } while (getGame().getSettlement(name) != null);
        return name;
    }

    
    public String getDefaultRegionName(RegionType regionType) {
        String prefix = nationID + ".region." + regionType.toString().toLowerCase() + ".";
        String name = null;
        int index = 1;
        Integer newIndex = regionNameIndex.get(regionType);
        if (newIndex != null) {
            index = newIndex.intValue();
        }
        do {
            name = null;
            if (Messages.containsKey(prefix + Integer.toString(index))) {
                name = Messages.message(prefix + Integer.toString(index));
                index++;
            }
        } while (name != null && getGame().getMap().getRegionByName(name) != null);
        if (name == null) {
            do {
                String type = Messages.message("model.region." + regionType.toString().toLowerCase() + ".name");
                name = Messages.message("model.region.default",
                                        "%nation%", getNationAsString(),
                                        "%type%", type,
                                        "%index%", Integer.toString(index));
                index++;
            } while (getGame().getMap().getRegionByName(name) != null);
        }
        regionNameIndex.put(regionType, index);
        return name;
    }

    
    public void setNewLandName(String newLandName) {
        this.newLandName = newLandName;
    }

    
    public int getLandPrice(Tile tile) {
        Player nationOwner = tile.getOwner();
        int price = 0;

        if (nationOwner == null || nationOwner == this) {
            return 0; 
        } else if (tile.getSettlement() != null) {
            return -1; 
        } else if (nationOwner.isEuropean()) {
            if (tile.getOwningSettlement() != null
                && tile.getOwningSettlement().getOwner() == nationOwner) {
                return -1; 
            } else {
                return 0; 
            }
        } 
        for (GoodsType type : FreeCol.getSpecification().getGoodsTypeList()) {
            price += tile.potential(type, null);
        }
        price *= Specification.getSpecification().getIntegerOption("model.option.landPriceFactor").getValue();
        price += 100;
        return (int) featureContainer.applyModifier(price, "model.modifier.landPaymentModifier",
                                                    null, getGame().getTurn());
    }

    
    public boolean hasContacted(Player player) {
        return getStance(player) != Stance.UNCONTACTED;
    }

    
    public void setContacted(Player player) {
        if (player != null && player != this) {
            stance.put(player.getId(), Stance.PEACE);
        }
    }

    
    public boolean hasBeenAttackedByPrivateers() {
        return attackedByPrivateers;
    }

    
    public void setAttackedByPrivateers() {
        attackedByPrivateers = true;
    }

    
    public Location getEntryLocation() {
        return entryLocation;
    }

    
    public void setEntryLocation(Location entryLocation) {
        this.entryLocation = entryLocation;
    }

    
    public boolean hasExplored(Tile tile) {
        return tile.isExplored();
    }

    
    public void setExplored(Tile tile) {
        logger.warning("Implemented by ServerPlayer");
    }

    
    public void setExplored(Unit unit) {
        if (getGame() == null || getGame().getMap() == null || unit == null || unit.getLocation() == null
            || unit.getTile() == null || isIndian()) {
            return;
        }
        if (canSeeTiles == null) {
            resetCanSeeTiles();
        }
        Iterator<Position> positionIterator = getGame().getMap().getCircleIterator(unit.getTile().getPosition(), true,
                                                                                   unit.getLineOfSight());
        while (positionIterator.hasNext()) {
            Map.Position p = positionIterator.next();
            canSeeTiles[p.getX()][p.getY()] = true;
        }
    }

    
    public void invalidateCanSeeTiles() {
        canSeeTiles = null;
    }

    
    public boolean resetCanSeeTiles() {
        Map map = getGame().getMap();
        if (map == null) {
            return false;
        }
        canSeeTiles = new boolean[map.getWidth()][map.getHeight()];
        if (!getGameOptions().getBoolean(GameOptions.FOG_OF_WAR)) {
            Iterator<Position> positionIterator = getGame().getMap().getWholeMapIterator();
            while (positionIterator.hasNext()) {
                Map.Position p = positionIterator.next();
                Tile tile = getGame().getMap().getTile(p);
                
                if (tile == null) {
                    continue;
                }
                canSeeTiles[p.getX()][p.getY()] = hasExplored(tile);
            }
        } else {
            Iterator<Unit> unitIterator = getUnitIterator();
            while (unitIterator.hasNext()) {
                Unit unit = unitIterator.next();
                if (!(unit.getLocation() instanceof Tile)) {
                    continue;
                }
                Map.Position position = unit.getTile().getPosition();
                if (position == null) {
                    logger.warning("position == null");
                }
                canSeeTiles[position.getX()][position.getY()] = true;
                
                Iterator<Position> positionIterator = map.getCircleIterator(position, true, unit.getLineOfSight());
                while (positionIterator.hasNext()) {
                    Map.Position p = positionIterator.next();
                    Tile t = map.getTile(p);
                    
                    
                    if(t == null || !hasExplored(t)){
                        continue;
                    }
                    canSeeTiles[p.getX()][p.getY()] = true;
                    
                }
            }
            for (Settlement settlement : getSettlements()) {
                Map.Position position = settlement.getTile().getPosition();
                canSeeTiles[position.getX()][position.getY()] = true;
                
                Iterator<Position> positionIterator = map.getCircleIterator(position, true, settlement
                        .getLineOfSight());
                while (positionIterator.hasNext()) {
                    Map.Position p = positionIterator.next();
                    Tile t = map.getTile(p);
                    
                    
                    if(t == null || !hasExplored(t)){
                        continue;
                    }
                    canSeeTiles[p.getX()][p.getY()] = true;
                    
                }
            }
        }
        return true;
    }

    
    public boolean canSee(Tile tile) {
        if (tile == null) {
            return false;
        }
        if (canSeeTiles == null && !resetCanSeeTiles()) {
            return false;
        }
        return canSeeTiles[tile.getX()][tile.getY()];
    }

    
    public boolean canBuildColonies() {
        return nationType.hasAbility("model.ability.foundColony");
    }

    
    public boolean canHaveFoundingFathers() {
        return nationType.hasAbility("model.ability.electFoundingFather");
    }

    
    public boolean hasFather(FoundingFather someFather) {
        return allFathers.contains(someFather);
    }

    
    public int getFatherCount() {
        return allFathers.size();
    }

    
    public void setCurrentFather(FoundingFather someFather) {
        currentFather = someFather;
    }

    
    public FoundingFather getCurrentFather() {
        return currentFather;
    }

    
    public int getRemainingFoundingFatherCost() {
        return getTotalFoundingFatherCost() - getLiberty();
    }

    
    public int getTotalFoundingFatherCost() {
        int base = Specification.getSpecification()
            .getIntegerOption("model.option.foundingFatherFactor").getValue();
        int count = getFatherCount();
        int previous = 1;
        for (int index = 0; index < count; index++) {
            previous += 2 * (index + 2);
        }
        return previous * base + count;
    }

    
    public void addFather(FoundingFather father) {

        allFathers.add(father);

        addModelMessage(this, ModelMessage.MessageType.DEFAULT,
                        "model.player.foundingFatherJoinedCongress",
                        "%foundingFather%", father.getName(),
                        "%description%", father.getDescription());
        history.add(new HistoryEvent(getGame().getTurn().getNumber(),
                                     HistoryEvent.Type.FOUNDING_FATHER,
                                     "%father%", father.getName()));
        featureContainer.add(father.getFeatureContainer());

        List<AbstractUnit> units = father.getUnits();
        if (units != null) {
            
            for (int index = 0; index < units.size(); index++) {
                AbstractUnit unit = units.get(index);
                String uniqueID = getId() + "newTurn" + father.getId() + index;
                getGame().getModelController().createUnit(uniqueID, getEurope(), this, unit.getUnitType());
            }
        }

        java.util.Map<UnitType, UnitType> upgrades = father.getUpgrades();
        if (upgrades != null) {
            Iterator<Unit> unitIterator = getUnitIterator();
            while (unitIterator.hasNext()) {
                Unit unit = unitIterator.next();
                UnitType newType = upgrades.get(unit.getType());
                if (newType != null) {
                    unit.setType(newType);
                }
            }
        }

        for (Ability ability : father.getFeatureContainer().getAbilities()) {
            if ("model.ability.addTaxToBells".equals(ability.getId())) {
                updateAddTaxToLiberty();
            }
        }

        for (String event : father.getEvents().keySet()) {
            if (event.equals("model.event.resetNativeAlarm")) {
                
                for (Player player : getGame().getPlayers()) {
                    if (!player.isEuropean() && player.getTension(this) != null) {
                        player.getTension(this).setValue(0);
                        for (IndianSettlement is : player.getIndianSettlements()) {
                            if (is.getAlarm(this) != null) {
                                is.getAlarm(this).setValue(0);
                            }
                        }
                    }
                }
            } else if (event.equals("model.event.boycottsLifted")) {
                for (GoodsType goodsType : FreeCol.getSpecification().getGoodsTypeList()) {
                    resetArrears(goodsType);
                }
            } else if (event.equals("model.event.freeBuilding")) {
                BuildingType type = FreeCol.getSpecification().getBuildingType(father.getEvents().get(event));
                for (Colony colony : getColonies()) {
                    if (colony.canBuild(type)) {
                        
                        
                        String taskIDplus = colony.getId() + "buildBuilding" + father.getId();
                        Building building = getGame().getModelController().createBuilding(taskIDplus, colony, type);
                        colony.addBuilding(building);
                    }
                }
            } else if (event.equals("model.event.seeAllColonies")) {
                exploreAllColonies();
            } else if (event.equals("model.event.increaseSonsOfLiberty")) {
                int value = Integer.parseInt(father.getEvents().get(event));
                for (Colony colony : getColonies()) {
                    
                    int requiredLiberty = ((colony.getSoL() + value) * Colony.LIBERTY_PER_REBEL *
                                           colony.getUnitCount()) / 100;
                    colony.addGoods(Goods.BELLS, requiredLiberty - colony.getGoodsCount(Goods.BELLS));
                }
            } else if (event.equals("model.event.newRecruits")) {
                for (int index = 0; index < Europe.RECRUIT_COUNT; index++) {
                    UnitType recruitable = getEurope().getRecruitable(index);
                    if (featureContainer.hasAbility("model.ability.canNotRecruitUnit", recruitable)) {
                        getEurope().setRecruitable(index, generateRecruitable(getId() + "slot." + Integer.toString(index+1)));
                    }
                }
            }
        }
    }

    
    public void endTurn() {
        removeModelMessages();
        resetCanSeeTiles();
    }

    
    public boolean canMoveToEurope() {
        return getEurope() != null;
    }

    
    public Europe getEurope() {
        return europe;
    }

    
    public String getEuropeName() {
        if (europe == null) {
            return null;
        } else {
            return Messages.message(nationID + ".europe");
        }
    }

    
    public Monarch getMonarch() {
        return monarch;
    }

    
    public void setMonarch(Monarch monarch) {
        this.monarch = monarch;
    }

    
    public int getGold() {
        return gold;
    }

    
    public void setGold(int newGold) {
        gold = newGold;
    }

    
    public void modifyGold(int amount) {
        if (this.gold == -1) {
            return;
        }
        if ((gold + amount) >= 0) {
            modifyScore((gold + amount) / 1000 - gold / 1000);
            gold += amount;
        } else {
            
            
            
            
            
            logger.warning("Cannot add " + amount + " gold for " + this + ": would be negative!");
            gold = 0;
        }
    }

    
    public Iterator<Unit> getUnitIterator() {
        return units.values().iterator();
    }

    public List<Unit> getUnits() {
        return new ArrayList<Unit>(units.values());
    }

    
    public int getNumberOfKingLandUnits() {
        int n = 0;
        for (Unit unit : getUnits()) {
            if (unit.hasAbility("model.ability.refUnit") && !unit.isNaval()) {
                n++;
            }
        }
        return n;
    }

    
    public boolean hasManOfWar() {
        Iterator<Unit> it = getUnitIterator();
        while (it.hasNext()) {
            Unit unit = it.next();
            if ("model.unit.manOWar".equals(unit.getType().getId())) {
                return true;
            }
        }
        return false;
    }

    
    public Unit getNextActiveUnit() {
        return nextActiveUnitIterator.next();
    }

    
    public Unit getNextGoingToUnit() {
        return nextGoingToUnitIterator.next();
    }

    
    public boolean hasNextActiveUnit() {
        return nextActiveUnitIterator.hasNext();
    }

    
    public boolean hasNextGoingToUnit() {
        return nextGoingToUnitIterator.hasNext();
    }

    
    public String getName() {
        return name;
    }

    
    public String toString() {
        return getName();
    }

    
    public void setName(String newName) {
        this.name = newName;
    }

    
    public NationType getNationType() {
        return nationType;
    }

    
    public void setNationType(NationType newNationType) {
        if (nationType != null) {
            featureContainer.remove(nationType.getFeatureContainer());
        }
        nationType = newNationType;
        featureContainer.add(newNationType.getFeatureContainer());
    }

    
    public Nation getNation() {
        return FreeCol.getSpecification().getNation(nationID);
    }

    
    public void setNation(Nation newNation) {
        Nation oldNation = getNation();
        nationID = newNation.getId();
        getGame().getNationOptions().getNations().put(oldNation, NationState.AVAILABLE);
        getGame().getNationOptions().getNations().put(newNation, NationState.NOT_AVAILABLE);
    }

    
    public String getNationID() {
        return nationID;
    }

    
    public String getNationAsString() {
        return (playerType == PlayerType.REBEL
                || playerType == PlayerType.INDEPENDENT)
            ? independentNationName
            : Messages.message(nationID + ".name");
    }

    
    public final String getRulerName() {
        return Messages.message(nationID + ".ruler");
    }

    
    public Color getColor() {
        return color;
    }

    
    public void setColor(Color c) {
        color = c;
    }

    
    public boolean isReady() {
        return ready;
    }

    
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    
    public Location getRepairLocation(Unit unit) {
        if (!unit.isNaval()) {
            throw new IllegalArgumentException("Repair for non-naval unit!?!");
        } else if (unit.getTile() == null) {
            throw new IllegalArgumentException("Repair for unit not on the map!?!");
        }

        Location closestLocation = null;
        int shortestDistance = Integer.MAX_VALUE;
        for (Colony colony : getColonies()) {
            if (colony == null || colony.getTile() == unit.getTile()) {
                
                
                
                continue;
            }
            int distance;
            if (colony.hasAbility("model.ability.repairUnits")) {
                
                
                
                PathNode pn = getGame().getMap().findPath(unit, unit.getTile(),
                                                          colony.getTile());
                if (pn != null && (distance = pn.getTotalTurns()) < shortestDistance) {
                    closestLocation = colony;
                    shortestDistance = distance;
                }
            }
        }
        if (closestLocation != null) {
            return closestLocation;
        }
        Tile tile = unit.getTile();
        return ((tile.getColony() != null && tile.getColony().isConnected())
                || tile.isConnected()) ? getEurope() : null;
    }

    public void incrementLiberty(int amount) {
        liberty += amount;
    }

    public void incrementImmigration(int amount) {
        immigration += amount;
    }

    
    public void reduceImmigration() {
        if (!canRecruitUnits()) {
            return;
        }

        int cost = getGameOptions().getBoolean(GameOptions.SAVE_PRODUCTION_OVERFLOW)
            ? immigrationRequired : immigration;

        if (cost > immigration) {
            immigration = 0;
        } else {
            immigration -= cost;
        }
    }

    
    public int getImmigration() {
        return (canRecruitUnits()) ? immigration : 0;
    }

    
    public void setImmigration(int immigration) {
        if (canRecruitUnits()) {
            this.immigration = immigration;
        }
    }


    
    public UnitType generateRecruitable(String taskId) {
        ArrayList<RandomChoice<UnitType>> recruitables = new ArrayList<RandomChoice<UnitType>>();
        for (UnitType unitType : FreeCol.getSpecification().getUnitTypeList()) {
            if (unitType.isRecruitable()
                && !getFeatureContainer().hasAbility("model.ability.canNotRecruitUnit",
                                                     unitType)) {
                int prob = unitType.getRecruitProbability();
                recruitables.add(new RandomChoice<UnitType>(unitType, prob));
            }
        }
        int total = RandomChoice.getTotalProbability(recruitables);
        int random = getGame().getModelController().getRandom(taskId, total);
        return RandomChoice.select(recruitables, random);
    }

    
    public final List<TradeRoute> getTradeRoutes() {
        return tradeRoutes;
    }

    
    public final void setTradeRoutes(final List<TradeRoute> newTradeRoutes) {
        this.tradeRoutes = newTradeRoutes;
    }

    
    public final void resetTradeRouteCounts() {
        Iterator<Unit> unitIterator = getUnitIterator();

        for (TradeRoute tradeRoute : tradeRoutes) tradeRoute.setCount(0);
        while (unitIterator.hasNext()) {
            TradeRoute tradeRoute = unitIterator.next().getTradeRoute();

            if (tradeRoute != null) {
                tradeRoute.setCount(1 + tradeRoute.getCount());
            }
        }
    }

    
    public boolean checkEmigrate() {
        if (!canRecruitUnits()) {
            return false;
        }
        return getImmigrationRequired() <= immigration;
    }

    
    public int getImmigrationRequired() {
        return (canRecruitUnits()) ? immigrationRequired : 0;
    }

    
    public void setImmigrationRequired(int immigrationRequired) {
        if (canRecruitUnits()) {
            this.immigrationRequired = immigrationRequired;
        }
    }

    
    public void updateImmigrationRequired() {
        if (!canRecruitUnits()) {
            return;
        }
        immigrationRequired += (int) featureContainer
            .applyModifier(Specification.getSpecification()
                           .getIntegerOption("model.option.crossesIncrement").getValue(),
                           "model.modifier.religiousUnrestBonus");
        
        
        
        
        
    }

    
    public boolean canRecruitUnits() {
        return playerType == PlayerType.COLONIAL;
    }

    
    public void modifyTension(Player player, int addToTension) {
        modifyTension(player, addToTension, null);
    }

    public void modifyTension(Player player, int addToTension, IndianSettlement origin) {
        if (player == this || player == null) {
            return;
        }
        
        if (tension.get(player) == null) {
            tension.put(player, new Tension(addToTension));
        } else {
            tension.get(player).modify(addToTension);
        }

        
        if(origin != null && origin.getOwner() != this){
            return;
        }
        
        
        
        
        if (isIndian()) {
            for (Settlement settlement : settlements) {
                
                if (origin != null && origin.equals(settlement)){
                    continue;
                }
                
                if (!(settlement instanceof IndianSettlement)){
                    throw new IllegalStateException("Indian player owns non indian settlement");
                }
                ((IndianSettlement) settlement).propagatedAlarm(player, addToTension);
            }
        }
    }

    
    public void setTension(Player player, Tension newTension) {
        if (player == this || player == null) {
            return;
        }
        tension.put(player, newTension);
    }

    
    public Tension getTension(Player player) {
        if (player == null) {
            return new Tension();
        } else {
            Tension newTension = tension.get(player);
            if (newTension == null) {
                newTension = new Tension(0);
            }
            tension.put(player, newTension);
            return newTension;
        }
    }

    
    public void surrenderTo(Player player) {
        if(!isIndian()){
            logger.warning("Only indians should surrender");
            return;
        }
        changeRelationWithPlayer(player, Stance.PEACE);
        getTension(player).setValue(Tension.SURRENDED);
    }

    
    public final List<HistoryEvent> getHistory() {
        return history;
    }

    
    public final void setHistory(final List<HistoryEvent> newHistory) {
        this.history = newHistory;
    }

    
    public int getOutpostValue(Tile t) {
        Market market = getMarket();
        if (t.getType().canSettle() && t.getSettlement() == null) {
            boolean nearbyTileIsOcean = false;
            float advantages = 1f;
            int value = 0;
            for (Tile tile : getGame().getMap().getSurroundingTiles(t, 1)) {
                if (tile.getColony() != null) {
                    
                    return 0;
                } else if (tile.getSettlement() != null) {
                    
                    SettlementType type = ((IndianNationType) tile.getSettlement().getOwner().getNationType())
                        .getTypeOfSettlement();
                    if (type == SettlementType.INCA_CITY || type == SettlementType.AZTEC_CITY) {
                        
                        advantages *= 0.25f;
                    } else {
                        advantages *= 0.5f;
                    }
                } else {
                    if (tile.isConnected()) {
                        nearbyTileIsOcean = true;
                    }
                    if (tile.getType()!=null) {
                        for (AbstractGoods production : tile.getType().getProduction()) {
                            GoodsType type = production.getType();
                            int potential = market.getSalePrice(type, tile.potential(type, null));
                            if (tile.getOwner() != null &&
                                tile.getOwner() != getGame().getCurrentPlayer()) {
                                
                                if (tile.getOwner().isEuropean()) {
                                    continue;
                                } else {
                                    potential /= 2;
                                }
                            }
                            value = Math.max(value, potential);
                        }
                    }
                }
            }
            
            
            GoodsType secondary = t.secondaryGoods();
            value += market.getSalePrice(secondary,t.potential(secondary, null));
            
            if (nearbyTileIsOcean) {
                return Math.max(0, (int) (value * advantages));
            }
        }
        return 0;
    }

    
    public int getColonyValue(Tile t) {
        
        
        final float MOD_HAS_RESOURCE           = 0.75f;
        final float MOD_NO_PATH                = 0.5f;
        final float MOD_LONG_PATH              = 0.75f;
        final float MOD_FOOD_LOW               = 0.75f;
        final float MOD_FOOD_VERY_LOW          = 0.5f;

        
        final float MOD_BUILD_MATERIAL_MISSING = 0.75f;

        
        final float MOD_ADJ_SETTLEMENT_BIG     = 0.25f;
        final float MOD_ADJ_SETTLEMENT         = 0.5f;
        final float MOD_OWNED_EUROPEAN         = 0.8f;
        final float MOD_OWNED_NATIVE           = 0.9f;

        
        final float MOD_HIGH_PRODUCTION        = 1.2f;
        final float MOD_GOOD_PRODUCTION        = 1.1f;

        
        final float[] MOD_OWN_COLONY     = {0.0f, 0.0f, 0.5f, 1.25f, 1.1f};
        final float[] MOD_ENEMY_COLONY   = {0.0f, 0.0f, 0.6f, 0.7f,  0.8f};
        final float[] MOD_NEUTRAL_COLONY = {0.0f, 0.0f, 0.9f, 0.95f, 1.0f};
        final float[] MOD_ENEMY_UNIT     = {0.0f, 0.5f, 0.6f, 0.7f,  0.8f};

        final int LONG_PATH_TURNS = 3;
        final int PRIMARY_GOODS_VALUE = 30;
        
        
        final int GOOD_PRODUCTION = 4;
        final int HIGH_PRODUCTION = 8;

        
        
        final int FOOD_LOW = 4;
        final int FOOD_VERY_LOW = 2;
        
        
        
        
        if (!t.getType().canSettle() || t.getSettlement() != null) {
            return 0;
        }

        
        for (Tile tile : getGame().getMap().getSurroundingTiles(t, 1)) {
            if (tile.getColony() != null) {
                return 0;
            }
        }

        
        int value = t.potential(t.primaryGoods(), null) * PRIMARY_GOODS_VALUE;
        

        
        float advantage = 1f;

        
        TypeCountMap<GoodsType> buildingMaterialMap = new TypeCountMap<GoodsType>();
        TypeCountMap<GoodsType> foodMap = new TypeCountMap<GoodsType>();
        for (GoodsType type : FreeCol.getSpecification().getGoodsTypeList()) {
            if (type.isRawBuildingMaterial()) {
                buildingMaterialMap.incrementCount(type, 0);
            } else if (type.isFoodType()) {
                foodMap.incrementCount(type, 0);
            }
        }

        
        
        if (t.hasResource()) {
            advantage *= MOD_HAS_RESOURCE;
        }

        
        
        
        final PathNode n = getGame().getMap().findPathToEurope(t);
        if (n == null) {
            
            
            advantage *= MOD_NO_PATH;
        } else if (n.getTotalTurns() > LONG_PATH_TURNS) {
            advantage *= MOD_LONG_PATH;
        }

        boolean supportingColony = false;
        Iterator<Position> it;
        for (int radius = 1; radius < 5; radius++) {
            it = getGame().getMap().getCircleIterator(t.getPosition(), false, radius);
            while (it.hasNext()) {
                Tile tile = getGame().getMap().getTile(it.next());
                Settlement set = tile.getSettlement(); 
                Colony col = tile.getColony(); 
                
                if (radius==1) {
                    
                    if (set != null) {
                        
                        SettlementType type = ((IndianNationType) set.getOwner().getNationType())
                            .getTypeOfSettlement();
                        if (type == SettlementType.INCA_CITY || type == SettlementType.AZTEC_CITY) {
                            
                            advantage *= MOD_ADJ_SETTLEMENT_BIG;
                        } else {
                            advantage *= MOD_ADJ_SETTLEMENT;
                        }
                        
                    
                    } else {
                        
                        if (tile.getOwner() != null && tile.getOwner() != this) {
                            if (tile.getOwner().isEuropean()) {
                                advantage *= MOD_OWNED_EUROPEAN;
                            } else {
                                advantage *= MOD_OWNED_NATIVE;
                            }
                        }
                        
                        
                        if (tile.getType()!=null) {
                            for (AbstractGoods production : tile.getType().getProduction()) {
                                GoodsType type = production.getType();
                                int potential = tile.potential(type, null);
                                value += potential * type.getInitialSellPrice();
                                
                                
                                int highProductionValue = 0;
                                if (potential > HIGH_PRODUCTION) {
                                    advantage *= MOD_HIGH_PRODUCTION;
                                    highProductionValue = 2;
                                } else if (potential > GOOD_PRODUCTION) {
                                    advantage *= MOD_GOOD_PRODUCTION;
                                    highProductionValue = 1;
                                }
                                if (type.isRawBuildingMaterial()) {
                                    buildingMaterialMap.incrementCount(type, highProductionValue);
                                } else if (type.isFoodType()) {
                                    foodMap.incrementCount(type, highProductionValue);
                                }
                            }
                        }
                    }

                
                } else {
                    if (value <= 0) {
                        
                        return 0;
                    }
                    if (col != null) {
                        
                        if (col.getOwner()==this) {
                            if (!supportingColony) {
                                supportingColony = true;
                                advantage *= MOD_OWN_COLONY[radius];
                            }
                        
                        } else {
                            if (getStance(col.getOwner()) == Stance.WAR) {
                                advantage *= MOD_ENEMY_COLONY[radius];
                            } else {
                                advantage *= MOD_NEUTRAL_COLONY[radius];
                            }
                        }
                    }
                }
                
                Iterator<Unit> ui = tile.getUnitIterator();
                while (ui.hasNext()) {
                    Unit u = ui.next();
                    if (u.getOwner() != this && u.isOffensiveUnit() && u.getOwner().isEuropean()
                        && getStance(u.getOwner()) == Stance.WAR) {
                        advantage *= MOD_ENEMY_UNIT[radius];
                    }
                }
            }
        }

        
        for (Integer buildingMaterial : buildingMaterialMap.values()) {
            if (buildingMaterial == 0) {
                advantage *= MOD_BUILD_MATERIAL_MISSING;
            }
        }
        int foodProduction = 0;
        for (Integer food : foodMap.values()) {
            foodProduction += food;
        }
        if (foodProduction < FOOD_VERY_LOW) {
            advantage *= MOD_FOOD_VERY_LOW;
        } else if (foodProduction < FOOD_LOW) {
            advantage *= MOD_FOOD_LOW;
        }
        
        return (int) (value * advantage);
    }

    
    public Stance getStance(Player player) {
        return (player == null || stance.get(player.getId()) == null)
            ? Stance.UNCONTACTED
            : stance.get(player.getId());
    }

    
    public static String getStanceAsString(Stance stance) {
        return Messages.message("model.stance." + stance.toString().toLowerCase());
    }

    
    public void setStance(Player player, Stance newStance) {
        if (player == null) {
            throw new IllegalArgumentException("Player must not be 'null'.");
        }
        if (player == this) {
            throw new IllegalArgumentException("Cannot set the stance towards ourselves.");
        }
        Stance oldStance = stance.get(player.getId());
        if (newStance.equals(oldStance)) {
            return;
        }
        if (newStance == Stance.CEASE_FIRE && oldStance != Stance.WAR) {
            throw new IllegalStateException("Cease fire can only be declared when at war.");
        }
        if (newStance == Stance.UNCONTACTED) {
            throw new IllegalStateException("Attempt to set UNCONTACTED stance");
        }
        stance.put(player.getId(), newStance);
    }

    public void changeRelationWithPlayer(Player player,Stance newStance){
        Stance oldStance = getStance(player);
        
        
        if(newStance == oldStance){
            return;
        }
        
        
        setStance(player, newStance);

        
        int modifier = 0;
        switch(newStance){
        case UNCONTACTED:
            throw new IllegalStateException("Attempt to set UNCONTACTED stance");
        case PEACE:
            if(oldStance == Stance.WAR){
                modifier = Tension.CEASE_FIRE_MODIFIER + Tension.PEACE_TREATY_MODIFIER;
            }
            if(oldStance == Stance.CEASE_FIRE){
                modifier = Tension.PEACE_TREATY_MODIFIER;
            }
            break;
        case CEASE_FIRE:
            if(oldStance == Stance.WAR){
                modifier = Tension.CEASE_FIRE_MODIFIER;
            }
            break;
        case ALLIANCE:
        case WAR:
            
            break;
        }
        modifyTension(player,modifier);
        
        if (player.getStance(this) != newStance) {
            getGame().getModelController().setStance(this, player, newStance);
            player.setStance(this, newStance);

            if(newStance == Stance.WAR){
                switch(oldStance){
                case UNCONTACTED:
                case PEACE:
                    modifier = Tension.TENSION_ADD_DECLARE_WAR_FROM_PEACE;
                    break;
                case CEASE_FIRE:
                    modifier = Tension.TENSION_ADD_DECLARE_WAR_FROM_CEASE_FIRE;
                    break;
                case ALLIANCE:
                case WAR:
                    
                    break;
                }
            }
            player.modifyTension(this, modifier);
        }
    }

    
    public int getRecruitPrice() {
        
        return getEurope().getRecruitPrice();
    }

    
    public int getLiberty() {
        if (!canHaveFoundingFathers()) {
            return 0;
        }
        return liberty;
    }

    
    public int getLibertyProductionNextTurn() {
        int libertyNextTurn = 0;
        for (Colony colony : getColonies()) {
            libertyNextTurn += colony.getProductionOf(Goods.BELLS);
        }
        return libertyNextTurn;
    }

    
    public void newTurn() {

        int newSoL = 0;

        
        if (isIndian()) {
            for (Tension tension1 : tension.values()) {
                if (tension1.getValue() > 0) {
                    tension1.modify(-(4 + tension1.getValue() / 100));
                }
            }
        }

        
        ArrayList<Settlement> settlements = new ArrayList<Settlement>(getSettlements());
        for (Settlement settlement : settlements) {
            logger.finest("Calling newTurn for settlement " + settlement.toString());
            settlement.newTurn();
            if (isEuropean()) {
                Colony colony = (Colony) settlement;
                newSoL += colony.getSoL();
            }
        }

        
        if (isEuropean()) {
            if (!hasAbility("model.ability.independenceDeclared") &&
                getLiberty() >= getTotalFoundingFatherCost() &&
                currentFather != null) {
                
                
                
                liberty -= getGameOptions().getBoolean(GameOptions.SAVE_PRODUCTION_OVERFLOW) ?
                    getTotalFoundingFatherCost() : liberty;
                addFather(currentFather);
                currentFather = null;
            }

            
            
            
            for (Unit unit : new ArrayList<Unit>(units.values())) {
                logger.finest("Calling newTurn for unit " + unit.getName() + " " + unit.getId());
                unit.newTurn();
            }

            if (getEurope() != null) {
                logger.finest("Calling newTurn for player " + getName() + "'s Europe");
                getEurope().newTurn();
            }

            int numberOfColonies = settlements.size();
            if (numberOfColonies > 0) {
                newSoL = newSoL / numberOfColonies;
                if (oldSoL / 10 != newSoL / 10) {
                    if (newSoL > oldSoL) {
                        addModelMessage(this, ModelMessage.MessageType.SONS_OF_LIBERTY, "model.player.SoLIncrease",
                                        "%oldSoL%", String.valueOf(oldSoL), "%newSoL%", String.valueOf(newSoL));
                    } else {
                        addModelMessage(this, ModelMessage.MessageType.SONS_OF_LIBERTY, "model.player.SoLDecrease",
                                        "%oldSoL%", String.valueOf(oldSoL), "%newSoL%", String.valueOf(newSoL));
                    }
                }
            }
            
            oldSoL = newSoL;
        } else {
            for (Iterator<Unit> unitIterator = getUnitIterator(); unitIterator.hasNext();) {
                Unit unit = unitIterator.next();
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest("Calling newTurn for unit " + unit.getName() + " " + unit.getId());
                }
                unit.newTurn();
            }
        }
    }

    private void exploreAllColonies() {
        
        ArrayList<Tile> tiles = new ArrayList<Tile>();
        Iterator<Position> tileIterator = getGame().getMap().getWholeMapIterator();
        while (tileIterator.hasNext()) {
            Tile tile = getGame().getMap().getTile((tileIterator.next()));
            
            if (tile.getColony() == null) {
                continue;
            }
            
            
            
            if(tile.getColony().getOwner() == this){
                continue;
            }

            tiles.add(tile);
            for (Direction direction : Direction.values()) {
                Tile addTile = getGame().getMap().getNeighbourOrNull(direction, tile);
                if (addTile != null) {
                    
                    
                    if(addTile.isExploredBy(this)){
                        continue;
                    }
                    tiles.add(addTile);
                }
            }
        }
        getGame().getModelController().exploreTiles(this, tiles);
    }

    
    public int getArrears(GoodsType type) {
        MarketData data = getMarket().getMarketData(type);
        return (data == null) ? 0 : data.getArrears();
    }

    
    public int getArrears(Goods goods) {
        return getArrears(goods.getType());
    }

    
    public void setArrears(GoodsType goodsType) {
        MarketData data = getMarket().getMarketData(goodsType);
        if (data == null) {
            data = new MarketData(goodsType);
            getMarket().putMarketData(goodsType, data);
        }
        Specification spec = Specification.getSpecification();
        data.setArrears(spec.getIntegerOption("model.option.arrearsFactor").getValue()
                        * data.getPaidForSale());
    }

    
    public void setArrears(Goods goods) {
        setArrears(goods.getType());
    }

    
    public void resetArrears(GoodsType goodsType) {
        MarketData data = getMarket().getMarketData(goodsType);
        if (data == null) {
            data = new MarketData(goodsType);
            getMarket().putMarketData(goodsType, data);
        }
        data.setArrears(0);
    }

    
    public void resetArrears(Goods goods) {
        resetArrears(goods.getType());
    }

    
    public boolean canTrade(GoodsType type) {
        return canTrade(type, Market.EUROPE);
    }

    
    public boolean canTrade(GoodsType type, int marketAccess) {
        MarketData data = getMarket().getMarketData(type);
        if (data == null || data.getArrears() == 0) {
            return true;
        } else if (marketAccess == Market.CUSTOM_HOUSE) {
            if (getGameOptions().getBoolean(GameOptions.CUSTOM_IGNORE_BOYCOTT)) {
                return true;
            } else if (hasAbility("model.ability.customHouseTradesWithForeignCountries")) {
                for (Player otherPlayer : getGame().getPlayers()) {
                    if (otherPlayer != this && otherPlayer.isEuropean()
                        && (getStance(otherPlayer) == Stance.PEACE ||
                            getStance(otherPlayer) == Stance.ALLIANCE)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    
    public boolean canTrade(Goods goods, int marketAccess) {
        return canTrade(goods.getType(), marketAccess);
    }

    
    public boolean canTrade(Goods goods) {
        return canTrade(goods, Market.EUROPE);
    }

    
    public int getTax() {
        return tax;
    }

    
    public void setTax(int amount) {
        if (amount != tax) {
            tax = amount;
            updateAddTaxToLiberty();
        }
    }

    private void updateAddTaxToLiberty() {
        Set<Modifier> libertyBonus = featureContainer.getModifierSet("model.goods.bells");
        for (Ability ability : featureContainer.getAbilitySet("model.ability.addTaxToBells")) {
            FreeColGameObjectType source = ability.getSource();
            if (source != null) {
                for (Modifier modifier : libertyBonus) {
                    if (source.equals(modifier.getSource())) {
                        modifier.setValue(tax);
                        return;
                    }
                }
            }
        }
    }


    
    public int getSales(GoodsType goodsType) {
        MarketData data = getMarket().getMarketData(goodsType);
        if (data == null) {
            return 0;
        } else {
            return data.getSales();
        }
    }

    
    public void modifySales(GoodsType goodsType, int amount) {
        MarketData data = getMarket().getMarketData(goodsType);
        if (data == null) {
            data = new MarketData(goodsType);
            getMarket().putMarketData(goodsType, data);
        }
        int oldSales = data.getSales();
        data.setSales(oldSales + amount);
    }

    
    public boolean hasTraded(GoodsType goodsType) {
        MarketData data = getMarket().getMarketData(goodsType);
        return data != null && data.getTraded();
    }

    
    public Goods getMostValuableGoods() {
        Goods goods = null;
        if (!isEuropean()) {
            return goods;
        }
        int value = 0;
        for (Colony colony : getColonies()) {
            List<Goods> colonyGoods = colony.getCompactGoods();
            for (Goods currentGoods : colonyGoods) {
                if (getArrears(currentGoods) == 0
                    && hasTraded(currentGoods.getType())) {
                    
                    if (currentGoods.getAmount() > 100) {
                        currentGoods.setAmount(100);
                    }
                    int goodsValue = market.getSalePrice(currentGoods);
                    if (goodsValue > value) {
                        value = goodsValue;
                        goods = currentGoods;
                    }
                }
            }
        }
        return goods;
    }

    
    public int getIncomeBeforeTaxes(GoodsType goodsType) {
        MarketData data = getMarket().getMarketData(goodsType);
        if (data == null) {
            return 0;
        } else {
            return data.getIncomeBeforeTaxes();
        }
    }

    
    public void modifyIncomeBeforeTaxes(GoodsType goodsType, int amount) {
        MarketData data = getMarket().getMarketData(goodsType);
        if (data == null) {
            data = new MarketData(goodsType);
            getMarket().putMarketData(goodsType, data);
        }
        int oldAmount = data.getIncomeBeforeTaxes();
        data.setIncomeBeforeTaxes(oldAmount += amount);
    }

    
    public int getIncomeAfterTaxes(GoodsType goodsType) {
        MarketData data = getMarket().getMarketData(goodsType);
        if (data == null) {
            return 0;
        } else {
            return data.getIncomeAfterTaxes();
        }
    }

    
    public void modifyIncomeAfterTaxes(GoodsType goodsType, int amount) {
        MarketData data = getMarket().getMarketData(goodsType);
        if (data == null) {
            data = new MarketData(goodsType);
            getMarket().putMarketData(goodsType, data);
        }
        int oldAmount = data.getIncomeAfterTaxes();
        data.setIncomeAfterTaxes(oldAmount + amount);
    }

    
    public DifficultyLevel getDifficulty() {
        int level = getGame().getGameOptions().getInteger(GameOptions.DIFFICULTY);
        return FreeCol.getSpecification().getDifficultyLevel(level);
    }

    
    public boolean equals(Player o) {
        if (o == null) {
            return false;
        } else if (getId() == null || o.getId() == null) {
            
            
            
            
            
            
            return false;
        } else {
            return getId().equals(o.getId());
        }
    }


    
    public abstract class UnitPredicate {
        public abstract boolean obtains(Unit unit);
    }

    
    public class ActivePredicate extends UnitPredicate {
        
        public boolean obtains(Unit unit) {
            return !unit.isDisposed()
                && unit.getMovesLeft() > 0
                && unit.getState() == UnitState.ACTIVE
                && unit.getDestination() == null
                && unit.getTradeRoute() == null
                && !(unit.getLocation() instanceof WorkLocation)
                && unit.getTile() != null;
        }
    }

    
    public class GoingToPredicate extends UnitPredicate {
        
        public boolean obtains(Unit unit) {
            return !unit.isDisposed()
                && unit.getMovesLeft() > 0
                && (unit.getDestination() != null || unit.getTradeRoute() != null)
                && !(unit.getLocation() instanceof WorkLocation)
                && unit.getTile() != null;
        }
    }

    
    public class UnitIterator implements Iterator<Unit> {

        private Iterator<Unit> unitIterator = null;

        private Player owner;

        private Unit nextUnit = null;

        private UnitPredicate predicate;


        
        public UnitIterator(Player owner, UnitPredicate predicate) {
            this.owner = owner;
            this.predicate = predicate;
        }

        public boolean hasNext() {
            if (nextUnit != null && predicate.obtains(nextUnit)) {
                return true;
            }
            if (unitIterator == null) {
                unitIterator = createUnitIterator();
            }
            while (unitIterator.hasNext()) {
                nextUnit = unitIterator.next();
                if (predicate.obtains(nextUnit)) {
                    return true;
                }
            }
            unitIterator = createUnitIterator();
            while (unitIterator.hasNext()) {
                nextUnit = unitIterator.next();
                if (predicate.obtains(nextUnit)) {
                    return true;
                }
            }
            nextUnit = null;
            return false;
        }

        public Unit next() {
            if (nextUnit == null || !predicate.obtains(nextUnit)) {
                hasNext();
            }
            Unit temp = nextUnit;
            nextUnit = null;
            return temp;
        }

        
        public void remove() {
            throw new UnsupportedOperationException();
        }

        
        private Iterator<Unit> createUnitIterator() {
            ArrayList<Unit> units = new ArrayList<Unit>();
            Map map = getGame().getMap();
            Iterator<Position> tileIterator = map.getWholeMapIterator();
            while (tileIterator.hasNext()) {
                Tile t = map.getTile(tileIterator.next());
                if (t != null && t.getFirstUnit() != null && t.getFirstUnit().getOwner().equals(owner)) {
                    Iterator<Unit> unitIterator = t.getUnitIterator();
                    while (unitIterator.hasNext()) {
                        Unit u = unitIterator.next();
                        Iterator<Unit> childUnitIterator = u.getUnitIterator();
                        while (childUnitIterator.hasNext()) {
                            Unit childUnit = childUnitIterator.next();
                            if (predicate.obtains(childUnit)) {
                                units.add(childUnit);
                            }
                        }
                        if (predicate.obtains(u)) {
                            units.add(u);
                        }
                    }
                }
            }
            return units.iterator();
        }
    }

    
    protected void toXMLImpl(XMLStreamWriter out, Player player, boolean showAll, boolean toSavedGame)
        throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());
        out.writeAttribute("ID", getId());
        out.writeAttribute("index", String.valueOf(index));
        out.writeAttribute("username", name);
        out.writeAttribute("nationID", nationID);
        if (nationType != null) {
            out.writeAttribute("nationType", nationType.getId());
        }
        out.writeAttribute("color", Integer.toString(color.getRGB()));
        out.writeAttribute("admin", Boolean.toString(admin));
        out.writeAttribute("ready", Boolean.toString(ready));
        out.writeAttribute("dead", Boolean.toString(dead));
        out.writeAttribute("playerType", playerType.toString());
        out.writeAttribute("ai", Boolean.toString(ai));
        out.writeAttribute("tax", Integer.toString(tax));
        out.writeAttribute("numberOfSettlements", Integer.toString(numberOfSettlements));

        if (getGame().isClientTrusted() || showAll || equals(player)) {
            out.writeAttribute("gold", Integer.toString(gold));
            out.writeAttribute("immigration", Integer.toString(immigration));
            out.writeAttribute("liberty", Integer.toString(liberty));
            if (currentFather != null) {
                out.writeAttribute("currentFather", currentFather.getId());
            }
            out.writeAttribute("immigrationRequired", Integer.toString(immigrationRequired));
            out.writeAttribute("attackedByPrivateers", Boolean.toString(attackedByPrivateers));
            out.writeAttribute("oldSoL", Integer.toString(oldSoL));
            out.writeAttribute("score", Integer.toString(score));
        } else {
            out.writeAttribute("gold", Integer.toString(-1));
            out.writeAttribute("immigration", Integer.toString(-1));
            out.writeAttribute("liberty", Integer.toString(-1));
            out.writeAttribute("immigrationRequired", Integer.toString(-1));
        }
        if (newLandName != null) {
            out.writeAttribute("newLandName", newLandName);
        }
        if (independentNationName != null) {
            out.writeAttribute("independentNationName", independentNationName);
        }
        if (entryLocation != null) {
            out.writeAttribute("entryLocation", entryLocation.getId());
        }
        

        for (Entry<Player, Tension> entry : tension.entrySet()) {
            out.writeStartElement(TENSION_TAG);
            out.writeAttribute("player", entry.getKey().getId());
            out.writeAttribute("value", String.valueOf(entry.getValue().getValue()));
            out.writeEndElement();
        }

        for (Entry<String, Stance> entry : stance.entrySet()) {
            out.writeStartElement(STANCE_TAG);
            out.writeAttribute("player", entry.getKey());
            out.writeAttribute("value", entry.getValue().toString());
            out.writeEndElement();
        }

        for (HistoryEvent event : history) {
            event.toXML(out, this);
        }

        for (TradeRoute route : getTradeRoutes()) {
            route.toXML(out, this);
        }

        if (market != null) {
            market.toXML(out, player, showAll, toSavedGame);
        }

        if (getGame().isClientTrusted() || showAll || equals(player)) {
            out.writeStartElement(FOUNDING_FATHER_TAG);
            out.writeAttribute(ARRAY_SIZE, Integer.toString(allFathers.size()));
            int index = 0;
            for (FoundingFather father : allFathers) {
                out.writeAttribute("x" + Integer.toString(index), father.getId());
                index++;
            }
            out.writeEndElement();

            if (europe != null) {
                europe.toXML(out, player, showAll, toSavedGame);
            }
            if (monarch != null) {
                monarch.toXML(out, player, showAll, toSavedGame);
            }
        }

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setId(in.getAttributeValue(null, "ID"));
        index = Integer.parseInt(in.getAttributeValue(null, "index"));
        name = in.getAttributeValue(null, "username");
        nationID = in.getAttributeValue(null, "nationID");
        if (!name.equals(UNKNOWN_ENEMY)) {
            nationType = FreeCol.getSpecification().getNationType(in.getAttributeValue(null, "nationType"));
        }
        color = new Color(Integer.parseInt(in.getAttributeValue(null, "color")));
        admin = getAttribute(in, "admin", false);
        gold = Integer.parseInt(in.getAttributeValue(null, "gold"));
        immigration = getAttribute(in, "immigration", 0);
        liberty = getAttribute(in, "liberty", 0);
        oldSoL = getAttribute(in, "oldSoL", 0);
        score = getAttribute(in, "score", 0);
        ready = getAttribute(in, "ready", false);
        ai = getAttribute(in, "ai", false);
        dead = getAttribute(in, "dead", false);
        tax = Integer.parseInt(in.getAttributeValue(null, "tax"));
        numberOfSettlements = getAttribute(in, "numberOfSettlements", 0);
        playerType = Enum.valueOf(PlayerType.class, in.getAttributeValue(null, "playerType"));
        currentFather = FreeCol.getSpecification().getType(in, "currentFather", FoundingFather.class, null);
        immigrationRequired = getAttribute(in, "immigrationRequired", 12);
        newLandName = getAttribute(in, "newLandName", null);
        independentNationName = getAttribute(in, "independentNationName", null);

        attackedByPrivateers = getAttribute(in, "attackedByPrivateers", false);
        final String entryLocationStr = in.getAttributeValue(null, "entryLocation");
        if (entryLocationStr != null) {
            entryLocation = (Location) getGame().getFreeColGameObject(entryLocationStr);
            if (entryLocation == null) {
                entryLocation = new Tile(getGame(), entryLocationStr);
            }
        }

        featureContainer = new FeatureContainer();
        if (nationType != null) {
            featureContainer.add(nationType.getFeatureContainer());
        }
        switch (playerType) {
        case REBEL:
        case INDEPENDENT:
            featureContainer.addAbility(new Ability("model.ability.independenceDeclared"));
            break;
        default:
            
            break;
        }

        tension.clear();
        stance.clear();
        allFathers.clear();
        europe = null;
        monarch = null;
        history.clear();
        tradeRoutes.clear();
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if (in.getLocalName().equals(TENSION_TAG)) {
                Player player = (Player) getGame().getFreeColGameObject(in.getAttributeValue(null, "player"));
                tension.put(player, new Tension(getAttribute(in, "value", 0)));
                in.nextTag(); 
            } else if (in.getLocalName().equals(FOUNDING_FATHER_TAG)) {
                int length = Integer.parseInt(in.getAttributeValue(null, ARRAY_SIZE));
                for (int index = 0; index < length; index++) {
                    String fatherId = in.getAttributeValue(null, "x" + String.valueOf(index));
                    FoundingFather father = FreeCol.getSpecification().getFoundingFather(fatherId);
                    allFathers.add(father);
                    
                    featureContainer.add(father.getFeatureContainer());
                }
                in.nextTag();
            } else if (in.getLocalName().equals(STANCE_TAG)) {
                String playerId = in.getAttributeValue(null, "player");
                stance.put(playerId, Enum.valueOf(Stance.class, in.getAttributeValue(null, "value")));
                in.nextTag(); 
            } else if (in.getLocalName().equals(Europe.getXMLElementTagName())) {
                europe = updateFreeColGameObject(in, Europe.class);
            } else if (in.getLocalName().equals(Monarch.getXMLElementTagName())) {
                monarch = updateFreeColGameObject(in, Monarch.class);
            } else if (in.getLocalName().equals(HistoryEvent.getXMLElementTagName())) {
                HistoryEvent event = new HistoryEvent();
                event.readFromXMLImpl(in);
                getHistory().add(event);
            } else if (in.getLocalName().equals(TradeRoute.getXMLElementTagName())) {
                TradeRoute route = updateFreeColGameObject(in, TradeRoute.class);
                tradeRoutes.add(route);
            } else if (in.getLocalName().equals(Market.getXMLElementTagName())) {
                market = updateFreeColGameObject(in, Market.class);
            } else {
                logger.warning("Unknown tag: " + in.getLocalName() + " loading player");
                in.nextTag();
            }
        }

        
        if (!in.getLocalName().equals(Player.getXMLElementTagName())) {
            logger.warning("Error parsing xml: expecting closing tag </" + Player.getXMLElementTagName() + "> "
                           + "found instead: " + in.getLocalName());
        }

        if (market == null) {
            market = new Market(getGame(), this);
        }
        invalidateCanSeeTiles();
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
        return "player";
    }

}
