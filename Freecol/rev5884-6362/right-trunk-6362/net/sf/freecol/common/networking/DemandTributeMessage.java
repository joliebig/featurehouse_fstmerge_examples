

package net.sf.freecol.common.networking;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.ModelMessage;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;



public class DemandTributeMessage extends Message {

    
    private String unitId;

    
    private String directionString;

    
    public DemandTributeMessage(Unit unit, Direction direction) {
        this.unitId = unit.getId();
        this.directionString = String.valueOf(direction);
    }

    
    public DemandTributeMessage(Game game, Element element) {
        this.unitId = element.getAttribute("unit");
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
        if (!unit.isArmed() && unit.getRole() != Unit.Role.SCOUT) {
            return Message.clientError("Unit is neither armed nor a scout: "
                                       + unitId);
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

        
        IndianSettlement indianSettlement = (IndianSettlement) settlement;
        int gold = server.getInGameController().demandTribute(player,
                                                              indianSettlement);
        unit.setMovesLeft(0);

        
        
        
        Element reply = createNewRootElement("multiple");
        Document doc = reply.getOwnerDocument();
        Element update = doc.createElement("update");
        reply.appendChild(update);
        update.appendChild(unit.toXMLElementPartial(doc, "movesLeft"));
        if (gold > 0) {
            update.appendChild(player.toXMLElementPartial(doc, "gold"));
        }
        Element messages = doc.createElement("addMessages");
        reply.appendChild(messages);
        ModelMessage m = (gold > 0)
            ? new ModelMessage(unit,
                               ModelMessage.MessageType.FOREIGN_DIPLOMACY,
                               indianSettlement,
                               "scoutSettlement.tributeAgree",
                               "%amount%", Integer.toString(gold))
            : new ModelMessage(unit,
                               ModelMessage.MessageType.FOREIGN_DIPLOMACY,
                               indianSettlement,
                               "scoutSettlement.tributeDisagree");
        messages.appendChild(m.toXMLElement(player, doc));
        return reply;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("unit", unitId);
        result.setAttribute("direction", directionString);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "demandTribute";
    }
}
