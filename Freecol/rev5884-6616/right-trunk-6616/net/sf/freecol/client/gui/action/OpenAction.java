

package net.sf.freecol.client.gui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;


public class OpenAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(OpenAction.class.getName());




    public static final String id = "openAction";


    
    OpenAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.game.open", null, KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
    }

    
    protected boolean shouldBeEnabled() {
        return true;
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        if (!freeColClient.isMapEditor()) {
            freeColClient.getInGameController().loadGame();
        } else {
            freeColClient.getMapEditorController().loadGame();
        }
    }
}
