

package net.sf.freecol.client.gui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.ImageLibrary;
import net.sf.freecol.client.gui.i18n.Messages;


public class SaveAndQuitAction extends MapboardAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(SaveAndQuitAction.class.getName());

    public static final String id = "SaveAndQuitAction";


    
    SaveAndQuitAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.game.quit", null,
              KeyStroke.getKeyStroke('Q', Toolkit.getDefaultToolkit() .getMenuShortcutKeyMask()));
        putValue(BUTTON_IMAGE, freeColClient.getImageLibrary()
                 .getUnitButtonImageIcon(ImageLibrary.UNIT_BUTTON_WAIT, 0));
        putValue(BUTTON_ROLLOVER_IMAGE, freeColClient.getImageLibrary()
                 .getUnitButtonImageIcon(ImageLibrary.UNIT_BUTTON_WAIT, 1));
        putValue(BUTTON_PRESSED_IMAGE, freeColClient.getImageLibrary()
                 .getUnitButtonImageIcon(ImageLibrary.UNIT_BUTTON_WAIT, 2));
        putValue(BUTTON_DISABLED_IMAGE, freeColClient.getImageLibrary()
                 .getUnitButtonImageIcon(ImageLibrary.UNIT_BUTTON_WAIT, 3));
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        
        if (getFreeColClient().getCanvas()
            .showConfirmDialog(Messages.message("quitDialog.areYouSure.text"),
                               Messages.message("ok"), Messages.message("cancel"))) {
            if(getFreeColClient().getCanvas()
               .showConfirmDialog(Messages.message("quitDialog.save.text"),
                                  Messages.message("yes"), Messages.message("no")))
                {
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

