

package net.sf.freecol.common.networking;

import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Element;



public class EmigrateUnitMessage extends Message {
    
    private int slot;

    
    public EmigrateUnitMessage(int slot) {
        this.slot = slot;
    }

    
    public EmigrateUnitMessage(Game game, Element element) {
        this.slot = Integer.parseInt(element.getAttribute("slot"));
    }

    
    public Element handle(FreeColServer server, Player player, Connection connection) {
        ServerPlayer serverPlayer = server.getPlayer(connection);

        Europe europe = player.getEurope();
        if (europe == null) {
            return Message.clientError("No Europe for a unit to migrate from.");
        }
        boolean fountain;
        int remaining = serverPlayer.getRemainingEmigrants();
        if (remaining > 0) {
            
            
            fountain = true;
            serverPlayer.setRemainingEmigrants(remaining - 1);
        } else if (player.checkEmigrate()) {
            fountain = false;
        } else {
            return Message.clientError("No emigrants available.");
        }

        
        return server.getInGameController()
            .emigrate(serverPlayer, slot, fountain);
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("slot", Integer.toString(slot));
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "emigrateUnit";
    }
}
