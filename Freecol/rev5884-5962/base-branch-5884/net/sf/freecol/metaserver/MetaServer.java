

package net.sf.freecol.metaserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.networking.Connection;


public final class MetaServer extends Thread {



    private static Logger logger = Logger.getLogger(MetaServer.class.getName());

    private static final int REMOVE_DEAD_SERVERS_INTERVAL = 120000;

    public static final int REMOVE_OLDER_THAN = 90000;

    
    private ServerSocket serverSocket;

    
    private HashMap<Socket, Connection> connections = new HashMap<Socket, Connection>();

    
    private boolean running = true;

    
    private int port;

    private NetworkHandler networkHandler;


    
    public static void main(String[] args) {
        int port = -1;
        try {
            port = Integer.parseInt(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Usage: java net.sf.freecol.metaserver.MetaServer PORT_NUMBER");
            System.exit(-1);
        } catch (NumberFormatException e) {
            System.out.println("Usage: java net.sf.freecol.metaserver.MetaServer PORT_NUMBER");
            System.exit(-1);
        }

        MetaServer metaServer = null;
        try {
            metaServer = new MetaServer(port);
        } catch (IOException e) {
            logger.warning("Could not create MetaServer!");
            System.exit(-1);
        }

        metaServer.start();
    }

    
    public MetaServer(int port) throws IOException {
        this.port = port;

        final MetaRegister mr = new MetaRegister();
        networkHandler = new NetworkHandler(this, mr);
        serverSocket = new ServerSocket(port);

        Timer t = new Timer(true);
        t.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    mr.removeDeadServers();
                } catch (Exception ex) {
                    logger.warning("Exception: " + ex.getMessage());
                }
            }
        }, REMOVE_DEAD_SERVERS_INTERVAL, REMOVE_DEAD_SERVERS_INTERVAL);
    }

    
    public void run() {
        while (running) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
                logger.info("Got client connection from " + clientSocket.getInetAddress().toString());
                Connection connection = new Connection(clientSocket, getNetworkHandler(), FreeCol.METASERVER_THREAD);
                connections.put(clientSocket, connection);
            } catch (IOException e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));

                logger.warning(sw.toString());
            }
        }
    }

    
    public NetworkHandler getNetworkHandler() {
        return networkHandler;
    }

    
    public int getPort() {
        return port;
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

        Iterator<Connection> connectionsIterator = connections.values().iterator();
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

        logger.info("Server shutdown.");
    }

    
    public Connection getConnection(Socket socket) {
        return connections.get(socket);
    }

    
    public void removeConnection(Connection connection) {
        connections.remove(connection.getSocket());
    }
}
