

package net.sf.freecol.server.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.Specification;
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
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.LostCityRumour;
import net.sf.freecol.common.model.LostCityRumour.RumourType;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Map.Position;
import net.sf.freecol.common.model.Market;
import net.sf.freecol.common.model.ModelController;
import net.sf.freecol.common.model.ModelMessage;
import net.sf.freecol.common.model.Modifier;
import net.sf.freecol.common.model.Monarch;
import net.sf.freecol.common.model.Nation;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Player.PlayerType;
import net.sf.freecol.common.model.Player.Stance;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tension;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TradeRoute.Stop;
import net.sf.freecol.common.model.Monarch.MonarchAction;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.UnitState;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.model.UnitTypeChange;
import net.sf.freecol.common.model.UnitTypeChange.ChangeType;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.common.util.RandomChoice;
import net.sf.freecol.server.FreeColServer;
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

    
    public void sendRemoveUnitToAll(Unit unit, ServerPlayer serverPlayer) {
        Element remove = Message.createNewRootElement("remove");
        unit.addToRemoveElement(remove);
        for (ServerPlayer enemyPlayer : getOtherPlayers(serverPlayer)) {
            if (unit.isVisibleTo(enemyPlayer)) {
                try {
                    enemyPlayer.getConnection().sendAndWait(remove);
                } catch (IOException e) {
                    logger.warning(e.getMessage());
                }
            }
        }
    }

    
    public void sendUpdateToAll(FreeColGameObject obj, ServerPlayer serverPlayer) {
        for (ServerPlayer enemyPlayer : getOtherPlayers(serverPlayer)) {
            Element update = Message.createNewRootElement("update");
            Document doc = update.getOwnerDocument();
            update.appendChild(obj.toXMLElement(enemyPlayer, doc));
            try {
                enemyPlayer.getConnection().sendAndWait(update);
            } catch (IOException e) {
                logger.warning(e.getMessage());
            }
        }
    }

    
    public void sendUpdatedTileToAll(Tile newTile, ServerPlayer serverPlayer) {
        for (ServerPlayer enemyPlayer : getOtherPlayers(serverPlayer)) {
            if (enemyPlayer.canSee(newTile)) {
                Element update = Message.createNewRootElement("update");
                Document doc = update.getOwnerDocument();
                update.appendChild(newTile.toXMLElement(enemyPlayer, doc));
                try {
                    enemyPlayer.getConnection().sendAndWait(update);
                } catch (IOException e) {
                    logger.warning(e.getMessage());
                }
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
            freeColServer.getServer().sendToAll(gameEndedElement, null);
            
            
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
            removeType = goodsTypes.get(getPseudoRandom().nextInt(goodsTypes.size()));
        } while (!removeType.isStorable());

        
        for (GoodsType type : goodsTypes) {
            if (type.isStorable()) {
                int amount = 10;
                if (type == removeType) {
                    amount += getPseudoRandom().nextInt(21);
                }
                market.addGoodsToMarket(type, -amount);
                if (market.hasPriceChanged(type)) {
                    messages.add(market.makePriceChangeMessage(type));
                    market.flushPriceChange(type);
                }
            }
        }

        
        Element element;
        if (messages.isEmpty()) {
            element = Message.createNewRootElement("update");
            Document doc = element.getOwnerDocument();
            element.appendChild(market.toXMLElement(player, doc));
        } else {
            element = Message.createNewRootElement("multiple");
            Document doc = element.getOwnerDocument();
            Element update = doc.createElement("update");
            element.appendChild(update);
            update.appendChild(market.toXMLElement(player, doc));
            Element mess = doc.createElement("addMessages");
            element.appendChild(mess);
            for (ModelMessage m : messages) {
                mess.appendChild(m.toXMLElement(player, doc));
            }
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
        ModelMessage m = new ModelMessage(serverPlayer,
                                          ModelMessage.MessageType.FOREIGN_DIPLOMACY,
                                          serverPlayer,
                                          ((serverPlayer.isEuropean()) ? "model.diplomacy.dead.european" : "model.diplomacy.dead.native"),
                                          "%nation%", serverPlayer.getNationAsString());
        messages.appendChild(m.toXMLElement(doc));

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
            freeColServer.getServer().sendToAll(newTurnElement, null);
        }
        
        ServerPlayer newPlayer = (ServerPlayer) getGame().getNextPlayer();
        getGame().setCurrentPlayer(newPlayer);
        if (newPlayer == null) {
            getGame().setCurrentPlayer(null);
            return null;
        }
        
        synchronized (newPlayer) {
            if (Player.checkForDeath(newPlayer)) {
                Element element = killPlayerElement(newPlayer);
                freeColServer.getServer().sendToAll(element, null);
                logger.info(newPlayer.getNationAsString() + " is dead.");
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
        freeColServer.getServer().sendToAll(setCurrentPlayerElement, null);
        
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
                colony.setOwner(strongestAIPlayer);
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
                logMessage += ":" + father.getName();
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
                            monarchActionElement.setAttribute("goods", goods.getName());
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

    public ServerPlayer createREFPlayer(ServerPlayer player){
        Nation refNation = player.getNation().getRefNation();
        ServerPlayer refPlayer = getFreeColServer().addAIPlayer(refNation);
        refPlayer.setEntryLocation(player.getEntryLocation());
        
        player.setStance(refPlayer, Stance.PEACE);
        refPlayer.setTension(player, new Tension(Tension.Level.CONTENT.getLimit()));
        player.setTension(refPlayer, new Tension(Tension.Level.CONTENT.getLimit()));
        
        return refPlayer;
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

    private void bombardEnemyShips(ServerPlayer currentPlayer) {
        logger.finest("Entering method bombardEnemyShips.");
        Map map = getFreeColServer().getGame().getMap();
        CombatModel combatModel = getFreeColServer().getGame().getCombatModel();
        for (Settlement settlement : currentPlayer.getSettlements()) {
            Colony colony = (Colony) settlement;
            if (colony.canBombardEnemyShip()){
                logger.fine("Colony " + colony.getName() + " can bombard enemy ships.");
                Position colonyPosition = colony.getTile().getPosition();
                for (Direction direction : Direction.values()) {
                    Tile tile = map.getTile(Map.getAdjacent(colonyPosition, direction));
                    
                    
                    if(tile == null || tile.isLand()){
                        continue;
                    }
                    
                    
                    
                    List<Unit> unitList = new ArrayList<Unit>(tile.getUnitList());
                    Iterator<Unit> unitIterator = unitList.iterator();
                    while (unitIterator.hasNext()) {
                        Unit unit = unitIterator.next();
                        Player player = unit.getOwner();
                    
                        
                        if(player == currentPlayer){
                                continue;
                        }
                        
                        
                        if(currentPlayer.getStance(player) != Stance.WAR &&
                                                !unit.hasAbility("model.ability.piracy")){
                                continue;
                        }

                        logger.info(colony.getName() + " found enemy unit to bombard: " + unit.getName() + "(" + unit.getOwner().getNationAsString() + ")");
                        
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

                                if (
                                                enemyPlayer.getConnection() == null) {
                                        continue;
                                }
                                
                                
                                if(!unit.isVisibleTo(enemyPlayer)){
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
                                
                                
                                if (enemyPlayer.canSee(unit.getTile())) { 
                            opponentAttackElement.setAttribute("update", "unit");
                            opponentAttackElement.appendChild(unit.toXMLElement(enemyPlayer, opponentAttackElement.getOwnerDocument()));
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
    }
    
    public java.util.Map<String,Object> getTransactionSession(Unit unit, Settlement settlement){
        java.util.Map<String, java.util.Map<String,Object>> unitTransactions = null;

        if(transactionSessions.containsKey(unit.getId())){
            unitTransactions = transactionSessions.get(unit.getId());
            if(unitTransactions.containsKey(settlement.getId())){
                return unitTransactions.get(settlement.getId());
            }
        }
        
        java.util.Map<String,Object> session = new HashMap<String,Object>();
        
        session.put("canGift", true);
        session.put("canSell", true);
        session.put("canBuy", true);
        session.put("actionTaken", false);
        session.put("hasSpaceLeft", unit.getSpaceLeft() != 0);
        session.put("unitMoves", unit.getMovesLeft());
        if(settlement.getOwner().getStance(unit.getOwner()) == Stance.WAR){
            session.put("canSell", false);
            session.put("canBuy", false);
        }
        else{
        	
            if(unit.getSpaceTaken() == 0){
                session.put("canSell", false);
            }
        }
        
        if(unit.getOwner().isAI()){
            return session;
        }
        
        
        
        
        if(unitTransactions == null){
            unitTransactions = new HashMap<String,java.util.Map<String, Object>>();
            transactionSessions.put(unit.getId(), unitTransactions);
        }
        unitTransactions.put(settlement.getId(), session);
        return session;
    }

    public void closeTransactionSession(Unit unit, Settlement settlement){
        java.util.Map<String, java.util.Map<String,Object>> unitTransactions;
        
        
        if(unit.getOwner().isAI()){
          return;  
        }
        
        if(!transactionSessions.containsKey(unit.getId())){
            throw new IllegalStateException("Trying to close a non-existing session");
        }
        
        unitTransactions = transactionSessions.get(unit.getId());   
        if(!unitTransactions.containsKey(settlement.getId())){
            throw new IllegalStateException("Trying to close a non-existing session");
        }
        
        unitTransactions.remove(settlement.getId());
        if(unitTransactions.isEmpty()){
            transactionSessions.remove(unit.getId());
        }
    }
    
    public boolean isTransactionSessionOpen(Unit unit, Settlement settlement){
        
        if(unit.getOwner().isAI()){
            return true;
        }
        
        if(!transactionSessions.containsKey(unit.getId())){
            return false;
        }
        if(settlement != null &&
           !transactionSessions.get(unit.getId()).containsKey(settlement.getId())){
                return false;
        }
        return true;
    }

    
    public ModelMessage emigrate(ServerPlayer player, int slot, boolean fountain) {
        
        
        
        boolean validSlot = 1 <= slot && slot <= Europe.RECRUIT_COUNT;
        int index = (validSlot) ? slot-1
            : getPseudoRandom().nextInt(Europe.RECRUIT_COUNT);

        
        Europe europe = player.getEurope();
        UnitType recruitType = europe.getRecruitable(index);
        Game game = getGame();
        Unit unit = new Unit(game, europe, player, recruitType, UnitState.ACTIVE,
                             recruitType.getDefaultEquipment());
        unit.setLocation(europe);

        
        if (!fountain) {
            player.updateImmigrationRequired();
            player.reduceImmigration();
        }

        
        
        
        String taskId = player.getId()
            + ".emigrate." + game.getTurn().toString()
            + ".slot." + Integer.toString(slot)
            + "." + Integer.toString(getPseudoRandom().nextInt(1000000));
        europe.setRecruitable(index, player.generateRecruitable(taskId));

        
        
        
        return (fountain || validSlot) ? null
            : new ModelMessage(player, ModelMessage.MessageType.UNIT_ADDED,
                               unit, "model.europe.emigrate",
                               "%europe%", europe.getName(),
                               "%unit%", unit.getName());
    }


    
    public Unit getSlowedBy(Unit unit, Tile newTile) {
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

    
    public List<FreeColObject> exploreLostCityRumour(ServerPlayer serverPlayer,
                                                     Unit unit) {
        List<FreeColObject> result = new ArrayList<FreeColObject>();
        Tile tile = unit.getTile();
        LostCityRumour lostCity = tile.getLostCityRumour();
        if (lostCity == null) return result;

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
            result.add(indianPlayer);
            result.add(new ModelMessage(serverPlayer,
                                        ModelMessage.MessageType.LOST_CITY_RUMOUR,
                                        unit,
                                        "lostCityRumour.BurialGround",
                                        "%nation%", indianPlayer.getNationAsString()));
            break;
        case EXPEDITION_VANISHES:
            unit.dispose();
            result.add(new ModelMessage(serverPlayer,
                                        ModelMessage.MessageType.LOST_CITY_RUMOUR,
                                        null,
                                        "lostCityRumour.ExpeditionVanishes"));
            break;
        case NOTHING:
            result.add(new ModelMessage(serverPlayer,
                                        ModelMessage.MessageType.LOST_CITY_RUMOUR,
                                        unit,
                                        "lostCityRumour.Nothing"));
            break;
        case LEARN:
            List<UnitType> learntUnitTypes = unit.getType().getUnitTypesLearntInLostCity();
            String oldName = unit.getName();
            unit.setType(learntUnitTypes.get(getPseudoRandom().nextInt(learntUnitTypes.size())));
            result.add(new ModelMessage(serverPlayer,
                                        ModelMessage.MessageType.LOST_CITY_RUMOUR,
                                        unit,
                                        "lostCityRumour.Learn",
                                        "%unit%", oldName,
                                        "%type%", unit.getType().getName()));
            break;
        case TRIBAL_CHIEF:
            int chiefAmount = getPseudoRandom().nextInt(dx * 10) + dx * 5;
            serverPlayer.modifyGold(chiefAmount);
            result.add(serverPlayer);
            result.add(new ModelMessage(serverPlayer,
                                        ModelMessage.MessageType.LOST_CITY_RUMOUR,
                                        unit,
                                        "lostCityRumour.TribalChief",
                                        "%money%", Integer.toString(chiefAmount)));
            break;
        case COLONIST:
            List<UnitType> newUnitTypes = specification.getUnitTypesWithAbility("model.ability.foundInLostCity");
            newUnit = new Unit(game, tile, serverPlayer,
                               newUnitTypes.get(getPseudoRandom().nextInt(newUnitTypes.size())),
                               UnitState.ACTIVE);
            result.add(new ModelMessage(serverPlayer,
                                        ModelMessage.MessageType.LOST_CITY_RUMOUR,
                                        newUnit,
                                        "lostCityRumour.Colonist"));
            break;
        case CIBOLA:
            String cityName = game.getCityOfCibola();
            if (cityName != null) {
                int treasureAmount = getPseudoRandom().nextInt(dx * 600) + dx * 300;
                String treasureString = String.valueOf(treasureAmount);
                if (treasureUnitTypes == null) {
                    treasureUnitTypes = specification.getUnitTypesWithAbility("model.ability.carryTreasure");
                }
                unitType = treasureUnitTypes.get(getPseudoRandom().nextInt(treasureUnitTypes.size()));
                newUnit = new Unit(game, tile, serverPlayer, unitType, UnitState.ACTIVE);
                newUnit.setTreasureAmount(treasureAmount);
                result.add(new ModelMessage(serverPlayer,
                                            ModelMessage.MessageType.LOST_CITY_RUMOUR,
                                            newUnit,
                                            "lostCityRumour.Cibola",
                                            "%city%", cityName,
                                            "%money%", treasureString));
                result.add(new HistoryEvent(game.getTurn().getNumber(),
                                            HistoryEvent.Type.CITY_OF_GOLD,
                                            "%city%", cityName,
                                            "%treasure%", treasureString));
                break;
            }
            
        case RUINS:
            int ruinsAmount = getPseudoRandom().nextInt(dx * 2) * 300 + 50;
            String ruinsString = String.valueOf(ruinsAmount);
            if (ruinsAmount < 500) { 
                serverPlayer.modifyGold(ruinsAmount);
                result.add(serverPlayer);
            } else {
                if (treasureUnitTypes == null) {
                    treasureUnitTypes = specification.getUnitTypesWithAbility("model.ability.carryTreasure");
                }
                unitType = treasureUnitTypes.get(getPseudoRandom().nextInt(treasureUnitTypes.size()));
                newUnit = new Unit(game, tile, serverPlayer, unitType, UnitState.ACTIVE);
                newUnit.setTreasureAmount(ruinsAmount);
            }
            result.add(new ModelMessage(serverPlayer,
                                        ModelMessage.MessageType.LOST_CITY_RUMOUR,
                                        ((newUnit != null) ? newUnit : unit),
                                        "lostCityRumour.Ruins",
                                        "%money%", ruinsString));
            break;
        case FOUNTAIN_OF_YOUTH:
            Europe europe = serverPlayer.getEurope();
            if (europe == null) {
                result.add(new ModelMessage(serverPlayer,
                                            ModelMessage.MessageType.LOST_CITY_RUMOUR,
                                            unit,
                                            "lostCityRumour.FountainOfYouthWithoutEurope"));
            } else {
                if (serverPlayer.hasAbility("model.ability.selectRecruit")
                    && !serverPlayer.isAI() 
                    ) {
                    
                    serverPlayer.setRemainingEmigrants(dx);
                } else {
                    for (int k = 0; k < dx; k++) {
                        new Unit(game, europe, serverPlayer, serverPlayer.generateRecruitable(serverPlayer.getId() + "fountain." + Integer.toString(k)),
                                 UnitState.ACTIVE);
                    }
                    result.add(europe);
                }
                result.add(new ModelMessage(serverPlayer,
                                            ModelMessage.MessageType.LOST_CITY_RUMOUR,
                                            unit,
                                            "lostCityRumour.FountainOfYouth"));
            }
            break;
        case NO_SUCH_RUMOUR:
        default:
            throw new IllegalStateException("No such rumour.");
        }
        tile.removeLostCityRumour();
        result.add(tile);
        return result;
    }

    
    public List<ServerPlayer> findAdjacentUncontacted(ServerPlayer serverPlayer,
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

    
    public List<FreeColObject> move(ServerPlayer serverPlayer, Unit unit,
                                    Tile newTile) {
        unit.setState(UnitState.ACTIVE);
        unit.setStateToAllChildren(UnitState.SENTRY);
        unit.setMovesLeft(unit.getMovesLeft() - unit.getMoveCost(newTile));
        unit.setLocation(newTile);
        unit.activeAdjacentSentryUnits(newTile);

        
        unit.setAlreadyOnHighSea(newTile.canMoveToEurope());

        
        List<FreeColObject> objects
            = (newTile.hasLostCityRumour() && serverPlayer.isEuropean())
            ? exploreLostCityRumour(serverPlayer, unit)
            : new ArrayList<FreeColObject>();

        
        
        
        
        
        if (!unit.isDisposed()) {
            List<ServerPlayer> contacts = findAdjacentUncontacted(serverPlayer, newTile);
            for (ServerPlayer other : contacts) {
                serverPlayer.setContacted(other);
                other.setContacted(serverPlayer);
                objects.add(other);
            }

            
            
            if (serverPlayer.isEuropean()) {
                for (Tile t : getGame().getMap().getSurroundingTiles(newTile, 1)) {
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
            }
        }

        return objects;
    }

    
    public int demandTribute(Player player, IndianSettlement settlement) {
        final int TURNS_PER_TRIBUTE = 5;
        Player indianPlayer = settlement.getOwner();
        int gold = 0;
        int year = getGame().getTurn().getNumber();
        if (settlement.getLastTribute() + TURNS_PER_TRIBUTE < year) {
            switch (indianPlayer.getTension(player).getLevel()) {
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

        
        
        settlement.modifyAlarm(player, Tension.TENSION_ADD_NORMAL);
        settlement.setLastTribute(year);
        indianPlayer.modifyGold(-gold);
        player.modifyGold(gold);
        return gold;
    }


    
    public boolean embarkUnit(ServerPlayer serverPlayer, Unit unit,
                              Unit carrier) {
        if (unit.isNaval() || carrier.getSpaceLeft() < unit.getSpaceTaken()) {
            return false;
        }

        Location sourceLocation = unit.getLocation();
        unit.setLocation(carrier);
        unit.setMovesLeft(0); 
        unit.setState(UnitState.SENTRY);

        
        if (sourceLocation instanceof Tile) {
            sendRemoveUnitToAll(unit, serverPlayer);
        }
        return true;
    }

    
    public boolean disembarkUnit(ServerPlayer serverPlayer, Unit unit) {
        if (unit.isNaval() || !(unit.getLocation() instanceof Unit)) {
            return false;
        }

        Unit carrier = (Unit) unit.getLocation();
        Location destination = carrier.getLocation();
        unit.setLocation(destination);
        unit.setMovesLeft(0); 
        unit.setState(UnitState.ACTIVE);

        
        if (!(destination instanceof Europe)) {
            sendUpdatedTileToAll(destination.getTile(), serverPlayer);
        }
        return true;
    }

    
    public void learnFromIndianSettlement(Unit unit,
                                          IndianSettlement settlement) {
        Player player = unit.getOwner();
        
        if (!settlement.allowContact(unit)) {
            throw new IllegalStateException("Contact denied at "
                                            + settlement.getName());
        }
        UnitType skill = settlement.getLearnableSkill();
        if (skill == null) {
            throw new IllegalStateException("No skill to learn at "
                                            + settlement.getName());
        }
        if (!unit.getType().canBeUpgraded(skill, ChangeType.NATIVES)) {
            throw new IllegalStateException("Unit " + unit.getName()
                                            + " can not learn skill " + skill.getName()
                                            + " at " + settlement.getName());
        }

        
        unit.setType(skill);
        unit.setMovesLeft(0);
        if (!settlement.isCapital()) {
            settlement.setLearnableSkill(null);
        }

        
        
        settlement.setVisited(player);
        settlement.getTile().updateIndianSettlementInformation(player);
    }

    
    public String scoutIndianSettlement(Unit unit,
                                        IndianSettlement settlement) {
        if (!settlement.allowContact(unit)) {
            throw new IllegalStateException("Contact denied at "
                                            + settlement.getName());
        }

        
        Player player = unit.getOwner();
        Tension tension = settlement.getAlarm(player);
        if (tension != null && tension.getLevel() == Tension.Level.HATEFUL) {
            unit.dispose();
            return "die";
        }

        
        String result;
        Tile tile = settlement.getTile();
        UnitType skill = settlement.getLearnableSkill();
        if (settlement.hasBeenVisited()) {
            
            result = "nothing";
        } else if (skill != null
                   && skill.hasAbility("model.ability.expertScout")
                   && unit.getType().canBeUpgraded(skill, ChangeType.NATIVES)) {
            
            
            
            unit.setType(settlement.getLearnableSkill());
            
            result = "expert";
        } else if (getPseudoRandom().nextInt(3) == 0) {
            
            Map map = getFreeColServer().getGame().getMap();
            for (Tile t : map.getSurroundingTiles(tile, IndianSettlement.TALES_RADIUS)) {
                if (t.isLand() || t.isCoast()) {
                    player.setExplored(t);
                }
            }
            result = "tales";
        } else {
            
            int gold = (getPseudoRandom().nextInt(400)
                        * settlement.getBonusMultiplier()) + 50;
            if (unit.hasAbility("model.ability.expertScout")) {
                gold = (gold * 11) / 10;
            }
            player.modifyGold(gold);
            settlement.getOwner().modifyGold(-gold);
            result = "beads";
        }

        
        settlement.setVisited(player);
        tile.updateIndianSettlementInformation(player);
        unit.setMovesLeft(0);
        return result;
    }

    
    public ModelMessage denounceMission(IndianSettlement settlement, Unit unit) {
        
        if (!settlement.allowContact(unit)) {
            throw new IllegalArgumentException("Contact denied at "
                                               + settlement.getName());
        }

        
        Player player = unit.getOwner();
        Unit missionary = settlement.getMissionary();
        Player enemy = missionary.getOwner();
        double random = Math.random();
        random *= enemy.getImmigration() / (player.getImmigration() + 1);
        if (missionary.hasAbility("model.ability.expertMissionary")) {
            random += 0.2;
        }
        if (unit.hasAbility("model.ability.expertMissionary")) {
            random -= 0.2;
        }

        if (random < 0.5) { 
            settlement.setMissionary(null);
            
            return establishMission(settlement, unit);
        }

        
        unit.dispose();
        return new ModelMessage(player, ModelMessage.MessageType.FOREIGN_DIPLOMACY,
                                unit, "indianSettlement.mission.noDenounce",
                                "%nation%", settlement.getOwner().getNationAsString());
    }

    
    public ModelMessage establishMission(IndianSettlement settlement,
                                         Unit unit) {
        
        if (!settlement.allowContact(unit)) {
            throw new IllegalArgumentException("Contact denied at "
                                               + settlement.getName());
        }

        
        Player player = unit.getOwner();
        Tension tension = settlement.getAlarm(player);
        if (tension == null) {
            tension = new Tension(0);
            settlement.setAlarm(player, tension);
        }

        
        switch (tension.getLevel()) {
        case HAPPY: case CONTENT: case DISPLEASED:
            settlement.setMissionary(unit);
            break;
        case ANGRY: case HATEFUL:
            unit.dispose();
            break;
        }

        
        String messageId = "indianSettlement.mission." + tension.toString().toLowerCase();
        return new ModelMessage(player, ModelMessage.MessageType.FOREIGN_DIPLOMACY,
                                unit, messageId,
                                "%nation%", settlement.getOwner().getNationAsString());
    }

    
    public int getInciteAmount(Player payingPlayer, Player targetPlayer,
                               Player attackingPlayer) {
        Tension payingTension = attackingPlayer.getTension(payingPlayer);
        Tension targetTension = attackingPlayer.getTension(targetPlayer);
        int payingValue = (payingTension == null) ? 0 : payingTension.getValue();
        int targetValue = (targetTension == null) ? 0 : targetTension.getValue();
        int amount = (payingTension != null && targetTension != null
                      && payingValue > targetValue) ? 10000 : 5000;
        amount += 20 * (payingValue - targetValue);
        return Math.max(amount, 650);
    }

    
    public boolean inciteIndianSettlement(IndianSettlement settlement,
                                          Player inciter, Player enemy,
                                          int gold) {
        
        Player indianPlayer = settlement.getOwner();
        int toIncite = getInciteAmount(inciter, enemy, indianPlayer);
        if (inciter.getGold() < gold) {
            return false;
        }

        
        
        inciter.modifyGold(-gold);
        indianPlayer.modifyGold(gold);
        indianPlayer.changeRelationWithPlayer(enemy, Stance.WAR);
        settlement.modifyAlarm(enemy, 1000); 
        enemy.modifyTension(indianPlayer, 500);
        enemy.modifyTension(inciter, 250);
        return true;
    }


    
    public void updateCurrentStop(ServerPlayer serverPlayer, Unit unit) {
        
        int current = unit.validateCurrentStop();
        if (current < 0) return;

        
        ArrayList<Stop> stops = unit.getTradeRoute().getStops();
        int next = current;
        for (;;) {
            if (++next >= stops.size()) next = 0;
            if (next == current) break;
            if (hasWorkAtStop(unit, stops.get(next))) break;
        }

        
        unit.setCurrentStop(next);
        unit.setDestination(stops.get(next).getLocation());
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
            if (loc.getTile() != oldLoc.getTile()) {
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

    
    public void propagateToEuropeanMarkets(GoodsType type, int amount,
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

    
    public void clearSpeciality(Unit unit, ServerPlayer serverPlayer) {
        UnitType newType = unit.getType().getUnitTypeChange(ChangeType.CLEAR_SKILL,
                                                            serverPlayer);
        if (newType == null) {
            throw new IllegalStateException("Can not clear this unit speciality: " + unit.getId());
        }
        
        
        
        if (unit.getLocation() instanceof Building
            && !((Building) unit.getLocation()).canAdd(newType)) {
            throw new IllegalStateException("Cannot clear speciality, building does not allow new unit type");
        }

        unit.setType(newType);
        if (unit.getLocation() instanceof Tile) {
            sendUpdatedTileToAll(unit.getTile(), serverPlayer);
        }
    }
}
