

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;


public class EndTurnAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(EndTurnAction.class.getName());




    public static final String id = "endTurnAction";


    
    EndTurnAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.game.endTurn", null, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        getFreeColClient().getInGameController().endTurn();
    }
}
