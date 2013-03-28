

package net.sf.freecol.common.networking;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.ai.AIPlayer;
import net.sf.freecol.server.control.InGameController;
import net.sf.freecol.server.model.ServerPlayer;



public class BuyMessage extends Message {
    
    private String unitId;

    
    private String settlementId;

    
    private Goods goods;

    
    private int gold;

    
    public BuyMessage(Unit unit, Settlement settlement, Goods goods, int gold) {
        this.unitId = unit.getId();
        this.settlementId = settlement.getId();
        this.goods = goods;
        this.gold = gold;
    }

    
    public BuyMessage(Game game, Element element) {
        this.unitId = element.getAttribute("unit");
        this.settlementId = element.getAttribute("settlement");
        this.gold = Integer.parseInt(element.getAttribute("gold"));
        this.goods = new Goods(game, Message.getChildElement(element, Goods.getXMLElementTagName()));
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

        
        if (goods.getLocation() != settlement) {
            return Message.createError("server.trade.noGoods",
                                       "Goods " + goods.getId()
                                       + " is not at settlement " + settlementId);
        }

        InGameController controller = (InGameController) server.getController();
        if (!controller.isTransactionSessionOpen(unit, settlement)) {
            return Message.clientError("Trying to trade without opening a transaction session");
        }
        java.util.Map<String,Object> session = controller.getTransactionSession(unit, settlement);
        if (!(Boolean) session.get("canBuy")
            || !(Boolean) session.get("hasSpaceLeft")) {
            return Message.clientError("Trying to buy in a session where buying is not allowed.");
        }

        
        AIPlayer ai = (AIPlayer) server.getAIMain().getAIObject(settlement.getOwner());
        int returnGold = ai.buyProposition(unit, goods, gold);
        if (returnGold != gold) {
            return Message.clientError("This was not the price we agreed upon! Cheater?");
        }

        Player settlementPlayer = settlement.getOwner();
        settlement.modifyAlarm(player, -gold / 50);
        settlementPlayer.modifyGold(gold);
        player.modifyGold(-gold);

        goods.setLocation(unit);
        settlement.updateWantedGoods();
        settlement.getTile().updateIndianSettlementInformation(player);

        session.put("actionTaken", true);
        session.put("canBuy", false);
        session.put("hasSpaceLeft", unit.getSpaceLeft() != 0);

        Element reply = Message.createNewRootElement("update");
        Document doc = reply.getOwnerDocument();
        reply.appendChild(player.toXMLElementPartial(doc, "gold", "score"));
        reply.appendChild(unit.toXMLElement(player, doc));
        reply.appendChild(settlement.getTile().toXMLElement(player, doc, false, false));
        return reply;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("unit", unitId);
        result.setAttribute("settlement", settlementId);
        result.setAttribute("gold", Integer.toString(gold));
        result.appendChild(goods.toXMLElement(null, result.getOwnerDocument()));
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "buy";
    }
}
