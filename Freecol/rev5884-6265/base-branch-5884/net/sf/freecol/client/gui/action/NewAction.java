

package net.sf.freecol.client.gui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;


public class NewAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(NewAction.class.getName());




    public static final String id = "newAction";


    
    NewAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.game.new", null, KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit()
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
            freeColClient.getCanvas().newGame();
        } else {
            freeColClient.getMapEditorController().newMap();
        }
    }
}
