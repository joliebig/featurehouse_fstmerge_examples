

package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.PlayerExploredTile;

import org.w3c.dom.Element;


public class TileItemContainer extends FreeColGameObject {

    private static final Logger logger = Logger.getLogger(TileItemContainer.class.getName());

    
    private Tile tile;

    
    private List<TileItem> tileItems = new ArrayList<TileItem>();

    
    private final Comparator<TileItem> tileItemComparator = new Comparator<TileItem>() {
        public int compare(TileItem tileItem1, TileItem tileItem2) {
            return tileItem1.getZIndex() - tileItem2.getZIndex();
        }
    };


    

    
    public TileItemContainer(Game game, Tile tile) {
        super(game);

        if (tile == null) {
            throw new IllegalArgumentException("Tile must not be 'null'.");
        }

        this.tile = tile;
    }



    public TileItemContainer(Game game, Tile tile, PlayerExploredTile pet) {
        super(game);

        if (tile == null) {
            throw new IllegalArgumentException("Tile must not be 'null'.");
        }

        this.tile = tile;

        tileItems.addAll(pet.getImprovements());
        if (pet.getResource() != null) {
            tileItems.add(pet.getResource());
        }
        if (pet.getLostCityRumour() != null) {
            tileItems.add(pet.getLostCityRumour());
        }
        Collections.sort(tileItems, tileItemComparator);
    }

    
    public TileItemContainer(Game game, Tile tile, XMLStreamReader in) throws XMLStreamException {
        super(game, in);

        if (tile == null) {
            throw new IllegalArgumentException("Tile must not be 'null'.");
        }

        this.tile = tile;
        readFromXML(in);
    }

    
    public TileItemContainer(Game game, Tile tile, Element e) {
        super(game, e);

        if (tile == null) {
            throw new IllegalArgumentException("Tile must not be 'null'.");
        }

        this.tile = tile;
        readFromXMLElement(e);
    }

    
    public TileItemContainer clone() {
        return clone(true, false);
    }
    public TileItemContainer clone(boolean importBonuses) {
        return clone(importBonuses, false);
    }
    public TileItemContainer clone(boolean importBonuses, boolean copyOnlyNatural) {
        TileItemContainer ticClone = new TileItemContainer(getGame(), getTile());
        ticClone.copyFrom(this, importBonuses, copyOnlyNatural);
        return ticClone;
    }

    

    public Tile getTile() {
        return tile;
    }

    
    public final List<TileItem> getTileItems() {
        return tileItems;
    }

    
    public final void setTileItems(final List<TileItem> newTileItems) {
        this.tileItems = newTileItems;
    }

    public Resource getResource() {
        for (TileItem item : tileItems) {
            if (item instanceof Resource) {
                return (Resource) item;
            }
        }
        return null;
    }

    public TileImprovement getRoad() {
        for (TileItem item : tileItems) {
            if (item instanceof TileImprovement && ((TileImprovement) item).isRoad()) {
                return (TileImprovement) item;
            }
        }
        return null;
    }

    public TileImprovement getRiver() {
        for (TileItem item : tileItems) {
            if (item instanceof TileImprovement && ((TileImprovement) item).isRiver()) {
                return (TileImprovement) item;
            }
        }
        return null;
    }

    
    public final LostCityRumour getLostCityRumour() {
        for (TileItem item : tileItems) {
            if (item instanceof LostCityRumour) {
                return (LostCityRumour) item;
            }
        }
        return null;
    }

