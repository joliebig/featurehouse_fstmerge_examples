

package net.sf.freecol.common.networking;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Element;



public class SellMessage extends Message {

    
    private String unitId;

    
    private String settlementId;

    
    private Goods goods;

    
    private String goldString;

    
    public SellMessage(Unit unit, Settlement settlement, Goods goods,
                       int gold) {
        this.unitId = unit.getId();
        this.settlementId = settlement.getId();
        this.goods = goods;
        this.goldString = Integer.toString(gold);
    }

    
    public SellMessage(Game game, Element element) {
        this.unitId = element.getAttribute("unit");
        this.settlementId = element.getAttribute("settlement");
        this.goods = new Goods(game, Message.getChildElement(element, Goods.getXMLElementTagName()));
        this.goldString = element.getAttribute("gold");
    }

    
    public Element handle(FreeColServer server, Player player, Connection connection) {
        ServerPlayer serverPlayer = server.getPlayer(connection);
        Game game = server.getGame();
        Unit unit;
        IndianSettlement settlement;

        try {
            unit = server.getUnitSafely(unitId, serverPlayer);
            settlement = server.getAdjacentIndianSettlementSafely(settlementId, unit);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }

        
        if (goods.getLocation() != unit) {
            return Message.createError("server.trade.noGoods",
                                       "Goods " + goods.getId()
                                       + " is not with unit " + unitId);
        }
        int gold;
        try {
            gold = Integer.parseInt(goldString);
        } catch (NumberFormatException e) {
            return Message.clientError("Bad gold: " + goldString);
        }

        
        return server.getInGameController()
            .sellToSettlement(serverPlayer, unit, settlement, goods, gold);
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("unit", unitId);
        result.setAttribute("settlement", settlementId);
        result.appendChild(goods.toXMLElement(null, result.getOwnerDocument()));
        result.setAttribute("gold", goldString);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "sell";
    }
}
