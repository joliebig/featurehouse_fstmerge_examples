

package net.sf.freecol.common.networking;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;

import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.ModelMessage;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.ai.AIPlayer;
import net.sf.freecol.server.control.InGameController;
import net.sf.freecol.server.model.ServerPlayer;



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

        InGameController controller = (InGameController) server.getController();
        if (!controller.isTransactionSessionOpen(unit, settlement)) {
            return Message.clientError("Trying to deliverGift without opening a transaction session");
        }
        java.util.Map<String,Object> session = controller.getTransactionSession(unit, settlement);

        IndianSettlement indianSettlement = null;
        if (settlement instanceof IndianSettlement) {
            indianSettlement = (IndianSettlement) settlement;
            indianSettlement.modifyAlarm(player, -indianSettlement.getPrice(goods) / 50);
        }

        server.getInGameController().moveGoods(goods, settlement);
        if (indianSettlement != null) {
            indianSettlement.updateWantedGoods();
            indianSettlement.getTile().updateIndianSettlementInformation(player);
        }

        session.put("actionTaken", true);
        session.put("canGift", false);
        session.put("hasSpaceLeft", unit.getSpaceLeft() != 0);

        ServerPlayer receiver = (ServerPlayer) settlement.getOwner();
        if (!receiver.isAI() && receiver.isConnected()
            && settlement instanceof Colony) {
            Element gift = Message.createNewRootElement("multiple");
            Document doc = gift.getOwnerDocument();
            Element update = doc.createElement("update");
            gift.appendChild(update);
            update.appendChild(unit.toXMLElement(receiver, doc, false, false));
            update.appendChild(settlement.toXMLElement(receiver, doc));
            Element messages = doc.createElement("addMessages");
            gift.appendChild(messages);
            ModelMessage m
                = new ModelMessage(settlement,
                                   ModelMessage.MessageType.GIFT_GOODS,
                                   goods.getType(),
                                   "model.unit.gift",
                                   "%player%", player.getNationAsString(),
                                   "%type%", goods.getName(),
                                   "%amount%", Integer.toString(goods.getAmount()),
                                   "%colony%", settlement.getName());
            messages.appendChild(m.toXMLElement(receiver, doc));
            try {
                receiver.getConnection().send(gift);
            } catch (IOException e) {
                logger.warning(e.getMessage());
            }
        }

        Element reply = Message.createNewRootElement("update");
        Document doc = reply.getOwnerDocument();
        reply.appendChild(unit.toXMLElement(player, doc));
        reply.appendChild(settlement.getTile().toXMLElement(player, doc, false, false));
        return reply;
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
