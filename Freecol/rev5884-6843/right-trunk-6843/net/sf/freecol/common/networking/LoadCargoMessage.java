

package net.sf.freecol.common.networking;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Element;



public class LoadCargoMessage extends Message {
    
    private Goods goods;

    
    private String carrierId;

    
    public LoadCargoMessage(Goods goods, Unit carrier) {
        this.goods = goods;
        this.carrierId = carrier.getId();
    }

    
    public LoadCargoMessage(Game game, Element element) {
        this.carrierId = element.getAttribute("carrier");
        this.goods = new Goods(game, (Element) element.getChildNodes().item(0));
    }

    
    public Element handle(FreeColServer server, Player player,
                          Connection connection) {
        ServerPlayer serverPlayer = server.getPlayer(connection);
        Game game = server.getGame();

        Unit unit;
        try {
            unit = server.getUnitSafely(carrierId, serverPlayer);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }

        
        return server.getInGameController()
            .loadCargo(serverPlayer, unit, goods);
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("carrier", carrierId);
        result.appendChild(goods.toXMLElement(null, result.getOwnerDocument()));
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "loadCargo";
    }
}
