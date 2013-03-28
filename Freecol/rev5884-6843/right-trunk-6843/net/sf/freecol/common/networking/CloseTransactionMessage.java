

package net.sf.freecol.common.networking;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Element;



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

        Unit unit;
        Settlement settlement;
        try {
            unit = server.getUnitSafely(unitId, serverPlayer);
            settlement = server.getAdjacentIndianSettlementSafely(settlementId, unit);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }

        
        return server.getInGameController()
            .closeTransaction(serverPlayer, unit, settlement);
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
