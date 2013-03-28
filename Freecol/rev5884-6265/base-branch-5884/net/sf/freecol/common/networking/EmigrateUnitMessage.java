

package net.sf.freecol.common.networking;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.ModelMessage;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.control.InGameController;
import net.sf.freecol.server.model.ServerPlayer;



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
        boolean fountain;

        if (europe == null) {
            return Message.clientError("No Europe for a unit to migrate from.");
        }
        int remaining = serverPlayer.getRemainingEmigrants();
        if (remaining > 0) {
            
            
            fountain = true;
            serverPlayer.setRemainingEmigrants(remaining - 1);
        } else if (player.checkEmigrate()) {
            fountain = false;
        } else {
            return Message.clientError("No emigrants available.");
        }

        InGameController controller = (InGameController) server.getController();
        ModelMessage m = controller.emigrate(serverPlayer, slot, fountain);

        Element reply = Message.createNewRootElement("multiple");
        Document doc = reply.getOwnerDocument();
        Element update = doc.createElement("update");
        reply.appendChild(update);
        update.appendChild(europe.toXMLElement(player, doc));
        if (!fountain) {
            update.appendChild(player.toXMLElementPartial(doc, "immigration",
                                                          "immigrationRequired"));
        }
        if (m != null) {
            Element messages = doc.createElement("addMessages");
            reply.appendChild(messages);
            messages.appendChild(m.toXMLElement(player, doc));
        }
        return reply;
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
