

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.panel.DifficultyDialog;


public class ShowDifficultyAction extends MapboardAction {

    public static final String id = "difficulty";

    
    ShowDifficultyAction(FreeColClient freeColClient) {
        super(freeColClient, "gameOptions.difficultySettings.difficulty.name", null,
              KeyStroke.getKeyStroke(KeyEvent.VK_F9, KeyEvent.SHIFT_MASK));
    }

    
    protected boolean shouldBeEnabled() {
        return true;
    }    
    
    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        Canvas canvas = freeColClient.getCanvas();
        canvas.showPanel(new DifficultyDialog(canvas, true));
    }
}