    public void clear() {
        for (TileItem item : tileItems) {
            item.dispose();
        }
        tileItems.clear();
    }

    
    public void removeIncompatibleImprovements() {
        TileType tileType = tile.getType();
        Iterator<TileItem> iterator = tileItems.iterator();
        while (iterator.hasNext()) {
            TileItem item = iterator.next();
            if ((item instanceof TileImprovement
                 && !((TileImprovement) item).getType().isTileTypeAllowed(tileType))
                || (item instanceof Resource
                    && !tileType.canHaveResourceType(((Resource) item).getType()))) {
                iterator.remove();
                item.dispose();
            }
        }
    }

    
    public List<TileImprovement> getImprovements() {
        return getImprovements(false);
    }
        
    
    public List<TileImprovement> getCompletedImprovements() {
        return getImprovements(true);
    }

    
    private List<TileImprovement> getImprovements(boolean completedOnly) {
        List<TileImprovement> improvements = new ArrayList<TileImprovement>();
        for (TileItem item : tileItems) {
            if (item instanceof TileImprovement
                && (!completedOnly || ((TileImprovement) item).isComplete())) {
                improvements.add((TileImprovement) item);
            }
        }
        return improvements;
    }

    
    public int getTotalBonusPotential(GoodsType g, UnitType unitType, int tilePotential) {
        int potential = tilePotential;
        int improvementBonus = 0;
        for (TileItem item : tileItems) {
            if (item instanceof TileImprovement) {
                improvementBonus += ((TileImprovement) item).getBonus(g);
            } else if (item instanceof Resource) {
                potential = ((Resource) item).getBonus(g, unitType, potential);
            }
        }
        if (potential > 0) {
            potential += improvementBonus;
        }
        return potential;
    }

    
    public Set<Modifier> getProductionBonus(GoodsType goodsType, UnitType unitType) {
        Set<Modifier> result = new HashSet<Modifier>();
        for (TileItem item : tileItems) {
            if (item instanceof Resource) {
                result.addAll(((Resource) item).getType().getProductionModifier(goodsType, unitType));
            } else if (item instanceof TileImprovement) {
                Modifier modifier = ((TileImprovement) item).getProductionModifier(goodsType);
                if (modifier != null) {
                    result.add(modifier);
                }
            }
        }
        return result;
    }

    
    public int getMoveCost(int basicMoveCost, Tile fromTile) {
        int moveCost = basicMoveCost;
        for (TileItem item : tileItems) {
            if (item instanceof TileImprovement) {
                moveCost = ((TileImprovement) item).getMovementCost(moveCost, fromTile);
            }
        }
        return moveCost;
    }

    
    public String getLabel(String separator) {
        String label = new String();
        for (TileItem item : tileItems) {
            if (item instanceof Resource
                || (item instanceof TileImprovement
                    && ((TileImprovement) item).isComplete())) {
                label += separator + Messages.message(item.getName());
            }
        }
        return label;
    }

    public String getLabel() {
        return getLabel("/");
    }

    

    
    public TileItem addTileItem(TileItem item) {
        if (item == null) {
            return null;
        } else {
            for (int index = 0; index < tileItems.size(); index++) {
                TileItem oldItem = tileItems.get(index);
                if (item instanceof TileImprovement
                    && oldItem instanceof TileImprovement
                    && ((TileImprovement) oldItem).getType().getId()
                    .equals(((TileImprovement) item).getType().getId())) {
                    if (((TileImprovement) oldItem).getMagnitude() < ((TileImprovement) item).getMagnitude()) {
                        tileItems.set(index, item);
                        oldItem.dispose();
                        return item;
                    } else {
                        
                        return oldItem;
                    }
                } else if (oldItem.getZIndex() > item.getZIndex()) {
                    tileItems.add(index, item);
                    return item;
                }
            }
            tileItems.add(item);
            return item;
        }
    }

    
    public TileItem removeTileItem(TileItem item) {
        return (tileItems.remove(item) ? item : null);
    }

    public void removeAll(Class c) {
        Iterator<TileItem> iterator = tileItems.iterator();
        while (iterator.hasNext()) {
            if (c.isInstance(iterator.next())) {
                iterator.remove();
            }
        }
    }
    
