



package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;



public class MiniMapZoomOutAction extends MapboardAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(MiniMapZoomOutAction.class.getName());

    public static final String id = "miniMapZoomOutAction";


    
    MiniMapZoomOutAction(FreeColClient freeColClient) {
        super(freeColClient, "unit.state.10", null, KeyEvent.VK_PLUS, KeyStroke.getKeyStroke('+', 0));
        addImageIcons("zoom_out");
    }
    
    
    
    protected boolean shouldBeEnabled() {
        MapControlsAction mca = (MapControlsAction) getFreeColClient().getActionManager().getFreeColAction(MapControlsAction.id);
        return super.shouldBeEnabled()
                && mca.getMapControls() != null
                && mca.getMapControls().canZoomOut();
    }      
    
    
    public String getId() {
        return id;
    }

        
    public void actionPerformed(ActionEvent e) {
        MapControlsAction mca = (MapControlsAction) getFreeColClient().getActionManager().getFreeColAction(MapControlsAction.id);
        mca.getMapControls().zoomOut();
        update();
        getFreeColClient().getActionManager().getFreeColAction(MiniMapZoomInAction.id).update();
    }
}
