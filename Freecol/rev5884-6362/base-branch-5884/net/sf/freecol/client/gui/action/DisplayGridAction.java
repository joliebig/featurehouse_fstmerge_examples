

package net.sf.freecol.client.gui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.KeyStroke;

import net.sf.freecol.client.ClientOptions;
import net.sf.freecol.client.FreeColClient;


public class DisplayGridAction extends SelectableAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(DisplayGridAction.class.getName());




    public static final String id = "displayGridAction";


    
    DisplayGridAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.view.displayGrid", null, KeyStroke.getKeyStroke('G', Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        setSelected(freeColClient.getClientOptions().getBoolean(ClientOptions.DISPLAY_GRID));
    }

    
    protected boolean shouldBeEnabled() {
        return true;
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        freeColClient.getGUI().setDisplayGrid(((JCheckBoxMenuItem) e.getSource()).isSelected());
        freeColClient.getCanvas().refresh();
    }
}
