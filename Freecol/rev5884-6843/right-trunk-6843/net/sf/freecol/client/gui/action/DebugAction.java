

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.FreeColClient;


public class DebugAction extends FreeColAction {

    public static final String id = "debugAction";

    
    DebugAction(FreeColClient freeColClient) {
        super(freeColClient, id);
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.SHIFT_MASK | KeyEvent.CTRL_MASK));
    }

    
    public void actionPerformed(ActionEvent e) {
        freeColClient.getInGameController().setInDebugMode(!FreeCol.isInDebugMode());
    }
}
