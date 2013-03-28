

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.ViewMode;


public class CenterAction extends MapboardAction {

    public static final String id = "centerAction";

    
    CenterAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.view.center", null, KeyStroke.getKeyStroke(KeyEvent.VK_C, 0));
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        getFreeColClient().getInGameController().centerActiveUnit();
    }
}
