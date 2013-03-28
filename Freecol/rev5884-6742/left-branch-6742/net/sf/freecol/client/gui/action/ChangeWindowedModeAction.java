

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;


public class ChangeWindowedModeAction extends SelectableAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ChangeWindowedModeAction.class.getName());




    public static final String id = "changeWindowedModeAction";


    
    ChangeWindowedModeAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.view.changeWindowedModeAction", null, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.ALT_MASK));
    }
    
    
    @Override
    public void update() {
        selected = !getFreeColClient().isWindowed();
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        getFreeColClient().changeWindowedMode(!getFreeColClient().isWindowed());
    }
}
