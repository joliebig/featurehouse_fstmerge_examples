

package net.sf.freecol.common.model;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;


public class AbstractGoods extends FreeColObject {

    
    private GoodsType type;

    
    private int amount;

    
    public AbstractGoods() {
        
    }

    
    public AbstractGoods(GoodsType type, int amount) {
        setId(type.getId());
        this.type = type;
        this.amount = amount;
    }

    
    public final GoodsType getType() {
        return type;
    }

    
    public final void setType(final GoodsType newType) {
        this.type = newType;
    }

    
    public final int getAmount() {
        return amount;
    }

    
    public final void setAmount(final int newAmount) {
        this.amount = newAmount;
    }

        
    public void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("type", getId());
        out.writeAttribute("amount", Integer.toString(amount));
        out.writeEndElement();
    }
    
    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {        
        type = FreeCol.getSpecification().getGoodsType(in.getAttributeValue(null, "type"));
        amount = Integer.parseInt(in.getAttributeValue(null, "amount"));
        in.nextTag();
    }


    
    public static String getXMLElementTagName() {
        return "abstractGoods";
    }

    public String toString() {
        return Integer.toString(amount) + " " + type;
    }

}
