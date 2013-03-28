



package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;



public class MiniMapZoomInAction extends MapboardAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(MiniMapZoomInAction.class.getName());

    public static final String id = "miniMapZoomInAction";


    
    MiniMapZoomInAction(FreeColClient freeColClient) {
        super(freeColClient, "unit.state.9", null, KeyEvent.VK_PLUS, KeyStroke.getKeyStroke('+', 0));
        addImageIcons("zoom_in");
    }
    
    
    
    public String getId() {
        return id;
    }
    

    
    protected boolean shouldBeEnabled() {
        MapControlsAction mca = (MapControlsAction) getFreeColClient().getActionManager().getFreeColAction(MapControlsAction.id);
        return super.shouldBeEnabled()
                && mca.getMapControls() != null
                && mca.getMapControls().canZoomIn();
    }  
    
        
    public void actionPerformed(ActionEvent e) {
        MapControlsAction mca = (MapControlsAction) getFreeColClient().getActionManager().getFreeColAction(MapControlsAction.id);
        mca.getMapControls().zoomIn();
        update();
        getFreeColClient().getActionManager().getFreeColAction(MiniMapZoomOutAction.id).update();
    }
}
