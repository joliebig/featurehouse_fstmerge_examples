

package net.sf.freecol.common.networking;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.ModelMessage;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;



public class CashInTreasureTrainMessage extends Message {
    
    private String unitId;

    
    public CashInTreasureTrainMessage(Unit unit) {
        this.unitId = unit.getId();
    }

    
    public CashInTreasureTrainMessage(Game game, Element element) {
        this.unitId = element.getAttribute("unit");
    }

    
    public Element handle(FreeColServer server, Player player, Connection connection) {
        ServerPlayer serverPlayer = server.getPlayer(connection);
        Unit unit;
        try {
            unit = server.getUnitSafely(unitId, serverPlayer);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }
        if (!unit.canCarryTreasure()) {
            return Message.clientError("Can not cash in unit " + unitId
                                       + " as it can not carry treasure.");
        }
        if (!unit.canCashInTreasureTrain()) {
            return Message.clientError("Can not cash in unit " + unitId
                                       + " as it is not in a suitable location.");
        }

        
        ModelMessage m = serverPlayer.cashInTreasureTrain(unit);
        server.getInGameController().sendRemoveUnitToAll(unit, serverPlayer);

        
        
        
        Element reply = Message.createNewRootElement("multiple");
        Document doc = reply.getOwnerDocument();
        Element messages = doc.createElement("addMessages");
        reply.appendChild(messages);
        messages.appendChild(m.toXMLElement(player, doc));
        Element update = doc.createElement("update");
        reply.appendChild(update);
        update.appendChild(player.toXMLElementPartial(doc, "gold", "score"));
        Element remove = doc.createElement("remove");
        reply.appendChild(remove);
        unit.addToRemoveElement(remove);
        return reply;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("unit", unitId);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "cashInTreasureTrain";
    }
}
