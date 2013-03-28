

package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;


public class UnitTradeItem extends TradeItem {
    
    
    private Unit unit;
        
    
    public UnitTradeItem(Game game, Player source, Player destination, Unit unit) {
        super(game, "tradeItem.unit", source, destination);
        this.unit = unit;
    }

    
    public UnitTradeItem(Game game, XMLStreamReader in) throws XMLStreamException {
        super(game, in);
        readFromXMLImpl(in);
    }

    
    public final Unit getUnit() {
        return unit;
    }

    
    public final void setUnit(final Unit newUnit) {
        this.unit = newUnit;
    }

    
    public boolean isValid() {
        return (unit.getOwner() == getSource());
    }

    
    public boolean isUnique() {
        return false;
    }
    
    
    public List<FreeColGameObject> makeTrade() {
        unit.setOwner(getDestination());
        List<FreeColGameObject> result = new ArrayList<FreeColGameObject>();
        result.add(unit);
        return result;
    }


    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        super.readFromXMLImpl(in);
        String unitID = in.getAttributeValue(null, "unit");
        this.unit = (Unit) getGame().getFreeColGameObject(unitID);
        in.nextTag();
    }

    
    public void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
        super.toXMLImpl(out);
        out.writeAttribute("unit", this.unit.getId());
        out.writeEndElement();
    }
    
    
    public static String getXMLElementTagName() {
        return "unitTradeItem";
    }

}

