

package net.sf.freecol.common.model;

import java.util.List;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.Map.Direction;

import org.w3c.dom.Element;



public class TileImprovement extends TileItem implements Named {

    private static Logger logger = Logger.getLogger(TileImprovement.class.getName());

    private TileImprovementType type;
    private int turnsToComplete;
    
    
    private int magnitude;
    
    
    public static final int NO_RIVER = 0;
    public static final int SMALL_RIVER = 1;
    public static final int LARGE_RIVER = 2;
    public static final int FJORD_RIVER = 3; 

    
    
    private int style;

    
    private boolean virtual;
    
    

    
    public TileImprovement(Game game, Tile tile, TileImprovementType type) {
        super(game, tile);
        if (type == null) {
            throw new IllegalArgumentException("Parameter 'type' must not be 'null'.");
        }
        this.type = type;
        if (!type.isNatural()) {
            this.turnsToComplete = tile.getType().getBasicWorkTurns() + type.getAddWorkTurns();
        }
        this.magnitude = type.getMagnitude();
    }

    public TileImprovement(Game game, XMLStreamReader in) throws XMLStreamException {
        super(game, in);
        readFromXML(in);
    }

    public TileImprovement(Game game, Element e) {
        super(game, e);
        readFromXMLElement(e);
    }

    
    public TileImprovement(Game game, String id) {
        super(game, id);
    }

    

    public TileImprovementType getType() {
        return type;
    }

    public int getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(int magnitude) {
        this.magnitude = magnitude;
    }

    
    public final boolean isVirtual() {
        return virtual;
    }

    
    public final void setVirtual(final boolean newVirtual) {
        this.virtual = newVirtual;
    }

    
    public boolean isRoad() {
        return getType().getId().equals("model.improvement.road");
    }

    
    public boolean isRiver() {
        return getType().getId().equals("model.improvement.river");
    }

    public String getNameKey() {
        return getType().getNameKey();
    }

    
    public String toString() {
        if (turnsToComplete > 0) {
            return getType().getId() + " (" + Integer.toString(turnsToComplete) + " turns left)";
        } else {
            return getType().getId();
        }
    }

    
    public int getTurnsToComplete() {
        return turnsToComplete;
    }

    public void setTurnsToComplete(int turns) {
        turnsToComplete = turns;
    }

    
    public final int getZIndex() {
        return type.getZIndex();
    }

    public boolean isComplete() {
        return turnsToComplete <= 0;
    }

    
    public int doWork(int turns) {
        turnsToComplete -= turns;
        if (turnsToComplete <= 0) {
            turnsToComplete = 0;
        }
        return turnsToComplete;
    }

    public int doWork() {
        return doWork(1);
    }

    public EquipmentType getExpendedEquipmentType() {
        return type.getExpendedEquipmentType();
    }

    public int getExpendedAmount() {
        return type.getExpendedAmount();
    }

    public GoodsType getDeliverGoodsType() {
        return type.getDeliverGoodsType();
    }

