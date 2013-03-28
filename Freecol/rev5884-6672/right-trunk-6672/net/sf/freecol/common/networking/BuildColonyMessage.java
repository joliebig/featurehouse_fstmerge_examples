

package net.sf.freecol.common.networking;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Element;



public class BuildColonyMessage extends Message {

    
    String colonyName;

    
    String builderId;


    
    public BuildColonyMessage(String colonyName, Unit builder) {
        this.colonyName = colonyName;
        this.builderId = builder.getId();
    }

    
    public BuildColonyMessage(Game game, Element element) {
        this.colonyName = element.getAttribute("name");
        this.builderId = element.getAttribute("unit");
    }

    
    public Element handle(FreeColServer server, Player player,
                          Connection connection) {
        Game game = player.getGame();
        ServerPlayer serverPlayer = server.getPlayer(connection);

        Unit unit;
        try {
            unit = server.getUnitSafely(builderId, serverPlayer);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }
        if (colonyName == null || colonyName.length() == 0) {
            return Message.createError("server.buildColony.badName",
                                       "Empty colony name");
        } else if (player.getColony(colonyName) != null) {
            return Message.createError("server.buildColony.badName",
                                       "Non-unique colony name " + colonyName);
        } else if (!unit.canBuildColony()) {
            return Message.createError("server.buildColony.badUnit",
                                       "Unit " + builderId
                                       + " can not build colony " + colonyName);
        }
        Tile tile = unit.getTile();
        if (tile.getOwner() != null && tile.getOwner() != player) {
            return Message.createError("server.buildColony.tileHasOwner",
                                       "Tile " + tile
                                       + " belongs to someone else");
        }

        
        return server.getInGameController()
            .buildSettlement(serverPlayer, unit, colonyName);
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("name", colonyName);
        result.setAttribute("unit", builderId);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "buildColony";
    }
}
