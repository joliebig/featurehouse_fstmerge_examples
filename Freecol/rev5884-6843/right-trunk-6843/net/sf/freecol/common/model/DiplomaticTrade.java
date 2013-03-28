

package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.model.Player.Stance;

import org.w3c.dom.Element;



public class DiplomaticTrade extends FreeColObject {

    
    public static enum TradeStatus {
        PROPOSE_TRADE,
        ACCEPT_TRADE,
        REJECT_TRADE
    }


    
    private List<TradeItem> items;

    private final Game game;

    
    private Player sender;

    
    private Player recipient;

    
    private TradeStatus status;


    
    public DiplomaticTrade(Game game, Player sender, Player recipient) {
        this(game, sender, recipient, new ArrayList<TradeItem>());
    }

    
    public DiplomaticTrade(Game game, Player sender, Player recipient,
                           List<TradeItem> items) {
        this.game = game;
        this.sender = sender;
        this.recipient = recipient;
        this.items = items;
        this.status = TradeStatus.PROPOSE_TRADE;
    }

    
    public DiplomaticTrade(Game game, Element element) {
        this.game = game;
        readFromXMLElement(element);
    }


    
    public TradeStatus getStatus() {
        return status;
    }

    
    public void setStatus(TradeStatus status) {
        this.status = status;
    }

    
    public final Player getSender() {
        return sender;
    }

    
    public final void setSender(final Player newSender) {
        this.sender = newSender;
    }

    
    public final Player getRecipient() {
        return recipient;
    }

    
    public final void setRecipient(final Player newRecipient) {
        this.recipient = newRecipient;
    }


    
    public void add(TradeItem newItem) {
        if (newItem.isUnique()) {
            removeType(newItem);
        }
        items.add(newItem);
    }

    
    public void remove(TradeItem newItem) {
        items.remove(newItem);
    }

    
    public void remove(int index) {
        items.remove(index);
    }

    
    public void removeType(TradeItem someItem) {
        Iterator<TradeItem> itemIterator = items.iterator();
        while (itemIterator.hasNext()) {
            if (itemIterator.next().getClass() == someItem.getClass()) {
                itemIterator.remove();
            }
        }
    }


    
    public List<TradeItem> getTradeItems() {
        return items;
    }

    
    public Iterator<TradeItem> iterator() {
        return items.iterator();
    }


    
    public Stance getStance() {
        Iterator<TradeItem> itemIterator = items.iterator();
        while (itemIterator.hasNext()) {
            TradeItem item = itemIterator.next();
            if (item instanceof StanceTradeItem) {
                return ((StanceTradeItem) item).getStance();
            }
        }
        return null;
    }
    
    
    public List<Goods> getGoodsGivenBy(Player player){
        List<Goods> goodsList = new ArrayList<Goods>();
        Iterator<TradeItem> itemIterator = items.iterator();
        while (itemIterator.hasNext()) {
            TradeItem item = itemIterator.next();
            if (item instanceof GoodsTradeItem && player == item.getSource()) {
                goodsList.add(((GoodsTradeItem) item).getGoods());
            }
        }
        return goodsList;
    }
    
    
    public List<Colony> getColoniesGivenBy(Player player){
        List<Colony> colonyList = new ArrayList<Colony>();
        Iterator<TradeItem> itemIterator = items.iterator();
        while (itemIterator.hasNext()) {
            TradeItem item = itemIterator.next();
            if (item instanceof ColonyTradeItem && player==item.getSource()) {
                colonyList.add(((ColonyTradeItem) item).getColony());
            }
        }
        return colonyList;
    }
    

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        String senderString = in.getAttributeValue(null, "sender");
        sender = (Player) game.getFreeColGameObject(senderString);

        String recipientString = in.getAttributeValue(null, "recipient");
        recipient = (Player) game.getFreeColGameObject(recipientString);

        status = Enum.valueOf(TradeStatus.class,
                              in.getAttributeValue(null, "status"));

        items = new ArrayList<TradeItem>();
        TradeItem item;
        while (in.hasNext()) {
            if (in.next() != XMLStreamConstants.START_ELEMENT) continue;
            if (in.getLocalName().equals(StanceTradeItem.getXMLElementTagName())) {
                item = new StanceTradeItem(game, in);
            } else if (in.getLocalName().equals(GoodsTradeItem.getXMLElementTagName())) {
                item = new GoodsTradeItem(game, in);
            } else if (in.getLocalName().equals(GoldTradeItem.getXMLElementTagName())) {
                item = new GoldTradeItem(game, in);
            } else if (in.getLocalName().equals(ColonyTradeItem.getXMLElementTagName())) {
                item = new ColonyTradeItem(game, in);
            } else if (in.getLocalName().equals(UnitTradeItem.getXMLElementTagName())) {
                item = new UnitTradeItem(game, in);
            } else {
                logger.warning("Unknown TradeItem: " + in.getLocalName());
                continue;
            }
            items.add(item);
        }

    }

    
    public void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
        out.writeAttribute("sender", sender.getId());
        out.writeAttribute("recipient", recipient.getId());
        out.writeAttribute("status", status.toString());
        for (TradeItem item : items) item.toXML(out);
        out.writeEndElement();
    }

    
    public static String getXMLElementTagName() {
        return "diplomaticTrade";
    }
}
