

package net.sf.freecol.common.networking;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.UnitState;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;



public class DisembarkMessage extends Message {
    
    private String unitId;

    
    public DisembarkMessage(Unit unit) {
        this.unitId = unit.getId();
    }

    
    public DisembarkMessage(Game game, Element element) {
        this.unitId = element.getAttribute("unit");
    }

    
    public Element handle(FreeColServer server, Player player, Connection connection) {
        ServerPlayer serverPlayer = server.getPlayer(connection);
        Unit unit;
        try {
            unit = server.getUnitSafely(unitId, serverPlayer);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }
        if (!(unit.getLocation() instanceof Unit)) {
            return Message.clientError("Not on a carrier: " + unitId);
        }

        
        Unit carrier = (Unit) unit.getLocation();
        if (!server.getInGameController().disembarkUnit(serverPlayer, unit)) {
            return Message.clientError("Unable to disembark " + unitId
                                       + " from " + carrier.getId());
        }

        
        Element reply = Message.createNewRootElement("update");
        Document doc = reply.getOwnerDocument();
        if (carrier.isInEurope()) {
            Europe europe = (Europe) carrier.getLocation();
            reply.appendChild(europe.toXMLElement(player, doc));
        } else {
            Tile tile = (Tile) carrier.getLocation();
            reply.appendChild(tile.toXMLElement(player, doc));
        }
        return reply;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("unit", unitId);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "disembark";
    }
}
