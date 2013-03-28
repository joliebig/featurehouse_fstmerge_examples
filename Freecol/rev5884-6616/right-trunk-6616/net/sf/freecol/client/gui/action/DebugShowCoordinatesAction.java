

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.JCheckBoxMenuItem;

import net.sf.freecol.client.FreeColClient;


public class DebugShowCoordinatesAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(DebugShowCoordinatesAction.class.getName());




    public static final String id = "debugShowCoordinatesAction";


    
    DebugShowCoordinatesAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.debug.showCoordinates", null, KeyEvent.VK_L);
    }

    
    protected boolean shouldBeEnabled() {
        return true;
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        freeColClient.getGUI().displayCoordinates = ((JCheckBoxMenuItem) e.getSource()).isSelected();
        freeColClient.getCanvas().refresh();
    }
}
