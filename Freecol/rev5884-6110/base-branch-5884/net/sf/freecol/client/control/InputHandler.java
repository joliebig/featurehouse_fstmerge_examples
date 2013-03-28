


package net.sf.freecol.client.control;

import java.util.logging.Logger;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.MessageHandler;

import org.w3c.dom.Element;


public abstract class InputHandler implements MessageHandler {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(InputHandler.class.getName());



    private final FreeColClient freeColClient;

    
    public InputHandler(FreeColClient freeColClient) {
        this.freeColClient = freeColClient;
    }


    
    protected FreeColClient getFreeColClient() {
        return freeColClient;
    }

    
    protected Game getGame() {
        return freeColClient.getGame();
    }


    
    public abstract Element handle(Connection connection, Element element);


    
    protected Element disconnect(Element disconnectElement) {
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (freeColClient.getCanvas().containsInGameComponents()) {
                    if (freeColClient.getFreeColServer() == null) {
                        freeColClient.getCanvas().returnToTitle();
                    } else {
                        freeColClient.getCanvas().removeInGameComponents();
                    }
                }
            }
        });

        return null;
    }
}
