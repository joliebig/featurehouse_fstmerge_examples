

package net.sf.freecol.server.control;

import net.sf.freecol.common.PseudoRandom;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerGame;


public class FreeColServerHolder {
    private final FreeColServer freeColServer;

    
    protected FreeColServerHolder(FreeColServer server) {
        this.freeColServer = server;
    }

    
    protected FreeColServer getFreeColServer() {
        return freeColServer;
    }

    
    protected ServerGame getGame() {
        return freeColServer.getGame();
    }

    
    protected PseudoRandom getPseudoRandom() {        
        return getFreeColServer().getPseudoRandom();
    }
}
