

package net.sf.freecol.common.networking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.HistoryEvent;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;



public class AbandonColonyMessage extends Message {

    
    String colonyId;


    
    public AbandonColonyMessage(Colony colony) {
        this.colonyId = colony.getId();
    }

    
    public AbandonColonyMessage(Game game, Element element) {
        this.colonyId = element.getAttribute("colony");
    }

    
    public Element handle(FreeColServer server, Player player, Connection connection) {
        Game game = player.getGame();
        ServerPlayer serverPlayer = server.getPlayer(connection);

        Colony colony;
        if (game.getFreeColGameObject(colonyId) instanceof Colony) {
            colony = (Colony) game.getFreeColGameObject(colonyId);
        } else {
            return Message.clientError("Not a colony: " + colonyId);
        }
        if (player != colony.getOwner()) {
            return Message.clientError("Player does not own colony: " + colonyId);
        }
        if (colony.getUnitCount() != 0) {
            return Message.clientError("Attempt to abandon colony " + colonyId
                                       + " with non-zero unit count "
                                       + Integer.toString(colony.getUnitCount()));
        }

        
        String name = colony.getName();
        Tile tile = colony.getTile();
        int radius = colony.getRadius();

        
        colony.dispose();
        HistoryEvent h = new HistoryEvent(game.getTurn().getNumber(),
                                          HistoryEvent.Type.ABANDON_COLONY,
                                          "%colony%", name);
        player.getHistory().add(h);
        server.getInGameController().sendUpdatedTileToAll(tile, serverPlayer);
        

        
        
        Element reply = Message.createNewRootElement("multiple");
        Document doc = reply.getOwnerDocument();
        Element update = doc.createElement("update");
        reply.appendChild(update);
        update.appendChild(tile.toXMLElement(player, doc));
        Map map = game.getMap();
        for (Tile t : map.getSurroundingTiles(tile, radius)) {
            update.appendChild(t.toXMLElement(player, doc));
        }
        Element history = doc.createElement("addHistory");
        reply.appendChild(history);
        history.appendChild(h.toXMLElement(player, doc));
        return reply;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("colony", colonyId);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "abandonColony";
    }
}
