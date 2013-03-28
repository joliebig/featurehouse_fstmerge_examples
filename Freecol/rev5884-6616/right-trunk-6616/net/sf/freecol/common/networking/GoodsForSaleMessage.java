

package net.sf.freecol.common.networking;

import java.util.List;
import java.util.ArrayList;

import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.ai.AIPlayer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



public class GoodsForSaleMessage extends Message {
    
    private String unitId;

    
    private String settlementId;

    
    private List<Goods> sellGoods;


    
    public GoodsForSaleMessage(Unit unit, Settlement settlement) {
        this.unitId = unit.getId();
        this.settlementId = settlement.getId();
        this.sellGoods = new ArrayList<Goods>();
    }

    
    public GoodsForSaleMessage(Game game, Element element) {
        this.unitId = element.getAttribute("unit");
        this.settlementId = element.getAttribute("settlement");
        this.sellGoods = new ArrayList<Goods>();
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            this.sellGoods.add(new Goods(game, (Element) children.item(i)));
        }
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
        sellGoods = settlement.getSellGoods();
        if (!sellGoods.isEmpty()) {
            AIPlayer aiPlayer = (AIPlayer) server.getAIMain().getAIObject(settlement.getOwner());
            for (Goods goods : sellGoods) {
                aiPlayer.registerSellGoods(goods);
            }
        }
        return this.toXMLElement();
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("unit", unitId);
        result.setAttribute("settlement", settlementId);
        if (!sellGoods.isEmpty()) {
            for (Goods goods : sellGoods) {
                result.appendChild(goods.toXMLElement(null, result.getOwnerDocument()));
            }
        }
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "goodsForSale";
    }
}
