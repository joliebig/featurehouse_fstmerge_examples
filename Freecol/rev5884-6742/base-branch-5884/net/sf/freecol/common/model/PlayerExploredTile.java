

package net.sf.freecol.common.model;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.Specification;



public class PlayerExploredTile extends FreeColGameObject {

    private static final Logger logger = Logger.getLogger(PlayerExploredTile.class.getName()); 

    
    private Player player;

    private boolean explored = false;

    
    private Player owner;

    
    private Resource resource;
    private LostCityRumour lostCityRumour;
    private List<TileImprovement> improvements;
    private TileImprovement road;
    private TileImprovement river;

    
    private int colonyUnitCount = 0, colonyStockadeLevel;

    
    private UnitType skill = null;
    private GoodsType[] wantedGoods = {null, null, null};
    private boolean settlementVisited = false;

    private Unit missionary = null;

    private boolean connected = false;

    private Tile tile;

    
    public PlayerExploredTile(Game game, Player player, Tile tile) {
        super(game);
        this.player = player;
        this.tile = tile;
        getTileItemInfo(tile.getTileItemContainer());
    }

    
    public PlayerExploredTile(Game game, XMLStreamReader in) throws XMLStreamException {
        super(game, in);
        readFromXML(in);
    }

    
    public void getTileItemInfo(TileItemContainer tic) {
        if (tic != null) {
            resource = tic.getResource();
            improvements = tic.getImprovements();
            road = tic.getRoad();
            river = tic.getRiver();
            lostCityRumour = tic.getLostCityRumour();
        } else {
            improvements = Collections.emptyList();
        }
    }

    public void setColonyUnitCount(int colonyUnitCount) {
        this.colonyUnitCount = colonyUnitCount;
    }

    public int getColonyUnitCount() {
        return colonyUnitCount;
    }

    public void setColonyStockadeLevel(int colonyStockadeLevel) {
        this.colonyStockadeLevel = colonyStockadeLevel;
    }

    public int getColonyStockadeLevel() {
        return colonyStockadeLevel;
    }

    public boolean hasRoad() {
        return (road != null);
    }

    public TileImprovement getRoad() {
        return road;
    }

    public boolean hasRiver() {
        return (river != null);
    }

    public TileImprovement getRiver() {
        return river;
    }

    public Resource getResource() {
        return resource;
    }

    public LostCityRumour getLostCityRumour() {
        return lostCityRumour;
    }

    public List<TileImprovement> getImprovements() {
        return improvements;
    }

    public void setLostCityRumour(LostCityRumour lostCityRumour) {
        this.lostCityRumour = lostCityRumour;
    }

