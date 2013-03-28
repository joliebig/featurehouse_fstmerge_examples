

package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Map.Position;

import org.w3c.dom.Element;



abstract public class Settlement extends FreeColGameObject implements Location, Named, Ownable {

    private static final Logger logger = Logger.getLogger(Settlement.class.getName()); 
    
    public static enum SettlementType {
        SMALL_COLONY, MEDIUM_COLONY, LARGE_COLONY, 
            SMALL_STOCKADE,
            MEDIUM_STOCKADE, MEDIUM_FORT,
            LARGE_STOCKADE, LARGE_FORT, LARGE_FORTRESS, 
            UNDEAD, 
            INDIAN_CAMP, INDIAN_VILLAGE, AZTEC_CITY, INCA_CITY }

    public static final int RADIUS = 1;
    public static final int FOOD_CONSUMPTION = 2;

    
    protected Player owner;

    
    private String name;

    
    protected Tile tile;
    
    protected GoodsContainer goodsContainer;
    
    
    private boolean isCapital = false;

    
    protected FeatureContainer featureContainer = new FeatureContainer();


    
    public Settlement(Game game, Player owner, String name, Tile tile) {
        super(game);
        this.owner = owner;
        this.name = name;
        this.tile = tile;

        
        if (tile.getOwningSettlement() != null) {
            if (tile.getOwningSettlement() instanceof Colony) {
                Colony oc = (Colony) tile.getOwningSettlement();
                ColonyTile ct = oc.getColonyTile(tile);
                ct.relocateWorkers();
            } else if (tile.getOwningSettlement() instanceof IndianSettlement) {
                logger.warning("An indian settlement is already owning the tile.");
            } else {
                logger.warning("An unknown type of settlement is already owning the tile.");
            }
        }

        owner.addSettlement(this);
    }

    

    
    public Settlement(Game game, XMLStreamReader in) throws XMLStreamException {
        super(game, in);
    }

    
    public Settlement(Game game, Element e) {
        super(game, e);
    }

    
    public Settlement(Game game, String id) {
        super(game, id);
    }
    

    
    public String getName() {
        return name;
    }

    
    public void setName(String newName) {
        this.name = newName;
    }

    
    public boolean isCapital() {
        return isCapital;
    }

    
    public void setCapital(boolean isCapital) {
        this.isCapital = isCapital;
    }


    
    public FeatureContainer getFeatureContainer() {
        return featureContainer;
    }

    
    public int getLineOfSight() {
        return 2;
    }
    

    
    abstract public Unit getDefendingUnit(Unit attacker);

    
    
    public Tile getTile() {
        return tile;
    }
    
    
    public Tile getTile(int x, int y) {
        if (x==0 && y==0) {
            return getGame().getMap().getNeighbourOrNull(Direction.N, tile);
        } else if (x==0 && y== 1) {
            return getGame().getMap().getNeighbourOrNull(Direction.NE, tile);
        } else if (x==0 && y== 2) {
            return getGame().getMap().getNeighbourOrNull(Direction.E, tile);
        } else if (x==1 && y== 0) {
            return getGame().getMap().getNeighbourOrNull(Direction.NW, tile);
        } else if (x==1 && y== 1) {
            return tile;
        } else if (x==1 && y== 2) {
            return getGame().getMap().getNeighbourOrNull(Direction.SE, tile);
        } else if (x==2 && y== 0) {
            return getGame().getMap().getNeighbourOrNull(Direction.W, tile);
        } else if (x==2 && y== 1) {
            return getGame().getMap().getNeighbourOrNull(Direction.SW, tile);
        } else if (x==2 && y== 2) {
            return getGame().getMap().getNeighbourOrNull(Direction.S, tile);
        } else {
            return null;
        }
    }


    
    private boolean canClaimTile(Tile tile) {
        
        return (owner.isIndian() && !tile.isLand()) ? false
            : (getTile().getDistanceTo(tile) > getRadius()) ? false
            : tile.getOwner() == null
            || tile.getOwningSettlement() == null
            || tile.getOwningSettlement() == this;
    }

    
    public void claimTile(Tile tile) {
        tile.setOwningSettlement(this);
        tile.setOwner(owner);
        tile.updatePlayerExploredTiles();
    }

    
    private void claimTiles() {
        Map map = getGame().getMap();
        Tile settlementTile = getTile();

        settlementTile.setOwningSettlement(this);
        settlementTile.setOwner(owner);
        owner.setExplored(settlementTile);
        settlementTile.updatePlayerExploredTiles();
        for (Tile tile : map.getSurroundingTiles(settlementTile, getRadius())) {
            if (canClaimTile(tile)) {
                claimTile(tile);
            }
        }
        for (Tile tile : map.getSurroundingTiles(settlementTile, getLineOfSight())) {
            owner.setExplored(tile);
        }
    }

    
    public void placeSettlement() {
        claimTiles();
        tile.setSettlement(this);
        owner.invalidateCanSeeTiles();
    }

    
    public Player getOwner() {
        return owner;
    }

    
    public void setOwner(Player player) {
        owner = player;
    }

    
    public void changeOwner(Player owner) {
        Player oldOwner = this.owner;        
        setOwner(owner);
        
        if (oldOwner.hasSettlement(this)) {
            oldOwner.removeSettlement(this);
        }
        if (!owner.hasSettlement(this)) {
            owner.addSettlement(this);
        }
        
        claimTiles();
        oldOwner.invalidateCanSeeTiles();
        owner.invalidateCanSeeTiles();

        if (getGame().getFreeColGameObjectListener() != null) {
            getGame().getFreeColGameObjectListener().ownerChanged(this, oldOwner, owner);
        }
    }

