

package net.sf.freecol.common.networking;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;



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

        Unit carrier;
        try {
            carrier = server.getUnitSafely(carrierId, serverPlayer);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }

        
        goods.adjustAmount();
        try {
            server.getInGameController().moveGoods(goods, carrier);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }
        if (carrier.getInitialMovesLeft() != carrier.getMovesLeft()) {
            carrier.setMovesLeft(0);
        }

        
        
        Element reply = Message.createNewRootElement("update");
        Document doc = reply.getOwnerDocument();
        Location loc = carrier.getLocation();
        if (loc instanceof Europe) {
            reply.appendChild(((Europe) loc).toXMLElement(player, doc));
        } else if (loc instanceof Tile) {
            reply.appendChild(((Tile) loc).toXMLElement(player, doc));
        } else { 
            throw new IllegalStateException("Carrier not in Europe or Tile.");
        }
        return reply;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        Document doc = result.getOwnerDocument();
        result.setAttribute("carrier", carrierId);
        result.appendChild(goods.toXMLElement(null, doc));
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "loadCargo";
    }
}
