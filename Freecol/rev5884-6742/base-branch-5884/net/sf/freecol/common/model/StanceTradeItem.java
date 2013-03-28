

package net.sf.freecol.common.model;

import java.util.Collections;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.model.Player.Stance;


public class StanceTradeItem extends TradeItem {
    
    
    private Stance stance;
        
    
    public StanceTradeItem(Game game, Player source, Player destination, Stance stance) {
        super(game, "tradeItem.stance", source, destination);
        this.stance = stance;
    }

    
    public StanceTradeItem(Game game, XMLStreamReader in) throws XMLStreamException {
        super(game, in);
        readFromXMLImpl(in);
    }

    
    public final Stance getStance() {
        return stance;
    }

    
    public final void setStance(final Stance newStance) {
        this.stance = newStance;
    }

    
    public boolean isValid() {
        return stance != null;
    }

    
    public boolean isUnique() {
        return true;
    }

    
    public List<FreeColGameObject> makeTrade() {
        getSource().changeRelationWithPlayer(getDestination(), stance);
        
        return Collections.emptyList();
    }


    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        super.readFromXMLImpl(in);
        this.stance = Enum.valueOf(Stance.class, in.getAttributeValue(null, "stance"));
        in.nextTag();
    }

    
    public void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
        super.toXMLImpl(out);
        out.writeAttribute("stance", this.stance.toString());
        out.writeEndElement();
    }
    
    
    public static String getXMLElementTagName() {
        return "stanceTradeItem";
    }

}

