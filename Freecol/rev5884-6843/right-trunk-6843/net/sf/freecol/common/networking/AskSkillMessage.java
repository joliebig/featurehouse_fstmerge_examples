

package net.sf.freecol.common.networking;

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

import org.w3c.dom.Element;



public class AskSkillMessage extends Message {
    
    private String unitId;

    
    private String directionString;

    
    public AskSkillMessage(Unit unit, Direction direction) {
        this.unitId = unit.getId();
        this.directionString = String.valueOf(direction);
    }

    
    public AskSkillMessage(Game game, Element element) {
        this.unitId = element.getAttribute("unitId");
        this.directionString = element.getAttribute("direction");
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
        Tile tile = serverPlayer.getGame().getMap()
            .getNeighbourOrNull(direction, unit.getTile());
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
        MoveType type = unit.getMoveType(settlement.getTile());
        if (type != MoveType.ENTER_INDIAN_SETTLEMENT_WITH_FREE_COLONIST) {
            return Message.clientError("Unable to enter "
                                       + settlement.getName()
                                       + ": " + type.whyIllegal());
        }

        
        return server.getInGameController()
            .askLearnSkill(serverPlayer, unit, (IndianSettlement) settlement);
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("unitId", unitId);
        result.setAttribute("direction", directionString);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "askSkill";
    }
}
