

package net.sf.freecol.common.networking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.DiplomaticTrade;
import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TradeItem;
import net.sf.freecol.common.model.Turn;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;



public class DiplomacyMessage extends Message {
    
    static List<DiplomacyMessage> savedAgreements = null;
    static Turn agreementTurn = null;

    
    private String unitId;

    
    private String directionString;

    
    private DiplomaticTrade agreement;

    
    public static enum TradeStatus {
        PROPOSE_TRADE,
        ACCEPT_TRADE,
        REJECT_TRADE
    }
    
    private TradeStatus status;


    
    public DiplomacyMessage(Unit unit, Direction direction,
                            DiplomaticTrade agreement) {
        this.unitId = unit.getId();
        this.directionString = String.valueOf(direction);
        this.agreement = agreement;
        this.status = (agreement.isAccept()) ? TradeStatus.ACCEPT_TRADE
            : TradeStatus.PROPOSE_TRADE;
    }

    
    public DiplomacyMessage(Game game, Element element) {
        this.unitId = element.getAttribute("unit");
        this.directionString = element.getAttribute("direction");
        NodeList nodes = element.getChildNodes();
        this.agreement = (nodes.getLength() < 1) ? null
            : new DiplomaticTrade(game, (Element) nodes.item(0));
        this.status = TradeStatus.PROPOSE_TRADE;
        String statusString = element.getAttribute("status");
        if (statusString != null) {
            if (statusString.equals("accept")) {
                this.status = TradeStatus.ACCEPT_TRADE;
            } else if (statusString.equals("reject")) {
                this.status = TradeStatus.REJECT_TRADE;
            }
        }
    }

    
    public Unit getUnit() {
        try {
            return (Unit) agreement.getGame().getFreeColGameObject(unitId);
        } catch (Exception e) {
        }
        return null;
    }

    
    public Settlement getSettlement() {
        try {
            Game game = agreement.getGame();
            Unit unit = (Unit) game.getFreeColGameObject(unitId);
            Direction direction = Enum.valueOf(Direction.class, directionString);
            Tile tile = game.getMap().getNeighbourOrNull(direction, unit.getTile());
            return tile.getSettlement();
        } catch (Exception e) {
        }
        return null;
    }

    
    public String getOtherNationName(Player player) {
        if (agreement != null) {
            if (agreement.getRecipient() != null
                && agreement.getRecipient() != player) {
                return agreement.getRecipient().getNationAsString();
            }
            if (agreement.getSender() != null
                && agreement.getSender() != player) {
                return agreement.getSender().getNationAsString();
            }
        }
        return null;
    }

    
    public DiplomaticTrade getAgreement() {
        return agreement;
    }

    
    public void setAgreement(DiplomaticTrade agreement) {
        this.agreement = agreement;
    }

    
    public boolean isAccept() {
        return status == TradeStatus.ACCEPT_TRADE;
    }

    
    public void setAccept() {
        status = TradeStatus.ACCEPT_TRADE;
    }

    
    public boolean isReject() {
        return status == TradeStatus.REJECT_TRADE;
    }

    
    public void setReject() {
        status = TradeStatus.REJECT_TRADE;
    }

