

package net.sf.freecol.common.networking;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Element;



public class CashInTreasureTrainMessage extends Message {
    
    private String unitId;

    
    public CashInTreasureTrainMessage(Unit unit) {
        this.unitId = unit.getId();
    }

    
    public CashInTreasureTrainMessage(Game game, Element element) {
        this.unitId = element.getAttribute("unit");
    }

    
    public Element handle(FreeColServer server, Player player,
                          Connection connection) {
        ServerPlayer serverPlayer = server.getPlayer(connection);

        Unit unit;
        try {
            unit = server.getUnitSafely(unitId, serverPlayer);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }
        if (!unit.canCarryTreasure()) {
            return Message.clientError("Can not cash in unit " + unitId
                                       + ", can not carry treasure.");
        }
        if (!unit.canCashInTreasureTrain()) {
            return Message.clientError("Can not cash in unit " + unitId
                                       + ", unsuitable location.");
        }

        
        return server.getInGameController()
            .cashInTreasureTrain(serverPlayer, unit);
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("unit", unitId);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "cashInTreasureTrain";
    }
}
