

package net.sf.freecol.common.model;

import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;

import org.w3c.dom.Element;


public class Goods extends AbstractGoods implements Locatable, Ownable, Named {

    private static Logger logger = Logger.getLogger(Goods.class.getName());

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
            throw new IllegalArgumentException("This location cannot store goods: " + location.toString());
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

    
    public StringTemplate getLabel(boolean sellable) {
        return StringTemplate.template("model.goods.goodsAmount")
            .addAmount("%amount%", getAmount())
            .addStringTemplate("%goods%", getType().getLabel(sellable));
    }

    
    public Tile getTile() {
        return (location != null) ? location.getTile() : null;
    }


    
    public Location getLocation() {
        return location;
    }

    
    public void setLocation(Location location) {
        this.location = location;
    }

    
    public void changeLocation(Location location) {
       if (location != null && location.getGoodsContainer() == null) {
           throw new IllegalArgumentException("Goods have to be located in a GoodsContainers.");
       }

       if (this.location != null) {
           this.location.remove(this);
       }
       this.location = null;

       if (location != null) {
           location.add(this);
       }
       this.location = location;
    }


    
    public int getSpaceTaken() {
        return 1;
    }

    
    public void adjustAmount() {
        int maxAmount = location.getGoodsContainer().getGoodsCount(getType());
        
        if (getAmount() > maxAmount)
            setAmount(maxAmount);
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
