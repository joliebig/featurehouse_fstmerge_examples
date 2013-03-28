

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.logging.Logger;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.common.model.Player;


public class DebugCrossBugAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(DebugCrossBugAction.class.getName());




    public static final String id = "debugCrossBugAction";


    
    DebugCrossBugAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.debug.crossBugAction", null, KeyEvent.VK_L);
    }

    
    protected boolean shouldBeEnabled() {
        return true;
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        freeColClient.getMyPlayer().updateImmigrationRequired();
        if (freeColClient.getFreeColServer() != null) {
            Iterator<Player> pi = freeColClient.getFreeColServer().getGame().getPlayerIterator();
            while (pi.hasNext()) {
                pi.next().updateImmigrationRequired();
            }
        }
    }
}
