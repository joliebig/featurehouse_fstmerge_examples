

package net.sf.freecol.common.networking;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.MoveType;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Element;



public class MoveMessage extends Message {
    
    private String unitId;

    
    private String directionString;

    
    public MoveMessage(Unit unit, Direction direction) {
        this.unitId = unit.getId();
        this.directionString = String.valueOf(direction);
    }

    
    public MoveMessage(Game game, Element element) {
        this.unitId = element.getAttribute("unit");
        this.directionString = element.getAttribute("direction");
    }

    
    public Element handle(FreeColServer server, Player player,
                          Connection connection) {
        ServerPlayer serverPlayer = server.getPlayer(connection);
        Game game = server.getGame();

        Unit unit;
        try {
            unit = server.getUnitSafely(unitId, serverPlayer);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }
        Tile oldTile = unit.getTile();
        if (oldTile == null) {
            return Message.clientError("Unit is not on the map: " + unitId);
        }
        Location oldLocation = unit.getLocation();
        Direction direction = Enum.valueOf(Direction.class, directionString);
        Tile newTile = game.getMap().getNeighbourOrNull(direction, oldTile);
        if (newTile == null) {
            return Message.clientError("Could not find tile"
                                       + " in direction: " + direction
                                       + " from unit: " + unitId);
        }
        MoveType moveType = unit.getMoveType(direction);
        if (!moveType.isProgress()) {
            return Message.clientError("Illegal move for: " + unitId
                                       + " type: " + moveType
                                       + " from: " + oldLocation.getId()
                                       + " to: " + newTile.getId());
        }

        
        return server.getInGameController()
            .move(serverPlayer, unit, newTile);
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("unit", this.unitId);
        result.setAttribute("direction", this.directionString);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "move";
    }
}
