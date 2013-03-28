

package net.sf.freecol.server.networking;

import java.io.IOException;

import net.sf.freecol.common.FreeColException;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.MessageHandler;

import org.w3c.dom.Element;


public final class DummyConnection extends Connection {



    
    private MessageHandler outgoingMessageHandler;

    private DummyConnection otherConnection;

    
    private final String _name;


    
    public DummyConnection(String name, MessageHandler incomingMessageHandler) {
        super();
        _name = name;
        setMessageHandler(incomingMessageHandler);
    }

    
    public void close() throws IOException {
        
    }

    
    public void send(Element element) throws IOException {
        try {
            outgoingMessageHandler.handle(getOtherConnection(), element);
        } catch (FreeColException e) {
        }
    }

    
    public Element ask(Element element) throws IOException {
        Element theResult = null;
        try {
            theResult = outgoingMessageHandler.handle(getOtherConnection(), element);
        } catch (FreeColException e) {
        }

        return theResult;
    }

    
    public void sendAndWait(Element element) throws IOException {
        ask(element);
    }

    
    private void setOutgoingMessageHandler(MessageHandler mh) {
        outgoingMessageHandler = mh;
    }

    
    public void setOutgoingMessageHandler(DummyConnection c) {
        otherConnection = c;
        setOutgoingMessageHandler(c.getMessageHandler());
    }

    
    public DummyConnection getOtherConnection() {
        return otherConnection;
    }

    
    public String toString() {
        return "DummyConnection[" + _name + "]";
    }
}
