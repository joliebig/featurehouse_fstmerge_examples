

package net.sf.freecol.common.networking;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Element;



public class EmbarkMessage extends Message {

    
    private String unitId;

    
    private String carrierId;

    
    private String directionString;


    
    public EmbarkMessage(Unit unit, Unit carrier, Direction direction) {
        this.unitId = unit.getId();
        this.carrierId = carrier.getId();
        this.directionString = (direction == null) ? null
            : String.valueOf(direction);
    }

    
    public EmbarkMessage(Game game, Element element) {
        this.unitId = element.getAttribute("unit");
        this.carrierId = element.getAttribute("carrier");
        this.directionString = (!element.hasAttribute("direction")) ? null
            : element.getAttribute("direction");
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
        Unit carrier;
        try {
            carrier = server.getUnitSafely(carrierId, serverPlayer);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }
        Location sourceLocation = unit.getLocation();
        Tile sourceTile = null;
        Tile destinationTile = null;
        Direction direction;
        if (directionString == null) {
            if (sourceLocation != carrier.getLocation()) {
                return Message.clientError("Unit: " + unitId
                                           + " and carrier: " + carrierId
                                           + " are not co-located.");
            }
            direction = null;
        } else {
            
            try {
                direction = Enum.valueOf(Direction.class, directionString);
            } catch (Exception e) {
                return Message.clientError(e.getMessage());
            }
            sourceTile = unit.getTile();
            if (sourceTile == null) {
                return Message.clientError("Unit is not on the map: " + unitId);
            }
            Map map = serverPlayer.getGame().getMap();
            destinationTile = map.getNeighbourOrNull(direction, sourceTile);
            if (destinationTile == null) {
                return Message.clientError("Could not find tile"
                                           + " in direction: " + direction
                                           + " from unit: " + unitId);
            }
            if (carrier.getTile() != destinationTile) {
                return Message.clientError("Carrier: " + carrierId
                                           + " is not at destination tile: "
                                           + destinationTile.toString());
            }
        }

        
        return server.getInGameController()
            .embarkUnit(serverPlayer, unit, carrier);
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("unit", unitId);
        result.setAttribute("carrier", carrierId);
        if (directionString != null) {
            result.setAttribute("direction", directionString);
        }
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "embark";
    }
}