    private void flushAgreements(Turn turn) {
        if (savedAgreements == null || agreementTurn == null
            || !agreementTurn.equals(turn)) {
            savedAgreements = new ArrayList<DiplomacyMessage>();
        }
        agreementTurn = turn;
    }
    private void saveAgreement(DiplomacyMessage message, Turn turn) {
        flushAgreements(turn);
        savedAgreements.add(message);
    }
    private DiplomacyMessage loadAgreement(DiplomacyMessage message, Turn turn) {
        DiplomacyMessage result = null;

        flushAgreements(turn);
        for (DiplomacyMessage dm : savedAgreements) {
            if (dm.isSameTransaction(message)) {
                result = dm;
                break;
            }
        }
        if (result != null) savedAgreements.remove(result);
        return result;
    }

    
    private boolean isSameTransaction(DiplomacyMessage message) {
        return message != null
            && message.unitId.equals(unitId)
            && message.directionString.equals(directionString)
            && message.agreement != null
            && message.agreement.getGame() == agreement.getGame()
            && message.agreement.getSender() == agreement.getSender()
            && message.agreement.getRecipient() == agreement.getRecipient();
    }

    
    private boolean isValidAcceptance(DiplomacyMessage message) {
        return isSameTransaction(message)
            && this.status == TradeStatus.PROPOSE_TRADE
            && message.status == TradeStatus.ACCEPT_TRADE;
    }

    
    private boolean isValidCounterProposal(DiplomacyMessage message) {
        return isSameTransaction(message)
            && this.status == TradeStatus.PROPOSE_TRADE
            && message.status == TradeStatus.PROPOSE_TRADE;
    }

    
    private Element createUpdateFor(final Player player,
                                    final List<FreeColGameObject> objects) {
        Element update = Message.createNewRootElement("update");
        Document doc = update.getOwnerDocument();

        for (FreeColGameObject object : objects) {
            if (object == player) { 
                update.appendChild(player.toXMLElementPartial(doc, "gold", "score"));
            } else if (object instanceof Player) {
                continue; 
            } else if (object instanceof Colony) {
                
                
                Colony colony = (Colony) object;
                Tile colonyTile = colony.getTile();
                Map map = colony.getGame().getMap();

                update.appendChild(colonyTile.toXMLElement(player, doc));
                for (Tile tile : map.getSurroundingTiles(colonyTile, 1)) {
                    update.appendChild(tile.toXMLElement(player, doc));
                }
            } else {
                update.appendChild(object.toXMLElement(player, doc));
            }
        }
        
        
        
        return (update.getChildNodes().getLength() > 0) ? update : null;
    }

    
    private void sendUpdates(final ServerPlayer player,
                             final ServerPlayer enemy,
                             final List<FreeColGameObject> objects) {
        Element update;
        Unit unit = getUnit();

        if ((update = createUpdateFor(enemy, objects)) != null) {
            try {
                enemy.getConnection().send(update);
            } catch (IOException e) {
                logger.warning(e.getMessage());
            }
        }

        
        if (!objects.contains(unit)) objects.add(unit);

        if ((update = createUpdateFor(player, objects)) != null) {
            try {
                player.getConnection().send(update);
            } catch (IOException e) {
                logger.warning(e.getMessage());
            }
        }
    }

    
    public Element handle(FreeColServer server, Connection connection) {
        ServerPlayer serverPlayer = server.getPlayer(connection);
        Game game = serverPlayer.getGame();
        Unit unit;
        DiplomacyMessage response;

        try {
            unit = server.getUnitSafely(unitId, serverPlayer);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }
        if (unit.getTile() == null) {
            return Message.clientError("Unit is not on the map: " + unitId);
        }
        Direction direction = Enum.valueOf(Direction.class, directionString);
        Tile newTile = game.getMap().getNeighbourOrNull(direction, unit.getTile());
        if (newTile == null) {
            return Message.clientError("Could not find tile"
                                       + " in direction: " + direction
                                       + " from unit: " + unitId);
        }
        Settlement settlement = newTile.getSettlement();
        if (settlement == null || !(settlement instanceof Colony)) {
            return Message.clientError("There is no colony at: "
                                       + newTile.getId());
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
        if(enemyPlayer == serverPlayer.getREFPlayer()){
            return Message.clientError("Player " + serverPlayer.getId()
                    + " tried to negotiate with his REF");
        }
        
        Connection enemyConnection = enemyPlayer.getConnection();
        if (enemyConnection == null) {
            return Message.createError("server.communicate",
                                       "Unable to communicate with the enemy.");
        }

        
        switch (status) {
        case ACCEPT_TRADE:
            response = loadAgreement(this, game.getTurn());
            if (response != null && response.isValidAcceptance(this)) {
                try {
                    enemyConnection.sendAndWait(this.toXMLElement());
                } catch (IOException e) {
                    logger.warning(e.getMessage());
                }
                
                
                sendUpdates(serverPlayer, enemyPlayer,
                            response.getAgreement().makeTrade());
                return null;
            }
            logger.warning("Accept of bogus trade.");
            this.setReject();
            
        case REJECT_TRADE:
            response = loadAgreement(this, game.getTurn());
            try {
                enemyConnection.sendAndWait(this.toXMLElement());
            } catch (IOException e) {
                logger.warning(e.getMessage());
            }
            return null;
        case PROPOSE_TRADE: 
            break;
        default:
            return Message.clientError("Invalid diplomacy status.");
        }

        
        
        if (!serverPlayer.hasContacted(settlementPlayer)) {
            serverPlayer.setContacted(settlementPlayer, true);
            settlementPlayer.setContacted(serverPlayer, true);
        }
        unit.setMovesLeft(0);
        TradeStatus state = TradeStatus.PROPOSE_TRADE;
        try {
            Element reply = enemyConnection.ask(this.toXMLElement());
            if (reply == null) {
                response = this;
                state = TradeStatus.REJECT_TRADE;
            } else {
                response = new DiplomacyMessage(game, reply);
                state = response.status;
            }
        } catch (IOException e) {
            logger.warning(e.getMessage());
            response = this;
            state = TradeStatus.REJECT_TRADE;
        }

        switch (state) {
        case PROPOSE_TRADE:
            if (this.isValidCounterProposal(response)) {
                saveAgreement(response, game.getTurn());
            } else {
                logger.warning("Confused diplomatic counterproposal.");
                response.setReject();
            }
            break;
        case ACCEPT_TRADE:
            if (this.isValidAcceptance(response)) {
                
                sendUpdates(serverPlayer, enemyPlayer,
                            this.agreement.makeTrade());
                return response.toXMLElement();
            }
            logger.warning("Confused diplomatic acceptance.");
            response.setReject();
            break;
        case REJECT_TRADE:
            response.setReject();
            break;
        default:
            logger.warning("Confused diplomatic status");
            response.setReject();
            break;
        }

        sendUpdates(serverPlayer, enemyPlayer, new ArrayList<FreeColGameObject>());
        return response.toXMLElement();
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("unit", unitId);
        result.setAttribute("direction", directionString);
        switch (status) {
        case PROPOSE_TRADE:result.setAttribute("status", ""); break;
        case ACCEPT_TRADE: result.setAttribute("status", "accept"); break;
        case REJECT_TRADE: result.setAttribute("status", "reject"); break;
        }
        result.appendChild(agreement.toXMLElement(null, result.getOwnerDocument()));
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "diplomacy";
    }
}
