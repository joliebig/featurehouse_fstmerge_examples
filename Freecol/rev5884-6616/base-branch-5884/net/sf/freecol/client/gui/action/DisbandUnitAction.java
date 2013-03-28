

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.ImageLibrary;


public class DisbandUnitAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(DisbandUnitAction.class.getName());




    public static final String id = "disbandUnitAction";


    
    DisbandUnitAction(FreeColClient freeColClient) {
        super(freeColClient, "unit.state.8", null, KeyStroke.getKeyStroke('D', 0));
        putValue(BUTTON_IMAGE, freeColClient.getImageLibrary().getUnitButtonImageIcon(ImageLibrary.UNIT_BUTTON_DISBAND,
                0));
        putValue(BUTTON_ROLLOVER_IMAGE, freeColClient.getImageLibrary().getUnitButtonImageIcon(
                ImageLibrary.UNIT_BUTTON_DISBAND, 1));
        putValue(BUTTON_PRESSED_IMAGE, freeColClient.getImageLibrary().getUnitButtonImageIcon(
                ImageLibrary.UNIT_BUTTON_DISBAND, 2));
        putValue(BUTTON_DISABLED_IMAGE, freeColClient.getImageLibrary().getUnitButtonImageIcon(
                ImageLibrary.UNIT_BUTTON_DISBAND, 3));
    }

    
    protected boolean shouldBeEnabled() {
        return super.shouldBeEnabled() && getFreeColClient().getGUI().getActiveUnit() != null;
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        getFreeColClient().getInGameController().disbandActiveUnit();
    }
}
