

package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;


public class TradeRouteStop {

    private Location location;

    
    private boolean modified = false;

    
    private List<AbstractGoods> goodsToUnload;

    
    private List<AbstractGoods> goodsToLoad;


    
    public TradeRouteStop(Location location) {
        this.location = location;
    }

    
    public final List<AbstractGoods> getGoodsToLoad() {
        return goodsToLoad;
    }

    
    public final void setGoodsToLoad(final List<AbstractGoods> newGoodsToLoad) {
        this.goodsToLoad = newGoodsToLoad;
    }

    
    public final List<AbstractGoods> getGoodsToUnload() {
        return goodsToUnload;
    }

    
    public final void setGoodsToUnload(final List<AbstractGoods> newGoodsToUnload) {
        this.goodsToUnload = newGoodsToUnload;
    }

    
    public final boolean isModified() {
        return modified;
    }

    
    public final void setModified(final boolean newModified) {
        this.modified = newModified;
    }

    
    public final Location getLocation() {
        return location;
    }

    
    public void setLocation(Location newLocation) {
        this.location = newLocation;
    }

    public String toString() {
        if (location == null) {
            return null;
        } else {
            return location.getLocationName().getId();
        }
    }

    public void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
        out.writeAttribute("location", this.location.getId());
        if (goodsToUnload != null) {
            out.writeStartElement("goodsToUnload");
            for (AbstractGoods goods : goodsToUnload) {
                goods.toXML(out);
            }
            out.writeEndElement();
        }
        if (goodsToLoad != null) {
            out.writeStartElement("goodsToLoad");
            for (AbstractGoods goods : goodsToLoad) {
                goods.toXML(out);
            }
            out.writeEndElement();
        }
        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        readFromXMLImpl(in, null);
    }

    
    protected void readFromXMLImpl(XMLStreamReader in, Game game) throws XMLStreamException {
        if (game != null) {
            location = (Location) game.getFreeColGameObject(in.getAttributeValue(null, "location"));
        }
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if (in.getLocalName().equals("goodsToUnload")) {
                goodsToUnload = new ArrayList<AbstractGoods>();
                while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
                    if (in.getLocalName().equals(AbstractGoods.getXMLElementTagName())) {
                        AbstractGoods goods = new AbstractGoods();
                        goods.readFromXML(in);
                        goodsToUnload.add(goods);
                    }
                }
            } else if (in.getLocalName().equals("goodsToLoad")) {
                goodsToLoad = new ArrayList<AbstractGoods>();
                while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
                    if (in.getLocalName().equals(AbstractGoods.getXMLElementTagName())) {
                        AbstractGoods goods = new AbstractGoods();
                        goods.readFromXML(in);
                        goodsToLoad.add(goods);
                    }
                }
            }
        }
    }


    
    public static String getXMLElementTagName() {
        return "tradeRouteStop";
    }

}