    public boolean hasLostCityRumour() {
        return lostCityRumour != null;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setExplored(boolean explored) {
        this.explored = explored;
    }

    
    public boolean isExplored() {
        return explored;
    }

    public void setSkill(UnitType newSkill) {
        this.skill = newSkill;
    }

    public UnitType getSkill() {
        return skill;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

    public void setWantedGoods(GoodsType[] newWantedGoods) {
        this.wantedGoods = newWantedGoods;
    }

    public GoodsType[] getWantedGoods() {
        return wantedGoods;
    }

    public void setMissionary(Unit missionary) {
        this.missionary = missionary;
    }

    public Unit getMissionary() {
        return missionary;
    }

    public void setVisited() {
        settlementVisited = true;
    }

    public boolean hasBeenVisited() {
        return settlementVisited;
    }

    
    public Player getPlayer() {
        return player;
    }

    
    public void toXMLImpl(XMLStreamWriter out, Player player, boolean showAll, boolean toSavedGame)
        throws XMLStreamException {

        
        out.writeStartElement(getXMLElementTagName());
        out.writeAttribute(ID_ATTRIBUTE, getId());

        out.writeAttribute("player", player.getId());
        out.writeAttribute("tile", tile.getId());

        if (!explored) {
            out.writeAttribute("explored", Boolean.toString(explored));
        }
        if (tile.getOwner() != owner && owner != null) {
            out.writeAttribute("owner", owner.getId());
        }

        out.writeAttribute("connected", Boolean.toString(connected));

        if (tile.getSettlement() != null) {
            if (tile.getSettlement() instanceof Colony) {
                out.writeAttribute("colonyUnitCount", Integer.toString(colonyUnitCount));
                out.writeAttribute("colonyStockadeLevel", Integer.toString(colonyStockadeLevel));
            } else if (settlementVisited) {
                out.writeAttribute("settlementVisited", Boolean.toString(settlementVisited));
                writeAttribute(out, "learnableSkill", skill);
                writeAttribute(out, "wantedGoods0", wantedGoods[0]);
                writeAttribute(out, "wantedGoods1", wantedGoods[1]);
                writeAttribute(out, "wantedGoods2", wantedGoods[2]);
                
                if (missionary != null) {
                    out.writeStartElement("missionary");
                    missionary.toXML(out, player, showAll, toSavedGame);
                    out.writeEndElement();
                }
            }
        }
        if (tile.hasResource()) {
            resource.toXML(out, player, showAll, toSavedGame);
        }
        if (tile.hasLostCityRumour()) {
            lostCityRumour.toXML(out, player, showAll, toSavedGame);
        }
        for (TileImprovement t : improvements) { 
            t.toXML(out, player, showAll, toSavedGame);
        }

        out.writeEndElement();
    }

    
    public void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {

        setId(in.getAttributeValue(null, ID_ATTRIBUTE));
        
        player = getFreeColGameObject(in, "player", Player.class);
        tile = getFreeColGameObject(in, "tile", Tile.class);

        explored = getAttribute(in, "explored", true);
        colonyUnitCount = getAttribute(in, "colonyUnitCount", 0);
        colonyStockadeLevel = getAttribute(in, "colonyStockadeLevel", 0);
        connected = getAttribute(in, "connected", false);

        owner = getFreeColGameObject(in, "owner", Player.class, tile.getOwner());

        settlementVisited = getAttribute(in, "settlementVisited", false);
        if (settlementVisited) {
            Specification spec = FreeCol.getSpecification();
            skill = spec.getType(in, "learnableSkill", UnitType.class, null);
            wantedGoods[0] = spec.getType(in, "wantedGoods0", GoodsType.class, null);
            wantedGoods[1] = spec.getType(in, "wantedGoods1", GoodsType.class, null);
            wantedGoods[2] = spec.getType(in, "wantedGoods2", GoodsType.class, null);
        }

        missionary = null;
        TileItemContainer tileItemContainer = new TileItemContainer(getGame(), tile);
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if (in.getLocalName().equals(IndianSettlement.MISSIONARY_TAG_NAME)) {
                in.nextTag(); 
                missionary = (Unit) getGame().getFreeColGameObject(in.getAttributeValue(null, ID_ATTRIBUTE));
                if (missionary == null) {
                    missionary = new Unit(getGame(), in);
                } else {
                    missionary.readFromXML(in);
                }
                in.nextTag(); 
            } else if (in.getLocalName().equals(Resource.getXMLElementTagName())) {
                Resource resource = (Resource) getGame().getFreeColGameObject(in.getAttributeValue(null, ID_ATTRIBUTE));
                if (resource != null) {
                    resource.readFromXML(in);
                } else {
                    resource = new Resource(getGame(), in);
                }
                tileItemContainer.addTileItem(resource);
            } else if (in.getLocalName().equals(LostCityRumour.getXMLElementTagName())) {
                LostCityRumour lostCityRumour = (LostCityRumour) getGame().getFreeColGameObject(in.getAttributeValue(null, ID_ATTRIBUTE));
                if (lostCityRumour != null) {
                    lostCityRumour.readFromXML(in);
                } else {
                    lostCityRumour = new LostCityRumour(getGame(), in);
                }
                tileItemContainer.addTileItem(lostCityRumour);
            } else if (in.getLocalName().equals(TileImprovement.getXMLElementTagName())) {
                TileImprovement ti = (TileImprovement) getGame().getFreeColGameObject(in.getAttributeValue(null, ID_ATTRIBUTE));
                if (ti != null) {
                    ti.readFromXML(in);
                } else {
                    ti = new TileImprovement(getGame(), in);
                }
                tileItemContainer.addTileItem(ti);
            } else {
                logger.warning("Unknown tag: " + in.getLocalName() + " loading PlayerExploredTile");
                in.nextTag();
            }
        }
        getTileItemInfo(tileItemContainer);
    }

                
    public static String getXMLElementTagName() {
        return "playerExploredTile";
    }
     
}
