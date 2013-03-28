

package net.sf.freecol.common.networking;

import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Element;



public class ClaimLandMessage extends Message {

    public static int STEAL_LAND = -1;
    
    private String tileId;

    
    private String settlementId;

    
    private String priceString;


    
    public ClaimLandMessage(Tile tile, Settlement settlement, int price) {
        this.tileId = tile.getId();
        this.settlementId = (settlement == null) ? null : settlement.getId();
        this.priceString = Integer.toString(price);
    }

    
    public ClaimLandMessage(Game game, Element element) {
        this.tileId = element.getAttribute("tile");
        this.settlementId = (element.hasAttribute("settlement"))
            ? element.getAttribute("settlement")
            : null;
        this.priceString = element.getAttribute("price");
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
        int price;
        try {
            price = Integer.parseInt(priceString);
        } catch (NumberFormatException e) {
            return Message.clientError("Bad price: " + priceString);
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

        
        return server.getInGameController()
            .claimLand(serverPlayer, tile, settlement, price);
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("tile", tileId);
        if (settlementId != null) {
            result.setAttribute("settlement", settlementId);
        }
        result.setAttribute("price", priceString);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "claimLand";
    }
}
