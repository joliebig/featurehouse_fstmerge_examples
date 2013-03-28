

package net.sf.freecol.common.networking;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.ModelMessage;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Player.PlayerType;
import net.sf.freecol.common.model.Player.Stance;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;



public class GiveIndependenceMessage extends Message {

    
    private String playerId;

    
    public GiveIndependenceMessage(Player player) {
        this.playerId = player.getId();
    }

    
    public GiveIndependenceMessage(Game game, Element element) {
        this.playerId = element.getAttribute("player");
    }

    
    public Element handle(FreeColServer server, Player player, Connection connection) {
        ServerPlayer serverPlayer = server.getPlayer(connection);
        Game game = serverPlayer.getGame();
        ServerPlayer independent;

        if (playerId == null || playerId.length() == 0) {
            return Message.clientError("Player ID must not be empty");
        } else if (game.getFreeColGameObjectSafely(playerId) instanceof Player) {
            independent = (ServerPlayer) game.getFreeColGameObjectSafely(playerId);
        } else {
            return Message.clientError("Not a player ID: " + playerId);
        }
        if (independent.getREFPlayer() != player) {
            return Message.clientError("Cannot give independence to a country we do not own.");
        }

        
        List<ModelMessage> indeps = independent.giveIndependence(serverPlayer);
        serverPlayer.changeRelationWithPlayer(independent, Stance.PEACE);

        
        Connection independentConnection = independent.getConnection();
        Element reply = Message.createNewRootElement("multiple");
        Document doc = reply.getOwnerDocument();
        Element update = doc.createElement("update");
        Element messages = doc.createElement("addMessages");
        reply.appendChild(update);
        reply.appendChild(messages);
        update.appendChild(independent.toXMLElement(doc));
        for (ModelMessage m : indeps) {
            messages.appendChild(m.toXMLElement(doc));
        }
        try {
            independentConnection.send(reply);
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
        
        reply = Message.createNewRootElement("update");
        doc = reply.getOwnerDocument();
        reply.appendChild(independent.toXMLElement(player, doc, false, false));
        server.getServer().sendToAll(reply, independentConnection);
        return null;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("player", playerId);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "giveIndependence";
    }
}
