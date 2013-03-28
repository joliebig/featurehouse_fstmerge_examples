

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.common.model.Player.PlayerType;


public class RetireAction extends MapboardAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(RetireAction.class.getName());

    public static final String id = "retireAction";


    
    RetireAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.game.retire", null, null);
    }

    
    public String getId() {
        return id;
    }

    
    protected boolean shouldBeEnabled() {
        return super.shouldBeEnabled()
            && freeColClient.getMyPlayer() != null
            && freeColClient.getMyPlayer().getPlayerType() != PlayerType.INDEPENDENT;
    }    
    
    
    public void actionPerformed(ActionEvent e) {
        getFreeColClient().getCanvas().retire();
    }
}
