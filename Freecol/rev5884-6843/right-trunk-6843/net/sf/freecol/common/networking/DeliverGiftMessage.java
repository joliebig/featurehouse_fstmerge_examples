

package net.sf.freecol.common.networking;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Element;



public class DeliverGiftMessage extends Message {
    
    private String unitId;

    
    private String settlementId;

    
    private Goods goods;

    
    public DeliverGiftMessage(Unit unit, Settlement settlement, Goods goods) {
        this.unitId = unit.getId();
        this.settlementId = settlement.getId();
        this.goods = goods;
    }

    
    public DeliverGiftMessage(Game game, Element element) {
        this.unitId = element.getAttribute("unit");
        this.settlementId = element.getAttribute("settlement");
        this.goods = new Goods(game, Message.getChildElement(element, Goods.getXMLElementTagName()));
    }

    
    public Unit getUnit() {
        try {
            return (Unit) goods.getGame().getFreeColGameObject(unitId);
        } catch (Exception e) {
        }
        return null;
    }

    
    public Settlement getSettlement() {
        try {
            return (Settlement) goods.getGame().getFreeColGameObject(settlementId);
        } catch (Exception e) {
        }
        return null;
    }

    
    public Goods getGoods() {
        return goods;
    }

    
    public Element handle(FreeColServer server, Player player, Connection connection) {
        ServerPlayer serverPlayer = server.getPlayer(connection);
        Game game = server.getGame();

        Unit unit;
        Settlement settlement;
        try {
            unit = server.getUnitSafely(unitId, serverPlayer);
            settlement = server.getAdjacentSettlementSafely(settlementId, unit);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }
        
        if (goods.getLocation() != unit) {
            return Message.createError("server.trade.noGoods", "deliverGift of non-existent goods");
        }

        
        return server.getInGameController()
            .deliverGiftToSettlement(serverPlayer, unit, settlement, goods);
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("unit", unitId);
        result.setAttribute("settlement", settlementId);
        result.appendChild(goods.toXMLElement(null, result.getOwnerDocument()));
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "deliverGift";
    }
}
