

package net.sf.freecol.client.gui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import net.sf.freecol.client.ClientOptions;
import net.sf.freecol.client.FreeColClient;


public class DisplayTileEmptyAction extends SelectableAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(DisplayTileEmptyAction.class.getName());




    public static final String id = "displayTileEmptyAction";


    
    DisplayTileEmptyAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.view.displayTileEmpty", null, 
              KeyStroke.getKeyStroke('E', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        setSelected(freeColClient.getClientOptions().getDisplayTileText()
                    == ClientOptions.DISPLAY_TILE_TEXT_EMPTY);
    }

    
    protected boolean shouldBeEnabled() {
        return true;
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        if (((JRadioButtonMenuItem) e.getSource()).isSelected()) {
            freeColClient.getGUI().setDisplayTileText(ClientOptions.DISPLAY_TILE_TEXT_EMPTY);
            freeColClient.getCanvas().refresh();
        }
    }
}
