

package net.sf.freecol.common.networking;

import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Element;



public class AbandonColonyMessage extends Message {

    
    String colonyId;


    
    public AbandonColonyMessage(Colony colony) {
        this.colonyId = colony.getId();
    }

    
    public AbandonColonyMessage(Game game, Element element) {
        this.colonyId = element.getAttribute("colony");
    }

    
    public Element handle(FreeColServer server, Player player,
                          Connection connection) {
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

        
        
        return server.getInGameController()
            .abandonSettlement(serverPlayer, colony);
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
