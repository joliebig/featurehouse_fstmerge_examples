

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.panel.GameOptionsDialog;


public class ShowGameOptionsAction extends MapboardAction {

    public static final String id = "gameOptions";

    
    ShowGameOptionsAction(FreeColClient freeColClient) {
        super(freeColClient, "gameOptions", null, KeyStroke.getKeyStroke(KeyEvent.VK_F11, KeyEvent.SHIFT_MASK));
    }

    
    protected boolean shouldBeEnabled() {
        return true;
    }    
    
    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        Canvas canvas = freeColClient.getCanvas();
        canvas.showPanel(new GameOptionsDialog(canvas, false));
    }
}
