

package net.sf.freecol.common.networking;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Element;



public class NewLandNameMessage extends Message {

    
    private String newLandName;

    
    private String welcomerId;

    
    private String acceptString;

    
    public NewLandNameMessage(String newLandName, Player welcomer,
                              boolean accept) {
        this.newLandName = newLandName;
        this.welcomerId = (welcomer == null) ? null : welcomer.getId();
        this.acceptString = Boolean.toString(accept);
    }

    
    public NewLandNameMessage(Game game, Element element) {
        this.newLandName = element.getAttribute("newLandName");
        this.welcomerId = (element.hasAttribute("welcomer"))
            ? element.getAttribute("welcomer") : null;
        this.acceptString = element.getAttribute("accept");
    }

    
    public Element handle(FreeColServer server, Player player,
                          Connection connection) {
        Game game = server.getGame();
        ServerPlayer serverPlayer = server.getPlayer(connection);

        if (newLandName == null || newLandName.length() == 0) {
            return Message.clientError("Empty new land name");
        }
        ServerPlayer welcomer = null;
        boolean accept = false;
        if (welcomerId != null) {
            if (game.getFreeColGameObjectSafely(welcomerId) instanceof ServerPlayer) {
                welcomer = (ServerPlayer) game.getFreeColGameObjectSafely(welcomerId);
                accept = Boolean.valueOf(acceptString);
            } else {
                return Message.clientError("Not a player: " + welcomerId);
            }
        }

        
        return server.getInGameController()
            .setNewLandName(serverPlayer, newLandName, welcomer, accept);
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("newLandName", newLandName);
        if (welcomerId != null) result.setAttribute("welcomer", welcomerId);
        result.setAttribute("accept", acceptString);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "newLandName";
    }
}
