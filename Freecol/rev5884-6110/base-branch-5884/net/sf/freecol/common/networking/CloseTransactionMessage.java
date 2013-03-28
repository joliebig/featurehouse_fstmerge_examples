

package net.sf.freecol.common.networking;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.control.InGameController;
import net.sf.freecol.server.model.ServerPlayer;



public class CloseTransactionMessage extends Message {
    
    private String unitId;

    
    private String settlementId;

    
    public CloseTransactionMessage(Unit unit, Settlement settlement) {
        this.unitId = unit.getId();
        this.settlementId = settlement.getId();
    }

    
    public CloseTransactionMessage(Game game, Element element) {
        this.unitId = element.getAttribute("unit");
        this.settlementId = element.getAttribute("settlement");
    }

    
    public Element handle(FreeColServer server, Player player, Connection connection) {
        ServerPlayer serverPlayer = server.getPlayer(connection);
        Game game = player.getGame();
        InGameController controller = (InGameController) server.getController();
        Unit unit;
        Settlement settlement;

        try {
            unit = server.getUnitSafely(unitId, serverPlayer);
            settlement = server.getAdjacentIndianSettlementSafely(settlementId, unit);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }
        if (!controller.isTransactionSessionOpen(unit, settlement)) {
            return Message.clientError("Trying to close a non-existent session.");
        }
        java.util.Map<String,Object> session = controller.getTransactionSession(unit, settlement);
        
        boolean actionTaken = ((Boolean)(session.get("actionTaken"))).booleanValue();
        if (!actionTaken) {
            Integer unitMoves = (Integer) session.get("unitMoves");
            unit.setMovesLeft(unitMoves);
            logger.info("Restoring moves for unit " + unit.getId()
                        + " to " + Integer.toString(unitMoves));
        }
        controller.closeTransactionSession(unit, settlement);

        
        Element reply = Message.createNewRootElement("update");
        Document doc = reply.getOwnerDocument();
        reply.appendChild(unit.toXMLElement(player, doc));
        return reply;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("unit", unitId);
        result.setAttribute("settlement", settlementId);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "closeTransaction";
    }
}
