

package net.sf.freecol.common.networking;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Element;



public class BuyGoodsMessage extends Message {
    
    private String carrierId;

    
    private String goodsTypeId;

    
    private String amountString;

    
    public BuyGoodsMessage(Unit carrier, GoodsType type, int amount) {
        this.carrierId = carrier.getId();
        this.goodsTypeId = type.getId();
        this.amountString = Integer.toString(amount);
    }

    
    public BuyGoodsMessage(Game game, Element element) {
        this.carrierId = element.getAttribute("carrier");
        this.goodsTypeId = element.getAttribute("type");
        this.amountString = element.getAttribute("amount");
    }

    
    public Element handle(FreeColServer server, Player player,
                          Connection connection) {
        ServerPlayer serverPlayer = server.getPlayer(connection);

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
        if (!player.canTrade(type)) {
            return Message.clientError("Goods are boycotted: " + goodsTypeId);
        }
        int amount;
        try {
            amount = Integer.parseInt(amountString);
        } catch (NumberFormatException e) {
            return Message.clientError("Bad amount: " + amountString);
        }
        if (amount <= 0) {
            return Message.clientError("Amount must be positive: "
                                       + amountString);
        }

        
        return server.getInGameController()
            .buyGoods(serverPlayer, carrier, type, amount);
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("carrier", carrierId);
        result.setAttribute("type", goodsTypeId);
        result.setAttribute("amount", amountString);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "buyGoods";
    }
}


