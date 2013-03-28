

package net.sf.freecol.common.networking;

import java.util.Map;

import org.w3c.dom.Element;

import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.IndianSettlement;
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
        ServerPlayer serverPlayer = server.getPlayer(connection);

        Unit unit;
        IndianSettlement settlement;
        try {
            unit = server.getUnitSafely(unitId, serverPlayer);
            settlement = server.getAdjacentIndianSettlementSafely(settlementId, unit);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }

        
        if (!settlement.allowContact(unit)) {
            return Message.clientError("Contact denied at "
                                       + settlement.getName());
        }
        
        InGameController igc = server.getInGameController();
        if (!igc.isTransactionSessionOpen(unit, settlement)
            && unit.getMovesLeft() <= 0) {
            return Message.clientError("Unit " + unitId + "has 0 moves left.");
        }

        java.util.Map<String,Object> session
            = igc.getTransactionSession(unit, settlement);
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
