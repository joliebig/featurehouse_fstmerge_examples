

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.panel.AboutPanel;


public class AboutAction extends FreeColAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(AboutAction.class.getName());




    public static final String id = "aboutAction";


    
    AboutAction(FreeColClient freeColClient) {
        super(freeColClient, "FreeCol " + FreeCol.getRevision(), "FreeCol " + FreeCol.getRevision(), 0, null, false);
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        Canvas canvas = freeColClient.getCanvas();
        canvas.showPanel(new AboutPanel(canvas));
    }
}
