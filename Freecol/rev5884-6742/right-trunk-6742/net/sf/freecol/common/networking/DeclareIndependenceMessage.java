

package net.sf.freecol.common.networking;

import net.sf.freecol.common.model.Event;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Limit;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Player.PlayerType;
import net.sf.freecol.common.model.Specification;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Element;



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
        ServerPlayer serverPlayer = server.getPlayer(connection);

        if (nationName == null || nationName.length() == 0
            || countryName == null || countryName.length() == 0) {
            return Message.clientError("Empty nation or country name.");
        }
        Event event = Specification.getSpecification().getEvent("model.event.declareIndependence");
        for (Limit limit : event.getLimits()) {
            if (!limit.evaluate(player)) {
                return Message.clientError(limit.getDescriptionKey() + " "
                                           + Integer.toString(limit.getRightHandSide().getValue()));
            }
        }
        if (player.getPlayerType() != PlayerType.COLONIAL) {
            return Message.clientError("Only colonial players can declare independence.");
        }

        
        return server.getInGameController().
            declareIndependence(serverPlayer, nationName, countryName);
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
