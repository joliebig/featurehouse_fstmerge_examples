

package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Unit.UnitState;
import net.sf.freecol.common.model.pathfinding.CostDecider;
import net.sf.freecol.common.model.pathfinding.GoalDecider;
import net.sf.freecol.common.model.Map.CircleIterator;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Map.Position;
import net.sf.freecol.common.model.Player.Stance;
import net.sf.freecol.common.model.Settlement.SettlementType;

import org.w3c.dom.Element;


public final class Tile extends FreeColGameObject implements Location, Named, Ownable {

    private static final Logger logger = Logger.getLogger(Tile.class.getName());

    private static final String UNITS_TAG_NAME = "units";

    public static final String UNIT_CHANGE = "UNIT_CHANGE";

    private TileType type;
    
    private int x, y;

    
    private Player owner;

    
    private Settlement settlement;

    
    private TileItemContainer tileItemContainer;

    
    private List<Unit> units = Collections.emptyList();


    
    private Settlement owningSettlement;

    
    private java.util.Map<Player, PlayerExploredTile> playerExploredTiles;

    
    private Region region;

    
    private boolean connected = false;

    
    private Boolean moveToEurope;

    
    private int style;

    
    public Tile(Game game, TileType type, int locX, int locY) {
        super(game);

        this.type = type;

        x = locX;
        y = locY;

        owningSettlement = null;
        settlement = null;

        if (!isViewShared()) {
            playerExploredTiles = new HashMap<Player, PlayerExploredTile>();
        }
    }

    
    public Tile(Game game, XMLStreamReader in) throws XMLStreamException {
        super(game, in);

        if (!isViewShared()) {
            playerExploredTiles = new HashMap<Player, PlayerExploredTile>();
        }

        readFromXML(in);
    }

    
    public Tile(Game game, Element e) {
        super(game, e);

        if (!isViewShared()) {
            playerExploredTiles = new HashMap<Player, PlayerExploredTile>();
        }

        readFromXMLElement(e);
    }

    
    public Tile(Game game, String id) {
        super(game, id);

        if (!isViewShared()) {
            playerExploredTiles = new HashMap<Player, PlayerExploredTile>();
        }
    }

    

