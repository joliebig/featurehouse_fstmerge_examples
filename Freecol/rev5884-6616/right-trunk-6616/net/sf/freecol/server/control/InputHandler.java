

package net.sf.freecol.server.control;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.common.networking.ChatMessage;
import net.sf.freecol.common.networking.MessageHandler;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;
import net.sf.freecol.server.networking.Server;

import org.w3c.dom.Element;


public abstract class InputHandler extends FreeColServerHolder implements MessageHandler {

    private static Logger logger = Logger.getLogger(InputHandler.class.getName());

    
    private final Map<String, NetworkRequestHandler> _handlerMap = Collections
            .synchronizedMap(new HashMap<String, NetworkRequestHandler>());


    
    public InputHandler(final FreeColServer freeColServer) {
        super(freeColServer);
        
        register("logout", new NetworkRequestHandler() {
                public Element handle(Connection connection, Element element) {
                    return logout(connection, element);
                }
            });
        register("disconnect", new DisconnectHandler());
        register("chat", new NetworkRequestHandler() {
                public Element handle(Connection connection, Element element) {
                    return new ChatMessage(freeColServer.getGame(),
                                           element).handle(freeColServer, connection);
                }
            });
        register("getRandomNumbers", new GetRandomNumbersHandler());
    }

    
    protected void register(String name, NetworkRequestHandler handler) {
        _handlerMap.put(name, handler);
    }

    
    public final Element handle(Connection connection, Element element) {
        String tagName = element.getTagName();
        NetworkRequestHandler handler = _handlerMap.get(tagName);
        if (handler != null) {
            try {
                return handler.handle(connection, element);
            } catch (Exception e) {
                
                logException(e);
                sendReconnectSafely(connection);
            }
        } else {
            
            logger.warning("No handler installed for " + tagName);
        }
        return null;
    }

    
    private void sendReconnectSafely(Connection connection) {
        try {
            connection.send(Message.createNewRootElement("reconnect"));
        } catch (IOException ex) {
            logger.warning("Could not send reconnect message!");
        }
    }

    
    protected void logException(Exception e) {
        if (e != null) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.warning(sw.toString());
        }
    }

    
    protected Element createErrorReply(String message) {
        Element reply = Message.createNewRootElement("error");
        
        reply.setAttribute("message", message);
        return reply;
    }

    
    abstract protected Element logout(Connection connection, Element logoutElement);


    
    interface NetworkRequestHandler {
        
        Element handle(Connection connection, Element element);
    }

    
    abstract class CurrentPlayerNetworkRequestHandler implements NetworkRequestHandler {
        public final Element handle(Connection conn, Element element) {
            ServerPlayer player = getFreeColServer().getPlayer(conn);
            if (isCurrentPlayer(player)) {
                try {
                    return handle(player, conn, element);
                } catch (Exception e) {
                    logException(e);
                    sendReconnectSafely(conn);
                    return null;
                }
            } else {
                logger.warning("Received message out of turn from " 
                        + player.getNation()
                        + " player:"
                        + element.getTagName());
                return createErrorReply("Not your turn.");
            }
        }

        
        private boolean isCurrentPlayer(Player player) {
            Game game = getFreeColServer().getGame();
            if (player != null && game != null) {
                return player.equals(game.getCurrentPlayer());
            }
            return false;
        }

        
        protected abstract Element handle(Player player, Connection conn, Element element);
    }

    private class GetRandomNumbersHandler implements NetworkRequestHandler {
        public Element handle(Connection conn, Element element) {
            int numRandomNumbers = Integer.parseInt(element.getAttribute("n"));
            StringBuffer sb = new StringBuffer();
            int[] numbers = getFreeColServer().getRandomNumbers(numRandomNumbers);
            for (int i = 0; i < numbers.length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(String.valueOf(numbers[i]));
            }
            Element reply = Message.createNewRootElement("getRandomNumbersConfirmed");
            reply.setAttribute("result", sb.toString());
            return reply;
        }
    }

    private class DisconnectHandler implements NetworkRequestHandler {
        public Element handle(Connection connection, Element disconnectElement) {
            
            ServerPlayer player = getFreeColServer().getPlayer(connection);
            logDisconnect(connection, player);
            if (player != null && player.isConnected()) {
                logout(connection, null);
            }
            try {
                connection.reallyClose();
            } catch (IOException e) {
                logger.warning("Could not close the connection.");
            }
            Server server = getFreeColServer().getServer();
            if (server != null) {
                server.removeConnection(connection);
            }
            return null;
        }

        private void logDisconnect(Connection connection, ServerPlayer player) {
            logger.info("Disconnection by: " + connection + ((player != null) ? " (" + player.getName() + ") " : ""));
        }
    }
}
