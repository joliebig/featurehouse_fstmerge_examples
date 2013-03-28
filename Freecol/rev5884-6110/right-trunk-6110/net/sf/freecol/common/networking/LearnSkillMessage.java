

package net.sf.freecol.common.networking;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.PlayerExploredTile;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tension;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.model.UnitTypeChange;
import net.sf.freecol.common.model.UnitTypeChange.ChangeType;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.control.InGameController;
import net.sf.freecol.server.model.ServerPlayer;



public class LearnSkillMessage extends Message {
    
    private String unitId;

    
    private String directionString;

    
    public LearnSkillMessage(Unit unit, Direction direction) {
        this.unitId = unit.getId();
        this.directionString = String.valueOf(direction);
    }

    
    public LearnSkillMessage(Game game, Element element) {
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

        
        
        InGameController igc = server.getInGameController();
        Element reply = Message.createNewRootElement("multiple");
        Document doc = reply.getOwnerDocument();
        IndianSettlement indianSettlement = (IndianSettlement) settlement;
        Tension tension = indianSettlement.getAlarm(player);
        if (tension == null) tension = new Tension(0);
        switch (tension.getLevel()) {
        case HATEFUL: 
            unit.dispose();

            Element remove = doc.createElement("remove");
            reply.appendChild(remove);
            unit.addToRemoveElement(remove);
            break;
        case ANGRY: 
            unit.setMovesLeft(0);

            Element updateFail = doc.createElement("update");
            reply.appendChild(updateFail);
            updateFail.appendChild(unit.toXMLElementPartial(doc, "movesLeft"));
            break;
        default: 
            try {
                igc.learnFromIndianSettlement(unit, indianSettlement);
            } catch (Exception e) {
                return Message.clientError(e.getMessage());
            }

            Element updateSuccess = doc.createElement("update");
            reply.appendChild(updateSuccess);
            updateSuccess.appendChild(unit.toXMLElement(player, doc));
            updateSuccess.appendChild(indianSettlement.toXMLElement(player, doc));
            break;
        }
        return reply;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("unitId", unitId);
        result.setAttribute("direction", directionString);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "learnSkill";
    }
}