    public boolean isViewShared() {
        return (getGame().getViewOwner() != null);
    }

    
    public Region getRegion() {
        return region;
    }

    
    public void setRegion(final Region newRegion) {
        this.region = newRegion;
    }

    
    public Region getDiscoverableRegion() {
        if (region == null) {
            return null;
        } else {
            return region.getDiscoverableRegion();
        }
    }

    
    public String getName() {
        if (isViewShared()) {
            if (isExplored()) {
                return getType().getName();
            } else {
                return Messages.message("unexplored");
            }
        } else {
            Player player = getGame().getCurrentPlayer();
            if (player != null) {
                PlayerExploredTile pet = playerExploredTiles.get(player);
                if (pet != null && pet.isExplored()) {
                    return getType().getName();
                }
                return Messages.message("unexplored");
            } else {
                logger.warning("player == null");
                return "";
            }
        }
    }

    
    public String getLabel() {
        if (tileItemContainer == null) {
            return getName();
        } else {
            return getName() + tileItemContainer.getLabel();
        }
    }
    
    
    public String getLocationName() {
        if (settlement == null) {
            Settlement nearSettlement = null;
            int radius = 8; 
            CircleIterator mapIterator = getMap().getCircleIterator(getPosition(), true, radius);
            while (mapIterator.hasNext()) {
                nearSettlement = getMap().getTile(mapIterator.nextPosition()).getSettlement();
                if (nearSettlement != null) {
                    return getName() + " ("
                        + Messages.message("nearLocation", "%location%",
                                           nearSettlement.getName()) + ")";
                }
            }
            if (region != null && region.getName() != null) {
                return getName() + " (" + region.getName() + ")";
            } else {
                return getName();
            }
        } else {
            return settlement.getLocationName();
        }
    }

    
    public int getStyle() {
        return style;
    }

    
    public void setStyle(final int newStyle) {
        this.style = newStyle;
    }

    
    public int getDistanceTo(Tile tile) {
        return getGame().getMap().getDistance(getPosition(), tile.getPosition());
    }

    
    public GoodsContainer getGoodsContainer() {
        return null;
    }

    
    public TileItemContainer getTileItemContainer() {
        return tileItemContainer;
    }

    
    public void setTileItemContainer(TileItemContainer newTileItemContainer) {
        tileItemContainer = newTileItemContainer;
    }

    
    public List<TileImprovement> getTileImprovements() {
        if (tileItemContainer == null) {
            return Collections.emptyList();
        } else {
            return tileItemContainer.getImprovements();
        }
    }

    
    public List<TileImprovement> getCompletedTileImprovements() {
        if (tileItemContainer == null) {
            return Collections.emptyList();
        } else {
            List<TileImprovement> result = new ArrayList<TileImprovement>();
            for (TileImprovement improvement : tileItemContainer.getImprovements()) {
                if (improvement.getTurnsToComplete() == 0) {
                    result.add(improvement);
                }
            }
            return result;
        }
    }

    
    public Unit getDefendingUnit(Unit attacker) {
        
        Unit tileDefender = null;
        float defencePower = -1.0f;

        for (Unit nextUnit : units) {
            if (isLand() != nextUnit.isNaval()) {
                
                
                float tmpPower = getGame().getCombatModel().getDefencePower(attacker,nextUnit);
                if (tmpPower > defencePower || tileDefender == null) {
                    tileDefender = nextUnit;
                    defencePower = tmpPower;
                }
            }
        }

        if ((tileDefender == null || !tileDefender.isDefensiveUnit()) &&
            getSettlement() != null) {
            
            Unit settlementDefender = settlement.getDefendingUnit(attacker);
            
            if (settlementDefender != null && 
                getGame().getCombatModel().getDefencePower(attacker, settlementDefender) > defencePower) {
                return settlementDefender;
            }
        }
        return tileDefender;
    }

    
    public int getMoveCost(Tile fromTile) {
        
        
        if (!isLand() || tileItemContainer == null) {
            return getType().getBasicMoveCost();
        } else {
            return tileItemContainer.getMoveCost(getType().getBasicMoveCost(), fromTile);
        }
    }

    
    public void disposeAllUnits() {
        
        
        for (Unit unit : new ArrayList<Unit>(units)) {
            unit.dispose();
        }
        updatePlayerExploredTiles();
    }
    
