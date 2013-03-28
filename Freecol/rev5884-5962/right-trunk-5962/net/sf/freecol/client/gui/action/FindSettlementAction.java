

package net.sf.freecol.client.gui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.panel.FindSettlementDialog;


public class FindSettlementAction extends MapboardAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(FindSettlementAction.class.getName());

    public static final String id = "findSettlementAction";


    
    FindSettlementAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.view.findSettlement", null,
              KeyStroke.getKeyStroke('F', Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
    }

    
    public String getId() {
        return id;
    }

    
    protected boolean shouldBeEnabled() {
        return super.shouldBeEnabled();
    }    
    
    
    public void actionPerformed(ActionEvent e) {
        Canvas canvas = getFreeColClient().getCanvas();
        canvas.showPanel(new FindSettlementDialog(canvas), false);
    }
}
