

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;

import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.ViewMode;


public class MoveAction extends MapboardAction {

    public static final String id = "moveAction.";

    public static final int[] accelerators = new int[] {
        KeyEvent.VK_NUMPAD8,
        KeyEvent.VK_NUMPAD9,
        KeyEvent.VK_NUMPAD6,
        KeyEvent.VK_NUMPAD3,
        KeyEvent.VK_NUMPAD2,
        KeyEvent.VK_NUMPAD1,
        KeyEvent.VK_NUMPAD4,
        KeyEvent.VK_NUMPAD7,
    };

    private Direction direction;

    
    MoveAction(FreeColClient freeColClient, Direction direction) {
        super(freeColClient, id + direction);
        this.direction = direction;
        setAccelerator(KeyStroke.getKeyStroke(accelerators[direction.ordinal()], 0));
    }

    
    public void actionPerformed(ActionEvent e) {
        switch(getFreeColClient().getGUI().getViewMode().getView()) {
        case ViewMode.MOVE_UNITS_MODE:
            getFreeColClient().getInGameController().moveActiveUnit(direction);
            break;
        case ViewMode.VIEW_TERRAIN_MODE:
            getFreeColClient().getGUI().moveTileCursor(direction);
            break;
        }
    }
}
