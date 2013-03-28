

package net.sf.freecol.common.networking;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;



public class DebugForeignColonyMessage extends Message {

    Tile tile;

    
    Element tileElement;

    
    public DebugForeignColonyMessage(Tile tile) {
        this.tile = tile;
        this.tileElement = null;
    }

    
    public DebugForeignColonyMessage(Game game, Element element) {
        this.tile = (Tile) game.getFreeColGameObject(element.getAttribute("tile"));
        this.tileElement = (element.getChildNodes().getLength() != 1) ? null
            : (Element) element.getChildNodes().item(0);
    }

    public Element getTileElement() {
        return tileElement;
    }


    
    public Element handle(FreeColServer server, Connection connection) {
        ServerPlayer serverPlayer = server.getPlayer(connection);
        if (!FreeCol.isInDebugMode()) {
            return Message.clientError("Not in Debug Mode!");
        } else if (tile == null) {
            return Message.clientError("Could not find tile");
        }
        Settlement settlement = tile.getSettlement();
        if (settlement == null) {
            return Message.clientError("There is no settlement at: " + tile.getId());
        }

        
        
        
        
        
        
        Element reply = createNewRootElement("update");
        Document doc = reply.getOwnerDocument();
        reply.appendChild(tile.toXMLElement(serverPlayer, doc, true, false));
        reply.appendChild(tile.toXMLElement(serverPlayer, doc, false, false));
        return reply;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("tile", tile.getId());
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "debugForeignColony";
    }
}
