

package net.sf.freecol.common.networking;

import org.w3c.dom.Element;

import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Nameable;
import net.sf.freecol.common.model.Ownable;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;



public class ChatMessage extends Message {
    
    private Player player;

    
    private String sender;

    
    private String message;

    
    private boolean privateChat;

    
    public ChatMessage(Player player, String message, boolean privateChat) {
        this.player = player;
        this.sender = player.getId();
        this.message = message;
        this.privateChat = privateChat;
    }

    
    public ChatMessage(Game game, Element element) {
        sender = element.getAttribute("sender");
        if (sender == null) {
            throw new IllegalStateException("sender is null");
        } else if (!(game.getFreeColGameObject(sender) instanceof Player)) {
            throw new IllegalStateException("not a player: " + sender);
        }
        player = (Player) game.getFreeColGameObject(sender);
        message = element.getAttribute("message");
        privateChat = Boolean.valueOf(element.getAttribute("privateChat")).booleanValue();
    }

    
    public Player getPlayer() {
        return player;
    }

    
    public String getMessage() {
        return message;
    }

    
    public boolean isPrivate() {
        return privateChat;
    }

    
    public Element handle(FreeColServer server, Connection connection) {
        
        player = server.getPlayer(connection);
        sender = player.getId();
        server.getServer().sendToAll(toXMLElement(), connection);
        return null;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("sender", sender);
        result.setAttribute("message", message);
        result.setAttribute("privateChat", String.valueOf(privateChat));
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "chat";
    }
}
