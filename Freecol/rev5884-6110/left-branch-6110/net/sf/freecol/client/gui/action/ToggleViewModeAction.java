

package net.sf.freecol.client.gui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.ViewMode;


public class ToggleViewModeAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ToggleViewModeAction.class.getName());




    public static final String id = "toggleViewModeAction";


    
    ToggleViewModeAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.view.toggle", null, KeyStroke.getKeyStroke('V', Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()
                | InputEvent.SHIFT_MASK));
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        getFreeColClient().getGUI().getViewMode().toggleViewMode();
    }
}