    public GoodsContainer getGoodsContainer() {
        return goodsContainer;
    }

    
    public Iterator<Goods> getGoodsIterator() {
        return goodsContainer.getGoodsIterator();
    }

    
    public List<Goods> getCompactGoods() {
        return goodsContainer.getCompactGoods();
    }


    
    public abstract void add(Locatable locatable);

    
    public abstract void remove(Locatable locatable);

    public abstract boolean canAdd(Locatable locatable);

    
    public abstract int getUnitCount();

    public abstract boolean contains(Locatable locatable);


    
    public void dispose() {
        Player oldOwner = owner;
        Tile settlementTile = getTile();
        Map map = getGame().getMap();
        ArrayList<Tile> lostTiles = new ArrayList<Tile>();
        for (Tile tile : map.getSurroundingTiles(settlementTile, getRadius())) {
            if (tile.getOwningSettlement() == this
                || (tile.getOwningSettlement() == null && tile.getOwner() == owner)) {
                tile.setOwningSettlement(null);
                tile.setOwner(null);
                tile.updatePlayerExploredTiles();
                lostTiles.add(tile);
            }
        }
        settlementTile.setSettlement(null);
        settlementTile.setOwner(null);
        settlementTile.updatePlayerExploredTiles();
        lostTiles.add(settlementTile);

        owner = null;
        oldOwner.removeSettlement(this);
        oldOwner.invalidateCanSeeTiles();
        goodsContainer.dispose();
        super.dispose();

        
        for (Tile lostTile : lostTiles) {
            if (lostTile.getOwningSettlement() != null) continue;
            for (Tile t : map.getSurroundingTiles(lostTile, 1)) {
                
                
                
                Settlement settlement = t.getOwningSettlement();
                if (settlement != null && settlement.canClaimTile(lostTile)) {
                    settlement.claimTile(lostTile);
                    break;
                }
            }
        }
    }
    
    
    public int getRadius() {
        if (isCapital) {
            return owner.getNationType().getCapitalRadius();
        } else {
            return owner.getNationType().getSettlementRadius();
        }
    }

    public abstract void newTurn();

    
    public void removeGoods(GoodsType type, int amount) {
        goodsContainer.removeGoods(type, amount);
    }

    
    public void removeGoods(AbstractGoods goods) {
        goodsContainer.removeGoods(goods);
    }

    
    public void removeGoods(GoodsType type) {
        goodsContainer.removeGoods(type);
    }

    
    public void addGoods(GoodsType type, int amount) {
        goodsContainer.addGoods(type.getStoredAs(), amount);
    }

    public void addGoods(AbstractGoods goods) {
        addGoods(goods.getType(), goods.getAmount());
    }

    
    public int getGoodsCount(GoodsType type) {
        if (type != null && !type.isStoredAs()) {
            return goodsContainer.getGoodsCount(type);
        } else {
            return 0;
        }
    }
    
    
    public abstract int getProductionOf(GoodsType goodsType);

    
    public int getFoodConsumption() {
        return FOOD_CONSUMPTION * getUnitCount();
    }
    
    
    public int getFoodConsumptionByType(GoodsType type) {
    	
    	
    	
    	
    	
    	if(!type.isFoodType()){
            logger.warning("Good type given isnt food type");
            return 0;
    	}
    	
    	int required = getFoodConsumption();
    	int consumed = 0;
    	GoodsType corn = FreeCol.getSpecification().getGoodsType("model.goods.food");
    	
    	for (GoodsType foodType : FreeCol.getSpecification().getGoodsFood()) {
            if(foodType == corn){
                
                continue;
            }
    		
            consumed = Math.min(getProductionOf(foodType),required);
            if(type == foodType){
                return consumed;
            }
            required -= consumed;
        }
    	
    	
    	consumed = Math.min(getProductionOf(corn),required);
    	
    	return consumed;
    }

    protected void removeFood(final int amount) {
        int rest = amount;
        List<AbstractGoods> backlog = new ArrayList<AbstractGoods>();
        for (GoodsType foodType : FreeCol.getSpecification().getGoodsFood()) {
            int available = getGoodsCount(foodType);
            if (available >= rest) {
                removeGoods(foodType, rest);
                for (AbstractGoods food : backlog) {
                    removeGoods(food.getType(), food.getAmount());
                }
                rest = 0;
            } else {
                backlog.add(new AbstractGoods(foodType, available));
                rest -= available;
            }
        }
        if (rest > 0) {
            throw new IllegalStateException("Attempted to remove more food than was present.");
        }
    }
            
    
    public int getFoodCount() {
        int result = 0;
        for (GoodsType foodType : FreeCol.getSpecification().getGoodsFood()) {
            result += getGoodsCount(foodType);
        }
        return result;
    }

    
    public boolean canBuildEquipment(EquipmentType equipmentType) {
        for (AbstractGoods requiredGoods : equipmentType.getGoodsRequired()) {
            if (getGoodsCount(requiredGoods.getType()) < requiredGoods.getAmount()) {
                return false;
            }
        }
        return true;
    }

}
