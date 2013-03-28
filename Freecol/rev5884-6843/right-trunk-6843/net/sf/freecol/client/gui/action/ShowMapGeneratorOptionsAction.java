

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.panel.MapGeneratorOptionsDialog;


public class ShowMapGeneratorOptionsAction extends MapboardAction {

    public static final String id = "mapGeneratorOptions";

    
    ShowMapGeneratorOptionsAction(FreeColClient freeColClient) {
        super(freeColClient, id, null, KeyStroke.getKeyStroke(KeyEvent.VK_F12, KeyEvent.SHIFT_MASK));
    }

    
    protected boolean shouldBeEnabled() {
        return true;
    }    
    
    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        Canvas canvas = freeColClient.getCanvas();
        canvas.showMapGeneratorOptionsDialog(false, freeColClient.getGame().getMapGeneratorOptions());
    }
}
