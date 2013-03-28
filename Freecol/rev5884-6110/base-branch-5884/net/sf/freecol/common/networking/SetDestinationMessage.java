

package net.sf.freecol.common.networking;

import org.w3c.dom.Element;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;



public class SetDestinationMessage extends Message {
    
    String unitId;

    
    String destinationId;


    
    public SetDestinationMessage(Unit unit, Location destination) {
        this.unitId = unit.getId();
        this.destinationId = (destination == null) ? null : destination.getId();
    }

    
    public SetDestinationMessage(Game game, Element element) {
        this.unitId = element.getAttribute("unit");
        this.destinationId = element.getAttribute("destination");
    }

    
    public Element handle(FreeColServer server, Connection connection) {
        ServerPlayer serverPlayer = server.getPlayer(connection);
        Game game = serverPlayer.getGame();
        Unit unit = server.getUnitSafely(unitId, serverPlayer);
        Location destination;

        if (destinationId == null || destinationId.length() == 0) {
            destination = null;
        } else if (!(game.getFreeColGameObject(destinationId) instanceof Location)) {
            return Message.clientError("Not a location ID: " + destinationId);
        } else {
            destination = (Location) game.getFreeColGameObject(destinationId);
        }
        unit.setDestination(destination);

        Element reply = Message.createNewRootElement("update");
        reply.appendChild(unit.toXMLElement(serverPlayer, reply.getOwnerDocument()));
        return reply;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("unit", unitId);
        if (destinationId != null) {
            result.setAttribute("destination", destinationId);
        }
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "setDestination";
    }
}
