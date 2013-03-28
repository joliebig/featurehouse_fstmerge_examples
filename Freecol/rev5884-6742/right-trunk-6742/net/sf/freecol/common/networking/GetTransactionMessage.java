

package net.sf.freecol.common.networking;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Element;



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

        return server.getInGameController()
            .getTransaction(serverPlayer, unit, settlement);
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
