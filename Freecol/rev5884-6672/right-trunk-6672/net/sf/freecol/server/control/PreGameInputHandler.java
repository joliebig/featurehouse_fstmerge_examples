

package net.sf.freecol.server.control;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.FreeColException;
import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.model.Nation;
import net.sf.freecol.common.model.NationOptions.Advantages;
import net.sf.freecol.common.model.NationOptions.NationState;
import net.sf.freecol.common.model.NationType;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.common.networking.NoRouteToServerException;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Element;


public final class PreGameInputHandler extends InputHandler {
    private static Logger logger = Logger.getLogger(PreGameInputHandler.class.getName());





    
    public PreGameInputHandler(FreeColServer freeColServer) {
        super(freeColServer);
        
        register("updateGameOptions", new NetworkRequestHandler() {
            public Element handle(Connection connection, Element element) {
                return updateGameOptions(connection, element);
            }
        });
        register("updateMapGeneratorOptions", new NetworkRequestHandler() {
            public Element handle(Connection connection, Element element) {
                return updateMapGeneratorOptions(connection, element);
            }
        });
        register("ready", new NetworkRequestHandler() {
            public Element handle(Connection connection, Element element) {
                return ready(connection, element);
            }
        });
        register("setNation", new NetworkRequestHandler() {
            public Element handle(Connection connection, Element element) {
                return nation(connection, element);
            }
        });
        register("setNationType", new NetworkRequestHandler() {
            public Element handle(Connection connection, Element element) {
                return nationType(connection, element);
            }
        });
        register("setColor", new NetworkRequestHandler() {
            public Element handle(Connection connection, Element element) {
                return color(connection, element);
            }
        });
        register("setAvailable", new NetworkRequestHandler() {
            public Element handle(Connection connection, Element element) {
                return available(connection, element);
            }
        });
        register("requestLaunch", new NetworkRequestHandler() {
            public Element handle(Connection connection, Element element) {
                return requestLaunch(connection, element);
            }
        });
    }

    
    private Element updateGameOptions(Connection connection, Element element) {
        ServerPlayer player = getFreeColServer().getPlayer(connection);
        if (!player.isAdmin()) {
            throw new IllegalStateException();
        }
        getFreeColServer().getGame().getGameOptions().readFromXMLElement((Element) element.getChildNodes().item(0));
        Element updateGameOptionsElement = Message.createNewRootElement("updateGameOptions");
        updateGameOptionsElement.appendChild(getFreeColServer().getGame().getGameOptions().toXMLElement(
                updateGameOptionsElement.getOwnerDocument()));
        getFreeColServer().getServer().sendToAll(updateGameOptionsElement, connection);
        return null;
    }

    
    private Element updateMapGeneratorOptions(Connection connection, Element element) {
        ServerPlayer player = getFreeColServer().getPlayer(connection);
        if (!player.isAdmin()) {
            throw new IllegalStateException();
        }
        getFreeColServer().getMapGenerator().getMapGeneratorOptions().readFromXMLElement(
                (Element) element.getChildNodes().item(0));
        Element umge = Message.createNewRootElement("updateMapGeneratorOptions");
        umge.appendChild(getFreeColServer().getMapGenerator().getMapGeneratorOptions().toXMLElement(
                umge.getOwnerDocument()));
        getFreeColServer().getServer().sendToAll(umge, connection);
        return null;
    }

    
    private Element ready(Connection connection, Element element) {
        ServerPlayer player = getFreeColServer().getPlayer(connection);
        if (player != null) {
            boolean ready = (new Boolean(element.getAttribute("value"))).booleanValue();
            player.setReady(ready);
            Element playerReady = Message.createNewRootElement("playerReady");
            playerReady.setAttribute("player", player.getId());
            playerReady.setAttribute("value", Boolean.toString(ready));
            getFreeColServer().getServer().sendToAll(playerReady, player.getConnection());
        } else {
            logger.warning("Ready from unknown connection.");
        }
        return null;
    }

    
    private Element nation(Connection connection, Element element) {
        ServerPlayer player = getFreeColServer().getPlayer(connection);
        if (player != null) {
            Nation nation = FreeCol.getSpecification().getNation(element.getAttribute("value"));
            if (getFreeColServer().getGame().getNationOptions().getNations().get(nation) ==
                NationState.AVAILABLE) {
                player.setNation(nation);
                Element updateNation = Message.createNewRootElement("updateNation");
                updateNation.setAttribute("player", player.getId());
                updateNation.setAttribute("value", nation.getId());
                getFreeColServer().getServer().sendToAll(updateNation, player.getConnection());
            } else {
                logger.warning("Selected non-selectable nation.");
            }
        } else {
            logger.warning("Nation from unknown connection.");
        }
        return null;
    }

    
    private Element nationType(Connection connection, Element element) {
        ServerPlayer player = getFreeColServer().getPlayer(connection);
        if (player != null) {
            NationType nationType = FreeCol.getSpecification().getNationType(element.getAttribute("value"));
            NationType fixedNationType = FreeCol.getSpecification().getNation(player.getNationID()).getType();
            Advantages advantages = getFreeColServer().getGame().getNationOptions().getNationalAdvantages();
            if (advantages == Advantages.SELECTABLE
                || (advantages == Advantages.FIXED && nationType.equals(fixedNationType))) {
                player.setNationType(nationType);
                Element updateNationType = Message.createNewRootElement("updateNationType");
                updateNationType.setAttribute("player", player.getId());
                updateNationType.setAttribute("value", nationType.getId());
                getFreeColServer().getServer().sendToAll(updateNationType, player.getConnection());
            } else {
                logger.warning("NationType is not selectable");
            }
        } else {
            logger.warning("NationType from unknown connection.");
        }
        return null;
    }

    
    private Element color(Connection connection, Element element) {
        ServerPlayer player = getFreeColServer().getPlayer(connection);
        if (player != null) {
            String color = element.getAttribute("value");
            player.setColor(new Color(Integer.decode(color)));
            Element updateColor = Message.createNewRootElement("updateColor");
            updateColor.setAttribute("player", player.getId());
            updateColor.setAttribute("value", color);
            getFreeColServer().getServer().sendToAll(updateColor, player.getConnection());
        } else {
            logger.warning("Color from unknown connection.");
        }
        return null;
    }

    
    private Element available(Connection connection, Element element) {
        ServerPlayer player = getFreeColServer().getPlayer(connection);
        if (player != null) {
            Nation nation = Specification.getSpecification().getNation(element.getAttribute("nation"));
            NationState state = Enum.valueOf(NationState.class, element.getAttribute("state"));
            getFreeColServer().getGame().getNationOptions().setNationState(nation, state);
            getFreeColServer().getServer().sendToAll(element, player.getConnection());
        } else {
            logger.warning("Available from unknown connection.");
        }
        return null;
    }

    
    private Element requestLaunch(Connection connection, Element element) {
        FreeColServer freeColServer = getFreeColServer();
        ServerPlayer launchingPlayer = freeColServer.getPlayer(connection);
        
        if (!launchingPlayer.isAdmin()) {
            Element reply = Message.createNewRootElement("error");
            reply.setAttribute("message", "Sorry, only the server admin can launch the game.");
            reply.setAttribute("messageID", "server.onlyAdminCanLaunch");
            return reply;
        }
        
        Iterator<Player> playerIterator = freeColServer.getGame().getPlayerIterator();
        LinkedList<Nation> nations = new LinkedList<Nation>();
        LinkedList<Color> colors = new LinkedList<Color>();
        while (playerIterator.hasNext()) {
            ServerPlayer player = (ServerPlayer) playerIterator.next();
            final Nation nation = FreeCol.getSpecification().getNation(player.getNationID());
            final Color color = player.getColor();
            
            for (int i = 0; i < nations.size(); i++) {
                if (nations.get(i) == nation) {
                    Element reply = Message.createNewRootElement("error");
                    reply
                            .setAttribute("message",
                                    "All players need to pick a unique nation before the game can start.");
                    reply.setAttribute("messageID", "server.invalidPlayerNations");
                    return reply;
                }
            }
            nations.add(nation);
            
            for (int i = 0; i < colors.size(); i++) {
                if (colors.get(i).equals(color)) {
                    Element reply = Message.createNewRootElement("error");
                    reply.setAttribute("message", "All players need to pick a unique color before the game can start.");
                    reply.setAttribute("messageID", "server.invalidPlayerColors");
                    return reply;
                }
            }
            colors.add(color);
        }
        
        if (!freeColServer.getGame().isAllPlayersReadyToLaunch()) {
            Element reply = Message.createNewRootElement("error");
            reply.setAttribute("message", "Not all players are ready to begin the game!");
            reply.setAttribute("messageID", "server.notAllReady");
            return reply;
        }
        try {
            ((PreGameController) freeColServer.getController()).startGame();
        } catch (FreeColException e) {
            
            Element reply = Message.createNewRootElement("error");
            reply.setAttribute("message", "An error occurred while starting the game!");
            reply.setAttribute("messageID", "server.errorStartingGame");
            return reply;
        }
        return null;
    }

    
    protected Element logout(Connection connection, Element logoutElement) {
        ServerPlayer player = getFreeColServer().getPlayer(connection);
        logger.info("Logout by: " + connection + ((player != null) ? " (" + player.getName() + ") " : ""));
        Element logoutMessage = Message.createNewRootElement("logout");
        logoutMessage.setAttribute("reason", "User has logged out.");
        logoutMessage.setAttribute("player", player.getId());
        player.setConnected(false);
        getFreeColServer().getGame().removePlayer(player);
        getFreeColServer().getServer().sendToAll(logoutMessage, connection);
        try {
            getFreeColServer().updateMetaServer();
        } catch (NoRouteToServerException e) {}
        
        return null;
    }
}
