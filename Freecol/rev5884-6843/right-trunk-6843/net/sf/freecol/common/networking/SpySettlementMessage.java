

package net.sf.freecol.common.networking;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.MoveType;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;



public class SpySettlementMessage extends Message {
    
    private String unitId;

    
    private String directionString;

    
    public SpySettlementMessage(Unit unit, Direction direction) {
        this.unitId = unit.getId();
        this.directionString = String.valueOf(direction);
    }

    
    public SpySettlementMessage(Game game, Element element) {
        this.unitId = element.getAttribute("unit");
        this.directionString = element.getAttribute("direction");
    }


    
    public Element handle(FreeColServer server, Connection connection) {
        ServerPlayer serverPlayer = server.getPlayer(connection);

        Unit unit;
        try {
            unit = server.getUnitSafely(unitId, serverPlayer);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }
        if (unit.getTile() == null) {
            return Message.clientError("Unit is not on the map: " + unitId);
        }
        Direction direction = Enum.valueOf(Direction.class, directionString);
        Game game = serverPlayer.getGame();
        Tile tile = game.getMap().getNeighbourOrNull(direction, unit.getTile());
        if (tile == null) {
            return Message.clientError("Could not find tile"
                                       + " in direction: " + direction
                                       + " from unit: " + unitId);
        }
        Settlement settlement = tile.getSettlement();
        if (settlement == null) {
            return Message.clientError("There is no settlement at: "
                                       + tile.getId());
        }
        MoveType type = unit.getMoveType(settlement.getTile());
        if (type != MoveType.ENTER_FOREIGN_COLONY_WITH_SCOUT) {
            return Message.clientError("Unable to enter at: "
                                       + settlement.getName()
                                       + ": " + type.whyIllegal());
        }

        unit.setMovesLeft(0);
        
        
        
        
        
        
        Element reply = createNewRootElement("update");
        Document doc = reply.getOwnerDocument();
        reply.appendChild(tile.toXMLElement(serverPlayer, doc, true, false));
        reply.appendChild(tile.toXMLElement(serverPlayer, doc, false, false));
        reply.appendChild(unit.toXMLElement(serverPlayer, doc));
        return reply;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("unit", unitId);
        result.setAttribute("direction", directionString);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "spySettlement";
    }
}
