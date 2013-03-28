

package net.sf.freecol.common.networking;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Market;
import net.sf.freecol.common.model.ModelMessage;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.control.InGameController;
import net.sf.freecol.server.model.ServerPlayer;



public class SellGoodsMessage extends Message {

    
    private String carrierId;

    
    private String goodsTypeId;

    
    private int amount;

    
    public SellGoodsMessage(Goods goods, Unit carrier) {
        this.carrierId = carrier.getId();
        this.goodsTypeId = goods.getType().getId();
        this.amount = goods.getAmount();
    }

    
    public SellGoodsMessage(Game game, Element element) {
        this.carrierId = element.getAttribute("carrier");
        this.goodsTypeId = element.getAttribute("type");
        this.amount = Integer.parseInt(element.getAttribute("amount"));
    }

    
    public Element handle(FreeColServer server, Player player,
                          Connection connection) {
        ServerPlayer serverPlayer = server.getPlayer(connection);
        Game game = server.getGame();

        
        Unit carrier;
        try {
            carrier = server.getUnitSafely(carrierId, serverPlayer);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }
        if (!carrier.canCarryGoods()) {
            return Message.clientError("Not a carrier: " + carrierId);
        }
        if (!carrier.isInEurope()) {
            return Message.clientError("Not in Europe: " + carrierId);
        }
        GoodsType type = FreeCol.getSpecification().getGoodsType(goodsTypeId);
        if (type == null) {
            return Message.clientError("Not a goods type: " + goodsTypeId);
        }
        if (amount <= 0) {
            return Message.clientError("Amount must be positive: "
                                       + Integer.toString(amount));
        }
        int present = carrier.getGoodsContainer().getGoodsCount(type);
        if (present < amount) {
            return Message.clientError("Attempt to sell " + Integer.toString(amount)
                                       + " " + type.getId() + " but only "
                                       + Integer.toString(present) + " present.");
        }

        
        
        
        
        
        
        
        
        
        InGameController igc = server.getInGameController();
        ModelMessage message = null;
        Market market = player.getMarket();
        try {
            market.sell(type, amount, player); 
            carrier.getGoodsContainer().addGoods(type, -amount);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }
        if (market.hasPriceChanged(type)) {
            
            
            message = market.makePriceChangeMessage(type);
            market.flushPriceChange(type);
        }
        igc.propagateToEuropeanMarkets(type, amount, serverPlayer);

        
        Element reply = Message.createNewRootElement("multiple");
        Document doc = reply.getOwnerDocument();
        Element update = doc.createElement("update");
        reply.appendChild(update);
        update.appendChild(player.toXMLElementPartial(doc, "gold", "score"));
        update.appendChild(carrier.toXMLElement(player, doc));
        if (message != null) {
            update.appendChild(market.toXMLElement(player, doc));
            Element addMessages = doc.createElement("addMessages");
            reply.appendChild(addMessages);
            addMessages.appendChild(message.toXMLElement(player, doc));
        }
        return reply;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("carrier", carrierId);
        result.setAttribute("type", goodsTypeId);
        result.setAttribute("amount", Integer.toString(amount));
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "sellGoods";
    }
}
