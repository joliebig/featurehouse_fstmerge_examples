


package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;


public class GoodsTradeItem extends TradeItem {
    
    
    private Goods goods;

    
    private Settlement settlement;
        
    
    public GoodsTradeItem(Game game, Player source, Player destination, Goods goods, Settlement settlement) {
        super(game, "tradeItem.goods", source, destination);
        this.goods = goods;
        this.settlement = settlement;
    }

    
    public GoodsTradeItem(Game game, XMLStreamReader in) throws XMLStreamException {
        super(game, in);
        readFromXMLImpl(in);
    }

    
    public final Goods getGoods() {
        return goods;
    }

    
    public final void setGoods(final Goods newGoods) {
        this.goods = newGoods;
    }

    
    public final Settlement getSettlement() {
        return settlement;
    }

    
    public final void setSettlement(final Settlement newSettlement) {
        this.settlement = newSettlement;
    }

    
    public boolean isValid() {
        if (!(goods.getLocation() instanceof Unit)) {
            return false;
        }
        Unit unit = (Unit) goods.getLocation();
        if (unit.getOwner() != getSource()) {
            return false;
        }
        if (settlement != null && settlement.getOwner() == getDestination()) {
            return true;
        } else {
            return false;
        }

    }
    
    
    public boolean isUnique() {
        return false;
    }

    
    public List<FreeColGameObject> makeTrade() {
        Location where = goods.getLocation();
        where.remove(goods);
        settlement.add(goods);
        List<FreeColGameObject> result = new ArrayList<FreeColGameObject>();
        result.add(where.getGoodsContainer());
        result.add(settlement);
        return result;
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        super.readFromXMLImpl(in);
        this.settlement = (Settlement) getGame().getFreeColGameObject(in.getAttributeValue(null, "settlement"));
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if (in.getLocalName().equals(Goods.getXMLElementTagName())) {
                this.goods = new Goods(getGame(), in);
            }
        }
    }

    
    public void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
        super.toXMLImpl(out);
        out.writeAttribute("settlement", settlement.getId());
        this.goods.toXML(out);
        out.writeEndElement();
    }
    
    
    public static String getXMLElementTagName() {
        return "goodsTradeItem";
    }

}


