

package net.sf.freecol.common.networking;

import org.w3c.dom.Element;

import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Nameable;
import net.sf.freecol.common.model.Ownable;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;



public class RenameMessage extends Message {
    
    private String id;

    
    private String newName;

    
    public RenameMessage(FreeColGameObject object, String newName) {
        this.id = object.getId();
        this.newName = newName;
    }

    
    public RenameMessage(Game game, Element element) {
        this.id = element.getAttribute("nameable");
        this.newName = element.getAttribute("name");
    }

    
    public Element handle(FreeColServer server, Player player, Connection connection) {
        ServerPlayer serverPlayer = server.getPlayer(connection);
        Nameable object = (Nameable) player.getGame().getFreeColGameObject(id);

        if (object == null) {
            return Message.clientError("Tried to rename an object with id " + id
                                       + " which could not be found.");
        }
        if (!(object instanceof Ownable) || ((Ownable) object).getOwner() != serverPlayer) {
            return Message.clientError("Not the owner of nameable: " + id);
        }
        object.setName(newName);

        Element reply = Message.createNewRootElement("update");
        reply.appendChild(((FreeColGameObject) object).toXMLElement(player, reply.getOwnerDocument()));
        return reply;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("nameable", id);
        result.setAttribute("name", newName);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "rename";
    }
}
