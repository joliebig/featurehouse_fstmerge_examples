

package net.sf.freecol.client.gui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;


public class SaveAction extends MapboardAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(SaveAction.class.getName());

    public static final String id = "saveAction";


    
    SaveAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.game.save", null, KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
    }

    
    protected boolean shouldBeEnabled() {
        if (freeColClient.isMapEditor()) {
            return true;
        }
        
        
        if (!freeColClient.canSaveCurrentGame()) {
            return false;
        }
        return !freeColClient.getCanvas().isShowingSubPanel();
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        if (!freeColClient.isMapEditor()) {
            freeColClient.getInGameController().saveGame();
        } else {
            freeColClient.getMapEditorController().saveGame();
        }
    }
}
