

package net.sf.freecol.server.control;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.NationOptions;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.common.networking.MessageHandler;
import net.sf.freecol.common.networking.NoRouteToServerException;
import net.sf.freecol.common.networking.StreamedMessageHandler;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;
import net.sf.freecol.server.networking.Server;

import org.w3c.dom.Element;


public final class UserConnectionHandler implements MessageHandler, StreamedMessageHandler {
    private static Logger logger = Logger.getLogger(UserConnectionHandler.class.getName());


    private final FreeColServer freeColServer;

    
    public UserConnectionHandler(FreeColServer freeColServer) {
        this.freeColServer = freeColServer;
    }

    
    public synchronized Element handle(Connection connection, Element element) {
        Element reply = null;

        String type = element.getTagName();

        if (type.equals("getVacantPlayers")) {
            reply = getVacantPlayers(connection, element);
        } else if (type.equals("disconnect")) {
            reply = disconnect(connection, element);                                
        } else {
            logger.warning("Unkown request: " + type);
        }

        return reply;
    }

    
    
    public void handle(Connection connection, XMLStreamReader in, XMLStreamWriter out) {
        if (in.getLocalName().equals("login")) {
            login(connection, in, out);
        } else {
            logger.warning("Unkown (streamed) request: " + in.getLocalName());
        }
    }
    
    
    public boolean accepts(String tagName) {
        return tagName.equals("login");
    }

    
    private Element getVacantPlayers(Connection connection, Element element) {
        Game game = freeColServer.getGame();

        if (freeColServer.getGameState() == FreeColServer.GameState.STARTING_GAME) {
            return null;
        }

        Element reply = Message.createNewRootElement("vacantPlayers");
        Iterator<Player> playerIterator = game.getPlayerIterator();
        while (playerIterator.hasNext()) {
            ServerPlayer player = (ServerPlayer) playerIterator.next();
            if (!player.isDead() && player.isEuropean() && !player.isREF()
                    && (!player.isConnected() || player.isAI())) {
                Element playerElement = reply.getOwnerDocument().createElement("player");
                playerElement.setAttribute("username", player.getName());
                reply.appendChild(playerElement);
            }
        }

        return reply;
    }


    
    private void login(Connection connection, XMLStreamReader in, XMLStreamWriter out) {        
        
        
        
        Game game = freeColServer.getGame();
        Server server = freeColServer.getServer();
        
        String username = in.getAttributeValue(null, "username");
        if (username == null) {
            throw new IllegalArgumentException("The attribute 'username' is missing.");
        }
        
        final String freeColVersion = in.getAttributeValue(null, "freeColVersion");
        if (freeColVersion == null) {
            throw new IllegalArgumentException("The attribute 'freeColVersion' is missing.");
        }
        
        if (!freeColVersion.equals(FreeCol.getVersion())) {
            Message.createError(out, "server.wrongFreeColVersion", "The game versions do not match.");
            return;
        }
        
        if (freeColServer.getGameState() != FreeColServer.GameState.STARTING_GAME) {
            if (game.getPlayerByName(username) == null) {
                Message.createError(out, "server.alreadyStarted", "The game has already been started!");
                logger.warning("game state: " + freeColServer.getGameState().toString());
                return;
            }

            ServerPlayer player = (ServerPlayer) game.getPlayerByName(username);
            if (player.isConnected() && !player.isAI()) {
                Message.createError(out, "server.usernameInUse", "The specified username is already in use.");
                return;
            }
            player.setConnection(connection);
            player.setConnected(true);

            if (player.isAI()) {
                player.setAI(false);
                Element setAIElement = Message.createNewRootElement("setAI");
                setAIElement.setAttribute("player", player.getId());
                setAIElement.setAttribute("ai", Boolean.toString(false));
                server.sendToAll(setAIElement);
            }

            
            boolean isCurrentPlayer = (game.getCurrentPlayer() == null);
            if (isCurrentPlayer) {
                game.setCurrentPlayer(player);
            }

            connection.setMessageHandler(freeColServer.getInGameInputHandler());

            try {
                freeColServer.updateMetaServer();
            } catch (NoRouteToServerException e) {}

            
            try {
                out.writeStartElement("loginConfirmed");
                out.writeAttribute("admin", Boolean.toString(player.isAdmin()));
                out.writeAttribute("singleplayer", Boolean.toString(freeColServer.isSingleplayer()));
                out.writeAttribute("startGame", "true");
                out.writeAttribute("isCurrentPlayer", Boolean.toString(isCurrentPlayer));
                freeColServer.getGame().toXML(out, player);
                freeColServer.getMapGenerator().getMapGeneratorOptions().toXML(out);
                out.writeEndElement();
            } catch (XMLStreamException e) {
                logger.warning("Could not write XML to stream (2).");
            }

            
            server.addConnection(connection);
            return;
        }

        
        int timeOut = 20000;
        while (freeColServer.getGame() == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}

            timeOut -= 1000;

            if (timeOut <= 0) {
                Message.createError(out, "server.timeOut", "Timeout when connecting to the server.");
                return;
            }
        }

        if (!freeColServer.getGame().canAddNewPlayer()) {
            Message.createError(out, "server.maximumPlayers", "Sorry, the maximum number of players reached.");
            return;
        }

        if (freeColServer.getGame().playerNameInUse(username)) {
            Message.createError(out, "server.usernameInUse", "The specified username is already in use.");
            return;
        }


        
        boolean admin = (freeColServer.getGame().getPlayers().size() == 0);
        ServerPlayer newPlayer = new ServerPlayer(freeColServer.getGame(), username, admin, 
                                                  connection.getSocket(), connection);

        freeColServer.getGame().addPlayer(newPlayer);

        
        Element addNewPlayer = Message.createNewRootElement("addPlayer");
        addNewPlayer.appendChild(newPlayer.toXMLElement(null, addNewPlayer.getOwnerDocument()));
        freeColServer.getServer().sendToAll(addNewPlayer, connection);

        connection.setMessageHandler(freeColServer.getPreGameInputHandler());

        try {
            freeColServer.updateMetaServer();
        } catch (NoRouteToServerException e) {}
        
        
        try {
            out.writeStartElement("loginConfirmed");
            out.writeAttribute("admin", (admin ? "true" : "false"));
            out.writeAttribute("singleplayer", Boolean.toString(freeColServer.isSingleplayer()));
            freeColServer.getGame().toXML(out, newPlayer);
            freeColServer.getMapGenerator().getMapGeneratorOptions().toXML(out);
            out.writeEndElement();
        }  catch (XMLStreamException e) {
            logger.warning("Could not write XML to stream (2).");
        }

        
        server.addConnection(connection);

        return;

    }
    
    
    private Element disconnect(Connection connection, Element disconnectElement) {
        try {
            connection.reallyClose();
        } catch (IOException e) {
            logger.warning("Could not close the connection.");
        }
        
        return null;
    }    
}
