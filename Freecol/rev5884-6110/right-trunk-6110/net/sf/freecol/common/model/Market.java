


package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;

import org.w3c.dom.Element;


public final class Market extends FreeColGameObject implements Ownable {

    public static final int MINIMUM_PRICE = 1, MAXIMUM_PRICE = 19;

    private Player owner;
    
    
    public static final int EUROPE = 0, CUSTOM_HOUSE = 1;
    
    private final Map<GoodsType, MarketData> marketData
        = new HashMap<GoodsType, MarketData>();

    private ArrayList<TransactionListener> transactionListeners
        = new ArrayList<TransactionListener>();
    
    

    public Market(Game game, Player player) {
        super(game);
        this.owner = player;
        
        
        for (GoodsType goodsType : FreeCol.getSpecification().getGoodsTypeList()) {
            MarketData data = new MarketData(goodsType);
            if (goodsType.isStorable()) {
                data.setAmountInMarket(goodsType.getInitialAmount());
                data.setPaidForSale(goodsType.getInitialSellPrice());
                data.setCostToBuy(goodsType.getInitialBuyPrice());
                data.setInitialPrice(goodsType.getInitialSellPrice());
                priceGoods(goodsType);
                data.setOldPrice(data.getCostToBuy());
            }
            marketData.put(goodsType, data);
        }
    }

    
    public Market(Game game, XMLStreamReader in) throws XMLStreamException {
        super(game, in);
        readFromXML(in);
    }

    
    public Market(Game game, Element e) {
        super(game, e);
        readFromXMLElement(e);
    }

    
    public Market(Game game, String id) {
        super(game, id);
    }
    
    
    private void priceGoods(GoodsType goodsType) {
        MarketData data = marketData.get(goodsType);
        if (data != null) {
            int newSalePrice = data.getInitialPrice();
            newSalePrice = Math.round(newSalePrice * goodsType.getInitialAmount()
                                      / (float) data.getAmountInMarket());
            int newPrice = newSalePrice + goodsType.getPriceDifference();

            
            
            if ((goodsType.isNewWorldGoodsType()
                 || (goodsType.getRawMaterial() != null
                     && goodsType.getRawMaterial().isNewWorldGoodsType()))
                && newSalePrice > data.getInitialPrice() + 2) {
                newSalePrice = data.getInitialPrice() + 2;
                newPrice = newSalePrice + goodsType.getPriceDifference();
            }

            if (newPrice > MAXIMUM_PRICE) {
                newPrice = MAXIMUM_PRICE;
                newSalePrice = newPrice - goodsType.getPriceDifference();
            } else if (newSalePrice < MINIMUM_PRICE) {
                newSalePrice = MINIMUM_PRICE;
                newPrice = newSalePrice + goodsType.getPriceDifference();
            }

            data.setCostToBuy(newPrice);
            data.setPaidForSale(newSalePrice);
        }
    }

    

    
    public MarketData getMarketData(GoodsType goodsType) {
        return marketData.get(goodsType);
    }

    
    public void putMarketData(GoodsType goodsType, MarketData data) {
        marketData.put(goodsType, data);
    }

    
    public Player getOwner() {
        return owner;
    }

    
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    
    public int costToBuy(GoodsType type) {
        MarketData data = marketData.get(type);
        return (data == null) ? 0 : data.getCostToBuy();
    }

    
    public int paidForSale(GoodsType type) {
        MarketData data = marketData.get(type);
        return (data == null) ? 0 : data.getPaidForSale();
    }


    
    public void sell(Goods goods, Player player) {
        sell(goods.getType(), goods.getAmount(), player, Market.EUROPE);
    }

    
    public void sell(GoodsType type, int amount, Player player) {
        sell(type, amount, player, Market.EUROPE);
    }

    
    public void sell(GoodsType type, int amount, Player player, int marketAccess) {
        if (player.canTrade(type, marketAccess)) {
            int tax = player.getTax();
            int incomeBeforeTaxes = getSalePrice(type, amount);
            int incomeAfterTaxes = ((100 - tax) * incomeBeforeTaxes) / 100;
            player.modifyGold(incomeAfterTaxes);
            player.modifySales(type, amount);
            player.modifyIncomeBeforeTaxes(type, incomeBeforeTaxes);
            player.modifyIncomeAfterTaxes(type, incomeAfterTaxes);
            
            int unitPrice = paidForSale(type);
            for(TransactionListener listener : transactionListeners) {
                listener.logSale(type, amount, unitPrice, tax);
            }

            amount = (int) player.getFeatureContainer()
                .applyModifier(amount, "model.modifier.tradeBonus",
                               type, getGame().getTurn());
            addGoodsToMarket(type, amount);
        } else {
            addModelMessage(this, ModelMessage.MessageType.WARNING,
                            "model.europe.market", "%goods%", type.getName());
        }
    }

    
    public void buy(GoodsType goodsType, int amount, Player player) {
        int price = getBidPrice(goodsType, amount);
        if (price > player.getGold()) {
            throw new IllegalStateException("Player " + player.getName()
                                            + " tried to buy " + Integer.toString(amount)
                                            + " " + goodsType.toString()
                                            + " for " + Integer.toString(price)
                                            + " but has " + Integer.toString(player.getGold())
                                            + " gold.");
        }
        player.modifyGold(-price);
        player.modifySales(goodsType, -amount);
        player.modifyIncomeBeforeTaxes(goodsType, -price);
        player.modifyIncomeAfterTaxes(goodsType, -price);

        int unitPrice = costToBuy(goodsType);
        for(TransactionListener listener : transactionListeners) {
            listener.logPurchase(goodsType, amount, unitPrice);
        }

        amount = (int) player.getFeatureContainer()
            .applyModifier(amount, "model.modifier.tradeBonus",
                           goodsType, getGame().getTurn());
        addGoodsToMarket(goodsType, -amount);
    }

    
    public void addGoodsToMarket(GoodsType goodsType, int amount) {
        MarketData data = getMarketData(goodsType);
        if (data == null) {
            data = new MarketData(goodsType);
            marketData.put(goodsType, data);
        }

        
        data.setAmountInMarket(Math.max(100, data.getAmountInMarket() + amount));
        data.setTraded(true);
        priceGoods(goodsType);
    }

    
    public int getBidPrice(GoodsType type, int amount) {
        MarketData data = marketData.get(type);
        return (data == null) ? 0 : amount * data.getCostToBuy();
    }

    
    public int getSalePrice(GoodsType type, int amount) {
        MarketData data = marketData.get(type);
        return (data == null) ? 0 : amount * data.getPaidForSale();
    }

    
    public int getSalePrice(Goods goods) {
        return getSalePrice(goods.getType(), goods.getAmount());
    }


    
    public boolean hasPriceChanged(GoodsType goodsType) {
        MarketData data = marketData.get(goodsType);
        return data != null && data.getOldPrice() != data.getCostToBuy();
    }

    
    public void flushPriceChange(GoodsType goodsType) {
        MarketData data = marketData.get(goodsType);
        if (data != null) {
            data.setOldPrice(data.getCostToBuy());
        }
    }

    
    public ModelMessage makePriceChangeMessage(GoodsType goodsType) {
        MarketData data = marketData.get(goodsType);
        int oldPrice = data.getOldPrice();
        int newPrice = data.getCostToBuy();
        int newSalePrice = data.getPaidForSale();
        return (oldPrice == newPrice) ? null
            : new ModelMessage(this,
                               ModelMessage.MessageType.MARKET_PRICES,
                               goodsType,
                               ((newPrice > oldPrice)
                                ? "model.market.priceIncrease"
                                : "model.market.priceDecrease"),
                               "%market%", owner.getMarketName(),
                               "%goods%", goodsType.getName(),
                               "%buy%", String.valueOf(newPrice),
                               "%sell%", String.valueOf(newSalePrice));
    }

    
    public void addTransactionListener(TransactionListener listener) {
        transactionListeners.add(listener);
    }

    
    public void removeTransactionListener(TransactionListener listener) {
        transactionListeners.remove(listener);
    }

    
    public TransactionListener[] getTransactionListener() {
        return transactionListeners.toArray(new TransactionListener[0]);
    }


    

    
    protected void toXMLImpl(XMLStreamWriter out, Player player,
                             boolean showAll, boolean toSavedGame)
        throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("ID", getId());
        out.writeAttribute("owner", owner.getId());
        
        for (MarketData data : marketData.values()) {
            data.toXML(out, player, showAll, toSavedGame);
        }

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setId(in.getAttributeValue(null, "ID"));
        owner = getFreeColGameObject(in, "owner", Player.class);

        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if (in.getLocalName().equals(MarketData.getXMLElementTagName())) {
                MarketData data = new MarketData();
                data.readFromXML(in);
                marketData.put(data.getGoodsType(), data);
            }
        }
        
        for (GoodsType goodsType : FreeCol.getSpecification().getGoodsTypeList()) {
            if (goodsType.isStorable()) {
                priceGoods(goodsType);
            }
        }
    }

    
    public static String getXMLElementTagName() {
        return "market";
    }
}
