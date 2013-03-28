

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Player.PlayerType;


public class DeclareIndependenceAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(DeclareIndependenceAction.class.getName());




    public static final String id = "declareIndependenceAction";


    
    DeclareIndependenceAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.game.declareIndependence", null);
    }

    
    protected boolean shouldBeEnabled() {
        Player p = getFreeColClient().getMyPlayer();
        return super.shouldBeEnabled() && p != null && p.getPlayerType() == PlayerType.COLONIAL;
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        getFreeColClient().getInGameController().declareIndependence();
        update();
    }
}