    public void copyFrom(TileItemContainer tic) {
        copyFrom(tic, true, false);
    }
    public void copyFrom(TileItemContainer tic, boolean importResources) {
        copyFrom(tic, importResources, false);
    }
    public void copyFrom(TileItemContainer tic, boolean importResources, boolean copyOnlyNatural) {
        tileItems.clear();
        for (TileItem item : tileItems) {
            if (item instanceof Resource) {
                if (importResources) {
                    Resource ticR = (Resource) item;
                    Resource r = new Resource(getGame(), tile, ticR.getType(), ticR.getQuantity());
                    tileItems.add(r);
                }
            } else if (item instanceof LostCityRumour) {
                LostCityRumour ticR = (LostCityRumour) item;
                LostCityRumour r = new LostCityRumour(getGame(), tile, ticR.getType(), ticR.getName());
                addTileItem(r);
            } else if (item instanceof TileImprovement) {
                if (!copyOnlyNatural || ((TileImprovement) item).getType().isNatural()) {
                    TileImprovement ti = (TileImprovement) item;
                    TileImprovement newTI = new TileImprovement(getGame(), tile, ti.getType());
                    newTI.setMagnitude(ti.getMagnitude());
                    newTI.setStyle(ti.getStyle());
                    newTI.setTurnsToComplete(ti.getTurnsToComplete());
                    addTileItem(newTI);
                }
            }
        }
    }

    
    public void removeAll() {
        clear();
    }

    
    public boolean contains(TileItem t) {
        return tileItems.contains(t);
    }

    
    public TileImprovement findTileImprovementType(TileImprovementType type) {
        for (TileItem item : tileItems) {
            if (item instanceof TileImprovement && ((TileImprovement) item).getType() == type) {
                return (TileImprovement) item;
            }
        }
        return null;
    }
    
    
    public boolean hasImprovement(TileImprovementType type) {
        TileImprovement improvement = findTileImprovementType(type);
        return improvement != null && improvement.isComplete();
    }

    
    public void dispose() {
        clear();
        super.dispose();
    }

    

    
    public TileImprovement addRiver(int magnitude, int style) {
        if (magnitude == TileImprovement.NO_RIVER) {
            return null;
        }
        TileImprovement river = new TileImprovement(getGame(), tile, FreeCol.getSpecification()
                                                    .getTileImprovementType("model.improvement.River"));
        river = (TileImprovement) addTileItem(river);
        river.setMagnitude(magnitude);
        river.setStyle(style);
        return river;
    }

    
    public TileImprovement removeRiver() {
        Iterator<TileItem> iterator = tileItems.iterator();
        while (iterator.hasNext()) {
            TileItem item = iterator.next();
            if (item instanceof TileImprovement && ((TileImprovement) item).isRiver()) {
                iterator.remove();
                return (TileImprovement) item;
            }
        }
        return null;
    }

    

    
    protected void toXMLImpl(XMLStreamWriter out, Player player, boolean showAll, boolean toSavedGame) 
        throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("ID", getId());
        out.writeAttribute("tile", tile.getId());

        for (TileItem item : tileItems) {
            item.toXML(out, player, showAll, toSavedGame);
        }

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setId(in.getAttributeValue(null, "ID"));

        tile = (Tile) getGame().getFreeColGameObject(in.getAttributeValue(null, "tile"));
        if (tile == null) {
            tile = new Tile(getGame(), in.getAttributeValue(null, "tile"));
        }

        tileItems.clear();

        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            TileItem item = (TileItem) getGame().getFreeColGameObject(in.getAttributeValue(null, "ID"));
            if (item == null) {
                if (in.getLocalName().equals(Resource.getXMLElementTagName())) {
                    item = new Resource(getGame(), in);
                } else if (in.getLocalName().equals(LostCityRumour.getXMLElementTagName())) {
                    item = new LostCityRumour(getGame(), in);
                } else if (in.getLocalName().equals(TileImprovement.getXMLElementTagName())) {
                    item = new TileImprovement(getGame(), in);
                }
            } else {
                item.readFromXML(in);
            }
            tileItems.add(item);
        }
        
        
        Collections.sort(tileItems, tileItemComparator);
    }


    
    public static String getXMLElementTagName() {
        return "tileitemcontainer";
    }
    
    
    
    public String toString() {
        StringBuffer sb = new StringBuffer(60);
        sb.append("TileItemContainer with: ");
        for (TileItem item : tileItems) {
            sb.append(item.toString() + ", ");
        }
        return sb.toString();
    }

}
