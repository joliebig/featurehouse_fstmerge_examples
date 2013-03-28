


package net.sf.freecol.server.networking;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.MessageHandler;
import net.sf.freecol.server.FreeColServer;

import org.w3c.dom.Element;




public final class Server extends Thread {

    private static Logger logger = Logger.getLogger(Server.class.getName());

    
    private ServerSocket serverSocket;

    
    private HashMap<Socket, Connection> connections = new HashMap<Socket, Connection>();

    
    private boolean running = true;

    
    private FreeColServer freeColServer;

    
    private int port;

    
    private final Object shutdownLock = new Object();

    
    public Server(FreeColServer freeColServer, int port) throws IOException {
        super(FreeCol.SERVER_THREAD+"Server");
        this.freeColServer = freeColServer;
        this.port = port;
        
        serverSocket = new ServerSocket(port);
    }

    
    public void run() {
        
        
        
        
        
        
        
        
        
        
        
        
        
        synchronized (shutdownLock) {
            while (running) {
                Socket clientSocket = null;
                try {
                    clientSocket = serverSocket.accept();

                    logger.info("Got client connection from "
                                + clientSocket.getInetAddress().toString());
                    
                        new Connection(clientSocket, freeColServer.getUserConnectionHandler(),
                                       FreeCol.SERVER_THREAD);
                    
                } catch (IOException e) {
                    if (running) {
                        StringWriter sw = new StringWriter();
                        e.printStackTrace(new PrintWriter(sw));
                        logger.warning(sw.toString());
                    }
                }
            }
        }
    }

    
    public void sendToAll(Element element, Connection exceptConnection) {
        Iterator<Connection> connectionIterator = getConnectionIterator();

        while (connectionIterator.hasNext()) {
            Connection connection = connectionIterator.next();
            if (connection != exceptConnection) {
                try {
                    connection.sendAndWait(element);
                } catch (IOException e) {
                    logger.warning("Exception while attempting to send to "
                                   + connection);
                }
            }
        }
    }

    
    public void sendToAll(Element element) {
        sendToAll(element, null);
    }

    
    public int getPort() {
        return port;
    }

    
    public void setMessageHandlerToAllConnections(MessageHandler messageHandler) {
        Iterator<Connection> connectionIterator = getConnectionIterator();

        while (connectionIterator.hasNext()) {
            Connection connection = connectionIterator.next();
            connection.setMessageHandler(messageHandler);
        }
    }

    
    public Iterator<Connection> getConnectionIterator() {
        return connections.values().iterator();
    }

    
    public void shutdown() {
        running = false;

        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.warning("Could not close the server socket!");
        }

        synchronized (shutdownLock) {
            
            
        }

        Iterator<Connection> connectionsIterator = getConnectionIterator();
        while (connectionsIterator.hasNext()) {
            Connection c = connectionsIterator.next();

            try {
                if (c != null) {
                    
                    c.close();
                }
            } catch (IOException e) {
                logger.warning("Could not close the connection.");
            }
        }

        freeColServer.removeFromMetaServer();

        logger.info("Server shutdown.");
    }

    
    public Connection getConnection(Socket socket) {
        return connections.get(socket);
    }

    
    public void addDummyConnection(Connection connection) {
        connections.put(new Socket(), connection);
    }

    
    public void addConnection(Connection connection) {
        connections.put(connection.getSocket(), connection);
    }

    
    public void removeConnection(Connection connection) {
        connections.remove(connection.getSocket());
    }
}
