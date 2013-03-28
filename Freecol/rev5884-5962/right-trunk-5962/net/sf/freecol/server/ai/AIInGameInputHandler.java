

package net.sf.freecol.server.ai;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.DiplomaticTrade;
import net.sf.freecol.common.model.FoundingFather;
import net.sf.freecol.common.model.FoundingFather.FoundingFatherType;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.Monarch.MonarchAction;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Player.Stance;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.DiplomacyMessage;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.common.networking.MessageHandler;
import net.sf.freecol.common.networking.StreamedMessageHandler;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;
import net.sf.freecol.server.networking.DummyConnection;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public final class AIInGameInputHandler implements MessageHandler, StreamedMessageHandler {

    private static final Logger logger = Logger.getLogger(AIInGameInputHandler.class.getName());

    
    private final ServerPlayer serverPlayer;

    
    private final FreeColServer freeColServer;

    private final AIMain aiMain;


    
    public AIInGameInputHandler(FreeColServer freeColServer, ServerPlayer me, AIMain aiMain) {
        this.freeColServer = freeColServer;
        this.serverPlayer = me;
        this.aiMain = aiMain;
        if (freeColServer == null) {
            throw new NullPointerException("freeColServer == null");
        } else if (me == null) {
            throw new NullPointerException("me == null");
        } else if (aiMain == null) {
            throw new NullPointerException("aiMain == null");
        }
        if (!me.isAI()) {
            logger.warning("VERY BAD: Applying AIInGameInputHandler to a non-AI player!!!");
        }
    }

    
    public synchronized Element handle(Connection connection, Element element) {
        Element reply = null;
        try {
            if (element != null) {
                String type = element.getTagName();

                
                
                if (type.equals("update")) {
                } else if (type.equals("remove")) {
                } else if (type.equals("setAI")) {
                } else if (type.equals("startGame")) {
                } else if (type.equals("updateGame")) {
                } else if (type.equals("addPlayer")) {
                } else if (type.equals("opponentMove")) {
                } else if (type.equals("opponentAttack")) {
                } else if (type.equals("attackResult")) {
                } else if (type.equals("setCurrentPlayer")) {
                    reply = setCurrentPlayer((DummyConnection) connection, element);
                } else if (type.equals("newTurn")) {
                } else if (type.equals("setDead")) {
                } else if (type.equals("gameEnded")) {
                } else if (type.equals("disconnect")) {
                } else if (type.equals("logout")) {
                } else if (type.equals("error")) {
                } else if (type.equals("chat")) {
                } else if (type.equals("chooseFoundingFather")) {
                    reply = chooseFoundingFather((DummyConnection) connection, element);
                } else if (type.equals("reconnect")) {
                    logger.warning("The server requests a reconnect. This means an illegal operation has been performed. Please refer to any previous error message.");
                } else if (type.equals("setStance")) {
                } else if (type.equals("monarchAction")) {
                    reply = monarchAction((DummyConnection) connection, element);
                } else if (type.equals("removeGoods")) {
                } else if (type.equals("indianDemand")) {
                    reply = indianDemand((DummyConnection) connection, element);
                } else if (type.equals("updateMarket")) {
                } else if (type.equals("marketElement")) {
                } else if (type.equals("diplomacy")) {
                    reply = diplomaticTrade((DummyConnection) connection, element);
                } else if (type.equals("addMessages")) {
                } else if (type.equals("addHistory")) {
                } else if (type.equals("multiple")) {
                } else {
                    logger.warning("Message is of unsupported type \"" + type + "\".");
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "AI input handler for " + serverPlayer + " caught error handling "
                    + element.getTagName(), e);
        }
        return reply;
    }

    
    public void handle(Connection connection, XMLStreamReader in, XMLStreamWriter out) {
        
    }

    
    public boolean accepts(String tagName) {
        return false;
    }

    
    private Element setCurrentPlayer(final DummyConnection connection, final Element setCurrentPlayerElement) {
        logger.finest("Entering setCurrentPlayer");
        final Game game = freeColServer.getGame();
        final Player currentPlayer = (Player) game.getFreeColGameObject(setCurrentPlayerElement.getAttribute("player"));

        if (serverPlayer.getId() == currentPlayer.getId()) {
            logger.finest("Starting new Thread for " + serverPlayer.getName());
            Thread t = new Thread(FreeCol.SERVER_THREAD+"AIPlayer (" + serverPlayer.getName() + ")") {
                public void run() {
                    try {
                        getAIPlayer().startWorking();
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "AI player failed while working!", e);
                    }
                    try {
                        connection.send(Message.createNewRootElement("endTurn"));
                    } catch (IOException e) {
                        logger.log(Level.WARNING, "Could not send \"endTurn\"-message!", e);
                    }
                }
            };
            t.start();
        }
        logger.finest("About to exit from setCurrentPlayerElement.");
        return null;
    }

    
    private Element chooseFoundingFather(DummyConnection connection, Element element) {
        final List<FoundingFather> possibleFoundingFathers = new ArrayList<FoundingFather>();
        for (FoundingFatherType type : FoundingFatherType.values()) {
            String id = element.getAttribute(type.toString());
            if (id != null && id != "") {
                possibleFoundingFathers.add(FreeCol.getSpecification().getFoundingFather(id));
            }
        }

        FoundingFather foundingFather = getAIPlayer().selectFoundingFather(possibleFoundingFathers);
        Element reply = Message.createNewRootElement("chosenFoundingFather");
        reply.setAttribute("foundingFather", foundingFather.getId());
        serverPlayer.setCurrentFather(foundingFather);

        return reply;
    }

    
    private Element monarchAction(DummyConnection connection, Element element) {
        MonarchAction action = Enum.valueOf(MonarchAction.class, element.getAttribute("action"));
        Element reply = null;
        boolean accept = false;
        switch (action) {
        case RAISE_TAX:
            int tax = Integer.parseInt(element.getAttribute("amount"));
            accept = getAIPlayer().acceptTax(tax);
            reply = Message.createNewRootElement("acceptTax");
            reply.setAttribute("accepted", String.valueOf(accept));
            break;
            
        case LOWER_TAX:
            int newTax = Integer.parseInt(element.getAttribute("amount"));
            getAIPlayer().getPlayer().setTax(newTax);
            break;

        case OFFER_MERCENARIES:
            reply = Message.createNewRootElement("hireMercenaries");
            accept = getAIPlayer().acceptMercenaryOffer();
            reply.setAttribute("accepted", String.valueOf(accept));
            break;

        default:
            logger.info("AI player ignoring monarch action " + action);
        }

        return reply;
    }

    
    private Element indianDemand(DummyConnection connection, Element element) {
        Game game = freeColServer.getGame();
        Unit unit = (Unit) game.getFreeColGameObject(element.getAttribute("unit"));
        Colony colony = (Colony) game.getFreeColGameObject(element.getAttribute("colony"));
        int gold = 0;
        Goods goods = null;
        Element goodsElement = Message.getChildElement(element, Goods.getXMLElementTagName());
        if (goodsElement == null) {
            gold = Integer.parseInt(element.getAttribute("gold"));
        } else {
            goods = new Goods(game, goodsElement);
        }
        boolean accept = getAIPlayer().acceptIndianDemand(unit, colony, goods, gold);
        element.setAttribute("accepted", String.valueOf(accept));
        return element;
    }

    
    private Element diplomaticTrade(DummyConnection connection, Element element) {
        
        DiplomacyMessage message = new DiplomacyMessage(freeColServer.getGame(), element);
        boolean accept = getAIPlayer().acceptDiplomaticTrade(message.getAgreement());

        if (accept) {
            message.setAccept();
            return message.toXMLElement();
        } else {
            return null;
        }
    }

    
    public AIPlayer getAIPlayer() {
        return (AIPlayer) aiMain.getAIObject(serverPlayer);
    }
}
