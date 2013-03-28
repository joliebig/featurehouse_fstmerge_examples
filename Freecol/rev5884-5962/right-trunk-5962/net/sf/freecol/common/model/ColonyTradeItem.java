

package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;


public class ColonyTradeItem extends TradeItem {
    
    
    private String colonyID;

    
    private Colony colony;

    
    private String colonyName;

    
    public ColonyTradeItem(Game game, Player source, Player destination, Colony colony) {
        super(game, "tradeItem.colony", source, destination);
        this.colony = colony;
        colonyID = colony.getId();
        colonyName = colony.getName();
    }

    
    public ColonyTradeItem(Game game, XMLStreamReader in) throws XMLStreamException {
        super(game, in);
        readFromXMLImpl(in);
    }

    
    public final Colony getColony() {
        return colony;
    }

    
    public final void setColony(final Colony newColony) {
        this.colony = newColony;
    }

    
    public boolean isValid() {
        return (colony.getOwner() == getSource() &&
                getDestination().isEuropean());
    }

    
    public boolean isUnique() {
        return false;
    }

    
    public String getColonyName() {
        return colonyName;
    }

    
    public List<FreeColGameObject> makeTrade() {
        colony.setOwner(getDestination());
        List<FreeColGameObject> result = new ArrayList<FreeColGameObject>();
        result.add(colony);
        return result;
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        super.readFromXMLImpl(in);
        colonyID = in.getAttributeValue(null, "colony");
        colonyName = in.getAttributeValue(null, "colonyName");
        colony = (Colony) getGame().getFreeColGameObject(colonyID);
        in.nextTag();
    }

    
    public void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
        super.toXMLImpl(out);
        out.writeAttribute("colony", colonyID);
        out.writeAttribute("colonyName", colonyName);
        out.writeEndElement();
    }
    
    
    public static String getXMLElementTagName() {
        return "colonyTradeItem";
    }

}