    public int getDeliverAmount() {
        return type.getDeliverAmount();
    }

    
    public int getBonus(GoodsType goodsType) {
        if (!isComplete()) {
            return 0;
        }
        return type.getBonus(goodsType);
    }

    
    public Modifier getProductionModifier(GoodsType goodsType) {
        if (!isComplete()) {
            return null;
        }
        return type.getProductionModifier(goodsType);
    }

    
    public int getMovementCost(int moveCost, Tile fromTile) {
        if (!isComplete()) {
            return moveCost;
        }
        String typeId = type.getId();
        if (typeId == null) {
            
            return type.getMovementCost(moveCost);
        }
        
        for (TileImprovement improvement : fromTile.getTileImprovements()) {
            if (improvement.getType().getId().equals(typeId)) {
                
                return type.getMovementCost(moveCost);
            }
        }       
        
        return moveCost;
    }

    
    public TileType getChange(TileType tileType) {
        if (!isComplete()) {
            return null;
        }
        return type.getChange(tileType);
    }

    
    public int getStyle() {
        return style;
    }

    
    public void setStyle(int style) {
        this.style = style;
    }

    
    public static int[] getBase(Direction[] directions, int baseNumber) {
        Direction[] allDirections = Direction.values();
        int[] base = new int[allDirections.length];
        int n = 1;
        for (int i = 0; i < allDirections.length; i++) {
            base[i] = 0;
            for (Direction direction : directions) {
                if (direction == allDirections[i]) {
                    base[i] = n;
                    n *= baseNumber;
                    break;
                }
            }
        }
        return base;
    }

    
    public int[] getStyleBreakdown(Direction[] directions, int baseNumber) {
        return getStyleBreakdown(getBase(directions, baseNumber));
    }

    
    public int[] getStyleBreakdown(int[] base) {
        int[] result = new int[8];
        int tempStyle = style;
        for (int i = base.length - 1; i >= 0; i--) {
            if (base[i] == 0) {
                continue;                       
            }
            result[i] = tempStyle / base[i];    
            tempStyle -= result[i] * base[i];   
        }
        return result;
    }

    public void compileStyleBreakdown(int[] base, int[] breakdown) {
        if (base.length != breakdown.length) {
            logger.warning("base.length != breakdown.length");
            return;
        }
        style = 0;
        for (int i = 0; i < base.length; i++) {
            style += base[i] * breakdown[i];
        }
    }

    
    public static TileImprovementType findBestTileImprovementType(Tile tile, GoodsType goodsType) {
        
        List<TileImprovementType> impTypeList = FreeCol.getSpecification().getTileImprovementTypeList();
        int bestValue = 0;
        TileImprovementType bestType = null;
        for (TileImprovementType impType : impTypeList) {
            if (impType.isNatural()) {
                continue;   
            }
            if (!impType.isTileTypeAllowed(tile.getType())) {
                continue;   
            }
            if (tile.findTileImprovementType(impType) != null) {
                continue;   
            }
            int value = impType.getValue(tile.getType(), goodsType);
            if (value > bestValue) {
                bestValue = value;
                bestType = impType;
            }
        }
        return bestType;
    }

    
    public boolean isWorkerAllowed(Unit unit) {
        if (unit == null) {
            return false;
        }
        if (isComplete()) {
            return false;
        }
        return type.isWorkerAllowed(unit);
    }

    
    @Override
    public void dispose() {
        super.dispose();
    }

    

    
    @Override
    protected void toXMLImpl(XMLStreamWriter out, Player player, boolean showAll, boolean toSavedGame)
        throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        
        out.writeAttribute("ID", getId());
        out.writeAttribute("tile", getTile().getId());
        out.writeAttribute("type", getType().getId());
        out.writeAttribute("turns", Integer.toString(turnsToComplete));
        out.writeAttribute("magnitude", Integer.toString(magnitude));
        out.writeAttribute("style", Integer.toString(style));
        if (virtual) {
            out.writeAttribute("virtual", Boolean.toString(virtual));
        }

        
        out.writeEndElement();
    }

    
    @Override
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setId(in.getAttributeValue(null, "ID"));

        tile = (Tile) getGame().getFreeColGameObject(in.getAttributeValue(null, "tile"));
        if (tile == null) {
            tile = new Tile(getGame(), in.getAttributeValue(null, "tile"));
        }
        type = FreeCol.getSpecification().getTileImprovementType(in.getAttributeValue(null, "type"));
        turnsToComplete = Integer.parseInt(in.getAttributeValue(null, "turns"));
        magnitude = Integer.parseInt(in.getAttributeValue(null, "magnitude"));
        style = Integer.parseInt(in.getAttributeValue(null, "style"));
        virtual = getAttribute(in, "virtual", false);
        
        in.nextTag();
    }

    
    public static String getXMLElementTagName() {
        return "tileimprovement";
    }

}
