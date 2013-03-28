

package net.sf.freecol.common.networking;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.freecol.common.model.FreeColObject;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.ModelMessage;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Player.PlayerType;
import net.sf.freecol.common.model.Player.Stance;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;



public class DeclareIndependenceMessage extends Message {

    
    private String nationName;

    
    private String countryName;

    
    public DeclareIndependenceMessage(String nationName, String countryName) {
        this.nationName = nationName;
        this.countryName = countryName;
    }

    
    public DeclareIndependenceMessage(Game game, Element element) {
        this.nationName = element.getAttribute("nationName");
        this.countryName = element.getAttribute("countryName");
    }

    
    public Element handle(FreeColServer server, Player player, Connection connection) {
        if (nationName == null || nationName.length() == 0
            || countryName == null || countryName.length() == 0) {
            return Message.clientError("Empty nation or country name.");
        }
        if (player.getSoL() < 50) {
            return Message.clientError("Cannot declare independence with SoL < 50: " + player.getSoL());
        }
        if (player.getPlayerType() != PlayerType.COLONIAL) {
            return Message.clientError("Only colonial players can declare independence.");
        }

        
        ServerPlayer serverPlayer = server.getPlayer(connection);
        ServerPlayer refPlayer = server.getInGameController().createREFPlayer(serverPlayer);

        
        List<FreeColObject> changes = serverPlayer.declareIndependence(nationName, countryName);

        
        Element reply = Message.createNewRootElement("update");
        Document doc = reply.getOwnerDocument();
        reply.appendChild(player.toXMLElementPartial(doc, "playerType", "independentNationName", "newLandName"));
        server.getServer().sendToAll(reply, connection);

        
        
        serverPlayer.changeRelationWithPlayer(refPlayer, Stance.WAR);

        
        reply = Message.createNewRootElement("multiple");
        doc = reply.getOwnerDocument();
        Element update = doc.createElement("update");
        Element remove = doc.createElement("remove");
        reply.appendChild(update);
        Element messages = doc.createElement("addMessages");
        for (FreeColObject obj : changes) {
            if (obj instanceof ModelMessage) {
                obj.addToOwnedElement(messages, player);
            } else if (obj instanceof Unit && ((Unit) obj).isDisposed()) {
                ((Unit) obj).addToRemoveElement(remove);
            } else {
                update.appendChild(obj.toXMLElement(player, doc));
            }
        }
        if (remove.hasChildNodes()) {
            reply.appendChild(remove);
        }
        if (messages.hasChildNodes()) {
            reply.appendChild(messages);
        }
        return reply;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("nationName", nationName);
        result.setAttribute("countryName", countryName);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "declareIndependence";
    }
}
