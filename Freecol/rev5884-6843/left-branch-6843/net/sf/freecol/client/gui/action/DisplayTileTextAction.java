

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import net.sf.freecol.client.ClientOptions;
import net.sf.freecol.client.FreeColClient;


public class DisplayTileTextAction extends SelectableAction {

    public static final String id = "displayTileTextAction.";

    
    public static enum DisplayText {
        EMPTY, NAMES, OWNERS, REGIONS
    };

    public static final int[] accelerators = new int[] {
        KeyEvent.VK_E,
        KeyEvent.VK_N,
        KeyEvent.VK_O,
        KeyEvent.VK_R
    };
        

    private DisplayText display;

    
    DisplayTileTextAction(FreeColClient freeColClient, DisplayText type) {
        super(freeColClient, id + type);
        display = type;
        setSelected(freeColClient.getClientOptions().getDisplayTileText() == type.ordinal());
        setAccelerator(KeyStroke.getKeyStroke(accelerators[type.ordinal()],
                                              KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK));
    }

    
    protected boolean shouldBeEnabled() {
        return true;
    }

    
    public void actionPerformed(ActionEvent e) {
        if (((JRadioButtonMenuItem) e.getSource()).isSelected()) {
            freeColClient.getGUI().setDisplayTileText(display);
            freeColClient.getCanvas().refresh();
        }
    }
}
