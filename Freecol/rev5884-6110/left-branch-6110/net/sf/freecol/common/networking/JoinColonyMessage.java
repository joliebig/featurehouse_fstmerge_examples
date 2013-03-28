

package net.sf.freecol.common.networking;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.HistoryEvent;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Map.CircleIterator;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;



public class JoinColonyMessage extends Message {

    
    String colonyId;

    
    String builderId;


    
    public JoinColonyMessage(Colony colony, Unit builder) {
        this.colonyId = colony.getId();
        this.builderId = builder.getId();
    }

    
    public JoinColonyMessage(Game game, Element element) {
        this.colonyId = element.getAttribute("colony");
        this.builderId = element.getAttribute("unit");
    }

    
    public Element handle(FreeColServer server, Player player, Connection connection) {
        Game game = player.getGame();
        ServerPlayer serverPlayer = server.getPlayer(connection);
        Unit unit;
        Colony colony;
        try {
            unit = server.getUnitSafely(builderId, serverPlayer);
            colony = (Colony) unit.getGame().getFreeColGameObject(colonyId);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }
        if (colony == null || unit.getOwner() != colony.getOwner()) {
            return Message.createError("server.buildColony.badUnit",
                                       "Unit " + builderId
                                       + " can not join colony " + colony.getName());
        }

        unit.joinColony(colony);
        
        Element reply = Message.createNewRootElement("multiple");
        Document doc = reply.getOwnerDocument();
        Element update = doc.createElement("update");
        Tile tile = unit.getTile();
        reply.appendChild(update);
        update.appendChild(tile.toXMLElement(player, doc));
        Map map = game.getMap();
        for (Tile t : map.getSurroundingTiles(tile, colony.getRadius())) {
            if (t.getOwningSettlement() == colony) {
                update.appendChild(t.toXMLElement(player, doc));
            }
        }
        return reply;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("colony", colonyId);
        result.setAttribute("unit", builderId);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "joinColony";
    }
}
