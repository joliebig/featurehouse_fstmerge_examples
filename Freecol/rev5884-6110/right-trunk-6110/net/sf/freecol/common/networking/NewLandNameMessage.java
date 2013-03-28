

package net.sf.freecol.common.networking;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.HistoryEvent;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;



public class NewLandNameMessage extends Message {
    
    private String newLandName;

    
    public NewLandNameMessage(String newLandName) {
        this.newLandName = newLandName;
    }

    
    public NewLandNameMessage(Game game, Element element) {
        this.newLandName = element.getAttribute("newLandName");
    }

    
    public Element handle(FreeColServer server, Player player,
                          Connection connection) {
        HistoryEvent h = new HistoryEvent(player.getGame().getTurn().getNumber(),
                                          HistoryEvent.Type.DISCOVER_NEW_WORLD,
                                          "%name%", newLandName);
        player.getHistory().add(h);
        player.setNewLandName(newLandName);
        

        Element reply = Message.createNewRootElement("multiple");
        Document doc = reply.getOwnerDocument();
        Element update = doc.createElement("update");
        Element history = doc.createElement("addHistory");
        reply.appendChild(update);
        reply.appendChild(history);
        update.appendChild(player.toXMLElementPartial(doc, "newLandName"));
        history.appendChild(h.toXMLElement(player, doc));
        return reply;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("newLandName", newLandName);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "newLandName";
    }
}
