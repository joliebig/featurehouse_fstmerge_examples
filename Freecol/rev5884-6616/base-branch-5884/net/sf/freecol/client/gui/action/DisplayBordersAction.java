

package net.sf.freecol.client.gui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.KeyStroke;

import net.sf.freecol.client.ClientOptions;
import net.sf.freecol.client.FreeColClient;


public class DisplayBordersAction extends SelectableAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(DisplayBordersAction.class.getName());

    public static final String id = "displayBordersAction";


    
    DisplayBordersAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.view.displayBorders", null,
              KeyStroke.getKeyStroke('B', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        setSelected(freeColClient.getClientOptions().getBoolean(ClientOptions.DISPLAY_BORDERS));
    }

    
    protected boolean shouldBeEnabled() {
        return true;
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        freeColClient.getGUI().setDisplayBorders(((JCheckBoxMenuItem) e.getSource()).isSelected());
        freeColClient.getCanvas().refresh();
    }
}
