

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.GUI;


public class TilePopupAction extends MapboardAction {

    public static final String id = "tilePopupAction";

    
    TilePopupAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.view.showTile", null,
              KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.SHIFT_MASK));
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        GUI gui = getFreeColClient().getGUI();
        getFreeColClient().getCanvas().showTilePopup(gui.getSelectedTile(),
                                                     gui.getCursor().getCanvasX(),
                                                     gui.getCursor().getCanvasY());
    }
}
