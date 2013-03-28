

package net.sf.freecol.client.gui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.i18n.Messages;


public class ReconnectAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ReconnectAction.class.getName());




    public static final String id = "reconnectAction";


    
    ReconnectAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.game.reconnect", null, KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit
                .getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    
    protected boolean shouldBeEnabled() {
        return true;
    }

    
    public String getId() {
        return id;
    }

    public String getMenuItemText() {
        return Messages.message("menuBar.game.reconnect");
    }

    
    public void actionPerformed(ActionEvent e) {
        freeColClient.getConnectController().reconnect();
    }
}
