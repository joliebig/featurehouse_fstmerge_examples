

package net.sf.freecol.client.gui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.Canvas;


public class SaveAndQuitAction extends MapboardAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(SaveAndQuitAction.class.getName());

    public static final String id = "SaveAndQuitAction";


    
    SaveAndQuitAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.game.quit", null,
              KeyStroke.getKeyStroke('Q', Toolkit.getDefaultToolkit() .getMenuShortcutKeyMask()));
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        Canvas canvas = getFreeColClient().getCanvas();
        if (canvas.showConfirmDialog("quitDialog.areYouSure.text", "ok", "cancel")) {
            if (canvas.showConfirmDialog("quitDialog.save.text", "yes", "no")) {
                if (!freeColClient.isMapEditor()) {
                    freeColClient.getInGameController().saveGame();
                } else {
                    freeColClient.getMapEditorController().saveGame();
                }
            }
            freeColClient.quit();
        }
    }
}

