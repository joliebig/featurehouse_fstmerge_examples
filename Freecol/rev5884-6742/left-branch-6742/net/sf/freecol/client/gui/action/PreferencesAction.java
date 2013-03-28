

package net.sf.freecol.client.gui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.i18n.Messages;


public class PreferencesAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(PreferencesAction.class.getName());




    public static final String id = "preferencesAction";


    
    PreferencesAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.game.preferences", null, KeyStroke.getKeyStroke('P', Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
    }

    
    protected boolean shouldBeEnabled() {
        return true;
    }

    
    public String getId() {
        return id;
    }

    public String getMenuItemText() {
        return Messages.message("menuBar.game.preferences");
    }

    
    public void actionPerformed(ActionEvent e) {
        freeColClient.getCanvas().showClientOptionsDialog();
    }
}
