

package net.sf.freecol.common.networking;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.control.InGameController;
import net.sf.freecol.server.model.ServerPlayer;



public class ClearSpecialityMessage extends Message {

    
    private String unitId;

    
    public ClearSpecialityMessage(Unit unit) {
        this.unitId = unit.getId();
    }

    
    public ClearSpecialityMessage(Game game, Element element) {
        this.unitId = element.getAttribute("unit");
    }

    
    public Element handle(FreeColServer server, Player player,
                          Connection connection) {
        ServerPlayer serverPlayer = server.getPlayer(connection);

        Unit unit;
        try {
            unit = server.getUnitSafely(unitId, serverPlayer);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }

        
        InGameController igc = server.getInGameController();
        try {
            igc.clearSpeciality(unit, serverPlayer);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }

        
        Element reply = Message.createNewRootElement("update");
        Document doc = reply.getOwnerDocument();
        reply.appendChild(unit.toXMLElement(player, doc));
        return reply;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("unit", unitId);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "clearSpeciality";
    }
}
