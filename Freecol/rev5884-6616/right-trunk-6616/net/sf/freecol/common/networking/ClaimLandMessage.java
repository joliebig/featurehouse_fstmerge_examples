

package net.sf.freecol.common.networking;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.FreeColObject;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;



public class ClaimLandMessage extends Message {
	public static int STEAL_LAND = -1;
    
    private String tileId;

    
    private String settlementId;

    
    private int price;


    
    public ClaimLandMessage(Tile tile, Settlement settlement, int price) {
        this.tileId = tile.getId();
        this.settlementId = (settlement == null) ? null : settlement.getId();
        this.price = price;
    }

    
    public ClaimLandMessage(Game game, Element element) {
        this.tileId = element.getAttribute("tile");
        this.settlementId = (element.hasAttribute("settlement"))
            ? element.getAttribute("settlement")
            : null;
        this.price = Integer.parseInt(element.getAttribute("price"));
    }

    
    public Element handle(FreeColServer server, Player player, Connection connection) {
        ServerPlayer serverPlayer = server.getPlayer(connection);
        Game game = server.getGame();
        Tile tile;
        if (game.getFreeColGameObjectSafely(tileId) instanceof Tile) {
            tile = (Tile) game.getFreeColGameObjectSafely(tileId);
        } else {
            return Message.clientError("Invalid tileId");
        }
        Settlement settlement;
        if (settlementId == null) {
            settlement = null;
        } else if (game.getFreeColGameObjectSafely(settlementId) instanceof Settlement) {
            settlement = (Settlement) game.getFreeColGameObjectSafely(settlementId);
        } else {
            return Message.clientError("Invalid settlementId");
        }

        
        int value = player.getLandPrice(tile);
        Player owner = tile.getOwner();
        Settlement ownerSettlement = tile.getOwningSettlement();
        if (owner == null) { 
            price = 0;
        } else if (owner == player) { 
            if (settlement != null
                && ownerSettlement != null
                && ownerSettlement instanceof Colony
                && ((Colony) ownerSettlement).getColonyTile(tile).getUnit() != null) {
                return Message.createError("tileTakenSelf", null);
            }
            price = 0;
        } else if (owner.isEuropean()) {
            if (tile.getOwningSettlement() == null  
                || tile.getOwningSettlement() == settlement) { 
                price = 0;
            } else { 
                return Message.createError("tileTakenEuro", null);
            }
        } else { 
            if (price < 0 || price >= value) { 
                ;
            } else { 
                return Message.createError("tileTakenInd", null);
            }
        }

        
        serverPlayer.claimLand(tile, settlement, price);
        server.getInGameController().sendUpdateToAll(serverPlayer, (FreeColObject) tile);

        
        
        Element reply = Message.createNewRootElement("update");
        Document doc = reply.getOwnerDocument();
        reply.appendChild(tile.toXMLElement(player, doc));
        if (price < 0) {
            if (ownerSettlement != null) {
                reply.appendChild(ownerSettlement.toXMLElement(player, doc, false, false));
                
                reply.appendChild(ownerSettlement.getOwner().toXMLElement(player, doc, false, false));
            }
            else{
            	logger.warning("Stealing land from a non-existing settlement");
            }
        } else if (price > 0) {
            reply.appendChild(player.toXMLElementPartial(doc, "gold"));
        }
        return reply;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("tile", tileId);
        if (settlementId != null) {
            result.setAttribute("settlement", settlementId);
        }
        result.setAttribute("price", Integer.toString(price));
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "claimLand";
    }
}
