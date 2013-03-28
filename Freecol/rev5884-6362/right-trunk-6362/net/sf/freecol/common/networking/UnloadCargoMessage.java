

package net.sf.freecol.common.networking;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;



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
        Unit carrier = (Unit) loc;
        if (carrier.getOwner() != player) {
            return Message.clientError("Unload from non-owned unit.");
        } else if (carrier.getTile() == null) {
            return Message.clientError("Unload from unit not on the map.");
        }

        
        Tile tile = carrier.getTile();
        Colony colony = (tile.getSettlement() instanceof Colony)
            ? (Colony) tile.getSettlement()
            : null;
        goods.adjustAmount();
        try {
            server.getInGameController().moveGoods(goods, colony);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }
        if (carrier.getInitialMovesLeft() != carrier.getMovesLeft()) {
            carrier.setMovesLeft(0);
        }

        
        
        Element reply = Message.createNewRootElement("update");
        Document doc = reply.getOwnerDocument();
        reply.appendChild(tile.toXMLElement(player, doc));
        return reply;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        Document doc = result.getOwnerDocument();
        result.appendChild(goods.toXMLElement(null, doc));
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "unloadCargo";
    }
}
