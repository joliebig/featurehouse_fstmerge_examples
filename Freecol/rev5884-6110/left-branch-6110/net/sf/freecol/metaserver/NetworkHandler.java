


package net.sf.freecol.metaserver;


import java.io.IOException;
import java.util.logging.Logger;

import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.common.networking.MessageHandler;

import org.w3c.dom.Element;





public final class NetworkHandler implements MessageHandler {
    private static Logger logger = Logger.getLogger(NetworkHandler.class.getName());


    private MetaServer metaServer;
    private MetaRegister metaRegister;



    
    public NetworkHandler(MetaServer metaServer, MetaRegister metaRegister) {
        this.metaServer = metaServer;
        this.metaRegister = metaRegister;
    }





    
    public synchronized Element handle(Connection connection, Element element) {
        Element reply = null;

        String type = element.getTagName();

        if (type.equals("register")) {
            reply = register(connection, element);
        } else if (type.equals("update")) {
            reply = update(connection, element);
        } else if (type.equals("getServerList")) {
            reply = getServerList(connection, element);
        } else if (type.equals("remove")) {
            reply = remove(connection, element);
        } else if (type.equals("disconnect")) {
            reply = disconnect(connection, element);
        } else {
            logger.warning("Unkown request: " + type);
        }

        return reply;
    }

    
    
    private Element getServerList(Connection connection, Element element) {
        return metaRegister.createServerList();
    }


    
    private Element register(Connection connection, Element element) {
        String name = element.getAttribute("name");
        String address = connection.getSocket().getInetAddress().getHostAddress();
        int port = Integer.parseInt(element.getAttribute("port"));
        int slotsAvailable = Integer.parseInt(element.getAttribute("slotsAvailable"));
        int currentlyPlaying = Integer.parseInt(element.getAttribute("currentlyPlaying"));
        boolean isGameStarted = Boolean.valueOf(element.getAttribute("isGameStarted")).booleanValue();
        String version = element.getAttribute("version");
        int gameState = Integer.parseInt(element.getAttribute("gameState"));

        try {
            metaRegister.addServer(name, address, port, slotsAvailable, currentlyPlaying, isGameStarted, version, gameState);
        } catch (IOException e) {
            if (version.compareTo("0.6.0") > 0) {
                Element reply = Message.createNewRootElement("noRouteToServer");
                return reply;
            } else {
                return null;
            }
        }

        if (version.compareTo("0.6.0") > 0) {
            Element reply = Message.createNewRootElement("ok");
            return reply;
        } else {
            return null;
        }
    }


    
    private Element update(Connection connection, Element element) {
        String name = element.getAttribute("name");
        String address = connection.getSocket().getInetAddress().getHostAddress();
        int port = Integer.parseInt(element.getAttribute("port"));
        int slotsAvailable = Integer.parseInt(element.getAttribute("slotsAvailable"));
        int currentlyPlaying = Integer.parseInt(element.getAttribute("currentlyPlaying"));
        boolean isGameStarted = Boolean.valueOf(element.getAttribute("isGameStarted")).booleanValue();
        String version = element.getAttribute("version");
        int gameState = Integer.parseInt(element.getAttribute("gameState"));

        try {
            metaRegister.updateServer(name, address, port, slotsAvailable, currentlyPlaying, isGameStarted, version, gameState);
        } catch (IOException e) {}

        return null;
    }


    
    private Element remove(Connection connection, Element element) {
        String address = connection.getSocket().getInetAddress().getHostAddress();
        int port = Integer.parseInt(element.getAttribute("port"));

        metaRegister.removeServer(address, port);

        return null;
    }


    
    private Element disconnect(Connection connection, Element element) {
        try {
            connection.reallyClose();
        } catch (IOException e) {
            logger.warning("Could not close the connection.");
        }

        metaServer.removeConnection(connection);

        return null;
    }
}
