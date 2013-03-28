

package net.sf.freecol.common.networking;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Element;



public class UnloadCargoMessage extends Message {

    
    private Goods goods;

    
    public UnloadCargoMessage(Goods goods) {
        this.goods = goods;
    }

    
    public UnloadCargoMessage(Game game, Element element) {
        this.goods = new Goods(game, (Element) element.getChildNodes().item(0));
    }

    
    public Element handle(FreeColServer server, Player player,
                          Connection connection) {
        ServerPlayer serverPlayer = server.getPlayer(connection);
        Game game = server.getGame();

        
        Location loc = goods.getLocation();
        if (loc == null) {
            return Message.clientError("Goods in a null location.");
        } else if (!(loc instanceof Unit)) {
            return Message.clientError("Unload from non-unit.");
        }
        Unit unit = (Unit) loc;
        if (unit.getOwner() != player) {
            return Message.clientError("Unload from non-owned unit.");
        }

        
        return server.getInGameController()
            .unloadCargo(serverPlayer, unit, goods);
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.appendChild(goods.toXMLElement(null, result.getOwnerDocument()));
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "unloadCargo";
    }
}
