

package net.sf.freecol.common.networking;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.HistoryEvent;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Region;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.control.InGameController;
import net.sf.freecol.server.model.ServerPlayer;



public class NewRegionNameMessage extends Message {
    
    private String newRegionName;

    
    private String unitId;

    
    public NewRegionNameMessage(String newRegionName, Unit unit) {
        this.newRegionName = newRegionName;
        this.unitId = unit.getId();
    }

    
    public NewRegionNameMessage(Game game, Element element) {
        this.newRegionName = element.getAttribute("newRegionName");
        this.unitId = element.getAttribute("unit");
    }

    
    public Element handle(FreeColServer server, Player player,
                          Connection connection) {
        ServerPlayer serverPlayer = server.getPlayer(connection);
        Game game = server.getGame();
        Unit unit;
        try {
            unit = server.getUnitSafely(unitId, serverPlayer);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }
        Tile tile = unit.getTile();
        if (tile == null) {
            return Message.clientError("Unit is not on the map: " + unitId);
        }
        Region region = tile.getDiscoverableRegion();
        if (region == null) {
            return Message.clientError("No discoverable region for: " + unitId);
        }
        if (region.isPacific()) {
            return Message.clientError("Can not rename the Pacific!");
        }

        
        InGameController controller = (InGameController) server.getController();
        HistoryEvent h = region.discover(serverPlayer,
                                         serverPlayer.getGame().getTurn(),
                                         newRegionName);
        controller.sendUpdateToAll(region, serverPlayer);

        
        Element reply = Message.createNewRootElement("multiple");
        Document doc = reply.getOwnerDocument();
        Element update = doc.createElement("update");
        reply.appendChild(update);
        update.appendChild(region.toXMLElement(player, doc));
        Element history = doc.createElement("addHistory");
        reply.appendChild(history);
        history.appendChild(h.toXMLElement(player, doc));
        return reply;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("newRegionName", newRegionName);
        result.setAttribute("unit", unitId);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "newRegionName";
    }
}
