

package net.sf.freecol.client.gui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;


public class QuitAction extends MapboardAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(QuitAction.class.getName());

    public static final String id = "quitAction";


    
    QuitAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.game.quit", null, KeyStroke.getKeyStroke('Q', Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        getFreeColClient().getCanvas().quit();
    }
}
