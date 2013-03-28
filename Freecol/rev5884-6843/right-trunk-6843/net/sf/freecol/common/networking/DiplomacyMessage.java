

package net.sf.freecol.common.networking;

import java.util.ArrayList;
import java.util.List;

import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.DiplomaticTrade;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.MoveType;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



public class DiplomacyMessage extends Message {

    
    private String unitId;

    
    private String settlementId;

    
    private DiplomaticTrade agreement;


    
    public DiplomacyMessage(Unit unit, Settlement settlement,
                            DiplomaticTrade agreement) {
        this.unitId = unit.getId();
        this.settlementId = settlement.getId();
        this.agreement = agreement;
    }

    
    public DiplomacyMessage(Game game, Element element) {
        this.unitId = element.getAttribute("unit");
        this.settlementId = element.getAttribute("settlement");
        NodeList nodes = element.getChildNodes();
        this.agreement = (nodes.getLength() < 1) ? null
            : new DiplomaticTrade(game, (Element) nodes.item(0));
    }

    
    public Unit getUnit(Game game) {
        return (game.getFreeColGameObject(unitId) instanceof Unit)
            ? (Unit) game.getFreeColGameObject(unitId)
            : null;
    }

    
    public Settlement getSettlement(Game game) {
        return (game.getFreeColGameObject(settlementId) instanceof Settlement)
            ? (Settlement) game.getFreeColGameObject(settlementId)
            : null;
    }

    
    public DiplomaticTrade getAgreement() {
        return agreement;
    }

    
    public void setAgreement(DiplomaticTrade agreement) {
        this.agreement = agreement;
    }

    
    public Element handle(FreeColServer server, Connection connection) {
        ServerPlayer serverPlayer = server.getPlayer(connection);

        Unit unit;
        try {
            unit = server.getUnitSafely(unitId, serverPlayer);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }
        if (unit.getTile() == null) {
            return Message.clientError("Unit is not on the map: " + unitId);
        }
        Settlement settlement;
        try {
            settlement = server.getAdjacentSettlementSafely(settlementId, unit);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }
        if (!(settlement instanceof Colony)) {
            return Message.clientError("Settlement is not a colony: "
                                       + settlementId);
        }
        MoveType type = unit.getMoveType(settlement.getTile());
        if (type != MoveType.ENTER_FOREIGN_COLONY_WITH_SCOUT) {
            return Message.clientError("Unable to enter "
                                       + settlement.getName()
                                       + ": " + type.whyIllegal());
        }
        if (agreement == null) {
            return Message.clientError("DiplomaticTrade with null agreement.");
        }
        if (agreement.getSender() != serverPlayer) {
            return Message.clientError("DiplomaticTrade received from player who is not the sender: " + serverPlayer.getId());
        }
        ServerPlayer enemyPlayer = (ServerPlayer) agreement.getRecipient();
        if (enemyPlayer == null) {
            return Message.clientError("DiplomaticTrade recipient is null");
        }
        if (enemyPlayer == serverPlayer) {
            return Message.clientError("DiplomaticTrade recipient matches sender: "
                                       + serverPlayer.getId());
        }
        Player settlementPlayer = settlement.getOwner();
        if (settlementPlayer != (Player) enemyPlayer) {
            return Message.clientError("DiplomaticTrade recipient: " + enemyPlayer.getId()
                                       + " does not match Settlement owner: " + settlementPlayer);
        }
        if (enemyPlayer == serverPlayer.getREFPlayer()) {
            return Message.clientError("Player can not negotiate with the REF: "
                                       + serverPlayer.getId());
        }

        
        return server.getInGameController()
            .diplomaticTrade(serverPlayer, unit, settlement, agreement);
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("unit", unitId);
        result.setAttribute("settlement", settlementId);
        result.appendChild(agreement.toXMLElement(null,
                result.getOwnerDocument()));
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "diplomacy";
    }
}
