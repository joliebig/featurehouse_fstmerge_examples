

package net.sf.freecol.server.control;

import java.util.logging.Logger;

import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.networking.Server;


public abstract class Controller extends FreeColServerHolder {
    private static final Logger logger = Logger.getLogger(Controller.class.getName());





    
    public Controller(FreeColServer freeColServer) {
        super(freeColServer);
    }

    
    public void shutdown() {
        Server server = getFreeColServer().getServer();
        if (server != null) {
            server.shutdown();
        } else {
            logger.warning("Server object is null while trying to shut down server.");
        }
    }
}
