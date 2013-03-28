

package net.sf.freecol.common.networking;

import java.util.Map;

import org.w3c.dom.Element;

import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.control.InGameController;
import net.sf.freecol.server.model.ServerPlayer;



public class GetTransactionMessage extends Message {
    
    private String unitId;

    
    private String settlementId;

    
    public GetTransactionMessage(Unit unit, Settlement settlement) {
        this.unitId = unit.getId();
        this.settlementId = settlement.getId();
    }

    
    public GetTransactionMessage(Game game, Element element) {
        this.unitId = element.getAttribute("unit");
        this.settlementId = element.getAttribute("settlement");
    }

    
    public Element handle(FreeColServer server, Player player, Connection connection) {
        InGameController controller = (InGameController) server.getController();
        ServerPlayer serverPlayer = server.getPlayer(connection);
        Game game = player.getGame();
        Unit unit;
        Settlement settlement;

        try {
            unit = server.getUnitSafely(unitId, serverPlayer);
            settlement = server.getAdjacentIndianSettlementSafely(settlementId, unit);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }

        
        if (!controller.isTransactionSessionOpen(unit, settlement)
            && unit.getMovesLeft() <= 0) {
            return Message.clientError("Unit " + unitId + "has no moves left.");
        }

        java.util.Map<String,Object> session = controller.getTransactionSession(unit, settlement);
        Element reply = Message.createNewRootElement("getTransactionAnswer");
        reply.setAttribute("canBuy", ((Boolean) session.get("canBuy")).toString());
        reply.setAttribute("canSell", ((Boolean) session.get("canSell")).toString());
        reply.setAttribute("canGift", ((Boolean) session.get("canGift")).toString());

        
        
        
        unit.setMovesLeft(0);

        return reply;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("unit", unitId);
        result.setAttribute("settlement", settlementId);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "getTransaction";
    }
}
