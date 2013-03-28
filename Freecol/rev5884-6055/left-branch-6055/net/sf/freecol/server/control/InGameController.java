

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
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.CombatModel;
import net.sf.freecol.common.model.EquipmentType;
import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.FoundingFather;
import net.sf.freecol.common.model.FoundingFather.FoundingFatherType;
import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.GameOptions;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Location;
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
import net.sf.freecol.common.model.Monarch.MonarchAction;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.UnitState;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.networking.Message;
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
                if (market.addGoodsToMarket(type, -amount)) {
                    messages.add(market.makePriceMessage(type));
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
                        try {
                            Element reply = nextPlayer.getConnection().ask(chooseFoundingFatherElement);
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
            };
        t.start();
    }

    
    private List<FoundingFather> getRandomFoundingFathers(Player player) {
        int age = getGame().getTurn().getAge();
        List<FoundingFather> randomFoundingFathers = new ArrayList<FoundingFather>();
        EnumMap<FoundingFatherType, Integer> weightSums = new
            EnumMap<FoundingFatherType, Integer>(FoundingFatherType.class);
        for (FoundingFather father : FreeCol.getSpecification().getFoundingFathers()) {
            if (!player.hasFather(father) && father.isAvailableTo(player)) {
                Integer weightSum = weightSums.get(father.getType());
                if (weightSum == null) {
                    weightSum = new Integer(0);
                }
                weightSums.put(father.getType(), weightSum + father.getWeight(age));
            }
        }
        for (java.util.Map.Entry<FoundingFatherType, Integer> entry : weightSums.entrySet()) {
            if (entry.getValue() != 0) {
                int r = getPseudoRandom().nextInt(entry.getValue()) + 1;
                int weightSum = 0;
                for (FoundingFather father : FreeCol.getSpecification().getFoundingFathers()) {
                    if (!player.hasFather(father) && father.getType() == entry.getKey()) {
                        weightSum += father.getWeight(age);
                        if (weightSum >= r) {
                            randomFoundingFathers.add(father);
                            break;
                        }
                    }
                }
            }
        }
        return randomFoundingFathers;
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
                                Element reply = nextPlayer.getConnection().ask(monarchActionElement);
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
                                        nextPlayer.getConnection().send(removeGoodsElement);
                                    } else {
                                        
                                        monarchActionElement.setAttribute("force", String.valueOf(true));
                                        nextPlayer.getConnection().send(monarchActionElement);
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
                                nextPlayer.getConnection().send(monarchActionElement); 
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
                                nextPlayer.getConnection().send(monarchActionElement);
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
                                nextPlayer.getConnection().send(monarchActionElement);
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
                                Element reply = nextPlayer.getConnection().ask(monarchActionElement);
                                boolean accepted = Boolean.valueOf(reply.getAttribute("accepted")).booleanValue();
                                if (accepted) {
                                    Element updateElement = Message.createNewRootElement("monarchAction");
                                    updateElement.setAttribute("action", String.valueOf(MonarchAction.ADD_UNITS));
                                    nextPlayer.modifyGold(-price);
                                    createUnits(units, updateElement, nextPlayer);
                                    nextPlayer.getConnection().send(updateElement);
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
    
    public boolean createMission(IndianSettlement settlement, Unit missionary) {
        Tension tension = settlement.getAlarm(missionary.getOwner());
        if (tension != null) {
            switch (tension.getLevel()) {
            case HAPPY: case CONTENT: case DISPLEASED:
                settlement.setMissionary(missionary);
                return true;
            case ANGRY: case HATEFUL:
                missionary.dispose();
                return false;
            }
        }
        return false;
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

}
