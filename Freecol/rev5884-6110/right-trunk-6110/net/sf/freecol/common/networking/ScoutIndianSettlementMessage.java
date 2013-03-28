

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
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.control.InGameController;
import net.sf.freecol.server.model.ServerPlayer;



public class ScoutIndianSettlementMessage extends Message {
    
    private String unitId;

    
    private String directionString;

    
    public ScoutIndianSettlementMessage(Unit unit, Direction direction) {
        this.unitId = unit.getId();
        this.directionString = String.valueOf(direction);
    }

    
    public ScoutIndianSettlementMessage(Game game, Element element) {
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
        InGameController igc = server.getInGameController();
        String result;
        try {
            result = igc.scoutIndianSettlement(unit, indianSettlement);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }

        
        
        
        Element reply = Message.createNewRootElement("multiple");
        Document doc = reply.getOwnerDocument();
        reply.setAttribute("result", result);
        if (unit.isDisposed()) {
            Element remove = doc.createElement("remove");
            reply.appendChild(remove);
            unit.addToRemoveElement(remove);
        } else {
            Element update = doc.createElement("update");
            reply.appendChild(update);
            
            update.appendChild(indianSettlement.toXMLElement(player, doc));
            
            
            int radius = unit.getLineOfSight();
            for (Tile t : map.getSurroundingTiles(tile, radius)) {
                if (!player.canSee(t)) {
                    update.appendChild(t.toXMLElement(player, doc));
                }
            }
            if ("tales".equals(result) && radius <= IndianSettlement.TALES_RADIUS) {
                for (Tile t : map.getSurroundingTiles(tile, radius+1, IndianSettlement.TALES_RADIUS)) {
                    if ((t.isLand() || t.isCoast()) && !player.canSee(t)) {
                        update.appendChild(t.toXMLElement(player, doc));
                    }
                }
            }

            
            if ("beads".equals(result)) {
                update.appendChild(player.toXMLElementPartial(doc, "gold", "score"));
            }
            
            
            if ("expert".equals(result)) {
                update.appendChild(unit.toXMLElement(player, doc));
            } else {
                update.appendChild(unit.toXMLElementPartial(doc, "movesLeft"));
            }
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
        return "scoutIndianSettlement";
    }
}
