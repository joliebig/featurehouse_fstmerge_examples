

package net.sf.freecol.common.networking;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Element;



public class UpdateCurrentStopMessage extends Message {
    
    private String unitId;

    
    public UpdateCurrentStopMessage(Unit unit) {
        this.unitId = unit.getId();
    }

    
    public UpdateCurrentStopMessage(Game game, Element element) {
        this.unitId = element.getAttribute("unit");
    }

    
    public Element handle(FreeColServer server, Connection connection) {
        ServerPlayer serverPlayer = server.getPlayer(connection);

        Unit unit;
        try {
            unit = server.getUnitSafely(unitId, serverPlayer);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }

        
        return server.getInGameController()
            .updateCurrentStop(serverPlayer, unit);
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("unit", unitId);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "updateCurrentStop";
    }
}