    public void dispose() {
        if (settlement != null) {
            settlement.dispose();
        }
        if (tileItemContainer != null) {
            tileItemContainer.dispose();
        }
        
        super.dispose();
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

    
    public int getTotalUnitCount() {
        int result = 0;
        for (Unit unit : units) {
            result++;
            result += unit.getUnitCount();
        }
        return result;
    }

    
    public boolean contains(Locatable locatable) {
        if (locatable instanceof Unit) {
            return units.contains(locatable);
        } else if (locatable instanceof TileItem) {
            return tileItemContainer != null && tileItemContainer.contains((TileItem) locatable);
        }

        logger.warning("Tile.contains(" + locatable + ") Not implemented yet!");

        return false;
    }

    
    public Map getMap() {
        return getGame().getMap();
    }

    
    public boolean isConnected() {
        return (connected || (type != null && type.isConnected()));
    }

    
    public void setConnected(final boolean newConnected) {
        this.connected = newConnected;
    }

    
    public boolean canMoveToEurope() {
        if (moveToEurope != null) {
            return moveToEurope;
        } else if (type == null) {
            return false;
        } else {
            return type.hasAbility("model.ability.moveToEurope");
        }
    }

    
    public void setMoveToEurope(final Boolean newMoveToEurope) {
        this.moveToEurope = newMoveToEurope;
    }

    
    public boolean isExplored() {
        return type != null;
    }

    
    public boolean isLand() {
        return type != null && !type.isWater();
    }

    
    public boolean isForested() {
        return type != null && type.isForested();
    }

    
    public boolean hasRiver() {
        return tileItemContainer != null && getTileItemContainer().getRiver() != null;
    }

    
    public boolean hasResource() {
        return tileItemContainer != null && getTileItemContainer().getResource() != null;
    }

    
    public boolean hasLostCityRumour() {
        return tileItemContainer != null && getTileItemContainer().getLostCityRumour() != null;
    }

    
    public boolean hasRoad() {
        return tileItemContainer != null && getTileItemContainer().getRoad() != null;
    }

    
    public TileImprovement getRoad() {
        if (tileItemContainer == null) {
            return null;
        } else {
            return getTileItemContainer().getRoad();
        }
    }

    
    public TileType getType() {
        return type;
    }

    
    public Player getOwner() {
        return owner;
    }

    
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    
    public boolean claimable(Player player) {
        Player owner = getOwner();
        return owner == null || owner == player || player.getLandPrice(this) >= 0;
    }

    
    public TileImprovement getRiver() {
        if (tileItemContainer == null) {
            return null;
        } else {
            return tileItemContainer.getRiver();
        }
    }

    
    public LostCityRumour getLostCityRumour() {
        if (tileItemContainer == null) {
            return null;
        } else {
            return tileItemContainer.getLostCityRumour();
        }
    }

    
    public void removeLostCityRumour() {
        if (tileItemContainer != null) {
            tileItemContainer.removeAll(LostCityRumour.class);
        }
    }

    
    public int getRiverStyle() {
        if (tileItemContainer == null) {
            return 0;
        } else {
            TileImprovement river = tileItemContainer.getRiver();
            if (river == null) {
                return 0;
            } else {
                return river.getStyle();
            }
        }
    }

    
    public Tile getNeighbourOrNull(Direction d) {
        return getMap().getNeighbourOrNull(d, this);
    }

    
    public boolean hasUnexploredAdjacent() {
        Iterator<Position> tileIterator = getMap().getAdjacentIterator(getPosition());
        while (tileIterator.hasNext()) {
            Tile t = getMap().getTile(tileIterator.next());
            if (!t.isExplored()) {
                return true;
            }
        }
        return false;
    }

    
    public boolean isCoast() {
        for (Direction direction : Direction.values()) {
            Tile otherTile = getMap().getNeighbourOrNull(direction, this);
            if (otherTile != null && otherTile.isLand()!=this.isLand()) {
                return true;
            }
        }
        return false;
    }

    
    public void setSettlement(Settlement s) {
        settlement = s;
        owningSettlement = s;
    }

    
    public Settlement getSettlement() {
        return settlement;
    }

    
    public Colony getColony() {

        if (settlement != null && settlement instanceof Colony) {
            return ((Colony) settlement);
        }

        return null;
    }

    
    public void setOwningSettlement(Settlement owner) {
        this.owningSettlement = owner;
    }

    
    public Settlement getOwningSettlement() {
        return owningSettlement;
    }

    
    public void setResource(ResourceType r) {
        if (r == null) {
            return;
        }
        if (tileItemContainer == null) {
            tileItemContainer = new TileItemContainer(getGame(), this);
        }

        Resource resource = new Resource(getGame(), this, r);
        tileItemContainer.addTileItem(resource);
        
        updatePlayerExploredTiles();
    }
    
    
    public void setType(TileType t) {
        if (t == null) {
            throw new IllegalArgumentException("Tile type must not be null");
        }
        type = t;
        if (tileItemContainer != null) {
            tileItemContainer.removeIncompatibleImprovements();
        }
        if (!isLand()) {
            settlement = null;
        }
        
        updatePlayerExploredTiles();
    }

     
    public int getX() {
        return x;
    }

    
    public int getY() {
        return y;
    }

    
    public Position getPosition() {
        return new Position(x, y);
    }

    
    public boolean isSettleable() {
        return getType().canSettle();
    }

    
    public boolean isColonizeable() {
        if (!isSettleable()) {
            return false;
        }

        if (settlement != null) {
            return false;
        }

        for (Direction direction : Direction.values()) {
            Tile otherTile = getMap().getNeighbourOrNull(direction, this);
            if (otherTile != null) {
                Settlement set = otherTile.getSettlement();
                if ((set != null) && (set.getOwner().isEuropean())) {
                    return false;
                }
            }
        }

        return true;
    }

    
    public Unit getMovableUnit() {
        if (getFirstUnit() != null) {
            Iterator<Unit> unitIterator = getUnitIterator();
            while (unitIterator.hasNext()) {
                Unit u = unitIterator.next();

                Iterator<Unit> childUnitIterator = u.getUnitIterator();
                while (childUnitIterator.hasNext()) {
                    Unit childUnit = childUnitIterator.next();

                    if ((childUnit.getMovesLeft() > 0) && (childUnit.getState() == UnitState.ACTIVE)) {
                        return childUnit;
                    }
                }

                if ((u.getMovesLeft() > 0) && (u.getState() == UnitState.ACTIVE)) {
                    return u;
                }
            }
        } else {
            return null;
        }

        Iterator<Unit> unitIterator = getUnitIterator();
        while (unitIterator.hasNext()) {
            Unit u = unitIterator.next();

            Iterator<Unit> childUnitIterator = u.getUnitIterator();
            while (childUnitIterator.hasNext()) {
                Unit childUnit = childUnitIterator.next();

                if ((childUnit.getMovesLeft() > 0)) {
                    return childUnit;
                }
            }

            if (u.getMovesLeft() > 0) {
                return u;
            }
        }

        return null;
    }

    
    public Tile getTile() {
        return this;
    }

    
    public void add(Locatable locatable) {
        if (locatable instanceof Unit) {
            if (!units.contains(locatable)) {
                if (units.equals(Collections.emptyList())) {
                    units = new ArrayList<Unit>();
                } 
                units.add((Unit) locatable);
                ((Unit) locatable).setState(Unit.UnitState.ACTIVE);
                firePropertyChange(UNIT_CHANGE, null, locatable);
            }
        } else if (locatable instanceof TileItem) {
            if (tileItemContainer == null) {
                tileItemContainer = new TileItemContainer(getGame(), this);
            }
            tileItemContainer.addTileItem((TileItem) locatable);
        } else {
            logger.warning("Tried to add an unrecognized 'Locatable' to a tile.");
        }
        updatePlayerExploredTiles();
    }

    
    public void remove(Locatable locatable) {
        if (locatable instanceof Unit) {
            boolean removed = units.remove(locatable);
            if (removed) {
                firePropertyChange(UNIT_CHANGE, locatable, null);
            } else {
                logger.warning("Unit with ID " + ((Unit) locatable).getId() +
                               " could not be removed from " + this.toString() + " with ID " +
                               getId());
            }
        } else if (locatable instanceof TileItem) {
            tileItemContainer.addTileItem((TileItem) locatable);
        } else {
            logger.warning("Tried to remove an unrecognized 'Locatable' from a tile.");
        }
        updatePlayerExploredTiles();
    }
    
    
    public void removeUnitNoUpdate(Unit unit) {
        units.remove(unit);
    }
    
    public void addUnitNoUpdate(Unit unit) {
        if (units.equals(Collections.emptyList())) {
            units = new ArrayList<Unit>();
        }
        units.add(unit);
    }

    
    public int getUnitCount() {
        return units.size();
    }

    
    public List<Unit> getUnitList() {
        return units;
    }
    
    
    public Iterator<Unit> getUnitIterator() {
        return units.iterator();
    }

    
    public boolean canAdd(Locatable locatable) {
        if (locatable instanceof Unit) {
            
            return true;
        } else if (locatable instanceof TileImprovement) {
            return ((TileImprovement) locatable).getType().isTileTypeAllowed(getType());
        } else {
            return false;
        }
    }

    
    public int potential(GoodsType goodsType, UnitType unitType) {
        return getTileTypePotential(getType(), goodsType, getTileItemContainer(), unitType);
    }

    
    public int getMaximumPotential(GoodsType goodsType, UnitType unitType) {
        
        
        
        
        

        List<TileType> tileTypes = new ArrayList<TileType>();
        tileTypes.add(getType());

        
        for (TileImprovementType impType : FreeCol.getSpecification().getTileImprovementTypeList()) {
            if (impType.getChange(getType()) != null) {
                
                tileTypes.add(impType.getChange(getType()));
            }
        }

        int maxProduction = 0;

        for (TileType tileType : tileTypes) {
            float potential = tileType.getProductionOf(goodsType, unitType);
            if (tileType == getType() && hasResource()) {
                for (TileItem item : tileItemContainer.getTileItems()) {
                    if (item instanceof Resource) {
                        potential = ((Resource) item).getBonus(goodsType, unitType, (int) potential);
                    }
                }
            }
            for (TileImprovementType impType : FreeCol.getSpecification().getTileImprovementTypeList()) {
                if (impType.isNatural() || !impType.isTileTypeAllowed(tileType)) {
                    continue;
                } else if (impType.getBonus(goodsType) > 0) {
                    potential = impType.getProductionModifier(goodsType).applyTo(potential);
                }
            }
            maxProduction = Math.max((int) potential, maxProduction);
        }
        return maxProduction;
    }

    
    public Set<Modifier> getProductionBonus(GoodsType goodsType, UnitType unitType) {
        Set<Modifier> result = new LinkedHashSet<Modifier>();
        result.addAll(type.getProductionBonus(goodsType));
        if (!result.isEmpty() && tileItemContainer != null) {
            result.addAll(tileItemContainer.getProductionBonus(goodsType, unitType));
        }
        return result;
    }

    
    public boolean canGetRoad() {
        return isLand() && (tileItemContainer == null || tileItemContainer.getRoad() == null);
    }

    
    public TileImprovement findTileImprovementType(TileImprovementType type) {
        if (tileItemContainer == null) {
            return null;
        } else {
            return tileItemContainer.findTileImprovementType(type);
        }
    }
    
    
    public boolean hasImprovement(TileImprovementType type) {
        if (type.changeContainsTarget(getType())) {
            return true;
        } else if (tileItemContainer != null) {
            return tileItemContainer.hasImprovement(type);
        }
        return false;
    }
    
    
    public static int getTileTypePotential(TileType tileType, GoodsType goodsType, 
                                           TileItemContainer tiContainer, UnitType unitType) {
        if (tileType == null || goodsType == null || !goodsType.isFarmed()) {
            return 0;
        }
        
        int potential = tileType.getProductionOf(goodsType, unitType);
        if (tiContainer != null) {
            potential = tiContainer.getTotalBonusPotential(goodsType, unitType, potential);
        }
        return potential;
    }

    
    public List<AbstractGoods> getSortedPotential() {
        return getSortedPotential(null, null);
    }

    
    public List<AbstractGoods> getSortedPotential(Unit unit) {
        return getSortedPotential(unit.getType(), unit.getOwner());
    }

    
    public List<AbstractGoods> getSortedPotential(UnitType unitType, Player owner) {

        List<AbstractGoods> goodsTypeList = new ArrayList<AbstractGoods>();
        if (getType() != null) {
            
            
            
            for (GoodsType goodsType : FreeCol.getSpecification().getFarmedGoodsTypeList()) {
                int potential = potential(goodsType, unitType);
                if (potential > 0) {
                    goodsTypeList.add(new AbstractGoods(goodsType, potential));
                }
            }
            if (owner == null || owner.getMarket() == null) {
                Collections.sort(goodsTypeList, new Comparator<AbstractGoods>() {
                        public int compare(AbstractGoods o, AbstractGoods p) {
                            return p.getAmount() - o.getAmount();
                        }
                    });
            } else {
                final Market market = owner.getMarket();
                Collections.sort(goodsTypeList, new Comparator<AbstractGoods>() {
                        public int compare(AbstractGoods o, AbstractGoods p) {
                            return market.getSalePrice(p.getType(), p.getAmount())
                                - market.getSalePrice(o.getType(), o.getAmount());
                        }
                    });
            }
        }
        return goodsTypeList;
    }

    
    public GoodsType primaryGoods() {
        if (type == null) {
            return null;
        }
        
        for (AbstractGoods goods : getSortedPotential()) {
            if (goods.getType().isFoodType()) {
                return goods.getType();
            }
        }
        return null;
    }

    
    public GoodsType secondaryGoods() {
        if (type == null) {
            return null;
        } else {
            return type.getSecondaryGoods();
        }
    }

    
    public void expendResource(GoodsType goodsType, UnitType unitType, Settlement settlement) {
        if (hasResource() && tileItemContainer.getResource().getQuantity() != -1) {
            Resource resource = tileItemContainer.getResource();
            
            
            int potential = getTileTypePotential(getType(), goodsType, tileItemContainer, unitType);
            for (TileItem item : tileItemContainer.getTileItems()) {
                if (item instanceof TileImprovement) {
                    potential += ((TileImprovement) item).getBonus(goodsType);
                }
            }

            if (resource.useQuantity(goodsType, unitType, potential) == 0) {
                addModelMessage(settlement, ModelMessage.MessageType.WARNING,
                                "model.tile.resourceExhausted", 
                                "%resource%", resource.getName(),
                                "%colony%", ((Colony) settlement).getName());
                tileItemContainer.removeTileItem(resource);
                updatePlayerExploredTiles();
            }
        }
    }


    
    public PlayerExploredTile getPlayerExploredTile(Player player) {
        if (playerExploredTiles == null) {
            return null;
        }
        return playerExploredTiles.get(player);
    }

    
    private void createPlayerExploredTile(Player player) {
        playerExploredTiles.put(player, new PlayerExploredTile(getGame(), player, this));
    }

    
    public void updatePlayerExploredTile(Player player) {

        if (playerExploredTiles == null || getGame().getViewOwner() != null) {
            return;
        }
        PlayerExploredTile pet = playerExploredTiles.get(player);
        if (pet == null) {

            if (player.isEuropean()) {
                String message = "'playerExploredTiles' for " + player.getPlayerType() + 
                                 " player '" + player.getName() + "' is 'null'. " + 
                                 player.canSee(this) + ", " + isExploredBy(player) + " ::: " + getPosition();
                logger.warning(message);
                
                pet = new PlayerExploredTile(getGame(), player, this);
                playerExploredTiles.put(player, pet);
            } else {
                return;
            }
        }

        pet.getTileItemInfo(tileItemContainer);

        pet.setConnected(connected);
        pet.setOwner(owner);

        if (getColony() != null) {
            pet.setColonyUnitCount(getSettlement().getUnitCount());
            
            
            
            Building stockade = getColony().getStockade();
            if (stockade != null){
            	pet.setColonyStockadeLevel(stockade.getType().getIndex());
            } else {
            	pet.setColonyStockadeLevel(0);
            }
        } else if (getSettlement() != null) {
            IndianSettlement settlement = (IndianSettlement) getSettlement();
            pet.setMissionary(settlement.getMissionary());
            if (settlement.hasBeenVisited(player)) {
                pet.setVisited();
            }
            
        } else {
            pet.setColonyUnitCount(0);
        }
    }

    
    public void updatePlayerExploredTiles() {
        if (playerExploredTiles == null || getGame().getViewOwner() != null) {
            return;
        }
        for (Player player : getGame().getPlayers()) {
            if (playerExploredTiles.get(player) != null ||
                (player.isEuropean() && player.canSee(this))) {
                updatePlayerExploredTile(player);
            }
        }
    }

    
    public boolean isExploredBy(Player player) {
        if (player.isIndian()) {
            return true;
        }
        if (playerExploredTiles == null || playerExploredTiles.get(player) == null || !isExplored()) {
            return false;
        }

        return getPlayerExploredTile(player).isExplored();
    }

    
    public void setExploredBy(Player player, boolean explored) {
        if (player.isIndian()) {
            return;
        }
        if (playerExploredTiles.get(player) == null) {
            createPlayerExploredTile(player);
        }
        getPlayerExploredTile(player).setExplored(explored);
        updatePlayerExploredTile(player);
    }

    
    public void updateIndianSettlementSkill(Player player) {
        IndianSettlement is = (IndianSettlement) getSettlement();
        PlayerExploredTile pet = getPlayerExploredTile(player);
        pet.setSkill(is.getLearnableSkill());
        pet.setVisited();
    }

    
    public void updateIndianSettlementInformation(Player player) {
        if (player.isIndian()) {
            return;
        }
        PlayerExploredTile playerExploredTile = getPlayerExploredTile(player);
        IndianSettlement is = (IndianSettlement) getSettlement();
        playerExploredTile.setSkill(is.getLearnableSkill());
        playerExploredTile.setWantedGoods(is.getWantedGoods());
        playerExploredTile.setVisited();
    }


    
    public int getWorkAmount(TileImprovementType workType) {
        if (workType == null) {
            return -1;
        }
        if (!workType.isTileAllowed(this)) {
            return -1;
        }
        
        return (getType().getBasicWorkTurns() + workType.getAddWorkTurns());
    }

    
    public Unit getOccupyingUnit() {
        Unit unit = getFirstUnit();
        Player owner = null;
        if (owningSettlement != null) {
            owner = owningSettlement.getOwner();
        }
        if (owner != null && unit != null && unit.getOwner() != owner
            && owner.getStance(unit.getOwner()) != Stance.ALLIANCE) {
            for(Unit enemyUnit : getUnitList()) {
                if (enemyUnit.isOffensiveUnit() && enemyUnit.getState() == UnitState.FORTIFIED) {
                    return enemyUnit;
                }
            }
        }
        return null;
    }

    
    public boolean isOccupied() {
        return getOccupyingUnit() != null;
    }
    
    
    public boolean isAdjacent(Tile tile) {
    	if (tile == null) {
    		return false;
    	}
    	return (this.getDistanceTo(tile) == 1);
    }
    
    
    public String toString() {
        return "Tile("+x+","+y+"):"+((type==null)?"unknown":type.getId());
    }


    
    protected void toXMLImpl(XMLStreamWriter out, Player player, boolean showAll, boolean toSavedGame)
        throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        if (toSavedGame && !showAll) {
            logger.warning("toSavedGame is true, but showAll is false");
        }

        PlayerExploredTile pet = null;
        if (!(showAll)) {
            
            
            if (player != null) {
                pet = playerExploredTiles.get(player);
            } else {
                logger.warning("player == null");
            }
        }

        out.writeAttribute(ID_ATTRIBUTE, getId());
        out.writeAttribute("x", Integer.toString(x));
        out.writeAttribute("y", Integer.toString(y));
        out.writeAttribute("style", Integer.toString(style));

        writeAttribute(out, "type", getType());
        writeAttribute(out, "region", getRegion());

        if (connected && !type.isConnected()) {
            out.writeAttribute("connected", Boolean.toString(true));
        }

        if (owner != null) {
            if (getGame().isClientTrusted() || showAll || player.canSee(this)) {
                out.writeAttribute("owner", owner.getId());
            } else if (pet != null) {
                writeAttribute(out, "owner", pet.getOwner());
            }
        }

        if ((getGame().isClientTrusted() || showAll || player.canSee(this)) && (owningSettlement != null)) {
            out.writeAttribute("owningSettlement", owningSettlement.getId());
        }

        if (settlement != null) {
            settlement.toXML(out, player, showAll, toSavedGame);
        }

        
        
        if (getGame().isClientTrusted() || showAll
            || (player.canSee(this) && (settlement == null || settlement.getOwner() == player))) {
            if (!units.isEmpty()) {
                out.writeStartElement(UNITS_TAG_NAME);
                for (Unit unit : units) {
                    unit.toXML(out, player, showAll, toSavedGame);
                }
                out.writeEndElement();
            }
            if (tileItemContainer != null) {
                tileItemContainer.toXML(out, player, showAll, toSavedGame);
            }
        } else {
            if (tileItemContainer != null) {
                TileItemContainer newTileItemContainer = null;
                if (pet != null) {
                    newTileItemContainer = new TileItemContainer(getGame(), this, pet);
                } else {
                    newTileItemContainer = new TileItemContainer(getGame(), this);                
                }
                newTileItemContainer.setFakeID(tileItemContainer.getId());
                newTileItemContainer.toXML(out, player, showAll, toSavedGame);
            }
        }

        if (toSavedGame) {
            for (Entry<Player, PlayerExploredTile> entry : playerExploredTiles.entrySet()) {
                if (entry.getValue().isExplored()) {
                    entry.getValue().toXML(out, entry.getKey(), showAll, toSavedGame);
                }
            }
        }

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setId(in.getAttributeValue(null, ID_ATTRIBUTE));

        x = Integer.parseInt(in.getAttributeValue(null, "x"));
        y = Integer.parseInt(in.getAttributeValue(null, "y"));
        style = getAttribute(in, "style", 0);

        String typeString = in.getAttributeValue(null, "type");
        if (typeString != null) {
            type = FreeCol.getSpecification().getTileType(typeString);
        }

        
        boolean needsRumour = getAttribute(in, LostCityRumour.getXMLElementTagName(), false);

        connected = getAttribute(in, "connected", false);
        owner = getFreeColGameObject(in, "owner", Player.class, null);
        region = getFreeColGameObject(in, "region", Region.class, null);

        final String owningSettlementStr = in.getAttributeValue(null, "owningSettlement");
        if (owningSettlementStr != null) {
            owningSettlement = (Settlement) getGame().getFreeColGameObject(owningSettlementStr);
            if (owningSettlement == null) {
                if (owningSettlementStr.startsWith(IndianSettlement.getXMLElementTagName())) {
                    owningSettlement = new IndianSettlement(getGame(), owningSettlementStr);
                } else if (owningSettlementStr.startsWith(Colony.getXMLElementTagName())) {
                    owningSettlement = new Colony(getGame(), owningSettlementStr);
                } else {
                    logger.warning("Unknown type of Settlement.");
                }
            }
        } else {
            owningSettlement = null;
        }

        boolean settlementSent = false;
        units.clear();
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if (in.getLocalName().equals(Colony.getXMLElementTagName())) {
                settlement = updateFreeColGameObject(in, Colony.class);
                settlementSent = true;
            } else if (in.getLocalName().equals(IndianSettlement.getXMLElementTagName())) {
                settlement = updateFreeColGameObject(in, IndianSettlement.class);
                settlementSent = true;
            } else if (in.getLocalName().equals(UNITS_TAG_NAME)) {
                while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
                    if (in.getLocalName().equals(Unit.getXMLElementTagName())) {
                        if (units.equals(Collections.emptyList())) {
                            units = new ArrayList<Unit>();
                        }
                        units.add(updateFreeColGameObject(in, Unit.class));
                    }
                }
            } else if (in.getLocalName().equals(TileItemContainer.getXMLElementTagName())) {
                tileItemContainer = (TileItemContainer) getGame().getFreeColGameObject(in.getAttributeValue(null, ID_ATTRIBUTE));
                if (tileItemContainer != null) {
                    tileItemContainer.readFromXML(in);
                } else {
                    tileItemContainer = new TileItemContainer(getGame(), this, in);
                }
            } else if (in.getLocalName().equals("playerExploredTile")) {
                
                Player player = (Player) getGame().getFreeColGameObject(in.getAttributeValue(null, "player"));
                if (playerExploredTiles.get(player) == null) {
                    PlayerExploredTile pet = new PlayerExploredTile(getGame(), in);
                    playerExploredTiles.put(player, pet);
                } else {
                    playerExploredTiles.get(player).readFromXML(in);
                }
            } else {
                logger.warning("Unknown tag: " + in.getLocalName() + " [" +
                               in.getAttributeValue(null, "ID") + "] " +
                               " loading tile with ID " +
                               getId());
                in.nextTag();
            }
        }
        if (!settlementSent && settlement != null) {
            settlement.dispose();
        }

        
        if (needsRumour) {
            add(new LostCityRumour(getGame(), this));
        }

    }

    
    public static String getXMLElementTagName() {
        return "tile";
    }
}
