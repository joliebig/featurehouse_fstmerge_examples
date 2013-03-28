

package net.sf.freecol.common.networking;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.ModelMessage;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.MoveType;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Element;



public class MissionaryMessage extends Message {
    
    private String unitId;

    
    private String directionString;

    
    private boolean denounce;

    
    public MissionaryMessage(Unit unit, Direction direction, boolean denounce) {
        this.unitId = unit.getId();
        this.directionString = String.valueOf(direction);
        this.denounce = denounce;
    }

    
    public MissionaryMessage(Game game, Element element) {
        this.unitId = element.getAttribute("unitId");
        this.directionString = element.getAttribute("direction");
        this.denounce = Boolean.valueOf(element.getAttribute("denounce")).booleanValue();
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
        if (unit.getTile() == null) {
            return Message.clientError("Unit is not on the map: " + unitId);
        }
        Direction direction = Enum.valueOf(Direction.class, directionString);
        Game game = serverPlayer.getGame();
        Map map = game.getMap();
        Tile tile = map.getNeighbourOrNull(direction, unit.getTile());
        if (tile == null) {
            return Message.clientError("Could not find tile"
                                       + " in direction: " + direction
                                       + " from unit: " + unitId);
        }
        Settlement settlement = tile.getSettlement();
        if (settlement == null || !(settlement instanceof IndianSettlement)) {
            return Message.clientError("There is no native settlement at: "
                                       + tile.getId());
        }
        IndianSettlement indianSettlement = (IndianSettlement) settlement;
        Unit missionary = indianSettlement.getMissionary();
        if (denounce) {
            if (missionary == null) {
                return Message.clientError("Denouncing an empty mission at: "
                                           + indianSettlement.getId());
            } else if (missionary.getOwner() == player) {
                return Message.clientError("Denouncing our own missionary at: "
                                           + indianSettlement.getId());
            }
        } else {
            if (missionary != null) {
                return Message.clientError("Establishing extra mission at: "
                                           + indianSettlement.getId());
            }
        }
        MoveType type = unit.getMoveType(settlement.getTile());
        if (type != MoveType.ENTER_INDIAN_SETTLEMENT_WITH_MISSIONARY) {
            return Message.clientError("Unable to enter "
                                       + settlement.getName()
                                       + ": " + type.whyIllegal());
        }

        
        return (denounce)
            ? server.getInGameController()
                .denounceMission(serverPlayer, unit, indianSettlement)
            : server.getInGameController()
                .establishMission(serverPlayer, unit, indianSettlement);
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("unitId", unitId);
        result.setAttribute("direction", directionString);
        result.setAttribute("denounce", Boolean.toString(denounce));
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "missionary";
    }
}
