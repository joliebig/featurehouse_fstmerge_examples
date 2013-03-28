

package net.sf.freecol.server.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.PseudoRandom;
import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.model.AbstractUnit;
import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.CombatModel;
import net.sf.freecol.common.model.EquipmentType;
import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.FoundingFather;
import net.sf.freecol.common.model.FoundingFather.FoundingFatherType;
import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.FreeColObject;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.GameOptions;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.HistoryEvent;
import net.sf.freecol.common.model.IndianNationType;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.LostCityRumour;
import net.sf.freecol.common.model.LostCityRumour.RumourType;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Map.CircleIterator;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Map.Position;
import net.sf.freecol.common.model.Market;
import net.sf.freecol.common.model.ModelController;
import net.sf.freecol.common.model.ModelMessage;
import net.sf.freecol.common.model.Modifier;
import net.sf.freecol.common.model.Monarch;
import net.sf.freecol.common.model.Nameable;
import net.sf.freecol.common.model.Nation;
import net.sf.freecol.common.model.Ownable;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Player.PlayerType;
import net.sf.freecol.common.model.Player.Stance;
import net.sf.freecol.common.model.PlayerExploredTile;
import net.sf.freecol.common.model.Region;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.StringTemplate;
import net.sf.freecol.common.model.Tension;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TradeRoute.Stop;
import net.sf.freecol.common.model.Turn;
import net.sf.freecol.common.model.Monarch.MonarchAction;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.MoveType;
import net.sf.freecol.common.model.Unit.UnitState;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.model.UnitTypeChange;
import net.sf.freecol.common.model.UnitTypeChange.ChangeType;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.common.util.RandomChoice;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.ai.AIPlayer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public final class InGameController extends Controller {

    private static Logger logger = Logger.getLogger(InGameController.class.getName());

    public int debugOnlyAITurns = 0;

    private java.util.Map<String,java.util.Map<String, java.util.Map<String,Object>>> transactionSessions;
    
    
    public InGameController(FreeColServer freeColServer) {
        super(freeColServer);
        
        transactionSessions = new HashMap<String,java.util.Map<String, java.util.Map<String,Object>>>();
    }


    
    public List<ServerPlayer> getOtherPlayers(ServerPlayer serverPlayer) {
        List<ServerPlayer> result = new ArrayList<ServerPlayer>();
        for (Player otherPlayer : getGame().getPlayers()) {
            ServerPlayer enemyPlayer = (ServerPlayer) otherPlayer;
            if (!enemyPlayer.equals(serverPlayer)
                && enemyPlayer.isConnected()) {
                result.add(enemyPlayer);
            }
        }
        return result;
    }

    
    
    public static enum UpdateType {
        ANIMATE,    
        ATTRIBUTE,  
        PARTIAL,    
        PRIVATE,    
        REMOVE,     
        STANCE,     
        UPDATE      
    };

    
    private static void addAnimate(List<Object> objects, Unit unit,
                                   Location oldLocation, Tile newTile) {
        addMore(objects, UpdateType.ANIMATE, unit, oldLocation, newTile);
    }

    
    private static void addAttribute(List<Object> objects, String attr,
                                     String value) {
        addMore(objects, UpdateType.ATTRIBUTE, attr, value);
    }

    
    private static void addMore(List<Object> objects, Object... more) {
        for (Object o : more) objects.add(o);
    }

    
    private static void addPartial(List<Object> objects, FreeColObject fco,
                                   String... more) {
        addMore(objects, UpdateType.PARTIAL, fco);
        for (Object o : more) objects.add(o);
    }

    
    public static void addRemove(List<Object> objects,
                                 FreeColGameObject fcgo) {
        addMore(objects, UpdateType.REMOVE, fcgo);
    }

    
    private static void addStance(List<Object> objects, Stance stance,
                                  ServerPlayer player1, ServerPlayer player2) {
        addMore(objects, UpdateType.STANCE, stance, player1, player2);
    }

    
    public Element buildUpdate(ServerPlayer serverPlayer, Object... objects) {
        List<Object> objectList = new ArrayList<Object>();
        for (Object o : objects) objectList.add(o);
        return buildUpdate(serverPlayer, objectList);
    }

    
    public Element buildUpdate(ServerPlayer serverPlayer,
                               List<Object> objects) {
        Document doc = Message.createNewDocument();
        List<String> attributes = new ArrayList<String>();
        List<Element> extras = new ArrayList<Element>();
        Element multiple = doc.createElement("multiple");
        Element update = doc.createElement("update");
        Element messages = doc.createElement("addMessages");
        Element history = doc.createElement("addHistory");
        Element remove = doc.createElement("remove");

        for (int i = 0; i < objects.size(); i++) {
            Object o = objects.get(i);
            if (o == null) {
                continue;
            } else if (o instanceof UpdateType) {
                switch ((UpdateType) o) {
                case ANIMATE: 
                    if (i+3 < objects.size()
                        && objects.get(i+1) instanceof Unit
                        && objects.get(i+2) instanceof FreeColGameObject
                        && objects.get(i+3) instanceof Tile) {
                        Unit unit = (Unit) objects.get(i+1);
                        FreeColGameObject oldLocation = (FreeColGameObject) objects.get(i+2);
                        Tile newTile = (Tile) objects.get(i+3);
                        Element animate = buildAnimate(serverPlayer, doc, unit,
                                                       oldLocation, newTile);
                        if (animate != null) {
                            extras.add(animate);
                            
                            if (!unit.isVisibleTo(serverPlayer)) {
                                unit.addToRemoveElement(remove);
                            }
                        }
                        i += 3;
                    } else {
                        throw new IllegalArgumentException("bogus ANIMATE");
                    }
                    break;
                case ATTRIBUTE: 
                    if (i+2 < objects.size()
                        && objects.get(i+1) instanceof String
                        && objects.get(i+2) instanceof String) {
                        attributes.add((String) objects.get(i+1));
                        attributes.add((String) objects.get(i+2));
                        i += 2;
                    } else {
                        throw new IllegalArgumentException("bogus ATTRIBUTE");
                    }
                    break;
                case PARTIAL: 
                    if (i+2 < objects.size()
                        && objects.get(i+1) instanceof FreeColObject
                        && objects.get(i+2) instanceof String) {
                        FreeColObject fco = (FreeColObject) objects.get(i+1);
                        
                        int n;
                        for (n = i+3; n < objects.size()
                                 && objects.get(n) instanceof String; n++);
                        n -= i+2;
                        String[] fields = new String[n];
                        for (int j = 0; j < n; j++) {
                            fields[j] = (String) objects.get(i+2+j);
                        }
                        
                        update.appendChild(fco.toXMLElement(null, doc, true,
                                                            false, fields));
                        i += n+1;
                    } else {
                        throw new IllegalArgumentException("bogus PARTIAL");
                    }
                    break;
                case PRIVATE: 
                    break;
                case REMOVE: 
                    if (i+1 < objects.size()
                        && objects.get(i+1) instanceof FreeColGameObject) {
                        FreeColGameObject fcgo = (FreeColGameObject) objects.get(i+1);
                        fcgo.addToRemoveElement(remove);
                        i += 1;
                    } else {
                        throw new IllegalArgumentException("bogus REMOVE");
                    }
                    break;
                case STANCE: 
                    if (i+3 < objects.size()
                        && objects.get(i+1) instanceof Stance
                        && objects.get(i+2) instanceof ServerPlayer
                        && objects.get(i+3) instanceof ServerPlayer) {
                        Element setStance = buildStance(serverPlayer, doc,
                                (Stance) objects.get(i+1),
                                (ServerPlayer) objects.get(i+2),
                                (ServerPlayer) objects.get(i+3));
                        if (setStance != null) extras.add(setStance);
                        i += 3;
                    } else {
                        throw new IllegalArgumentException("bogus STANCE");
                    }
                    break;
                case UPDATE: 
                    break;
                }
            } else if (o instanceof ModelMessage) {
                
                ((ModelMessage) o).addToOwnedElement(messages, serverPlayer);
            } else if (o instanceof HistoryEvent) {
                
                ((HistoryEvent) o).addToOwnedElement(history, serverPlayer);
            } else if (o instanceof FreeColGameObject) {
                FreeColGameObject fcgo = (FreeColGameObject) o;
                if (fcgo.isDisposed()) {
                    
                    fcgo.addToRemoveElement(remove);
                } else if (fcgo instanceof Ownable
                           && ((Ownable) fcgo).getOwner() == (Player) serverPlayer) {
                    
                    update.appendChild(fcgo.toXMLElement(serverPlayer, doc));
                } else if (fcgo instanceof Unit) {
                    
                    Unit unit = (Unit) fcgo;
                    if (unit.isVisibleTo(serverPlayer)) {
                        update.appendChild(unit.toXMLElement(serverPlayer, doc));
                    }
                } else if (fcgo instanceof Settlement) {
                    
                    Tile tile = ((Settlement) fcgo).getTile();
                    if (serverPlayer.canSee(tile)) {
                        update.appendChild(fcgo.toXMLElement(serverPlayer, doc));
                    }
                } else if (fcgo instanceof Tile) {
                    
                    Tile tile = (Tile) fcgo;
                    if (serverPlayer.canSee(tile)) {
                        update.appendChild(tile.toXMLElement(serverPlayer, doc, false, false));
                    }
                } else if (fcgo instanceof Region) {
                    
                    update.appendChild(fcgo.toXMLElement(serverPlayer, doc));
                } else {
                    logger.warning("Attempt to update hidden object: "
                                   + fcgo.getId());
                }
            } else {
                throw new IllegalStateException("Bogus object: "
                                                + o.toString());
            }
        }

        
        
        
        int n = 0;
        Element child = null;
        Element result;
        while (extras.size() > 0) {
            child = extras.remove(0);
            multiple.appendChild(child);
            n++;
        }
        if (update.hasChildNodes()) {
            multiple.appendChild(update);
            child = update;
            n++;
        }
        if (messages.hasChildNodes()) {
            multiple.appendChild(messages);
            child = messages;
            n++;
        }
        if (history.hasChildNodes()) {
            multiple.appendChild(history);
            child = history;
            n++;
        }
        if (remove.hasChildNodes()) {
            multiple.appendChild(remove);
            child = remove;
            n++;
        }
        switch (n) {
        case 0:
            if (attributes.isEmpty()) return null;
            doc.appendChild(update);
            result = update;
            break;
        case 1:
            multiple.removeChild(child);
            doc.appendChild(child);
            result = child;
            break;
        default:
            doc.appendChild(multiple);
            result = multiple;
            break;
        }
        
        for (int i = 0; i < attributes.size(); i += 2) {
            result.setAttribute(attributes.get(i), attributes.get(i+1));
        }

        return result;
    }

    
    private Element buildAnimate(ServerPlayer serverPlayer, Document doc,
                                 Unit unit,
                                 FreeColGameObject oldLocation, Tile newTile) {
        Tile oldTile = ((Location) oldLocation).getTile();
        boolean seeOld = unit.getOwner() == serverPlayer
            || (serverPlayer.canSee(oldTile) && oldLocation instanceof Tile
                && oldTile.getSettlement() == null);
        boolean seeNew = unit.isVisibleTo(serverPlayer);
        if (seeOld || seeNew) {
            Element element = doc.createElement("animateMove");
            element.setAttribute("unit", unit.getId());
            element.setAttribute("oldTile", oldTile.getId());
            element.setAttribute("newTile", newTile.getId());
            if (!seeOld) {
                
                
                
                
                element.appendChild(unit.toXMLElement(serverPlayer, doc));
            }
            return element;
        }
        return null;
    }

    
    private Element buildStance(ServerPlayer serverPlayer, Document doc,
                                Stance stance,
                                ServerPlayer player1, ServerPlayer player2) {
        if (serverPlayer == player1 || serverPlayer == player2
            || stance == Stance.WAR) {
            Element element = doc.createElement("setStance");
            element.setAttribute("stance", stance.toString());
            element.setAttribute("first", player1.getId());
            element.setAttribute("second", player2.getId());
            return element;
        }
        return null;
    }

    
    public void sendUpdateToAll(ServerPlayer serverPlayer, Object... objects) {
        List<Object> objectList = new ArrayList<Object>();
        for (Object o : objects) objectList.add(o);
        sendToOthers(serverPlayer, objectList);
    }

    
    public void sendUpdateToAll(ServerPlayer serverPlayer, List<Object> objects) {
        sendToOthers(serverPlayer, objects);
    }

    
    public void sendToOthers(ServerPlayer serverPlayer, Object... objects) {
        List<Object> objectList = new ArrayList<Object>();
        for (Object o : objects) objectList.add(o);
        sendToOthers(serverPlayer, objectList);
    }

    
    public void sendToOthers(ServerPlayer serverPlayer,
                             List<Object> allObjects) {
        
        List<Object> objects = new ArrayList<Object>();
        for (Object o : allObjects) {
            if (o == UpdateType.PRIVATE) break;
            objects.add(o);
        }
        if (objects.isEmpty()) return;

        
        for (ServerPlayer other : getOtherPlayers(serverPlayer)) {
            sendElement(other, objects);
        }
    }

    
    public void sendToOthers(ServerPlayer serverPlayer, Element element) {
        if (element != null) {
            for (ServerPlayer other : getOtherPlayers(serverPlayer)) {
                sendElement(other, element);
            }
        }
    }

    
    public void sendToAll(Element element) {
        if (element != null) {
            for (ServerPlayer other : getOtherPlayers(null)) {
                sendElement(other, element);
            }
        }
    }

    
    public void sendElement(ServerPlayer serverPlayer, List<Object> objects) {
        sendElement(serverPlayer, buildUpdate(serverPlayer, objects));
    }

    
    public void sendElement(ServerPlayer player, Element element) {
        if (element != null) {
            try {
                player.getConnection().sendAndWait(element);
            } catch (Exception e) {
                logger.warning(e.getMessage());
            }
        }
    }


    
    public void endTurn(ServerPlayer player) {
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {}
        
        
        FreeColServer freeColServer = getFreeColServer();
        ServerPlayer oldPlayer = (ServerPlayer) getGame().getCurrentPlayer();
        
        if (oldPlayer != player) {
            throw new IllegalArgumentException("It is not "
                + player.getName() + "'s turn, it is "
                + ((oldPlayer == null) ? "noone" : oldPlayer.getName()) + "'s!");
        }
        
        player.clearModelMessages();
        freeColServer.getModelController().clearTaskRegister();

        Player winner = checkForWinner();
        if (winner != null && (!freeColServer.isSingleplayer() || !winner.isAI())) {
            Element gameEndedElement = Message.createNewRootElement("gameEnded");
            gameEndedElement.setAttribute("winner", winner.getId());
            sendToAll(gameEndedElement);
            
            
            if (FreeCol.getFreeColClient() == null) {
                new Timer(true).schedule(new TimerTask() {
                    @Override
                    public void run() {
                        System.exit(0);
                    }
                }, 20000);
            }
            return;
        }
        
        ServerPlayer newPlayer = (ServerPlayer) nextPlayer();
        
        if (newPlayer != null 
            && !newPlayer.isAI()
            && (!newPlayer.isConnected() || debugOnlyAITurns > 0)) {
            endTurn(newPlayer);
            return;
        }
    }

    
    public void yearlyGoodsRemoval(ServerPlayer player) {
        List<ModelMessage> messages = new ArrayList<ModelMessage>();
        List<GoodsType> goodsTypes = FreeCol.getSpecification().getGoodsTypeList();
        Market market = player.getMarket();

        
        GoodsType removeType;
        do {
            int randomGoods = getPseudoRandom().nextInt(goodsTypes.size());
            removeType = goodsTypes.get(randomGoods);
        } while (!removeType.isStorable());

        
        for (GoodsType type : goodsTypes) {
            if (type.isStorable() && market.hasBeenTraded(type)) {
                int amount = getGame().getTurn().getNumber() / 10;
                if (type == removeType && amount > 0) {
                    amount += getPseudoRandom().nextInt(2 * amount + 1);
                }
                if (amount > 0) {
                    market.addGoodsToMarket(type, -amount);
                }
            }
            if (market.hasPriceChanged(type)) {
                messages.add(market.makePriceChangeMessage(type));
                market.flushPriceChange(type);
            }
        }

        
        Element element = Message.createNewRootElement("multiple");
        Document doc = element.getOwnerDocument();
        Element update = doc.createElement("update");
        element.appendChild(update);
        update.appendChild(market.toXMLElement(player, doc));
        Element mess = doc.createElement("addMessages");
        for (ModelMessage m : messages) {
            m.addToOwnedElement(mess, player);
        }
        if (mess.hasChildNodes()) {
            element.appendChild(mess);
        }
        try {
            player.getConnection().send(element);
        } catch (Exception e) {
            logger.warning("Error sending yearly market update to "
                           + player.getName() + ": " + e.getMessage());
        }
    }

    
    private Element killPlayerElement(ServerPlayer serverPlayer) {
        Element element = Message.createNewRootElement("multiple");
        Document doc = element.getOwnerDocument();

        Element update = doc.createElement("update");
        element.appendChild(update);
        Player player = (Player) serverPlayer;
        player.setDead(true);
        update.appendChild(player.toXMLElementPartial(doc, "dead"));

        if (!serverPlayer.getUnits().isEmpty()) {
            Element remove = doc.createElement("remove");
            element.appendChild(remove);
            List<Unit> unitList = new ArrayList<Unit>(serverPlayer.getUnits());
            for (Unit unit : unitList) {
                serverPlayer.removeUnit(unit);
                unit.addToRemoveElement(remove);
                unit.dispose();
            }
        }

        Element messages = doc.createElement("addMessages");
        element.appendChild(messages);
        String messageId = serverPlayer.isEuropean() ? "model.diplomacy.dead.european"
            : "model.diplomacy.dead.native";
        ModelMessage m = new ModelMessage(ModelMessage.MessageType.FOREIGN_DIPLOMACY,
                                          messageId, serverPlayer)
            .addStringTemplate("%nation%", serverPlayer.getNationName());
        m.addToOwnedElement(messages, serverPlayer);

        Element setDeadElement = doc.createElement("setDead");
        element.appendChild(setDeadElement);
        setDeadElement.setAttribute("player", serverPlayer.getId());

        return element;
    }


    
    private Player nextPlayer() {
        final FreeColServer freeColServer = getFreeColServer();
        
        if (!isHumanPlayersLeft()) {
            getGame().setCurrentPlayer(null);
            return null;
        }
        
        if (getGame().isNextPlayerInNewTurn()) {
            getGame().newTurn();
            if (getGame().getTurn().getAge() > 1
                && !getGame().getSpanishSuccession()) {
                checkSpanishSuccession();
            }
            if (debugOnlyAITurns > 0) {
                debugOnlyAITurns--;
            }
            Element newTurnElement = Message.createNewRootElement("newTurn");
            sendToAll(newTurnElement);
        }
        
        ServerPlayer newPlayer = (ServerPlayer) getGame().getNextPlayer();
        getGame().setCurrentPlayer(newPlayer);
        if (newPlayer == null) {
            getGame().setCurrentPlayer(null);
            return null;
        }
        
        synchronized (newPlayer) {
            if (Player.checkForDeath(newPlayer)) {
                newPlayer.setDead(true);
                Element element = killPlayerElement(newPlayer);
                sendToAll(element);
                logger.info(newPlayer.getNation() + " is dead.");
                return nextPlayer();
            }
        }
        
        if (newPlayer.isEuropean()) {
            yearlyGoodsRemoval(newPlayer);

            if (newPlayer.getCurrentFather() == null && newPlayer.getSettlements().size() > 0) {
                chooseFoundingFather(newPlayer);
            }
            if (newPlayer.getMonarch() != null) {
                monarchAction(newPlayer);
            }
            bombardEnemyShips(newPlayer);
        }
        else if (newPlayer.isIndian()) {
            
            for (IndianSettlement indianSettlement: newPlayer.getIndianSettlements()) {
                if (indianSettlement.checkForNewMissionaryConvert()) {
                    
                    Unit missionary = indianSettlement.getMissionary();
                    ServerPlayer european = (ServerPlayer) missionary.getOwner();
                    
                    Tile settlementTile = indianSettlement.getTile();
                    Tile targetTile = null;
                    Iterator<Position> ffi = getGame().getMap().getFloodFillIterator(settlementTile.getPosition());
                    while (ffi.hasNext()) {
                        Tile t = getGame().getMap().getTile(ffi.next());
                        if (settlementTile.getDistanceTo(t) > IndianSettlement.MAX_CONVERT_DISTANCE) {
                            break;
                        }
                        if (t.getSettlement() != null && t.getSettlement().getOwner() == european) {
                            targetTile = t;
                            break;
                        }
                    }
        
                    if (targetTile != null) {
                        
                        List<UnitType> converts = FreeCol.getSpecification().getUnitTypesWithAbility("model.ability.convert");
                        if (converts.size() > 0) {
                            
                            Unit brave = indianSettlement.getUnitIterator().next();
                            String nationId = brave.getOwner().getNationID();
                            brave.dispose();
                            ModelController modelController = getGame().getModelController();
                            int random = modelController.getRandom(indianSettlement.getId() + "getNewConvertType", converts.size());
                            UnitType unitType = converts.get(random);
                            Unit unit = modelController.createUnit(indianSettlement.getId() + "newTurn100missionary", targetTile,
                                    european, unitType);
                            
                            try {
                                Element updateElement = Message.createNewRootElement("newConvert");
                                updateElement.setAttribute("nation", nationId);
                                updateElement.setAttribute("colonyTile", targetTile.getId());
                                updateElement.appendChild(unit.toXMLElement(european,updateElement.getOwnerDocument()));
                                european.getConnection().send(updateElement);
                                logger.info("New convert created for " + european.getName() + " with ID=" + unit.getId());
                            } catch (IOException e) {
                                logger.warning("Could not send message to: " + european.getName());
                            }
                        }
                    }
                }
            }
        }
        
        Element setCurrentPlayerElement = Message.createNewRootElement("setCurrentPlayer");
        setCurrentPlayerElement.setAttribute("player", newPlayer.getId());
        sendToAll(setCurrentPlayerElement);
        
        return newPlayer;
    }

    private void checkSpanishSuccession() {
        boolean rebelMajority = false;
        Player weakestAIPlayer = null;
        Player strongestAIPlayer = null;
        java.util.Map<Player, Element> documentMap = new HashMap<Player, Element>();
        for (Player player : getGame().getPlayers()) {
            documentMap.put(player, Message.createNewRootElement("spanishSuccession"));
            if (player.isEuropean()) {
                if (player.isAI() && !player.isREF()) {
                    if (weakestAIPlayer == null
                        || weakestAIPlayer.getScore() > player.getScore()) {
                        weakestAIPlayer = player;
                    }
                    if (strongestAIPlayer == null
                        || strongestAIPlayer.getScore() < player.getScore()) {
                        strongestAIPlayer = player;
                    }
                } else if (player.getSoL() > 50) {
                    rebelMajority = true;
                }
            }
        }

        if (rebelMajority
            && weakestAIPlayer != null
            && strongestAIPlayer != null
            && weakestAIPlayer != strongestAIPlayer) {
            documentMap.remove(weakestAIPlayer);
            for (Element element : documentMap.values()) {
                element.setAttribute("loser", weakestAIPlayer.getId());
                element.setAttribute("winner", strongestAIPlayer.getId());
            }
            for (Colony colony : weakestAIPlayer.getColonies()) {
                colony.changeOwner(strongestAIPlayer);
                for (Entry<Player, Element> entry : documentMap.entrySet()) {
                    if (entry.getKey().canSee(colony.getTile())) {
                        entry.getValue().appendChild(colony.toXMLElement(entry.getKey(),
                                                                         entry.getValue().getOwnerDocument()));
                    }
                }
            }
            for (Unit unit : weakestAIPlayer.getUnits()) {
                unit.setOwner(strongestAIPlayer);
                for (Entry<Player, Element> entry : documentMap.entrySet()) {
                    if (entry.getKey().canSee(unit.getTile())) {
                        entry.getValue().appendChild(unit.toXMLElement(entry.getKey(),
                                                                       entry.getValue().getOwnerDocument()));
                    }
                }
            }
            for (Entry<Player, Element> entry : documentMap.entrySet()) {
                try {
                    ((ServerPlayer) entry.getKey()).getConnection().send(entry.getValue());
                } catch (IOException e) {
                    logger.warning("Could not send message to: " + entry.getKey().getName());
                }
            }
            weakestAIPlayer.setDead(true);
            getGame().setSpanishSuccession(true);
        }
    }
    
    private boolean isHumanPlayersLeft() {
        for (Player player : getFreeColServer().getGame().getPlayers()) {
            if (!player.isDead() && !player.isAI() && ((ServerPlayer) player).isConnected()) {
                return true;
            }
        }
        return false;
    }

    private void chooseFoundingFather(ServerPlayer player) {
        final ServerPlayer nextPlayer = player;
        Thread t = new Thread(FreeCol.SERVER_THREAD+"FoundingFather-thread") {
                public void run() {
                    List<FoundingFather> randomFoundingFathers = getRandomFoundingFathers(nextPlayer);
                    boolean atLeastOneChoice = false;
                    Element chooseFoundingFatherElement = Message.createNewRootElement("chooseFoundingFather");
                    for (FoundingFather father : randomFoundingFathers) {
                        chooseFoundingFatherElement.setAttribute(father.getType().toString(),
                                                                 father.getId());
                        atLeastOneChoice = true;
                    }
                    if (!atLeastOneChoice) {
                        nextPlayer.setCurrentFather(null);
                    } else {
                        Connection conn = nextPlayer.getConnection();
                        if (conn != null) {
                            try {
                                Element reply = conn.ask(chooseFoundingFatherElement);
                                FoundingFather father = FreeCol.getSpecification().
                                    getFoundingFather(reply.getAttribute("foundingFather"));
                                if (!randomFoundingFathers.contains(father)) {
                                    throw new IllegalArgumentException();
                                }
                                nextPlayer.setCurrentFather(father);
                            } catch (IOException e) {
                                logger.warning("Could not send message to: " + nextPlayer.getName());
                            }
                        }
                    }
                }
            };
        t.start();
    }

    
    private List<FoundingFather> getRandomFoundingFathers(Player player) {
        
        Specification spec = FreeCol.getSpecification();
        int age = getGame().getTurn().getAge();
        EnumMap<FoundingFatherType, List<RandomChoice<FoundingFather>>> choices
            = new EnumMap<FoundingFatherType,
                List<RandomChoice<FoundingFather>>>(FoundingFatherType.class);
        for (FoundingFather father : spec.getFoundingFathers()) {
            if (!player.hasFather(father) && father.isAvailableTo(player)) {
                FoundingFatherType type = father.getType();
                List<RandomChoice<FoundingFather>> rc = choices.get(type);
                if (rc == null) {
                    rc = new ArrayList<RandomChoice<FoundingFather>>();
                }
                int weight = father.getWeight(age);
                rc.add(new RandomChoice<FoundingFather>(father, weight));
                choices.put(father.getType(), rc);
            }
        }

        
        List<FoundingFather> randomFathers = new ArrayList<FoundingFather>();
        String logMessage = "Random fathers";
        for (FoundingFatherType type : FoundingFatherType.values()) {
            List<RandomChoice<FoundingFather>> rc = choices.get(type);
            if (rc != null) {
                FoundingFather father
                    = RandomChoice.getWeightedRandom(getPseudoRandom(), rc);
                randomFathers.add(father);
                logMessage += ":" + father.getNameKey();
            }
        }
        logger.info(logMessage);
        return randomFathers;
    }

    
    public Player checkForWinner() {
        List<Player> players = getGame().getPlayers();
        GameOptions go = getGame().getGameOptions();
        if (go.getBoolean(GameOptions.VICTORY_DEFEAT_REF)) {
            for (Player player : players) {
                if (!player.isAI() && player.getPlayerType() == PlayerType.INDEPENDENT) {
                    return player;
                }
            }
        }
        if (go.getBoolean(GameOptions.VICTORY_DEFEAT_EUROPEANS)) {
            Player winner = null;
            for (Player player : players) {
                if (!player.isDead() && player.isEuropean() && !player.isREF()) {
                    if (winner != null) {
                        
                        winner = null;
                        break;
                    } else {
                        winner = player;
                    }
                }
            }
            if (winner != null) {
                return winner;
            }
        }
        if (go.getBoolean(GameOptions.VICTORY_DEFEAT_HUMANS)) {
            Player winner = null;
            for (Player player : players) {
                if (!player.isDead() && !player.isAI()) {
                    if (winner != null) {
                        
                        winner = null;
                        break;
                    } else {
                        winner = player;
                    }
                }
            }
            if (winner != null) {
                return winner;
            }
        }
        return null;
    }

    
    private void monarchAction(ServerPlayer player) {
        final ServerPlayer nextPlayer = player;
        final Connection conn = player.getConnection();
        if (conn == null) return;
        Thread t = new Thread("monarchAction") {
                public void run() {
                    try {
                        Monarch monarch = nextPlayer.getMonarch();
                        MonarchAction action = monarch.getAction();
                        Element monarchActionElement = Message.createNewRootElement("monarchAction");
                        monarchActionElement.setAttribute("action", String.valueOf(action));
                        switch (action) {
                        case RAISE_TAX:
                            int oldTax = nextPlayer.getTax();
                            int newTax = monarch.getNewTax(MonarchAction.RAISE_TAX);
                            if (newTax > 100) {
                                logger.warning("Tax rate exceeds 100 percent.");
                                return;
                            }
                            Goods goods = nextPlayer.getMostValuableGoods();
                            if (goods == null) {
                                return;
                            }
                            monarchActionElement.setAttribute("amount", String.valueOf(newTax));
                            
                            monarchActionElement.setAttribute("goods", Messages.message(goods.getNameKey()));
                            monarchActionElement.setAttribute("force", String.valueOf(false));
                            try {
                                nextPlayer.setTax(newTax); 
                                Element reply = conn.ask(monarchActionElement);
                                boolean accepted = Boolean.valueOf(reply.getAttribute("accepted")).booleanValue();
                            
                                if (!accepted) {
                                    Colony colony = (Colony) goods.getLocation();
                                    if (colony.getGoodsCount(goods.getType()) >= goods.getAmount()) {
                                        nextPlayer.setTax(oldTax); 
                                        Element removeGoodsElement = Message.createNewRootElement("removeGoods");
                                        colony.removeGoods(goods);
                                        nextPlayer.setArrears(goods);
                                        colony.getFeatureContainer().addModifier(Modifier
                                            .createTeaPartyModifier(getGame().getTurn()));
                                        removeGoodsElement.appendChild(goods.toXMLElement(nextPlayer, removeGoodsElement
                                                                                          .getOwnerDocument()));
                                        conn.send(removeGoodsElement);
                                    } else {
                                        
                                        monarchActionElement.setAttribute("force", String.valueOf(true));
                                        conn.send(monarchActionElement);
                                    }
                                }
                            } catch (IOException e) {
                                logger.warning("Could not send message to: " + nextPlayer.getName());
                            }
                            break;
                        case LOWER_TAX:
                            int taxLowered = monarch.getNewTax(MonarchAction.LOWER_TAX);
                            if (taxLowered < 0) {
                                logger.warning("Tax rate less than 0 percent.");
                                return;
                            }
                            monarchActionElement.setAttribute("amount", String.valueOf(taxLowered));
                            try {
                                nextPlayer.setTax(taxLowered); 
                                conn.send(monarchActionElement);
                            } catch (IOException e) {
                                logger.warning("Could not send message to: " + nextPlayer.getName());
                            }
                            break;
                        case ADD_TO_REF:
                            List<AbstractUnit> unitsToAdd = monarch.addToREF();
                            monarch.addToREF(unitsToAdd);
                            Element additionElement = monarchActionElement.getOwnerDocument().createElement("addition");
                            for (AbstractUnit unit : unitsToAdd) {
                                additionElement.appendChild(unit.toXMLElement(nextPlayer,additionElement.getOwnerDocument()));
                            }
                            monarchActionElement.appendChild(additionElement);
                            try {
                                conn.send(monarchActionElement);
                            } catch (IOException e) {
                                logger.warning("Could not send message to: " + nextPlayer.getName());
                            }
                            break;
                        case DECLARE_WAR:
                            Player enemy = monarch.declareWar();
                            if (enemy == null) {
                                
                                logger.warning("Declared war on nobody.");
                                return;
                            }
                            
                            if(nextPlayer.isAI()){
                                nextPlayer.modifyTension(enemy, Tension.TENSION_ADD_DECLARE_WAR_FROM_PEACE);
                            }
                            nextPlayer.changeRelationWithPlayer(enemy, Stance.WAR);
                            monarchActionElement.setAttribute("enemy", enemy.getId());
                            try {
                                conn.send(monarchActionElement);
                            } catch (IOException e) {
                                logger.warning("Could not send message to: " + nextPlayer.getName());
                            }
                            break;
                            
                        case OFFER_MERCENARIES:
                            Element mercenaryElement = monarchActionElement.getOwnerDocument().createElement("mercenaries");
                            List<AbstractUnit> units = monarch.getMercenaries();
                            int price = monarch.getPrice(units, true);
                            monarchActionElement.setAttribute("price", String.valueOf(price));
                            for (AbstractUnit unit : units) {
                                mercenaryElement.appendChild(unit.toXMLElement(monarchActionElement.getOwnerDocument()));
                            }
                            monarchActionElement.appendChild(mercenaryElement);
                            try {
                                Element reply = conn.ask(monarchActionElement);
                                boolean accepted = Boolean.valueOf(reply.getAttribute("accepted")).booleanValue();
                                if (accepted) {
                                    Element updateElement = Message.createNewRootElement("monarchAction");
                                    updateElement.setAttribute("action", String.valueOf(MonarchAction.ADD_UNITS));
                                    nextPlayer.modifyGold(-price);
                                    createUnits(units, updateElement, nextPlayer);
                                    conn.send(updateElement);
                                }
                            } catch (IOException e) {
                                logger.warning("Could not send message to: " + nextPlayer.getName());
                            }
                            break;
                        case NO_ACTION:
                            
                            break;
                        }
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Monarch action failed!", e);
                    }
                }
            };
        t.start();
    }

    
    public ServerPlayer createREFPlayer(ServerPlayer player) {
        Nation refNation = player.getNation().getRefNation();
        ServerPlayer refPlayer = getFreeColServer().addAIPlayer(refNation);
        refPlayer.setEntryLocation(player.getEntryLocation());
        
        player.setStance(refPlayer, Stance.PEACE);
        refPlayer.setTension(player, new Tension(Tension.Level.CONTENT.getLimit()));
        player.setTension(refPlayer, new Tension(Tension.Level.CONTENT.getLimit()));
        createREFUnits(player, refPlayer);
        return refPlayer;
    }
    
    public List<Unit> createREFUnits(ServerPlayer player, ServerPlayer refPlayer){
        EquipmentType muskets = Specification.getSpecification().getEquipmentType("model.equipment.muskets");
        EquipmentType horses = Specification.getSpecification().getEquipmentType("model.equipment.horses");
        
        List<Unit> unitsList = new ArrayList<Unit>();
        List<Unit> navalUnits = new ArrayList<Unit>();
        List<Unit> landUnits = new ArrayList<Unit>();
        
        
        for (AbstractUnit unit : player.getMonarch().getNavalUnits()) {
            for (int index = 0; index < unit.getNumber(); index++) {
                Unit newUnit = new Unit(getGame(), refPlayer.getEurope(), refPlayer,
                                        unit.getUnitType(), UnitState.TO_AMERICA);
                navalUnits.add(newUnit);
            }
        }
        unitsList.addAll(navalUnits);
        
        
        for (AbstractUnit unit : player.getMonarch().getLandUnits()) {
            EquipmentType[] equipment = EquipmentType.NO_EQUIPMENT;
            switch(unit.getRole()) {
            case SOLDIER:
                equipment = new EquipmentType[] { muskets };
                break;
            case DRAGOON:
                equipment = new EquipmentType[] { horses, muskets };
                break;
            default:
            }
            for (int index = 0; index < unit.getNumber(); index++) {
                landUnits.add(new Unit(getGame(), refPlayer.getEurope(), refPlayer,
                                        unit.getUnitType(), UnitState.ACTIVE, equipment));
            }
        }
        unitsList.addAll(landUnits);
            
        
        Iterator<Unit> carriers = navalUnits.iterator();
        for(Unit unit : landUnits){
            
            
            
            boolean noSpaceForUnit=true;
            for(Unit carrier : navalUnits){
                if (unit.getSpaceTaken() <= carrier.getSpaceLeft()) {
                    noSpaceForUnit=false;
                    break;
                }
            }
            
            if(noSpaceForUnit){
                continue;
            }
            
            Unit carrier = null;
            while (carrier == null){
                
                if (!carriers.hasNext()) {
                    carriers = navalUnits.iterator();
                }
                carrier = carriers.next();
                
                if (unit.getSpaceTaken() > carrier.getSpaceLeft()) {
                    carrier = null;
                }
            }
            
            unit.setLocation(carrier);
            
            
        }
        return unitsList;
    }

    private void createUnits(List<AbstractUnit> units, Element element, ServerPlayer nextPlayer) {
        String musketsTypeStr = null;
        String horsesTypeStr = null;
        if(nextPlayer.isIndian()){
                musketsTypeStr = "model.equipment.indian.muskets";
            horsesTypeStr = "model.equipment.indian.horses";
        } else {
                musketsTypeStr = "model.equipment.muskets";
            horsesTypeStr = "model.equipment.horses";
        }

        final EquipmentType muskets = FreeCol.getSpecification().getEquipmentType(musketsTypeStr);
        final EquipmentType horses = FreeCol.getSpecification().getEquipmentType(horsesTypeStr);

        EquipmentType[] soldier = new EquipmentType[] { muskets };
        EquipmentType[] dragoon = new EquipmentType[] { horses, muskets };
        for (AbstractUnit unit : units) {
            EquipmentType[] equipment = EquipmentType.NO_EQUIPMENT;
            for (int count = 0; count < unit.getNumber(); count++) {
                switch(unit.getRole()) {
                case SOLDIER:
                    equipment = soldier;
                    break;
                case DRAGOON:
                    equipment = dragoon;
                    break;
                default:
                }
                Unit newUnit = new Unit(getGame(), nextPlayer.getEurope(), nextPlayer,
                                        unit.getUnitType(), UnitState.ACTIVE, equipment);
                
                if (element != null) {
                    element.appendChild(newUnit.toXMLElement(nextPlayer, element.getOwnerDocument()));
                }
            }
        }
    }

    private void bombardEnemyShips(ServerPlayer currentPlayer) {
        logger.finest("Entering method bombardEnemyShips.");
        Map map = getFreeColServer().getGame().getMap();
        CombatModel combatModel = getFreeColServer().getGame().getCombatModel();
        for (Settlement settlement : currentPlayer.getSettlements()) {
            Colony colony = (Colony) settlement;
            
            if (!colony.canBombardEnemyShip()){
            	continue;
            }

            logger.fine("Colony " + colony.getName() + " can bombard enemy ships.");
            Position colonyPosition = colony.getTile().getPosition();
            for (Direction direction : Direction.values()) {
            	Tile tile = map.getTile(Map.getAdjacent(colonyPosition, direction));

            	
            	if(tile == null || tile.isLand()){
            		continue;
            	}

            	
            	
            	List<Unit> unitList = new ArrayList<Unit>(tile.getUnitList());
            	for(Unit unit : unitList){
                    logger.fine(colony.getName() + " found unit : " + unit.toString());
            		
            		
            		Tile unitTile = unit.getTile();
            		
            		Player player = unit.getOwner();

            		
            		if(player == currentPlayer){
            			continue;
            		}

            		
            		if(currentPlayer.getStance(player) != Stance.WAR &&
            				!unit.hasAbility("model.ability.piracy")){
                            logger.warning(colony.getName() + " found unit to not bombard: "
                                           + unit.toString());
            			continue;
            		}

            		logger.warning(colony.getName() + " found enemy unit to bombard: " +
                                       unit.toString());
            		
            		CombatModel.CombatResult result = combatModel.generateAttackResult(colony, unit);

            		
            		Location repairLocation = null;
            		if(result.type == CombatModel.CombatResultType.WIN){
            			repairLocation = player.getRepairLocation(unit);
            		}

            		
            		getGame().getCombatModel().bombard(colony, unit, result, repairLocation);

            		
            		
            		int plunderGold = -1;
            		Iterator<Player> enemyPlayerIterator = getFreeColServer().getGame().getPlayerIterator();
            		while (enemyPlayerIterator.hasNext()) {
            			ServerPlayer enemyPlayer = (ServerPlayer) enemyPlayerIterator.next();

            			if (enemyPlayer.getConnection() == null) {
            				continue;
            			}

            			
            			if(!enemyPlayer.canSee(unitTile)){
            				continue;
            			}

            			Element opponentAttackElement = Message.createNewRootElement("opponentAttack");                                 
            			opponentAttackElement.setAttribute("direction", direction.toString());
            			opponentAttackElement.setAttribute("result", result.type.toString());
            			opponentAttackElement.setAttribute("plunderGold", Integer.toString(plunderGold));
            			opponentAttackElement.setAttribute("colony", colony.getId());
            			opponentAttackElement.setAttribute("defender", unit.getId());
            			opponentAttackElement.setAttribute("damage", String.valueOf(result.damage));

            			
            			if(enemyPlayer == player && repairLocation != null){
            				opponentAttackElement.setAttribute("repairIn", repairLocation.getId());
            			}

            			
            			if (!enemyPlayer.canSee(colony.getTile())) {
            				opponentAttackElement.setAttribute("update", "tile");
            				enemyPlayer.setExplored(colony.getTile());
            				opponentAttackElement.appendChild(colony.getTile().toXMLElement(
            						enemyPlayer, opponentAttackElement.getOwnerDocument()));
            			}

            			
            			try {
            				enemyPlayer.getConnection().send(opponentAttackElement);
            			} catch (IOException e) {
            				logger.warning("Could not send message to: " + enemyPlayer.getName()
            						+ " with connection " + enemyPlayer.getConnection());
            			}
            		}
                }
            }
        }
    }
    
    
    public Element cashInTreasureTrain(ServerPlayer serverPlayer, Unit unit) {
        List<Object> objects = new ArrayList<Object>();

        
        int fullAmount = unit.getTreasureAmount();
        int cashInAmount = (fullAmount - unit.getTransportFee())
            * (100 - serverPlayer.getTax()) / 100;
        serverPlayer.modifyGold(cashInAmount);
        objects.add((FreeColGameObject) unit.getLocation());
        addPartial(objects, (Player) serverPlayer, "gold", "score");

        
        String messageId = (serverPlayer.getPlayerType() == PlayerType.REBEL
            || serverPlayer.getPlayerType() == PlayerType.INDEPENDENT)
            ? "model.unit.cashInTreasureTrain.independent"
            : "model.unit.cashInTreasureTrain.colonial";
        objects.add(new ModelMessage(messageId, serverPlayer, unit)
                    .addAmount("%amount%", fullAmount)
                    .addAmount("%cashInAmount%", cashInAmount));

        
        objects.addAll(unit.disposeList());

        
        
        return buildUpdate(serverPlayer, objects);
    }


    
    public Element renameObject(ServerPlayer serverPlayer, Nameable object,
                                String newName) {
        object.setName(newName);

        
        List<Object> objects = new ArrayList<Object>();
        FreeColGameObject fcgo = (FreeColGameObject) object;
        addPartial(objects, fcgo, "name");
        sendToOthers(serverPlayer, objects);
        return buildUpdate(serverPlayer, objects);
    }


    
    public java.util.Map<String,Object> getTransactionSession(Unit unit, Settlement settlement) {
        java.util.Map<String, java.util.Map<String,Object>> unitTransactions = null;
        
        if (transactionSessions.containsKey(unit.getId())) {
            unitTransactions = transactionSessions.get(unit.getId());
            if (unitTransactions.containsKey(settlement.getId())) {
                return unitTransactions.get(settlement.getId());
            }
        }

        
        java.util.Map<String,Object> session = new HashMap<String,Object>();
        
        session.put("canGift", true);
        session.put("actionTaken", false);
        session.put("unitMoves", unit.getMovesLeft());
        if (settlement.getOwner().getStance(unit.getOwner()) == Stance.WAR) {
            session.put("canSell", false);
            session.put("canBuy", false);
        } else {
            session.put("canBuy", true);
            
            
            session.put("canSell", unit.getSpaceTaken() != 0);
        }

        
        if (unit.getOwner().isAI()) {
            return session;
        }
        
        
        
        if (unitTransactions == null) {
            unitTransactions = new HashMap<String,java.util.Map<String, Object>>();
            transactionSessions.put(unit.getId(), unitTransactions);
        }
        unitTransactions.put(settlement.getId(), session);
        return session;
    }

    
    public void closeTransactionSession(Unit unit, Settlement settlement) {
        
        if (unit.getOwner().isAI()) {
            return;
        }

        if (!transactionSessions.containsKey(unit.getId())) {
            throw new IllegalStateException("Trying to close a non-existing session (unit)");
        }

        java.util.Map<String, java.util.Map<String,Object>> unitTransactions
            = transactionSessions.get(unit.getId());
        if (!unitTransactions.containsKey(settlement.getId())) {
            throw new IllegalStateException("Trying to close a non-existing session (settlement)");
        }

        unitTransactions.remove(settlement.getId());
        if (unitTransactions.isEmpty()) {
            transactionSessions.remove(unit.getId());
        }
    }
    
    
    public boolean isTransactionSessionOpen(Unit unit, Settlement settlement) {
        
        if (unit.getOwner().isAI()) return true;

        return transactionSessions.containsKey(unit.getId())
            && settlement != null
            && transactionSessions.get(unit.getId()).containsKey(settlement.getId());
    }

    
    public Element getTransaction(ServerPlayer serverPlayer, Unit unit,
                                  Settlement settlement) {
        List<Object> objects = new ArrayList<Object>();
        java.util.Map<String,Object> session;

        if (isTransactionSessionOpen(unit, settlement)) {
            session = getTransactionSession(unit, settlement);
        } else {
            if (unit.getMovesLeft() <= 0) {
                return Message.clientError("Unit " + unit.getId()
                                           + " has no moves left.");
            }
            session = getTransactionSession(unit, settlement);
            
            
            
            unit.setMovesLeft(0);
            addPartial(objects, unit, "movesLeft");
        }

        
        addAttribute(objects, "canBuy",
                     ((Boolean) session.get("canBuy")).toString());
        addAttribute(objects, "canSell",
                     ((Boolean) session.get("canSell")).toString());
        addAttribute(objects, "canGift",
                     ((Boolean) session.get("canGift")).toString());

        
        return buildUpdate(serverPlayer, objects);
    }

    
    public Element closeTransaction(ServerPlayer serverPlayer, Unit unit,
                                    Settlement settlement) {
        if (!isTransactionSessionOpen(unit, settlement)) {
            return Message.clientError("No such transaction session.");
        }

        java.util.Map<String,Object> session
            = getTransactionSession(unit, settlement);
        List<Object> objects = new ArrayList<Object>();

        
        Boolean actionTaken = (Boolean) session.get("actionTaken");
        if (!actionTaken) {
            Integer unitMoves = (Integer) session.get("unitMoves");
            unit.setMovesLeft(unitMoves);
            addPartial(objects, unit, "movesLeft");
        }
        closeTransactionSession(unit, settlement);

        
        return (objects.isEmpty()) ? null : buildUpdate(serverPlayer, objects);
    }


    
    public List<Goods> getGoodsForSale(Unit unit, Settlement settlement)
        throws IllegalStateException {
        List<Goods> sellGoods = null;

        if (settlement instanceof IndianSettlement) {
            IndianSettlement indianSettlement = (IndianSettlement) settlement;
            sellGoods = indianSettlement.getSellGoods();
            if (!sellGoods.isEmpty()) {
                AIPlayer aiPlayer = (AIPlayer) getFreeColServer().getAIMain()
                    .getAIObject(indianSettlement.getOwner());
                for (Goods goods : sellGoods) {
                    aiPlayer.registerSellGoods(goods);
                }
            }
        } else { 
            throw new IllegalStateException("Bogus settlement");
        }
        return sellGoods;
    }


    
    public Element buyProposition(ServerPlayer serverPlayer,
                                  Unit unit, Settlement settlement,
                                  Goods goods, int price) {
        if (!isTransactionSessionOpen(unit, settlement)) {
            return Message.clientError("Proposing to buy without opening a transaction session?!");
        }
        java.util.Map<String,Object> session
            = getTransactionSession(unit, settlement);
        if (!(Boolean) session.get("canBuy")) {
            return Message.clientError("Proposing to buy in a session where buying is not allowed.");
        }

        
        AIPlayer ai = (AIPlayer) getFreeColServer().getAIMain()
            .getAIObject(settlement.getOwner());
        int gold = ai.buyProposition(unit, settlement, goods, price);

        
        List<Object> objects = new ArrayList<Object>();
        addAttribute(objects, "gold", Integer.toString(gold));
        return buildUpdate(serverPlayer, objects);
    }

    
    public Element sellProposition(ServerPlayer serverPlayer,
                                   Unit unit, Settlement settlement,
                                   Goods goods, int price) {
        if (!isTransactionSessionOpen(unit, settlement)) {
            return Message.clientError("Proposing to sell without opening a transaction session");
        }
        java.util.Map<String,Object> session
            = getTransactionSession(unit, settlement);
        if (!(Boolean) session.get("canSell")) {
            return Message.clientError("Proposing to sell in a session where selling is not allowed.");
        }

        
        AIPlayer ai = (AIPlayer) getFreeColServer().getAIMain()
            .getAIObject(settlement.getOwner());
        int gold = ai.sellProposition(unit, settlement, goods, price);

        
        List<Object> objects = new ArrayList<Object>();
        addAttribute(objects, "gold", Integer.toString(gold));
        return buildUpdate(serverPlayer, objects);
    }


    
    private void propagateToEuropeanMarkets(GoodsType type, int amount,
                                            ServerPlayer serverPlayer) {
        
        final int lowerBound = 5; 
        final int upperBound = 30;
        amount *= getPseudoRandom().nextInt(upperBound - lowerBound + 1)
            + lowerBound;
        amount /= 100;
        if (amount == 0) return;

        
        
        
        Market market;
        for (ServerPlayer other : getOtherPlayers(serverPlayer)) {
            if (other.isEuropean() && (market = other.getMarket()) != null) {
                market.addGoodsToMarket(type, amount);
            }
        }
    }

    
    public Element buyGoods(ServerPlayer serverPlayer, Unit unit,
                            GoodsType type, int amount) {
        List<Object> objects = new ArrayList<Object>();
        Market market = serverPlayer.getMarket();

        
        
        
        
        
        
        
        
        market.buy(type, amount, serverPlayer);
        unit.getGoodsContainer().addGoods(type, amount);
        objects.add(unit);
        addPartial(objects, serverPlayer, "gold");
        if (market.hasPriceChanged(type)) {
            
            
            objects.add(market.makePriceChangeMessage(type));
            market.flushPriceChange(type);
        }
        propagateToEuropeanMarkets(type, amount, serverPlayer);

        
        return buildUpdate(serverPlayer, objects);
    }

    
    public Element sellGoods(ServerPlayer serverPlayer, Unit unit,
                             GoodsType type, int amount) {
        List<Object> objects = new ArrayList<Object>();
        Market market = serverPlayer.getMarket();

        
        
        
        
        
        
        
        
        
        market.sell(type, amount, serverPlayer);
        unit.getGoodsContainer().addGoods(type, -amount);
        objects.add(unit);
        addPartial(objects, serverPlayer, "gold");
        if (market.hasPriceChanged(type)) {
            
            
            objects.add(market.makePriceChangeMessage(type));
            market.flushPriceChange(type);
        }
        propagateToEuropeanMarkets(type, amount, serverPlayer);

        
        return buildUpdate(serverPlayer, objects);
    }


    
    public Element emigrate(ServerPlayer serverPlayer, int slot,
                            boolean fountain) {
        List<Object> objects = new ArrayList<Object>();

        
        
        
        boolean validSlot = 1 <= slot && slot <= Europe.RECRUIT_COUNT;
        int index = (validSlot) ? slot-1
            : getPseudoRandom().nextInt(Europe.RECRUIT_COUNT);

        
        Europe europe = serverPlayer.getEurope();
        UnitType recruitType = europe.getRecruitable(index);
        Game game = getGame();
        Unit unit = new Unit(game, europe, serverPlayer, recruitType,
                             UnitState.ACTIVE,
                             recruitType.getDefaultEquipment());
        unit.setLocation(europe);

        
        
        if (!fountain) {
            serverPlayer.updateImmigrationRequired();
            serverPlayer.reduceImmigration();
            addPartial(objects, (Player) serverPlayer,
                       "immigration", "immigrationRequired");
        }

        
        
        
        String taskId = serverPlayer.getId()
            + ".emigrate." + game.getTurn().toString()
            + ".slot." + Integer.toString(slot)
            + "." + Integer.toString(getPseudoRandom().nextInt(1000000));
        europe.setRecruitable(index, serverPlayer.generateRecruitable(taskId));
        objects.add(europe);

        
        
        
        if (!fountain && !validSlot) {
            objects.add(new ModelMessage(ModelMessage.MessageType.UNIT_ADDED,
                                         "model.europe.emigrate",
                                         serverPlayer, unit)
                        .add("%europe%", europe.getNameKey())
                        .addStringTemplate("%unit%", unit.getLabel()));
        }

        
        return buildUpdate(serverPlayer, objects);
    }


    
    private Unit getSlowedBy(Unit unit, Tile newTile) {
        Player player = unit.getOwner();
        Game game = unit.getGame();
        CombatModel combatModel = game.getCombatModel();
        Unit attacker = null;
        boolean pirate = unit.hasAbility("model.ability.piracy");
        float attackPower = 0;

        if (!unit.isNaval() || unit.getMovesLeft() <= 0) return null;
        for (Tile tile : game.getMap().getSurroundingTiles(newTile, 1)) {
            
            
            Player enemy;
            if (tile.isLand()
                || tile.getColony() != null
                || tile.getFirstUnit() == null
                || (enemy = tile.getFirstUnit().getOwner()) == player) continue;
            for (Unit enemyUnit : tile.getUnitList()) {
                if (pirate || enemyUnit.hasAbility("model.ability.piracy")
                    || (enemyUnit.isOffensiveUnit()
                        && player.getStance(enemy) == Stance.WAR)) {
                    attackPower += combatModel.getOffencePower(enemyUnit, unit);
                    if (attacker == null) {
                        attacker = enemyUnit;
                    }
                }
            }
        }
        if (attackPower > 0) {
            float defencePower = combatModel.getDefencePower(attacker, unit);
            float totalProbability = attackPower + defencePower;
            if (getPseudoRandom().nextInt(Math.round(totalProbability) + 1)
                < attackPower) {
                int diff = Math.max(0, Math.round(attackPower - defencePower));
                int moves = Math.min(9, 3 + diff / 3);
                unit.setMovesLeft(unit.getMovesLeft() - moves);
                logger.info(unit.getId()
                            + " slowed by " + attacker.getId()
                            + " by " + Integer.toString(moves) + " moves.");
            } else {
                attacker = null;
            }
        }
        return attacker;
    }

    
    private RumourType getLostCityRumourType(LostCityRumour lostCity,
                                             Unit unit, int difficulty) {
        Tile tile = unit.getTile();
        Player player = unit.getOwner();
        RumourType rumour = lostCity.getType();
        if (rumour != null) {
            
            
            switch (rumour) {
            case BURIAL_GROUND:
                if (tile.getOwner() == null || !tile.getOwner().isIndian()) {
                    rumour = RumourType.NOTHING;
                }
                break;
            case LEARN:
                if (unit.getType().getUnitTypesLearntInLostCity().isEmpty()) {
                    rumour = RumourType.NOTHING;
                }
                break;
            default:
                break;
            }
            return rumour;
        }

        
        
        
        
        final int BAD_EVENT_PERCENTAGE[]  = { 11, 17, 23, 30, 37 };
        final int GOOD_EVENT_PERCENTAGE[] = { 75, 62, 48, 33, 17 };
        

        
        
        
        
        final int BAD_EVENT_MOD[]  = { -6, -7, -7, -8, -9 };
        final int GOOD_EVENT_MOD[] = { 14, 15, 16, 18, 20 };

        
        
        
        
        boolean isExpertScout = unit.hasAbility("model.ability.expertScout")
            && unit.hasAbility("model.ability.scoutIndianSettlement");
        boolean hasDeSoto = player.hasAbility("model.ability.rumoursAlwaysPositive");
        int percentNeutral;
        int percentBad;
        int percentGood;
        if (hasDeSoto) {
            percentBad  = 0;
            percentGood = 100;
            percentNeutral = 0;
        } else {
            
            percentBad  = BAD_EVENT_PERCENTAGE[difficulty];
            percentGood = GOOD_EVENT_PERCENTAGE[difficulty];

            
            if (isExpertScout) {
                percentBad  += BAD_EVENT_MOD[difficulty];
                percentGood += GOOD_EVENT_MOD[difficulty];
            }

            
            
            if (percentBad + percentGood < 100) {
                percentNeutral = 100 - percentBad - percentGood;
            } else {
                percentNeutral = 0;
            }
        }

        
        
        int eventNothing = 100;

        
        int eventVanish = 100;
        int eventBurialGround = 0;
        
        if (tile.getOwner() != null && tile.getOwner().isIndian()) {
            eventVanish = 75;
            eventBurialGround = 25;
        }

        
        int eventLearn    = 30;
        int eventTrinkets = 30;
        int eventColonist = 20;
        
        if (unit.getType().getUnitTypesLearntInLostCity().isEmpty()) {
            eventLearn    =  0;
            eventTrinkets = 50;
            eventColonist = 30;
        }

        
        
        int eventRuins    = 9;
        int eventCibola   = 6;
        int eventFountain = 5;

        
        
        
        eventNothing      *= percentNeutral;
        eventVanish       *= percentBad;
        eventBurialGround *= percentBad;
        eventLearn        *= percentGood;
        eventTrinkets     *= percentGood;
        eventColonist     *= percentGood;
        eventRuins        *= percentGood;
        eventCibola       *= percentGood;
        eventFountain     *= percentGood;

        
        List<RandomChoice<RumourType>> choices = new ArrayList<RandomChoice<RumourType>>();
        if (eventNothing > 0) {
            choices.add(new RandomChoice<RumourType>(RumourType.NOTHING, eventNothing));
        }
        if (eventVanish > 0) {
            choices.add(new RandomChoice<RumourType>(RumourType.EXPEDITION_VANISHES, eventVanish));
        }
        if (eventBurialGround > 0) {
            choices.add(new RandomChoice<RumourType>(RumourType.BURIAL_GROUND, eventBurialGround));
        }
        if (eventLearn > 0) {
            choices.add(new RandomChoice<RumourType>(RumourType.LEARN, eventLearn));
        }
        if (eventTrinkets > 0) {
            choices.add(new RandomChoice<RumourType>(RumourType.TRIBAL_CHIEF, eventTrinkets));
        }
        if (eventColonist > 0) {
            choices.add(new RandomChoice<RumourType>(RumourType.COLONIST, eventColonist));
        }
        if (eventRuins > 0) {
            choices.add(new RandomChoice<RumourType>(RumourType.RUINS, eventRuins));
        }
        if (eventCibola > 0) {
            choices.add(new RandomChoice<RumourType>(RumourType.CIBOLA, eventCibola));
        }
        if (eventFountain > 0) {
            choices.add(new RandomChoice<RumourType>(RumourType.FOUNTAIN_OF_YOUTH, eventFountain));
        }
        return RandomChoice.getWeightedRandom(getPseudoRandom(), choices);
    }

    
    private List<Object> exploreLostCityRumour(ServerPlayer serverPlayer,
                                               Unit unit) {
        List<Object> objects = new ArrayList<Object>();
        Tile tile = unit.getTile();
        LostCityRumour lostCity = tile.getLostCityRumour();
        if (lostCity == null) return objects;

        Specification specification = FreeCol.getSpecification();
        int difficulty = specification.getRangeOption("model.option.difficulty").getValue();
        int dx = 10 - difficulty;
        Game game = unit.getGame();
        UnitType unitType;
        Unit newUnit = null;
        List<UnitType> treasureUnitTypes = null;

        switch (getLostCityRumourType(lostCity, unit, difficulty)) {
        case BURIAL_GROUND:
            Player indianPlayer = tile.getOwner();
            indianPlayer.modifyTension(serverPlayer, Tension.Level.HATEFUL.getLimit());
            objects.add(indianPlayer);
            objects.add(new ModelMessage(ModelMessage.MessageType.LOST_CITY_RUMOUR,
                                        "lostCityRumour.BurialGround",
                                        serverPlayer, unit)
                       .addStringTemplate("%nation%", indianPlayer.getNationName()));
            break;
        case EXPEDITION_VANISHES:
            objects.addAll(unit.disposeList());
            objects.add(new ModelMessage(ModelMessage.MessageType.LOST_CITY_RUMOUR,
                                        "lostCityRumour.ExpeditionVanishes", serverPlayer));
            break;
        case NOTHING:
            objects.add(new ModelMessage(ModelMessage.MessageType.LOST_CITY_RUMOUR,
                                        "lostCityRumour.Nothing", serverPlayer, unit));
            break;
        case LEARN:
            List<UnitType> learntUnitTypes = unit.getType().getUnitTypesLearntInLostCity();
            StringTemplate oldName = unit.getLabel();
            unit.setType(learntUnitTypes.get(getPseudoRandom().nextInt(learntUnitTypes.size())));
            objects.add(new ModelMessage(ModelMessage.MessageType.LOST_CITY_RUMOUR,
                                        "lostCityRumour.Learn", serverPlayer, unit)
                       .addStringTemplate("%unit%", oldName)
                       .add("%type%", unit.getType().getNameKey()));
            break;
        case TRIBAL_CHIEF:
            int chiefAmount = getPseudoRandom().nextInt(dx * 10) + dx * 5;
            serverPlayer.modifyGold(chiefAmount);
            addPartial(objects, serverPlayer, "gold", "score");
            objects.add(new ModelMessage(ModelMessage.MessageType.LOST_CITY_RUMOUR,
                                        "lostCityRumour.TribalChief", serverPlayer, unit)
                       .addAmount("%money%", chiefAmount));
            break;
        case COLONIST:
            List<UnitType> newUnitTypes = specification.getUnitTypesWithAbility("model.ability.foundInLostCity");
            newUnit = new Unit(game, tile, serverPlayer,
                               newUnitTypes.get(getPseudoRandom().nextInt(newUnitTypes.size())),
                               UnitState.ACTIVE);
            objects.add(new ModelMessage(ModelMessage.MessageType.LOST_CITY_RUMOUR,
                                        "lostCityRumour.Colonist", serverPlayer, newUnit));
            break;
        case CIBOLA:
            String cityName = game.getCityOfCibola();
            if (cityName != null) {
                int treasureAmount = getPseudoRandom().nextInt(dx * 600) + dx * 300;
                if (treasureUnitTypes == null) {
                    treasureUnitTypes = specification.getUnitTypesWithAbility("model.ability.carryTreasure");
                }
                unitType = treasureUnitTypes.get(getPseudoRandom().nextInt(treasureUnitTypes.size()));
                newUnit = new Unit(game, tile, serverPlayer, unitType, UnitState.ACTIVE);
                newUnit.setTreasureAmount(treasureAmount);
                objects.add(new ModelMessage(ModelMessage.MessageType.LOST_CITY_RUMOUR,
                                            "lostCityRumour.Cibola", serverPlayer, newUnit)
                           .add("%city%", cityName)
                           .addAmount("%money%", treasureAmount));
                objects.add(new HistoryEvent(game.getTurn().getNumber(), HistoryEvent.EventType.CITY_OF_GOLD)
                           .add("%city%", cityName)
                           .addAmount("%treasure%", treasureAmount));
                break;
            }
            
        case RUINS:
            int ruinsAmount = getPseudoRandom().nextInt(dx * 2) * 300 + 50;
            if (ruinsAmount < 500) { 
                serverPlayer.modifyGold(ruinsAmount);
                addPartial(objects, serverPlayer, "gold", "score");
            } else {
                if (treasureUnitTypes == null) {
                    treasureUnitTypes = specification.getUnitTypesWithAbility("model.ability.carryTreasure");
                }
                unitType = treasureUnitTypes.get(getPseudoRandom().nextInt(treasureUnitTypes.size()));
                newUnit = new Unit(game, tile, serverPlayer, unitType, UnitState.ACTIVE);
                newUnit.setTreasureAmount(ruinsAmount);
            }
            objects.add(new ModelMessage(ModelMessage.MessageType.LOST_CITY_RUMOUR,
                                        "lostCityRumour.Ruins",
                                        serverPlayer, ((newUnit != null) ? newUnit : unit))
                       .addAmount("%money%", ruinsAmount));
            break;
        case FOUNTAIN_OF_YOUTH:
            Europe europe = serverPlayer.getEurope();
            if (europe == null) {
                objects.add(new ModelMessage(ModelMessage.MessageType.LOST_CITY_RUMOUR,
                                            "lostCityRumour.FountainOfYouthWithoutEurope",
                                            serverPlayer, unit));
            } else {
                if (serverPlayer.hasAbility("model.ability.selectRecruit")
                    && !serverPlayer.isAI()) { 
                    
                    serverPlayer.setRemainingEmigrants(dx);
                    addAttribute(objects, "fountainOfYouth", Integer.toString(dx));
                } else {
                    for (int k = 0; k < dx; k++) {
                        new Unit(game, europe, serverPlayer, serverPlayer.generateRecruitable(serverPlayer.getId() + "fountain." + Integer.toString(k)),
                                 UnitState.ACTIVE);
                    }
                    objects.add(europe);
                }
                objects.add(new ModelMessage(ModelMessage.MessageType.LOST_CITY_RUMOUR,
                                            "lostCityRumour.FountainOfYouth",
                                            serverPlayer, unit));
            }
            break;
        case NO_SUCH_RUMOUR:
        default:
            throw new IllegalStateException("No such rumour.");
        }
        tile.removeLostCityRumour();
        return objects;
    }

    
    private List<ServerPlayer> findUncontacted(ServerPlayer serverPlayer,
                                               Tile tile) {
        List<ServerPlayer> players = new ArrayList<ServerPlayer>();
        for (Tile t : getGame().getMap().getSurroundingTiles(tile, 1)) {
            if (t == null || !t.isLand()) {
                continue; 
            }

            ServerPlayer otherPlayer = null;
            if (t.getSettlement() != null) {
                otherPlayer = (ServerPlayer) t.getSettlement().getOwner();
            } else if (t.getFirstUnit() != null) {
                otherPlayer = (ServerPlayer) t.getFirstUnit().getOwner();
            }

            
            if (otherPlayer != null && otherPlayer != serverPlayer
                && !serverPlayer.hasContacted(otherPlayer)) {
                players.add(otherPlayer);
            }
        }
        return players;
    }

    
    public Element move(ServerPlayer serverPlayer, Unit unit, Tile newTile) {
        List<Object> objects = new ArrayList<Object>();
        Game game = getGame();
        Turn turn = game.getTurn();

        
        
        List<Object> privateObjects = new ArrayList<Object>();
        int los = unit.getLineOfSight();
        for (Tile tile : game.getMap().getSurroundingTiles(newTile, los)) {
            if (!serverPlayer.canSee(tile)) privateObjects.add(tile);
        }

        
        Location oldLocation = unit.getLocation();
        unit.setState(UnitState.ACTIVE);
        unit.setStateToAllChildren(UnitState.SENTRY);
        if (oldLocation instanceof Unit) {
            unit.setMovesLeft(0); 
        } else {
            unit.setMovesLeft(unit.getMovesLeft() - unit.getMoveCost(newTile));
        }
        unit.setLocation(newTile);
        unit.activeAdjacentSentryUnits(newTile);
        
        unit.setAlreadyOnHighSea(newTile.canMoveToEurope());

        
        objects.add((FreeColGameObject) oldLocation);
        objects.add(newTile);

        
        
        if (newTile.hasLostCityRumour() && serverPlayer.isEuropean()) {
            List<Object> rumourObjects
                = exploreLostCityRumour(serverPlayer, unit);
            if (unit.isDisposed()) {
                privateObjects.clear(); 
            }
            privateObjects.addAll(rumourObjects);
        }

        
        addAnimate(objects, unit, oldLocation, newTile);

        
        if (!unit.isDisposed() && newTile.isLand()) {
            ServerPlayer welcomer = null;
            for (ServerPlayer other : findUncontacted(serverPlayer, newTile)) {
                
                if (serverPlayer.isEuropean() && other.isIndian()
                    && !serverPlayer.isNewLandNamed()
                    && (welcomer == null || newTile.getOwner() == other)) {
                    welcomer = other;
                }
                serverPlayer.setContacted(other);
                other.setContacted(serverPlayer);
                addStance(objects, Stance.PEACE, serverPlayer, other);
                if (serverPlayer.isEuropean()) {
                    HistoryEvent h = new HistoryEvent(turn.getNumber(),
                            HistoryEvent.EventType.MEET_NATION)
                        .addStringTemplate("%nation%", other.getNationName());
                    serverPlayer.addHistory(h);
                    privateObjects.add(h);
                }
            }
            if (welcomer != null) {
                addAttribute(privateObjects, "welcome", welcomer.getId());
                addAttribute(privateObjects, "camps",
                    Integer.toString(welcomer.getNumberOfSettlements()));
            }
        }

        
        objects.add(UpdateType.PRIVATE);
        objects.addAll(privateObjects);

        if (!unit.isDisposed() && serverPlayer.isEuropean()) {
            
            
            for (Tile t : game.getMap().getSurroundingTiles(newTile, 1)) {
                Settlement settlement = t.getSettlement();
                if (settlement != null
                    && settlement instanceof IndianSettlement) {
                    IndianSettlement indians = (IndianSettlement) settlement;
                    if (indians.getAlarm(serverPlayer) == null) {
                        Player indianPlayer = indians.getOwner();
                        indians.setAlarm(serverPlayer,
                                         indianPlayer.getTension(serverPlayer));
                        objects.add(indians);
                    }
                }
            }

            
            Unit slowedBy = getSlowedBy(unit, newTile);
            if (slowedBy != null) {
                addAttribute(objects, "slowedBy", slowedBy.getId());
            }

            
            if (newTile.isLand() && !serverPlayer.isNewLandNamed()) {
                String newLandName = Messages.getNewLandName(serverPlayer);
                if (serverPlayer.isAI()) {
                    
                    
                    
                    
                    serverPlayer.setNewLandName(newLandName);
                } else { 
                    addAttribute(objects, "nameNewLand", newLandName);
                }
            }

            
            Region region = newTile.getDiscoverableRegion();
            if (region != null) {
                HistoryEvent h = null;
                if (region.isPacific()) {
                    addAttribute(objects, "discoverPacific", "true");
                    h = region.discover(serverPlayer, turn,
                                        "model.region.pacific");
                    objects.add(0, region); 
                } else {
                    String regionName
                        = Messages.getDefaultRegionName(serverPlayer,
                                                        region.getType());
                    if (serverPlayer.isAI()) {
                        
                        region.discover(serverPlayer, turn, regionName);
                        objects.add(0, region); 
                    } else { 
                        addAttribute(objects, "discoverRegion", regionName);
                        addAttribute(objects, "regionType",
                                     Messages.message(region.getLabel()));
                    }
                }
                if (h != null) {
                    serverPlayer.addHistory(h);
                    objects.add(h);
                }
            }
        }

        sendToOthers(serverPlayer, objects);
        return buildUpdate(serverPlayer, objects);
    }

    
    public Element setNewLandName(ServerPlayer serverPlayer, String name,
                                  ServerPlayer welcomer, boolean accept) {
        List<Object> objects = new ArrayList<Object>();
        serverPlayer.setNewLandName(name);

        
        
        
        
        
        
        if (welcomer != null) {
            if (accept) { 
                for (Unit u : serverPlayer.getUnits()) {
                    if (u.isNaval()) continue;
                    Tile tile = u.getTile();
                    if (tile == null) continue;
                    if (tile.isLand() && tile.getOwner() == welcomer) {
                        tile.setOwner(serverPlayer);
                        objects.add(tile);
                        break;
                    }
                }
                welcomer = null;
            } else {
                
                
                
                
                
                welcomer.modifyTension(serverPlayer,
                                       Tension.TENSION_ADD_MINOR);
            }
        }

        
        sendToOthers(serverPlayer, objects);

        
        addPartial(objects, serverPlayer, "newLandName");
        int turn = serverPlayer.getGame().getTurn().getNumber();
        HistoryEvent h = new HistoryEvent(turn,
                    HistoryEvent.EventType.DISCOVER_NEW_WORLD)
            .addName("%name%", name);
        objects.add(h);
        serverPlayer.addHistory(h);

        return buildUpdate(serverPlayer, objects);
    }

    
    public Element setNewRegionName(ServerPlayer serverPlayer, Unit unit,
                                    Region region, String name) {
        Game game = serverPlayer.getGame();
        List<Object> objects = new ArrayList<Object>();
        
        objects.add(region);

        
        objects.add(UpdateType.PRIVATE);
        HistoryEvent h = region.discover(serverPlayer, game.getTurn(), name);
        serverPlayer.addHistory(h);
        objects.add(h);

        
        sendToOthers(serverPlayer, objects);
        return buildUpdate(serverPlayer, objects);
    }


    
    public Element embarkUnit(ServerPlayer serverPlayer, Unit unit,
                              Unit carrier) {
        if (unit.isNaval()) {
            return Message.clientError("Naval unit " + unit.getId()
                                       + " can not embark.");
        }
        if (carrier.getSpaceLeft() < unit.getSpaceTaken()) {
            return Message.clientError("No space available for unit "
                                       + unit.getId() + " to embark.");
        }

        List<Object> objects = new ArrayList<Object>();
        Location oldLocation = unit.getLocation();
        boolean visible = oldLocation instanceof Tile
            && ((Tile) oldLocation).getSettlement() == null
            && carrier.getLocation() != oldLocation;
        unit.setLocation(carrier);
        unit.setMovesLeft(0);
        unit.setState(UnitState.SENTRY);
        objects.add(oldLocation);
        if (carrier.getLocation() != oldLocation) {
            objects.add(carrier);
            addAnimate(objects, unit, oldLocation, carrier.getTile());
        }

        
        
        List<Object> otherObjects = new ArrayList<Object>();
        otherObjects.add(oldLocation);
        if (visible) {
            otherObjects.add(carrier);
            addAnimate(otherObjects, unit, oldLocation, carrier.getTile());
        } else {
            addRemove(otherObjects, unit);
        }
        sendToOthers(serverPlayer, otherObjects);
        return buildUpdate(serverPlayer, objects);
    }

    
    public Element disembarkUnit(ServerPlayer serverPlayer, Unit unit) {
        if (unit.isNaval()) {
            return Message.clientError("Naval unit " + unit.getId()
                                       + " can not disembark.");
        }
        if (!(unit.getLocation() instanceof Unit)) {
            return Message.clientError("Unit " + unit.getId()
                                       + " is not embarked.");
        }

        Unit carrier = (Unit) unit.getLocation();
        Location newLocation = carrier.getLocation();
        unit.setLocation(newLocation);
        unit.setMovesLeft(0); 
        unit.setState(UnitState.ACTIVE);

        
        sendToOthers(serverPlayer, newLocation);
        return buildUpdate(serverPlayer, newLocation);
    }


    
    public Element askLearnSkill(ServerPlayer serverPlayer, Unit unit,
                                 IndianSettlement settlement) {
        List<Object> objects = new ArrayList<Object>();
        Tile tile = settlement.getTile();
        PlayerExploredTile pet = tile.getPlayerExploredTile(serverPlayer);
        pet.setVisited();
        pet.setSkill(settlement.getLearnableSkill());
        objects.add(tile);
        unit.setMovesLeft(0);
        addPartial(objects, unit, "movesLeft");

        
        return buildUpdate(serverPlayer, objects);
    }

    
    public Element learnFromIndianSettlement(ServerPlayer serverPlayer,
                                             Unit unit,
                                             IndianSettlement settlement) {
        
        UnitType skill = settlement.getLearnableSkill();
        if (skill == null) {
            return Message.clientError("No skill to learn at "
                                       + settlement.getName());
        }
        if (!unit.getType().canBeUpgraded(skill, ChangeType.NATIVES)) {
            return Message.clientError("Unit " + unit.toString()
                                       + " can not learn skill " + skill
                                       + " at " + settlement.getName());
        }

        
        List<Object> objects = new ArrayList<Object>();
        unit.setMovesLeft(0);
        FreeColGameObject fcgo = (FreeColGameObject) unit.getLocation();
        Tension tension = settlement.getAlarm(serverPlayer);
        switch (tension.getLevel()) {
        case HATEFUL: 
            objects.addAll(unit.disposeList());
            objects.add(fcgo);
            break;
        case ANGRY: 
            objects.add(UpdateType.PRIVATE);
            addPartial(objects, unit, "movesLeft");
            break;
        default:
            
            
            unit.setType(skill);
            if (!settlement.isCapital()) {
                settlement.setLearnableSkill(null);
            }
            Tile tile = settlement.getTile();
            tile.updateIndianSettlementInformation(serverPlayer);
            objects.add(unit);
            objects.add(UpdateType.PRIVATE);
            objects.add(tile);
            break;
        }

        
        sendToOthers(serverPlayer, objects);
        return buildUpdate(serverPlayer, objects);
    }


    
    public Element demandTribute(ServerPlayer serverPlayer, Unit unit,
                                 IndianSettlement settlement) {
        List<Object> objects = new ArrayList<Object>();

        final int TURNS_PER_TRIBUTE = 5;
        Player indianPlayer = settlement.getOwner();
        int gold = 0;
        int year = getGame().getTurn().getNumber();
        if (settlement.getLastTribute() + TURNS_PER_TRIBUTE < year
            && indianPlayer.getGold() > 0) {
            switch (indianPlayer.getTension(serverPlayer).getLevel()) {
            case HAPPY:
            case CONTENT:
                gold = Math.min(indianPlayer.getGold() / 10, 100);
                break;
            case DISPLEASED:
                gold = Math.min(indianPlayer.getGold() / 20, 100);
                break;
            case ANGRY:
            case HATEFUL:
            default:
                break; 
            }
        }

        
        
        settlement.modifyAlarm(serverPlayer, Tension.TENSION_ADD_NORMAL);
        settlement.setLastTribute(year);
        ModelMessage m;
        if (gold > 0) {
            indianPlayer.modifyGold(-gold);
            serverPlayer.modifyGold(gold);
            addPartial(objects, serverPlayer, "gold", "score");
            m = new ModelMessage(ModelMessage.MessageType.FOREIGN_DIPLOMACY,
                                 "scoutSettlement.tributeAgree",
                                 unit, settlement)
                .addAmount("%amount%", gold);
        } else {
            m = new ModelMessage(ModelMessage.MessageType.FOREIGN_DIPLOMACY,
                                 "scoutSettlement.tributeDisagree",
                                 unit, settlement);
        }
        objects.add(m);
        unit.setMovesLeft(0);
        addPartial(objects, unit, "movesLeft");

        
        return buildUpdate(serverPlayer, objects);
    }

    
    public Element scoutIndianSettlement(ServerPlayer serverPlayer,
                                         Unit unit,
                                         IndianSettlement settlement) {
        List<Object> objects = new ArrayList<Object>();
        String result;

        
        Player player = unit.getOwner();
        Tension tension = settlement.getAlarm(player);
        if (tension != null && tension.getLevel() == Tension.Level.HATEFUL) {
            objects.addAll(unit.disposeList());
            result = "die";
        } else {
            
            int gold = 0;
            Tile tile = settlement.getTile();
            int radius = unit.getLineOfSight();
            UnitType skill = settlement.getLearnableSkill();
            if (settlement.hasBeenVisited()) {
                
                result = "nothing";
            } else if (skill != null
                       && skill.hasAbility("model.ability.expertScout")
                       && unit.getType().canBeUpgraded(skill, ChangeType.NATIVES)) {
                
                
                
                unit.setType(settlement.getLearnableSkill());
                
                objects.add(unit);
                result = "expert";
            } else if (getPseudoRandom().nextInt(3) == 0) {
                
                radius = Math.max(radius, IndianSettlement.TALES_RADIUS);
                result = "tales";
            } else {
                
                gold = (getPseudoRandom().nextInt(400)
                            * settlement.getBonusMultiplier()) + 50;
                if (unit.hasAbility("model.ability.expertScout")) {
                    gold = (gold * 11) / 10;
                }
                serverPlayer.modifyGold(gold);
                settlement.getOwner().modifyGold(-gold);
                result = "beads";
            }

            
            objects.add(UpdateType.PRIVATE);
            if (gold > 0) {
                addPartial(objects, serverPlayer, "gold", "score");
            }

            
            
            settlement.setVisited(player);
            tile.updateIndianSettlementInformation(player);
            objects.add(tile);
            Map map = getFreeColServer().getGame().getMap();
            for (Tile t : map.getSurroundingTiles(tile, radius)) {
                if (!serverPlayer.canSee(t) && (t.isLand() || t.isCoast())) {
                    player.setExplored(t);
                    objects.add(t);
                }
            }

            
            unit.setMovesLeft(0);
            if (!objects.contains(unit)) {
                addPartial(objects, unit, "movesLeft");
            }
        }
        
        addAttribute(objects, "result", result);

        
        
        sendToOthers(serverPlayer, objects);
        return buildUpdate(serverPlayer, objects);
    }


    
    public Element denounceMission(ServerPlayer serverPlayer, Unit unit,
                                   IndianSettlement settlement) {
        
        Location oldLocation = unit.getLocation();
        Unit missionary = settlement.getMissionary();
        ServerPlayer enemy = (ServerPlayer) missionary.getOwner();
        double random = Math.random();
        random *= enemy.getImmigration() / (serverPlayer.getImmigration() + 1);
        if (missionary.hasAbility("model.ability.expertMissionary")) {
            random += 0.2;
        }
        if (unit.hasAbility("model.ability.expertMissionary")) {
            random -= 0.2;
        }

        if (random < 0.5) { 
            settlement.setMissionary(null);

            
            if (enemy.isConnected()) {
                List<Object> objects = new ArrayList<Object>();
                objects.addAll(missionary.disposeList());
                ModelMessage m = new ModelMessage(ModelMessage.MessageType.FOREIGN_DIPLOMACY,
                                             "indianSettlement.mission.denounced",
                                             settlement)
                            .addStringTemplate("%settlement%", settlement.getLocationName());
                objects.add(m);
                objects.add(settlement);
                sendElement(enemy, objects);
            }

            return establishMission(serverPlayer, unit, settlement);
        }

        
        List<Object> objects = new ArrayList<Object>();
        objects.addAll(unit.disposeList());
        objects.add((FreeColGameObject) oldLocation);
        objects.add(UpdateType.PRIVATE);
        objects.add(new ModelMessage(ModelMessage.MessageType.FOREIGN_DIPLOMACY,
                                     "indianSettlement.mission.noDenounce",
                                     serverPlayer, unit)
                    .addStringTemplate("%nation%", settlement.getOwner().getNationName()));

        
        sendToOthers(serverPlayer, objects);
        return buildUpdate(serverPlayer, objects);
    }

    
    public Element establishMission(ServerPlayer serverPlayer, Unit unit,
                                    IndianSettlement settlement) {
        List<Object> objects = new ArrayList<Object>();
        
        objects.add((FreeColGameObject) unit.getLocation());

        
        
        Tension tension = settlement.getAlarm(serverPlayer);
        if (tension == null) {
            tension = new Tension(0);
            settlement.setAlarm(serverPlayer, tension);
        }
        switch (tension.getLevel()) {
        case HATEFUL: case ANGRY:
            objects.addAll(unit.disposeList());
            break;
        case HAPPY: case CONTENT: case DISPLEASED:
            settlement.setMissionary(unit);
            objects.add(settlement);
        }
        objects.add(UpdateType.PRIVATE);
        String messageId = "indianSettlement.mission."
            + settlement.getAlarm(serverPlayer).toString().toLowerCase();
        objects.add(new ModelMessage(ModelMessage.MessageType.FOREIGN_DIPLOMACY,
                                     messageId, serverPlayer, unit)
                    .addStringTemplate("%nation%", settlement.getOwner().getNationName()));

        
        
        sendToOthers(serverPlayer, objects);
        return buildUpdate(serverPlayer, objects);
    }

    
    public Element incite(ServerPlayer serverPlayer, Unit unit,
                          IndianSettlement settlement, Player enemy, int gold) {
        List<Object> objects = new ArrayList<Object>();

        
        Player nativePlayer = settlement.getOwner();
        Tension payingTension = nativePlayer.getTension(serverPlayer);
        Tension targetTension = nativePlayer.getTension(enemy);
        int payingValue = (payingTension == null) ? 0 : payingTension.getValue();
        int targetValue = (targetTension == null) ? 0 : targetTension.getValue();
        int goldToPay = (payingTension != null && targetTension != null
                      && payingValue > targetValue) ? 10000 : 5000;
        goldToPay += 20 * (payingValue - targetValue);
        goldToPay = Math.max(goldToPay, 650);

        
        unit.setMovesLeft(0);
        addPartial(objects, unit, "movesLeft");
        if (gold < 0) { 
            addAttribute(objects, "gold", Integer.toString(goldToPay));
        } else if (gold < goldToPay || serverPlayer.getGold() < gold) {
            objects.add(new ModelMessage(ModelMessage.MessageType.FOREIGN_DIPLOMACY,
                                         "indianSettlement.inciteGoldFail",
                                         serverPlayer, settlement)
                        .addStringTemplate("%player%", enemy.getNationName())
                        .addAmount("%amount%", goldToPay));
            addAttribute(objects, "gold", "0");
        } else {
            
            
            serverPlayer.modifyGold(-gold);
            nativePlayer.modifyGold(gold);
            addAttribute(objects, "gold", Integer.toString(gold));
            addPartial(objects, serverPlayer, "gold");
            nativePlayer.changeRelationWithPlayer(enemy, Stance.WAR);
            settlement.modifyAlarm(enemy, 1000); 
            enemy.modifyTension(nativePlayer, 500);
            enemy.modifyTension(serverPlayer, 250);
        }

        
        return buildUpdate(serverPlayer, objects);
    }


    
    public Element setDestination(ServerPlayer serverPlayer, Unit unit,
                                  Location destination) {
        if (unit.getTradeRoute() != null) unit.setTradeRoute(null);
        unit.setDestination(destination);

        
        return buildUpdate(serverPlayer, unit);
    }


    
    private boolean hasWorkAtStop(Unit unit, Stop stop) {
        ArrayList<GoodsType> stopGoods = stop.getCargo();
        int cargoSize = stopGoods.size();
        for (Goods goods : unit.getGoodsList()) {
            GoodsType type = goods.getType();
            if (stopGoods.contains(type)) {
                if (unit.getLoadableAmount(type) > 0) {
                    
                    
                    
                    Location loc = stop.getLocation();
                    if (loc instanceof Colony) {
                        if (((Colony) loc).getExportAmount(type) > 0) {
                            return true;
                        }
                    } else if (loc instanceof Europe) {
                        return true;
                    }
                } else {
                    cargoSize--; 
                }
            } else {
                return true; 
            }
        }

        
        return unit.getSpaceLeft() > 0 && cargoSize > 0;
    }

    
    public Element updateCurrentStop(ServerPlayer serverPlayer, Unit unit) {
        List<Object> objects = new ArrayList<Object>();

        
        int current = unit.validateCurrentStop();
        if (current < 0) return null; 

        ArrayList<Stop> stops = unit.getTradeRoute().getStops();
        int next = current;
        for (;;) {
            if (++next >= stops.size()) next = 0;
            if (next == current) break;
            if (hasWorkAtStop(unit, stops.get(next))) break;
        }

        
        
        
        unit.setCurrentStop(next);
        unit.setDestination(stops.get(next).getLocation());

        
        return buildUpdate(serverPlayer, unit);
    }

    
    public void moveGoods(Goods goods, Location loc)
        throws IllegalStateException {
        Location oldLoc = goods.getLocation();
        if (oldLoc == null) {
            throw new IllegalStateException("Goods in null location.");
        } else if (loc == null) {
            ; 
        } else if (loc instanceof Unit) {
            if (((Unit) loc).isInEurope()) {
                if (!(oldLoc instanceof Unit && ((Unit) oldLoc).isInEurope())) {
                    throw new IllegalStateException("Goods and carrier not both in Europe.");
                }
            } else if (loc.getTile() == null) {
                throw new IllegalStateException("Carrier not on the map.");
            } else if (oldLoc instanceof IndianSettlement) {
                
            } else if (loc.getTile() != oldLoc.getTile()) {
                throw new IllegalStateException("Goods and carrier not co-located.");
            }
        } else if (loc instanceof IndianSettlement) {
            
        } else if (loc instanceof Colony) {
            if (oldLoc instanceof Unit
                && ((Unit) oldLoc).getOwner() != ((Colony) loc).getOwner()) {
                
            } else if (loc.getTile() != oldLoc.getTile()) {
                throw new IllegalStateException("Goods and carrier not both in Colony.");
            }
        } else if (loc.getGoodsContainer() == null) {
            throw new IllegalStateException("New location with null GoodsContainer.");
        }

        oldLoc.remove(goods);
        goods.setLocation(null);

        if (loc != null) {
            loc.add(goods);
            goods.setLocation(loc);
        }
    }

    
    public Element buyFromSettlement(ServerPlayer serverPlayer, Unit unit,
                                     IndianSettlement settlement,
                                     Goods goods, int amount) {
        if (!isTransactionSessionOpen(unit, settlement)) {
            return Message.clientError("Trying to buy without opening a transaction session");
        }
        java.util.Map<String,Object> session
            = getTransactionSession(unit, settlement);
        if (!(Boolean) session.get("canBuy")) {
            return Message.clientError("Trying to buy in a session where buying is not allowed.");
        }
        if (unit.getSpaceLeft() <= 0) {
            return Message.clientError("Unit is full, unable to buy.");
        }
        
        AIPlayer ai = (AIPlayer) getFreeColServer().getAIMain().getAIObject(settlement.getOwner());
        int returnGold = ai.buyProposition(unit, settlement, goods, amount);
        if (returnGold != amount) {
            return Message.clientError("This was not the price we agreed upon! Cheater?");
        }
        
        if (serverPlayer.getGold() < amount) {
            return Message.clientError("Insufficient gold to buy.");
        }

        
        List<Object> objects = new ArrayList<Object>();
        moveGoods(goods, unit);
        objects.add(unit);

        Player settlementPlayer = settlement.getOwner();
        settlement.updateWantedGoods();
        settlement.getTile().updateIndianSettlementInformation(serverPlayer);
        settlement.modifyAlarm(serverPlayer, -amount / 50);
        settlementPlayer.modifyGold(amount);
        serverPlayer.modifyGold(-amount);
        objects.add(UpdateType.PRIVATE);
        objects.add(settlement.getTile());
        addPartial(objects, serverPlayer, "gold");
        session.put("actionTaken", true);
        session.put("canBuy", false);

        
        sendToOthers(serverPlayer, objects);
        return buildUpdate(serverPlayer, objects);
    }

    
    public Element sellToSettlement(ServerPlayer serverPlayer, Unit unit,
                                    IndianSettlement settlement,
                                    Goods goods, int amount) {
        if (!isTransactionSessionOpen(unit, settlement)) {
            return Message.clientError("Trying to sell without opening a transaction session");
        }
        java.util.Map<String,Object> session
            = getTransactionSession(unit, settlement);
        if (!(Boolean) session.get("canSell")) {
            return Message.clientError("Trying to sell in a session where selling is not allowed.");
        }

        
        AIPlayer ai = (AIPlayer) getFreeColServer().getAIMain().getAIObject(settlement.getOwner());
        int returnGold = ai.sellProposition(unit, settlement, goods, amount);
        if (returnGold != amount) {
            return Message.clientError("This was not the price we agreed upon! Cheater?");
        }

        
        List<Object> objects = new ArrayList<Object>();
        moveGoods(goods, settlement);
        objects.add(unit);

        Player settlementPlayer = settlement.getOwner();
        settlementPlayer.modifyGold(-amount);
        settlement.modifyAlarm(serverPlayer, -settlement.getPrice(goods) / 500);
        serverPlayer.modifyGold(amount);
        settlement.updateWantedGoods();
        settlement.getTile().updateIndianSettlementInformation(serverPlayer);
        objects.add(UpdateType.PRIVATE);
        objects.add(settlement.getTile());
        addPartial(objects, serverPlayer, "gold");
        session.put("actionTaken", true);
        session.put("canSell", false);

        
        sendToOthers(serverPlayer, objects);
        return buildUpdate(serverPlayer, objects);
    }

    
    public Element deliverGiftToSettlement(ServerPlayer serverPlayer,
                                           Unit unit, Settlement settlement,
                                           Goods goods) {
        if (!isTransactionSessionOpen(unit, settlement)) {
            return Message.clientError("Trying to deliverGift without opening a transaction session");
        }

        List<Object> objects = new ArrayList<Object>();
        java.util.Map<String,Object> session
            = getTransactionSession(unit, settlement);
        Tile tile = settlement.getTile();
        moveGoods(goods, settlement);
        objects.add(unit);
        if (settlement instanceof IndianSettlement) {
            IndianSettlement indianSettlement = (IndianSettlement) settlement;
            indianSettlement.modifyAlarm(serverPlayer, -indianSettlement.getPrice(goods) / 50);
            indianSettlement.updateWantedGoods();
            tile.updateIndianSettlementInformation(serverPlayer);
            objects.add(UpdateType.PRIVATE);
            objects.add(tile);
        }
        session.put("actionTaken", true);
        session.put("canGift", false);

        
        ServerPlayer receiver = (ServerPlayer) settlement.getOwner();
        if (!receiver.isAI() && receiver.isConnected()
            && settlement instanceof Colony) {
            List<Object> giftObjects = new ArrayList<Object>();
            giftObjects.add(unit);
            giftObjects.add(settlement);
            giftObjects.add(new ModelMessage(ModelMessage.MessageType.GIFT_GOODS,
                                             "model.unit.gift", settlement, goods.getType())
                            .addStringTemplate("%player%", serverPlayer.getNationName())
                            .add("%type%", goods.getNameKey())
                            .addAmount("%amount%", goods.getAmount())
                            .addName("%colony%", settlement.getName()));
            sendElement(receiver, giftObjects);
        }

        
        sendToOthers(serverPlayer, objects);
        return buildUpdate(serverPlayer, objects);
    }


    
    public Element loadCargo(ServerPlayer serverPlayer, Unit unit,
                             Goods goods) {
        goods.adjustAmount();
        moveGoods(goods, unit);
        if (unit.getInitialMovesLeft() != unit.getMovesLeft()) {
            unit.setMovesLeft(0);
        }

        
        
        
        FreeColGameObject fcgo = (FreeColGameObject) unit.getLocation();
        sendToOthers(serverPlayer, fcgo);
        return buildUpdate(serverPlayer, fcgo);
    }

    
    public Element unloadCargo(ServerPlayer serverPlayer, Unit unit,
                               Goods goods) {
        FreeColGameObject update;
        Location loc;
        if (unit.isInEurope()) { 
            loc = null;
            update = unit;
        } else if (unit.getTile() == null) {
            return Message.clientError("Unit not on the map.");
        } else if (unit.getTile().getSettlement() instanceof Colony) {
            loc = unit.getTile().getSettlement();
            update = unit.getTile();
        } else { 
            loc = null;
            update = unit;
        }
        goods.adjustAmount();
        moveGoods(goods, loc);
        if (unit.getInitialMovesLeft() != unit.getMovesLeft()) {
            unit.setMovesLeft(0);
        }

        
        if (update == unit) sendToOthers(serverPlayer, update);
        return buildUpdate(serverPlayer, update);
    }


    
    public void clearSpeciality(Unit unit, ServerPlayer serverPlayer) {
        UnitType newType = unit.getType().getUnitTypeChange(ChangeType.CLEAR_SKILL,
                                                            serverPlayer);
        if (newType == null) {
            throw new IllegalStateException("Can not clear this unit speciality: " + unit.getId());
        }
        
        
        
        Location oldLocation = unit.getLocation();
        if (oldLocation instanceof Building
            && !((Building) oldLocation).canAdd(newType)) {
            throw new IllegalStateException("Cannot clear speciality, building does not allow new unit type");
        }

        unit.setType(newType);
        if (oldLocation instanceof Tile) {
            sendToOthers(serverPlayer, (Tile) oldLocation);
        }
    }


    
    private UnitType generateSkillForLocation(Map map, Tile tile,
                                              IndianNationType nationType) {
        List<RandomChoice<UnitType>> skills = nationType.getSkills();
        java.util.Map<GoodsType, Integer> scale
            = new HashMap<GoodsType, Integer>();

        for (RandomChoice<UnitType> skill : skills) {
            scale.put(skill.getObject().getExpertProduction(), 1);
        }

        Iterator<Position> iter = map.getAdjacentIterator(tile.getPosition());
        while (iter.hasNext()) {
            Map.Position p = iter.next();
            Tile t = map.getTile(p);
            for (GoodsType goodsType : scale.keySet()) {
                scale.put(goodsType, scale.get(goodsType).intValue()
                          + t.potential(goodsType, null));
            }
        }

        List<RandomChoice<UnitType>> scaledSkills
            = new ArrayList<RandomChoice<UnitType>>();
        for (RandomChoice<UnitType> skill : skills) {
            UnitType unitType = skill.getObject();
            int scaleValue = scale.get(unitType.getExpertProduction()).intValue();
            scaledSkills.add(new RandomChoice<UnitType>(unitType, skill.getProbability() * scaleValue));
        }

        PseudoRandom prng = getPseudoRandom();
        UnitType skill = RandomChoice.getWeightedRandom(prng, scaledSkills);
        if (skill == null) {
            
            Specification spec = FreeCol.getSpecification();
            List<UnitType> unitList
                = spec.getUnitTypesWithAbility("model.ability.expertScout");
            return unitList.get(prng.nextInt(unitList.size()));
        }
        return skill;
    }

    
    public Element buildSettlement(ServerPlayer serverPlayer, Unit unit,
                                   String name) {
        Game game = serverPlayer.getGame();
        Tile tile = unit.getTile();
        Settlement settlement;
        if (serverPlayer.isEuropean()) {
            settlement = new Colony(game, serverPlayer, name, tile);
        } else {
            IndianNationType nationType
                = (IndianNationType) serverPlayer.getNationType();
            UnitType skill = generateSkillForLocation(game.getMap(), tile,
                                                      nationType);
            settlement = new IndianSettlement(game, serverPlayer, tile,
                                              name, false, skill,
                                              new HashSet<Player>(), null);
            
        }
        settlement.placeSettlement();

        
        unit.setState(UnitState.IN_COLONY);
        unit.setLocation(settlement);
        unit.setMovesLeft(0);

        
        List<Object> objects = new ArrayList<Object>();
        objects.add(tile);
        Map map = game.getMap();
        for (Tile t : map.getSurroundingTiles(tile, settlement.getRadius())) {
            if (t.getOwningSettlement() == settlement) objects.add(t);
        }

        
        objects.add(UpdateType.PRIVATE);
        objects.add(new HistoryEvent(game.getTurn().getNumber(),
                                     HistoryEvent.EventType.FOUND_COLONY)
                    .addName("%colony%", settlement.getName()));
        
        
        for (Tile t : map.getSurroundingTiles(tile, unit.getLineOfSight() + 1,
                                              settlement.getLineOfSight())) {
            if (!objects.contains(t)) objects.add(t);
        }

        
        sendToOthers(serverPlayer, objects);
        return buildUpdate(serverPlayer, objects);
    }

    
    public Element joinColony(ServerPlayer serverPlayer, Unit unit,
                              Colony colony) {
        List<Tile> ownedTiles = colony.getOwnedTiles();
        Tile tile = colony.getTile();

        
        unit.setState(UnitState.IN_COLONY);
        unit.setLocation(colony);
        unit.setMovesLeft(0);

        
        List<Object> objects = new ArrayList<Object>();
        objects.add(tile);
        Map map = serverPlayer.getGame().getMap();
        for (Tile t : map.getSurroundingTiles(tile, colony.getRadius())) {
            if (t.getOwningSettlement() == colony && !ownedTiles.contains(t)) {
                objects.add(t);
            }
        }

        
        sendToOthers(serverPlayer, objects);
        return buildUpdate(serverPlayer, objects);
    }


    
    public Element abandonSettlement(ServerPlayer serverPlayer,
                                     Settlement settlement) {
        List<Object> objects = new ArrayList<Object>();
        
        objects.addAll(settlement.getOwnedTiles());

        HistoryEvent h = null;
        
        if (settlement instanceof Colony) {
            h = new HistoryEvent(getGame().getTurn().getNumber(),
                                 HistoryEvent.EventType.ABANDON_COLONY)
                .addName("%colony%", settlement.getName());
        }

        
        objects.addAll(settlement.disposeList());

        if (h != null) { 
            objects.add(UpdateType.PRIVATE);
            objects.add(h);
        }

        
        sendToOthers(serverPlayer, objects);
        return buildUpdate(serverPlayer, objects);
    }

}
