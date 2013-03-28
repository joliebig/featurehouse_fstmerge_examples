

package net.sf.freecol.common.model;

import java.util.List;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;

import org.w3c.dom.Element;


public class Goods extends AbstractGoods implements Locatable, Ownable, Typed<GoodsType> {

    private static Logger logger = Logger.getLogger(Goods.class.getName());

    
    public static GoodsType FOOD, LUMBER, ORE, SILVER, HORSES,
        RUM, CIGARS, CLOTH, COATS, TRADEGOODS, TOOLS, MUSKETS, 
        FISH, BELLS, CROSSES, HAMMERS,
    
        SUGAR, TOBACCO, FURS, COTTON;

    private Game game;
    private Location location;

    

    
    public Goods(Game game, Location location, GoodsType type, int amount) {
        if (game == null) {
            throw new IllegalArgumentException("Parameter 'game' must not be 'null'.");
        }
        
        if (type == null) {
            throw new IllegalArgumentException("Parameter 'type' must not be 'null'.");
        }

        if (location != null && location.getGoodsContainer() == null){
            throw new IllegalArgumentException("This location cannot store goods");
        }

        this.game = game;
        this.location = location;
        setType(type);
        setAmount(amount);
    }

    
    public Goods(Game game, XMLStreamReader in) throws XMLStreamException {
        this.game = game;
        readFromXML(in);

    }
    
    
    public Goods(Game game, Element e) {
        this.game = game;
        readFromXMLElement(e);
    }


    

    
    public static void initialize(List<GoodsType> goodsList, int numberOfTypes) {
        for (GoodsType g : goodsList) {
            try {
                String fieldName = g.getId().substring(g.getId().lastIndexOf('.') + 1).toUpperCase();
                Goods.class.getDeclaredField(fieldName).set(null, g);
            } catch (Exception e) {
                logger.warning("Error assigning a GoodsType to Goods." +
                        g.getId().toUpperCase() + "\n" + e.toString());
            }
        }
        
    }

    

    
    public Player getOwner() {
        return (location instanceof Ownable) ? ((Ownable) location).getOwner() : null;
    }

    
    public void setOwner(Player p) {
        throw new UnsupportedOperationException();
    }

    
    public String toString() {
        return toString(this);
    }

    public static String toString(Goods goods) {
        return toString(goods.getType(), goods.getAmount());
    }

    public static String toString(GoodsType goodsType, int amount) {
        return Integer.toString(amount) + " " + goodsType.getId();
    }

    
    public Tile getTile() {
        return (location != null) ? location.getTile() : null;
    }


    
    public void setLocation(Location location) {
        
        if (location != null && location.getGoodsContainer() == null) {
            throw new IllegalArgumentException("Goods have to be located in a GoodsContainers.");
        }
        
        try {
            if ((this.location != null)) {
                this.location.remove(this);
            }

            if (location != null) {
                location.add(this);
            }

            this.location = location;
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Could not move the goods of type: "
                    + getType() + " with amount: " + getAmount() + " from "
                    + this.location + " to " + location, e);
        }
    }


    
    public Location getLocation() {
        return location;
    }


    
    public int getSpaceTaken() {
        return 1;
    }

    
    public void adjustAmount() {
        int maxAmount = location.getGoodsContainer().getGoodsCount(getType());
        
        if (getAmount() > maxAmount)
            setAmount(maxAmount);
    }

    
    public void loadOnto(Unit carrier) {
        if (getLocation() == null) {
            throw new IllegalStateException("The goods need to be taken from a place, but 'location == null'.");
        }
        if ((getLocation().getTile() != carrier.getTile())) {
            throw new IllegalStateException("It is not allowed to load cargo onto a ship on another tile.");
        }
        if (getLocation().getTile() == null){
            
            Unit source = (Unit)getLocation();

            
            if (!carrier.isInEurope() || !source.isInEurope()){
                throw new IllegalStateException("Loading cargo onto a ship that is not in port in Europe.");
            }
        }
        setLocation(carrier);
    }


    
    public void unload() {
        if (!(getLocation() instanceof Unit)){
            throw new IllegalStateException("Goods not on a unit");
        }

        Unit carrier = (Unit) getLocation();
        Location location = carrier.getLocation();

        if (location instanceof Europe || location.getTile().getSettlement() == null || !(location.getTile().getSettlement() instanceof Colony)) {
            throw new IllegalStateException("Goods may only be unloaded while the carrier is in a colony");
        }
        setLocation(location.getTile().getSettlement());
    }


    
    public Game getGame() {
        return game;
    }

    
    public void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("type", getType().getId());
        out.writeAttribute("amount", Integer.toString(getAmount()));

        if (location != null) {
            out.writeAttribute("location", location.getId());
        } else {
            logger.warning("Creating an XML-element for a 'Goods' without a 'Location'.");
        }

        out.writeEndElement();
    }
    
    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {        
        setType(FreeCol.getSpecification().getGoodsType(in.getAttributeValue(null, "type")));
        setAmount(Integer.parseInt(in.getAttributeValue(null, "amount")));

        final String locationStr = in.getAttributeValue(null, "location");
        if (locationStr != null) {
            location = (Location) getGame().getFreeColGameObject(locationStr);
        }
        
        in.nextTag();
    }


    
    public static String getXMLElementTagName() {
        return "goods";
    }
}
