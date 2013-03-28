

package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;


public class GoldTradeItem extends TradeItem {
    
    
    private int gold;
        
    
    public GoldTradeItem(Game game, Player source, Player destination, int gold) {
        super(game, "tradeItem.gold", source, destination);
        this.gold = gold;
    }

    
    public GoldTradeItem(Game game, XMLStreamReader in) throws XMLStreamException {
        super(game, in);
        readFromXMLImpl(in);
    }


    
    public final int getGold() {
        return gold;
    }

    
    public final void setGold(final int newGold) {
        this.gold = newGold;
    }

    
    public boolean isValid() {
        return ((gold >= 0) && (getSource().getGold() >= gold));
    }

    
    public boolean isUnique() {
        return true;
    }

    
    public List<FreeColGameObject> makeTrade() {
        getSource().modifyGold(-gold);
        getDestination().modifyGold(gold);
        List<FreeColGameObject> result = new ArrayList<FreeColGameObject>();
        result.add(getSource());
        result.add(getDestination());
        return result;
    }


    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        super.readFromXMLImpl(in);
        this.gold = Integer.parseInt(in.getAttributeValue(null, "gold"));
        in.nextTag();
    }

    
    public void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
        super.toXMLImpl(out);
        out.writeAttribute("gold", Integer.toString(this.gold));
        out.writeEndElement();
    }
    
    
    public static String getXMLElementTagName() {
        return "goldTradeItem";
    }

}
