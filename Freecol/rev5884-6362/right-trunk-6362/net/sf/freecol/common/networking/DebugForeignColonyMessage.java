

package net.sf.freecol.common.networking;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;



public class DebugForeignColonyMessage extends Message {

    
    String tileId;

    
    public DebugForeignColonyMessage(Tile tile) {
        this.tileId = tile.getId();
    }

    
    public DebugForeignColonyMessage(Game game, Element element) {
        this.tileId = element.getAttribute("tile");
    }


    
    public Element handle(FreeColServer server, Connection connection) {
        ServerPlayer serverPlayer = server.getPlayer(connection);
        Game game = serverPlayer.getGame();

        if (!FreeCol.isInDebugMode()) {
            return Message.clientError("Not in Debug Mode!");
        }
        Tile tile;
        if (game.getFreeColGameObjectSafely(tileId) instanceof Tile) {
            tile = (Tile) game.getFreeColGameObjectSafely(tileId);
        } else {
            return Message.clientError("Invalid tileId");
        }
        if (tile.getColony() == null) {
            return Message.clientError("There is no colony at: " + tileId);
        }

        
        
        
        Element reply = createNewRootElement("update");
        Document doc = reply.getOwnerDocument();
        reply.appendChild(tile.toXMLElement(serverPlayer, doc, true, false));
        reply.appendChild(tile.toXMLElement(serverPlayer, doc, false, false));
        return reply;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("tile", tileId);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "debugForeignColony";
    }
}
