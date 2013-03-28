

package net.sf.freecol.client.gui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import net.sf.freecol.client.ClientOptions;
import net.sf.freecol.client.FreeColClient;


public class DisplayTileOwnersAction extends SelectableAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(DisplayTileOwnersAction.class.getName());




    public static final String id = "displayTileOwnersAction";


    
    DisplayTileOwnersAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.view.displayTileOwners", null, 
              KeyStroke.getKeyStroke('Y', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        setSelected(freeColClient.getClientOptions().getDisplayTileText()
                    == ClientOptions.DISPLAY_TILE_TEXT_OWNERS);
    }

    
    protected boolean shouldBeEnabled() {
        return true;
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        if (((JRadioButtonMenuItem) e.getSource()).isSelected()) {
            freeColClient.getGUI().setDisplayTileText(ClientOptions.DISPLAY_TILE_TEXT_OWNERS);
            freeColClient.getCanvas().refresh();
        }
    }
}
