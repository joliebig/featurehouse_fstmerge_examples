


package net.sf.freecol.server.ai;

import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Unit;

import org.w3c.dom.Element;



public class GoodsWish extends Wish {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(GoodsWish.class.getName());
    
    private GoodsType goodsType;
    private int amountRequested;

    
    public GoodsWish(AIMain aiMain, Location destination, int value, int amountRequested, GoodsType goodsType) {
        super(aiMain, getXMLElementTagName() + ":" + aiMain.getNextID());

        if (destination == null) {
            throw new NullPointerException("destination == null");
        }       

        this.destination = destination;
        setValue(value);
        this.goodsType = goodsType;
        this.amountRequested = amountRequested;
    }

    
    public GoodsWish(AIMain aiMain, Location destination, int value, GoodsType goodsType) {
        this(aiMain,destination,value,100,goodsType);
    }

    
    public GoodsWish(AIMain aiMain, Element element) {
        super(aiMain, element.getAttribute("ID"));
        readFromXMLElement(element);
    }
    
    
     public GoodsWish(AIMain aiMain, String id) {
         super(aiMain, id);
     }
     
     
     public GoodsWish(AIMain aiMain, XMLStreamReader in) throws XMLStreamException {
         super(aiMain, in.getAttributeValue(null, "ID"));
         readFromXML(in);
     }

     
     
     public GoodsType getGoodsType() {
         return goodsType;
     }
     
     public int getGoodsAmount() {
        return amountRequested;
     }
     
     
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
         
        out.writeAttribute("ID", getId());
         
        out.writeAttribute("destination", destination.getId());
        if (transportable != null) {
            out.writeAttribute("transportable", transportable.getId());
        }
        out.writeAttribute("value", Integer.toString(getValue()));
        out.writeAttribute("goodsType", goodsType.getId());
        out.writeAttribute("amountRequested", Integer.toString(amountRequested));

        out.writeEndElement();
     }

     
     protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {        
         setId(in.getAttributeValue(null, "ID"));
         destination = (Location) getAIMain().getFreeColGameObject(in.getAttributeValue(null, "destination"));
         
         final String transportableStr = in.getAttributeValue(null, "transportable");
         if (transportableStr != null) {
             transportable = (Transportable) getAIMain().getAIObject(transportableStr);
             if (transportable == null) {
                 transportable = new AIGoods(getAIMain(), transportableStr);
             }
         } else {
             transportable = null;
         }
         setValue(Integer.parseInt(in.getAttributeValue(null, "value")));
         
         goodsType = FreeCol.getSpecification().getGoodsType(in.getAttributeValue(null, "goodsType"));

         final String amountStr = in.getAttributeValue(null, "amountRequested");
         if (amountStr != null) {
            amountRequested = Integer.parseInt(amountStr);
         } else {
            
            
            amountRequested = 100;    
         }
         
         in.nextTag();
     }


    
    public static String getXMLElementTagName() {
        return "GoodsWish";
    }

    public String toString() {
        return "GoodsWish: " + amountRequested + " " + goodsType
            + " (" + getValue() + ")";
    }

}
