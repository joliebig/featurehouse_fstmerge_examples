


package net.sf.freecol.client.networking;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.MessageHandler;

import org.w3c.dom.Element;



public final class Client {
    private static final Logger logger = Logger.getLogger(Client.class.getName());



    
    private final Connection c;

    private String host;
    private int port;


    
    public Client(String host, int port, MessageHandler handler) throws IOException {
        this.host = host;
        this.port = port;
        c = new Connection(host, port, handler, FreeCol.CLIENT_THREAD);
    }



    public String getHost() {
        return host;
    }
    
    
    public int getPort() {
        return port;
    }
    
    
    
    public void send(Element element) {
        try {
            c.send(element);
        } catch (IOException e) {
            logger.warning("Could not send the specified message: " + element);
        }
    }


    
    public void sendAndWait(Element element) {
        try {
            c.sendAndWait(element);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Client.sendAndWait could not send " + element, e);
        }
    }


    
    public Element ask(Element element) {
        try {
            return c.ask(element);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Client.ask could not send " + element, e);
        }
        return null;
    }


    
    public Connection getConnection() {
        return c;
    }


    
    public void disconnect() {
        try {
            c.close();
        } catch (IOException e) {
            logger.warning("Exception while closing connection: " + e);
        }
    }


    
    public void setMessageHandler(MessageHandler mh) {
        c.setMessageHandler(mh);
    }
}
