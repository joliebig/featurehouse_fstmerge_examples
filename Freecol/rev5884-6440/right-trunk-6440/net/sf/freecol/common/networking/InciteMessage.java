

package net.sf.freecol.common.networking;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.MoveType;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.control.InGameController;
import net.sf.freecol.server.model.ServerPlayer;



public class InciteMessage extends Message {
    
    private String unitId;

    
    private String directionString;

    
    private String enemyId;

    
    private String goldString;

    
    public InciteMessage(Unit unit, Direction direction, Player enemy, int gold) {
        this.unitId = unit.getId();
        this.directionString = String.valueOf(direction);
        this.enemyId = enemy.getId();
        this.goldString = Integer.toString(gold);
    }

    
    public InciteMessage(Game game, Element element) {
        this.unitId = element.getAttribute("unitId");
        this.directionString = element.getAttribute("direction");
        this.enemyId = element.getAttribute("enemyId");
        this.goldString = element.getAttribute("gold");
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
        if (unit.getTile() == null) {
            return Message.clientError("Unit is not on the map: " + unitId);
        }
        Direction direction = Enum.valueOf(Direction.class, directionString);
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
        Player settlementPlayer = indianSettlement.getOwner();
        Player enemy;
        if (enemyId == null || enemyId.length() == 0) {
            return Message.clientError("Empty enemyId.");
        }
        FreeColGameObject obj = game.getFreeColGameObjectSafely(enemyId);
        if (!(obj instanceof Player)) {
            return Message.clientError("Not a player: " + enemyId);
        }
        enemy = (Player) obj;
        if (enemy == player) {
            return Message.clientError("Inciting against oneself!");
        }
        if (!enemy.isEuropean()) {
            return Message.clientError("Inciting against non-European!");
        }
        MoveType type = unit.getSimpleMoveType(settlement.getTile());
        if (type != MoveType.ENTER_INDIAN_SETTLEMENT_WITH_MISSIONARY) {
            return Message.clientError("Unable to enter "
                                       + settlement.getName()
                                       + ": " + type.whyIllegal());
        }

        
        InGameController igc = server.getInGameController();
        int gold = Integer.parseInt(goldString);
        int goldToPay;
        try {
            if (gold < 0) { 
                goldToPay = igc.getInciteAmount(player, enemy, settlementPlayer);
            } else if (igc.inciteIndianSettlement(indianSettlement, player,
                                                  enemy, gold)) {
                goldToPay = gold; 
            } else {
                goldToPay = 0;
            }
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }
        unit.setMovesLeft(0);

        
        
        
        
        Element reply = Message.createNewRootElement("update");
        Document doc = reply.getOwnerDocument();
        reply.setAttribute("gold", Integer.toString(goldToPay));
        reply.appendChild(unit.toXMLElementPartial(doc, "movesLeft"));
        if (gold > 0 && goldToPay > 0) {
            reply.appendChild(player.toXMLElementPartial(doc, "gold"));
        }
        return reply;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("unitId", unitId);
        result.setAttribute("direction", directionString);
        result.setAttribute("enemyId", enemyId);
        result.setAttribute("gold", goldString);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "incite";
    }
}
