

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.panel.MainPanel;


public class ShowMainAction extends FreeColAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ShowMainAction.class.getName());




    public static final String id = "showMainAction";


    
    ShowMainAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.game.returnToMain", null, null);
    }

    
    protected boolean shouldBeEnabled() {
        return true;
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        if (!getFreeColClient().getCanvas().showConfirmDialog("stopCurrentGame.text", "stopCurrentGame.yes", "stopCurrentGame.no")) {
            return;
        }
        
        getFreeColClient().getCanvas().removeInGameComponents();
        getFreeColClient().setMapEditor(false);
        getFreeColClient().setGame(null);
        getFreeColClient().getCanvas().returnToTitle();
    }
}
